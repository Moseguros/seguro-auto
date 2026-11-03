// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'quote_summary_vehicle.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$QuoteSummaryVehicle extends QuoteSummaryVehicle {
  @override
  final String? make;
  @override
  final String? model;
  @override
  final String? plate;

  factory _$QuoteSummaryVehicle(
          [void Function(QuoteSummaryVehicleBuilder)? updates]) =>
      (QuoteSummaryVehicleBuilder()..update(updates))._build();

  _$QuoteSummaryVehicle._({this.make, this.model, this.plate}) : super._();
  @override
  QuoteSummaryVehicle rebuild(
          void Function(QuoteSummaryVehicleBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  QuoteSummaryVehicleBuilder toBuilder() =>
      QuoteSummaryVehicleBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is QuoteSummaryVehicle &&
        make == other.make &&
        model == other.model &&
        plate == other.plate;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, make.hashCode);
    _$hash = $jc(_$hash, model.hashCode);
    _$hash = $jc(_$hash, plate.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'QuoteSummaryVehicle')
          ..add('make', make)
          ..add('model', model)
          ..add('plate', plate))
        .toString();
  }
}

class QuoteSummaryVehicleBuilder
    implements Builder<QuoteSummaryVehicle, QuoteSummaryVehicleBuilder> {
  _$QuoteSummaryVehicle? _$v;

  String? _make;
  String? get make => _$this._make;
  set make(String? make) => _$this._make = make;

  String? _model;
  String? get model => _$this._model;
  set model(String? model) => _$this._model = model;

  String? _plate;
  String? get plate => _$this._plate;
  set plate(String? plate) => _$this._plate = plate;

  QuoteSummaryVehicleBuilder() {
    QuoteSummaryVehicle._defaults(this);
  }

  QuoteSummaryVehicleBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _make = $v.make;
      _model = $v.model;
      _plate = $v.plate;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(QuoteSummaryVehicle other) {
    _$v = other as _$QuoteSummaryVehicle;
  }

  @override
  void update(void Function(QuoteSummaryVehicleBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  QuoteSummaryVehicle build() => _build();

  _$QuoteSummaryVehicle _build() {
    final _$result = _$v ??
        _$QuoteSummaryVehicle._(
          make: make,
          model: model,
          plate: plate,
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
