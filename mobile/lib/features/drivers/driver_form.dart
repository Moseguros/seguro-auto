import 'package:built_collection/built_collection.dart';
import 'package:flutter/material.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../core/api/api_error.dart';
import '../../core/errors/problem.dart';
import '../../core/format/dates.dart';
import '../../core/widgets/message_banner.dart';
import '../../l10n/app_localizations.dart';
import '../profile/profile_rules.dart' show normalizeName;
import '../vehicles/vehicle_labels.dart';
import 'driver_rules.dart';

/// Dados do formulário, já validados.
class DriverInput {
  const DriverInput({
    required this.fullName,
    required this.birthDate,
    required this.licenceNumber,
    required this.licenceCategories,
    required this.licenceIssueDate,
    required this.isOwner,
  });

  final String fullName;
  final DateTime birthDate;

  /// `null` ao alterar sem mexer na carta (o backend mantém a actual).
  final String? licenceNumber;
  final List<String> licenceCategories;
  final DateTime licenceIssueDate;
  final bool isOwner;

  static Date _date(DateTime value) => Date(value.year, value.month, value.day);

  CreateDriverRequest toCreateRequest() => CreateDriverRequest(
    (b) => b
      ..fullName = fullName
      ..birthDate = _date(birthDate)
      ..licenceNumber = licenceNumber
      ..licenceCategories = ListBuilder(licenceCategories)
      ..licenceIssueDate = _date(licenceIssueDate)
      ..isOwner = isOwner,
  );

  UpdateDriverRequest toUpdateRequest(int version) => UpdateDriverRequest(
    (b) => b
      ..fullName = fullName
      ..birthDate = _date(birthDate)
      ..licenceNumber = licenceNumber
      ..licenceCategories = ListBuilder(licenceCategories)
      ..licenceIssueDate = _date(licenceIssueDate)
      ..isOwner = isOwner
      ..version = version,
  );
}

typedef SaveDriver = Future<DriverResponse> Function(DriverInput input);

const _fields = [
  'fullName',
  'birthDate',
  'licenceNumber',
  'licenceCategories',
  'licenceIssueDate',
  'isOwner',
];

/// `licenceCategories[1]` → `licenceCategories`.
String _fieldOf(String field) => field.split(RegExp(r'[.\[]')).first;

DateTime? _fromDate(Date? value) =>
    value == null ? null : DateTime(value.year, value.month, value.day);

/// Formulário do condutor, com as mesmas regras da web (S2-06 §5).
///
/// O número da carta só existe no [TextEditingController] deste formulário: não
/// vai para `shared_preferences`, armazenamento seguro nem cache.
class DriverForm extends StatefulWidget {
  const DriverForm({
    super.key,
    this.initial,
    required this.canBeOwner,
    required this.categories,
    required this.loadProfile,
    required this.onSave,
    required this.onSaved,
    required this.onReload,
    this.today,
  });

  /// Condutor a alterar; `null` para registar.
  final DriverResponse? initial;

  /// "Sou eu" só aparece se não houver outro condutor `isOwner`.
  final bool canBeOwner;

  /// Valores de `LICENCE_CATEGORY`.
  final List<PublicReferenceValueResponse> categories;

  /// `getMyProfile`, para "Sou eu".
  final Future<CustomerProfileResponse> Function() loadProfile;
  final SaveDriver onSave;
  final ValueChanged<DriverResponse> onSaved;
  final VoidCallback onReload;

  /// Hoje em `Africa/Maputo` (testes).
  final DateTime? today;

  @override
  State<DriverForm> createState() => _DriverFormState();
}

class _DriverFormState extends State<DriverForm> {
  final _formKey = GlobalKey<FormState>();
  final _fullName = TextEditingController();
  final _birthDateText = TextEditingController();
  final _licenceNumber = TextEditingController();
  final _issueDateText = TextEditingController();
  DateTime? _birthDate;
  DateTime? _issueDate;
  final Set<String> _categories = {};
  bool _isOwner = false;
  bool _editingLicence = false;
  bool _loadingProfile = false;
  bool _saving = false;
  bool _conflict = false;
  String? _message;
  final Map<String, String> _serverErrors = {};

  DateTime get _today => widget.today ?? todayInMaputo();

  bool get _isEdit => widget.initial != null;

  bool get _showLicenceField => !_isEdit || _editingLicence;

  @override
  void initState() {
    super.initState();
    final driver = widget.initial;
    if (driver == null) return;
    _fullName.text = driver.fullName;
    _birthDate = _fromDate(driver.birthDate);
    _issueDate = _fromDate(driver.licenceIssueDate);
    _categories.addAll(driver.licenceCategories);
    _isOwner = driver.isOwner;
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    _syncDates();
  }

