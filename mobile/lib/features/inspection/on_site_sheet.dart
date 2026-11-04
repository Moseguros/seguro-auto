import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../core/errors/problem.dart';
import '../../core/network/connectivity.dart';
import '../../core/widgets/message_banner.dart';
import '../../l10n/app_localizations.dart';
import '../proposals/proposal_controller.dart';

/// Abre o pedido de vistoria presencial. Devolve a proposta actualizada, ou
/// `null` se o cliente desistir.
Future<ProposalResponse?> showOnSiteSheet(
  BuildContext context,
  ProposalResponse proposal,
) => showModalBottomSheet<ProposalResponse>(
  context: context,
  isScrollControlled: true,
  useSafeArea: true,
  builder: (context) => OnSiteSheet(proposal: proposal),
);

/// "Prefiro vistoria presencial" (S4-09 §3): local e horas preferidas
/// (10–500 caracteres) → `requestOnSiteInspection`. Precisa de rede.
class OnSiteSheet extends ConsumerStatefulWidget {
  const OnSiteSheet({super.key, required this.proposal});

  final ProposalResponse proposal;

  @override
  ConsumerState<OnSiteSheet> createState() => _OnSiteSheetState();
}

class _OnSiteSheetState extends ConsumerState<OnSiteSheet> {
  final _notes = TextEditingController();
  final _form = GlobalKey<FormState>();
  bool _busy = false;
  String? _error;

  @override
  void dispose() {
    _notes.dispose();
    super.dispose();
  }

  Future<void> _send() async {
    final l10n = AppLocalizations.of(context);
    if (!_form.currentState!.validate()) return;
    setState(() {
      _busy = true;
      _error = null;
    });
    try {
      if (!await ref.read(connectivityMonitorProvider).isOnline()) {
        setState(() => _error = l10n.errorNoConnection);
        return;
      }
      final updated = await ref
          .read(proposalRepositoryProvider)
          .requestOnSiteInspection(
            widget.proposal.id,
            notes: _notes.text.trim(),
            version: widget.proposal.version,
          );
      if (mounted) Navigator.of(context).pop(updated);
    } on Object catch (error) {
      final problem = Problem.from(error);
      if (mounted) {
        setState(
          () => _error = problem.status == 0
              ? l10n.errorNoConnection
              : problem.message(l10n),
        );
      }
    } finally {
      if (mounted) setState(() => _busy = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    final error = _error;
    return Padding(
      padding: EdgeInsets.fromLTRB(
        16,
        16,
        16,
        16 + MediaQuery.viewInsetsOf(context).bottom,
      ),
      child: Form(
        key: _form,
        child: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Text(l10n.inspectionOnSiteTitle, style: theme.textTheme.titleLarge),
            const SizedBox(height: 16),
            TextFormField(
              key: const Key('on-site-notes'),
              controller: _notes,
              minLines: 3,
              maxLines: 6,
              maxLength: 500,
              decoration: InputDecoration(
                labelText: l10n.inspectionOnSiteNotes,
                border: const OutlineInputBorder(),
              ),
              validator: (value) {
                final length = value?.trim().length ?? 0;
                return length < 10 || length > 500
                    ? l10n.proposalDetailsLength
                    : null;
              },
            ),
            if (error != null) ...[
              const SizedBox(height: 8),
              MessageBanner(error, key: const Key('on-site-error')),
            ],
            const SizedBox(height: 16),
            FilledButton(
              key: const Key('on-site-send'),
              style: FilledButton.styleFrom(minimumSize: const Size(64, 48)),
              onPressed: _busy ? null : _send,
              child: Text(l10n.inspectionOnSiteSend),
            ),
          ],
        ),
      ),
    );
  }
}
