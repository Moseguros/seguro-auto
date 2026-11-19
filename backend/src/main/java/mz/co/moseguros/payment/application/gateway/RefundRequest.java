package mz.co.moseguros.payment.application.gateway;

import java.util.UUID;

import mz.co.moseguros.shared.domain.Money;

/** Pedido de reembolso ao fornecedor (S7; não usado no S5). */
public record RefundRequest(UUID intentId, Money amount, String providerTxId) {
}
