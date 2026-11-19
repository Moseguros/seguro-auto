package mz.co.moseguros.policy;

import java.time.Instant;
import java.util.UUID;

/** Apólice da lista do cliente ({@link PolicyApi#customerPolicies}, S6-01 §9). */
public record PolicySummaryView(UUID id, String number, PolicyStatus status, CoverStatus coverStatus,
                                UUID insurerId, String insurerName, String productName, CoverageType coverageType,
                                UUID vehicleId, String plate, Instant startAt, Instant endAt) {
}
