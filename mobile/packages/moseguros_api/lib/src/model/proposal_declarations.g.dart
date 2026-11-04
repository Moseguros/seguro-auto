// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'proposal_declarations.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$ProposalDeclarations extends ProposalDeclarations {
  @override
  final BuiltList<DeclarationAnswerResponse> answers;
  @override
  final DateTime confirmedAt;

  factory _$ProposalDeclarations(
          [void Function(ProposalDeclarationsBuilder)? updates]) =>
      (ProposalDeclarationsBuilder()..update(updates))._build();

  _$ProposalDeclarations._({required this.answers, required this.confirmedAt})
      : super._();
  @override
  ProposalDeclarations rebuild(
          void Function(ProposalDeclarationsBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  ProposalDeclarationsBuilder toBuilder() =>
      ProposalDeclarationsBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is ProposalDeclarations &&
        answers == other.answers &&
        confirmedAt == other.confirmedAt;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, answers.hashCode);
    _$hash = $jc(_$hash, confirmedAt.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'ProposalDeclarations')
          ..add('answers', answers)
          ..add('confirmedAt', confirmedAt))
        .toString();
  }
}

class ProposalDeclarationsBuilder
    implements Builder<ProposalDeclarations, ProposalDeclarationsBuilder> {
  _$ProposalDeclarations? _$v;

  ListBuilder<DeclarationAnswerResponse>? _answers;
  ListBuilder<DeclarationAnswerResponse> get answers =>
      _$this._answers ??= ListBuilder<DeclarationAnswerResponse>();
  set answers(ListBuilder<DeclarationAnswerResponse>? answers) =>
      _$this._answers = answers;

  DateTime? _confirmedAt;
  DateTime? get confirmedAt => _$this._confirmedAt;
  set confirmedAt(DateTime? confirmedAt) => _$this._confirmedAt = confirmedAt;

  ProposalDeclarationsBuilder() {
    ProposalDeclarations._defaults(this);
  }

  ProposalDeclarationsBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _answers = $v.answers.toBuilder();
      _confirmedAt = $v.confirmedAt;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(ProposalDeclarations other) {
    _$v = other as _$ProposalDeclarations;
  }

  @override
  void update(void Function(ProposalDeclarationsBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  ProposalDeclarations build() => _build();

  _$ProposalDeclarations _build() {
    _$ProposalDeclarations _$result;
    try {
      _$result = _$v ??
          _$ProposalDeclarations._(
            answers: answers.build(),
            confirmedAt: BuiltValueNullFieldError.checkNotNull(
                confirmedAt, r'ProposalDeclarations', 'confirmedAt'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'answers';
        answers.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'ProposalDeclarations', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
