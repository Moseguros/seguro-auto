// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'declaration_answer_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$DeclarationAnswerResponse extends DeclarationAnswerResponse {
  @override
  final bool answer;
  @override
  final int? claimsCount;
  @override
  final String? details;
  @override
  final DeclarationQuestion questionCode;

  factory _$DeclarationAnswerResponse(
          [void Function(DeclarationAnswerResponseBuilder)? updates]) =>
      (DeclarationAnswerResponseBuilder()..update(updates))._build();

  _$DeclarationAnswerResponse._(
      {required this.answer,
      this.claimsCount,
      this.details,
      required this.questionCode})
      : super._();
  @override
  DeclarationAnswerResponse rebuild(
          void Function(DeclarationAnswerResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  DeclarationAnswerResponseBuilder toBuilder() =>
      DeclarationAnswerResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is DeclarationAnswerResponse &&
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
    return (newBuiltValueToStringHelper(r'DeclarationAnswerResponse')
          ..add('answer', answer)
          ..add('claimsCount', claimsCount)
          ..add('details', details)
          ..add('questionCode', questionCode))
        .toString();
  }
}

class DeclarationAnswerResponseBuilder
    implements
        Builder<DeclarationAnswerResponse, DeclarationAnswerResponseBuilder> {
  _$DeclarationAnswerResponse? _$v;

  bool? _answer;
  bool? get answer => _$this._answer;
  set answer(bool? answer) => _$this._answer = answer;

  int? _claimsCount;
  int? get claimsCount => _$this._claimsCount;
  set claimsCount(int? claimsCount) => _$this._claimsCount = claimsCount;

  String? _details;
  String? get details => _$this._details;
  set details(String? details) => _$this._details = details;

  DeclarationQuestion? _questionCode;
  DeclarationQuestion? get questionCode => _$this._questionCode;
  set questionCode(DeclarationQuestion? questionCode) =>
      _$this._questionCode = questionCode;

  DeclarationAnswerResponseBuilder() {
    DeclarationAnswerResponse._defaults(this);
  }

  DeclarationAnswerResponseBuilder get _$this {
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
  void replace(DeclarationAnswerResponse other) {
    _$v = other as _$DeclarationAnswerResponse;
  }

  @override
  void update(void Function(DeclarationAnswerResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  DeclarationAnswerResponse build() => _build();

  _$DeclarationAnswerResponse _build() {
    final _$result = _$v ??
        _$DeclarationAnswerResponse._(
          answer: BuiltValueNullFieldError.checkNotNull(
              answer, r'DeclarationAnswerResponse', 'answer'),
          claimsCount: claimsCount,
          details: details,
          questionCode: BuiltValueNullFieldError.checkNotNull(
              questionCode, r'DeclarationAnswerResponse', 'questionCode'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
