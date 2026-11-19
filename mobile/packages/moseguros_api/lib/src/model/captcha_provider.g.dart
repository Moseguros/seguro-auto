// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'captcha_provider.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const CaptchaProvider _$TURNSTILE = const CaptchaProvider._('TURNSTILE');
const CaptchaProvider _$NONE = const CaptchaProvider._('NONE');

CaptchaProvider _$valueOf(String name) {
  switch (name) {
    case 'TURNSTILE':
      return _$TURNSTILE;
    case 'NONE':
      return _$NONE;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<CaptchaProvider> _$values =
    BuiltSet<CaptchaProvider>(const <CaptchaProvider>[
  _$TURNSTILE,
  _$NONE,
]);

class _$CaptchaProviderMeta {
  const _$CaptchaProviderMeta();
  CaptchaProvider get TURNSTILE => _$TURNSTILE;
  CaptchaProvider get NONE => _$NONE;
  CaptchaProvider valueOf(String name) => _$valueOf(name);
  BuiltSet<CaptchaProvider> get values => _$values;
}

abstract class _$CaptchaProviderMixin {
  // ignore: non_constant_identifier_names
  _$CaptchaProviderMeta get CaptchaProvider => const _$CaptchaProviderMeta();
}

Serializer<CaptchaProvider> _$captchaProviderSerializer =
    _$CaptchaProviderSerializer();

class _$CaptchaProviderSerializer
    implements PrimitiveSerializer<CaptchaProvider> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'TURNSTILE': 'TURNSTILE',
    'NONE': 'NONE',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'TURNSTILE': 'TURNSTILE',
    'NONE': 'NONE',
  };

  @override
  final Iterable<Type> types = const <Type>[CaptchaProvider];
  @override
  final String wireName = 'CaptchaProvider';

  @override
  Object serialize(Serializers serializers, CaptchaProvider object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  CaptchaProvider deserialize(Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      CaptchaProvider.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
