package mz.co.moseguros.ponto24mock;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import mz.co.moseguros.ponto24mock.MockProperties.Format;
import mz.co.moseguros.ponto24mock.MockProperties.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Pagamentos aceites e ficheiros de movimentos enviados (S5-08 §6.3). Tudo em memória: um reinício perde o
 * histórico e as sequências. Os métodos públicos são {@code synchronized} (o cron e a API usam o mesmo
 * estado).
 */
@Service
public class SettlementService {

    private static final Logger log = LoggerFactory.getLogger(SettlementService.class);

    static final long MEPS_FEE = 1_000;   // 10,00 por registo
    static final long BMEPS_FEE = 500;    // 5,00 por registo
    static final String OFFICIAL_MEPS_ENTITY = "90002";
    static final String OFFICIAL_BMEPS_ENTITY = "90090";

    private static final DateTimeFormatter YYYYMMDD = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter BMEPS_NAME = DateTimeFormatter.ofPattern("yyMMddHHmmss");

    /** Estado de um pagamento. */
    public enum Status { PENDING_SETTLEMENT, SETTLED }

    /** Defeito pedido num ficheiro, para testar a pasta {@code error} do worker. */
    public enum Corruption { TRAILER_COUNT, TRAILER_TOTAL, LINE_LENGTH, ENTITY }

    /** Erro de negócio do simulador ({@code { "error": code }}). */
    public static class MockException extends RuntimeException {
        private final int status;
        private final String code;

        public MockException(int status, String code, String message) {
            super(message);
            this.status = status;
            this.code = code;
        }

        public int status() {
            return status;
        }

        public String code() {
            return code;
        }
    }

    /** Um pagamento aceite pela "rede". */
    public static final class Payment {
        final UUID id;
        final String entity;
        final String reference;
        final long amountCentavos;
        final Instant paidAt;
        final String channel;
        final boolean forced;
        final Instant createdAt;
        final long bankLog;
        Status status = Status.PENDING_SETTLEMENT;
        String fileName;

        Payment(UUID id, String entity, String reference, long amountCentavos, Instant paidAt, String channel,
                boolean forced, Instant createdAt, long bankLog) {
            this.id = id;
            this.entity = entity;
            this.reference = reference;
            this.amountCentavos = amountCentavos;
            this.paidAt = paidAt;
            this.channel = channel;
            this.forced = forced;
            this.createdAt = createdAt;
            this.bankLog = bankLog;
        }

        public Map<String, Object> toJson() {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", id);
            m.put("entity", entity);
            m.put("reference", reference);
            m.put("amountCentavos", amountCentavos);
            m.put("paidAt", paidAt.toString());
            m.put("channel", channel);
            m.put("forced", forced);
            m.put("status", status.name());
            m.put("fileName", fileName);
            return m;
        }
    }

    /** Um ficheiro enviado (o conteúdo fica para o reenvio). */
    public record SentFile(String name, String entity, Format format, String formatFileId, int records,
                           long totalCentavos, String destination, Instant sentAt, String corruption,
                           String resendOf, byte[] content) {

        public Map<String, Object> toJson() {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("name", name);
            m.put("entity", entity);
            m.put("format", format.name());
            m.put("formatFileId", formatFileId);
            m.put("records", records);
            m.put("totalCentavos", totalCentavos);
            m.put("destination", destination);
            m.put("sentAt", sentAt.toString());
            m.put("corruption", corruption);
            m.put("resendOf", resendOf);
            return m;
        }
    }

    private record Generated(String name, String formatFileId, byte[] content, Runnable commit) {
    }

    private final MockProperties properties;
    private final SftpDelivery delivery;
    private final Clock clock;

    private final Map<UUID, Payment> payments = new LinkedHashMap<>();
    private final List<SentFile> sent = new ArrayList<>();
    /** MEPS: dia e último número de sequência usado no dia, por entidade. */
    private final Map<String, LocalDate> mepsDay = new HashMap<>();
    private final Map<String, Integer> mepsSequence = new HashMap<>();
    /** MEPS: A005 do último ficheiro sem defeito, por entidade (A007 do seguinte). */
    private final Map<String, String> mepsLastGood = new HashMap<>();
    /** BMEPS: último ID do ficheiro por empresa e perfil. */
    private final Map<String, Integer> bmepsSequence = new HashMap<>();
    private final AtomicLong bankLog = new AtomicLong();
    private int resendCounter;

