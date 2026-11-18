// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'payment_method_version_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$PaymentMethodVersionRequest extends PaymentMethodVersionRequest {
  @override
  final int version;

  factory _$PaymentMethodVersionRequest(
          [void Function(PaymentMethodVersionRequestBuilder)? updates]) =>
      (PaymentMethodVersionRequestBuilder()..update(updates))._build();

  _$PaymentMethodVersionRequest._({required this.version}) : super._();
  @override
  PaymentMethodVersionRequest rebuild(
          void Function(PaymentMethodVersionRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PaymentMethodVersionRequestBuilder toBuilder() =>
      PaymentMethodVersionRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PaymentMethodVersionRequest && version == other.version;
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
    return (newBuiltValueToStringHelper(r'PaymentMethodVersionRequest')
          ..add('version', version))
        .toString();
  }
}

class PaymentMethodVersionRequestBuilder
    implements
        Builder<PaymentMethodVersionRequest,
            PaymentMethodVersionRequestBuilder> {
  _$PaymentMethodVersionRequest? _$v;

  int? _version;
  int? get version => _$this._version;
  set version(int? version) => _$this._version = version;

  PaymentMethodVersionRequestBuilder() {
    PaymentMethodVersionRequest._defaults(this);
  }

  PaymentMethodVersionRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _version = $v.version;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(PaymentMethodVersionRequest other) {
    _$v = other as _$PaymentMethodVersionRequest;
  }

  @override
  void update(void Function(PaymentMethodVersionRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PaymentMethodVersionRequest build() => _build();

  _$PaymentMethodVersionRequest _build() {
    final _$result = _$v ??
        _$PaymentMethodVersionRequest._(
          version: BuiltValueNullFieldError.checkNotNull(
              version, r'PaymentMethodVersionRequest', 'version'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
