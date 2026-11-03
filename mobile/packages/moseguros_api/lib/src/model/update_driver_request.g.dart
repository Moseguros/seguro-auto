// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'update_driver_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$UpdateDriverRequest extends UpdateDriverRequest {
  @override
  final Date birthDate;
  @override
  final String fullName;
  @override
  final bool? isOwner;
  @override
  final BuiltList<String> licenceCategories;
  @override
  final Date licenceIssueDate;
  @override
  final String? licenceNumber;
  @override
  final int version;

  factory _$UpdateDriverRequest(
          [void Function(UpdateDriverRequestBuilder)? updates]) =>
      (UpdateDriverRequestBuilder()..update(updates))._build();

  _$UpdateDriverRequest._(
      {required this.birthDate,
      required this.fullName,
      this.isOwner,
      required this.licenceCategories,
      required this.licenceIssueDate,
      this.licenceNumber,
      required this.version})
      : super._();
  @override
  UpdateDriverRequest rebuild(
          void Function(UpdateDriverRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  UpdateDriverRequestBuilder toBuilder() =>
      UpdateDriverRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is UpdateDriverRequest &&
        birthDate == other.birthDate &&
        fullName == other.fullName &&
        isOwner == other.isOwner &&
        licenceCategories == other.licenceCategories &&
        licenceIssueDate == other.licenceIssueDate &&
        licenceNumber == other.licenceNumber &&
        version == other.version;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, birthDate.hashCode);
    _$hash = $jc(_$hash, fullName.hashCode);
    _$hash = $jc(_$hash, isOwner.hashCode);
    _$hash = $jc(_$hash, licenceCategories.hashCode);
    _$hash = $jc(_$hash, licenceIssueDate.hashCode);
    _$hash = $jc(_$hash, licenceNumber.hashCode);
    _$hash = $jc(_$hash, version.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'UpdateDriverRequest')
          ..add('birthDate', birthDate)
          ..add('fullName', fullName)
          ..add('isOwner', isOwner)
          ..add('licenceCategories', licenceCategories)
          ..add('licenceIssueDate', licenceIssueDate)
          ..add('licenceNumber', licenceNumber)
          ..add('version', version))
        .toString();
  }
}

class UpdateDriverRequestBuilder
    implements Builder<UpdateDriverRequest, UpdateDriverRequestBuilder> {
  _$UpdateDriverRequest? _$v;

  Date? _birthDate;
  Date? get birthDate => _$this._birthDate;
  set birthDate(Date? birthDate) => _$this._birthDate = birthDate;

  String? _fullName;
  String? get fullName => _$this._fullName;
  set fullName(String? fullName) => _$this._fullName = fullName;

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

  String? _licenceNumber;
  String? get licenceNumber => _$this._licenceNumber;
  set licenceNumber(String? licenceNumber) =>
      _$this._licenceNumber = licenceNumber;

  int? _version;
  int? get version => _$this._version;
  set version(int? version) => _$this._version = version;

  UpdateDriverRequestBuilder() {
    UpdateDriverRequest._defaults(this);
  }

  UpdateDriverRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _birthDate = $v.birthDate;
      _fullName = $v.fullName;
      _isOwner = $v.isOwner;
      _licenceCategories = $v.licenceCategories.toBuilder();
      _licenceIssueDate = $v.licenceIssueDate;
      _licenceNumber = $v.licenceNumber;
      _version = $v.version;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(UpdateDriverRequest other) {
    _$v = other as _$UpdateDriverRequest;
  }

  @override
  void update(void Function(UpdateDriverRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  UpdateDriverRequest build() => _build();

  _$UpdateDriverRequest _build() {
    _$UpdateDriverRequest _$result;
    try {
      _$result = _$v ??
          _$UpdateDriverRequest._(
            birthDate: BuiltValueNullFieldError.checkNotNull(
                birthDate, r'UpdateDriverRequest', 'birthDate'),
            fullName: BuiltValueNullFieldError.checkNotNull(
                fullName, r'UpdateDriverRequest', 'fullName'),
            isOwner: isOwner,
            licenceCategories: licenceCategories.build(),
            licenceIssueDate: BuiltValueNullFieldError.checkNotNull(
                licenceIssueDate, r'UpdateDriverRequest', 'licenceIssueDate'),
            licenceNumber: licenceNumber,
            version: BuiltValueNullFieldError.checkNotNull(
                version, r'UpdateDriverRequest', 'version'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'licenceCategories';
        licenceCategories.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'UpdateDriverRequest', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
