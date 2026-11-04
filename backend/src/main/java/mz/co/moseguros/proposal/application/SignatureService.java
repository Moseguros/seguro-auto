package mz.co.moseguros.proposal.application;

import java.time.Clock;
import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import mz.co.moseguros.catalog.CatalogApi;
import mz.co.moseguros.document.DocumentApi;
import mz.co.moseguros.document.FileClassification;
import mz.co.moseguros.document.FileRef;
import mz.co.moseguros.document.PdfTarget;
import mz.co.moseguros.identity.CustomerView;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.identity.SignOtpChallenge;
import mz.co.moseguros.identity.SignOtpRequest;
import mz.co.moseguros.identity.SignOtpVerification;
import mz.co.moseguros.proposal.domain.Declaration;
import mz.co.moseguros.proposal.domain.DeclarationRepository;
import mz.co.moseguros.proposal.domain.PolicyholderSnapshot;
import mz.co.moseguros.proposal.domain.Proposal;
import mz.co.moseguros.proposal.domain.ProposalRepository;
import mz.co.moseguros.proposal.domain.Signature;
import mz.co.moseguros.proposal.domain.SignatureRepository;
import mz.co.moseguros.proposal.domain.SuitabilityAnswer;
import mz.co.moseguros.proposal.domain.SuitabilityAnswerRepository;
import mz.co.moseguros.shared.audit.AuditRecord;
import mz.co.moseguros.shared.audit.AuditService;
import mz.co.moseguros.shared.domain.Channel;
import mz.co.moseguros.shared.domain.Money;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.error.FieldErrorCodes;
import mz.co.moseguros.shared.error.FieldValidationException;
import mz.co.moseguros.shared.security.CurrentUser;
import mz.co.moseguros.shared.tenant.Scope;
import mz.co.moseguros.shared.tenant.TenantContext;
import mz.co.moseguros.shared.tenant.TenantSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Assinatura electrónica simples da proposta por OTP sobre o hash do PDF (S4-04 §7):
 * {@code requestProposalSignature} gera o PDF, guarda-o com o SHA-256 e envia o código ao tomador;
 * {@code confirmProposalSignature} valida o código e grava a assinatura ligada ao hash.
 *
 * <p>Os logs têm o número da proposta, o id do ficheiro e o desafio; nunca o código, o documento nem o
 * NUIT do tomador (que só vão para o modelo do PDF).
 */
@Service
public class SignatureService {

    private static final Logger log = LoggerFactory.getLogger(SignatureService.class);

    /** Modelo do PDF da proposta ({@code PdfTemplates.PROPOSAL}). */
    static final String TEMPLATE = "proposal";
    /** {@code document.file.purpose} do PDF da proposta. */
    static final String FILE_PURPOSE = "PROPOSAL_DOCUMENT";

    private final ProposalQueryService queries;
    private final ProposalRepository proposals;
    private final SignatureRepository signatures;
    private final DeclarationRepository declarations;
    private final SuitabilityAnswerRepository suitabilityAnswers;
    private final ProposalService proposalService;
    private final ProposalPdfModelBuilder pdfModel;
    private final ProposalJson json;
    private final CatalogApi catalog;
    private final DocumentApi documents;
    private final IdentityApi identity;
    private final AuditService audit;
    private final CurrentUser currentUser;
    private final ProposalProperties properties;
    private final Clock clock;

    public SignatureService(ProposalQueryService queries, ProposalRepository proposals, SignatureRepository signatures,
                            DeclarationRepository declarations, SuitabilityAnswerRepository suitabilityAnswers,
                            ProposalService proposalService, ProposalPdfModelBuilder pdfModel, ProposalJson json,
                            CatalogApi catalog, DocumentApi documents, IdentityApi identity, AuditService audit,
                            CurrentUser currentUser, ProposalProperties properties, Clock clock) {
        this.queries = queries;
        this.proposals = proposals;
        this.signatures = signatures;
        this.declarations = declarations;
        this.suitabilityAnswers = suitabilityAnswers;
        this.proposalService = proposalService;
        this.pdfModel = pdfModel;
        this.json = json;
        this.catalog = catalog;
        this.documents = documents;
        this.identity = identity;
        this.audit = audit;
        this.currentUser = currentUser;
        this.properties = properties;
        this.clock = clock;
    }

    /**
     * Pedido de assinatura em curso.
     *
     * @param challenge código enviado (o {@code debugCode} só no perfil {@code local})
     */
    public record SignRequest(SignOtpChallenge challenge, UUID documentFileId, String documentHash) {
    }

