// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'insurer_profile_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$InsurerProfileResponse extends InsurerProfileResponse {
  @override
  final String complaintsEmail;
  @override
  final String? ombudsmanOrganizationId;
  @override
  final String shortCode;

  factory _$InsurerProfileResponse(
          [void Function(InsurerProfileResponseBuilder)? updates]) =>
      (InsurerProfileResponseBuilder()..update(updates))._build();

  _$InsurerProfileResponse._(
      {required this.complaintsEmail,
      this.ombudsmanOrganizationId,
      required this.shortCode})
      : super._();
  @override
  InsurerProfileResponse rebuild(
          void Function(InsurerProfileResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  InsurerProfileResponseBuilder toBuilder() =>
      InsurerProfileResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is InsurerProfileResponse &&
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
    return (newBuiltValueToStringHelper(r'InsurerProfileResponse')
          ..add('complaintsEmail', complaintsEmail)
          ..add('ombudsmanOrganizationId', ombudsmanOrganizationId)
          ..add('shortCode', shortCode))
        .toString();
  }
}

class InsurerProfileResponseBuilder
    implements Builder<InsurerProfileResponse, InsurerProfileResponseBuilder> {
  _$InsurerProfileResponse? _$v;

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

  InsurerProfileResponseBuilder() {
    InsurerProfileResponse._defaults(this);
  }

  InsurerProfileResponseBuilder get _$this {
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
  void replace(InsurerProfileResponse other) {
    _$v = other as _$InsurerProfileResponse;
  }

  @override
  void update(void Function(InsurerProfileResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  InsurerProfileResponse build() => _build();

  _$InsurerProfileResponse _build() {
    final _$result = _$v ??
        _$InsurerProfileResponse._(
          complaintsEmail: BuiltValueNullFieldError.checkNotNull(
              complaintsEmail, r'InsurerProfileResponse', 'complaintsEmail'),
          ombudsmanOrganizationId: ombudsmanOrganizationId,
          shortCode: BuiltValueNullFieldError.checkNotNull(
              shortCode, r'InsurerProfileResponse', 'shortCode'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
