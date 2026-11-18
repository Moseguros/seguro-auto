package mz.co.moseguros.shared.secrets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

/** Cofre em memória (S5-01 §2): o mesmo contrato do Vault, sem mostrar valores. */
class InMemorySecretStoreTest {

    static final String PATH = "secret/insurers/0193/payments/MPESA/apiKey";

    final InMemorySecretStore store = new InMemorySecretStore();

    @Test
    void putGetExistsDelete() {
        assertThat(store.exists(PATH)).isFalse();
        assertThat(store.get(PATH)).isEmpty();
        store.put(PATH, "valor-1");
        assertThat(store.exists(PATH)).isTrue();
        assertThat(store.get(PATH)).contains("valor-1");
        store.put(PATH, "valor-2");
        assertThat(store.get(PATH)).contains("valor-2");
        store.delete(PATH);
        assertThat(store.exists(PATH)).isFalse();
        store.delete(PATH); // apagar o que não existe não falha
    }

    @Test
    void neverShowsTheValues() {
        store.put(PATH, "valor-secreto");
        assertThat(store.toString()).doesNotContain("valor-secreto").contains("1 secrets");
    }

    @Test
    void rejectsInvalidPaths() {
        assertThatThrownBy(() -> store.put("/secret/x", "v")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> store.get("secret/../x")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> store.put(" ", "v")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> store.put(PATH, null)).isInstanceOf(NullPointerException.class);
    }
}
