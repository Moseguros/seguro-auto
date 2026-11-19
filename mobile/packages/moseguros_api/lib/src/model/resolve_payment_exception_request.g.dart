// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'resolve_payment_exception_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const ResolvePaymentExceptionRequestResolutionEnum
    _$resolvePaymentExceptionRequestResolutionEnum_LINKED =
    const ResolvePaymentExceptionRequestResolutionEnum._('LINKED');
const ResolvePaymentExceptionRequestResolutionEnum
    _$resolvePaymentExceptionRequestResolutionEnum_REFUND =
    const ResolvePaymentExceptionRequestResolutionEnum._('REFUND');
const ResolvePaymentExceptionRequestResolutionEnum
    _$resolvePaymentExceptionRequestResolutionEnum_IGNORED =
    const ResolvePaymentExceptionRequestResolutionEnum._('IGNORED');
const ResolvePaymentExceptionRequestResolutionEnum
    _$resolvePaymentExceptionRequestResolutionEnum_MARK_SUCCEEDED =
    const ResolvePaymentExceptionRequestResolutionEnum._('MARK_SUCCEEDED');
const ResolvePaymentExceptionRequestResolutionEnum
    _$resolvePaymentExceptionRequestResolutionEnum_MARK_FAILED =
    const ResolvePaymentExceptionRequestResolutionEnum._('MARK_FAILED');

