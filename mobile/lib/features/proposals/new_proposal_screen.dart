import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../core/errors/problem.dart';
import '../../core/widgets/cta_button.dart';
import '../../core/widgets/message_banner.dart';
import '../../l10n/app_localizations.dart';
import '../quote/widgets/step_scaffold.dart' show QuoteSteps;
import 'proposal_controller.dart';

/// Erros da cotação que pedem uma simulação nova (S4-08 §2).
const _quoteGoneCodes = {
  'QUOTE_EXPIRED',
  'QUOTE_NOT_AVAILABLE',
  'QUOTE_ALREADY_USED',
  'QUOTE_DATA_CHANGED',
};

/// `/proposals/new?quoteId=`: cria a proposta (`createProposal`, canal `APP`) e
/// substitui-se por `/proposals/:id`.
///
/// `PROFILE_INCOMPLETE` → `/profile?complete=1&from=<esta rota>`;
/// `PROPOSAL_ALREADY_EXISTS` → a proposta que já existe.
class NewProposalScreen extends ConsumerStatefulWidget {
  const NewProposalScreen({super.key, required this.quoteId});

  final String? quoteId;

  @override
  ConsumerState<NewProposalScreen> createState() => _NewProposalScreenState();
}

class _NewProposalScreenState extends ConsumerState<NewProposalScreen> {
  Problem? _problem;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) => _create());
  }

  Future<void> _create() async {
    final quoteId = widget.quoteId;
    if (quoteId == null || quoteId.isEmpty) {
      setState(
        () => _problem = const Problem(status: 404, code: 'QUOTE_NOT_FOUND'),
      );
      return;
    }
    setState(() => _problem = null);
    try {
      final proposal = await ref
          .read(proposalRepositoryProvider)
          .create(quoteId);
      ref.invalidate(myProposalsProvider);
      if (mounted) context.pushReplacement('/proposals/${proposal.id}');
    } on Object catch (error) {
      final problem = Problem.from(error);
      if (!mounted) return;
      if (problem.code == 'PROFILE_INCOMPLETE') {
        final here = Uri(
          path: '/proposals/new',
          queryParameters: {'quoteId': quoteId},
        ).toString();
        context.go(
          Uri(
            path: '/profile',
            queryParameters: {'complete': '1', 'from': here},
          ).toString(),
        );
        return;
      }
      final existing = problem.body['proposalId'];
      if (problem.code == 'PROPOSAL_ALREADY_EXISTS' && existing is String) {
        context.pushReplacement('/proposals/$existing');
        return;
      }
      setState(() => _problem = problem);
    }
  }

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final problem = _problem;
    return Scaffold(
      appBar: AppBar(title: Text(l10n.proposalsTitle)),
      body: SafeArea(
        child: problem == null
            ? Center(
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    const CircularProgressIndicator(),
                    const SizedBox(height: 16),
                    Text(l10n.proposalCreating),
                  ],
                ),
              )
            : ListView(
                padding: const EdgeInsets.all(16),
                children: [
                  MessageBanner(
                    problem.message(l10n),
                    key: const Key('new-proposal-error'),
                  ),
                  const SizedBox(height: 16),
                  if (_quoteGoneCodes.contains(problem.code))
                    CtaButton(
                      key: const Key('new-proposal-simulate'),
                      label: l10n.ctaQuote,
                      onPressed: () => context.go(QuoteSteps.vehicle),
                    )
                  else
                    OutlinedButton(
                      key: const Key('new-proposal-retry'),
                      style: OutlinedButton.styleFrom(
                        minimumSize: const Size(64, 48),
                      ),
                      onPressed: _create,
                      child: Text(l10n.retry),
                    ),
                ],
              ),
      ),
    );
  }
}
