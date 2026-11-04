// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'inspection_review.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$InspectionReview extends InspectionReview {
  @override
  final String? notes;
  @override
  final String? rejectionReason;
  @override
  final BuiltList<PhotoSlot> retakeSlots;
  @override
  final DateTime reviewedAt;

  factory _$InspectionReview(
          [void Function(InspectionReviewBuilder)? updates]) =>
      (InspectionReviewBuilder()..update(updates))._build();

  _$InspectionReview._(
      {this.notes,
      this.rejectionReason,
      required this.retakeSlots,
      required this.reviewedAt})
      : super._();
  @override
  InspectionReview rebuild(void Function(InspectionReviewBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  InspectionReviewBuilder toBuilder() =>
      InspectionReviewBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is InspectionReview &&
        notes == other.notes &&
        rejectionReason == other.rejectionReason &&
        retakeSlots == other.retakeSlots &&
        reviewedAt == other.reviewedAt;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, notes.hashCode);
    _$hash = $jc(_$hash, rejectionReason.hashCode);
    _$hash = $jc(_$hash, retakeSlots.hashCode);
    _$hash = $jc(_$hash, reviewedAt.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'InspectionReview')
          ..add('notes', notes)
          ..add('rejectionReason', rejectionReason)
          ..add('retakeSlots', retakeSlots)
          ..add('reviewedAt', reviewedAt))
        .toString();
  }
}

class InspectionReviewBuilder
    implements Builder<InspectionReview, InspectionReviewBuilder> {
  _$InspectionReview? _$v;

  String? _notes;
  String? get notes => _$this._notes;
  set notes(String? notes) => _$this._notes = notes;

  String? _rejectionReason;
  String? get rejectionReason => _$this._rejectionReason;
  set rejectionReason(String? rejectionReason) =>
      _$this._rejectionReason = rejectionReason;

  ListBuilder<PhotoSlot>? _retakeSlots;
  ListBuilder<PhotoSlot> get retakeSlots =>
      _$this._retakeSlots ??= ListBuilder<PhotoSlot>();
  set retakeSlots(ListBuilder<PhotoSlot>? retakeSlots) =>
      _$this._retakeSlots = retakeSlots;

  DateTime? _reviewedAt;
  DateTime? get reviewedAt => _$this._reviewedAt;
  set reviewedAt(DateTime? reviewedAt) => _$this._reviewedAt = reviewedAt;

  InspectionReviewBuilder() {
    InspectionReview._defaults(this);
  }

  InspectionReviewBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _notes = $v.notes;
      _rejectionReason = $v.rejectionReason;
      _retakeSlots = $v.retakeSlots.toBuilder();
      _reviewedAt = $v.reviewedAt;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(InspectionReview other) {
    _$v = other as _$InspectionReview;
  }

  @override
  void update(void Function(InspectionReviewBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  InspectionReview build() => _build();

  _$InspectionReview _build() {
    _$InspectionReview _$result;
    try {
      _$result = _$v ??
          _$InspectionReview._(
            notes: notes,
            rejectionReason: rejectionReason,
            retakeSlots: retakeSlots.build(),
            reviewedAt: BuiltValueNullFieldError.checkNotNull(
                reviewedAt, r'InspectionReview', 'reviewedAt'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'retakeSlots';
        retakeSlots.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'InspectionReview', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
