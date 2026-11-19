// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'public_verification_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$PublicVerificationResponse extends PublicVerificationResponse {
  @override
  final CertificateCheckStatus? certificateStatus;
  @override
  final DateTime checkedAt;
  @override
  final CoverageType? coverageType;
  @override
  final String? insurerName;
  @override
  final String? plateMasked;
  @override
  final VerificationResult status;
  @override
  final DateTime? validFrom;
  @override
  final DateTime? validTo;

  factory _$PublicVerificationResponse(
          [void Function(PublicVerificationResponseBuilder)? updates]) =>
      (PublicVerificationResponseBuilder()..update(updates))._build();

  _$PublicVerificationResponse._(
      {this.certificateStatus,
      required this.checkedAt,
      this.coverageType,
      this.insurerName,
      this.plateMasked,
      required this.status,
      this.validFrom,
      this.validTo})
      : super._();
  @override
  PublicVerificationResponse rebuild(
          void Function(PublicVerificationResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PublicVerificationResponseBuilder toBuilder() =>
      PublicVerificationResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PublicVerificationResponse &&
        certificateStatus == other.certificateStatus &&
        checkedAt == other.checkedAt &&
        coverageType == other.coverageType &&
        insurerName == other.insurerName &&
        plateMasked == other.plateMasked &&
        status == other.status &&
        validFrom == other.validFrom &&
        validTo == other.validTo;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, certificateStatus.hashCode);
    _$hash = $jc(_$hash, checkedAt.hashCode);
    _$hash = $jc(_$hash, coverageType.hashCode);
    _$hash = $jc(_$hash, insurerName.hashCode);
    _$hash = $jc(_$hash, plateMasked.hashCode);
    _$hash = $jc(_$hash, status.hashCode);
    _$hash = $jc(_$hash, validFrom.hashCode);
    _$hash = $jc(_$hash, validTo.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'PublicVerificationResponse')
          ..add('certificateStatus', certificateStatus)
          ..add('checkedAt', checkedAt)
          ..add('coverageType', coverageType)
          ..add('insurerName', insurerName)
          ..add('plateMasked', plateMasked)
          ..add('status', status)
          ..add('validFrom', validFrom)
          ..add('validTo', validTo))
        .toString();
  }
}

class PublicVerificationResponseBuilder
    implements
        Builder<PublicVerificationResponse, PublicVerificationResponseBuilder> {
  _$PublicVerificationResponse? _$v;

  CertificateCheckStatus? _certificateStatus;
  CertificateCheckStatus? get certificateStatus => _$this._certificateStatus;
  set certificateStatus(CertificateCheckStatus? certificateStatus) =>
      _$this._certificateStatus = certificateStatus;

  DateTime? _checkedAt;
  DateTime? get checkedAt => _$this._checkedAt;
  set checkedAt(DateTime? checkedAt) => _$this._checkedAt = checkedAt;

  CoverageType? _coverageType;
  CoverageType? get coverageType => _$this._coverageType;
  set coverageType(CoverageType? coverageType) =>
      _$this._coverageType = coverageType;

  String? _insurerName;
  String? get insurerName => _$this._insurerName;
  set insurerName(String? insurerName) => _$this._insurerName = insurerName;

  String? _plateMasked;
  String? get plateMasked => _$this._plateMasked;
  set plateMasked(String? plateMasked) => _$this._plateMasked = plateMasked;

  VerificationResult? _status;
  VerificationResult? get status => _$this._status;
  set status(VerificationResult? status) => _$this._status = status;

  DateTime? _validFrom;
  DateTime? get validFrom => _$this._validFrom;
  set validFrom(DateTime? validFrom) => _$this._validFrom = validFrom;

  DateTime? _validTo;
  DateTime? get validTo => _$this._validTo;
  set validTo(DateTime? validTo) => _$this._validTo = validTo;

  PublicVerificationResponseBuilder() {
    PublicVerificationResponse._defaults(this);
  }

  PublicVerificationResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _certificateStatus = $v.certificateStatus;
      _checkedAt = $v.checkedAt;
      _coverageType = $v.coverageType;
      _insurerName = $v.insurerName;
      _plateMasked = $v.plateMasked;
      _status = $v.status;
      _validFrom = $v.validFrom;
      _validTo = $v.validTo;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(PublicVerificationResponse other) {
    _$v = other as _$PublicVerificationResponse;
  }

  @override
  void update(void Function(PublicVerificationResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PublicVerificationResponse build() => _build();

  _$PublicVerificationResponse _build() {
    final _$result = _$v ??
        _$PublicVerificationResponse._(
          certificateStatus: certificateStatus,
          checkedAt: BuiltValueNullFieldError.checkNotNull(
              checkedAt, r'PublicVerificationResponse', 'checkedAt'),
          coverageType: coverageType,
          insurerName: insurerName,
          plateMasked: plateMasked,
          status: BuiltValueNullFieldError.checkNotNull(
              status, r'PublicVerificationResponse', 'status'),
          validFrom: validFrom,
          validTo: validTo,
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
