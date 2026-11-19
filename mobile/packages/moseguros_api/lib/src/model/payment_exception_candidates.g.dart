// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'payment_exception_candidates.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$PaymentExceptionCandidates extends PaymentExceptionCandidates {
  @override
  final BuiltList<PaymentExceptionCandidate> candidates;

  factory _$PaymentExceptionCandidates(
          [void Function(PaymentExceptionCandidatesBuilder)? updates]) =>
      (PaymentExceptionCandidatesBuilder()..update(updates))._build();

  _$PaymentExceptionCandidates._({required this.candidates}) : super._();
  @override
  PaymentExceptionCandidates rebuild(
          void Function(PaymentExceptionCandidatesBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PaymentExceptionCandidatesBuilder toBuilder() =>
      PaymentExceptionCandidatesBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PaymentExceptionCandidates &&
        candidates == other.candidates;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, candidates.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'PaymentExceptionCandidates')
          ..add('candidates', candidates))
        .toString();
  }
}

class PaymentExceptionCandidatesBuilder
    implements
        Builder<PaymentExceptionCandidates, PaymentExceptionCandidatesBuilder> {
  _$PaymentExceptionCandidates? _$v;

  ListBuilder<PaymentExceptionCandidate>? _candidates;
  ListBuilder<PaymentExceptionCandidate> get candidates =>
      _$this._candidates ??= ListBuilder<PaymentExceptionCandidate>();
  set candidates(ListBuilder<PaymentExceptionCandidate>? candidates) =>
      _$this._candidates = candidates;

  PaymentExceptionCandidatesBuilder() {
    PaymentExceptionCandidates._defaults(this);
  }

  PaymentExceptionCandidatesBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _candidates = $v.candidates.toBuilder();
      _$v = null;
    }
    return this;
  }

  @override
  void replace(PaymentExceptionCandidates other) {
    _$v = other as _$PaymentExceptionCandidates;
  }

  @override
  void update(void Function(PaymentExceptionCandidatesBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PaymentExceptionCandidates build() => _build();

  _$PaymentExceptionCandidates _build() {
    _$PaymentExceptionCandidates _$result;
    try {
      _$result = _$v ??
          _$PaymentExceptionCandidates._(
            candidates: candidates.build(),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'candidates';
        candidates.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'PaymentExceptionCandidates', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