  void _syncDates() {
    final format = MaterialLocalizations.of(context).formatCompactDate;
    _birthDateText.text = _birthDate == null ? '' : format(_birthDate!);
    _issueDateText.text = _issueDate == null ? '' : format(_issueDate!);
  }

  @override
  void dispose() {
    _fullName.dispose();
    _birthDateText.dispose();
    _licenceNumber.dispose();
    _issueDateText.dispose();
    super.dispose();
  }

  String? _errorText(AppLocalizations l10n, String field, String? code) {
    final server = _serverErrors[field];
    if (server != null) return server;
    return code == null ? null : fieldErrorMessage(l10n, code);
  }

  void _clearServer(String field) {
    if (_serverErrors.remove(field) != null) setState(() {});
  }

  Future<void> _setOwner(bool value) async {
    final l10n = AppLocalizations.of(context);
    setState(() {
      _isOwner = value;
      _serverErrors.remove('isOwner');
    });
    if (!value) return;
    setState(() => _loadingProfile = true);
    try {
      final profile = await widget.loadProfile();
      if (!mounted) return;
      setState(() {
        final name = profile.fullName;
        if (name != null && name.isNotEmpty) _fullName.text = name;
        final birth = _fromDate(profile.birthDate);
        if (birth != null) _birthDate = birth;
        _serverErrors
          ..remove('fullName')
          ..remove('birthDate');
        _syncDates();
      });
    } catch (error) {
      if (mounted) setState(() => _message = Problem.from(error).message(l10n));
    } finally {
      if (mounted) setState(() => _loadingProfile = false);
    }
  }

  Future<DateTime?> _pickDate({
    required DateTime? current,
    required DateTime first,
  }) async {
    final last = _today;
    var initial = current ?? last;
    if (initial.isAfter(last)) initial = last;
    if (initial.isBefore(first)) initial = first;
    return showDatePicker(
      context: context,
      initialDate: initial,
      firstDate: first,
      lastDate: last,
      initialEntryMode: DatePickerEntryMode.calendarOnly,
    );
  }

  Future<void> _pickBirthDate() async {
    final picked = await _pickDate(
      current: _birthDate,
      first: earliestDriverBirthDate(_today),
    );
    if (picked == null || !mounted) return;
    setState(() {
      _birthDate = picked;
      _serverErrors.remove('birthDate');
      _syncDates();
    });
  }

  Future<void> _pickIssueDate() async {
    final picked = await _pickDate(
      current: _issueDate,
      first: _birthDate ?? earliestDriverBirthDate(_today),
    );
    if (picked == null || !mounted) return;
    setState(() {
      _issueDate = picked;
      _serverErrors.remove('licenceIssueDate');
      _syncDates();
    });
  }

