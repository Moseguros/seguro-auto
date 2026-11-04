// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'update_insurer_rating_settings_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$UpdateInsurerRatingSettingsRequest
    extends UpdateInsurerRatingSettingsRequest {
  @override
  final BuiltList<Charge> charges;
  @override
  final int version;

  factory _$UpdateInsurerRatingSettingsRequest(
          [void Function(UpdateInsurerRatingSettingsRequestBuilder)?
              updates]) =>
      (UpdateInsurerRatingSettingsRequestBuilder()..update(updates))._build();

  _$UpdateInsurerRatingSettingsRequest._(
      {required this.charges, required this.version})
      : super._();
  @override
  UpdateInsurerRatingSettingsRequest rebuild(
          void Function(UpdateInsurerRatingSettingsRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  UpdateInsurerRatingSettingsRequestBuilder toBuilder() =>
      UpdateInsurerRatingSettingsRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is UpdateInsurerRatingSettingsRequest &&
        charges == other.charges &&
        version == other.version;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, charges.hashCode);
    _$hash = $jc(_$hash, version.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'UpdateInsurerRatingSettingsRequest')
          ..add('charges', charges)
          ..add('version', version))
        .toString();
  }
}

class UpdateInsurerRatingSettingsRequestBuilder
    implements
        Builder<UpdateInsurerRatingSettingsRequest,
            UpdateInsurerRatingSettingsRequestBuilder> {
  _$UpdateInsurerRatingSettingsRequest? _$v;

  ListBuilder<Charge>? _charges;
  ListBuilder<Charge> get charges => _$this._charges ??= ListBuilder<Charge>();
  set charges(ListBuilder<Charge>? charges) => _$this._charges = charges;

  int? _version;
  int? get version => _$this._version;
  set version(int? version) => _$this._version = version;

  UpdateInsurerRatingSettingsRequestBuilder() {
    UpdateInsurerRatingSettingsRequest._defaults(this);
  }

  UpdateInsurerRatingSettingsRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _charges = $v.charges.toBuilder();
      _version = $v.version;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(UpdateInsurerRatingSettingsRequest other) {
    _$v = other as _$UpdateInsurerRatingSettingsRequest;
  }

  @override
  void update(
      void Function(UpdateInsurerRatingSettingsRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  UpdateInsurerRatingSettingsRequest build() => _build();

  _$UpdateInsurerRatingSettingsRequest _build() {
    _$UpdateInsurerRatingSettingsRequest _$result;
    try {
      _$result = _$v ??
          _$UpdateInsurerRatingSettingsRequest._(
            charges: charges.build(),
            version: BuiltValueNullFieldError.checkNotNull(
                version, r'UpdateInsurerRatingSettingsRequest', 'version'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'charges';
        charges.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'UpdateInsurerRatingSettingsRequest', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
