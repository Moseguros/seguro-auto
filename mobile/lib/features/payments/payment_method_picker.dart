import 'package:flutter/material.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../l10n/app_localizations.dart';
import 'payer_msisdn_field.dart';
import 'payment_labels.dart';

/// Uma opção por forma de pagamento, pela ordem recebida (S5-11 §3), com o campo
/// extra da escolhida: o número M-Pesa ou o texto da entidade e referência.
class PaymentMethodPicker extends StatelessWidget {
  const PaymentMethodPicker({
    super.key,
    required this.methods,
    required this.selected,
    required this.onSelected,
    required this.msisdn,
    required this.onMsisdnChanged,
    this.msisdnError,
    this.enabled = true,
  });

  final List<PaymentMethodOption> methods;

  /// Método escolhido pelo nome da API (`MPESA`, `BANK_REFERENCE`), ou `null`.
  final String? selected;
  final ValueChanged<String> onSelected;
  final TextEditingController msisdn;
  final ValueChanged<String> onMsisdnChanged;
  final String? msisdnError;
  final bool enabled;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: [
        Text(l10n.paymentsMethodLabel, style: theme.textTheme.titleMedium),
        const SizedBox(height: 8),
        RadioGroup<String>(
          groupValue: selected,
          onChanged: (value) {
            if (enabled && value != null) onSelected(value);
          },
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              for (final option in methods)
                _MethodCard(
                  option: option,
                  selected: selected == option.method.name,
                  enabled: enabled,
                  onTap: () => onSelected(option.method.name),
                  extra: option.method.name == 'MPESA'
                      ? PayerMsisdnField(
                          key: const Key('payer-msisdn'),
                          controller: msisdn,
                          label: l10n.paymentsMethodMpesaNumber,
                          onChanged: onMsisdnChanged,
                          errorCode: msisdnError,
                        )
                      : Text(l10n.paymentsMethodReferenceHelp),
                ),
            ],
          ),
        ),
      ],
    );
  }
}

class _MethodCard extends StatelessWidget {
  const _MethodCard({
    required this.option,
    required this.selected,
    required this.enabled,
    required this.onTap,
    required this.extra,
  });

  final PaymentMethodOption option;
  final bool selected;
  final bool enabled;
  final VoidCallback onTap;
  final Widget extra;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final scheme = Theme.of(context).colorScheme;
    final name = option.method.name;
    final displayName = option.displayName;
    return Card(
      key: Key('method-$name'),
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(12),
        side: BorderSide(
          color: selected ? scheme.primary : scheme.outlineVariant,
          width: selected ? 2 : 1,
        ),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          InkWell(
            onTap: enabled ? onTap : null,
            borderRadius: BorderRadius.circular(12),
            child: ConstrainedBox(
              constraints: const BoxConstraints(minHeight: 56),
              child: Row(
                children: [
                  Radio<String>(value: name, enabled: enabled),
                  Icon(
                    name == 'MPESA'
                        ? Icons.phone_android
                        : Icons.account_balance_outlined,
                    color: scheme.primary,
                  ),
                  const SizedBox(width: 12),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          paymentMethodLabel(l10n, name),
                          style: const TextStyle(fontWeight: FontWeight.w600),
                        ),
                        if (displayName != null && displayName.isNotEmpty)
                          Text(displayName),
                      ],
                    ),
                  ),
                ],
              ),
            ),
          ),
          if (selected)
            Padding(
              padding: const EdgeInsets.fromLTRB(16, 0, 16, 16),
              child: extra,
            ),
        ],
      ),
    );
  }
}
