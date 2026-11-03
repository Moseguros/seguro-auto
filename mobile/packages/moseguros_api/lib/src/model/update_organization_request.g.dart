// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'update_organization_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const UpdateOrganizationRequestProvinceEnum
    _$updateOrganizationRequestProvinceEnum_MAPUTO_CITY =
    const UpdateOrganizationRequestProvinceEnum._('MAPUTO_CITY');
const UpdateOrganizationRequestProvinceEnum
    _$updateOrganizationRequestProvinceEnum_MAPUTO =
    const UpdateOrganizationRequestProvinceEnum._('MAPUTO');
const UpdateOrganizationRequestProvinceEnum
    _$updateOrganizationRequestProvinceEnum_GAZA =
    const UpdateOrganizationRequestProvinceEnum._('GAZA');
const UpdateOrganizationRequestProvinceEnum
    _$updateOrganizationRequestProvinceEnum_INHAMBANE =
    const UpdateOrganizationRequestProvinceEnum._('INHAMBANE');
const UpdateOrganizationRequestProvinceEnum
    _$updateOrganizationRequestProvinceEnum_SOFALA =
    const UpdateOrganizationRequestProvinceEnum._('SOFALA');
const UpdateOrganizationRequestProvinceEnum
    _$updateOrganizationRequestProvinceEnum_MANICA =
    const UpdateOrganizationRequestProvinceEnum._('MANICA');
const UpdateOrganizationRequestProvinceEnum
    _$updateOrganizationRequestProvinceEnum_TETE =
    const UpdateOrganizationRequestProvinceEnum._('TETE');
const UpdateOrganizationRequestProvinceEnum
    _$updateOrganizationRequestProvinceEnum_ZAMBEZIA =
    const UpdateOrganizationRequestProvinceEnum._('ZAMBEZIA');
const UpdateOrganizationRequestProvinceEnum
    _$updateOrganizationRequestProvinceEnum_NAMPULA =
    const UpdateOrganizationRequestProvinceEnum._('NAMPULA');
const UpdateOrganizationRequestProvinceEnum
    _$updateOrganizationRequestProvinceEnum_CABO_DELGADO =
    const UpdateOrganizationRequestProvinceEnum._('CABO_DELGADO');
const UpdateOrganizationRequestProvinceEnum
    _$updateOrganizationRequestProvinceEnum_NIASSA =
    const UpdateOrganizationRequestProvinceEnum._('NIASSA');

