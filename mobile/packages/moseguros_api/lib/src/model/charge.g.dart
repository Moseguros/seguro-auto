// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'charge.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const ChargeTypeEnum _$chargeTypeEnum_PERCENT =
    const ChargeTypeEnum._('PERCENT');
const ChargeTypeEnum _$chargeTypeEnum_FIXED = const ChargeTypeEnum._('FIXED');

ChargeTypeEnum _$chargeTypeEnumValueOf(String name) {
  switch (name) {
    case 'PERCENT':
      return _$chargeTypeEnum_PERCENT;
    case 'FIXED':
      return _$chargeTypeEnum_FIXED;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<ChargeTypeEnum> _$chargeTypeEnumValues =
    BuiltSet<ChargeTypeEnum>(const <ChargeTypeEnum>[
  _$chargeTypeEnum_PERCENT,
  _$chargeTypeEnum_FIXED,
]);

Serializer<ChargeTypeEnum> _$chargeTypeEnumSerializer =
    _$ChargeTypeEnumSerializer();

class _$ChargeTypeEnumSerializer
    implements PrimitiveSerializer<ChargeTypeEnum> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'PERCENT': 'PERCENT',
    'FIXED': 'FIXED',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'PERCENT': 'PERCENT',
    'FIXED': 'FIXED',
  };

  @override
  final Iterable<Type> types = const <Type>[ChargeTypeEnum];
  @override
  final String wireName = 'ChargeTypeEnum';

  @override
  Object serialize(Serializers serializers, ChargeTypeEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  ChargeTypeEnum deserialize(Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      ChargeTypeEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$Charge extends Charge {
  @override
  final String code;
  @override
  final String labelEn;
  @override
  final String labelPt;
  @override
  final ChargeTypeEnum type;
  @override
  final ChargeValue value;

  factory _$Charge([void Function(ChargeBuilder)? updates]) =>
      (ChargeBuilder()..update(updates))._build();

  _$Charge._(
      {required this.code,
      required this.labelEn,
      required this.labelPt,
      required this.type,
      required this.value})
      : super._();
  @override
  Charge rebuild(void Function(ChargeBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  ChargeBuilder toBuilder() => ChargeBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is Charge &&
        code == other.code &&
        labelEn == other.labelEn &&
        labelPt == other.labelPt &&
        type == other.type &&
        value == other.value;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, code.hashCode);
    _$hash = $jc(_$hash, labelEn.hashCode);
    _$hash = $jc(_$hash, labelPt.hashCode);
    _$hash = $jc(_$hash, type.hashCode);
    _$hash = $jc(_$hash, value.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'Charge')
          ..add('code', code)
          ..add('labelEn', labelEn)
          ..add('labelPt', labelPt)
          ..add('type', type)
          ..add('value', value))
        .toString();
  }
}

class ChargeBuilder implements Builder<Charge, ChargeBuilder> {
  _$Charge? _$v;

  String? _code;
  String? get code => _$this._code;
  set code(String? code) => _$this._code = code;

  String? _labelEn;
  String? get labelEn => _$this._labelEn;
  set labelEn(String? labelEn) => _$this._labelEn = labelEn;

  String? _labelPt;
  String? get labelPt => _$this._labelPt;
  set labelPt(String? labelPt) => _$this._labelPt = labelPt;

  ChargeTypeEnum? _type;
  ChargeTypeEnum? get type => _$this._type;
  set type(ChargeTypeEnum? type) => _$this._type = type;

  ChargeValueBuilder? _value;
  ChargeValueBuilder get value => _$this._value ??= ChargeValueBuilder();
  set value(ChargeValueBuilder? value) => _$this._value = value;

  ChargeBuilder() {
    Charge._defaults(this);
  }

  ChargeBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _code = $v.code;
      _labelEn = $v.labelEn;
      _labelPt = $v.labelPt;
      _type = $v.type;
      _value = $v.value.toBuilder();
      _$v = null;
    }
    return this;
  }

  @override
  void replace(Charge other) {
    _$v = other as _$Charge;
  }

  @override
  void update(void Function(ChargeBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  Charge build() => _build();

  _$Charge _build() {
    _$Charge _$result;
    try {
      _$result = _$v ??
          _$Charge._(
            code:
                BuiltValueNullFieldError.checkNotNull(code, r'Charge', 'code'),
            labelEn: BuiltValueNullFieldError.checkNotNull(
                labelEn, r'Charge', 'labelEn'),
            labelPt: BuiltValueNullFieldError.checkNotNull(
                labelPt, r'Charge', 'labelPt'),
            type:
                BuiltValueNullFieldError.checkNotNull(type, r'Charge', 'type'),
            value: value.build(),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'value';
        value.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'Charge', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
