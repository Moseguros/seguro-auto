// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'info_request_item.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const InfoRequestItem _$DECLARATIONS = const InfoRequestItem._('DECLARATIONS');
const InfoRequestItem _$SUITABILITY = const InfoRequestItem._('SUITABILITY');
const InfoRequestItem _$INSPECTION = const InfoRequestItem._('INSPECTION');
const InfoRequestItem _$KYC = const InfoRequestItem._('KYC');
const InfoRequestItem _$POLICYHOLDER = const InfoRequestItem._('POLICYHOLDER');
const InfoRequestItem _$OTHER = const InfoRequestItem._('OTHER');

InfoRequestItem _$valueOf(String name) {
  switch (name) {
    case 'DECLARATIONS':
      return _$DECLARATIONS;
    case 'SUITABILITY':
      return _$SUITABILITY;
    case 'INSPECTION':
      return _$INSPECTION;
    case 'KYC':
      return _$KYC;
    case 'POLICYHOLDER':
      return _$POLICYHOLDER;
    case 'OTHER':
      return _$OTHER;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<InfoRequestItem> _$values =
    BuiltSet<InfoRequestItem>(const <InfoRequestItem>[
  _$DECLARATIONS,
  _$SUITABILITY,
  _$INSPECTION,
  _$KYC,
  _$POLICYHOLDER,
  _$OTHER,
]);

class _$InfoRequestItemMeta {
  const _$InfoRequestItemMeta();
  InfoRequestItem get DECLARATIONS => _$DECLARATIONS;
  InfoRequestItem get SUITABILITY => _$SUITABILITY;
  InfoRequestItem get INSPECTION => _$INSPECTION;
  InfoRequestItem get KYC => _$KYC;
  InfoRequestItem get POLICYHOLDER => _$POLICYHOLDER;
  InfoRequestItem get OTHER => _$OTHER;
  InfoRequestItem valueOf(String name) => _$valueOf(name);
  BuiltSet<InfoRequestItem> get values => _$values;
}

abstract class _$InfoRequestItemMixin {
  // ignore: non_constant_identifier_names
  _$InfoRequestItemMeta get InfoRequestItem => const _$InfoRequestItemMeta();
}

Serializer<InfoRequestItem> _$infoRequestItemSerializer =
    _$InfoRequestItemSerializer();

class _$InfoRequestItemSerializer
    implements PrimitiveSerializer<InfoRequestItem> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'DECLARATIONS': 'DECLARATIONS',
    'SUITABILITY': 'SUITABILITY',
    'INSPECTION': 'INSPECTION',
    'KYC': 'KYC',
    'POLICYHOLDER': 'POLICYHOLDER',
    'OTHER': 'OTHER',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'DECLARATIONS': 'DECLARATIONS',
    'SUITABILITY': 'SUITABILITY',
    'INSPECTION': 'INSPECTION',
    'KYC': 'KYC',
    'POLICYHOLDER': 'POLICYHOLDER',
    'OTHER': 'OTHER',
  };

  @override
  final Iterable<Type> types = const <Type>[InfoRequestItem];
  @override
  final String wireName = 'InfoRequestItem';

  @override
  Object serialize(Serializers serializers, InfoRequestItem object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  InfoRequestItem deserialize(Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      InfoRequestItem.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
