// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'policy_vehicle_ref.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$PolicyVehicleRef extends PolicyVehicleRef {
  @override
  final String id;
  @override
  final String? make;
  @override
  final String? model;
  @override
  final String plate;
  @override
  final int? year;

  factory _$PolicyVehicleRef(
          [void Function(PolicyVehicleRefBuilder)? updates]) =>
      (PolicyVehicleRefBuilder()..update(updates))._build();

  _$PolicyVehicleRef._(
      {required this.id, this.make, this.model, required this.plate, this.year})
      : super._();
  @override
  PolicyVehicleRef rebuild(void Function(PolicyVehicleRefBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PolicyVehicleRefBuilder toBuilder() =>
      PolicyVehicleRefBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PolicyVehicleRef &&
        id == other.id &&
        make == other.make &&
        model == other.model &&
        plate == other.plate &&
        year == other.year;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, id.hashCode);
    _$hash = $jc(_$hash, make.hashCode);
    _$hash = $jc(_$hash, model.hashCode);
    _$hash = $jc(_$hash, plate.hashCode);
    _$hash = $jc(_$hash, year.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'PolicyVehicleRef')
          ..add('id', id)
          ..add('make', make)
          ..add('model', model)
          ..add('plate', plate)
          ..add('year', year))
        .toString();
  }
}

class PolicyVehicleRefBuilder
    implements Builder<PolicyVehicleRef, PolicyVehicleRefBuilder> {
  _$PolicyVehicleRef? _$v;

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

  int? _year;
  int? get year => _$this._year;
  set year(int? year) => _$this._year = year;

  PolicyVehicleRefBuilder() {
    PolicyVehicleRef._defaults(this);
  }

  PolicyVehicleRefBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _id = $v.id;
      _make = $v.make;
      _model = $v.model;
      _plate = $v.plate;
      _year = $v.year;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(PolicyVehicleRef other) {
    _$v = other as _$PolicyVehicleRef;
  }

  @override
  void update(void Function(PolicyVehicleRefBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PolicyVehicleRef build() => _build();

  _$PolicyVehicleRef _build() {
    final _$result = _$v ??
        _$PolicyVehicleRef._(
          id: BuiltValueNullFieldError.checkNotNull(
              id, r'PolicyVehicleRef', 'id'),
          make: make,
          model: model,
          plate: BuiltValueNullFieldError.checkNotNull(
              plate, r'PolicyVehicleRef', 'plate'),
          year: year,
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
