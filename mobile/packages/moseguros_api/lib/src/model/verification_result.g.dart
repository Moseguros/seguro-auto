// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'verification_result.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const VerificationResult _$VALID = const VerificationResult._('VALID');
const VerificationResult _$NOT_YET_VALID =
    const VerificationResult._('NOT_YET_VALID');
const VerificationResult _$EXPIRED = const VerificationResult._('EXPIRED');
const VerificationResult _$SUSPENDED = const VerificationResult._('SUSPENDED');
const VerificationResult _$CANCELLED = const VerificationResult._('CANCELLED');
const VerificationResult _$NOT_FOUND = const VerificationResult._('NOT_FOUND');

VerificationResult _$valueOf(String name) {
  switch (name) {
    case 'VALID':
      return _$VALID;
    case 'NOT_YET_VALID':
      return _$NOT_YET_VALID;
    case 'EXPIRED':
      return _$EXPIRED;
    case 'SUSPENDED':
      return _$SUSPENDED;
    case 'CANCELLED':
      return _$CANCELLED;
    case 'NOT_FOUND':
      return _$NOT_FOUND;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<VerificationResult> _$values =
    BuiltSet<VerificationResult>(const <VerificationResult>[
  _$VALID,
  _$NOT_YET_VALID,
  _$EXPIRED,
  _$SUSPENDED,
  _$CANCELLED,
  _$NOT_FOUND,
]);

class _$VerificationResultMeta {
  const _$VerificationResultMeta();
  VerificationResult get VALID => _$VALID;
  VerificationResult get NOT_YET_VALID => _$NOT_YET_VALID;
  VerificationResult get EXPIRED => _$EXPIRED;
  VerificationResult get SUSPENDED => _$SUSPENDED;
  VerificationResult get CANCELLED => _$CANCELLED;
  VerificationResult get NOT_FOUND => _$NOT_FOUND;
  VerificationResult valueOf(String name) => _$valueOf(name);
  BuiltSet<VerificationResult> get values => _$values;
}

abstract class _$VerificationResultMixin {
  // ignore: non_constant_identifier_names
  _$VerificationResultMeta get VerificationResult =>
      const _$VerificationResultMeta();
}

Serializer<VerificationResult> _$verificationResultSerializer =
    _$VerificationResultSerializer();

class _$VerificationResultSerializer
    implements PrimitiveSerializer<VerificationResult> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'VALID': 'VALID',
    'NOT_YET_VALID': 'NOT_YET_VALID',
    'EXPIRED': 'EXPIRED',
    'SUSPENDED': 'SUSPENDED',
    'CANCELLED': 'CANCELLED',
    'NOT_FOUND': 'NOT_FOUND',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'VALID': 'VALID',
    'NOT_YET_VALID': 'NOT_YET_VALID',
    'EXPIRED': 'EXPIRED',
    'SUSPENDED': 'SUSPENDED',
    'CANCELLED': 'CANCELLED',
    'NOT_FOUND': 'NOT_FOUND',
  };

  @override
  final Iterable<Type> types = const <Type>[VerificationResult];
  @override
  final String wireName = 'VerificationResult';

  @override
  Object serialize(Serializers serializers, VerificationResult object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  VerificationResult deserialize(Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      VerificationResult.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
