package mz.co.moseguros.catalog;

/**
 * Tipo de documento de uma versão de produto. Há no máximo um {@code GENERAL_CONDITIONS} e um
 * {@code PRE_CONTRACT_INFO} por língua; os dois são obrigatórios em pt para publicar.
 */
public enum ProductDocumentType {
    GENERAL_CONDITIONS,
    PRE_CONTRACT_INFO,
    SPECIAL_CONDITIONS,
    OTHER;

    /** {@code true} nos tipos com um só documento por língua (um novo envio substitui o ficheiro). */
    public boolean isMain() {
        return this == GENERAL_CONDITIONS || this == PRE_CONTRACT_INFO;
    }
}
