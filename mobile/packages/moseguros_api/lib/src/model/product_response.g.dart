// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'product_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const ProductResponseStatusEnum _$productResponseStatusEnum_DRAFT =
    const ProductResponseStatusEnum._('DRAFT');
const ProductResponseStatusEnum _$productResponseStatusEnum_PUBLISHED =
    const ProductResponseStatusEnum._('PUBLISHED');
const ProductResponseStatusEnum _$productResponseStatusEnum_RETIRED =
    const ProductResponseStatusEnum._('RETIRED');

ProductResponseStatusEnum _$productResponseStatusEnumValueOf(String name) {
  switch (name) {
    case 'DRAFT':
      return _$productResponseStatusEnum_DRAFT;
    case 'PUBLISHED':
      return _$productResponseStatusEnum_PUBLISHED;
    case 'RETIRED':
      return _$productResponseStatusEnum_RETIRED;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<ProductResponseStatusEnum> _$productResponseStatusEnumValues =
    BuiltSet<ProductResponseStatusEnum>(const <ProductResponseStatusEnum>[
  _$productResponseStatusEnum_DRAFT,
  _$productResponseStatusEnum_PUBLISHED,
  _$productResponseStatusEnum_RETIRED,
]);

const ProductResponseTypeEnum _$productResponseTypeEnum_MTPL_ONLY =
    const ProductResponseTypeEnum._('MTPL_ONLY');
const ProductResponseTypeEnum _$productResponseTypeEnum_COMPREHENSIVE =
    const ProductResponseTypeEnum._('COMPREHENSIVE');
const ProductResponseTypeEnum _$productResponseTypeEnum_TEMPORARY =
    const ProductResponseTypeEnum._('TEMPORARY');
const ProductResponseTypeEnum _$productResponseTypeEnum_BORDER =
    const ProductResponseTypeEnum._('BORDER');
const ProductResponseTypeEnum _$productResponseTypeEnum_FLEET =
    const ProductResponseTypeEnum._('FLEET');

ProductResponseTypeEnum _$productResponseTypeEnumValueOf(String name) {
  switch (name) {
    case 'MTPL_ONLY':
      return _$productResponseTypeEnum_MTPL_ONLY;
    case 'COMPREHENSIVE':
      return _$productResponseTypeEnum_COMPREHENSIVE;
    case 'TEMPORARY':
      return _$productResponseTypeEnum_TEMPORARY;
    case 'BORDER':
      return _$productResponseTypeEnum_BORDER;
    case 'FLEET':
      return _$productResponseTypeEnum_FLEET;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<ProductResponseTypeEnum> _$productResponseTypeEnumValues =
    BuiltSet<ProductResponseTypeEnum>(const <ProductResponseTypeEnum>[
  _$productResponseTypeEnum_MTPL_ONLY,
  _$productResponseTypeEnum_COMPREHENSIVE,
  _$productResponseTypeEnum_TEMPORARY,
  _$productResponseTypeEnum_BORDER,
  _$productResponseTypeEnum_FLEET,
]);

Serializer<ProductResponseStatusEnum> _$productResponseStatusEnumSerializer =
    _$ProductResponseStatusEnumSerializer();
Serializer<ProductResponseTypeEnum> _$productResponseTypeEnumSerializer =
    _$ProductResponseTypeEnumSerializer();

class _$ProductResponseStatusEnumSerializer
    implements PrimitiveSerializer<ProductResponseStatusEnum> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'DRAFT': 'DRAFT',
    'PUBLISHED': 'PUBLISHED',
    'RETIRED': 'RETIRED',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'DRAFT': 'DRAFT',
    'PUBLISHED': 'PUBLISHED',
    'RETIRED': 'RETIRED',
  };

  @override
  final Iterable<Type> types = const <Type>[ProductResponseStatusEnum];
  @override
  final String wireName = 'ProductResponseStatusEnum';

  @override
  Object serialize(Serializers serializers, ProductResponseStatusEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  ProductResponseStatusEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      ProductResponseStatusEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$ProductResponseTypeEnumSerializer
    implements PrimitiveSerializer<ProductResponseTypeEnum> {
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
  final Iterable<Type> types = const <Type>[ProductResponseTypeEnum];
  @override
  final String wireName = 'ProductResponseTypeEnum';

  @override
  Object serialize(Serializers serializers, ProductResponseTypeEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  ProductResponseTypeEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      ProductResponseTypeEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$ProductResponse extends ProductResponse {
  @override
  final String code;
  @override
  final DateTime createdAt;
  @override
  final ProductVersionRefResponse? currentVersion;
  @override
  final String? description;
  @override
  final ProductVersionRefResponse? draftVersion;
  @override
  final String id;
  @override
  final String name;
  @override
  final DateTime? retiredAt;
  @override
  final ProductResponseStatusEnum status;
  @override
  final ProductResponseTypeEnum type;
  @override
  final DateTime updatedAt;
  @override
  final int version;
  @override
  final BuiltList<ProductVersionRefResponse> versions;

  factory _$ProductResponse([void Function(ProductResponseBuilder)? updates]) =>
      (ProductResponseBuilder()..update(updates))._build();

  _$ProductResponse._(
      {required this.code,
      required this.createdAt,
      this.currentVersion,
      this.description,
      this.draftVersion,
      required this.id,
      required this.name,
      this.retiredAt,
      required this.status,
      required this.type,
      required this.updatedAt,
      required this.version,
      required this.versions})
      : super._();
  @override
  ProductResponse rebuild(void Function(ProductResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  ProductResponseBuilder toBuilder() => ProductResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is ProductResponse &&
        code == other.code &&
        createdAt == other.createdAt &&
        currentVersion == other.currentVersion &&
        description == other.description &&
        draftVersion == other.draftVersion &&
        id == other.id &&
        name == other.name &&
        retiredAt == other.retiredAt &&
        status == other.status &&
        type == other.type &&
        updatedAt == other.updatedAt &&
        version == other.version &&
        versions == other.versions;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, code.hashCode);
    _$hash = $jc(_$hash, createdAt.hashCode);
    _$hash = $jc(_$hash, currentVersion.hashCode);
    _$hash = $jc(_$hash, description.hashCode);
    _$hash = $jc(_$hash, draftVersion.hashCode);
    _$hash = $jc(_$hash, id.hashCode);
    _$hash = $jc(_$hash, name.hashCode);
    _$hash = $jc(_$hash, retiredAt.hashCode);
    _$hash = $jc(_$hash, status.hashCode);
    _$hash = $jc(_$hash, type.hashCode);
    _$hash = $jc(_$hash, updatedAt.hashCode);
    _$hash = $jc(_$hash, version.hashCode);
    _$hash = $jc(_$hash, versions.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'ProductResponse')
          ..add('code', code)
          ..add('createdAt', createdAt)
          ..add('currentVersion', currentVersion)
          ..add('description', description)
          ..add('draftVersion', draftVersion)
          ..add('id', id)
          ..add('name', name)
          ..add('retiredAt', retiredAt)
          ..add('status', status)
          ..add('type', type)
          ..add('updatedAt', updatedAt)
          ..add('version', version)
          ..add('versions', versions))
        .toString();
  }
}

class ProductResponseBuilder
    implements Builder<ProductResponse, ProductResponseBuilder> {
  _$ProductResponse? _$v;

  String? _code;
  String? get code => _$this._code;
  set code(String? code) => _$this._code = code;

  DateTime? _createdAt;
  DateTime? get createdAt => _$this._createdAt;
  set createdAt(DateTime? createdAt) => _$this._createdAt = createdAt;

  ProductVersionRefResponseBuilder? _currentVersion;
  ProductVersionRefResponseBuilder get currentVersion =>
      _$this._currentVersion ??= ProductVersionRefResponseBuilder();
  set currentVersion(ProductVersionRefResponseBuilder? currentVersion) =>
      _$this._currentVersion = currentVersion;

  String? _description;
  String? get description => _$this._description;
  set description(String? description) => _$this._description = description;

  ProductVersionRefResponseBuilder? _draftVersion;
  ProductVersionRefResponseBuilder get draftVersion =>
      _$this._draftVersion ??= ProductVersionRefResponseBuilder();
  set draftVersion(ProductVersionRefResponseBuilder? draftVersion) =>
      _$this._draftVersion = draftVersion;

  String? _id;
  String? get id => _$this._id;
  set id(String? id) => _$this._id = id;

  String? _name;
  String? get name => _$this._name;
  set name(String? name) => _$this._name = name;

  DateTime? _retiredAt;
  DateTime? get retiredAt => _$this._retiredAt;
  set retiredAt(DateTime? retiredAt) => _$this._retiredAt = retiredAt;

  ProductResponseStatusEnum? _status;
  ProductResponseStatusEnum? get status => _$this._status;
  set status(ProductResponseStatusEnum? status) => _$this._status = status;

  ProductResponseTypeEnum? _type;
  ProductResponseTypeEnum? get type => _$this._type;
  set type(ProductResponseTypeEnum? type) => _$this._type = type;

  DateTime? _updatedAt;
  DateTime? get updatedAt => _$this._updatedAt;
  set updatedAt(DateTime? updatedAt) => _$this._updatedAt = updatedAt;

  int? _version;
  int? get version => _$this._version;
  set version(int? version) => _$this._version = version;

  ListBuilder<ProductVersionRefResponse>? _versions;
  ListBuilder<ProductVersionRefResponse> get versions =>
      _$this._versions ??= ListBuilder<ProductVersionRefResponse>();
  set versions(ListBuilder<ProductVersionRefResponse>? versions) =>
      _$this._versions = versions;

  ProductResponseBuilder() {
    ProductResponse._defaults(this);
  }

  ProductResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _code = $v.code;
      _createdAt = $v.createdAt;
      _currentVersion = $v.currentVersion?.toBuilder();
      _description = $v.description;
      _draftVersion = $v.draftVersion?.toBuilder();
      _id = $v.id;
      _name = $v.name;
      _retiredAt = $v.retiredAt;
      _status = $v.status;
      _type = $v.type;
      _updatedAt = $v.updatedAt;
      _version = $v.version;
      _versions = $v.versions.toBuilder();
      _$v = null;
    }
    return this;
  }

  @override
  void replace(ProductResponse other) {
    _$v = other as _$ProductResponse;
  }

  @override
  void update(void Function(ProductResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  ProductResponse build() => _build();

  _$ProductResponse _build() {
    _$ProductResponse _$result;
    try {
      _$result = _$v ??
          _$ProductResponse._(
            code: BuiltValueNullFieldError.checkNotNull(
                code, r'ProductResponse', 'code'),
            createdAt: BuiltValueNullFieldError.checkNotNull(
                createdAt, r'ProductResponse', 'createdAt'),
            currentVersion: _currentVersion?.build(),
            description: description,
            draftVersion: _draftVersion?.build(),
            id: BuiltValueNullFieldError.checkNotNull(
                id, r'ProductResponse', 'id'),
            name: BuiltValueNullFieldError.checkNotNull(
                name, r'ProductResponse', 'name'),
            retiredAt: retiredAt,
            status: BuiltValueNullFieldError.checkNotNull(
                status, r'ProductResponse', 'status'),
            type: BuiltValueNullFieldError.checkNotNull(
                type, r'ProductResponse', 'type'),
            updatedAt: BuiltValueNullFieldError.checkNotNull(
                updatedAt, r'ProductResponse', 'updatedAt'),
            version: BuiltValueNullFieldError.checkNotNull(
                version, r'ProductResponse', 'version'),
            versions: versions.build(),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'currentVersion';
        _currentVersion?.build();

        _$failedField = 'draftVersion';
        _draftVersion?.build();

        _$failedField = 'versions';
        versions.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'ProductResponse', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
