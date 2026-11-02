package mz.co.moseguros.identity.application;

import mz.co.moseguros.notification.OtpDeliveryFailed;
import mz.co.moseguros.shared.tenant.RunAsSystem;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

/**
 * Alternativa de canal por recibo de entrega: quando o fornecedor informa que não entregou um
 * código, tenta o canal seguinte se o desafio ainda estiver activo e tiver sido enviado há no
 * máximo 30 s ({@link OtpService#fallbackAfterDeliveryFailure}).
 */
@Component
public class OtpFallbackListener {

    private final OtpService otpService;

    public OtpFallbackListener(OtpService otpService) {
        this.otpService = otpService;
    }

    @ApplicationModuleListener
    @RunAsSystem
    public void on(OtpDeliveryFailed event) {
        otpService.fallbackAfterDeliveryFailure(event.correlationId());
    }
}
