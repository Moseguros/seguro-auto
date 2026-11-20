package mz.co.moseguros.verification.application;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/** {@link IpPrefix} (S6-03 §4 regra 5): IPv4 → /24, IPv6 → /48. */
class IpPrefixTest {

    @Test
    void truncatesIpv4ToSlash24() {
        assertThat(IpPrefix.of("41.220.15.77")).isEqualTo("41.220.15.0/24");
        assertThat(IpPrefix.of(" 10.0.0.1 ")).isEqualTo("10.0.0.0/24");
        assertThat(IpPrefix.of("::ffff:41.220.15.77")).isEqualTo("41.220.15.0/24");
    }

    @Test
    void truncatesIpv6ToSlash48() {
        assertThat(IpPrefix.of("2001:db8:abcd:12:34:56:78:9a")).isEqualTo("2001:db8:abcd::/48");
        assertThat(IpPrefix.of("2c0f:f8f0:1:2::1")).isEqualTo("2c0f:f8f0:1::/48");
        assertThat(IpPrefix.of("[2001:db8:abcd::1]")).isEqualTo("2001:db8:abcd::/48");
        assertThat(IpPrefix.of("fe80::1%eth0")).isEqualTo("fe80:0:0::/48");
        assertThat(IpPrefix.of("0:0:0:0:0:0:0:1")).isEqualTo("0:0:0::/48");
    }

    @Test
    void ignoresWhatIsNotALiteral() {
        assertThat(IpPrefix.of(null)).isNull();
        assertThat(IpPrefix.of("")).isNull();
        assertThat(IpPrefix.of("localhost")).isNull();
        assertThat(IpPrefix.of("example.com")).isNull();
    }
}
