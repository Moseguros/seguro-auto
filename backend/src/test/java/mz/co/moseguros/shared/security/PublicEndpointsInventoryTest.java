package mz.co.moseguros.shared.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.YamlMapFactoryBean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * Inventário dos endpoints que respondem sem token (S6-08 §6, ASVS V4.1.1/V14.5): percorre os
 * mapeamentos do {@link RequestMappingHandlerMapping} de todos os controladores do backend e mais os
 * endpoints do Actuator expostos ({@code management.endpoints.web.exposure.include}) e lista os que caem
 * nos caminhos públicos do {@link SecurityConfig} ({@code PUBLIC_PATHS}, {@code OPTIONAL_AUTH_PATHS}, o
 * Prometheus) ou do {@link InternalSecurityConfig} ({@code POST /internal/v1/auth/token}), e os que têm
 * {@code @PreAuthorize} com {@code permitAll}. A lista tem de ser igual a
 * {@code security/public-endpoints.txt}: um endpoint público novo obriga a mudar esse ficheiro (com a
 * justificação), e o revisor vê-o no PR.
 *
 * <p>A autorização por permissão fica no serviço ({@code @PreAuthorize}, AGENTS.md §6), por isso o
 * controlador sem {@code @PreAuthorize} não é, por si, um endpoint público: os outros caminhos exigem um
 * token válido na cadeia de segurança.
 */
class PublicEndpointsInventoryTest {

    private static final String INVENTORY = "security/public-endpoints.txt";
    private static final String BASE_PACKAGE = "mz.co.moseguros";

    @Test
    void publicEndpointsMatchTheReviewedInventory() throws Exception {
        Map<String, String> expected = readInventory();
        TreeSet<String> actual = new TreeSet<>(publicEndpoints());

        assertThat(expected.values())
                .as("cada linha de %s tem justificação", INVENTORY)
                .allSatisfy(j -> assertThat(j).isNotBlank());
        assertThat(actual)
                .as("endpoints sem token (método caminho); actualize %s com a justificação:%n%s", INVENTORY,
                        String.join("\n", actual))
                .containsExactlyInAnyOrderElementsOf(expected.keySet());
    }

    @Test
    void publicMatchersStillBlockAuthenticatedAreas() {
        // Sentinelas: estas áreas nunca podem entrar nos caminhos públicos por engano.
        for (String path : List.of("/api/v1/me/profile", "/api/v1/insurer/policies", "/api/v1/admin/users",
                "/api/v1/police/verifications", "/internal/v1/file-sources", "/api/v1/info")) {
            assertThat(isAnonymous("GET", path)).as(path).isFalse();
        }
    }

    static List<String> publicEndpoints() throws Exception {
        List<String> result = new ArrayList<>();
        Mapping mapping = new Mapping();
        for (Class<?> controller : controllers()) {
            for (Method method : controller.getDeclaredMethods()) {
                if (method.isBridge() || method.isSynthetic()) {
                    continue;
                }
                RequestMappingInfo info = mapping.info(method, controller);
                if (info == null) {
                    continue;
                }
                List<String> methods = info.getMethodsCondition().getMethods().isEmpty()
                        ? List.of("ANY")
                        : info.getMethodsCondition().getMethods().stream().map(RequestMethod::name).sorted().toList();
                boolean permitAll = permitAll(method) || permitAll(controller);
                for (String pattern : info.getPatternValues()) {
                    for (String httpMethod : methods) {
                        if (permitAll || isAnonymous(httpMethod, pattern)) {
                            result.add(httpMethod + " " + pattern);
                        }
                    }
                }
            }
        }
        for (String endpoint : actuatorEndpoints()) {
            String path = "/api/v1/" + endpoint;
            if (isAnonymous("GET", path)) {
                result.add("GET " + path);
            }
        }
        return result;
    }

    static boolean isAnonymous(String httpMethod, String path) {
        MockHttpServletRequest request = new MockHttpServletRequest("ANY".equals(httpMethod) ? "GET" : httpMethod,
                path);
        request.setServletPath(path);
        return SecurityConfig.PUBLIC.matches(request)
                || SecurityConfig.OPTIONAL_AUTH.matches(request)
                || ObservabilityAccessFilter.PROMETHEUS.matches(request)
                || InternalSecurityConfig.TOKEN_ENDPOINT.matches(request);
    }

    private static boolean permitAll(java.lang.reflect.AnnotatedElement element) {
        PreAuthorize pre = AnnotatedElementUtils.findMergedAnnotation(element, PreAuthorize.class);
        return pre != null && pre.value().replace(" ", "").equals("permitAll()");
    }

    private static List<Class<?>> controllers() throws ClassNotFoundException {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Controller.class));
        List<Class<?>> result = new ArrayList<>();
        for (BeanDefinition candidate : scanner.findCandidateComponents(BASE_PACKAGE)) {
            Class<?> type = Class.forName(candidate.getBeanClassName());
            String location = String.valueOf(type.getProtectionDomain().getCodeSource().getLocation());
            if (!location.contains("test-classes")) { // controladores só de teste (ex.: SecurityProbeController)
                result.add(type);
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private static List<String> actuatorEndpoints() {
        YamlMapFactoryBean yaml = new YamlMapFactoryBean();
        yaml.setResources(new ClassPathResource("application.yml"));
        Map<String, Object> root = yaml.getObject();
        Map<String, Object> management = (Map<String, Object>) root.get("management");
        Map<String, Object> endpoints = (Map<String, Object>) management.get("endpoints");
        Map<String, Object> web = (Map<String, Object>) endpoints.get("web");
        Map<String, Object> exposure = (Map<String, Object>) web.get("exposure");
        return List.of(String.valueOf(exposure.get("include")).split(",")).stream().map(String::strip).toList();
    }

    private static Map<String, String> readInventory() throws IOException {
        Map<String, String> result = new TreeMap<>();
        try (InputStream in = new ClassPathResource(INVENTORY).getInputStream()) {
            for (String raw : new String(in.readAllBytes(), StandardCharsets.UTF_8).split("\\R")) {
                String line = raw.strip();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                String[] parts = line.split("\\|", 2);
                String key = parts[0].strip().replaceAll("\\s+", " ");
                result.put(key, parts.length > 1 ? parts[1].strip() : "");
            }
        }
        return result;
    }

    /** Expõe o cálculo do mapeamento sem levantar o contexto Spring. */
    static final class Mapping extends RequestMappingHandlerMapping {

        Mapping() {
            StaticApplicationContext context = new StaticApplicationContext();
            context.refresh();
            setApplicationContext(context);
            afterPropertiesSet();
        }

        RequestMappingInfo info(Method method, Class<?> type) {
            return getMappingForMethod(method, type);
        }
    }
}
