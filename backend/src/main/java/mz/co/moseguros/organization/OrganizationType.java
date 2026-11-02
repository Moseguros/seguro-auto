package mz.co.moseguros.organization;

/**
 * Tipo de organização. Não muda depois de criada. Público: o módulo {@code identity} usa-o
 * através do {@code OrganizationApi}.
 */
public enum OrganizationType {
    INSURER,
    BROKER,
    AGENT,
    WORKSHOP,
    ASSESSOR,
    ASSISTANCE,
    OMBUDSMAN,
    REGULATOR,
    POLICE,
    CORPORATE_CLIENT;

    /** Seguradoras e mediadores precisam do número de autorização da ASFPM. */
    public boolean requiresLicense() {
        return this == INSURER || this == BROKER || this == AGENT;
    }
}
