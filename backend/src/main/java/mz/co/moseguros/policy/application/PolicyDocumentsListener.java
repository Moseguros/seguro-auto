package mz.co.moseguros.policy.application;

import mz.co.moseguros.policy.PolicyIssued;
import mz.co.moseguros.shared.tenant.RunAsSystem;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

/**
 * Apólice emitida ({@link PolicyIssued}, S6-01 §6) → certificado e condições particulares em PDF e aviso
 * {@code POLICY_ISSUED} ao cliente (S6-02 §1), depois do commit da emissão, em {@code SYSTEM}. Uma excepção
 * propaga-se: a publicação fica incompleta e o {@code IncompletePublicationsJob} reenvia-a; o
 * {@link PolicyDocumentsJob} apanha também as apólices que ficaram sem documentos.
 */
@Component
public class PolicyDocumentsListener {

    private final PolicyDocumentService documents;

    public PolicyDocumentsListener(PolicyDocumentService documents) {
        this.documents = documents;
    }

    @ApplicationModuleListener
    @RunAsSystem
    public void on(PolicyIssued event) {
        documents.generate(event.policyId());
    }
}
