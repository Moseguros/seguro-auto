// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'insurer_policy_summary_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$InsurerPolicySummaryResponse extends InsurerPolicySummaryResponse {
  @override
  final CoverStatus coverStatus;
  @override
  final CoverageType coverageType;
  @override
  final DateTime endAt;
  @override
  final String id;
  @override
  final DateTime issuedAt;
  @override
  final String number;
  @override
  final String plate;
  @override
  final String policyholderName;
  @override
  final String productName;
  @override
  final DateTime startAt;
  @override
  final PolicyStatus status;
  @override
  final Money totalPremium;

  factory _$InsurerPolicySummaryResponse(
          [void Function(InsurerPolicySummaryResponseBuilder)? updates]) =>
      (InsurerPolicySummaryResponseBuilder()..update(updates))._build();

  _$InsurerPolicySummaryResponse._(
      {required this.coverStatus,
      required this.coverageType,
      required this.endAt,
      required this.id,
      required this.issuedAt,
      required this.number,
      required this.plate,
      required this.policyholderName,
      required this.productName,
      required this.startAt,
      required this.status,
      required this.totalPremium})
      : super._();
  @override
  InsurerPolicySummaryResponse rebuild(
          void Function(InsurerPolicySummaryResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  InsurerPolicySummaryResponseBuilder toBuilder() =>
      InsurerPolicySummaryResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is InsurerPolicySummaryResponse &&
        coverStatus == other.coverStatus &&
        coverageType == other.coverageType &&
        endAt == other.endAt &&
        id == other.id &&
        issuedAt == other.issuedAt &&
        number == other.number &&
        plate == other.plate &&
        policyholderName == other.policyholderName &&
        productName == other.productName &&
        startAt == other.startAt &&
        status == other.status &&
        totalPremium == other.totalPremium;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, coverStatus.hashCode);
    _$hash = $jc(_$hash, coverageType.hashCode);
    _$hash = $jc(_$hash, endAt.hashCode);
    _$hash = $jc(_$hash, id.hashCode);
    _$hash = $jc(_$hash, issuedAt.hashCode);
    _$hash = $jc(_$hash, number.hashCode);
    _$hash = $jc(_$hash, plate.hashCode);
    _$hash = $jc(_$hash, policyholderName.hashCode);
    _$hash = $jc(_$hash, productName.hashCode);
    _$hash = $jc(_$hash, startAt.hashCode);
    _$hash = $jc(_$hash, status.hashCode);
    _$hash = $jc(_$hash, totalPremium.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'InsurerPolicySummaryResponse')
          ..add('coverStatus', coverStatus)
          ..add('coverageType', coverageType)
          ..add('endAt', endAt)
          ..add('id', id)
          ..add('issuedAt', issuedAt)
          ..add('number', number)
          ..add('plate', plate)
          ..add('policyholderName', policyholderName)
          ..add('productName', productName)
          ..add('startAt', startAt)
          ..add('status', status)
          ..add('totalPremium', totalPremium))
        .toString();
  }
}

