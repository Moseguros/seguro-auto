// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'money.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const MoneyCurrencyEnum _$moneyCurrencyEnum_MZN =
    const MoneyCurrencyEnum._('MZN');

MoneyCurrencyEnum _$moneyCurrencyEnumValueOf(String name) {
  switch (name) {
    case 'MZN':
      return _$moneyCurrencyEnum_MZN;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<MoneyCurrencyEnum> _$moneyCurrencyEnumValues =
    BuiltSet<MoneyCurrencyEnum>(const <MoneyCurrencyEnum>[
  _$moneyCurrencyEnum_MZN,
]);

Serializer<MoneyCurrencyEnum> _$moneyCurrencyEnumSerializer =
    _$MoneyCurrencyEnumSerializer();

class _$MoneyCurrencyEnumSerializer
    implements PrimitiveSerializer<MoneyCurrencyEnum> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'MZN': 'MZN',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'MZN': 'MZN',
  };

  @override
  final Iterable<Type> types = const <Type>[MoneyCurrencyEnum];
  @override
  final String wireName = 'MoneyCurrencyEnum';

  @override
  Object serialize(Serializers serializers, MoneyCurrencyEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  MoneyCurrencyEnum deserialize(Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      MoneyCurrencyEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$Money extends Money {
  @override
  final int amount;
  @override
  final MoneyCurrencyEnum currency;

  factory _$Money([void Function(MoneyBuilder)? updates]) =>
      (MoneyBuilder()..update(updates))._build();

  _$Money._({required this.amount, required this.currency}) : super._();
  @override
  Money rebuild(void Function(MoneyBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  MoneyBuilder toBuilder() => MoneyBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is Money &&
        amount == other.amount &&
        currency == other.currency;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, amount.hashCode);
    _$hash = $jc(_$hash, currency.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'Money')
          ..add('amount', amount)
          ..add('currency', currency))
        .toString();
  }
}

class MoneyBuilder implements Builder<Money, MoneyBuilder> {
  _$Money? _$v;

  int? _amount;
  int? get amount => _$this._amount;
  set amount(int? amount) => _$this._amount = amount;

  MoneyCurrencyEnum? _currency;
  MoneyCurrencyEnum? get currency => _$this._currency;
  set currency(MoneyCurrencyEnum? currency) => _$this._currency = currency;

  MoneyBuilder() {
    Money._defaults(this);
  }

  MoneyBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _amount = $v.amount;
      _currency = $v.currency;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(Money other) {
    _$v = other as _$Money;
  }

  @override
  void update(void Function(MoneyBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  Money build() => _build();

  _$Money _build() {
    final _$result = _$v ??
        _$Money._(
          amount:
              BuiltValueNullFieldError.checkNotNull(amount, r'Money', 'amount'),
          currency: BuiltValueNullFieldError.checkNotNull(
              currency, r'Money', 'currency'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
