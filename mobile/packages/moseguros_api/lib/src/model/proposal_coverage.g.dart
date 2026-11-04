// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'proposal_coverage.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const ProposalCoverageCodeEnum _$proposalCoverageCodeEnum_MTPL =
    const ProposalCoverageCodeEnum._('MTPL');
const ProposalCoverageCodeEnum _$proposalCoverageCodeEnum_VTPL =
    const ProposalCoverageCodeEnum._('VTPL');
const ProposalCoverageCodeEnum _$proposalCoverageCodeEnum_OD_COLLISION =
    const ProposalCoverageCodeEnum._('OD_COLLISION');
const ProposalCoverageCodeEnum _$proposalCoverageCodeEnum_OD_GLASS =
    const ProposalCoverageCodeEnum._('OD_GLASS');
const ProposalCoverageCodeEnum _$proposalCoverageCodeEnum_OD_FIRE =
    const ProposalCoverageCodeEnum._('OD_FIRE');
const ProposalCoverageCodeEnum _$proposalCoverageCodeEnum_OD_NATURE =
    const ProposalCoverageCodeEnum._('OD_NATURE');
const ProposalCoverageCodeEnum _$proposalCoverageCodeEnum_OD_THEFT =
    const ProposalCoverageCodeEnum._('OD_THEFT');
const ProposalCoverageCodeEnum _$proposalCoverageCodeEnum_OCCUPANTS =
    const ProposalCoverageCodeEnum._('OCCUPANTS');
const ProposalCoverageCodeEnum _$proposalCoverageCodeEnum_ASSISTANCE =
    const ProposalCoverageCodeEnum._('ASSISTANCE');
const ProposalCoverageCodeEnum _$proposalCoverageCodeEnum_LEGAL =
    const ProposalCoverageCodeEnum._('LEGAL');
const ProposalCoverageCodeEnum _$proposalCoverageCodeEnum_SRCC =
    const ProposalCoverageCodeEnum._('SRCC');
const ProposalCoverageCodeEnum _$proposalCoverageCodeEnum_CROSS_BORDER =
    const ProposalCoverageCodeEnum._('CROSS_BORDER');

