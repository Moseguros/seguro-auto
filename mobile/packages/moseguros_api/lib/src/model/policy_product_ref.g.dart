// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'policy_product_ref.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const PolicyProductRefTypeEnum _$policyProductRefTypeEnum_MTPL_ONLY =
    const PolicyProductRefTypeEnum._('MTPL_ONLY');
const PolicyProductRefTypeEnum _$policyProductRefTypeEnum_COMPREHENSIVE =
    const PolicyProductRefTypeEnum._('COMPREHENSIVE');
const PolicyProductRefTypeEnum _$policyProductRefTypeEnum_TEMPORARY =
    const PolicyProductRefTypeEnum._('TEMPORARY');
const PolicyProductRefTypeEnum _$policyProductRefTypeEnum_BORDER =
    const PolicyProductRefTypeEnum._('BORDER');
const PolicyProductRefTypeEnum _$policyProductRefTypeEnum_FLEET =
    const PolicyProductRefTypeEnum._('FLEET');

PolicyProductRefTypeEnum _$policyProductRefTypeEnumValueOf(String name) {
  switch (name) {
    case 'MTPL_ONLY':
      return _$policyProductRefTypeEnum_MTPL_ONLY;
    case 'COMPREHENSIVE':
      return _$policyProductRefTypeEnum_COMPREHENSIVE;
    case 'TEMPORARY':
      return _$policyProductRefTypeEnum_TEMPORARY;
    case 'BORDER':
      return _$policyProductRefTypeEnum_BORDER;
    case 'FLEET':
      return _$policyProductRefTypeEnum_FLEET;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<PolicyProductRefTypeEnum> _$policyProductRefTypeEnumValues =
    BuiltSet<PolicyProductRefTypeEnum>(const <PolicyProductRefTypeEnum>[
  _$policyProductRefTypeEnum_MTPL_ONLY,
  _$policyProductRefTypeEnum_COMPREHENSIVE,
  _$policyProductRefTypeEnum_TEMPORARY,
  _$policyProductRefTypeEnum_BORDER,
  _$policyProductRefTypeEnum_FLEET,
]);

Serializer<PolicyProductRefTypeEnum> _$policyProductRefTypeEnumSerializer =
    _$PolicyProductRefTypeEnumSerializer();

class _$PolicyProductRefTypeEnumSerializer
    implements PrimitiveSerializer<PolicyProductRefTypeEnum> {
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
  final Iterable<Type> types = const <Type>[PolicyProductRefTypeEnum];
  @override
  final String wireName = 'PolicyProductRefTypeEnum';

  @override
  Object serialize(Serializers serializers, PolicyProductRefTypeEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  PolicyProductRefTypeEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      PolicyProductRefTypeEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$PolicyProductRef extends PolicyProductRef {
  @override
  final String name;
  @override
  final PolicyProductRefTypeEnum type;
  @override
  final String versionId;

  factory _$PolicyProductRef(
          [void Function(PolicyProductRefBuilder)? updates]) =>
      (PolicyProductRefBuilder()..update(updates))._build();

  _$PolicyProductRef._(
      {required this.name, required this.type, required this.versionId})
      : super._();
  @override
  PolicyProductRef rebuild(void Function(PolicyProductRefBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PolicyProductRefBuilder toBuilder() =>
      PolicyProductRefBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PolicyProductRef &&
        name == other.name &&
        type == other.type &&
        versionId == other.versionId;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, name.hashCode);
    _$hash = $jc(_$hash, type.hashCode);
    _$hash = $jc(_$hash, versionId.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'PolicyProductRef')
          ..add('name', name)
          ..add('type', type)
          ..add('versionId', versionId))
        .toString();
  }
}

class PolicyProductRefBuilder
    implements Builder<PolicyProductRef, PolicyProductRefBuilder> {
  _$PolicyProductRef? _$v;

  String? _name;
  String? get name => _$this._name;
  set name(String? name) => _$this._name = name;

  PolicyProductRefTypeEnum? _type;
  PolicyProductRefTypeEnum? get type => _$this._type;
  set type(PolicyProductRefTypeEnum? type) => _$this._type = type;

  String? _versionId;
  String? get versionId => _$this._versionId;
  set versionId(String? versionId) => _$this._versionId = versionId;

  PolicyProductRefBuilder() {
    PolicyProductRef._defaults(this);
  }

  PolicyProductRefBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _name = $v.name;
      _type = $v.type;
      _versionId = $v.versionId;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(PolicyProductRef other) {
    _$v = other as _$PolicyProductRef;
  }

  @override
  void update(void Function(PolicyProductRefBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PolicyProductRef build() => _build();

  _$PolicyProductRef _build() {
    final _$result = _$v ??
        _$PolicyProductRef._(
          name: BuiltValueNullFieldError.checkNotNull(
              name, r'PolicyProductRef', 'name'),
          type: BuiltValueNullFieldError.checkNotNull(
              type, r'PolicyProductRef', 'type'),
          versionId: BuiltValueNullFieldError.checkNotNull(
              versionId, r'PolicyProductRef', 'versionId'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
