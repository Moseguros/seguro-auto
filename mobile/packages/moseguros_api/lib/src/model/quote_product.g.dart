// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'quote_product.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const QuoteProductTypeEnum _$quoteProductTypeEnum_MTPL_ONLY =
    const QuoteProductTypeEnum._('MTPL_ONLY');
const QuoteProductTypeEnum _$quoteProductTypeEnum_COMPREHENSIVE =
    const QuoteProductTypeEnum._('COMPREHENSIVE');
const QuoteProductTypeEnum _$quoteProductTypeEnum_TEMPORARY =
    const QuoteProductTypeEnum._('TEMPORARY');
const QuoteProductTypeEnum _$quoteProductTypeEnum_BORDER =
    const QuoteProductTypeEnum._('BORDER');
const QuoteProductTypeEnum _$quoteProductTypeEnum_FLEET =
    const QuoteProductTypeEnum._('FLEET');

QuoteProductTypeEnum _$quoteProductTypeEnumValueOf(String name) {
  switch (name) {
    case 'MTPL_ONLY':
      return _$quoteProductTypeEnum_MTPL_ONLY;
    case 'COMPREHENSIVE':
      return _$quoteProductTypeEnum_COMPREHENSIVE;
    case 'TEMPORARY':
      return _$quoteProductTypeEnum_TEMPORARY;
    case 'BORDER':
      return _$quoteProductTypeEnum_BORDER;
    case 'FLEET':
      return _$quoteProductTypeEnum_FLEET;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<QuoteProductTypeEnum> _$quoteProductTypeEnumValues =
    BuiltSet<QuoteProductTypeEnum>(const <QuoteProductTypeEnum>[
  _$quoteProductTypeEnum_MTPL_ONLY,
  _$quoteProductTypeEnum_COMPREHENSIVE,
  _$quoteProductTypeEnum_TEMPORARY,
  _$quoteProductTypeEnum_BORDER,
  _$quoteProductTypeEnum_FLEET,
]);

Serializer<QuoteProductTypeEnum> _$quoteProductTypeEnumSerializer =
    _$QuoteProductTypeEnumSerializer();

class _$QuoteProductTypeEnumSerializer
    implements PrimitiveSerializer<QuoteProductTypeEnum> {
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
  final Iterable<Type> types = const <Type>[QuoteProductTypeEnum];
  @override
  final String wireName = 'QuoteProductTypeEnum';

  @override
  Object serialize(Serializers serializers, QuoteProductTypeEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  QuoteProductTypeEnum deserialize(Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      QuoteProductTypeEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$QuoteProduct extends QuoteProduct {
  @override
  final String code;
  @override
  final String? generalConditionsFileId;
  @override
  final String id;
  @override
  final String name;
  @override
  final String? preContractInfoFileId;
  @override
  final QuoteProductTypeEnum type;
  @override
  final String versionId;
  @override
  final int versionNo;

  factory _$QuoteProduct([void Function(QuoteProductBuilder)? updates]) =>
      (QuoteProductBuilder()..update(updates))._build();

  _$QuoteProduct._(
      {required this.code,
      this.generalConditionsFileId,
      required this.id,
      required this.name,
      this.preContractInfoFileId,
      required this.type,
      required this.versionId,
      required this.versionNo})
      : super._();
  @override
  QuoteProduct rebuild(void Function(QuoteProductBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  QuoteProductBuilder toBuilder() => QuoteProductBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is QuoteProduct &&
        code == other.code &&
        generalConditionsFileId == other.generalConditionsFileId &&
        id == other.id &&
        name == other.name &&
        preContractInfoFileId == other.preContractInfoFileId &&
        type == other.type &&
        versionId == other.versionId &&
        versionNo == other.versionNo;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, code.hashCode);
    _$hash = $jc(_$hash, generalConditionsFileId.hashCode);
    _$hash = $jc(_$hash, id.hashCode);
    _$hash = $jc(_$hash, name.hashCode);
    _$hash = $jc(_$hash, preContractInfoFileId.hashCode);
    _$hash = $jc(_$hash, type.hashCode);
    _$hash = $jc(_$hash, versionId.hashCode);
    _$hash = $jc(_$hash, versionNo.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'QuoteProduct')
          ..add('code', code)
          ..add('generalConditionsFileId', generalConditionsFileId)
          ..add('id', id)
          ..add('name', name)
          ..add('preContractInfoFileId', preContractInfoFileId)
          ..add('type', type)
          ..add('versionId', versionId)
          ..add('versionNo', versionNo))
        .toString();
  }
}

class QuoteProductBuilder
    implements Builder<QuoteProduct, QuoteProductBuilder> {
  _$QuoteProduct? _$v;

  String? _code;
  String? get code => _$this._code;
  set code(String? code) => _$this._code = code;

  String? _generalConditionsFileId;
  String? get generalConditionsFileId => _$this._generalConditionsFileId;
  set generalConditionsFileId(String? generalConditionsFileId) =>
      _$this._generalConditionsFileId = generalConditionsFileId;

  String? _id;
  String? get id => _$this._id;
  set id(String? id) => _$this._id = id;

  String? _name;
  String? get name => _$this._name;
  set name(String? name) => _$this._name = name;

  String? _preContractInfoFileId;
  String? get preContractInfoFileId => _$this._preContractInfoFileId;
  set preContractInfoFileId(String? preContractInfoFileId) =>
      _$this._preContractInfoFileId = preContractInfoFileId;

  QuoteProductTypeEnum? _type;
  QuoteProductTypeEnum? get type => _$this._type;
  set type(QuoteProductTypeEnum? type) => _$this._type = type;

  String? _versionId;
  String? get versionId => _$this._versionId;
  set versionId(String? versionId) => _$this._versionId = versionId;

  int? _versionNo;
  int? get versionNo => _$this._versionNo;
  set versionNo(int? versionNo) => _$this._versionNo = versionNo;

  QuoteProductBuilder() {
    QuoteProduct._defaults(this);
  }

  QuoteProductBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _code = $v.code;
      _generalConditionsFileId = $v.generalConditionsFileId;
      _id = $v.id;
      _name = $v.name;
      _preContractInfoFileId = $v.preContractInfoFileId;
      _type = $v.type;
      _versionId = $v.versionId;
      _versionNo = $v.versionNo;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(QuoteProduct other) {
    _$v = other as _$QuoteProduct;
  }

  @override
  void update(void Function(QuoteProductBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  QuoteProduct build() => _build();

  _$QuoteProduct _build() {
    final _$result = _$v ??
        _$QuoteProduct._(
          code: BuiltValueNullFieldError.checkNotNull(
              code, r'QuoteProduct', 'code'),
          generalConditionsFileId: generalConditionsFileId,
          id: BuiltValueNullFieldError.checkNotNull(id, r'QuoteProduct', 'id'),
          name: BuiltValueNullFieldError.checkNotNull(
              name, r'QuoteProduct', 'name'),
          preContractInfoFileId: preContractInfoFileId,
          type: BuiltValueNullFieldError.checkNotNull(
              type, r'QuoteProduct', 'type'),
          versionId: BuiltValueNullFieldError.checkNotNull(
              versionId, r'QuoteProduct', 'versionId'),
          versionNo: BuiltValueNullFieldError.checkNotNull(
              versionNo, r'QuoteProduct', 'versionNo'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
