// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'rating_rules_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const RatingRulesResponseProductTypeEnum
    _$ratingRulesResponseProductTypeEnum_MTPL_ONLY =
    const RatingRulesResponseProductTypeEnum._('MTPL_ONLY');
const RatingRulesResponseProductTypeEnum
    _$ratingRulesResponseProductTypeEnum_COMPREHENSIVE =
    const RatingRulesResponseProductTypeEnum._('COMPREHENSIVE');
const RatingRulesResponseProductTypeEnum
    _$ratingRulesResponseProductTypeEnum_TEMPORARY =
    const RatingRulesResponseProductTypeEnum._('TEMPORARY');
const RatingRulesResponseProductTypeEnum
    _$ratingRulesResponseProductTypeEnum_BORDER =
    const RatingRulesResponseProductTypeEnum._('BORDER');
const RatingRulesResponseProductTypeEnum
    _$ratingRulesResponseProductTypeEnum_FLEET =
    const RatingRulesResponseProductTypeEnum._('FLEET');

RatingRulesResponseProductTypeEnum _$ratingRulesResponseProductTypeEnumValueOf(
    String name) {
  switch (name) {
    case 'MTPL_ONLY':
      return _$ratingRulesResponseProductTypeEnum_MTPL_ONLY;
    case 'COMPREHENSIVE':
      return _$ratingRulesResponseProductTypeEnum_COMPREHENSIVE;
    case 'TEMPORARY':
      return _$ratingRulesResponseProductTypeEnum_TEMPORARY;
    case 'BORDER':
      return _$ratingRulesResponseProductTypeEnum_BORDER;
    case 'FLEET':
      return _$ratingRulesResponseProductTypeEnum_FLEET;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<RatingRulesResponseProductTypeEnum>
    _$ratingRulesResponseProductTypeEnumValues = BuiltSet<
        RatingRulesResponseProductTypeEnum>(const <RatingRulesResponseProductTypeEnum>[
  _$ratingRulesResponseProductTypeEnum_MTPL_ONLY,
  _$ratingRulesResponseProductTypeEnum_COMPREHENSIVE,
  _$ratingRulesResponseProductTypeEnum_TEMPORARY,
  _$ratingRulesResponseProductTypeEnum_BORDER,
  _$ratingRulesResponseProductTypeEnum_FLEET,
]);

Serializer<RatingRulesResponseProductTypeEnum>
    _$ratingRulesResponseProductTypeEnumSerializer =
    _$RatingRulesResponseProductTypeEnumSerializer();

class _$RatingRulesResponseProductTypeEnumSerializer
    implements PrimitiveSerializer<RatingRulesResponseProductTypeEnum> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'MTPL_ONLY': 'MTPL_ONLY',
    'COMPREHENSIVE': 'COMPREHENSIVE',
    'TEMPORARY': 'TEMPORARY',
    'BORDER': 'BORDER',
    'FLEET': 'FLEET',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'MTPL_ONLY': 'MTPL_ONLY',
    'COMPREHENSIVE': 'COMPREHENSIVE',
    'TEMPORARY': 'TEMPORARY',
    'BORDER': 'BORDER',
    'FLEET': 'FLEET',
  };

  @override
  final Iterable<Type> types = const <Type>[RatingRulesResponseProductTypeEnum];
  @override
  final String wireName = 'RatingRulesResponseProductTypeEnum';

  @override
  Object serialize(
          Serializers serializers, RatingRulesResponseProductTypeEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  RatingRulesResponseProductTypeEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      RatingRulesResponseProductTypeEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$RatingRulesResponse extends RatingRulesResponse {
  @override
  final bool configured;
  @override
  final BuiltList<RatingCoverage> coverages;
  @override
  final bool editable;
  @override
  final RatingRulesResponseProductTypeEnum productType;
  @override
  final String productVersionId;
  @override
  final JsonObject? rating;
  @override
  final int version;

  factory _$RatingRulesResponse(
          [void Function(RatingRulesResponseBuilder)? updates]) =>
      (RatingRulesResponseBuilder()..update(updates))._build();

  _$RatingRulesResponse._(
      {required this.configured,
      required this.coverages,
      required this.editable,
      required this.productType,
      required this.productVersionId,
      this.rating,
      required this.version})
      : super._();
  @override
  RatingRulesResponse rebuild(
          void Function(RatingRulesResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  RatingRulesResponseBuilder toBuilder() =>
      RatingRulesResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is RatingRulesResponse &&
        configured == other.configured &&
        coverages == other.coverages &&
        editable == other.editable &&
        productType == other.productType &&
        productVersionId == other.productVersionId &&
        rating == other.rating &&
        version == other.version;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, configured.hashCode);
    _$hash = $jc(_$hash, coverages.hashCode);
    _$hash = $jc(_$hash, editable.hashCode);
    _$hash = $jc(_$hash, productType.hashCode);
    _$hash = $jc(_$hash, productVersionId.hashCode);
    _$hash = $jc(_$hash, rating.hashCode);
    _$hash = $jc(_$hash, version.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'RatingRulesResponse')
          ..add('configured', configured)
          ..add('coverages', coverages)
          ..add('editable', editable)
          ..add('productType', productType)
          ..add('productVersionId', productVersionId)
          ..add('rating', rating)
          ..add('version', version))
        .toString();
  }
}

class RatingRulesResponseBuilder
    implements Builder<RatingRulesResponse, RatingRulesResponseBuilder> {
  _$RatingRulesResponse? _$v;

  bool? _configured;
  bool? get configured => _$this._configured;
  set configured(bool? configured) => _$this._configured = configured;

  ListBuilder<RatingCoverage>? _coverages;
  ListBuilder<RatingCoverage> get coverages =>
      _$this._coverages ??= ListBuilder<RatingCoverage>();
  set coverages(ListBuilder<RatingCoverage>? coverages) =>
      _$this._coverages = coverages;

  bool? _editable;
  bool? get editable => _$this._editable;
  set editable(bool? editable) => _$this._editable = editable;

  RatingRulesResponseProductTypeEnum? _productType;
  RatingRulesResponseProductTypeEnum? get productType => _$this._productType;
  set productType(RatingRulesResponseProductTypeEnum? productType) =>
      _$this._productType = productType;

  String? _productVersionId;
  String? get productVersionId => _$this._productVersionId;
  set productVersionId(String? productVersionId) =>
      _$this._productVersionId = productVersionId;

  JsonObject? _rating;
  JsonObject? get rating => _$this._rating;
  set rating(JsonObject? rating) => _$this._rating = rating;

  int? _version;
  int? get version => _$this._version;
  set version(int? version) => _$this._version = version;

  RatingRulesResponseBuilder() {
    RatingRulesResponse._defaults(this);
  }

  RatingRulesResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _configured = $v.configured;
      _coverages = $v.coverages.toBuilder();
      _editable = $v.editable;
      _productType = $v.productType;
      _productVersionId = $v.productVersionId;
      _rating = $v.rating;
      _version = $v.version;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(RatingRulesResponse other) {
    _$v = other as _$RatingRulesResponse;
  }

  @override
  void update(void Function(RatingRulesResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  RatingRulesResponse build() => _build();

  _$RatingRulesResponse _build() {
    _$RatingRulesResponse _$result;
    try {
      _$result = _$v ??
          _$RatingRulesResponse._(
            configured: BuiltValueNullFieldError.checkNotNull(
                configured, r'RatingRulesResponse', 'configured'),
            coverages: coverages.build(),
            editable: BuiltValueNullFieldError.checkNotNull(
                editable, r'RatingRulesResponse', 'editable'),
            productType: BuiltValueNullFieldError.checkNotNull(
                productType, r'RatingRulesResponse', 'productType'),
            productVersionId: BuiltValueNullFieldError.checkNotNull(
                productVersionId, r'RatingRulesResponse', 'productVersionId'),
            rating: rating,
            version: BuiltValueNullFieldError.checkNotNull(
                version, r'RatingRulesResponse', 'version'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'coverages';
        coverages.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'RatingRulesResponse', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
