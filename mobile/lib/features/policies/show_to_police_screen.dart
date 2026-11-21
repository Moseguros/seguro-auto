import 'dart:async';
import 'dart:math' as math;

import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../core/clock.dart';
import '../../core/errors/problem.dart';
import '../../core/format/dates.dart';
import '../../core/theme.dart';
import '../../l10n/app_localizations.dart';
import 'policies_controller.dart';
import 'policy_labels.dart';
import 'policy_repository.dart';
import 'screen_keeper.dart';

/// Idade a partir da qual os dados guardados mostram o aviso.
const policeStaleAfter = Duration(hours: 24);

/// `/policies/:id/police` — "Mostrar à polícia" (S6-07 §3). Funciona sem rede,
/// a partir da cache. Fundo branco, QR grande, brilho no máximo e o ecrã sempre
/// ligado enquanto está aberto.
class ShowToPoliceScreen extends ConsumerStatefulWidget {
  const ShowToPoliceScreen({super.key, required this.policyId});

  final String policyId;

  @override
  ConsumerState<ShowToPoliceScreen> createState() => _ShowToPoliceState();
}

class _ShowToPoliceState extends ConsumerState<ShowToPoliceScreen> {
  late final ScreenKeeper _keeper;

  @override
  void initState() {
    super.initState();
    _keeper = ref.read(screenKeeperProvider);
    unawaited(_keeper.keepBright());
  }

  @override
  void dispose() {
    unawaited(_keeper.restore());
    super.dispose();
  }

  void _close() {
    if (context.canPop()) {
      context.pop();
    } else {
      context.go('/policies/${widget.policyId}');
    }
  }

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final state = ref.watch(policyDetailProvider(widget.policyId));
    final value = state.value;
    final entry = value?.value;
    final certificate = entry?.detail?.certificate;

    final Widget content;
    if (entry != null && certificate != null) {
      content = _PoliceCard(
        entry: entry,
        certificate: certificate,
        now: ref.read(clockProvider)(),
      );
    } else if (state.isLoading) {
      content = const Center(child: CircularProgressIndicator());
    } else {
      content = Center(
        child: Padding(
          padding: const EdgeInsets.all(24),
          child: Text(
            state.hasError
                ? Problem.from(state.error!).message(l10n)
                : l10n.policeNoCertificate,
            key: const Key('police-unavailable'),
            textAlign: TextAlign.center,
            style: const TextStyle(color: Colors.black87),
          ),
        ),
      );
    }

    return Scaffold(
      // Branco mesmo em modo escuro, para o QR ler bem.
      backgroundColor: Colors.white,
      appBar: AppBar(
        backgroundColor: Colors.white,
        foregroundColor: BrandColors.primary,
        automaticallyImplyLeading: false,
        title: Text(l10n.policyShowToPolice),
        actions: [
          TextButton.icon(
            key: const Key('police-close'),
            style: TextButton.styleFrom(
              foregroundColor: BrandColors.primary,
              minimumSize: const Size(48, 48),
            ),
            onPressed: _close,
            icon: const Icon(Icons.close),
            label: Text(l10n.close),
          ),
        ],
      ),
      body: SafeArea(child: content),
    );
  }
}

class _PoliceCard extends StatelessWidget {
  const _PoliceCard({
    required this.entry,
    required this.certificate,
    required this.now,
  });

  final PolicyEntry entry;
  final PolicyCertificate certificate;
  final DateTime now;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    final stale =
        now.toUtc().difference(entry.syncedAt.toUtc()) > policeStaleAfter;
    const ink = Colors.black87;
    return LayoutBuilder(
      builder: (context, constraints) {
        final side =
            math.min(constraints.maxWidth, constraints.maxHeight) * 0.8;
        return SingleChildScrollView(
          key: const Key('police-screen'),
          padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
          child: DefaultTextStyle.merge(
            style: const TextStyle(color: ink),
            textAlign: TextAlign.center,
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                Center(
                  child: CertificateQr(
                    key: const Key('police-qr'),
                    data: certificate.verificationUrl,
                    size: side,
                  ),
                ),
                const SizedBox(height: 8),
                Text(
                  l10n.policeQrHint,
                  style: theme.textTheme.bodyMedium?.copyWith(color: ink),
                  textAlign: TextAlign.center,
                ),
                const SizedBox(height: 16),
                Text(
                  entry.vehicle.plate,
                  key: const Key('police-plate'),
                  textAlign: TextAlign.center,
                  style: const TextStyle(
                    color: Colors.black,
                    fontSize: 36,
                    fontWeight: FontWeight.w800,
                    letterSpacing: 1.5,
                  ),
                ),
                if (entry.insurer.name case final insurer?)
                  Text(
                    insurer,
                    textAlign: TextAlign.center,
                    style: theme.textTheme.titleMedium?.copyWith(color: ink),
                  ),
                const SizedBox(height: 8),
                Text(
                  l10n.policyValidFromTo(
                    formatInstantDateTime(certificate.validFrom),
                    formatInstantDateTime(certificate.validTo),
                  ),
                  textAlign: TextAlign.center,
                ),
                Text(
                  coverageTypeLabel(l10n, entry.coverageType),
                  textAlign: TextAlign.center,
                ),
                Text(
                  l10n.policyCertificateNumber(certificate.number),
                  textAlign: TextAlign.center,
                ),
                Text(
                  l10n.policiesNumber(entry.number),
                  textAlign: TextAlign.center,
                ),
                const SizedBox(height: 12),
                Text(
                  l10n.policeStatusAt(
                    formatInstantDateTime(entry.syncedAt),
                    coverStatusLabel(l10n, entry.coverStatus),
                  ),
                  key: const Key('police-status'),
                  textAlign: TextAlign.center,
                  style: theme.textTheme.titleSmall?.copyWith(color: ink),
                ),
                if (stale) ...[
                  const SizedBox(height: 12),
                  Container(
                    key: const Key('police-stale'),
                    padding: const EdgeInsets.all(12),
                    decoration: BoxDecoration(
                      color: const Color(0xFFFFF4E5),
                      border: Border.all(color: const Color(0xFFB26A00)),
                      borderRadius: BorderRadius.circular(8),
                    ),
                    child: Text(l10n.policeStale, textAlign: TextAlign.center),
                  ),
                ],
              ],
            ),
          ),
        );
      },
    );
  }
}
