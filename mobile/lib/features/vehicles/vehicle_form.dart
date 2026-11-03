import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../core/api/api_error.dart';
import '../../core/errors/problem.dart';
import '../../core/format/money.dart';
import '../../core/l10n/province_name.dart';
import '../../core/widgets/message_banner.dart';
import '../../core/widgets/money_field.dart';
import '../../l10n/app_localizations.dart';
import 'registry_lookup_button.dart';
import 'vehicle_labels.dart';
import 'vehicle_rules.dart';

/// Dados do formulário, já validados, para `createMyVehicle` / `updateMyVehicle`.
class VehicleInput {
  const VehicleInput({
    required this.plate,
    required this.chassisNumber,
    required this.make,
    required this.model,
    required this.year,
    required this.engineCc,
    required this.category,
    required this.seats,
    required this.grossWeightKg,
    required this.usage,
    required this.provinceOfCirculation,
    required this.commercialValueCents,
    required this.hasTracker,
  });

  final String plate;
  final String chassisNumber;
  final String make;
  final String model;
  final int year;
  final int? engineCc;
  final String category;
  final int seats;
  final int? grossWeightKg;
  final String usage;
  final String provinceOfCirculation;
  final int? commercialValueCents;
  final bool hasTracker;

  Money? get _commercialValue {
    final cents = commercialValueCents;
    return cents == null
        ? null
        : Money(
            (b) => b
              ..amount = cents
              ..currency = MoneyCurrencyEnum.MZN,
          );
  }

  CreateVehicleRequest toCreateRequest() => CreateVehicleRequest(
    (b) => b
      ..plate = plate
      ..chassisNumber = chassisNumber
      ..make = make
      ..model = model
      ..year = year
      ..engineCc = engineCc
      ..category = CreateVehicleRequestCategoryEnum.valueOf(category)
      ..seats = seats
      ..grossWeightKg = grossWeightKg
      ..usage = CreateVehicleRequestUsageEnum.valueOf(usage)
      ..provinceOfCirculation =
          CreateVehicleRequestProvinceOfCirculationEnum.valueOf(
            provinceOfCirculation,
          )
      ..commercialValue = _commercialValue?.toBuilder()
      ..hasTracker = hasTracker,
  );

  UpdateVehicleRequest toUpdateRequest(int version) => UpdateVehicleRequest(
    (b) => b
      ..plate = plate
      ..chassisNumber = chassisNumber
      ..make = make
      ..model = model
      ..year = year
      ..engineCc = engineCc
      ..category = UpdateVehicleRequestCategoryEnum.valueOf(category)
      ..seats = seats
      ..grossWeightKg = grossWeightKg
      ..usage = UpdateVehicleRequestUsageEnum.valueOf(usage)
      ..provinceOfCirculation =
          UpdateVehicleRequestProvinceOfCirculationEnum.valueOf(
            provinceOfCirculation,
          )
      ..commercialValue = _commercialValue?.toBuilder()
      ..hasTracker = hasTracker
      ..version = version,
  );
}

/// Grava o veículo e devolve a resposta da API.
typedef SaveVehicle = Future<VehicleResponse> Function(VehicleInput input);

const _fields = [
  'plate',
  'chassisNumber',
  'make',
  'model',
  'year',
  'engineCc',
  'category',
  'seats',
  'grossWeightKg',
  'usage',
  'provinceOfCirculation',
  'commercialValue',
];

/// Campo do formulário a que pertence um `errors[].field` do servidor
/// (ex.: `commercialValue.amount` → `commercialValue`).
String _fieldOf(String field) => field.split(RegExp(r'[.\[]')).first;

/// Formulário do veículo, com as mesmas regras e componentes equivalentes aos da
/// web (S2-06 §3).
class VehicleForm extends StatefulWidget {
  const VehicleForm({
    super.key,
    this.initial,
    required this.onSave,
    required this.onSaved,
    required this.onReload,
    required this.currentYear,
    this.makes = const [],
    this.models = const [],
    this.plateExamples = const [],
  });

