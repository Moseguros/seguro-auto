// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'inspection_status.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const InspectionStatus _$PENDING = const InspectionStatus._('PENDING');
const InspectionStatus _$SUBMITTED = const InspectionStatus._('SUBMITTED');
const InspectionStatus _$APPROVED = const InspectionStatus._('APPROVED');
const InspectionStatus _$REJECTED = const InspectionStatus._('REJECTED');

InspectionStatus _$valueOf(String name) {
  switch (name) {
    case 'PENDING':
      return _$PENDING;
    case 'SUBMITTED':
      return _$SUBMITTED;
    case 'APPROVED':
      return _$APPROVED;
    case 'REJECTED':
      return _$REJECTED;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<InspectionStatus> _$values =
    BuiltSet<InspectionStatus>(const <InspectionStatus>[
  _$PENDING,
  _$SUBMITTED,
  _$APPROVED,
  _$REJECTED,
]);

class _$InspectionStatusMeta {
  const _$InspectionStatusMeta();
  InspectionStatus get PENDING => _$PENDING;
  InspectionStatus get SUBMITTED => _$SUBMITTED;
  InspectionStatus get APPROVED => _$APPROVED;
  InspectionStatus get REJECTED => _$REJECTED;
  InspectionStatus valueOf(String name) => _$valueOf(name);
  BuiltSet<InspectionStatus> get values => _$values;
}

abstract class _$InspectionStatusMixin {
  // ignore: non_constant_identifier_names
  _$InspectionStatusMeta get InspectionStatus => const _$InspectionStatusMeta();
}

Serializer<InspectionStatus> _$inspectionStatusSerializer =
    _$InspectionStatusSerializer();

class _$InspectionStatusSerializer
    implements PrimitiveSerializer<InspectionStatus> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'PENDING': 'PENDING',
    'SUBMITTED': 'SUBMITTED',
    'APPROVED': 'APPROVED',
    'REJECTED': 'REJECTED',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'PENDING': 'PENDING',
    'SUBMITTED': 'SUBMITTED',
    'APPROVED': 'APPROVED',
    'REJECTED': 'REJECTED',
  };

  @override
  final Iterable<Type> types = const <Type>[InspectionStatus];
  @override
  final String wireName = 'InspectionStatus';

  @override
  Object serialize(Serializers serializers, InspectionStatus object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  InspectionStatus deserialize(Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      InspectionStatus.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
