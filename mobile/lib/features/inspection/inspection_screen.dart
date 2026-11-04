import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../core/api/api_error.dart';
import '../../core/db/app_database.dart';
import '../../core/errors/problem.dart';
import '../../core/files/file_download.dart';
import '../../core/location/location_service.dart';
import '../../core/network/connectivity.dart';
import '../../core/widgets/message_banner.dart';
import '../../l10n/app_localizations.dart';
import '../onboarding/onboarding_controller.dart' show sharedPreferencesProvider;
import '../proposals/proposal_controller.dart';
import 'guided_camera_screen.dart';
import 'inspection_repository.dart';
import 'inspection_slots.dart';
import 'on_site_sheet.dart';
import 'upload_queue_runner.dart';

/// Estado local de um ângulo (S4-09 §3).
enum SlotState { toTake, queued, uploading, uploaded, failed }

/// Um ângulo fixo: o estado e de onde vem a miniatura.
class SlotView {
  const SlotView({
    required this.slot,
    required this.state,
    this.local,
    this.remote,
    this.retake = false,
    this.missing = false,
  });

  final String slot;
  final SlotState state;

  /// Fotografia na fila (a mais recente do ângulo).
  final InspectionUpload? local;

  /// Fotografia já no servidor.
  final InspectionPhotoResponse? remote;

  /// A seguradora pediu para a tirar de novo e ainda não há fotografia nova.
  final bool retake;

  /// O servidor diz que falta (`missingSlots`).
  final bool missing;
}

SlotState _stateOf(InspectionUpload upload) => switch (upload.status) {
  UploadStatus.queued => SlotState.queued,
  UploadStatus.uploading => SlotState.uploading,
  UploadStatus.done => SlotState.uploaded,
  _ => SlotState.failed,
};

/// Estado dos 6 ângulos obrigatórios a partir da vistoria do servidor e da fila.
List<SlotView> slotViews(
  InspectionResponse inspection,
  List<InspectionUpload> uploads,
) {
  final review = inspection.review;
  final rejected = inspection.status == InspectionStatus.REJECTED;
  return [
    for (final slot in requiredSlots)
      () {
        final local = uploads.where((u) => u.slot == slot).lastOrNull;
        final remote = (inspection.photos?.toList() ?? const <InspectionPhotoResponse>[])
            .where((photo) => photo.slot.name == slot)
            .lastOrNull;
        final retakeAsked =
            rejected && (review?.retakeSlots.any((s) => s.name == slot) ?? false);
        final newerLocal =
            local != null &&
            (review == null || local.createdAt.isAfter(review.reviewedAt));
        final retake = retakeAsked && !newerLocal;
        final missing =
            inspection.missingSlots?.any((s) => s.name == slot) ?? false;
        final SlotState state;
        if (local != null && (!retakeAsked || newerLocal)) {
          state = _stateOf(local);
        } else if (remote != null && !retake) {
          state = SlotState.uploaded;
        } else {
          state = SlotState.toTake;
        }
        return SlotView(
          slot: slot,
          state: state,
          local: local,
          remote: remote,
          retake: retake,
          missing: missing && state == SlotState.toTake,
        );
      }(),
  ];
}

/// "Enviar vistoria" activo: os 6 ângulos "Na fila", "A enviar" ou "Enviada" e
/// quilometragem válida.
bool canSubmitInspection(List<SlotView> views, int? odometerKm) =>
    odometerKm != null &&
    odometerKm >= 0 &&
    odometerKm <= maxOdometerKm &&
    views.every(
      (view) =>
          view.state == SlotState.queued ||
          view.state == SlotState.uploading ||
          view.state == SlotState.uploaded,
    );

const maxOdometerKm = 2000000;

/// Lê a quilometragem escrita (só dígitos); `null` se inválida.
int? parseOdometer(String text) {
  final digits = text.replaceAll(RegExp(r'\s'), '');
  if (digits.isEmpty || !RegExp(r'^\d+$').hasMatch(digits)) return null;
  final value = int.tryParse(digits);
  if (value == null || value > maxOdometerKm) return null;
  return value;
}

/// A vistoria aceita fotografias novas.
bool inspectionEditable(ProposalResponse proposal) {
  final inspection = proposal.inspection;
  return isEditable(proposal.status) &&
      inspection.mode != InspectionMode.ON_SITE &&
      (inspection.status == InspectionStatus.PENDING ||
          inspection.status == InspectionStatus.REJECTED ||
          inspection.status == null);
}

