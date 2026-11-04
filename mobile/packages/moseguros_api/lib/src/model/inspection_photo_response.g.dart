// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'inspection_photo_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$InspectionPhotoResponse extends InspectionPhotoResponse {
  @override
  final String fileId;
  @override
  final String id;
  @override
  final num? latitude;
  @override
  final num? longitude;
  @override
  final PhotoSlot slot;
  @override
  final PhotoSource source_;
  @override
  final DateTime? takenAt;
  @override
  final DateTime uploadedAt;

  factory _$InspectionPhotoResponse(
          [void Function(InspectionPhotoResponseBuilder)? updates]) =>
      (InspectionPhotoResponseBuilder()..update(updates))._build();

  _$InspectionPhotoResponse._(
      {required this.fileId,
      required this.id,
      this.latitude,
      this.longitude,
      required this.slot,
      required this.source_,
      this.takenAt,
      required this.uploadedAt})
      : super._();
  @override
  InspectionPhotoResponse rebuild(
          void Function(InspectionPhotoResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  InspectionPhotoResponseBuilder toBuilder() =>
      InspectionPhotoResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is InspectionPhotoResponse &&
        fileId == other.fileId &&
        id == other.id &&
        latitude == other.latitude &&
        longitude == other.longitude &&
        slot == other.slot &&
        source_ == other.source_ &&
        takenAt == other.takenAt &&
        uploadedAt == other.uploadedAt;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, fileId.hashCode);
    _$hash = $jc(_$hash, id.hashCode);
    _$hash = $jc(_$hash, latitude.hashCode);
    _$hash = $jc(_$hash, longitude.hashCode);
    _$hash = $jc(_$hash, slot.hashCode);
    _$hash = $jc(_$hash, source_.hashCode);
    _$hash = $jc(_$hash, takenAt.hashCode);
    _$hash = $jc(_$hash, uploadedAt.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'InspectionPhotoResponse')
          ..add('fileId', fileId)
          ..add('id', id)
          ..add('latitude', latitude)
          ..add('longitude', longitude)
          ..add('slot', slot)
          ..add('source_', source_)
          ..add('takenAt', takenAt)
          ..add('uploadedAt', uploadedAt))
        .toString();
  }
}

class InspectionPhotoResponseBuilder
    implements
        Builder<InspectionPhotoResponse, InspectionPhotoResponseBuilder> {
  _$InspectionPhotoResponse? _$v;

  String? _fileId;
  String? get fileId => _$this._fileId;
  set fileId(String? fileId) => _$this._fileId = fileId;

  String? _id;
  String? get id => _$this._id;
  set id(String? id) => _$this._id = id;

  num? _latitude;
  num? get latitude => _$this._latitude;
  set latitude(num? latitude) => _$this._latitude = latitude;

  num? _longitude;
  num? get longitude => _$this._longitude;
  set longitude(num? longitude) => _$this._longitude = longitude;

  PhotoSlot? _slot;
  PhotoSlot? get slot => _$this._slot;
  set slot(PhotoSlot? slot) => _$this._slot = slot;

  PhotoSource? _source_;
  PhotoSource? get source_ => _$this._source_;
  set source_(PhotoSource? source_) => _$this._source_ = source_;

  DateTime? _takenAt;
  DateTime? get takenAt => _$this._takenAt;
  set takenAt(DateTime? takenAt) => _$this._takenAt = takenAt;

  DateTime? _uploadedAt;
  DateTime? get uploadedAt => _$this._uploadedAt;
  set uploadedAt(DateTime? uploadedAt) => _$this._uploadedAt = uploadedAt;

  InspectionPhotoResponseBuilder() {
    InspectionPhotoResponse._defaults(this);
  }

  InspectionPhotoResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _fileId = $v.fileId;
      _id = $v.id;
      _latitude = $v.latitude;
      _longitude = $v.longitude;
      _slot = $v.slot;
      _source_ = $v.source_;
      _takenAt = $v.takenAt;
      _uploadedAt = $v.uploadedAt;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(InspectionPhotoResponse other) {
    _$v = other as _$InspectionPhotoResponse;
  }

  @override
  void update(void Function(InspectionPhotoResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  InspectionPhotoResponse build() => _build();

  _$InspectionPhotoResponse _build() {
    final _$result = _$v ??
        _$InspectionPhotoResponse._(
          fileId: BuiltValueNullFieldError.checkNotNull(
              fileId, r'InspectionPhotoResponse', 'fileId'),
          id: BuiltValueNullFieldError.checkNotNull(
              id, r'InspectionPhotoResponse', 'id'),
          latitude: latitude,
          longitude: longitude,
          slot: BuiltValueNullFieldError.checkNotNull(
              slot, r'InspectionPhotoResponse', 'slot'),
          source_: BuiltValueNullFieldError.checkNotNull(
              source_, r'InspectionPhotoResponse', 'source_'),
          takenAt: takenAt,
          uploadedAt: BuiltValueNullFieldError.checkNotNull(
              uploadedAt, r'InspectionPhotoResponse', 'uploadedAt'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
