// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'failure_code.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const FailureCode _$CUSTOMER_CANCELLED =
    const FailureCode._('CUSTOMER_CANCELLED');
const FailureCode _$INSUFFICIENT_BALANCE =
    const FailureCode._('INSUFFICIENT_BALANCE');
const FailureCode _$INVALID_MSISDN = const FailureCode._('INVALID_MSISDN');
const FailureCode _$PROVIDER_REJECTED =
    const FailureCode._('PROVIDER_REJECTED');
const FailureCode _$PROVIDER_CONFIG_ERROR =
    const FailureCode._('PROVIDER_CONFIG_ERROR');
const FailureCode _$PROVIDER_UNAVAILABLE =
    const FailureCode._('PROVIDER_UNAVAILABLE');
const FailureCode _$TIMEOUT_EXPIRED = const FailureCode._('TIMEOUT_EXPIRED');
const FailureCode _$EXPIRED = const FailureCode._('EXPIRED');
const FailureCode _$SUPERSEDED = const FailureCode._('SUPERSEDED');
const FailureCode _$MANUAL = const FailureCode._('MANUAL');

FailureCode _$valueOf(String name) {
  switch (name) {
    case 'CUSTOMER_CANCELLED':
      return _$CUSTOMER_CANCELLED;
    case 'INSUFFICIENT_BALANCE':
      return _$INSUFFICIENT_BALANCE;
    case 'INVALID_MSISDN':
      return _$INVALID_MSISDN;
    case 'PROVIDER_REJECTED':
      return _$PROVIDER_REJECTED;
    case 'PROVIDER_CONFIG_ERROR':
      return _$PROVIDER_CONFIG_ERROR;
    case 'PROVIDER_UNAVAILABLE':
      return _$PROVIDER_UNAVAILABLE;
    case 'TIMEOUT_EXPIRED':
      return _$TIMEOUT_EXPIRED;
    case 'EXPIRED':
      return _$EXPIRED;
    case 'SUPERSEDED':
      return _$SUPERSEDED;
    case 'MANUAL':
      return _$MANUAL;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<FailureCode> _$values =
    BuiltSet<FailureCode>(const <FailureCode>[
  _$CUSTOMER_CANCELLED,
  _$INSUFFICIENT_BALANCE,
  _$INVALID_MSISDN,
  _$PROVIDER_REJECTED,
  _$PROVIDER_CONFIG_ERROR,
  _$PROVIDER_UNAVAILABLE,
  _$TIMEOUT_EXPIRED,
  _$EXPIRED,
  _$SUPERSEDED,
  _$MANUAL,
]);

class _$FailureCodeMeta {
  const _$FailureCodeMeta();
  FailureCode get CUSTOMER_CANCELLED => _$CUSTOMER_CANCELLED;
  FailureCode get INSUFFICIENT_BALANCE => _$INSUFFICIENT_BALANCE;
  FailureCode get INVALID_MSISDN => _$INVALID_MSISDN;
  FailureCode get PROVIDER_REJECTED => _$PROVIDER_REJECTED;
  FailureCode get PROVIDER_CONFIG_ERROR => _$PROVIDER_CONFIG_ERROR;
  FailureCode get PROVIDER_UNAVAILABLE => _$PROVIDER_UNAVAILABLE;
  FailureCode get TIMEOUT_EXPIRED => _$TIMEOUT_EXPIRED;
  FailureCode get EXPIRED => _$EXPIRED;
  FailureCode get SUPERSEDED => _$SUPERSEDED;
  FailureCode get MANUAL => _$MANUAL;
  FailureCode valueOf(String name) => _$valueOf(name);
  BuiltSet<FailureCode> get values => _$values;
}

abstract class _$FailureCodeMixin {
  // ignore: non_constant_identifier_names
  _$FailureCodeMeta get FailureCode => const _$FailureCodeMeta();
}

Serializer<FailureCode> _$failureCodeSerializer = _$FailureCodeSerializer();

class _$FailureCodeSerializer implements PrimitiveSerializer<FailureCode> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'CUSTOMER_CANCELLED': 'CUSTOMER_CANCELLED',
    'INSUFFICIENT_BALANCE': 'INSUFFICIENT_BALANCE',
    'INVALID_MSISDN': 'INVALID_MSISDN',
    'PROVIDER_REJECTED': 'PROVIDER_REJECTED',
    'PROVIDER_CONFIG_ERROR': 'PROVIDER_CONFIG_ERROR',
    'PROVIDER_UNAVAILABLE': 'PROVIDER_UNAVAILABLE',
    'TIMEOUT_EXPIRED': 'TIMEOUT_EXPIRED',
    'EXPIRED': 'EXPIRED',
    'SUPERSEDED': 'SUPERSEDED',
    'MANUAL': 'MANUAL',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'CUSTOMER_CANCELLED': 'CUSTOMER_CANCELLED',
    'INSUFFICIENT_BALANCE': 'INSUFFICIENT_BALANCE',
    'INVALID_MSISDN': 'INVALID_MSISDN',
    'PROVIDER_REJECTED': 'PROVIDER_REJECTED',
    'PROVIDER_CONFIG_ERROR': 'PROVIDER_CONFIG_ERROR',
    'PROVIDER_UNAVAILABLE': 'PROVIDER_UNAVAILABLE',
    'TIMEOUT_EXPIRED': 'TIMEOUT_EXPIRED',
    'EXPIRED': 'EXPIRED',
    'SUPERSEDED': 'SUPERSEDED',
    'MANUAL': 'MANUAL',
  };

  @override
  final Iterable<Type> types = const <Type>[FailureCode];
  @override
  final String wireName = 'FailureCode';

  @override
  Object serialize(Serializers serializers, FailureCode object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  FailureCode deserialize(Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      FailureCode.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
