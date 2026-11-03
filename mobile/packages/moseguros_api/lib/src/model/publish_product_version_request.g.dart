// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'publish_product_version_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$PublishProductVersionRequest extends PublishProductVersionRequest {
  @override
  final int version;

  factory _$PublishProductVersionRequest(
          [void Function(PublishProductVersionRequestBuilder)? updates]) =>
      (PublishProductVersionRequestBuilder()..update(updates))._build();

  _$PublishProductVersionRequest._({required this.version}) : super._();
  @override
  PublishProductVersionRequest rebuild(
          void Function(PublishProductVersionRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PublishProductVersionRequestBuilder toBuilder() =>
      PublishProductVersionRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PublishProductVersionRequest && version == other.version;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, version.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'PublishProductVersionRequest')
          ..add('version', version))
        .toString();
  }
}

class PublishProductVersionRequestBuilder
    implements
        Builder<PublishProductVersionRequest,
            PublishProductVersionRequestBuilder> {
  _$PublishProductVersionRequest? _$v;

  int? _version;
  int? get version => _$this._version;
  set version(int? version) => _$this._version = version;

  PublishProductVersionRequestBuilder() {
    PublishProductVersionRequest._defaults(this);
  }

  PublishProductVersionRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _version = $v.version;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(PublishProductVersionRequest other) {
    _$v = other as _$PublishProductVersionRequest;
  }

  @override
  void update(void Function(PublishProductVersionRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PublishProductVersionRequest build() => _build();

  _$PublishProductVersionRequest _build() {
    final _$result = _$v ??
        _$PublishProductVersionRequest._(
          version: BuiltValueNullFieldError.checkNotNull(
              version, r'PublishProductVersionRequest', 'version'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
