// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'underwriting_failure.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const UnderwritingFailure _$AUTO_ACCEPT_DISABLED =
    const UnderwritingFailure._('AUTO_ACCEPT_DISABLED');
const UnderwritingFailure _$KYC_REQUIRED =
    const UnderwritingFailure._('KYC_REQUIRED');
const UnderwritingFailure _$DECLARATION_POSITIVE =
    const UnderwritingFailure._('DECLARATION_POSITIVE');
const UnderwritingFailure _$INSPECTION_NOT_APPROVED =
    const UnderwritingFailure._('INSPECTION_NOT_APPROVED');
const UnderwritingFailure _$VEHICLE_VALUE_MISSING =
    const UnderwritingFailure._('VEHICLE_VALUE_MISSING');
const UnderwritingFailure _$VEHICLE_VALUE_ABOVE_LIMIT =
    const UnderwritingFailure._('VEHICLE_VALUE_ABOVE_LIMIT');

UnderwritingFailure _$valueOf(String name) {
  switch (name) {
    case 'AUTO_ACCEPT_DISABLED':
      return _$AUTO_ACCEPT_DISABLED;
    case 'KYC_REQUIRED':
      return _$KYC_REQUIRED;
    case 'DECLARATION_POSITIVE':
      return _$DECLARATION_POSITIVE;
    case 'INSPECTION_NOT_APPROVED':
      return _$INSPECTION_NOT_APPROVED;
    case 'VEHICLE_VALUE_MISSING':
      return _$VEHICLE_VALUE_MISSING;
    case 'VEHICLE_VALUE_ABOVE_LIMIT':
      return _$VEHICLE_VALUE_ABOVE_LIMIT;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<UnderwritingFailure> _$values =
    BuiltSet<UnderwritingFailure>(const <UnderwritingFailure>[
  _$AUTO_ACCEPT_DISABLED,
  _$KYC_REQUIRED,
  _$DECLARATION_POSITIVE,
  _$INSPECTION_NOT_APPROVED,
  _$VEHICLE_VALUE_MISSING,
  _$VEHICLE_VALUE_ABOVE_LIMIT,
]);

class _$UnderwritingFailureMeta {
  const _$UnderwritingFailureMeta();
  UnderwritingFailure get AUTO_ACCEPT_DISABLED => _$AUTO_ACCEPT_DISABLED;
  UnderwritingFailure get KYC_REQUIRED => _$KYC_REQUIRED;
  UnderwritingFailure get DECLARATION_POSITIVE => _$DECLARATION_POSITIVE;
  UnderwritingFailure get INSPECTION_NOT_APPROVED => _$INSPECTION_NOT_APPROVED;
  UnderwritingFailure get VEHICLE_VALUE_MISSING => _$VEHICLE_VALUE_MISSING;
  UnderwritingFailure get VEHICLE_VALUE_ABOVE_LIMIT =>
      _$VEHICLE_VALUE_ABOVE_LIMIT;
  UnderwritingFailure valueOf(String name) => _$valueOf(name);
  BuiltSet<UnderwritingFailure> get values => _$values;
}

abstract class _$UnderwritingFailureMixin {
  // ignore: non_constant_identifier_names
  _$UnderwritingFailureMeta get UnderwritingFailure =>
      const _$UnderwritingFailureMeta();
}

Serializer<UnderwritingFailure> _$underwritingFailureSerializer =
    _$UnderwritingFailureSerializer();

class _$UnderwritingFailureSerializer
    implements PrimitiveSerializer<UnderwritingFailure> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'AUTO_ACCEPT_DISABLED': 'AUTO_ACCEPT_DISABLED',
    'KYC_REQUIRED': 'KYC_REQUIRED',
    'DECLARATION_POSITIVE': 'DECLARATION_POSITIVE',
    'INSPECTION_NOT_APPROVED': 'INSPECTION_NOT_APPROVED',
    'VEHICLE_VALUE_MISSING': 'VEHICLE_VALUE_MISSING',
    'VEHICLE_VALUE_ABOVE_LIMIT': 'VEHICLE_VALUE_ABOVE_LIMIT',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'AUTO_ACCEPT_DISABLED': 'AUTO_ACCEPT_DISABLED',
    'KYC_REQUIRED': 'KYC_REQUIRED',
    'DECLARATION_POSITIVE': 'DECLARATION_POSITIVE',
    'INSPECTION_NOT_APPROVED': 'INSPECTION_NOT_APPROVED',
    'VEHICLE_VALUE_MISSING': 'VEHICLE_VALUE_MISSING',
    'VEHICLE_VALUE_ABOVE_LIMIT': 'VEHICLE_VALUE_ABOVE_LIMIT',
  };

  @override
  final Iterable<Type> types = const <Type>[UnderwritingFailure];
  @override
  final String wireName = 'UnderwritingFailure';

  @override
  Object serialize(Serializers serializers, UnderwritingFailure object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  UnderwritingFailure deserialize(Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      UnderwritingFailure.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
