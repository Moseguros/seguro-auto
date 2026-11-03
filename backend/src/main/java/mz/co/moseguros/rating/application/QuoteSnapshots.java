package mz.co.moseguros.rating.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import mz.co.moseguros.catalog.CoverageCode;
import mz.co.moseguros.rating.domain.tariff.RatedDriver;
import mz.co.moseguros.rating.domain.tariff.RatedVehicle;
import mz.co.moseguros.rating.domain.tariff.RatingInput.RequestedCoverage;
import org.springframework.stereotype.Component;

/**
 * Conversão dos snapshots do pedido ({@code vehicle_snapshot}, {@code driver_snapshot},
 * {@code requested_coverages}) e do {@code detail} dos motivos entre os records e o JSON gravado.
 */
@Component
public class QuoteSnapshots {

    private final ObjectMapper mapper;

    public QuoteSnapshots(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public JsonNode vehicle(RatedVehicle vehicle) {
        return mapper.valueToTree(vehicle);
    }

    public RatedVehicle vehicle(JsonNode json) {
        return read(json, RatedVehicle.class);
    }

    public JsonNode driver(RatedDriver driver) {
        return mapper.valueToTree(driver);
    }

    public RatedDriver driver(JsonNode json) {
        return read(json, RatedDriver.class);
    }

    /** {@code [{ "code": "VTPL", "capital": 500000000 }]} ({@code capital} {@code null} quando não pedido). */
    public JsonNode coverages(List<RequestedCoverage> coverages) {
        ArrayNode array = JsonNodeFactory.instance.arrayNode();
        for (RequestedCoverage c : coverages) {
            ObjectNode n = array.addObject();
            n.put("code", c.code().name());
            if (c.capital() == null) {
                n.putNull("capital");
            } else {
                n.put("capital", c.capital().longValue());
            }
        }
        return array;
    }

    public List<RequestedCoverage> coverages(JsonNode json) {
        List<RequestedCoverage> result = new ArrayList<>();
        for (JsonNode n : json) {
            JsonNode capital = n.get("capital");
            result.add(new RequestedCoverage(CoverageCode.valueOf(n.get("code").asText()),
                    capital == null || capital.isNull() ? null : capital.asLong()));
        }
        return result;
    }

    /** {@code detail} de um motivo; {@code null} ou vazio → {@code null}. */
    public JsonNode detail(Map<String, Object> detail) {
        return detail == null || detail.isEmpty() ? null : mapper.valueToTree(detail);
    }

    private <T> T read(JsonNode json, Class<T> type) {
        try {
            return mapper.treeToValue(json, type);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Unreadable quote snapshot", e);
        }
    }
}
