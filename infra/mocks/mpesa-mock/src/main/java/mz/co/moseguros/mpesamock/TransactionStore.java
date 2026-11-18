package mz.co.moseguros.mpesamock;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import org.springframework.stereotype.Component;

/** Transacções em memória (perdem-se quando o simulador reinicia). */
@Component
class TransactionStore {

    static final int LIST_LIMIT = 500;
    private static final String ID_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private final SecureRandom random = new SecureRandom();
    private final Map<String, MockTransaction> byTransactionId = new ConcurrentHashMap<>();
    private final Map<String, MockTransaction> byThirdPartyReference = new ConcurrentHashMap<>();
    private final Deque<MockTransaction> newestFirst = new ConcurrentLinkedDeque<>();
    private final Set<String> usedThirdPartyReferences = ConcurrentHashMap.newKeySet();

    /** Reserva a {@code ThirdPartyReference} de um C2B; {@code false} se já foi usada (INS-10). */
    boolean reserveThirdPartyReference(String reference) {
        return usedThirdPartyReferences.add(reference);
    }

    void add(MockTransaction transaction) {
        byTransactionId.put(transaction.getTransactionId(), transaction);
        if (transaction.getOperation() == MockTransaction.Operation.C2B) {
            byThirdPartyReference.putIfAbsent(transaction.getThirdPartyReference(), transaction);
        }
        newestFirst.addFirst(transaction);
    }

    Optional<MockTransaction> byTransactionId(String transactionId) {
        return Optional.ofNullable(byTransactionId.get(transactionId));
    }

    /** O {@code input_QueryReference} é o {@code TransactionID} ou a {@code ThirdPartyReference} do C2B. */
    Optional<MockTransaction> byQueryReference(String reference) {
        MockTransaction found = byTransactionId.get(reference);
        return Optional.ofNullable(found != null ? found : byThirdPartyReference.get(reference));
    }

    List<MockTransaction> latest() {
        List<MockTransaction> result = new ArrayList<>();
        for (MockTransaction t : newestFirst) {
            if (result.size() == LIST_LIMIT) {
                break;
            }
            result.add(t);
        }
        return result;
    }

    void reset() {
        byTransactionId.clear();
        byThirdPartyReference.clear();
        newestFirst.clear();
        usedThirdPartyReferences.clear();
    }

    /** 10 caracteres {@code [0-9A-Z]}, único entre as transacções registadas. */
    String newTransactionId() {
        while (true) {
            StringBuilder sb = new StringBuilder(10);
            for (int i = 0; i < 10; i++) {
                sb.append(ID_ALPHABET.charAt(random.nextInt(ID_ALPHABET.length())));
            }
            String id = sb.toString();
            if (!byTransactionId.containsKey(id)) {
                return id;
            }
        }
    }

    /** 32 hexadecimais. */
    String newConversationId() {
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }
}