  Future<void> _submit() async {
    final l10n = AppLocalizations.of(context);
    FocusScope.of(context).unfocus();
    setState(() {
      _message = null;
      _conflict = false;
      _serverErrors.clear();
    });
    if (!(_formKey.currentState?.validate() ?? false)) return;

    final licence = normalizeLicenceNumber(_licenceNumber.text);
    final input = DriverInput(
      fullName: normalizeName(_fullName.text),
      birthDate: _birthDate!,
      licenceNumber: _showLicenceField && licence.isNotEmpty ? licence : null,
      licenceCategories: [
        // Pela ordem da lista de referência.
        for (final category in widget.categories)
          if (_categories.contains(category.code)) category.code,
        for (final code in _categories)
          if (!widget.categories.any((c) => c.code == code)) code,
      ],
      licenceIssueDate: _issueDate!,
      isOwner: widget.canBeOwner && _isOwner,
    );

    setState(() => _saving = true);
    try {
      final saved = await widget.onSave(input);
      widget.onSaved(saved);
    } catch (error) {
      if (!mounted) return;
      final problem = Problem.from(error);
      setState(() {
        final onFields = problem.errors.where(
          (e) => _fields.contains(_fieldOf(e.field)),
        );
        switch (problem.code) {
          case 'VALIDATION_ERROR' when onFields.isNotEmpty:
            for (final e in onFields) {
              _serverErrors.putIfAbsent(
                _fieldOf(e.field),
                () => fieldErrorMessage(l10n, e.code),
              );
            }
          case 'DRIVER_ALREADY_REGISTERED':
            _editingLicence = true;
            _serverErrors['licenceNumber'] = problem.message(l10n);
          case 'OWNER_DRIVER_ALREADY_EXISTS':
            _serverErrors['isOwner'] = problem.message(l10n);
          case 'CONCURRENT_MODIFICATION':
            _conflict = true;
          default:
            _message = problem.message(l10n);
        }
      });
      _formKey.currentState?.validate();
    } finally {
      if (mounted) setState(() => _saving = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final theme = Theme.of(context);
    final language = Localizations.localeOf(context).languageCode;
    const gap = SizedBox(height: 16);
    final ownerError = _serverErrors['isOwner'];

    return Form(
      key: _formKey,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          if (_conflict) ...[
            MessageBanner(
              l10n.driverConflict,
              key: const Key('driver-conflict'),
            ),
            Align(
              alignment: AlignmentDirectional.centerStart,
              child: TextButton(
                key: const Key('driver-reload'),
                onPressed: widget.onReload,
                child: Text(l10n.reload),
              ),
            ),
            gap,
          ],
          if (_message != null) ...[
            MessageBanner(_message!, key: const Key('driver-form-message')),
            gap,
          ],
          if (widget.canBeOwner) ...[
            SwitchListTile(
              key: const Key('driver-isOwner'),
              contentPadding: EdgeInsets.zero,
              title: Text(l10n.driverFieldIsMe),
              subtitle: ownerError != null
                  ? Text(
                      ownerError,
                      style: TextStyle(color: theme.colorScheme.error),
                    )
                  : (_loadingProfile ? const LinearProgressIndicator() : null),
              value: _isOwner,
              onChanged: _saving ? null : _setOwner,
            ),
            const SizedBox(height: 8),
          ],
          TextFormField(
            key: const Key('driver-fullName'),
            controller: _fullName,
            textCapitalization: TextCapitalization.words,
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
            key: const Key('driver-birthDate'),
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
              driverBirthDateError(_birthDate, _today),
            ),
          ),
          gap,
          if (_showLicenceField)
            TextFormField(
              key: const Key('driver-licenceNumber'),
              controller: _licenceNumber,
              textCapitalization: TextCapitalization.characters,
              autocorrect: false,
              enableSuggestions: false,
              decoration: InputDecoration(
                labelText: l10n.driverFieldLicenceNumber,
                errorMaxLines: 3,
              ),
              onChanged: (_) => _clearServer('licenceNumber'),
              validator: (value) => _errorText(
                l10n,
                'licenceNumber',
                licenceNumberError(value ?? '', required: !_isEdit),
              ),
            )
          else
            InputDecorator(
              key: const Key('driver-licenceNumber-masked'),
              decoration: InputDecoration(
                labelText: l10n.driverFieldLicenceNumber,
                suffixIcon: Semantics(
                  label: l10n.driverChangeLicence,
                  button: true,
                  excludeSemantics: true,
                  child: TextButton(
                    key: const Key('driver-change-licence'),
                    onPressed: () => setState(() => _editingLicence = true),
                    child: Text(l10n.profileChange),
                  ),
                ),
              ),
              child: Text(widget.initial?.licenceNumberMasked ?? ''),
            ),
          gap,
          FormField<Set<String>>(
            key: const Key('driver-licenceCategories'),
            validator: (_) => _errorText(
              l10n,
              'licenceCategories',
              licenceCategoriesError(_categories),
            ),
            builder: (field) => InputDecorator(
              decoration: InputDecoration(
                labelText: l10n.driverFieldLicenceCategories,
                border: InputBorder.none,
                contentPadding: EdgeInsets.zero,
                errorText: field.errorText,
                errorMaxLines: 3,
              ),
              child: Padding(
                padding: const EdgeInsets.only(top: 8),
                child: Wrap(
                  spacing: 8,
                  runSpacing: 4,
                  children: [
                    for (final category in widget.categories)
                      FilterChip(
                        key: Key('licence-category-${category.code}'),
                        label: Text(referenceLabel(category, language)),
                        selected: _categories.contains(category.code),
                        onSelected: (selected) {
                          setState(() {
                            selected
                                ? _categories.add(category.code)
                                : _categories.remove(category.code);
                            _serverErrors.remove('licenceCategories');
                          });
                          field.didChange(_categories);
                        },
                      ),
                  ],
                ),
              ),
            ),
          ),
          gap,
          TextFormField(
            key: const Key('driver-licenceIssueDate'),
            controller: _issueDateText,
            readOnly: true,
            onTap: _pickIssueDate,
            decoration: InputDecoration(
              labelText: l10n.driverFieldLicenceIssueDate,
              suffixIcon: const Icon(Icons.calendar_today_outlined),
              errorMaxLines: 3,
            ),
            validator: (_) => _errorText(
              l10n,
              'licenceIssueDate',
              licenceIssueDateError(_issueDate, _birthDate, _today),
            ),
          ),
          const SizedBox(height: 24),
          FilledButton(
            key: const Key('driver-save'),
            style: FilledButton.styleFrom(minimumSize: const Size(64, 48)),
            onPressed: _saving || _loadingProfile ? null : _submit,
            child: Text(l10n.save),
          ),
        ],
      ),
    );
  }
}
