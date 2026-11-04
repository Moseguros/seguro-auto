// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'save_declarations_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$SaveDeclarationsRequest extends SaveDeclarationsRequest {
  @override
  final BuiltList<DeclarationAnswerRequest>? answers;
  @override
  final bool truthfulnessConfirmed;
  @override
  final int version;

  factory _$SaveDeclarationsRequest(
          [void Function(SaveDeclarationsRequestBuilder)? updates]) =>
      (SaveDeclarationsRequestBuilder()..update(updates))._build();

  _$SaveDeclarationsRequest._(
      {this.answers,
      required this.truthfulnessConfirmed,
      required this.version})
      : super._();
  @override
  SaveDeclarationsRequest rebuild(
          void Function(SaveDeclarationsRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  SaveDeclarationsRequestBuilder toBuilder() =>
      SaveDeclarationsRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is SaveDeclarationsRequest &&
        answers == other.answers &&
        truthfulnessConfirmed == other.truthfulnessConfirmed &&
        version == other.version;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, answers.hashCode);
    _$hash = $jc(_$hash, truthfulnessConfirmed.hashCode);
    _$hash = $jc(_$hash, version.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'SaveDeclarationsRequest')
          ..add('answers', answers)
          ..add('truthfulnessConfirmed', truthfulnessConfirmed)
          ..add('version', version))
        .toString();
  }
}

class SaveDeclarationsRequestBuilder
    implements
        Builder<SaveDeclarationsRequest, SaveDeclarationsRequestBuilder> {
  _$SaveDeclarationsRequest? _$v;

  ListBuilder<DeclarationAnswerRequest>? _answers;
  ListBuilder<DeclarationAnswerRequest> get answers =>
      _$this._answers ??= ListBuilder<DeclarationAnswerRequest>();
  set answers(ListBuilder<DeclarationAnswerRequest>? answers) =>
      _$this._answers = answers;

  bool? _truthfulnessConfirmed;
  bool? get truthfulnessConfirmed => _$this._truthfulnessConfirmed;
  set truthfulnessConfirmed(bool? truthfulnessConfirmed) =>
      _$this._truthfulnessConfirmed = truthfulnessConfirmed;

  int? _version;
  int? get version => _$this._version;
  set version(int? version) => _$this._version = version;

  SaveDeclarationsRequestBuilder() {
    SaveDeclarationsRequest._defaults(this);
  }

  SaveDeclarationsRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _answers = $v.answers?.toBuilder();
      _truthfulnessConfirmed = $v.truthfulnessConfirmed;
      _version = $v.version;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(SaveDeclarationsRequest other) {
    _$v = other as _$SaveDeclarationsRequest;
  }

  @override
  void update(void Function(SaveDeclarationsRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  SaveDeclarationsRequest build() => _build();

  _$SaveDeclarationsRequest _build() {
    _$SaveDeclarationsRequest _$result;
    try {
      _$result = _$v ??
          _$SaveDeclarationsRequest._(
            answers: _answers?.build(),
            truthfulnessConfirmed: BuiltValueNullFieldError.checkNotNull(
                truthfulnessConfirmed,
                r'SaveDeclarationsRequest',
                'truthfulnessConfirmed'),
            version: BuiltValueNullFieldError.checkNotNull(
                version, r'SaveDeclarationsRequest', 'version'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'answers';
        _answers?.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'SaveDeclarationsRequest', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
