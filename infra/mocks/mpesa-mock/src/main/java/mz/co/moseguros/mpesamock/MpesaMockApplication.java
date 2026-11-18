package mz.co.moseguros.mpesamock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Simulador local da API M-Pesa da Vodacom Moçambique (C2B, consulta de estado e reversão),
 * com o mesmo formato de pedidos e respostas. Guarda as transacções em memória. Só para
 * desenvolvimento e testes: nunca recebe dinheiro nem credenciais reais.
 */
@SpringBootApplication
@EnableConfigurationProperties(MockProperties.class)
public class MpesaMockApplication {

    public static void main(String[] args) {
        SpringApplication.run(MpesaMockApplication.class, args);
    }
}
