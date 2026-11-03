package mz.co.moseguros.rating.domain;

/** Quem pediu as cotações (S3-02 §1): o próprio cliente ou um mediador em nome dele. */
public enum QuoteOrigin {
    /** O cliente, com um veículo e um condutor seus ({@code POST /quotes}). */
    CUSTOMER,
    /** Um corretor ou agente, com dados escritos à mão e o telemóvel do cliente ({@code POST /broker/quotes}). */
    BROKER
}
