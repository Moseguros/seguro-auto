package mz.co.moseguros.mpesamock;

import static mz.co.moseguros.mpesamock.MpesaSupport.orEmpty;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.Clock;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Reversão: {@code PUT :18354/ipg/v1x/reversal/}. Só uma transacção C2B {@code Completed} pode
 * ser revertida (total ou parcialmente); passa a {@code Reversed} e a reversão fica registada com
 * um {@code TransactionID} novo. Na MoSeguros as devoluções fazem-se fora da plataforma: isto existe
 * para o simulador cobrir a API.
 */
@RestController
class ReversalController {

    private static final Logger log = LoggerFactory.getLogger(ReversalController.class);

    private final AuthVerifier auth;
    private final TransactionStore store;
    private final MockProperties props;
    private final Clock clock = Clock.systemUTC();

    ReversalController(AuthVerifier auth, TransactionStore store, MockProperties props) {
        this.auth = auth;
        this.store = store;
        this.props = props;
    }

    @PutMapping({PortGuardFilter.REVERSAL_PATH + "/", PortGuardFilter.REVERSAL_PATH})
    ResponseEntity<Map<String, String>> reverse(HttpServletRequest request,
            @RequestBody(required = false) String body) {
        Map<String, String> fields = MpesaSupport.parseBody(body);
        String thirdPartyReference = fields == null ? "" : orEmpty(fields.get("input_ThirdPartyReference"));

        Optional<ResponseCodes> authError = auth.verify(request);
        if (authError.isPresent()) {
            return reject(authError.get(), thirdPartyReference);
        }
        if (fields == null) {
            return reject(ResponseCodes.INS_21, thirdPartyReference);
        }
        String transactionId = fields.get("input_TransactionID");
        String securityCredential = fields.get("input_SecurityCredential");
        String initiatorIdentifier = fields.get("input_InitiatorIdentifier");
        String serviceProviderCode = fields.get("input_ServiceProviderCode");
        String reversalAmount = fields.get("input_ReversalAmount");
        if (Stream.of(transactionId, securityCredential, initiatorIdentifier, thirdPartyReference,
                serviceProviderCode).anyMatch(MpesaSupport::missing)) {
            return reject(ResponseCodes.INS_20, thirdPartyReference);
        }
        if (!serviceProviderCode.equals(props.getServiceProviderCode())) {
            return reject(ResponseCodes.INS_13, thirdPartyReference);
        }
        if (!initiatorIdentifier.equals(props.getInitiatorIdentifier())) {
            return reject(ResponseCodes.INS_24, thirdPartyReference);
        }
        if (!securityCredential.equals(props.getSecurityCredential())) {
            return reject(ResponseCodes.INS_25, thirdPartyReference);
        }
        boolean partial = !MpesaSupport.missing(reversalAmount);
        if (partial && !MpesaSupport.validAmount(reversalAmount)) {
            return reject(ResponseCodes.INS_15, thirdPartyReference);
        }
        Optional<MockTransaction> original = store.byTransactionId(transactionId)
                .filter(t -> t.getOperation() == MockTransaction.Operation.C2B)
                .filter(t -> t.getStatus() == MockTransaction.Status.Completed);
        if (original.isEmpty()) {
            return reject(ResponseCodes.INS_18, thirdPartyReference);
        }
        MockTransaction c2b = original.get();
        if (partial && new BigDecimal(reversalAmount).compareTo(new BigDecimal(c2b.getAmount())) > 0) {
            return reject(ResponseCodes.INS_15, thirdPartyReference);
        }
        if (!c2b.reverse()) {
            // Outra reversão concorrente chegou primeiro.
            return reject(ResponseCodes.INS_18, thirdPartyReference);
        }
        MockTransaction reversal = new MockTransaction(MockTransaction.Operation.REVERSAL, store.newTransactionId(),
                store.newConversationId(), thirdPartyReference, null, c2b.getMsisdn(),
                partial ? reversalAmount : c2b.getAmount(), c2b.getTransactionId(), clock.instant(),
                MockTransaction.Status.Pending, null);
        reversal.complete(MockTransaction.Status.Completed, ResponseCodes.INS_0.code(), clock.instant());
        store.add(reversal);
        log.info("REVERSAL {} transactionId={} reversalTransactionId={}", ResponseCodes.INS_0.code(),
                c2b.getTransactionId(), reversal.getTransactionId());
        Map<String, String> out = MpesaSupport.output(reversal.getConversationId(), ResponseCodes.INS_0);
        out.put("output_ThirdPartyReference", thirdPartyReference);
        out.put("output_TransactionID", reversal.getTransactionId());
        return MpesaSupport.respond(ResponseCodes.INS_0.httpStatus(), out);
    }

    private ResponseEntity<Map<String, String>> reject(ResponseCodes code, String thirdPartyReference) {
        log.info("REVERSAL {} thirdPartyReference={}", code.code(), thirdPartyReference);
        Map<String, String> out = MpesaSupport.output(store.newConversationId(), code);
        out.put("output_ThirdPartyReference", thirdPartyReference);
        out.put("output_TransactionID", "");
        return MpesaSupport.respond(code.httpStatus(), out);
    }
}
