// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'insurer_proposal_driver.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$InsurerProposalDriver extends InsurerProposalDriver {
  @override
  final Date? birthDate;
  @override
  final String fullName;
  @override
  final String id;
  @override
  final BuiltList<String> licenceCategories;
  @override
  final Date licenceIssueDate;
  @override
  final String licenceNumberMasked;

  factory _$InsurerProposalDriver(
          [void Function(InsurerProposalDriverBuilder)? updates]) =>
      (InsurerProposalDriverBuilder()..update(updates))._build();

  _$InsurerProposalDriver._(
      {this.birthDate,
      required this.fullName,
      required this.id,
      required this.licenceCategories,
      required this.licenceIssueDate,
      required this.licenceNumberMasked})
      : super._();
  @override
  InsurerProposalDriver rebuild(
          void Function(InsurerProposalDriverBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  InsurerProposalDriverBuilder toBuilder() =>
      InsurerProposalDriverBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is InsurerProposalDriver &&
        birthDate == other.birthDate &&
        fullName == other.fullName &&
        id == other.id &&
        licenceCategories == other.licenceCategories &&
        licenceIssueDate == other.licenceIssueDate &&
        licenceNumberMasked == other.licenceNumberMasked;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, birthDate.hashCode);
    _$hash = $jc(_$hash, fullName.hashCode);
    _$hash = $jc(_$hash, id.hashCode);
    _$hash = $jc(_$hash, licenceCategories.hashCode);
    _$hash = $jc(_$hash, licenceIssueDate.hashCode);
    _$hash = $jc(_$hash, licenceNumberMasked.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'InsurerProposalDriver')
          ..add('birthDate', birthDate)
          ..add('fullName', fullName)
          ..add('id', id)
          ..add('licenceCategories', licenceCategories)
          ..add('licenceIssueDate', licenceIssueDate)
          ..add('licenceNumberMasked', licenceNumberMasked))
        .toString();
  }
}

class InsurerProposalDriverBuilder
    implements Builder<InsurerProposalDriver, InsurerProposalDriverBuilder> {
  _$InsurerProposalDriver? _$v;

  Date? _birthDate;
  Date? get birthDate => _$this._birthDate;
  set birthDate(Date? birthDate) => _$this._birthDate = birthDate;

  String? _fullName;
  String? get fullName => _$this._fullName;
  set fullName(String? fullName) => _$this._fullName = fullName;

  String? _id;
  String? get id => _$this._id;
  set id(String? id) => _$this._id = id;

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

  InsurerProposalDriverBuilder() {
    InsurerProposalDriver._defaults(this);
  }

  InsurerProposalDriverBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _birthDate = $v.birthDate;
      _fullName = $v.fullName;
      _id = $v.id;
      _licenceCategories = $v.licenceCategories.toBuilder();
      _licenceIssueDate = $v.licenceIssueDate;
      _licenceNumberMasked = $v.licenceNumberMasked;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(InsurerProposalDriver other) {
    _$v = other as _$InsurerProposalDriver;
  }

  @override
  void update(void Function(InsurerProposalDriverBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  InsurerProposalDriver build() => _build();

  _$InsurerProposalDriver _build() {
    _$InsurerProposalDriver _$result;
    try {
      _$result = _$v ??
          _$InsurerProposalDriver._(
            birthDate: birthDate,
            fullName: BuiltValueNullFieldError.checkNotNull(
                fullName, r'InsurerProposalDriver', 'fullName'),
            id: BuiltValueNullFieldError.checkNotNull(
                id, r'InsurerProposalDriver', 'id'),
            licenceCategories: licenceCategories.build(),
            licenceIssueDate: BuiltValueNullFieldError.checkNotNull(
                licenceIssueDate, r'InsurerProposalDriver', 'licenceIssueDate'),
            licenceNumberMasked: BuiltValueNullFieldError.checkNotNull(
                licenceNumberMasked,
                r'InsurerProposalDriver',
                'licenceNumberMasked'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'licenceCategories';
        licenceCategories.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'InsurerProposalDriver', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
