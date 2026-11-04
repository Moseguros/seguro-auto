package mz.co.moseguros.proposal.api;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import mz.co.moseguros.shared.domain.Money;
import mz.co.moseguros.shared.domain.Province;
import mz.co.moseguros.shared.domain.VehicleCategory;
import mz.co.moseguros.shared.domain.VehicleUsage;
import mz.co.moseguros.vehicle.NewDriver;
import mz.co.moseguros.vehicle.NewVehicle;

/**
 * Proposta criada pelo mediador a partir da sua cotação ({@code createProposalForClient}, S4-06 §5): o
 * veículo e o condutor completos (registados na conta do cliente) e, só se o perfil do cliente estiver
 * incompleto, o tomador.
 */
public record CreateBrokerProposalRequest(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "Cotação OFFERED do mediador")
        UUID quoteId,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        BrokerProposalVehicleRequest vehicle,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        BrokerProposalDriverRequest driver,
        @Schema(description = "Obrigatório se o perfil do cliente estiver incompleto; proibido (NOT_ALLOWED) se "
                + "estiver completo")
        BrokerPolicyholderRequest policyholder) {

    /**
     * Veículo completo, com as regras do pedido de veículo do S2-03. Os campos usados na tarifa têm de ser
     * iguais aos da cotação (senão 409 {@code QUOTE_DATA_CHANGED}).
     */
    public record BrokerProposalVehicleRequest(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                    description = "Num dos formatos activos (espaços, hífens e minúsculas aceites)",
                    example = "ABC-123-MC")
            String plate,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                    description = "5–30 letras ou dígitos (espaços e hífens ignorados)", example = "JTDBR32E720012345")
            String chassisNumber,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 60, example = "Toyota")
            String make,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 60, example = "Corolla")
            String model,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                    description = "De 1900 ao próximo ano (em Africa/Maputo)", example = "2024")
            Integer year,
            @Schema(description = "1–30 000; obrigatório excepto em TRAILER, onde tem de faltar", example = "1600")
            Integer engineCc,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            VehicleCategory category,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                    description = "TRAILER: 0; MOTORCYCLE: 1–3; outras: 1–100", example = "5")
            Integer seats,
            @Schema(description = "1–100 000; obrigatório em HEAVY e TRAILER", example = "40000")
            Integer grossWeightKg,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            VehicleUsage usage,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
            Province provinceOfCirculation,
            @Schema(description = "Valor comercial em centavos (MZN); a ausência conta como valor na comparação "
                    + "com a cotação")
            Money commercialValue,
            @Schema(description = "Localizador instalado (defeito false)")
            Boolean hasTracker) {

        NewVehicle toNewVehicle() {
            return new NewVehicle(plate, chassisNumber, make, model, year, engineCc, category, seats, grossWeightKg,
                    usage, provinceOfCirculation, commercialValue, hasTracker);
        }
    }

    /**
     * Condutor completo, com as regras do pedido de condutor do S2-03. A data de nascimento (e a data e as
     * categorias da carta, se a cotação as tiver) têm de ser iguais às da cotação.
     */
    public record BrokerProposalDriverRequest(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, minLength = 2, maxLength = 120,
                    description = "Letras, espaços, apóstrofo e hífen; pelo menos duas palavras",
                    example = "João Carlos Sitoe")
            String fullName,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "1991-09-09")
            LocalDate birthDate,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                    description = "5–20 letras, dígitos, / ou - (espaços ignorados). Nunca é devolvido.",
                    example = "MAP-123456")
            String licenceNumber,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED,
                    description = "1–12 códigos activos em LICENCE_CATEGORY, sem repetidos")
            List<String> licenceCategories,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "2011-04-04")
            LocalDate licenceIssueDate,
            @Schema(description = "O condutor é o próprio cliente (defeito false); só um por cliente")
            Boolean isOwner) {

        NewDriver toNewDriver() {
            return new NewDriver(fullName, birthDate, licenceNumber, licenceCategories, licenceIssueDate, isOwner);
        }

        /** Sem o número da carta, para nunca ir para um log. */
        @Override
        public String toString() {
            return "BrokerProposalDriverRequest[licenceCategories=" + licenceCategories + ", isOwner=" + isOwner
                    + "]";
        }
    }
}
