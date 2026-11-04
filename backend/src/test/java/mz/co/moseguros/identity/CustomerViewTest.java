package mz.co.moseguros.identity;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.UUID;

import mz.co.moseguros.shared.domain.Msisdn;
import org.junit.jupiter.api.Test;

/** {@link CustomerView#toString()} nunca mostra o documento, o NUIT, o telemóvel nem o e-mail (S4-01 §6). */
class CustomerViewTest {

    @Test
    void toStringHasNoSensitiveValues() {
        CustomerView view = new CustomerView(UUID.randomUUID(), "Ana Maria Mussa", LocalDate.of(1990, 5, 14), "BI",
                "110100123456A", "123456789", "Av. Julius Nyerere, 1234", "MAPUTO_CITY", Msisdn.of("841234567"), true,
                "ana.mussa@exemplo.co.mz", true, "pt", true, true);

        String text = view.toString();

        assertThat(text).doesNotContain("110100123456A", "123456789", "841234567", "258841234567",
                "ana.mussa@exemplo.co.mz", "ana.mussa");
        assertThat(text).contains("+258*****4567", "idDocType=BI");
    }
}
