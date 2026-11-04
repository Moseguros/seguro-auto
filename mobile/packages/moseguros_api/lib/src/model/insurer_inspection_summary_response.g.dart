// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'insurer_inspection_summary_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$InsurerInspectionSummaryResponse
    extends InsurerInspectionSummaryResponse {
  @override
  final String customerName;
  @override
  final String id;
  @override
  final InspectionMode mode;
  @override
  final int photoCount;
  @override
  final String proposalId;
  @override
  final String proposalNumber;
  @override
  final InspectionStatus status;
  @override
  final DateTime? submittedAt;
  @override
  final String vehiclePlate;

  factory _$InsurerInspectionSummaryResponse(
          [void Function(InsurerInspectionSummaryResponseBuilder)? updates]) =>
      (InsurerInspectionSummaryResponseBuilder()..update(updates))._build();

  _$InsurerInspectionSummaryResponse._(
      {required this.customerName,
      required this.id,
      required this.mode,
      required this.photoCount,
      required this.proposalId,
      required this.proposalNumber,
      required this.status,
      this.submittedAt,
      required this.vehiclePlate})
      : super._();
  @override
  InsurerInspectionSummaryResponse rebuild(
          void Function(InsurerInspectionSummaryResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  InsurerInspectionSummaryResponseBuilder toBuilder() =>
      InsurerInspectionSummaryResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is InsurerInspectionSummaryResponse &&
        customerName == other.customerName &&
        id == other.id &&
        mode == other.mode &&
        photoCount == other.photoCount &&
        proposalId == other.proposalId &&
        proposalNumber == other.proposalNumber &&
        status == other.status &&
        submittedAt == other.submittedAt &&
        vehiclePlate == other.vehiclePlate;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, customerName.hashCode);
    _$hash = $jc(_$hash, id.hashCode);
    _$hash = $jc(_$hash, mode.hashCode);
    _$hash = $jc(_$hash, photoCount.hashCode);
    _$hash = $jc(_$hash, proposalId.hashCode);
    _$hash = $jc(_$hash, proposalNumber.hashCode);
    _$hash = $jc(_$hash, status.hashCode);
    _$hash = $jc(_$hash, submittedAt.hashCode);
    _$hash = $jc(_$hash, vehiclePlate.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'InsurerInspectionSummaryResponse')
          ..add('customerName', customerName)
          ..add('id', id)
          ..add('mode', mode)
          ..add('photoCount', photoCount)
          ..add('proposalId', proposalId)
          ..add('proposalNumber', proposalNumber)
          ..add('status', status)
          ..add('submittedAt', submittedAt)
          ..add('vehiclePlate', vehiclePlate))
        .toString();
  }
}

class InsurerInspectionSummaryResponseBuilder
    implements
        Builder<InsurerInspectionSummaryResponse,
            InsurerInspectionSummaryResponseBuilder> {
  _$InsurerInspectionSummaryResponse? _$v;

  String? _customerName;
  String? get customerName => _$this._customerName;
  set customerName(String? customerName) => _$this._customerName = customerName;

  String? _id;
  String? get id => _$this._id;
  set id(String? id) => _$this._id = id;

  InspectionMode? _mode;
  InspectionMode? get mode => _$this._mode;
  set mode(InspectionMode? mode) => _$this._mode = mode;

  int? _photoCount;
  int? get photoCount => _$this._photoCount;
  set photoCount(int? photoCount) => _$this._photoCount = photoCount;

  String? _proposalId;
  String? get proposalId => _$this._proposalId;
  set proposalId(String? proposalId) => _$this._proposalId = proposalId;

  String? _proposalNumber;
  String? get proposalNumber => _$this._proposalNumber;
  set proposalNumber(String? proposalNumber) =>
      _$this._proposalNumber = proposalNumber;

  InspectionStatus? _status;
  InspectionStatus? get status => _$this._status;
  set status(InspectionStatus? status) => _$this._status = status;

  DateTime? _submittedAt;
  DateTime? get submittedAt => _$this._submittedAt;
  set submittedAt(DateTime? submittedAt) => _$this._submittedAt = submittedAt;

  String? _vehiclePlate;
  String? get vehiclePlate => _$this._vehiclePlate;
  set vehiclePlate(String? vehiclePlate) => _$this._vehiclePlate = vehiclePlate;

  InsurerInspectionSummaryResponseBuilder() {
    InsurerInspectionSummaryResponse._defaults(this);
  }

  InsurerInspectionSummaryResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _customerName = $v.customerName;
      _id = $v.id;
      _mode = $v.mode;
      _photoCount = $v.photoCount;
      _proposalId = $v.proposalId;
      _proposalNumber = $v.proposalNumber;
      _status = $v.status;
      _submittedAt = $v.submittedAt;
      _vehiclePlate = $v.vehiclePlate;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(InsurerInspectionSummaryResponse other) {
    _$v = other as _$InsurerInspectionSummaryResponse;
  }

  @override
  void update(void Function(InsurerInspectionSummaryResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  InsurerInspectionSummaryResponse build() => _build();

  _$InsurerInspectionSummaryResponse _build() {
    final _$result = _$v ??
        _$InsurerInspectionSummaryResponse._(
          customerName: BuiltValueNullFieldError.checkNotNull(customerName,
              r'InsurerInspectionSummaryResponse', 'customerName'),
          id: BuiltValueNullFieldError.checkNotNull(
              id, r'InsurerInspectionSummaryResponse', 'id'),
          mode: BuiltValueNullFieldError.checkNotNull(
              mode, r'InsurerInspectionSummaryResponse', 'mode'),
          photoCount: BuiltValueNullFieldError.checkNotNull(
              photoCount, r'InsurerInspectionSummaryResponse', 'photoCount'),
          proposalId: BuiltValueNullFieldError.checkNotNull(
              proposalId, r'InsurerInspectionSummaryResponse', 'proposalId'),
          proposalNumber: BuiltValueNullFieldError.checkNotNull(proposalNumber,
              r'InsurerInspectionSummaryResponse', 'proposalNumber'),
          status: BuiltValueNullFieldError.checkNotNull(
              status, r'InsurerInspectionSummaryResponse', 'status'),
          submittedAt: submittedAt,
          vehiclePlate: BuiltValueNullFieldError.checkNotNull(vehiclePlate,
              r'InsurerInspectionSummaryResponse', 'vehiclePlate'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
