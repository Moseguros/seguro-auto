// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'public_reference_values_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$PublicReferenceValuesResponse extends PublicReferenceValuesResponse {
  @override
  final BuiltList<PublicReferenceValueResponse> values;

  factory _$PublicReferenceValuesResponse(
          [void Function(PublicReferenceValuesResponseBuilder)? updates]) =>
      (PublicReferenceValuesResponseBuilder()..update(updates))._build();

  _$PublicReferenceValuesResponse._({required this.values}) : super._();
  @override
  PublicReferenceValuesResponse rebuild(
          void Function(PublicReferenceValuesResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PublicReferenceValuesResponseBuilder toBuilder() =>
      PublicReferenceValuesResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PublicReferenceValuesResponse && values == other.values;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, values.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'PublicReferenceValuesResponse')
          ..add('values', values))
        .toString();
  }
}

class PublicReferenceValuesResponseBuilder
    implements
        Builder<PublicReferenceValuesResponse,
            PublicReferenceValuesResponseBuilder> {
  _$PublicReferenceValuesResponse? _$v;

  ListBuilder<PublicReferenceValueResponse>? _values;
  ListBuilder<PublicReferenceValueResponse> get values =>
      _$this._values ??= ListBuilder<PublicReferenceValueResponse>();
  set values(ListBuilder<PublicReferenceValueResponse>? values) =>
      _$this._values = values;

  PublicReferenceValuesResponseBuilder() {
    PublicReferenceValuesResponse._defaults(this);
  }

  PublicReferenceValuesResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _values = $v.values.toBuilder();
      _$v = null;
    }
    return this;
  }

  @override
  void replace(PublicReferenceValuesResponse other) {
    _$v = other as _$PublicReferenceValuesResponse;
  }

  @override
  void update(void Function(PublicReferenceValuesResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PublicReferenceValuesResponse build() => _build();

  _$PublicReferenceValuesResponse _build() {
    _$PublicReferenceValuesResponse _$result;
    try {
      _$result = _$v ??
          _$PublicReferenceValuesResponse._(
            values: values.build(),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'values';
        values.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'PublicReferenceValuesResponse', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
