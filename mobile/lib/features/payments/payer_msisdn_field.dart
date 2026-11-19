import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import '../../core/api/api_error.dart';
import '../../l10n/app_localizations.dart';

/// Campo de telemóvel com o prefixo fixo `+258` e 9 dígitos (teclado numérico).
class PayerMsisdnField extends StatelessWidget {
  const PayerMsisdnField({
    super.key,
    required this.controller,
    required this.label,
    required this.onChanged,
    this.errorCode,
    this.autofocus = false,
  });

  final TextEditingController controller;
  final String label;
  final ValueChanged<String> onChanged;

  /// Código `validation<Code>` a mostrar, ou `null`.
  final String? errorCode;
  final bool autofocus;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final code = errorCode;
    return TextField(
      controller: controller,
      autofocus: autofocus,
      keyboardType: TextInputType.phone,
      inputFormatters: [
        FilteringTextInputFormatter.digitsOnly,
        LengthLimitingTextInputFormatter(9),
      ],
      autofillHints: const [AutofillHints.telephoneNumberNational],
      onChanged: onChanged,
      decoration: InputDecoration(
        labelText: label,
        prefixText: '+258 ',
        errorText: code == null ? null : fieldErrorMessage(l10n, code),
        errorMaxLines: 3,
      ),
    );
  }
}
