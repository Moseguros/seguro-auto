// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'proposal_driver.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$ProposalDriver extends ProposalDriver {
  @override
  final String fullName;
  @override
  final String id;
  @override
  final Date licenceIssueDate;
  @override
  final String licenceNumberMasked;

  factory _$ProposalDriver([void Function(ProposalDriverBuilder)? updates]) =>
      (ProposalDriverBuilder()..update(updates))._build();

  _$ProposalDriver._(
      {required this.fullName,
      required this.id,
      required this.licenceIssueDate,
      required this.licenceNumberMasked})
      : super._();
  @override
  ProposalDriver rebuild(void Function(ProposalDriverBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  ProposalDriverBuilder toBuilder() => ProposalDriverBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is ProposalDriver &&
        fullName == other.fullName &&
        id == other.id &&
        licenceIssueDate == other.licenceIssueDate &&
        licenceNumberMasked == other.licenceNumberMasked;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, fullName.hashCode);
    _$hash = $jc(_$hash, id.hashCode);
    _$hash = $jc(_$hash, licenceIssueDate.hashCode);
    _$hash = $jc(_$hash, licenceNumberMasked.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'ProposalDriver')
          ..add('fullName', fullName)
          ..add('id', id)
          ..add('licenceIssueDate', licenceIssueDate)
          ..add('licenceNumberMasked', licenceNumberMasked))
        .toString();
  }
}

class ProposalDriverBuilder
    implements Builder<ProposalDriver, ProposalDriverBuilder> {
  _$ProposalDriver? _$v;

  String? _fullName;
  String? get fullName => _$this._fullName;
  set fullName(String? fullName) => _$this._fullName = fullName;

  String? _id;
  String? get id => _$this._id;
  set id(String? id) => _$this._id = id;

  Date? _licenceIssueDate;
  Date? get licenceIssueDate => _$this._licenceIssueDate;
  set licenceIssueDate(Date? licenceIssueDate) =>
      _$this._licenceIssueDate = licenceIssueDate;

  String? _licenceNumberMasked;
  String? get licenceNumberMasked => _$this._licenceNumberMasked;
  set licenceNumberMasked(String? licenceNumberMasked) =>
      _$this._licenceNumberMasked = licenceNumberMasked;

  ProposalDriverBuilder() {
    ProposalDriver._defaults(this);
  }

  ProposalDriverBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _fullName = $v.fullName;
      _id = $v.id;
      _licenceIssueDate = $v.licenceIssueDate;
      _licenceNumberMasked = $v.licenceNumberMasked;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(ProposalDriver other) {
    _$v = other as _$ProposalDriver;
  }

  @override
  void update(void Function(ProposalDriverBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  ProposalDriver build() => _build();

  _$ProposalDriver _build() {
    final _$result = _$v ??
        _$ProposalDriver._(
          fullName: BuiltValueNullFieldError.checkNotNull(
              fullName, r'ProposalDriver', 'fullName'),
          id: BuiltValueNullFieldError.checkNotNull(
              id, r'ProposalDriver', 'id'),
          licenceIssueDate: BuiltValueNullFieldError.checkNotNull(
              licenceIssueDate, r'ProposalDriver', 'licenceIssueDate'),
          licenceNumberMasked: BuiltValueNullFieldError.checkNotNull(
              licenceNumberMasked, r'ProposalDriver', 'licenceNumberMasked'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
