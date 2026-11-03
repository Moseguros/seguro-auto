// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'product_document_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const ProductDocumentResponseDocTypeEnum
    _$productDocumentResponseDocTypeEnum_GENERAL_CONDITIONS =
    const ProductDocumentResponseDocTypeEnum._('GENERAL_CONDITIONS');
const ProductDocumentResponseDocTypeEnum
    _$productDocumentResponseDocTypeEnum_PRE_CONTRACT_INFO =
    const ProductDocumentResponseDocTypeEnum._('PRE_CONTRACT_INFO');
const ProductDocumentResponseDocTypeEnum
    _$productDocumentResponseDocTypeEnum_SPECIAL_CONDITIONS =
    const ProductDocumentResponseDocTypeEnum._('SPECIAL_CONDITIONS');
const ProductDocumentResponseDocTypeEnum
    _$productDocumentResponseDocTypeEnum_OTHER =
    const ProductDocumentResponseDocTypeEnum._('OTHER');

ProductDocumentResponseDocTypeEnum _$productDocumentResponseDocTypeEnumValueOf(
    String name) {
  switch (name) {
    case 'GENERAL_CONDITIONS':
      return _$productDocumentResponseDocTypeEnum_GENERAL_CONDITIONS;
    case 'PRE_CONTRACT_INFO':
      return _$productDocumentResponseDocTypeEnum_PRE_CONTRACT_INFO;
    case 'SPECIAL_CONDITIONS':
      return _$productDocumentResponseDocTypeEnum_SPECIAL_CONDITIONS;
    case 'OTHER':
      return _$productDocumentResponseDocTypeEnum_OTHER;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<ProductDocumentResponseDocTypeEnum>
    _$productDocumentResponseDocTypeEnumValues = BuiltSet<
        ProductDocumentResponseDocTypeEnum>(const <ProductDocumentResponseDocTypeEnum>[
  _$productDocumentResponseDocTypeEnum_GENERAL_CONDITIONS,
  _$productDocumentResponseDocTypeEnum_PRE_CONTRACT_INFO,
  _$productDocumentResponseDocTypeEnum_SPECIAL_CONDITIONS,
  _$productDocumentResponseDocTypeEnum_OTHER,
]);

const ProductDocumentResponseLanguageEnum
    _$productDocumentResponseLanguageEnum_pt =
    const ProductDocumentResponseLanguageEnum._('pt');
const ProductDocumentResponseLanguageEnum
    _$productDocumentResponseLanguageEnum_en =
    const ProductDocumentResponseLanguageEnum._('en');

ProductDocumentResponseLanguageEnum
    _$productDocumentResponseLanguageEnumValueOf(String name) {
  switch (name) {
    case 'pt':
      return _$productDocumentResponseLanguageEnum_pt;
    case 'en':
      return _$productDocumentResponseLanguageEnum_en;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<ProductDocumentResponseLanguageEnum>
    _$productDocumentResponseLanguageEnumValues = BuiltSet<
        ProductDocumentResponseLanguageEnum>(const <ProductDocumentResponseLanguageEnum>[
  _$productDocumentResponseLanguageEnum_pt,
  _$productDocumentResponseLanguageEnum_en,
]);

Serializer<ProductDocumentResponseDocTypeEnum>
    _$productDocumentResponseDocTypeEnumSerializer =
    _$ProductDocumentResponseDocTypeEnumSerializer();
Serializer<ProductDocumentResponseLanguageEnum>
    _$productDocumentResponseLanguageEnumSerializer =
    _$ProductDocumentResponseLanguageEnumSerializer();

class _$ProductDocumentResponseDocTypeEnumSerializer
    implements PrimitiveSerializer<ProductDocumentResponseDocTypeEnum> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'GENERAL_CONDITIONS': 'GENERAL_CONDITIONS',
    'PRE_CONTRACT_INFO': 'PRE_CONTRACT_INFO',
    'SPECIAL_CONDITIONS': 'SPECIAL_CONDITIONS',
    'OTHER': 'OTHER',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'GENERAL_CONDITIONS': 'GENERAL_CONDITIONS',
    'PRE_CONTRACT_INFO': 'PRE_CONTRACT_INFO',
    'SPECIAL_CONDITIONS': 'SPECIAL_CONDITIONS',
    'OTHER': 'OTHER',
  };

  @override
  final Iterable<Type> types = const <Type>[ProductDocumentResponseDocTypeEnum];
  @override
  final String wireName = 'ProductDocumentResponseDocTypeEnum';

  @override
  Object serialize(
          Serializers serializers, ProductDocumentResponseDocTypeEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  ProductDocumentResponseDocTypeEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      ProductDocumentResponseDocTypeEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$ProductDocumentResponseLanguageEnumSerializer
    implements PrimitiveSerializer<ProductDocumentResponseLanguageEnum> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'pt': 'pt',
    'en': 'en',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'pt': 'pt',
    'en': 'en',
  };

  @override
  final Iterable<Type> types = const <Type>[
    ProductDocumentResponseLanguageEnum
  ];
  @override
  final String wireName = 'ProductDocumentResponseLanguageEnum';

  @override
  Object serialize(
          Serializers serializers, ProductDocumentResponseLanguageEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  ProductDocumentResponseLanguageEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      ProductDocumentResponseLanguageEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$ProductDocumentResponse extends ProductDocumentResponse {
  @override
  final ProductDocumentResponseDocTypeEnum docType;
  @override
  final String fileId;
  @override
  final String id;
  @override
  final ProductDocumentResponseLanguageEnum language;
  @override
  final String? title;

  factory _$ProductDocumentResponse(
          [void Function(ProductDocumentResponseBuilder)? updates]) =>
      (ProductDocumentResponseBuilder()..update(updates))._build();

  _$ProductDocumentResponse._(
      {required this.docType,
      required this.fileId,
      required this.id,
      required this.language,
      this.title})
      : super._();
  @override
  ProductDocumentResponse rebuild(
          void Function(ProductDocumentResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  ProductDocumentResponseBuilder toBuilder() =>
      ProductDocumentResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is ProductDocumentResponse &&
        docType == other.docType &&
        fileId == other.fileId &&
        id == other.id &&
        language == other.language &&
        title == other.title;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, docType.hashCode);
    _$hash = $jc(_$hash, fileId.hashCode);
    _$hash = $jc(_$hash, id.hashCode);
    _$hash = $jc(_$hash, language.hashCode);
    _$hash = $jc(_$hash, title.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'ProductDocumentResponse')
          ..add('docType', docType)
          ..add('fileId', fileId)
          ..add('id', id)
          ..add('language', language)
          ..add('title', title))
        .toString();
  }
}

class ProductDocumentResponseBuilder
    implements
        Builder<ProductDocumentResponse, ProductDocumentResponseBuilder> {
  _$ProductDocumentResponse? _$v;

  ProductDocumentResponseDocTypeEnum? _docType;
  ProductDocumentResponseDocTypeEnum? get docType => _$this._docType;
  set docType(ProductDocumentResponseDocTypeEnum? docType) =>
      _$this._docType = docType;

  String? _fileId;
  String? get fileId => _$this._fileId;
  set fileId(String? fileId) => _$this._fileId = fileId;

  String? _id;
  String? get id => _$this._id;
  set id(String? id) => _$this._id = id;

  ProductDocumentResponseLanguageEnum? _language;
  ProductDocumentResponseLanguageEnum? get language => _$this._language;
  set language(ProductDocumentResponseLanguageEnum? language) =>
      _$this._language = language;

  String? _title;
  String? get title => _$this._title;
  set title(String? title) => _$this._title = title;

  ProductDocumentResponseBuilder() {
    ProductDocumentResponse._defaults(this);
  }

  ProductDocumentResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _docType = $v.docType;
      _fileId = $v.fileId;
      _id = $v.id;
      _language = $v.language;
      _title = $v.title;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(ProductDocumentResponse other) {
    _$v = other as _$ProductDocumentResponse;
  }

  @override
  void update(void Function(ProductDocumentResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  ProductDocumentResponse build() => _build();

  _$ProductDocumentResponse _build() {
    final _$result = _$v ??
        _$ProductDocumentResponse._(
          docType: BuiltValueNullFieldError.checkNotNull(
              docType, r'ProductDocumentResponse', 'docType'),
          fileId: BuiltValueNullFieldError.checkNotNull(
              fileId, r'ProductDocumentResponse', 'fileId'),
          id: BuiltValueNullFieldError.checkNotNull(
              id, r'ProductDocumentResponse', 'id'),
          language: BuiltValueNullFieldError.checkNotNull(
              language, r'ProductDocumentResponse', 'language'),
          title: title,
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
