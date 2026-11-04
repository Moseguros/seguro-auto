// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'risk_driver_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$RiskDriverRequest extends RiskDriverRequest {
  @override
  final Date birthDate;
  @override
  final BuiltList<String>? licenceCategories;
  @override
  final Date? licenceIssueDate;

  factory _$RiskDriverRequest(
          [void Function(RiskDriverRequestBuilder)? updates]) =>
      (RiskDriverRequestBuilder()..update(updates))._build();

  _$RiskDriverRequest._(
      {required this.birthDate, this.licenceCategories, this.licenceIssueDate})
      : super._();
  @override
  RiskDriverRequest rebuild(void Function(RiskDriverRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  RiskDriverRequestBuilder toBuilder() =>
      RiskDriverRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is RiskDriverRequest &&
        birthDate == other.birthDate &&
        licenceCategories == other.licenceCategories &&
        licenceIssueDate == other.licenceIssueDate;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, birthDate.hashCode);
    _$hash = $jc(_$hash, licenceCategories.hashCode);
    _$hash = $jc(_$hash, licenceIssueDate.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'RiskDriverRequest')
          ..add('birthDate', birthDate)
          ..add('licenceCategories', licenceCategories)
          ..add('licenceIssueDate', licenceIssueDate))
        .toString();
  }
}

class RiskDriverRequestBuilder
    implements Builder<RiskDriverRequest, RiskDriverRequestBuilder> {
  _$RiskDriverRequest? _$v;

  Date? _birthDate;
  Date? get birthDate => _$this._birthDate;
  set birthDate(Date? birthDate) => _$this._birthDate = birthDate;

  ListBuilder<String>? _licenceCategories;
  ListBuilder<String> get licenceCategories =>
      _$this._licenceCategories ??= ListBuilder<String>();
  set licenceCategories(ListBuilder<String>? licenceCategories) =>
      _$this._licenceCategories = licenceCategories;

  Date? _licenceIssueDate;
  Date? get licenceIssueDate => _$this._licenceIssueDate;
  set licenceIssueDate(Date? licenceIssueDate) =>
      _$this._licenceIssueDate = licenceIssueDate;

  RiskDriverRequestBuilder() {
    RiskDriverRequest._defaults(this);
  }

  RiskDriverRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _birthDate = $v.birthDate;
      _licenceCategories = $v.licenceCategories?.toBuilder();
      _licenceIssueDate = $v.licenceIssueDate;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(RiskDriverRequest other) {
    _$v = other as _$RiskDriverRequest;
  }

  @override
  void update(void Function(RiskDriverRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  RiskDriverRequest build() => _build();

  _$RiskDriverRequest _build() {
    _$RiskDriverRequest _$result;
    try {
      _$result = _$v ??
          _$RiskDriverRequest._(
            birthDate: BuiltValueNullFieldError.checkNotNull(
                birthDate, r'RiskDriverRequest', 'birthDate'),
            licenceCategories: _licenceCategories?.build(),
            licenceIssueDate: licenceIssueDate,
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'licenceCategories';
        _licenceCategories?.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'RiskDriverRequest', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
