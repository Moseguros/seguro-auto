// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'proposal_product.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const ProposalProductTypeEnum _$proposalProductTypeEnum_MTPL_ONLY =
    const ProposalProductTypeEnum._('MTPL_ONLY');
const ProposalProductTypeEnum _$proposalProductTypeEnum_COMPREHENSIVE =
    const ProposalProductTypeEnum._('COMPREHENSIVE');
const ProposalProductTypeEnum _$proposalProductTypeEnum_TEMPORARY =
    const ProposalProductTypeEnum._('TEMPORARY');
const ProposalProductTypeEnum _$proposalProductTypeEnum_BORDER =
    const ProposalProductTypeEnum._('BORDER');
const ProposalProductTypeEnum _$proposalProductTypeEnum_FLEET =
    const ProposalProductTypeEnum._('FLEET');

ProposalProductTypeEnum _$proposalProductTypeEnumValueOf(String name) {
  switch (name) {
    case 'MTPL_ONLY':
      return _$proposalProductTypeEnum_MTPL_ONLY;
    case 'COMPREHENSIVE':
      return _$proposalProductTypeEnum_COMPREHENSIVE;
    case 'TEMPORARY':
      return _$proposalProductTypeEnum_TEMPORARY;
    case 'BORDER':
      return _$proposalProductTypeEnum_BORDER;
    case 'FLEET':
      return _$proposalProductTypeEnum_FLEET;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<ProposalProductTypeEnum> _$proposalProductTypeEnumValues =
    BuiltSet<ProposalProductTypeEnum>(const <ProposalProductTypeEnum>[
  _$proposalProductTypeEnum_MTPL_ONLY,
  _$proposalProductTypeEnum_COMPREHENSIVE,
  _$proposalProductTypeEnum_TEMPORARY,
  _$proposalProductTypeEnum_BORDER,
  _$proposalProductTypeEnum_FLEET,
]);

Serializer<ProposalProductTypeEnum> _$proposalProductTypeEnumSerializer =
    _$ProposalProductTypeEnumSerializer();

class _$ProposalProductTypeEnumSerializer
    implements PrimitiveSerializer<ProposalProductTypeEnum> {
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
  final Iterable<Type> types = const <Type>[ProposalProductTypeEnum];
  @override
  final String wireName = 'ProposalProductTypeEnum';

  @override
  Object serialize(Serializers serializers, ProposalProductTypeEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  ProposalProductTypeEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      ProposalProductTypeEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$ProposalProduct extends ProposalProduct {
  @override
  final BuiltList<ProposalProductDocument> documents;
  @override
  final String id;
  @override
  final String name;
  @override
  final ProposalProductTypeEnum type;
  @override
  final String versionId;

  factory _$ProposalProduct([void Function(ProposalProductBuilder)? updates]) =>
      (ProposalProductBuilder()..update(updates))._build();

  _$ProposalProduct._(
      {required this.documents,
      required this.id,
      required this.name,
      required this.type,
      required this.versionId})
      : super._();
  @override
  ProposalProduct rebuild(void Function(ProposalProductBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  ProposalProductBuilder toBuilder() => ProposalProductBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is ProposalProduct &&
        documents == other.documents &&
        id == other.id &&
        name == other.name &&
        type == other.type &&
        versionId == other.versionId;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, documents.hashCode);
    _$hash = $jc(_$hash, id.hashCode);
    _$hash = $jc(_$hash, name.hashCode);
    _$hash = $jc(_$hash, type.hashCode);
    _$hash = $jc(_$hash, versionId.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'ProposalProduct')
          ..add('documents', documents)
          ..add('id', id)
          ..add('name', name)
          ..add('type', type)
          ..add('versionId', versionId))
        .toString();
  }
}

class ProposalProductBuilder
    implements Builder<ProposalProduct, ProposalProductBuilder> {
  _$ProposalProduct? _$v;

  ListBuilder<ProposalProductDocument>? _documents;
  ListBuilder<ProposalProductDocument> get documents =>
      _$this._documents ??= ListBuilder<ProposalProductDocument>();
  set documents(ListBuilder<ProposalProductDocument>? documents) =>
      _$this._documents = documents;

  String? _id;
  String? get id => _$this._id;
  set id(String? id) => _$this._id = id;

  String? _name;
  String? get name => _$this._name;
  set name(String? name) => _$this._name = name;

  ProposalProductTypeEnum? _type;
  ProposalProductTypeEnum? get type => _$this._type;
  set type(ProposalProductTypeEnum? type) => _$this._type = type;

  String? _versionId;
  String? get versionId => _$this._versionId;
  set versionId(String? versionId) => _$this._versionId = versionId;

  ProposalProductBuilder() {
    ProposalProduct._defaults(this);
  }

  ProposalProductBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _documents = $v.documents.toBuilder();
      _id = $v.id;
      _name = $v.name;
      _type = $v.type;
      _versionId = $v.versionId;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(ProposalProduct other) {
    _$v = other as _$ProposalProduct;
  }

  @override
  void update(void Function(ProposalProductBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  ProposalProduct build() => _build();

  _$ProposalProduct _build() {
    _$ProposalProduct _$result;
    try {
      _$result = _$v ??
          _$ProposalProduct._(
            documents: documents.build(),
            id: BuiltValueNullFieldError.checkNotNull(
                id, r'ProposalProduct', 'id'),
            name: BuiltValueNullFieldError.checkNotNull(
                name, r'ProposalProduct', 'name'),
            type: BuiltValueNullFieldError.checkNotNull(
                type, r'ProposalProduct', 'type'),
            versionId: BuiltValueNullFieldError.checkNotNull(
                versionId, r'ProposalProduct', 'versionId'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'documents';
        documents.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'ProposalProduct', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
