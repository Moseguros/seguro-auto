// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'rating_integration_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const RatingIntegrationResponseRatingModeEnum
    _$ratingIntegrationResponseRatingModeEnum_INTERNAL =
    const RatingIntegrationResponseRatingModeEnum._('INTERNAL');
const RatingIntegrationResponseRatingModeEnum
    _$ratingIntegrationResponseRatingModeEnum_EXTERNAL =
    const RatingIntegrationResponseRatingModeEnum._('EXTERNAL');

RatingIntegrationResponseRatingModeEnum
    _$ratingIntegrationResponseRatingModeEnumValueOf(String name) {
  switch (name) {
    case 'INTERNAL':
      return _$ratingIntegrationResponseRatingModeEnum_INTERNAL;
    case 'EXTERNAL':
      return _$ratingIntegrationResponseRatingModeEnum_EXTERNAL;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<RatingIntegrationResponseRatingModeEnum>
    _$ratingIntegrationResponseRatingModeEnumValues = BuiltSet<
        RatingIntegrationResponseRatingModeEnum>(const <RatingIntegrationResponseRatingModeEnum>[
  _$ratingIntegrationResponseRatingModeEnum_INTERNAL,
  _$ratingIntegrationResponseRatingModeEnum_EXTERNAL,
]);

Serializer<RatingIntegrationResponseRatingModeEnum>
    _$ratingIntegrationResponseRatingModeEnumSerializer =
    _$RatingIntegrationResponseRatingModeEnumSerializer();

class _$RatingIntegrationResponseRatingModeEnumSerializer
    implements PrimitiveSerializer<RatingIntegrationResponseRatingModeEnum> {
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
    RatingIntegrationResponseRatingModeEnum
  ];
  @override
  final String wireName = 'RatingIntegrationResponseRatingModeEnum';

  @override
  Object serialize(Serializers serializers,
          RatingIntegrationResponseRatingModeEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  RatingIntegrationResponseRatingModeEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      RatingIntegrationResponseRatingModeEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$RatingIntegrationResponse extends RatingIntegrationResponse {
  @override
  final String? ratingEndpointUrl;
  @override
  final RatingIntegrationResponseRatingModeEnum ratingMode;
  @override
  final String? ratingSecretRef;
  @override
  final int version;

  factory _$RatingIntegrationResponse(
          [void Function(RatingIntegrationResponseBuilder)? updates]) =>
      (RatingIntegrationResponseBuilder()..update(updates))._build();

  _$RatingIntegrationResponse._(
      {this.ratingEndpointUrl,
      required this.ratingMode,
      this.ratingSecretRef,
      required this.version})
      : super._();
  @override
  RatingIntegrationResponse rebuild(
          void Function(RatingIntegrationResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  RatingIntegrationResponseBuilder toBuilder() =>
      RatingIntegrationResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is RatingIntegrationResponse &&
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
    return (newBuiltValueToStringHelper(r'RatingIntegrationResponse')
          ..add('ratingEndpointUrl', ratingEndpointUrl)
          ..add('ratingMode', ratingMode)
          ..add('ratingSecretRef', ratingSecretRef)
          ..add('version', version))
        .toString();
  }
}

class RatingIntegrationResponseBuilder
    implements
        Builder<RatingIntegrationResponse, RatingIntegrationResponseBuilder> {
  _$RatingIntegrationResponse? _$v;

  String? _ratingEndpointUrl;
  String? get ratingEndpointUrl => _$this._ratingEndpointUrl;
  set ratingEndpointUrl(String? ratingEndpointUrl) =>
      _$this._ratingEndpointUrl = ratingEndpointUrl;

  RatingIntegrationResponseRatingModeEnum? _ratingMode;
  RatingIntegrationResponseRatingModeEnum? get ratingMode => _$this._ratingMode;
  set ratingMode(RatingIntegrationResponseRatingModeEnum? ratingMode) =>
      _$this._ratingMode = ratingMode;

  String? _ratingSecretRef;
  String? get ratingSecretRef => _$this._ratingSecretRef;
  set ratingSecretRef(String? ratingSecretRef) =>
      _$this._ratingSecretRef = ratingSecretRef;

  int? _version;
  int? get version => _$this._version;
  set version(int? version) => _$this._version = version;

  RatingIntegrationResponseBuilder() {
    RatingIntegrationResponse._defaults(this);
  }

  RatingIntegrationResponseBuilder get _$this {
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
  void replace(RatingIntegrationResponse other) {
    _$v = other as _$RatingIntegrationResponse;
  }

  @override
  void update(void Function(RatingIntegrationResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  RatingIntegrationResponse build() => _build();

  _$RatingIntegrationResponse _build() {
    final _$result = _$v ??
        _$RatingIntegrationResponse._(
          ratingEndpointUrl: ratingEndpointUrl,
          ratingMode: BuiltValueNullFieldError.checkNotNull(
              ratingMode, r'RatingIntegrationResponse', 'ratingMode'),
          ratingSecretRef: ratingSecretRef,
          version: BuiltValueNullFieldError.checkNotNull(
              version, r'RatingIntegrationResponse', 'version'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
