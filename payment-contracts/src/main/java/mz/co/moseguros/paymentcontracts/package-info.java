/**
 * Contratos partilhados entre o {@code payment-file-worker} e o backend (ADR-004, ADR-007, S5-06).
 *
 * <ul>
 *   <li>Mensagens RabbitMQ publicadas pelo worker no exchange {@link
 *       mz.co.moseguros.paymentcontracts.PaymentsTopology#EVENTS_EXCHANGE}: {@link
 *       mz.co.moseguros.paymentcontracts.ReferencePaymentReceived}, {@link
 *       mz.co.moseguros.paymentcontracts.PaymentFileProcessed}, {@link
 *       mz.co.moseguros.paymentcontracts.PaymentFileRejected} e {@link
 *       mz.co.moseguros.paymentcontracts.PaymentSourcePolled}. Cada uma tem o seu JSON Schema
 *       (draft 2020-12, {@code additionalProperties: false}) em {@code schemas/*.v1.json}.
 *   <li>Contrato REST interno do worker ({@code GET /internal/v1/payment-file-sources}, S5-09):
 *       {@link mz.co.moseguros.paymentcontracts.FileSourceList}.
 * </ul>
 *
 * <p>JSON em camelCase, instantes em ISO 8601 UTC ({@code 2027-01-15T09:12:44Z}), dinheiro em
 * {@link mz.co.moseguros.paymentcontracts.MoneyAmount} (centavos, {@code MZN}). As duas aplicações
 * usam o {@code ObjectMapper} do Spring Boot ({@code JavaTimeModule},
 * {@code WRITE_DATES_AS_TIMESTAMPS = false}); os campos {@code null} não são escritos.
 *
 * <p><strong>Evolução.</strong> Esta é a versão de esquema 1 ({@link
 * mz.co.moseguros.paymentcontracts.MessageHeaders#SCHEMA_VERSION}). Um campo novo e
 * <em>opcional</em> mantém {@code schemaVersion = 1} (acrescenta-se ao record e ao schema, e quem
 * consome tolera a sua ausência). Uma mudança incompatível (campo obrigatório novo, campo
 * retirado, tipo ou significado alterado) cria um record {@code …V2}, um schema {@code *.v2.json}
 * e uma fila nova, e as duas versões convivem até não haver mensagens da antiga.
 */
package mz.co.moseguros.paymentcontracts;
