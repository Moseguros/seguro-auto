package mz.co.moseguros.identity.application;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import mz.co.moseguros.identity.domain.OtpChallenge.IdentifierType;
import mz.co.moseguros.identity.domain.OtpIdentifier;
import mz.co.moseguros.identity.domain.UserAccount;
import mz.co.moseguros.identity.domain.UserAccountRepository;
import mz.co.moseguros.shared.audit.ActorType;
import mz.co.moseguros.shared.audit.AuditRecord;
import mz.co.moseguros.shared.audit.AuditService;
import mz.co.moseguros.shared.domain.Channel;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.error.ErrorCodes;
import mz.co.moseguros.shared.security.CurrentUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Contactos do cliente autenticado ({@code /me/contacts}, S1-06 §3): listar, acrescentar ou trocar
 * o telemóvel ou o e-mail com confirmação por OTP, e remover.
 *
 * <p>A conta lê-se e grava-se em âmbito {@code CUSTOMER} (RLS). Saber se o contacto pertence a
 * outra conta precisa de {@code SYSTEM} ({@link IdentitySystemLookups}); os códigos vêm do
 * {@link OtpService}. O envio do código fica fora de transacção, como no login.
 */
@Service
public class ContactService {

    private static final Logger log = LoggerFactory.getLogger(ContactService.class);

    private final CurrentUser currentUser;
    private final UserAccountRepository accounts;
    private final IdentitySystemLookups lookups;
    private final OtpService otpService;
    private final AuditService audit;
    private final TransactionTemplate tx;

    public ContactService(CurrentUser currentUser, UserAccountRepository accounts, IdentitySystemLookups lookups,
                          OtpService otpService, AuditService audit, TransactionTemplate tx) {
        this.currentUser = currentUser;
        this.accounts = accounts;
        this.lookups = lookups;
        this.otpService = otpService;
        this.audit = audit;
        this.tx = tx;
    }

    /** Contactos que existem, completos (o cliente vê os seus). */
    @PreAuthorize("hasAuthority('self:profile')")
    @Transactional(readOnly = true)
    public List<ContactView> listMyContacts() {
        return contactsOf(load(currentUser.id()));
    }

    /**
     * Envia um código para o contacto novo (ou para reenviar).
     *
     * @param channel canal escolhido pelo cliente, ou {@code null} para o canal por defeito
     * @throws DomainException {@code CONTACT_ALREADY_VERIFIED}, {@code CONTACT_ALREADY_IN_USE} ou os
     *                         erros do pedido de código do S1-03
     */
    @PreAuthorize("hasAuthority('self:profile')")
    public OtpService.RequestResult startVerification(IdentifierType type, String value, Channel channel) {
        OtpIdentifier identifier = OtpIdentifier.parse(value, type, "value");
        UUID userId = currentUser.id();
        UserAccount account = tx.execute(status -> load(userId));
        if (account.hasVerifiedContact(identifier)) {
            throw DomainException.of(ErrorCodes.CONTACT_ALREADY_VERIFIED, "The contact is already verified");
        }
        if (lookups.customerContactTaken(identifier.type(), identifier.value(), userId)) {
            throw alreadyInUse();
        }
        OtpService.RequestResult result = otpService.requestForContact(identifier, userId, channel);
        log.info("Customer {} asked to verify {} (challenge {})", userId, identifier, result.challengeId());
        return result;
    }

    /**
     * Confirma o código e grava o contacto no lugar do anterior do mesmo tipo, verificado; auditoria
     * {@code CUSTOMER.CONTACT_CHANGE} com os valores mascarados.
     *
     * @throws DomainException os erros do {@code verifyOtp}, ou {@code CONTACT_ALREADY_IN_USE} se
     *                         outra conta ficou entretanto com o contacto
     */
    @PreAuthorize("hasAuthority('self:profile')")
    public List<ContactView> verify(UUID challengeId, String code) {
        UUID userId = currentUser.id();
        OtpIdentifier identifier = otpService.verifyContact(challengeId, code, userId);
        if (lookups.customerContactTaken(identifier.type(), identifier.value(), userId)) {
            throw alreadyInUse();
        }
        try {
            return tx.execute(status -> {
                UserAccount account = load(userId);
                String before = masked(account, identifier.type());
                account.replaceContact(identifier);
                accounts.saveAndFlush(account);
                audit.record(new AuditRecord(ActorType.CUSTOMER, userId.toString(), null, "CUSTOMER.CONTACT_CHANGE",
                        "UserAccount", userId.toString(), change(identifier.type(), before),
                        change(identifier.type(), identifier.masked())));
                log.info("Customer {} changed the contact {}", userId, identifier);
                return contactsOf(account);
            });
        } catch (DataIntegrityViolationException e) {
            // O índice único é a última defesa: outra conta ficou com o contacto entretanto.
            log.info("Customer {}: contact {} was taken by another account", userId, identifier);
            throw alreadyInUse();
        }
    }

    /**
     * Remove o contacto do tipo indicado; sem ele não muda nada. Auditoria
     * {@code CUSTOMER.CONTACT_REMOVE}.
     *
     * @throws DomainException {@code LAST_CONTACT} se a conta ficasse sem nenhum contacto verificado
     */
    @PreAuthorize("hasAuthority('self:profile')")
    @Transactional
    public void removeMyContact(IdentifierType type) {
        UUID userId = currentUser.id();
        UserAccount account = load(userId);
        String before = masked(account, type);
        if (account.removeContact(type)) {
            audit.record(new AuditRecord(ActorType.CUSTOMER, userId.toString(), null, "CUSTOMER.CONTACT_REMOVE",
                    "UserAccount", userId.toString(), change(type, before), null));
            log.info("Customer {} removed the {} contact", userId, type);
        }
    }

    private UserAccount load(UUID userId) {
        return accounts.findById(userId)
                .orElseThrow(() -> new DomainException(ErrorCodes.UNAUTHORIZED, 401, "Account not found"));
    }

    static List<ContactView> contactsOf(UserAccount account) {
        List<ContactView> contacts = new ArrayList<>();
        if (account.getMsisdn() != null) {
            contacts.add(new ContactView(IdentifierType.MSISDN, account.getMsisdn().e164(),
                    account.isMsisdnVerified()));
        }
        if (account.getEmail() != null) {
            contacts.add(new ContactView(IdentifierType.EMAIL, account.getEmail(), account.isEmailVerified()));
        }
        return contacts;
    }

    /** Valor actual do tipo indicado, mascarado, ou {@code null}. */
    private static String masked(UserAccount account, IdentifierType type) {
        if (type == IdentifierType.MSISDN) {
            return account.getMsisdn() == null ? null : account.getMsisdn().masked();
        }
        return account.getEmail() == null ? null : new OtpIdentifier(IdentifierType.EMAIL, account.getEmail()).masked();
    }

    private static Map<String, Object> change(IdentifierType type, String maskedValue) {
        Map<String, Object> state = new LinkedHashMap<>();
        state.put("type", type.name());
        state.put("value", maskedValue);
        return state;
    }

    private static DomainException alreadyInUse() {
        return DomainException.of(ErrorCodes.CONTACT_ALREADY_IN_USE, "The contact belongs to another account");
    }

    /** Contacto do cliente. */
    public record ContactView(IdentifierType type, String value, boolean verified) {

        @Override
        public String toString() {
            return "ContactView[type=" + type + ", verified=" + verified + "]";
        }
    }
}
