// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'inspection_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$InspectionResponse extends InspectionResponse {
  @override
  final String? customerNotes;
  @override
  final String? id;
  @override
  final int? maxExtraPhotos;
  @override
  final BuiltList<PhotoSlot>? missingSlots;
  @override
  final InspectionMode? mode;
  @override
  final int? odometerKm;
  @override
  final BuiltList<InspectionPhotoResponse>? photos;
  @override
  final bool required_;
  @override
  final InspectionReview? review;
  @override
  final InspectionStatus? status;
  @override
  final DateTime? submittedAt;
  @override
  final int? version;

  factory _$InspectionResponse(
          [void Function(InspectionResponseBuilder)? updates]) =>
      (InspectionResponseBuilder()..update(updates))._build();

  _$InspectionResponse._(
      {this.customerNotes,
      this.id,
      this.maxExtraPhotos,
      this.missingSlots,
      this.mode,
      this.odometerKm,
      this.photos,
      required this.required_,
      this.review,
      this.status,
      this.submittedAt,
      this.version})
      : super._();
  @override
  InspectionResponse rebuild(
          void Function(InspectionResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  InspectionResponseBuilder toBuilder() =>
      InspectionResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is InspectionResponse &&
        customerNotes == other.customerNotes &&
        id == other.id &&
        maxExtraPhotos == other.maxExtraPhotos &&
        missingSlots == other.missingSlots &&
        mode == other.mode &&
        odometerKm == other.odometerKm &&
        photos == other.photos &&
        required_ == other.required_ &&
        review == other.review &&
        status == other.status &&
        submittedAt == other.submittedAt &&
        version == other.version;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, customerNotes.hashCode);
    _$hash = $jc(_$hash, id.hashCode);
    _$hash = $jc(_$hash, maxExtraPhotos.hashCode);
    _$hash = $jc(_$hash, missingSlots.hashCode);
    _$hash = $jc(_$hash, mode.hashCode);
    _$hash = $jc(_$hash, odometerKm.hashCode);
    _$hash = $jc(_$hash, photos.hashCode);
    _$hash = $jc(_$hash, required_.hashCode);
    _$hash = $jc(_$hash, review.hashCode);
    _$hash = $jc(_$hash, status.hashCode);
    _$hash = $jc(_$hash, submittedAt.hashCode);
    _$hash = $jc(_$hash, version.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'InspectionResponse')
          ..add('customerNotes', customerNotes)
          ..add('id', id)
          ..add('maxExtraPhotos', maxExtraPhotos)
          ..add('missingSlots', missingSlots)
          ..add('mode', mode)
          ..add('odometerKm', odometerKm)
          ..add('photos', photos)
          ..add('required_', required_)
          ..add('review', review)
          ..add('status', status)
          ..add('submittedAt', submittedAt)
          ..add('version', version))
        .toString();
  }
}

class InspectionResponseBuilder
    implements Builder<InspectionResponse, InspectionResponseBuilder> {
  _$InspectionResponse? _$v;

  String? _customerNotes;
  String? get customerNotes => _$this._customerNotes;
  set customerNotes(String? customerNotes) =>
      _$this._customerNotes = customerNotes;

  String? _id;
  String? get id => _$this._id;
  set id(String? id) => _$this._id = id;

  int? _maxExtraPhotos;
  int? get maxExtraPhotos => _$this._maxExtraPhotos;
  set maxExtraPhotos(int? maxExtraPhotos) =>
      _$this._maxExtraPhotos = maxExtraPhotos;

  ListBuilder<PhotoSlot>? _missingSlots;
  ListBuilder<PhotoSlot> get missingSlots =>
      _$this._missingSlots ??= ListBuilder<PhotoSlot>();
  set missingSlots(ListBuilder<PhotoSlot>? missingSlots) =>
      _$this._missingSlots = missingSlots;

  InspectionMode? _mode;
  InspectionMode? get mode => _$this._mode;
  set mode(InspectionMode? mode) => _$this._mode = mode;

  int? _odometerKm;
  int? get odometerKm => _$this._odometerKm;
  set odometerKm(int? odometerKm) => _$this._odometerKm = odometerKm;

  ListBuilder<InspectionPhotoResponse>? _photos;
  ListBuilder<InspectionPhotoResponse> get photos =>
      _$this._photos ??= ListBuilder<InspectionPhotoResponse>();
  set photos(ListBuilder<InspectionPhotoResponse>? photos) =>
      _$this._photos = photos;

  bool? _required_;
  bool? get required_ => _$this._required_;
  set required_(bool? required_) => _$this._required_ = required_;

  InspectionReviewBuilder? _review;
  InspectionReviewBuilder get review =>
      _$this._review ??= InspectionReviewBuilder();
  set review(InspectionReviewBuilder? review) => _$this._review = review;

  InspectionStatus? _status;
  InspectionStatus? get status => _$this._status;
  set status(InspectionStatus? status) => _$this._status = status;

  DateTime? _submittedAt;
  DateTime? get submittedAt => _$this._submittedAt;
  set submittedAt(DateTime? submittedAt) => _$this._submittedAt = submittedAt;

  int? _version;
  int? get version => _$this._version;
  set version(int? version) => _$this._version = version;

  InspectionResponseBuilder() {
    InspectionResponse._defaults(this);
  }

  InspectionResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _customerNotes = $v.customerNotes;
      _id = $v.id;
      _maxExtraPhotos = $v.maxExtraPhotos;
      _missingSlots = $v.missingSlots?.toBuilder();
      _mode = $v.mode;
      _odometerKm = $v.odometerKm;
      _photos = $v.photos?.toBuilder();
      _required_ = $v.required_;
      _review = $v.review?.toBuilder();
      _status = $v.status;
      _submittedAt = $v.submittedAt;
      _version = $v.version;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(InspectionResponse other) {
    _$v = other as _$InspectionResponse;
  }

  @override
  void update(void Function(InspectionResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  InspectionResponse build() => _build();

  _$InspectionResponse _build() {
    _$InspectionResponse _$result;
    try {
      _$result = _$v ??
          _$InspectionResponse._(
            customerNotes: customerNotes,
            id: id,
            maxExtraPhotos: maxExtraPhotos,
            missingSlots: _missingSlots?.build(),
            mode: mode,
            odometerKm: odometerKm,
            photos: _photos?.build(),
            required_: BuiltValueNullFieldError.checkNotNull(
                required_, r'InspectionResponse', 'required_'),
            review: _review?.build(),
            status: status,
            submittedAt: submittedAt,
            version: version,
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'missingSlots';
        _missingSlots?.build();

        _$failedField = 'photos';
        _photos?.build();

        _$failedField = 'review';
        _review?.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'InspectionResponse', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
