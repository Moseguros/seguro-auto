// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'notification_category.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const NotificationCategory _$PAYMENT = const NotificationCategory._('PAYMENT');
const NotificationCategory _$POLICY = const NotificationCategory._('POLICY');
const NotificationCategory _$LEGAL = const NotificationCategory._('LEGAL');
const NotificationCategory _$PROPOSAL =
    const NotificationCategory._('PROPOSAL');
const NotificationCategory _$QUOTE = const NotificationCategory._('QUOTE');

NotificationCategory _$valueOf(String name) {
  switch (name) {
    case 'PAYMENT':
      return _$PAYMENT;
    case 'POLICY':
      return _$POLICY;
    case 'LEGAL':
      return _$LEGAL;
    case 'PROPOSAL':
      return _$PROPOSAL;
    case 'QUOTE':
      return _$QUOTE;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<NotificationCategory> _$values =
    BuiltSet<NotificationCategory>(const <NotificationCategory>[
  _$PAYMENT,
  _$POLICY,
  _$LEGAL,
  _$PROPOSAL,
  _$QUOTE,
]);

class _$NotificationCategoryMeta {
  const _$NotificationCategoryMeta();
  NotificationCategory get PAYMENT => _$PAYMENT;
  NotificationCategory get POLICY => _$POLICY;
  NotificationCategory get LEGAL => _$LEGAL;
  NotificationCategory get PROPOSAL => _$PROPOSAL;
  NotificationCategory get QUOTE => _$QUOTE;
  NotificationCategory valueOf(String name) => _$valueOf(name);
  BuiltSet<NotificationCategory> get values => _$values;
}

abstract class _$NotificationCategoryMixin {
  // ignore: non_constant_identifier_names
  _$NotificationCategoryMeta get NotificationCategory =>
      const _$NotificationCategoryMeta();
}

Serializer<NotificationCategory> _$notificationCategorySerializer =
    _$NotificationCategorySerializer();

class _$NotificationCategorySerializer
    implements PrimitiveSerializer<NotificationCategory> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'PAYMENT': 'PAYMENT',
    'POLICY': 'POLICY',
    'LEGAL': 'LEGAL',
    'PROPOSAL': 'PROPOSAL',
    'QUOTE': 'QUOTE',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'PAYMENT': 'PAYMENT',
    'POLICY': 'POLICY',
    'LEGAL': 'LEGAL',
    'PROPOSAL': 'PROPOSAL',
    'QUOTE': 'QUOTE',
  };

  @override
  final Iterable<Type> types = const <Type>[NotificationCategory];
  @override
  final String wireName = 'NotificationCategory';

  @override
  Object serialize(Serializers serializers, NotificationCategory object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  NotificationCategory deserialize(Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      NotificationCategory.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