    /**
     * {@code requestProposalSignature} (S4-04 §7), por esta ordem: proposta editável (409
     * {@code PROPOSAL_NOT_EDITABLE}) e versão igual (409 {@code CONCURRENT_MODIFICATION});
     * {@code documentsAcknowledged = true} (400 {@code MUST_BE_TRUE}); proposta completa sem o item
     * {@code SIGNATURE} (409 {@code PROPOSAL_INCOMPLETE} com {@code missing}); PDF; código ao tomador;
     * pedido gravado (substitui o anterior; não aumenta {@code content_revision}).
     *
     * <p>Corre numa transacção: se o envio do código falhar, o pedido não fica gravado e o PDF fica órfão
     * (limpo ao fim de 30 dias).
     *
     * @param channel canal escolhido pelo cliente ou {@code null}
     */
    @PreAuthorize("hasAnyAuthority('self:proposals','proposal:create-for-client')")
    @Transactional
    public SignRequest request(UUID proposalId, Boolean documentsAcknowledged, Channel channel, Long version) {
        Proposal proposal = queries.accessible(proposalId);
        proposal.requireEditable();
        DeclarationService.requireVersion(proposal, version);
        if (!Boolean.TRUE.equals(documentsAcknowledged)) {
            throw new FieldValidationException("documentsAcknowledged", FieldErrorCodes.MUST_BE_TRUE,
                    "The policyholder must confirm having read the product documents");
        }
        Instant now = clock.instant();
        List<ProposalCompleteness.Item> missing = ProposalCompleteness.missing(
                ProposalCompleteness.Facts.of(proposal, queries.inspectionStatus(proposal)), now, false);
        if (!missing.isEmpty()) {
            List<String> items = missing.stream().map(Enum::name).toList();
            throw DomainException.of(ErrorCodes.PROPOSAL_INCOMPLETE, "There are still steps missing: " + items)
                    .withProperty("missing", items);
        }
        String destination = signerDestination(proposal.getCustomerUserId());

        // PDF da revisão actual, ligado à proposta.
        PolicyholderSnapshot holder = json.policyholder(proposal.getPolicyholder());
        List<ProductDocuments.Selected> productDocuments = ProductDocuments.select(
                catalog.version(proposal.getProductVersionId()).documents(), ProposalPdfModelBuilder.language(holder));
        Map<String, Object> model = pdfModel.build(proposal, sortedDeclarations(proposal),
                sortedSuitability(proposal), productDocuments, null, destination, now);
        FileRef file = documents.renderPdf(TEMPLATE, model, pdfModel.locale(proposal),
                new PdfTarget(ProposalFileAccessPolicy.OWNER_TYPE, proposal.getId(), FILE_PURPOSE,
                        proposal.getInsurerId(), FileClassification.SENSITIVE,
                        proposal.getNumber() + "-r" + proposal.getContentRevision() + ".pdf"));

        // Código ao tomador, ligado ao hash do PDF. Pedido pelo mediador (S4-06 §6): assistido, com o nome
        // do mediador na mensagem; o destino é sempre o da conta do cliente.
        boolean assisted = actingBroker(proposal);
        SignOtpChallenge challenge = identity.requestSignOtp(new SignOtpRequest(proposal.getCustomerUserId(),
                file.id(), file.sha256(), proposal.getNumber(), proposal.getInsurerName(),
                Money.ofCentavos(proposal.getTotalAmount()), assisted ? proposal.getBrokerName() : null, channel));

        proposal.requestSignature(file.id(), file.sha256(), challenge.challengeId(),
                assisted ? currentUser.id() : null);
        proposals.saveAndFlush(proposal);
        Map<String, Object> after = new LinkedHashMap<>();
        after.put("documentFileId", file.id());
        after.put("documentHash", file.sha256());
        after.put("channel", challenge.channel().name());
        after.put("assisted", assisted);
        audit.record(new AuditRecord(proposalService.actorType(), currentUser.id().toString(), proposal.getInsurerId(),
                "PROPOSAL.SIGN_REQUEST", "Proposal", proposal.getId().toString(), null, after));
        log.info("Signature of proposal {} (revision {}) requested: file {}, challenge {} via {}",
                proposal.getNumber(), proposal.getContentRevision(), file.id(), challenge.challengeId(),
                challenge.channel());
        return new SignRequest(challenge, file.id(), file.sha256());
    }

