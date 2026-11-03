// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'simulate_rating_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$SimulateRatingRequest extends SimulateRatingRequest {
  @override
  final BuiltList<Charge>? charges;
  @override
  final BuiltList<RequestedCoverageRequest>? coverages;
  @override
  final RiskDriverRequest driver;
  @override
  final int durationDays;
  @override
  final JsonObject? rating;
  @override
  final Date startDate;
  @override
  final SimulationVehicleRequest vehicle;

  factory _$SimulateRatingRequest(
          [void Function(SimulateRatingRequestBuilder)? updates]) =>
      (SimulateRatingRequestBuilder()..update(updates))._build();

  _$SimulateRatingRequest._(
      {this.charges,
      this.coverages,
      required this.driver,
      required this.durationDays,
      this.rating,
      required this.startDate,
      required this.vehicle})
      : super._();
  @override
  SimulateRatingRequest rebuild(
          void Function(SimulateRatingRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  SimulateRatingRequestBuilder toBuilder() =>
      SimulateRatingRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is SimulateRatingRequest &&
        charges == other.charges &&
        coverages == other.coverages &&
        driver == other.driver &&
        durationDays == other.durationDays &&
        rating == other.rating &&
        startDate == other.startDate &&
        vehicle == other.vehicle;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, charges.hashCode);
    _$hash = $jc(_$hash, coverages.hashCode);
    _$hash = $jc(_$hash, driver.hashCode);
    _$hash = $jc(_$hash, durationDays.hashCode);
    _$hash = $jc(_$hash, rating.hashCode);
    _$hash = $jc(_$hash, startDate.hashCode);
    _$hash = $jc(_$hash, vehicle.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'SimulateRatingRequest')
          ..add('charges', charges)
          ..add('coverages', coverages)
          ..add('driver', driver)
          ..add('durationDays', durationDays)
          ..add('rating', rating)
          ..add('startDate', startDate)
          ..add('vehicle', vehicle))
        .toString();
  }
}

class SimulateRatingRequestBuilder
    implements Builder<SimulateRatingRequest, SimulateRatingRequestBuilder> {
  _$SimulateRatingRequest? _$v;

  ListBuilder<Charge>? _charges;
  ListBuilder<Charge> get charges => _$this._charges ??= ListBuilder<Charge>();
  set charges(ListBuilder<Charge>? charges) => _$this._charges = charges;

  ListBuilder<RequestedCoverageRequest>? _coverages;
  ListBuilder<RequestedCoverageRequest> get coverages =>
      _$this._coverages ??= ListBuilder<RequestedCoverageRequest>();
  set coverages(ListBuilder<RequestedCoverageRequest>? coverages) =>
      _$this._coverages = coverages;

  RiskDriverRequestBuilder? _driver;
  RiskDriverRequestBuilder get driver =>
      _$this._driver ??= RiskDriverRequestBuilder();
  set driver(RiskDriverRequestBuilder? driver) => _$this._driver = driver;

  int? _durationDays;
  int? get durationDays => _$this._durationDays;
  set durationDays(int? durationDays) => _$this._durationDays = durationDays;

  JsonObject? _rating;
  JsonObject? get rating => _$this._rating;
  set rating(JsonObject? rating) => _$this._rating = rating;

  Date? _startDate;
  Date? get startDate => _$this._startDate;
  set startDate(Date? startDate) => _$this._startDate = startDate;

  SimulationVehicleRequestBuilder? _vehicle;
  SimulationVehicleRequestBuilder get vehicle =>
      _$this._vehicle ??= SimulationVehicleRequestBuilder();
  set vehicle(SimulationVehicleRequestBuilder? vehicle) =>
      _$this._vehicle = vehicle;

  SimulateRatingRequestBuilder() {
    SimulateRatingRequest._defaults(this);
  }

  SimulateRatingRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _charges = $v.charges?.toBuilder();
      _coverages = $v.coverages?.toBuilder();
      _driver = $v.driver.toBuilder();
      _durationDays = $v.durationDays;
      _rating = $v.rating;
      _startDate = $v.startDate;
      _vehicle = $v.vehicle.toBuilder();
      _$v = null;
    }
    return this;
  }

  @override
  void replace(SimulateRatingRequest other) {
    _$v = other as _$SimulateRatingRequest;
  }

  @override
  void update(void Function(SimulateRatingRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  SimulateRatingRequest build() => _build();

  _$SimulateRatingRequest _build() {
    _$SimulateRatingRequest _$result;
    try {
      _$result = _$v ??
          _$SimulateRatingRequest._(
            charges: _charges?.build(),
            coverages: _coverages?.build(),
            driver: driver.build(),
            durationDays: BuiltValueNullFieldError.checkNotNull(
                durationDays, r'SimulateRatingRequest', 'durationDays'),
            rating: rating,
            startDate: BuiltValueNullFieldError.checkNotNull(
                startDate, r'SimulateRatingRequest', 'startDate'),
            vehicle: vehicle.build(),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'charges';
        _charges?.build();
        _$failedField = 'coverages';
        _coverages?.build();
        _$failedField = 'driver';
        driver.build();

        _$failedField = 'vehicle';
        vehicle.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'SimulateRatingRequest', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
