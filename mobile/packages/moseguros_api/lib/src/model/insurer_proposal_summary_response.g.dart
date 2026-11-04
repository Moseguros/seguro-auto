// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'insurer_proposal_summary_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const InsurerProposalSummaryResponseProductTypeEnum
    _$insurerProposalSummaryResponseProductTypeEnum_MTPL_ONLY =
    const InsurerProposalSummaryResponseProductTypeEnum._('MTPL_ONLY');
const InsurerProposalSummaryResponseProductTypeEnum
    _$insurerProposalSummaryResponseProductTypeEnum_COMPREHENSIVE =
    const InsurerProposalSummaryResponseProductTypeEnum._('COMPREHENSIVE');
const InsurerProposalSummaryResponseProductTypeEnum
    _$insurerProposalSummaryResponseProductTypeEnum_TEMPORARY =
    const InsurerProposalSummaryResponseProductTypeEnum._('TEMPORARY');
const InsurerProposalSummaryResponseProductTypeEnum
    _$insurerProposalSummaryResponseProductTypeEnum_BORDER =
    const InsurerProposalSummaryResponseProductTypeEnum._('BORDER');
const InsurerProposalSummaryResponseProductTypeEnum
    _$insurerProposalSummaryResponseProductTypeEnum_FLEET =
    const InsurerProposalSummaryResponseProductTypeEnum._('FLEET');