/// Mensagem de um código guardado na fila (topo ou campo).
String queueErrorMessage(AppLocalizations l10n, String? code) {
  if (code == null) return l10n.errorUnknown;
  final top = apiErrorMessage(l10n, code);
  if (top != l10n.errorUnknown) return top;
  final field = fieldErrorMessage(l10n, code);
  return field == code ? l10n.errorUnknown : field;
}

/// Chave em `shared_preferences`: a explicação da localização já foi mostrada.
const locationRationaleKey = 'location_rationale_shown';

/// `/proposals/:id/inspection`: vistoria guiada com fila offline (S4-09 §3).
class InspectionScreen extends ConsumerStatefulWidget {
  const InspectionScreen({super.key, required this.proposalId});

  final String proposalId;

  @override
  ConsumerState<InspectionScreen> createState() => _InspectionScreenState();
}

class _InspectionScreenState extends ConsumerState<InspectionScreen> {
  final _odometer = TextEditingController();
  bool _odometerTouched = false;
  int _doneSeen = -1;
  String? _submissionSeen;

  String get _id => widget.proposalId;

  @override
  void initState() {
    super.initState();
    _odometer.addListener(() => setState(() {}));
  }

  @override
  void dispose() {
    _odometer.dispose();
    super.dispose();
  }

  Future<void> _reload() =>
      ref.read(proposalProvider(_id).notifier).reload();

  /// Pede a localização uma vez, com a explicação (opcional para o cliente).
  Future<void> _askLocationOnce() async {
    final preferences = ref.read(sharedPreferencesProvider);
    if (preferences.getBool(locationRationaleKey) ?? false) return;
    final l10n = AppLocalizations.of(context);
    await showDialog<void>(
      context: context,
      builder: (context) => AlertDialog(
        content: Text(l10n.locationRationale, key: const Key('location-rationale')),
        actions: [
          TextButton(
            key: const Key('location-continue'),
            onPressed: () => Navigator.of(context).pop(),
            child: Text(l10n.locationContinue),
          ),
        ],
      ),
    );
    await preferences.setBool(locationRationaleKey, true);
    await ref.read(locationServiceProvider).requestPermission();
  }

  Future<void> _openCamera(String slot, {List<String> then = const []}) async {
    await _askLocationOnce();
    if (!mounted) return;
    await context.push<bool>(guidedCameraPath(_id, slot, then: then));
  }

  Future<void> _start(List<SlotView> views) async {
    final toTake = [
      for (final view in views)
        if (view.state == SlotState.toTake || view.state == SlotState.failed)
          view.slot,
    ];
    if (toTake.isEmpty) return;
    await _openCamera(toTake.first, then: toTake.sublist(1));
  }

  Future<void> _submit() async {
    final km = parseOdometer(_odometer.text);
    if (km == null) return;
    await ref.read(inspectionRepositoryProvider).queueSubmission(_id, km);
    await ref.read(uploadQueueRunnerProvider).wake();
  }

  Future<void> _onSite(ProposalResponse proposal) async {
    final l10n = AppLocalizations.of(context);
    final messenger = ScaffoldMessenger.of(context);
    if (!await ref.read(connectivityMonitorProvider).isOnline()) {
      messenger
        ..hideCurrentSnackBar()
        ..showSnackBar(SnackBar(content: Text(l10n.errorNoConnection)));
      return;
    }
    if (!mounted) return;
    final updated = await showOnSiteSheet(context, proposal);
    if (updated != null) {
      ref.read(proposalProvider(_id).notifier).replace(updated);
    }
  }

