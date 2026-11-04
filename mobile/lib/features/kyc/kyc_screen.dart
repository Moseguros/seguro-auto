import 'dart:typed_data';

import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../core/errors/problem.dart';
import '../../core/widgets/message_banner.dart';
import '../../l10n/app_localizations.dart';
import 'kyc_capture_screen.dart';
import 'kyc_controller.dart';

/// `/identity`: verificação de identidade fora de uma proposta (S4-08 §3). A
/// seguradora é a da proposta editável ou em análise mais recente; sem nenhuma,
/// só o estado e "A verificação de identidade é pedida quando faz uma proposta."
class KycScreen extends ConsumerWidget {
  const KycScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final l10n = AppLocalizations.of(context);
    final kyc = ref.watch(myKycProvider);
    final insurer = ref.watch(kycInsurerProvider);
    final docType = ref.watch(kycIdDocTypeProvider);

    Widget body;
    final error = kyc.error ?? insurer.error;
    if (kyc.hasValue && insurer.hasValue && !docType.isLoading) {
      body = ListView(
        padding: const EdgeInsets.all(16),
        children: [
          KycForm(
            state: kyc.value!.state,
            check: kyc.value!.check,
            insurerId: insurer.value,
            idDocType: docType.value,
            onSubmitted: () {
              ref.invalidate(myKycProvider);
            },
          ),
        ],
      );
    } else if (error != null) {
      body = ListView(
        padding: const EdgeInsets.all(16),
        children: [
          MessageBanner(Problem.from(error).message(l10n)),
          const SizedBox(height: 16),
          OutlinedButton(
            onPressed: () {
              ref.invalidate(myKycProvider);
              ref.invalidate(kycInsurerProvider);
            },
            child: Text(l10n.retry),
          ),
        ],
      );
    } else {
      body = const Center(child: CircularProgressIndicator());
    }
    return Scaffold(
      appBar: AppBar(title: Text(l10n.kycTitle)),
      body: SafeArea(child: body),
    );
  }
}

/// Estado do KYC e, quando se pode enviar, o formulário com a câmara
/// (frente e verso do documento, selfie). Usado em `/identity` e no passo
/// "Identidade" da proposta.
class KycForm extends ConsumerStatefulWidget {
  const KycForm({
    super.key,
    required this.state,
    required this.insurerId,
    required this.idDocType,
    required this.onSubmitted,
    this.check,
    this.requiredForAutomaticAcceptance,
    this.onNotNow,
  });

  final KycState state;
  final KycCheckResponse? check;

  /// Seguradora a quem se envia; `null` = sem proposta (só o estado).
  final String? insurerId;

  /// Tipo de documento do perfil (sem verso para `PASSPORT`).
  final String? idDocType;

  /// `ProposalResponse.kyc.requiredForAutomaticAcceptance` (só nas propostas).
  final bool? requiredForAutomaticAcceptance;

  final VoidCallback onSubmitted;

  /// "Agora não" (só nas propostas).
  final VoidCallback? onNotNow;

  @override
  ConsumerState<KycForm> createState() => _KycFormState();
}

class _KycFormState extends ConsumerState<KycForm> {
  Uint8List? _front;
  Uint8List? _back;
  Uint8List? _selfie;
  bool _busy = false;
  String? _error;

  bool get _needsBack => kycNeedsBack(widget.idDocType);

  KycPhotos get _photos => KycPhotos(
    docFront: _front,
    docBack: _needsBack ? _back : null,
    selfie: _selfie,
  );

  Future<void> _capture(KycShot shot) async {
    final photo = await captureKycPhoto(context, shot);
    if (photo == null || !mounted) return;
    setState(() {
      _error = null;
      switch (shot) {
        case KycShot.docFront:
          _front = photo;
        case KycShot.docBack:
          _back = photo;
        case KycShot.selfie:
          _selfie = photo;
      }
    });
  }

