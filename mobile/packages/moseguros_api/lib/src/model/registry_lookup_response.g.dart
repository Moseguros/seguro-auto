// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'registry_lookup_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$RegistryLookupResponse extends RegistryLookupResponse {
  @override
  final VehicleCategory category;
  @override
  final String chassisNumber;
  @override
  final int? engineCc;
  @override
  final int? grossWeightKg;
  @override
  final String make;
  @override
  final String model;
  @override
  final String plate;
  @override
  final int seats;
  @override
  final String source_;
  @override
  final int year;

  factory _$RegistryLookupResponse(
          [void Function(RegistryLookupResponseBuilder)? updates]) =>
      (RegistryLookupResponseBuilder()..update(updates))._build();

  _$RegistryLookupResponse._(
      {required this.category,
      required this.chassisNumber,
      this.engineCc,
      this.grossWeightKg,
      required this.make,
      required this.model,
      required this.plate,
      required this.seats,
      required this.source_,
      required this.year})
      : super._();
  @override
  RegistryLookupResponse rebuild(
          void Function(RegistryLookupResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  RegistryLookupResponseBuilder toBuilder() =>
      RegistryLookupResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is RegistryLookupResponse &&
        category == other.category &&
        chassisNumber == other.chassisNumber &&
        engineCc == other.engineCc &&
        grossWeightKg == other.grossWeightKg &&
        make == other.make &&
        model == other.model &&
        plate == other.plate &&
        seats == other.seats &&
        source_ == other.source_ &&
        year == other.year;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, category.hashCode);
    _$hash = $jc(_$hash, chassisNumber.hashCode);
    _$hash = $jc(_$hash, engineCc.hashCode);
    _$hash = $jc(_$hash, grossWeightKg.hashCode);
    _$hash = $jc(_$hash, make.hashCode);
    _$hash = $jc(_$hash, model.hashCode);
    _$hash = $jc(_$hash, plate.hashCode);
    _$hash = $jc(_$hash, seats.hashCode);
    _$hash = $jc(_$hash, source_.hashCode);
    _$hash = $jc(_$hash, year.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'RegistryLookupResponse')
          ..add('category', category)
          ..add('chassisNumber', chassisNumber)
          ..add('engineCc', engineCc)
          ..add('grossWeightKg', grossWeightKg)
          ..add('make', make)
          ..add('model', model)
          ..add('plate', plate)
          ..add('seats', seats)
          ..add('source_', source_)
          ..add('year', year))
        .toString();
  }
}

class RegistryLookupResponseBuilder
    implements Builder<RegistryLookupResponse, RegistryLookupResponseBuilder> {
  _$RegistryLookupResponse? _$v;

  VehicleCategory? _category;
  VehicleCategory? get category => _$this._category;
  set category(VehicleCategory? category) => _$this._category = category;

  String? _chassisNumber;
  String? get chassisNumber => _$this._chassisNumber;
  set chassisNumber(String? chassisNumber) =>
      _$this._chassisNumber = chassisNumber;

  int? _engineCc;
  int? get engineCc => _$this._engineCc;
  set engineCc(int? engineCc) => _$this._engineCc = engineCc;

  int? _grossWeightKg;
  int? get grossWeightKg => _$this._grossWeightKg;
  set grossWeightKg(int? grossWeightKg) =>
      _$this._grossWeightKg = grossWeightKg;

  String? _make;
  String? get make => _$this._make;
  set make(String? make) => _$this._make = make;

  String? _model;
  String? get model => _$this._model;
  set model(String? model) => _$this._model = model;

  String? _plate;
  String? get plate => _$this._plate;
  set plate(String? plate) => _$this._plate = plate;

  int? _seats;
  int? get seats => _$this._seats;
  set seats(int? seats) => _$this._seats = seats;

  String? _source_;
  String? get source_ => _$this._source_;
  set source_(String? source_) => _$this._source_ = source_;

  int? _year;
  int? get year => _$this._year;
  set year(int? year) => _$this._year = year;

  RegistryLookupResponseBuilder() {
    RegistryLookupResponse._defaults(this);
  }

  RegistryLookupResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _category = $v.category;
      _chassisNumber = $v.chassisNumber;
      _engineCc = $v.engineCc;
      _grossWeightKg = $v.grossWeightKg;
      _make = $v.make;
      _model = $v.model;
      _plate = $v.plate;
      _seats = $v.seats;
      _source_ = $v.source_;
      _year = $v.year;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(RegistryLookupResponse other) {
    _$v = other as _$RegistryLookupResponse;
  }

  @override
  void update(void Function(RegistryLookupResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  RegistryLookupResponse build() => _build();

  _$RegistryLookupResponse _build() {
    final _$result = _$v ??
        _$RegistryLookupResponse._(
          category: BuiltValueNullFieldError.checkNotNull(
              category, r'RegistryLookupResponse', 'category'),
          chassisNumber: BuiltValueNullFieldError.checkNotNull(
              chassisNumber, r'RegistryLookupResponse', 'chassisNumber'),
          engineCc: engineCc,
          grossWeightKg: grossWeightKg,
          make: BuiltValueNullFieldError.checkNotNull(
              make, r'RegistryLookupResponse', 'make'),
          model: BuiltValueNullFieldError.checkNotNull(
              model, r'RegistryLookupResponse', 'model'),
          plate: BuiltValueNullFieldError.checkNotNull(
              plate, r'RegistryLookupResponse', 'plate'),
          seats: BuiltValueNullFieldError.checkNotNull(
              seats, r'RegistryLookupResponse', 'seats'),
          source_: BuiltValueNullFieldError.checkNotNull(
              source_, r'RegistryLookupResponse', 'source_'),
          year: BuiltValueNullFieldError.checkNotNull(
              year, r'RegistryLookupResponse', 'year'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
