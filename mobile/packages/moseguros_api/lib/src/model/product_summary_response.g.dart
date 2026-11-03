// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'product_summary_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const ProductSummaryResponseStatusEnum
    _$productSummaryResponseStatusEnum_DRAFT =
    const ProductSummaryResponseStatusEnum._('DRAFT');
const ProductSummaryResponseStatusEnum
    _$productSummaryResponseStatusEnum_PUBLISHED =
    const ProductSummaryResponseStatusEnum._('PUBLISHED');
const ProductSummaryResponseStatusEnum
    _$productSummaryResponseStatusEnum_RETIRED =
    const ProductSummaryResponseStatusEnum._('RETIRED');

ProductSummaryResponseStatusEnum _$productSummaryResponseStatusEnumValueOf(
    String name) {
  switch (name) {
    case 'DRAFT':
      return _$productSummaryResponseStatusEnum_DRAFT;
    case 'PUBLISHED':
      return _$productSummaryResponseStatusEnum_PUBLISHED;
    case 'RETIRED':
      return _$productSummaryResponseStatusEnum_RETIRED;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<ProductSummaryResponseStatusEnum>
    _$productSummaryResponseStatusEnumValues = BuiltSet<
        ProductSummaryResponseStatusEnum>(const <ProductSummaryResponseStatusEnum>[
  _$productSummaryResponseStatusEnum_DRAFT,
  _$productSummaryResponseStatusEnum_PUBLISHED,
  _$productSummaryResponseStatusEnum_RETIRED,
]);

const ProductSummaryResponseTypeEnum
    _$productSummaryResponseTypeEnum_MTPL_ONLY =
    const ProductSummaryResponseTypeEnum._('MTPL_ONLY');
const ProductSummaryResponseTypeEnum
    _$productSummaryResponseTypeEnum_COMPREHENSIVE =
    const ProductSummaryResponseTypeEnum._('COMPREHENSIVE');
const ProductSummaryResponseTypeEnum
    _$productSummaryResponseTypeEnum_TEMPORARY =
    const ProductSummaryResponseTypeEnum._('TEMPORARY');
const ProductSummaryResponseTypeEnum _$productSummaryResponseTypeEnum_BORDER =
    const ProductSummaryResponseTypeEnum._('BORDER');
const ProductSummaryResponseTypeEnum _$productSummaryResponseTypeEnum_FLEET =
    const ProductSummaryResponseTypeEnum._('FLEET');

ProductSummaryResponseTypeEnum _$productSummaryResponseTypeEnumValueOf(
    String name) {
  switch (name) {
    case 'MTPL_ONLY':
      return _$productSummaryResponseTypeEnum_MTPL_ONLY;
    case 'COMPREHENSIVE':
      return _$productSummaryResponseTypeEnum_COMPREHENSIVE;
    case 'TEMPORARY':
      return _$productSummaryResponseTypeEnum_TEMPORARY;
    case 'BORDER':
      return _$productSummaryResponseTypeEnum_BORDER;
    case 'FLEET':
      return _$productSummaryResponseTypeEnum_FLEET;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<ProductSummaryResponseTypeEnum>
    _$productSummaryResponseTypeEnumValues = BuiltSet<
        ProductSummaryResponseTypeEnum>(const <ProductSummaryResponseTypeEnum>[
  _$productSummaryResponseTypeEnum_MTPL_ONLY,
  _$productSummaryResponseTypeEnum_COMPREHENSIVE,
  _$productSummaryResponseTypeEnum_TEMPORARY,
  _$productSummaryResponseTypeEnum_BORDER,
  _$productSummaryResponseTypeEnum_FLEET,
]);

Serializer<ProductSummaryResponseStatusEnum>
    _$productSummaryResponseStatusEnumSerializer =
    _$ProductSummaryResponseStatusEnumSerializer();
Serializer<ProductSummaryResponseTypeEnum>
    _$productSummaryResponseTypeEnumSerializer =
    _$ProductSummaryResponseTypeEnumSerializer();

class _$ProductSummaryResponseStatusEnumSerializer
    implements PrimitiveSerializer<ProductSummaryResponseStatusEnum> {
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
  final Iterable<Type> types = const <Type>[ProductSummaryResponseStatusEnum];
  @override
  final String wireName = 'ProductSummaryResponseStatusEnum';

  @override
  Object serialize(
          Serializers serializers, ProductSummaryResponseStatusEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  ProductSummaryResponseStatusEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      ProductSummaryResponseStatusEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$ProductSummaryResponseTypeEnumSerializer
    implements PrimitiveSerializer<ProductSummaryResponseTypeEnum> {
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
  final Iterable<Type> types = const <Type>[ProductSummaryResponseTypeEnum];
  @override
  final String wireName = 'ProductSummaryResponseTypeEnum';

  @override
  Object serialize(
          Serializers serializers, ProductSummaryResponseTypeEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  ProductSummaryResponseTypeEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      ProductSummaryResponseTypeEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$ProductSummaryResponse extends ProductSummaryResponse {
  @override
  final String code;
  @override
  final int? currentVersionNo;
  @override
  final bool hasDraft;
  @override
  final String id;
  @override
  final String name;
  @override
  final ProductSummaryResponseStatusEnum status;
  @override
  final ProductSummaryResponseTypeEnum type;
  @override
  final DateTime updatedAt;

  factory _$ProductSummaryResponse(
          [void Function(ProductSummaryResponseBuilder)? updates]) =>
      (ProductSummaryResponseBuilder()..update(updates))._build();

  _$ProductSummaryResponse._(
      {required this.code,
      this.currentVersionNo,
      required this.hasDraft,
      required this.id,
      required this.name,
      required this.status,
      required this.type,
      required this.updatedAt})
      : super._();
  @override
  ProductSummaryResponse rebuild(
          void Function(ProductSummaryResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  ProductSummaryResponseBuilder toBuilder() =>
      ProductSummaryResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is ProductSummaryResponse &&
        code == other.code &&
        currentVersionNo == other.currentVersionNo &&
        hasDraft == other.hasDraft &&
        id == other.id &&
        name == other.name &&
        status == other.status &&
        type == other.type &&
        updatedAt == other.updatedAt;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, code.hashCode);
    _$hash = $jc(_$hash, currentVersionNo.hashCode);
    _$hash = $jc(_$hash, hasDraft.hashCode);
    _$hash = $jc(_$hash, id.hashCode);
    _$hash = $jc(_$hash, name.hashCode);
    _$hash = $jc(_$hash, status.hashCode);
    _$hash = $jc(_$hash, type.hashCode);
    _$hash = $jc(_$hash, updatedAt.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'ProductSummaryResponse')
          ..add('code', code)
          ..add('currentVersionNo', currentVersionNo)
          ..add('hasDraft', hasDraft)
          ..add('id', id)
          ..add('name', name)
          ..add('status', status)
          ..add('type', type)
          ..add('updatedAt', updatedAt))
        .toString();
  }
}

class ProductSummaryResponseBuilder
    implements Builder<ProductSummaryResponse, ProductSummaryResponseBuilder> {
  _$ProductSummaryResponse? _$v;

  String? _code;
  String? get code => _$this._code;
  set code(String? code) => _$this._code = code;

  int? _currentVersionNo;
  int? get currentVersionNo => _$this._currentVersionNo;
  set currentVersionNo(int? currentVersionNo) =>
      _$this._currentVersionNo = currentVersionNo;

  bool? _hasDraft;
  bool? get hasDraft => _$this._hasDraft;
  set hasDraft(bool? hasDraft) => _$this._hasDraft = hasDraft;

  String? _id;
  String? get id => _$this._id;
  set id(String? id) => _$this._id = id;

  String? _name;
  String? get name => _$this._name;
  set name(String? name) => _$this._name = name;

  ProductSummaryResponseStatusEnum? _status;
  ProductSummaryResponseStatusEnum? get status => _$this._status;
  set status(ProductSummaryResponseStatusEnum? status) =>
      _$this._status = status;

  ProductSummaryResponseTypeEnum? _type;
  ProductSummaryResponseTypeEnum? get type => _$this._type;
  set type(ProductSummaryResponseTypeEnum? type) => _$this._type = type;

  DateTime? _updatedAt;
  DateTime? get updatedAt => _$this._updatedAt;
  set updatedAt(DateTime? updatedAt) => _$this._updatedAt = updatedAt;

  ProductSummaryResponseBuilder() {
    ProductSummaryResponse._defaults(this);
  }

  ProductSummaryResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _code = $v.code;
      _currentVersionNo = $v.currentVersionNo;
      _hasDraft = $v.hasDraft;
      _id = $v.id;
      _name = $v.name;
      _status = $v.status;
      _type = $v.type;
      _updatedAt = $v.updatedAt;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(ProductSummaryResponse other) {
    _$v = other as _$ProductSummaryResponse;
  }

  @override
  void update(void Function(ProductSummaryResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  ProductSummaryResponse build() => _build();

  _$ProductSummaryResponse _build() {
    final _$result = _$v ??
        _$ProductSummaryResponse._(
          code: BuiltValueNullFieldError.checkNotNull(
              code, r'ProductSummaryResponse', 'code'),
          currentVersionNo: currentVersionNo,
          hasDraft: BuiltValueNullFieldError.checkNotNull(
              hasDraft, r'ProductSummaryResponse', 'hasDraft'),
          id: BuiltValueNullFieldError.checkNotNull(
              id, r'ProductSummaryResponse', 'id'),
          name: BuiltValueNullFieldError.checkNotNull(
              name, r'ProductSummaryResponse', 'name'),
          status: BuiltValueNullFieldError.checkNotNull(
              status, r'ProductSummaryResponse', 'status'),
          type: BuiltValueNullFieldError.checkNotNull(
              type, r'ProductSummaryResponse', 'type'),
          updatedAt: BuiltValueNullFieldError.checkNotNull(
              updatedAt, r'ProductSummaryResponse', 'updatedAt'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
