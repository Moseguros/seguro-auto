package mz.co.moseguros.identity.application;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import mz.co.moseguros.identity.domain.AccountType;
import mz.co.moseguros.identity.domain.CustomerProfile;
import mz.co.moseguros.identity.domain.CustomerProfileRepository;
import mz.co.moseguros.identity.domain.OtpChallenge.IdentifierType;
import mz.co.moseguros.identity.domain.OtpIdentifier;
import mz.co.moseguros.identity.domain.UserAccount;
import mz.co.moseguros.identity.domain.UserAccountRepository;
import mz.co.moseguros.identity.domain.UserRole;
import mz.co.moseguros.identity.domain.UserRoleRepository;
import mz.co.moseguros.shared.audit.ActorType;
import mz.co.moseguros.shared.audit.AuditRecord;
import mz.co.moseguros.shared.audit.AuditService;
import mz.co.moseguros.shared.domain.Channel;
import mz.co.moseguros.shared.domain.Msisdn;
import mz.co.moseguros.shared.security.CurrentUser;
import mz.co.moseguros.shared.tenant.RunAsSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Criação das contas de cliente: a do login por OTP (S1-03, telemóvel ou e-mail verificado) e a
 * encontrada ou criada pelo telemóvel quando um mediador cota em nome do cliente (S3-02 §6.1,
 * telemóvel por verificar). As duas criam a conta {@code ACTIVE} com a língua por defeito, o
 * {@code customer_profile} vazio, o perfil {@code CUSTOMER} e a auditoria {@code CUSTOMER.REGISTER}.
 */
@Component
public class CustomerAccountFactory {

    private static final Logger log = LoggerFactory.getLogger(CustomerAccountFactory.class);

    static final String CUSTOMER_ROLE = "CUSTOMER";
    static final String SOURCE_FIND_OR_CREATE = "FIND_OR_CREATE";

    private final UserAccountRepository accounts;
    private final CustomerProfileRepository profiles;
    private final UserRoleRepository userRoles;
    private final AuditService audit;
    private final CurrentUser currentUser;
    private final TransactionTemplate newTransaction;

    @Autowired
    public CustomerAccountFactory(UserAccountRepository accounts, CustomerProfileRepository profiles,
                                  UserRoleRepository userRoles, AuditService audit, CurrentUser currentUser,
                                  PlatformTransactionManager transactionManager) {
        this.accounts = accounts;
        this.profiles = profiles;
        this.userRoles = userRoles;
        this.audit = audit;
        this.currentUser = currentUser;
        this.newTransaction = new TransactionTemplate(transactionManager);
        this.newTransaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    /** Só para {@link #createVerified} (o {@code OtpService} construído nos testes unitários do S1-03). */
    CustomerAccountFactory(UserAccountRepository accounts, CustomerProfileRepository profiles,
                           UserRoleRepository userRoles, AuditService audit) {
        this.accounts = accounts;
        this.profiles = profiles;
        this.userRoles = userRoles;
        this.audit = audit;
        this.currentUser = null;
        this.newTransaction = null;
    }

    /**
     * Conta criada no login por OTP (S1-03), com o identificador já verificado. Corre na transacção
     * de quem chama (o {@code verify}, em {@code SYSTEM}); o actor auditado é o próprio cliente.
     */
    UserAccount createVerified(OtpIdentifier identifier, Channel channel) {
        UserAccount account = identifier.type() == IdentifierType.MSISDN
                ? UserAccount.newCustomer(identifier.msisdn())
                : UserAccount.newCustomer(identifier.email());
        Map<String, Object> after = new LinkedHashMap<>();
        after.put("type", AccountType.CUSTOMER.name());
        after.put("identifierType", identifier.type().name());
        after.put("identifier", identifier.masked());
        after.put("channel", channel.name());
        return register(account, ActorType.CUSTOMER, account.getId().toString(), after);
    }

    /**
     * Id da conta {@code CUSTOMER} com este telemóvel (em qualquer estado, também {@code DISABLED});
     * se não existir, cria-a com o telemóvel por verificar ({@link UserAccount#newUnverifiedCustomer}).
     *
     * <p>Corre em {@code SYSTEM} e numa transacção própria, para que duas chamadas simultâneas para
     * o mesmo número não criem duas contas: o índice único {@code ux_user_customer_msisdn} decide e
     * a chamada que perde volta a ler a conta criada pela outra.
     */
    @RunAsSystem
    public UUID findOrCreateByMsisdn(Msisdn msisdn) {
        Optional<UUID> existing = findByMsisdn(msisdn);
        if (existing.isPresent()) {
            return existing.get();
        }
        try {
            return newTransaction.execute(status -> {
                Optional<UserAccount> again = accounts.findByTypeAndMsisdn(AccountType.CUSTOMER, msisdn);
                if (again.isPresent()) {
                    return again.get().getId();
                }
                UserAccount account = UserAccount.newUnverifiedCustomer(msisdn);
                Map<String, Object> after = new LinkedHashMap<>();
                after.put("type", AccountType.CUSTOMER.name());
                after.put("identifierType", IdentifierType.MSISDN.name());
                after.put("identifier", msisdn.masked());
                after.put("msisdnVerified", false);
                after.put("source", SOURCE_FIND_OR_CREATE);
                boolean staff = currentUser.isAuthenticated();
                register(account, staff ? ActorType.STAFF : ActorType.SYSTEM,
                        staff ? currentUser.id().toString() : "system", after);
                accounts.flush();
                log.info("Customer account {} created for {} (find or create)", account.getId(), msisdn.masked());
                return account.getId();
            });
        } catch (DataIntegrityViolationException e) {
            // Outra chamada criou a conta entretanto (índice único): devolve essa.
            return findByMsisdn(msisdn).orElseThrow(() -> e);
        }
    }

    private Optional<UUID> findByMsisdn(Msisdn msisdn) {
        return Optional.ofNullable(newTransaction.execute(status ->
                accounts.findByTypeAndMsisdn(AccountType.CUSTOMER, msisdn).map(UserAccount::getId).orElse(null)));
    }

    private UserAccount register(UserAccount account, ActorType actorType, String actorId, Map<String, Object> after) {
        accounts.save(account);
        profiles.save(CustomerProfile.emptyFor(account));
        userRoles.save(new UserRole(account.getId(), CUSTOMER_ROLE));
        audit.record(new AuditRecord(actorType, actorId, null, "CUSTOMER.REGISTER", "UserAccount",
                account.getId().toString(), null, after));
        return account;
    }
}
