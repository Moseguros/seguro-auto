package mz.co.moseguros.shared.audit;

/**
 * Quem fez a acção auditada.
 */
public enum ActorType {
    /** Cliente final (segurado ou tomador). */
    CUSTOMER,
    /** Utilizador de uma organização (seguradora, mediador, plataforma). */
    STAFF,
    /** A própria aplicação (jobs, arranque). */
    SYSTEM,
    /** Outro serviço (ex.: {@code payment-file-worker}). */
    SERVICE
}
