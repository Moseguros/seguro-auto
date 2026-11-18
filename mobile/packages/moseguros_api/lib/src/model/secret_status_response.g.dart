// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'secret_status_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$SecretStatusResponse extends SecretStatusResponse {
  @override
  final bool configured;
  @override
  final bool required_;
  @override
  final DateTime? updatedAt;

  factory _$SecretStatusResponse(
          [void Function(SecretStatusResponseBuilder)? updates]) =>
      (SecretStatusResponseBuilder()..update(updates))._build();

  _$SecretStatusResponse._(
      {required this.configured, required this.required_, this.updatedAt})
      : super._();
  @override
  SecretStatusResponse rebuild(
          void Function(SecretStatusResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  SecretStatusResponseBuilder toBuilder() =>
      SecretStatusResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is SecretStatusResponse &&
        configured == other.configured &&
        required_ == other.required_ &&
        updatedAt == other.updatedAt;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, configured.hashCode);
    _$hash = $jc(_$hash, required_.hashCode);
    _$hash = $jc(_$hash, updatedAt.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'SecretStatusResponse')
          ..add('configured', configured)
          ..add('required_', required_)
          ..add('updatedAt', updatedAt))
        .toString();
  }
}

class SecretStatusResponseBuilder
    implements Builder<SecretStatusResponse, SecretStatusResponseBuilder> {
  _$SecretStatusResponse? _$v;

  bool? _configured;
  bool? get configured => _$this._configured;
  set configured(bool? configured) => _$this._configured = configured;

  bool? _required_;
  bool? get required_ => _$this._required_;
  set required_(bool? required_) => _$this._required_ = required_;

  DateTime? _updatedAt;
  DateTime? get updatedAt => _$this._updatedAt;
  set updatedAt(DateTime? updatedAt) => _$this._updatedAt = updatedAt;

  SecretStatusResponseBuilder() {
    SecretStatusResponse._defaults(this);
  }

  SecretStatusResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _configured = $v.configured;
      _required_ = $v.required_;
      _updatedAt = $v.updatedAt;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(SecretStatusResponse other) {
    _$v = other as _$SecretStatusResponse;
  }

  @override
  void update(void Function(SecretStatusResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  SecretStatusResponse build() => _build();

  _$SecretStatusResponse _build() {
    final _$result = _$v ??
        _$SecretStatusResponse._(
          configured: BuiltValueNullFieldError.checkNotNull(
              configured, r'SecretStatusResponse', 'configured'),
          required_: BuiltValueNullFieldError.checkNotNull(
              required_, r'SecretStatusResponse', 'required_'),
          updatedAt: updatedAt,
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
