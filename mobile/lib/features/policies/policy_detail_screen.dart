import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../core/format/dates.dart';
import '../../l10n/app_localizations.dart';
import '../quote/quote_labels.dart' show apiDate, coverageName, money;
import 'document_cache.dart';
import 'policies_controller.dart';
import 'policy_labels.dart';
import 'policy_list_screen.dart' show CachedDataBanner, PolicyErrorView;

/// `/policies/:id` (S6-07 §3): cabeçalho, certificado, período e prémio,
/// coberturas, prestações (só leitura) e documentos.
class PolicyDetailScreen extends ConsumerWidget {
  const PolicyDetailScreen({super.key, required this.policyId});

  final String policyId;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final l10n = AppLocalizations.of(context);
    final provider = policyDetailProvider(policyId);
    final state = ref.watch(provider);
    final controller = ref.read(provider.notifier);
    final value = state.value;
    final detail = value?.value.detail;

    final Widget body;
    if (value == null || detail == null) {
      body = state.hasError
          ? PolicyErrorView(error: state.error!, onRetry: controller.refresh)
          : const Center(child: CircularProgressIndicator());
    } else {
      body = RefreshIndicator(
        onRefresh: controller.refresh,
        child: ListView(
          key: const Key('policy-detail'),
          physics: const AlwaysScrollableScrollPhysics(),
          padding: const EdgeInsets.all(16),
          children: [
            CachedDataBanner(problem: value.problem, syncedAt: value.syncedAt),
            _Header(policy: detail),
            const SizedBox(height: 16),
            CertificateCard(policy: detail),
            const SizedBox(height: 16),
            _PeriodCard(policy: detail),
            const SizedBox(height: 16),
            _CoveragesCard(policy: detail),
            if (detail.instalments.isNotEmpty) ...[
              const SizedBox(height: 16),
              _InstalmentsCard(policy: detail),
            ],
            const SizedBox(height: 16),
            _DocumentsCard(policy: detail, slow: value.documentsSlow),
          ],
        ),
      );
    }
    return Scaffold(
      appBar: AppBar(title: Text(detail?.vehicle.plate ?? l10n.policiesTitle)),
      body: SafeArea(child: body),
    );
  }
}

class _Header extends StatelessWidget {
  const _Header({required this.policy});

  final PolicyDetailResponse policy;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    final makeModel = vehicleMakeModel(policy.vehicle);
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          policy.vehicle.plate,
          style: theme.textTheme.headlineMedium?.copyWith(
            fontWeight: FontWeight.w700,
          ),
        ),
        if (makeModel.isNotEmpty) Text(makeModel),
        const SizedBox(height: 8),
        Wrap(
          spacing: 8,
          runSpacing: 8,
          children: [
            PolicyStatusChip(policy.status),
            CoverStatusChip(policy.coverStatus),
          ],
        ),
        const SizedBox(height: 8),
        Text(l10n.policiesNumber(policy.number)),
        Text([?policy.insurer.name, policy.product.name].join(' · ')),
      ],
    );
  }
}

/// Cartão do certificado: QR (`verificationUrl`, 160 px), n.º, validade,
/// "Mostrar à polícia" e "Ver certificado (PDF)".
class CertificateCard extends StatelessWidget {
  const CertificateCard({super.key, required this.policy});

  final PolicyDetailResponse policy;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    final certificate = policy.certificate;
    final document = certificateDocument(policy);
    final pending =
        document == null || document.status == PolicyDocumentStatus.PENDING;
    return Card(
      key: const Key('certificate-card'),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Text(
              l10n.documentTypeCertificate,
              style: theme.textTheme.titleLarge,
            ),
            const SizedBox(height: 12),
            if (certificate == null)
              Text(
                l10n.policyCertificatePending,
                key: const Key('certificate-pending'),
              )
            else if (certificate.status == CertificateStatus.REVOKED)
              Text(l10n.policyCertificateRevoked)
            else ...[
              Center(
                child: Container(
                  color: Colors.white,
                  padding: const EdgeInsets.all(8),
                  child: CertificateQr(
                    key: const Key('certificate-qr'),
                    data: certificate.verificationUrl,
                    size: 160,
                  ),
                ),
              ),
              const SizedBox(height: 12),
              Text(l10n.policyCertificateNumber(certificate.number)),
              Text(
                l10n.policyValidFromTo(
                  formatInstantDateTime(certificate.validFrom),
                  formatInstantDateTime(certificate.validTo),
                ),
              ),
              const SizedBox(height: 16),
              FilledButton.icon(
                key: const Key('show-to-police'),
                style: FilledButton.styleFrom(minimumSize: const Size(64, 48)),
                onPressed: () => context.push('/policies/${policy.id}/police'),
                icon: const Icon(Icons.qr_code_2),
                label: Text(l10n.policyShowToPolice),
              ),
              const SizedBox(height: 8),
              if (pending)
                Text(
                  l10n.policyCertificatePending,
                  key: const Key('certificate-pending'),
                  textAlign: TextAlign.center,
                )
              else
                OutlinedButton.icon(
                  key: const Key('view-certificate'),
                  style: OutlinedButton.styleFrom(
                    minimumSize: const Size(64, 48),
                  ),
                  onPressed: () =>
                      context.push('/policies/${policy.id}/certificate'),
                  icon: const Icon(Icons.picture_as_pdf_outlined),
                  label: Text(l10n.policyViewCertificate),
                ),
            ],
          ],
        ),
      ),
    );
  }
}

