// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'save_suitability_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$SaveSuitabilityRequest extends SaveSuitabilityRequest {
  @override
  final BuiltList<SuitabilityAnswerRequest>? answers;
  @override
  final int version;

  factory _$SaveSuitabilityRequest(
          [void Function(SaveSuitabilityRequestBuilder)? updates]) =>
      (SaveSuitabilityRequestBuilder()..update(updates))._build();

  _$SaveSuitabilityRequest._({this.answers, required this.version}) : super._();
  @override
  SaveSuitabilityRequest rebuild(
          void Function(SaveSuitabilityRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  SaveSuitabilityRequestBuilder toBuilder() =>
      SaveSuitabilityRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is SaveSuitabilityRequest &&
        answers == other.answers &&
        version == other.version;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, answers.hashCode);
    _$hash = $jc(_$hash, version.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'SaveSuitabilityRequest')
          ..add('answers', answers)
          ..add('version', version))
        .toString();
  }
}

class SaveSuitabilityRequestBuilder
    implements Builder<SaveSuitabilityRequest, SaveSuitabilityRequestBuilder> {
  _$SaveSuitabilityRequest? _$v;

  ListBuilder<SuitabilityAnswerRequest>? _answers;
  ListBuilder<SuitabilityAnswerRequest> get answers =>
      _$this._answers ??= ListBuilder<SuitabilityAnswerRequest>();
  set answers(ListBuilder<SuitabilityAnswerRequest>? answers) =>
      _$this._answers = answers;

  int? _version;
  int? get version => _$this._version;
  set version(int? version) => _$this._version = version;

  SaveSuitabilityRequestBuilder() {
    SaveSuitabilityRequest._defaults(this);
  }

  SaveSuitabilityRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _answers = $v.answers?.toBuilder();
      _version = $v.version;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(SaveSuitabilityRequest other) {
    _$v = other as _$SaveSuitabilityRequest;
  }

  @override
  void update(void Function(SaveSuitabilityRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  SaveSuitabilityRequest build() => _build();

  _$SaveSuitabilityRequest _build() {
    _$SaveSuitabilityRequest _$result;
    try {
      _$result = _$v ??
          _$SaveSuitabilityRequest._(
            answers: _answers?.build(),
            version: BuiltValueNullFieldError.checkNotNull(
                version, r'SaveSuitabilityRequest', 'version'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'answers';
        _answers?.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'SaveSuitabilityRequest', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
