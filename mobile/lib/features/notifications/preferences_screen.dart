import 'package:built_collection/built_collection.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../core/api/api_error.dart';
import '../../core/errors/problem.dart';
import '../../core/push/device_registration.dart';
import '../../core/push/push_service.dart';
import '../../core/widgets/message_banner.dart';
import '../../l10n/app_localizations.dart';
import 'notifications_controller.dart';

String notificationCategoryLabel(
  AppLocalizations l10n,
  NotificationCategory category,
) => switch (category) {
  NotificationCategory.PAYMENT => l10n.notificationCategoryPayment,
  NotificationCategory.POLICY => l10n.notificationCategoryPolicy,
  NotificationCategory.LEGAL => l10n.notificationCategoryLegal,
  NotificationCategory.PROPOSAL => l10n.notificationCategoryProposal,
  NotificationCategory.QUOTE => l10n.notificationCategoryQuote,
  _ => category.name,
};

String notificationChannelLabel(
  AppLocalizations l10n,
  NotificationChannel channel,
) => switch (channel) {
  NotificationChannel.SMS => l10n.channelSms,
  NotificationChannel.WHATSAPP => l10n.channelWhatsapp,
  NotificationChannel.EMAIL => l10n.channelEmail,
  NotificationChannel.PUSH => l10n.channelPush,
  _ => channel.name,
};

typedef _Choices = Map<NotificationCategory, Map<NotificationChannel, bool>>;

_Choices _choicesOf(NotificationPreferencesResponse response) => {
  for (final category in response.categories)
    category.category: {
      for (final channel in category.channels) channel.channel: channel.enabled,
    },
};

/// `/notifications/preferences` (S6-07 §5): um interruptor por canal em cada
/// categoria. Numa categoria obrigatória, `SMS`, `WHATSAPP` e `EMAIL` não podem
/// ficar todos desligados ("Guardar" desactivado).
class PreferencesScreen extends ConsumerWidget {
  const PreferencesScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final l10n = AppLocalizations.of(context);
    final state = ref.watch(preferencesProvider);
    final value = state.value;
    if (value != null) {
      return _PreferencesForm(key: ValueKey(value), preferences: value);
    }
    final Widget body;
    if (state.hasError) {
      body = ListView(
        padding: const EdgeInsets.all(16),
        children: [
          MessageBanner(Problem.from(state.error!).message(l10n)),
          const SizedBox(height: 16),
          OutlinedButton(
            style: OutlinedButton.styleFrom(minimumSize: const Size(64, 48)),
            onPressed: () => ref.invalidate(preferencesProvider),
            child: Text(l10n.retry),
          ),
        ],
      );
    } else {
      body = const Center(child: CircularProgressIndicator());
    }
    return Scaffold(
      appBar: AppBar(title: Text(l10n.preferencesTitle)),
      body: SafeArea(child: body),
    );
  }
}

class _PreferencesForm extends ConsumerStatefulWidget {
  const _PreferencesForm({super.key, required this.preferences});

  final NotificationPreferencesResponse preferences;

  @override
  ConsumerState<_PreferencesForm> createState() => _PreferencesFormState();
}

class _PreferencesFormState extends ConsumerState<_PreferencesForm> {
  late final _Choices _initial = _choicesOf(widget.preferences);
  late final _Choices _choices = {
    for (final entry in _initial.entries) entry.key: {...entry.value},
  };

  /// Erros do servidor por categoria (`categories[i].channels`).
  final Map<NotificationCategory, String> _serverErrors = {};
  bool _saving = false;

  bool _changed(NotificationCategory category) {
    final before = _initial[category] ?? const {};
    final after = _choices[category] ?? const {};
    return after.entries.any((entry) => before[entry.key] != entry.value);
  }

  bool get _dirty => _choices.keys.any(_changed);

  bool get _invalid => widget.preferences.categories.any(
    (category) =>
        mandatoryCategoryInvalid(category, _choices[category.category]!),
  );

