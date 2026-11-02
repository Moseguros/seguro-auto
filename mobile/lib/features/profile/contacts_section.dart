import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../core/errors/problem.dart';
import '../../core/theme.dart';
import '../../core/widgets/message_banner.dart';
import '../../l10n/app_localizations.dart';
import '../auth/otp.dart';
import 'contact_sheet.dart';
import 'profile_controller.dart';

const _contactTypes = [OtpCodes.msisdn, OtpCodes.email];

/// Secção "Contactos" do perfil: telemóvel e e-mail verificados.
class ContactsSection extends ConsumerStatefulWidget {
  const ContactsSection({super.key});

  @override
  ConsumerState<ContactsSection> createState() => _ContactsSectionState();
}

class _ContactsSectionState extends ConsumerState<ContactsSection> {
  String? _message;
  bool _busy = false;

  ContactResponse? _byType(ContactsResponse data, String type) {
    for (final contact in data.contacts) {
      if (contact.type.name == type) return contact;
    }
    return null;
  }

  Future<void> _edit(String type, {required bool replacing}) async {
    setState(() => _message = null);
    final confirmed = await showContactSheet(
      context,
      type: type,
      replacing: replacing,
    );
    if (confirmed == true && mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(AppLocalizations.of(context).contactsSaved)),
      );
    }
  }

  Future<void> _remove(ContactResponse contact) async {
    final l10n = AppLocalizations.of(context);
    setState(() => _message = null);
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: Text(l10n.contactsConfirmRemoveTitle),
        content: Text(l10n.contactsConfirmRemoveText(contact.value)),
        actions: [
          TextButton(
            onPressed: () => Navigator.of(context).pop(false),
            child: Text(l10n.cancel),
          ),
          TextButton(
            key: const Key('confirm-remove-contact'),
            style: TextButton.styleFrom(
              foregroundColor: Theme.of(context).colorScheme.error,
            ),
            onPressed: () => Navigator.of(context).pop(true),
            child: Text(l10n.contactsConfirmRemove),
          ),
        ],
      ),
    );
    if (confirmed != true || !mounted) return;
    setState(() => _busy = true);
    try {
      await ref
          .read(contactsControllerProvider.notifier)
          .remove(contact.type.name);
      if (mounted) {
        ScaffoldMessenger.of(
          context,
        ).showSnackBar(SnackBar(content: Text(l10n.contactsRemoved)));
      }
    } catch (error) {
      if (mounted) setState(() => _message = Problem.from(error).message(l10n));
    } finally {
      if (mounted) setState(() => _busy = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    final contacts = ref.watch(contactsControllerProvider);

    final Widget body = switch (contacts) {
      AsyncData(:final value) => Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          for (final type in _contactTypes)
            _ContactRow(
              type: type,
              contact: _byType(value, type),
              busy: _busy,
              onAdd: () => _edit(type, replacing: false),
              onChange: () => _edit(type, replacing: true),
              onRemove: (contact) => _remove(contact),
            ),
          if (_message != null) ...[
            const SizedBox(height: 8),
            MessageBanner(_message!, key: const Key('contacts-error')),
          ],
        ],
      ),
      AsyncError(:final error) => MessageBanner(
        Problem.from(error).message(l10n),
      ),
      _ => const Center(child: CircularProgressIndicator()),
    };

    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Text(l10n.contactsTitle, style: theme.textTheme.titleLarge),
            const SizedBox(height: 8),
            body,
          ],
        ),
      ),
    );
  }
}

class _ContactRow extends StatelessWidget {
  const _ContactRow({
    required this.type,
    required this.contact,
    required this.busy,
    required this.onAdd,
    required this.onChange,
    required this.onRemove,
  });

  final String type;
  final ContactResponse? contact;
  final bool busy;
  final VoidCallback onAdd;
  final VoidCallback onChange;
  final ValueChanged<ContactResponse> onRemove;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    final msisdn = type == OtpCodes.msisdn;
    final icon = msisdn ? Icons.smartphone : Icons.email_outlined;
    final contact = this.contact;

    if (contact == null) {
      return Padding(
        padding: const EdgeInsets.symmetric(vertical: 8),
        child: Align(
          alignment: AlignmentDirectional.centerStart,
          child: OutlinedButton.icon(
            key: Key('add-contact-$type'),
            onPressed: busy ? null : onAdd,
            icon: Icon(icon),
            label: Text(
              msisdn ? l10n.contactsAddMsisdn : l10n.contactsAddEmail,
            ),
          ),
        ),
      );
    }

    final verifiedColor = theme.brightness == Brightness.dark
        ? BrandColors.successDark
        : BrandColors.success;
    return Padding(
      key: Key('contact-$type'),
      padding: const EdgeInsets.symmetric(vertical: 8),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Icon(icon, color: theme.colorScheme.primary),
              const SizedBox(width: 12),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      msisdn ? l10n.contactTypeMsisdn : l10n.contactTypeEmail,
                      style: theme.textTheme.bodySmall?.copyWith(
                        color: theme.colorScheme.onSurfaceVariant,
                      ),
                    ),
                    Text(contact.value, style: theme.textTheme.bodyLarge),
                  ],
                ),
              ),
              if (contact.verified)
                Chip(
                  visualDensity: VisualDensity.compact,
                  label: Text(l10n.contactsVerified),
                  labelStyle: TextStyle(color: verifiedColor),
                  side: BorderSide(color: verifiedColor),
                  backgroundColor: Colors.transparent,
                ),
            ],
          ),
          Wrap(
            spacing: 8,
            children: [
              Semantics(
                label: msisdn
                    ? l10n.contactsChangeMsisdn
                    : l10n.contactsChangeEmail,
                button: true,
                excludeSemantics: true,
                child: TextButton(
                  key: Key('change-contact-$type'),
                  onPressed: busy ? null : onChange,
                  child: Text(l10n.contactsChangeShort),
                ),
              ),
              Semantics(
                label: msisdn
                    ? l10n.contactsRemoveMsisdn
                    : l10n.contactsRemoveEmail,
                button: true,
                excludeSemantics: true,
                child: TextButton(
                  key: Key('remove-contact-$type'),
                  style: TextButton.styleFrom(
                    foregroundColor: theme.colorScheme.error,
                  ),
                  onPressed: busy ? null : () => onRemove(contact),
                  child: Text(l10n.contactsRemoveShort),
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }
}
