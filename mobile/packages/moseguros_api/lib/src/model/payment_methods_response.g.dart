// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'payment_methods_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$PaymentMethodsResponse extends PaymentMethodsResponse {
  @override
  final BuiltList<PaymentMethodResponse>? methods;

  factory _$PaymentMethodsResponse(
          [void Function(PaymentMethodsResponseBuilder)? updates]) =>
      (PaymentMethodsResponseBuilder()..update(updates))._build();

  _$PaymentMethodsResponse._({this.methods}) : super._();
  @override
  PaymentMethodsResponse rebuild(
          void Function(PaymentMethodsResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PaymentMethodsResponseBuilder toBuilder() =>
      PaymentMethodsResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PaymentMethodsResponse && methods == other.methods;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, methods.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'PaymentMethodsResponse')
          ..add('methods', methods))
        .toString();
  }
}

class PaymentMethodsResponseBuilder
    implements Builder<PaymentMethodsResponse, PaymentMethodsResponseBuilder> {
  _$PaymentMethodsResponse? _$v;

  ListBuilder<PaymentMethodResponse>? _methods;
  ListBuilder<PaymentMethodResponse> get methods =>
      _$this._methods ??= ListBuilder<PaymentMethodResponse>();
  set methods(ListBuilder<PaymentMethodResponse>? methods) =>
      _$this._methods = methods;

  PaymentMethodsResponseBuilder() {
    PaymentMethodsResponse._defaults(this);
  }

  PaymentMethodsResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _methods = $v.methods?.toBuilder();
      _$v = null;
    }
    return this;
  }

  @override
  void replace(PaymentMethodsResponse other) {
    _$v = other as _$PaymentMethodsResponse;
  }

  @override
  void update(void Function(PaymentMethodsResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PaymentMethodsResponse build() => _build();

  _$PaymentMethodsResponse _build() {
    _$PaymentMethodsResponse _$result;
    try {
      _$result = _$v ??
          _$PaymentMethodsResponse._(
            methods: _methods?.build(),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'methods';
        _methods?.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'PaymentMethodsResponse', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
