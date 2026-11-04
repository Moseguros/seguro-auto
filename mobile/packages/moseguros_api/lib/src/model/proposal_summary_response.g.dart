// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'proposal_summary_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const ProposalSummaryResponseProductTypeEnum
    _$proposalSummaryResponseProductTypeEnum_MTPL_ONLY =
    const ProposalSummaryResponseProductTypeEnum._('MTPL_ONLY');
const ProposalSummaryResponseProductTypeEnum
    _$proposalSummaryResponseProductTypeEnum_COMPREHENSIVE =
    const ProposalSummaryResponseProductTypeEnum._('COMPREHENSIVE');
const ProposalSummaryResponseProductTypeEnum
    _$proposalSummaryResponseProductTypeEnum_TEMPORARY =
    const ProposalSummaryResponseProductTypeEnum._('TEMPORARY');
const ProposalSummaryResponseProductTypeEnum
    _$proposalSummaryResponseProductTypeEnum_BORDER =
    const ProposalSummaryResponseProductTypeEnum._('BORDER');
const ProposalSummaryResponseProductTypeEnum
    _$proposalSummaryResponseProductTypeEnum_FLEET =
    const ProposalSummaryResponseProductTypeEnum._('FLEET');

ProposalSummaryResponseProductTypeEnum
    _$proposalSummaryResponseProductTypeEnumValueOf(String name) {
  switch (name) {
    case 'MTPL_ONLY':
      return _$proposalSummaryResponseProductTypeEnum_MTPL_ONLY;
    case 'COMPREHENSIVE':
      return _$proposalSummaryResponseProductTypeEnum_COMPREHENSIVE;
    case 'TEMPORARY':
      return _$proposalSummaryResponseProductTypeEnum_TEMPORARY;
    case 'BORDER':
      return _$proposalSummaryResponseProductTypeEnum_BORDER;
    case 'FLEET':
      return _$proposalSummaryResponseProductTypeEnum_FLEET;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<ProposalSummaryResponseProductTypeEnum>
    _$proposalSummaryResponseProductTypeEnumValues = BuiltSet<
        ProposalSummaryResponseProductTypeEnum>(const <ProposalSummaryResponseProductTypeEnum>[
  _$proposalSummaryResponseProductTypeEnum_MTPL_ONLY,
  _$proposalSummaryResponseProductTypeEnum_COMPREHENSIVE,
  _$proposalSummaryResponseProductTypeEnum_TEMPORARY,
  _$proposalSummaryResponseProductTypeEnum_BORDER,
  _$proposalSummaryResponseProductTypeEnum_FLEET,
]);

Serializer<ProposalSummaryResponseProductTypeEnum>
    _$proposalSummaryResponseProductTypeEnumSerializer =
    _$ProposalSummaryResponseProductTypeEnumSerializer();

class _$ProposalSummaryResponseProductTypeEnumSerializer
    implements PrimitiveSerializer<ProposalSummaryResponseProductTypeEnum> {
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
    ProposalSummaryResponseProductTypeEnum
  ];
  @override
  final String wireName = 'ProposalSummaryResponseProductTypeEnum';

  @override
  Object serialize(Serializers serializers,
          ProposalSummaryResponseProductTypeEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  ProposalSummaryResponseProductTypeEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      ProposalSummaryResponseProductTypeEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$ProposalSummaryResponse extends ProposalSummaryResponse {
  @override
  final DateTime createdAt;
  @override
  final DateTime? expiresAt;
  @override
  final String id;
  @override
  final ProposalOrganization? insurer;
  @override
  final String number;
  @override
  final String productName;
  @override
  final ProposalSummaryResponseProductTypeEnum productType;
  @override
  final DateTime startAt;
  @override
  final ProposalStatus status;
  @override
  final Money total;
  @override
  final String vehiclePlate;

  factory _$ProposalSummaryResponse(
          [void Function(ProposalSummaryResponseBuilder)? updates]) =>
      (ProposalSummaryResponseBuilder()..update(updates))._build();

  _$ProposalSummaryResponse._(
      {required this.createdAt,
      this.expiresAt,
      required this.id,
      this.insurer,
      required this.number,
      required this.productName,
      required this.productType,
      required this.startAt,
      required this.status,
      required this.total,
      required this.vehiclePlate})
      : super._();
  @override
  ProposalSummaryResponse rebuild(
          void Function(ProposalSummaryResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  ProposalSummaryResponseBuilder toBuilder() =>
      ProposalSummaryResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is ProposalSummaryResponse &&
        createdAt == other.createdAt &&
        expiresAt == other.expiresAt &&
        id == other.id &&
        insurer == other.insurer &&
        number == other.number &&
        productName == other.productName &&
        productType == other.productType &&
        startAt == other.startAt &&
        status == other.status &&
        total == other.total &&
        vehiclePlate == other.vehiclePlate;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, createdAt.hashCode);
    _$hash = $jc(_$hash, expiresAt.hashCode);
    _$hash = $jc(_$hash, id.hashCode);
    _$hash = $jc(_$hash, insurer.hashCode);
    _$hash = $jc(_$hash, number.hashCode);
    _$hash = $jc(_$hash, productName.hashCode);
    _$hash = $jc(_$hash, productType.hashCode);
    _$hash = $jc(_$hash, startAt.hashCode);
    _$hash = $jc(_$hash, status.hashCode);
    _$hash = $jc(_$hash, total.hashCode);
    _$hash = $jc(_$hash, vehiclePlate.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'ProposalSummaryResponse')
          ..add('createdAt', createdAt)
          ..add('expiresAt', expiresAt)
          ..add('id', id)
          ..add('insurer', insurer)
          ..add('number', number)
          ..add('productName', productName)
          ..add('productType', productType)
          ..add('startAt', startAt)
          ..add('status', status)
          ..add('total', total)
          ..add('vehiclePlate', vehiclePlate))
        .toString();
  }
}

class ProposalSummaryResponseBuilder
    implements
        Builder<ProposalSummaryResponse, ProposalSummaryResponseBuilder> {
  _$ProposalSummaryResponse? _$v;

  DateTime? _createdAt;
  DateTime? get createdAt => _$this._createdAt;
  set createdAt(DateTime? createdAt) => _$this._createdAt = createdAt;

  DateTime? _expiresAt;
  DateTime? get expiresAt => _$this._expiresAt;
  set expiresAt(DateTime? expiresAt) => _$this._expiresAt = expiresAt;

  String? _id;
  String? get id => _$this._id;
  set id(String? id) => _$this._id = id;

  ProposalOrganizationBuilder? _insurer;
  ProposalOrganizationBuilder get insurer =>
      _$this._insurer ??= ProposalOrganizationBuilder();
  set insurer(ProposalOrganizationBuilder? insurer) =>
      _$this._insurer = insurer;

  String? _number;
  String? get number => _$this._number;
  set number(String? number) => _$this._number = number;

  String? _productName;
  String? get productName => _$this._productName;
  set productName(String? productName) => _$this._productName = productName;

  ProposalSummaryResponseProductTypeEnum? _productType;
  ProposalSummaryResponseProductTypeEnum? get productType =>
      _$this._productType;
  set productType(ProposalSummaryResponseProductTypeEnum? productType) =>
      _$this._productType = productType;

  DateTime? _startAt;
  DateTime? get startAt => _$this._startAt;
  set startAt(DateTime? startAt) => _$this._startAt = startAt;

  ProposalStatus? _status;
  ProposalStatus? get status => _$this._status;
  set status(ProposalStatus? status) => _$this._status = status;

  MoneyBuilder? _total;
  MoneyBuilder get total => _$this._total ??= MoneyBuilder();
  set total(MoneyBuilder? total) => _$this._total = total;

  String? _vehiclePlate;
  String? get vehiclePlate => _$this._vehiclePlate;
  set vehiclePlate(String? vehiclePlate) => _$this._vehiclePlate = vehiclePlate;

  ProposalSummaryResponseBuilder() {
    ProposalSummaryResponse._defaults(this);
  }

  ProposalSummaryResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _createdAt = $v.createdAt;
      _expiresAt = $v.expiresAt;
      _id = $v.id;
      _insurer = $v.insurer?.toBuilder();
      _number = $v.number;
      _productName = $v.productName;
      _productType = $v.productType;
      _startAt = $v.startAt;
      _status = $v.status;
      _total = $v.total.toBuilder();
      _vehiclePlate = $v.vehiclePlate;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(ProposalSummaryResponse other) {
    _$v = other as _$ProposalSummaryResponse;
  }

  @override
  void update(void Function(ProposalSummaryResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  ProposalSummaryResponse build() => _build();

  _$ProposalSummaryResponse _build() {
    _$ProposalSummaryResponse _$result;
    try {
      _$result = _$v ??
          _$ProposalSummaryResponse._(
            createdAt: BuiltValueNullFieldError.checkNotNull(
                createdAt, r'ProposalSummaryResponse', 'createdAt'),
            expiresAt: expiresAt,
            id: BuiltValueNullFieldError.checkNotNull(
                id, r'ProposalSummaryResponse', 'id'),
            insurer: _insurer?.build(),
            number: BuiltValueNullFieldError.checkNotNull(
                number, r'ProposalSummaryResponse', 'number'),
            productName: BuiltValueNullFieldError.checkNotNull(
                productName, r'ProposalSummaryResponse', 'productName'),
            productType: BuiltValueNullFieldError.checkNotNull(
                productType, r'ProposalSummaryResponse', 'productType'),
            startAt: BuiltValueNullFieldError.checkNotNull(
                startAt, r'ProposalSummaryResponse', 'startAt'),
            status: BuiltValueNullFieldError.checkNotNull(
                status, r'ProposalSummaryResponse', 'status'),
            total: total.build(),
            vehiclePlate: BuiltValueNullFieldError.checkNotNull(
                vehiclePlate, r'ProposalSummaryResponse', 'vehiclePlate'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'insurer';
        _insurer?.build();

        _$failedField = 'total';
        total.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'ProposalSummaryResponse', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
