// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'payment_file_source_status.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const PaymentFileSourceStatusFileFormatEnum
    _$paymentFileSourceStatusFileFormatEnum_MEPS =
    const PaymentFileSourceStatusFileFormatEnum._('MEPS');
const PaymentFileSourceStatusFileFormatEnum
    _$paymentFileSourceStatusFileFormatEnum_BMEPS =
    const PaymentFileSourceStatusFileFormatEnum._('BMEPS');

PaymentFileSourceStatusFileFormatEnum
    _$paymentFileSourceStatusFileFormatEnumValueOf(String name) {
  switch (name) {
    case 'MEPS':
      return _$paymentFileSourceStatusFileFormatEnum_MEPS;
    case 'BMEPS':
      return _$paymentFileSourceStatusFileFormatEnum_BMEPS;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<PaymentFileSourceStatusFileFormatEnum>
    _$paymentFileSourceStatusFileFormatEnumValues = BuiltSet<
        PaymentFileSourceStatusFileFormatEnum>(const <PaymentFileSourceStatusFileFormatEnum>[
  _$paymentFileSourceStatusFileFormatEnum_MEPS,
  _$paymentFileSourceStatusFileFormatEnum_BMEPS,
]);

const PaymentFileSourceStatusLastPollOutcomeEnum
    _$paymentFileSourceStatusLastPollOutcomeEnum_OK =
    const PaymentFileSourceStatusLastPollOutcomeEnum._('OK');
const PaymentFileSourceStatusLastPollOutcomeEnum
    _$paymentFileSourceStatusLastPollOutcomeEnum_ERROR =
    const PaymentFileSourceStatusLastPollOutcomeEnum._('ERROR');

PaymentFileSourceStatusLastPollOutcomeEnum
    _$paymentFileSourceStatusLastPollOutcomeEnumValueOf(String name) {
  switch (name) {
    case 'OK':
      return _$paymentFileSourceStatusLastPollOutcomeEnum_OK;
    case 'ERROR':
      return _$paymentFileSourceStatusLastPollOutcomeEnum_ERROR;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<PaymentFileSourceStatusLastPollOutcomeEnum>
    _$paymentFileSourceStatusLastPollOutcomeEnumValues = BuiltSet<
        PaymentFileSourceStatusLastPollOutcomeEnum>(const <PaymentFileSourceStatusLastPollOutcomeEnum>[
  _$paymentFileSourceStatusLastPollOutcomeEnum_OK,
  _$paymentFileSourceStatusLastPollOutcomeEnum_ERROR,
]);

Serializer<PaymentFileSourceStatusFileFormatEnum>
    _$paymentFileSourceStatusFileFormatEnumSerializer =
    _$PaymentFileSourceStatusFileFormatEnumSerializer();
Serializer<PaymentFileSourceStatusLastPollOutcomeEnum>
    _$paymentFileSourceStatusLastPollOutcomeEnumSerializer =
    _$PaymentFileSourceStatusLastPollOutcomeEnumSerializer();

class _$PaymentFileSourceStatusFileFormatEnumSerializer
    implements PrimitiveSerializer<PaymentFileSourceStatusFileFormatEnum> {
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
    PaymentFileSourceStatusFileFormatEnum
  ];
  @override
  final String wireName = 'PaymentFileSourceStatusFileFormatEnum';

  @override
  Object serialize(
          Serializers serializers, PaymentFileSourceStatusFileFormatEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  PaymentFileSourceStatusFileFormatEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      PaymentFileSourceStatusFileFormatEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$PaymentFileSourceStatusLastPollOutcomeEnumSerializer
    implements PrimitiveSerializer<PaymentFileSourceStatusLastPollOutcomeEnum> {
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
    PaymentFileSourceStatusLastPollOutcomeEnum
  ];
  @override
  final String wireName = 'PaymentFileSourceStatusLastPollOutcomeEnum';

  @override
  Object serialize(Serializers serializers,
          PaymentFileSourceStatusLastPollOutcomeEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  PaymentFileSourceStatusLastPollOutcomeEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      PaymentFileSourceStatusLastPollOutcomeEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$PaymentFileSourceStatus extends PaymentFileSourceStatus {
  @override
  final bool configured;
  @override
  final bool? enabled;
  @override
  final String? entity;
  @override
  final PaymentFileSourceStatusFileFormatEnum? fileFormat;
  @override
  final DateTime? lastPollAt;
  @override
  final String? lastPollError;
  @override
  final PaymentFileSourceStatusLastPollOutcomeEnum? lastPollOutcome;
  @override
  final DateTime? lastSuccessAt;
  @override
  final int? staleAfterMinutes;
  @override
  final DateTime? staleSince;

  factory _$PaymentFileSourceStatus(
          [void Function(PaymentFileSourceStatusBuilder)? updates]) =>
      (PaymentFileSourceStatusBuilder()..update(updates))._build();

  _$PaymentFileSourceStatus._(
      {required this.configured,
      this.enabled,
      this.entity,
      this.fileFormat,
      this.lastPollAt,
      this.lastPollError,
      this.lastPollOutcome,
      this.lastSuccessAt,
      this.staleAfterMinutes,
      this.staleSince})
      : super._();
  @override
  PaymentFileSourceStatus rebuild(
          void Function(PaymentFileSourceStatusBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PaymentFileSourceStatusBuilder toBuilder() =>
      PaymentFileSourceStatusBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PaymentFileSourceStatus &&
        configured == other.configured &&
        enabled == other.enabled &&
        entity == other.entity &&
        fileFormat == other.fileFormat &&
        lastPollAt == other.lastPollAt &&
        lastPollError == other.lastPollError &&
        lastPollOutcome == other.lastPollOutcome &&
        lastSuccessAt == other.lastSuccessAt &&
        staleAfterMinutes == other.staleAfterMinutes &&
        staleSince == other.staleSince;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, configured.hashCode);
    _$hash = $jc(_$hash, enabled.hashCode);
    _$hash = $jc(_$hash, entity.hashCode);
    _$hash = $jc(_$hash, fileFormat.hashCode);
    _$hash = $jc(_$hash, lastPollAt.hashCode);
    _$hash = $jc(_$hash, lastPollError.hashCode);
    _$hash = $jc(_$hash, lastPollOutcome.hashCode);
    _$hash = $jc(_$hash, lastSuccessAt.hashCode);
    _$hash = $jc(_$hash, staleAfterMinutes.hashCode);
    _$hash = $jc(_$hash, staleSince.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'PaymentFileSourceStatus')
          ..add('configured', configured)
          ..add('enabled', enabled)
          ..add('entity', entity)
          ..add('fileFormat', fileFormat)
          ..add('lastPollAt', lastPollAt)
          ..add('lastPollError', lastPollError)
          ..add('lastPollOutcome', lastPollOutcome)
          ..add('lastSuccessAt', lastSuccessAt)
          ..add('staleAfterMinutes', staleAfterMinutes)
          ..add('staleSince', staleSince))
        .toString();
  }
}

class PaymentFileSourceStatusBuilder
    implements
        Builder<PaymentFileSourceStatus, PaymentFileSourceStatusBuilder> {
  _$PaymentFileSourceStatus? _$v;

  bool? _configured;
  bool? get configured => _$this._configured;
  set configured(bool? configured) => _$this._configured = configured;

  bool? _enabled;
  bool? get enabled => _$this._enabled;
  set enabled(bool? enabled) => _$this._enabled = enabled;

  String? _entity;
  String? get entity => _$this._entity;
  set entity(String? entity) => _$this._entity = entity;

  PaymentFileSourceStatusFileFormatEnum? _fileFormat;
  PaymentFileSourceStatusFileFormatEnum? get fileFormat => _$this._fileFormat;
  set fileFormat(PaymentFileSourceStatusFileFormatEnum? fileFormat) =>
      _$this._fileFormat = fileFormat;

  DateTime? _lastPollAt;
  DateTime? get lastPollAt => _$this._lastPollAt;
  set lastPollAt(DateTime? lastPollAt) => _$this._lastPollAt = lastPollAt;

  String? _lastPollError;
  String? get lastPollError => _$this._lastPollError;
  set lastPollError(String? lastPollError) =>
      _$this._lastPollError = lastPollError;

  PaymentFileSourceStatusLastPollOutcomeEnum? _lastPollOutcome;
  PaymentFileSourceStatusLastPollOutcomeEnum? get lastPollOutcome =>
      _$this._lastPollOutcome;
  set lastPollOutcome(
          PaymentFileSourceStatusLastPollOutcomeEnum? lastPollOutcome) =>
      _$this._lastPollOutcome = lastPollOutcome;

  DateTime? _lastSuccessAt;
  DateTime? get lastSuccessAt => _$this._lastSuccessAt;
  set lastSuccessAt(DateTime? lastSuccessAt) =>
      _$this._lastSuccessAt = lastSuccessAt;

  int? _staleAfterMinutes;
  int? get staleAfterMinutes => _$this._staleAfterMinutes;
  set staleAfterMinutes(int? staleAfterMinutes) =>
      _$this._staleAfterMinutes = staleAfterMinutes;

  DateTime? _staleSince;
  DateTime? get staleSince => _$this._staleSince;
  set staleSince(DateTime? staleSince) => _$this._staleSince = staleSince;

  PaymentFileSourceStatusBuilder() {
    PaymentFileSourceStatus._defaults(this);
  }

  PaymentFileSourceStatusBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _configured = $v.configured;
      _enabled = $v.enabled;
      _entity = $v.entity;
      _fileFormat = $v.fileFormat;
      _lastPollAt = $v.lastPollAt;
      _lastPollError = $v.lastPollError;
      _lastPollOutcome = $v.lastPollOutcome;
      _lastSuccessAt = $v.lastSuccessAt;
      _staleAfterMinutes = $v.staleAfterMinutes;
      _staleSince = $v.staleSince;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(PaymentFileSourceStatus other) {
    _$v = other as _$PaymentFileSourceStatus;
  }

  @override
  void update(void Function(PaymentFileSourceStatusBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PaymentFileSourceStatus build() => _build();

  _$PaymentFileSourceStatus _build() {
    final _$result = _$v ??
        _$PaymentFileSourceStatus._(
          configured: BuiltValueNullFieldError.checkNotNull(
              configured, r'PaymentFileSourceStatus', 'configured'),
          enabled: enabled,
          entity: entity,
          fileFormat: fileFormat,
          lastPollAt: lastPollAt,
          lastPollError: lastPollError,
          lastPollOutcome: lastPollOutcome,
          lastSuccessAt: lastSuccessAt,
          staleAfterMinutes: staleAfterMinutes,
          staleSince: staleSince,
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
