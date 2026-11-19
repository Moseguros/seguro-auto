-- Aviso de simulação a expirar (S6-04 §9.1, decisão D16): o QuoteExpiryNotifierJob grava quando avisou o
-- cliente, para avisar uma só vez por pedido.
alter table rating.quote_request add column expiry_notified_at timestamptz null;
