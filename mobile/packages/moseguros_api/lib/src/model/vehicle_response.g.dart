// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'vehicle_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const VehicleResponseProvinceOfCirculationEnum
    _$vehicleResponseProvinceOfCirculationEnum_MAPUTO_CITY =
    const VehicleResponseProvinceOfCirculationEnum._('MAPUTO_CITY');
const VehicleResponseProvinceOfCirculationEnum
    _$vehicleResponseProvinceOfCirculationEnum_MAPUTO =
    const VehicleResponseProvinceOfCirculationEnum._('MAPUTO');
const VehicleResponseProvinceOfCirculationEnum
    _$vehicleResponseProvinceOfCirculationEnum_GAZA =
    const VehicleResponseProvinceOfCirculationEnum._('GAZA');
const VehicleResponseProvinceOfCirculationEnum
    _$vehicleResponseProvinceOfCirculationEnum_INHAMBANE =
    const VehicleResponseProvinceOfCirculationEnum._('INHAMBANE');
const VehicleResponseProvinceOfCirculationEnum
    _$vehicleResponseProvinceOfCirculationEnum_SOFALA =
    const VehicleResponseProvinceOfCirculationEnum._('SOFALA');
const VehicleResponseProvinceOfCirculationEnum
    _$vehicleResponseProvinceOfCirculationEnum_MANICA =
    const VehicleResponseProvinceOfCirculationEnum._('MANICA');
const VehicleResponseProvinceOfCirculationEnum
    _$vehicleResponseProvinceOfCirculationEnum_TETE =
    const VehicleResponseProvinceOfCirculationEnum._('TETE');
const VehicleResponseProvinceOfCirculationEnum
    _$vehicleResponseProvinceOfCirculationEnum_ZAMBEZIA =
    const VehicleResponseProvinceOfCirculationEnum._('ZAMBEZIA');
const VehicleResponseProvinceOfCirculationEnum
    _$vehicleResponseProvinceOfCirculationEnum_NAMPULA =
    const VehicleResponseProvinceOfCirculationEnum._('NAMPULA');
const VehicleResponseProvinceOfCirculationEnum
    _$vehicleResponseProvinceOfCirculationEnum_CABO_DELGADO =
    const VehicleResponseProvinceOfCirculationEnum._('CABO_DELGADO');
const VehicleResponseProvinceOfCirculationEnum
    _$vehicleResponseProvinceOfCirculationEnum_NIASSA =
    const VehicleResponseProvinceOfCirculationEnum._('NIASSA');

