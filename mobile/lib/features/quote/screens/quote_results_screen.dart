import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../../core/errors/problem.dart';
import '../../../core/format/dates.dart';
import '../../../core/widgets/cta_button.dart';
import '../../../core/widgets/message_banner.dart';
import '../../../l10n/app_localizations.dart';
import '../quote_labels.dart';
import '../quote_repository.dart';
import '../widgets/compare_bar.dart';
import '../widgets/quote_card.dart';
import '../widgets/quote_load_error.dart';
import '../widgets/unavailable_list.dart';

/// Cotações que aparecem em cartões: oferecidas, usadas e expiradas, pela ordem
/// da API.
List<QuoteResponse> cardQuotes(QuoteRequestResponse request) => [
  for (final quote in request.quotes)
    if (quote.status != QuoteResponseStatusEnum.NOT_AVAILABLE) quote,
];

/// Todas as que eram `OFFERED` estão `EXPIRED` (S3-04 §3).
bool isExpired(QuoteRequestResponse request) {
  final statuses = [for (final quote in request.quotes) quote.status];
  return !statuses.contains(QuoteResponseStatusEnum.OFFERED) &&
      statuses.contains(QuoteResponseStatusEnum.EXPIRED);
}

/// `/quote/results/:requestId`: as propostas ordenadas por preço.
class QuoteResultsScreen extends ConsumerStatefulWidget {
  const QuoteResultsScreen({super.key, required this.requestId});

  final String requestId;

  @override
  ConsumerState<QuoteResultsScreen> createState() => _QuoteResultsScreenState();
}

class _QuoteResultsScreenState extends ConsumerState<QuoteResultsScreen> {
  final List<String> _selected = [];

  String get _base => '/quote/results/${widget.requestId}';

  void _toggle(String id, bool checked) {
    final l10n = AppLocalizations.of(context);
    setState(() {
      if (!checked) {
        _selected.remove(id);
      } else if (!_selected.contains(id)) {
        if (_selected.length >= maxCompared) {
          ScaffoldMessenger.of(context)
            ..hideCurrentSnackBar()
            ..showSnackBar(SnackBar(content: Text(l10n.quoteCompareMax)));
          return;
        }
        _selected.add(id);
      }
    });
  }

  void _redo() => context.go('/quote/vehicle?redo=${widget.requestId}');

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final provider = quoteRequestProvider(widget.requestId);
    final request = ref.watch(provider);
    final value = request.value;

    Widget body;
    Widget? bottom;
    if (value != null) {
      final offered = {
        for (final quote in value.quotes)
          if (quote.status == QuoteResponseStatusEnum.OFFERED) quote.id,
      };
      // Uma cotação que deixou de estar oferecida sai da selecção.
      _selected.removeWhere((id) => !offered.contains(id));
      body = RefreshIndicator(
        onRefresh: () => ref.refresh(provider.future),
        child: _Results(
          request: value,
          selected: _selected,
          onToggle: _toggle,
          onRedo: _redo,
          onDetail: (quote) => context.push('$_base/quotes/${quote.id}'),
        ),
      );
      if (_selected.isNotEmpty && !isExpired(value)) {
        bottom = CompareBar(
          count: _selected.length,
          onCompare: () =>
              context.push('$_base/compare?ids=${_selected.join(',')}'),
        );
      }
    } else if (request.hasError) {
      final problem = Problem.from(request.error!);
      final notFound = problem.status == 404;
      body = ListView(
        padding: const EdgeInsets.all(16),
        children: notFound
            ? [
                MessageBanner(
                  l10n.errorQuoteRequestNotFound,
                  key: const Key('quote-not-found'),
                ),
                const SizedBox(height: 16),
                OutlinedButton(
                  onPressed: () => context.go('/quotes'),
                  child: Text(l10n.quoteGoToMyQuotes),
                ),
              ]
            : [
                QuoteLoadError(
                  error: request.error!,
                  onRetry: () => ref.invalidate(provider),
                ),
              ],
      );
    } else {
      body = const Center(child: CircularProgressIndicator());
    }

