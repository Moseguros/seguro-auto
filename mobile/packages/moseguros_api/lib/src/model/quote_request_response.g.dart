// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'quote_request_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const QuoteRequestResponseOriginEnum _$quoteRequestResponseOriginEnum_CUSTOMER =
    const QuoteRequestResponseOriginEnum._('CUSTOMER');
const QuoteRequestResponseOriginEnum _$quoteRequestResponseOriginEnum_BROKER =
    const QuoteRequestResponseOriginEnum._('BROKER');

QuoteRequestResponseOriginEnum _$quoteRequestResponseOriginEnumValueOf(
    String name) {
  switch (name) {
    case 'CUSTOMER':
      return _$quoteRequestResponseOriginEnum_CUSTOMER;
    case 'BROKER':
      return _$quoteRequestResponseOriginEnum_BROKER;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<QuoteRequestResponseOriginEnum>
    _$quoteRequestResponseOriginEnumValues = BuiltSet<
        QuoteRequestResponseOriginEnum>(const <QuoteRequestResponseOriginEnum>[
  _$quoteRequestResponseOriginEnum_CUSTOMER,
  _$quoteRequestResponseOriginEnum_BROKER,
]);

const QuoteRequestResponseProductTypeEnum
    _$quoteRequestResponseProductTypeEnum_MTPL_ONLY =
    const QuoteRequestResponseProductTypeEnum._('MTPL_ONLY');
const QuoteRequestResponseProductTypeEnum
    _$quoteRequestResponseProductTypeEnum_COMPREHENSIVE =
    const QuoteRequestResponseProductTypeEnum._('COMPREHENSIVE');
const QuoteRequestResponseProductTypeEnum
    _$quoteRequestResponseProductTypeEnum_TEMPORARY =
    const QuoteRequestResponseProductTypeEnum._('TEMPORARY');
const QuoteRequestResponseProductTypeEnum
    _$quoteRequestResponseProductTypeEnum_BORDER =
    const QuoteRequestResponseProductTypeEnum._('BORDER');
const QuoteRequestResponseProductTypeEnum
    _$quoteRequestResponseProductTypeEnum_FLEET =
    const QuoteRequestResponseProductTypeEnum._('FLEET');

QuoteRequestResponseProductTypeEnum
    _$quoteRequestResponseProductTypeEnumValueOf(String name) {
  switch (name) {
    case 'MTPL_ONLY':
      return _$quoteRequestResponseProductTypeEnum_MTPL_ONLY;
    case 'COMPREHENSIVE':
      return _$quoteRequestResponseProductTypeEnum_COMPREHENSIVE;
    case 'TEMPORARY':
      return _$quoteRequestResponseProductTypeEnum_TEMPORARY;
    case 'BORDER':
      return _$quoteRequestResponseProductTypeEnum_BORDER;
    case 'FLEET':
      return _$quoteRequestResponseProductTypeEnum_FLEET;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<QuoteRequestResponseProductTypeEnum>
    _$quoteRequestResponseProductTypeEnumValues = BuiltSet<
        QuoteRequestResponseProductTypeEnum>(const <QuoteRequestResponseProductTypeEnum>[
  _$quoteRequestResponseProductTypeEnum_MTPL_ONLY,
  _$quoteRequestResponseProductTypeEnum_COMPREHENSIVE,
  _$quoteRequestResponseProductTypeEnum_TEMPORARY,
  _$quoteRequestResponseProductTypeEnum_BORDER,
  _$quoteRequestResponseProductTypeEnum_FLEET,
]);

Serializer<QuoteRequestResponseOriginEnum>
    _$quoteRequestResponseOriginEnumSerializer =
    _$QuoteRequestResponseOriginEnumSerializer();
Serializer<QuoteRequestResponseProductTypeEnum>
    _$quoteRequestResponseProductTypeEnumSerializer =
    _$QuoteRequestResponseProductTypeEnumSerializer();

class _$QuoteRequestResponseOriginEnumSerializer
    implements PrimitiveSerializer<QuoteRequestResponseOriginEnum> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'CUSTOMER': 'CUSTOMER',
    'BROKER': 'BROKER',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'CUSTOMER': 'CUSTOMER',
    'BROKER': 'BROKER',
  };

  @override
  final Iterable<Type> types = const <Type>[QuoteRequestResponseOriginEnum];
  @override
  final String wireName = 'QuoteRequestResponseOriginEnum';

  @override
  Object serialize(
          Serializers serializers, QuoteRequestResponseOriginEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  QuoteRequestResponseOriginEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      QuoteRequestResponseOriginEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$QuoteRequestResponseProductTypeEnumSerializer
    implements PrimitiveSerializer<QuoteRequestResponseProductTypeEnum> {
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
    QuoteRequestResponseProductTypeEnum
  ];
  @override
  final String wireName = 'QuoteRequestResponseProductTypeEnum';

  @override
  Object serialize(
          Serializers serializers, QuoteRequestResponseProductTypeEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  QuoteRequestResponseProductTypeEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      QuoteRequestResponseProductTypeEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$QuoteRequestResponse extends QuoteRequestResponse {
  @override
  final String? clientMsisdn;
  @override
  final DateTime createdAt;
  @override
  final QuotedDriver driver;
  @override
  final int durationDays;
  @override
  final Date endDate;
  @override
  final String id;
  @override
  final QuoteRequestResponseOriginEnum origin;
  @override
  final QuoteRequestResponseProductTypeEnum productType;
  @override
  final BuiltList<QuoteResponse> quotes;
  @override
  final BuiltList<RequestedCoverageResponse> requestedCoverages;
  @override
  final Date startDate;
  @override
  final QuotedVehicle vehicle;

  factory _$QuoteRequestResponse(
          [void Function(QuoteRequestResponseBuilder)? updates]) =>
      (QuoteRequestResponseBuilder()..update(updates))._build();

  _$QuoteRequestResponse._(
      {this.clientMsisdn,
      required this.createdAt,
      required this.driver,
      required this.durationDays,
      required this.endDate,
      required this.id,
      required this.origin,
      required this.productType,
      required this.quotes,
      required this.requestedCoverages,
      required this.startDate,
      required this.vehicle})
      : super._();
  @override
  QuoteRequestResponse rebuild(
          void Function(QuoteRequestResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  QuoteRequestResponseBuilder toBuilder() =>
      QuoteRequestResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is QuoteRequestResponse &&
        clientMsisdn == other.clientMsisdn &&
        createdAt == other.createdAt &&
        driver == other.driver &&
        durationDays == other.durationDays &&
        endDate == other.endDate &&
        id == other.id &&
        origin == other.origin &&
        productType == other.productType &&
        quotes == other.quotes &&
        requestedCoverages == other.requestedCoverages &&
        startDate == other.startDate &&
        vehicle == other.vehicle;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, clientMsisdn.hashCode);
    _$hash = $jc(_$hash, createdAt.hashCode);
    _$hash = $jc(_$hash, driver.hashCode);
    _$hash = $jc(_$hash, durationDays.hashCode);
    _$hash = $jc(_$hash, endDate.hashCode);
    _$hash = $jc(_$hash, id.hashCode);
    _$hash = $jc(_$hash, origin.hashCode);
    _$hash = $jc(_$hash, productType.hashCode);
    _$hash = $jc(_$hash, quotes.hashCode);
    _$hash = $jc(_$hash, requestedCoverages.hashCode);
    _$hash = $jc(_$hash, startDate.hashCode);
    _$hash = $jc(_$hash, vehicle.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'QuoteRequestResponse')
          ..add('clientMsisdn', clientMsisdn)
          ..add('createdAt', createdAt)
          ..add('driver', driver)
          ..add('durationDays', durationDays)
          ..add('endDate', endDate)
          ..add('id', id)
          ..add('origin', origin)
          ..add('productType', productType)
          ..add('quotes', quotes)
          ..add('requestedCoverages', requestedCoverages)
          ..add('startDate', startDate)
          ..add('vehicle', vehicle))
        .toString();
  }
}

class QuoteRequestResponseBuilder
    implements Builder<QuoteRequestResponse, QuoteRequestResponseBuilder> {
  _$QuoteRequestResponse? _$v;

  String? _clientMsisdn;
  String? get clientMsisdn => _$this._clientMsisdn;
  set clientMsisdn(String? clientMsisdn) => _$this._clientMsisdn = clientMsisdn;

  DateTime? _createdAt;
  DateTime? get createdAt => _$this._createdAt;
  set createdAt(DateTime? createdAt) => _$this._createdAt = createdAt;

  QuotedDriverBuilder? _driver;
  QuotedDriverBuilder get driver => _$this._driver ??= QuotedDriverBuilder();
  set driver(QuotedDriverBuilder? driver) => _$this._driver = driver;

  int? _durationDays;
  int? get durationDays => _$this._durationDays;
  set durationDays(int? durationDays) => _$this._durationDays = durationDays;

  Date? _endDate;
  Date? get endDate => _$this._endDate;
  set endDate(Date? endDate) => _$this._endDate = endDate;

  String? _id;
  String? get id => _$this._id;
  set id(String? id) => _$this._id = id;

  QuoteRequestResponseOriginEnum? _origin;
  QuoteRequestResponseOriginEnum? get origin => _$this._origin;
  set origin(QuoteRequestResponseOriginEnum? origin) => _$this._origin = origin;

  QuoteRequestResponseProductTypeEnum? _productType;
  QuoteRequestResponseProductTypeEnum? get productType => _$this._productType;
  set productType(QuoteRequestResponseProductTypeEnum? productType) =>
      _$this._productType = productType;

  ListBuilder<QuoteResponse>? _quotes;
  ListBuilder<QuoteResponse> get quotes =>
      _$this._quotes ??= ListBuilder<QuoteResponse>();
  set quotes(ListBuilder<QuoteResponse>? quotes) => _$this._quotes = quotes;

  ListBuilder<RequestedCoverageResponse>? _requestedCoverages;
  ListBuilder<RequestedCoverageResponse> get requestedCoverages =>
      _$this._requestedCoverages ??= ListBuilder<RequestedCoverageResponse>();
  set requestedCoverages(
          ListBuilder<RequestedCoverageResponse>? requestedCoverages) =>
      _$this._requestedCoverages = requestedCoverages;

  Date? _startDate;
  Date? get startDate => _$this._startDate;
  set startDate(Date? startDate) => _$this._startDate = startDate;

  QuotedVehicleBuilder? _vehicle;
  QuotedVehicleBuilder get vehicle =>
      _$this._vehicle ??= QuotedVehicleBuilder();
  set vehicle(QuotedVehicleBuilder? vehicle) => _$this._vehicle = vehicle;

  QuoteRequestResponseBuilder() {
    QuoteRequestResponse._defaults(this);
  }

  QuoteRequestResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _clientMsisdn = $v.clientMsisdn;
      _createdAt = $v.createdAt;
      _driver = $v.driver.toBuilder();
      _durationDays = $v.durationDays;
      _endDate = $v.endDate;
      _id = $v.id;
      _origin = $v.origin;
      _productType = $v.productType;
      _quotes = $v.quotes.toBuilder();
      _requestedCoverages = $v.requestedCoverages.toBuilder();
      _startDate = $v.startDate;
      _vehicle = $v.vehicle.toBuilder();
      _$v = null;
    }
    return this;
  }

  @override
  void replace(QuoteRequestResponse other) {
    _$v = other as _$QuoteRequestResponse;
  }

  @override
  void update(void Function(QuoteRequestResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  QuoteRequestResponse build() => _build();

  _$QuoteRequestResponse _build() {
    _$QuoteRequestResponse _$result;
    try {
      _$result = _$v ??
          _$QuoteRequestResponse._(
            clientMsisdn: clientMsisdn,
            createdAt: BuiltValueNullFieldError.checkNotNull(
                createdAt, r'QuoteRequestResponse', 'createdAt'),
            driver: driver.build(),
            durationDays: BuiltValueNullFieldError.checkNotNull(
                durationDays, r'QuoteRequestResponse', 'durationDays'),
            endDate: BuiltValueNullFieldError.checkNotNull(
                endDate, r'QuoteRequestResponse', 'endDate'),
            id: BuiltValueNullFieldError.checkNotNull(
                id, r'QuoteRequestResponse', 'id'),
            origin: BuiltValueNullFieldError.checkNotNull(
                origin, r'QuoteRequestResponse', 'origin'),
            productType: BuiltValueNullFieldError.checkNotNull(
                productType, r'QuoteRequestResponse', 'productType'),
            quotes: quotes.build(),
            requestedCoverages: requestedCoverages.build(),
            startDate: BuiltValueNullFieldError.checkNotNull(
                startDate, r'QuoteRequestResponse', 'startDate'),
            vehicle: vehicle.build(),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'driver';
        driver.build();

        _$failedField = 'quotes';
        quotes.build();
        _$failedField = 'requestedCoverages';
        requestedCoverages.build();

        _$failedField = 'vehicle';
        vehicle.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'QuoteRequestResponse', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
