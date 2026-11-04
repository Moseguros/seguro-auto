import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../../core/errors/problem.dart';
import '../wizard_steps.dart';

/// Grava com o `version` da última resposta; devolve o erro (já tratado no caso
/// dos 409) ou `null` se gravou.
typedef SaveProposal =
    Future<Problem?> Function(
      Future<ProposalResponse> Function(int version) action,
    );

/// O que o assistente dá a cada passo.
class StepHost {
  const StepHost({
    required this.proposal,
    required this.save,
    required this.goTo,
    required this.advance,
    required this.reload,
  });

  final ProposalResponse proposal;
  final SaveProposal save;
  final void Function(ProposalStep step) goTo;

  /// Passa ao passo seguinte.
  final VoidCallback advance;

  final Future<void> Function() reload;
}

/// Um passo do assistente: "Continuar" chama [submit], que grava (se preciso) e
/// devolve `true` para avançar.
mixin ProposalStepState<T extends ConsumerStatefulWidget> on ConsumerState<T> {
  Future<bool> submit();
}

/// Rótulo e valor numa linha (resumos).
class InfoLine extends StatelessWidget {
  const InfoLine(this.label, this.value, {super.key, this.strong = false});

  final String label;
  final String value;
  final bool strong;

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final style = strong
        ? theme.textTheme.titleMedium?.copyWith(fontWeight: FontWeight.w700)
        : theme.textTheme.bodyMedium;
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 4),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Expanded(
            child: Text(
              label,
              style: style?.copyWith(
                color: strong ? null : theme.colorScheme.onSurfaceVariant,
              ),
            ),
          ),
          const SizedBox(width: 12),
          Flexible(
            child: Text(value, style: style, textAlign: TextAlign.end),
          ),
        ],
      ),
    );
  }
}
