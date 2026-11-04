// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'declaration_answer_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$DeclarationAnswerRequest extends DeclarationAnswerRequest {
  @override
  final bool answer;
  @override
  final int? claimsCount;
  @override
  final String? details;
  @override
  final String questionCode;

  factory _$DeclarationAnswerRequest(
          [void Function(DeclarationAnswerRequestBuilder)? updates]) =>
      (DeclarationAnswerRequestBuilder()..update(updates))._build();

  _$DeclarationAnswerRequest._(
      {required this.answer,
      this.claimsCount,
      this.details,
      required this.questionCode})
      : super._();
  @override
  DeclarationAnswerRequest rebuild(
          void Function(DeclarationAnswerRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  DeclarationAnswerRequestBuilder toBuilder() =>
      DeclarationAnswerRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is DeclarationAnswerRequest &&
        answer == other.answer &&
        claimsCount == other.claimsCount &&
        details == other.details &&
        questionCode == other.questionCode;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, answer.hashCode);
    _$hash = $jc(_$hash, claimsCount.hashCode);
    _$hash = $jc(_$hash, details.hashCode);
    _$hash = $jc(_$hash, questionCode.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'DeclarationAnswerRequest')
          ..add('answer', answer)
          ..add('claimsCount', claimsCount)
          ..add('details', details)
          ..add('questionCode', questionCode))
        .toString();
  }
}

class DeclarationAnswerRequestBuilder
    implements
        Builder<DeclarationAnswerRequest, DeclarationAnswerRequestBuilder> {
  _$DeclarationAnswerRequest? _$v;

  bool? _answer;
  bool? get answer => _$this._answer;
  set answer(bool? answer) => _$this._answer = answer;

  int? _claimsCount;
  int? get claimsCount => _$this._claimsCount;
  set claimsCount(int? claimsCount) => _$this._claimsCount = claimsCount;

  String? _details;
  String? get details => _$this._details;
  set details(String? details) => _$this._details = details;

  String? _questionCode;
  String? get questionCode => _$this._questionCode;
  set questionCode(String? questionCode) => _$this._questionCode = questionCode;

  DeclarationAnswerRequestBuilder() {
    DeclarationAnswerRequest._defaults(this);
  }

  DeclarationAnswerRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _answer = $v.answer;
      _claimsCount = $v.claimsCount;
      _details = $v.details;
      _questionCode = $v.questionCode;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(DeclarationAnswerRequest other) {
    _$v = other as _$DeclarationAnswerRequest;
  }

  @override
  void update(void Function(DeclarationAnswerRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  DeclarationAnswerRequest build() => _build();

  _$DeclarationAnswerRequest _build() {
    final _$result = _$v ??
        _$DeclarationAnswerRequest._(
          answer: BuiltValueNullFieldError.checkNotNull(
              answer, r'DeclarationAnswerRequest', 'answer'),
          claimsCount: claimsCount,
          details: details,
          questionCode: BuiltValueNullFieldError.checkNotNull(
              questionCode, r'DeclarationAnswerRequest', 'questionCode'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
