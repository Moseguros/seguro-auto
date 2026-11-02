package mz.co.moseguros.shared.json;

import java.io.IOException;
import java.util.function.Function;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import mz.co.moseguros.shared.domain.Matricula;
import mz.co.moseguros.shared.domain.Money;
import mz.co.moseguros.shared.domain.Msisdn;
import mz.co.moseguros.shared.domain.Nuit;
import mz.co.moseguros.shared.domain.PlateParser;
import org.springframework.stereotype.Component;

/**
 * Serialização JSON dos value objects partilhados como strings:
 * {@link Msisdn} em E.164, {@link Nuit} com 9 dígitos e {@link Matricula} formatada.
 * {@code Money} usa a forma do record ({@code {"amount": 299900, "currency": "MZN"}}).
 *
 * <p>Ao desserializar, um valor inválido lança a {@code DomainException} do value object;
 * o Jackson acrescenta-lhe o caminho do campo e o {@code GlobalExceptionHandler}
 * devolve 400 {@code VALIDATION_ERROR} com esse campo em {@code errors[]}.
 *
 * <p>É um bean: o Spring Boot regista-o no {@code ObjectMapper} da aplicação.
 */
@Component
public class ValueObjectsModule extends SimpleModule {

    public ValueObjectsModule(PlateParser plateParser) {
        super("MoSegurosValueObjects");
        addSerializer(Msisdn.class, new StringValueSerializer<>(Msisdn.class, Msisdn::e164));
        addDeserializer(Msisdn.class, new StringValueDeserializer<>(Msisdn.class, Msisdn::of));
        addSerializer(Nuit.class, new StringValueSerializer<>(Nuit.class, Nuit::value));
        addDeserializer(Nuit.class, new StringValueDeserializer<>(Nuit.class, Nuit::of));
        addSerializer(Matricula.class, new StringValueSerializer<>(Matricula.class, Matricula::value));
        addDeserializer(Matricula.class, new StringValueDeserializer<>(Matricula.class, plateParser::parse));
        setMixInAnnotation(Money.class, MoneyMixin.class);
    }

    /** {@code Money} só tem {@code amount} e {@code currency} em JSON (sem {@code negative}). */
    @JsonIgnoreProperties({"negative"})
    abstract static class MoneyMixin {
    }

    static final class StringValueSerializer<T> extends StdScalarSerializer<T> {

        private final transient Function<T, String> toJson;

        StringValueSerializer(Class<T> type, Function<T, String> toJson) {
            super(type);
            this.toJson = toJson;
        }

        @Override
        public void serialize(T value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeString(toJson.apply(value));
        }
    }

    static final class StringValueDeserializer<T> extends StdScalarDeserializer<T> {

        private final transient Function<String, T> fromJson;

        StringValueDeserializer(Class<T> type, Function<String, T> fromJson) {
            super(type);
            this.fromJson = fromJson;
        }

        @Override
        @SuppressWarnings("unchecked")
        public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            if (!p.hasToken(JsonToken.VALUE_STRING)) {
                return (T) ctxt.handleUnexpectedToken(handledType(), p);
            }
            return fromJson.apply(p.getText());
        }
    }
}
