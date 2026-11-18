package mz.co.moseguros.paymentcontracts;

/**
 * Propriedades AMQP de todas as mensagens deste contrato. Quem publica (worker, S5-09) põe:
 *
 * <ul>
 *   <li>{@code message-id} = o {@code messageId} da mensagem ({@link MessageIds});
 *   <li>{@code content-type} = {@value #CONTENT_TYPE};
 *   <li>{@code type} = {@link #typeOf(Class)} (nome simples da classe, ex.: {@code
 *       ReferencePaymentReceived});
 *   <li>cabeçalho {@value #SCHEMA_VERSION_HEADER} = {@value #SCHEMA_VERSION};
 *   <li>{@code delivery-mode} = {@value #DELIVERY_MODE_PERSISTENT} (persistente).
 * </ul>
 */
public final class MessageHeaders {

    public static final String CONTENT_TYPE = "application/json";
    public static final String SCHEMA_VERSION_HEADER = "schemaVersion";
    /** Versão de esquema das mensagens deste pacote (ver o Javadoc do pacote sobre a evolução). */
    public static final int SCHEMA_VERSION = 1;
    /** Valor AMQP 0-9-1 de {@code delivery-mode} persistente ({@code MessageDeliveryMode.PERSISTENT}). */
    public static final int DELIVERY_MODE_PERSISTENT = 2;

    private MessageHeaders() {}

    /** Valor da propriedade AMQP {@code type}: o nome simples da classe da mensagem. */
    public static String typeOf(Class<?> messageClass) {
        return messageClass.getSimpleName();
    }
}
