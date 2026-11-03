// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'platform_setting_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const PlatformSettingResponseValueTypeEnum
    _$platformSettingResponseValueTypeEnum_MONEY =
    const PlatformSettingResponseValueTypeEnum._('MONEY');
const PlatformSettingResponseValueTypeEnum
    _$platformSettingResponseValueTypeEnum_INTEGER =
    const PlatformSettingResponseValueTypeEnum._('INTEGER');
const PlatformSettingResponseValueTypeEnum
    _$platformSettingResponseValueTypeEnum_BOOLEAN =
    const PlatformSettingResponseValueTypeEnum._('BOOLEAN');
const PlatformSettingResponseValueTypeEnum
    _$platformSettingResponseValueTypeEnum_STRING =
    const PlatformSettingResponseValueTypeEnum._('STRING');

PlatformSettingResponseValueTypeEnum
    _$platformSettingResponseValueTypeEnumValueOf(String name) {
  switch (name) {
    case 'MONEY':
      return _$platformSettingResponseValueTypeEnum_MONEY;
    case 'INTEGER':
      return _$platformSettingResponseValueTypeEnum_INTEGER;
    case 'BOOLEAN':
      return _$platformSettingResponseValueTypeEnum_BOOLEAN;
    case 'STRING':
      return _$platformSettingResponseValueTypeEnum_STRING;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<PlatformSettingResponseValueTypeEnum>
    _$platformSettingResponseValueTypeEnumValues = BuiltSet<
        PlatformSettingResponseValueTypeEnum>(const <PlatformSettingResponseValueTypeEnum>[
  _$platformSettingResponseValueTypeEnum_MONEY,
  _$platformSettingResponseValueTypeEnum_INTEGER,
  _$platformSettingResponseValueTypeEnum_BOOLEAN,
  _$platformSettingResponseValueTypeEnum_STRING,
]);

Serializer<PlatformSettingResponseValueTypeEnum>
    _$platformSettingResponseValueTypeEnumSerializer =
    _$PlatformSettingResponseValueTypeEnumSerializer();

class _$PlatformSettingResponseValueTypeEnumSerializer
    implements PrimitiveSerializer<PlatformSettingResponseValueTypeEnum> {
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
    PlatformSettingResponseValueTypeEnum
  ];
  @override
  final String wireName = 'PlatformSettingResponseValueTypeEnum';

  @override
  Object serialize(
          Serializers serializers, PlatformSettingResponseValueTypeEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  PlatformSettingResponseValueTypeEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      PlatformSettingResponseValueTypeEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$PlatformSettingResponse extends PlatformSettingResponse {
  @override
  final String key;
  @override
  final DateTime updatedAt;
  @override
  final String updatedBy;
  @override
  final String value;
  @override
  final PlatformSettingResponseValueTypeEnum valueType;
  @override
  final int version;

  factory _$PlatformSettingResponse(
          [void Function(PlatformSettingResponseBuilder)? updates]) =>
      (PlatformSettingResponseBuilder()..update(updates))._build();

  _$PlatformSettingResponse._(
      {required this.key,
      required this.updatedAt,
      required this.updatedBy,
      required this.value,
      required this.valueType,
      required this.version})
      : super._();
  @override
  PlatformSettingResponse rebuild(
          void Function(PlatformSettingResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PlatformSettingResponseBuilder toBuilder() =>
      PlatformSettingResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PlatformSettingResponse &&
        key == other.key &&
        updatedAt == other.updatedAt &&
        updatedBy == other.updatedBy &&
        value == other.value &&
        valueType == other.valueType &&
        version == other.version;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, key.hashCode);
    _$hash = $jc(_$hash, updatedAt.hashCode);
    _$hash = $jc(_$hash, updatedBy.hashCode);
    _$hash = $jc(_$hash, value.hashCode);
    _$hash = $jc(_$hash, valueType.hashCode);
    _$hash = $jc(_$hash, version.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'PlatformSettingResponse')
          ..add('key', key)
          ..add('updatedAt', updatedAt)
          ..add('updatedBy', updatedBy)
          ..add('value', value)
          ..add('valueType', valueType)
          ..add('version', version))
        .toString();
  }
}

class PlatformSettingResponseBuilder
    implements
        Builder<PlatformSettingResponse, PlatformSettingResponseBuilder> {
  _$PlatformSettingResponse? _$v;

  String? _key;
  String? get key => _$this._key;
  set key(String? key) => _$this._key = key;

  DateTime? _updatedAt;
  DateTime? get updatedAt => _$this._updatedAt;
  set updatedAt(DateTime? updatedAt) => _$this._updatedAt = updatedAt;

  String? _updatedBy;
  String? get updatedBy => _$this._updatedBy;
  set updatedBy(String? updatedBy) => _$this._updatedBy = updatedBy;

  String? _value;
  String? get value => _$this._value;
  set value(String? value) => _$this._value = value;

  PlatformSettingResponseValueTypeEnum? _valueType;
  PlatformSettingResponseValueTypeEnum? get valueType => _$this._valueType;
  set valueType(PlatformSettingResponseValueTypeEnum? valueType) =>
      _$this._valueType = valueType;

  int? _version;
  int? get version => _$this._version;
  set version(int? version) => _$this._version = version;

  PlatformSettingResponseBuilder() {
    PlatformSettingResponse._defaults(this);
  }

  PlatformSettingResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _key = $v.key;
      _updatedAt = $v.updatedAt;
      _updatedBy = $v.updatedBy;
      _value = $v.value;
      _valueType = $v.valueType;
      _version = $v.version;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(PlatformSettingResponse other) {
    _$v = other as _$PlatformSettingResponse;
  }

  @override
  void update(void Function(PlatformSettingResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PlatformSettingResponse build() => _build();

  _$PlatformSettingResponse _build() {
    final _$result = _$v ??
        _$PlatformSettingResponse._(
          key: BuiltValueNullFieldError.checkNotNull(
              key, r'PlatformSettingResponse', 'key'),
          updatedAt: BuiltValueNullFieldError.checkNotNull(
              updatedAt, r'PlatformSettingResponse', 'updatedAt'),
          updatedBy: BuiltValueNullFieldError.checkNotNull(
              updatedBy, r'PlatformSettingResponse', 'updatedBy'),
          value: BuiltValueNullFieldError.checkNotNull(
              value, r'PlatformSettingResponse', 'value'),
          valueType: BuiltValueNullFieldError.checkNotNull(
              valueType, r'PlatformSettingResponse', 'valueType'),
          version: BuiltValueNullFieldError.checkNotNull(
              version, r'PlatformSettingResponse', 'version'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
