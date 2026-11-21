package mz.co.moseguros.policy.application;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.time.Clock;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import mz.co.moseguros.catalog.CatalogApi;
import mz.co.moseguros.catalog.CoverageCode;
import mz.co.moseguros.catalog.CoverageView;
import mz.co.moseguros.catalog.ProductVersionView;
import mz.co.moseguros.document.DocumentApi;
import mz.co.moseguros.document.FileClassification;
import mz.co.moseguros.document.FileContent;
import mz.co.moseguros.document.FileRef;
import mz.co.moseguros.document.PdfTarget;
import mz.co.moseguros.document.application.PdfTemplates;
import mz.co.moseguros.identity.CustomerView;
import mz.co.moseguros.identity.IdentityApi;
import mz.co.moseguros.notification.NotificationApi;
import mz.co.moseguros.notification.NotificationCategory;
import mz.co.moseguros.organization.InsurerProfileView;
import mz.co.moseguros.organization.OrganizationApi;
import mz.co.moseguros.policy.CertificateStatus;
import mz.co.moseguros.policy.PolicyCoverageView;
import mz.co.moseguros.policy.application.PolicyPdfModelFactory.Header;
import mz.co.moseguros.policy.application.PolicyPdfModelFactory.Logo;
import mz.co.moseguros.policy.domain.Certificate;
import mz.co.moseguros.policy.domain.CertificateRepository;
import mz.co.moseguros.policy.domain.InstalmentRepository;
import mz.co.moseguros.policy.domain.Policy;
import mz.co.moseguros.policy.domain.PolicyEvent;
import mz.co.moseguros.policy.domain.PolicyEventRepository;
import mz.co.moseguros.policy.domain.PolicyEventType;
import mz.co.moseguros.policy.domain.PolicyRepository;
import mz.co.moseguros.shared.audit.ActorType;
import mz.co.moseguros.shared.audit.AuditRecord;
import mz.co.moseguros.shared.audit.AuditService;
import mz.co.moseguros.shared.error.DomainException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Gera os PDF da apólice (S6-02 §1): o certificado (com o código QR da verificação) e as condições
 * particulares, guardados no {@code document} com dono {@code POLICY}; na primeira vez que os dois
 * ficam prontos grava {@code DOCUMENTS_READY} na linha temporal, a auditoria e avisa o cliente
 * ({@code POLICY_ISSUED}). Idempotente: um ficheiro que já existe não se gera de novo.
 *
 * <p>Chamado em {@code SYSTEM} pelo {@link PolicyDocumentsListener} e pelo {@link PolicyDocumentsJob}.
 * Um erro desfaz tudo (os ficheiros já guardados ficam órfãos e o {@code OrphanFileCleanupJob} limpa-os
 * ao fim de 30 dias); a execução seguinte do job gera-os de novo.
 */
@Service
public class PolicyDocumentService {

    /** {@code actorId} da linha temporal e da auditoria. */
    public static final String ACTOR = "policy-documents";
    public static final String OWNER_TYPE = PolicyFileAccessPolicy.OWNER_TYPE;
    public static final String PURPOSE_CERTIFICATE = "POLICY_CERTIFICATE";
    public static final String PURPOSE_SCHEDULE = "POLICY_SCHEDULE";
    static final String NOTIFICATION_TEMPLATE = "POLICY_ISSUED";
    /** Nome da seguradora quando o perfil não existe (não devia acontecer). */
    static final String UNKNOWN_INSURER = "—";
    private static final Logger log = LoggerFactory.getLogger(PolicyDocumentService.class);

    private final PolicyRepository policies;
    private final CertificateRepository certificates;
    private final InstalmentRepository instalments;
    private final PolicyEventRepository policyEvents;
    private final DocumentApi documents;
    private final IdentityApi identity;
    private final OrganizationApi organizations;
    private final CatalogApi catalog;
    private final NotificationApi notifications;
    private final AuditService audit;
    private final PolicyPdfModelFactory models;
    private final QrCodeRenderer qrCodes;
    private final VerificationTokens tokens;
    private final ObjectMapper objectMapper;
    private final TransactionTemplate readOnlyNewTx;
    private final Clock clock;

