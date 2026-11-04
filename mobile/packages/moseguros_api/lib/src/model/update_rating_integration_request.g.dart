// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'update_rating_integration_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const UpdateRatingIntegrationRequestRatingModeEnum
    _$updateRatingIntegrationRequestRatingModeEnum_INTERNAL =
    const UpdateRatingIntegrationRequestRatingModeEnum._('INTERNAL');
const UpdateRatingIntegrationRequestRatingModeEnum
    _$updateRatingIntegrationRequestRatingModeEnum_EXTERNAL =
    const UpdateRatingIntegrationRequestRatingModeEnum._('EXTERNAL');

UpdateRatingIntegrationRequestRatingModeEnum
    _$updateRatingIntegrationRequestRatingModeEnumValueOf(String name) {
  switch (name) {
    case 'INTERNAL':
      return _$updateRatingIntegrationRequestRatingModeEnum_INTERNAL;
    case 'EXTERNAL':
      return _$updateRatingIntegrationRequestRatingModeEnum_EXTERNAL;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<UpdateRatingIntegrationRequestRatingModeEnum>
    _$updateRatingIntegrationRequestRatingModeEnumValues = BuiltSet<
        UpdateRatingIntegrationRequestRatingModeEnum>(const <UpdateRatingIntegrationRequestRatingModeEnum>[
  _$updateRatingIntegrationRequestRatingModeEnum_INTERNAL,
  _$updateRatingIntegrationRequestRatingModeEnum_EXTERNAL,
]);

Serializer<UpdateRatingIntegrationRequestRatingModeEnum>
    _$updateRatingIntegrationRequestRatingModeEnumSerializer =
    _$UpdateRatingIntegrationRequestRatingModeEnumSerializer();

class _$UpdateRatingIntegrationRequestRatingModeEnumSerializer
    implements
        PrimitiveSerializer<UpdateRatingIntegrationRequestRatingModeEnum> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'INTERNAL': 'INTERNAL',
    'EXTERNAL': 'EXTERNAL',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'INTERNAL': 'INTERNAL',
    'EXTERNAL': 'EXTERNAL',
  };

  @override
  final Iterable<Type> types = const <Type>[
    UpdateRatingIntegrationRequestRatingModeEnum
  ];
  @override
  final String wireName = 'UpdateRatingIntegrationRequestRatingModeEnum';

  @override
  Object serialize(Serializers serializers,
          UpdateRatingIntegrationRequestRatingModeEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  UpdateRatingIntegrationRequestRatingModeEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      UpdateRatingIntegrationRequestRatingModeEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$UpdateRatingIntegrationRequest extends UpdateRatingIntegrationRequest {
  @override
  final String? ratingEndpointUrl;
  @override
  final UpdateRatingIntegrationRequestRatingModeEnum ratingMode;
  @override
  final String? ratingSecretRef;
  @override
  final int version;

  factory _$UpdateRatingIntegrationRequest(
          [void Function(UpdateRatingIntegrationRequestBuilder)? updates]) =>
      (UpdateRatingIntegrationRequestBuilder()..update(updates))._build();

  _$UpdateRatingIntegrationRequest._(
      {this.ratingEndpointUrl,
      required this.ratingMode,
      this.ratingSecretRef,
      required this.version})
      : super._();
  @override
  UpdateRatingIntegrationRequest rebuild(
          void Function(UpdateRatingIntegrationRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  UpdateRatingIntegrationRequestBuilder toBuilder() =>
      UpdateRatingIntegrationRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is UpdateRatingIntegrationRequest &&
        ratingEndpointUrl == other.ratingEndpointUrl &&
        ratingMode == other.ratingMode &&
        ratingSecretRef == other.ratingSecretRef &&
        version == other.version;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, ratingEndpointUrl.hashCode);
    _$hash = $jc(_$hash, ratingMode.hashCode);
    _$hash = $jc(_$hash, ratingSecretRef.hashCode);
    _$hash = $jc(_$hash, version.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'UpdateRatingIntegrationRequest')
          ..add('ratingEndpointUrl', ratingEndpointUrl)
          ..add('ratingMode', ratingMode)
          ..add('ratingSecretRef', ratingSecretRef)
          ..add('version', version))
        .toString();
  }
}

class UpdateRatingIntegrationRequestBuilder
    implements
        Builder<UpdateRatingIntegrationRequest,
            UpdateRatingIntegrationRequestBuilder> {
  _$UpdateRatingIntegrationRequest? _$v;

  String? _ratingEndpointUrl;
  String? get ratingEndpointUrl => _$this._ratingEndpointUrl;
  set ratingEndpointUrl(String? ratingEndpointUrl) =>
      _$this._ratingEndpointUrl = ratingEndpointUrl;

  UpdateRatingIntegrationRequestRatingModeEnum? _ratingMode;
  UpdateRatingIntegrationRequestRatingModeEnum? get ratingMode =>
      _$this._ratingMode;
  set ratingMode(UpdateRatingIntegrationRequestRatingModeEnum? ratingMode) =>
      _$this._ratingMode = ratingMode;

  String? _ratingSecretRef;
  String? get ratingSecretRef => _$this._ratingSecretRef;
  set ratingSecretRef(String? ratingSecretRef) =>
      _$this._ratingSecretRef = ratingSecretRef;

  int? _version;
  int? get version => _$this._version;
  set version(int? version) => _$this._version = version;

  UpdateRatingIntegrationRequestBuilder() {
    UpdateRatingIntegrationRequest._defaults(this);
  }

  UpdateRatingIntegrationRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _ratingEndpointUrl = $v.ratingEndpointUrl;
      _ratingMode = $v.ratingMode;
      _ratingSecretRef = $v.ratingSecretRef;
      _version = $v.version;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(UpdateRatingIntegrationRequest other) {
    _$v = other as _$UpdateRatingIntegrationRequest;
  }

  @override
  void update(void Function(UpdateRatingIntegrationRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  UpdateRatingIntegrationRequest build() => _build();

  _$UpdateRatingIntegrationRequest _build() {
    final _$result = _$v ??
        _$UpdateRatingIntegrationRequest._(
          ratingEndpointUrl: ratingEndpointUrl,
          ratingMode: BuiltValueNullFieldError.checkNotNull(
              ratingMode, r'UpdateRatingIntegrationRequest', 'ratingMode'),
          ratingSecretRef: ratingSecretRef,
          version: BuiltValueNullFieldError.checkNotNull(
              version, r'UpdateRatingIntegrationRequest', 'version'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