    public SettlementService(MockProperties properties, SftpDelivery delivery, Clock clock) {
        this.properties = properties;
        this.delivery = delivery;
        this.clock = clock;
    }

    // --- pagamentos ---

    public synchronized Payment pay(String entity, String reference, long amountCentavos, Instant paidAt,
                                    String channel, boolean forced) {
        Instant now = clock.instant();
        Payment p = new Payment(UUID.randomUUID(), entity, reference, amountCentavos, paidAt == null ? now : paidAt,
                channel, forced, now, bankLog.incrementAndGet());
        payments.put(p.id, p);
        log.info("Payment accepted: entity {} amount {} {}", entity, amountCentavos, forced ? "(forced)" : "");
        return p;
    }

    /** Mais recentes primeiro. */
    public synchronized List<Map<String, Object>> payments() {
        List<Payment> list = new ArrayList<>(payments.values());
        list.sort(Comparator.comparing((Payment p) -> p.createdAt).reversed());
        return list.stream().map(Payment::toJson).toList();
    }

    public synchronized List<Map<String, Object>> files() {
        List<SentFile> list = new ArrayList<>(sent);
        list.sort(Comparator.comparing(SentFile::sentAt).reversed());
        return list.stream().map(SentFile::toJson).toList();
    }

    // --- envio ---

    @Scheduled(cron = "${ponto24mock.settlement-cron}")
    public void scheduledRun() {
        try {
            run(null, null);
        } catch (RuntimeException e) {
            log.warn("Scheduled settlement failed: {}", e.getMessage());
        }
    }

