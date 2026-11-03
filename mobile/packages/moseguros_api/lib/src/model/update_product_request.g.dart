// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'update_product_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const UpdateProductRequestTypeEnum _$updateProductRequestTypeEnum_MTPL_ONLY =
    const UpdateProductRequestTypeEnum._('MTPL_ONLY');
const UpdateProductRequestTypeEnum
    _$updateProductRequestTypeEnum_COMPREHENSIVE =
    const UpdateProductRequestTypeEnum._('COMPREHENSIVE');
const UpdateProductRequestTypeEnum _$updateProductRequestTypeEnum_TEMPORARY =
    const UpdateProductRequestTypeEnum._('TEMPORARY');
const UpdateProductRequestTypeEnum _$updateProductRequestTypeEnum_BORDER =
    const UpdateProductRequestTypeEnum._('BORDER');
const UpdateProductRequestTypeEnum _$updateProductRequestTypeEnum_FLEET =
    const UpdateProductRequestTypeEnum._('FLEET');

UpdateProductRequestTypeEnum _$updateProductRequestTypeEnumValueOf(
    String name) {
  switch (name) {
    case 'MTPL_ONLY':
      return _$updateProductRequestTypeEnum_MTPL_ONLY;
    case 'COMPREHENSIVE':
      return _$updateProductRequestTypeEnum_COMPREHENSIVE;
    case 'TEMPORARY':
      return _$updateProductRequestTypeEnum_TEMPORARY;
    case 'BORDER':
      return _$updateProductRequestTypeEnum_BORDER;
    case 'FLEET':
      return _$updateProductRequestTypeEnum_FLEET;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<UpdateProductRequestTypeEnum>
    _$updateProductRequestTypeEnumValues =
    BuiltSet<UpdateProductRequestTypeEnum>(const <UpdateProductRequestTypeEnum>[
  _$updateProductRequestTypeEnum_MTPL_ONLY,
  _$updateProductRequestTypeEnum_COMPREHENSIVE,
  _$updateProductRequestTypeEnum_TEMPORARY,
  _$updateProductRequestTypeEnum_BORDER,
  _$updateProductRequestTypeEnum_FLEET,
]);

Serializer<UpdateProductRequestTypeEnum>
    _$updateProductRequestTypeEnumSerializer =
    _$UpdateProductRequestTypeEnumSerializer();

class _$UpdateProductRequestTypeEnumSerializer
    implements PrimitiveSerializer<UpdateProductRequestTypeEnum> {
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
  final Iterable<Type> types = const <Type>[UpdateProductRequestTypeEnum];
  @override
  final String wireName = 'UpdateProductRequestTypeEnum';

  @override
  Object serialize(Serializers serializers, UpdateProductRequestTypeEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  UpdateProductRequestTypeEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      UpdateProductRequestTypeEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$UpdateProductRequest extends UpdateProductRequest {
  @override
  final String? description;
  @override
  final String name;
  @override
  final UpdateProductRequestTypeEnum type;
  @override
  final int version;

  factory _$UpdateProductRequest(
          [void Function(UpdateProductRequestBuilder)? updates]) =>
      (UpdateProductRequestBuilder()..update(updates))._build();

  _$UpdateProductRequest._(
      {this.description,
      required this.name,
      required this.type,
      required this.version})
      : super._();
  @override
  UpdateProductRequest rebuild(
          void Function(UpdateProductRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  UpdateProductRequestBuilder toBuilder() =>
      UpdateProductRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is UpdateProductRequest &&
        description == other.description &&
        name == other.name &&
        type == other.type &&
        version == other.version;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, description.hashCode);
    _$hash = $jc(_$hash, name.hashCode);
    _$hash = $jc(_$hash, type.hashCode);
    _$hash = $jc(_$hash, version.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'UpdateProductRequest')
          ..add('description', description)
          ..add('name', name)
          ..add('type', type)
          ..add('version', version))
        .toString();
  }
}

class UpdateProductRequestBuilder
    implements Builder<UpdateProductRequest, UpdateProductRequestBuilder> {
  _$UpdateProductRequest? _$v;

  String? _description;
  String? get description => _$this._description;
  set description(String? description) => _$this._description = description;

  String? _name;
  String? get name => _$this._name;
  set name(String? name) => _$this._name = name;

  UpdateProductRequestTypeEnum? _type;
  UpdateProductRequestTypeEnum? get type => _$this._type;
  set type(UpdateProductRequestTypeEnum? type) => _$this._type = type;

  int? _version;
  int? get version => _$this._version;
  set version(int? version) => _$this._version = version;

  UpdateProductRequestBuilder() {
    UpdateProductRequest._defaults(this);
  }

  UpdateProductRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _description = $v.description;
      _name = $v.name;
      _type = $v.type;
      _version = $v.version;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(UpdateProductRequest other) {
    _$v = other as _$UpdateProductRequest;
  }

  @override
  void update(void Function(UpdateProductRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  UpdateProductRequest build() => _build();

  _$UpdateProductRequest _build() {
    final _$result = _$v ??
        _$UpdateProductRequest._(
          description: description,
          name: BuiltValueNullFieldError.checkNotNull(
              name, r'UpdateProductRequest', 'name'),
          type: BuiltValueNullFieldError.checkNotNull(
              type, r'UpdateProductRequest', 'type'),
          version: BuiltValueNullFieldError.checkNotNull(
              version, r'UpdateProductRequest', 'version'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
