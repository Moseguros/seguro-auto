// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'payment_method_test_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const PaymentMethodTestResponseResultEnum
    _$paymentMethodTestResponseResultEnum_OK =
    const PaymentMethodTestResponseResultEnum._('OK');
const PaymentMethodTestResponseResultEnum
    _$paymentMethodTestResponseResultEnum_FAILED =
    const PaymentMethodTestResponseResultEnum._('FAILED');
const PaymentMethodTestResponseResultEnum
    _$paymentMethodTestResponseResultEnum_NOT_SUPPORTED =
    const PaymentMethodTestResponseResultEnum._('NOT_SUPPORTED');

PaymentMethodTestResponseResultEnum
    _$paymentMethodTestResponseResultEnumValueOf(String name) {
  switch (name) {
    case 'OK':
      return _$paymentMethodTestResponseResultEnum_OK;
    case 'FAILED':
      return _$paymentMethodTestResponseResultEnum_FAILED;
    case 'NOT_SUPPORTED':
      return _$paymentMethodTestResponseResultEnum_NOT_SUPPORTED;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<PaymentMethodTestResponseResultEnum>
    _$paymentMethodTestResponseResultEnumValues = BuiltSet<
        PaymentMethodTestResponseResultEnum>(const <PaymentMethodTestResponseResultEnum>[
  _$paymentMethodTestResponseResultEnum_OK,
  _$paymentMethodTestResponseResultEnum_FAILED,
  _$paymentMethodTestResponseResultEnum_NOT_SUPPORTED,
]);

Serializer<PaymentMethodTestResponseResultEnum>
    _$paymentMethodTestResponseResultEnumSerializer =
    _$PaymentMethodTestResponseResultEnumSerializer();

class _$PaymentMethodTestResponseResultEnumSerializer
    implements PrimitiveSerializer<PaymentMethodTestResponseResultEnum> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'OK': 'OK',
    'FAILED': 'FAILED',
    'NOT_SUPPORTED': 'NOT_SUPPORTED',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'OK': 'OK',
    'FAILED': 'FAILED',
    'NOT_SUPPORTED': 'NOT_SUPPORTED',
  };

  @override
  final Iterable<Type> types = const <Type>[
    PaymentMethodTestResponseResultEnum
  ];
  @override
  final String wireName = 'PaymentMethodTestResponseResultEnum';

  @override
  Object serialize(
          Serializers serializers, PaymentMethodTestResponseResultEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  PaymentMethodTestResponseResultEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      PaymentMethodTestResponseResultEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$PaymentMethodTestResponse extends PaymentMethodTestResponse {
  @override
  final String? message;
  @override
  final PaymentMethodTestResponseResultEnum result;
  @override
  final DateTime testedAt;

  factory _$PaymentMethodTestResponse(
          [void Function(PaymentMethodTestResponseBuilder)? updates]) =>
      (PaymentMethodTestResponseBuilder()..update(updates))._build();

  _$PaymentMethodTestResponse._(
      {this.message, required this.result, required this.testedAt})
      : super._();
  @override
  PaymentMethodTestResponse rebuild(
          void Function(PaymentMethodTestResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PaymentMethodTestResponseBuilder toBuilder() =>
      PaymentMethodTestResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PaymentMethodTestResponse &&
        message == other.message &&
        result == other.result &&
        testedAt == other.testedAt;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, message.hashCode);
    _$hash = $jc(_$hash, result.hashCode);
    _$hash = $jc(_$hash, testedAt.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'PaymentMethodTestResponse')
          ..add('message', message)
          ..add('result', result)
          ..add('testedAt', testedAt))
        .toString();
  }
}

class PaymentMethodTestResponseBuilder
    implements
        Builder<PaymentMethodTestResponse, PaymentMethodTestResponseBuilder> {
  _$PaymentMethodTestResponse? _$v;

  String? _message;
  String? get message => _$this._message;
  set message(String? message) => _$this._message = message;

  PaymentMethodTestResponseResultEnum? _result;
  PaymentMethodTestResponseResultEnum? get result => _$this._result;
  set result(PaymentMethodTestResponseResultEnum? result) =>
      _$this._result = result;

  DateTime? _testedAt;
  DateTime? get testedAt => _$this._testedAt;
  set testedAt(DateTime? testedAt) => _$this._testedAt = testedAt;

  PaymentMethodTestResponseBuilder() {
    PaymentMethodTestResponse._defaults(this);
  }

  PaymentMethodTestResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _message = $v.message;
      _result = $v.result;
      _testedAt = $v.testedAt;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(PaymentMethodTestResponse other) {
    _$v = other as _$PaymentMethodTestResponse;
  }

  @override
  void update(void Function(PaymentMethodTestResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PaymentMethodTestResponse build() => _build();

  _$PaymentMethodTestResponse _build() {
    final _$result = _$v ??
        _$PaymentMethodTestResponse._(
          message: message,
          result: BuiltValueNullFieldError.checkNotNull(
              result, r'PaymentMethodTestResponse', 'result'),
          testedAt: BuiltValueNullFieldError.checkNotNull(
              testedAt, r'PaymentMethodTestResponse', 'testedAt'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
