// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'problem_detail.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$ProblemDetail extends ProblemDetail {
  @override
  final String code;
  @override
  final String? detail;
  @override
  final BuiltList<FieldError>? errors;
  @override
  final String? instance;
  @override
  final int status;
  @override
  final String title;
  @override
  final String type;

  factory _$ProblemDetail([void Function(ProblemDetailBuilder)? updates]) =>
      (ProblemDetailBuilder()..update(updates))._build();

  _$ProblemDetail._(
      {required this.code,
      this.detail,
      this.errors,
      this.instance,
      required this.status,
      required this.title,
      required this.type})
      : super._();
  @override
  ProblemDetail rebuild(void Function(ProblemDetailBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  ProblemDetailBuilder toBuilder() => ProblemDetailBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is ProblemDetail &&
        code == other.code &&
        detail == other.detail &&
        errors == other.errors &&
        instance == other.instance &&
        status == other.status &&
        title == other.title &&
        type == other.type;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, code.hashCode);
    _$hash = $jc(_$hash, detail.hashCode);
    _$hash = $jc(_$hash, errors.hashCode);
    _$hash = $jc(_$hash, instance.hashCode);
    _$hash = $jc(_$hash, status.hashCode);
    _$hash = $jc(_$hash, title.hashCode);
    _$hash = $jc(_$hash, type.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'ProblemDetail')
          ..add('code', code)
          ..add('detail', detail)
          ..add('errors', errors)
          ..add('instance', instance)
          ..add('status', status)
          ..add('title', title)
          ..add('type', type))
        .toString();
  }
}

class ProblemDetailBuilder
    implements Builder<ProblemDetail, ProblemDetailBuilder> {
  _$ProblemDetail? _$v;

  String? _code;
  String? get code => _$this._code;
  set code(String? code) => _$this._code = code;

  String? _detail;
  String? get detail => _$this._detail;
  set detail(String? detail) => _$this._detail = detail;

  ListBuilder<FieldError>? _errors;
  ListBuilder<FieldError> get errors =>
      _$this._errors ??= ListBuilder<FieldError>();
  set errors(ListBuilder<FieldError>? errors) => _$this._errors = errors;

  String? _instance;
  String? get instance => _$this._instance;
  set instance(String? instance) => _$this._instance = instance;

  int? _status;
  int? get status => _$this._status;
  set status(int? status) => _$this._status = status;

  String? _title;
  String? get title => _$this._title;
  set title(String? title) => _$this._title = title;

  String? _type;
  String? get type => _$this._type;
  set type(String? type) => _$this._type = type;

  ProblemDetailBuilder() {
    ProblemDetail._defaults(this);
  }

  ProblemDetailBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _code = $v.code;
      _detail = $v.detail;
      _errors = $v.errors?.toBuilder();
      _instance = $v.instance;
      _status = $v.status;
      _title = $v.title;
      _type = $v.type;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(ProblemDetail other) {
    _$v = other as _$ProblemDetail;
  }

  @override
  void update(void Function(ProblemDetailBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  ProblemDetail build() => _build();

  _$ProblemDetail _build() {
    _$ProblemDetail _$result;
    try {
      _$result = _$v ??
          _$ProblemDetail._(
            code: BuiltValueNullFieldError.checkNotNull(
                code, r'ProblemDetail', 'code'),
            detail: detail,
            errors: _errors?.build(),
            instance: instance,
            status: BuiltValueNullFieldError.checkNotNull(
                status, r'ProblemDetail', 'status'),
            title: BuiltValueNullFieldError.checkNotNull(
                title, r'ProblemDetail', 'title'),
            type: BuiltValueNullFieldError.checkNotNull(
                type, r'ProblemDetail', 'type'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'errors';
        _errors?.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'ProblemDetail', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
