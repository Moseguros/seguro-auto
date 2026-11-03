// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'public_platform_setting_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const PublicPlatformSettingResponseValueTypeEnum
    _$publicPlatformSettingResponseValueTypeEnum_MONEY =
    const PublicPlatformSettingResponseValueTypeEnum._('MONEY');
const PublicPlatformSettingResponseValueTypeEnum
    _$publicPlatformSettingResponseValueTypeEnum_INTEGER =
    const PublicPlatformSettingResponseValueTypeEnum._('INTEGER');
const PublicPlatformSettingResponseValueTypeEnum
    _$publicPlatformSettingResponseValueTypeEnum_BOOLEAN =
    const PublicPlatformSettingResponseValueTypeEnum._('BOOLEAN');
const PublicPlatformSettingResponseValueTypeEnum
    _$publicPlatformSettingResponseValueTypeEnum_STRING =
    const PublicPlatformSettingResponseValueTypeEnum._('STRING');

PublicPlatformSettingResponseValueTypeEnum
    _$publicPlatformSettingResponseValueTypeEnumValueOf(String name) {
  switch (name) {
    case 'MONEY':
      return _$publicPlatformSettingResponseValueTypeEnum_MONEY;
    case 'INTEGER':
      return _$publicPlatformSettingResponseValueTypeEnum_INTEGER;
    case 'BOOLEAN':
      return _$publicPlatformSettingResponseValueTypeEnum_BOOLEAN;
    case 'STRING':
      return _$publicPlatformSettingResponseValueTypeEnum_STRING;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<PublicPlatformSettingResponseValueTypeEnum>
    _$publicPlatformSettingResponseValueTypeEnumValues = BuiltSet<
        PublicPlatformSettingResponseValueTypeEnum>(const <PublicPlatformSettingResponseValueTypeEnum>[
  _$publicPlatformSettingResponseValueTypeEnum_MONEY,
  _$publicPlatformSettingResponseValueTypeEnum_INTEGER,
  _$publicPlatformSettingResponseValueTypeEnum_BOOLEAN,
  _$publicPlatformSettingResponseValueTypeEnum_STRING,
]);

Serializer<PublicPlatformSettingResponseValueTypeEnum>
    _$publicPlatformSettingResponseValueTypeEnumSerializer =
    _$PublicPlatformSettingResponseValueTypeEnumSerializer();

class _$PublicPlatformSettingResponseValueTypeEnumSerializer
    implements PrimitiveSerializer<PublicPlatformSettingResponseValueTypeEnum> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'MONEY': 'MONEY',
    'INTEGER': 'INTEGER',
    'BOOLEAN': 'BOOLEAN',
    'STRING': 'STRING',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'MONEY': 'MONEY',
    'INTEGER': 'INTEGER',
    'BOOLEAN': 'BOOLEAN',
    'STRING': 'STRING',
  };

  @override
  final Iterable<Type> types = const <Type>[
    PublicPlatformSettingResponseValueTypeEnum
  ];
  @override
  final String wireName = 'PublicPlatformSettingResponseValueTypeEnum';

  @override
  Object serialize(Serializers serializers,
          PublicPlatformSettingResponseValueTypeEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  PublicPlatformSettingResponseValueTypeEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      PublicPlatformSettingResponseValueTypeEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$PublicPlatformSettingResponse extends PublicPlatformSettingResponse {
  @override
  final String key;
  @override
  final String value;
  @override
  final PublicPlatformSettingResponseValueTypeEnum valueType;

  factory _$PublicPlatformSettingResponse(
          [void Function(PublicPlatformSettingResponseBuilder)? updates]) =>
      (PublicPlatformSettingResponseBuilder()..update(updates))._build();

  _$PublicPlatformSettingResponse._(
      {required this.key, required this.value, required this.valueType})
      : super._();
  @override
  PublicPlatformSettingResponse rebuild(
          void Function(PublicPlatformSettingResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PublicPlatformSettingResponseBuilder toBuilder() =>
      PublicPlatformSettingResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PublicPlatformSettingResponse &&
        key == other.key &&
        value == other.value &&
        valueType == other.valueType;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, key.hashCode);
    _$hash = $jc(_$hash, value.hashCode);
    _$hash = $jc(_$hash, valueType.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'PublicPlatformSettingResponse')
          ..add('key', key)
          ..add('value', value)
          ..add('valueType', valueType))
        .toString();
  }
}

class PublicPlatformSettingResponseBuilder
    implements
        Builder<PublicPlatformSettingResponse,
            PublicPlatformSettingResponseBuilder> {
  _$PublicPlatformSettingResponse? _$v;

  String? _key;
  String? get key => _$this._key;
  set key(String? key) => _$this._key = key;

  String? _value;
  String? get value => _$this._value;
  set value(String? value) => _$this._value = value;

  PublicPlatformSettingResponseValueTypeEnum? _valueType;
  PublicPlatformSettingResponseValueTypeEnum? get valueType =>
      _$this._valueType;
  set valueType(PublicPlatformSettingResponseValueTypeEnum? valueType) =>
      _$this._valueType = valueType;

  PublicPlatformSettingResponseBuilder() {
    PublicPlatformSettingResponse._defaults(this);
  }

  PublicPlatformSettingResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _key = $v.key;
      _value = $v.value;
      _valueType = $v.valueType;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(PublicPlatformSettingResponse other) {
    _$v = other as _$PublicPlatformSettingResponse;
  }

  @override
  void update(void Function(PublicPlatformSettingResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PublicPlatformSettingResponse build() => _build();

  _$PublicPlatformSettingResponse _build() {
    final _$result = _$v ??
        _$PublicPlatformSettingResponse._(
          key: BuiltValueNullFieldError.checkNotNull(
              key, r'PublicPlatformSettingResponse', 'key'),
          value: BuiltValueNullFieldError.checkNotNull(
              value, r'PublicPlatformSettingResponse', 'value'),
          valueType: BuiltValueNullFieldError.checkNotNull(
              valueType, r'PublicPlatformSettingResponse', 'valueType'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
