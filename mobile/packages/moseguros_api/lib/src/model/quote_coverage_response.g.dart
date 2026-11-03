// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'quote_coverage_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const QuoteCoverageResponseCodeEnum _$quoteCoverageResponseCodeEnum_MTPL =
    const QuoteCoverageResponseCodeEnum._('MTPL');
const QuoteCoverageResponseCodeEnum _$quoteCoverageResponseCodeEnum_VTPL =
    const QuoteCoverageResponseCodeEnum._('VTPL');
const QuoteCoverageResponseCodeEnum
    _$quoteCoverageResponseCodeEnum_OD_COLLISION =
    const QuoteCoverageResponseCodeEnum._('OD_COLLISION');
const QuoteCoverageResponseCodeEnum _$quoteCoverageResponseCodeEnum_OD_GLASS =
    const QuoteCoverageResponseCodeEnum._('OD_GLASS');
const QuoteCoverageResponseCodeEnum _$quoteCoverageResponseCodeEnum_OD_FIRE =
    const QuoteCoverageResponseCodeEnum._('OD_FIRE');
const QuoteCoverageResponseCodeEnum _$quoteCoverageResponseCodeEnum_OD_NATURE =
    const QuoteCoverageResponseCodeEnum._('OD_NATURE');
const QuoteCoverageResponseCodeEnum _$quoteCoverageResponseCodeEnum_OD_THEFT =
    const QuoteCoverageResponseCodeEnum._('OD_THEFT');
const QuoteCoverageResponseCodeEnum _$quoteCoverageResponseCodeEnum_OCCUPANTS =
    const QuoteCoverageResponseCodeEnum._('OCCUPANTS');
const QuoteCoverageResponseCodeEnum _$quoteCoverageResponseCodeEnum_ASSISTANCE =
    const QuoteCoverageResponseCodeEnum._('ASSISTANCE');
const QuoteCoverageResponseCodeEnum _$quoteCoverageResponseCodeEnum_LEGAL =
    const QuoteCoverageResponseCodeEnum._('LEGAL');
const QuoteCoverageResponseCodeEnum _$quoteCoverageResponseCodeEnum_SRCC =
    const QuoteCoverageResponseCodeEnum._('SRCC');
const QuoteCoverageResponseCodeEnum
    _$quoteCoverageResponseCodeEnum_CROSS_BORDER =
    const QuoteCoverageResponseCodeEnum._('CROSS_BORDER');

