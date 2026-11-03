// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'update_rating_rules_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$UpdateRatingRulesRequest extends UpdateRatingRulesRequest {
  @override
  final JsonObject rating;
  @override
  final int version;

  factory _$UpdateRatingRulesRequest(
          [void Function(UpdateRatingRulesRequestBuilder)? updates]) =>
      (UpdateRatingRulesRequestBuilder()..update(updates))._build();

  _$UpdateRatingRulesRequest._({required this.rating, required this.version})
      : super._();
  @override
  UpdateRatingRulesRequest rebuild(
          void Function(UpdateRatingRulesRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  UpdateRatingRulesRequestBuilder toBuilder() =>
      UpdateRatingRulesRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is UpdateRatingRulesRequest &&
        rating == other.rating &&
        version == other.version;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, rating.hashCode);
    _$hash = $jc(_$hash, version.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'UpdateRatingRulesRequest')
          ..add('rating', rating)
          ..add('version', version))
        .toString();
  }
}

class UpdateRatingRulesRequestBuilder
    implements
        Builder<UpdateRatingRulesRequest, UpdateRatingRulesRequestBuilder> {
  _$UpdateRatingRulesRequest? _$v;

  JsonObject? _rating;
  JsonObject? get rating => _$this._rating;
  set rating(JsonObject? rating) => _$this._rating = rating;

  int? _version;
  int? get version => _$this._version;
  set version(int? version) => _$this._version = version;

  UpdateRatingRulesRequestBuilder() {
    UpdateRatingRulesRequest._defaults(this);
  }

  UpdateRatingRulesRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _rating = $v.rating;
      _version = $v.version;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(UpdateRatingRulesRequest other) {
    _$v = other as _$UpdateRatingRulesRequest;
  }

  @override
  void update(void Function(UpdateRatingRulesRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  UpdateRatingRulesRequest build() => _build();

  _$UpdateRatingRulesRequest _build() {
    final _$result = _$v ??
        _$UpdateRatingRulesRequest._(
          rating: BuiltValueNullFieldError.checkNotNull(
              rating, r'UpdateRatingRulesRequest', 'rating'),
          version: BuiltValueNullFieldError.checkNotNull(
              version, r'UpdateRatingRulesRequest', 'version'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
