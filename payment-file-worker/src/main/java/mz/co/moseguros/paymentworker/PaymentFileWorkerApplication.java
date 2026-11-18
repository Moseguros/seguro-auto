package mz.co.moseguros.paymentworker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * {@code payment-file-worker} (S5-09, ADR-004, ADR-007): pede ao backend as fontes de ficheiros de
 * movimentos (token de serviço), lê cada uma continuamente por SFTP, valida os ficheiros MEPS/BMEPS e
 * publica cada pagamento no RabbitMQ com <i>publisher confirms</i>. Um ficheiro nunca é processado
 * duas vezes (SHA-256 do conteúdo e identificador do ficheiro no formato). Sem endpoints de negócio:
 * só o Actuator na 8081.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class PaymentFileWorkerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentFileWorkerApplication.class, args);
    }
}