/// Linha "rótulo: valor" dos cartões.
class _Row extends StatelessWidget {
  const _Row(this.label, this.value);

  final String label;
  final String value;

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 4),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Expanded(
            child: Text(
              label,
              style: theme.textTheme.bodyMedium?.copyWith(
                color: theme.colorScheme.onSurfaceVariant,
              ),
            ),
          ),
          const SizedBox(width: 12),
          Flexible(child: Text(value, textAlign: TextAlign.end)),
        ],
      ),
    );
  }
}

class _Section extends StatelessWidget {
  const _Section({required this.title, required this.children});

  final String title;
  final List<Widget> children;

  @override
  Widget build(BuildContext context) => Card(
    child: Padding(
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          Text(title, style: Theme.of(context).textTheme.titleLarge),
          const SizedBox(height: 8),
          ...children,
        ],
      ),
    ),
  );
}

class _PeriodCard extends StatelessWidget {
  const _PeriodCard({required this.policy});

  final PolicyDetailResponse policy;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    return _Section(
      title: l10n.policyPeriodTitle,
      children: [
        _Row(l10n.policyStart, formatInstantDateTime(policy.startAt)),
        _Row(l10n.policyEnd, formatInstantDateTime(policy.endAt)),
        _Row(l10n.policyRenewal, renewalTypeLabel(l10n, policy.renewalType)),
        _Row(
          l10n.policyPaymentPlan,
          paymentPlanLabel(l10n, policy.paymentPlan),
        ),
        _Row(l10n.policyTotalPremium, money(l10n, policy.totalPremium)),
      ],
    );
  }
}

class _CoveragesCard extends StatelessWidget {
  const _CoveragesCard({required this.policy});

  final PolicyDetailResponse policy;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    return _Section(
      title: l10n.policyCoveragesTitle,
      children: [
        for (final coverage in policy.coverages)
          Padding(
            padding: const EdgeInsets.symmetric(vertical: 6),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  coverageName(l10n, coverage.code),
                  style: theme.textTheme.titleSmall,
                ),
                if (coverage.capital case final capital?)
                  Text(l10n.policyCoverageCapital(money(l10n, capital))),
                Text(l10n.policyCoveragePremium(money(l10n, coverage.premium))),
              ],
            ),
          ),
      ],
    );
  }
}

class _InstalmentsCard extends StatelessWidget {
  const _InstalmentsCard({required this.policy});

  final PolicyDetailResponse policy;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    return _Section(
      title: l10n.policyInstalmentsTitle,
      children: [
        for (final instalment in policy.instalments)
          _Row(
            l10n.policyInstalment(instalment.seq, apiDate(instalment.dueDate)),
            '${money(l10n, instalment.amount)} · '
            '${instalmentStatusLabel(l10n, instalment.status)}',
          ),
      ],
    );
  }
}

class _DocumentsCard extends StatelessWidget {
  const _DocumentsCard({required this.policy, required this.slow});

  final PolicyDetailResponse policy;
  final bool slow;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    return _Section(
      title: l10n.policyDocumentsTitle,
      children: [
        if (slow)
          Padding(
            padding: const EdgeInsets.only(bottom: 8),
            child: Text(
              l10n.policyDocumentsSlow,
              key: const Key('documents-slow'),
            ),
          ),
        for (final document in policy.documents)
          ListTile(
            key: Key('document-${document.type.name}'),
            contentPadding: EdgeInsets.zero,
            leading: const Icon(Icons.picture_as_pdf_outlined),
            title: Text(documentTypeLabel(l10n, document.type)),
            subtitle: document.status == PolicyDocumentStatus.PENDING
                ? Text(l10n.policyDocumentPending)
                : null,
            trailing: document.status == PolicyDocumentStatus.READY
                ? const Icon(Icons.chevron_right)
                : null,
            enabled:
                document.status == PolicyDocumentStatus.READY &&
                document.fileId != null,
            onTap: () => context.push(
              '/policies/${policy.id}/certificate?fileId=${document.fileId}',
            ),
          ),
      ],
    );
  }
}
