package mz.co.moseguros.shared.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

/**
 * {@link ClientIpResolver} (S0-03, decisão D15): o {@code X-Forwarded-For} só conta quando o pedido vem
 * de um proxy de confiança.
 */
class ClientIpResolverTest {

    private static MockHttpServletRequest request(String remote, String forwardedFor) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr(remote);
        if (forwardedFor != null) {
            request.addHeader("X-Forwarded-For", forwardedFor);
        }
        return request;
    }

    private static ClientIpResolver resolver(String... trusted) {
        return new ClientIpResolver(new ClientIpResolver.Properties(List.of(trusted)));
    }

    @Test
    void usesTheFirstForwardedValueWhenTheRemoteAddressIsATrustedProxy() {
        ClientIpResolver resolver = resolver("10.0.0.2", "172.18.0.0/16");

        assertThat(resolver.resolve(request("10.0.0.2", "41.220.1.10, 10.0.0.1"))).isEqualTo("41.220.1.10");
        assertThat(resolver.resolve(request("172.18.5.9", " 41.220.1.11 "))).isEqualTo("41.220.1.11");
    }

    @Test
    void ignoresTheHeaderWhenTheRemoteAddressIsNotTrusted() {
        ClientIpResolver resolver = resolver("10.0.0.2", "172.18.0.0/16");

        assertThat(resolver.resolve(request("41.220.1.10", "1.2.3.4"))).isEqualTo("41.220.1.10");
        assertThat(resolver.resolve(request("172.19.0.1", "1.2.3.4"))).isEqualTo("172.19.0.1");
        assertThat(resolver.resolve(request("10.0.0.3", "1.2.3.4"))).isEqualTo("10.0.0.3");
    }

    @Test
    void withoutTrustedProxiesTheHeaderIsAlwaysIgnored() {
        ClientIpResolver resolver = new ClientIpResolver(new ClientIpResolver.Properties(null));

        assertThat(resolver.resolve(request("127.0.0.1", "1.2.3.4"))).isEqualTo("127.0.0.1");
    }

    @Test
    void trustedProxyWithoutHeaderOrWithBlankHeaderUsesTheRemoteAddress() {
        ClientIpResolver resolver = resolver("10.0.0.0/8");

        assertThat(resolver.resolve(request("10.1.2.3", null))).isEqualTo("10.1.2.3");
        assertThat(resolver.resolve(request("10.1.2.3", "  "))).isEqualTo("10.1.2.3");
        assertThat(resolver.resolve(request("10.1.2.3", ", 1.2.3.4"))).isEqualTo("10.1.2.3");
    }

    @Test
    void supportsIpv6AndOddPrefixes() {
        ClientIpResolver resolver = resolver("fd00::/8", "192.168.1.0/25");

        assertThat(resolver.resolve(request("fd12::1", "2001:db8::7"))).isEqualTo("2001:db8::7");
        assertThat(resolver.resolve(request("192.168.1.100", "1.2.3.4"))).isEqualTo("1.2.3.4");
        assertThat(resolver.resolve(request("192.168.1.200", "1.2.3.4"))).isEqualTo("192.168.1.200");
        assertThat(resolver.isTrusted("not-an-ip")).isFalse();
    }

    @Test
    void rejectsInvalidConfiguration() {
        assertThatThrownBy(() -> resolver("proxy.local")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> resolver("10.0.0.0/33")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> resolver("10.0.0.0/x")).isInstanceOf(IllegalArgumentException.class);
    }
}
