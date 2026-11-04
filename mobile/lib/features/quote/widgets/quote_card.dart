import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../../core/files/file_download.dart';
import '../../../core/format/dates.dart';
import '../../../l10n/app_localizations.dart';
import '../quote_labels.dart';
import 'recent_quotes.dart' show QuoteTag;

/// Logótipo da seguradora (quando há `logoFileId`) ou avatar com as iniciais.
class InsurerAvatar extends ConsumerWidget {
  const InsurerAvatar({super.key, required this.insurer, this.radius = 20});

  final QuoteInsurer insurer;
  final double radius;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final logoId = insurer.logoFileId;
    final logo = logoId == null ? null : ref.watch(fileBytesProvider(logoId));
    final file = logo?.value;
    if (file != null && file.isImage) {
      return CircleAvatar(
        radius: radius,
        backgroundColor: Colors.white,
        backgroundImage: MemoryImage(file.bytes),
      );
    }
    return CircleAvatar(
      radius: radius,
      child: Text(insurerInitials(insurer.displayName)),
    );
  }
}

/// Uma cotação nos resultados (S3-05 §3): seguradora e produto, total em
/// destaque, encargos, coberturas, franquias, validade, "Ver detalhe" e
/// "Comparar".
class QuoteCard extends StatelessWidget {
  const QuoteCard({
    super.key,
    required this.quote,
    required this.cheapest,
    required this.onDetail,
    this.compareSelected = false,
    this.onCompare,
  });

  final QuoteResponse quote;

  /// Etiqueta "Mais barata".
  final bool cheapest;
  final VoidCallback onDetail;
  final bool compareSelected;

  /// `null` esconde a caixa "Comparar" (cotação usada ou expirada).
  final ValueChanged<bool>? onCompare;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    final muted = theme.textTheme.bodyMedium?.copyWith(
      color: theme.colorScheme.onSurfaceVariant,
    );
    final total = quote.totalAmount;
    final validUntil = quote.validUntil;
    final coverages =
        quote.coverages?.toList() ?? const <QuoteCoverageResponse>[];
    final onCompare = this.onCompare;
    return Card(
      key: Key('quote-card-${quote.id}'),
      margin: const EdgeInsets.only(bottom: 12),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Row(
              children: [
                InsurerAvatar(insurer: quote.insurer),
                const SizedBox(width: 12),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        quote.insurer.displayName,
                        style: theme.textTheme.titleMedium,
                      ),
                      Text(quote.product.name, style: muted),
                    ],
                  ),
                ),
              ],
            ),
            if (cheapest ||
                quote.status == QuoteResponseStatusEnum.USED ||
                quote.status == QuoteResponseStatusEnum.EXPIRED) ...[
              const SizedBox(height: 8),
              Wrap(
                spacing: 8,
                children: [
                  if (cheapest)
                    QuoteTag(
                      l10n.quoteCheapest,
                      key: Key('quote-cheapest-${quote.id}'),
                    ),
                  if (quote.status == QuoteResponseStatusEnum.USED)
                    QuoteTag(l10n.quoteUsed),
                  if (quote.status == QuoteResponseStatusEnum.EXPIRED)
                    QuoteTag(
                      l10n.quoteExpiredLabel,
                      color: theme.colorScheme.error,
                    ),
                ],
              ),
            ],
            if (total != null) ...[
              const SizedBox(height: 12),
              Text(
                money(l10n, total),
                key: Key('quote-total-${quote.id}'),
                style: theme.textTheme.headlineSmall?.copyWith(
                  fontWeight: FontWeight.w700,
                  fontFeatures: const [FontFeature.tabularFigures()],
                ),
              ),
              Text(
                l10n.quoteIncludesCharges(
                  moneyCents(l10n, chargesTotal(quote)),
                ),
                style: muted,
              ),
            ],
            if (coverages.isNotEmpty) ...[
              const SizedBox(height: 12),
              Wrap(
                spacing: 6,
                runSpacing: 6,
                children: [
                  for (final coverage in coverages)
                    Chip(
                      visualDensity: VisualDensity.compact,
                      label: Text(coverageName(l10n, coverage.code.name)),
                    ),
                ],
              ),
              for (final coverage in coverages)
                if (deductibleValue(l10n, coverage.deductible)
                    case final value?)
                  Padding(
                    padding: const EdgeInsets.only(top: 4),
                    child: Text(
                      '${coverageName(l10n, coverage.code.name)}: '
                      '${l10n.quoteDeductible(value)}',
                      style: muted,
                    ),
                  ),
            ],
            if (validUntil != null) ...[
              const SizedBox(height: 8),
              Text(
                l10n.quoteValidUntil(formatInstantDate(validUntil)),
                style: muted,
              ),
            ],
            const SizedBox(height: 12),
            Wrap(
              spacing: 8,
              runSpacing: 8,
              crossAxisAlignment: WrapCrossAlignment.center,
              alignment: WrapAlignment.spaceBetween,
              children: [
                OutlinedButton(
                  key: Key('quote-detail-${quote.id}'),
                  onPressed: onDetail,
                  child: Text(l10n.quoteViewDetail),
                ),
                if (onCompare != null)
                  InkWell(
                    onTap: () => onCompare(!compareSelected),
                    child: Row(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        Checkbox(
                          key: Key('quote-compare-${quote.id}'),
                          value: compareSelected,
                          onChanged: (value) => onCompare(value ?? false),
                        ),
                        Text(l10n.quoteCompare),
                        const SizedBox(width: 8),
                      ],
                    ),
                  ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
