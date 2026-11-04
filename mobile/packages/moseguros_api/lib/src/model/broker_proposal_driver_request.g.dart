// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'broker_proposal_driver_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$BrokerProposalDriverRequest extends BrokerProposalDriverRequest {
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
  final String licenceNumber;

  factory _$BrokerProposalDriverRequest(
          [void Function(BrokerProposalDriverRequestBuilder)? updates]) =>
      (BrokerProposalDriverRequestBuilder()..update(updates))._build();

  _$BrokerProposalDriverRequest._(
      {required this.birthDate,
      required this.fullName,
      this.isOwner,
      required this.licenceCategories,
      required this.licenceIssueDate,
      required this.licenceNumber})
      : super._();
  @override
  BrokerProposalDriverRequest rebuild(
          void Function(BrokerProposalDriverRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  BrokerProposalDriverRequestBuilder toBuilder() =>
      BrokerProposalDriverRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is BrokerProposalDriverRequest &&
        birthDate == other.birthDate &&
        fullName == other.fullName &&
        isOwner == other.isOwner &&
        licenceCategories == other.licenceCategories &&
        licenceIssueDate == other.licenceIssueDate &&
        licenceNumber == other.licenceNumber;
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
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'BrokerProposalDriverRequest')
          ..add('birthDate', birthDate)
          ..add('fullName', fullName)
          ..add('isOwner', isOwner)
          ..add('licenceCategories', licenceCategories)
          ..add('licenceIssueDate', licenceIssueDate)
          ..add('licenceNumber', licenceNumber))
        .toString();
  }
}

class BrokerProposalDriverRequestBuilder
    implements
        Builder<BrokerProposalDriverRequest,
            BrokerProposalDriverRequestBuilder> {
  _$BrokerProposalDriverRequest? _$v;

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

  BrokerProposalDriverRequestBuilder() {
    BrokerProposalDriverRequest._defaults(this);
  }

  BrokerProposalDriverRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _birthDate = $v.birthDate;
      _fullName = $v.fullName;
      _isOwner = $v.isOwner;
      _licenceCategories = $v.licenceCategories.toBuilder();
      _licenceIssueDate = $v.licenceIssueDate;
      _licenceNumber = $v.licenceNumber;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(BrokerProposalDriverRequest other) {
    _$v = other as _$BrokerProposalDriverRequest;
  }

  @override
  void update(void Function(BrokerProposalDriverRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  BrokerProposalDriverRequest build() => _build();

  _$BrokerProposalDriverRequest _build() {
    _$BrokerProposalDriverRequest _$result;
    try {
      _$result = _$v ??
          _$BrokerProposalDriverRequest._(
            birthDate: BuiltValueNullFieldError.checkNotNull(
                birthDate, r'BrokerProposalDriverRequest', 'birthDate'),
            fullName: BuiltValueNullFieldError.checkNotNull(
                fullName, r'BrokerProposalDriverRequest', 'fullName'),
            isOwner: isOwner,
            licenceCategories: licenceCategories.build(),
            licenceIssueDate: BuiltValueNullFieldError.checkNotNull(
                licenceIssueDate,
                r'BrokerProposalDriverRequest',
                'licenceIssueDate'),
            licenceNumber: BuiltValueNullFieldError.checkNotNull(
                licenceNumber, r'BrokerProposalDriverRequest', 'licenceNumber'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'licenceCategories';
        licenceCategories.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'BrokerProposalDriverRequest', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
