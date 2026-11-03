package mz.co.moseguros.document;

/**
 * Sensibilidade do conteúdo de um ficheiro: {@code PUBLIC} (ex.: condições gerais de um produto
 * publicado), {@code PERSONAL} (dados pessoais, ex.: livrete) ou {@code SENSITIVE} (ex.:
 * documentos de identificação). As leituras de {@code SENSITIVE} serão auditadas no Sprint 11.
 */
public enum FileClassification {
    PUBLIC,
    PERSONAL,
    SENSITIVE
}
