import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../core/api/api_error.dart';
import '../../core/errors/problem.dart';
import '../../core/l10n/province_name.dart';
import '../../core/widgets/message_banner.dart';
import '../../l10n/app_localizations.dart';
import 'profile_rules.dart';

/// Nome visível de um tipo de documento (`idDocType.<CODE>` na web).
String idDocTypeName(AppLocalizations l10n, String code) => switch (code) {
  'BI' => l10n.idDocTypeBi,
  'PASSPORT' => l10n.idDocTypePassport,
  'DIRE' => l10n.idDocTypeDire,
  _ => code,
};

/// Grava o perfil (`updateMyProfile`).
typedef SaveProfile =
    Future<CustomerProfileResponse> Function(UpdateMyProfileRequest request);

const _fields = [
  'fullName',
  'birthDate',
  'idDocType',
  'idDocNumber',
  'nuit',
  'address',
  'province',
];

/// Formulário "Dados pessoais", com as mesmas regras da web (S1-07).
///
/// O documento e o NUIT já guardados aparecem mascarados, com "Alterar"; se não forem
/// alterados, o pedido não os envia (o backend mantém os actuais). Com um perfil
/// novo (gravado ou recarregado), o pai recria o formulário (`key` com a versão).
class ProfileForm extends StatefulWidget {
  const ProfileForm({
    super.key,
    required this.profile,
    required this.onSave,
    required this.onSaved,
    required this.onConflict,
    this.today,
  });

  final CustomerProfileResponse profile;
  final SaveProfile onSave;
  final ValueChanged<CustomerProfileResponse> onSaved;

  /// 409 `CONCURRENT_MODIFICATION`: o pai recarrega o perfil.
  final VoidCallback onConflict;

  /// Hoje em `Africa/Maputo` (testes).
  final DateTime? today;

  @override
  State<ProfileForm> createState() => _ProfileFormState();
}

class _ProfileFormState extends State<ProfileForm> {
  final _formKey = GlobalKey<FormState>();
  final _fullName = TextEditingController();
  final _birthDateText = TextEditingController();
  final _idDocNumber = TextEditingController();
  final _nuit = TextEditingController();
  final _address = TextEditingController();
  DateTime? _birthDate;
  String? _idDocType;
  String? _province;
  bool _editingDocument = false;
  bool _editingNuit = false;
  bool _saving = false;
  String? _message;
  final Map<String, String> _serverErrors = {};

  DateTime get _today => widget.today ?? todayInMaputo();

  CustomerProfileResponse get _profile => widget.profile;

  bool get _hasDocument => (_profile.idDocNumberMasked ?? '').isNotEmpty;

  bool get _hasNuit => (_profile.nuitMasked ?? '').isNotEmpty;

  bool get _documentTypeChanged =>
      _hasDocument &&
      _profile.idDocType != null &&
      _idDocType != _profile.idDocType!.name;

  bool get _showDocumentField =>
      !_hasDocument || _editingDocument || _documentTypeChanged;

  bool get _showNuitField => !_hasNuit || _editingNuit;

  @override
  void initState() {
    super.initState();
    _reset();
  }

  void _reset() {
    final birth = _profile.birthDate;
    _fullName.text = _profile.fullName ?? '';
    _birthDate = birth == null
        ? null
        : DateTime(birth.year, birth.month, birth.day);
    _idDocType = _profile.idDocType?.name;
    _idDocNumber.clear();
    _nuit.clear();
    _address.text = _profile.address ?? '';
    _province = _profile.province?.name;
    _editingDocument = false;
    _editingNuit = false;
    _serverErrors.clear();
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    _syncBirthDateText();
  }

  void _syncBirthDateText() {
    final date = _birthDate;
    _birthDateText.text = date == null
        ? ''
        : MaterialLocalizations.of(context).formatCompactDate(date);
  }

  @override
  void dispose() {
    _fullName.dispose();
    _birthDateText.dispose();
    _idDocNumber.dispose();
    _nuit.dispose();
    _address.dispose();
    super.dispose();
  }

  String? _errorText(AppLocalizations l10n, String field, String? code) {
    final server = _serverErrors[field];
    final value = code ?? server;
    return value == null ? null : fieldErrorMessage(l10n, value);
  }

