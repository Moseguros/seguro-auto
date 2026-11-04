package mz.co.moseguros.proposal.application;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import mz.co.moseguros.catalog.ProductDocumentType;
import mz.co.moseguros.catalog.ProductDocumentView;

/**
 * Documentos do produto que o tomador declara ter lido antes de assinar (S4-04 §3 item 12 e §8): as
 * Condições Gerais e a Informação Pré-contratual da versão da proposta, na língua do tomador ou, se
 * não houver, em {@code pt}.
 */
public final class ProductDocuments {

    /** Tipos mostrados, por esta ordem. */
    static final List<ProductDocumentType> TYPES = List.of(ProductDocumentType.GENERAL_CONDITIONS,
            ProductDocumentType.PRE_CONTRACT_INFO);
    static final String DEFAULT_LANGUAGE = "pt";

    /** Documento escolhido para um tipo. */
    public record Selected(ProductDocumentType type, UUID fileId) {
    }

    private ProductDocuments() {
    }

    /**
     * Um documento por tipo de {@link #TYPES}, na língua pedida ou em {@code pt}; um tipo sem documento
     * fica de fora.
     */
    public static List<Selected> select(List<ProductDocumentView> documents, String language) {
        List<Selected> selected = new ArrayList<>();
        if (documents == null) {
            return selected;
        }
        String wanted = language == null ? DEFAULT_LANGUAGE : language;
        for (ProductDocumentType type : TYPES) {
            documents.stream().filter(d -> d.docType() == type && wanted.equals(d.language())).findFirst()
                    .or(() -> documents.stream()
                            .filter(d -> d.docType() == type && DEFAULT_LANGUAGE.equals(d.language())).findFirst())
                    .ifPresent(d -> selected.add(new Selected(type, d.fileId())));
        }
        return selected;
    }
}
