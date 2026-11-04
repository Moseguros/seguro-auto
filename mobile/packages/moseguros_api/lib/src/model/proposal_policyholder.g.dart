// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'proposal_policyholder.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$ProposalPolicyholder extends ProposalPolicyholder {
  @override
  final String? address;
  @override
  final Date birthDate;
  @override
  final String? emailMasked;
  @override
  final String fullName;
  @override
  final String idDocNumberMasked;
  @override
  final String idDocType;
  @override
  final String? msisdnMasked;
  @override
  final String nuitMasked;
  @override
  final String province;

  factory _$ProposalPolicyholder(
          [void Function(ProposalPolicyholderBuilder)? updates]) =>
      (ProposalPolicyholderBuilder()..update(updates))._build();

  _$ProposalPolicyholder._(
      {this.address,
      required this.birthDate,
      this.emailMasked,
      required this.fullName,
      required this.idDocNumberMasked,
      required this.idDocType,
      this.msisdnMasked,
      required this.nuitMasked,
      required this.province})
      : super._();
  @override
  ProposalPolicyholder rebuild(
          void Function(ProposalPolicyholderBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  ProposalPolicyholderBuilder toBuilder() =>
      ProposalPolicyholderBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is ProposalPolicyholder &&
        address == other.address &&
        birthDate == other.birthDate &&
        emailMasked == other.emailMasked &&
        fullName == other.fullName &&
        idDocNumberMasked == other.idDocNumberMasked &&
        idDocType == other.idDocType &&
        msisdnMasked == other.msisdnMasked &&
        nuitMasked == other.nuitMasked &&
        province == other.province;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, address.hashCode);
    _$hash = $jc(_$hash, birthDate.hashCode);
    _$hash = $jc(_$hash, emailMasked.hashCode);
    _$hash = $jc(_$hash, fullName.hashCode);
    _$hash = $jc(_$hash, idDocNumberMasked.hashCode);
    _$hash = $jc(_$hash, idDocType.hashCode);
    _$hash = $jc(_$hash, msisdnMasked.hashCode);
    _$hash = $jc(_$hash, nuitMasked.hashCode);
    _$hash = $jc(_$hash, province.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'ProposalPolicyholder')
          ..add('address', address)
          ..add('birthDate', birthDate)
          ..add('emailMasked', emailMasked)
          ..add('fullName', fullName)
          ..add('idDocNumberMasked', idDocNumberMasked)
          ..add('idDocType', idDocType)
          ..add('msisdnMasked', msisdnMasked)
          ..add('nuitMasked', nuitMasked)
          ..add('province', province))
        .toString();
  }
}

class ProposalPolicyholderBuilder
    implements Builder<ProposalPolicyholder, ProposalPolicyholderBuilder> {
  _$ProposalPolicyholder? _$v;

  String? _address;
  String? get address => _$this._address;
  set address(String? address) => _$this._address = address;

  Date? _birthDate;
  Date? get birthDate => _$this._birthDate;
  set birthDate(Date? birthDate) => _$this._birthDate = birthDate;

  String? _emailMasked;
  String? get emailMasked => _$this._emailMasked;
  set emailMasked(String? emailMasked) => _$this._emailMasked = emailMasked;

  String? _fullName;
  String? get fullName => _$this._fullName;
  set fullName(String? fullName) => _$this._fullName = fullName;

  String? _idDocNumberMasked;
  String? get idDocNumberMasked => _$this._idDocNumberMasked;
  set idDocNumberMasked(String? idDocNumberMasked) =>
      _$this._idDocNumberMasked = idDocNumberMasked;

  String? _idDocType;
  String? get idDocType => _$this._idDocType;
  set idDocType(String? idDocType) => _$this._idDocType = idDocType;

  String? _msisdnMasked;
  String? get msisdnMasked => _$this._msisdnMasked;
  set msisdnMasked(String? msisdnMasked) => _$this._msisdnMasked = msisdnMasked;

  String? _nuitMasked;
  String? get nuitMasked => _$this._nuitMasked;
  set nuitMasked(String? nuitMasked) => _$this._nuitMasked = nuitMasked;

  String? _province;
  String? get province => _$this._province;
  set province(String? province) => _$this._province = province;

  ProposalPolicyholderBuilder() {
    ProposalPolicyholder._defaults(this);
  }

  ProposalPolicyholderBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _address = $v.address;
      _birthDate = $v.birthDate;
      _emailMasked = $v.emailMasked;
      _fullName = $v.fullName;
      _idDocNumberMasked = $v.idDocNumberMasked;
      _idDocType = $v.idDocType;
      _msisdnMasked = $v.msisdnMasked;
      _nuitMasked = $v.nuitMasked;
      _province = $v.province;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(ProposalPolicyholder other) {
    _$v = other as _$ProposalPolicyholder;
  }

  @override
  void update(void Function(ProposalPolicyholderBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  ProposalPolicyholder build() => _build();

  _$ProposalPolicyholder _build() {
    final _$result = _$v ??
        _$ProposalPolicyholder._(
          address: address,
          birthDate: BuiltValueNullFieldError.checkNotNull(
              birthDate, r'ProposalPolicyholder', 'birthDate'),
          emailMasked: emailMasked,
          fullName: BuiltValueNullFieldError.checkNotNull(
              fullName, r'ProposalPolicyholder', 'fullName'),
          idDocNumberMasked: BuiltValueNullFieldError.checkNotNull(
              idDocNumberMasked, r'ProposalPolicyholder', 'idDocNumberMasked'),
          idDocType: BuiltValueNullFieldError.checkNotNull(
              idDocType, r'ProposalPolicyholder', 'idDocType'),
          msisdnMasked: msisdnMasked,
          nuitMasked: BuiltValueNullFieldError.checkNotNull(
              nuitMasked, r'ProposalPolicyholder', 'nuitMasked'),
          province: BuiltValueNullFieldError.checkNotNull(
              province, r'ProposalPolicyholder', 'province'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
