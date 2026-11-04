import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../../core/format/dates.dart';
import '../../../l10n/app_localizations.dart';
import '../quote_labels.dart';
import '../quote_repository.dart';
import '../quote_rules.dart';
import '../widgets/compare_bar.dart' show maxCompared;
import '../widgets/quote_document_button.dart';
import '../widgets/quote_load_error.dart';
import '../widgets/recent_quotes.dart' show QuoteTag;

/// Largura da coluna dos nomes das linhas.
const compareLabelWidth = 112.0;

/// Largura de cada coluna de cotação.
const compareColumnWidth = 152.0;

/// Cotações a comparar: 2 ou 3 ids, todos `OFFERED` deste pedido; senão `null`
/// (volta aos resultados).
List<QuoteResponse>? comparedQuotes(
  QuoteRequestResponse request,
  List<String> ids,
) {
  final unique = ids.toSet();
  if (unique.length != ids.length ||
      ids.length < 2 ||
      ids.length > maxCompared) {
    return null;
  }
  final quotes = <QuoteResponse>[];
  for (final id in ids) {
    final quote = request.quotes.where((q) => q.id == id).firstOrNull;
    if (quote == null || quote.status != QuoteResponseStatusEnum.OFFERED) {
      return null;
    }
    quotes.add(quote);
  }
  return quotes;
}

/// Uma linha da comparação: nome, um texto por cotação (para saber se é
/// diferente) e as células.
class CompareRow {
  CompareRow(
    this.label,
    this.texts, {
    List<Widget>? cells,
    this.compared = true,
  }) : cells = cells ?? [for (final text in texts) Text(text)];

  final String label;
  final List<String> texts;
  final List<Widget> cells;

  /// `false` nas linhas que não se comparam (documentos, "Ver detalhe").
  final bool compared;

  /// Valor diferente em pelo menos uma coluna (as "principais diferenças").
  bool get differs => compared && texts.toSet().length > 1;
}

/// As linhas da comparação, pela ordem da S3-04 §4 (sem a de documentos).
List<CompareRow> compareRows(
  AppLocalizations l10n,
  List<QuoteResponse> quotes,
) {
  String amount(Money? value) => value == null ? '—' : money(l10n, value);

  final rows = <CompareRow>[
    CompareRow(l10n.compareProduct, [for (final q in quotes) q.product.name]),
    CompareRow(
      l10n.compareTotal,
      [for (final q in quotes) amount(q.totalAmount)],
      cells: [
        for (final q in quotes)
          Text(
            amount(q.totalAmount),
            style: const TextStyle(
              fontWeight: FontWeight.w700,
              fontFeatures: [FontFeature.tabularFigures()],
            ),
          ),
      ],
    ),
    CompareRow(l10n.compareCommercialPremium, [
      for (final q in quotes) amount(q.commercialPremium),
    ]),
  ];

  // Encargos pelo `code`, pela ordem em que aparecem.
  final charges = <String, QuoteChargeResponse>{};
  for (final quote in quotes) {
    for (final charge in quote.charges ?? const <QuoteChargeResponse>[]) {
      charges.putIfAbsent(charge.code, () => charge);
    }
  }
  for (final MapEntry(key: code, value: first) in charges.entries) {
    rows.add(
      CompareRow(chargeLabel(l10n, first), [
        for (final quote in quotes)
          (quote.charges ?? const <QuoteChargeResponse>[])
                  .where((c) => c.code == code)
                  .map((c) => money(l10n, c.amount))
                  .firstOrNull ??
              '—',
      ]),
    );
  }

  // Uma linha por cobertura da união.
  final codes = {
    for (final quote in quotes)
      for (final coverage in quote.coverages ?? const <QuoteCoverageResponse>[])
        coverage.code.name,
  };
  for (final code in CoverageCodes.all.where(codes.contains)) {
    rows.add(
      CompareRow(coverageName(l10n, code), [
        for (final quote in quotes)
          _coverageText(
            l10n,
            (quote.coverages ?? const <QuoteCoverageResponse>[])
                .where((c) => c.code.name == code)
                .firstOrNull,
          ),
      ]),
    );
  }

  rows.add(
    CompareRow(l10n.compareValidUntil, [
      for (final q in quotes)
        q.validUntil == null ? '—' : formatInstantDate(q.validUntil!),
    ]),
  );
  return rows;
}

String _coverageText(AppLocalizations l10n, QuoteCoverageResponse? coverage) {
  if (coverage == null) return l10n.compareNotIncluded;
  final capital = coverage.capital == null
      ? '—'
      : money(l10n, coverage.capital!);
  final premium = money(l10n, coverage.premium);
  final deductible = deductibleValue(l10n, coverage.deductible);
  return deductible == null
      ? l10n.compareCoverageNoDeductible(capital, premium)
      : l10n.compareCoverageWithDeductible(capital, deductible, premium);
}

/// `/quote/results/:requestId/compare?ids=a,b,c`: comparação lado a lado.
class QuoteCompareScreen extends ConsumerWidget {
  const QuoteCompareScreen({
    super.key,
    required this.requestId,
    required this.ids,
  });

  final String requestId;
  final List<String> ids;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final l10n = AppLocalizations.of(context);
    final provider = quoteRequestProvider(requestId);
    final request = ref.watch(provider);
    final base = '/quote/results/$requestId';
    final value = request.value;

