// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'admin_payment_file_source.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const AdminPaymentFileSourceFileFormatEnum
    _$adminPaymentFileSourceFileFormatEnum_MEPS =
    const AdminPaymentFileSourceFileFormatEnum._('MEPS');
const AdminPaymentFileSourceFileFormatEnum
    _$adminPaymentFileSourceFileFormatEnum_BMEPS =
    const AdminPaymentFileSourceFileFormatEnum._('BMEPS');

AdminPaymentFileSourceFileFormatEnum
    _$adminPaymentFileSourceFileFormatEnumValueOf(String name) {
  switch (name) {
    case 'MEPS':
      return _$adminPaymentFileSourceFileFormatEnum_MEPS;
    case 'BMEPS':
      return _$adminPaymentFileSourceFileFormatEnum_BMEPS;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<AdminPaymentFileSourceFileFormatEnum>
    _$adminPaymentFileSourceFileFormatEnumValues = BuiltSet<
        AdminPaymentFileSourceFileFormatEnum>(const <AdminPaymentFileSourceFileFormatEnum>[
  _$adminPaymentFileSourceFileFormatEnum_MEPS,
  _$adminPaymentFileSourceFileFormatEnum_BMEPS,
]);

const AdminPaymentFileSourceLastPollOutcomeEnum
    _$adminPaymentFileSourceLastPollOutcomeEnum_OK =
    const AdminPaymentFileSourceLastPollOutcomeEnum._('OK');
const AdminPaymentFileSourceLastPollOutcomeEnum
    _$adminPaymentFileSourceLastPollOutcomeEnum_ERROR =
    const AdminPaymentFileSourceLastPollOutcomeEnum._('ERROR');

AdminPaymentFileSourceLastPollOutcomeEnum
    _$adminPaymentFileSourceLastPollOutcomeEnumValueOf(String name) {
  switch (name) {
    case 'OK':
      return _$adminPaymentFileSourceLastPollOutcomeEnum_OK;
    case 'ERROR':
      return _$adminPaymentFileSourceLastPollOutcomeEnum_ERROR;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<AdminPaymentFileSourceLastPollOutcomeEnum>
    _$adminPaymentFileSourceLastPollOutcomeEnumValues = BuiltSet<
        AdminPaymentFileSourceLastPollOutcomeEnum>(const <AdminPaymentFileSourceLastPollOutcomeEnum>[
  _$adminPaymentFileSourceLastPollOutcomeEnum_OK,
  _$adminPaymentFileSourceLastPollOutcomeEnum_ERROR,
]);

Serializer<AdminPaymentFileSourceFileFormatEnum>
    _$adminPaymentFileSourceFileFormatEnumSerializer =
    _$AdminPaymentFileSourceFileFormatEnumSerializer();
Serializer<AdminPaymentFileSourceLastPollOutcomeEnum>
    _$adminPaymentFileSourceLastPollOutcomeEnumSerializer =
    _$AdminPaymentFileSourceLastPollOutcomeEnumSerializer();

class _$AdminPaymentFileSourceFileFormatEnumSerializer
    implements PrimitiveSerializer<AdminPaymentFileSourceFileFormatEnum> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'MEPS': 'MEPS',
    'BMEPS': 'BMEPS',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'MEPS': 'MEPS',
    'BMEPS': 'BMEPS',
  };

  @override
  final Iterable<Type> types = const <Type>[
    AdminPaymentFileSourceFileFormatEnum
  ];
  @override
  final String wireName = 'AdminPaymentFileSourceFileFormatEnum';

  @override
  Object serialize(
          Serializers serializers, AdminPaymentFileSourceFileFormatEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  AdminPaymentFileSourceFileFormatEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      AdminPaymentFileSourceFileFormatEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$AdminPaymentFileSourceLastPollOutcomeEnumSerializer
    implements PrimitiveSerializer<AdminPaymentFileSourceLastPollOutcomeEnum> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'OK': 'OK',
    'ERROR': 'ERROR',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'OK': 'OK',
    'ERROR': 'ERROR',
  };

  @override
  final Iterable<Type> types = const <Type>[
    AdminPaymentFileSourceLastPollOutcomeEnum
  ];
  @override
  final String wireName = 'AdminPaymentFileSourceLastPollOutcomeEnum';

  @override
  Object serialize(Serializers serializers,
          AdminPaymentFileSourceLastPollOutcomeEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  AdminPaymentFileSourceLastPollOutcomeEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      AdminPaymentFileSourceLastPollOutcomeEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$AdminPaymentFileSource extends AdminPaymentFileSource {
  @override
  final bool enabled;
  @override
  final String? entity;
  @override
  final AdminPaymentFileSourceFileFormatEnum? fileFormat;
  @override
  final int filesLast24h;
  @override
  final PaymentFileSourceInsurer insurer;
  @override
  final DateTime? lastPollAt;
  @override
  final String? lastPollError;
  @override
  final AdminPaymentFileSourceLastPollOutcomeEnum? lastPollOutcome;
  @override
  final DateTime? lastSuccessAt;
  @override
  final int rejectedLast24h;
  @override
  final String sourceId;
  @override
  final DateTime? staleSince;

  factory _$AdminPaymentFileSource(
          [void Function(AdminPaymentFileSourceBuilder)? updates]) =>
      (AdminPaymentFileSourceBuilder()..update(updates))._build();

  _$AdminPaymentFileSource._(
      {required this.enabled,
      this.entity,
      this.fileFormat,
      required this.filesLast24h,
      required this.insurer,
      this.lastPollAt,
      this.lastPollError,
      this.lastPollOutcome,
      this.lastSuccessAt,
      required this.rejectedLast24h,
      required this.sourceId,
      this.staleSince})
      : super._();
  @override
  AdminPaymentFileSource rebuild(
          void Function(AdminPaymentFileSourceBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  AdminPaymentFileSourceBuilder toBuilder() =>
      AdminPaymentFileSourceBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is AdminPaymentFileSource &&
        enabled == other.enabled &&
        entity == other.entity &&
        fileFormat == other.fileFormat &&
        filesLast24h == other.filesLast24h &&
        insurer == other.insurer &&
        lastPollAt == other.lastPollAt &&
        lastPollError == other.lastPollError &&
        lastPollOutcome == other.lastPollOutcome &&
        lastSuccessAt == other.lastSuccessAt &&
        rejectedLast24h == other.rejectedLast24h &&
        sourceId == other.sourceId &&
        staleSince == other.staleSince;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, enabled.hashCode);
    _$hash = $jc(_$hash, entity.hashCode);
    _$hash = $jc(_$hash, fileFormat.hashCode);
    _$hash = $jc(_$hash, filesLast24h.hashCode);
    _$hash = $jc(_$hash, insurer.hashCode);
    _$hash = $jc(_$hash, lastPollAt.hashCode);
    _$hash = $jc(_$hash, lastPollError.hashCode);
    _$hash = $jc(_$hash, lastPollOutcome.hashCode);
    _$hash = $jc(_$hash, lastSuccessAt.hashCode);
    _$hash = $jc(_$hash, rejectedLast24h.hashCode);
    _$hash = $jc(_$hash, sourceId.hashCode);
    _$hash = $jc(_$hash, staleSince.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'AdminPaymentFileSource')
          ..add('enabled', enabled)
          ..add('entity', entity)
          ..add('fileFormat', fileFormat)
          ..add('filesLast24h', filesLast24h)
          ..add('insurer', insurer)
          ..add('lastPollAt', lastPollAt)
          ..add('lastPollError', lastPollError)
          ..add('lastPollOutcome', lastPollOutcome)
          ..add('lastSuccessAt', lastSuccessAt)
          ..add('rejectedLast24h', rejectedLast24h)
          ..add('sourceId', sourceId)
          ..add('staleSince', staleSince))
        .toString();
  }
}

class AdminPaymentFileSourceBuilder
    implements Builder<AdminPaymentFileSource, AdminPaymentFileSourceBuilder> {
  _$AdminPaymentFileSource? _$v;

  bool? _enabled;
  bool? get enabled => _$this._enabled;
  set enabled(bool? enabled) => _$this._enabled = enabled;

  String? _entity;
  String? get entity => _$this._entity;
  set entity(String? entity) => _$this._entity = entity;

  AdminPaymentFileSourceFileFormatEnum? _fileFormat;
  AdminPaymentFileSourceFileFormatEnum? get fileFormat => _$this._fileFormat;
  set fileFormat(AdminPaymentFileSourceFileFormatEnum? fileFormat) =>
      _$this._fileFormat = fileFormat;

  int? _filesLast24h;
  int? get filesLast24h => _$this._filesLast24h;
  set filesLast24h(int? filesLast24h) => _$this._filesLast24h = filesLast24h;

  PaymentFileSourceInsurerBuilder? _insurer;
  PaymentFileSourceInsurerBuilder get insurer =>
      _$this._insurer ??= PaymentFileSourceInsurerBuilder();
  set insurer(PaymentFileSourceInsurerBuilder? insurer) =>
      _$this._insurer = insurer;

  DateTime? _lastPollAt;
  DateTime? get lastPollAt => _$this._lastPollAt;
  set lastPollAt(DateTime? lastPollAt) => _$this._lastPollAt = lastPollAt;

  String? _lastPollError;
  String? get lastPollError => _$this._lastPollError;
  set lastPollError(String? lastPollError) =>
      _$this._lastPollError = lastPollError;

  AdminPaymentFileSourceLastPollOutcomeEnum? _lastPollOutcome;
  AdminPaymentFileSourceLastPollOutcomeEnum? get lastPollOutcome =>
      _$this._lastPollOutcome;
  set lastPollOutcome(
          AdminPaymentFileSourceLastPollOutcomeEnum? lastPollOutcome) =>
      _$this._lastPollOutcome = lastPollOutcome;

  DateTime? _lastSuccessAt;
  DateTime? get lastSuccessAt => _$this._lastSuccessAt;
  set lastSuccessAt(DateTime? lastSuccessAt) =>
      _$this._lastSuccessAt = lastSuccessAt;

  int? _rejectedLast24h;
  int? get rejectedLast24h => _$this._rejectedLast24h;
  set rejectedLast24h(int? rejectedLast24h) =>
      _$this._rejectedLast24h = rejectedLast24h;

  String? _sourceId;
  String? get sourceId => _$this._sourceId;
  set sourceId(String? sourceId) => _$this._sourceId = sourceId;

  DateTime? _staleSince;
  DateTime? get staleSince => _$this._staleSince;
  set staleSince(DateTime? staleSince) => _$this._staleSince = staleSince;

  AdminPaymentFileSourceBuilder() {
    AdminPaymentFileSource._defaults(this);
  }

  AdminPaymentFileSourceBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _enabled = $v.enabled;
      _entity = $v.entity;
      _fileFormat = $v.fileFormat;
      _filesLast24h = $v.filesLast24h;
      _insurer = $v.insurer.toBuilder();
      _lastPollAt = $v.lastPollAt;
      _lastPollError = $v.lastPollError;
      _lastPollOutcome = $v.lastPollOutcome;
      _lastSuccessAt = $v.lastSuccessAt;
      _rejectedLast24h = $v.rejectedLast24h;
      _sourceId = $v.sourceId;
      _staleSince = $v.staleSince;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(AdminPaymentFileSource other) {
    _$v = other as _$AdminPaymentFileSource;
  }

  @override
  void update(void Function(AdminPaymentFileSourceBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  AdminPaymentFileSource build() => _build();

  _$AdminPaymentFileSource _build() {
    _$AdminPaymentFileSource _$result;
    try {
      _$result = _$v ??
          _$AdminPaymentFileSource._(
            enabled: BuiltValueNullFieldError.checkNotNull(
                enabled, r'AdminPaymentFileSource', 'enabled'),
            entity: entity,
            fileFormat: fileFormat,
            filesLast24h: BuiltValueNullFieldError.checkNotNull(
                filesLast24h, r'AdminPaymentFileSource', 'filesLast24h'),
            insurer: insurer.build(),
            lastPollAt: lastPollAt,
            lastPollError: lastPollError,
            lastPollOutcome: lastPollOutcome,
            lastSuccessAt: lastSuccessAt,
            rejectedLast24h: BuiltValueNullFieldError.checkNotNull(
                rejectedLast24h, r'AdminPaymentFileSource', 'rejectedLast24h'),
            sourceId: BuiltValueNullFieldError.checkNotNull(
                sourceId, r'AdminPaymentFileSource', 'sourceId'),
            staleSince: staleSince,
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'insurer';
        insurer.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'AdminPaymentFileSource', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