QuoteCoverageResponseCodeEnum _$quoteCoverageResponseCodeEnumValueOf(
    String name) {
  switch (name) {
    case 'MTPL':
      return _$quoteCoverageResponseCodeEnum_MTPL;
    case 'VTPL':
      return _$quoteCoverageResponseCodeEnum_VTPL;
    case 'OD_COLLISION':
      return _$quoteCoverageResponseCodeEnum_OD_COLLISION;
    case 'OD_GLASS':
      return _$quoteCoverageResponseCodeEnum_OD_GLASS;
    case 'OD_FIRE':
      return _$quoteCoverageResponseCodeEnum_OD_FIRE;
    case 'OD_NATURE':
      return _$quoteCoverageResponseCodeEnum_OD_NATURE;
    case 'OD_THEFT':
      return _$quoteCoverageResponseCodeEnum_OD_THEFT;
    case 'OCCUPANTS':
      return _$quoteCoverageResponseCodeEnum_OCCUPANTS;
    case 'ASSISTANCE':
      return _$quoteCoverageResponseCodeEnum_ASSISTANCE;
    case 'LEGAL':
      return _$quoteCoverageResponseCodeEnum_LEGAL;
    case 'SRCC':
      return _$quoteCoverageResponseCodeEnum_SRCC;
    case 'CROSS_BORDER':
      return _$quoteCoverageResponseCodeEnum_CROSS_BORDER;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<QuoteCoverageResponseCodeEnum>
    _$quoteCoverageResponseCodeEnumValues = BuiltSet<
        QuoteCoverageResponseCodeEnum>(const <QuoteCoverageResponseCodeEnum>[
  _$quoteCoverageResponseCodeEnum_MTPL,
  _$quoteCoverageResponseCodeEnum_VTPL,
  _$quoteCoverageResponseCodeEnum_OD_COLLISION,
  _$quoteCoverageResponseCodeEnum_OD_GLASS,
  _$quoteCoverageResponseCodeEnum_OD_FIRE,
  _$quoteCoverageResponseCodeEnum_OD_NATURE,
  _$quoteCoverageResponseCodeEnum_OD_THEFT,
  _$quoteCoverageResponseCodeEnum_OCCUPANTS,
  _$quoteCoverageResponseCodeEnum_ASSISTANCE,
  _$quoteCoverageResponseCodeEnum_LEGAL,
  _$quoteCoverageResponseCodeEnum_SRCC,
  _$quoteCoverageResponseCodeEnum_CROSS_BORDER,
]);

Serializer<QuoteCoverageResponseCodeEnum>
    _$quoteCoverageResponseCodeEnumSerializer =
    _$QuoteCoverageResponseCodeEnumSerializer();

class _$QuoteCoverageResponseCodeEnumSerializer
    implements PrimitiveSerializer<QuoteCoverageResponseCodeEnum> {
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
  final Iterable<Type> types = const <Type>[QuoteCoverageResponseCodeEnum];
  @override
  final String wireName = 'QuoteCoverageResponseCodeEnum';

  @override
  Object serialize(
          Serializers serializers, QuoteCoverageResponseCodeEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  QuoteCoverageResponseCodeEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      QuoteCoverageResponseCodeEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$QuoteCoverageResponse extends QuoteCoverageResponse {
  @override
  final Money? capital;
  @override
  final QuoteCoverageResponseCodeEnum code;
  @override
  final QuoteDeductible deductible;
  @override
  final bool mandatory;
  @override
  final Money premium;

  factory _$QuoteCoverageResponse(
          [void Function(QuoteCoverageResponseBuilder)? updates]) =>
      (QuoteCoverageResponseBuilder()..update(updates))._build();

  _$QuoteCoverageResponse._(
      {this.capital,
      required this.code,
      required this.deductible,
      required this.mandatory,
      required this.premium})
      : super._();
  @override
  QuoteCoverageResponse rebuild(
          void Function(QuoteCoverageResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  QuoteCoverageResponseBuilder toBuilder() =>
      QuoteCoverageResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is QuoteCoverageResponse &&
        capital == other.capital &&
        code == other.code &&
        deductible == other.deductible &&
        mandatory == other.mandatory &&
        premium == other.premium;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, capital.hashCode);
    _$hash = $jc(_$hash, code.hashCode);
    _$hash = $jc(_$hash, deductible.hashCode);
    _$hash = $jc(_$hash, mandatory.hashCode);
    _$hash = $jc(_$hash, premium.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'QuoteCoverageResponse')
          ..add('capital', capital)
          ..add('code', code)
          ..add('deductible', deductible)
          ..add('mandatory', mandatory)
          ..add('premium', premium))
        .toString();
  }
}

class QuoteCoverageResponseBuilder
    implements Builder<QuoteCoverageResponse, QuoteCoverageResponseBuilder> {
  _$QuoteCoverageResponse? _$v;

  MoneyBuilder? _capital;
  MoneyBuilder get capital => _$this._capital ??= MoneyBuilder();
  set capital(MoneyBuilder? capital) => _$this._capital = capital;

  QuoteCoverageResponseCodeEnum? _code;
  QuoteCoverageResponseCodeEnum? get code => _$this._code;
  set code(QuoteCoverageResponseCodeEnum? code) => _$this._code = code;

  QuoteDeductibleBuilder? _deductible;
  QuoteDeductibleBuilder get deductible =>
      _$this._deductible ??= QuoteDeductibleBuilder();
  set deductible(QuoteDeductibleBuilder? deductible) =>
      _$this._deductible = deductible;

  bool? _mandatory;
  bool? get mandatory => _$this._mandatory;
  set mandatory(bool? mandatory) => _$this._mandatory = mandatory;

  MoneyBuilder? _premium;
  MoneyBuilder get premium => _$this._premium ??= MoneyBuilder();
  set premium(MoneyBuilder? premium) => _$this._premium = premium;

  QuoteCoverageResponseBuilder() {
    QuoteCoverageResponse._defaults(this);
  }

  QuoteCoverageResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _capital = $v.capital?.toBuilder();
      _code = $v.code;
      _deductible = $v.deductible.toBuilder();
      _mandatory = $v.mandatory;
      _premium = $v.premium.toBuilder();
      _$v = null;
    }
    return this;
  }

  @override
  void replace(QuoteCoverageResponse other) {
    _$v = other as _$QuoteCoverageResponse;
  }

  @override
  void update(void Function(QuoteCoverageResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  QuoteCoverageResponse build() => _build();

  _$QuoteCoverageResponse _build() {
    _$QuoteCoverageResponse _$result;
    try {
      _$result = _$v ??
          _$QuoteCoverageResponse._(
            capital: _capital?.build(),
            code: BuiltValueNullFieldError.checkNotNull(
                code, r'QuoteCoverageResponse', 'code'),
            deductible: deductible.build(),
            mandatory: BuiltValueNullFieldError.checkNotNull(
                mandatory, r'QuoteCoverageResponse', 'mandatory'),
            premium: premium.build(),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'capital';
        _capital?.build();

        _$failedField = 'deductible';
        deductible.build();

        _$failedField = 'premium';
        premium.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'QuoteCoverageResponse', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
