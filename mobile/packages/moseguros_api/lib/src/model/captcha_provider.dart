//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'captcha_provider.g.dart';

class CaptchaProvider extends EnumClass {

  @BuiltValueEnumConst(wireName: r'TURNSTILE')
  static const CaptchaProvider TURNSTILE = _$TURNSTILE;
  @BuiltValueEnumConst(wireName: r'NONE')
  static const CaptchaProvider NONE = _$NONE;

  static Serializer<CaptchaProvider> get serializer => _$captchaProviderSerializer;

  const CaptchaProvider._(String name): super(name);

  static BuiltSet<CaptchaProvider> get values => _$values;
  static CaptchaProvider valueOf(String name) => _$valueOf(name);
}

/// Optionally, enum_class can generate a mixin to go with your enum for use
/// with Angular. It exposes your enum constants as getters. So, if you mix it
/// in to your Dart component class, the values become available to the
/// corresponding Angular template.
///
/// Trigger mixin generation by writing a line like this one next to your enum.
abstract class CaptchaProviderMixin = Object with _$CaptchaProviderMixin;

