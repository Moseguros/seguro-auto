package mz.co.moseguros.proposal.api;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import mz.co.moseguros.proposal.application.InspectionService;
import mz.co.moseguros.proposal.application.InspectionService.PhotoInput;
import mz.co.moseguros.proposal.application.InspectionService.PhotoResult;
import mz.co.moseguros.proposal.application.InspectionService.Upload;
import mz.co.moseguros.proposal.application.ProposalJson;
import mz.co.moseguros.proposal.domain.PhotoSlot;
import mz.co.moseguros.proposal.domain.PhotoSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Vistoria da proposta pelo cliente dono ({@code self:proposals}) ou pelo mediador da proposta
 * ({@code proposal:create-for-client}) (S4-03 §5).
 */
@RestController
@RequestMapping("/api/v1/proposals/{id}/inspection")
@Tag(name = ProposalTag.NAME, description = ProposalTag.DESCRIPTION)
public class InspectionController {

    private static final String RULES = " Proposta de outro cliente → 404 PROPOSAL_NOT_FOUND; sem coberturas OD_* → 409 "
            + "INSPECTION_NOT_REQUIRED; proposta fora de DRAFT/INFO_REQUESTED → 409 PROPOSAL_NOT_EDITABLE; vistoria "
            + "fora de PENDING/REJECTED → 409 INSPECTION_NOT_EDITABLE. Aumenta contentRevision e renova expiresAt.";

    private final InspectionService service;
    private final ProposalJson json;

    public InspectionController(InspectionService service, ProposalJson json) {
        this.service = service;
        this.json = json;
    }

    @PostMapping(value = "/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(operationId = "uploadInspectionPhoto", summary = "Envia uma fotografia da vistoria",
            description = "Permissão self:proposals ou proposal:create-for-client. file: JPEG, PNG ou HEIC até 10 MB "
                    + "(em falta → REQUIRED; vazio → 400 FILE_EMPTY; maior → 413 FILE_TOO_LARGE; outro tipo → 415 "
                    + "FILE_TYPE_NOT_ALLOWED). slot obrigatório (INVALID_SLOT); EXTRA além do máximo → "
                    + "EXTRA_PHOTOS_LIMIT; uma fotografia nova num ângulo fixo substitui a anterior. takenAt no futuro "
                    + "(+5 min) → PHOTO_IN_FUTURE, com mais de 7 dias → PHOTO_TOO_OLD; latitude e longitude juntas e "
                    + "dentro dos limites (INVALID_COORDINATES). Com um clientPhotoId já enviado devolve 200 com a "
                    + "mesma fotografia, sem gravar nada. Numa vistoria REJECTED passa-a a PENDING." + RULES,
            responses = {
                @ApiResponse(responseCode = "201", description = "Fotografia gravada",
                        content = @Content(schema = @Schema(implementation = InspectionPhotoResponse.class))),
                @ApiResponse(responseCode = "200", description = "Repetição do mesmo clientPhotoId",
                        content = @Content(schema = @Schema(implementation = InspectionPhotoResponse.class)))
            })
    public ResponseEntity<InspectionPhotoResponse> uploadInspectionPhoto(
            @PathVariable UUID id,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @Schema(implementation = PhotoSlot.class)
            @RequestPart(value = "slot", required = false) String slot,
            @Schema(format = "uuid", description = "Gerado pelo cliente; torna o envio idempotente")
            @RequestPart(value = "clientPhotoId", required = false) String clientPhotoId,
            @Schema(implementation = PhotoSource.class, description = "Por defeito UPLOAD")
            @RequestPart(value = "source", required = false) String source,
            @Schema(format = "date-time", description = "Hora da fotografia (ISO 8601)")
            @RequestPart(value = "takenAt", required = false) String takenAt,
            @Schema(type = "number", example = "-25.965312")
            @RequestPart(value = "latitude", required = false) String latitude,
            @Schema(type = "number", example = "32.583121")
            @RequestPart(value = "longitude", required = false) String longitude) throws IOException {
        PhotoResult result = service.upload(id, file == null ? null
                        : new Upload(file.getBytes(), file.getOriginalFilename(), file.getContentType()),
                new PhotoInput(slot, clientPhotoId, source, takenAt, latitude, longitude));
        InspectionPhotoResponse body = InspectionPhotoResponse.from(result.photo());
        if (!result.created()) {
            return ResponseEntity.ok(body);
        }
        return ResponseEntity.created(URI.create("/api/v1/proposals/" + id + "/inspection/photos/" + body.id()))
                .body(body);
    }

    @DeleteMapping("/photos/{photoId}")
    @Operation(operationId = "removeInspectionPhoto", summary = "Retira uma fotografia da vistoria",
            description = "Permissão self:proposals ou proposal:create-for-client. Fica guardada com removedAt. "
                    + "Inexistente ou já retirada → 404 PHOTO_NOT_FOUND." + RULES)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeInspectionPhoto(@PathVariable UUID id, @PathVariable UUID photoId) {
        service.remove(id, photoId);
    }

    @PostMapping("/submit")
    @Operation(operationId = "submitInspection", summary = "Submete a vistoria por fotografias",
            description = "Permissão self:proposals ou proposal:create-for-client. PENDING → SUBMITTED com a "
                    + "quilometragem (REQUIRED, OUT_OF_RANGE). Faltam ângulos → 409 INSPECTION_PHOTOS_MISSING "
                    + "(missingSlots); REJECTED sem fotografia nova → 409 INVALID_STATE_TRANSITION; version da "
                    + "proposta diferente → 409 CONCURRENT_MODIFICATION." + RULES)
    public ProposalResponse submitInspection(@PathVariable UUID id, @RequestBody SubmitInspectionRequest request) {
        return ProposalResponse.from(service.submit(id, request.odometerKm(), request.version()), json);
    }

    @PostMapping("/request-on-site")
    @Operation(operationId = "requestOnSiteInspection", summary = "Pede uma vistoria presencial",
            description = "Permissão self:proposals ou proposal:create-for-client. PENDING ou REJECTED → SUBMITTED "
                    + "com mode = ON_SITE; notes 10–500 (REQUIRED, TOO_SHORT, TOO_LONG); as fotografias já enviadas "
                    + "ficam. A seguradora contacta o cliente para marcar a visita; version da proposta diferente → "
                    + "409 CONCURRENT_MODIFICATION." + RULES)
    public ProposalResponse requestOnSiteInspection(@PathVariable UUID id,
                                                    @RequestBody RequestOnSiteInspectionRequest request) {
        return ProposalResponse.from(service.requestOnSite(id, request.notes(), request.version()), json);
    }
}
