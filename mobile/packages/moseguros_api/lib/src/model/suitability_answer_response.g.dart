// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'suitability_answer_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$SuitabilityAnswerResponse extends SuitabilityAnswerResponse {
  @override
  final String answer;
  @override
  final SuitabilityQuestion questionCode;

  factory _$SuitabilityAnswerResponse(
          [void Function(SuitabilityAnswerResponseBuilder)? updates]) =>
      (SuitabilityAnswerResponseBuilder()..update(updates))._build();

  _$SuitabilityAnswerResponse._(
      {required this.answer, required this.questionCode})
      : super._();
  @override
  SuitabilityAnswerResponse rebuild(
          void Function(SuitabilityAnswerResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  SuitabilityAnswerResponseBuilder toBuilder() =>
      SuitabilityAnswerResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is SuitabilityAnswerResponse &&
        answer == other.answer &&
        questionCode == other.questionCode;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, answer.hashCode);
    _$hash = $jc(_$hash, questionCode.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'SuitabilityAnswerResponse')
          ..add('answer', answer)
          ..add('questionCode', questionCode))
        .toString();
  }
}

class SuitabilityAnswerResponseBuilder
    implements
        Builder<SuitabilityAnswerResponse, SuitabilityAnswerResponseBuilder> {
  _$SuitabilityAnswerResponse? _$v;

  String? _answer;
  String? get answer => _$this._answer;
  set answer(String? answer) => _$this._answer = answer;

  SuitabilityQuestion? _questionCode;
  SuitabilityQuestion? get questionCode => _$this._questionCode;
  set questionCode(SuitabilityQuestion? questionCode) =>
      _$this._questionCode = questionCode;

  SuitabilityAnswerResponseBuilder() {
    SuitabilityAnswerResponse._defaults(this);
  }

  SuitabilityAnswerResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _answer = $v.answer;
      _questionCode = $v.questionCode;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(SuitabilityAnswerResponse other) {
    _$v = other as _$SuitabilityAnswerResponse;
  }

  @override
  void update(void Function(SuitabilityAnswerResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  SuitabilityAnswerResponse build() => _build();

  _$SuitabilityAnswerResponse _build() {
    final _$result = _$v ??
        _$SuitabilityAnswerResponse._(
          answer: BuiltValueNullFieldError.checkNotNull(
              answer, r'SuitabilityAnswerResponse', 'answer'),
          questionCode: BuiltValueNullFieldError.checkNotNull(
              questionCode, r'SuitabilityAnswerResponse', 'questionCode'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