    /**
     * {@code confirmProposalSignature} (S4-04 §7): proposta editável (409 {@code PROPOSAL_NOT_EDITABLE});
     * pedido em curso com este desafio (409 {@code SIGN_REQUEST_NOT_FOUND}); proposta sem alterações
     * desde o pedido (409 {@code PROPOSAL_CHANGED_SINCE_SIGN_REQUEST}); código válido para o hash do PDF
     * (erros {@code OTP_*} tal e qual); grava a assinatura e renova o prazo.
     *
     * <p>Não faz rollback com uma {@code DomainException}: as tentativas erradas do código ficam
     * gravadas (S1-03); nada da proposta foi alterado antes da validação.
     *
     * @param ip        IP do pedido ({@code ClientIpResolver})
     * @param userAgent {@code User-Agent} do pedido
     */
    @PreAuthorize("hasAnyAuthority('self:proposals','proposal:create-for-client')")
    @Transactional(noRollbackFor = DomainException.class)
    public ProposalDetails confirm(UUID proposalId, UUID challengeId, String code, String ip, String userAgent) {
        Proposal proposal = queries.accessible(proposalId);
        proposal.requireEditable();
        proposal.requireSignRequest(challengeId);
        if (code == null || code.isBlank()) {
            throw new FieldValidationException("code", FieldErrorCodes.REQUIRED, "The code is required");
        }
        SignOtpVerification verification = identity.verifySignOtp(proposal.getCustomerUserId(), challengeId,
                code.strip(), proposal.getSignRequestHash());

        // Confirmada pelo mediador → assistida (S4-06 §6); pelo próprio cliente → não assistida, mesmo que o
        // pedido tenha sido do mediador. O signatário é sempre o cliente.
        UUID assistedBy = actingBroker(proposal) ? currentUser.id() : null;
        Signature signature = Signature.of(proposal, new Signature.Signed(proposal.getSignRequestFileId(),
                proposal.getSignRequestHash(), verification.challengeId(), verification.channel(),
                verification.destinationMasked(), assistedBy, assistedBy == null ? null : proposal.getBrokerOrgId(),
                verification.verifiedAt(), ip, userAgent));
        signatures.save(signature);
        proposal.sign(signature, clock.instant(), properties.expiry());
        proposals.saveAndFlush(proposal);

        Map<String, Object> after = new LinkedHashMap<>();
        after.put("signatureId", signature.getId());
        after.put("documentHash", signature.getDocumentHash());
        after.put("channel", signature.getOtpChannel().name());
        after.put("destinationMasked", signature.getDestinationMasked());
        after.put("assisted", assistedBy != null);
        audit.record(new AuditRecord(proposalService.actorType(), currentUser.id().toString(), proposal.getInsurerId(),
                "PROPOSAL.SIGN", "Proposal", proposal.getId().toString(), null, after));
        log.info("Proposal {} (revision {}) signed: signature {}, document {}", proposal.getNumber(),
                proposal.getContentRevision(), signature.getId(), signature.getDocumentFileId());
        return queries.details(proposal);
    }

    /**
     * Quem pede é um utilizador do mediador da proposta (âmbito {@code ORG} da organização
     * {@code broker_org_id}, S4-06 §6).
     */
    static boolean actingBroker(Proposal proposal) {
        TenantSnapshot tenant = TenantContext.current();
        return tenant.scope() == Scope.ORG && proposal.getBrokerOrgId() != null
                && proposal.getBrokerOrgId().equals(tenant.orgId());
    }

    /**
     * Destino mascarado do código, para o texto do PDF: o telemóvel da conta ou o e-mail verificado (a
     * regra do {@code IdentityApi.requestSignOtp}). Sem nenhum → 409 {@code SIGNER_CONTACT_MISSING}, antes
     * de gerar o PDF.
     */
    private String signerDestination(UUID customerUserId) {
        CustomerView customer = identity.customer(customerUserId).orElse(null);
        if (customer != null && customer.msisdn() != null) {
            return customer.msisdn().masked();
        }
        if (customer != null && customer.email() != null && customer.emailVerified()) {
            return maskEmail(customer.email());
        }
        throw DomainException.of(ErrorCodes.SIGNER_CONTACT_MISSING,
                "The policyholder has no mobile number or verified e-mail to receive the code");
    }

    /** {@code a***@exemplo.co.mz}, como o {@code OtpIdentifier.masked} do identity. */
    static String maskEmail(String email) {
        int at = email.indexOf('@');
        if (at <= 0) {
            return "***";
        }
        return email.charAt(0) + "***" + email.substring(at);
    }

    private List<Declaration> sortedDeclarations(Proposal proposal) {
        return declarations.findByProposalId(proposal.getId()).stream()
                .sorted(Comparator.comparing(Declaration::getQuestionCode)).toList();
    }

    private List<SuitabilityAnswer> sortedSuitability(Proposal proposal) {
        return suitabilityAnswers.findByProposalId(proposal.getId()).stream()
                .sorted(Comparator.comparing(SuitabilityAnswer::getQuestionCode)).toList();
    }
}