  /// Veículo a alterar; `null` para acrescentar.
  final VehicleResponse? initial;
  final SaveVehicle onSave;
  final ValueChanged<VehicleResponse> onSaved;

  /// "Recarregar" depois de um 409 `CONCURRENT_MODIFICATION`.
  final VoidCallback onReload;

  /// Ano actual em `Africa/Maputo` (o máximo aceite é o seguinte).
  final int currentYear;

  /// Listas `VEHICLE_MAKE` e `VEHICLE_MODEL` (sugestões; o texto é livre).
  final List<PublicReferenceValueResponse> makes;
  final List<PublicReferenceValueResponse> models;

  /// `example` dos formatos de matrícula activos.
  final List<String> plateExamples;

  @override
  State<VehicleForm> createState() => _VehicleFormState();
}

enum _Notice { none, registryFilled, conflict }

class _VehicleFormState extends State<VehicleForm> {
  final _formKey = GlobalKey<FormState>();
  final _plate = TextEditingController();
  final _chassis = TextEditingController();
  final _make = TextEditingController();
  final _model = TextEditingController();
  final _year = TextEditingController();
  final _engineCc = TextEditingController();
  final _seats = TextEditingController();
  final _grossWeight = TextEditingController();
  final _commercialValue = TextEditingController();
  final _makeFocus = FocusNode();
  final _modelFocus = FocusNode();
  String? _category;
  String? _usage;
  String? _province;
  bool _hasTracker = false;
  bool _saving = false;

  /// Aviso no topo (erro do servidor ou do registo).
  String? _message;
  _Notice _notice = _Notice.none;

  /// Mensagens já traduzidas, por campo.
  final Map<String, String> _serverErrors = {};

  bool _initialized = false;