class InsurerPolicySummaryResponseBuilder
    implements
        Builder<InsurerPolicySummaryResponse,
            InsurerPolicySummaryResponseBuilder> {
  _$InsurerPolicySummaryResponse? _$v;

  CoverStatus? _coverStatus;
  CoverStatus? get coverStatus => _$this._coverStatus;
  set coverStatus(CoverStatus? coverStatus) =>
      _$this._coverStatus = coverStatus;

  CoverageType? _coverageType;
  CoverageType? get coverageType => _$this._coverageType;
  set coverageType(CoverageType? coverageType) =>
      _$this._coverageType = coverageType;

  DateTime? _endAt;
  DateTime? get endAt => _$this._endAt;
  set endAt(DateTime? endAt) => _$this._endAt = endAt;

  String? _id;
  String? get id => _$this._id;
  set id(String? id) => _$this._id = id;

  DateTime? _issuedAt;
  DateTime? get issuedAt => _$this._issuedAt;
  set issuedAt(DateTime? issuedAt) => _$this._issuedAt = issuedAt;

  String? _number;
  String? get number => _$this._number;
  set number(String? number) => _$this._number = number;

  String? _plate;
  String? get plate => _$this._plate;
  set plate(String? plate) => _$this._plate = plate;

  String? _policyholderName;
  String? get policyholderName => _$this._policyholderName;
  set policyholderName(String? policyholderName) =>
      _$this._policyholderName = policyholderName;

  String? _productName;
  String? get productName => _$this._productName;
  set productName(String? productName) => _$this._productName = productName;

  DateTime? _startAt;
  DateTime? get startAt => _$this._startAt;
  set startAt(DateTime? startAt) => _$this._startAt = startAt;

  PolicyStatus? _status;
  PolicyStatus? get status => _$this._status;
  set status(PolicyStatus? status) => _$this._status = status;

  MoneyBuilder? _totalPremium;
  MoneyBuilder get totalPremium => _$this._totalPremium ??= MoneyBuilder();
  set totalPremium(MoneyBuilder? totalPremium) =>
      _$this._totalPremium = totalPremium;

  InsurerPolicySummaryResponseBuilder() {
    InsurerPolicySummaryResponse._defaults(this);
  }

  InsurerPolicySummaryResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _coverStatus = $v.coverStatus;
      _coverageType = $v.coverageType;
      _endAt = $v.endAt;
      _id = $v.id;
      _issuedAt = $v.issuedAt;
      _number = $v.number;
      _plate = $v.plate;
      _policyholderName = $v.policyholderName;
      _productName = $v.productName;
      _startAt = $v.startAt;
      _status = $v.status;
      _totalPremium = $v.totalPremium.toBuilder();
      _$v = null;
    }
    return this;
  }

  @override
  void replace(InsurerPolicySummaryResponse other) {
    _$v = other as _$InsurerPolicySummaryResponse;
  }

  @override
  void update(void Function(InsurerPolicySummaryResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  InsurerPolicySummaryResponse build() => _build();

  _$InsurerPolicySummaryResponse _build() {
    _$InsurerPolicySummaryResponse _$result;
    try {
      _$result = _$v ??
          _$InsurerPolicySummaryResponse._(
            coverStatus: BuiltValueNullFieldError.checkNotNull(
                coverStatus, r'InsurerPolicySummaryResponse', 'coverStatus'),
            coverageType: BuiltValueNullFieldError.checkNotNull(
                coverageType, r'InsurerPolicySummaryResponse', 'coverageType'),
            endAt: BuiltValueNullFieldError.checkNotNull(
                endAt, r'InsurerPolicySummaryResponse', 'endAt'),
            id: BuiltValueNullFieldError.checkNotNull(
                id, r'InsurerPolicySummaryResponse', 'id'),
            issuedAt: BuiltValueNullFieldError.checkNotNull(
                issuedAt, r'InsurerPolicySummaryResponse', 'issuedAt'),
            number: BuiltValueNullFieldError.checkNotNull(
                number, r'InsurerPolicySummaryResponse', 'number'),
            plate: BuiltValueNullFieldError.checkNotNull(
                plate, r'InsurerPolicySummaryResponse', 'plate'),
            policyholderName: BuiltValueNullFieldError.checkNotNull(
                policyholderName,
                r'InsurerPolicySummaryResponse',
                'policyholderName'),
            productName: BuiltValueNullFieldError.checkNotNull(
                productName, r'InsurerPolicySummaryResponse', 'productName'),
            startAt: BuiltValueNullFieldError.checkNotNull(
                startAt, r'InsurerPolicySummaryResponse', 'startAt'),
            status: BuiltValueNullFieldError.checkNotNull(
                status, r'InsurerPolicySummaryResponse', 'status'),
            totalPremium: totalPremium.build(),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'totalPremium';
        totalPremium.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'InsurerPolicySummaryResponse', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
