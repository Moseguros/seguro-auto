// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'driver_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$DriverResponse extends DriverResponse {
  @override
  final Date birthDate;
  @override
  final DateTime createdAt;
  @override
  final String fullName;
  @override
  final String id;
  @override
  final bool isOwner;
  @override
  final BuiltList<String> licenceCategories;
  @override
  final Date licenceIssueDate;
  @override
  final String licenceNumberMasked;
  @override
  final DateTime updatedAt;
  @override
  final BuiltList<String> vehicleIds;
  @override
  final int version;

  factory _$DriverResponse([void Function(DriverResponseBuilder)? updates]) =>
      (DriverResponseBuilder()..update(updates))._build();

  _$DriverResponse._(
      {required this.birthDate,
      required this.createdAt,
      required this.fullName,
      required this.id,
      required this.isOwner,
      required this.licenceCategories,
      required this.licenceIssueDate,
      required this.licenceNumberMasked,
      required this.updatedAt,
      required this.vehicleIds,
      required this.version})
      : super._();
  @override
  DriverResponse rebuild(void Function(DriverResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  DriverResponseBuilder toBuilder() => DriverResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is DriverResponse &&
        birthDate == other.birthDate &&
        createdAt == other.createdAt &&
        fullName == other.fullName &&
        id == other.id &&
        isOwner == other.isOwner &&
        licenceCategories == other.licenceCategories &&
        licenceIssueDate == other.licenceIssueDate &&
        licenceNumberMasked == other.licenceNumberMasked &&
        updatedAt == other.updatedAt &&
        vehicleIds == other.vehicleIds &&
        version == other.version;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, birthDate.hashCode);
    _$hash = $jc(_$hash, createdAt.hashCode);
    _$hash = $jc(_$hash, fullName.hashCode);
    _$hash = $jc(_$hash, id.hashCode);
    _$hash = $jc(_$hash, isOwner.hashCode);
    _$hash = $jc(_$hash, licenceCategories.hashCode);
    _$hash = $jc(_$hash, licenceIssueDate.hashCode);
    _$hash = $jc(_$hash, licenceNumberMasked.hashCode);
    _$hash = $jc(_$hash, updatedAt.hashCode);
    _$hash = $jc(_$hash, vehicleIds.hashCode);
    _$hash = $jc(_$hash, version.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'DriverResponse')
          ..add('birthDate', birthDate)
          ..add('createdAt', createdAt)
          ..add('fullName', fullName)
          ..add('id', id)
          ..add('isOwner', isOwner)
          ..add('licenceCategories', licenceCategories)
          ..add('licenceIssueDate', licenceIssueDate)
          ..add('licenceNumberMasked', licenceNumberMasked)
          ..add('updatedAt', updatedAt)
          ..add('vehicleIds', vehicleIds)
          ..add('version', version))
        .toString();
  }
}

class DriverResponseBuilder
    implements Builder<DriverResponse, DriverResponseBuilder> {
  _$DriverResponse? _$v;

  Date? _birthDate;
  Date? get birthDate => _$this._birthDate;
  set birthDate(Date? birthDate) => _$this._birthDate = birthDate;

  DateTime? _createdAt;
  DateTime? get createdAt => _$this._createdAt;
  set createdAt(DateTime? createdAt) => _$this._createdAt = createdAt;

  String? _fullName;
  String? get fullName => _$this._fullName;
  set fullName(String? fullName) => _$this._fullName = fullName;

  String? _id;
  String? get id => _$this._id;
  set id(String? id) => _$this._id = id;

  bool? _isOwner;
  bool? get isOwner => _$this._isOwner;
  set isOwner(bool? isOwner) => _$this._isOwner = isOwner;

  ListBuilder<String>? _licenceCategories;
  ListBuilder<String> get licenceCategories =>
      _$this._licenceCategories ??= ListBuilder<String>();
  set licenceCategories(ListBuilder<String>? licenceCategories) =>
      _$this._licenceCategories = licenceCategories;

  Date? _licenceIssueDate;
  Date? get licenceIssueDate => _$this._licenceIssueDate;
  set licenceIssueDate(Date? licenceIssueDate) =>
      _$this._licenceIssueDate = licenceIssueDate;

  String? _licenceNumberMasked;
  String? get licenceNumberMasked => _$this._licenceNumberMasked;
  set licenceNumberMasked(String? licenceNumberMasked) =>
      _$this._licenceNumberMasked = licenceNumberMasked;

  DateTime? _updatedAt;
  DateTime? get updatedAt => _$this._updatedAt;
  set updatedAt(DateTime? updatedAt) => _$this._updatedAt = updatedAt;

  ListBuilder<String>? _vehicleIds;
  ListBuilder<String> get vehicleIds =>
      _$this._vehicleIds ??= ListBuilder<String>();
  set vehicleIds(ListBuilder<String>? vehicleIds) =>
      _$this._vehicleIds = vehicleIds;

  int? _version;
  int? get version => _$this._version;
  set version(int? version) => _$this._version = version;

  DriverResponseBuilder() {
    DriverResponse._defaults(this);
  }

  DriverResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _birthDate = $v.birthDate;
      _createdAt = $v.createdAt;
      _fullName = $v.fullName;
      _id = $v.id;
      _isOwner = $v.isOwner;
      _licenceCategories = $v.licenceCategories.toBuilder();
      _licenceIssueDate = $v.licenceIssueDate;
      _licenceNumberMasked = $v.licenceNumberMasked;
      _updatedAt = $v.updatedAt;
      _vehicleIds = $v.vehicleIds.toBuilder();
      _version = $v.version;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(DriverResponse other) {
    _$v = other as _$DriverResponse;
  }

  @override
  void update(void Function(DriverResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  DriverResponse build() => _build();

  _$DriverResponse _build() {
    _$DriverResponse _$result;
    try {
      _$result = _$v ??
          _$DriverResponse._(
            birthDate: BuiltValueNullFieldError.checkNotNull(
                birthDate, r'DriverResponse', 'birthDate'),
            createdAt: BuiltValueNullFieldError.checkNotNull(
                createdAt, r'DriverResponse', 'createdAt'),
            fullName: BuiltValueNullFieldError.checkNotNull(
                fullName, r'DriverResponse', 'fullName'),
            id: BuiltValueNullFieldError.checkNotNull(
                id, r'DriverResponse', 'id'),
            isOwner: BuiltValueNullFieldError.checkNotNull(
                isOwner, r'DriverResponse', 'isOwner'),
            licenceCategories: licenceCategories.build(),
            licenceIssueDate: BuiltValueNullFieldError.checkNotNull(
                licenceIssueDate, r'DriverResponse', 'licenceIssueDate'),
            licenceNumberMasked: BuiltValueNullFieldError.checkNotNull(
                licenceNumberMasked, r'DriverResponse', 'licenceNumberMasked'),
            updatedAt: BuiltValueNullFieldError.checkNotNull(
                updatedAt, r'DriverResponse', 'updatedAt'),
            vehicleIds: vehicleIds.build(),
            version: BuiltValueNullFieldError.checkNotNull(
                version, r'DriverResponse', 'version'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'licenceCategories';
        licenceCategories.build();

        _$failedField = 'vehicleIds';
        vehicleIds.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'DriverResponse', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
