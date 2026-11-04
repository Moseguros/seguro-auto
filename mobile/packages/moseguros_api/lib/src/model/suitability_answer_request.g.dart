// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'suitability_answer_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$SuitabilityAnswerRequest extends SuitabilityAnswerRequest {
  @override
  final String answer;
  @override
  final String questionCode;

  factory _$SuitabilityAnswerRequest(
          [void Function(SuitabilityAnswerRequestBuilder)? updates]) =>
      (SuitabilityAnswerRequestBuilder()..update(updates))._build();

  _$SuitabilityAnswerRequest._(
      {required this.answer, required this.questionCode})
      : super._();
  @override
  SuitabilityAnswerRequest rebuild(
          void Function(SuitabilityAnswerRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  SuitabilityAnswerRequestBuilder toBuilder() =>
      SuitabilityAnswerRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is SuitabilityAnswerRequest &&
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
    return (newBuiltValueToStringHelper(r'SuitabilityAnswerRequest')
          ..add('answer', answer)
          ..add('questionCode', questionCode))
        .toString();
  }
}

class SuitabilityAnswerRequestBuilder
    implements
        Builder<SuitabilityAnswerRequest, SuitabilityAnswerRequestBuilder> {
  _$SuitabilityAnswerRequest? _$v;

  String? _answer;
  String? get answer => _$this._answer;
  set answer(String? answer) => _$this._answer = answer;

  String? _questionCode;
  String? get questionCode => _$this._questionCode;
  set questionCode(String? questionCode) => _$this._questionCode = questionCode;

  SuitabilityAnswerRequestBuilder() {
    SuitabilityAnswerRequest._defaults(this);
  }

  SuitabilityAnswerRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _answer = $v.answer;
      _questionCode = $v.questionCode;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(SuitabilityAnswerRequest other) {
    _$v = other as _$SuitabilityAnswerRequest;
  }

  @override
  void update(void Function(SuitabilityAnswerRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  SuitabilityAnswerRequest build() => _build();

  _$SuitabilityAnswerRequest _build() {
    final _$result = _$v ??
        _$SuitabilityAnswerRequest._(
          answer: BuiltValueNullFieldError.checkNotNull(
              answer, r'SuitabilityAnswerRequest', 'answer'),
          questionCode: BuiltValueNullFieldError.checkNotNull(
              questionCode, r'SuitabilityAnswerRequest', 'questionCode'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
