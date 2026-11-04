import 'package:flutter/material.dart';

import '../../../core/widgets/cta_button.dart';
import '../../../l10n/app_localizations.dart';

/// Máximo de cotações comparadas lado a lado.
const maxCompared = 3;

/// "Comparar propostas" só com 2 ou 3 seleccionadas.
bool canCompare(int selected) => selected >= 2 && selected <= maxCompared;

/// Barra fixa no fundo dos resultados quando há selecção (S3-05 §3):
/// "{n} seleccionadas (máximo 3)" e o `CtaButton` "Comparar propostas".
class CompareBar extends StatelessWidget {
  const CompareBar({super.key, required this.count, required this.onCompare});

  final int count;
  final VoidCallback onCompare;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    return BottomAppBar(
      key: const Key('compare-bar'),
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      child: Row(
        children: [
          Expanded(
            child: Text(
              l10n.quoteCompareSelected(count),
              key: const Key('compare-count'),
            ),
          ),
          const SizedBox(width: 8),
          Flexible(
            child: CtaButton(
              key: const Key('compare-button'),
              label: l10n.ctaCompare,
              onPressed: canCompare(count) ? onCompare : null,
            ),
          ),
        ],
      ),
    );
  }
}
