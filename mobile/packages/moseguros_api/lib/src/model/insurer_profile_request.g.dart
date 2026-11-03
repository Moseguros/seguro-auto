// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'insurer_profile_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$InsurerProfileRequest extends InsurerProfileRequest {
  @override
  final String complaintsEmail;
  @override
  final String? ombudsmanOrganizationId;
  @override
  final String shortCode;

  factory _$InsurerProfileRequest(
          [void Function(InsurerProfileRequestBuilder)? updates]) =>
      (InsurerProfileRequestBuilder()..update(updates))._build();

  _$InsurerProfileRequest._(
      {required this.complaintsEmail,
      this.ombudsmanOrganizationId,
      required this.shortCode})
      : super._();
  @override
  InsurerProfileRequest rebuild(
          void Function(InsurerProfileRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  InsurerProfileRequestBuilder toBuilder() =>
      InsurerProfileRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is InsurerProfileRequest &&
        complaintsEmail == other.complaintsEmail &&
        ombudsmanOrganizationId == other.ombudsmanOrganizationId &&
        shortCode == other.shortCode;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, complaintsEmail.hashCode);
    _$hash = $jc(_$hash, ombudsmanOrganizationId.hashCode);
    _$hash = $jc(_$hash, shortCode.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'InsurerProfileRequest')
          ..add('complaintsEmail', complaintsEmail)
          ..add('ombudsmanOrganizationId', ombudsmanOrganizationId)
          ..add('shortCode', shortCode))
        .toString();
  }
}

class InsurerProfileRequestBuilder
    implements Builder<InsurerProfileRequest, InsurerProfileRequestBuilder> {
  _$InsurerProfileRequest? _$v;

  String? _complaintsEmail;
  String? get complaintsEmail => _$this._complaintsEmail;
  set complaintsEmail(String? complaintsEmail) =>
      _$this._complaintsEmail = complaintsEmail;

  String? _ombudsmanOrganizationId;
  String? get ombudsmanOrganizationId => _$this._ombudsmanOrganizationId;
  set ombudsmanOrganizationId(String? ombudsmanOrganizationId) =>
      _$this._ombudsmanOrganizationId = ombudsmanOrganizationId;

  String? _shortCode;
  String? get shortCode => _$this._shortCode;
  set shortCode(String? shortCode) => _$this._shortCode = shortCode;

  InsurerProfileRequestBuilder() {
    InsurerProfileRequest._defaults(this);
  }

  InsurerProfileRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _complaintsEmail = $v.complaintsEmail;
      _ombudsmanOrganizationId = $v.ombudsmanOrganizationId;
      _shortCode = $v.shortCode;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(InsurerProfileRequest other) {
    _$v = other as _$InsurerProfileRequest;
  }

  @override
  void update(void Function(InsurerProfileRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  InsurerProfileRequest build() => _build();

  _$InsurerProfileRequest _build() {
    final _$result = _$v ??
        _$InsurerProfileRequest._(
          complaintsEmail: BuiltValueNullFieldError.checkNotNull(
              complaintsEmail, r'InsurerProfileRequest', 'complaintsEmail'),
          ombudsmanOrganizationId: ombudsmanOrganizationId,
          shortCode: BuiltValueNullFieldError.checkNotNull(
              shortCode, r'InsurerProfileRequest', 'shortCode'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
