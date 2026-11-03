// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'vehicle_driver_assignment.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$VehicleDriverAssignment extends VehicleDriverAssignment {
  @override
  final String driverId;
  @override
  final bool habitual;

  factory _$VehicleDriverAssignment(
          [void Function(VehicleDriverAssignmentBuilder)? updates]) =>
      (VehicleDriverAssignmentBuilder()..update(updates))._build();

  _$VehicleDriverAssignment._({required this.driverId, required this.habitual})
      : super._();
  @override
  VehicleDriverAssignment rebuild(
          void Function(VehicleDriverAssignmentBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  VehicleDriverAssignmentBuilder toBuilder() =>
      VehicleDriverAssignmentBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is VehicleDriverAssignment &&
        driverId == other.driverId &&
        habitual == other.habitual;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, driverId.hashCode);
    _$hash = $jc(_$hash, habitual.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'VehicleDriverAssignment')
          ..add('driverId', driverId)
          ..add('habitual', habitual))
        .toString();
  }
}

class VehicleDriverAssignmentBuilder
    implements
        Builder<VehicleDriverAssignment, VehicleDriverAssignmentBuilder> {
  _$VehicleDriverAssignment? _$v;

  String? _driverId;
  String? get driverId => _$this._driverId;
  set driverId(String? driverId) => _$this._driverId = driverId;

  bool? _habitual;
  bool? get habitual => _$this._habitual;
  set habitual(bool? habitual) => _$this._habitual = habitual;

  VehicleDriverAssignmentBuilder() {
    VehicleDriverAssignment._defaults(this);
  }

  VehicleDriverAssignmentBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _driverId = $v.driverId;
      _habitual = $v.habitual;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(VehicleDriverAssignment other) {
    _$v = other as _$VehicleDriverAssignment;
  }

  @override
  void update(void Function(VehicleDriverAssignmentBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  VehicleDriverAssignment build() => _build();

  _$VehicleDriverAssignment _build() {
    final _$result = _$v ??
        _$VehicleDriverAssignment._(
          driverId: BuiltValueNullFieldError.checkNotNull(
              driverId, r'VehicleDriverAssignment', 'driverId'),
          habitual: BuiltValueNullFieldError.checkNotNull(
              habitual, r'VehicleDriverAssignment', 'habitual'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
