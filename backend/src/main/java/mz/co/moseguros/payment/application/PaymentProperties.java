package mz.co.moseguros.payment.application;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Parâmetros do núcleo de pagamentos ({@code moseguros.payment}, S5-02).
 */
@Configuration
@ConfigurationProperties("moseguros.payment")
public class PaymentProperties {

    /** Linhas máximas da exportação CSV da seguradora (acima → 400 {@code EXPORT_TOO_LARGE}). */
    private int exportMaxRows = 10_000;

    /** Threads do envio assíncrono (M-Pesa). */
    private int dispatchThreads = 4;

    public int getExportMaxRows() {
        return exportMaxRows;
    }

    public void setExportMaxRows(int exportMaxRows) {
        if (exportMaxRows < 1) {
            throw new IllegalArgumentException("export-max-rows must be at least 1");
        }
        this.exportMaxRows = exportMaxRows;
    }

    public int getDispatchThreads() {
        return dispatchThreads;
    }

    public void setDispatchThreads(int dispatchThreads) {
        this.dispatchThreads = dispatchThreads;
    }
}
