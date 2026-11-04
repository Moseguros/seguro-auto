// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'kyc_state.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const KycState _$NONE = const KycState._('NONE');
const KycState _$PENDING = const KycState._('PENDING');
const KycState _$VERIFIED = const KycState._('VERIFIED');
const KycState _$REJECTED = const KycState._('REJECTED');
const KycState _$OUTDATED = const KycState._('OUTDATED');

KycState _$valueOf(String name) {
  switch (name) {
    case 'NONE':
      return _$NONE;
    case 'PENDING':
      return _$PENDING;
    case 'VERIFIED':
      return _$VERIFIED;
    case 'REJECTED':
      return _$REJECTED;
    case 'OUTDATED':
      return _$OUTDATED;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<KycState> _$values = BuiltSet<KycState>(const <KycState>[
  _$NONE,
  _$PENDING,
  _$VERIFIED,
  _$REJECTED,
  _$OUTDATED,
]);

class _$KycStateMeta {
  const _$KycStateMeta();
  KycState get NONE => _$NONE;
  KycState get PENDING => _$PENDING;
  KycState get VERIFIED => _$VERIFIED;
  KycState get REJECTED => _$REJECTED;
  KycState get OUTDATED => _$OUTDATED;
  KycState valueOf(String name) => _$valueOf(name);
  BuiltSet<KycState> get values => _$values;
}

abstract class _$KycStateMixin {
  // ignore: non_constant_identifier_names
  _$KycStateMeta get KycState => const _$KycStateMeta();
}

Serializer<KycState> _$kycStateSerializer = _$KycStateSerializer();

class _$KycStateSerializer implements PrimitiveSerializer<KycState> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'NONE': 'NONE',
    'PENDING': 'PENDING',
    'VERIFIED': 'VERIFIED',
    'REJECTED': 'REJECTED',
    'OUTDATED': 'OUTDATED',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'NONE': 'NONE',
    'PENDING': 'PENDING',
    'VERIFIED': 'VERIFIED',
    'REJECTED': 'REJECTED',
    'OUTDATED': 'OUTDATED',
  };

  @override
  final Iterable<Type> types = const <Type>[KycState];
  @override
  final String wireName = 'KycState';

  @override
  Object serialize(Serializers serializers, KycState object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  KycState deserialize(Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      KycState.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