  void _clearServer(String field) {
    if (_serverErrors.remove(field) != null) setState(() {});
  }

  Future<void> _pickBirthDate() async {
    final today = _today;
    final last = latestBirthDate(today);
    final first = earliestBirthDate(today);
    var initial = _birthDate ?? last;
    if (initial.isAfter(last)) initial = last;
    if (initial.isBefore(first)) initial = first;
    final picked = await showDatePicker(
      context: context,
      initialDate: initial,
      firstDate: first,
      lastDate: last,
      initialEntryMode: DatePickerEntryMode.calendarOnly,
    );
    if (picked == null || !mounted) return;
    setState(() {
      _birthDate = picked;
      _serverErrors.remove('birthDate');
      _syncBirthDateText();
    });
  }

  Future<void> _submit() async {
    final l10n = AppLocalizations.of(context);
    setState(() {
      _message = null;
      _serverErrors.clear();
    });
    if (!(_formKey.currentState?.validate() ?? false)) return;

    final document = normalizeDocumentNumber(_idDocNumber.text);
    final nuit = normalizeNuit(_nuit.text);
    final address = _address.text.trim();
    final birth = _birthDate!;
    final request = UpdateMyProfileRequest(
      (b) => b
        ..fullName = normalizeName(_fullName.text)
        ..birthDate = Date(birth.year, birth.month, birth.day)
        ..idDocType = UpdateMyProfileRequestIdDocTypeEnum.valueOf(_idDocType!)
        ..idDocNumber = _showDocumentField && document.isNotEmpty
            ? document
            : null
        ..nuit = _showNuitField && nuit.isNotEmpty ? nuit : null
        ..address = address.isEmpty ? null : address
        ..province = UpdateMyProfileRequestProvinceEnum.valueOf(_province!)
        ..version = _profile.version,
    );

    setState(() => _saving = true);
    try {
      final saved = await widget.onSave(request);
      // O pai recria o formulário com a versão nova: o aviso fica a cargo dele.
      widget.onSaved(saved);
    } catch (error) {
      if (!mounted) return;
      final problem = Problem.from(error);
      setState(() {
        final onFields = problem.errors.where((e) => _fields.contains(e.field));
        if (problem.code == 'VALIDATION_ERROR' && onFields.isNotEmpty) {
          for (final e in onFields) {
            _serverErrors[e.field] = e.code;
          }
        } else {
          _message = problem.message(l10n);
        }
      });
      _formKey.currentState?.validate();
      if (problem.code == 'CONCURRENT_MODIFICATION') widget.onConflict();
    } finally {
      if (mounted) setState(() => _saving = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    const gap = SizedBox(height: 16);

    return Form(
      key: _formKey,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          TextFormField(
            key: const Key('profile-fullName'),
            controller: _fullName,
            textCapitalization: TextCapitalization.words,
            autofillHints: const [AutofillHints.name],
            decoration: InputDecoration(
              labelText: l10n.profileFieldFullName,
              errorMaxLines: 3,
            ),
            onChanged: (_) => _clearServer('fullName'),
            validator: (value) =>
                _errorText(l10n, 'fullName', nameError(value ?? '')),
          ),
          gap,
          TextFormField(
            key: const Key('profile-birthDate'),
            controller: _birthDateText,
            readOnly: true,
            onTap: _pickBirthDate,
            decoration: InputDecoration(
              labelText: l10n.profileFieldBirthDate,
              suffixIcon: const Icon(Icons.calendar_today_outlined),
              errorMaxLines: 3,
            ),
            validator: (_) => _errorText(
              l10n,
              'birthDate',
              birthDateError(_birthDate, _today),
            ),
          ),
          gap,
          DropdownButtonFormField<String>(
            key: const Key('profile-idDocType'),
            initialValue: _idDocType,
            isExpanded: true,
            hint: Text(l10n.profileChoose),
            decoration: InputDecoration(labelText: l10n.profileFieldIdDocType),
            items: [
              for (final type in idDocTypes)
                DropdownMenuItem(
                  value: type,
                  child: Text(idDocTypeName(l10n, type)),
                ),
            ],
            onChanged: (value) => setState(() {
              _idDocType = value;
              _serverErrors.remove('idDocType');
            }),
            validator: (value) =>
                _errorText(l10n, 'idDocType', idDocTypeError(value)),
          ),
          gap,
          if (_showDocumentField)
            TextFormField(
              key: const Key('profile-idDocNumber'),
              controller: _idDocNumber,
              textCapitalization: TextCapitalization.characters,
              autocorrect: false,
              decoration: InputDecoration(
                labelText: l10n.profileFieldIdDocNumber,
                errorMaxLines: 3,
              ),
              onChanged: (_) => _clearServer('idDocNumber'),
              validator: (value) => _errorText(
                l10n,
                'idDocNumber',
                documentNumberError(
                  value ?? '',
                  required: !_hasDocument || _documentTypeChanged,
                ),
              ),
            )
          else
            _MaskedValue(
              key: const Key('profile-idDocNumber-masked'),
              label: l10n.profileFieldIdDocNumber,
              value: _profile.idDocNumberMasked ?? '',
              changeLabel: l10n.profileChangeDocument,
              onChange: () => setState(() => _editingDocument = true),
            ),
          gap,
          if (_showNuitField)
            TextFormField(
              key: const Key('profile-nuit'),
              controller: _nuit,
              keyboardType: TextInputType.number,
              inputFormatters: [
                FilteringTextInputFormatter.allow(RegExp(r'[\d ]')),
                LengthLimitingTextInputFormatter(11),
              ],
              decoration: InputDecoration(
                labelText: l10n.profileFieldNuit,
                helperText: l10n.profileNuitHelper,
                errorMaxLines: 3,
              ),
              onChanged: (_) => _clearServer('nuit'),
              validator: (value) => _errorText(
                l10n,
                'nuit',
                nuitError(value ?? '', required: !_hasNuit),
              ),
            )
          else
            _MaskedValue(
              key: const Key('profile-nuit-masked'),
              label: l10n.profileFieldNuit,
              value: _profile.nuitMasked ?? '',
              changeLabel: l10n.profileChangeNuit,
              onChange: () => setState(() => _editingNuit = true),
            ),
          gap,
          TextFormField(
            key: const Key('profile-address'),
            controller: _address,
            autofillHints: const [AutofillHints.fullStreetAddress],
            decoration: InputDecoration(
              labelText: l10n.profileFieldAddress,
              helperText: l10n.profileOptional,
              errorMaxLines: 3,
            ),
            onChanged: (_) => _clearServer('address'),
            validator: (value) =>
                _errorText(l10n, 'address', addressError(value ?? '')),
          ),
          gap,
          DropdownButtonFormField<String>(
            key: const Key('profile-province'),
            initialValue: _province,
            isExpanded: true,
            hint: Text(l10n.profileChoose),
            decoration: InputDecoration(labelText: l10n.profileFieldProvince),
            items: [
              for (final code in provinceCodes)
                DropdownMenuItem(
                  value: code,
                  child: Text(provinceName(l10n, code)),
                ),
            ],
            onChanged: (value) => setState(() {
              _province = value;
              _serverErrors.remove('province');
            }),
            validator: (value) => _errorText(
              l10n,
              'province',
              provinceError(value, provinceCodes),
            ),
          ),
          if (_message != null) ...[
            gap,
            MessageBanner(_message!, key: const Key('profile-error')),
          ],
          const SizedBox(height: 24),
          FilledButton(
            key: const Key('profile-save'),
            style: FilledButton.styleFrom(minimumSize: const Size(64, 48)),
            onPressed: _saving ? null : _submit,
            child: Text(l10n.profileSave),
          ),
        ],
      ),
    );
  }
}

/// Valor já guardado, mascarado e só de leitura, com o botão "Alterar".
class _MaskedValue extends StatelessWidget {
  const _MaskedValue({
    super.key,
    required this.label,
    required this.value,
    required this.changeLabel,
    required this.onChange,
  });

  final String label;
  final String value;
  final String changeLabel;
  final VoidCallback onChange;

  @override
  Widget build(BuildContext context) {
    return InputDecorator(
      decoration: InputDecoration(
        labelText: label,
        suffixIcon: Semantics(
          label: changeLabel,
          button: true,
          excludeSemantics: true,
          child: TextButton(
            onPressed: onChange,
            child: Text(AppLocalizations.of(context).profileChange),
          ),
        ),
      ),
      child: Text(value),
    );
  }
}
