import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../kyc/kyc_controller.dart';
import '../../kyc/kyc_screen.dart';
import 'step_host.dart';

/// Passo "Identidade" (S4-08 §3, S4-09 §6): estado do KYC e captura com a
/// câmara, enviada à seguradora da proposta. "Agora não" avança sem enviar.
class IdentityStep extends ConsumerStatefulWidget {
  const IdentityStep({super.key, required this.host, required this.onSkip});

  final StepHost host;

  /// "Agora não": o passo conta como concluído nesta sessão.
  final VoidCallback onSkip;

  @override
  ConsumerState<IdentityStep> createState() => _IdentityStepState();
}

class _IdentityStepState extends ConsumerState<IdentityStep>
    with ProposalStepState {
  @override
  Future<bool> submit() async => true;

  @override
  Widget build(BuildContext context) {
    final proposal = widget.host.proposal;
    final state = proposal.kyc.state;
    // O motivo de uma recusa vem da última verificação (`getMyKyc`).
    final check = state == KycState.REJECTED
        ? ref.watch(myKycProvider).value?.check
        : null;
    return ListView(
      padding: const EdgeInsets.all(16),
      children: [
        KycForm(
          state: state,
          check: check,
          insurerId: proposal.insurer?.id,
          idDocType: proposal.policyholder.idDocType,
          requiredForAutomaticAcceptance:
              proposal.kyc.requiredForAutomaticAcceptance,
          onSubmitted: () async {
            await widget.host.reload();
            widget.host.advance();
          },
          onNotNow: widget.onSkip,
        ),
      ],
    );
  }
}
