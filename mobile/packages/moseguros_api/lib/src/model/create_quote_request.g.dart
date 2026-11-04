// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'create_quote_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const CreateQuoteRequestProductTypeEnum
    _$createQuoteRequestProductTypeEnum_MTPL_ONLY =
    const CreateQuoteRequestProductTypeEnum._('MTPL_ONLY');
const CreateQuoteRequestProductTypeEnum
    _$createQuoteRequestProductTypeEnum_COMPREHENSIVE =
    const CreateQuoteRequestProductTypeEnum._('COMPREHENSIVE');
const CreateQuoteRequestProductTypeEnum
    _$createQuoteRequestProductTypeEnum_TEMPORARY =
    const CreateQuoteRequestProductTypeEnum._('TEMPORARY');
const CreateQuoteRequestProductTypeEnum
    _$createQuoteRequestProductTypeEnum_BORDER =
    const CreateQuoteRequestProductTypeEnum._('BORDER');
const CreateQuoteRequestProductTypeEnum
    _$createQuoteRequestProductTypeEnum_FLEET =
    const CreateQuoteRequestProductTypeEnum._('FLEET');

CreateQuoteRequestProductTypeEnum _$createQuoteRequestProductTypeEnumValueOf(
    String name) {
  switch (name) {
    case 'MTPL_ONLY':
      return _$createQuoteRequestProductTypeEnum_MTPL_ONLY;
    case 'COMPREHENSIVE':
      return _$createQuoteRequestProductTypeEnum_COMPREHENSIVE;
    case 'TEMPORARY':
      return _$createQuoteRequestProductTypeEnum_TEMPORARY;
    case 'BORDER':
      return _$createQuoteRequestProductTypeEnum_BORDER;
    case 'FLEET':
      return _$createQuoteRequestProductTypeEnum_FLEET;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<CreateQuoteRequestProductTypeEnum>
    _$createQuoteRequestProductTypeEnumValues = BuiltSet<
        CreateQuoteRequestProductTypeEnum>(const <CreateQuoteRequestProductTypeEnum>[
  _$createQuoteRequestProductTypeEnum_MTPL_ONLY,
  _$createQuoteRequestProductTypeEnum_COMPREHENSIVE,
  _$createQuoteRequestProductTypeEnum_TEMPORARY,
  _$createQuoteRequestProductTypeEnum_BORDER,
  _$createQuoteRequestProductTypeEnum_FLEET,
]);

Serializer<CreateQuoteRequestProductTypeEnum>
    _$createQuoteRequestProductTypeEnumSerializer =
    _$CreateQuoteRequestProductTypeEnumSerializer();

class _$CreateQuoteRequestProductTypeEnumSerializer
    implements PrimitiveSerializer<CreateQuoteRequestProductTypeEnum> {
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
  final Iterable<Type> types = const <Type>[CreateQuoteRequestProductTypeEnum];
  @override
  final String wireName = 'CreateQuoteRequestProductTypeEnum';

  @override
  Object serialize(
          Serializers serializers, CreateQuoteRequestProductTypeEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  CreateQuoteRequestProductTypeEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      CreateQuoteRequestProductTypeEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$CreateQuoteRequest extends CreateQuoteRequest {
  @override
  final BuiltList<RequestedCoverageRequest>? coverages;
  @override
  final String driverId;
  @override
  final int durationDays;
  @override
  final CreateQuoteRequestProductTypeEnum productType;
  @override
  final Date startDate;
  @override
  final String vehicleId;

  factory _$CreateQuoteRequest(
          [void Function(CreateQuoteRequestBuilder)? updates]) =>
      (CreateQuoteRequestBuilder()..update(updates))._build();

  _$CreateQuoteRequest._(
      {this.coverages,
      required this.driverId,
      required this.durationDays,
      required this.productType,
      required this.startDate,
      required this.vehicleId})
      : super._();
  @override
  CreateQuoteRequest rebuild(
          void Function(CreateQuoteRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  CreateQuoteRequestBuilder toBuilder() =>
      CreateQuoteRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is CreateQuoteRequest &&
        coverages == other.coverages &&
        driverId == other.driverId &&
        durationDays == other.durationDays &&
        productType == other.productType &&
        startDate == other.startDate &&
        vehicleId == other.vehicleId;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, coverages.hashCode);
    _$hash = $jc(_$hash, driverId.hashCode);
    _$hash = $jc(_$hash, durationDays.hashCode);
    _$hash = $jc(_$hash, productType.hashCode);
    _$hash = $jc(_$hash, startDate.hashCode);
    _$hash = $jc(_$hash, vehicleId.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'CreateQuoteRequest')
          ..add('coverages', coverages)
          ..add('driverId', driverId)
          ..add('durationDays', durationDays)
          ..add('productType', productType)
          ..add('startDate', startDate)
          ..add('vehicleId', vehicleId))
        .toString();
  }
}

class CreateQuoteRequestBuilder
    implements Builder<CreateQuoteRequest, CreateQuoteRequestBuilder> {
  _$CreateQuoteRequest? _$v;

  ListBuilder<RequestedCoverageRequest>? _coverages;
  ListBuilder<RequestedCoverageRequest> get coverages =>
      _$this._coverages ??= ListBuilder<RequestedCoverageRequest>();
  set coverages(ListBuilder<RequestedCoverageRequest>? coverages) =>
      _$this._coverages = coverages;

  String? _driverId;
  String? get driverId => _$this._driverId;
  set driverId(String? driverId) => _$this._driverId = driverId;

  int? _durationDays;
  int? get durationDays => _$this._durationDays;
  set durationDays(int? durationDays) => _$this._durationDays = durationDays;

  CreateQuoteRequestProductTypeEnum? _productType;
  CreateQuoteRequestProductTypeEnum? get productType => _$this._productType;
  set productType(CreateQuoteRequestProductTypeEnum? productType) =>
      _$this._productType = productType;

  Date? _startDate;
  Date? get startDate => _$this._startDate;
  set startDate(Date? startDate) => _$this._startDate = startDate;

  String? _vehicleId;
  String? get vehicleId => _$this._vehicleId;
  set vehicleId(String? vehicleId) => _$this._vehicleId = vehicleId;

  CreateQuoteRequestBuilder() {
    CreateQuoteRequest._defaults(this);
  }

  CreateQuoteRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _coverages = $v.coverages?.toBuilder();
      _driverId = $v.driverId;
      _durationDays = $v.durationDays;
      _productType = $v.productType;
      _startDate = $v.startDate;
      _vehicleId = $v.vehicleId;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(CreateQuoteRequest other) {
    _$v = other as _$CreateQuoteRequest;
  }

  @override
  void update(void Function(CreateQuoteRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  CreateQuoteRequest build() => _build();

  _$CreateQuoteRequest _build() {
    _$CreateQuoteRequest _$result;
    try {
      _$result = _$v ??
          _$CreateQuoteRequest._(
            coverages: _coverages?.build(),
            driverId: BuiltValueNullFieldError.checkNotNull(
                driverId, r'CreateQuoteRequest', 'driverId'),
            durationDays: BuiltValueNullFieldError.checkNotNull(
                durationDays, r'CreateQuoteRequest', 'durationDays'),
            productType: BuiltValueNullFieldError.checkNotNull(
                productType, r'CreateQuoteRequest', 'productType'),
            startDate: BuiltValueNullFieldError.checkNotNull(
                startDate, r'CreateQuoteRequest', 'startDate'),
            vehicleId: BuiltValueNullFieldError.checkNotNull(
                vehicleId, r'CreateQuoteRequest', 'vehicleId'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'coverages';
        _coverages?.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'CreateQuoteRequest', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
