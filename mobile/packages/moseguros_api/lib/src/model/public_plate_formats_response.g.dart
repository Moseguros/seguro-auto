// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'public_plate_formats_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$PublicPlateFormatsResponse extends PublicPlateFormatsResponse {
  @override
  final BuiltList<PublicPlateFormatResponse> formats;

  factory _$PublicPlateFormatsResponse(
          [void Function(PublicPlateFormatsResponseBuilder)? updates]) =>
      (PublicPlateFormatsResponseBuilder()..update(updates))._build();

  _$PublicPlateFormatsResponse._({required this.formats}) : super._();
  @override
  PublicPlateFormatsResponse rebuild(
          void Function(PublicPlateFormatsResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PublicPlateFormatsResponseBuilder toBuilder() =>
      PublicPlateFormatsResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PublicPlateFormatsResponse && formats == other.formats;
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
    return (newBuiltValueToStringHelper(r'PublicPlateFormatsResponse')
          ..add('formats', formats))
        .toString();
  }
}

class PublicPlateFormatsResponseBuilder
    implements
        Builder<PublicPlateFormatsResponse, PublicPlateFormatsResponseBuilder> {
  _$PublicPlateFormatsResponse? _$v;

  ListBuilder<PublicPlateFormatResponse>? _formats;
  ListBuilder<PublicPlateFormatResponse> get formats =>
      _$this._formats ??= ListBuilder<PublicPlateFormatResponse>();
  set formats(ListBuilder<PublicPlateFormatResponse>? formats) =>
      _$this._formats = formats;

  PublicPlateFormatsResponseBuilder() {
    PublicPlateFormatsResponse._defaults(this);
  }

  PublicPlateFormatsResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _formats = $v.formats.toBuilder();
      _$v = null;
    }
    return this;
  }

  @override
  void replace(PublicPlateFormatsResponse other) {
    _$v = other as _$PublicPlateFormatsResponse;
  }

  @override
  void update(void Function(PublicPlateFormatsResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PublicPlateFormatsResponse build() => _build();

  _$PublicPlateFormatsResponse _build() {
    _$PublicPlateFormatsResponse _$result;
    try {
      _$result = _$v ??
          _$PublicPlateFormatsResponse._(
            formats: formats.build(),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'formats';
        formats.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'PublicPlateFormatsResponse', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
