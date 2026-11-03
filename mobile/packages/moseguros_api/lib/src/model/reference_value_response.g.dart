// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'reference_value_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const ReferenceValueResponseDomainEnum
    _$referenceValueResponseDomainEnum_VEHICLE_MAKE =
    const ReferenceValueResponseDomainEnum._('VEHICLE_MAKE');
const ReferenceValueResponseDomainEnum
    _$referenceValueResponseDomainEnum_VEHICLE_MODEL =
    const ReferenceValueResponseDomainEnum._('VEHICLE_MODEL');
const ReferenceValueResponseDomainEnum
    _$referenceValueResponseDomainEnum_LICENCE_CATEGORY =
    const ReferenceValueResponseDomainEnum._('LICENCE_CATEGORY');

ReferenceValueResponseDomainEnum _$referenceValueResponseDomainEnumValueOf(
    String name) {
  switch (name) {
    case 'VEHICLE_MAKE':
      return _$referenceValueResponseDomainEnum_VEHICLE_MAKE;
    case 'VEHICLE_MODEL':
      return _$referenceValueResponseDomainEnum_VEHICLE_MODEL;
    case 'LICENCE_CATEGORY':
      return _$referenceValueResponseDomainEnum_LICENCE_CATEGORY;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<ReferenceValueResponseDomainEnum>
    _$referenceValueResponseDomainEnumValues = BuiltSet<
        ReferenceValueResponseDomainEnum>(const <ReferenceValueResponseDomainEnum>[
  _$referenceValueResponseDomainEnum_VEHICLE_MAKE,
  _$referenceValueResponseDomainEnum_VEHICLE_MODEL,
  _$referenceValueResponseDomainEnum_LICENCE_CATEGORY,
]);

Serializer<ReferenceValueResponseDomainEnum>
    _$referenceValueResponseDomainEnumSerializer =
    _$ReferenceValueResponseDomainEnumSerializer();

class _$ReferenceValueResponseDomainEnumSerializer
    implements PrimitiveSerializer<ReferenceValueResponseDomainEnum> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'VEHICLE_MAKE': 'VEHICLE_MAKE',
    'VEHICLE_MODEL': 'VEHICLE_MODEL',
    'LICENCE_CATEGORY': 'LICENCE_CATEGORY',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'VEHICLE_MAKE': 'VEHICLE_MAKE',
    'VEHICLE_MODEL': 'VEHICLE_MODEL',
    'LICENCE_CATEGORY': 'LICENCE_CATEGORY',
  };

  @override
  final Iterable<Type> types = const <Type>[ReferenceValueResponseDomainEnum];
  @override
  final String wireName = 'ReferenceValueResponseDomainEnum';

  @override
  Object serialize(
          Serializers serializers, ReferenceValueResponseDomainEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  ReferenceValueResponseDomainEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      ReferenceValueResponseDomainEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$ReferenceValueResponse extends ReferenceValueResponse {
  @override
  final bool active;
  @override
  final String code;
  @override
  final ReferenceValueResponseDomainEnum domain;
  @override
  final String id;
  @override
  final String? labelEn;
  @override
  final String labelPt;
  @override
  final String? parentCode;
  @override
  final int sortOrder;
  @override
  final DateTime updatedAt;
  @override
  final int version;

  factory _$ReferenceValueResponse(
          [void Function(ReferenceValueResponseBuilder)? updates]) =>
      (ReferenceValueResponseBuilder()..update(updates))._build();

  _$ReferenceValueResponse._(
      {required this.active,
      required this.code,
      required this.domain,
      required this.id,
      this.labelEn,
      required this.labelPt,
      this.parentCode,
      required this.sortOrder,
      required this.updatedAt,
      required this.version})
      : super._();
  @override
  ReferenceValueResponse rebuild(
          void Function(ReferenceValueResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  ReferenceValueResponseBuilder toBuilder() =>
      ReferenceValueResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is ReferenceValueResponse &&
        active == other.active &&
        code == other.code &&
        domain == other.domain &&
        id == other.id &&
        labelEn == other.labelEn &&
        labelPt == other.labelPt &&
        parentCode == other.parentCode &&
        sortOrder == other.sortOrder &&
        updatedAt == other.updatedAt &&
        version == other.version;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, active.hashCode);
    _$hash = $jc(_$hash, code.hashCode);
    _$hash = $jc(_$hash, domain.hashCode);
    _$hash = $jc(_$hash, id.hashCode);
    _$hash = $jc(_$hash, labelEn.hashCode);
    _$hash = $jc(_$hash, labelPt.hashCode);
    _$hash = $jc(_$hash, parentCode.hashCode);
    _$hash = $jc(_$hash, sortOrder.hashCode);
    _$hash = $jc(_$hash, updatedAt.hashCode);
    _$hash = $jc(_$hash, version.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'ReferenceValueResponse')
          ..add('active', active)
          ..add('code', code)
          ..add('domain', domain)
          ..add('id', id)
          ..add('labelEn', labelEn)
          ..add('labelPt', labelPt)
          ..add('parentCode', parentCode)
          ..add('sortOrder', sortOrder)
          ..add('updatedAt', updatedAt)
          ..add('version', version))
        .toString();
  }
}

class ReferenceValueResponseBuilder
    implements Builder<ReferenceValueResponse, ReferenceValueResponseBuilder> {
  _$ReferenceValueResponse? _$v;

  bool? _active;
  bool? get active => _$this._active;
  set active(bool? active) => _$this._active = active;

  String? _code;
  String? get code => _$this._code;
  set code(String? code) => _$this._code = code;

  ReferenceValueResponseDomainEnum? _domain;
  ReferenceValueResponseDomainEnum? get domain => _$this._domain;
  set domain(ReferenceValueResponseDomainEnum? domain) =>
      _$this._domain = domain;

  String? _id;
  String? get id => _$this._id;
  set id(String? id) => _$this._id = id;

  String? _labelEn;
  String? get labelEn => _$this._labelEn;
  set labelEn(String? labelEn) => _$this._labelEn = labelEn;

  String? _labelPt;
  String? get labelPt => _$this._labelPt;
  set labelPt(String? labelPt) => _$this._labelPt = labelPt;

  String? _parentCode;
  String? get parentCode => _$this._parentCode;
  set parentCode(String? parentCode) => _$this._parentCode = parentCode;

  int? _sortOrder;
  int? get sortOrder => _$this._sortOrder;
  set sortOrder(int? sortOrder) => _$this._sortOrder = sortOrder;

  DateTime? _updatedAt;
  DateTime? get updatedAt => _$this._updatedAt;
  set updatedAt(DateTime? updatedAt) => _$this._updatedAt = updatedAt;

  int? _version;
  int? get version => _$this._version;
  set version(int? version) => _$this._version = version;

  ReferenceValueResponseBuilder() {
    ReferenceValueResponse._defaults(this);
  }

  ReferenceValueResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _active = $v.active;
      _code = $v.code;
      _domain = $v.domain;
      _id = $v.id;
      _labelEn = $v.labelEn;
      _labelPt = $v.labelPt;
      _parentCode = $v.parentCode;
      _sortOrder = $v.sortOrder;
      _updatedAt = $v.updatedAt;
      _version = $v.version;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(ReferenceValueResponse other) {
    _$v = other as _$ReferenceValueResponse;
  }

  @override
  void update(void Function(ReferenceValueResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  ReferenceValueResponse build() => _build();

  _$ReferenceValueResponse _build() {
    final _$result = _$v ??
        _$ReferenceValueResponse._(
          active: BuiltValueNullFieldError.checkNotNull(
              active, r'ReferenceValueResponse', 'active'),
          code: BuiltValueNullFieldError.checkNotNull(
              code, r'ReferenceValueResponse', 'code'),
          domain: BuiltValueNullFieldError.checkNotNull(
              domain, r'ReferenceValueResponse', 'domain'),
          id: BuiltValueNullFieldError.checkNotNull(
              id, r'ReferenceValueResponse', 'id'),
          labelEn: labelEn,
          labelPt: BuiltValueNullFieldError.checkNotNull(
              labelPt, r'ReferenceValueResponse', 'labelPt'),
          parentCode: parentCode,
          sortOrder: BuiltValueNullFieldError.checkNotNull(
              sortOrder, r'ReferenceValueResponse', 'sortOrder'),
          updatedAt: BuiltValueNullFieldError.checkNotNull(
              updatedAt, r'ReferenceValueResponse', 'updatedAt'),
          version: BuiltValueNullFieldError.checkNotNull(
              version, r'ReferenceValueResponse', 'version'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
