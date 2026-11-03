import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../core/api/api_client.dart';
import '../../core/errors/problem.dart';
import '../../core/widgets/message_banner.dart';
import '../../l10n/app_localizations.dart';
import '../auth/code_screen.dart';
import '../auth/login_controller.dart';
import '../auth/login_screen.dart';
import '../auth/otp.dart';
import 'profile_controller.dart';

/// Abre o `BottomSheet` para acrescentar ou alterar um contacto.
/// Devolve `true` quando o contacto ficou confirmado.
Future<bool?> showContactSheet(
  BuildContext context, {
  required String type,
  required bool replacing,
}) => showModalBottomSheet<bool>(
  context: context,
  isScrollControlled: true,
  useSafeArea: true,
  showDragHandle: true,
  builder: (context) => ContactSheet(type: type, replacing: replacing),
);

/// Acrescenta ou troca um contacto: os mesmos passos do login (identificador → código),
/// mas com `startContactVerification` (também no reenvio) e `verifyContact`.
class ContactSheet extends ConsumerStatefulWidget {
  const ContactSheet({super.key, required this.type, required this.replacing});

  /// `MSISDN` ou `EMAIL`.
  final String type;

  /// Já existe um contacto deste tipo (o título diz "Alterar").
  final bool replacing;

  @override
  ConsumerState<ContactSheet> createState() => _ContactSheetState();
}

class _ContactSheetState extends ConsumerState<ContactSheet> {
  OtpRequestData? _lastRequest;
  SentCode? _sent;

  String _title(AppLocalizations l10n) {
    final msisdn = widget.type == OtpCodes.msisdn;
    if (widget.replacing) {
      return msisdn ? l10n.contactsChangeMsisdn : l10n.contactsChangeEmail;
    }
    return msisdn ? l10n.contactsAddMsisdn : l10n.contactsAddEmail;
  }

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final options = ref.watch(otpOptionsProvider);
    final contacts = ref.read(contactsControllerProvider.notifier);
    final sent = _sent;

    final Widget content = switch (options) {
      AsyncData(:final value) when sent != null => CodeForm(
        sent: sent,
        codeLength: value.codeLength,
        onRequest: contacts.startVerification,
        onVerify: (challengeId, code) async {
          await contacts.verify(challengeId, code);
          if (context.mounted) Navigator.of(context).pop(true);
        },
        getChallenge: (challengeId) async =>
            (await ref
                    .read(apiClientProvider)
                    .identity
                    .getOtpChallenge(challengeId: challengeId))
                .data!,
        onChangeIdentifier: () => setState(() => _sent = null),
      ),
      AsyncData(:final value) => IdentifierForm(
        options: value,
        types: [widget.type],
        initial: _lastRequest,
        onRequest: contacts.startVerification,
        onSent: (value) => setState(() {
          _lastRequest = value.request;
          _sent = value;
        }),
      ),
      AsyncError(:final error) => MessageBanner(
        Problem.from(error).message(l10n),
      ),
      _ => const Center(
        child: Padding(
          padding: EdgeInsets.all(24),
          child: CircularProgressIndicator(),
        ),
      ),
    };

    return Padding(
      padding: EdgeInsets.only(
        left: 16,
        right: 16,
        bottom: 16 + MediaQuery.viewInsetsOf(context).bottom,
      ),
      child: SingleChildScrollView(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Text(_title(l10n), style: Theme.of(context).textTheme.titleLarge),
            const SizedBox(height: 16),
            content,
            const SizedBox(height: 8),
            TextButton(
              onPressed: () => Navigator.of(context).pop(false),
              child: Text(l10n.cancel),
            ),
          ],
        ),
      ),
    );
  }
}
