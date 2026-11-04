// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'quote_request_summary_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const QuoteRequestSummaryResponseOriginEnum
    _$quoteRequestSummaryResponseOriginEnum_CUSTOMER =
    const QuoteRequestSummaryResponseOriginEnum._('CUSTOMER');
const QuoteRequestSummaryResponseOriginEnum
    _$quoteRequestSummaryResponseOriginEnum_BROKER =
    const QuoteRequestSummaryResponseOriginEnum._('BROKER');

QuoteRequestSummaryResponseOriginEnum
    _$quoteRequestSummaryResponseOriginEnumValueOf(String name) {
  switch (name) {
    case 'CUSTOMER':
      return _$quoteRequestSummaryResponseOriginEnum_CUSTOMER;
    case 'BROKER':
      return _$quoteRequestSummaryResponseOriginEnum_BROKER;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<QuoteRequestSummaryResponseOriginEnum>
    _$quoteRequestSummaryResponseOriginEnumValues = BuiltSet<
        QuoteRequestSummaryResponseOriginEnum>(const <QuoteRequestSummaryResponseOriginEnum>[
  _$quoteRequestSummaryResponseOriginEnum_CUSTOMER,
  _$quoteRequestSummaryResponseOriginEnum_BROKER,
]);

const QuoteRequestSummaryResponseProductTypeEnum
    _$quoteRequestSummaryResponseProductTypeEnum_MTPL_ONLY =
    const QuoteRequestSummaryResponseProductTypeEnum._('MTPL_ONLY');
const QuoteRequestSummaryResponseProductTypeEnum
    _$quoteRequestSummaryResponseProductTypeEnum_COMPREHENSIVE =
    const QuoteRequestSummaryResponseProductTypeEnum._('COMPREHENSIVE');
const QuoteRequestSummaryResponseProductTypeEnum
    _$quoteRequestSummaryResponseProductTypeEnum_TEMPORARY =
    const QuoteRequestSummaryResponseProductTypeEnum._('TEMPORARY');
const QuoteRequestSummaryResponseProductTypeEnum
    _$quoteRequestSummaryResponseProductTypeEnum_BORDER =
    const QuoteRequestSummaryResponseProductTypeEnum._('BORDER');
const QuoteRequestSummaryResponseProductTypeEnum
    _$quoteRequestSummaryResponseProductTypeEnum_FLEET =
    const QuoteRequestSummaryResponseProductTypeEnum._('FLEET');

QuoteRequestSummaryResponseProductTypeEnum
    _$quoteRequestSummaryResponseProductTypeEnumValueOf(String name) {
  switch (name) {
    case 'MTPL_ONLY':
      return _$quoteRequestSummaryResponseProductTypeEnum_MTPL_ONLY;
    case 'COMPREHENSIVE':
      return _$quoteRequestSummaryResponseProductTypeEnum_COMPREHENSIVE;
    case 'TEMPORARY':
      return _$quoteRequestSummaryResponseProductTypeEnum_TEMPORARY;
    case 'BORDER':
      return _$quoteRequestSummaryResponseProductTypeEnum_BORDER;
    case 'FLEET':
      return _$quoteRequestSummaryResponseProductTypeEnum_FLEET;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<QuoteRequestSummaryResponseProductTypeEnum>
    _$quoteRequestSummaryResponseProductTypeEnumValues = BuiltSet<
        QuoteRequestSummaryResponseProductTypeEnum>(const <QuoteRequestSummaryResponseProductTypeEnum>[
  _$quoteRequestSummaryResponseProductTypeEnum_MTPL_ONLY,
  _$quoteRequestSummaryResponseProductTypeEnum_COMPREHENSIVE,
  _$quoteRequestSummaryResponseProductTypeEnum_TEMPORARY,
  _$quoteRequestSummaryResponseProductTypeEnum_BORDER,
  _$quoteRequestSummaryResponseProductTypeEnum_FLEET,
]);

Serializer<QuoteRequestSummaryResponseOriginEnum>
    _$quoteRequestSummaryResponseOriginEnumSerializer =
    _$QuoteRequestSummaryResponseOriginEnumSerializer();
Serializer<QuoteRequestSummaryResponseProductTypeEnum>
    _$quoteRequestSummaryResponseProductTypeEnumSerializer =
    _$QuoteRequestSummaryResponseProductTypeEnumSerializer();

class _$QuoteRequestSummaryResponseOriginEnumSerializer
    implements PrimitiveSerializer<QuoteRequestSummaryResponseOriginEnum> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'CUSTOMER': 'CUSTOMER',
    'BROKER': 'BROKER',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'CUSTOMER': 'CUSTOMER',
    'BROKER': 'BROKER',
  };

  @override
  final Iterable<Type> types = const <Type>[
    QuoteRequestSummaryResponseOriginEnum
  ];
  @override
  final String wireName = 'QuoteRequestSummaryResponseOriginEnum';

  @override
  Object serialize(
          Serializers serializers, QuoteRequestSummaryResponseOriginEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  QuoteRequestSummaryResponseOriginEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      QuoteRequestSummaryResponseOriginEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$QuoteRequestSummaryResponseProductTypeEnumSerializer
    implements PrimitiveSerializer<QuoteRequestSummaryResponseProductTypeEnum> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'MTPL_ONLY': 'MTPL_ONLY',
    'COMPREHENSIVE': 'COMPREHENSIVE',
    'TEMPORARY': 'TEMPORARY',
    'BORDER': 'BORDER',
    'FLEET': 'FLEET',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'MTPL_ONLY': 'MTPL_ONLY',
    'COMPREHENSIVE': 'COMPREHENSIVE',
    'TEMPORARY': 'TEMPORARY',
    'BORDER': 'BORDER',
    'FLEET': 'FLEET',
  };

  @override
  final Iterable<Type> types = const <Type>[
    QuoteRequestSummaryResponseProductTypeEnum
  ];
  @override
  final String wireName = 'QuoteRequestSummaryResponseProductTypeEnum';

  @override
  Object serialize(Serializers serializers,
          QuoteRequestSummaryResponseProductTypeEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  QuoteRequestSummaryResponseProductTypeEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      QuoteRequestSummaryResponseProductTypeEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$QuoteRequestSummaryResponse extends QuoteRequestSummaryResponse {
  @override
  final Money? bestTotal;
  @override
  final String? clientMsisdn;
  @override
  final DateTime createdAt;
  @override
  final int durationDays;
  @override
  final bool expired;
  @override
  final String id;
  @override
  final int offeredCount;
  @override
  final QuoteRequestSummaryResponseOriginEnum origin;
  @override
  final QuoteRequestSummaryResponseProductTypeEnum productType;
  @override
  final Date startDate;
  @override
  final DateTime? validUntil;
  @override
  final QuoteSummaryVehicle vehicle;

  factory _$QuoteRequestSummaryResponse(
          [void Function(QuoteRequestSummaryResponseBuilder)? updates]) =>
      (QuoteRequestSummaryResponseBuilder()..update(updates))._build();

  _$QuoteRequestSummaryResponse._(
      {this.bestTotal,
      this.clientMsisdn,
      required this.createdAt,
      required this.durationDays,
      required this.expired,
      required this.id,
      required this.offeredCount,
      required this.origin,
      required this.productType,
      required this.startDate,
      this.validUntil,
      required this.vehicle})
      : super._();
  @override
  QuoteRequestSummaryResponse rebuild(
          void Function(QuoteRequestSummaryResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  QuoteRequestSummaryResponseBuilder toBuilder() =>
      QuoteRequestSummaryResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is QuoteRequestSummaryResponse &&
        bestTotal == other.bestTotal &&
        clientMsisdn == other.clientMsisdn &&
        createdAt == other.createdAt &&
        durationDays == other.durationDays &&
        expired == other.expired &&
        id == other.id &&
        offeredCount == other.offeredCount &&
        origin == other.origin &&
        productType == other.productType &&
        startDate == other.startDate &&
        validUntil == other.validUntil &&
        vehicle == other.vehicle;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, bestTotal.hashCode);
    _$hash = $jc(_$hash, clientMsisdn.hashCode);
    _$hash = $jc(_$hash, createdAt.hashCode);
    _$hash = $jc(_$hash, durationDays.hashCode);
    _$hash = $jc(_$hash, expired.hashCode);
    _$hash = $jc(_$hash, id.hashCode);
    _$hash = $jc(_$hash, offeredCount.hashCode);
    _$hash = $jc(_$hash, origin.hashCode);
    _$hash = $jc(_$hash, productType.hashCode);
    _$hash = $jc(_$hash, startDate.hashCode);
    _$hash = $jc(_$hash, validUntil.hashCode);
    _$hash = $jc(_$hash, vehicle.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'QuoteRequestSummaryResponse')
          ..add('bestTotal', bestTotal)
          ..add('clientMsisdn', clientMsisdn)
          ..add('createdAt', createdAt)
          ..add('durationDays', durationDays)
          ..add('expired', expired)
          ..add('id', id)
          ..add('offeredCount', offeredCount)
          ..add('origin', origin)
          ..add('productType', productType)
          ..add('startDate', startDate)
          ..add('validUntil', validUntil)
          ..add('vehicle', vehicle))
        .toString();
  }
}

class QuoteRequestSummaryResponseBuilder
    implements
        Builder<QuoteRequestSummaryResponse,
            QuoteRequestSummaryResponseBuilder> {
  _$QuoteRequestSummaryResponse? _$v;

  MoneyBuilder? _bestTotal;
  MoneyBuilder get bestTotal => _$this._bestTotal ??= MoneyBuilder();
  set bestTotal(MoneyBuilder? bestTotal) => _$this._bestTotal = bestTotal;

  String? _clientMsisdn;
  String? get clientMsisdn => _$this._clientMsisdn;
  set clientMsisdn(String? clientMsisdn) => _$this._clientMsisdn = clientMsisdn;

  DateTime? _createdAt;
  DateTime? get createdAt => _$this._createdAt;
  set createdAt(DateTime? createdAt) => _$this._createdAt = createdAt;

  int? _durationDays;
  int? get durationDays => _$this._durationDays;
  set durationDays(int? durationDays) => _$this._durationDays = durationDays;

  bool? _expired;
  bool? get expired => _$this._expired;
  set expired(bool? expired) => _$this._expired = expired;

  String? _id;
  String? get id => _$this._id;
  set id(String? id) => _$this._id = id;

  int? _offeredCount;
  int? get offeredCount => _$this._offeredCount;
  set offeredCount(int? offeredCount) => _$this._offeredCount = offeredCount;

  QuoteRequestSummaryResponseOriginEnum? _origin;
  QuoteRequestSummaryResponseOriginEnum? get origin => _$this._origin;
  set origin(QuoteRequestSummaryResponseOriginEnum? origin) =>
      _$this._origin = origin;

  QuoteRequestSummaryResponseProductTypeEnum? _productType;
  QuoteRequestSummaryResponseProductTypeEnum? get productType =>
      _$this._productType;
  set productType(QuoteRequestSummaryResponseProductTypeEnum? productType) =>
      _$this._productType = productType;

  Date? _startDate;
  Date? get startDate => _$this._startDate;
  set startDate(Date? startDate) => _$this._startDate = startDate;

  DateTime? _validUntil;
  DateTime? get validUntil => _$this._validUntil;
  set validUntil(DateTime? validUntil) => _$this._validUntil = validUntil;

  QuoteSummaryVehicleBuilder? _vehicle;
  QuoteSummaryVehicleBuilder get vehicle =>
      _$this._vehicle ??= QuoteSummaryVehicleBuilder();
  set vehicle(QuoteSummaryVehicleBuilder? vehicle) => _$this._vehicle = vehicle;

  QuoteRequestSummaryResponseBuilder() {
    QuoteRequestSummaryResponse._defaults(this);
  }

  QuoteRequestSummaryResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _bestTotal = $v.bestTotal?.toBuilder();
      _clientMsisdn = $v.clientMsisdn;
      _createdAt = $v.createdAt;
      _durationDays = $v.durationDays;
      _expired = $v.expired;
      _id = $v.id;
      _offeredCount = $v.offeredCount;
      _origin = $v.origin;
      _productType = $v.productType;
      _startDate = $v.startDate;
      _validUntil = $v.validUntil;
      _vehicle = $v.vehicle.toBuilder();
      _$v = null;
    }
    return this;
  }

  @override
  void replace(QuoteRequestSummaryResponse other) {
    _$v = other as _$QuoteRequestSummaryResponse;
  }

  @override
  void update(void Function(QuoteRequestSummaryResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  QuoteRequestSummaryResponse build() => _build();

  _$QuoteRequestSummaryResponse _build() {
    _$QuoteRequestSummaryResponse _$result;
    try {
      _$result = _$v ??
          _$QuoteRequestSummaryResponse._(
            bestTotal: _bestTotal?.build(),
            clientMsisdn: clientMsisdn,
            createdAt: BuiltValueNullFieldError.checkNotNull(
                createdAt, r'QuoteRequestSummaryResponse', 'createdAt'),
            durationDays: BuiltValueNullFieldError.checkNotNull(
                durationDays, r'QuoteRequestSummaryResponse', 'durationDays'),
            expired: BuiltValueNullFieldError.checkNotNull(
                expired, r'QuoteRequestSummaryResponse', 'expired'),
            id: BuiltValueNullFieldError.checkNotNull(
                id, r'QuoteRequestSummaryResponse', 'id'),
            offeredCount: BuiltValueNullFieldError.checkNotNull(
                offeredCount, r'QuoteRequestSummaryResponse', 'offeredCount'),
            origin: BuiltValueNullFieldError.checkNotNull(
                origin, r'QuoteRequestSummaryResponse', 'origin'),
            productType: BuiltValueNullFieldError.checkNotNull(
                productType, r'QuoteRequestSummaryResponse', 'productType'),
            startDate: BuiltValueNullFieldError.checkNotNull(
                startDate, r'QuoteRequestSummaryResponse', 'startDate'),
            validUntil: validUntil,
            vehicle: vehicle.build(),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'bestTotal';
        _bestTotal?.build();

        _$failedField = 'vehicle';
        vehicle.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'QuoteRequestSummaryResponse', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