  /// Quando a fila envia uma fotografia ou a submissão, a proposta é relida.
  void _watchQueue(
    List<InspectionUpload> uploads,
    InspectionSubmission? submission,
  ) {
    final done = uploads.where((u) => u.status == UploadStatus.done).length;
    final submissionState = submission == null
        ? null
        : '${submission.status}:${submission.lastErrorCode}';
    final changed =
        (_doneSeen >= 0 && done != _doneSeen) ||
        (_submissionSeen != null && submissionState != _submissionSeen);
    _doneSeen = done;
    _submissionSeen = submissionState;
    if (changed) {
      WidgetsBinding.instance.addPostFrameCallback((_) {
        if (mounted) _reload();
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final proposal = ref.watch(proposalProvider(_id));
    final uploads = ref.watch(inspectionUploadsProvider(_id)).value ?? const [];
    final submission = ref.watch(inspectionSubmissionProvider(_id)).value;
    final online = ref.watch(onlineProvider).value ?? true;
    _watchQueue(uploads, submission);

    // As fotografias já mostradas pelo servidor saem da fila.
    ref.listen(proposalProvider(_id), (_, next) {
      final photos = next.value?.inspection.photos;
      if (photos == null) return;
      ref
          .read(inspectionRepositoryProvider)
          .forgetUploaded(_id, {for (final photo in photos) photo.id});
    });

    final value = proposal.value;
    Widget body;
    if (value != null) {
      body = _body(context, value, uploads, submission, online);
    } else if (proposal.hasError) {
      body = ListView(
        padding: const EdgeInsets.all(16),
        children: [
          MessageBanner(Problem.from(proposal.error!).message(l10n)),
          const SizedBox(height: 16),
          OutlinedButton(onPressed: _reload, child: Text(l10n.retry)),
        ],
      );
    } else {
      body = const Center(child: CircularProgressIndicator());
    }
    return Scaffold(
      appBar: AppBar(title: Text(l10n.inspectionTitle)),
      body: SafeArea(
        child: RefreshIndicator(onRefresh: _reload, child: body),
      ),
    );
  }

  Widget _body(
    BuildContext context,
    ProposalResponse proposal,
    List<InspectionUpload> uploads,
    InspectionSubmission? submission,
    bool online,
  ) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    final inspection = proposal.inspection;
    final views = slotViews(inspection, uploads);
    final editable = inspectionEditable(proposal);
    if (!_odometerTouched) {
      final km = submission?.odometerKm ?? inspection.odometerKm;
      if (km != null) _odometer.text = '$km';
      _odometerTouched = true;
    }
    final km = parseOdometer(_odometer.text);
    final queuedSubmission =
        submission != null && submission.status == SubmissionStatus.queued;
    final failedSubmission =
        submission != null && submission.status == SubmissionStatus.failed;
    final extrasLocal = uploads.where((u) => u.slot == extraSlot).toList();
    final extrasRemote = (inspection.photos?.toList() ?? const <InspectionPhotoResponse>[])
        .where((p) => p.slot == PhotoSlot.EXTRA)
        .toList();
    final maxExtras = inspection.maxExtraPhotos ?? 0;
    final extraCount =
        extrasRemote.length +
        extrasLocal.where((u) => u.status != UploadStatus.done).length;

    return ListView(
      padding: const EdgeInsets.all(16),
      children: [
        if (!online) ...[
          MessageBanner(
            l10n.inspectionOfflineBanner,
            key: const Key('inspection-offline'),
            kind: MessageKind.info,
          ),
          const SizedBox(height: 12),
        ],
        Text(l10n.inspectionIntro),
        const SizedBox(height: 12),
        if (inspection.status == InspectionStatus.REJECTED &&
            inspection.review != null) ...[
          MessageBanner(
            l10n.inspectionRejectedBanner(
              inspection.review!.rejectionReason ?? '',
            ),
            key: const Key('inspection-rejected'),
          ),
          const SizedBox(height: 12),
        ],
        if (inspection.mode == InspectionMode.ON_SITE) ...[
          MessageBanner(
            l10n.inspectionOnSiteRequested,
            key: const Key('inspection-on-site'),
            kind: MessageKind.info,
          ),
          const SizedBox(height: 12),
        ] else if (!editable) ...[
          MessageBanner(
            l10n.inspectionNotEditable,
            key: const Key('inspection-not-editable'),
            kind: MessageKind.success,
          ),
          const SizedBox(height: 12),
        ],
        for (final view in views)
          _SlotTile(
            view: view,
            enabled: editable,
            onTap: () => _openCamera(view.slot),
            onDiscard: view.local == null
                ? null
                : () => ref
                      .read(inspectionRepositoryProvider)
                      .discard(view.local!.clientPhotoId),
          ),
        const SizedBox(height: 8),
        if (editable)
          FilledButton.icon(
            key: const Key('inspection-start'),
            style: FilledButton.styleFrom(minimumSize: const Size(64, 48)),
            onPressed: views.any(
                  (v) =>
                      v.state == SlotState.toTake || v.state == SlotState.failed,
                )
                ? () => _start(views)
                : null,
            icon: const Icon(Icons.photo_camera_outlined),
            label: Text(l10n.inspectionStart),
          ),
        const SizedBox(height: 24),
        Text(l10n.inspectionExtras, style: theme.textTheme.titleMedium),
        const SizedBox(height: 8),
        for (final photo in extrasRemote)
          _ExtraTile(remote: photo),
        for (final upload in extrasLocal.where(
          (u) => u.status != UploadStatus.done,
        ))
          _ExtraTile(
            local: upload,
            onDiscard: () => ref
                .read(inspectionRepositoryProvider)
                .discard(upload.clientPhotoId),
          ),
        if (editable)
          Align(
            alignment: AlignmentDirectional.centerStart,
            child: OutlinedButton.icon(
              key: const Key('inspection-add-extra'),
              style: OutlinedButton.styleFrom(minimumSize: const Size(64, 48)),
              onPressed: extraCount < maxExtras
                  ? () => _openCamera(extraSlot)
                  : null,
              icon: const Icon(Icons.add_a_photo_outlined),
              label: Text(l10n.inspectionAddExtra),
            ),
          ),
        const SizedBox(height: 24),
        if (editable) ...[
          TextField(
            key: const Key('inspection-odometer'),
            controller: _odometer,
            keyboardType: TextInputType.number,
            inputFormatters: [FilteringTextInputFormatter.digitsOnly],
            decoration: InputDecoration(
              labelText: l10n.inspectionOdometer,
              suffixText: 'km',
              border: const OutlineInputBorder(),
              errorText: _odometer.text.isNotEmpty && km == null
                  ? l10n.inspectionOdometerInvalid
                  : null,
            ),
          ),
          const SizedBox(height: 16),
          if (failedSubmission) ...[
            MessageBanner(
              queueErrorMessage(l10n, submission.lastErrorCode),
              key: const Key('inspection-submit-error'),
            ),
            const SizedBox(height: 12),
          ],
          if (queuedSubmission) ...[
            MessageBanner(
              online
                  ? l10n.inspectionSubmitQueued
                  : l10n.inspectionWillSubmitWhenOnline,
              key: const Key('inspection-submit-queued'),
              kind: MessageKind.info,
            ),
            const SizedBox(height: 12),
          ],
          FilledButton(
            key: const Key('inspection-submit'),
            style: FilledButton.styleFrom(minimumSize: const Size(64, 48)),
            onPressed: !queuedSubmission && canSubmitInspection(views, km)
                ? _submit
                : null,
            child: Text(l10n.inspectionSubmit),
          ),
          const SizedBox(height: 8),
          Align(
            alignment: AlignmentDirectional.centerStart,
            child: TextButton(
              key: const Key('inspection-on-site-link'),
              style: TextButton.styleFrom(minimumSize: const Size(48, 48)),
              onPressed: () => _onSite(proposal),
              child: Text(l10n.inspectionOnSiteLink),
            ),
          ),
        ],
      ],
    );
  }
}

class _SlotTile extends StatelessWidget {
  const _SlotTile({
    required this.view,
    required this.enabled,
    required this.onTap,
    required this.onDiscard,
  });

  final SlotView view;
  final bool enabled;
  final VoidCallback onTap;
  final VoidCallback? onDiscard;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    final scheme = theme.colorScheme;
    final (icon, label, color) = switch (view.state) {
      SlotState.toTake => (
        Icons.photo_camera_outlined,
        view.retake ? l10n.inspectionRetakeMarker : l10n.inspectionToTake,
        view.retake || view.missing ? scheme.error : scheme.onSurfaceVariant,
      ),
      SlotState.queued => (
        Icons.schedule,
        l10n.inspectionQueued,
        scheme.primary,
      ),
      SlotState.uploading => (
        Icons.cloud_upload_outlined,
        l10n.inspectionUploading,
        scheme.primary,
      ),
      SlotState.uploaded => (
        Icons.check_circle,
        l10n.inspectionUploaded,
        Colors.green.shade700,
      ),
      SlotState.failed => (Icons.error_outline, l10n.inspectionFailed, scheme.error),
    };
    final failed = view.state == SlotState.failed;
    return Card(
      key: Key('slot-${view.slot}'),
      margin: const EdgeInsets.only(bottom: 8),
      child: InkWell(
        onTap: enabled && view.state != SlotState.uploading ? onTap : null,
        borderRadius: BorderRadius.circular(12),
        child: Padding(
          padding: const EdgeInsets.all(12),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              Row(
                children: [
                  _Thumbnail(local: view.local, remote: view.remote, view: view),
                  const SizedBox(width: 12),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          slotName(l10n, view.slot),
                          style: theme.textTheme.titleSmall,
                        ),
                        const SizedBox(height: 4),
                        Row(
                          children: [
                            Icon(icon, size: 18, color: color),
                            const SizedBox(width: 4),
                            Flexible(
                              child: Text(
                                label,
                                key: Key('slot-${view.slot}-state'),
                                style: theme.textTheme.bodySmall?.copyWith(
                                  color: color,
                                ),
                              ),
                            ),
                          ],
                        ),
                        if (view.state == SlotState.uploading)
                          const Padding(
                            padding: EdgeInsets.only(top: 4),
                            child: LinearProgressIndicator(),
                          ),
                      ],
                    ),
                  ),
                ],
              ),
              if (failed) ...[
                const SizedBox(height: 8),
                Text(
                  queueErrorMessage(l10n, view.local?.lastErrorCode),
                  key: Key('slot-${view.slot}-error'),
                  style: TextStyle(color: scheme.error),
                ),
                Wrap(
                  spacing: 8,
                  children: [
                    TextButton(
                      key: Key('slot-${view.slot}-retake'),
                      style: TextButton.styleFrom(
                        minimumSize: const Size(48, 48),
                      ),
                      onPressed: enabled ? onTap : null,
                      child: Text(l10n.inspectionTakeAgain),
                    ),
                    TextButton(
                      key: Key('slot-${view.slot}-discard'),
                      style: TextButton.styleFrom(
                        minimumSize: const Size(48, 48),
                      ),
                      onPressed: onDiscard,
                      child: Text(l10n.inspectionDiscard),
                    ),
                  ],
                ),
              ],
            ],
          ),
        ),
      ),
    );
  }
}

