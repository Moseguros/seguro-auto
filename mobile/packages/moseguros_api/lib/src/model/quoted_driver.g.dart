// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'quoted_driver.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$QuotedDriver extends QuotedDriver {
  @override
  final Date? birthDate;
  @override
  final String? driverId;
  @override
  final BuiltList<String> licenceCategories;
  @override
  final Date? licenceIssueDate;

  factory _$QuotedDriver([void Function(QuotedDriverBuilder)? updates]) =>
      (QuotedDriverBuilder()..update(updates))._build();

  _$QuotedDriver._(
      {this.birthDate,
      this.driverId,
      required this.licenceCategories,
      this.licenceIssueDate})
      : super._();
  @override
  QuotedDriver rebuild(void Function(QuotedDriverBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  QuotedDriverBuilder toBuilder() => QuotedDriverBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is QuotedDriver &&
        birthDate == other.birthDate &&
        driverId == other.driverId &&
        licenceCategories == other.licenceCategories &&
        licenceIssueDate == other.licenceIssueDate;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, birthDate.hashCode);
    _$hash = $jc(_$hash, driverId.hashCode);
    _$hash = $jc(_$hash, licenceCategories.hashCode);
    _$hash = $jc(_$hash, licenceIssueDate.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'QuotedDriver')
          ..add('birthDate', birthDate)
          ..add('driverId', driverId)
          ..add('licenceCategories', licenceCategories)
          ..add('licenceIssueDate', licenceIssueDate))
        .toString();
  }
}

class QuotedDriverBuilder
    implements Builder<QuotedDriver, QuotedDriverBuilder> {
  _$QuotedDriver? _$v;

  Date? _birthDate;
  Date? get birthDate => _$this._birthDate;
  set birthDate(Date? birthDate) => _$this._birthDate = birthDate;

  String? _driverId;
  String? get driverId => _$this._driverId;
  set driverId(String? driverId) => _$this._driverId = driverId;

  ListBuilder<String>? _licenceCategories;
  ListBuilder<String> get licenceCategories =>
      _$this._licenceCategories ??= ListBuilder<String>();
  set licenceCategories(ListBuilder<String>? licenceCategories) =>
      _$this._licenceCategories = licenceCategories;

  Date? _licenceIssueDate;
  Date? get licenceIssueDate => _$this._licenceIssueDate;
  set licenceIssueDate(Date? licenceIssueDate) =>
      _$this._licenceIssueDate = licenceIssueDate;

  QuotedDriverBuilder() {
    QuotedDriver._defaults(this);
  }

  QuotedDriverBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _birthDate = $v.birthDate;
      _driverId = $v.driverId;
      _licenceCategories = $v.licenceCategories.toBuilder();
      _licenceIssueDate = $v.licenceIssueDate;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(QuotedDriver other) {
    _$v = other as _$QuotedDriver;
  }

  @override
  void update(void Function(QuotedDriverBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  QuotedDriver build() => _build();

  _$QuotedDriver _build() {
    _$QuotedDriver _$result;
    try {
      _$result = _$v ??
          _$QuotedDriver._(
            birthDate: birthDate,
            driverId: driverId,
            licenceCategories: licenceCategories.build(),
            licenceIssueDate: licenceIssueDate,
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'licenceCategories';
        licenceCategories.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'QuotedDriver', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
