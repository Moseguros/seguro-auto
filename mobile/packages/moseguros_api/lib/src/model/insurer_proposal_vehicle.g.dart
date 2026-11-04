// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'insurer_proposal_vehicle.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$InsurerProposalVehicle extends InsurerProposalVehicle {
  @override
  final VehicleCategory category;
  @override
  final String? chassisNumber;
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
  final int seats;
  @override
  final VehicleUsage usage;
  @override
  final int year;

  factory _$InsurerProposalVehicle(
          [void Function(InsurerProposalVehicleBuilder)? updates]) =>
      (InsurerProposalVehicleBuilder()..update(updates))._build();

  _$InsurerProposalVehicle._(
      {required this.category,
      this.chassisNumber,
      this.commercialValue,
      required this.id,
      required this.make,
      required this.model,
      required this.plate,
      required this.seats,
      required this.usage,
      required this.year})
      : super._();
  @override
  InsurerProposalVehicle rebuild(
          void Function(InsurerProposalVehicleBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  InsurerProposalVehicleBuilder toBuilder() =>
      InsurerProposalVehicleBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is InsurerProposalVehicle &&
        category == other.category &&
        chassisNumber == other.chassisNumber &&
        commercialValue == other.commercialValue &&
        id == other.id &&
        make == other.make &&
        model == other.model &&
        plate == other.plate &&
        seats == other.seats &&
        usage == other.usage &&
        year == other.year;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, category.hashCode);
    _$hash = $jc(_$hash, chassisNumber.hashCode);
    _$hash = $jc(_$hash, commercialValue.hashCode);
    _$hash = $jc(_$hash, id.hashCode);
    _$hash = $jc(_$hash, make.hashCode);
    _$hash = $jc(_$hash, model.hashCode);
    _$hash = $jc(_$hash, plate.hashCode);
    _$hash = $jc(_$hash, seats.hashCode);
    _$hash = $jc(_$hash, usage.hashCode);
    _$hash = $jc(_$hash, year.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'InsurerProposalVehicle')
          ..add('category', category)
          ..add('chassisNumber', chassisNumber)
          ..add('commercialValue', commercialValue)
          ..add('id', id)
          ..add('make', make)
          ..add('model', model)
          ..add('plate', plate)
          ..add('seats', seats)
          ..add('usage', usage)
          ..add('year', year))
        .toString();
  }
}

class InsurerProposalVehicleBuilder
    implements Builder<InsurerProposalVehicle, InsurerProposalVehicleBuilder> {
  _$InsurerProposalVehicle? _$v;

  VehicleCategory? _category;
  VehicleCategory? get category => _$this._category;
  set category(VehicleCategory? category) => _$this._category = category;

  String? _chassisNumber;
  String? get chassisNumber => _$this._chassisNumber;
  set chassisNumber(String? chassisNumber) =>
      _$this._chassisNumber = chassisNumber;

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

  int? _seats;
  int? get seats => _$this._seats;
  set seats(int? seats) => _$this._seats = seats;

  VehicleUsage? _usage;
  VehicleUsage? get usage => _$this._usage;
  set usage(VehicleUsage? usage) => _$this._usage = usage;

  int? _year;
  int? get year => _$this._year;
  set year(int? year) => _$this._year = year;

  InsurerProposalVehicleBuilder() {
    InsurerProposalVehicle._defaults(this);
  }

  InsurerProposalVehicleBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _category = $v.category;
      _chassisNumber = $v.chassisNumber;
      _commercialValue = $v.commercialValue?.toBuilder();
      _id = $v.id;
      _make = $v.make;
      _model = $v.model;
      _plate = $v.plate;
      _seats = $v.seats;
      _usage = $v.usage;
      _year = $v.year;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(InsurerProposalVehicle other) {
    _$v = other as _$InsurerProposalVehicle;
  }

  @override
  void update(void Function(InsurerProposalVehicleBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  InsurerProposalVehicle build() => _build();

  _$InsurerProposalVehicle _build() {
    _$InsurerProposalVehicle _$result;
    try {
      _$result = _$v ??
          _$InsurerProposalVehicle._(
            category: BuiltValueNullFieldError.checkNotNull(
                category, r'InsurerProposalVehicle', 'category'),
            chassisNumber: chassisNumber,
            commercialValue: _commercialValue?.build(),
            id: BuiltValueNullFieldError.checkNotNull(
                id, r'InsurerProposalVehicle', 'id'),
            make: BuiltValueNullFieldError.checkNotNull(
                make, r'InsurerProposalVehicle', 'make'),
            model: BuiltValueNullFieldError.checkNotNull(
                model, r'InsurerProposalVehicle', 'model'),
            plate: BuiltValueNullFieldError.checkNotNull(
                plate, r'InsurerProposalVehicle', 'plate'),
            seats: BuiltValueNullFieldError.checkNotNull(
                seats, r'InsurerProposalVehicle', 'seats'),
            usage: BuiltValueNullFieldError.checkNotNull(
                usage, r'InsurerProposalVehicle', 'usage'),
            year: BuiltValueNullFieldError.checkNotNull(
                year, r'InsurerProposalVehicle', 'year'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'commercialValue';
        _commercialValue?.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'InsurerProposalVehicle', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