  Future<void> _submit() async {
    final l10n = AppLocalizations.of(context);
    final insurerId = widget.insurerId;
    if (insurerId == null) return;
    if (!_photos.isComplete(needsBack: _needsBack)) {
      setState(() => _error = l10n.kycPhotosMissing);
      return;
    }
    setState(() {
      _busy = true;
      _error = null;
    });
    try {
      await ref
          .read(kycSubmitterProvider)
          .submit(insurerId: insurerId, photos: _photos);
      if (!mounted) return;
      setState(() {
        _front = null;
        _back = null;
        _selfie = null;
      });
      widget.onSubmitted();
    } on NoConnectionException {
      // As imagens ficam no ecrã até sair.
      if (mounted) setState(() => _error = l10n.errorNoConnection);
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
    final state = widget.state;
    final canSend =
        widget.insurerId != null &&
        (state == KycState.NONE ||
            state == KycState.REJECTED ||
            state == KycState.OUTDATED);
    final required = widget.requiredForAutomaticAcceptance;
    final error = _error;

    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: [
        switch (state) {
          KycState.VERIFIED => MessageBanner(
            l10n.kycStateVerified,
            key: const Key('kyc-state'),
            kind: MessageKind.success,
          ),
          KycState.PENDING => MessageBanner(
            l10n.kycStatePending,
            key: const Key('kyc-state'),
            kind: MessageKind.info,
          ),
          KycState.REJECTED => MessageBanner(
            '${l10n.kycStateRejected} ${kycRejectionText(l10n, widget.check)}',
            key: const Key('kyc-state'),
          ),
          KycState.OUTDATED => MessageBanner(
            l10n.kycStateOutdated,
            key: const Key('kyc-state'),
            kind: MessageKind.info,
          ),
          _ => const SizedBox.shrink(),
        },
        if (state != KycState.NONE) const SizedBox(height: 16),
        if (widget.insurerId == null)
          Text(l10n.kycNoProposal, key: const Key('kyc-no-proposal')),
        if (canSend) ...[
          if (required != null) ...[
            Text(
              required ? l10n.kycRequiredForAutoAcceptance : l10n.kycOptional,
              key: const Key('kyc-why'),
              style: theme.textTheme.bodyMedium,
            ),
            const SizedBox(height: 16),
          ],
          _ShotTile(
            key: const Key('kyc-doc-front'),
            label: l10n.kycDocFront,
            photo: _front,
            busy: _busy,
            onCapture: () => _capture(KycShot.docFront),
          ),
          if (_needsBack)
            _ShotTile(
              key: const Key('kyc-doc-back'),
              label: l10n.kycDocBack,
              photo: _back,
              busy: _busy,
              onCapture: () => _capture(KycShot.docBack),
            ),
          _ShotTile(
            key: const Key('kyc-selfie'),
            label: l10n.kycSelfie,
            photo: _selfie,
            busy: _busy,
            onCapture: () => _capture(KycShot.selfie),
          ),
          if (error != null) ...[
            const SizedBox(height: 8),
            MessageBanner(error, key: const Key('kyc-error')),
          ],
          const SizedBox(height: 16),
          FilledButton(
            key: const Key('kyc-submit'),
            style: FilledButton.styleFrom(minimumSize: const Size(64, 48)),
            onPressed: _busy ? null : _submit,
            child: _busy
                ? const SizedBox.square(
                    dimension: 20,
                    child: CircularProgressIndicator(strokeWidth: 2),
                  )
                : Text(l10n.kycSubmit),
          ),
        ],
        if (widget.onNotNow != null && canSend) ...[
          const SizedBox(height: 8),
          TextButton(
            key: const Key('kyc-not-now'),
            style: TextButton.styleFrom(minimumSize: const Size(48, 48)),
            onPressed: _busy ? null : widget.onNotNow,
            child: Text(l10n.kycNotNow),
          ),
        ],
      ],
    );
  }
}

class _ShotTile extends StatelessWidget {
  const _ShotTile({
    super.key,
    required this.label,
    required this.photo,
    required this.busy,
    required this.onCapture,
  });

  final String label;
  final Uint8List? photo;
  final bool busy;
  final VoidCallback onCapture;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final scheme = Theme.of(context).colorScheme;
    final photo = this.photo;
    return Card(
      margin: const EdgeInsets.only(bottom: 8),
      child: Padding(
        padding: const EdgeInsets.all(12),
        child: Row(
          children: [
            ClipRRect(
              borderRadius: BorderRadius.circular(8),
              child: SizedBox.square(
                dimension: 56,
                child: ColoredBox(
                  color: scheme.surfaceContainerHighest,
                  child: photo == null
                      ? Icon(
                          Icons.badge_outlined,
                          color: scheme.onSurfaceVariant,
                        )
                      : Image.memory(photo, fit: BoxFit.cover, cacheWidth: 112),
                ),
              ),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(label),
                  TextButton(
                    style: TextButton.styleFrom(
                      minimumSize: const Size(48, 48),
                      padding: EdgeInsets.zero,
                    ),
                    onPressed: busy ? null : onCapture,
                    child: Text(
                      photo == null ? l10n.kycTakePhoto : l10n.kycRetakePhoto,
                    ),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}
