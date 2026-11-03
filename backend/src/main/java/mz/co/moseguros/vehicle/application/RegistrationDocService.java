package mz.co.moseguros.vehicle.application;

import java.util.UUID;

import mz.co.moseguros.document.DocumentApi;
import mz.co.moseguros.document.FileClassification;
import mz.co.moseguros.document.FileRef;
import mz.co.moseguros.document.StoreFileCommand;
import mz.co.moseguros.shared.audit.ActorType;
import mz.co.moseguros.shared.audit.AuditRecord;
import mz.co.moseguros.shared.audit.AuditService;
import mz.co.moseguros.shared.error.DomainException;
import mz.co.moseguros.shared.security.CurrentUser;
import mz.co.moseguros.vehicle.domain.Vehicle;
import mz.co.moseguros.vehicle.domain.VehicleRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Livrete do veículo (S2-03 §4): foto ou PDF guardado pelo {@link DocumentApi} (dono
 * {@value #OWNER_TYPE}, finalidade {@value #PURPOSE}). Um envio novo passa a ser o actual; o anterior
 * continua guardado e ligado (histórico).
 */
@Service
public class RegistrationDocService {

    public static final String OWNER_TYPE = "VEHICLE";
    public static final String PURPOSE = "REGISTRATION_DOC";

    private final CurrentUser currentUser;
    private final VehicleService vehicleService;
    private final VehicleRepository vehicles;
    private final DocumentApi documents;
    private final AuditService audit;

    public RegistrationDocService(CurrentUser currentUser, VehicleService vehicleService, VehicleRepository vehicles,
                                  DocumentApi documents, AuditService audit) {
        this.currentUser = currentUser;
        this.vehicleService = vehicleService;
        this.vehicles = vehicles;
        this.documents = documents;
        this.audit = audit;
    }

    /**
     * Guarda o livrete e torna-o o actual. Auditoria {@code VEHICLE.REGISTRATION_DOC_UPLOAD}.
     *
     * @throws DomainException {@code VEHICLE_NOT_FOUND} (404), {@code FILE_EMPTY} (400),
     *                         {@code FILE_TOO_LARGE} (413) ou {@code FILE_TYPE_NOT_ALLOWED} (415)
     */
    @PreAuthorize("hasAuthority('self:vehicles')")
    @Transactional
    public VehicleDetails upload(UUID vehicleId, String originalFilename, String declaredContentType, byte[] content) {
        UUID ownerId = currentUser.id();
        Vehicle vehicle = vehicleService.loadActive(vehicleId);
        // Transacção própria (REQUIRES_NEW): se esta falhar depois, o ficheiro fica órfão e é limpo.
        FileRef file = documents.store(new StoreFileCommand(OWNER_TYPE, vehicleId, PURPOSE, FileClassification.PERSONAL,
                null, originalFilename, declaredContentType, null, content));
        UUID previous = vehicle.getRegistrationDocFileId();
        vehicle.attachRegistrationDoc(file.id());
        vehicles.saveAndFlush(vehicle);
        documents.markLinked(file.id());
        audit.record(new AuditRecord(ActorType.CUSTOMER, ownerId.toString(), null, "VEHICLE.REGISTRATION_DOC_UPLOAD",
                "Vehicle", vehicleId.toString(), previous == null ? null : new DocState(previous),
                new DocState(file.id())));
        return vehicleService.details(vehicle);
    }

    /** Livrete na auditoria. */
    public record DocState(UUID fileId) {
    }
}
