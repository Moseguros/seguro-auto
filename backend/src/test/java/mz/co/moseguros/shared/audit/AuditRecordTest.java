package mz.co.moseguros.shared.audit;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class AuditRecordTest {

    @Test
    void acceptsMinimalRecord() {
        assertThatCode(() -> new AuditRecord(ActorType.SYSTEM, "job", null, "X.Y", "X", "1", null, null))
                .doesNotThrowAnyException();
    }

    @Test
    void requiresMandatoryFields() {
        assertThatThrownBy(() -> new AuditRecord(null, "job", null, "X.Y", "X", "1", null, null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new AuditRecord(ActorType.SYSTEM, " ", null, "X.Y", "X", "1", null, null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new AuditRecord(ActorType.SYSTEM, "job", null, null, "X", "1", null, null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new AuditRecord(ActorType.SYSTEM, "job", null, "X.Y", "", "1", null, null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new AuditRecord(ActorType.SYSTEM, "job", null, "X.Y", "X", null, null, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void enforcesColumnLengths() {
        assertThatThrownBy(() -> new AuditRecord(ActorType.SYSTEM, "a".repeat(65), null, "X.Y", "X", "1", null, null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new AuditRecord(ActorType.SYSTEM, "job", null, "A".repeat(81), "X", "1", null, null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new AuditRecord(ActorType.SYSTEM, "job", null, "X.Y", "X".repeat(61), "1", null, null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new AuditRecord(ActorType.SYSTEM, "job", null, "X.Y", "X", "1".repeat(65), null, null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