    Widget body;
    if (value != null) {
      final quotes = comparedQuotes(value, ids);
      if (quotes == null) {
        WidgetsBinding.instance.addPostFrameCallback((_) {
          if (context.mounted) context.go(base);
        });
        body = const SizedBox.shrink();
      } else {
        body = _CompareTable(
          quotes: quotes,
          onDetail: (quote) => context.push('$base/quotes/${quote.id}'),
        );
      }
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
      appBar: AppBar(title: Text(l10n.compareTitle)),
      body: SafeArea(child: body),
    );
  }
}

class _CompareTable extends StatefulWidget {
  const _CompareTable({required this.quotes, required this.onDetail});

  final List<QuoteResponse> quotes;
  final ValueChanged<QuoteResponse> onDetail;

  @override
  State<_CompareTable> createState() => _CompareTableState();
}

class _CompareTableState extends State<_CompareTable> {
  final _horizontal = ScrollController();

  @override
  void dispose() {
    _horizontal.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    final quotes = widget.quotes;
    var cheapest = quotes.first;
    for (final quote in quotes) {
      if ((quote.totalAmount?.amount ?? 0) <
          (cheapest.totalAmount?.amount ?? 0)) {
        cheapest = quote;
      }
    }

    final rows = <CompareRow>[
      CompareRow(
        l10n.compareInsurer,
        [for (final q in quotes) q.insurer.displayName],
        cells: [
          for (final q in quotes)
            Column(
              key: Key('compare-column-${q.id}'),
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(q.insurer.displayName, style: theme.textTheme.titleSmall),
                if (identical(q, cheapest)) ...[
                  const SizedBox(height: 4),
                  QuoteTag(
                    l10n.quoteCheapest,
                    key: Key('compare-cheapest-${q.id}'),
                  ),
                ],
              ],
            ),
        ],
      ),
      ...compareRows(l10n, quotes),
      CompareRow(
        l10n.compareDocuments,
        const [],
        compared: false,
        cells: [
          for (final q in quotes)
            Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                if (q.product.generalConditionsFileId case final id?)
                  QuoteDocumentButton(
                    fileId: id,
                    label: l10n.quoteGeneralConditions,
                    name: 'condicoes-gerais',
                  ),
                if (q.product.preContractInfoFileId case final id?)
                  QuoteDocumentButton(
                    fileId: id,
                    label: l10n.quotePreContractInfo,
                    name: 'informacao-pre-contratual',
                  ),
              ],
            ),
        ],
      ),
      CompareRow(
        '',
        const [],
        compared: false,
        cells: [
          for (final q in quotes)
            OutlinedButton(
              key: Key('compare-detail-${q.id}'),
              onPressed: () => widget.onDetail(q),
              child: Text(l10n.quoteViewDetail, textAlign: TextAlign.center),
            ),
        ],
      ),
    ];

    final width = compareLabelWidth + compareColumnWidth * quotes.length;
    return SingleChildScrollView(
      padding: const EdgeInsets.symmetric(vertical: 8),
      child: SingleChildScrollView(
        key: const Key('compare-scroll'),
        controller: _horizontal,
        scrollDirection: Axis.horizontal,
        child: SizedBox(
          width: width,
          child: Column(
            children: [
              for (final row in rows)
                _CompareLine(row: row, horizontal: _horizontal),
            ],
          ),
        ),
      ),
    );
  }
}

/// Uma linha: a primeira coluna fica fixa à esquerda enquanto as colunas das
/// cotações fazem scroll horizontal.
///
/// A linha é desenhada da direita para a esquerda (com as células por ordem
/// inversa) para o nome ser pintado por cima das colunas que passam por baixo
/// dele; o nome desloca-se com o scroll, por isso fica sempre no mesmo sítio.
class _CompareLine extends StatelessWidget {
  const _CompareLine({required this.row, required this.horizontal});

  final CompareRow row;
  final ScrollController horizontal;

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final differs = row.differs;
    final background = differs
        ? theme.scaffoldBackgroundColor
        : theme.colorScheme.surface;
    final label = AnimatedBuilder(
      animation: horizontal,
      builder: (context, child) => Transform.translate(
        offset: Offset(horizontal.hasClients ? horizontal.offset : 0, 0),
        child: child,
      ),
      child: Container(
        width: compareLabelWidth,
        padding: const EdgeInsets.all(8),
        decoration: BoxDecoration(
          color: background,
          border: Border(
            right: BorderSide(color: theme.colorScheme.outlineVariant),
          ),
        ),
        child: Text(
          row.label,
          key: differs ? Key('compare-differs-${row.label}') : null,
          style: differs ? const TextStyle(fontWeight: FontWeight.w700) : null,
        ),
      ),
    );
    return DecoratedBox(
      decoration: BoxDecoration(
        color: background,
        border: Border(
          bottom: BorderSide(color: theme.colorScheme.outlineVariant),
        ),
      ),
      child: IntrinsicHeight(
        child: Row(
          textDirection: TextDirection.rtl,
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            for (final cell in row.cells.reversed)
              Container(
                width: compareColumnWidth,
                padding: const EdgeInsets.all(8),
                alignment: AlignmentDirectional.topStart,
                child: cell,
              ),
            label,
          ],
        ),
      ),
    );
  }
}
