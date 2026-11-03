// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'customer_profile_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const CustomerProfileResponseIdDocTypeEnum
    _$customerProfileResponseIdDocTypeEnum_BI =
    const CustomerProfileResponseIdDocTypeEnum._('BI');
const CustomerProfileResponseIdDocTypeEnum
    _$customerProfileResponseIdDocTypeEnum_PASSPORT =
    const CustomerProfileResponseIdDocTypeEnum._('PASSPORT');
const CustomerProfileResponseIdDocTypeEnum
    _$customerProfileResponseIdDocTypeEnum_DIRE =
    const CustomerProfileResponseIdDocTypeEnum._('DIRE');

CustomerProfileResponseIdDocTypeEnum
    _$customerProfileResponseIdDocTypeEnumValueOf(String name) {
  switch (name) {
    case 'BI':
      return _$customerProfileResponseIdDocTypeEnum_BI;
    case 'PASSPORT':
      return _$customerProfileResponseIdDocTypeEnum_PASSPORT;
    case 'DIRE':
      return _$customerProfileResponseIdDocTypeEnum_DIRE;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<CustomerProfileResponseIdDocTypeEnum>
    _$customerProfileResponseIdDocTypeEnumValues = BuiltSet<
        CustomerProfileResponseIdDocTypeEnum>(const <CustomerProfileResponseIdDocTypeEnum>[
  _$customerProfileResponseIdDocTypeEnum_BI,
  _$customerProfileResponseIdDocTypeEnum_PASSPORT,
  _$customerProfileResponseIdDocTypeEnum_DIRE,
]);

const CustomerProfileResponseProvinceEnum
    _$customerProfileResponseProvinceEnum_MAPUTO_CITY =
    const CustomerProfileResponseProvinceEnum._('MAPUTO_CITY');
const CustomerProfileResponseProvinceEnum
    _$customerProfileResponseProvinceEnum_MAPUTO =
    const CustomerProfileResponseProvinceEnum._('MAPUTO');
const CustomerProfileResponseProvinceEnum
    _$customerProfileResponseProvinceEnum_GAZA =
    const CustomerProfileResponseProvinceEnum._('GAZA');
const CustomerProfileResponseProvinceEnum
    _$customerProfileResponseProvinceEnum_INHAMBANE =
    const CustomerProfileResponseProvinceEnum._('INHAMBANE');
const CustomerProfileResponseProvinceEnum
    _$customerProfileResponseProvinceEnum_SOFALA =
    const CustomerProfileResponseProvinceEnum._('SOFALA');
const CustomerProfileResponseProvinceEnum
    _$customerProfileResponseProvinceEnum_MANICA =
    const CustomerProfileResponseProvinceEnum._('MANICA');
const CustomerProfileResponseProvinceEnum
    _$customerProfileResponseProvinceEnum_TETE =
    const CustomerProfileResponseProvinceEnum._('TETE');
const CustomerProfileResponseProvinceEnum
    _$customerProfileResponseProvinceEnum_ZAMBEZIA =
    const CustomerProfileResponseProvinceEnum._('ZAMBEZIA');
const CustomerProfileResponseProvinceEnum
    _$customerProfileResponseProvinceEnum_NAMPULA =
    const CustomerProfileResponseProvinceEnum._('NAMPULA');
const CustomerProfileResponseProvinceEnum
    _$customerProfileResponseProvinceEnum_CABO_DELGADO =
    const CustomerProfileResponseProvinceEnum._('CABO_DELGADO');
const CustomerProfileResponseProvinceEnum
    _$customerProfileResponseProvinceEnum_NIASSA =
    const CustomerProfileResponseProvinceEnum._('NIASSA');

CustomerProfileResponseProvinceEnum
    _$customerProfileResponseProvinceEnumValueOf(String name) {
  switch (name) {
    case 'MAPUTO_CITY':
      return _$customerProfileResponseProvinceEnum_MAPUTO_CITY;
    case 'MAPUTO':
      return _$customerProfileResponseProvinceEnum_MAPUTO;
    case 'GAZA':
      return _$customerProfileResponseProvinceEnum_GAZA;
    case 'INHAMBANE':
      return _$customerProfileResponseProvinceEnum_INHAMBANE;
    case 'SOFALA':
      return _$customerProfileResponseProvinceEnum_SOFALA;
    case 'MANICA':
      return _$customerProfileResponseProvinceEnum_MANICA;
    case 'TETE':
      return _$customerProfileResponseProvinceEnum_TETE;
    case 'ZAMBEZIA':
      return _$customerProfileResponseProvinceEnum_ZAMBEZIA;
    case 'NAMPULA':
      return _$customerProfileResponseProvinceEnum_NAMPULA;
    case 'CABO_DELGADO':
      return _$customerProfileResponseProvinceEnum_CABO_DELGADO;
    case 'NIASSA':
      return _$customerProfileResponseProvinceEnum_NIASSA;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<CustomerProfileResponseProvinceEnum>
    _$customerProfileResponseProvinceEnumValues = BuiltSet<
        CustomerProfileResponseProvinceEnum>(const <CustomerProfileResponseProvinceEnum>[
  _$customerProfileResponseProvinceEnum_MAPUTO_CITY,
  _$customerProfileResponseProvinceEnum_MAPUTO,
  _$customerProfileResponseProvinceEnum_GAZA,
  _$customerProfileResponseProvinceEnum_INHAMBANE,
  _$customerProfileResponseProvinceEnum_SOFALA,
  _$customerProfileResponseProvinceEnum_MANICA,
  _$customerProfileResponseProvinceEnum_TETE,
  _$customerProfileResponseProvinceEnum_ZAMBEZIA,
  _$customerProfileResponseProvinceEnum_NAMPULA,
  _$customerProfileResponseProvinceEnum_CABO_DELGADO,
  _$customerProfileResponseProvinceEnum_NIASSA,
]);

Serializer<CustomerProfileResponseIdDocTypeEnum>
    _$customerProfileResponseIdDocTypeEnumSerializer =
    _$CustomerProfileResponseIdDocTypeEnumSerializer();
Serializer<CustomerProfileResponseProvinceEnum>
    _$customerProfileResponseProvinceEnumSerializer =
    _$CustomerProfileResponseProvinceEnumSerializer();

class _$CustomerProfileResponseIdDocTypeEnumSerializer
    implements PrimitiveSerializer<CustomerProfileResponseIdDocTypeEnum> {
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
    CustomerProfileResponseIdDocTypeEnum
  ];
  @override
  final String wireName = 'CustomerProfileResponseIdDocTypeEnum';

  @override
  Object serialize(
          Serializers serializers, CustomerProfileResponseIdDocTypeEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  CustomerProfileResponseIdDocTypeEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      CustomerProfileResponseIdDocTypeEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$CustomerProfileResponseProvinceEnumSerializer
    implements PrimitiveSerializer<CustomerProfileResponseProvinceEnum> {
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
    CustomerProfileResponseProvinceEnum
  ];
  @override
  final String wireName = 'CustomerProfileResponseProvinceEnum';

  @override
  Object serialize(
          Serializers serializers, CustomerProfileResponseProvinceEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  CustomerProfileResponseProvinceEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      CustomerProfileResponseProvinceEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$CustomerProfileResponse extends CustomerProfileResponse {
  @override
  final String? address;
  @override
  final Date? birthDate;
  @override
  final bool complete;
  @override
  final String? fullName;
  @override
  final String? idDocNumberMasked;
  @override
  final CustomerProfileResponseIdDocTypeEnum? idDocType;
  @override
  final String? nuitMasked;
  @override
  final CustomerProfileResponseProvinceEnum? province;
  @override
  final int version;

  factory _$CustomerProfileResponse(
          [void Function(CustomerProfileResponseBuilder)? updates]) =>
      (CustomerProfileResponseBuilder()..update(updates))._build();

  _$CustomerProfileResponse._(
      {this.address,
      this.birthDate,
      required this.complete,
      this.fullName,
      this.idDocNumberMasked,
      this.idDocType,
      this.nuitMasked,
      this.province,
      required this.version})
      : super._();
  @override
  CustomerProfileResponse rebuild(
          void Function(CustomerProfileResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  CustomerProfileResponseBuilder toBuilder() =>
      CustomerProfileResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is CustomerProfileResponse &&
        address == other.address &&
        birthDate == other.birthDate &&
        complete == other.complete &&
        fullName == other.fullName &&
        idDocNumberMasked == other.idDocNumberMasked &&
        idDocType == other.idDocType &&
        nuitMasked == other.nuitMasked &&
        province == other.province &&
        version == other.version;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, address.hashCode);
    _$hash = $jc(_$hash, birthDate.hashCode);
    _$hash = $jc(_$hash, complete.hashCode);
    _$hash = $jc(_$hash, fullName.hashCode);
    _$hash = $jc(_$hash, idDocNumberMasked.hashCode);
    _$hash = $jc(_$hash, idDocType.hashCode);
    _$hash = $jc(_$hash, nuitMasked.hashCode);
    _$hash = $jc(_$hash, province.hashCode);
    _$hash = $jc(_$hash, version.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'CustomerProfileResponse')
          ..add('address', address)
          ..add('birthDate', birthDate)
          ..add('complete', complete)
          ..add('fullName', fullName)
          ..add('idDocNumberMasked', idDocNumberMasked)
          ..add('idDocType', idDocType)
          ..add('nuitMasked', nuitMasked)
          ..add('province', province)
          ..add('version', version))
        .toString();
  }
}

class CustomerProfileResponseBuilder
    implements
        Builder<CustomerProfileResponse, CustomerProfileResponseBuilder> {
  _$CustomerProfileResponse? _$v;

  String? _address;
  String? get address => _$this._address;
  set address(String? address) => _$this._address = address;

  Date? _birthDate;
  Date? get birthDate => _$this._birthDate;
  set birthDate(Date? birthDate) => _$this._birthDate = birthDate;

  bool? _complete;
  bool? get complete => _$this._complete;
  set complete(bool? complete) => _$this._complete = complete;

  String? _fullName;
  String? get fullName => _$this._fullName;
  set fullName(String? fullName) => _$this._fullName = fullName;

  String? _idDocNumberMasked;
  String? get idDocNumberMasked => _$this._idDocNumberMasked;
  set idDocNumberMasked(String? idDocNumberMasked) =>
      _$this._idDocNumberMasked = idDocNumberMasked;

  CustomerProfileResponseIdDocTypeEnum? _idDocType;
  CustomerProfileResponseIdDocTypeEnum? get idDocType => _$this._idDocType;
  set idDocType(CustomerProfileResponseIdDocTypeEnum? idDocType) =>
      _$this._idDocType = idDocType;

  String? _nuitMasked;
  String? get nuitMasked => _$this._nuitMasked;
  set nuitMasked(String? nuitMasked) => _$this._nuitMasked = nuitMasked;

  CustomerProfileResponseProvinceEnum? _province;
  CustomerProfileResponseProvinceEnum? get province => _$this._province;
  set province(CustomerProfileResponseProvinceEnum? province) =>
      _$this._province = province;

  int? _version;
  int? get version => _$this._version;
  set version(int? version) => _$this._version = version;

  CustomerProfileResponseBuilder() {
    CustomerProfileResponse._defaults(this);
  }

  CustomerProfileResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _address = $v.address;
      _birthDate = $v.birthDate;
      _complete = $v.complete;
      _fullName = $v.fullName;
      _idDocNumberMasked = $v.idDocNumberMasked;
      _idDocType = $v.idDocType;
      _nuitMasked = $v.nuitMasked;
      _province = $v.province;
      _version = $v.version;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(CustomerProfileResponse other) {
    _$v = other as _$CustomerProfileResponse;
  }

  @override
  void update(void Function(CustomerProfileResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  CustomerProfileResponse build() => _build();

  _$CustomerProfileResponse _build() {
    final _$result = _$v ??
        _$CustomerProfileResponse._(
          address: address,
          birthDate: birthDate,
          complete: BuiltValueNullFieldError.checkNotNull(
              complete, r'CustomerProfileResponse', 'complete'),
          fullName: fullName,
          idDocNumberMasked: idDocNumberMasked,
          idDocType: idDocType,
          nuitMasked: nuitMasked,
          province: province,
          version: BuiltValueNullFieldError.checkNotNull(
              version, r'CustomerProfileResponse', 'version'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