    return Scaffold(
      appBar: AppBar(title: Text(l10n.quoteResultsTitle)),
      body: SafeArea(child: body),
      bottomNavigationBar: bottom,
    );
  }
}

class _Results extends StatelessWidget {
  const _Results({
    required this.request,
    required this.selected,
    required this.onToggle,
    required this.onRedo,
    required this.onDetail,
  });

  final QuoteRequestResponse request;
  final List<String> selected;
  final void Function(String id, bool checked) onToggle;
  final VoidCallback onRedo;
  final ValueChanged<QuoteResponse> onDetail;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    final muted = theme.textTheme.bodyMedium?.copyWith(
      color: theme.colorScheme.onSurfaceVariant,
    );
    final cards = cardQuotes(request);
    final unavailable = [
      for (final quote in request.quotes)
        if (quote.status == QuoteResponseStatusEnum.NOT_AVAILABLE) quote,
    ];
    final offeredCount = cards
        .where((q) => q.status == QuoteResponseStatusEnum.OFFERED)
        .length;
    final expired = isExpired(request);
    final empty = cards.isEmpty;
    final vehicle = request.vehicle;
    final validUntil = latestValidUntil(request.quotes, {
      QuoteResponseStatusEnum.OFFERED,
    });
    final expiredAt = latestValidUntil(request.quotes, {
      QuoteResponseStatusEnum.EXPIRED,
    });

    final children = <Widget>[
      Text(
        quotedVehicleTitle(
          plate: vehicle.plate,
          make: vehicle.make,
          model: vehicle.model,
        ),
        style: theme.textTheme.titleLarge,
      ),
      Text(productTypeName(l10n, request.productType.name), style: muted),
      Text(
        l10n.quotePeriodRange(
          apiDate(request.startDate),
          apiDate(request.endDate),
        ),
        style: muted,
      ),
      const SizedBox(height: 8),
      if (request.origin == QuoteRequestResponseOriginEnum.BROKER)
        Text(l10n.quoteByBroker, key: const Key('quote-by-broker'))
      else if (validUntil != null)
        Text(
          l10n.quoteSavedUntil(formatInstantDate(validUntil)),
          key: const Key('quote-saved-until'),
        ),
      if (!empty && !expired) ...[
        const SizedBox(height: 8),
        Align(
          alignment: AlignmentDirectional.centerStart,
          child: OutlinedButton(
            key: const Key('quote-change-simulation'),
            onPressed: onRedo,
            child: Text(l10n.quoteChangeSimulation),
          ),
        ),
      ],
      const SizedBox(height: 16),
    ];

    if (empty) {
      children.addAll([
        MessageBanner(
          l10n.quoteNoOffers,
          key: const Key('quote-no-offers'),
          kind: MessageKind.info,
        ),
        const SizedBox(height: 16),
        FilledButton(
          key: const Key('quote-change-simulation'),
          onPressed: onRedo,
          child: Text(l10n.quoteChangeSimulation),
        ),
        const SizedBox(height: 16),
      ]);
    } else if (expired) {
      children.addAll([
        MessageBanner(
          l10n.quoteExpiredOn(
            expiredAt == null ? '' : formatInstantDate(expiredAt),
          ),
          key: const Key('quote-expired'),
          kind: MessageKind.info,
        ),
        const SizedBox(height: 16),
        CtaButton(label: l10n.ctaQuote, onPressed: onRedo),
        const SizedBox(height: 16),
      ]);
    }

    for (final (index, quote) in cards.indexed) {
      final isOffered = quote.status == QuoteResponseStatusEnum.OFFERED;
      children.add(
        QuoteCard(
          quote: quote,
          cheapest: index == 0 && isOffered && offeredCount >= 2,
          onDetail: () => onDetail(quote),
          compareSelected: selected.contains(quote.id),
          onCompare: isOffered && !expired
              ? (checked) => onToggle(quote.id, checked)
              : null,
        ),
      );
    }
    children.add(UnavailableList(quotes: unavailable));

    return ListView(
      // O "puxar para actualizar" funciona mesmo com poucos cartões.
      physics: const AlwaysScrollableScrollPhysics(),
      padding: const EdgeInsets.all(16),
      children: children,
    );
  }
}
