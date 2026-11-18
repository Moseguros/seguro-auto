package mz.co.moseguros.mpesamock;

import static mz.co.moseguros.mpesamock.MpesaSupport.orEmpty;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Consulta de estado: {@code GET :18353/ipg/v1x/queryTransactionStatus/}. O
 * {@code input_QueryReference} é o {@code TransactionID} ou a {@code ThirdPartyReference} do C2B
 * original; uma referência desconhecida dá INS-0 com o estado {@code N/A}.
 */
@RestController
class QueryController {

    static final String UNKNOWN_STATUS = "N/A";

    private static final Logger log = LoggerFactory.getLogger(QueryController.class);

    private final AuthVerifier auth;
    private final TransactionStore store;
    private final MockProperties props;

    QueryController(AuthVerifier auth, TransactionStore store, MockProperties props) {
        this.auth = auth;
        this.store = store;
        this.props = props;
    }

    @GetMapping({PortGuardFilter.QUERY_PATH + "/", PortGuardFilter.QUERY_PATH})
    ResponseEntity<Map<String, String>> query(HttpServletRequest request,
            @RequestParam(name = "input_QueryReference", required = false) String queryReference,
            @RequestParam(name = "input_ServiceProviderCode", required = false) String serviceProviderCode,
            @RequestParam(name = "input_ThirdPartyReference", required = false) String thirdPartyReference) {
        Optional<ResponseCodes> error = auth.verify(request);
        if (error.isEmpty() && Stream.of(queryReference, serviceProviderCode, thirdPartyReference)
                .anyMatch(MpesaSupport::missing)) {
            error = Optional.of(ResponseCodes.INS_20);
        }
        if (error.isEmpty() && !serviceProviderCode.equals(props.getServiceProviderCode())) {
            error = Optional.of(ResponseCodes.INS_13);
        }
        if (error.isPresent()) {
            return answer(error.get(), "", queryReference, thirdPartyReference);
        }
        String status = store.byQueryReference(queryReference)
                .map(t -> t.getStatus().name())
                .orElse(UNKNOWN_STATUS);
        return answer(ResponseCodes.INS_0, status, queryReference, thirdPartyReference);
    }

    private ResponseEntity<Map<String, String>> answer(ResponseCodes code, String status, String queryReference,
            String thirdPartyReference) {
        log.info("QUERY {} queryReference={} status={}", code.code(), orEmpty(queryReference), status);
        Map<String, String> out = MpesaSupport.output(store.newConversationId(), code);
        out.put("output_ResponseTransactionStatus", status);
        out.put("output_ThirdPartyReference", orEmpty(thirdPartyReference));
        return MpesaSupport.respond(code.httpStatus(), out);
    }
}