    public PolicyDocumentService(PolicyRepository policies, CertificateRepository certificates,
                                 InstalmentRepository instalments, PolicyEventRepository policyEvents,
                                 DocumentApi documents, IdentityApi identity, OrganizationApi organizations,
                                 CatalogApi catalog, NotificationApi notifications, AuditService audit,
                                 PolicyPdfModelFactory models, QrCodeRenderer qrCodes, VerificationTokens tokens,
                                 ObjectMapper objectMapper, PlatformTransactionManager transactionManager,
                                 Clock clock) {
        this.policies = policies;
        this.certificates = certificates;
        this.instalments = instalments;
        this.policyEvents = policyEvents;
        this.documents = documents;
        this.identity = identity;
        this.organizations = organizations;
        this.catalog = catalog;
        this.notifications = notifications;
        this.audit = audit;
        this.models = models;
        this.qrCodes = qrCodes;
        this.tokens = tokens;
        this.objectMapper = objectMapper;
        this.readOnlyNewTx = new TransactionTemplate(transactionManager);
        this.readOnlyNewTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        this.readOnlyNewTx.setReadOnly(true);
        this.clock = clock;
    }

    /** Resultado de {@link #generate}. */
    public enum Outcome {
        /** Pelo menos um PDF gerado nesta chamada. */
        GENERATED,
        /** Os dois PDF já existiam. */
        ALREADY_READY,
        /** Apólice inexistente ou sem certificado {@code VALID}: nada a fazer. */
        NO_CERTIFICATE
    }

    /**
     * Gera os PDF em falta da apólice (S6-02 §1, passos 1 a 6), numa transacção nova, no âmbito de quem chama
     * ({@code SYSTEM}).
     *
     * @throws DomainException {@code PDF_TEMPLATE_NOT_FOUND} ou {@code PDF_RENDER_FAILED} (nada fica gravado)
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Outcome generate(UUID policyId) {
        // 1. Apólice e certificado VALID
        Policy policy = policies.findById(policyId).orElse(null);
        Certificate certificate = policy == null ? null
                : certificates.findByPolicyIdAndStatus(policyId, CertificateStatus.VALID).orElse(null);
        if (certificate == null) {
            log.info("Policy documents not generated: no valid certificate policyId={}", policyId);
            return Outcome.NO_CERTIFICATE;
        }
        UUID previousCertificateFile = certificate.getPdfFileId();
        UUID previousScheduleFile = policy.getScheduleFileId();
        if (previousCertificateFile != null && previousScheduleFile != null) {
            return Outcome.ALREADY_READY;
        }

        // 2. Língua do cliente e dados comuns
        Locale locale = locale(policy.getPolicyholderUserId());
        InsurerProfileView profile = organizations.insurerProfile(policy.getInsurerId()).orElse(null);
        String insurerName = profile == null || profile.displayName() == null ? UNKNOWN_INSURER
                : profile.displayName();
        Header header = new Header(insurerName, logo(profile), clock.instant());
        ProductVersionView version = productVersion(policy);

        // 3. Certificado
        FileRef certificateFile = null;
        if (previousCertificateFile == null) {
            String verificationUrl = tokens.verificationUrl(certificate.getVerificationToken());
            Map<String, Object> model = models.certificate(policy, certificate, header, territorialScope(version),
                    verificationUrl, qrCodes.dataUri(verificationUrl));
            certificateFile = documents.renderPdf(PdfTemplates.POLICY_CERTIFICATE, model, locale,
                    new PdfTarget(OWNER_TYPE, policyId, PURPOSE_CERTIFICATE, policy.getInsurerId(),
                            FileClassification.PERSONAL, certificateFileName(certificate)));
            certificate.attachPdf(certificateFile.id());
        }

        // 4. Condições particulares
        FileRef scheduleFile = null;
        if (previousScheduleFile == null) {
            Map<String, Object> model = models.schedule(policy, header, coverages(policy),
                    instalments.findByPolicyIdOrderBySeqAsc(policyId), version == null ? null : version.versionNo());
            scheduleFile = documents.renderPdf(PdfTemplates.POLICY_SCHEDULE, model, locale,
                    new PdfTarget(OWNER_TYPE, policyId, PURPOSE_SCHEDULE, policy.getInsurerId(),
                            FileClassification.PERSONAL, scheduleFileName(policy)));
            policy.attachSchedule(scheduleFile.id());
        }
        certificates.save(certificate);
        policies.saveAndFlush(policy);

        // 5. Auditoria; na primeira vez, linha temporal e aviso ao cliente
        FileRef finalCertificate = certificateFile != null ? certificateFile : documents.metadata(previousCertificateFile);
        FileRef finalSchedule = scheduleFile != null ? scheduleFile : documents.metadata(previousScheduleFile);
        audit(policy, previousCertificateFile, previousScheduleFile, finalCertificate, finalSchedule);
        if (!policyEvents.existsByPolicyIdAndType(policyId, PolicyEventType.DOCUMENTS_READY)) {
            ObjectNode details = objectMapper.createObjectNode()
                    .put("certificateFileId", finalCertificate.id().toString())
                    .put("scheduleFileId", finalSchedule.id().toString());
            policyEvents.save(PolicyEvent.of(policy, PolicyEventType.DOCUMENTS_READY, clock.instant(),
                    ActorType.SYSTEM, ACTOR, details));
            notifyCustomer(policy, insurerName);
        }
        log.info("Policy documents generated policyId={} certificateFileId={} scheduleFileId={}", policyId,
                finalCertificate.id(), finalSchedule.id());
        return Outcome.GENERATED;
    }

    /** Nome do PDF do certificado ({@code CT-2027-0001234.pdf}). */
    public static String certificateFileName(Certificate certificate) {
        return certificate.getNumber() + ".pdf";
    }