    /**
     * Um ficheiro por entidade com pagamentos pendentes (só da {@code entity}, se indicada). Com um defeito e sem
     * pagamentos pendentes, envia um ficheiro sem registos com o defeito. Os pagamentos só passam a
     * {@code SETTLED} depois de um envio sem erros e sem defeito.
     *
     * @return os ficheiros enviados e os envios que falharam
     */
    public synchronized Map<String, Object> run(String entity, Corruption corruption) {
        if (entity != null) {
            requireSource(entity);
        }
        Map<String, List<Payment>> pending = new LinkedHashMap<>();
        for (Payment p : payments.values()) {
            if (p.status == Status.PENDING_SETTLEMENT && (entity == null || entity.equals(p.entity))) {
                pending.computeIfAbsent(p.entity, k -> new ArrayList<>()).add(p);
            }
        }
        if (pending.isEmpty() && corruption != null) {
            if (entity != null) {
                pending.put(entity, List.of());
            } else {
                properties.sources().forEach(s -> pending.put(s.entity(), List.of()));
            }
        }
        List<Map<String, Object>> files = new ArrayList<>();
        List<Map<String, Object>> errors = new ArrayList<>();
        for (Map.Entry<String, List<Payment>> e : pending.entrySet()) {
            Optional<Source> source = properties.source(e.getKey());
            if (source.isEmpty()) {
                errors.add(Map.of("entity", e.getKey(), "error", "UNKNOWN_ENTITY"));
                continue;
            }
            List<Payment> list = new ArrayList<>(e.getValue());
            list.sort(Comparator.comparing((Payment p) -> p.paidAt));
            try {
                SentFile file = send(source.get(), list, corruption);
                if (corruption == null) {
                    for (Payment p : list) {
                        p.status = Status.SETTLED;
                        p.fileName = file.name();
                    }
                }
                files.add(file.toJson());
            } catch (SftpDelivery.DeliveryException ex) {
                log.warn("{}; {} payments stay pending", ex.getMessage(), list.size());
                errors.add(Map.of("entity", e.getKey(), "error", "SFTP_DELIVERY_FAILED"));
            }
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("files", files);
        if (!errors.isEmpty()) {
            result.put("errors", errors);
        }
        return result;
    }

    /** Um ficheiro sem registos para a entidade. */
    public synchronized Map<String, Object> empty(String entity) {
        Source source = requireSource(entity);
        return send(source, List.of(), null).toJson();
    }

    /** O mesmo conteúdo de um ficheiro já enviado, com outro nome. */
    public synchronized Map<String, Object> resend(String fileName) {
        SentFile original = sent.stream().filter(f -> f.name().equals(fileName)).findFirst()
                .orElseThrow(() -> new MockException(404, "UNKNOWN_FILE", "No file sent with this name"));
        Source source = requireSource(original.entity());
        String suffix = ".resend" + (++resendCounter);
        String name = original.name().endsWith(".txt")
                ? original.name().substring(0, original.name().length() - 4) + suffix + ".txt"
                : original.name() + suffix;
        deliver(source, name, original.content());
        SentFile copy = new SentFile(name, original.entity(), original.format(), original.formatFileId(),
                original.records(), original.totalCentavos(), source.destination(), clock.instant(),
                original.corruption(), original.name(), original.content());
        sent.add(copy);
        log.info("File {} sent again as {} to entity {}", original.name(), name, original.entity());
        return copy.toJson();
    }

    /** Envia o exemplo oficial tal como está (MEPS para a entidade 90002, BMEPS para a 90090). */
    public synchronized Map<String, Object> officialExample(Format format) {
        String entity = format == Format.MEPS ? OFFICIAL_MEPS_ENTITY : OFFICIAL_BMEPS_ENTITY;
        Source source = requireSource(entity);
        if (source.format() != format) {
            throw new MockException(409, "FORMAT_MISMATCH", "Entity " + entity + " is configured with " + source.format());
        }
        byte[] content = example(format);
        String name = format == Format.MEPS
                ? "MEPS_" + entity + "_201211230.txt"
                : uniqueName("BMEPS_out80002508121122000000");
        deliver(source, name, content);
        SentFile file = new SentFile(name, entity, format, format == Format.MEPS ? "201211230" : "800-02-00001",
                format == Format.MEPS ? 11 : 14, format == Format.MEPS ? 5_132_000 : 8_074_000, source.destination(),
                clock.instant(), null, null, content);
        sent.add(file);
        log.info("Official {} example sent as {} to entity {}", format, name, entity);
        return file.toJson();
    }

    /** Apaga pagamentos, ficheiros e sequências. */
    public synchronized void reset() {
        payments.clear();
        sent.clear();
        mepsDay.clear();
        mepsSequence.clear();
        mepsLastGood.clear();
        bmepsSequence.clear();
        bankLog.set(0);
        resendCounter = 0;
    }

    // --- interno ---

    private SentFile send(Source source, List<Payment> list, Corruption corruption) {
        long fee = source.format() == Format.MEPS ? MEPS_FEE : BMEPS_FEE;
        List<FilePayment> filePayments = list.stream()
                .map(p -> new FilePayment(p.reference, p.amountCentavos, fee, p.paidAt, p.channel, p.bankLog))
                .toList();
        Generated g = source.format() == Format.MEPS
                ? meps(source, filePayments, corruption)
                : bmeps(source, filePayments, corruption);
        deliver(source, g.name(), g.content());
        g.commit().run();
        long total = filePayments.stream().mapToLong(FilePayment::amountCentavos).sum();
        SentFile file = new SentFile(g.name(), source.entity(), source.format(), g.formatFileId(), filePayments.size(),
                total, source.destination(), clock.instant(), corruption == null ? null : corruption.name(), null,
                g.content());
        sent.add(file);
        log.info("File {} sent to entity {} ({}): {} records, total {}{}", g.name(), source.entity(), source.format(),
                file.records(), total, corruption == null ? "" : " with defect " + corruption);
        return file;
    }

    private Generated meps(Source source, List<FilePayment> list, Corruption corruption) {
        String entity = source.entity();
        LocalDate today = LocalDate.now(clock.withZone(properties.zone()));
        int last = today.equals(mepsDay.get(entity)) ? mepsSequence.getOrDefault(entity, 0) : 0;
        if (last >= 9) {
            throw new MockException(409, "DAILY_SEQUENCE_EXHAUSTED",
                    "Entity " + entity + " already has 9 files today");
        }
        int seq = last + 1;
        String a005 = YYYYMMDD.format(today) + seq;
        List<String> lines = MepsFileWriter.lines(entity, a005, mepsLastGood.get(entity), list,
                properties.fileStyle(), properties.zone());
        if (corruption != null) {
            corrupt(lines, corruption, Format.MEPS);
        }
        Runnable commit = () -> {
            mepsDay.put(entity, today);
            mepsSequence.put(entity, seq);
            if (corruption == null) {
                mepsLastGood.put(entity, a005);
            }
        };
        return new Generated("MEPS_" + entity + "_" + a005 + ".txt", a005, MepsFileWriter.toBytes(lines), commit);
    }

    private Generated bmeps(Source source, List<FilePayment> list, Corruption corruption) {
        String key = source.companyId() + "-" + source.profileId();
        int seq = bmepsSequence.getOrDefault(key, 0) + 1;
        if (seq > 99_999) {
            throw new MockException(409, "FILE_SEQUENCE_EXHAUSTED", "BMEPS file id over 99999");
        }
        LocalDateTime now = LocalDateTime.now(clock.withZone(properties.zone()));
        List<String> lines = BmepsFileWriter.lines(source.companyId(), source.profileId(), now.toLocalDate(), seq, list,
                properties.fileStyle(), properties.zone());
        if (corruption != null) {
            corrupt(lines, corruption, Format.BMEPS);
        }
        String name = uniqueName("BMEPS_out" + source.companyId() + source.profileId() + "508" + BMEPS_NAME.format(now));
        return new Generated(name, key + "-" + MepsFileWriter.num(seq, 5), MepsFileWriter.toBytes(lines),
                () -> bmepsSequence.put(key, seq));
    }

    /** Aplica o defeito pedido às linhas já geradas. */
    static void corrupt(List<String> lines, Corruption corruption, Format format) {
        int last = lines.size() - 1;
        String trailer = lines.get(last);
        boolean meps = format == Format.MEPS;
        switch (corruption) {
            case TRAILER_COUNT -> {
                int from = 1;
                int len = meps ? 8 : 7;
                long count = Long.parseLong(trailer.substring(from, from + len)) + 1;
                lines.set(last, trailer.substring(0, from) + MepsFileWriter.num(count, len) + trailer.substring(from + len));
            }
            case TRAILER_TOTAL -> {
                int from = meps ? 9 : 8;
                int len = meps ? 17 : 16;
                long total = Long.parseLong(trailer.substring(from, from + len)) + 1;
                lines.set(last, trailer.substring(0, from) + MepsFileWriter.num(total, len) + trailer.substring(from + len));
            }
            case LINE_LENGTH -> {
                int i = Math.max(0, last - 1); // o último detalhe, ou o cabeçalho
                lines.set(i, lines.get(i) + " ");
            }
            case ENTITY -> {
                String header = lines.get(0);
                if (meps) {
                    int entity = Integer.parseInt(header.substring(39, 44));
                    lines.set(0, header.substring(0, 39) + MepsFileWriter.num((entity + 1) % 100_000, 5)
                            + header.substring(44));
                } else {
                    int company = Integer.parseInt(header.substring(1, 4));
                    lines.set(0, header.charAt(0) + MepsFileWriter.num((company + 1) % 1000, 3) + header.substring(4));
                }
            }
            default -> throw new IllegalArgumentException(corruption.name());
        }
    }

    private void deliver(Source source, String name, byte[] content) {
        String password = properties.password(source.entity()).orElseThrow(() -> new SftpDelivery.DeliveryException(
                "No SFTP password configured for entity " + source.entity() + " (" + MockProperties.PASSWORD_PREFIX
                        + source.entity() + ")", null));
        delivery.deliver(source, password, name, content);
    }

    private String uniqueName(String name) {
        String candidate = name;
        int n = 1;
        while (exists(candidate)) {
            candidate = name + "-" + n++;
        }
        return candidate;
    }

    private boolean exists(String name) {
        return sent.stream().anyMatch(f -> f.name().equals(name));
    }

    private Source requireSource(String entity) {
        return properties.source(entity)
                .orElseThrow(() -> new MockException(404, "UNKNOWN_ENTITY", "No destination for entity " + entity));
    }

    static byte[] example(Format format) {
        String name = format == Format.MEPS ? "exemplo-ficheiro-meps.txt" : "exemplo-ficheiro-bmeps.txt";
        try (InputStream in = SettlementService.class.getResourceAsStream("/examples/" + name)) {
            if (in == null) {
                throw new IllegalStateException("Missing example " + name);
            }
            return in.readAllBytes();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
