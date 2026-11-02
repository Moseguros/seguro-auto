package mz.co.moseguros.shared.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class IdsTest {

    @Test
    void generatesTimeOrderedUuidV7() {
        UUID first = Ids.newId();
        UUID second = Ids.newId();
        assertThat(first.version()).isEqualTo(7);
        assertThat(first).isNotEqualTo(second);
        assertThat(first.toString().compareTo(second.toString())).isNegative();
    }
}
