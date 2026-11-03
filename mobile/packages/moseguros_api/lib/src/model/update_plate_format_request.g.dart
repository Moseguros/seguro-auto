// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'update_plate_format_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$UpdatePlateFormatRequest extends UpdatePlateFormatRequest {
  @override
  final String displayTemplate;
  @override
  final String example;
  @override
  final String label;
  @override
  final String pattern;
  @override
  final int sortOrder;
  @override
  final int version;

  factory _$UpdatePlateFormatRequest(
          [void Function(UpdatePlateFormatRequestBuilder)? updates]) =>
      (UpdatePlateFormatRequestBuilder()..update(updates))._build();

  _$UpdatePlateFormatRequest._(
      {required this.displayTemplate,
      required this.example,
      required this.label,
      required this.pattern,
      required this.sortOrder,
      required this.version})
      : super._();
  @override
  UpdatePlateFormatRequest rebuild(
          void Function(UpdatePlateFormatRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  UpdatePlateFormatRequestBuilder toBuilder() =>
      UpdatePlateFormatRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is UpdatePlateFormatRequest &&
        displayTemplate == other.displayTemplate &&
        example == other.example &&
        label == other.label &&
        pattern == other.pattern &&
        sortOrder == other.sortOrder &&
        version == other.version;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, displayTemplate.hashCode);
    _$hash = $jc(_$hash, example.hashCode);
    _$hash = $jc(_$hash, label.hashCode);
    _$hash = $jc(_$hash, pattern.hashCode);
    _$hash = $jc(_$hash, sortOrder.hashCode);
    _$hash = $jc(_$hash, version.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'UpdatePlateFormatRequest')
          ..add('displayTemplate', displayTemplate)
          ..add('example', example)
          ..add('label', label)
          ..add('pattern', pattern)
          ..add('sortOrder', sortOrder)
          ..add('version', version))
        .toString();
  }
}

class UpdatePlateFormatRequestBuilder
    implements
        Builder<UpdatePlateFormatRequest, UpdatePlateFormatRequestBuilder> {
  _$UpdatePlateFormatRequest? _$v;

  String? _displayTemplate;
  String? get displayTemplate => _$this._displayTemplate;
  set displayTemplate(String? displayTemplate) =>
      _$this._displayTemplate = displayTemplate;

  String? _example;
  String? get example => _$this._example;
  set example(String? example) => _$this._example = example;

  String? _label;
  String? get label => _$this._label;
  set label(String? label) => _$this._label = label;

  String? _pattern;
  String? get pattern => _$this._pattern;
  set pattern(String? pattern) => _$this._pattern = pattern;

  int? _sortOrder;
  int? get sortOrder => _$this._sortOrder;
  set sortOrder(int? sortOrder) => _$this._sortOrder = sortOrder;

  int? _version;
  int? get version => _$this._version;
  set version(int? version) => _$this._version = version;

  UpdatePlateFormatRequestBuilder() {
    UpdatePlateFormatRequest._defaults(this);
  }

  UpdatePlateFormatRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _displayTemplate = $v.displayTemplate;
      _example = $v.example;
      _label = $v.label;
      _pattern = $v.pattern;
      _sortOrder = $v.sortOrder;
      _version = $v.version;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(UpdatePlateFormatRequest other) {
    _$v = other as _$UpdatePlateFormatRequest;
  }

  @override
  void update(void Function(UpdatePlateFormatRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  UpdatePlateFormatRequest build() => _build();

  _$UpdatePlateFormatRequest _build() {
    final _$result = _$v ??
        _$UpdatePlateFormatRequest._(
          displayTemplate: BuiltValueNullFieldError.checkNotNull(
              displayTemplate, r'UpdatePlateFormatRequest', 'displayTemplate'),
          example: BuiltValueNullFieldError.checkNotNull(
              example, r'UpdatePlateFormatRequest', 'example'),
          label: BuiltValueNullFieldError.checkNotNull(
              label, r'UpdatePlateFormatRequest', 'label'),
          pattern: BuiltValueNullFieldError.checkNotNull(
              pattern, r'UpdatePlateFormatRequest', 'pattern'),
          sortOrder: BuiltValueNullFieldError.checkNotNull(
              sortOrder, r'UpdatePlateFormatRequest', 'sortOrder'),
          version: BuiltValueNullFieldError.checkNotNull(
              version, r'UpdatePlateFormatRequest', 'version'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
