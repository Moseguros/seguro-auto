package mz.co.moseguros;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import mz.co.moseguros.shared.web.MetaController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Convenções do contrato: os clientes web e mobile são gerados a partir dos
 * {@code operationId} e das tags, por isso têm de ser explícitos, únicos e estáveis.
 */
class OpenApiConventionsTest {

    static final String BASE_PACKAGE = "mz.co.moseguros";
    static final Pattern OPERATION_ID = Pattern.compile("^[a-z]+[A-Z][A-Za-z]*$");

    @Test
    void allRestControllersFollowTheConventions() {
        List<Class<?>> controllers = restControllers();

        assertThat(controllers).contains(MetaController.class);
        assertThat(violations(controllers)).isEmpty();
    }

    @Test
    void detectsMissingOperationId() {
        assertThat(violations(List.of(NoOperationIdController.class)))
                .anyMatch(v -> v.contains("without @Operation(operationId)"));
    }

    @Test
    void detectsMissingTag() {
        assertThat(violations(List.of(NoTagController.class)))
                .anyMatch(v -> v.contains("without @Tag"));
    }

    @Test
    void detectsInvalidOperationId() {
        assertThat(violations(List.of(BadOperationIdController.class)))
                .anyMatch(v -> v.contains("does not match"));
    }

    @Test
    void detectsDuplicateOperationIds() {
        assertThat(violations(List.of(NoTagController.class, DuplicateController.class)))
                .anyMatch(v -> v.contains("duplicate operationId 'listThings'"));
    }

    /** Classes {@code @RestController} de produção em {@code mz.co.moseguros}. */
    static List<Class<?>> restControllers() {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(RestController.class));
        List<Class<?>> classes = new ArrayList<>();
        for (BeanDefinition bd : scanner.findCandidateComponents(BASE_PACKAGE)) {
            Class<?> type = ClassUtils.resolveClassName(bd.getBeanClassName(), OpenApiConventionsTest.class.getClassLoader());
            if (!isTestClass(type)) {
                classes.add(type);
            }
        }
        classes.sort(Comparator.comparing(Class::getName));
        return classes;
    }

    /** Controladores auxiliares dos testes (ex.: {@code standaloneSetup}) não fazem parte do contrato. */
    static boolean isTestClass(Class<?> type) {
        Class<?> outer = type;
        while (outer.getEnclosingClass() != null) {
            outer = outer.getEnclosingClass();
        }
        return outer.getProtectionDomain().getCodeSource().getLocation()
                .equals(OpenApiConventionsTest.class.getProtectionDomain().getCodeSource().getLocation());
    }

    static List<String> violations(List<Class<?>> controllers) {
        List<String> violations = new ArrayList<>();
        Map<String, String> seen = new HashMap<>();
        for (Class<?> controller : controllers) {
            if (!AnnotatedElementUtils.hasAnnotation(controller, Tag.class)) {
                violations.add(controller.getName() + ": controller without @Tag");
            }
            Method[] methods = controller.getDeclaredMethods();
            Arrays.sort(methods, Comparator.comparing(Method::getName));
            for (Method method : methods) {
                if (!AnnotatedElementUtils.hasAnnotation(method, RequestMapping.class)) {
                    continue; // inclui @GetMapping, @PostMapping, @PutMapping, @PatchMapping e @DeleteMapping
                }
                String where = controller.getName() + "#" + method.getName();
                Operation operation = method.getAnnotation(Operation.class);
                String id = operation == null ? "" : operation.operationId().strip();
                if (id.isEmpty()) {
                    violations.add(where + ": endpoint without @Operation(operationId)");
                    continue;
                }
                if (!OPERATION_ID.matcher(id).matches()) {
                    violations.add(where + ": operationId '" + id + "' does not match " + OPERATION_ID.pattern());
                }
                String previous = seen.putIfAbsent(id, where);
                if (previous != null) {
                    violations.add(where + ": duplicate operationId '" + id + "' (also in " + previous + ")");
                }
            }
        }
        return violations;
    }

    // Exemplos inválidos. Sem @RestController de propósito: o component scan da
    // aplicação (mz.co.moseguros) apanhá-los-ia nos testes de integração.

    @Tag(name = "Example")
    static class NoOperationIdController {
        @GetMapping("/x")
        @Operation(summary = "sem operationId")
        void get() {
        }
    }

    static class NoTagController {
        @GetMapping("/things")
        @Operation(operationId = "listThings")
        void list() {
        }
    }

    @Tag(name = "Example")
    static class BadOperationIdController {
        @PostMapping("/x")
        @Operation(operationId = "Create_thing")
        void create() {
        }
    }

    @Tag(name = "Example")
    static class DuplicateController {
        @GetMapping("/other-things")
        @Operation(operationId = "listThings")
        void list() {
        }
    }
}
