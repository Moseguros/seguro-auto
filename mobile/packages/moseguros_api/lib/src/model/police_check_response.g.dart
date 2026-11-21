// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'police_check_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$PoliceCheckResponse extends PoliceCheckResponse {
  @override
  final DateTime checkedAt;
  @override
  final String id;
  @override
  final String? plateMasked;
  @override
  final VerificationResult result;
  @override
  final SubjectType subjectType;

  factory _$PoliceCheckResponse(
          [void Function(PoliceCheckResponseBuilder)? updates]) =>
      (PoliceCheckResponseBuilder()..update(updates))._build();

  _$PoliceCheckResponse._(
      {required this.checkedAt,
      required this.id,
      this.plateMasked,
      required this.result,
      required this.subjectType})
      : super._();
  @override
  PoliceCheckResponse rebuild(
          void Function(PoliceCheckResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PoliceCheckResponseBuilder toBuilder() =>
      PoliceCheckResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PoliceCheckResponse &&
        checkedAt == other.checkedAt &&
        id == other.id &&
        plateMasked == other.plateMasked &&
        result == other.result &&
        subjectType == other.subjectType;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, checkedAt.hashCode);
    _$hash = $jc(_$hash, id.hashCode);
    _$hash = $jc(_$hash, plateMasked.hashCode);
    _$hash = $jc(_$hash, result.hashCode);
    _$hash = $jc(_$hash, subjectType.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'PoliceCheckResponse')
          ..add('checkedAt', checkedAt)
          ..add('id', id)
          ..add('plateMasked', plateMasked)
          ..add('result', result)
          ..add('subjectType', subjectType))
        .toString();
  }
}

class PoliceCheckResponseBuilder
    implements Builder<PoliceCheckResponse, PoliceCheckResponseBuilder> {
  _$PoliceCheckResponse? _$v;

  DateTime? _checkedAt;
  DateTime? get checkedAt => _$this._checkedAt;
  set checkedAt(DateTime? checkedAt) => _$this._checkedAt = checkedAt;

  String? _id;
  String? get id => _$this._id;
  set id(String? id) => _$this._id = id;

  String? _plateMasked;
  String? get plateMasked => _$this._plateMasked;
  set plateMasked(String? plateMasked) => _$this._plateMasked = plateMasked;

  VerificationResult? _result;
  VerificationResult? get result => _$this._result;
  set result(VerificationResult? result) => _$this._result = result;

  SubjectType? _subjectType;
  SubjectType? get subjectType => _$this._subjectType;
  set subjectType(SubjectType? subjectType) =>
      _$this._subjectType = subjectType;

  PoliceCheckResponseBuilder() {
    PoliceCheckResponse._defaults(this);
  }

  PoliceCheckResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _checkedAt = $v.checkedAt;
      _id = $v.id;
      _plateMasked = $v.plateMasked;
      _result = $v.result;
      _subjectType = $v.subjectType;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(PoliceCheckResponse other) {
    _$v = other as _$PoliceCheckResponse;
  }

  @override
  void update(void Function(PoliceCheckResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PoliceCheckResponse build() => _build();

  _$PoliceCheckResponse _build() {
    final _$result = _$v ??
        _$PoliceCheckResponse._(
          checkedAt: BuiltValueNullFieldError.checkNotNull(
              checkedAt, r'PoliceCheckResponse', 'checkedAt'),
          id: BuiltValueNullFieldError.checkNotNull(
              id, r'PoliceCheckResponse', 'id'),
          plateMasked: plateMasked,
          result: BuiltValueNullFieldError.checkNotNull(
              result, r'PoliceCheckResponse', 'result'),
          subjectType: BuiltValueNullFieldError.checkNotNull(
              subjectType, r'PoliceCheckResponse', 'subjectType'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
