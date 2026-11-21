package mz.co.moseguros.notification.api;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import mz.co.moseguros.notification.application.TemplateAdminService;
import mz.co.moseguros.notification.application.TemplateAdminService.Details;
import mz.co.moseguros.notification.domain.NotificationTemplate;
import mz.co.moseguros.notification.domain.NotificationTemplate.TemplateText;
import mz.co.moseguros.shared.web.PageRequests;
import mz.co.moseguros.shared.web.PageResponse;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Modelos das notificações (S6-04 §8; {@code notification-template:manage}, âmbito {@code PLATFORM}). */
@RestController
@RequestMapping("/api/v1/admin/notification-templates")
@Tag(name = NotificationTag.NAME, description = NotificationTag.DESCRIPTION)
public class NotificationTemplateAdminController {

    private final TemplateAdminService templates;

    public NotificationTemplateAdminController(TemplateAdminService templates) {
        this.templates = templates;
    }

    @GetMapping
    @Operation(operationId = "listNotificationTemplates", summary = "Lista os modelos das notificações",
            description = "Permissão notification-template:manage. Filtros code (prefixo), channel, locale, "
                    + "customized; por code, channel e locale.")
    public PageResponse<TemplateSummaryResponse> listNotificationTemplates(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) NotificationChannel channel,
            @RequestParam(required = false) String locale,
            @RequestParam(required = false) Boolean customized,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        return PageResponse.of(templates.list(code, channel == null ? null : channel.toDomain(), locale, customized,
                        PageRequests.of(page, size, null, Set.of(), Sort.by("code", "channel", "locale"))),
                TemplateSummaryResponse::from);
    }

    @GetMapping("/{id}")
    @Operation(operationId = "getNotificationTemplate", summary = "Consulta um modelo",
            description = "Permissão notification-template:manage. defaultSubject e defaultBody são o texto do "
                    + "ficheiro de mensagens. Inexistente → 404 TEMPLATE_NOT_FOUND.")
    public TemplateDetailResponse getNotificationTemplate(@PathVariable UUID id) {
        return TemplateDetailResponse.from(templates.get(id));
    }

    @PutMapping("/{id}")
    @Operation(operationId = "updateNotificationTemplate", summary = "Altera o texto de um modelo",
            description = "Permissão notification-template:manage. Validação: TEMPLATE_SYNTAX, "
                    + "TEMPLATE_UNKNOWN_VARIABLE, REQUIRED, TOO_LONG, SMS_TOO_LONG (errors[] de 400 "
                    + "VALIDATION_ERROR); version diferente → 409 CONCURRENT_MODIFICATION. Fica customized.")
    public TemplateDetailResponse updateNotificationTemplate(@PathVariable UUID id,
                                                             @Valid @RequestBody UpdateTemplateRequest request) {
        return TemplateDetailResponse.from(templates.update(id, new TemplateText(request.subject(), request.body(),
                request.whatsappTemplateName(), request.whatsappParams()), request.active() == null
                || request.active(), request.version()));
    }

    @PostMapping("/{id}/reset")
    @Operation(operationId = "resetNotificationTemplate", summary = "Repõe o texto do ficheiro",
            description = "Permissão notification-template:manage. customized passa a false.")
    public TemplateDetailResponse resetNotificationTemplate(@PathVariable UUID id) {
        return TemplateDetailResponse.from(templates.reset(id));
    }

    @PostMapping("/{id}/preview")
    @Operation(operationId = "previewNotificationTemplate", summary = "Pré-visualiza um texto",
            description = "Permissão notification-template:manage. Não grava. Sem params usa os valores de "
                    + "exemplo. smsText, length, limit e fits só em SMS. Erros de validação como no PUT.")
    public TemplatePreviewResponse previewNotificationTemplate(@PathVariable UUID id,
                                                               @Valid @RequestBody PreviewTemplateRequest request) {
        TemplateAdminService.Preview p = templates.preview(id, request.subject(), request.body(), request.params());
        return new TemplatePreviewResponse(p.subject(), p.body(), p.smsText(), p.length(), p.limit(), p.fits());
    }

    @PostMapping("/{id}/test")
    @Operation(operationId = "testNotificationTemplate", summary = "Envia uma mensagem de teste",
            description = "Permissão notification-template:manage. Envia já, com os valores de exemplo e o texto "
                    + "gravado, sem alternativa: telemóvel para SMS/WHATSAPP (INVALID_MSISDN), e-mail para EMAIL "
                    + "(INVALID_EMAIL), token FCM para PUSH.")
    public TemplateTestResponse testNotificationTemplate(@PathVariable UUID id,
                                                         @Valid @RequestBody TestTemplateRequest request) {
        TemplateAdminService.TestResult result = templates.test(id, request.destination());
        return new TemplateTestResponse(result.status(), result.reason());
    }

    @Schema(name = "NotificationTemplateSummary")
    public record TemplateSummaryResponse(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) UUID id,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "POLICY_ISSUED") String code,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) NotificationChannel channel,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "pt") String locale,
            String subject,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) boolean active,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) boolean customized,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) Instant updatedAt,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String updatedBy) {

        static TemplateSummaryResponse from(NotificationTemplate t) {
            return new TemplateSummaryResponse(t.getId(), t.getCode(), NotificationChannel.from(t.getChannel()),
                    t.getLocale(), t.getSubject(), t.isActive(), t.isCustomized(), t.getUpdatedAt(), t.getUpdatedBy());
        }
    }

    @Schema(name = "NotificationTemplateDetail")
    public record TemplateDetailResponse(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) UUID id,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String code,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) NotificationChannel channel,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String locale,
            String subject,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String body,
            String whatsappTemplateName,
            List<String> whatsappParams,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) List<String> variables,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) Map<String, String> sampleParams,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) boolean active,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) boolean customized,
            String defaultSubject,
            String defaultBody,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) long version,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) Instant updatedAt,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String updatedBy) {

        static TemplateDetailResponse from(Details d) {
            NotificationTemplate t = d.template();
            return new TemplateDetailResponse(t.getId(), t.getCode(), NotificationChannel.from(t.getChannel()),
                    t.getLocale(), t.getSubject(), t.getBody(), t.getWhatsappTemplateName(),
                    t.getWhatsappTemplateName() == null ? null : t.getWhatsappParams(), t.getVariables(),
                    t.getSampleParams(), t.isActive(), t.isCustomized(), d.defaultSubject(), d.defaultBody(),
                    t.getVersion(), t.getUpdatedAt(), t.getUpdatedBy());
        }
    }

    @Schema(name = "UpdateNotificationTemplateRequest")
    public record UpdateTemplateRequest(
            @Size(max = 1000) String subject,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String body,
            @Size(max = 100) String whatsappTemplateName,
            @Size(max = 20) List<String> whatsappParams,
            Boolean active,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) Long version) {
    }

    @Schema(name = "PreviewNotificationTemplateRequest")
    public record PreviewTemplateRequest(
            @Size(max = 1000) String subject,
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String body,
            Map<String, String> params) {
    }

    /** {@code smsText}, {@code length}, {@code limit} e {@code fits} só em SMS. */
    @Schema(name = "NotificationTemplatePreview")
    public record TemplatePreviewResponse(String subject, String body, String smsText, Integer length, Integer limit,
                                          Boolean fits) {
    }

    @Schema(name = "TestNotificationTemplateRequest")
    public record TestTemplateRequest(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED) @Size(max = 512) String destination) {
    }

    /** @param status {@code SENT} ou {@code FAILED} */
    @Schema(name = "NotificationTemplateTestResult")
    public record TemplateTestResponse(
            @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "SENT") String status,
            String reason) {
    }
}
