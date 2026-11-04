package mz.co.moseguros.vehicle;

import java.time.LocalDate;
import java.util.List;

/**
 * Condutor a registar na conta de um cliente por outro módulo ({@link VehicleApi#registerForCustomer},
 * S4-06 §3), ainda por validar: as regras e os códigos são os do pedido de condutor do S2-03. O número
 * da carta vem em claro: {@link #toString()} não o inclui.
 */
public record NewDriver(String fullName, LocalDate birthDate, String licenceNumber, List<String> licenceCategories,
                        LocalDate licenceIssueDate, Boolean isOwner) {

    @Override
    public String toString() {
        return "NewDriver[licenceCategories=" + licenceCategories + ", isOwner=" + isOwner + "]";
    }
}
