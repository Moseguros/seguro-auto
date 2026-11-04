// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'quote_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const QuoteResponseStatusEnum _$quoteResponseStatusEnum_OFFERED =
    const QuoteResponseStatusEnum._('OFFERED');
const QuoteResponseStatusEnum _$quoteResponseStatusEnum_NOT_AVAILABLE =
    const QuoteResponseStatusEnum._('NOT_AVAILABLE');
const QuoteResponseStatusEnum _$quoteResponseStatusEnum_EXPIRED =
    const QuoteResponseStatusEnum._('EXPIRED');
const QuoteResponseStatusEnum _$quoteResponseStatusEnum_USED =
    const QuoteResponseStatusEnum._('USED');

QuoteResponseStatusEnum _$quoteResponseStatusEnumValueOf(String name) {
  switch (name) {
    case 'OFFERED':
      return _$quoteResponseStatusEnum_OFFERED;
    case 'NOT_AVAILABLE':
      return _$quoteResponseStatusEnum_NOT_AVAILABLE;
    case 'EXPIRED':
      return _$quoteResponseStatusEnum_EXPIRED;
    case 'USED':
      return _$quoteResponseStatusEnum_USED;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<QuoteResponseStatusEnum> _$quoteResponseStatusEnumValues =
    BuiltSet<QuoteResponseStatusEnum>(const <QuoteResponseStatusEnum>[
  _$quoteResponseStatusEnum_OFFERED,
  _$quoteResponseStatusEnum_NOT_AVAILABLE,
  _$quoteResponseStatusEnum_EXPIRED,
  _$quoteResponseStatusEnum_USED,
]);

Serializer<QuoteResponseStatusEnum> _$quoteResponseStatusEnumSerializer =
    _$QuoteResponseStatusEnumSerializer();

class _$QuoteResponseStatusEnumSerializer
    implements PrimitiveSerializer<QuoteResponseStatusEnum> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'OFFERED': 'OFFERED',
    'NOT_AVAILABLE': 'NOT_AVAILABLE',
    'EXPIRED': 'EXPIRED',
    'USED': 'USED',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'OFFERED': 'OFFERED',
    'NOT_AVAILABLE': 'NOT_AVAILABLE',
    'EXPIRED': 'EXPIRED',
    'USED': 'USED',
  };

  @override
  final Iterable<Type> types = const <Type>[QuoteResponseStatusEnum];
  @override
  final String wireName = 'QuoteResponseStatusEnum';

  @override
  Object serialize(Serializers serializers, QuoteResponseStatusEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  QuoteResponseStatusEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      QuoteResponseStatusEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$QuoteResponse extends QuoteResponse {
  @override
  final BuiltList<QuoteChargeResponse>? charges;
  @override
  final Money? commercialPremium;
  @override
  final BuiltList<QuoteCoverageResponse>? coverages;
  @override
  final String id;
  @override
  final QuoteInsurer insurer;
  @override
  final QuoteProduct product;
  @override
  final Money? roundingAdjustment;
  @override
  final QuoteResponseStatusEnum status;
  @override
  final Money? totalAmount;
  @override
  final BuiltMap<String, JsonObject?>? unavailableDetail;
  @override
  final UnavailableReason? unavailableReason;
  @override
  final DateTime? validUntil;

  factory _$QuoteResponse([void Function(QuoteResponseBuilder)? updates]) =>
      (QuoteResponseBuilder()..update(updates))._build();

  _$QuoteResponse._(
      {this.charges,
      this.commercialPremium,
      this.coverages,
      required this.id,
      required this.insurer,
      required this.product,
      this.roundingAdjustment,
      required this.status,
      this.totalAmount,
      this.unavailableDetail,
      this.unavailableReason,
      this.validUntil})
      : super._();
  @override
  QuoteResponse rebuild(void Function(QuoteResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  QuoteResponseBuilder toBuilder() => QuoteResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is QuoteResponse &&
        charges == other.charges &&
        commercialPremium == other.commercialPremium &&
        coverages == other.coverages &&
        id == other.id &&
        insurer == other.insurer &&
        product == other.product &&
        roundingAdjustment == other.roundingAdjustment &&
        status == other.status &&
        totalAmount == other.totalAmount &&
        unavailableDetail == other.unavailableDetail &&
        unavailableReason == other.unavailableReason &&
        validUntil == other.validUntil;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, charges.hashCode);
    _$hash = $jc(_$hash, commercialPremium.hashCode);
    _$hash = $jc(_$hash, coverages.hashCode);
    _$hash = $jc(_$hash, id.hashCode);
    _$hash = $jc(_$hash, insurer.hashCode);
    _$hash = $jc(_$hash, product.hashCode);
    _$hash = $jc(_$hash, roundingAdjustment.hashCode);
    _$hash = $jc(_$hash, status.hashCode);
    _$hash = $jc(_$hash, totalAmount.hashCode);
    _$hash = $jc(_$hash, unavailableDetail.hashCode);
    _$hash = $jc(_$hash, unavailableReason.hashCode);
    _$hash = $jc(_$hash, validUntil.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'QuoteResponse')
          ..add('charges', charges)
          ..add('commercialPremium', commercialPremium)
          ..add('coverages', coverages)
          ..add('id', id)
          ..add('insurer', insurer)
          ..add('product', product)
          ..add('roundingAdjustment', roundingAdjustment)
          ..add('status', status)
          ..add('totalAmount', totalAmount)
          ..add('unavailableDetail', unavailableDetail)
          ..add('unavailableReason', unavailableReason)
          ..add('validUntil', validUntil))
        .toString();
  }
}

class QuoteResponseBuilder
    implements Builder<QuoteResponse, QuoteResponseBuilder> {
  _$QuoteResponse? _$v;

  ListBuilder<QuoteChargeResponse>? _charges;
  ListBuilder<QuoteChargeResponse> get charges =>
      _$this._charges ??= ListBuilder<QuoteChargeResponse>();
  set charges(ListBuilder<QuoteChargeResponse>? charges) =>
      _$this._charges = charges;

  MoneyBuilder? _commercialPremium;
  MoneyBuilder get commercialPremium =>
      _$this._commercialPremium ??= MoneyBuilder();
  set commercialPremium(MoneyBuilder? commercialPremium) =>
      _$this._commercialPremium = commercialPremium;

  ListBuilder<QuoteCoverageResponse>? _coverages;
  ListBuilder<QuoteCoverageResponse> get coverages =>
      _$this._coverages ??= ListBuilder<QuoteCoverageResponse>();
  set coverages(ListBuilder<QuoteCoverageResponse>? coverages) =>
      _$this._coverages = coverages;

  String? _id;
  String? get id => _$this._id;
  set id(String? id) => _$this._id = id;

  QuoteInsurerBuilder? _insurer;
  QuoteInsurerBuilder get insurer => _$this._insurer ??= QuoteInsurerBuilder();
  set insurer(QuoteInsurerBuilder? insurer) => _$this._insurer = insurer;

  QuoteProductBuilder? _product;
  QuoteProductBuilder get product => _$this._product ??= QuoteProductBuilder();
  set product(QuoteProductBuilder? product) => _$this._product = product;

  MoneyBuilder? _roundingAdjustment;
  MoneyBuilder get roundingAdjustment =>
      _$this._roundingAdjustment ??= MoneyBuilder();
  set roundingAdjustment(MoneyBuilder? roundingAdjustment) =>
      _$this._roundingAdjustment = roundingAdjustment;

  QuoteResponseStatusEnum? _status;
  QuoteResponseStatusEnum? get status => _$this._status;
  set status(QuoteResponseStatusEnum? status) => _$this._status = status;

  MoneyBuilder? _totalAmount;
  MoneyBuilder get totalAmount => _$this._totalAmount ??= MoneyBuilder();
  set totalAmount(MoneyBuilder? totalAmount) =>
      _$this._totalAmount = totalAmount;

  MapBuilder<String, JsonObject?>? _unavailableDetail;
  MapBuilder<String, JsonObject?> get unavailableDetail =>
      _$this._unavailableDetail ??= MapBuilder<String, JsonObject?>();
  set unavailableDetail(MapBuilder<String, JsonObject?>? unavailableDetail) =>
      _$this._unavailableDetail = unavailableDetail;

  UnavailableReason? _unavailableReason;
  UnavailableReason? get unavailableReason => _$this._unavailableReason;
  set unavailableReason(UnavailableReason? unavailableReason) =>
      _$this._unavailableReason = unavailableReason;

  DateTime? _validUntil;
  DateTime? get validUntil => _$this._validUntil;
  set validUntil(DateTime? validUntil) => _$this._validUntil = validUntil;

  QuoteResponseBuilder() {
    QuoteResponse._defaults(this);
  }

  QuoteResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _charges = $v.charges?.toBuilder();
      _commercialPremium = $v.commercialPremium?.toBuilder();
      _coverages = $v.coverages?.toBuilder();
      _id = $v.id;
      _insurer = $v.insurer.toBuilder();
      _product = $v.product.toBuilder();
      _roundingAdjustment = $v.roundingAdjustment?.toBuilder();
      _status = $v.status;
      _totalAmount = $v.totalAmount?.toBuilder();
      _unavailableDetail = $v.unavailableDetail?.toBuilder();
      _unavailableReason = $v.unavailableReason;
      _validUntil = $v.validUntil;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(QuoteResponse other) {
    _$v = other as _$QuoteResponse;
  }

  @override
  void update(void Function(QuoteResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  QuoteResponse build() => _build();

  _$QuoteResponse _build() {
    _$QuoteResponse _$result;
    try {
      _$result = _$v ??
          _$QuoteResponse._(
            charges: _charges?.build(),
            commercialPremium: _commercialPremium?.build(),
            coverages: _coverages?.build(),
            id: BuiltValueNullFieldError.checkNotNull(
                id, r'QuoteResponse', 'id'),
            insurer: insurer.build(),
            product: product.build(),
            roundingAdjustment: _roundingAdjustment?.build(),
            status: BuiltValueNullFieldError.checkNotNull(
                status, r'QuoteResponse', 'status'),
            totalAmount: _totalAmount?.build(),
            unavailableDetail: _unavailableDetail?.build(),
            unavailableReason: unavailableReason,
            validUntil: validUntil,
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'charges';
        _charges?.build();
        _$failedField = 'commercialPremium';
        _commercialPremium?.build();
        _$failedField = 'coverages';
        _coverages?.build();

        _$failedField = 'insurer';
        insurer.build();
        _$failedField = 'product';
        product.build();
        _$failedField = 'roundingAdjustment';
        _roundingAdjustment?.build();

        _$failedField = 'totalAmount';
        _totalAmount?.build();
        _$failedField = 'unavailableDetail';
        _unavailableDetail?.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'QuoteResponse', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