VehicleResponseProvinceOfCirculationEnum
    _$vehicleResponseProvinceOfCirculationEnumValueOf(String name) {
  switch (name) {
    case 'MAPUTO_CITY':
      return _$vehicleResponseProvinceOfCirculationEnum_MAPUTO_CITY;
    case 'MAPUTO':
      return _$vehicleResponseProvinceOfCirculationEnum_MAPUTO;
    case 'GAZA':
      return _$vehicleResponseProvinceOfCirculationEnum_GAZA;
    case 'INHAMBANE':
      return _$vehicleResponseProvinceOfCirculationEnum_INHAMBANE;
    case 'SOFALA':
      return _$vehicleResponseProvinceOfCirculationEnum_SOFALA;
    case 'MANICA':
      return _$vehicleResponseProvinceOfCirculationEnum_MANICA;
    case 'TETE':
      return _$vehicleResponseProvinceOfCirculationEnum_TETE;
    case 'ZAMBEZIA':
      return _$vehicleResponseProvinceOfCirculationEnum_ZAMBEZIA;
    case 'NAMPULA':
      return _$vehicleResponseProvinceOfCirculationEnum_NAMPULA;
    case 'CABO_DELGADO':
      return _$vehicleResponseProvinceOfCirculationEnum_CABO_DELGADO;
    case 'NIASSA':
      return _$vehicleResponseProvinceOfCirculationEnum_NIASSA;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<VehicleResponseProvinceOfCirculationEnum>
    _$vehicleResponseProvinceOfCirculationEnumValues = BuiltSet<
        VehicleResponseProvinceOfCirculationEnum>(const <VehicleResponseProvinceOfCirculationEnum>[
  _$vehicleResponseProvinceOfCirculationEnum_MAPUTO_CITY,
  _$vehicleResponseProvinceOfCirculationEnum_MAPUTO,
  _$vehicleResponseProvinceOfCirculationEnum_GAZA,
  _$vehicleResponseProvinceOfCirculationEnum_INHAMBANE,
  _$vehicleResponseProvinceOfCirculationEnum_SOFALA,
  _$vehicleResponseProvinceOfCirculationEnum_MANICA,
  _$vehicleResponseProvinceOfCirculationEnum_TETE,
  _$vehicleResponseProvinceOfCirculationEnum_ZAMBEZIA,
  _$vehicleResponseProvinceOfCirculationEnum_NAMPULA,
  _$vehicleResponseProvinceOfCirculationEnum_CABO_DELGADO,
  _$vehicleResponseProvinceOfCirculationEnum_NIASSA,
]);

Serializer<VehicleResponseProvinceOfCirculationEnum>
    _$vehicleResponseProvinceOfCirculationEnumSerializer =
    _$VehicleResponseProvinceOfCirculationEnumSerializer();

class _$VehicleResponseProvinceOfCirculationEnumSerializer
    implements PrimitiveSerializer<VehicleResponseProvinceOfCirculationEnum> {
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
    VehicleResponseProvinceOfCirculationEnum
  ];
  @override
  final String wireName = 'VehicleResponseProvinceOfCirculationEnum';

  @override
  Object serialize(Serializers serializers,
          VehicleResponseProvinceOfCirculationEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  VehicleResponseProvinceOfCirculationEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      VehicleResponseProvinceOfCirculationEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$VehicleResponse extends VehicleResponse {
  @override
  final VehicleCategory category;
  @override
  final String chassisNumber;
  @override
  final Money? commercialValue;
  @override
  final DateTime createdAt;
  @override
  final BuiltList<VehicleDriverResponse> drivers;
  @override
  final int? engineCc;
  @override
  final int? grossWeightKg;
  @override
  final bool hasTracker;
  @override
  final String id;
  @override
  final bool inatroVerified;
  @override
  final String make;
  @override
  final String model;
  @override
  final String plate;
  @override
  final VehicleResponseProvinceOfCirculationEnum provinceOfCirculation;
  @override
  final RegistrationDocResponse? registrationDoc;
  @override
  final int seats;
  @override
  final DateTime updatedAt;
  @override
  final VehicleUsage usage;
  @override
  final int version;
  @override
  final int year;

  factory _$VehicleResponse([void Function(VehicleResponseBuilder)? updates]) =>
      (VehicleResponseBuilder()..update(updates))._build();

  _$VehicleResponse._(
      {required this.category,
      required this.chassisNumber,
      this.commercialValue,
      required this.createdAt,
      required this.drivers,
      this.engineCc,
      this.grossWeightKg,
      required this.hasTracker,
      required this.id,
      required this.inatroVerified,
      required this.make,
      required this.model,
      required this.plate,
      required this.provinceOfCirculation,
      this.registrationDoc,
      required this.seats,
      required this.updatedAt,
      required this.usage,
      required this.version,
      required this.year})
      : super._();
  @override
  VehicleResponse rebuild(void Function(VehicleResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  VehicleResponseBuilder toBuilder() => VehicleResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is VehicleResponse &&
        category == other.category &&
        chassisNumber == other.chassisNumber &&
        commercialValue == other.commercialValue &&
        createdAt == other.createdAt &&
        drivers == other.drivers &&
        engineCc == other.engineCc &&
        grossWeightKg == other.grossWeightKg &&
        hasTracker == other.hasTracker &&
        id == other.id &&
        inatroVerified == other.inatroVerified &&
        make == other.make &&
        model == other.model &&
        plate == other.plate &&
        provinceOfCirculation == other.provinceOfCirculation &&
        registrationDoc == other.registrationDoc &&
        seats == other.seats &&
        updatedAt == other.updatedAt &&
        usage == other.usage &&
        version == other.version &&
        year == other.year;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, category.hashCode);
    _$hash = $jc(_$hash, chassisNumber.hashCode);
    _$hash = $jc(_$hash, commercialValue.hashCode);
    _$hash = $jc(_$hash, createdAt.hashCode);
    _$hash = $jc(_$hash, drivers.hashCode);
    _$hash = $jc(_$hash, engineCc.hashCode);
    _$hash = $jc(_$hash, grossWeightKg.hashCode);
    _$hash = $jc(_$hash, hasTracker.hashCode);
    _$hash = $jc(_$hash, id.hashCode);
    _$hash = $jc(_$hash, inatroVerified.hashCode);
    _$hash = $jc(_$hash, make.hashCode);
    _$hash = $jc(_$hash, model.hashCode);
    _$hash = $jc(_$hash, plate.hashCode);
    _$hash = $jc(_$hash, provinceOfCirculation.hashCode);
    _$hash = $jc(_$hash, registrationDoc.hashCode);
    _$hash = $jc(_$hash, seats.hashCode);
    _$hash = $jc(_$hash, updatedAt.hashCode);
    _$hash = $jc(_$hash, usage.hashCode);
    _$hash = $jc(_$hash, version.hashCode);
    _$hash = $jc(_$hash, year.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'VehicleResponse')
          ..add('category', category)
          ..add('chassisNumber', chassisNumber)
          ..add('commercialValue', commercialValue)
          ..add('createdAt', createdAt)
          ..add('drivers', drivers)
          ..add('engineCc', engineCc)
          ..add('grossWeightKg', grossWeightKg)
          ..add('hasTracker', hasTracker)
          ..add('id', id)
          ..add('inatroVerified', inatroVerified)
          ..add('make', make)
          ..add('model', model)
          ..add('plate', plate)
          ..add('provinceOfCirculation', provinceOfCirculation)
          ..add('registrationDoc', registrationDoc)
          ..add('seats', seats)
          ..add('updatedAt', updatedAt)
          ..add('usage', usage)
          ..add('version', version)
          ..add('year', year))
        .toString();
  }
}

class VehicleResponseBuilder
    implements Builder<VehicleResponse, VehicleResponseBuilder> {
  _$VehicleResponse? _$v;

  VehicleCategory? _category;
  VehicleCategory? get category => _$this._category;
  set category(VehicleCategory? category) => _$this._category = category;

  String? _chassisNumber;
  String? get chassisNumber => _$this._chassisNumber;
  set chassisNumber(String? chassisNumber) =>
      _$this._chassisNumber = chassisNumber;

  MoneyBuilder? _commercialValue;
  MoneyBuilder get commercialValue =>
      _$this._commercialValue ??= MoneyBuilder();
  set commercialValue(MoneyBuilder? commercialValue) =>
      _$this._commercialValue = commercialValue;

  DateTime? _createdAt;
  DateTime? get createdAt => _$this._createdAt;
  set createdAt(DateTime? createdAt) => _$this._createdAt = createdAt;

  ListBuilder<VehicleDriverResponse>? _drivers;
  ListBuilder<VehicleDriverResponse> get drivers =>
      _$this._drivers ??= ListBuilder<VehicleDriverResponse>();
  set drivers(ListBuilder<VehicleDriverResponse>? drivers) =>
      _$this._drivers = drivers;

  int? _engineCc;
  int? get engineCc => _$this._engineCc;
  set engineCc(int? engineCc) => _$this._engineCc = engineCc;

  int? _grossWeightKg;
  int? get grossWeightKg => _$this._grossWeightKg;
  set grossWeightKg(int? grossWeightKg) =>
      _$this._grossWeightKg = grossWeightKg;

  bool? _hasTracker;
  bool? get hasTracker => _$this._hasTracker;
  set hasTracker(bool? hasTracker) => _$this._hasTracker = hasTracker;

  String? _id;
  String? get id => _$this._id;
  set id(String? id) => _$this._id = id;

  bool? _inatroVerified;
  bool? get inatroVerified => _$this._inatroVerified;
  set inatroVerified(bool? inatroVerified) =>
      _$this._inatroVerified = inatroVerified;

  String? _make;
  String? get make => _$this._make;
  set make(String? make) => _$this._make = make;

  String? _model;
  String? get model => _$this._model;
  set model(String? model) => _$this._model = model;

  String? _plate;
  String? get plate => _$this._plate;
  set plate(String? plate) => _$this._plate = plate;

  VehicleResponseProvinceOfCirculationEnum? _provinceOfCirculation;
  VehicleResponseProvinceOfCirculationEnum? get provinceOfCirculation =>
      _$this._provinceOfCirculation;
  set provinceOfCirculation(
          VehicleResponseProvinceOfCirculationEnum? provinceOfCirculation) =>
      _$this._provinceOfCirculation = provinceOfCirculation;

  RegistrationDocResponseBuilder? _registrationDoc;
  RegistrationDocResponseBuilder get registrationDoc =>
      _$this._registrationDoc ??= RegistrationDocResponseBuilder();
  set registrationDoc(RegistrationDocResponseBuilder? registrationDoc) =>
      _$this._registrationDoc = registrationDoc;

  int? _seats;
  int? get seats => _$this._seats;
  set seats(int? seats) => _$this._seats = seats;

  DateTime? _updatedAt;
  DateTime? get updatedAt => _$this._updatedAt;
  set updatedAt(DateTime? updatedAt) => _$this._updatedAt = updatedAt;

  VehicleUsage? _usage;
  VehicleUsage? get usage => _$this._usage;
  set usage(VehicleUsage? usage) => _$this._usage = usage;

  int? _version;
  int? get version => _$this._version;
  set version(int? version) => _$this._version = version;

  int? _year;
  int? get year => _$this._year;
  set year(int? year) => _$this._year = year;

  VehicleResponseBuilder() {
    VehicleResponse._defaults(this);
  }

  VehicleResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _category = $v.category;
      _chassisNumber = $v.chassisNumber;
      _commercialValue = $v.commercialValue?.toBuilder();
      _createdAt = $v.createdAt;
      _drivers = $v.drivers.toBuilder();
      _engineCc = $v.engineCc;
      _grossWeightKg = $v.grossWeightKg;
      _hasTracker = $v.hasTracker;
      _id = $v.id;
      _inatroVerified = $v.inatroVerified;
      _make = $v.make;
      _model = $v.model;
      _plate = $v.plate;
      _provinceOfCirculation = $v.provinceOfCirculation;
      _registrationDoc = $v.registrationDoc?.toBuilder();
      _seats = $v.seats;
      _updatedAt = $v.updatedAt;
      _usage = $v.usage;
      _version = $v.version;
      _year = $v.year;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(VehicleResponse other) {
    _$v = other as _$VehicleResponse;
  }

  @override
  void update(void Function(VehicleResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  VehicleResponse build() => _build();

  _$VehicleResponse _build() {
    _$VehicleResponse _$result;
    try {
      _$result = _$v ??
          _$VehicleResponse._(
            category: BuiltValueNullFieldError.checkNotNull(
                category, r'VehicleResponse', 'category'),
            chassisNumber: BuiltValueNullFieldError.checkNotNull(
                chassisNumber, r'VehicleResponse', 'chassisNumber'),
            commercialValue: _commercialValue?.build(),
            createdAt: BuiltValueNullFieldError.checkNotNull(
                createdAt, r'VehicleResponse', 'createdAt'),
            drivers: drivers.build(),
            engineCc: engineCc,
            grossWeightKg: grossWeightKg,
            hasTracker: BuiltValueNullFieldError.checkNotNull(
                hasTracker, r'VehicleResponse', 'hasTracker'),
            id: BuiltValueNullFieldError.checkNotNull(
                id, r'VehicleResponse', 'id'),
            inatroVerified: BuiltValueNullFieldError.checkNotNull(
                inatroVerified, r'VehicleResponse', 'inatroVerified'),
            make: BuiltValueNullFieldError.checkNotNull(
                make, r'VehicleResponse', 'make'),
            model: BuiltValueNullFieldError.checkNotNull(
                model, r'VehicleResponse', 'model'),
            plate: BuiltValueNullFieldError.checkNotNull(
                plate, r'VehicleResponse', 'plate'),
            provinceOfCirculation: BuiltValueNullFieldError.checkNotNull(
                provinceOfCirculation,
                r'VehicleResponse',
                'provinceOfCirculation'),
            registrationDoc: _registrationDoc?.build(),
            seats: BuiltValueNullFieldError.checkNotNull(
                seats, r'VehicleResponse', 'seats'),
            updatedAt: BuiltValueNullFieldError.checkNotNull(
                updatedAt, r'VehicleResponse', 'updatedAt'),
            usage: BuiltValueNullFieldError.checkNotNull(
                usage, r'VehicleResponse', 'usage'),
            version: BuiltValueNullFieldError.checkNotNull(
                version, r'VehicleResponse', 'version'),
            year: BuiltValueNullFieldError.checkNotNull(
                year, r'VehicleResponse', 'year'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'commercialValue';
        _commercialValue?.build();

        _$failedField = 'drivers';
        drivers.build();

        _$failedField = 'registrationDoc';
        _registrationDoc?.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'VehicleResponse', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
