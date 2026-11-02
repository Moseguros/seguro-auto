package mz.co.moseguros.shared.web;

import java.util.Iterator;
import java.util.Set;

import com.fasterxml.jackson.databind.JavaType;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverterContext;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.media.Schema;
import mz.co.moseguros.shared.domain.Matricula;
import mz.co.moseguros.shared.domain.Money;
import mz.co.moseguros.shared.domain.Msisdn;
import mz.co.moseguros.shared.domain.Nuit;

/**
 * Os value objects partilhados aparecem no contrato como referências aos esquemas
 * definidos em {@link OpenApiConfig#sharedSchemas()} ({@code Money} objecto;
 * {@code Msisdn}, {@code Nuit} e {@code Matricula} strings com {@code pattern}), e não
 * como os records Java.
 */
public class ValueObjectsModelConverter implements ModelConverter {

    private static final Set<Class<?>> VALUE_OBJECTS = Set.of(Money.class, Msisdn.class, Nuit.class, Matricula.class);

    @Override
    @SuppressWarnings("rawtypes")
    public Schema resolve(AnnotatedType type, ModelConverterContext context, Iterator<ModelConverter> chain) {
        JavaType javaType = Json.mapper().constructType(type.getType());
        if (javaType != null && VALUE_OBJECTS.contains(javaType.getRawClass())) {
            return new Schema<>().$ref("#/components/schemas/" + javaType.getRawClass().getSimpleName());
        }
        return chain.hasNext() ? chain.next().resolve(type, context, chain) : null;
    }
}
