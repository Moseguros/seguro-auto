package mz.co.moseguros.organization;

/**
 * Estado de uma organização: {@code ACTIVE → SUSPENDED → ACTIVE}. Público: o módulo
 * {@code identity} usa-o através do {@code OrganizationApi}.
 */
public enum OrganizationStatus {
    ACTIVE,
    SUSPENDED
}
