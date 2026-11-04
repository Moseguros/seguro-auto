// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'insurer_rating_settings_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const InsurerRatingSettingsResponseRatingModeEnum
    _$insurerRatingSettingsResponseRatingModeEnum_INTERNAL =
    const InsurerRatingSettingsResponseRatingModeEnum._('INTERNAL');
const InsurerRatingSettingsResponseRatingModeEnum
    _$insurerRatingSettingsResponseRatingModeEnum_EXTERNAL =
    const InsurerRatingSettingsResponseRatingModeEnum._('EXTERNAL');

InsurerRatingSettingsResponseRatingModeEnum
    _$insurerRatingSettingsResponseRatingModeEnumValueOf(String name) {
  switch (name) {
    case 'INTERNAL':
      return _$insurerRatingSettingsResponseRatingModeEnum_INTERNAL;
    case 'EXTERNAL':
      return _$insurerRatingSettingsResponseRatingModeEnum_EXTERNAL;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<InsurerRatingSettingsResponseRatingModeEnum>
    _$insurerRatingSettingsResponseRatingModeEnumValues = BuiltSet<
        InsurerRatingSettingsResponseRatingModeEnum>(const <InsurerRatingSettingsResponseRatingModeEnum>[
  _$insurerRatingSettingsResponseRatingModeEnum_INTERNAL,
  _$insurerRatingSettingsResponseRatingModeEnum_EXTERNAL,
]);

Serializer<InsurerRatingSettingsResponseRatingModeEnum>
    _$insurerRatingSettingsResponseRatingModeEnumSerializer =
    _$InsurerRatingSettingsResponseRatingModeEnumSerializer();

class _$InsurerRatingSettingsResponseRatingModeEnumSerializer
    implements
        PrimitiveSerializer<InsurerRatingSettingsResponseRatingModeEnum> {
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
    InsurerRatingSettingsResponseRatingModeEnum
  ];
  @override
  final String wireName = 'InsurerRatingSettingsResponseRatingModeEnum';

  @override
  Object serialize(Serializers serializers,
          InsurerRatingSettingsResponseRatingModeEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  InsurerRatingSettingsResponseRatingModeEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      InsurerRatingSettingsResponseRatingModeEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$InsurerRatingSettingsResponse extends InsurerRatingSettingsResponse {
  @override
  final BuiltList<Charge> charges;
  @override
  final InsurerRatingSettingsResponseRatingModeEnum ratingMode;
  @override
  final int version;

  factory _$InsurerRatingSettingsResponse(
          [void Function(InsurerRatingSettingsResponseBuilder)? updates]) =>
      (InsurerRatingSettingsResponseBuilder()..update(updates))._build();

  _$InsurerRatingSettingsResponse._(
      {required this.charges, required this.ratingMode, required this.version})
      : super._();
  @override
  InsurerRatingSettingsResponse rebuild(
          void Function(InsurerRatingSettingsResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  InsurerRatingSettingsResponseBuilder toBuilder() =>
      InsurerRatingSettingsResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is InsurerRatingSettingsResponse &&
        charges == other.charges &&
        ratingMode == other.ratingMode &&
        version == other.version;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, charges.hashCode);
    _$hash = $jc(_$hash, ratingMode.hashCode);
    _$hash = $jc(_$hash, version.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'InsurerRatingSettingsResponse')
          ..add('charges', charges)
          ..add('ratingMode', ratingMode)
          ..add('version', version))
        .toString();
  }
}

class InsurerRatingSettingsResponseBuilder
    implements
        Builder<InsurerRatingSettingsResponse,
            InsurerRatingSettingsResponseBuilder> {
  _$InsurerRatingSettingsResponse? _$v;

  ListBuilder<Charge>? _charges;
  ListBuilder<Charge> get charges => _$this._charges ??= ListBuilder<Charge>();
  set charges(ListBuilder<Charge>? charges) => _$this._charges = charges;

  InsurerRatingSettingsResponseRatingModeEnum? _ratingMode;
  InsurerRatingSettingsResponseRatingModeEnum? get ratingMode =>
      _$this._ratingMode;
  set ratingMode(InsurerRatingSettingsResponseRatingModeEnum? ratingMode) =>
      _$this._ratingMode = ratingMode;

  int? _version;
  int? get version => _$this._version;
  set version(int? version) => _$this._version = version;

  InsurerRatingSettingsResponseBuilder() {
    InsurerRatingSettingsResponse._defaults(this);
  }

  InsurerRatingSettingsResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _charges = $v.charges.toBuilder();
      _ratingMode = $v.ratingMode;
      _version = $v.version;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(InsurerRatingSettingsResponse other) {
    _$v = other as _$InsurerRatingSettingsResponse;
  }

  @override
  void update(void Function(InsurerRatingSettingsResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  InsurerRatingSettingsResponse build() => _build();

  _$InsurerRatingSettingsResponse _build() {
    _$InsurerRatingSettingsResponse _$result;
    try {
      _$result = _$v ??
          _$InsurerRatingSettingsResponse._(
            charges: charges.build(),
            ratingMode: BuiltValueNullFieldError.checkNotNull(
                ratingMode, r'InsurerRatingSettingsResponse', 'ratingMode'),
            version: BuiltValueNullFieldError.checkNotNull(
                version, r'InsurerRatingSettingsResponse', 'version'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'charges';
        charges.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'InsurerRatingSettingsResponse', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
