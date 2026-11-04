import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../core/widgets/cta_button.dart';
import '../../../l10n/app_localizations.dart';
import '../quote_repository.dart';
import '../widgets/quote_load_error.dart';
import '../widgets/recent_quotes.dart';
import '../widgets/step_scaffold.dart' show QuoteSteps;

/// `/quotes`: "As minhas cotações", paginada (`size=20`), mais páginas ao chegar
/// ao fim.
class MyQuotesScreen extends ConsumerWidget {
  const MyQuotesScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final l10n = AppLocalizations.of(context);
    final quotes = ref.watch(myQuotesControllerProvider);
    final value = quotes.value;

    Widget body;
    if (value != null) {
      if (value.items.isEmpty) {
        body = ListView(
          padding: const EdgeInsets.all(16),
          children: [
            Text(l10n.myQuotesEmpty, key: const Key('my-quotes-empty')),
            const SizedBox(height: 16),
            CtaButton(
              label: l10n.ctaQuote,
              onPressed: () => context.push(QuoteSteps.vehicle),
            ),
          ],
        );
      } else {
        final items = value.items;
        body = RefreshIndicator(
          onRefresh: () => ref.refresh(myQuotesControllerProvider.future),
          child: ListView.builder(
            key: const Key('my-quotes-list'),
            physics: const AlwaysScrollableScrollPhysics(),
            padding: const EdgeInsets.all(16),
            itemCount: items.length + (value.hasMore ? 1 : 0),
            itemBuilder: (context, index) {
              if (index >= items.length) {
                // Chegou ao fim: carrega a página seguinte.
                WidgetsBinding.instance.addPostFrameCallback(
                  (_) =>
                      ref.read(myQuotesControllerProvider.notifier).loadMore(),
                );
                return const Padding(
                  padding: EdgeInsets.all(16),
                  child: Center(child: CircularProgressIndicator()),
                );
              }
              return QuoteSummaryCard(summary: items[index]);
            },
          ),
        );
      }
    } else if (quotes.hasError) {
      body = Padding(
        padding: const EdgeInsets.all(16),
        child: QuoteLoadError(
          error: quotes.error!,
          onRetry: () => ref.invalidate(myQuotesControllerProvider),
        ),
      );
    } else {
      body = const Center(child: CircularProgressIndicator());
    }
    return Scaffold(
      appBar: AppBar(title: Text(l10n.myQuotesTitle)),
      body: SafeArea(child: body),
    );
  }
}
