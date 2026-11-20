//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'notification_category.g.dart';

class NotificationCategory extends EnumClass {

  @BuiltValueEnumConst(wireName: r'PAYMENT')
  static const NotificationCategory PAYMENT = _$PAYMENT;
  @BuiltValueEnumConst(wireName: r'POLICY')
  static const NotificationCategory POLICY = _$POLICY;
  @BuiltValueEnumConst(wireName: r'LEGAL')
  static const NotificationCategory LEGAL = _$LEGAL;
  @BuiltValueEnumConst(wireName: r'PROPOSAL')
  static const NotificationCategory PROPOSAL = _$PROPOSAL;
  @BuiltValueEnumConst(wireName: r'QUOTE')
  static const NotificationCategory QUOTE = _$QUOTE;

  static Serializer<NotificationCategory> get serializer => _$notificationCategorySerializer;

  const NotificationCategory._(String name): super(name);

  static BuiltSet<NotificationCategory> get values => _$values;
  static NotificationCategory valueOf(String name) => _$valueOf(name);
}

/// Optionally, enum_class can generate a mixin to go with your enum for use
/// with Angular. It exposes your enum constants as getters. So, if you mix it
/// in to your Dart component class, the values become available to the
/// corresponding Angular template.
///
/// Trigger mixin generation by writing a line like this one next to your enum.
abstract class NotificationCategoryMixin = Object with _$NotificationCategoryMixin;

