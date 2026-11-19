package mz.co.moseguros.mpesamock;

import static mz.co.moseguros.mpesamock.MpesaSupport.mask;
import static mz.co.moseguros.mpesamock.MpesaSupport.missing;
import static mz.co.moseguros.mpesamock.MpesaSupport.orEmpty;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Clock;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * C2B (o cliente paga): {@code POST :18352/ipg/v1x/c2bPayment/singleStage/}. O resultado vem dos
 * três últimos dígitos do número ({@link Behaviour}); a espera faz-se na thread do pedido, sem
 * bloquear os outros.
 */
@RestController
class C2bController {

    private static final Logger log = LoggerFactory.getLogger(C2bController.class);

    private final AuthVerifier auth;
    private final TransactionStore store;
    private final MockProperties props;
    private final Clock clock = Clock.systemUTC();

    C2bController(AuthVerifier auth, TransactionStore store, MockProperties props) {
        this.auth = auth;
        this.store = store;
        this.props = props;
    }

    @PostMapping({PortGuardFilter.C2B_PATH + "/", PortGuardFilter.C2B_PATH})
    ResponseEntity<Map<String, String>> c2b(HttpServletRequest request, @RequestBody(required = false) String body) {
        Map<String, String> fields = MpesaSupport.parseBody(body);
        String thirdPartyReference = fields == null ? "" : orEmpty(fields.get("input_ThirdPartyReference"));
        String msisdn = fields == null ? null : fields.get("input_CustomerMSISDN");

        Optional<ResponseCodes> authError = auth.verify(request);
        if (authError.isPresent()) {
            return reject(authError.get(), thirdPartyReference, msisdn);
        }
        if (fields == null) {
            return reject(ResponseCodes.INS_21, thirdPartyReference, msisdn);
        }
        String transactionReference = fields.get("input_TransactionReference");
        String amount = fields.get("input_Amount");
        String serviceProviderCode = fields.get("input_ServiceProviderCode");
        if (Stream.of(transactionReference, msisdn, amount, thirdPartyReference, serviceProviderCode)
                .anyMatch(MpesaSupport::missing)) {
            return reject(ResponseCodes.INS_20, thirdPartyReference, msisdn);
        }
        if (!MpesaSupport.REFERENCE.matcher(transactionReference).matches()
                || !MpesaSupport.MSISDN.matcher(msisdn).matches()
                || !MpesaSupport.validAmount(amount)
                || !MpesaSupport.REFERENCE.matcher(thirdPartyReference).matches()) {
            return reject(ResponseCodes.INS_21, thirdPartyReference, msisdn);
        }
        if (!serviceProviderCode.equals(props.getServiceProviderCode())) {
            return reject(ResponseCodes.INS_13, thirdPartyReference, msisdn);
        }

        String conversationId = store.newConversationId();
        if (!store.reserveThirdPartyReference(thirdPartyReference)) {
            MockTransaction duplicate = register(conversationId, thirdPartyReference, transactionReference, msisdn,
                    amount);
            duplicate.complete(MockTransaction.Status.Cancelled, ResponseCodes.INS_10.code(), clock.instant());
            return answer(duplicate, ResponseCodes.INS_10);
        }

        Behaviour behaviour = Behaviour.forMsisdn(msisdn);
        MockTransaction transaction = register(conversationId, thirdPartyReference, transactionReference, msisdn,
                amount);
        sleep(behaviour.delaySeconds(props));
        // Conclui mesmo que o cliente já tenha desistido (o …003 fica Completed depois do timeout dele).
        transaction.complete(behaviour.finalStatus(), behaviour.responseCode().code(), clock.instant());
        return answer(transaction, behaviour.responseCode());
    }

    private MockTransaction register(String conversationId, String thirdPartyReference, String transactionReference,
            String msisdn, String amount) {
        MockTransaction transaction = new MockTransaction(MockTransaction.Operation.C2B, store.newTransactionId(),
                conversationId, thirdPartyReference, transactionReference, msisdn, amount, null, clock.instant(),
                MockTransaction.Status.Pending, null);
        store.add(transaction);
        return transaction;
    }

    private ResponseEntity<Map<String, String>> answer(MockTransaction transaction, ResponseCodes code) {
        log.info("C2B {} msisdn={} thirdPartyReference={} transactionId={}", code.code(),
                mask(transaction.getMsisdn()), transaction.getThirdPartyReference(), transaction.getTransactionId());
        Map<String, String> out = MpesaSupport.output(transaction.getConversationId(), code);
        out.put("output_ThirdPartyReference", transaction.getThirdPartyReference());
        out.put("output_TransactionID", transaction.getTransactionId());
        int status = code == ResponseCodes.INS_0 ? ResponseCodes.C2B_SUCCESS_HTTP_STATUS : code.httpStatus();
        return MpesaSupport.respond(status, out);
    }

    /** Erros de autenticação, campos e código de serviço: nada fica registado e o TransactionID vem vazio. */
    private ResponseEntity<Map<String, String>> reject(ResponseCodes code, String thirdPartyReference, String msisdn) {
        log.info("C2B {} msisdn={} thirdPartyReference={}", code.code(), missing(msisdn) ? "-" : mask(msisdn),
                thirdPartyReference);
        Map<String, String> out = MpesaSupport.output(store.newConversationId(), code);
        out.put("output_ThirdPartyReference", thirdPartyReference);
        out.put("output_TransactionID", "");
        return MpesaSupport.respond(code.httpStatus(), out);
    }

    private static void sleep(int seconds) {
        if (seconds <= 0) {
            return;
        }
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