ResolvePaymentExceptionRequestResolutionEnum
    _$resolvePaymentExceptionRequestResolutionEnumValueOf(String name) {
  switch (name) {
    case 'LINKED':
      return _$resolvePaymentExceptionRequestResolutionEnum_LINKED;
    case 'REFUND':
      return _$resolvePaymentExceptionRequestResolutionEnum_REFUND;
    case 'IGNORED':
      return _$resolvePaymentExceptionRequestResolutionEnum_IGNORED;
    case 'MARK_SUCCEEDED':
      return _$resolvePaymentExceptionRequestResolutionEnum_MARK_SUCCEEDED;
    case 'MARK_FAILED':
      return _$resolvePaymentExceptionRequestResolutionEnum_MARK_FAILED;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<ResolvePaymentExceptionRequestResolutionEnum>
    _$resolvePaymentExceptionRequestResolutionEnumValues = BuiltSet<
        ResolvePaymentExceptionRequestResolutionEnum>(const <ResolvePaymentExceptionRequestResolutionEnum>[
  _$resolvePaymentExceptionRequestResolutionEnum_LINKED,
  _$resolvePaymentExceptionRequestResolutionEnum_REFUND,
  _$resolvePaymentExceptionRequestResolutionEnum_IGNORED,
  _$resolvePaymentExceptionRequestResolutionEnum_MARK_SUCCEEDED,
  _$resolvePaymentExceptionRequestResolutionEnum_MARK_FAILED,
]);

Serializer<ResolvePaymentExceptionRequestResolutionEnum>
    _$resolvePaymentExceptionRequestResolutionEnumSerializer =
    _$ResolvePaymentExceptionRequestResolutionEnumSerializer();

class _$ResolvePaymentExceptionRequestResolutionEnumSerializer
    implements
        PrimitiveSerializer<ResolvePaymentExceptionRequestResolutionEnum> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'LINKED': 'LINKED',
    'REFUND': 'REFUND',
    'IGNORED': 'IGNORED',
    'MARK_SUCCEEDED': 'MARK_SUCCEEDED',
    'MARK_FAILED': 'MARK_FAILED',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'LINKED': 'LINKED',
    'REFUND': 'REFUND',
    'IGNORED': 'IGNORED',
    'MARK_SUCCEEDED': 'MARK_SUCCEEDED',
    'MARK_FAILED': 'MARK_FAILED',
  };

  @override
  final Iterable<Type> types = const <Type>[
    ResolvePaymentExceptionRequestResolutionEnum
  ];
  @override
  final String wireName = 'ResolvePaymentExceptionRequestResolutionEnum';

  @override
  Object serialize(Serializers serializers,
          ResolvePaymentExceptionRequestResolutionEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  ResolvePaymentExceptionRequestResolutionEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      ResolvePaymentExceptionRequestResolutionEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$ResolvePaymentExceptionRequest extends ResolvePaymentExceptionRequest {
  @override
  final String? intentId;
  @override
  final String note;
  @override
  final String? providerTxId;
  @override
  final ResolvePaymentExceptionRequestResolutionEnum resolution;
  @override
  final int version;

  factory _$ResolvePaymentExceptionRequest(
          [void Function(ResolvePaymentExceptionRequestBuilder)? updates]) =>
      (ResolvePaymentExceptionRequestBuilder()..update(updates))._build();

  _$ResolvePaymentExceptionRequest._(
      {this.intentId,
      required this.note,
      this.providerTxId,
      required this.resolution,
      required this.version})
      : super._();
  @override
  ResolvePaymentExceptionRequest rebuild(
          void Function(ResolvePaymentExceptionRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  ResolvePaymentExceptionRequestBuilder toBuilder() =>
      ResolvePaymentExceptionRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is ResolvePaymentExceptionRequest &&
        intentId == other.intentId &&
        note == other.note &&
        providerTxId == other.providerTxId &&
        resolution == other.resolution &&
        version == other.version;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, intentId.hashCode);
    _$hash = $jc(_$hash, note.hashCode);
    _$hash = $jc(_$hash, providerTxId.hashCode);
    _$hash = $jc(_$hash, resolution.hashCode);
    _$hash = $jc(_$hash, version.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'ResolvePaymentExceptionRequest')
          ..add('intentId', intentId)
          ..add('note', note)
          ..add('providerTxId', providerTxId)
          ..add('resolution', resolution)
          ..add('version', version))
        .toString();
  }
}

class ResolvePaymentExceptionRequestBuilder
    implements
        Builder<ResolvePaymentExceptionRequest,
            ResolvePaymentExceptionRequestBuilder> {
  _$ResolvePaymentExceptionRequest? _$v;

  String? _intentId;
  String? get intentId => _$this._intentId;
  set intentId(String? intentId) => _$this._intentId = intentId;

  String? _note;
  String? get note => _$this._note;
  set note(String? note) => _$this._note = note;

  String? _providerTxId;
  String? get providerTxId => _$this._providerTxId;
  set providerTxId(String? providerTxId) => _$this._providerTxId = providerTxId;

  ResolvePaymentExceptionRequestResolutionEnum? _resolution;
  ResolvePaymentExceptionRequestResolutionEnum? get resolution =>
      _$this._resolution;
  set resolution(ResolvePaymentExceptionRequestResolutionEnum? resolution) =>
      _$this._resolution = resolution;

  int? _version;
  int? get version => _$this._version;
  set version(int? version) => _$this._version = version;

  ResolvePaymentExceptionRequestBuilder() {
    ResolvePaymentExceptionRequest._defaults(this);
  }

  ResolvePaymentExceptionRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _intentId = $v.intentId;
      _note = $v.note;
      _providerTxId = $v.providerTxId;
      _resolution = $v.resolution;
      _version = $v.version;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(ResolvePaymentExceptionRequest other) {
    _$v = other as _$ResolvePaymentExceptionRequest;
  }

  @override
  void update(void Function(ResolvePaymentExceptionRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  ResolvePaymentExceptionRequest build() => _build();

  _$ResolvePaymentExceptionRequest _build() {
    final _$result = _$v ??
        _$ResolvePaymentExceptionRequest._(
          intentId: intentId,
          note: BuiltValueNullFieldError.checkNotNull(
              note, r'ResolvePaymentExceptionRequest', 'note'),
          providerTxId: providerTxId,
          resolution: BuiltValueNullFieldError.checkNotNull(
              resolution, r'ResolvePaymentExceptionRequest', 'resolution'),
          version: BuiltValueNullFieldError.checkNotNull(
              version, r'ResolvePaymentExceptionRequest', 'version'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
