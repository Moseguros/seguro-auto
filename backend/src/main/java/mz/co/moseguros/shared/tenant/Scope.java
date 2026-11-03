package mz.co.moseguros.shared.tenant;

/**
 * Âmbito de uma transacção (ADR-002, adenda de 01/11/2026). Vai para {@code app.scope} e
 * decide, com as políticas de Row Level Security, que linhas a transacção vê.
 */
public enum Scope {
    /** Sem âmbito: não vê nenhuma linha das tabelas com RLS. */
    NONE,
    /** Fluxos sem utilizador autenticado, jobs e verificações pontuais; vê tudo. */
    SYSTEM,
    /** Staff da plataforma; vê tudo. */
    PLATFORM,
    /** Staff de uma organização; vê as linhas da sua organização. */
    ORG,
    /** Cliente final; vê as suas linhas. */
    CUSTOMER
}
