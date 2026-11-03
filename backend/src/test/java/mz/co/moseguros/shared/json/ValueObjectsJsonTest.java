package mz.co.moseguros.shared.json;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import mz.co.moseguros.shared.domain.Matricula;
import mz.co.moseguros.shared.domain.Money;
import mz.co.moseguros.shared.domain.Msisdn;
import mz.co.moseguros.shared.domain.Nuit;
import mz.co.moseguros.shared.domain.PlateParser;
import mz.co.moseguros.shared.domain.StaticPlateFormatProvider;
import mz.co.moseguros.shared.error.DomainException;
import org.junit.jupiter.api.Test;

class ValueObjectsJsonTest {

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new ValueObjectsModule(
                    new PlateParser(StaticPlateFormatProvider.of(StaticPlateFormatProvider.STANDARD))));

    record Holder(Msisdn msisdn, Nuit nuit, Matricula plate, Money premium) {
    }

    @Test
    void moneyRoundTrip() throws Exception {
        String json = mapper.writeValueAsString(Money.ofCentavos(299900));
        assertThat(json).isEqualTo("{\"amount\":299900,\"currency\":\"MZN\"}");
        assertThat(mapper.readValue(json, Money.class)).isEqualTo(Money.ofCentavos(299900));
    }

    @Test
    void moneyWithOtherCurrencyFails() {
        assertThatThrownBy(() -> mapper.readValue("{\"amount\":1,\"currency\":\"USD\"}", Money.class))
                .isInstanceOf(JsonMappingException.class)
                .hasRootCauseInstanceOf(DomainException.class);
    }

    @Test
    void msisdnRoundTrip() throws Exception {
        assertThat(mapper.writeValueAsString(Msisdn.of("841234567"))).isEqualTo("\"+258841234567\"");
        assertThat(mapper.readValue("\"84 123 4567\"", Msisdn.class)).isEqualTo(Msisdn.of("841234567"));
    }

    @Test
    void nuitRoundTrip() throws Exception {
        assertThat(mapper.writeValueAsString(Nuit.of("123456789"))).isEqualTo("\"123456789\"");
        assertThat(mapper.readValue("\"123 456 789\"", Nuit.class)).isEqualTo(Nuit.of("123456789"));
    }

    @Test
    void matriculaRoundTripUsesThePlateParser() throws Exception {
        Matricula parsed = mapper.readValue("\"abc 123 mc\"", Matricula.class);
        assertThat(parsed).isEqualTo(Matricula.restore("ABC-123-MC", "STANDARD"));
        assertThat(mapper.writeValueAsString(parsed)).isEqualTo("\"ABC-123-MC\"");
    }

    @Test
    void objectRoundTrip() throws Exception {
        String json = "{\"msisdn\":\"+258841234567\",\"nuit\":\"123456789\",\"plate\":\"ABC-123-MC\","
                + "\"premium\":{\"amount\":100,\"currency\":\"MZN\"}}";
        Holder holder = mapper.readValue(json, Holder.class);
        assertThat(holder.msisdn()).isEqualTo(Msisdn.of("841234567"));
        assertThat(mapper.readTree(mapper.writeValueAsString(holder))).isEqualTo(mapper.readTree(json));
    }

    @Test
    void invalidValueKeepsDomainCodeAndFieldPath() {
        assertThatThrownBy(() -> mapper.readValue("{\"msisdn\":\"123\"}", Holder.class))
                .isInstanceOfSatisfying(JsonMappingException.class, e -> {
                    assertThat(e.getPath()).extracting(JsonMappingException.Reference::getFieldName)
                            .containsExactly("msisdn");
                    assertThat(e).hasRootCauseInstanceOf(DomainException.class);
                    assertThat(((DomainException) e.getCause()).getCode()).isEqualTo("INVALID_MSISDN");
                });
    }

    @Test
    void nonStringValueIsRejected() {
        assertThatThrownBy(() -> mapper.readValue("841234567", Msisdn.class))
                .isInstanceOf(MismatchedInputException.class);
    }
}
