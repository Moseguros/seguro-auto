// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'policy_coverage.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const PolicyCoverageDeductibleTypeEnum _$policyCoverageDeductibleTypeEnum_NONE =
    const PolicyCoverageDeductibleTypeEnum._('NONE');
const PolicyCoverageDeductibleTypeEnum
    _$policyCoverageDeductibleTypeEnum_FIXED =
    const PolicyCoverageDeductibleTypeEnum._('FIXED');
const PolicyCoverageDeductibleTypeEnum
    _$policyCoverageDeductibleTypeEnum_PERCENT =
    const PolicyCoverageDeductibleTypeEnum._('PERCENT');

PolicyCoverageDeductibleTypeEnum _$policyCoverageDeductibleTypeEnumValueOf(
    String name) {
  switch (name) {
    case 'NONE':
      return _$policyCoverageDeductibleTypeEnum_NONE;
    case 'FIXED':
      return _$policyCoverageDeductibleTypeEnum_FIXED;
    case 'PERCENT':
      return _$policyCoverageDeductibleTypeEnum_PERCENT;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<PolicyCoverageDeductibleTypeEnum>
    _$policyCoverageDeductibleTypeEnumValues = BuiltSet<
        PolicyCoverageDeductibleTypeEnum>(const <PolicyCoverageDeductibleTypeEnum>[
  _$policyCoverageDeductibleTypeEnum_NONE,
  _$policyCoverageDeductibleTypeEnum_FIXED,
  _$policyCoverageDeductibleTypeEnum_PERCENT,
]);

Serializer<PolicyCoverageDeductibleTypeEnum>
    _$policyCoverageDeductibleTypeEnumSerializer =
    _$PolicyCoverageDeductibleTypeEnumSerializer();

class _$PolicyCoverageDeductibleTypeEnumSerializer
    implements PrimitiveSerializer<PolicyCoverageDeductibleTypeEnum> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'NONE': 'NONE',
    'FIXED': 'FIXED',
    'PERCENT': 'PERCENT',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'NONE': 'NONE',
    'FIXED': 'FIXED',
    'PERCENT': 'PERCENT',
  };

  @override
  final Iterable<Type> types = const <Type>[PolicyCoverageDeductibleTypeEnum];
  @override
  final String wireName = 'PolicyCoverageDeductibleTypeEnum';

  @override
  Object serialize(
          Serializers serializers, PolicyCoverageDeductibleTypeEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  PolicyCoverageDeductibleTypeEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      PolicyCoverageDeductibleTypeEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$PolicyCoverage extends PolicyCoverage {
  @override
  final Money? capital;
  @override
  final String code;
  @override
  final Money? deductibleAmount;
  @override
  final num? deductiblePercent;
  @override
  final PolicyCoverageDeductibleTypeEnum deductibleType;
  @override
  final bool mandatory;
  @override
  final Money premium;

  factory _$PolicyCoverage([void Function(PolicyCoverageBuilder)? updates]) =>
      (PolicyCoverageBuilder()..update(updates))._build();

  _$PolicyCoverage._(
      {this.capital,
      required this.code,
      this.deductibleAmount,
      this.deductiblePercent,
      required this.deductibleType,
      required this.mandatory,
      required this.premium})
      : super._();
  @override
  PolicyCoverage rebuild(void Function(PolicyCoverageBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PolicyCoverageBuilder toBuilder() => PolicyCoverageBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PolicyCoverage &&
        capital == other.capital &&
        code == other.code &&
        deductibleAmount == other.deductibleAmount &&
        deductiblePercent == other.deductiblePercent &&
        deductibleType == other.deductibleType &&
        mandatory == other.mandatory &&
        premium == other.premium;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, capital.hashCode);
    _$hash = $jc(_$hash, code.hashCode);
    _$hash = $jc(_$hash, deductibleAmount.hashCode);
    _$hash = $jc(_$hash, deductiblePercent.hashCode);
    _$hash = $jc(_$hash, deductibleType.hashCode);
    _$hash = $jc(_$hash, mandatory.hashCode);
    _$hash = $jc(_$hash, premium.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'PolicyCoverage')
          ..add('capital', capital)
          ..add('code', code)
          ..add('deductibleAmount', deductibleAmount)
          ..add('deductiblePercent', deductiblePercent)
          ..add('deductibleType', deductibleType)
          ..add('mandatory', mandatory)
          ..add('premium', premium))
        .toString();
  }
}

class PolicyCoverageBuilder
    implements Builder<PolicyCoverage, PolicyCoverageBuilder> {
  _$PolicyCoverage? _$v;

  MoneyBuilder? _capital;
  MoneyBuilder get capital => _$this._capital ??= MoneyBuilder();
  set capital(MoneyBuilder? capital) => _$this._capital = capital;

  String? _code;
  String? get code => _$this._code;
  set code(String? code) => _$this._code = code;

  MoneyBuilder? _deductibleAmount;
  MoneyBuilder get deductibleAmount =>
      _$this._deductibleAmount ??= MoneyBuilder();
  set deductibleAmount(MoneyBuilder? deductibleAmount) =>
      _$this._deductibleAmount = deductibleAmount;

  num? _deductiblePercent;
  num? get deductiblePercent => _$this._deductiblePercent;
  set deductiblePercent(num? deductiblePercent) =>
      _$this._deductiblePercent = deductiblePercent;

  PolicyCoverageDeductibleTypeEnum? _deductibleType;
  PolicyCoverageDeductibleTypeEnum? get deductibleType =>
      _$this._deductibleType;
  set deductibleType(PolicyCoverageDeductibleTypeEnum? deductibleType) =>
      _$this._deductibleType = deductibleType;

  bool? _mandatory;
  bool? get mandatory => _$this._mandatory;
  set mandatory(bool? mandatory) => _$this._mandatory = mandatory;

  MoneyBuilder? _premium;
  MoneyBuilder get premium => _$this._premium ??= MoneyBuilder();
  set premium(MoneyBuilder? premium) => _$this._premium = premium;

  PolicyCoverageBuilder() {
    PolicyCoverage._defaults(this);
  }

  PolicyCoverageBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _capital = $v.capital?.toBuilder();
      _code = $v.code;
      _deductibleAmount = $v.deductibleAmount?.toBuilder();
      _deductiblePercent = $v.deductiblePercent;
      _deductibleType = $v.deductibleType;
      _mandatory = $v.mandatory;
      _premium = $v.premium.toBuilder();
      _$v = null;
    }
    return this;
  }

  @override
  void replace(PolicyCoverage other) {
    _$v = other as _$PolicyCoverage;
  }

  @override
  void update(void Function(PolicyCoverageBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PolicyCoverage build() => _build();

  _$PolicyCoverage _build() {
    _$PolicyCoverage _$result;
    try {
      _$result = _$v ??
          _$PolicyCoverage._(
            capital: _capital?.build(),
            code: BuiltValueNullFieldError.checkNotNull(
                code, r'PolicyCoverage', 'code'),
            deductibleAmount: _deductibleAmount?.build(),
            deductiblePercent: deductiblePercent,
            deductibleType: BuiltValueNullFieldError.checkNotNull(
                deductibleType, r'PolicyCoverage', 'deductibleType'),
            mandatory: BuiltValueNullFieldError.checkNotNull(
                mandatory, r'PolicyCoverage', 'mandatory'),
            premium: premium.build(),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'capital';
        _capital?.build();

        _$failedField = 'deductibleAmount';
        _deductibleAmount?.build();

        _$failedField = 'premium';
        premium.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'PolicyCoverage', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
