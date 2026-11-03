// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'plate_formats_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$PlateFormatsResponse extends PlateFormatsResponse {
  @override
  final BuiltList<PlateFormatResponse> formats;

  factory _$PlateFormatsResponse(
          [void Function(PlateFormatsResponseBuilder)? updates]) =>
      (PlateFormatsResponseBuilder()..update(updates))._build();

  _$PlateFormatsResponse._({required this.formats}) : super._();
  @override
  PlateFormatsResponse rebuild(
          void Function(PlateFormatsResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PlateFormatsResponseBuilder toBuilder() =>
      PlateFormatsResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PlateFormatsResponse && formats == other.formats;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, formats.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'PlateFormatsResponse')
          ..add('formats', formats))
        .toString();
  }
}

class PlateFormatsResponseBuilder
    implements Builder<PlateFormatsResponse, PlateFormatsResponseBuilder> {
  _$PlateFormatsResponse? _$v;

  ListBuilder<PlateFormatResponse>? _formats;
  ListBuilder<PlateFormatResponse> get formats =>
      _$this._formats ??= ListBuilder<PlateFormatResponse>();
  set formats(ListBuilder<PlateFormatResponse>? formats) =>
      _$this._formats = formats;

  PlateFormatsResponseBuilder() {
    PlateFormatsResponse._defaults(this);
  }

  PlateFormatsResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _formats = $v.formats.toBuilder();
      _$v = null;
    }
    return this;
  }

  @override
  void replace(PlateFormatsResponse other) {
    _$v = other as _$PlateFormatsResponse;
  }

  @override
  void update(void Function(PlateFormatsResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PlateFormatsResponse build() => _build();

  _$PlateFormatsResponse _build() {
    _$PlateFormatsResponse _$result;
    try {
      _$result = _$v ??
          _$PlateFormatsResponse._(
            formats: formats.build(),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'formats';
        formats.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'PlateFormatsResponse', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
