import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../../core/format/dates.dart';
import '../../../l10n/app_localizations.dart';
import '../quote_labels.dart';
import '../quote_repository.dart';

/// Etiqueta de contorno (estado ou origem).
class QuoteTag extends StatelessWidget {
  const QuoteTag(this.label, {super.key, this.color});

  final String label;
  final Color? color;

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final color = this.color ?? theme.colorScheme.primary;
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
      decoration: BoxDecoration(
        border: Border.all(color: color),
        borderRadius: BorderRadius.circular(12),
      ),
      child: Text(
        label,
        style: theme.textTheme.labelMedium?.copyWith(color: color),
      ),
    );
  }
}

/// Cartão de um pedido (S3-04 §6): veículo, tipo, "Pelo mediador", início,
/// "{n} propostas", "desde {total}", validade ou "Expirada". Toque → resultados.
class QuoteSummaryCard extends StatelessWidget {
  const QuoteSummaryCard({super.key, required this.summary});

  final QuoteRequestSummaryResponse summary;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    final muted = theme.textTheme.bodyMedium?.copyWith(
      color: theme.colorScheme.onSurfaceVariant,
    );
    final vehicle = summary.vehicle;
    final best = summary.bestTotal;
    final validUntil = summary.validUntil;
    return Card(
      key: Key('quote-summary-${summary.id}'),
      margin: const EdgeInsets.only(bottom: 8),
      child: InkWell(
        borderRadius: BorderRadius.circular(8),
        onTap: () => context.push('/quote/results/${summary.id}'),
        child: Padding(
          padding: const EdgeInsets.all(12),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                quotedVehicleTitle(
                  plate: vehicle.plate,
                  make: vehicle.make,
                  model: vehicle.model,
                ),
                style: theme.textTheme.titleMedium,
              ),
              const SizedBox(height: 4),
              Text(
                productTypeName(l10n, summary.productType.name),
                style: muted,
              ),
              Text(
                l10n.myQuotesStart(apiDate(summary.startDate)),
                style: muted,
              ),
              const SizedBox(height: 8),
              Wrap(
                spacing: 8,
                runSpacing: 4,
                crossAxisAlignment: WrapCrossAlignment.center,
                children: [
                  Text(l10n.myQuotesOffers(summary.offeredCount)),
                  if (best != null)
                    Text(
                      l10n.myQuotesFrom(money(l10n, best)),
                      style: const TextStyle(
                        fontWeight: FontWeight.w600,
                        fontFeatures: [FontFeature.tabularFigures()],
                      ),
                    ),
                  if (summary.origin ==
                      QuoteRequestSummaryResponseOriginEnum.BROKER)
                    QuoteTag(l10n.myQuotesByBroker),
                  if (summary.expired)
                    QuoteTag(
                      l10n.quoteExpiredLabel,
                      color: theme.colorScheme.error,
                    )
                  else if (validUntil != null)
                    Text(
                      l10n.quoteValidUntil(formatInstantDate(validUntil)),
                      style: muted,
                    ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }
}

/// As 3 simulações mais recentes e "Ver todas" → `/quotes`. Não aparece se não
/// houver (nem enquanto carrega ou se falhar).
class RecentQuotes extends ConsumerWidget {
  const RecentQuotes({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final l10n = AppLocalizations.of(context);
    final recent = ref.watch(recentQuotesProvider).value;
    if (recent == null || recent.isEmpty) return const SizedBox.shrink();
    return Column(
      key: const Key('recent-quotes'),
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: [
        Text(l10n.myQuotesTitle, style: Theme.of(context).textTheme.titleLarge),
        const SizedBox(height: 8),
        for (final summary in recent) QuoteSummaryCard(summary: summary),
        OutlinedButton(
          key: const Key('recent-quotes-all'),
          onPressed: () => context.push('/quotes'),
          child: Text(l10n.myQuotesSeeAll),
        ),
      ],
    );
  }
}
