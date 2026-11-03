// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'update_vehicle_drivers_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$UpdateVehicleDriversRequest extends UpdateVehicleDriversRequest {
  @override
  final BuiltList<VehicleDriverAssignment> drivers;
  @override
  final int version;

  factory _$UpdateVehicleDriversRequest(
          [void Function(UpdateVehicleDriversRequestBuilder)? updates]) =>
      (UpdateVehicleDriversRequestBuilder()..update(updates))._build();

  _$UpdateVehicleDriversRequest._(
      {required this.drivers, required this.version})
      : super._();
  @override
  UpdateVehicleDriversRequest rebuild(
          void Function(UpdateVehicleDriversRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  UpdateVehicleDriversRequestBuilder toBuilder() =>
      UpdateVehicleDriversRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is UpdateVehicleDriversRequest &&
        drivers == other.drivers &&
        version == other.version;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, drivers.hashCode);
    _$hash = $jc(_$hash, version.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'UpdateVehicleDriversRequest')
          ..add('drivers', drivers)
          ..add('version', version))
        .toString();
  }
}

class UpdateVehicleDriversRequestBuilder
    implements
        Builder<UpdateVehicleDriversRequest,
            UpdateVehicleDriversRequestBuilder> {
  _$UpdateVehicleDriversRequest? _$v;

  ListBuilder<VehicleDriverAssignment>? _drivers;
  ListBuilder<VehicleDriverAssignment> get drivers =>
      _$this._drivers ??= ListBuilder<VehicleDriverAssignment>();
  set drivers(ListBuilder<VehicleDriverAssignment>? drivers) =>
      _$this._drivers = drivers;

  int? _version;
  int? get version => _$this._version;
  set version(int? version) => _$this._version = version;

  UpdateVehicleDriversRequestBuilder() {
    UpdateVehicleDriversRequest._defaults(this);
  }

  UpdateVehicleDriversRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _drivers = $v.drivers.toBuilder();
      _version = $v.version;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(UpdateVehicleDriversRequest other) {
    _$v = other as _$UpdateVehicleDriversRequest;
  }

  @override
  void update(void Function(UpdateVehicleDriversRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  UpdateVehicleDriversRequest build() => _build();

  _$UpdateVehicleDriversRequest _build() {
    _$UpdateVehicleDriversRequest _$result;
    try {
      _$result = _$v ??
          _$UpdateVehicleDriversRequest._(
            drivers: drivers.build(),
            version: BuiltValueNullFieldError.checkNotNull(
                version, r'UpdateVehicleDriversRequest', 'version'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'drivers';
        drivers.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'UpdateVehicleDriversRequest', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
