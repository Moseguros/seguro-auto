// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'product_version_ref_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const ProductVersionRefResponseStatusEnum
    _$productVersionRefResponseStatusEnum_DRAFT =
    const ProductVersionRefResponseStatusEnum._('DRAFT');
const ProductVersionRefResponseStatusEnum
    _$productVersionRefResponseStatusEnum_PUBLISHED =
    const ProductVersionRefResponseStatusEnum._('PUBLISHED');
const ProductVersionRefResponseStatusEnum
    _$productVersionRefResponseStatusEnum_SUPERSEDED =
    const ProductVersionRefResponseStatusEnum._('SUPERSEDED');
const ProductVersionRefResponseStatusEnum
    _$productVersionRefResponseStatusEnum_RETIRED =
    const ProductVersionRefResponseStatusEnum._('RETIRED');

ProductVersionRefResponseStatusEnum
    _$productVersionRefResponseStatusEnumValueOf(String name) {
  switch (name) {
    case 'DRAFT':
      return _$productVersionRefResponseStatusEnum_DRAFT;
    case 'PUBLISHED':
      return _$productVersionRefResponseStatusEnum_PUBLISHED;
    case 'SUPERSEDED':
      return _$productVersionRefResponseStatusEnum_SUPERSEDED;
    case 'RETIRED':
      return _$productVersionRefResponseStatusEnum_RETIRED;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<ProductVersionRefResponseStatusEnum>
    _$productVersionRefResponseStatusEnumValues = BuiltSet<
        ProductVersionRefResponseStatusEnum>(const <ProductVersionRefResponseStatusEnum>[
  _$productVersionRefResponseStatusEnum_DRAFT,
  _$productVersionRefResponseStatusEnum_PUBLISHED,
  _$productVersionRefResponseStatusEnum_SUPERSEDED,
  _$productVersionRefResponseStatusEnum_RETIRED,
]);

Serializer<ProductVersionRefResponseStatusEnum>
    _$productVersionRefResponseStatusEnumSerializer =
    _$ProductVersionRefResponseStatusEnumSerializer();

class _$ProductVersionRefResponseStatusEnumSerializer
    implements PrimitiveSerializer<ProductVersionRefResponseStatusEnum> {
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
  final Iterable<Type> types = const <Type>[
    ProductVersionRefResponseStatusEnum
  ];
  @override
  final String wireName = 'ProductVersionRefResponseStatusEnum';

  @override
  Object serialize(
          Serializers serializers, ProductVersionRefResponseStatusEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  ProductVersionRefResponseStatusEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      ProductVersionRefResponseStatusEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$ProductVersionRefResponse extends ProductVersionRefResponse {
  @override
  final String? changeNotes;
  @override
  final String id;
  @override
  final DateTime? publishedAt;
  @override
  final String? publishedBy;
  @override
  final ProductVersionRefResponseStatusEnum status;
  @override
  final Date? validFrom;
  @override
  final Date? validTo;
  @override
  final int versionNo;

  factory _$ProductVersionRefResponse(
          [void Function(ProductVersionRefResponseBuilder)? updates]) =>
      (ProductVersionRefResponseBuilder()..update(updates))._build();

  _$ProductVersionRefResponse._(
      {this.changeNotes,
      required this.id,
      this.publishedAt,
      this.publishedBy,
      required this.status,
      this.validFrom,
      this.validTo,
      required this.versionNo})
      : super._();
  @override
  ProductVersionRefResponse rebuild(
          void Function(ProductVersionRefResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  ProductVersionRefResponseBuilder toBuilder() =>
      ProductVersionRefResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is ProductVersionRefResponse &&
        changeNotes == other.changeNotes &&
        id == other.id &&
        publishedAt == other.publishedAt &&
        publishedBy == other.publishedBy &&
        status == other.status &&
        validFrom == other.validFrom &&
        validTo == other.validTo &&
        versionNo == other.versionNo;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, changeNotes.hashCode);
    _$hash = $jc(_$hash, id.hashCode);
    _$hash = $jc(_$hash, publishedAt.hashCode);
    _$hash = $jc(_$hash, publishedBy.hashCode);
    _$hash = $jc(_$hash, status.hashCode);
    _$hash = $jc(_$hash, validFrom.hashCode);
    _$hash = $jc(_$hash, validTo.hashCode);
    _$hash = $jc(_$hash, versionNo.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'ProductVersionRefResponse')
          ..add('changeNotes', changeNotes)
          ..add('id', id)
          ..add('publishedAt', publishedAt)
          ..add('publishedBy', publishedBy)
          ..add('status', status)
          ..add('validFrom', validFrom)
          ..add('validTo', validTo)
          ..add('versionNo', versionNo))
        .toString();
  }
}

class ProductVersionRefResponseBuilder
    implements
        Builder<ProductVersionRefResponse, ProductVersionRefResponseBuilder> {
  _$ProductVersionRefResponse? _$v;

  String? _changeNotes;
  String? get changeNotes => _$this._changeNotes;
  set changeNotes(String? changeNotes) => _$this._changeNotes = changeNotes;

  String? _id;
  String? get id => _$this._id;
  set id(String? id) => _$this._id = id;

  DateTime? _publishedAt;
  DateTime? get publishedAt => _$this._publishedAt;
  set publishedAt(DateTime? publishedAt) => _$this._publishedAt = publishedAt;

  String? _publishedBy;
  String? get publishedBy => _$this._publishedBy;
  set publishedBy(String? publishedBy) => _$this._publishedBy = publishedBy;

  ProductVersionRefResponseStatusEnum? _status;
  ProductVersionRefResponseStatusEnum? get status => _$this._status;
  set status(ProductVersionRefResponseStatusEnum? status) =>
      _$this._status = status;

  Date? _validFrom;
  Date? get validFrom => _$this._validFrom;
  set validFrom(Date? validFrom) => _$this._validFrom = validFrom;

  Date? _validTo;
  Date? get validTo => _$this._validTo;
  set validTo(Date? validTo) => _$this._validTo = validTo;

  int? _versionNo;
  int? get versionNo => _$this._versionNo;
  set versionNo(int? versionNo) => _$this._versionNo = versionNo;

  ProductVersionRefResponseBuilder() {
    ProductVersionRefResponse._defaults(this);
  }

  ProductVersionRefResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _changeNotes = $v.changeNotes;
      _id = $v.id;
      _publishedAt = $v.publishedAt;
      _publishedBy = $v.publishedBy;
      _status = $v.status;
      _validFrom = $v.validFrom;
      _validTo = $v.validTo;
      _versionNo = $v.versionNo;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(ProductVersionRefResponse other) {
    _$v = other as _$ProductVersionRefResponse;
  }

  @override
  void update(void Function(ProductVersionRefResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  ProductVersionRefResponse build() => _build();

  _$ProductVersionRefResponse _build() {
    final _$result = _$v ??
        _$ProductVersionRefResponse._(
          changeNotes: changeNotes,
          id: BuiltValueNullFieldError.checkNotNull(
              id, r'ProductVersionRefResponse', 'id'),
          publishedAt: publishedAt,
          publishedBy: publishedBy,
          status: BuiltValueNullFieldError.checkNotNull(
              status, r'ProductVersionRefResponse', 'status'),
          validFrom: validFrom,
          validTo: validTo,
          versionNo: BuiltValueNullFieldError.checkNotNull(
              versionNo, r'ProductVersionRefResponse', 'versionNo'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
