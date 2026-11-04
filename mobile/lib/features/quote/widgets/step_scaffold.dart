import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

import '../../../l10n/app_localizations.dart';

/// Número de passos do assistente do cliente.
const quoteStepCount = 5;

/// Caminhos dos passos, pela ordem.
abstract final class QuoteSteps {
  static const vehicle = '/quote/vehicle';
  static const type = '/quote/type';
  static const coverages = '/quote/coverages';
  static const period = '/quote/period';
  static const summary = '/quote/summary';
}

/// Volta ao passo [path] (que está mais abaixo na pilha), tirando os de cima.
void popToStep(BuildContext context, String path) {
  Navigator.of(
    context,
  ).popUntil((route) => route.settings.name == path || route.isFirst);
}

/// Sai de um passo: "Voltar" e o botão "voltar" do Android tiram o passo da
/// pilha; sem nada por baixo, vai para o início.
void leaveStep(BuildContext context) {
  if (context.canPop()) {
    context.pop();
  } else {
    context.go('/');
  }
}

/// Moldura de um passo (S3-05 §2): título, "Passo {n} de 5", barra de progresso,
/// conteúdo com scroll e, fixos no fundo, "Voltar" e "Continuar" (ou [action],
/// no resumo).
class StepScaffold extends StatelessWidget {
  const StepScaffold({
    super.key,
    required this.step,
    required this.title,
    required this.children,
    this.onContinue,
    this.action,
  });

  /// 1 a 5.
  final int step;
  final String title;
  final List<Widget> children;

  /// `null` desactiva "Continuar" (passo inválido).
  final VoidCallback? onContinue;

  /// Substitui "Continuar" (o `CtaButton` "Simular seguro" do resumo).
  final Widget? action;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    return Scaffold(
      appBar: AppBar(
        title: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(title, maxLines: 1, overflow: TextOverflow.ellipsis),
            Text(
              l10n.quoteStepOf(step, quoteStepCount),
              key: const Key('quote-step-of'),
              style: theme.textTheme.bodySmall,
            ),
          ],
        ),
        bottom: PreferredSize(
          preferredSize: const Size.fromHeight(4),
          child: LinearProgressIndicator(
            key: const Key('quote-progress'),
            value: step / quoteStepCount,
          ),
        ),
      ),
      body: SafeArea(
        child: ListView(padding: const EdgeInsets.all(16), children: children),
      ),
      bottomNavigationBar: SafeArea(
        child: Material(
          elevation: 3,
          color: theme.colorScheme.surface,
          child: Padding(
            padding: const EdgeInsets.fromLTRB(16, 8, 16, 8),
            child: Row(
              children: [
                TextButton(
                  key: const Key('quote-back'),
                  onPressed: () => leaveStep(context),
                  child: Text(l10n.quoteBack),
                ),
                const SizedBox(width: 8),
                Expanded(
                  child:
                      action ??
                      FilledButton(
                        key: const Key('quote-continue'),
                        style: FilledButton.styleFrom(
                          minimumSize: const Size(64, 48),
                        ),
                        onPressed: onContinue,
                        child: Text(l10n.quoteContinue),
                      ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