  /// Quantas vezes o registo preencheu o formulário.
  int _registryFills = 0;

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    if (_initialized) return;
    _initialized = true;
    final vehicle = widget.initial;
    if (vehicle == null) return;
    final language = Localizations.localeOf(context).languageCode;
    _plate.text = vehicle.plate;
    _chassis.text = vehicle.chassisNumber;
    _make.text = vehicle.make;
    _model.text = vehicle.model;
    _year.text = '${vehicle.year}';
    _engineCc.text = vehicle.engineCc?.toString() ?? '';
    _seats.text = '${vehicle.seats}';
    _grossWeight.text = vehicle.grossWeightKg?.toString() ?? '';
    _commercialValue.text = MoneyField.textFor(
      vehicle.commercialValue?.amount,
      language,
    );
    _category = vehicle.category.name;
    _usage = vehicle.usage.name;
    _province = vehicle.provinceOfCirculation.name;
    _hasTracker = vehicle.hasTracker;
  }

  @override
  void dispose() {
    for (final controller in [
      _plate,
      _chassis,
      _make,
      _model,
      _year,
      _engineCc,
      _seats,
      _grossWeight,
      _commercialValue,
    ]) {
      controller.dispose();
    }
    _makeFocus.dispose();
    _modelFocus.dispose();
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

  void _setCategory(String? value) {
    setState(() {
      final wasTrailer = hasFixedSeats(_category);
      _category = value;
      _serverErrors
        ..remove('category')
        ..remove('seats')
        ..remove('engineCc');
      if (hasFixedSeats(value)) {
        _seats.text = '0';
        _engineCc.clear();
      } else if (wasTrailer && _seats.text == '0') {
        _seats.clear();
      }
    });
  }

  void _fillFromRegistry(RegistryLookupResponse data) {
    setState(() {
      _chassis.text = data.chassisNumber;
      _make.text = data.make;
      _model.text = data.model;
      _year.text = '${data.year}';
      _category = data.category.name;
      _engineCc.text = hasEngine(_category)
          ? (data.engineCc?.toString() ?? '')
          : '';
      _seats.text = '${data.seats}';
      _grossWeight.text = data.grossWeightKg?.toString() ?? '';
      _serverErrors.clear();
      _message = null;
      _notice = _Notice.registryFilled;
      _registryFills++;
    });
  }

  void _registryNotice(String message) => setState(() {
    _notice = _Notice.none;
    _message = message;
  });

  void _plateError(String code) {
    final l10n = AppLocalizations.of(context);
    setState(() {
      _notice = _Notice.none;
      _message = null;
      _serverErrors['plate'] = fieldErrorMessage(l10n, code);
    });
    _formKey.currentState?.validate();
  }

  List<String> get _makeLabels => [
    for (final make in widget.makes) make.labelPt,
  ];

  List<String> get _modelLabels {
    final written = _make.text.trim().toLowerCase();
    PublicReferenceValueResponse? make;
    for (final value in widget.makes) {
      if (value.labelPt.toLowerCase() == written ||
          (value.labelEn ?? '').toLowerCase() == written) {
        make = value;
        break;
      }
    }
    if (make == null) return const [];
    return [
      for (final model in widget.models)
        if (model.parentCode == make.code) model.labelPt,
    ];
  }

  Iterable<String> _suggest(List<String> options, TextEditingValue value) {
    final text = value.text.trim().toLowerCase();
    if (text.isEmpty) return options;
    return options.where((option) => option.toLowerCase().contains(text));
  }

  Future<void> _submit() async {
    final l10n = AppLocalizations.of(context);
    FocusScope.of(context).unfocus();
    setState(() {
      _message = null;
      if (_notice == _Notice.conflict) _notice = _Notice.none;
      _serverErrors.clear();
    });
    if (!(_formKey.currentState?.validate() ?? false)) return;

    final category = _category!;
    final input = VehicleInput(
      plate: _plate.text.trim(),
      chassisNumber: normalizeChassis(_chassis.text),
      make: _make.text.trim(),
      model: _model.text.trim(),
      year: parseOptionalInt(_year.text)!,
      engineCc: hasEngine(category) ? parseOptionalInt(_engineCc.text) : null,
      category: category,
      seats: hasFixedSeats(category) ? 0 : parseOptionalInt(_seats.text)!,
      grossWeightKg: parseOptionalInt(_grossWeight.text),
      usage: _usage!,
      provinceOfCirculation: _province!,
      commercialValueCents: parseMeticais(_commercialValue.text).cents,
      hasTracker: _hasTracker,
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
        if (problem.code == 'VALIDATION_ERROR' && onFields.isNotEmpty) {
          for (final e in onFields) {
            _serverErrors.putIfAbsent(
              _fieldOf(e.field),
              () => fieldErrorMessage(l10n, e.code),
            );
          }
        } else if (problem.code == 'VEHICLE_ALREADY_REGISTERED') {
          _serverErrors['plate'] = problem.message(l10n);
        } else if (problem.code == 'CONCURRENT_MODIFICATION') {
          _notice = _Notice.conflict;
        } else {
          _message = problem.message(l10n);
        }
      });
      _formKey.currentState?.validate();
    } finally {
      if (mounted) setState(() => _saving = false);
    }
  }

  Widget _autocomplete({
    required Key key,
    required String field,
    required String label,
    required TextEditingController controller,
    required FocusNode focusNode,
    required List<String> Function() options,
  }) {
    final l10n = AppLocalizations.of(context);
    return Autocomplete<String>(
      textEditingController: controller,
      focusNode: focusNode,
      optionsBuilder: (value) => _suggest(options(), value),
      onSelected: (_) => _clearServer(field),
      fieldViewBuilder: (context, controller, focusNode, onSubmitted) =>
          TextFormField(
            key: key,
            controller: controller,
            focusNode: focusNode,
            textCapitalization: TextCapitalization.words,
            decoration: InputDecoration(labelText: label, errorMaxLines: 3),
            onChanged: (_) {
              _clearServer(field);
              // As sugestões de modelo dependem da marca escrita.
              if (field == 'make') setState(() {});
            },
            onFieldSubmitted: (_) => onSubmitted(),
            validator: (value) =>
                _errorText(l10n, field, makeOrModelError(value ?? '')),
          ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    const gap = SizedBox(height: 16);
    final digitsOnly = [FilteringTextInputFormatter.digitsOnly];
    final examples = widget.plateExamples;

    return Form(
      key: _formKey,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          if (_notice == _Notice.conflict) ...[
            MessageBanner(
              l10n.vehicleConflict,
              key: const Key('vehicle-conflict'),
            ),
            Align(
              alignment: AlignmentDirectional.centerStart,
              child: TextButton(
                key: const Key('vehicle-reload'),
                onPressed: widget.onReload,
                child: Text(l10n.reload),
              ),
            ),
            gap,
          ],
          if (_message != null) ...[
            MessageBanner(_message!, key: const Key('vehicle-form-message')),
            gap,
          ],
          TextFormField(
            key: const Key('vehicle-plate'),
            controller: _plate,
            textCapitalization: TextCapitalization.characters,
            autocorrect: false,
            decoration: InputDecoration(
              labelText: l10n.vehicleFieldPlate,
              helperText: examples.isEmpty
                  ? null
                  : l10n.vehiclePlateHelper(examples.join(l10n.vehicleOr)),
              helperMaxLines: 2,
              errorMaxLines: 3,
            ),
            onChanged: (_) => _clearServer('plate'),
            validator: (value) =>
                _errorText(l10n, 'plate', plateError(value ?? '')),
          ),
          const SizedBox(height: 8),
          Align(
            alignment: AlignmentDirectional.centerStart,
            child: RegistryLookupButton(
              plate: _plate,
              onFound: _fillFromRegistry,
              onNotice: _registryNotice,
              onPlateError: _plateError,
            ),
          ),
          if (_notice == _Notice.registryFilled) ...[
            const SizedBox(height: 8),
            MessageBanner(
              l10n.vehicleRegistryFilled,
              key: const Key('registry-filled'),
              kind: MessageKind.info,
            ),
          ],
          gap,
          TextFormField(
            key: const Key('vehicle-chassis'),
            controller: _chassis,
            textCapitalization: TextCapitalization.characters,
            autocorrect: false,
            decoration: InputDecoration(
              labelText: l10n.vehicleFieldChassis,
              errorMaxLines: 3,
            ),
            onChanged: (_) => _clearServer('chassisNumber'),
            validator: (value) =>
                _errorText(l10n, 'chassisNumber', chassisError(value ?? '')),
          ),
          gap,
          _autocomplete(
            key: const Key('vehicle-make'),
            field: 'make',
            label: l10n.vehicleFieldMake,
            controller: _make,
            focusNode: _makeFocus,
            options: () => _makeLabels,
          ),
          gap,
          _autocomplete(
            key: const Key('vehicle-model'),
            field: 'model',
            label: l10n.vehicleFieldModel,
            controller: _model,
            focusNode: _modelFocus,
            options: () => _modelLabels,
          ),
          gap,
          TextFormField(
            key: const Key('vehicle-year'),
            controller: _year,
            keyboardType: TextInputType.number,
            inputFormatters: [
              ...digitsOnly,
              LengthLimitingTextInputFormatter(4),
            ],
            decoration: InputDecoration(
              labelText: l10n.vehicleFieldYear,
              errorMaxLines: 3,
            ),
            onChanged: (_) => _clearServer('year'),
            validator: (value) => _errorText(
              l10n,
              'year',
              yearError(value ?? '', widget.currentYear),
            ),
          ),
          gap,
          // Recriado quando o registo preenche a categoria (`initialValue`).
          KeyedSubtree(
            key: ValueKey('category-$_registryFills'),
            child: DropdownButtonFormField<String>(
              key: const Key('vehicle-category'),
              initialValue: _category,
              isExpanded: true,
              hint: Text(l10n.profileChoose),
              decoration: InputDecoration(labelText: l10n.vehicleFieldCategory),
              items: [
                for (final code in vehicleCategories)
                  DropdownMenuItem(
                    value: code,
                    child: Text(vehicleCategoryName(l10n, code)),
                  ),
              ],
              onChanged: _setCategory,
              validator: (value) =>
                  _errorText(l10n, 'category', categoryError(value)),
            ),
          ),
          if (hasEngine(_category)) ...[
            gap,
            TextFormField(
              key: const Key('vehicle-engineCc'),
              controller: _engineCc,
              keyboardType: TextInputType.number,
              inputFormatters: digitsOnly,
              decoration: InputDecoration(
                labelText: l10n.vehicleFieldEngineCc,
                errorMaxLines: 3,
              ),
              onChanged: (_) => _clearServer('engineCc'),
              validator: (value) => _errorText(
                l10n,
                'engineCc',
                engineCcError(value ?? '', _category),
              ),
            ),
          ],
          gap,
          TextFormField(
            key: const Key('vehicle-seats'),
            controller: _seats,
            enabled: !hasFixedSeats(_category),
            keyboardType: TextInputType.number,
            inputFormatters: digitsOnly,
            decoration: InputDecoration(
              labelText: l10n.vehicleFieldSeats,
              errorMaxLines: 3,
            ),
            onChanged: (_) => _clearServer('seats'),
            validator: (value) =>
                _errorText(l10n, 'seats', seatsError(value ?? '', _category)),
          ),
          gap,
          TextFormField(
            key: const Key('vehicle-grossWeight'),
            controller: _grossWeight,
            keyboardType: TextInputType.number,
            inputFormatters: digitsOnly,
            decoration: InputDecoration(
              labelText: l10n.vehicleFieldGrossWeight,
              helperText: requiresGrossWeight(_category)
                  ? null
                  : l10n.profileOptional,
              errorMaxLines: 3,
            ),
            onChanged: (_) => _clearServer('grossWeightKg'),
            validator: (value) => _errorText(
              l10n,
              'grossWeightKg',
              grossWeightError(value ?? '', _category),
            ),
          ),
          gap,
          DropdownButtonFormField<String>(
            key: const Key('vehicle-usage'),
            initialValue: _usage,
            isExpanded: true,
            hint: Text(l10n.profileChoose),
            decoration: InputDecoration(labelText: l10n.vehicleFieldUsage),
            items: [
              for (final code in vehicleUsages)
                DropdownMenuItem(
                  value: code,
                  child: Text(vehicleUsageName(l10n, code)),
                ),
            ],
            onChanged: (value) => setState(() {
              _usage = value;
              _serverErrors.remove('usage');
            }),
            validator: (value) => _errorText(l10n, 'usage', usageError(value)),
          ),
          gap,
          DropdownButtonFormField<String>(
            key: const Key('vehicle-province'),
            initialValue: _province,
            isExpanded: true,
            hint: Text(l10n.profileChoose),
            decoration: InputDecoration(labelText: l10n.vehicleFieldProvince),
            items: [
              for (final code in provinceCodes)
                DropdownMenuItem(
                  value: code,
                  child: Text(provinceName(l10n, code)),
                ),
            ],
            onChanged: (value) => setState(() {
              _province = value;
              _serverErrors.remove('provinceOfCirculation');
            }),
            validator: (value) => _errorText(
              l10n,
              'provinceOfCirculation',
              provinceOfCirculationError(value, provinceCodes),
            ),
          ),
          gap,
          MoneyField(
            key: const Key('vehicle-commercialValue'),
            controller: _commercialValue,
            label: l10n.vehicleFieldCommercialValue,
            helperText: l10n.vehicleCommercialValueHelper,
            onChanged: (_) => _clearServer('commercialValue'),
            validator: (value) => _errorText(
              l10n,
              'commercialValue',
              commercialValueError(value ?? ''),
            ),
          ),
          const SizedBox(height: 8),
          SwitchListTile(
            key: const Key('vehicle-hasTracker'),
            contentPadding: EdgeInsets.zero,
            title: Text(l10n.vehicleFieldHasTracker),
            value: _hasTracker,
            onChanged: (value) => setState(() => _hasTracker = value),
          ),
          const SizedBox(height: 24),
          FilledButton(
            key: const Key('vehicle-save'),
            style: FilledButton.styleFrom(minimumSize: const Size(64, 48)),
            onPressed: _saving ? null : _submit,
            child: Text(l10n.save),
          ),
        ],
      ),
    );
  }
}
