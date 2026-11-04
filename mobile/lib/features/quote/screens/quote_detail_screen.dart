import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../../core/format/dates.dart';
import '../../../core/widgets/message_banner.dart';
import '../../../l10n/app_localizations.dart';
import '../quote_labels.dart';
import '../quote_repository.dart';
import '../widgets/quote_card.dart' show InsurerAvatar;
import '../widgets/quote_document_button.dart';
import '../widgets/quote_load_error.dart';
import '../widgets/recent_quotes.dart' show QuoteTag;

/// `/quote/results/:requestId/quotes/:quoteId`: detalhe de uma cotação
/// (S3-04 §5).
class QuoteDetailScreen extends ConsumerWidget {
  const QuoteDetailScreen({
    super.key,
    required this.requestId,
    required this.quoteId,
  });

  final String requestId;
  final String quoteId;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final l10n = AppLocalizations.of(context);
    final provider = quoteRequestProvider(requestId);
    final request = ref.watch(provider);
    final value = request.value;

    Widget body;
    if (value != null) {
      final quote = value.quotes.where((q) => q.id == quoteId).firstOrNull;
      body = quote == null
          ? ListView(
              padding: const EdgeInsets.all(16),
              children: [
                MessageBanner(
                  l10n.errorQuoteNotFound,
                  key: const Key('quote-not-found'),
                ),
              ],
            )
          : _Detail(request: value, quote: quote);
    } else if (request.hasError) {
      body = Padding(
        padding: const EdgeInsets.all(16),
        child: QuoteLoadError(
          error: request.error!,
          onRetry: () => ref.invalidate(provider),
        ),
      );
    } else {
      body = const Center(child: CircularProgressIndicator());
    }
    return Scaffold(
      appBar: AppBar(title: Text(l10n.quoteDetailTitle)),
      body: SafeArea(child: body),
    );
  }
}

class _Detail extends StatelessWidget {
  const _Detail({required this.request, required this.quote});

  final QuoteRequestResponse request;
  final QuoteResponse quote;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    final muted = theme.textTheme.bodyMedium?.copyWith(
      color: theme.colorScheme.onSurfaceVariant,
    );
    final status = quote.status;
    final validUntil = quote.validUntil;

    final children = <Widget>[
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
                  style: theme.textTheme.titleLarge,
                ),
                Text(
                  l10n.quoteProductVersion(
                    quote.product.name,
                    quote.product.versionNo,
                  ),
                  style: muted,
                ),
              ],
            ),
          ),
        ],
      ),
      if (status != QuoteResponseStatusEnum.OFFERED) ...[
        const SizedBox(height: 8),
        Align(
          alignment: AlignmentDirectional.centerStart,
          child: QuoteTag(
            switch (status) {
              QuoteResponseStatusEnum.USED => l10n.quoteUsed,
              QuoteResponseStatusEnum.EXPIRED => l10n.quoteExpiredLabel,
              _ => l10n.quoteNotAvailableLabel,
            },
            key: const Key('quote-status'),
            color: status == QuoteResponseStatusEnum.USED
                ? null
                : theme.colorScheme.error,
          ),
        ),
      ],
      const SizedBox(height: 16),
    ];

    if (status == QuoteResponseStatusEnum.NOT_AVAILABLE) {
      children.add(
        MessageBanner(
          unavailableReasonText(l10n, quote.unavailableReason?.name),
          key: const Key('quote-unavailable-reason'),
          kind: MessageKind.info,
        ),
      );
      return ListView(padding: const EdgeInsets.all(16), children: children);
    }

    Widget amountLine(String label, Money? value, {bool strong = false}) {
      final style = strong
          ? theme.textTheme.titleMedium?.copyWith(fontWeight: FontWeight.w700)
          : theme.textTheme.bodyLarge;
      return Padding(
        padding: const EdgeInsets.symmetric(vertical: 4),
        child: Row(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Expanded(child: Text(label, style: style)),
            const SizedBox(width: 8),
            Text(
              value == null ? '—' : money(l10n, value),
              style: style?.copyWith(
                fontFeatures: const [FontFeature.tabularFigures()],
              ),
            ),
          ],
        ),
      );
    }

    children.addAll([
      Text(l10n.quoteDetailCoverages, style: theme.textTheme.titleMedium),
      const SizedBox(height: 8),
      for (final coverage in quote.coverages ?? const <QuoteCoverageResponse>[])
        Card(
          margin: const EdgeInsets.only(bottom: 8),
          child: Padding(
            padding: const EdgeInsets.all(12),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                amountLine(
                  coverageName(l10n, coverage.code.name),
                  coverage.premium,
                ),
                if (coverage.capital case final capital?)
                  Text(
                    l10n.quoteDetailCapital(money(l10n, capital)),
                    style: muted,
                  ),
                Text(switch (deductibleValue(l10n, coverage.deductible)) {
                  final value? => l10n.quoteDeductible(value),
                  null => l10n.quoteNoDeductible,
                }, style: muted),
              ],
            ),
          ),
        ),
      const Divider(height: 24),
      amountLine(l10n.compareCommercialPremium, quote.commercialPremium),
      for (final charge in quote.charges ?? const <QuoteChargeResponse>[])
        amountLine(chargeLabel(l10n, charge), charge.amount),
      if (quote.roundingAdjustment case final rounding?
          when rounding.amount != 0)
        amountLine(l10n.quoteRoundingAdjustment, rounding),
      amountLine(l10n.compareTotal, quote.totalAmount, strong: true),
      const Divider(height: 24),
      Text(l10n.quoteDetailPeriod, style: theme.textTheme.titleSmall),
      Text(
        l10n.quotePeriodRange(
          apiDate(request.startDate),
          apiDate(request.endDate),
        ),
      ),
      if (validUntil != null) ...[
        const SizedBox(height: 8),
        Text(l10n.quoteDetailValidity, style: theme.textTheme.titleSmall),
        Text(l10n.quoteValidUntil(formatInstantDate(validUntil))),
      ],
      const SizedBox(height: 16),
      Text(l10n.compareDocuments, style: theme.textTheme.titleSmall),
      const SizedBox(height: 8),
      if (quote.product.generalConditionsFileId case final id?)
        QuoteDocumentButton(
          fileId: id,
          label: l10n.quoteGeneralConditions,
          name: 'condicoes-gerais',
        ),
      if (quote.product.preContractInfoFileId case final id?) ...[
        const SizedBox(height: 8),
        QuoteDocumentButton(
          fileId: id,
          label: l10n.quotePreContractInfo,
          name: 'informacao-pre-contratual',
        ),
      ],
      const SizedBox(height: 16),
      MessageBanner(
        l10n.quoteProposalSoon,
        key: const Key('quote-proposal-soon'),
        kind: MessageKind.info,
      ),
    ]);
    return ListView(padding: const EdgeInsets.all(16), children: children);
  }
}
