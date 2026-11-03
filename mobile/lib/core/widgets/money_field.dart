import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import '../format/money.dart';

/// Campo de valor em meticais, com o sufixo "MT" (equivalente ao `MoneyField` da web).
///
/// O valor lê-se do [controller] com [parseMeticais]; ao perder o foco, um valor
/// válido é reescrito com [formatAmount] (ex.: `3 000 000,00`).
class MoneyField extends StatefulWidget {
  const MoneyField({
    super.key,
    required this.controller,
    required this.label,
    this.helperText,
    this.validator,
    this.onChanged,
    this.enabled = true,
  });

  final TextEditingController controller;
  final String label;
  final String? helperText;
  final FormFieldValidator<String>? validator;
  final ValueChanged<String>? onChanged;
  final bool enabled;

  /// Texto de um valor em centavos para pôr no campo.
  static String textFor(int? cents, String languageCode) =>
      cents == null ? '' : formatAmount(cents, languageCode);

  @override
  State<MoneyField> createState() => _MoneyFieldState();
}

class _MoneyFieldState extends State<MoneyField> {
  final _focus = FocusNode();

  @override
  void initState() {
    super.initState();
    _focus.addListener(_onFocusChange);
  }

  void _onFocusChange() {
    if (_focus.hasFocus) return;
    final parsed = parseMeticais(widget.controller.text);
    final cents = parsed.cents;
    if (parsed.invalid || cents == null) return;
    final language = Localizations.localeOf(context).languageCode;
    widget.controller.text = formatAmount(cents, language);
  }

  @override
  void dispose() {
    _focus
      ..removeListener(_onFocusChange)
      ..dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return TextFormField(
      controller: widget.controller,
      focusNode: _focus,
      enabled: widget.enabled,
      keyboardType: const TextInputType.numberWithOptions(decimal: true),
      inputFormatters: [
        FilteringTextInputFormatter.allow(RegExp(r'[\d\s  .,]')),
      ],
      decoration: InputDecoration(
        labelText: widget.label,
        helperText: widget.helperText,
        helperMaxLines: 3,
        errorMaxLines: 3,
        suffixText: 'MT',
      ),
      onChanged: widget.onChanged,
      validator: widget.validator,
    );
  }
}
