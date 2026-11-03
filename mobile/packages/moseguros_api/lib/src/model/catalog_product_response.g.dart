// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'catalog_product_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const CatalogProductResponseStatusEnum
    _$catalogProductResponseStatusEnum_PUBLISHED =
    const CatalogProductResponseStatusEnum._('PUBLISHED');
const CatalogProductResponseStatusEnum
    _$catalogProductResponseStatusEnum_SUPERSEDED =
    const CatalogProductResponseStatusEnum._('SUPERSEDED');
const CatalogProductResponseStatusEnum
    _$catalogProductResponseStatusEnum_RETIRED =
    const CatalogProductResponseStatusEnum._('RETIRED');

CatalogProductResponseStatusEnum _$catalogProductResponseStatusEnumValueOf(
    String name) {
  switch (name) {
    case 'PUBLISHED':
      return _$catalogProductResponseStatusEnum_PUBLISHED;
    case 'SUPERSEDED':
      return _$catalogProductResponseStatusEnum_SUPERSEDED;
    case 'RETIRED':
      return _$catalogProductResponseStatusEnum_RETIRED;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<CatalogProductResponseStatusEnum>
    _$catalogProductResponseStatusEnumValues = BuiltSet<
        CatalogProductResponseStatusEnum>(const <CatalogProductResponseStatusEnum>[
  _$catalogProductResponseStatusEnum_PUBLISHED,
  _$catalogProductResponseStatusEnum_SUPERSEDED,
  _$catalogProductResponseStatusEnum_RETIRED,
]);

const CatalogProductResponseTypeEnum
    _$catalogProductResponseTypeEnum_MTPL_ONLY =
    const CatalogProductResponseTypeEnum._('MTPL_ONLY');
const CatalogProductResponseTypeEnum
    _$catalogProductResponseTypeEnum_COMPREHENSIVE =
    const CatalogProductResponseTypeEnum._('COMPREHENSIVE');
const CatalogProductResponseTypeEnum
    _$catalogProductResponseTypeEnum_TEMPORARY =
    const CatalogProductResponseTypeEnum._('TEMPORARY');
const CatalogProductResponseTypeEnum _$catalogProductResponseTypeEnum_BORDER =
    const CatalogProductResponseTypeEnum._('BORDER');
const CatalogProductResponseTypeEnum _$catalogProductResponseTypeEnum_FLEET =
    const CatalogProductResponseTypeEnum._('FLEET');

CatalogProductResponseTypeEnum _$catalogProductResponseTypeEnumValueOf(
    String name) {
  switch (name) {
    case 'MTPL_ONLY':
      return _$catalogProductResponseTypeEnum_MTPL_ONLY;
    case 'COMPREHENSIVE':
      return _$catalogProductResponseTypeEnum_COMPREHENSIVE;
    case 'TEMPORARY':
      return _$catalogProductResponseTypeEnum_TEMPORARY;
    case 'BORDER':
      return _$catalogProductResponseTypeEnum_BORDER;
    case 'FLEET':
      return _$catalogProductResponseTypeEnum_FLEET;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<CatalogProductResponseTypeEnum>
    _$catalogProductResponseTypeEnumValues = BuiltSet<
        CatalogProductResponseTypeEnum>(const <CatalogProductResponseTypeEnum>[
  _$catalogProductResponseTypeEnum_MTPL_ONLY,
  _$catalogProductResponseTypeEnum_COMPREHENSIVE,
  _$catalogProductResponseTypeEnum_TEMPORARY,
  _$catalogProductResponseTypeEnum_BORDER,
  _$catalogProductResponseTypeEnum_FLEET,
]);

Serializer<CatalogProductResponseStatusEnum>
    _$catalogProductResponseStatusEnumSerializer =
    _$CatalogProductResponseStatusEnumSerializer();
Serializer<CatalogProductResponseTypeEnum>
    _$catalogProductResponseTypeEnumSerializer =
    _$CatalogProductResponseTypeEnumSerializer();

class _$CatalogProductResponseStatusEnumSerializer
    implements PrimitiveSerializer<CatalogProductResponseStatusEnum> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'PUBLISHED': 'PUBLISHED',
    'SUPERSEDED': 'SUPERSEDED',
    'RETIRED': 'RETIRED',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'PUBLISHED': 'PUBLISHED',
    'SUPERSEDED': 'SUPERSEDED',
    'RETIRED': 'RETIRED',
  };

  @override
  final Iterable<Type> types = const <Type>[CatalogProductResponseStatusEnum];
  @override
  final String wireName = 'CatalogProductResponseStatusEnum';

  @override
  Object serialize(
          Serializers serializers, CatalogProductResponseStatusEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  CatalogProductResponseStatusEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      CatalogProductResponseStatusEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$CatalogProductResponseTypeEnumSerializer
    implements PrimitiveSerializer<CatalogProductResponseTypeEnum> {
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
  final Iterable<Type> types = const <Type>[CatalogProductResponseTypeEnum];
  @override
  final String wireName = 'CatalogProductResponseTypeEnum';

  @override
  Object serialize(
          Serializers serializers, CatalogProductResponseTypeEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  CatalogProductResponseTypeEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      CatalogProductResponseTypeEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$CatalogProductResponse extends CatalogProductResponse {
  @override
  final String code;
  @override
  final BuiltList<CoverageResponse> coverages;
  @override
  final String? description;
  @override
  final BuiltList<ProductDocumentResponse> documents;
  @override
  final CatalogInsurerResponse insurer;
  @override
  final String name;
  @override
  final String productId;
  @override
  final String productVersionId;
  @override
  final CatalogProductResponseStatusEnum status;
  @override
  final CatalogProductResponseTypeEnum type;
  @override
  final BuiltList<VehicleUsage> usages;
  @override
  final Date validFrom;
  @override
  final Date? validTo;
  @override
  final BuiltList<VehicleCategory> vehicleCategories;
  @override
  final int versionNo;

  factory _$CatalogProductResponse(
          [void Function(CatalogProductResponseBuilder)? updates]) =>
      (CatalogProductResponseBuilder()..update(updates))._build();

  _$CatalogProductResponse._(
      {required this.code,
      required this.coverages,
      this.description,
      required this.documents,
      required this.insurer,
      required this.name,
      required this.productId,
      required this.productVersionId,
      required this.status,
      required this.type,
      required this.usages,
      required this.validFrom,
      this.validTo,
      required this.vehicleCategories,
      required this.versionNo})
      : super._();
  @override
  CatalogProductResponse rebuild(
          void Function(CatalogProductResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  CatalogProductResponseBuilder toBuilder() =>
      CatalogProductResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is CatalogProductResponse &&
        code == other.code &&
        coverages == other.coverages &&
        description == other.description &&
        documents == other.documents &&
        insurer == other.insurer &&
        name == other.name &&
        productId == other.productId &&
        productVersionId == other.productVersionId &&
        status == other.status &&
        type == other.type &&
        usages == other.usages &&
        validFrom == other.validFrom &&
        validTo == other.validTo &&
        vehicleCategories == other.vehicleCategories &&
        versionNo == other.versionNo;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, code.hashCode);
    _$hash = $jc(_$hash, coverages.hashCode);
    _$hash = $jc(_$hash, description.hashCode);
    _$hash = $jc(_$hash, documents.hashCode);
    _$hash = $jc(_$hash, insurer.hashCode);
    _$hash = $jc(_$hash, name.hashCode);
    _$hash = $jc(_$hash, productId.hashCode);
    _$hash = $jc(_$hash, productVersionId.hashCode);
    _$hash = $jc(_$hash, status.hashCode);
    _$hash = $jc(_$hash, type.hashCode);
    _$hash = $jc(_$hash, usages.hashCode);
    _$hash = $jc(_$hash, validFrom.hashCode);
    _$hash = $jc(_$hash, validTo.hashCode);
    _$hash = $jc(_$hash, vehicleCategories.hashCode);
    _$hash = $jc(_$hash, versionNo.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'CatalogProductResponse')
          ..add('code', code)
          ..add('coverages', coverages)
          ..add('description', description)
          ..add('documents', documents)
          ..add('insurer', insurer)
          ..add('name', name)
          ..add('productId', productId)
          ..add('productVersionId', productVersionId)
          ..add('status', status)
          ..add('type', type)
          ..add('usages', usages)
          ..add('validFrom', validFrom)
          ..add('validTo', validTo)
          ..add('vehicleCategories', vehicleCategories)
          ..add('versionNo', versionNo))
        .toString();
  }
}

class CatalogProductResponseBuilder
    implements Builder<CatalogProductResponse, CatalogProductResponseBuilder> {
  _$CatalogProductResponse? _$v;

  String? _code;
  String? get code => _$this._code;
  set code(String? code) => _$this._code = code;

  ListBuilder<CoverageResponse>? _coverages;
  ListBuilder<CoverageResponse> get coverages =>
      _$this._coverages ??= ListBuilder<CoverageResponse>();
  set coverages(ListBuilder<CoverageResponse>? coverages) =>
      _$this._coverages = coverages;

  String? _description;
  String? get description => _$this._description;
  set description(String? description) => _$this._description = description;

  ListBuilder<ProductDocumentResponse>? _documents;
  ListBuilder<ProductDocumentResponse> get documents =>
      _$this._documents ??= ListBuilder<ProductDocumentResponse>();
  set documents(ListBuilder<ProductDocumentResponse>? documents) =>
      _$this._documents = documents;

  CatalogInsurerResponseBuilder? _insurer;
  CatalogInsurerResponseBuilder get insurer =>
      _$this._insurer ??= CatalogInsurerResponseBuilder();
  set insurer(CatalogInsurerResponseBuilder? insurer) =>
      _$this._insurer = insurer;

  String? _name;
  String? get name => _$this._name;
  set name(String? name) => _$this._name = name;

  String? _productId;
  String? get productId => _$this._productId;
  set productId(String? productId) => _$this._productId = productId;

  String? _productVersionId;
  String? get productVersionId => _$this._productVersionId;
  set productVersionId(String? productVersionId) =>
      _$this._productVersionId = productVersionId;

  CatalogProductResponseStatusEnum? _status;
  CatalogProductResponseStatusEnum? get status => _$this._status;
  set status(CatalogProductResponseStatusEnum? status) =>
      _$this._status = status;

  CatalogProductResponseTypeEnum? _type;
  CatalogProductResponseTypeEnum? get type => _$this._type;
  set type(CatalogProductResponseTypeEnum? type) => _$this._type = type;

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

  int? _versionNo;
  int? get versionNo => _$this._versionNo;
  set versionNo(int? versionNo) => _$this._versionNo = versionNo;

  CatalogProductResponseBuilder() {
    CatalogProductResponse._defaults(this);
  }

  CatalogProductResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _code = $v.code;
      _coverages = $v.coverages.toBuilder();
      _description = $v.description;
      _documents = $v.documents.toBuilder();
      _insurer = $v.insurer.toBuilder();
      _name = $v.name;
      _productId = $v.productId;
      _productVersionId = $v.productVersionId;
      _status = $v.status;
      _type = $v.type;
      _usages = $v.usages.toBuilder();
      _validFrom = $v.validFrom;
      _validTo = $v.validTo;
      _vehicleCategories = $v.vehicleCategories.toBuilder();
      _versionNo = $v.versionNo;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(CatalogProductResponse other) {
    _$v = other as _$CatalogProductResponse;
  }

  @override
  void update(void Function(CatalogProductResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  CatalogProductResponse build() => _build();

  _$CatalogProductResponse _build() {
    _$CatalogProductResponse _$result;
    try {
      _$result = _$v ??
          _$CatalogProductResponse._(
            code: BuiltValueNullFieldError.checkNotNull(
                code, r'CatalogProductResponse', 'code'),
            coverages: coverages.build(),
            description: description,
            documents: documents.build(),
            insurer: insurer.build(),
            name: BuiltValueNullFieldError.checkNotNull(
                name, r'CatalogProductResponse', 'name'),
            productId: BuiltValueNullFieldError.checkNotNull(
                productId, r'CatalogProductResponse', 'productId'),
            productVersionId: BuiltValueNullFieldError.checkNotNull(
                productVersionId,
                r'CatalogProductResponse',
                'productVersionId'),
            status: BuiltValueNullFieldError.checkNotNull(
                status, r'CatalogProductResponse', 'status'),
            type: BuiltValueNullFieldError.checkNotNull(
                type, r'CatalogProductResponse', 'type'),
            usages: usages.build(),
            validFrom: BuiltValueNullFieldError.checkNotNull(
                validFrom, r'CatalogProductResponse', 'validFrom'),
            validTo: validTo,
            vehicleCategories: vehicleCategories.build(),
            versionNo: BuiltValueNullFieldError.checkNotNull(
                versionNo, r'CatalogProductResponse', 'versionNo'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'coverages';
        coverages.build();

        _$failedField = 'documents';
        documents.build();
        _$failedField = 'insurer';
        insurer.build();

        _$failedField = 'usages';
        usages.build();

        _$failedField = 'vehicleCategories';
        vehicleCategories.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'CatalogProductResponse', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
