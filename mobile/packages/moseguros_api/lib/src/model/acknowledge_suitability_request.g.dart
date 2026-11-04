// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'acknowledge_suitability_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$AcknowledgeSuitabilityRequest extends AcknowledgeSuitabilityRequest {
  @override
  final int version;

  factory _$AcknowledgeSuitabilityRequest(
          [void Function(AcknowledgeSuitabilityRequestBuilder)? updates]) =>
      (AcknowledgeSuitabilityRequestBuilder()..update(updates))._build();

  _$AcknowledgeSuitabilityRequest._({required this.version}) : super._();
  @override
  AcknowledgeSuitabilityRequest rebuild(
          void Function(AcknowledgeSuitabilityRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  AcknowledgeSuitabilityRequestBuilder toBuilder() =>
      AcknowledgeSuitabilityRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is AcknowledgeSuitabilityRequest && version == other.version;
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
    return (newBuiltValueToStringHelper(r'AcknowledgeSuitabilityRequest')
          ..add('version', version))
        .toString();
  }
}

class AcknowledgeSuitabilityRequestBuilder
    implements
        Builder<AcknowledgeSuitabilityRequest,
            AcknowledgeSuitabilityRequestBuilder> {
  _$AcknowledgeSuitabilityRequest? _$v;

  int? _version;
  int? get version => _$this._version;
  set version(int? version) => _$this._version = version;

  AcknowledgeSuitabilityRequestBuilder() {
    AcknowledgeSuitabilityRequest._defaults(this);
  }

  AcknowledgeSuitabilityRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _version = $v.version;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(AcknowledgeSuitabilityRequest other) {
    _$v = other as _$AcknowledgeSuitabilityRequest;
  }

  @override
  void update(void Function(AcknowledgeSuitabilityRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  AcknowledgeSuitabilityRequest build() => _build();

  _$AcknowledgeSuitabilityRequest _build() {
    final _$result = _$v ??
        _$AcknowledgeSuitabilityRequest._(
          version: BuiltValueNullFieldError.checkNotNull(
              version, r'AcknowledgeSuitabilityRequest', 'version'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
