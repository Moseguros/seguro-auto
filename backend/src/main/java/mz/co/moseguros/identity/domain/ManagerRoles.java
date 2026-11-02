package mz.co.moseguros.identity.domain;

import java.util.List;

/**
 * Perfis de gestão (S1-04), por tipo de organização ({@code null} = plataforma). Dois conceitos
 * diferentes:
 * <ul>
 *   <li>{@link #managerRoles} — os perfis que fazem de alguém gestor (regra {@code LAST_MANAGER}
 *       do S1-05 e aviso "sem gestor" do S1-08);</li>
 *   <li>{@link #managerInviteRoles} — os perfis a dar ao primeiro gestor no ecrã de criação de
 *       organização.</li>
 * </ul>
 */
public final class ManagerRoles {

    public static final String PLATFORM_ADMIN = "PLATFORM_ADMIN";
    public static final String INSURER_ADMIN = "INSURER_ADMIN";
    public static final String ORG_ADMIN = "ORG_ADMIN";
    public static final String CUSTOMER = "CUSTOMER";

    private ManagerRoles() {
    }

    /** @param organizationType tipo da organização ({@code INSURER}…) ou {@code null} para a plataforma */
    public static List<String> managerRoles(String organizationType) {
        if (organizationType == null) {
            return List.of(PLATFORM_ADMIN);
        }
        return List.of("INSURER".equals(organizationType) ? INSURER_ADMIN : ORG_ADMIN);
    }

    /** @param organizationType tipo da organização ({@code INSURER}…) ou {@code null} para a plataforma */
    public static List<String> managerInviteRoles(String organizationType) {
        if (organizationType == null) {
            return List.of(PLATFORM_ADMIN);
        }
        return switch (organizationType) {
            case "INSURER" -> List.of(INSURER_ADMIN);
            case "BROKER" -> List.of(ORG_ADMIN, "BROKER");
            case "AGENT" -> List.of(ORG_ADMIN, "AGENT");
            case "WORKSHOP" -> List.of(ORG_ADMIN, "WORKSHOP");
            case "ASSESSOR" -> List.of(ORG_ADMIN, "ASSESSOR");
            case "ASSISTANCE" -> List.of(ORG_ADMIN, "ASSISTANCE_PROVIDER");
            case "OMBUDSMAN" -> List.of(ORG_ADMIN, "OMBUDSMAN");
            case "REGULATOR" -> List.of(ORG_ADMIN, "REGULATOR_VIEWER");
            case "POLICE" -> List.of(ORG_ADMIN, "POLICE_OFFICER");
            case "CORPORATE_CLIENT" -> List.of(ORG_ADMIN, "FLEET_MANAGER");
            default -> throw new IllegalArgumentException("Unknown organization type: " + organizationType);
        };
    }

    /**
     * O perfil pode ser atribuído a staff deste tipo de organização: {@code role.org_types} contém
     * o tipo; na plataforma, só perfis sem tipos ({@code org_types = null}) excepto {@code CUSTOMER}.
     */
    public static boolean assignable(Role role, String organizationType) {
        if (organizationType == null) {
            return role.getOrgTypes() == null && !CUSTOMER.equals(role.getCode());
        }
        return role.isAssignableIn(organizationType);
    }
}
