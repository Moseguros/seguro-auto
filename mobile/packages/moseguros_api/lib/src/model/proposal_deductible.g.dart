// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'proposal_deductible.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const ProposalDeductibleTypeEnum _$proposalDeductibleTypeEnum_NONE =
    const ProposalDeductibleTypeEnum._('NONE');
const ProposalDeductibleTypeEnum _$proposalDeductibleTypeEnum_FIXED =
    const ProposalDeductibleTypeEnum._('FIXED');
const ProposalDeductibleTypeEnum _$proposalDeductibleTypeEnum_PERCENT =
    const ProposalDeductibleTypeEnum._('PERCENT');

ProposalDeductibleTypeEnum _$proposalDeductibleTypeEnumValueOf(String name) {
  switch (name) {
    case 'NONE':
      return _$proposalDeductibleTypeEnum_NONE;
    case 'FIXED':
      return _$proposalDeductibleTypeEnum_FIXED;
    case 'PERCENT':
      return _$proposalDeductibleTypeEnum_PERCENT;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<ProposalDeductibleTypeEnum> _$proposalDeductibleTypeEnumValues =
    BuiltSet<ProposalDeductibleTypeEnum>(const <ProposalDeductibleTypeEnum>[
  _$proposalDeductibleTypeEnum_NONE,
  _$proposalDeductibleTypeEnum_FIXED,
  _$proposalDeductibleTypeEnum_PERCENT,
]);

Serializer<ProposalDeductibleTypeEnum> _$proposalDeductibleTypeEnumSerializer =
    _$ProposalDeductibleTypeEnumSerializer();

class _$ProposalDeductibleTypeEnumSerializer
    implements PrimitiveSerializer<ProposalDeductibleTypeEnum> {
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
  final Iterable<Type> types = const <Type>[ProposalDeductibleTypeEnum];
  @override
  final String wireName = 'ProposalDeductibleTypeEnum';

  @override
  Object serialize(Serializers serializers, ProposalDeductibleTypeEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  ProposalDeductibleTypeEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      ProposalDeductibleTypeEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$ProposalDeductible extends ProposalDeductible {
  @override
  final Money? amount;
  @override
  final Money? minAmount;
  @override
  final String? percent;
  @override
  final ProposalDeductibleTypeEnum type;

  factory _$ProposalDeductible(
          [void Function(ProposalDeductibleBuilder)? updates]) =>
      (ProposalDeductibleBuilder()..update(updates))._build();

  _$ProposalDeductible._(
      {this.amount, this.minAmount, this.percent, required this.type})
      : super._();
  @override
  ProposalDeductible rebuild(
          void Function(ProposalDeductibleBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  ProposalDeductibleBuilder toBuilder() =>
      ProposalDeductibleBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is ProposalDeductible &&
        amount == other.amount &&
        minAmount == other.minAmount &&
        percent == other.percent &&
        type == other.type;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, amount.hashCode);
    _$hash = $jc(_$hash, minAmount.hashCode);
    _$hash = $jc(_$hash, percent.hashCode);
    _$hash = $jc(_$hash, type.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'ProposalDeductible')
          ..add('amount', amount)
          ..add('minAmount', minAmount)
          ..add('percent', percent)
          ..add('type', type))
        .toString();
  }
}

class ProposalDeductibleBuilder
    implements Builder<ProposalDeductible, ProposalDeductibleBuilder> {
  _$ProposalDeductible? _$v;

  MoneyBuilder? _amount;
  MoneyBuilder get amount => _$this._amount ??= MoneyBuilder();
  set amount(MoneyBuilder? amount) => _$this._amount = amount;

  MoneyBuilder? _minAmount;
  MoneyBuilder get minAmount => _$this._minAmount ??= MoneyBuilder();
  set minAmount(MoneyBuilder? minAmount) => _$this._minAmount = minAmount;

  String? _percent;
  String? get percent => _$this._percent;
  set percent(String? percent) => _$this._percent = percent;

  ProposalDeductibleTypeEnum? _type;
  ProposalDeductibleTypeEnum? get type => _$this._type;
  set type(ProposalDeductibleTypeEnum? type) => _$this._type = type;

  ProposalDeductibleBuilder() {
    ProposalDeductible._defaults(this);
  }

  ProposalDeductibleBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _amount = $v.amount?.toBuilder();
      _minAmount = $v.minAmount?.toBuilder();
      _percent = $v.percent;
      _type = $v.type;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(ProposalDeductible other) {
    _$v = other as _$ProposalDeductible;
  }

  @override
  void update(void Function(ProposalDeductibleBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  ProposalDeductible build() => _build();

  _$ProposalDeductible _build() {
    _$ProposalDeductible _$result;
    try {
      _$result = _$v ??
          _$ProposalDeductible._(
            amount: _amount?.build(),
            minAmount: _minAmount?.build(),
            percent: percent,
            type: BuiltValueNullFieldError.checkNotNull(
                type, r'ProposalDeductible', 'type'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'amount';
        _amount?.build();
        _$failedField = 'minAmount';
        _minAmount?.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'ProposalDeductible', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
