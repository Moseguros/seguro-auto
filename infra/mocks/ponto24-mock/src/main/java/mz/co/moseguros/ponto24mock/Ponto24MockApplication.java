package mz.co.moseguros.ponto24mock;

import java.time.Clock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Simulador da rede Ponto24 e do banco (S5-08 §6): só para local, testes e QA. Nenhum dinheiro real é
 * movimentado; não conhece o backend.
 */
@SpringBootApplication
@EnableScheduling
public class Ponto24MockApplication {

    public static void main(String[] args) {
        SpringApplication.run(Ponto24MockApplication.class, args);
    }

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }
}
