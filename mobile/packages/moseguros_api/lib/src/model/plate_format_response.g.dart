// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'plate_format_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$PlateFormatResponse extends PlateFormatResponse {
  @override
  final bool active;
  @override
  final String code;
  @override
  final String displayTemplate;
  @override
  final String example;
  @override
  final String id;
  @override
  final String label;
  @override
  final String pattern;
  @override
  final int sortOrder;
  @override
  final int version;

  factory _$PlateFormatResponse(
          [void Function(PlateFormatResponseBuilder)? updates]) =>
      (PlateFormatResponseBuilder()..update(updates))._build();

  _$PlateFormatResponse._(
      {required this.active,
      required this.code,
      required this.displayTemplate,
      required this.example,
      required this.id,
      required this.label,
      required this.pattern,
      required this.sortOrder,
      required this.version})
      : super._();
  @override
  PlateFormatResponse rebuild(
          void Function(PlateFormatResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PlateFormatResponseBuilder toBuilder() =>
      PlateFormatResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PlateFormatResponse &&
        active == other.active &&
        code == other.code &&
        displayTemplate == other.displayTemplate &&
        example == other.example &&
        id == other.id &&
        label == other.label &&
        pattern == other.pattern &&
        sortOrder == other.sortOrder &&
        version == other.version;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, active.hashCode);
    _$hash = $jc(_$hash, code.hashCode);
    _$hash = $jc(_$hash, displayTemplate.hashCode);
    _$hash = $jc(_$hash, example.hashCode);
    _$hash = $jc(_$hash, id.hashCode);
    _$hash = $jc(_$hash, label.hashCode);
    _$hash = $jc(_$hash, pattern.hashCode);
    _$hash = $jc(_$hash, sortOrder.hashCode);
    _$hash = $jc(_$hash, version.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'PlateFormatResponse')
          ..add('active', active)
          ..add('code', code)
          ..add('displayTemplate', displayTemplate)
          ..add('example', example)
          ..add('id', id)
          ..add('label', label)
          ..add('pattern', pattern)
          ..add('sortOrder', sortOrder)
          ..add('version', version))
        .toString();
  }
}

class PlateFormatResponseBuilder
    implements Builder<PlateFormatResponse, PlateFormatResponseBuilder> {
  _$PlateFormatResponse? _$v;

  bool? _active;
  bool? get active => _$this._active;
  set active(bool? active) => _$this._active = active;

  String? _code;
  String? get code => _$this._code;
  set code(String? code) => _$this._code = code;

  String? _displayTemplate;
  String? get displayTemplate => _$this._displayTemplate;
  set displayTemplate(String? displayTemplate) =>
      _$this._displayTemplate = displayTemplate;

  String? _example;
  String? get example => _$this._example;
  set example(String? example) => _$this._example = example;

  String? _id;
  String? get id => _$this._id;
  set id(String? id) => _$this._id = id;

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

  PlateFormatResponseBuilder() {
    PlateFormatResponse._defaults(this);
  }

  PlateFormatResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _active = $v.active;
      _code = $v.code;
      _displayTemplate = $v.displayTemplate;
      _example = $v.example;
      _id = $v.id;
      _label = $v.label;
      _pattern = $v.pattern;
      _sortOrder = $v.sortOrder;
      _version = $v.version;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(PlateFormatResponse other) {
    _$v = other as _$PlateFormatResponse;
  }

  @override
  void update(void Function(PlateFormatResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PlateFormatResponse build() => _build();

  _$PlateFormatResponse _build() {
    final _$result = _$v ??
        _$PlateFormatResponse._(
          active: BuiltValueNullFieldError.checkNotNull(
              active, r'PlateFormatResponse', 'active'),
          code: BuiltValueNullFieldError.checkNotNull(
              code, r'PlateFormatResponse', 'code'),
          displayTemplate: BuiltValueNullFieldError.checkNotNull(
              displayTemplate, r'PlateFormatResponse', 'displayTemplate'),
          example: BuiltValueNullFieldError.checkNotNull(
              example, r'PlateFormatResponse', 'example'),
          id: BuiltValueNullFieldError.checkNotNull(
              id, r'PlateFormatResponse', 'id'),
          label: BuiltValueNullFieldError.checkNotNull(
              label, r'PlateFormatResponse', 'label'),
          pattern: BuiltValueNullFieldError.checkNotNull(
              pattern, r'PlateFormatResponse', 'pattern'),
          sortOrder: BuiltValueNullFieldError.checkNotNull(
              sortOrder, r'PlateFormatResponse', 'sortOrder'),
          version: BuiltValueNullFieldError.checkNotNull(
              version, r'PlateFormatResponse', 'version'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
