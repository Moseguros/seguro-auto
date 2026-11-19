import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../core/auth/session_controller.dart';
import '../../core/errors/problem.dart';
import '../../core/widgets/cta_button.dart';
import '../../core/widgets/message_banner.dart';
import '../../l10n/app_localizations.dart';
import '../quote/quote_labels.dart';
import 'payment_controller.dart';
import 'payment_labels.dart';
import 'payment_method_picker.dart';
import 'payment_summary_card.dart';

enum _Blocked { notFound, notPayable, paid }

/// `/proposals/:id/pay` (S5-11 §3): resumo, escolha da forma de pagamento e o
/// botão laranja "Adquirir apólice" (o único do ecrã), fixo no fundo com o
/// total por cima.
class PaymentScreen extends ConsumerStatefulWidget {
  const PaymentScreen({super.key, required this.proposalId});

  final String proposalId;

  @override
  ConsumerState<PaymentScreen> createState() => _PaymentScreenState();
}

class _PaymentScreenState extends ConsumerState<PaymentScreen> {
  final _msisdn = TextEditingController();
  String? _method;
  bool _msisdnTouched = false;
  String? _serverFieldError;
  _Blocked? _blocked;
  String? _notice;
  bool _paying = false;

  @override
  void initState() {
    super.initState();
    final national = nationalFromMsisdn(
      ref.read(sessionControllerProvider).value?.me?.msisdn,
    );
    if (mpesaNational.hasMatch(national)) _msisdn.text = national;
  }

  @override
  void dispose() {
    _msisdn.dispose();
    super.dispose();
  }

  String? get _msisdnError => _method == 'MPESA'
      ? (_serverFieldError ?? validateMpesaMsisdn(_msisdn.text))
      : null;

