// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'proposal_vehicle.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$ProposalVehicle extends ProposalVehicle {
  @override
  final VehicleCategory category;
  @override
  final Money? commercialValue;
  @override
  final String id;
  @override
  final String make;
  @override
  final String model;
  @override
  final String plate;
  @override
  final VehicleUsage usage;
  @override
  final int year;

  factory _$ProposalVehicle([void Function(ProposalVehicleBuilder)? updates]) =>
      (ProposalVehicleBuilder()..update(updates))._build();

  _$ProposalVehicle._(
      {required this.category,
      this.commercialValue,
      required this.id,
      required this.make,
      required this.model,
      required this.plate,
      required this.usage,
      required this.year})
      : super._();
  @override
  ProposalVehicle rebuild(void Function(ProposalVehicleBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  ProposalVehicleBuilder toBuilder() => ProposalVehicleBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is ProposalVehicle &&
        category == other.category &&
        commercialValue == other.commercialValue &&
        id == other.id &&
        make == other.make &&
        model == other.model &&
        plate == other.plate &&
        usage == other.usage &&
        year == other.year;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, category.hashCode);
    _$hash = $jc(_$hash, commercialValue.hashCode);
    _$hash = $jc(_$hash, id.hashCode);
    _$hash = $jc(_$hash, make.hashCode);
    _$hash = $jc(_$hash, model.hashCode);
    _$hash = $jc(_$hash, plate.hashCode);
    _$hash = $jc(_$hash, usage.hashCode);
    _$hash = $jc(_$hash, year.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'ProposalVehicle')
          ..add('category', category)
          ..add('commercialValue', commercialValue)
          ..add('id', id)
          ..add('make', make)
          ..add('model', model)
          ..add('plate', plate)
          ..add('usage', usage)
          ..add('year', year))
        .toString();
  }
}

class ProposalVehicleBuilder
    implements Builder<ProposalVehicle, ProposalVehicleBuilder> {
  _$ProposalVehicle? _$v;

  VehicleCategory? _category;
  VehicleCategory? get category => _$this._category;
  set category(VehicleCategory? category) => _$this._category = category;

  MoneyBuilder? _commercialValue;
  MoneyBuilder get commercialValue =>
      _$this._commercialValue ??= MoneyBuilder();
  set commercialValue(MoneyBuilder? commercialValue) =>
      _$this._commercialValue = commercialValue;

  String? _id;
  String? get id => _$this._id;
  set id(String? id) => _$this._id = id;

  String? _make;
  String? get make => _$this._make;
  set make(String? make) => _$this._make = make;

  String? _model;
  String? get model => _$this._model;
  set model(String? model) => _$this._model = model;

  String? _plate;
  String? get plate => _$this._plate;
  set plate(String? plate) => _$this._plate = plate;

  VehicleUsage? _usage;
  VehicleUsage? get usage => _$this._usage;
  set usage(VehicleUsage? usage) => _$this._usage = usage;

  int? _year;
  int? get year => _$this._year;
  set year(int? year) => _$this._year = year;

  ProposalVehicleBuilder() {
    ProposalVehicle._defaults(this);
  }

  ProposalVehicleBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _category = $v.category;
      _commercialValue = $v.commercialValue?.toBuilder();
      _id = $v.id;
      _make = $v.make;
      _model = $v.model;
      _plate = $v.plate;
      _usage = $v.usage;
      _year = $v.year;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(ProposalVehicle other) {
    _$v = other as _$ProposalVehicle;
  }

  @override
  void update(void Function(ProposalVehicleBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  ProposalVehicle build() => _build();

  _$ProposalVehicle _build() {
    _$ProposalVehicle _$result;
    try {
      _$result = _$v ??
          _$ProposalVehicle._(
            category: BuiltValueNullFieldError.checkNotNull(
                category, r'ProposalVehicle', 'category'),
            commercialValue: _commercialValue?.build(),
            id: BuiltValueNullFieldError.checkNotNull(
                id, r'ProposalVehicle', 'id'),
            make: BuiltValueNullFieldError.checkNotNull(
                make, r'ProposalVehicle', 'make'),
            model: BuiltValueNullFieldError.checkNotNull(
                model, r'ProposalVehicle', 'model'),
            plate: BuiltValueNullFieldError.checkNotNull(
                plate, r'ProposalVehicle', 'plate'),
            usage: BuiltValueNullFieldError.checkNotNull(
                usage, r'ProposalVehicle', 'usage'),
            year: BuiltValueNullFieldError.checkNotNull(
                year, r'ProposalVehicle', 'year'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'commercialValue';
        _commercialValue?.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'ProposalVehicle', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
