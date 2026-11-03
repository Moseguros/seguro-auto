// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'update_product_version_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$UpdateProductVersionRequest extends UpdateProductVersionRequest {
  @override
  final String? changeNotes;
  @override
  final BuiltList<VehicleUsage> usages;
  @override
  final BuiltList<VehicleCategory> vehicleCategories;
  @override
  final int version;

  factory _$UpdateProductVersionRequest(
          [void Function(UpdateProductVersionRequestBuilder)? updates]) =>
      (UpdateProductVersionRequestBuilder()..update(updates))._build();

  _$UpdateProductVersionRequest._(
      {this.changeNotes,
      required this.usages,
      required this.vehicleCategories,
      required this.version})
      : super._();
  @override
  UpdateProductVersionRequest rebuild(
          void Function(UpdateProductVersionRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  UpdateProductVersionRequestBuilder toBuilder() =>
      UpdateProductVersionRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is UpdateProductVersionRequest &&
        changeNotes == other.changeNotes &&
        usages == other.usages &&
        vehicleCategories == other.vehicleCategories &&
        version == other.version;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, changeNotes.hashCode);
    _$hash = $jc(_$hash, usages.hashCode);
    _$hash = $jc(_$hash, vehicleCategories.hashCode);
    _$hash = $jc(_$hash, version.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'UpdateProductVersionRequest')
          ..add('changeNotes', changeNotes)
          ..add('usages', usages)
          ..add('vehicleCategories', vehicleCategories)
          ..add('version', version))
        .toString();
  }
}

class UpdateProductVersionRequestBuilder
    implements
        Builder<UpdateProductVersionRequest,
            UpdateProductVersionRequestBuilder> {
  _$UpdateProductVersionRequest? _$v;

  String? _changeNotes;
  String? get changeNotes => _$this._changeNotes;
  set changeNotes(String? changeNotes) => _$this._changeNotes = changeNotes;

  ListBuilder<VehicleUsage>? _usages;
  ListBuilder<VehicleUsage> get usages =>
      _$this._usages ??= ListBuilder<VehicleUsage>();
  set usages(ListBuilder<VehicleUsage>? usages) => _$this._usages = usages;

  ListBuilder<VehicleCategory>? _vehicleCategories;
  ListBuilder<VehicleCategory> get vehicleCategories =>
      _$this._vehicleCategories ??= ListBuilder<VehicleCategory>();
  set vehicleCategories(ListBuilder<VehicleCategory>? vehicleCategories) =>
      _$this._vehicleCategories = vehicleCategories;

  int? _version;
  int? get version => _$this._version;
  set version(int? version) => _$this._version = version;

  UpdateProductVersionRequestBuilder() {
    UpdateProductVersionRequest._defaults(this);
  }

  UpdateProductVersionRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _changeNotes = $v.changeNotes;
      _usages = $v.usages.toBuilder();
      _vehicleCategories = $v.vehicleCategories.toBuilder();
      _version = $v.version;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(UpdateProductVersionRequest other) {
    _$v = other as _$UpdateProductVersionRequest;
  }

  @override
  void update(void Function(UpdateProductVersionRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  UpdateProductVersionRequest build() => _build();

  _$UpdateProductVersionRequest _build() {
    _$UpdateProductVersionRequest _$result;
    try {
      _$result = _$v ??
          _$UpdateProductVersionRequest._(
            changeNotes: changeNotes,
            usages: usages.build(),
            vehicleCategories: vehicleCategories.build(),
            version: BuiltValueNullFieldError.checkNotNull(
                version, r'UpdateProductVersionRequest', 'version'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'usages';
        usages.build();
        _$failedField = 'vehicleCategories';
        vehicleCategories.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'UpdateProductVersionRequest', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
