package mz.co.moseguros.rating;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/** Condutor a tarifar (S3-01 §8), copiado do {@code DriverSnapshot} pela S3-02. */
public record RatedDriver(UUID driverId, LocalDate birthDate, LocalDate licenceIssueDate,
                          List<String> licenceCategories) {
}
