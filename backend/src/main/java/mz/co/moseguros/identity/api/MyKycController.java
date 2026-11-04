package mz.co.moseguros.identity.api;

import java.io.IOException;
import java.net.URI;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import mz.co.moseguros.identity.application.KycService;
import mz.co.moseguros.identity.application.KycService.Upload;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * KYC do cliente autenticado (S4-01 §3; só clientes, {@code self:profile}; staff → 403).
 */
@RestController
@RequestMapping("/api/v1/me/kyc")
@Tag(name = IdentityTag.NAME, description = IdentityTag.DESCRIPTION)
public class MyKycController {

    private final KycService service;

    public MyKycController(KycService service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(operationId = "submitMyKyc", summary = "Envia o documento e a selfie para verificação",
            description = "Os dados do documento vêm do perfil. Perfil incompleto → 409 PROFILE_INCOMPLETE; "
                    + "verificação em análise → 409 KYC_ALREADY_PENDING; identidade já verificada (e perfil sem "
                    + "alterações) → 409 KYC_ALREADY_VERIFIED. Partes em falta ou inválidas → 400 VALIDATION_ERROR "
                    + "(insurerId: REQUIRED ou INVALID_INSURER; docFront e selfie: REQUIRED; docBack: REQUIRED no BI "
                    + "e no DIRE, NOT_ALLOWED no passaporte). Cada ficheiro: JPEG, PNG ou HEIC até 10 MB (vazio → 400 "
                    + "FILE_EMPTY; maior → 413 FILE_TOO_LARGE; outro tipo → 415 FILE_TYPE_NOT_ALLOWED).")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<KycCheckResponse> submitMyKyc(
            @Schema(format = "uuid", description = "Seguradora a quem a verificação é enviada")
            @RequestPart(value = "insurerId", required = false) String insurerId,
            @RequestPart(value = "docFront", required = false) MultipartFile docFront,
            @RequestPart(value = "docBack", required = false) MultipartFile docBack,
            @RequestPart(value = "selfie", required = false) MultipartFile selfie) throws IOException {
        KycCheckResponse body = KycCheckResponse.from(service.submit(insurerId, upload(docFront), upload(docBack),
                upload(selfie)));
        return ResponseEntity.created(URI.create("/api/v1/me/kyc")).body(body);
    }

    @GetMapping
    @Operation(operationId = "getMyKyc", summary = "Estado da verificação de identidade do cliente",
            description = "state: NONE (sem verificações; check omitido), PENDING, VERIFIED, REJECTED ou OUTDATED "
                    + "(verificada, mas o perfil mudou depois). check é a verificação mais recente.")
    public MyKycResponse getMyKyc() {
        return MyKycResponse.from(service.getMine());
    }

    private static Upload upload(MultipartFile file) throws IOException {
        return file == null ? null : new Upload(file.getBytes(), file.getOriginalFilename(), file.getContentType());
    }
}
