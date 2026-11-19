package mz.co.moseguros.payment.domain;

import java.util.UUID;

/** Ficheiros (e quantos rejeitados) de uma fonte num período. */
public record SourceFileCount(UUID sourceId, long files, long rejected) {
}
