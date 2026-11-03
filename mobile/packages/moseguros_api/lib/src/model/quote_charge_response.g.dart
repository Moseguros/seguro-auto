// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'quote_charge_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$QuoteChargeResponse extends QuoteChargeResponse {
  @override
  final Money amount;
  @override
  final String code;
  @override
  final String labelEn;
  @override
  final String labelPt;

  factory _$QuoteChargeResponse(
          [void Function(QuoteChargeResponseBuilder)? updates]) =>
      (QuoteChargeResponseBuilder()..update(updates))._build();

  _$QuoteChargeResponse._(
      {required this.amount,
      required this.code,
      required this.labelEn,
      required this.labelPt})
      : super._();
  @override
  QuoteChargeResponse rebuild(
          void Function(QuoteChargeResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  QuoteChargeResponseBuilder toBuilder() =>
      QuoteChargeResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is QuoteChargeResponse &&
        amount == other.amount &&
        code == other.code &&
        labelEn == other.labelEn &&
        labelPt == other.labelPt;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, amount.hashCode);
    _$hash = $jc(_$hash, code.hashCode);
    _$hash = $jc(_$hash, labelEn.hashCode);
    _$hash = $jc(_$hash, labelPt.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'QuoteChargeResponse')
          ..add('amount', amount)
          ..add('code', code)
          ..add('labelEn', labelEn)
          ..add('labelPt', labelPt))
        .toString();
  }
}

class QuoteChargeResponseBuilder
    implements Builder<QuoteChargeResponse, QuoteChargeResponseBuilder> {
  _$QuoteChargeResponse? _$v;

  MoneyBuilder? _amount;
  MoneyBuilder get amount => _$this._amount ??= MoneyBuilder();
  set amount(MoneyBuilder? amount) => _$this._amount = amount;

  String? _code;
  String? get code => _$this._code;
  set code(String? code) => _$this._code = code;

  String? _labelEn;
  String? get labelEn => _$this._labelEn;
  set labelEn(String? labelEn) => _$this._labelEn = labelEn;

  String? _labelPt;
  String? get labelPt => _$this._labelPt;
  set labelPt(String? labelPt) => _$this._labelPt = labelPt;

  QuoteChargeResponseBuilder() {
    QuoteChargeResponse._defaults(this);
  }

  QuoteChargeResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _amount = $v.amount.toBuilder();
      _code = $v.code;
      _labelEn = $v.labelEn;
      _labelPt = $v.labelPt;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(QuoteChargeResponse other) {
    _$v = other as _$QuoteChargeResponse;
  }

  @override
  void update(void Function(QuoteChargeResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  QuoteChargeResponse build() => _build();

  _$QuoteChargeResponse _build() {
    _$QuoteChargeResponse _$result;
    try {
      _$result = _$v ??
          _$QuoteChargeResponse._(
            amount: amount.build(),
            code: BuiltValueNullFieldError.checkNotNull(
                code, r'QuoteChargeResponse', 'code'),
            labelEn: BuiltValueNullFieldError.checkNotNull(
                labelEn, r'QuoteChargeResponse', 'labelEn'),
            labelPt: BuiltValueNullFieldError.checkNotNull(
                labelPt, r'QuoteChargeResponse', 'labelPt'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'amount';
        amount.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'QuoteChargeResponse', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
