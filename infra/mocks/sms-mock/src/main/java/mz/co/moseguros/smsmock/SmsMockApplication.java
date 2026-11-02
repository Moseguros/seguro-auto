package mz.co.moseguros.smsmock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Simulador local de envio de SMS. Guarda as mensagens em memória para serem vistas em
 * {@code GET /} ou {@code GET /sms}. Só para desenvolvimento.
 */
@SpringBootApplication
public class SmsMockApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmsMockApplication.class, args);
    }
}
