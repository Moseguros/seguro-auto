// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'update_my_profile_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const UpdateMyProfileRequestIdDocTypeEnum
    _$updateMyProfileRequestIdDocTypeEnum_BI =
    const UpdateMyProfileRequestIdDocTypeEnum._('BI');
const UpdateMyProfileRequestIdDocTypeEnum
    _$updateMyProfileRequestIdDocTypeEnum_PASSPORT =
    const UpdateMyProfileRequestIdDocTypeEnum._('PASSPORT');
const UpdateMyProfileRequestIdDocTypeEnum
    _$updateMyProfileRequestIdDocTypeEnum_DIRE =
    const UpdateMyProfileRequestIdDocTypeEnum._('DIRE');

UpdateMyProfileRequestIdDocTypeEnum
    _$updateMyProfileRequestIdDocTypeEnumValueOf(String name) {
  switch (name) {
    case 'BI':
      return _$updateMyProfileRequestIdDocTypeEnum_BI;
    case 'PASSPORT':
      return _$updateMyProfileRequestIdDocTypeEnum_PASSPORT;
    case 'DIRE':
      return _$updateMyProfileRequestIdDocTypeEnum_DIRE;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<UpdateMyProfileRequestIdDocTypeEnum>
    _$updateMyProfileRequestIdDocTypeEnumValues = BuiltSet<
        UpdateMyProfileRequestIdDocTypeEnum>(const <UpdateMyProfileRequestIdDocTypeEnum>[
  _$updateMyProfileRequestIdDocTypeEnum_BI,
  _$updateMyProfileRequestIdDocTypeEnum_PASSPORT,
  _$updateMyProfileRequestIdDocTypeEnum_DIRE,
]);

const UpdateMyProfileRequestProvinceEnum
    _$updateMyProfileRequestProvinceEnum_MAPUTO_CITY =
    const UpdateMyProfileRequestProvinceEnum._('MAPUTO_CITY');
const UpdateMyProfileRequestProvinceEnum
    _$updateMyProfileRequestProvinceEnum_MAPUTO =
    const UpdateMyProfileRequestProvinceEnum._('MAPUTO');
const UpdateMyProfileRequestProvinceEnum
    _$updateMyProfileRequestProvinceEnum_GAZA =
    const UpdateMyProfileRequestProvinceEnum._('GAZA');
const UpdateMyProfileRequestProvinceEnum
    _$updateMyProfileRequestProvinceEnum_INHAMBANE =
    const UpdateMyProfileRequestProvinceEnum._('INHAMBANE');
const UpdateMyProfileRequestProvinceEnum
    _$updateMyProfileRequestProvinceEnum_SOFALA =
    const UpdateMyProfileRequestProvinceEnum._('SOFALA');
const UpdateMyProfileRequestProvinceEnum
    _$updateMyProfileRequestProvinceEnum_MANICA =
    const UpdateMyProfileRequestProvinceEnum._('MANICA');
const UpdateMyProfileRequestProvinceEnum
    _$updateMyProfileRequestProvinceEnum_TETE =
    const UpdateMyProfileRequestProvinceEnum._('TETE');
const UpdateMyProfileRequestProvinceEnum
    _$updateMyProfileRequestProvinceEnum_ZAMBEZIA =
    const UpdateMyProfileRequestProvinceEnum._('ZAMBEZIA');
const UpdateMyProfileRequestProvinceEnum
    _$updateMyProfileRequestProvinceEnum_NAMPULA =
    const UpdateMyProfileRequestProvinceEnum._('NAMPULA');
const UpdateMyProfileRequestProvinceEnum
    _$updateMyProfileRequestProvinceEnum_CABO_DELGADO =
    const UpdateMyProfileRequestProvinceEnum._('CABO_DELGADO');
const UpdateMyProfileRequestProvinceEnum
    _$updateMyProfileRequestProvinceEnum_NIASSA =
    const UpdateMyProfileRequestProvinceEnum._('NIASSA');

UpdateMyProfileRequestProvinceEnum _$updateMyProfileRequestProvinceEnumValueOf(
    String name) {
  switch (name) {
    case 'MAPUTO_CITY':
      return _$updateMyProfileRequestProvinceEnum_MAPUTO_CITY;
    case 'MAPUTO':
      return _$updateMyProfileRequestProvinceEnum_MAPUTO;
    case 'GAZA':
      return _$updateMyProfileRequestProvinceEnum_GAZA;
    case 'INHAMBANE':
      return _$updateMyProfileRequestProvinceEnum_INHAMBANE;
    case 'SOFALA':
      return _$updateMyProfileRequestProvinceEnum_SOFALA;
    case 'MANICA':
      return _$updateMyProfileRequestProvinceEnum_MANICA;
    case 'TETE':
      return _$updateMyProfileRequestProvinceEnum_TETE;
    case 'ZAMBEZIA':
      return _$updateMyProfileRequestProvinceEnum_ZAMBEZIA;
    case 'NAMPULA':
      return _$updateMyProfileRequestProvinceEnum_NAMPULA;
    case 'CABO_DELGADO':
      return _$updateMyProfileRequestProvinceEnum_CABO_DELGADO;
    case 'NIASSA':
      return _$updateMyProfileRequestProvinceEnum_NIASSA;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<UpdateMyProfileRequestProvinceEnum>
    _$updateMyProfileRequestProvinceEnumValues = BuiltSet<
        UpdateMyProfileRequestProvinceEnum>(const <UpdateMyProfileRequestProvinceEnum>[
  _$updateMyProfileRequestProvinceEnum_MAPUTO_CITY,
  _$updateMyProfileRequestProvinceEnum_MAPUTO,
  _$updateMyProfileRequestProvinceEnum_GAZA,
  _$updateMyProfileRequestProvinceEnum_INHAMBANE,
  _$updateMyProfileRequestProvinceEnum_SOFALA,
  _$updateMyProfileRequestProvinceEnum_MANICA,
  _$updateMyProfileRequestProvinceEnum_TETE,
  _$updateMyProfileRequestProvinceEnum_ZAMBEZIA,
  _$updateMyProfileRequestProvinceEnum_NAMPULA,
  _$updateMyProfileRequestProvinceEnum_CABO_DELGADO,
  _$updateMyProfileRequestProvinceEnum_NIASSA,
]);

Serializer<UpdateMyProfileRequestIdDocTypeEnum>
    _$updateMyProfileRequestIdDocTypeEnumSerializer =
    _$UpdateMyProfileRequestIdDocTypeEnumSerializer();
Serializer<UpdateMyProfileRequestProvinceEnum>
    _$updateMyProfileRequestProvinceEnumSerializer =
    _$UpdateMyProfileRequestProvinceEnumSerializer();

class _$UpdateMyProfileRequestIdDocTypeEnumSerializer
    implements PrimitiveSerializer<UpdateMyProfileRequestIdDocTypeEnum> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'BI': 'BI',
    'PASSPORT': 'PASSPORT',
    'DIRE': 'DIRE',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'BI': 'BI',
    'PASSPORT': 'PASSPORT',
    'DIRE': 'DIRE',
  };

  @override
  final Iterable<Type> types = const <Type>[
    UpdateMyProfileRequestIdDocTypeEnum
  ];
  @override
  final String wireName = 'UpdateMyProfileRequestIdDocTypeEnum';

  @override
  Object serialize(
          Serializers serializers, UpdateMyProfileRequestIdDocTypeEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  UpdateMyProfileRequestIdDocTypeEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      UpdateMyProfileRequestIdDocTypeEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$UpdateMyProfileRequestProvinceEnumSerializer
    implements PrimitiveSerializer<UpdateMyProfileRequestProvinceEnum> {
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
  final Iterable<Type> types = const <Type>[UpdateMyProfileRequestProvinceEnum];
  @override
  final String wireName = 'UpdateMyProfileRequestProvinceEnum';

  @override
  Object serialize(
          Serializers serializers, UpdateMyProfileRequestProvinceEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  UpdateMyProfileRequestProvinceEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      UpdateMyProfileRequestProvinceEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$UpdateMyProfileRequest extends UpdateMyProfileRequest {
  @override
  final String? address;
  @override
  final Date birthDate;
  @override
  final String fullName;
  @override
  final String? idDocNumber;
  @override
  final UpdateMyProfileRequestIdDocTypeEnum idDocType;
  @override
  final String? nuit;
  @override
  final UpdateMyProfileRequestProvinceEnum province;
  @override
  final int version;

  factory _$UpdateMyProfileRequest(
          [void Function(UpdateMyProfileRequestBuilder)? updates]) =>
      (UpdateMyProfileRequestBuilder()..update(updates))._build();

  _$UpdateMyProfileRequest._(
      {this.address,
      required this.birthDate,
      required this.fullName,
      this.idDocNumber,
      required this.idDocType,
      this.nuit,
      required this.province,
      required this.version})
      : super._();
  @override
  UpdateMyProfileRequest rebuild(
          void Function(UpdateMyProfileRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  UpdateMyProfileRequestBuilder toBuilder() =>
      UpdateMyProfileRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is UpdateMyProfileRequest &&
        address == other.address &&
        birthDate == other.birthDate &&
        fullName == other.fullName &&
        idDocNumber == other.idDocNumber &&
        idDocType == other.idDocType &&
        nuit == other.nuit &&
        province == other.province &&
        version == other.version;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, address.hashCode);
    _$hash = $jc(_$hash, birthDate.hashCode);
    _$hash = $jc(_$hash, fullName.hashCode);
    _$hash = $jc(_$hash, idDocNumber.hashCode);
    _$hash = $jc(_$hash, idDocType.hashCode);
    _$hash = $jc(_$hash, nuit.hashCode);
    _$hash = $jc(_$hash, province.hashCode);
    _$hash = $jc(_$hash, version.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'UpdateMyProfileRequest')
          ..add('address', address)
          ..add('birthDate', birthDate)
          ..add('fullName', fullName)
          ..add('idDocNumber', idDocNumber)
          ..add('idDocType', idDocType)
          ..add('nuit', nuit)
          ..add('province', province)
          ..add('version', version))
        .toString();
  }
}

class UpdateMyProfileRequestBuilder
    implements Builder<UpdateMyProfileRequest, UpdateMyProfileRequestBuilder> {
  _$UpdateMyProfileRequest? _$v;

  String? _address;
  String? get address => _$this._address;
  set address(String? address) => _$this._address = address;

  Date? _birthDate;
  Date? get birthDate => _$this._birthDate;
  set birthDate(Date? birthDate) => _$this._birthDate = birthDate;

  String? _fullName;
  String? get fullName => _$this._fullName;
  set fullName(String? fullName) => _$this._fullName = fullName;

  String? _idDocNumber;
  String? get idDocNumber => _$this._idDocNumber;
  set idDocNumber(String? idDocNumber) => _$this._idDocNumber = idDocNumber;

  UpdateMyProfileRequestIdDocTypeEnum? _idDocType;
  UpdateMyProfileRequestIdDocTypeEnum? get idDocType => _$this._idDocType;
  set idDocType(UpdateMyProfileRequestIdDocTypeEnum? idDocType) =>
      _$this._idDocType = idDocType;

  String? _nuit;
  String? get nuit => _$this._nuit;
  set nuit(String? nuit) => _$this._nuit = nuit;

  UpdateMyProfileRequestProvinceEnum? _province;
  UpdateMyProfileRequestProvinceEnum? get province => _$this._province;
  set province(UpdateMyProfileRequestProvinceEnum? province) =>
      _$this._province = province;

  int? _version;
  int? get version => _$this._version;
  set version(int? version) => _$this._version = version;

  UpdateMyProfileRequestBuilder() {
    UpdateMyProfileRequest._defaults(this);
  }

  UpdateMyProfileRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _address = $v.address;
      _birthDate = $v.birthDate;
      _fullName = $v.fullName;
      _idDocNumber = $v.idDocNumber;
      _idDocType = $v.idDocType;
      _nuit = $v.nuit;
      _province = $v.province;
      _version = $v.version;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(UpdateMyProfileRequest other) {
    _$v = other as _$UpdateMyProfileRequest;
  }

  @override
  void update(void Function(UpdateMyProfileRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  UpdateMyProfileRequest build() => _build();

  _$UpdateMyProfileRequest _build() {
    final _$result = _$v ??
        _$UpdateMyProfileRequest._(
          address: address,
          birthDate: BuiltValueNullFieldError.checkNotNull(
              birthDate, r'UpdateMyProfileRequest', 'birthDate'),
          fullName: BuiltValueNullFieldError.checkNotNull(
              fullName, r'UpdateMyProfileRequest', 'fullName'),
          idDocNumber: idDocNumber,
          idDocType: BuiltValueNullFieldError.checkNotNull(
              idDocType, r'UpdateMyProfileRequest', 'idDocType'),
          nuit: nuit,
          province: BuiltValueNullFieldError.checkNotNull(
              province, r'UpdateMyProfileRequest', 'province'),
          version: BuiltValueNullFieldError.checkNotNull(
              version, r'UpdateMyProfileRequest', 'version'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
