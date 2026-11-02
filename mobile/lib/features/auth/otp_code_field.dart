import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import '../../l10n/app_localizations.dart';

/// Campo do código: [length] caixas sobre um único campo de texto, para o teclado
/// numérico, colar o código inteiro e o preenchimento automático do sistema
/// (`AutofillHints.oneTimeCode`). Chama [onCompleted] quando fica completo.
class OtpCodeField extends StatefulWidget {
  const OtpCodeField({
    super.key,
    required this.length,
    required this.onCompleted,
    this.enabled = true,
    this.hasError = false,
    this.autofocus = true,
  });

  final int length;
  final ValueChanged<String> onCompleted;
  final bool enabled;
  final bool hasError;
  final bool autofocus;

  @override
  State<OtpCodeField> createState() => _OtpCodeFieldState();
}

class _OtpCodeFieldState extends State<OtpCodeField> {
  final _controller = TextEditingController();
  final _focus = FocusNode();

  @override
  void initState() {
    super.initState();
    _focus.addListener(() => setState(() {}));
  }

  @override
  void dispose() {
    _controller.dispose();
    _focus.dispose();
    super.dispose();
  }

  void _changed(String value) {
    setState(() {});
    if (value.length == widget.length) widget.onCompleted(value);
  }

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    final scheme = theme.colorScheme;
    final text = _controller.text;

    final boxes = Row(
      children: [
        for (var i = 0; i < widget.length; i++) ...[
          if (i > 0) const SizedBox(width: 8),
          Expanded(
            child: AspectRatio(
              aspectRatio: 0.85,
              child: DecoratedBox(
                decoration: BoxDecoration(
                  color: scheme.surface,
                  borderRadius: BorderRadius.circular(8),
                  border: Border.all(
                    width:
                        _focus.hasFocus &&
                            (i == text.length ||
                                (i == widget.length - 1 &&
                                    text.length == widget.length))
                        ? 2
                        : 1,
                    color: widget.hasError
                        ? scheme.error
                        : (_focus.hasFocus && i == text.length
                              ? scheme.primary
                              : scheme.onSurfaceVariant.withValues(alpha: 0.5)),
                  ),
                ),
                child: Center(
                  child: Text(
                    i < text.length ? text[i] : '',
                    style: theme.textTheme.headlineSmall?.copyWith(
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                ),
              ),
            ),
          ),
        ],
      ],
    );

    return Stack(
      children: [
        ExcludeSemantics(child: boxes),
        Positioned.fill(
          child: Opacity(
            opacity: 0,
            child: TextField(
              key: const Key('otp-code-input'),
              controller: _controller,
              focusNode: _focus,
              enabled: widget.enabled,
              autofocus: widget.autofocus,
              keyboardType: TextInputType.number,
              textInputAction: TextInputAction.done,
              autofillHints: const [AutofillHints.oneTimeCode],
              inputFormatters: [
                FilteringTextInputFormatter.digitsOnly,
                LengthLimitingTextInputFormatter(widget.length),
              ],
              showCursor: false,
              enableInteractiveSelection: true,
              decoration: InputDecoration(
                labelText: l10n.authCodeInputLabel,
                border: InputBorder.none,
                counterText: '',
              ),
              onChanged: _changed,
            ),
          ),
        ),
      ],
    );
  }
}
