// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'quote_deductible.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const QuoteDeductibleTypeEnum _$quoteDeductibleTypeEnum_NONE =
    const QuoteDeductibleTypeEnum._('NONE');
const QuoteDeductibleTypeEnum _$quoteDeductibleTypeEnum_FIXED =
    const QuoteDeductibleTypeEnum._('FIXED');
const QuoteDeductibleTypeEnum _$quoteDeductibleTypeEnum_PERCENT =
    const QuoteDeductibleTypeEnum._('PERCENT');

QuoteDeductibleTypeEnum _$quoteDeductibleTypeEnumValueOf(String name) {
  switch (name) {
    case 'NONE':
      return _$quoteDeductibleTypeEnum_NONE;
    case 'FIXED':
      return _$quoteDeductibleTypeEnum_FIXED;
    case 'PERCENT':
      return _$quoteDeductibleTypeEnum_PERCENT;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<QuoteDeductibleTypeEnum> _$quoteDeductibleTypeEnumValues =
    BuiltSet<QuoteDeductibleTypeEnum>(const <QuoteDeductibleTypeEnum>[
  _$quoteDeductibleTypeEnum_NONE,
  _$quoteDeductibleTypeEnum_FIXED,
  _$quoteDeductibleTypeEnum_PERCENT,
]);

Serializer<QuoteDeductibleTypeEnum> _$quoteDeductibleTypeEnumSerializer =
    _$QuoteDeductibleTypeEnumSerializer();

class _$QuoteDeductibleTypeEnumSerializer
    implements PrimitiveSerializer<QuoteDeductibleTypeEnum> {
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
  final Iterable<Type> types = const <Type>[QuoteDeductibleTypeEnum];
  @override
  final String wireName = 'QuoteDeductibleTypeEnum';

  @override
  Object serialize(Serializers serializers, QuoteDeductibleTypeEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  QuoteDeductibleTypeEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      QuoteDeductibleTypeEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$QuoteDeductible extends QuoteDeductible {
  @override
  final Money? amount;
  @override
  final Money? minAmount;
  @override
  final String? percent;
  @override
  final QuoteDeductibleTypeEnum type;

  factory _$QuoteDeductible([void Function(QuoteDeductibleBuilder)? updates]) =>
      (QuoteDeductibleBuilder()..update(updates))._build();

  _$QuoteDeductible._(
      {this.amount, this.minAmount, this.percent, required this.type})
      : super._();
  @override
  QuoteDeductible rebuild(void Function(QuoteDeductibleBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  QuoteDeductibleBuilder toBuilder() => QuoteDeductibleBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is QuoteDeductible &&
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
    return (newBuiltValueToStringHelper(r'QuoteDeductible')
          ..add('amount', amount)
          ..add('minAmount', minAmount)
          ..add('percent', percent)
          ..add('type', type))
        .toString();
  }
}

class QuoteDeductibleBuilder
    implements Builder<QuoteDeductible, QuoteDeductibleBuilder> {
  _$QuoteDeductible? _$v;

  MoneyBuilder? _amount;
  MoneyBuilder get amount => _$this._amount ??= MoneyBuilder();
  set amount(MoneyBuilder? amount) => _$this._amount = amount;

  MoneyBuilder? _minAmount;
  MoneyBuilder get minAmount => _$this._minAmount ??= MoneyBuilder();
  set minAmount(MoneyBuilder? minAmount) => _$this._minAmount = minAmount;

  String? _percent;
  String? get percent => _$this._percent;
  set percent(String? percent) => _$this._percent = percent;

  QuoteDeductibleTypeEnum? _type;
  QuoteDeductibleTypeEnum? get type => _$this._type;
  set type(QuoteDeductibleTypeEnum? type) => _$this._type = type;

  QuoteDeductibleBuilder() {
    QuoteDeductible._defaults(this);
  }

  QuoteDeductibleBuilder get _$this {
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
  void replace(QuoteDeductible other) {
    _$v = other as _$QuoteDeductible;
  }

  @override
  void update(void Function(QuoteDeductibleBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  QuoteDeductible build() => _build();

  _$QuoteDeductible _build() {
    _$QuoteDeductible _$result;
    try {
      _$result = _$v ??
          _$QuoteDeductible._(
            amount: _amount?.build(),
            minAmount: _minAmount?.build(),
            percent: percent,
            type: BuiltValueNullFieldError.checkNotNull(
                type, r'QuoteDeductible', 'type'),
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
            r'QuoteDeductible', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
