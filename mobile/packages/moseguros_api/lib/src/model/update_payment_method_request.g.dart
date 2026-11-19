// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'update_payment_method_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const UpdatePaymentMethodRequestEnvironmentEnum
    _$updatePaymentMethodRequestEnvironmentEnum_SANDBOX =
    const UpdatePaymentMethodRequestEnvironmentEnum._('SANDBOX');
const UpdatePaymentMethodRequestEnvironmentEnum
    _$updatePaymentMethodRequestEnvironmentEnum_PRODUCTION =
    const UpdatePaymentMethodRequestEnvironmentEnum._('PRODUCTION');

UpdatePaymentMethodRequestEnvironmentEnum
    _$updatePaymentMethodRequestEnvironmentEnumValueOf(String name) {
  switch (name) {
    case 'SANDBOX':
      return _$updatePaymentMethodRequestEnvironmentEnum_SANDBOX;
    case 'PRODUCTION':
      return _$updatePaymentMethodRequestEnvironmentEnum_PRODUCTION;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<UpdatePaymentMethodRequestEnvironmentEnum>
    _$updatePaymentMethodRequestEnvironmentEnumValues = BuiltSet<
        UpdatePaymentMethodRequestEnvironmentEnum>(const <UpdatePaymentMethodRequestEnvironmentEnum>[
  _$updatePaymentMethodRequestEnvironmentEnum_SANDBOX,
  _$updatePaymentMethodRequestEnvironmentEnum_PRODUCTION,
]);

Serializer<UpdatePaymentMethodRequestEnvironmentEnum>
    _$updatePaymentMethodRequestEnvironmentEnumSerializer =
    _$UpdatePaymentMethodRequestEnvironmentEnumSerializer();

class _$UpdatePaymentMethodRequestEnvironmentEnumSerializer
    implements PrimitiveSerializer<UpdatePaymentMethodRequestEnvironmentEnum> {
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
    UpdatePaymentMethodRequestEnvironmentEnum
  ];
  @override
  final String wireName = 'UpdatePaymentMethodRequestEnvironmentEnum';

  @override
  Object serialize(Serializers serializers,
          UpdatePaymentMethodRequestEnvironmentEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  UpdatePaymentMethodRequestEnvironmentEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      UpdatePaymentMethodRequestEnvironmentEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$UpdatePaymentMethodRequest extends UpdatePaymentMethodRequest {
  @override
  final String? displayName;
  @override
  final UpdatePaymentMethodRequestEnvironmentEnum environment;
  @override
  final int intentValidityMinutes;
  @override
  final Money? maxAmount;
  @override
  final Money? minAmount;
  @override
  final BuiltMap<String, String?>? secrets;
  @override
  final JsonObject settings;
  @override
  final int sortOrder;
  @override
  final int? version;

  factory _$UpdatePaymentMethodRequest(
          [void Function(UpdatePaymentMethodRequestBuilder)? updates]) =>
      (UpdatePaymentMethodRequestBuilder()..update(updates))._build();

  _$UpdatePaymentMethodRequest._(
      {this.displayName,
      required this.environment,
      required this.intentValidityMinutes,
      this.maxAmount,
      this.minAmount,
      this.secrets,
      required this.settings,
      required this.sortOrder,
      this.version})
      : super._();
  @override
  UpdatePaymentMethodRequest rebuild(
          void Function(UpdatePaymentMethodRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  UpdatePaymentMethodRequestBuilder toBuilder() =>
      UpdatePaymentMethodRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is UpdatePaymentMethodRequest &&
        displayName == other.displayName &&
        environment == other.environment &&
        intentValidityMinutes == other.intentValidityMinutes &&
        maxAmount == other.maxAmount &&
        minAmount == other.minAmount &&
        secrets == other.secrets &&
        settings == other.settings &&
        sortOrder == other.sortOrder &&
        version == other.version;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, displayName.hashCode);
    _$hash = $jc(_$hash, environment.hashCode);
    _$hash = $jc(_$hash, intentValidityMinutes.hashCode);
    _$hash = $jc(_$hash, maxAmount.hashCode);
    _$hash = $jc(_$hash, minAmount.hashCode);
    _$hash = $jc(_$hash, secrets.hashCode);
    _$hash = $jc(_$hash, settings.hashCode);
    _$hash = $jc(_$hash, sortOrder.hashCode);
    _$hash = $jc(_$hash, version.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'UpdatePaymentMethodRequest')
          ..add('displayName', displayName)
          ..add('environment', environment)
          ..add('intentValidityMinutes', intentValidityMinutes)
          ..add('maxAmount', maxAmount)
          ..add('minAmount', minAmount)
          ..add('secrets', secrets)
          ..add('settings', settings)
          ..add('sortOrder', sortOrder)
          ..add('version', version))
        .toString();
  }
}

class UpdatePaymentMethodRequestBuilder
    implements
        Builder<UpdatePaymentMethodRequest, UpdatePaymentMethodRequestBuilder> {
  _$UpdatePaymentMethodRequest? _$v;

  String? _displayName;
  String? get displayName => _$this._displayName;
  set displayName(String? displayName) => _$this._displayName = displayName;

  UpdatePaymentMethodRequestEnvironmentEnum? _environment;
  UpdatePaymentMethodRequestEnvironmentEnum? get environment =>
      _$this._environment;
  set environment(UpdatePaymentMethodRequestEnvironmentEnum? environment) =>
      _$this._environment = environment;

  int? _intentValidityMinutes;
  int? get intentValidityMinutes => _$this._intentValidityMinutes;
  set intentValidityMinutes(int? intentValidityMinutes) =>
      _$this._intentValidityMinutes = intentValidityMinutes;

  MoneyBuilder? _maxAmount;
  MoneyBuilder get maxAmount => _$this._maxAmount ??= MoneyBuilder();
  set maxAmount(MoneyBuilder? maxAmount) => _$this._maxAmount = maxAmount;

  MoneyBuilder? _minAmount;
  MoneyBuilder get minAmount => _$this._minAmount ??= MoneyBuilder();
  set minAmount(MoneyBuilder? minAmount) => _$this._minAmount = minAmount;

  MapBuilder<String, String?>? _secrets;
  MapBuilder<String, String?> get secrets =>
      _$this._secrets ??= MapBuilder<String, String?>();
  set secrets(MapBuilder<String, String?>? secrets) =>
      _$this._secrets = secrets;

  JsonObject? _settings;
  JsonObject? get settings => _$this._settings;
  set settings(JsonObject? settings) => _$this._settings = settings;

  int? _sortOrder;
  int? get sortOrder => _$this._sortOrder;
  set sortOrder(int? sortOrder) => _$this._sortOrder = sortOrder;

  int? _version;
  int? get version => _$this._version;
  set version(int? version) => _$this._version = version;

  UpdatePaymentMethodRequestBuilder() {
    UpdatePaymentMethodRequest._defaults(this);
  }

  UpdatePaymentMethodRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _displayName = $v.displayName;
      _environment = $v.environment;
      _intentValidityMinutes = $v.intentValidityMinutes;
      _maxAmount = $v.maxAmount?.toBuilder();
      _minAmount = $v.minAmount?.toBuilder();
      _secrets = $v.secrets?.toBuilder();
      _settings = $v.settings;
      _sortOrder = $v.sortOrder;
      _version = $v.version;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(UpdatePaymentMethodRequest other) {
    _$v = other as _$UpdatePaymentMethodRequest;
  }

  @override
  void update(void Function(UpdatePaymentMethodRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  UpdatePaymentMethodRequest build() => _build();

  _$UpdatePaymentMethodRequest _build() {
    _$UpdatePaymentMethodRequest _$result;
    try {
      _$result = _$v ??
          _$UpdatePaymentMethodRequest._(
            displayName: displayName,
            environment: BuiltValueNullFieldError.checkNotNull(
                environment, r'UpdatePaymentMethodRequest', 'environment'),
            intentValidityMinutes: BuiltValueNullFieldError.checkNotNull(
                intentValidityMinutes,
                r'UpdatePaymentMethodRequest',
                'intentValidityMinutes'),
            maxAmount: _maxAmount?.build(),
            minAmount: _minAmount?.build(),
            secrets: _secrets?.build(),
            settings: BuiltValueNullFieldError.checkNotNull(
                settings, r'UpdatePaymentMethodRequest', 'settings'),
            sortOrder: BuiltValueNullFieldError.checkNotNull(
                sortOrder, r'UpdatePaymentMethodRequest', 'sortOrder'),
            version: version,
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'maxAmount';
        _maxAmount?.build();
        _$failedField = 'minAmount';
        _minAmount?.build();
        _$failedField = 'secrets';
        _secrets?.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'UpdatePaymentMethodRequest', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
