// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'validate_rating_rules_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$ValidateRatingRulesRequest extends ValidateRatingRulesRequest {
  @override
  final JsonObject rating;

  factory _$ValidateRatingRulesRequest(
          [void Function(ValidateRatingRulesRequestBuilder)? updates]) =>
      (ValidateRatingRulesRequestBuilder()..update(updates))._build();

  _$ValidateRatingRulesRequest._({required this.rating}) : super._();
  @override
  ValidateRatingRulesRequest rebuild(
          void Function(ValidateRatingRulesRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  ValidateRatingRulesRequestBuilder toBuilder() =>
      ValidateRatingRulesRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is ValidateRatingRulesRequest && rating == other.rating;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, rating.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'ValidateRatingRulesRequest')
          ..add('rating', rating))
        .toString();
  }
}

class ValidateRatingRulesRequestBuilder
    implements
        Builder<ValidateRatingRulesRequest, ValidateRatingRulesRequestBuilder> {
  _$ValidateRatingRulesRequest? _$v;

  JsonObject? _rating;
  JsonObject? get rating => _$this._rating;
  set rating(JsonObject? rating) => _$this._rating = rating;

  ValidateRatingRulesRequestBuilder() {
    ValidateRatingRulesRequest._defaults(this);
  }

  ValidateRatingRulesRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _rating = $v.rating;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(ValidateRatingRulesRequest other) {
    _$v = other as _$ValidateRatingRulesRequest;
  }

  @override
  void update(void Function(ValidateRatingRulesRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  ValidateRatingRulesRequest build() => _build();

  _$ValidateRatingRulesRequest _build() {
    final _$result = _$v ??
        _$ValidateRatingRulesRequest._(
          rating: BuiltValueNullFieldError.checkNotNull(
              rating, r'ValidateRatingRulesRequest', 'rating'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
