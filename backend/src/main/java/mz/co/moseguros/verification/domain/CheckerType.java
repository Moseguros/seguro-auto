package mz.co.moseguros.verification.domain;

/** Quem consultou (S6-03 §1): qualquer pessoa ({@link #PUBLIC}) ou um agente da polícia ({@link #POLICE}). */
public enum CheckerType {
    PUBLIC,
    POLICE
}
