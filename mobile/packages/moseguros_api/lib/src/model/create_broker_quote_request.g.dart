// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'create_broker_quote_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const CreateBrokerQuoteRequestProductTypeEnum
    _$createBrokerQuoteRequestProductTypeEnum_MTPL_ONLY =
    const CreateBrokerQuoteRequestProductTypeEnum._('MTPL_ONLY');
const CreateBrokerQuoteRequestProductTypeEnum
    _$createBrokerQuoteRequestProductTypeEnum_COMPREHENSIVE =
    const CreateBrokerQuoteRequestProductTypeEnum._('COMPREHENSIVE');
const CreateBrokerQuoteRequestProductTypeEnum
    _$createBrokerQuoteRequestProductTypeEnum_TEMPORARY =
    const CreateBrokerQuoteRequestProductTypeEnum._('TEMPORARY');
const CreateBrokerQuoteRequestProductTypeEnum
    _$createBrokerQuoteRequestProductTypeEnum_BORDER =
    const CreateBrokerQuoteRequestProductTypeEnum._('BORDER');
const CreateBrokerQuoteRequestProductTypeEnum
    _$createBrokerQuoteRequestProductTypeEnum_FLEET =
    const CreateBrokerQuoteRequestProductTypeEnum._('FLEET');

CreateBrokerQuoteRequestProductTypeEnum
    _$createBrokerQuoteRequestProductTypeEnumValueOf(String name) {
  switch (name) {
    case 'MTPL_ONLY':
      return _$createBrokerQuoteRequestProductTypeEnum_MTPL_ONLY;
    case 'COMPREHENSIVE':
      return _$createBrokerQuoteRequestProductTypeEnum_COMPREHENSIVE;
    case 'TEMPORARY':
      return _$createBrokerQuoteRequestProductTypeEnum_TEMPORARY;
    case 'BORDER':
      return _$createBrokerQuoteRequestProductTypeEnum_BORDER;
    case 'FLEET':
      return _$createBrokerQuoteRequestProductTypeEnum_FLEET;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<CreateBrokerQuoteRequestProductTypeEnum>
    _$createBrokerQuoteRequestProductTypeEnumValues = BuiltSet<
        CreateBrokerQuoteRequestProductTypeEnum>(const <CreateBrokerQuoteRequestProductTypeEnum>[
  _$createBrokerQuoteRequestProductTypeEnum_MTPL_ONLY,
  _$createBrokerQuoteRequestProductTypeEnum_COMPREHENSIVE,
  _$createBrokerQuoteRequestProductTypeEnum_TEMPORARY,
  _$createBrokerQuoteRequestProductTypeEnum_BORDER,
  _$createBrokerQuoteRequestProductTypeEnum_FLEET,
]);

Serializer<CreateBrokerQuoteRequestProductTypeEnum>
    _$createBrokerQuoteRequestProductTypeEnumSerializer =
    _$CreateBrokerQuoteRequestProductTypeEnumSerializer();

class _$CreateBrokerQuoteRequestProductTypeEnumSerializer
    implements PrimitiveSerializer<CreateBrokerQuoteRequestProductTypeEnum> {
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
    CreateBrokerQuoteRequestProductTypeEnum
  ];
  @override
  final String wireName = 'CreateBrokerQuoteRequestProductTypeEnum';

  @override
  Object serialize(Serializers serializers,
          CreateBrokerQuoteRequestProductTypeEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  CreateBrokerQuoteRequestProductTypeEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      CreateBrokerQuoteRequestProductTypeEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$CreateBrokerQuoteRequest extends CreateBrokerQuoteRequest {
  @override
  final String clientMsisdn;
  @override
  final BuiltList<RequestedCoverageRequest>? coverages;
  @override
  final RiskDriverRequest driver;
  @override
  final int durationDays;
  @override
  final CreateBrokerQuoteRequestProductTypeEnum productType;
  @override
  final Date startDate;
  @override
  final BrokerVehicleRequest vehicle;

  factory _$CreateBrokerQuoteRequest(
          [void Function(CreateBrokerQuoteRequestBuilder)? updates]) =>
      (CreateBrokerQuoteRequestBuilder()..update(updates))._build();

  _$CreateBrokerQuoteRequest._(
      {required this.clientMsisdn,
      this.coverages,
      required this.driver,
      required this.durationDays,
      required this.productType,
      required this.startDate,
      required this.vehicle})
      : super._();
  @override
  CreateBrokerQuoteRequest rebuild(
          void Function(CreateBrokerQuoteRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  CreateBrokerQuoteRequestBuilder toBuilder() =>
      CreateBrokerQuoteRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is CreateBrokerQuoteRequest &&
        clientMsisdn == other.clientMsisdn &&
        coverages == other.coverages &&
        driver == other.driver &&
        durationDays == other.durationDays &&
        productType == other.productType &&
        startDate == other.startDate &&
        vehicle == other.vehicle;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, clientMsisdn.hashCode);
    _$hash = $jc(_$hash, coverages.hashCode);
    _$hash = $jc(_$hash, driver.hashCode);
    _$hash = $jc(_$hash, durationDays.hashCode);
    _$hash = $jc(_$hash, productType.hashCode);
    _$hash = $jc(_$hash, startDate.hashCode);
    _$hash = $jc(_$hash, vehicle.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'CreateBrokerQuoteRequest')
          ..add('clientMsisdn', clientMsisdn)
          ..add('coverages', coverages)
          ..add('driver', driver)
          ..add('durationDays', durationDays)
          ..add('productType', productType)
          ..add('startDate', startDate)
          ..add('vehicle', vehicle))
        .toString();
  }
}

class CreateBrokerQuoteRequestBuilder
    implements
        Builder<CreateBrokerQuoteRequest, CreateBrokerQuoteRequestBuilder> {
  _$CreateBrokerQuoteRequest? _$v;

  String? _clientMsisdn;
  String? get clientMsisdn => _$this._clientMsisdn;
  set clientMsisdn(String? clientMsisdn) => _$this._clientMsisdn = clientMsisdn;

  ListBuilder<RequestedCoverageRequest>? _coverages;
  ListBuilder<RequestedCoverageRequest> get coverages =>
      _$this._coverages ??= ListBuilder<RequestedCoverageRequest>();
  set coverages(ListBuilder<RequestedCoverageRequest>? coverages) =>
      _$this._coverages = coverages;

  RiskDriverRequestBuilder? _driver;
  RiskDriverRequestBuilder get driver =>
      _$this._driver ??= RiskDriverRequestBuilder();
  set driver(RiskDriverRequestBuilder? driver) => _$this._driver = driver;

  int? _durationDays;
  int? get durationDays => _$this._durationDays;
  set durationDays(int? durationDays) => _$this._durationDays = durationDays;

  CreateBrokerQuoteRequestProductTypeEnum? _productType;
  CreateBrokerQuoteRequestProductTypeEnum? get productType =>
      _$this._productType;
  set productType(CreateBrokerQuoteRequestProductTypeEnum? productType) =>
      _$this._productType = productType;

  Date? _startDate;
  Date? get startDate => _$this._startDate;
  set startDate(Date? startDate) => _$this._startDate = startDate;

  BrokerVehicleRequestBuilder? _vehicle;
  BrokerVehicleRequestBuilder get vehicle =>
      _$this._vehicle ??= BrokerVehicleRequestBuilder();
  set vehicle(BrokerVehicleRequestBuilder? vehicle) =>
      _$this._vehicle = vehicle;

  CreateBrokerQuoteRequestBuilder() {
    CreateBrokerQuoteRequest._defaults(this);
  }

  CreateBrokerQuoteRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _clientMsisdn = $v.clientMsisdn;
      _coverages = $v.coverages?.toBuilder();
      _driver = $v.driver.toBuilder();
      _durationDays = $v.durationDays;
      _productType = $v.productType;
      _startDate = $v.startDate;
      _vehicle = $v.vehicle.toBuilder();
      _$v = null;
    }
    return this;
  }

  @override
  void replace(CreateBrokerQuoteRequest other) {
    _$v = other as _$CreateBrokerQuoteRequest;
  }

  @override
  void update(void Function(CreateBrokerQuoteRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  CreateBrokerQuoteRequest build() => _build();

  _$CreateBrokerQuoteRequest _build() {
    _$CreateBrokerQuoteRequest _$result;
    try {
      _$result = _$v ??
          _$CreateBrokerQuoteRequest._(
            clientMsisdn: BuiltValueNullFieldError.checkNotNull(
                clientMsisdn, r'CreateBrokerQuoteRequest', 'clientMsisdn'),
            coverages: _coverages?.build(),
            driver: driver.build(),
            durationDays: BuiltValueNullFieldError.checkNotNull(
                durationDays, r'CreateBrokerQuoteRequest', 'durationDays'),
            productType: BuiltValueNullFieldError.checkNotNull(
                productType, r'CreateBrokerQuoteRequest', 'productType'),
            startDate: BuiltValueNullFieldError.checkNotNull(
                startDate, r'CreateBrokerQuoteRequest', 'startDate'),
            vehicle: vehicle.build(),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'coverages';
        _coverages?.build();
        _$failedField = 'driver';
        driver.build();

        _$failedField = 'vehicle';
        vehicle.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'CreateBrokerQuoteRequest', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
