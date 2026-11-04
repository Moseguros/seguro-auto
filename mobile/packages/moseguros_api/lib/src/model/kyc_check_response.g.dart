// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'kyc_check_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const KycCheckResponseIdDocTypeEnum _$kycCheckResponseIdDocTypeEnum_BI =
    const KycCheckResponseIdDocTypeEnum._('BI');
const KycCheckResponseIdDocTypeEnum _$kycCheckResponseIdDocTypeEnum_PASSPORT =
    const KycCheckResponseIdDocTypeEnum._('PASSPORT');
const KycCheckResponseIdDocTypeEnum _$kycCheckResponseIdDocTypeEnum_DIRE =
    const KycCheckResponseIdDocTypeEnum._('DIRE');

KycCheckResponseIdDocTypeEnum _$kycCheckResponseIdDocTypeEnumValueOf(
    String name) {
  switch (name) {
    case 'BI':
      return _$kycCheckResponseIdDocTypeEnum_BI;
    case 'PASSPORT':
      return _$kycCheckResponseIdDocTypeEnum_PASSPORT;
    case 'DIRE':
      return _$kycCheckResponseIdDocTypeEnum_DIRE;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<KycCheckResponseIdDocTypeEnum>
    _$kycCheckResponseIdDocTypeEnumValues = BuiltSet<
        KycCheckResponseIdDocTypeEnum>(const <KycCheckResponseIdDocTypeEnum>[
  _$kycCheckResponseIdDocTypeEnum_BI,
  _$kycCheckResponseIdDocTypeEnum_PASSPORT,
  _$kycCheckResponseIdDocTypeEnum_DIRE,
]);

Serializer<KycCheckResponseIdDocTypeEnum>
    _$kycCheckResponseIdDocTypeEnumSerializer =
    _$KycCheckResponseIdDocTypeEnumSerializer();

class _$KycCheckResponseIdDocTypeEnumSerializer
    implements PrimitiveSerializer<KycCheckResponseIdDocTypeEnum> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'BI': 'BI',
    'PASSPORT': 'PASSPORT',
    'DIRE': 'DIRE',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'BI': 'BI',
    'PASSPORT': 'PASSPORT',
    'DIRE': 'DIRE',
  };

  @override
  final Iterable<Type> types = const <Type>[KycCheckResponseIdDocTypeEnum];
  @override
  final String wireName = 'KycCheckResponseIdDocTypeEnum';

  @override
  Object serialize(
          Serializers serializers, KycCheckResponseIdDocTypeEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  KycCheckResponseIdDocTypeEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      KycCheckResponseIdDocTypeEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$KycCheckResponse extends KycCheckResponse {
  @override
  final String id;
  @override
  final String idDocNumberMasked;
  @override
  final KycCheckResponseIdDocTypeEnum idDocType;
  @override
  final String insurerId;
  @override
  final String? rejectionReason;
  @override
  final KycRejectionReason? rejectionReasonCode;
  @override
  final DateTime? reviewedAt;
  @override
  final KycCheckStatus status;
  @override
  final DateTime submittedAt;

  factory _$KycCheckResponse(
          [void Function(KycCheckResponseBuilder)? updates]) =>
      (KycCheckResponseBuilder()..update(updates))._build();

  _$KycCheckResponse._(
      {required this.id,
      required this.idDocNumberMasked,
      required this.idDocType,
      required this.insurerId,
      this.rejectionReason,
      this.rejectionReasonCode,
      this.reviewedAt,
      required this.status,
      required this.submittedAt})
      : super._();
  @override
  KycCheckResponse rebuild(void Function(KycCheckResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  KycCheckResponseBuilder toBuilder() =>
      KycCheckResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is KycCheckResponse &&
        id == other.id &&
        idDocNumberMasked == other.idDocNumberMasked &&
        idDocType == other.idDocType &&
        insurerId == other.insurerId &&
        rejectionReason == other.rejectionReason &&
        rejectionReasonCode == other.rejectionReasonCode &&
        reviewedAt == other.reviewedAt &&
        status == other.status &&
        submittedAt == other.submittedAt;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, id.hashCode);
    _$hash = $jc(_$hash, idDocNumberMasked.hashCode);
    _$hash = $jc(_$hash, idDocType.hashCode);
    _$hash = $jc(_$hash, insurerId.hashCode);
    _$hash = $jc(_$hash, rejectionReason.hashCode);
    _$hash = $jc(_$hash, rejectionReasonCode.hashCode);
    _$hash = $jc(_$hash, reviewedAt.hashCode);
    _$hash = $jc(_$hash, status.hashCode);
    _$hash = $jc(_$hash, submittedAt.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'KycCheckResponse')
          ..add('id', id)
          ..add('idDocNumberMasked', idDocNumberMasked)
          ..add('idDocType', idDocType)
          ..add('insurerId', insurerId)
          ..add('rejectionReason', rejectionReason)
          ..add('rejectionReasonCode', rejectionReasonCode)
          ..add('reviewedAt', reviewedAt)
          ..add('status', status)
          ..add('submittedAt', submittedAt))
        .toString();
  }
}

class KycCheckResponseBuilder
    implements Builder<KycCheckResponse, KycCheckResponseBuilder> {
  _$KycCheckResponse? _$v;

  String? _id;
  String? get id => _$this._id;
  set id(String? id) => _$this._id = id;

  String? _idDocNumberMasked;
  String? get idDocNumberMasked => _$this._idDocNumberMasked;
  set idDocNumberMasked(String? idDocNumberMasked) =>
      _$this._idDocNumberMasked = idDocNumberMasked;

  KycCheckResponseIdDocTypeEnum? _idDocType;
  KycCheckResponseIdDocTypeEnum? get idDocType => _$this._idDocType;
  set idDocType(KycCheckResponseIdDocTypeEnum? idDocType) =>
      _$this._idDocType = idDocType;

  String? _insurerId;
  String? get insurerId => _$this._insurerId;
  set insurerId(String? insurerId) => _$this._insurerId = insurerId;

  String? _rejectionReason;
  String? get rejectionReason => _$this._rejectionReason;
  set rejectionReason(String? rejectionReason) =>
      _$this._rejectionReason = rejectionReason;

  KycRejectionReason? _rejectionReasonCode;
  KycRejectionReason? get rejectionReasonCode => _$this._rejectionReasonCode;
  set rejectionReasonCode(KycRejectionReason? rejectionReasonCode) =>
      _$this._rejectionReasonCode = rejectionReasonCode;

  DateTime? _reviewedAt;
  DateTime? get reviewedAt => _$this._reviewedAt;
  set reviewedAt(DateTime? reviewedAt) => _$this._reviewedAt = reviewedAt;

  KycCheckStatus? _status;
  KycCheckStatus? get status => _$this._status;
  set status(KycCheckStatus? status) => _$this._status = status;

  DateTime? _submittedAt;
  DateTime? get submittedAt => _$this._submittedAt;
  set submittedAt(DateTime? submittedAt) => _$this._submittedAt = submittedAt;

  KycCheckResponseBuilder() {
    KycCheckResponse._defaults(this);
  }

  KycCheckResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _id = $v.id;
      _idDocNumberMasked = $v.idDocNumberMasked;
      _idDocType = $v.idDocType;
      _insurerId = $v.insurerId;
      _rejectionReason = $v.rejectionReason;
      _rejectionReasonCode = $v.rejectionReasonCode;
      _reviewedAt = $v.reviewedAt;
      _status = $v.status;
      _submittedAt = $v.submittedAt;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(KycCheckResponse other) {
    _$v = other as _$KycCheckResponse;
  }

  @override
  void update(void Function(KycCheckResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  KycCheckResponse build() => _build();

  _$KycCheckResponse _build() {
    final _$result = _$v ??
        _$KycCheckResponse._(
          id: BuiltValueNullFieldError.checkNotNull(
              id, r'KycCheckResponse', 'id'),
          idDocNumberMasked: BuiltValueNullFieldError.checkNotNull(
              idDocNumberMasked, r'KycCheckResponse', 'idDocNumberMasked'),
          idDocType: BuiltValueNullFieldError.checkNotNull(
              idDocType, r'KycCheckResponse', 'idDocType'),
          insurerId: BuiltValueNullFieldError.checkNotNull(
              insurerId, r'KycCheckResponse', 'insurerId'),
          rejectionReason: rejectionReason,
          rejectionReasonCode: rejectionReasonCode,
          reviewedAt: reviewedAt,
          status: BuiltValueNullFieldError.checkNotNull(
              status, r'KycCheckResponse', 'status'),
          submittedAt: BuiltValueNullFieldError.checkNotNull(
              submittedAt, r'KycCheckResponse', 'submittedAt'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
