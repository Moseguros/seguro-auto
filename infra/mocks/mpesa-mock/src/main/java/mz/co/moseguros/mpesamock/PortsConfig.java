package mz.co.moseguros.mpesamock;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.AbstractProtocol;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Uma porta por operação, como na API da Vodacom: o conector principal ({@code server.port},
 * 18352) serve o C2B e os endpoints de apoio; acrescentam-se os conectores da consulta (18353) e
 * da reversão (18354). Cada conector tem até 200 threads, para as esperas do C2B não bloquearem
 * outros pedidos.
 */
@Configuration(proxyBeanMethods = false)
class PortsConfig {

    static final int MAX_THREADS = 200;

    @Bean
    WebServerFactoryCustomizer<TomcatServletWebServerFactory> additionalConnectors(MockProperties props) {
        return factory -> factory.addAdditionalTomcatConnectors(
                connector(props.getPorts().getQuery()), connector(props.getPorts().getReversal()));
    }

    private static Connector connector(int port) {
        Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
        connector.setPort(port);
        if (connector.getProtocolHandler() instanceof AbstractProtocol<?> protocol) {
            protocol.setMaxThreads(MAX_THREADS);
        }
        return connector;
    }
}
