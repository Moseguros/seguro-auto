// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'proposal_product_document.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const ProposalProductDocumentTypeEnum
    _$proposalProductDocumentTypeEnum_GENERAL_CONDITIONS =
    const ProposalProductDocumentTypeEnum._('GENERAL_CONDITIONS');
const ProposalProductDocumentTypeEnum
    _$proposalProductDocumentTypeEnum_PRE_CONTRACT_INFO =
    const ProposalProductDocumentTypeEnum._('PRE_CONTRACT_INFO');
const ProposalProductDocumentTypeEnum
    _$proposalProductDocumentTypeEnum_SPECIAL_CONDITIONS =
    const ProposalProductDocumentTypeEnum._('SPECIAL_CONDITIONS');
const ProposalProductDocumentTypeEnum _$proposalProductDocumentTypeEnum_OTHER =
    const ProposalProductDocumentTypeEnum._('OTHER');

ProposalProductDocumentTypeEnum _$proposalProductDocumentTypeEnumValueOf(
    String name) {
  switch (name) {
    case 'GENERAL_CONDITIONS':
      return _$proposalProductDocumentTypeEnum_GENERAL_CONDITIONS;
    case 'PRE_CONTRACT_INFO':
      return _$proposalProductDocumentTypeEnum_PRE_CONTRACT_INFO;
    case 'SPECIAL_CONDITIONS':
      return _$proposalProductDocumentTypeEnum_SPECIAL_CONDITIONS;
    case 'OTHER':
      return _$proposalProductDocumentTypeEnum_OTHER;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<ProposalProductDocumentTypeEnum>
    _$proposalProductDocumentTypeEnumValues = BuiltSet<
        ProposalProductDocumentTypeEnum>(const <ProposalProductDocumentTypeEnum>[
  _$proposalProductDocumentTypeEnum_GENERAL_CONDITIONS,
  _$proposalProductDocumentTypeEnum_PRE_CONTRACT_INFO,
  _$proposalProductDocumentTypeEnum_SPECIAL_CONDITIONS,
  _$proposalProductDocumentTypeEnum_OTHER,
]);

Serializer<ProposalProductDocumentTypeEnum>
    _$proposalProductDocumentTypeEnumSerializer =
    _$ProposalProductDocumentTypeEnumSerializer();

class _$ProposalProductDocumentTypeEnumSerializer
    implements PrimitiveSerializer<ProposalProductDocumentTypeEnum> {
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
  final Iterable<Type> types = const <Type>[ProposalProductDocumentTypeEnum];
  @override
  final String wireName = 'ProposalProductDocumentTypeEnum';

  @override
  Object serialize(
          Serializers serializers, ProposalProductDocumentTypeEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  ProposalProductDocumentTypeEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      ProposalProductDocumentTypeEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$ProposalProductDocument extends ProposalProductDocument {
  @override
  final String fileId;
  @override
  final ProposalProductDocumentTypeEnum type;

  factory _$ProposalProductDocument(
          [void Function(ProposalProductDocumentBuilder)? updates]) =>
      (ProposalProductDocumentBuilder()..update(updates))._build();

  _$ProposalProductDocument._({required this.fileId, required this.type})
      : super._();
  @override
  ProposalProductDocument rebuild(
          void Function(ProposalProductDocumentBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  ProposalProductDocumentBuilder toBuilder() =>
      ProposalProductDocumentBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is ProposalProductDocument &&
        fileId == other.fileId &&
        type == other.type;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, fileId.hashCode);
    _$hash = $jc(_$hash, type.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'ProposalProductDocument')
          ..add('fileId', fileId)
          ..add('type', type))
        .toString();
  }
}

class ProposalProductDocumentBuilder
    implements
        Builder<ProposalProductDocument, ProposalProductDocumentBuilder> {
  _$ProposalProductDocument? _$v;

  String? _fileId;
  String? get fileId => _$this._fileId;
  set fileId(String? fileId) => _$this._fileId = fileId;

  ProposalProductDocumentTypeEnum? _type;
  ProposalProductDocumentTypeEnum? get type => _$this._type;
  set type(ProposalProductDocumentTypeEnum? type) => _$this._type = type;

  ProposalProductDocumentBuilder() {
    ProposalProductDocument._defaults(this);
  }

  ProposalProductDocumentBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _fileId = $v.fileId;
      _type = $v.type;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(ProposalProductDocument other) {
    _$v = other as _$ProposalProductDocument;
  }

  @override
  void update(void Function(ProposalProductDocumentBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  ProposalProductDocument build() => _build();

  _$ProposalProductDocument _build() {
    final _$result = _$v ??
        _$ProposalProductDocument._(
          fileId: BuiltValueNullFieldError.checkNotNull(
              fileId, r'ProposalProductDocument', 'fileId'),
          type: BuiltValueNullFieldError.checkNotNull(
              type, r'ProposalProductDocument', 'type'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