ProposalCoverageCodeEnum _$proposalCoverageCodeEnumValueOf(String name) {
  switch (name) {
    case 'MTPL':
      return _$proposalCoverageCodeEnum_MTPL;
    case 'VTPL':
      return _$proposalCoverageCodeEnum_VTPL;
    case 'OD_COLLISION':
      return _$proposalCoverageCodeEnum_OD_COLLISION;
    case 'OD_GLASS':
      return _$proposalCoverageCodeEnum_OD_GLASS;
    case 'OD_FIRE':
      return _$proposalCoverageCodeEnum_OD_FIRE;
    case 'OD_NATURE':
      return _$proposalCoverageCodeEnum_OD_NATURE;
    case 'OD_THEFT':
      return _$proposalCoverageCodeEnum_OD_THEFT;
    case 'OCCUPANTS':
      return _$proposalCoverageCodeEnum_OCCUPANTS;
    case 'ASSISTANCE':
      return _$proposalCoverageCodeEnum_ASSISTANCE;
    case 'LEGAL':
      return _$proposalCoverageCodeEnum_LEGAL;
    case 'SRCC':
      return _$proposalCoverageCodeEnum_SRCC;
    case 'CROSS_BORDER':
      return _$proposalCoverageCodeEnum_CROSS_BORDER;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<ProposalCoverageCodeEnum> _$proposalCoverageCodeEnumValues =
    BuiltSet<ProposalCoverageCodeEnum>(const <ProposalCoverageCodeEnum>[
  _$proposalCoverageCodeEnum_MTPL,
  _$proposalCoverageCodeEnum_VTPL,
  _$proposalCoverageCodeEnum_OD_COLLISION,
  _$proposalCoverageCodeEnum_OD_GLASS,
  _$proposalCoverageCodeEnum_OD_FIRE,
  _$proposalCoverageCodeEnum_OD_NATURE,
  _$proposalCoverageCodeEnum_OD_THEFT,
  _$proposalCoverageCodeEnum_OCCUPANTS,
  _$proposalCoverageCodeEnum_ASSISTANCE,
  _$proposalCoverageCodeEnum_LEGAL,
  _$proposalCoverageCodeEnum_SRCC,
  _$proposalCoverageCodeEnum_CROSS_BORDER,
]);

Serializer<ProposalCoverageCodeEnum> _$proposalCoverageCodeEnumSerializer =
    _$ProposalCoverageCodeEnumSerializer();

class _$ProposalCoverageCodeEnumSerializer
    implements PrimitiveSerializer<ProposalCoverageCodeEnum> {
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
  final Iterable<Type> types = const <Type>[ProposalCoverageCodeEnum];
  @override
  final String wireName = 'ProposalCoverageCodeEnum';

  @override
  Object serialize(Serializers serializers, ProposalCoverageCodeEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  ProposalCoverageCodeEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      ProposalCoverageCodeEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$ProposalCoverage extends ProposalCoverage {
  @override
  final Money? capital;
  @override
  final ProposalCoverageCodeEnum code;
  @override
  final ProposalDeductible deductible;
  @override
  final bool mandatory;
  @override
  final Money premium;

  factory _$ProposalCoverage(
          [void Function(ProposalCoverageBuilder)? updates]) =>
      (ProposalCoverageBuilder()..update(updates))._build();

  _$ProposalCoverage._(
      {this.capital,
      required this.code,
      required this.deductible,
      required this.mandatory,
      required this.premium})
      : super._();
  @override
  ProposalCoverage rebuild(void Function(ProposalCoverageBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  ProposalCoverageBuilder toBuilder() =>
      ProposalCoverageBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is ProposalCoverage &&
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
    return (newBuiltValueToStringHelper(r'ProposalCoverage')
          ..add('capital', capital)
          ..add('code', code)
          ..add('deductible', deductible)
          ..add('mandatory', mandatory)
          ..add('premium', premium))
        .toString();
  }
}

class ProposalCoverageBuilder
    implements Builder<ProposalCoverage, ProposalCoverageBuilder> {
  _$ProposalCoverage? _$v;

  MoneyBuilder? _capital;
  MoneyBuilder get capital => _$this._capital ??= MoneyBuilder();
  set capital(MoneyBuilder? capital) => _$this._capital = capital;

  ProposalCoverageCodeEnum? _code;
  ProposalCoverageCodeEnum? get code => _$this._code;
  set code(ProposalCoverageCodeEnum? code) => _$this._code = code;

  ProposalDeductibleBuilder? _deductible;
  ProposalDeductibleBuilder get deductible =>
      _$this._deductible ??= ProposalDeductibleBuilder();
  set deductible(ProposalDeductibleBuilder? deductible) =>
      _$this._deductible = deductible;

  bool? _mandatory;
  bool? get mandatory => _$this._mandatory;
  set mandatory(bool? mandatory) => _$this._mandatory = mandatory;

  MoneyBuilder? _premium;
  MoneyBuilder get premium => _$this._premium ??= MoneyBuilder();
  set premium(MoneyBuilder? premium) => _$this._premium = premium;

  ProposalCoverageBuilder() {
    ProposalCoverage._defaults(this);
  }

  ProposalCoverageBuilder get _$this {
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
  void replace(ProposalCoverage other) {
    _$v = other as _$ProposalCoverage;
  }

  @override
  void update(void Function(ProposalCoverageBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  ProposalCoverage build() => _build();

  _$ProposalCoverage _build() {
    _$ProposalCoverage _$result;
    try {
      _$result = _$v ??
          _$ProposalCoverage._(
            capital: _capital?.build(),
            code: BuiltValueNullFieldError.checkNotNull(
                code, r'ProposalCoverage', 'code'),
            deductible: deductible.build(),
            mandatory: BuiltValueNullFieldError.checkNotNull(
                mandatory, r'ProposalCoverage', 'mandatory'),
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
            r'ProposalCoverage', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