    /** Nome do PDF das condições particulares ({@code AP-2027-000123-condicoes-particulares.pdf}). */
    public static String scheduleFileName(Policy policy) {
        return policy.getNumber() + "-condicoes-particulares.pdf";
    }

    private void notifyCustomer(Policy policy, String insurerName) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("policyNumber", policy.getNumber());
        params.put("insurerName", insurerName);
        params.put("plate", policy.getPlate());
        params.put("validTo", policy.getEndAt());
        params.put("webPath", "/cliente/apolices/" + policy.getId());
        params.put("deepLink", "moseguros://policy/" + policy.getId());
        notifications.notify(policy.getPolicyholderUserId(), NOTIFICATION_TEMPLATE, params,
                NotificationCategory.POLICY);
    }

    private void audit(Policy policy, UUID previousCertificateFile, UUID previousScheduleFile, FileRef certificate,
                       FileRef schedule) {
        Map<String, Object> before = new LinkedHashMap<>();
        before.put("certificateFileId", previousCertificateFile == null ? null : previousCertificateFile.toString());
        before.put("scheduleFileId", previousScheduleFile == null ? null : previousScheduleFile.toString());
        Map<String, Object> after = new LinkedHashMap<>();
        after.put("certificateFileId", certificate.id().toString());
        after.put("certificateSha256", certificate.sha256());
        after.put("scheduleFileId", schedule.id().toString());
        after.put("scheduleSha256", schedule.sha256());
        audit.record(new AuditRecord(ActorType.SYSTEM, ACTOR, policy.getInsurerId(), "POLICY.DOCUMENTS_GENERATE",
                "Policy", policy.getId().toString(), before, after));
    }

    /** {@code en} se for a língua preferida do cliente; senão {@code pt}. */
    private Locale locale(UUID userId) {
        String language = identity.customer(userId).map(CustomerView::preferredLanguage).orElse(null);
        return "en".equalsIgnoreCase(language) ? Locale.ENGLISH : Locale.forLanguageTag("pt");
    }

    /** A versão do produto; {@code null} se não for legível (o PDF sai sem o número e sem o âmbito). */
    private ProductVersionView productVersion(Policy policy) {
        try {
            return catalog.version(policy.getProductVersionId());
        } catch (DomainException e) {
            log.warn("Product version not readable for the policy documents policyId={} code={}", policy.getId(),
                    e.getCode());
            return null;
        }
    }

    /** Âmbito territorial da cobertura {@code MTPL} da versão; vazio se não houver. */
    private static List<String> territorialScope(ProductVersionView version) {
        if (version == null || version.coverages() == null) {
            return List.of();
        }
        return version.coverages().stream()
                .filter(c -> c.code() == CoverageCode.MTPL)
                .findFirst()
                .map(CoverageView::territorialScope)
                .orElse(List.of());
    }

    private List<PolicyCoverageView> coverages(Policy policy) {
        return List.of(objectMapper.convertValue(policy.getCoverages(), PolicyCoverageView[].class));
    }

    /**
     * Logótipo da seguradora numa transacção só de leitura à parte (um ficheiro em falta não desfaz a
     * geração); sem logótipo ou ilegível → {@code null} (o cabeçalho mostra o nome em texto).
     */
    private Logo logo(InsurerProfileView profile) {
        UUID fileId = profile == null ? null : profile.logoFileId();
        if (fileId == null) {
            return null;
        }
        try {
            return readOnlyNewTx.execute(status -> {
                FileContent content = documents.open(fileId);
                try (InputStream in = content.content()) {
                    return PolicyPdfModelFactory.logo(in.readAllBytes(), content.ref().contentType());
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        } catch (RuntimeException e) {
            log.warn("Insurer logo not readable insurerId={} fileId={} ({})", profile.insurerId(), fileId,
                    e.getClass().getSimpleName());
            return null;
        }
    }
}