InsurerProposalSummaryResponseProductTypeEnum
    _$insurerProposalSummaryResponseProductTypeEnumValueOf(String name) {
  switch (name) {
    case 'MTPL_ONLY':
      return _$insurerProposalSummaryResponseProductTypeEnum_MTPL_ONLY;
    case 'COMPREHENSIVE':
      return _$insurerProposalSummaryResponseProductTypeEnum_COMPREHENSIVE;
    case 'TEMPORARY':
      return _$insurerProposalSummaryResponseProductTypeEnum_TEMPORARY;
    case 'BORDER':
      return _$insurerProposalSummaryResponseProductTypeEnum_BORDER;
    case 'FLEET':
      return _$insurerProposalSummaryResponseProductTypeEnum_FLEET;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<InsurerProposalSummaryResponseProductTypeEnum>
    _$insurerProposalSummaryResponseProductTypeEnumValues = BuiltSet<
        InsurerProposalSummaryResponseProductTypeEnum>(const <InsurerProposalSummaryResponseProductTypeEnum>[
  _$insurerProposalSummaryResponseProductTypeEnum_MTPL_ONLY,
  _$insurerProposalSummaryResponseProductTypeEnum_COMPREHENSIVE,
  _$insurerProposalSummaryResponseProductTypeEnum_TEMPORARY,
  _$insurerProposalSummaryResponseProductTypeEnum_BORDER,
  _$insurerProposalSummaryResponseProductTypeEnum_FLEET,
]);

Serializer<InsurerProposalSummaryResponseProductTypeEnum>
    _$insurerProposalSummaryResponseProductTypeEnumSerializer =
    _$InsurerProposalSummaryResponseProductTypeEnumSerializer();

class _$InsurerProposalSummaryResponseProductTypeEnumSerializer
    implements
        PrimitiveSerializer<InsurerProposalSummaryResponseProductTypeEnum> {
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
    InsurerProposalSummaryResponseProductTypeEnum
  ];
  @override
  final String wireName = 'InsurerProposalSummaryResponseProductTypeEnum';

  @override
  Object serialize(Serializers serializers,
          InsurerProposalSummaryResponseProductTypeEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  InsurerProposalSummaryResponseProductTypeEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      InsurerProposalSummaryResponseProductTypeEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$InsurerProposalSummaryResponse extends InsurerProposalSummaryResponse {
  @override
  final AcceptanceMode? acceptanceMode;
  @override
  final String? brokerName;
  @override
  final ProposalChannel channel;
  @override
  final String customerName;
  @override
  final String id;
  @override
  final InspectionStatus? inspectionStatus;
  @override
  final KycState kycState;
  @override
  final String number;
  @override
  final String productName;
  @override
  final InsurerProposalSummaryResponseProductTypeEnum productType;
  @override
  final DateTime? slaDueAt;
  @override
  final SlaStatus? slaStatus;
  @override
  final ProposalStatus status;
  @override
  final DateTime submittedAt;
  @override
  final Money total;
  @override
  final String vehiclePlate;

  factory _$InsurerProposalSummaryResponse(
          [void Function(InsurerProposalSummaryResponseBuilder)? updates]) =>
      (InsurerProposalSummaryResponseBuilder()..update(updates))._build();

  _$InsurerProposalSummaryResponse._(
      {this.acceptanceMode,
      this.brokerName,
      required this.channel,
      required this.customerName,
      required this.id,
      this.inspectionStatus,
      required this.kycState,
      required this.number,
      required this.productName,
      required this.productType,
      this.slaDueAt,
      this.slaStatus,
      required this.status,
      required this.submittedAt,
      required this.total,
      required this.vehiclePlate})
      : super._();
  @override
  InsurerProposalSummaryResponse rebuild(
          void Function(InsurerProposalSummaryResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  InsurerProposalSummaryResponseBuilder toBuilder() =>
      InsurerProposalSummaryResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is InsurerProposalSummaryResponse &&
        acceptanceMode == other.acceptanceMode &&
        brokerName == other.brokerName &&
        channel == other.channel &&
        customerName == other.customerName &&
        id == other.id &&
        inspectionStatus == other.inspectionStatus &&
        kycState == other.kycState &&
        number == other.number &&
        productName == other.productName &&
        productType == other.productType &&
        slaDueAt == other.slaDueAt &&
        slaStatus == other.slaStatus &&
        status == other.status &&
        submittedAt == other.submittedAt &&
        total == other.total &&
        vehiclePlate == other.vehiclePlate;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, acceptanceMode.hashCode);
    _$hash = $jc(_$hash, brokerName.hashCode);
    _$hash = $jc(_$hash, channel.hashCode);
    _$hash = $jc(_$hash, customerName.hashCode);
    _$hash = $jc(_$hash, id.hashCode);
    _$hash = $jc(_$hash, inspectionStatus.hashCode);
    _$hash = $jc(_$hash, kycState.hashCode);
    _$hash = $jc(_$hash, number.hashCode);
    _$hash = $jc(_$hash, productName.hashCode);
    _$hash = $jc(_$hash, productType.hashCode);
    _$hash = $jc(_$hash, slaDueAt.hashCode);
    _$hash = $jc(_$hash, slaStatus.hashCode);
    _$hash = $jc(_$hash, status.hashCode);
    _$hash = $jc(_$hash, submittedAt.hashCode);
    _$hash = $jc(_$hash, total.hashCode);
    _$hash = $jc(_$hash, vehiclePlate.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'InsurerProposalSummaryResponse')
          ..add('acceptanceMode', acceptanceMode)
          ..add('brokerName', brokerName)
          ..add('channel', channel)
          ..add('customerName', customerName)
          ..add('id', id)
          ..add('inspectionStatus', inspectionStatus)
          ..add('kycState', kycState)
          ..add('number', number)
          ..add('productName', productName)
          ..add('productType', productType)
          ..add('slaDueAt', slaDueAt)
          ..add('slaStatus', slaStatus)
          ..add('status', status)
          ..add('submittedAt', submittedAt)
          ..add('total', total)
          ..add('vehiclePlate', vehiclePlate))
        .toString();
  }
}

class InsurerProposalSummaryResponseBuilder
    implements
        Builder<InsurerProposalSummaryResponse,
            InsurerProposalSummaryResponseBuilder> {
  _$InsurerProposalSummaryResponse? _$v;

  AcceptanceMode? _acceptanceMode;
  AcceptanceMode? get acceptanceMode => _$this._acceptanceMode;
  set acceptanceMode(AcceptanceMode? acceptanceMode) =>
      _$this._acceptanceMode = acceptanceMode;

  String? _brokerName;
  String? get brokerName => _$this._brokerName;
  set brokerName(String? brokerName) => _$this._brokerName = brokerName;

  ProposalChannel? _channel;
  ProposalChannel? get channel => _$this._channel;
  set channel(ProposalChannel? channel) => _$this._channel = channel;

  String? _customerName;
  String? get customerName => _$this._customerName;
  set customerName(String? customerName) => _$this._customerName = customerName;

  String? _id;
  String? get id => _$this._id;
  set id(String? id) => _$this._id = id;

  InspectionStatus? _inspectionStatus;
  InspectionStatus? get inspectionStatus => _$this._inspectionStatus;
  set inspectionStatus(InspectionStatus? inspectionStatus) =>
      _$this._inspectionStatus = inspectionStatus;

  KycState? _kycState;
  KycState? get kycState => _$this._kycState;
  set kycState(KycState? kycState) => _$this._kycState = kycState;

  String? _number;
  String? get number => _$this._number;
  set number(String? number) => _$this._number = number;

  String? _productName;
  String? get productName => _$this._productName;
  set productName(String? productName) => _$this._productName = productName;

  InsurerProposalSummaryResponseProductTypeEnum? _productType;
  InsurerProposalSummaryResponseProductTypeEnum? get productType =>
      _$this._productType;
  set productType(InsurerProposalSummaryResponseProductTypeEnum? productType) =>
      _$this._productType = productType;

  DateTime? _slaDueAt;
  DateTime? get slaDueAt => _$this._slaDueAt;
  set slaDueAt(DateTime? slaDueAt) => _$this._slaDueAt = slaDueAt;

  SlaStatus? _slaStatus;
  SlaStatus? get slaStatus => _$this._slaStatus;
  set slaStatus(SlaStatus? slaStatus) => _$this._slaStatus = slaStatus;

  ProposalStatus? _status;
  ProposalStatus? get status => _$this._status;
  set status(ProposalStatus? status) => _$this._status = status;

  DateTime? _submittedAt;
  DateTime? get submittedAt => _$this._submittedAt;
  set submittedAt(DateTime? submittedAt) => _$this._submittedAt = submittedAt;

  MoneyBuilder? _total;
  MoneyBuilder get total => _$this._total ??= MoneyBuilder();
  set total(MoneyBuilder? total) => _$this._total = total;

  String? _vehiclePlate;
  String? get vehiclePlate => _$this._vehiclePlate;
  set vehiclePlate(String? vehiclePlate) => _$this._vehiclePlate = vehiclePlate;

  InsurerProposalSummaryResponseBuilder() {
    InsurerProposalSummaryResponse._defaults(this);
  }

  InsurerProposalSummaryResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _acceptanceMode = $v.acceptanceMode;
      _brokerName = $v.brokerName;
      _channel = $v.channel;
      _customerName = $v.customerName;
      _id = $v.id;
      _inspectionStatus = $v.inspectionStatus;
      _kycState = $v.kycState;
      _number = $v.number;
      _productName = $v.productName;
      _productType = $v.productType;
      _slaDueAt = $v.slaDueAt;
      _slaStatus = $v.slaStatus;
      _status = $v.status;
      _submittedAt = $v.submittedAt;
      _total = $v.total.toBuilder();
      _vehiclePlate = $v.vehiclePlate;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(InsurerProposalSummaryResponse other) {
    _$v = other as _$InsurerProposalSummaryResponse;
  }

  @override
  void update(void Function(InsurerProposalSummaryResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  InsurerProposalSummaryResponse build() => _build();

  _$InsurerProposalSummaryResponse _build() {
    _$InsurerProposalSummaryResponse _$result;
    try {
      _$result = _$v ??
          _$InsurerProposalSummaryResponse._(
            acceptanceMode: acceptanceMode,
            brokerName: brokerName,
            channel: BuiltValueNullFieldError.checkNotNull(
                channel, r'InsurerProposalSummaryResponse', 'channel'),
            customerName: BuiltValueNullFieldError.checkNotNull(customerName,
                r'InsurerProposalSummaryResponse', 'customerName'),
            id: BuiltValueNullFieldError.checkNotNull(
                id, r'InsurerProposalSummaryResponse', 'id'),
            inspectionStatus: inspectionStatus,
            kycState: BuiltValueNullFieldError.checkNotNull(
                kycState, r'InsurerProposalSummaryResponse', 'kycState'),
            number: BuiltValueNullFieldError.checkNotNull(
                number, r'InsurerProposalSummaryResponse', 'number'),
            productName: BuiltValueNullFieldError.checkNotNull(
                productName, r'InsurerProposalSummaryResponse', 'productName'),
            productType: BuiltValueNullFieldError.checkNotNull(
                productType, r'InsurerProposalSummaryResponse', 'productType'),
            slaDueAt: slaDueAt,
            slaStatus: slaStatus,
            status: BuiltValueNullFieldError.checkNotNull(
                status, r'InsurerProposalSummaryResponse', 'status'),
            submittedAt: BuiltValueNullFieldError.checkNotNull(
                submittedAt, r'InsurerProposalSummaryResponse', 'submittedAt'),
            total: total.build(),
            vehiclePlate: BuiltValueNullFieldError.checkNotNull(vehiclePlate,
                r'InsurerProposalSummaryResponse', 'vehiclePlate'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'total';
        total.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'InsurerProposalSummaryResponse', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
