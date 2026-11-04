// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'proposal_checklist.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const ProposalChecklistDeclarationsEnum
    _$proposalChecklistDeclarationsEnum_DONE =
    const ProposalChecklistDeclarationsEnum._('DONE');
const ProposalChecklistDeclarationsEnum
    _$proposalChecklistDeclarationsEnum_MISSING =
    const ProposalChecklistDeclarationsEnum._('MISSING');

ProposalChecklistDeclarationsEnum _$proposalChecklistDeclarationsEnumValueOf(
    String name) {
  switch (name) {
    case 'DONE':
      return _$proposalChecklistDeclarationsEnum_DONE;
    case 'MISSING':
      return _$proposalChecklistDeclarationsEnum_MISSING;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<ProposalChecklistDeclarationsEnum>
    _$proposalChecklistDeclarationsEnumValues = BuiltSet<
        ProposalChecklistDeclarationsEnum>(const <ProposalChecklistDeclarationsEnum>[
  _$proposalChecklistDeclarationsEnum_DONE,
  _$proposalChecklistDeclarationsEnum_MISSING,
]);

const ProposalChecklistInspectionEnum
    _$proposalChecklistInspectionEnum_NOT_REQUIRED =
    const ProposalChecklistInspectionEnum._('NOT_REQUIRED');
const ProposalChecklistInspectionEnum
    _$proposalChecklistInspectionEnum_MISSING =
    const ProposalChecklistInspectionEnum._('MISSING');
const ProposalChecklistInspectionEnum _$proposalChecklistInspectionEnum_DONE =
    const ProposalChecklistInspectionEnum._('DONE');
const ProposalChecklistInspectionEnum
    _$proposalChecklistInspectionEnum_REJECTED =
    const ProposalChecklistInspectionEnum._('REJECTED');

ProposalChecklistInspectionEnum _$proposalChecklistInspectionEnumValueOf(
    String name) {
  switch (name) {
    case 'NOT_REQUIRED':
      return _$proposalChecklistInspectionEnum_NOT_REQUIRED;
    case 'MISSING':
      return _$proposalChecklistInspectionEnum_MISSING;
    case 'DONE':
      return _$proposalChecklistInspectionEnum_DONE;
    case 'REJECTED':
      return _$proposalChecklistInspectionEnum_REJECTED;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<ProposalChecklistInspectionEnum>
    _$proposalChecklistInspectionEnumValues = BuiltSet<
        ProposalChecklistInspectionEnum>(const <ProposalChecklistInspectionEnum>[
  _$proposalChecklistInspectionEnum_NOT_REQUIRED,
  _$proposalChecklistInspectionEnum_MISSING,
  _$proposalChecklistInspectionEnum_DONE,
  _$proposalChecklistInspectionEnum_REJECTED,
]);

const ProposalChecklistSignatureEnum _$proposalChecklistSignatureEnum_MISSING =
    const ProposalChecklistSignatureEnum._('MISSING');
const ProposalChecklistSignatureEnum _$proposalChecklistSignatureEnum_DONE =
    const ProposalChecklistSignatureEnum._('DONE');

ProposalChecklistSignatureEnum _$proposalChecklistSignatureEnumValueOf(
    String name) {
  switch (name) {
    case 'MISSING':
      return _$proposalChecklistSignatureEnum_MISSING;
    case 'DONE':
      return _$proposalChecklistSignatureEnum_DONE;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<ProposalChecklistSignatureEnum>
    _$proposalChecklistSignatureEnumValues = BuiltSet<
        ProposalChecklistSignatureEnum>(const <ProposalChecklistSignatureEnum>[
  _$proposalChecklistSignatureEnum_MISSING,
  _$proposalChecklistSignatureEnum_DONE,
]);

const ProposalChecklistSuitabilityEnum _$proposalChecklistSuitabilityEnum_DONE =
    const ProposalChecklistSuitabilityEnum._('DONE');
const ProposalChecklistSuitabilityEnum
    _$proposalChecklistSuitabilityEnum_MISSING =
    const ProposalChecklistSuitabilityEnum._('MISSING');
const ProposalChecklistSuitabilityEnum
    _$proposalChecklistSuitabilityEnum_ACK_REQUIRED =
    const ProposalChecklistSuitabilityEnum._('ACK_REQUIRED');

ProposalChecklistSuitabilityEnum _$proposalChecklistSuitabilityEnumValueOf(
    String name) {
  switch (name) {
    case 'DONE':
      return _$proposalChecklistSuitabilityEnum_DONE;
    case 'MISSING':
      return _$proposalChecklistSuitabilityEnum_MISSING;
    case 'ACK_REQUIRED':
      return _$proposalChecklistSuitabilityEnum_ACK_REQUIRED;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<ProposalChecklistSuitabilityEnum>
    _$proposalChecklistSuitabilityEnumValues = BuiltSet<
        ProposalChecklistSuitabilityEnum>(const <ProposalChecklistSuitabilityEnum>[
  _$proposalChecklistSuitabilityEnum_DONE,
  _$proposalChecklistSuitabilityEnum_MISSING,
  _$proposalChecklistSuitabilityEnum_ACK_REQUIRED,
]);

Serializer<ProposalChecklistDeclarationsEnum>
    _$proposalChecklistDeclarationsEnumSerializer =
    _$ProposalChecklistDeclarationsEnumSerializer();
Serializer<ProposalChecklistInspectionEnum>
    _$proposalChecklistInspectionEnumSerializer =
    _$ProposalChecklistInspectionEnumSerializer();
Serializer<ProposalChecklistSignatureEnum>
    _$proposalChecklistSignatureEnumSerializer =
    _$ProposalChecklistSignatureEnumSerializer();
Serializer<ProposalChecklistSuitabilityEnum>
    _$proposalChecklistSuitabilityEnumSerializer =
    _$ProposalChecklistSuitabilityEnumSerializer();

class _$ProposalChecklistDeclarationsEnumSerializer
    implements PrimitiveSerializer<ProposalChecklistDeclarationsEnum> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'DONE': 'DONE',
    'MISSING': 'MISSING',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'DONE': 'DONE',
    'MISSING': 'MISSING',
  };

  @override
  final Iterable<Type> types = const <Type>[ProposalChecklistDeclarationsEnum];
  @override
  final String wireName = 'ProposalChecklistDeclarationsEnum';

  @override
  Object serialize(
          Serializers serializers, ProposalChecklistDeclarationsEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  ProposalChecklistDeclarationsEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      ProposalChecklistDeclarationsEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$ProposalChecklistInspectionEnumSerializer
    implements PrimitiveSerializer<ProposalChecklistInspectionEnum> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'NOT_REQUIRED': 'NOT_REQUIRED',
    'MISSING': 'MISSING',
    'DONE': 'DONE',
    'REJECTED': 'REJECTED',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'NOT_REQUIRED': 'NOT_REQUIRED',
    'MISSING': 'MISSING',
    'DONE': 'DONE',
    'REJECTED': 'REJECTED',
  };

  @override
  final Iterable<Type> types = const <Type>[ProposalChecklistInspectionEnum];
  @override
  final String wireName = 'ProposalChecklistInspectionEnum';

  @override
  Object serialize(
          Serializers serializers, ProposalChecklistInspectionEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  ProposalChecklistInspectionEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      ProposalChecklistInspectionEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$ProposalChecklistSignatureEnumSerializer
    implements PrimitiveSerializer<ProposalChecklistSignatureEnum> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'MISSING': 'MISSING',
    'DONE': 'DONE',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'MISSING': 'MISSING',
    'DONE': 'DONE',
  };

  @override
  final Iterable<Type> types = const <Type>[ProposalChecklistSignatureEnum];
  @override
  final String wireName = 'ProposalChecklistSignatureEnum';

  @override
  Object serialize(
          Serializers serializers, ProposalChecklistSignatureEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  ProposalChecklistSignatureEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      ProposalChecklistSignatureEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$ProposalChecklistSuitabilityEnumSerializer
    implements PrimitiveSerializer<ProposalChecklistSuitabilityEnum> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'DONE': 'DONE',
    'MISSING': 'MISSING',
    'ACK_REQUIRED': 'ACK_REQUIRED',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'DONE': 'DONE',
    'MISSING': 'MISSING',
    'ACK_REQUIRED': 'ACK_REQUIRED',
  };

  @override
  final Iterable<Type> types = const <Type>[ProposalChecklistSuitabilityEnum];
  @override
  final String wireName = 'ProposalChecklistSuitabilityEnum';

  @override
  Object serialize(
          Serializers serializers, ProposalChecklistSuitabilityEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  ProposalChecklistSuitabilityEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      ProposalChecklistSuitabilityEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$ProposalChecklist extends ProposalChecklist {
  @override
  final ProposalChecklistDeclarationsEnum declarations;
  @override
  final ProposalChecklistInspectionEnum inspection;
  @override
  final ProposalChecklistSignatureEnum signature;
  @override
  final ProposalChecklistSuitabilityEnum suitability;

  factory _$ProposalChecklist(
          [void Function(ProposalChecklistBuilder)? updates]) =>
      (ProposalChecklistBuilder()..update(updates))._build();

  _$ProposalChecklist._(
      {required this.declarations,
      required this.inspection,
      required this.signature,
      required this.suitability})
      : super._();
  @override
  ProposalChecklist rebuild(void Function(ProposalChecklistBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  ProposalChecklistBuilder toBuilder() =>
      ProposalChecklistBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is ProposalChecklist &&
        declarations == other.declarations &&
        inspection == other.inspection &&
        signature == other.signature &&
        suitability == other.suitability;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, declarations.hashCode);
    _$hash = $jc(_$hash, inspection.hashCode);
    _$hash = $jc(_$hash, signature.hashCode);
    _$hash = $jc(_$hash, suitability.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'ProposalChecklist')
          ..add('declarations', declarations)
          ..add('inspection', inspection)
          ..add('signature', signature)
          ..add('suitability', suitability))
        .toString();
  }
}

class ProposalChecklistBuilder
    implements Builder<ProposalChecklist, ProposalChecklistBuilder> {
  _$ProposalChecklist? _$v;

  ProposalChecklistDeclarationsEnum? _declarations;
  ProposalChecklistDeclarationsEnum? get declarations => _$this._declarations;
  set declarations(ProposalChecklistDeclarationsEnum? declarations) =>
      _$this._declarations = declarations;

  ProposalChecklistInspectionEnum? _inspection;
  ProposalChecklistInspectionEnum? get inspection => _$this._inspection;
  set inspection(ProposalChecklistInspectionEnum? inspection) =>
      _$this._inspection = inspection;

  ProposalChecklistSignatureEnum? _signature;
  ProposalChecklistSignatureEnum? get signature => _$this._signature;
  set signature(ProposalChecklistSignatureEnum? signature) =>
      _$this._signature = signature;

  ProposalChecklistSuitabilityEnum? _suitability;
  ProposalChecklistSuitabilityEnum? get suitability => _$this._suitability;
  set suitability(ProposalChecklistSuitabilityEnum? suitability) =>
      _$this._suitability = suitability;

  ProposalChecklistBuilder() {
    ProposalChecklist._defaults(this);
  }

  ProposalChecklistBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _declarations = $v.declarations;
      _inspection = $v.inspection;
      _signature = $v.signature;
      _suitability = $v.suitability;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(ProposalChecklist other) {
    _$v = other as _$ProposalChecklist;
  }

  @override
  void update(void Function(ProposalChecklistBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  ProposalChecklist build() => _build();

  _$ProposalChecklist _build() {
    final _$result = _$v ??
        _$ProposalChecklist._(
          declarations: BuiltValueNullFieldError.checkNotNull(
              declarations, r'ProposalChecklist', 'declarations'),
          inspection: BuiltValueNullFieldError.checkNotNull(
              inspection, r'ProposalChecklist', 'inspection'),
          signature: BuiltValueNullFieldError.checkNotNull(
              signature, r'ProposalChecklist', 'signature'),
          suitability: BuiltValueNullFieldError.checkNotNull(
              suitability, r'ProposalChecklist', 'suitability'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
