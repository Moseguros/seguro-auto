import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../core/errors/problem.dart';
import '../../core/format/dates.dart';
import '../../core/widgets/message_banner.dart';
import '../../l10n/app_localizations.dart';
import '../quote/quote_labels.dart';
import 'payment_controller.dart';
import 'payment_labels.dart';
import 'payment_summary_card.dart' show tabularFigures;

const paymentsPageSize = 20;

/// `/payments` (S5-11 §5): os pagamentos do cliente, os mais recentes primeiro,
/// com mais páginas ao chegar ao fim da lista.
class PaymentsListScreen extends ConsumerStatefulWidget {
  const PaymentsListScreen({super.key});

  @override
  ConsumerState<PaymentsListScreen> createState() => _PaymentsListScreenState();
}

class _PaymentsListScreenState extends ConsumerState<PaymentsListScreen> {
  final _items = <IntentView>[];
  int _nextPage = 0;
  bool _hasMore = true;
  bool _loading = false;
  Object? _error;

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load({bool reset = false}) async {
    if (_loading) return;
    if (reset) {
      _nextPage = 0;
      _hasMore = true;
    }
    if (!_hasMore) return;
    setState(() {
      _loading = true;
      _error = null;
    });
    try {
      final page = await ref
          .read(paymentRepositoryProvider)
          .listMine(page: _nextPage, size: paymentsPageSize);
      if (!mounted) return;
      setState(() {
        if (reset) _items.clear();
        _items.addAll(page.content);
        _nextPage = page.page + 1;
        _hasMore = (page.page + 1) * page.size < page.totalElements;
        _loading = false;
      });
    } on Object catch (error) {
      if (!mounted) return;
      setState(() {
        _error = error;
        _loading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final error = _error;
    final Widget body;
    if (_items.isEmpty && error != null) {
      body = ListView(
        padding: const EdgeInsets.all(16),
        children: [
          MessageBanner(Problem.from(error).message(l10n)),
          const SizedBox(height: 16),
          OutlinedButton(
            style: OutlinedButton.styleFrom(minimumSize: const Size(64, 48)),
            onPressed: _load,
            child: Text(l10n.retry),
          ),
        ],
      );
    } else if (_items.isEmpty && _loading) {
      body = const Center(child: CircularProgressIndicator());
    } else if (_items.isEmpty) {
      body = ListView(
        padding: const EdgeInsets.all(16),
        children: [
          Text(l10n.paymentsListEmpty, key: const Key('payments-empty')),
        ],
      );
    } else {
      body = RefreshIndicator(
        onRefresh: () => _load(reset: true),
        child: NotificationListener<ScrollNotification>(
          onNotification: (notification) {
            if (notification.metrics.extentAfter < 200 && _hasMore) {
              Future.microtask(_load);
            }
            return false;
          },
          child: ListView.builder(
            padding: const EdgeInsets.all(16),
            itemCount: _items.length + (_hasMore || error != null ? 1 : 0),
            itemBuilder: (context, index) {
              if (index < _items.length) return _PaymentCard(_items[index]);
              if (error != null) {
                return TextButton(onPressed: _load, child: Text(l10n.retry));
              }
              return const Padding(
                padding: EdgeInsets.all(16),
                child: Center(child: CircularProgressIndicator()),
              );
            },
          ),
        ),
      );
    }
    return Scaffold(
      appBar: AppBar(title: Text(l10n.paymentsListTitle)),
      body: SafeArea(child: body),
    );
  }
}

class _PaymentCard extends StatelessWidget {
  const _PaymentCard(this.intent);

  final IntentView intent;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    return Card(
      key: Key('payment-${intent.id}'),
      child: InkWell(
        borderRadius: BorderRadius.circular(12),
        onTap: () => context.push('/payments/${intent.id}'),
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                children: [
                  Expanded(
                    child: Text(
                      intent.target.label,
                      style: const TextStyle(fontWeight: FontWeight.w600),
                    ),
                  ),
                  PaymentStatusChip(status: intent.status),
                ],
              ),
              const SizedBox(height: 4),
              Text(
                '${formatInstantDateTime(intent.createdAt)} · '
                '${intent.insurer.displayName ?? ''}',
                style: theme.textTheme.bodySmall,
              ),
              const SizedBox(height: 4),
              Row(
                children: [
                  Expanded(
                    child: Text(paymentMethodLabel(l10n, intent.method.name)),
                  ),
                  Text(
                    money(l10n, intent.amount),
                    style: const TextStyle(
                      fontWeight: FontWeight.w600,
                      fontFeatures: tabularFigures,
                    ),
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
