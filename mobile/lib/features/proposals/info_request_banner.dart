import 'package:flutter/material.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../core/api/api_error.dart';
import '../../l10n/app_localizations.dart';
import 'wizard_steps.dart';

/// Pedido de informação da seguradora (`INFO_REQUESTED`, S4-08 §3): a mensagem e
/// os itens, cada um com ligação para o passo correspondente.
class InfoRequestBanner extends StatelessWidget {
  const InfoRequestBanner({
    super.key,
    required this.request,
    required this.onOpenStep,
  });

  final ProposalInfoRequest request;
  final void Function(ProposalStep step) onOpenStep;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    final scheme = theme.colorScheme;
    return Semantics(
      container: true,
      liveRegion: true,
      child: Container(
        key: const Key('info-request-banner'),
        padding: const EdgeInsets.all(12),
        decoration: BoxDecoration(
          color: scheme.primary.withValues(alpha: 0.08),
          border: Border.all(color: scheme.primary.withValues(alpha: 0.5)),
          borderRadius: BorderRadius.circular(8),
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              l10n.proposalInfoRequestedTitle,
              style: theme.textTheme.titleSmall,
            ),
            const SizedBox(height: 4),
            Text(request.message),
            const SizedBox(height: 4),
            Wrap(
              spacing: 8,
              children: [
                for (final item in request.items)
                  TextButton(
                    key: Key('info-request-${item.name}'),
                    style: TextButton.styleFrom(
                      minimumSize: const Size(48, 48),
                    ),
                    onPressed: () => onOpenStep(stepForInfoItem(item)),
                    child: Text(proposalInfoRequestItem(l10n, item.name)),
                  ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