  Future<void> _save() async {
    final l10n = AppLocalizations.of(context);
    final changed = [
      for (final category in widget.preferences.categories)
        if (_changed(category.category)) category.category,
    ];
    setState(() {
      _saving = true;
      _serverErrors.clear();
    });
    try {
      await ref.read(preferencesProvider.notifier).save([
        for (final category in changed)
          NotificationCategoryPreferencesChange(
            (b) => b
              ..category = category
              ..channels = ListBuilder([
                for (final entry in _choices[category]!.entries)
                  NotificationChannelPreferenceChange(
                    (c) => c
                      ..channel = entry.key
                      ..enabled = entry.value,
                  ),
              ]),
          ),
      ]);
      // O formulário é recriado com a resposta.
      if (!mounted) return;
      ScaffoldMessenger.of(context)
        ..hideCurrentSnackBar()
        ..showSnackBar(SnackBar(content: Text(l10n.preferencesSaved)));
    } on Object catch (error) {
      if (!mounted) return;
      final problem = Problem.from(error);
      final fieldErrors = <NotificationCategory, String>{};
      for (final fieldError in problem.errors) {
        final match = RegExp(
          r'^categories\[(\d+)\]',
        ).firstMatch(fieldError.field);
        final index = int.tryParse(match?.group(1) ?? '');
        if (index != null && index < changed.length) {
          fieldErrors[changed[index]] = fieldErrorMessage(
            l10n,
            fieldError.code,
          );
        }
      }
      setState(() {
        _saving = false;
        _serverErrors.addAll(fieldErrors);
      });
      if (fieldErrors.isEmpty) {
        ScaffoldMessenger.of(context)
          ..hideCurrentSnackBar()
          ..showSnackBar(SnackBar(content: Text(problem.message(l10n))));
      }
    }
  }

  Future<void> _enablePush() async {
    final push = ref.read(pushServiceProvider);
    if (await push.requestPermission()) {
      await ref.read(deviceRegistrationProvider).register();
    }
    await ref.read(preferencesProvider.notifier).reload();
  }

  Future<bool> _confirmDiscard() async {
    final l10n = AppLocalizations.of(context);
    final leave = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: Text(l10n.preferencesDiscardTitle),
        content: Text(l10n.preferencesDiscardText),
        actions: [
          TextButton(
            onPressed: () => Navigator.of(context).pop(false),
            child: Text(l10n.cancel),
          ),
          TextButton(
            key: const Key('preferences-discard'),
            onPressed: () => Navigator.of(context).pop(true),
            child: Text(l10n.preferencesDiscard),
          ),
        ],
      ),
    );
    return leave ?? false;
  }

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final pushSupported = ref.watch(pushServiceProvider).supported;
    return PopScope(
      canPop: !_dirty,
      onPopInvokedWithResult: (didPop, _) async {
        if (didPop) return;
        if (await _confirmDiscard() && context.mounted) {
          Navigator.of(context).pop();
        }
      },
      child: Scaffold(
        appBar: AppBar(title: Text(l10n.preferencesTitle)),
        body: SafeArea(
          child: ListView(
            padding: const EdgeInsets.all(16),
            children: [
              for (final category in widget.preferences.categories)
                _CategorySection(
                  category: category,
                  choices: _choices[category.category]!,
                  serverError: _serverErrors[category.category],
                  pushSupported: pushSupported,
                  onChanged: (channel, enabled) => setState(() {
                    _choices[category.category]![channel] = enabled;
                    _serverErrors.remove(category.category);
                  }),
                  onEnablePush: _enablePush,
                ),
            ],
          ),
        ),
        // "Guardar" fica sempre à vista, por baixo da lista.
        bottomNavigationBar: SafeArea(
          child: Padding(
            padding: const EdgeInsets.fromLTRB(16, 8, 16, 16),
            child: FilledButton(
              key: const Key('preferences-save'),
              style: FilledButton.styleFrom(minimumSize: const Size(64, 48)),
              onPressed: _saving || !_dirty || _invalid ? null : _save,
              child: Text(l10n.save),
            ),
          ),
        ),
      ),
    );
  }
}