/// Miniatura: ficheiro local (fila) ou fotografia descarregada do servidor.
class _Thumbnail extends ConsumerWidget {
  const _Thumbnail({this.local, this.remote, this.view});

  final InspectionUpload? local;
  final InspectionPhotoResponse? remote;
  final SlotView? view;

  static const size = 64.0;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final scheme = Theme.of(context).colorScheme;
    Widget? image;
    final local = this.local;
    final remote = this.remote;
    final state = view?.state;
    if (local != null &&
        local.status != UploadStatus.done &&
        state != SlotState.toTake &&
        File(local.filePath).existsSync()) {
      image = Image.file(
        File(local.filePath),
        fit: BoxFit.cover,
        cacheWidth: 128,
        errorBuilder: (_, _, _) => const SizedBox.shrink(),
      );
    } else if (remote != null && state != SlotState.toTake) {
      final bytes = ref.watch(fileBytesProvider(remote.fileId)).value;
      if (bytes != null && bytes.isImage) {
        image = Image.memory(bytes.bytes, fit: BoxFit.cover, cacheWidth: 128);
      }
    }
    return ClipRRect(
      borderRadius: BorderRadius.circular(8),
      child: SizedBox.square(
        dimension: size,
        child: ColoredBox(
          color: scheme.surfaceContainerHighest,
          child:
              image ??
              Icon(Icons.directions_car_outlined, color: scheme.onSurfaceVariant),
        ),
      ),
    );
  }
}

class _ExtraTile extends StatelessWidget {
  const _ExtraTile({this.local, this.remote, this.onDiscard});

  final InspectionUpload? local;
  final InspectionPhotoResponse? remote;
  final VoidCallback? onDiscard;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final local = this.local;
    final state = local == null ? SlotState.uploaded : _stateOf(local);
    final label = switch (state) {
      SlotState.queued => l10n.inspectionQueued,
      SlotState.uploading => l10n.inspectionUploading,
      SlotState.failed => queueErrorMessage(l10n, local?.lastErrorCode),
      _ => l10n.inspectionUploaded,
    };
    return ListTile(
      contentPadding: EdgeInsets.zero,
      leading: _Thumbnail(
        local: local,
        remote: remote,
        view: SlotView(slot: extraSlot, state: state),
      ),
      title: Text(l10n.inspectionSlotExtra),
      subtitle: Text(label),
      trailing: local != null && state != SlotState.uploading
          ? IconButton(
              tooltip: l10n.inspectionDiscard,
              onPressed: onDiscard,
              icon: const Icon(Icons.delete_outline),
            )
          : null,
    );
  }
}
