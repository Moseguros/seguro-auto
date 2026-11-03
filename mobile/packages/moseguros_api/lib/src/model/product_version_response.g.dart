// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'product_version_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const ProductVersionResponseProductTypeEnum
    _$productVersionResponseProductTypeEnum_MTPL_ONLY =
    const ProductVersionResponseProductTypeEnum._('MTPL_ONLY');
const ProductVersionResponseProductTypeEnum
    _$productVersionResponseProductTypeEnum_COMPREHENSIVE =
    const ProductVersionResponseProductTypeEnum._('COMPREHENSIVE');
const ProductVersionResponseProductTypeEnum
    _$productVersionResponseProductTypeEnum_TEMPORARY =
    const ProductVersionResponseProductTypeEnum._('TEMPORARY');
const ProductVersionResponseProductTypeEnum
    _$productVersionResponseProductTypeEnum_BORDER =
    const ProductVersionResponseProductTypeEnum._('BORDER');
const ProductVersionResponseProductTypeEnum
    _$productVersionResponseProductTypeEnum_FLEET =
    const ProductVersionResponseProductTypeEnum._('FLEET');

ProductVersionResponseProductTypeEnum
    _$productVersionResponseProductTypeEnumValueOf(String name) {
  switch (name) {
    case 'MTPL_ONLY':
      return _$productVersionResponseProductTypeEnum_MTPL_ONLY;
    case 'COMPREHENSIVE':
      return _$productVersionResponseProductTypeEnum_COMPREHENSIVE;
    case 'TEMPORARY':
      return _$productVersionResponseProductTypeEnum_TEMPORARY;
    case 'BORDER':
      return _$productVersionResponseProductTypeEnum_BORDER;
    case 'FLEET':
      return _$productVersionResponseProductTypeEnum_FLEET;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<ProductVersionResponseProductTypeEnum>
    _$productVersionResponseProductTypeEnumValues = BuiltSet<
        ProductVersionResponseProductTypeEnum>(const <ProductVersionResponseProductTypeEnum>[
  _$productVersionResponseProductTypeEnum_MTPL_ONLY,
  _$productVersionResponseProductTypeEnum_COMPREHENSIVE,
  _$productVersionResponseProductTypeEnum_TEMPORARY,
  _$productVersionResponseProductTypeEnum_BORDER,
  _$productVersionResponseProductTypeEnum_FLEET,
]);

const ProductVersionResponseStatusEnum
    _$productVersionResponseStatusEnum_DRAFT =
    const ProductVersionResponseStatusEnum._('DRAFT');
const ProductVersionResponseStatusEnum
    _$productVersionResponseStatusEnum_PUBLISHED =
    const ProductVersionResponseStatusEnum._('PUBLISHED');
const ProductVersionResponseStatusEnum
    _$productVersionResponseStatusEnum_SUPERSEDED =
    const ProductVersionResponseStatusEnum._('SUPERSEDED');
const ProductVersionResponseStatusEnum
    _$productVersionResponseStatusEnum_RETIRED =
    const ProductVersionResponseStatusEnum._('RETIRED');

ProductVersionResponseStatusEnum _$productVersionResponseStatusEnumValueOf(
    String name) {
  switch (name) {
    case 'DRAFT':
      return _$productVersionResponseStatusEnum_DRAFT;
    case 'PUBLISHED':
      return _$productVersionResponseStatusEnum_PUBLISHED;
    case 'SUPERSEDED':
      return _$productVersionResponseStatusEnum_SUPERSEDED;
    case 'RETIRED':
      return _$productVersionResponseStatusEnum_RETIRED;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<ProductVersionResponseStatusEnum>
    _$productVersionResponseStatusEnumValues = BuiltSet<
        ProductVersionResponseStatusEnum>(const <ProductVersionResponseStatusEnum>[
  _$productVersionResponseStatusEnum_DRAFT,
  _$productVersionResponseStatusEnum_PUBLISHED,
  _$productVersionResponseStatusEnum_SUPERSEDED,
  _$productVersionResponseStatusEnum_RETIRED,
]);

Serializer<ProductVersionResponseProductTypeEnum>
    _$productVersionResponseProductTypeEnumSerializer =
    _$ProductVersionResponseProductTypeEnumSerializer();
Serializer<ProductVersionResponseStatusEnum>
    _$productVersionResponseStatusEnumSerializer =
    _$ProductVersionResponseStatusEnumSerializer();

class _$ProductVersionResponseProductTypeEnumSerializer
    implements PrimitiveSerializer<ProductVersionResponseProductTypeEnum> {
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
  final Iterable<Type> types = const <Type>[
    ProductVersionResponseProductTypeEnum
  ];
  @override
  final String wireName = 'ProductVersionResponseProductTypeEnum';

  @override
  Object serialize(
          Serializers serializers, ProductVersionResponseProductTypeEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  ProductVersionResponseProductTypeEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      ProductVersionResponseProductTypeEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$ProductVersionResponseStatusEnumSerializer
    implements PrimitiveSerializer<ProductVersionResponseStatusEnum> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'DRAFT': 'DRAFT',
    'PUBLISHED': 'PUBLISHED',
    'SUPERSEDED': 'SUPERSEDED',
    'RETIRED': 'RETIRED',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'DRAFT': 'DRAFT',
    'PUBLISHED': 'PUBLISHED',
    'SUPERSEDED': 'SUPERSEDED',
    'RETIRED': 'RETIRED',
  };

  @override
  final Iterable<Type> types = const <Type>[ProductVersionResponseStatusEnum];
  @override
  final String wireName = 'ProductVersionResponseStatusEnum';

  @override
  Object serialize(
          Serializers serializers, ProductVersionResponseStatusEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  ProductVersionResponseStatusEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      ProductVersionResponseStatusEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$ProductVersionResponse extends ProductVersionResponse {
  @override
  final String? basedOnVersionId;
  @override
  final String? changeNotes;
  @override
  final BuiltList<CoverageResponse> coverages;
  @override
  final DateTime createdAt;
  @override
  final BuiltList<ProductDocumentResponse> documents;
  @override
  final bool editable;
  @override
  final String id;
  @override
  final String productCode;
  @override
  final String productId;
  @override
  final String productName;
  @override
  final ProductVersionResponseProductTypeEnum productType;
  @override
  final DateTime? publishedAt;
  @override
  final String? publishedBy;
  @override
  final ProductVersionResponseStatusEnum status;
  @override
  final DateTime updatedAt;
  @override
  final BuiltList<VehicleUsage> usages;
  @override
  final Date? validFrom;
  @override
  final Date? validTo;
  @override
  final BuiltList<VehicleCategory> vehicleCategories;
  @override
  final int version;
  @override
  final int versionNo;

  factory _$ProductVersionResponse(
          [void Function(ProductVersionResponseBuilder)? updates]) =>
      (ProductVersionResponseBuilder()..update(updates))._build();

  _$ProductVersionResponse._(
      {this.basedOnVersionId,
      this.changeNotes,
      required this.coverages,
      required this.createdAt,
      required this.documents,
      required this.editable,
      required this.id,
      required this.productCode,
      required this.productId,
      required this.productName,
      required this.productType,
      this.publishedAt,
      this.publishedBy,
      required this.status,
      required this.updatedAt,
      required this.usages,
      this.validFrom,
      this.validTo,
      required this.vehicleCategories,
      required this.version,
      required this.versionNo})
      : super._();
  @override
  ProductVersionResponse rebuild(
          void Function(ProductVersionResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  ProductVersionResponseBuilder toBuilder() =>
      ProductVersionResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is ProductVersionResponse &&
        basedOnVersionId == other.basedOnVersionId &&
        changeNotes == other.changeNotes &&
        coverages == other.coverages &&
        createdAt == other.createdAt &&
        documents == other.documents &&
        editable == other.editable &&
        id == other.id &&
        productCode == other.productCode &&
        productId == other.productId &&
        productName == other.productName &&
        productType == other.productType &&
        publishedAt == other.publishedAt &&
        publishedBy == other.publishedBy &&
        status == other.status &&
        updatedAt == other.updatedAt &&
        usages == other.usages &&
        validFrom == other.validFrom &&
        validTo == other.validTo &&
        vehicleCategories == other.vehicleCategories &&
        version == other.version &&
        versionNo == other.versionNo;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, basedOnVersionId.hashCode);
    _$hash = $jc(_$hash, changeNotes.hashCode);
    _$hash = $jc(_$hash, coverages.hashCode);
    _$hash = $jc(_$hash, createdAt.hashCode);
    _$hash = $jc(_$hash, documents.hashCode);
    _$hash = $jc(_$hash, editable.hashCode);
    _$hash = $jc(_$hash, id.hashCode);
    _$hash = $jc(_$hash, productCode.hashCode);
    _$hash = $jc(_$hash, productId.hashCode);
    _$hash = $jc(_$hash, productName.hashCode);
    _$hash = $jc(_$hash, productType.hashCode);
    _$hash = $jc(_$hash, publishedAt.hashCode);
    _$hash = $jc(_$hash, publishedBy.hashCode);
    _$hash = $jc(_$hash, status.hashCode);
    _$hash = $jc(_$hash, updatedAt.hashCode);
    _$hash = $jc(_$hash, usages.hashCode);
    _$hash = $jc(_$hash, validFrom.hashCode);
    _$hash = $jc(_$hash, validTo.hashCode);
    _$hash = $jc(_$hash, vehicleCategories.hashCode);
    _$hash = $jc(_$hash, version.hashCode);
    _$hash = $jc(_$hash, versionNo.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'ProductVersionResponse')
          ..add('basedOnVersionId', basedOnVersionId)
          ..add('changeNotes', changeNotes)
          ..add('coverages', coverages)
          ..add('createdAt', createdAt)
          ..add('documents', documents)
          ..add('editable', editable)
          ..add('id', id)
          ..add('productCode', productCode)
          ..add('productId', productId)
          ..add('productName', productName)
          ..add('productType', productType)
          ..add('publishedAt', publishedAt)
          ..add('publishedBy', publishedBy)
          ..add('status', status)
          ..add('updatedAt', updatedAt)
          ..add('usages', usages)
          ..add('validFrom', validFrom)
          ..add('validTo', validTo)
          ..add('vehicleCategories', vehicleCategories)
          ..add('version', version)
          ..add('versionNo', versionNo))
        .toString();
  }
}

class ProductVersionResponseBuilder
    implements Builder<ProductVersionResponse, ProductVersionResponseBuilder> {
  _$ProductVersionResponse? _$v;

  String? _basedOnVersionId;
  String? get basedOnVersionId => _$this._basedOnVersionId;
  set basedOnVersionId(String? basedOnVersionId) =>
      _$this._basedOnVersionId = basedOnVersionId;

  String? _changeNotes;
  String? get changeNotes => _$this._changeNotes;
  set changeNotes(String? changeNotes) => _$this._changeNotes = changeNotes;

  ListBuilder<CoverageResponse>? _coverages;
  ListBuilder<CoverageResponse> get coverages =>
      _$this._coverages ??= ListBuilder<CoverageResponse>();
  set coverages(ListBuilder<CoverageResponse>? coverages) =>
      _$this._coverages = coverages;

  DateTime? _createdAt;
  DateTime? get createdAt => _$this._createdAt;
  set createdAt(DateTime? createdAt) => _$this._createdAt = createdAt;

  ListBuilder<ProductDocumentResponse>? _documents;
  ListBuilder<ProductDocumentResponse> get documents =>
      _$this._documents ??= ListBuilder<ProductDocumentResponse>();
  set documents(ListBuilder<ProductDocumentResponse>? documents) =>
      _$this._documents = documents;

  bool? _editable;
  bool? get editable => _$this._editable;
  set editable(bool? editable) => _$this._editable = editable;

  String? _id;
  String? get id => _$this._id;
  set id(String? id) => _$this._id = id;

  String? _productCode;
  String? get productCode => _$this._productCode;
  set productCode(String? productCode) => _$this._productCode = productCode;

  String? _productId;
  String? get productId => _$this._productId;
  set productId(String? productId) => _$this._productId = productId;

  String? _productName;
  String? get productName => _$this._productName;
  set productName(String? productName) => _$this._productName = productName;

  ProductVersionResponseProductTypeEnum? _productType;
  ProductVersionResponseProductTypeEnum? get productType => _$this._productType;
  set productType(ProductVersionResponseProductTypeEnum? productType) =>
      _$this._productType = productType;

  DateTime? _publishedAt;
  DateTime? get publishedAt => _$this._publishedAt;
  set publishedAt(DateTime? publishedAt) => _$this._publishedAt = publishedAt;

  String? _publishedBy;
  String? get publishedBy => _$this._publishedBy;
  set publishedBy(String? publishedBy) => _$this._publishedBy = publishedBy;

  ProductVersionResponseStatusEnum? _status;
  ProductVersionResponseStatusEnum? get status => _$this._status;
  set status(ProductVersionResponseStatusEnum? status) =>
      _$this._status = status;

  DateTime? _updatedAt;
  DateTime? get updatedAt => _$this._updatedAt;
  set updatedAt(DateTime? updatedAt) => _$this._updatedAt = updatedAt;

  ListBuilder<VehicleUsage>? _usages;
  ListBuilder<VehicleUsage> get usages =>
      _$this._usages ??= ListBuilder<VehicleUsage>();
  set usages(ListBuilder<VehicleUsage>? usages) => _$this._usages = usages;

  Date? _validFrom;
  Date? get validFrom => _$this._validFrom;
  set validFrom(Date? validFrom) => _$this._validFrom = validFrom;

  Date? _validTo;
  Date? get validTo => _$this._validTo;
  set validTo(Date? validTo) => _$this._validTo = validTo;

  ListBuilder<VehicleCategory>? _vehicleCategories;
  ListBuilder<VehicleCategory> get vehicleCategories =>
      _$this._vehicleCategories ??= ListBuilder<VehicleCategory>();
  set vehicleCategories(ListBuilder<VehicleCategory>? vehicleCategories) =>
      _$this._vehicleCategories = vehicleCategories;

  int? _version;
  int? get version => _$this._version;
  set version(int? version) => _$this._version = version;

  int? _versionNo;
  int? get versionNo => _$this._versionNo;
  set versionNo(int? versionNo) => _$this._versionNo = versionNo;

  ProductVersionResponseBuilder() {
    ProductVersionResponse._defaults(this);
  }

  ProductVersionResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _basedOnVersionId = $v.basedOnVersionId;
      _changeNotes = $v.changeNotes;
      _coverages = $v.coverages.toBuilder();
      _createdAt = $v.createdAt;
      _documents = $v.documents.toBuilder();
      _editable = $v.editable;
      _id = $v.id;
      _productCode = $v.productCode;
      _productId = $v.productId;
      _productName = $v.productName;
      _productType = $v.productType;
      _publishedAt = $v.publishedAt;
      _publishedBy = $v.publishedBy;
      _status = $v.status;
      _updatedAt = $v.updatedAt;
      _usages = $v.usages.toBuilder();
      _validFrom = $v.validFrom;
      _validTo = $v.validTo;
      _vehicleCategories = $v.vehicleCategories.toBuilder();
      _version = $v.version;
      _versionNo = $v.versionNo;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(ProductVersionResponse other) {
    _$v = other as _$ProductVersionResponse;
  }

  @override
  void update(void Function(ProductVersionResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  ProductVersionResponse build() => _build();

  _$ProductVersionResponse _build() {
    _$ProductVersionResponse _$result;
    try {
      _$result = _$v ??
          _$ProductVersionResponse._(
            basedOnVersionId: basedOnVersionId,
            changeNotes: changeNotes,
            coverages: coverages.build(),
            createdAt: BuiltValueNullFieldError.checkNotNull(
                createdAt, r'ProductVersionResponse', 'createdAt'),
            documents: documents.build(),
            editable: BuiltValueNullFieldError.checkNotNull(
                editable, r'ProductVersionResponse', 'editable'),
            id: BuiltValueNullFieldError.checkNotNull(
                id, r'ProductVersionResponse', 'id'),
            productCode: BuiltValueNullFieldError.checkNotNull(
                productCode, r'ProductVersionResponse', 'productCode'),
            productId: BuiltValueNullFieldError.checkNotNull(
                productId, r'ProductVersionResponse', 'productId'),
            productName: BuiltValueNullFieldError.checkNotNull(
                productName, r'ProductVersionResponse', 'productName'),
            productType: BuiltValueNullFieldError.checkNotNull(
                productType, r'ProductVersionResponse', 'productType'),
            publishedAt: publishedAt,
            publishedBy: publishedBy,
            status: BuiltValueNullFieldError.checkNotNull(
                status, r'ProductVersionResponse', 'status'),
            updatedAt: BuiltValueNullFieldError.checkNotNull(
                updatedAt, r'ProductVersionResponse', 'updatedAt'),
            usages: usages.build(),
            validFrom: validFrom,
            validTo: validTo,
            vehicleCategories: vehicleCategories.build(),
            version: BuiltValueNullFieldError.checkNotNull(
                version, r'ProductVersionResponse', 'version'),
            versionNo: BuiltValueNullFieldError.checkNotNull(
                versionNo, r'ProductVersionResponse', 'versionNo'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'coverages';
        coverages.build();

        _$failedField = 'documents';
        documents.build();

        _$failedField = 'usages';
        usages.build();

        _$failedField = 'vehicleCategories';
        vehicleCategories.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'ProductVersionResponse', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
