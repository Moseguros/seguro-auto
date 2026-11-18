// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'payment_method_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const PaymentMethodResponseEnvironmentEnum
    _$paymentMethodResponseEnvironmentEnum_SANDBOX =
    const PaymentMethodResponseEnvironmentEnum._('SANDBOX');
const PaymentMethodResponseEnvironmentEnum
    _$paymentMethodResponseEnvironmentEnum_PRODUCTION =
    const PaymentMethodResponseEnvironmentEnum._('PRODUCTION');

PaymentMethodResponseEnvironmentEnum
    _$paymentMethodResponseEnvironmentEnumValueOf(String name) {
  switch (name) {
    case 'SANDBOX':
      return _$paymentMethodResponseEnvironmentEnum_SANDBOX;
    case 'PRODUCTION':
      return _$paymentMethodResponseEnvironmentEnum_PRODUCTION;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<PaymentMethodResponseEnvironmentEnum>
    _$paymentMethodResponseEnvironmentEnumValues = BuiltSet<
        PaymentMethodResponseEnvironmentEnum>(const <PaymentMethodResponseEnvironmentEnum>[
  _$paymentMethodResponseEnvironmentEnum_SANDBOX,
  _$paymentMethodResponseEnvironmentEnum_PRODUCTION,
]);

const PaymentMethodResponseMethodEnum _$paymentMethodResponseMethodEnum_MPESA =
    const PaymentMethodResponseMethodEnum._('MPESA');
const PaymentMethodResponseMethodEnum
    _$paymentMethodResponseMethodEnum_BANK_REFERENCE =
    const PaymentMethodResponseMethodEnum._('BANK_REFERENCE');

PaymentMethodResponseMethodEnum _$paymentMethodResponseMethodEnumValueOf(
    String name) {
  switch (name) {
    case 'MPESA':
      return _$paymentMethodResponseMethodEnum_MPESA;
    case 'BANK_REFERENCE':
      return _$paymentMethodResponseMethodEnum_BANK_REFERENCE;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<PaymentMethodResponseMethodEnum>
    _$paymentMethodResponseMethodEnumValues = BuiltSet<
        PaymentMethodResponseMethodEnum>(const <PaymentMethodResponseMethodEnum>[
  _$paymentMethodResponseMethodEnum_MPESA,
  _$paymentMethodResponseMethodEnum_BANK_REFERENCE,
]);

Serializer<PaymentMethodResponseEnvironmentEnum>
    _$paymentMethodResponseEnvironmentEnumSerializer =
    _$PaymentMethodResponseEnvironmentEnumSerializer();
Serializer<PaymentMethodResponseMethodEnum>
    _$paymentMethodResponseMethodEnumSerializer =
    _$PaymentMethodResponseMethodEnumSerializer();

class _$PaymentMethodResponseEnvironmentEnumSerializer
    implements PrimitiveSerializer<PaymentMethodResponseEnvironmentEnum> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'SANDBOX': 'SANDBOX',
    'PRODUCTION': 'PRODUCTION',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'SANDBOX': 'SANDBOX',
    'PRODUCTION': 'PRODUCTION',
  };

  @override
  final Iterable<Type> types = const <Type>[
    PaymentMethodResponseEnvironmentEnum
  ];
  @override
  final String wireName = 'PaymentMethodResponseEnvironmentEnum';

  @override
  Object serialize(
          Serializers serializers, PaymentMethodResponseEnvironmentEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  PaymentMethodResponseEnvironmentEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      PaymentMethodResponseEnvironmentEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$PaymentMethodResponseMethodEnumSerializer
    implements PrimitiveSerializer<PaymentMethodResponseMethodEnum> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'MPESA': 'MPESA',
    'BANK_REFERENCE': 'BANK_REFERENCE',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'MPESA': 'MPESA',
    'BANK_REFERENCE': 'BANK_REFERENCE',
  };

  @override
  final Iterable<Type> types = const <Type>[PaymentMethodResponseMethodEnum];
  @override
  final String wireName = 'PaymentMethodResponseMethodEnum';

  @override
  Object serialize(
          Serializers serializers, PaymentMethodResponseMethodEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  PaymentMethodResponseMethodEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      PaymentMethodResponseMethodEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$PaymentMethodResponse extends PaymentMethodResponse {
  @override
  final bool configured;
  @override
  final String? displayName;
  @override
  final bool enabled;
  @override
  final PaymentMethodResponseEnvironmentEnum environment;
  @override
  final int intentValidityMinutes;
  @override
  final LastErrorResponse? lastError;
  @override
  final LastTestResponse? lastTest;
  @override
  final Money? maxAmount;
  @override
  final PaymentMethodResponseMethodEnum method;
  @override
  final Money? minAmount;
  @override
  final BuiltMap<String, SecretStatusResponse> secrets;
  @override
  final JsonObject settings;
  @override
  final JsonObject settingsSchema;
  @override
  final int sortOrder;
  @override
  final DateTime? updatedAt;
  @override
  final int? version;

  factory _$PaymentMethodResponse(
          [void Function(PaymentMethodResponseBuilder)? updates]) =>
      (PaymentMethodResponseBuilder()..update(updates))._build();

  _$PaymentMethodResponse._(
      {required this.configured,
      this.displayName,
      required this.enabled,
      required this.environment,
      required this.intentValidityMinutes,
      this.lastError,
      this.lastTest,
      this.maxAmount,
      required this.method,
      this.minAmount,
      required this.secrets,
      required this.settings,
      required this.settingsSchema,
      required this.sortOrder,
      this.updatedAt,
      this.version})
      : super._();
  @override
  PaymentMethodResponse rebuild(
          void Function(PaymentMethodResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PaymentMethodResponseBuilder toBuilder() =>
      PaymentMethodResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PaymentMethodResponse &&
        configured == other.configured &&
        displayName == other.displayName &&
        enabled == other.enabled &&
        environment == other.environment &&
        intentValidityMinutes == other.intentValidityMinutes &&
        lastError == other.lastError &&
        lastTest == other.lastTest &&
        maxAmount == other.maxAmount &&
        method == other.method &&
        minAmount == other.minAmount &&
        secrets == other.secrets &&
        settings == other.settings &&
        settingsSchema == other.settingsSchema &&
        sortOrder == other.sortOrder &&
        updatedAt == other.updatedAt &&
        version == other.version;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, configured.hashCode);
    _$hash = $jc(_$hash, displayName.hashCode);
    _$hash = $jc(_$hash, enabled.hashCode);
    _$hash = $jc(_$hash, environment.hashCode);
    _$hash = $jc(_$hash, intentValidityMinutes.hashCode);
    _$hash = $jc(_$hash, lastError.hashCode);
    _$hash = $jc(_$hash, lastTest.hashCode);
    _$hash = $jc(_$hash, maxAmount.hashCode);
    _$hash = $jc(_$hash, method.hashCode);
    _$hash = $jc(_$hash, minAmount.hashCode);
    _$hash = $jc(_$hash, secrets.hashCode);
    _$hash = $jc(_$hash, settings.hashCode);
    _$hash = $jc(_$hash, settingsSchema.hashCode);
    _$hash = $jc(_$hash, sortOrder.hashCode);
    _$hash = $jc(_$hash, updatedAt.hashCode);
    _$hash = $jc(_$hash, version.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'PaymentMethodResponse')
          ..add('configured', configured)
          ..add('displayName', displayName)
          ..add('enabled', enabled)
          ..add('environment', environment)
          ..add('intentValidityMinutes', intentValidityMinutes)
          ..add('lastError', lastError)
          ..add('lastTest', lastTest)
          ..add('maxAmount', maxAmount)
          ..add('method', method)
          ..add('minAmount', minAmount)
          ..add('secrets', secrets)
          ..add('settings', settings)
          ..add('settingsSchema', settingsSchema)
          ..add('sortOrder', sortOrder)
          ..add('updatedAt', updatedAt)
          ..add('version', version))
        .toString();
  }
}

class PaymentMethodResponseBuilder
    implements Builder<PaymentMethodResponse, PaymentMethodResponseBuilder> {
  _$PaymentMethodResponse? _$v;

  bool? _configured;
  bool? get configured => _$this._configured;
  set configured(bool? configured) => _$this._configured = configured;

  String? _displayName;
  String? get displayName => _$this._displayName;
  set displayName(String? displayName) => _$this._displayName = displayName;

  bool? _enabled;
  bool? get enabled => _$this._enabled;
  set enabled(bool? enabled) => _$this._enabled = enabled;

  PaymentMethodResponseEnvironmentEnum? _environment;
  PaymentMethodResponseEnvironmentEnum? get environment => _$this._environment;
  set environment(PaymentMethodResponseEnvironmentEnum? environment) =>
      _$this._environment = environment;

  int? _intentValidityMinutes;
  int? get intentValidityMinutes => _$this._intentValidityMinutes;
  set intentValidityMinutes(int? intentValidityMinutes) =>
      _$this._intentValidityMinutes = intentValidityMinutes;

  LastErrorResponseBuilder? _lastError;
  LastErrorResponseBuilder get lastError =>
      _$this._lastError ??= LastErrorResponseBuilder();
  set lastError(LastErrorResponseBuilder? lastError) =>
      _$this._lastError = lastError;

  LastTestResponseBuilder? _lastTest;
  LastTestResponseBuilder get lastTest =>
      _$this._lastTest ??= LastTestResponseBuilder();
  set lastTest(LastTestResponseBuilder? lastTest) =>
      _$this._lastTest = lastTest;

  MoneyBuilder? _maxAmount;
  MoneyBuilder get maxAmount => _$this._maxAmount ??= MoneyBuilder();
  set maxAmount(MoneyBuilder? maxAmount) => _$this._maxAmount = maxAmount;

  PaymentMethodResponseMethodEnum? _method;
  PaymentMethodResponseMethodEnum? get method => _$this._method;
  set method(PaymentMethodResponseMethodEnum? method) =>
      _$this._method = method;

  MoneyBuilder? _minAmount;
  MoneyBuilder get minAmount => _$this._minAmount ??= MoneyBuilder();
  set minAmount(MoneyBuilder? minAmount) => _$this._minAmount = minAmount;

  MapBuilder<String, SecretStatusResponse>? _secrets;
  MapBuilder<String, SecretStatusResponse> get secrets =>
      _$this._secrets ??= MapBuilder<String, SecretStatusResponse>();
  set secrets(MapBuilder<String, SecretStatusResponse>? secrets) =>
      _$this._secrets = secrets;

  JsonObject? _settings;
  JsonObject? get settings => _$this._settings;
  set settings(JsonObject? settings) => _$this._settings = settings;

  JsonObject? _settingsSchema;
  JsonObject? get settingsSchema => _$this._settingsSchema;
  set settingsSchema(JsonObject? settingsSchema) =>
      _$this._settingsSchema = settingsSchema;

  int? _sortOrder;
  int? get sortOrder => _$this._sortOrder;
  set sortOrder(int? sortOrder) => _$this._sortOrder = sortOrder;

  DateTime? _updatedAt;
  DateTime? get updatedAt => _$this._updatedAt;
  set updatedAt(DateTime? updatedAt) => _$this._updatedAt = updatedAt;

  int? _version;
  int? get version => _$this._version;
  set version(int? version) => _$this._version = version;

  PaymentMethodResponseBuilder() {
    PaymentMethodResponse._defaults(this);
  }

  PaymentMethodResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _configured = $v.configured;
      _displayName = $v.displayName;
      _enabled = $v.enabled;
      _environment = $v.environment;
      _intentValidityMinutes = $v.intentValidityMinutes;
      _lastError = $v.lastError?.toBuilder();
      _lastTest = $v.lastTest?.toBuilder();
      _maxAmount = $v.maxAmount?.toBuilder();
      _method = $v.method;
      _minAmount = $v.minAmount?.toBuilder();
      _secrets = $v.secrets.toBuilder();
      _settings = $v.settings;
      _settingsSchema = $v.settingsSchema;
      _sortOrder = $v.sortOrder;
      _updatedAt = $v.updatedAt;
      _version = $v.version;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(PaymentMethodResponse other) {
    _$v = other as _$PaymentMethodResponse;
  }

  @override
  void update(void Function(PaymentMethodResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PaymentMethodResponse build() => _build();

  _$PaymentMethodResponse _build() {
    _$PaymentMethodResponse _$result;
    try {
      _$result = _$v ??
          _$PaymentMethodResponse._(
            configured: BuiltValueNullFieldError.checkNotNull(
                configured, r'PaymentMethodResponse', 'configured'),
            displayName: displayName,
            enabled: BuiltValueNullFieldError.checkNotNull(
                enabled, r'PaymentMethodResponse', 'enabled'),
            environment: BuiltValueNullFieldError.checkNotNull(
                environment, r'PaymentMethodResponse', 'environment'),
            intentValidityMinutes: BuiltValueNullFieldError.checkNotNull(
                intentValidityMinutes,
                r'PaymentMethodResponse',
                'intentValidityMinutes'),
            lastError: _lastError?.build(),
            lastTest: _lastTest?.build(),
            maxAmount: _maxAmount?.build(),
            method: BuiltValueNullFieldError.checkNotNull(
                method, r'PaymentMethodResponse', 'method'),
            minAmount: _minAmount?.build(),
            secrets: secrets.build(),
            settings: BuiltValueNullFieldError.checkNotNull(
                settings, r'PaymentMethodResponse', 'settings'),
            settingsSchema: BuiltValueNullFieldError.checkNotNull(
                settingsSchema, r'PaymentMethodResponse', 'settingsSchema'),
            sortOrder: BuiltValueNullFieldError.checkNotNull(
                sortOrder, r'PaymentMethodResponse', 'sortOrder'),
            updatedAt: updatedAt,
            version: version,
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'lastError';
        _lastError?.build();
        _$failedField = 'lastTest';
        _lastTest?.build();
        _$failedField = 'maxAmount';
        _maxAmount?.build();

        _$failedField = 'minAmount';
        _minAmount?.build();
        _$failedField = 'secrets';
        secrets.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'PaymentMethodResponse', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
