// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'underwriting_inputs.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$UnderwritingInputs extends UnderwritingInputs {
  @override
  final bool hasOwnDamage;
  @override
  final InspectionStatus? inspectionStatus;
  @override
  final KycState kycState;
  @override
  final int maxVehicleValue;
  @override
  final BuiltList<DeclarationQuestion> positiveDeclarations;
  @override
  final int simplifiedKycMaxPremium;
  @override
  final int totalAmount;
  @override
  final int? vehicleValue;

  factory _$UnderwritingInputs(
          [void Function(UnderwritingInputsBuilder)? updates]) =>
      (UnderwritingInputsBuilder()..update(updates))._build();

  _$UnderwritingInputs._(
      {required this.hasOwnDamage,
      this.inspectionStatus,
      required this.kycState,
      required this.maxVehicleValue,
      required this.positiveDeclarations,
      required this.simplifiedKycMaxPremium,
      required this.totalAmount,
      this.vehicleValue})
      : super._();
  @override
  UnderwritingInputs rebuild(
          void Function(UnderwritingInputsBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  UnderwritingInputsBuilder toBuilder() =>
      UnderwritingInputsBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is UnderwritingInputs &&
        hasOwnDamage == other.hasOwnDamage &&
        inspectionStatus == other.inspectionStatus &&
        kycState == other.kycState &&
        maxVehicleValue == other.maxVehicleValue &&
        positiveDeclarations == other.positiveDeclarations &&
        simplifiedKycMaxPremium == other.simplifiedKycMaxPremium &&
        totalAmount == other.totalAmount &&
        vehicleValue == other.vehicleValue;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, hasOwnDamage.hashCode);
    _$hash = $jc(_$hash, inspectionStatus.hashCode);
    _$hash = $jc(_$hash, kycState.hashCode);
    _$hash = $jc(_$hash, maxVehicleValue.hashCode);
    _$hash = $jc(_$hash, positiveDeclarations.hashCode);
    _$hash = $jc(_$hash, simplifiedKycMaxPremium.hashCode);
    _$hash = $jc(_$hash, totalAmount.hashCode);
    _$hash = $jc(_$hash, vehicleValue.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'UnderwritingInputs')
          ..add('hasOwnDamage', hasOwnDamage)
          ..add('inspectionStatus', inspectionStatus)
          ..add('kycState', kycState)
          ..add('maxVehicleValue', maxVehicleValue)
          ..add('positiveDeclarations', positiveDeclarations)
          ..add('simplifiedKycMaxPremium', simplifiedKycMaxPremium)
          ..add('totalAmount', totalAmount)
          ..add('vehicleValue', vehicleValue))
        .toString();
  }
}

class UnderwritingInputsBuilder
    implements Builder<UnderwritingInputs, UnderwritingInputsBuilder> {
  _$UnderwritingInputs? _$v;

  bool? _hasOwnDamage;
  bool? get hasOwnDamage => _$this._hasOwnDamage;
  set hasOwnDamage(bool? hasOwnDamage) => _$this._hasOwnDamage = hasOwnDamage;

  InspectionStatus? _inspectionStatus;
  InspectionStatus? get inspectionStatus => _$this._inspectionStatus;
  set inspectionStatus(InspectionStatus? inspectionStatus) =>
      _$this._inspectionStatus = inspectionStatus;

  KycState? _kycState;
  KycState? get kycState => _$this._kycState;
  set kycState(KycState? kycState) => _$this._kycState = kycState;

  int? _maxVehicleValue;
  int? get maxVehicleValue => _$this._maxVehicleValue;
  set maxVehicleValue(int? maxVehicleValue) =>
      _$this._maxVehicleValue = maxVehicleValue;

  ListBuilder<DeclarationQuestion>? _positiveDeclarations;
  ListBuilder<DeclarationQuestion> get positiveDeclarations =>
      _$this._positiveDeclarations ??= ListBuilder<DeclarationQuestion>();
  set positiveDeclarations(
          ListBuilder<DeclarationQuestion>? positiveDeclarations) =>
      _$this._positiveDeclarations = positiveDeclarations;

  int? _simplifiedKycMaxPremium;
  int? get simplifiedKycMaxPremium => _$this._simplifiedKycMaxPremium;
  set simplifiedKycMaxPremium(int? simplifiedKycMaxPremium) =>
      _$this._simplifiedKycMaxPremium = simplifiedKycMaxPremium;

  int? _totalAmount;
  int? get totalAmount => _$this._totalAmount;
  set totalAmount(int? totalAmount) => _$this._totalAmount = totalAmount;

  int? _vehicleValue;
  int? get vehicleValue => _$this._vehicleValue;
  set vehicleValue(int? vehicleValue) => _$this._vehicleValue = vehicleValue;

  UnderwritingInputsBuilder() {
    UnderwritingInputs._defaults(this);
  }

  UnderwritingInputsBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _hasOwnDamage = $v.hasOwnDamage;
      _inspectionStatus = $v.inspectionStatus;
      _kycState = $v.kycState;
      _maxVehicleValue = $v.maxVehicleValue;
      _positiveDeclarations = $v.positiveDeclarations.toBuilder();
      _simplifiedKycMaxPremium = $v.simplifiedKycMaxPremium;
      _totalAmount = $v.totalAmount;
      _vehicleValue = $v.vehicleValue;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(UnderwritingInputs other) {
    _$v = other as _$UnderwritingInputs;
  }

  @override
  void update(void Function(UnderwritingInputsBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  UnderwritingInputs build() => _build();

  _$UnderwritingInputs _build() {
    _$UnderwritingInputs _$result;
    try {
      _$result = _$v ??
          _$UnderwritingInputs._(
            hasOwnDamage: BuiltValueNullFieldError.checkNotNull(
                hasOwnDamage, r'UnderwritingInputs', 'hasOwnDamage'),
            inspectionStatus: inspectionStatus,
            kycState: BuiltValueNullFieldError.checkNotNull(
                kycState, r'UnderwritingInputs', 'kycState'),
            maxVehicleValue: BuiltValueNullFieldError.checkNotNull(
                maxVehicleValue, r'UnderwritingInputs', 'maxVehicleValue'),
            positiveDeclarations: positiveDeclarations.build(),
            simplifiedKycMaxPremium: BuiltValueNullFieldError.checkNotNull(
                simplifiedKycMaxPremium,
                r'UnderwritingInputs',
                'simplifiedKycMaxPremium'),
            totalAmount: BuiltValueNullFieldError.checkNotNull(
                totalAmount, r'UnderwritingInputs', 'totalAmount'),
            vehicleValue: vehicleValue,
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'positiveDeclarations';
        positiveDeclarations.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'UnderwritingInputs', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
