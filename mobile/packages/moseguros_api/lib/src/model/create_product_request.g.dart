// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'create_product_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const CreateProductRequestTypeEnum _$createProductRequestTypeEnum_MTPL_ONLY =
    const CreateProductRequestTypeEnum._('MTPL_ONLY');
const CreateProductRequestTypeEnum
    _$createProductRequestTypeEnum_COMPREHENSIVE =
    const CreateProductRequestTypeEnum._('COMPREHENSIVE');
const CreateProductRequestTypeEnum _$createProductRequestTypeEnum_TEMPORARY =
    const CreateProductRequestTypeEnum._('TEMPORARY');
const CreateProductRequestTypeEnum _$createProductRequestTypeEnum_BORDER =
    const CreateProductRequestTypeEnum._('BORDER');
const CreateProductRequestTypeEnum _$createProductRequestTypeEnum_FLEET =
    const CreateProductRequestTypeEnum._('FLEET');

CreateProductRequestTypeEnum _$createProductRequestTypeEnumValueOf(
    String name) {
  switch (name) {
    case 'MTPL_ONLY':
      return _$createProductRequestTypeEnum_MTPL_ONLY;
    case 'COMPREHENSIVE':
      return _$createProductRequestTypeEnum_COMPREHENSIVE;
    case 'TEMPORARY':
      return _$createProductRequestTypeEnum_TEMPORARY;
    case 'BORDER':
      return _$createProductRequestTypeEnum_BORDER;
    case 'FLEET':
      return _$createProductRequestTypeEnum_FLEET;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<CreateProductRequestTypeEnum>
    _$createProductRequestTypeEnumValues =
    BuiltSet<CreateProductRequestTypeEnum>(const <CreateProductRequestTypeEnum>[
  _$createProductRequestTypeEnum_MTPL_ONLY,
  _$createProductRequestTypeEnum_COMPREHENSIVE,
  _$createProductRequestTypeEnum_TEMPORARY,
  _$createProductRequestTypeEnum_BORDER,
  _$createProductRequestTypeEnum_FLEET,
]);

Serializer<CreateProductRequestTypeEnum>
    _$createProductRequestTypeEnumSerializer =
    _$CreateProductRequestTypeEnumSerializer();

class _$CreateProductRequestTypeEnumSerializer
    implements PrimitiveSerializer<CreateProductRequestTypeEnum> {
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
  final Iterable<Type> types = const <Type>[CreateProductRequestTypeEnum];
  @override
  final String wireName = 'CreateProductRequestTypeEnum';

  @override
  Object serialize(Serializers serializers, CreateProductRequestTypeEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  CreateProductRequestTypeEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      CreateProductRequestTypeEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$CreateProductRequest extends CreateProductRequest {
  @override
  final String code;
  @override
  final String? description;
  @override
  final String name;
  @override
  final CreateProductRequestTypeEnum type;

  factory _$CreateProductRequest(
          [void Function(CreateProductRequestBuilder)? updates]) =>
      (CreateProductRequestBuilder()..update(updates))._build();

  _$CreateProductRequest._(
      {required this.code,
      this.description,
      required this.name,
      required this.type})
      : super._();
  @override
  CreateProductRequest rebuild(
          void Function(CreateProductRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  CreateProductRequestBuilder toBuilder() =>
      CreateProductRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is CreateProductRequest &&
        code == other.code &&
        description == other.description &&
        name == other.name &&
        type == other.type;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, code.hashCode);
    _$hash = $jc(_$hash, description.hashCode);
    _$hash = $jc(_$hash, name.hashCode);
    _$hash = $jc(_$hash, type.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'CreateProductRequest')
          ..add('code', code)
          ..add('description', description)
          ..add('name', name)
          ..add('type', type))
        .toString();
  }
}

class CreateProductRequestBuilder
    implements Builder<CreateProductRequest, CreateProductRequestBuilder> {
  _$CreateProductRequest? _$v;

  String? _code;
  String? get code => _$this._code;
  set code(String? code) => _$this._code = code;

  String? _description;
  String? get description => _$this._description;
  set description(String? description) => _$this._description = description;

  String? _name;
  String? get name => _$this._name;
  set name(String? name) => _$this._name = name;

  CreateProductRequestTypeEnum? _type;
  CreateProductRequestTypeEnum? get type => _$this._type;
  set type(CreateProductRequestTypeEnum? type) => _$this._type = type;

  CreateProductRequestBuilder() {
    CreateProductRequest._defaults(this);
  }

  CreateProductRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _code = $v.code;
      _description = $v.description;
      _name = $v.name;
      _type = $v.type;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(CreateProductRequest other) {
    _$v = other as _$CreateProductRequest;
  }

  @override
  void update(void Function(CreateProductRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  CreateProductRequest build() => _build();

  _$CreateProductRequest _build() {
    final _$result = _$v ??
        _$CreateProductRequest._(
          code: BuiltValueNullFieldError.checkNotNull(
              code, r'CreateProductRequest', 'code'),
          description: description,
          name: BuiltValueNullFieldError.checkNotNull(
              name, r'CreateProductRequest', 'name'),
          type: BuiltValueNullFieldError.checkNotNull(
              type, r'CreateProductRequest', 'type'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
