// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'rating_coverage.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const RatingCoverageCapitalTypeEnum _$ratingCoverageCapitalTypeEnum_FIXED =
    const RatingCoverageCapitalTypeEnum._('FIXED');
const RatingCoverageCapitalTypeEnum
    _$ratingCoverageCapitalTypeEnum_VEHICLE_VALUE =
    const RatingCoverageCapitalTypeEnum._('VEHICLE_VALUE');
const RatingCoverageCapitalTypeEnum _$ratingCoverageCapitalTypeEnum_OPTIONS =
    const RatingCoverageCapitalTypeEnum._('OPTIONS');
const RatingCoverageCapitalTypeEnum _$ratingCoverageCapitalTypeEnum_PER_PERSON =
    const RatingCoverageCapitalTypeEnum._('PER_PERSON');

RatingCoverageCapitalTypeEnum _$ratingCoverageCapitalTypeEnumValueOf(
    String name) {
  switch (name) {
    case 'FIXED':
      return _$ratingCoverageCapitalTypeEnum_FIXED;
    case 'VEHICLE_VALUE':
      return _$ratingCoverageCapitalTypeEnum_VEHICLE_VALUE;
    case 'OPTIONS':
      return _$ratingCoverageCapitalTypeEnum_OPTIONS;
    case 'PER_PERSON':
      return _$ratingCoverageCapitalTypeEnum_PER_PERSON;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<RatingCoverageCapitalTypeEnum>
    _$ratingCoverageCapitalTypeEnumValues = BuiltSet<
        RatingCoverageCapitalTypeEnum>(const <RatingCoverageCapitalTypeEnum>[
  _$ratingCoverageCapitalTypeEnum_FIXED,
  _$ratingCoverageCapitalTypeEnum_VEHICLE_VALUE,
  _$ratingCoverageCapitalTypeEnum_OPTIONS,
  _$ratingCoverageCapitalTypeEnum_PER_PERSON,
]);

const RatingCoverageCodeEnum _$ratingCoverageCodeEnum_MTPL =
    const RatingCoverageCodeEnum._('MTPL');
const RatingCoverageCodeEnum _$ratingCoverageCodeEnum_VTPL =
    const RatingCoverageCodeEnum._('VTPL');
const RatingCoverageCodeEnum _$ratingCoverageCodeEnum_OD_COLLISION =
    const RatingCoverageCodeEnum._('OD_COLLISION');
const RatingCoverageCodeEnum _$ratingCoverageCodeEnum_OD_GLASS =
    const RatingCoverageCodeEnum._('OD_GLASS');
const RatingCoverageCodeEnum _$ratingCoverageCodeEnum_OD_FIRE =
    const RatingCoverageCodeEnum._('OD_FIRE');
const RatingCoverageCodeEnum _$ratingCoverageCodeEnum_OD_NATURE =
    const RatingCoverageCodeEnum._('OD_NATURE');
const RatingCoverageCodeEnum _$ratingCoverageCodeEnum_OD_THEFT =
    const RatingCoverageCodeEnum._('OD_THEFT');
const RatingCoverageCodeEnum _$ratingCoverageCodeEnum_OCCUPANTS =
    const RatingCoverageCodeEnum._('OCCUPANTS');
const RatingCoverageCodeEnum _$ratingCoverageCodeEnum_ASSISTANCE =
    const RatingCoverageCodeEnum._('ASSISTANCE');
const RatingCoverageCodeEnum _$ratingCoverageCodeEnum_LEGAL =
    const RatingCoverageCodeEnum._('LEGAL');
const RatingCoverageCodeEnum _$ratingCoverageCodeEnum_SRCC =
    const RatingCoverageCodeEnum._('SRCC');
const RatingCoverageCodeEnum _$ratingCoverageCodeEnum_CROSS_BORDER =
    const RatingCoverageCodeEnum._('CROSS_BORDER');

RatingCoverageCodeEnum _$ratingCoverageCodeEnumValueOf(String name) {
  switch (name) {
    case 'MTPL':
      return _$ratingCoverageCodeEnum_MTPL;
    case 'VTPL':
      return _$ratingCoverageCodeEnum_VTPL;
    case 'OD_COLLISION':
      return _$ratingCoverageCodeEnum_OD_COLLISION;
    case 'OD_GLASS':
      return _$ratingCoverageCodeEnum_OD_GLASS;
    case 'OD_FIRE':
      return _$ratingCoverageCodeEnum_OD_FIRE;
    case 'OD_NATURE':
      return _$ratingCoverageCodeEnum_OD_NATURE;
    case 'OD_THEFT':
      return _$ratingCoverageCodeEnum_OD_THEFT;
    case 'OCCUPANTS':
      return _$ratingCoverageCodeEnum_OCCUPANTS;
    case 'ASSISTANCE':
      return _$ratingCoverageCodeEnum_ASSISTANCE;
    case 'LEGAL':
      return _$ratingCoverageCodeEnum_LEGAL;
    case 'SRCC':
      return _$ratingCoverageCodeEnum_SRCC;
    case 'CROSS_BORDER':
      return _$ratingCoverageCodeEnum_CROSS_BORDER;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<RatingCoverageCodeEnum> _$ratingCoverageCodeEnumValues =
    BuiltSet<RatingCoverageCodeEnum>(const <RatingCoverageCodeEnum>[
  _$ratingCoverageCodeEnum_MTPL,
  _$ratingCoverageCodeEnum_VTPL,
  _$ratingCoverageCodeEnum_OD_COLLISION,
  _$ratingCoverageCodeEnum_OD_GLASS,
  _$ratingCoverageCodeEnum_OD_FIRE,
  _$ratingCoverageCodeEnum_OD_NATURE,
  _$ratingCoverageCodeEnum_OD_THEFT,
  _$ratingCoverageCodeEnum_OCCUPANTS,
  _$ratingCoverageCodeEnum_ASSISTANCE,
  _$ratingCoverageCodeEnum_LEGAL,
  _$ratingCoverageCodeEnum_SRCC,
  _$ratingCoverageCodeEnum_CROSS_BORDER,
]);

Serializer<RatingCoverageCapitalTypeEnum>
    _$ratingCoverageCapitalTypeEnumSerializer =
    _$RatingCoverageCapitalTypeEnumSerializer();
Serializer<RatingCoverageCodeEnum> _$ratingCoverageCodeEnumSerializer =
    _$RatingCoverageCodeEnumSerializer();

class _$RatingCoverageCapitalTypeEnumSerializer
    implements PrimitiveSerializer<RatingCoverageCapitalTypeEnum> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'FIXED': 'FIXED',
    'VEHICLE_VALUE': 'VEHICLE_VALUE',
    'OPTIONS': 'OPTIONS',
    'PER_PERSON': 'PER_PERSON',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'FIXED': 'FIXED',
    'VEHICLE_VALUE': 'VEHICLE_VALUE',
    'OPTIONS': 'OPTIONS',
    'PER_PERSON': 'PER_PERSON',
  };

  @override
  final Iterable<Type> types = const <Type>[RatingCoverageCapitalTypeEnum];
  @override
  final String wireName = 'RatingCoverageCapitalTypeEnum';

  @override
  Object serialize(
          Serializers serializers, RatingCoverageCapitalTypeEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  RatingCoverageCapitalTypeEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      RatingCoverageCapitalTypeEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$RatingCoverageCodeEnumSerializer
    implements PrimitiveSerializer<RatingCoverageCodeEnum> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'MTPL': 'MTPL',
    'VTPL': 'VTPL',
    'OD_COLLISION': 'OD_COLLISION',
    'OD_GLASS': 'OD_GLASS',
    'OD_FIRE': 'OD_FIRE',
    'OD_NATURE': 'OD_NATURE',
    'OD_THEFT': 'OD_THEFT',
    'OCCUPANTS': 'OCCUPANTS',
    'ASSISTANCE': 'ASSISTANCE',
    'LEGAL': 'LEGAL',
    'SRCC': 'SRCC',
    'CROSS_BORDER': 'CROSS_BORDER',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'MTPL': 'MTPL',
    'VTPL': 'VTPL',
    'OD_COLLISION': 'OD_COLLISION',
    'OD_GLASS': 'OD_GLASS',
    'OD_FIRE': 'OD_FIRE',
    'OD_NATURE': 'OD_NATURE',
    'OD_THEFT': 'OD_THEFT',
    'OCCUPANTS': 'OCCUPANTS',
    'ASSISTANCE': 'ASSISTANCE',
    'LEGAL': 'LEGAL',
    'SRCC': 'SRCC',
    'CROSS_BORDER': 'CROSS_BORDER',
  };

  @override
  final Iterable<Type> types = const <Type>[RatingCoverageCodeEnum];
  @override
  final String wireName = 'RatingCoverageCodeEnum';

  @override
  Object serialize(Serializers serializers, RatingCoverageCodeEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  RatingCoverageCodeEnum deserialize(Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      RatingCoverageCodeEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$RatingCoverage extends RatingCoverage {
  @override
  final Money? capital;
  @override
  final BuiltList<Money>? capitalOptions;
  @override
  final RatingCoverageCapitalTypeEnum capitalType;
  @override
  final RatingCoverageCodeEnum code;
  @override
  final bool mandatory;

  factory _$RatingCoverage([void Function(RatingCoverageBuilder)? updates]) =>
      (RatingCoverageBuilder()..update(updates))._build();

  _$RatingCoverage._(
      {this.capital,
      this.capitalOptions,
      required this.capitalType,
      required this.code,
      required this.mandatory})
      : super._();
  @override
  RatingCoverage rebuild(void Function(RatingCoverageBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  RatingCoverageBuilder toBuilder() => RatingCoverageBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is RatingCoverage &&
        capital == other.capital &&
        capitalOptions == other.capitalOptions &&
        capitalType == other.capitalType &&
        code == other.code &&
        mandatory == other.mandatory;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, capital.hashCode);
    _$hash = $jc(_$hash, capitalOptions.hashCode);
    _$hash = $jc(_$hash, capitalType.hashCode);
    _$hash = $jc(_$hash, code.hashCode);
    _$hash = $jc(_$hash, mandatory.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'RatingCoverage')
          ..add('capital', capital)
          ..add('capitalOptions', capitalOptions)
          ..add('capitalType', capitalType)
          ..add('code', code)
          ..add('mandatory', mandatory))
        .toString();
  }
}

class RatingCoverageBuilder
    implements Builder<RatingCoverage, RatingCoverageBuilder> {
  _$RatingCoverage? _$v;

  MoneyBuilder? _capital;
  MoneyBuilder get capital => _$this._capital ??= MoneyBuilder();
  set capital(MoneyBuilder? capital) => _$this._capital = capital;

  ListBuilder<Money>? _capitalOptions;
  ListBuilder<Money> get capitalOptions =>
      _$this._capitalOptions ??= ListBuilder<Money>();
  set capitalOptions(ListBuilder<Money>? capitalOptions) =>
      _$this._capitalOptions = capitalOptions;

  RatingCoverageCapitalTypeEnum? _capitalType;
  RatingCoverageCapitalTypeEnum? get capitalType => _$this._capitalType;
  set capitalType(RatingCoverageCapitalTypeEnum? capitalType) =>
      _$this._capitalType = capitalType;

  RatingCoverageCodeEnum? _code;
  RatingCoverageCodeEnum? get code => _$this._code;
  set code(RatingCoverageCodeEnum? code) => _$this._code = code;

  bool? _mandatory;
  bool? get mandatory => _$this._mandatory;
  set mandatory(bool? mandatory) => _$this._mandatory = mandatory;

  RatingCoverageBuilder() {
    RatingCoverage._defaults(this);
  }

  RatingCoverageBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _capital = $v.capital?.toBuilder();
      _capitalOptions = $v.capitalOptions?.toBuilder();
      _capitalType = $v.capitalType;
      _code = $v.code;
      _mandatory = $v.mandatory;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(RatingCoverage other) {
    _$v = other as _$RatingCoverage;
  }

  @override
  void update(void Function(RatingCoverageBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  RatingCoverage build() => _build();

  _$RatingCoverage _build() {
    _$RatingCoverage _$result;
    try {
      _$result = _$v ??
          _$RatingCoverage._(
            capital: _capital?.build(),
            capitalOptions: _capitalOptions?.build(),
            capitalType: BuiltValueNullFieldError.checkNotNull(
                capitalType, r'RatingCoverage', 'capitalType'),
            code: BuiltValueNullFieldError.checkNotNull(
                code, r'RatingCoverage', 'code'),
            mandatory: BuiltValueNullFieldError.checkNotNull(
                mandatory, r'RatingCoverage', 'mandatory'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'capital';
        _capital?.build();
        _$failedField = 'capitalOptions';
        _capitalOptions?.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'RatingCoverage', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
