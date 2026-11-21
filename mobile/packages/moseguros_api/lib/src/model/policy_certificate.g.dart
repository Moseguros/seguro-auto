// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'policy_certificate.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$PolicyCertificate extends PolicyCertificate {
  @override
  final String? fileId;
  @override
  final String id;
  @override
  final String number;
  @override
  final CertificateStatus status;
  @override
  final DateTime validFrom;
  @override
  final DateTime validTo;
  @override
  final String verificationUrl;

  factory _$PolicyCertificate(
          [void Function(PolicyCertificateBuilder)? updates]) =>
      (PolicyCertificateBuilder()..update(updates))._build();

  _$PolicyCertificate._(
      {this.fileId,
      required this.id,
      required this.number,
      required this.status,
      required this.validFrom,
      required this.validTo,
      required this.verificationUrl})
      : super._();
  @override
  PolicyCertificate rebuild(void Function(PolicyCertificateBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PolicyCertificateBuilder toBuilder() =>
      PolicyCertificateBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PolicyCertificate &&
        fileId == other.fileId &&
        id == other.id &&
        number == other.number &&
        status == other.status &&
        validFrom == other.validFrom &&
        validTo == other.validTo &&
        verificationUrl == other.verificationUrl;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, fileId.hashCode);
    _$hash = $jc(_$hash, id.hashCode);
    _$hash = $jc(_$hash, number.hashCode);
    _$hash = $jc(_$hash, status.hashCode);
    _$hash = $jc(_$hash, validFrom.hashCode);
    _$hash = $jc(_$hash, validTo.hashCode);
    _$hash = $jc(_$hash, verificationUrl.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'PolicyCertificate')
          ..add('fileId', fileId)
          ..add('id', id)
          ..add('number', number)
          ..add('status', status)
          ..add('validFrom', validFrom)
          ..add('validTo', validTo)
          ..add('verificationUrl', verificationUrl))
        .toString();
  }
}

class PolicyCertificateBuilder
    implements Builder<PolicyCertificate, PolicyCertificateBuilder> {
  _$PolicyCertificate? _$v;

  String? _fileId;
  String? get fileId => _$this._fileId;
  set fileId(String? fileId) => _$this._fileId = fileId;

  String? _id;
  String? get id => _$this._id;
  set id(String? id) => _$this._id = id;

  String? _number;
  String? get number => _$this._number;
  set number(String? number) => _$this._number = number;

  CertificateStatus? _status;
  CertificateStatus? get status => _$this._status;
  set status(CertificateStatus? status) => _$this._status = status;

  DateTime? _validFrom;
  DateTime? get validFrom => _$this._validFrom;
  set validFrom(DateTime? validFrom) => _$this._validFrom = validFrom;

  DateTime? _validTo;
  DateTime? get validTo => _$this._validTo;
  set validTo(DateTime? validTo) => _$this._validTo = validTo;

  String? _verificationUrl;
  String? get verificationUrl => _$this._verificationUrl;
  set verificationUrl(String? verificationUrl) =>
      _$this._verificationUrl = verificationUrl;

  PolicyCertificateBuilder() {
    PolicyCertificate._defaults(this);
  }

  PolicyCertificateBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _fileId = $v.fileId;
      _id = $v.id;
      _number = $v.number;
      _status = $v.status;
      _validFrom = $v.validFrom;
      _validTo = $v.validTo;
      _verificationUrl = $v.verificationUrl;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(PolicyCertificate other) {
    _$v = other as _$PolicyCertificate;
  }

  @override
  void update(void Function(PolicyCertificateBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PolicyCertificate build() => _build();

  _$PolicyCertificate _build() {
    final _$result = _$v ??
        _$PolicyCertificate._(
          fileId: fileId,
          id: BuiltValueNullFieldError.checkNotNull(
              id, r'PolicyCertificate', 'id'),
          number: BuiltValueNullFieldError.checkNotNull(
              number, r'PolicyCertificate', 'number'),
          status: BuiltValueNullFieldError.checkNotNull(
              status, r'PolicyCertificate', 'status'),
          validFrom: BuiltValueNullFieldError.checkNotNull(
              validFrom, r'PolicyCertificate', 'validFrom'),
          validTo: BuiltValueNullFieldError.checkNotNull(
              validTo, r'PolicyCertificate', 'validTo'),
          verificationUrl: BuiltValueNullFieldError.checkNotNull(
              verificationUrl, r'PolicyCertificate', 'verificationUrl'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
