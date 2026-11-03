// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'vehicle_driver_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$VehicleDriverResponse extends VehicleDriverResponse {
  @override
  final String driverId;
  @override
  final String fullName;
  @override
  final bool habitual;

  factory _$VehicleDriverResponse(
          [void Function(VehicleDriverResponseBuilder)? updates]) =>
      (VehicleDriverResponseBuilder()..update(updates))._build();

  _$VehicleDriverResponse._(
      {required this.driverId, required this.fullName, required this.habitual})
      : super._();
  @override
  VehicleDriverResponse rebuild(
          void Function(VehicleDriverResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  VehicleDriverResponseBuilder toBuilder() =>
      VehicleDriverResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is VehicleDriverResponse &&
        driverId == other.driverId &&
        fullName == other.fullName &&
        habitual == other.habitual;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, driverId.hashCode);
    _$hash = $jc(_$hash, fullName.hashCode);
    _$hash = $jc(_$hash, habitual.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'VehicleDriverResponse')
          ..add('driverId', driverId)
          ..add('fullName', fullName)
          ..add('habitual', habitual))
        .toString();
  }
}

class VehicleDriverResponseBuilder
    implements Builder<VehicleDriverResponse, VehicleDriverResponseBuilder> {
  _$VehicleDriverResponse? _$v;

  String? _driverId;
  String? get driverId => _$this._driverId;
  set driverId(String? driverId) => _$this._driverId = driverId;

  String? _fullName;
  String? get fullName => _$this._fullName;
  set fullName(String? fullName) => _$this._fullName = fullName;

  bool? _habitual;
  bool? get habitual => _$this._habitual;
  set habitual(bool? habitual) => _$this._habitual = habitual;

  VehicleDriverResponseBuilder() {
    VehicleDriverResponse._defaults(this);
  }

  VehicleDriverResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _driverId = $v.driverId;
      _fullName = $v.fullName;
      _habitual = $v.habitual;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(VehicleDriverResponse other) {
    _$v = other as _$VehicleDriverResponse;
  }

  @override
  void update(void Function(VehicleDriverResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  VehicleDriverResponse build() => _build();

  _$VehicleDriverResponse _build() {
    final _$result = _$v ??
        _$VehicleDriverResponse._(
          driverId: BuiltValueNullFieldError.checkNotNull(
              driverId, r'VehicleDriverResponse', 'driverId'),
          fullName: BuiltValueNullFieldError.checkNotNull(
              fullName, r'VehicleDriverResponse', 'fullName'),
          habitual: BuiltValueNullFieldError.checkNotNull(
              habitual, r'VehicleDriverResponse', 'habitual'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