UpdateOrganizationRequestProvinceEnum
    _$updateOrganizationRequestProvinceEnumValueOf(String name) {
  switch (name) {
    case 'MAPUTO_CITY':
      return _$updateOrganizationRequestProvinceEnum_MAPUTO_CITY;
    case 'MAPUTO':
      return _$updateOrganizationRequestProvinceEnum_MAPUTO;
    case 'GAZA':
      return _$updateOrganizationRequestProvinceEnum_GAZA;
    case 'INHAMBANE':
      return _$updateOrganizationRequestProvinceEnum_INHAMBANE;
    case 'SOFALA':
      return _$updateOrganizationRequestProvinceEnum_SOFALA;
    case 'MANICA':
      return _$updateOrganizationRequestProvinceEnum_MANICA;
    case 'TETE':
      return _$updateOrganizationRequestProvinceEnum_TETE;
    case 'ZAMBEZIA':
      return _$updateOrganizationRequestProvinceEnum_ZAMBEZIA;
    case 'NAMPULA':
      return _$updateOrganizationRequestProvinceEnum_NAMPULA;
    case 'CABO_DELGADO':
      return _$updateOrganizationRequestProvinceEnum_CABO_DELGADO;
    case 'NIASSA':
      return _$updateOrganizationRequestProvinceEnum_NIASSA;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<UpdateOrganizationRequestProvinceEnum>
    _$updateOrganizationRequestProvinceEnumValues = BuiltSet<
        UpdateOrganizationRequestProvinceEnum>(const <UpdateOrganizationRequestProvinceEnum>[
  _$updateOrganizationRequestProvinceEnum_MAPUTO_CITY,
  _$updateOrganizationRequestProvinceEnum_MAPUTO,
  _$updateOrganizationRequestProvinceEnum_GAZA,
  _$updateOrganizationRequestProvinceEnum_INHAMBANE,
  _$updateOrganizationRequestProvinceEnum_SOFALA,
  _$updateOrganizationRequestProvinceEnum_MANICA,
  _$updateOrganizationRequestProvinceEnum_TETE,
  _$updateOrganizationRequestProvinceEnum_ZAMBEZIA,
  _$updateOrganizationRequestProvinceEnum_NAMPULA,
  _$updateOrganizationRequestProvinceEnum_CABO_DELGADO,
  _$updateOrganizationRequestProvinceEnum_NIASSA,
]);

Serializer<UpdateOrganizationRequestProvinceEnum>
    _$updateOrganizationRequestProvinceEnumSerializer =
    _$UpdateOrganizationRequestProvinceEnumSerializer();

class _$UpdateOrganizationRequestProvinceEnumSerializer
    implements PrimitiveSerializer<UpdateOrganizationRequestProvinceEnum> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'MAPUTO_CITY': 'MAPUTO_CITY',
    'MAPUTO': 'MAPUTO',
    'GAZA': 'GAZA',
    'INHAMBANE': 'INHAMBANE',
    'SOFALA': 'SOFALA',
    'MANICA': 'MANICA',
    'TETE': 'TETE',
    'ZAMBEZIA': 'ZAMBEZIA',
    'NAMPULA': 'NAMPULA',
    'CABO_DELGADO': 'CABO_DELGADO',
    'NIASSA': 'NIASSA',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'MAPUTO_CITY': 'MAPUTO_CITY',
    'MAPUTO': 'MAPUTO',
    'GAZA': 'GAZA',
    'INHAMBANE': 'INHAMBANE',
    'SOFALA': 'SOFALA',
    'MANICA': 'MANICA',
    'TETE': 'TETE',
    'ZAMBEZIA': 'ZAMBEZIA',
    'NAMPULA': 'NAMPULA',
    'CABO_DELGADO': 'CABO_DELGADO',
    'NIASSA': 'NIASSA',
  };

  @override
  final Iterable<Type> types = const <Type>[
    UpdateOrganizationRequestProvinceEnum
  ];
  @override
  final String wireName = 'UpdateOrganizationRequestProvinceEnum';

  @override
  Object serialize(
          Serializers serializers, UpdateOrganizationRequestProvinceEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  UpdateOrganizationRequestProvinceEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      UpdateOrganizationRequestProvinceEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$UpdateOrganizationRequest extends UpdateOrganizationRequest {
  @override
  final String? address;
  @override
  final String contactEmail;
  @override
  final String? contactPhone;
  @override
  final InsurerProfileRequest? insurerProfile;
  @override
  final String legalName;
  @override
  final String? licenseNumber;
  @override
  final String nuit;
  @override
  final UpdateOrganizationRequestProvinceEnum? province;
  @override
  final String? tradeName;
  @override
  final int version;

  factory _$UpdateOrganizationRequest(
          [void Function(UpdateOrganizationRequestBuilder)? updates]) =>
      (UpdateOrganizationRequestBuilder()..update(updates))._build();

  _$UpdateOrganizationRequest._(
      {this.address,
      required this.contactEmail,
      this.contactPhone,
      this.insurerProfile,
      required this.legalName,
      this.licenseNumber,
      required this.nuit,
      this.province,
      this.tradeName,
      required this.version})
      : super._();
  @override
  UpdateOrganizationRequest rebuild(
          void Function(UpdateOrganizationRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  UpdateOrganizationRequestBuilder toBuilder() =>
      UpdateOrganizationRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is UpdateOrganizationRequest &&
        address == other.address &&
        contactEmail == other.contactEmail &&
        contactPhone == other.contactPhone &&
        insurerProfile == other.insurerProfile &&
        legalName == other.legalName &&
        licenseNumber == other.licenseNumber &&
        nuit == other.nuit &&
        province == other.province &&
        tradeName == other.tradeName &&
        version == other.version;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, address.hashCode);
    _$hash = $jc(_$hash, contactEmail.hashCode);
    _$hash = $jc(_$hash, contactPhone.hashCode);
    _$hash = $jc(_$hash, insurerProfile.hashCode);
    _$hash = $jc(_$hash, legalName.hashCode);
    _$hash = $jc(_$hash, licenseNumber.hashCode);
    _$hash = $jc(_$hash, nuit.hashCode);
    _$hash = $jc(_$hash, province.hashCode);
    _$hash = $jc(_$hash, tradeName.hashCode);
    _$hash = $jc(_$hash, version.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'UpdateOrganizationRequest')
          ..add('address', address)
          ..add('contactEmail', contactEmail)
          ..add('contactPhone', contactPhone)
          ..add('insurerProfile', insurerProfile)
          ..add('legalName', legalName)
          ..add('licenseNumber', licenseNumber)
          ..add('nuit', nuit)
          ..add('province', province)
          ..add('tradeName', tradeName)
          ..add('version', version))
        .toString();
  }
}

class UpdateOrganizationRequestBuilder
    implements
        Builder<UpdateOrganizationRequest, UpdateOrganizationRequestBuilder> {
  _$UpdateOrganizationRequest? _$v;

  String? _address;
  String? get address => _$this._address;
  set address(String? address) => _$this._address = address;

  String? _contactEmail;
  String? get contactEmail => _$this._contactEmail;
  set contactEmail(String? contactEmail) => _$this._contactEmail = contactEmail;

  String? _contactPhone;
  String? get contactPhone => _$this._contactPhone;
  set contactPhone(String? contactPhone) => _$this._contactPhone = contactPhone;

  InsurerProfileRequestBuilder? _insurerProfile;
  InsurerProfileRequestBuilder get insurerProfile =>
      _$this._insurerProfile ??= InsurerProfileRequestBuilder();
  set insurerProfile(InsurerProfileRequestBuilder? insurerProfile) =>
      _$this._insurerProfile = insurerProfile;

  String? _legalName;
  String? get legalName => _$this._legalName;
  set legalName(String? legalName) => _$this._legalName = legalName;

  String? _licenseNumber;
  String? get licenseNumber => _$this._licenseNumber;
  set licenseNumber(String? licenseNumber) =>
      _$this._licenseNumber = licenseNumber;

  String? _nuit;
  String? get nuit => _$this._nuit;
  set nuit(String? nuit) => _$this._nuit = nuit;

  UpdateOrganizationRequestProvinceEnum? _province;
  UpdateOrganizationRequestProvinceEnum? get province => _$this._province;
  set province(UpdateOrganizationRequestProvinceEnum? province) =>
      _$this._province = province;

  String? _tradeName;
  String? get tradeName => _$this._tradeName;
  set tradeName(String? tradeName) => _$this._tradeName = tradeName;

  int? _version;
  int? get version => _$this._version;
  set version(int? version) => _$this._version = version;

  UpdateOrganizationRequestBuilder() {
    UpdateOrganizationRequest._defaults(this);
  }

  UpdateOrganizationRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _address = $v.address;
      _contactEmail = $v.contactEmail;
      _contactPhone = $v.contactPhone;
      _insurerProfile = $v.insurerProfile?.toBuilder();
      _legalName = $v.legalName;
      _licenseNumber = $v.licenseNumber;
      _nuit = $v.nuit;
      _province = $v.province;
      _tradeName = $v.tradeName;
      _version = $v.version;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(UpdateOrganizationRequest other) {
    _$v = other as _$UpdateOrganizationRequest;
  }

  @override
  void update(void Function(UpdateOrganizationRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  UpdateOrganizationRequest build() => _build();

  _$UpdateOrganizationRequest _build() {
    _$UpdateOrganizationRequest _$result;
    try {
      _$result = _$v ??
          _$UpdateOrganizationRequest._(
            address: address,
            contactEmail: BuiltValueNullFieldError.checkNotNull(
                contactEmail, r'UpdateOrganizationRequest', 'contactEmail'),
            contactPhone: contactPhone,
            insurerProfile: _insurerProfile?.build(),
            legalName: BuiltValueNullFieldError.checkNotNull(
                legalName, r'UpdateOrganizationRequest', 'legalName'),
            licenseNumber: licenseNumber,
            nuit: BuiltValueNullFieldError.checkNotNull(
                nuit, r'UpdateOrganizationRequest', 'nuit'),
            province: province,
            tradeName: tradeName,
            version: BuiltValueNullFieldError.checkNotNull(
                version, r'UpdateOrganizationRequest', 'version'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'insurerProfile';
        _insurerProfile?.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'UpdateOrganizationRequest', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
