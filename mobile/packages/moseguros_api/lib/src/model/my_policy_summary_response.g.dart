// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'my_policy_summary_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$MyPolicySummaryResponse extends MyPolicySummaryResponse {
  @override
  final bool certificateReady;
  @override
  final CoverStatus coverStatus;
  @override
  final CoverageType coverageType;
  @override
  final DateTime endAt;
  @override
  final String id;
  @override
  final PolicyInsurerRef insurer;
  @override
  final String number;
  @override
  final String productName;
  @override
  final DateTime startAt;
  @override
  final PolicyStatus status;
  @override
  final PolicyVehicleRef vehicle;

  factory _$MyPolicySummaryResponse(
          [void Function(MyPolicySummaryResponseBuilder)? updates]) =>
      (MyPolicySummaryResponseBuilder()..update(updates))._build();

  _$MyPolicySummaryResponse._(
      {required this.certificateReady,
      required this.coverStatus,
      required this.coverageType,
      required this.endAt,
      required this.id,
      required this.insurer,
      required this.number,
      required this.productName,
      required this.startAt,
      required this.status,
      required this.vehicle})
      : super._();
  @override
  MyPolicySummaryResponse rebuild(
          void Function(MyPolicySummaryResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  MyPolicySummaryResponseBuilder toBuilder() =>
      MyPolicySummaryResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is MyPolicySummaryResponse &&
        certificateReady == other.certificateReady &&
        coverStatus == other.coverStatus &&
        coverageType == other.coverageType &&
        endAt == other.endAt &&
        id == other.id &&
        insurer == other.insurer &&
        number == other.number &&
        productName == other.productName &&
        startAt == other.startAt &&
        status == other.status &&
        vehicle == other.vehicle;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, certificateReady.hashCode);
    _$hash = $jc(_$hash, coverStatus.hashCode);
    _$hash = $jc(_$hash, coverageType.hashCode);
    _$hash = $jc(_$hash, endAt.hashCode);
    _$hash = $jc(_$hash, id.hashCode);
    _$hash = $jc(_$hash, insurer.hashCode);
    _$hash = $jc(_$hash, number.hashCode);
    _$hash = $jc(_$hash, productName.hashCode);
    _$hash = $jc(_$hash, startAt.hashCode);
    _$hash = $jc(_$hash, status.hashCode);
    _$hash = $jc(_$hash, vehicle.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'MyPolicySummaryResponse')
          ..add('certificateReady', certificateReady)
          ..add('coverStatus', coverStatus)
          ..add('coverageType', coverageType)
          ..add('endAt', endAt)
          ..add('id', id)
          ..add('insurer', insurer)
          ..add('number', number)
          ..add('productName', productName)
          ..add('startAt', startAt)
          ..add('status', status)
          ..add('vehicle', vehicle))
        .toString();
  }
}

class MyPolicySummaryResponseBuilder
    implements
        Builder<MyPolicySummaryResponse, MyPolicySummaryResponseBuilder> {
  _$MyPolicySummaryResponse? _$v;

  bool? _certificateReady;
  bool? get certificateReady => _$this._certificateReady;
  set certificateReady(bool? certificateReady) =>
      _$this._certificateReady = certificateReady;

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

  PolicyInsurerRefBuilder? _insurer;
  PolicyInsurerRefBuilder get insurer =>
      _$this._insurer ??= PolicyInsurerRefBuilder();
  set insurer(PolicyInsurerRefBuilder? insurer) => _$this._insurer = insurer;

  String? _number;
  String? get number => _$this._number;
  set number(String? number) => _$this._number = number;

  String? _productName;
  String? get productName => _$this._productName;
  set productName(String? productName) => _$this._productName = productName;

  DateTime? _startAt;
  DateTime? get startAt => _$this._startAt;
  set startAt(DateTime? startAt) => _$this._startAt = startAt;

  PolicyStatus? _status;
  PolicyStatus? get status => _$this._status;
  set status(PolicyStatus? status) => _$this._status = status;

  PolicyVehicleRefBuilder? _vehicle;
  PolicyVehicleRefBuilder get vehicle =>
      _$this._vehicle ??= PolicyVehicleRefBuilder();
  set vehicle(PolicyVehicleRefBuilder? vehicle) => _$this._vehicle = vehicle;

  MyPolicySummaryResponseBuilder() {
    MyPolicySummaryResponse._defaults(this);
  }

  MyPolicySummaryResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _certificateReady = $v.certificateReady;
      _coverStatus = $v.coverStatus;
      _coverageType = $v.coverageType;
      _endAt = $v.endAt;
      _id = $v.id;
      _insurer = $v.insurer.toBuilder();
      _number = $v.number;
      _productName = $v.productName;
      _startAt = $v.startAt;
      _status = $v.status;
      _vehicle = $v.vehicle.toBuilder();
      _$v = null;
    }
    return this;
  }

  @override
  void replace(MyPolicySummaryResponse other) {
    _$v = other as _$MyPolicySummaryResponse;
  }

  @override
  void update(void Function(MyPolicySummaryResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  MyPolicySummaryResponse build() => _build();

  _$MyPolicySummaryResponse _build() {
    _$MyPolicySummaryResponse _$result;
    try {
      _$result = _$v ??
          _$MyPolicySummaryResponse._(
            certificateReady: BuiltValueNullFieldError.checkNotNull(
                certificateReady,
                r'MyPolicySummaryResponse',
                'certificateReady'),
            coverStatus: BuiltValueNullFieldError.checkNotNull(
                coverStatus, r'MyPolicySummaryResponse', 'coverStatus'),
            coverageType: BuiltValueNullFieldError.checkNotNull(
                coverageType, r'MyPolicySummaryResponse', 'coverageType'),
            endAt: BuiltValueNullFieldError.checkNotNull(
                endAt, r'MyPolicySummaryResponse', 'endAt'),
            id: BuiltValueNullFieldError.checkNotNull(
                id, r'MyPolicySummaryResponse', 'id'),
            insurer: insurer.build(),
            number: BuiltValueNullFieldError.checkNotNull(
                number, r'MyPolicySummaryResponse', 'number'),
            productName: BuiltValueNullFieldError.checkNotNull(
                productName, r'MyPolicySummaryResponse', 'productName'),
            startAt: BuiltValueNullFieldError.checkNotNull(
                startAt, r'MyPolicySummaryResponse', 'startAt'),
            status: BuiltValueNullFieldError.checkNotNull(
                status, r'MyPolicySummaryResponse', 'status'),
            vehicle: vehicle.build(),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'insurer';
        insurer.build();

        _$failedField = 'vehicle';
        vehicle.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'MyPolicySummaryResponse', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
