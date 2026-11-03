// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'product_version_validation_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$ProductVersionValidationResponse
    extends ProductVersionValidationResponse {
  @override
  final BuiltList<RuleError> errors;
  @override
  final bool publishable;

  factory _$ProductVersionValidationResponse(
          [void Function(ProductVersionValidationResponseBuilder)? updates]) =>
      (ProductVersionValidationResponseBuilder()..update(updates))._build();

  _$ProductVersionValidationResponse._(
      {required this.errors, required this.publishable})
      : super._();
  @override
  ProductVersionValidationResponse rebuild(
          void Function(ProductVersionValidationResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  ProductVersionValidationResponseBuilder toBuilder() =>
      ProductVersionValidationResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is ProductVersionValidationResponse &&
        errors == other.errors &&
        publishable == other.publishable;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, errors.hashCode);
    _$hash = $jc(_$hash, publishable.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'ProductVersionValidationResponse')
          ..add('errors', errors)
          ..add('publishable', publishable))
        .toString();
  }
}

class ProductVersionValidationResponseBuilder
    implements
        Builder<ProductVersionValidationResponse,
            ProductVersionValidationResponseBuilder> {
  _$ProductVersionValidationResponse? _$v;

  ListBuilder<RuleError>? _errors;
  ListBuilder<RuleError> get errors =>
      _$this._errors ??= ListBuilder<RuleError>();
  set errors(ListBuilder<RuleError>? errors) => _$this._errors = errors;

  bool? _publishable;
  bool? get publishable => _$this._publishable;
  set publishable(bool? publishable) => _$this._publishable = publishable;

  ProductVersionValidationResponseBuilder() {
    ProductVersionValidationResponse._defaults(this);
  }

  ProductVersionValidationResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _errors = $v.errors.toBuilder();
      _publishable = $v.publishable;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(ProductVersionValidationResponse other) {
    _$v = other as _$ProductVersionValidationResponse;
  }

  @override
  void update(void Function(ProductVersionValidationResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  ProductVersionValidationResponse build() => _build();

  _$ProductVersionValidationResponse _build() {
    _$ProductVersionValidationResponse _$result;
    try {
      _$result = _$v ??
          _$ProductVersionValidationResponse._(
            errors: errors.build(),
            publishable: BuiltValueNullFieldError.checkNotNull(publishable,
                r'ProductVersionValidationResponse', 'publishable'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'errors';
        errors.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'ProductVersionValidationResponse', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