class _CategorySection extends StatelessWidget {
  const _CategorySection({
    required this.category,
    required this.choices,
    required this.serverError,
    required this.pushSupported,
    required this.onChanged,
    required this.onEnablePush,
  });

  final NotificationCategoryPreferences category;
  final Map<NotificationChannel, bool> choices;
  final String? serverError;
  final bool pushSupported;
  final void Function(NotificationChannel channel, bool enabled) onChanged;
  final VoidCallback onEnablePush;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    final invalid = mandatoryCategoryInvalid(category, choices);
    final error = invalid
        ? l10n.validationMandatoryCategoryNeedsChannel
        : serverError;
    return Card(
      key: Key('preferences-${category.category.name}'),
      margin: const EdgeInsets.only(bottom: 12),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Row(
              children: [
                Expanded(
                  child: Text(
                    notificationCategoryLabel(l10n, category.category),
                    style: theme.textTheme.titleLarge,
                  ),
                ),
                if (category.mandatory)
                  Chip(label: Text(l10n.preferencesMandatory)),
              ],
            ),
            if (category.mandatory)
              Text(
                l10n.preferencesMandatoryText,
                style: theme.textTheme.bodyMedium?.copyWith(
                  color: theme.colorScheme.onSurfaceVariant,
                ),
              ),
            for (final channel in category.channels)
              _ChannelSwitch(
                category: category.category,
                channel: channel,
                enabled: choices[channel.channel] ?? false,
                pushSupported: pushSupported,
                onChanged: (value) => onChanged(channel.channel, value),
                onEnablePush: onEnablePush,
              ),
            if (error != null) ...[
              const SizedBox(height: 8),
              MessageBanner(
                error,
                key: Key('preferences-error-${category.category.name}'),
              ),
            ],
          ],
        ),
      ),
    );
  }
}

class _ChannelSwitch extends StatelessWidget {
  const _ChannelSwitch({
    required this.category,
    required this.channel,
    required this.enabled,
    required this.pushSupported,
    required this.onChanged,
    required this.onEnablePush,
  });

  final NotificationCategory category;
  final NotificationChannelPreference channel;
  final bool enabled;
  final bool pushSupported;
  final ValueChanged<bool> onChanged;
  final VoidCallback onEnablePush;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final available = channel.available;
    final toggle = SwitchListTile(
      key: Key('preferences-${category.name}-${channel.channel.name}'),
      contentPadding: EdgeInsets.zero,
      title: Text(notificationChannelLabel(l10n, channel.channel)),
      value: enabled,
      onChanged: available ? onChanged : null,
    );
    if (available) return toggle;
    // Fora do `SwitchListTile` (desactivado, não deixa tocar nos botões).
    final isPush = channel.channel == NotificationChannel.PUSH;
    final text = switch (channel.channel) {
      NotificationChannel.EMAIL => l10n.preferencesNeedsEmail,
      NotificationChannel.PUSH => l10n.preferencesPushUnavailable,
      _ => l10n.preferencesNeedsPhone,
    };
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        toggle,
        Text(
          text,
          style: Theme.of(context).textTheme.bodyMedium?.copyWith(
            color: Theme.of(context).colorScheme.onSurfaceVariant,
          ),
        ),
        if (!isPush)
          TextButton(
            style: TextButton.styleFrom(
              minimumSize: const Size(48, 48),
              padding: EdgeInsets.zero,
            ),
            onPressed: () => context.go('/profile'),
            child: Text(l10n.preferencesAddInProfile),
          )
        else if (pushSupported)
          TextButton(
            key: Key('preferences-enable-push-${category.name}'),
            style: TextButton.styleFrom(
              minimumSize: const Size(48, 48),
              padding: EdgeInsets.zero,
            ),
            onPressed: onEnablePush,
            child: Text(l10n.pushPromptEnable),
          ),
      ],
    );
  }
}
