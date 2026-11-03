// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'create_plate_format_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$CreatePlateFormatRequest extends CreatePlateFormatRequest {
  @override
  final String code;
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

  factory _$CreatePlateFormatRequest(
          [void Function(CreatePlateFormatRequestBuilder)? updates]) =>
      (CreatePlateFormatRequestBuilder()..update(updates))._build();

  _$CreatePlateFormatRequest._(
      {required this.code,
      required this.displayTemplate,
      required this.example,
      required this.label,
      required this.pattern,
      required this.sortOrder})
      : super._();
  @override
  CreatePlateFormatRequest rebuild(
          void Function(CreatePlateFormatRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  CreatePlateFormatRequestBuilder toBuilder() =>
      CreatePlateFormatRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is CreatePlateFormatRequest &&
        code == other.code &&
        displayTemplate == other.displayTemplate &&
        example == other.example &&
        label == other.label &&
        pattern == other.pattern &&
        sortOrder == other.sortOrder;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, code.hashCode);
    _$hash = $jc(_$hash, displayTemplate.hashCode);
    _$hash = $jc(_$hash, example.hashCode);
    _$hash = $jc(_$hash, label.hashCode);
    _$hash = $jc(_$hash, pattern.hashCode);
    _$hash = $jc(_$hash, sortOrder.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'CreatePlateFormatRequest')
          ..add('code', code)
          ..add('displayTemplate', displayTemplate)
          ..add('example', example)
          ..add('label', label)
          ..add('pattern', pattern)
          ..add('sortOrder', sortOrder))
        .toString();
  }
}

class CreatePlateFormatRequestBuilder
    implements
        Builder<CreatePlateFormatRequest, CreatePlateFormatRequestBuilder> {
  _$CreatePlateFormatRequest? _$v;

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

  String? _label;
  String? get label => _$this._label;
  set label(String? label) => _$this._label = label;

  String? _pattern;
  String? get pattern => _$this._pattern;
  set pattern(String? pattern) => _$this._pattern = pattern;

  int? _sortOrder;
  int? get sortOrder => _$this._sortOrder;
  set sortOrder(int? sortOrder) => _$this._sortOrder = sortOrder;

  CreatePlateFormatRequestBuilder() {
    CreatePlateFormatRequest._defaults(this);
  }

  CreatePlateFormatRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _code = $v.code;
      _displayTemplate = $v.displayTemplate;
      _example = $v.example;
      _label = $v.label;
      _pattern = $v.pattern;
      _sortOrder = $v.sortOrder;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(CreatePlateFormatRequest other) {
    _$v = other as _$CreatePlateFormatRequest;
  }

  @override
  void update(void Function(CreatePlateFormatRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  CreatePlateFormatRequest build() => _build();

  _$CreatePlateFormatRequest _build() {
    final _$result = _$v ??
        _$CreatePlateFormatRequest._(
          code: BuiltValueNullFieldError.checkNotNull(
              code, r'CreatePlateFormatRequest', 'code'),
          displayTemplate: BuiltValueNullFieldError.checkNotNull(
              displayTemplate, r'CreatePlateFormatRequest', 'displayTemplate'),
          example: BuiltValueNullFieldError.checkNotNull(
              example, r'CreatePlateFormatRequest', 'example'),
          label: BuiltValueNullFieldError.checkNotNull(
              label, r'CreatePlateFormatRequest', 'label'),
          pattern: BuiltValueNullFieldError.checkNotNull(
              pattern, r'CreatePlateFormatRequest', 'pattern'),
          sortOrder: BuiltValueNullFieldError.checkNotNull(
              sortOrder, r'CreatePlateFormatRequest', 'sortOrder'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
