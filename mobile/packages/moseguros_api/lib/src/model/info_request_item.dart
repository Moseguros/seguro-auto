//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'info_request_item.g.dart';

class InfoRequestItem extends EnumClass {

  @BuiltValueEnumConst(wireName: r'DECLARATIONS')
  static const InfoRequestItem DECLARATIONS = _$DECLARATIONS;
  @BuiltValueEnumConst(wireName: r'SUITABILITY')
  static const InfoRequestItem SUITABILITY = _$SUITABILITY;
  @BuiltValueEnumConst(wireName: r'INSPECTION')
  static const InfoRequestItem INSPECTION = _$INSPECTION;
  @BuiltValueEnumConst(wireName: r'KYC')
  static const InfoRequestItem KYC = _$KYC;
  @BuiltValueEnumConst(wireName: r'POLICYHOLDER')
  static const InfoRequestItem POLICYHOLDER = _$POLICYHOLDER;
  @BuiltValueEnumConst(wireName: r'OTHER')
  static const InfoRequestItem OTHER = _$OTHER;

  static Serializer<InfoRequestItem> get serializer => _$infoRequestItemSerializer;

  const InfoRequestItem._(String name): super(name);

  static BuiltSet<InfoRequestItem> get values => _$values;
  static InfoRequestItem valueOf(String name) => _$valueOf(name);
}

/// Optionally, enum_class can generate a mixin to go with your enum for use
/// with Angular. It exposes your enum constants as getters. So, if you mix it
/// in to your Dart component class, the values become available to the
/// corresponding Angular template.
///
/// Trigger mixin generation by writing a line like this one next to your enum.
abstract class InfoRequestItemMixin = Object with _$InfoRequestItemMixin;