  Future<void> _pay() async {
    final method = _method;
    if (method == null) return;
    if (_msisdnError != null) {
      setState(() => _msisdnTouched = true);
      return;
    }
    final l10n = AppLocalizations.of(context);
    setState(() {
      _paying = true;
      _notice = null;
    });
    try {
      final intent = await ref
          .read(paymentControllerProvider(widget.proposalId))
          .pay(
            method: method,
            payerMsisdn: method == 'MPESA' ? e164(_msisdn.text) : null,
          );
      if (!mounted) return;
      context.pushReplacement('/payments/${intent.id}');
    } on Object catch (error) {
      if (!mounted) return;
      final problem = Problem.from(error);
      final intentId = problem.intentId;
      if (problem.code == 'PAYMENT_IN_PROGRESS' && intentId != null) {
        context.pushReplacement('/payments/$intentId');
        return;
      }
      setState(() {
        _paying = false;
        switch (problem.code) {
          case 'PAYMENT_TARGET_ALREADY_PAID':
            _blocked = _Blocked.paid;
          case 'PAYMENT_TARGET_NOT_PAYABLE':
            _blocked = _Blocked.notPayable;
          case 'PROPOSAL_NOT_FOUND':
            _blocked = _Blocked.notFound;
          case 'PAYMENT_METHOD_NOT_AVAILABLE':
            _notice = l10n.paymentsPayMethodUnavailable;
            _method = null;
            ref.invalidate(paymentMethodsProvider(widget.proposalId));
          default:
            final field = problem.code == 'VALIDATION_ERROR'
                ? problem.fieldError(['payerMsisdn'])
                : null;
            if (field != null) {
              _serverFieldError = field.code;
              _msisdnTouched = true;
            } else {
              _notice = problem.message(l10n);
            }
        }
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    // Mantém a chave de idempotência enquanto o ecrã está aberto.
    ref.watch(paymentControllerProvider(widget.proposalId));
    final provider = paymentMethodsProvider(widget.proposalId);
    final methods = ref.watch(provider);

    final data = methods.value;
    // Um só método: fica escolhido.
    if (data != null && _method == null && data.methods.length == 1) {
      _method = data.methods.first.method.name;
    }

    Widget? bottom;
    final Widget body;
    final blocked =
        _blocked ??
        (data?.paid == true
            ? _Blocked.paid
            : methods.hasError
            ? _blockedFor(methods.error!)
            : null);
    if (blocked != null) {
      body = _BlockedView(kind: blocked, proposalId: widget.proposalId);
    } else if (data != null) {
      final active = data.activeIntent;
      final mpesaBusy =
          active != null &&
          active.method == IntentViewMethodEnum.MPESA &&
          !isFinalPayment(active.status);
      final valid = _method != null && _msisdnError == null;
      final insurer = data.insurer.displayName ?? '';
      final notice = _notice;
      body = ListView(
        padding: const EdgeInsets.all(16),
        children: [
          if (active != null) ...[
            _ActiveIntentBanner(intentId: active.id),
            const SizedBox(height: 16),
          ],
          PaymentSummaryCard(
            proposalNumber: data.proposalNumber,
            insurerName: insurer,
            amount: data.amount,
            expiresAt: data.expiresAt,
          ),
          const SizedBox(height: 16),
          if (notice != null) ...[
            MessageBanner(notice, key: const Key('payment-notice')),
            const SizedBox(height: 16),
          ],
          if (data.methods.isEmpty)
            MessageBanner(
              l10n.paymentsPayNoMethods,
              key: const Key('payment-no-methods'),
              kind: MessageKind.info,
            )
          else
            PaymentMethodPicker(
              methods: data.methods.toList(),
              selected: _method,
              enabled: !_paying,
              onSelected: (method) => setState(() {
                _method = method;
                _notice = null;
              }),
              msisdn: _msisdn,
              msisdnError: _msisdnTouched ? _msisdnError : null,
              onMsisdnChanged: (value) => setState(() {
                _serverFieldError = null;
                if (value.length == 9) _msisdnTouched = true;
              }),
            ),
        ],
      );
      if (data.methods.isNotEmpty) {
        bottom = _PayBar(
          total: money(l10n, data.amount),
          busy: _paying,
          onPressed: valid && !mpesaBusy && !_paying ? _pay : null,
        );
      }
    } else if (methods.hasError) {
      body = ListView(
        padding: const EdgeInsets.all(16),
        children: [
          MessageBanner(Problem.from(methods.error!).message(l10n)),
          const SizedBox(height: 16),
          OutlinedButton(
            style: OutlinedButton.styleFrom(minimumSize: const Size(64, 48)),
            onPressed: () => ref.invalidate(provider),
            child: Text(l10n.retry),
          ),
        ],
      );
    } else {
      body = const _PaymentSkeleton();
    }

    return Scaffold(
      appBar: AppBar(title: Text(l10n.paymentsTitle)),
      body: SafeArea(child: body),
      bottomNavigationBar: bottom,
    );
  }

  /// Erros da leitura dos métodos que bloqueiam o pagamento.
  _Blocked? _blockedFor(Object error) {
    final problem = Problem.from(error);
    if (problem.code == 'PROPOSAL_NOT_FOUND' || problem.status == 404) {
      return _Blocked.notFound;
    }
    if (problem.code == 'PAYMENT_TARGET_NOT_PAYABLE') {
      return _Blocked.notPayable;
    }
    return null;
  }
}

class _PayBar extends StatelessWidget {
  const _PayBar({
    required this.total,
    required this.busy,
    required this.onPressed,
  });

  final String total;
  final bool busy;
  final VoidCallback? onPressed;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    return SafeArea(
      child: Material(
        elevation: 3,
        color: theme.colorScheme.surface,
        child: Padding(
          padding: const EdgeInsets.fromLTRB(16, 8, 16, 8),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              Row(
                children: [
                  Expanded(child: Text(l10n.paymentsSummaryTotal)),
                  Text(
                    total,
                    style: const TextStyle(
                      fontWeight: FontWeight.w700,
                      fontFeatures: tabularFigures,
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 8),
              CtaButton(
                key: const Key('payment-submit'),
                label: l10n.paymentsPaySubmit,
                onPressed: onPressed,
                icon: busy
                    ? const SizedBox.square(
                        dimension: 18,
                        child: CircularProgressIndicator(strokeWidth: 2),
                      )
                    : null,
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class _ActiveIntentBanner extends StatelessWidget {
  const _ActiveIntentBanner({required this.intentId});

  final String intentId;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: [
        MessageBanner(
          l10n.paymentsPayActiveIntent,
          key: const Key('payment-active-intent'),
          kind: MessageKind.info,
        ),
        const SizedBox(height: 8),
        FilledButton(
          key: const Key('payment-view-active'),
          style: FilledButton.styleFrom(minimumSize: const Size(64, 48)),
          onPressed: () => context.push('/payments/$intentId'),
          child: Text(l10n.paymentsPayViewPayment),
        ),
      ],
    );
  }
}

class _BlockedView extends StatelessWidget {
  const _BlockedView({required this.kind, required this.proposalId});

  final _Blocked kind;
  final String proposalId;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final (message, kindOf) = switch (kind) {
      _Blocked.notFound => (l10n.paymentsPayNotFound, MessageKind.error),
      _Blocked.notPayable => (l10n.paymentsPayNotPayable, MessageKind.error),
      _Blocked.paid => (l10n.paymentsPayAlreadyPaid, MessageKind.success),
    };
    return ListView(
      padding: const EdgeInsets.all(16),
      children: [
        MessageBanner(message, key: const Key('payment-blocked'), kind: kindOf),
        const SizedBox(height: 16),
        OutlinedButton(
          style: OutlinedButton.styleFrom(minimumSize: const Size(64, 48)),
          onPressed: () => context.go(
            kind == _Blocked.notFound ? '/proposals' : '/proposals/$proposalId',
          ),
          child: Text(
            kind == _Blocked.notFound
                ? l10n.paymentsPayBackToProposals
                : l10n.paymentsPayBackToProposal,
          ),
        ),
      ],
    );
  }
}

/// Esqueleto do cartão de resumo e de duas opções, enquanto carrega.
class _PaymentSkeleton extends StatelessWidget {
  const _PaymentSkeleton();

  @override
  Widget build(BuildContext context) {
    final color = Theme.of(context).colorScheme.surfaceContainerHighest;
    Widget block(double height) => Container(
      height: height,
      margin: const EdgeInsets.only(bottom: 16),
      decoration: BoxDecoration(
        color: color,
        borderRadius: BorderRadius.circular(12),
      ),
    );
    return Padding(
      key: const Key('payment-skeleton'),
      padding: const EdgeInsets.all(16),
      child: Column(children: [block(150), block(64), block(64)]),
    );
  }
}
