package mz.co.moseguros.document.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Set;
import java.util.UUID;

import mz.co.moseguros.document.FileAccessPolicy;
import mz.co.moseguros.shared.security.CurrentUser;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * O registo das {@link FileAccessPolicy} recusa o arranque com dois beans para o mesmo tipo ou
 * com uma política sem tipos (S2-01 §4).
 */
class FileAccessPolicyRegistryTest {

    private final ApplicationContextRunner context = new ApplicationContextRunner()
            .withBean(FileAccessPolicyRegistry.class);

    @Test
    void startsWithDistinctOwnerTypes() {
        context.withUserConfiguration(VehiclePolicy.class, ProductPolicy.class).run(ctx -> {
            assertThat(ctx).hasNotFailed();
            FileAccessPolicyRegistry registry = ctx.getBean(FileAccessPolicyRegistry.class);
            assertThat(registry.ownerTypes()).containsExactly("INSURER_LOGO", "PRODUCT_VERSION", "VEHICLE");
            assertThat(registry.find("VEHICLE")).containsInstanceOf(VehiclePolicy.Policy.class);
            assertThat(registry.find("CLAIM")).isEmpty();
            assertThat(registry.find(null)).isEmpty();
            assertThatThrownBy(() -> registry.require("CLAIM")).isInstanceOf(IllegalArgumentException.class);
        });
    }

    @Test
    void startsWithoutPolicies() {
        context.run(ctx -> assertThat(ctx.getBean(FileAccessPolicyRegistry.class).ownerTypes()).isEmpty());
    }

    @Test
    void twoBeansForTheSameOwnerTypeFailTheStartup() {
        context.withUserConfiguration(VehiclePolicy.class, AnotherVehiclePolicy.class).run(ctx ->
                assertThat(ctx).hasFailed().getFailure()
                        .hasRootCauseInstanceOf(IllegalStateException.class)
                        .rootCause().hasMessageContaining("VEHICLE"));
    }

    @Test
    void policyWithoutOwnerTypesFailsTheStartup() {
        context.withUserConfiguration(EmptyPolicy.class).run(ctx ->
                assertThat(ctx).hasFailed().getFailure()
                        .rootCause().isInstanceOf(IllegalStateException.class)
                        .hasMessageContaining("no owner types"));
    }

    @Test
    void supportsMatchesOwnerTypes() {
        FileAccessPolicy policy = new ProductPolicy().policy();
        assertThat(policy.supports("PRODUCT_VERSION")).isTrue();
        assertThat(policy.supports("VEHICLE")).isFalse();
    }

    static FileAccessPolicy policy(Set<String> types) {
        return new FileAccessPolicy() {
            @Override
            public Set<String> ownerTypes() {
                return types;
            }

            @Override
            public boolean canRead(String ownerType, UUID ownerId, CurrentUser user) {
                return false;
            }
        };
    }

    @Configuration(proxyBeanMethods = false)
    static class VehiclePolicy {
        @Bean
        Policy vehiclePolicy() {
            return new Policy();
        }

        static class Policy implements FileAccessPolicy {
            @Override
            public Set<String> ownerTypes() {
                return Set.of("VEHICLE");
            }

            @Override
            public boolean canRead(String ownerType, UUID ownerId, CurrentUser user) {
                return false;
            }
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class AnotherVehiclePolicy {
        @Bean
        FileAccessPolicy anotherVehiclePolicy() {
            return policy(Set.of("VEHICLE"));
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class ProductPolicy {
        @Bean
        FileAccessPolicy policy() {
            return FileAccessPolicyRegistryTest.policy(Set.of("PRODUCT_VERSION", "INSURER_LOGO"));
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class EmptyPolicy {
        @Bean
        FileAccessPolicy emptyPolicy() {
            return policy(Set.of());
        }
    }
}
