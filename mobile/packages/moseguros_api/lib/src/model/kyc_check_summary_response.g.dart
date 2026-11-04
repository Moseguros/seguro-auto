// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'kyc_check_summary_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const KycCheckSummaryResponseIdDocTypeEnum
    _$kycCheckSummaryResponseIdDocTypeEnum_BI =
    const KycCheckSummaryResponseIdDocTypeEnum._('BI');
const KycCheckSummaryResponseIdDocTypeEnum
    _$kycCheckSummaryResponseIdDocTypeEnum_PASSPORT =
    const KycCheckSummaryResponseIdDocTypeEnum._('PASSPORT');
const KycCheckSummaryResponseIdDocTypeEnum
    _$kycCheckSummaryResponseIdDocTypeEnum_DIRE =
    const KycCheckSummaryResponseIdDocTypeEnum._('DIRE');

KycCheckSummaryResponseIdDocTypeEnum
    _$kycCheckSummaryResponseIdDocTypeEnumValueOf(String name) {
  switch (name) {
    case 'BI':
      return _$kycCheckSummaryResponseIdDocTypeEnum_BI;
    case 'PASSPORT':
      return _$kycCheckSummaryResponseIdDocTypeEnum_PASSPORT;
    case 'DIRE':
      return _$kycCheckSummaryResponseIdDocTypeEnum_DIRE;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<KycCheckSummaryResponseIdDocTypeEnum>
    _$kycCheckSummaryResponseIdDocTypeEnumValues = BuiltSet<
        KycCheckSummaryResponseIdDocTypeEnum>(const <KycCheckSummaryResponseIdDocTypeEnum>[
  _$kycCheckSummaryResponseIdDocTypeEnum_BI,
  _$kycCheckSummaryResponseIdDocTypeEnum_PASSPORT,
  _$kycCheckSummaryResponseIdDocTypeEnum_DIRE,
]);

Serializer<KycCheckSummaryResponseIdDocTypeEnum>
    _$kycCheckSummaryResponseIdDocTypeEnumSerializer =
    _$KycCheckSummaryResponseIdDocTypeEnumSerializer();

class _$KycCheckSummaryResponseIdDocTypeEnumSerializer
    implements PrimitiveSerializer<KycCheckSummaryResponseIdDocTypeEnum> {
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
  final Iterable<Type> types = const <Type>[
    KycCheckSummaryResponseIdDocTypeEnum
  ];
  @override
  final String wireName = 'KycCheckSummaryResponseIdDocTypeEnum';

  @override
  Object serialize(
          Serializers serializers, KycCheckSummaryResponseIdDocTypeEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  KycCheckSummaryResponseIdDocTypeEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      KycCheckSummaryResponseIdDocTypeEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$KycCheckSummaryResponse extends KycCheckSummaryResponse {
  @override
  final String fullName;
  @override
  final String id;
  @override
  final KycCheckSummaryResponseIdDocTypeEnum idDocType;
  @override
  final DateTime? reviewedAt;
  @override
  final KycCheckStatus status;
  @override
  final DateTime submittedAt;

  factory _$KycCheckSummaryResponse(
          [void Function(KycCheckSummaryResponseBuilder)? updates]) =>
      (KycCheckSummaryResponseBuilder()..update(updates))._build();

  _$KycCheckSummaryResponse._(
      {required this.fullName,
      required this.id,
      required this.idDocType,
      this.reviewedAt,
      required this.status,
      required this.submittedAt})
      : super._();
  @override
  KycCheckSummaryResponse rebuild(
          void Function(KycCheckSummaryResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  KycCheckSummaryResponseBuilder toBuilder() =>
      KycCheckSummaryResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is KycCheckSummaryResponse &&
        fullName == other.fullName &&
        id == other.id &&
        idDocType == other.idDocType &&
        reviewedAt == other.reviewedAt &&
        status == other.status &&
        submittedAt == other.submittedAt;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, fullName.hashCode);
    _$hash = $jc(_$hash, id.hashCode);
    _$hash = $jc(_$hash, idDocType.hashCode);
    _$hash = $jc(_$hash, reviewedAt.hashCode);
    _$hash = $jc(_$hash, status.hashCode);
    _$hash = $jc(_$hash, submittedAt.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'KycCheckSummaryResponse')
          ..add('fullName', fullName)
          ..add('id', id)
          ..add('idDocType', idDocType)
          ..add('reviewedAt', reviewedAt)
          ..add('status', status)
          ..add('submittedAt', submittedAt))
        .toString();
  }
}

class KycCheckSummaryResponseBuilder
    implements
        Builder<KycCheckSummaryResponse, KycCheckSummaryResponseBuilder> {
  _$KycCheckSummaryResponse? _$v;

  String? _fullName;
  String? get fullName => _$this._fullName;
  set fullName(String? fullName) => _$this._fullName = fullName;

  String? _id;
  String? get id => _$this._id;
  set id(String? id) => _$this._id = id;

  KycCheckSummaryResponseIdDocTypeEnum? _idDocType;
  KycCheckSummaryResponseIdDocTypeEnum? get idDocType => _$this._idDocType;
  set idDocType(KycCheckSummaryResponseIdDocTypeEnum? idDocType) =>
      _$this._idDocType = idDocType;

  DateTime? _reviewedAt;
  DateTime? get reviewedAt => _$this._reviewedAt;
  set reviewedAt(DateTime? reviewedAt) => _$this._reviewedAt = reviewedAt;

  KycCheckStatus? _status;
  KycCheckStatus? get status => _$this._status;
  set status(KycCheckStatus? status) => _$this._status = status;

  DateTime? _submittedAt;
  DateTime? get submittedAt => _$this._submittedAt;
  set submittedAt(DateTime? submittedAt) => _$this._submittedAt = submittedAt;

  KycCheckSummaryResponseBuilder() {
    KycCheckSummaryResponse._defaults(this);
  }

  KycCheckSummaryResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _fullName = $v.fullName;
      _id = $v.id;
      _idDocType = $v.idDocType;
      _reviewedAt = $v.reviewedAt;
      _status = $v.status;
      _submittedAt = $v.submittedAt;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(KycCheckSummaryResponse other) {
    _$v = other as _$KycCheckSummaryResponse;
  }

  @override
  void update(void Function(KycCheckSummaryResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  KycCheckSummaryResponse build() => _build();

  _$KycCheckSummaryResponse _build() {
    final _$result = _$v ??
        _$KycCheckSummaryResponse._(
          fullName: BuiltValueNullFieldError.checkNotNull(
              fullName, r'KycCheckSummaryResponse', 'fullName'),
          id: BuiltValueNullFieldError.checkNotNull(
              id, r'KycCheckSummaryResponse', 'id'),
          idDocType: BuiltValueNullFieldError.checkNotNull(
              idDocType, r'KycCheckSummaryResponse', 'idDocType'),
          reviewedAt: reviewedAt,
          status: BuiltValueNullFieldError.checkNotNull(
              status, r'KycCheckSummaryResponse', 'status'),
          submittedAt: BuiltValueNullFieldError.checkNotNull(
              submittedAt, r'KycCheckSummaryResponse', 'submittedAt'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
