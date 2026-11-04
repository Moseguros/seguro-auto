// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'insurer_kyc_check_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$InsurerKycCheckResponse extends InsurerKycCheckResponse {
  @override
  final KycCustomerResponse customer;
  @override
  final KycFilesResponse files;
  @override
  final String id;
  @override
  final BuiltList<KycPreviousCheckResponse> previousChecks;
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
  @override
  final int version;

  factory _$InsurerKycCheckResponse(
          [void Function(InsurerKycCheckResponseBuilder)? updates]) =>
      (InsurerKycCheckResponseBuilder()..update(updates))._build();

  _$InsurerKycCheckResponse._(
      {required this.customer,
      required this.files,
      required this.id,
      required this.previousChecks,
      this.rejectionReason,
      this.rejectionReasonCode,
      this.reviewedAt,
      required this.status,
      required this.submittedAt,
      required this.version})
      : super._();
  @override
  InsurerKycCheckResponse rebuild(
          void Function(InsurerKycCheckResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  InsurerKycCheckResponseBuilder toBuilder() =>
      InsurerKycCheckResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is InsurerKycCheckResponse &&
        customer == other.customer &&
        files == other.files &&
        id == other.id &&
        previousChecks == other.previousChecks &&
        rejectionReason == other.rejectionReason &&
        rejectionReasonCode == other.rejectionReasonCode &&
        reviewedAt == other.reviewedAt &&
        status == other.status &&
        submittedAt == other.submittedAt &&
        version == other.version;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, customer.hashCode);
    _$hash = $jc(_$hash, files.hashCode);
    _$hash = $jc(_$hash, id.hashCode);
    _$hash = $jc(_$hash, previousChecks.hashCode);
    _$hash = $jc(_$hash, rejectionReason.hashCode);
    _$hash = $jc(_$hash, rejectionReasonCode.hashCode);
    _$hash = $jc(_$hash, reviewedAt.hashCode);
    _$hash = $jc(_$hash, status.hashCode);
    _$hash = $jc(_$hash, submittedAt.hashCode);
    _$hash = $jc(_$hash, version.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'InsurerKycCheckResponse')
          ..add('customer', customer)
          ..add('files', files)
          ..add('id', id)
          ..add('previousChecks', previousChecks)
          ..add('rejectionReason', rejectionReason)
          ..add('rejectionReasonCode', rejectionReasonCode)
          ..add('reviewedAt', reviewedAt)
          ..add('status', status)
          ..add('submittedAt', submittedAt)
          ..add('version', version))
        .toString();
  }
}

class InsurerKycCheckResponseBuilder
    implements
        Builder<InsurerKycCheckResponse, InsurerKycCheckResponseBuilder> {
  _$InsurerKycCheckResponse? _$v;

  KycCustomerResponseBuilder? _customer;
  KycCustomerResponseBuilder get customer =>
      _$this._customer ??= KycCustomerResponseBuilder();
  set customer(KycCustomerResponseBuilder? customer) =>
      _$this._customer = customer;

  KycFilesResponseBuilder? _files;
  KycFilesResponseBuilder get files =>
      _$this._files ??= KycFilesResponseBuilder();
  set files(KycFilesResponseBuilder? files) => _$this._files = files;

  String? _id;
  String? get id => _$this._id;
  set id(String? id) => _$this._id = id;

  ListBuilder<KycPreviousCheckResponse>? _previousChecks;
  ListBuilder<KycPreviousCheckResponse> get previousChecks =>
      _$this._previousChecks ??= ListBuilder<KycPreviousCheckResponse>();
  set previousChecks(ListBuilder<KycPreviousCheckResponse>? previousChecks) =>
      _$this._previousChecks = previousChecks;

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

  int? _version;
  int? get version => _$this._version;
  set version(int? version) => _$this._version = version;

  InsurerKycCheckResponseBuilder() {
    InsurerKycCheckResponse._defaults(this);
  }

  InsurerKycCheckResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _customer = $v.customer.toBuilder();
      _files = $v.files.toBuilder();
      _id = $v.id;
      _previousChecks = $v.previousChecks.toBuilder();
      _rejectionReason = $v.rejectionReason;
      _rejectionReasonCode = $v.rejectionReasonCode;
      _reviewedAt = $v.reviewedAt;
      _status = $v.status;
      _submittedAt = $v.submittedAt;
      _version = $v.version;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(InsurerKycCheckResponse other) {
    _$v = other as _$InsurerKycCheckResponse;
  }

  @override
  void update(void Function(InsurerKycCheckResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  InsurerKycCheckResponse build() => _build();

  _$InsurerKycCheckResponse _build() {
    _$InsurerKycCheckResponse _$result;
    try {
      _$result = _$v ??
          _$InsurerKycCheckResponse._(
            customer: customer.build(),
            files: files.build(),
            id: BuiltValueNullFieldError.checkNotNull(
                id, r'InsurerKycCheckResponse', 'id'),
            previousChecks: previousChecks.build(),
            rejectionReason: rejectionReason,
            rejectionReasonCode: rejectionReasonCode,
            reviewedAt: reviewedAt,
            status: BuiltValueNullFieldError.checkNotNull(
                status, r'InsurerKycCheckResponse', 'status'),
            submittedAt: BuiltValueNullFieldError.checkNotNull(
                submittedAt, r'InsurerKycCheckResponse', 'submittedAt'),
            version: BuiltValueNullFieldError.checkNotNull(
                version, r'InsurerKycCheckResponse', 'version'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'customer';
        customer.build();
        _$failedField = 'files';
        files.build();

        _$failedField = 'previousChecks';
        previousChecks.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'InsurerKycCheckResponse', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
