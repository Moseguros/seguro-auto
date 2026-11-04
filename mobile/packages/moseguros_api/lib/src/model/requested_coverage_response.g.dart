// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'requested_coverage_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const RequestedCoverageResponseCodeEnum
    _$requestedCoverageResponseCodeEnum_MTPL =
    const RequestedCoverageResponseCodeEnum._('MTPL');
const RequestedCoverageResponseCodeEnum
    _$requestedCoverageResponseCodeEnum_VTPL =
    const RequestedCoverageResponseCodeEnum._('VTPL');
const RequestedCoverageResponseCodeEnum
    _$requestedCoverageResponseCodeEnum_OD_COLLISION =
    const RequestedCoverageResponseCodeEnum._('OD_COLLISION');
const RequestedCoverageResponseCodeEnum
    _$requestedCoverageResponseCodeEnum_OD_GLASS =
    const RequestedCoverageResponseCodeEnum._('OD_GLASS');
const RequestedCoverageResponseCodeEnum
    _$requestedCoverageResponseCodeEnum_OD_FIRE =
    const RequestedCoverageResponseCodeEnum._('OD_FIRE');
const RequestedCoverageResponseCodeEnum
    _$requestedCoverageResponseCodeEnum_OD_NATURE =
    const RequestedCoverageResponseCodeEnum._('OD_NATURE');
const RequestedCoverageResponseCodeEnum
    _$requestedCoverageResponseCodeEnum_OD_THEFT =
    const RequestedCoverageResponseCodeEnum._('OD_THEFT');
const RequestedCoverageResponseCodeEnum
    _$requestedCoverageResponseCodeEnum_OCCUPANTS =
    const RequestedCoverageResponseCodeEnum._('OCCUPANTS');
const RequestedCoverageResponseCodeEnum
    _$requestedCoverageResponseCodeEnum_ASSISTANCE =
    const RequestedCoverageResponseCodeEnum._('ASSISTANCE');
const RequestedCoverageResponseCodeEnum
    _$requestedCoverageResponseCodeEnum_LEGAL =
    const RequestedCoverageResponseCodeEnum._('LEGAL');
const RequestedCoverageResponseCodeEnum
    _$requestedCoverageResponseCodeEnum_SRCC =
    const RequestedCoverageResponseCodeEnum._('SRCC');
const RequestedCoverageResponseCodeEnum
    _$requestedCoverageResponseCodeEnum_CROSS_BORDER =
    const RequestedCoverageResponseCodeEnum._('CROSS_BORDER');

RequestedCoverageResponseCodeEnum _$requestedCoverageResponseCodeEnumValueOf(
    String name) {
  switch (name) {
    case 'MTPL':
      return _$requestedCoverageResponseCodeEnum_MTPL;
    case 'VTPL':
      return _$requestedCoverageResponseCodeEnum_VTPL;
    case 'OD_COLLISION':
      return _$requestedCoverageResponseCodeEnum_OD_COLLISION;
    case 'OD_GLASS':
      return _$requestedCoverageResponseCodeEnum_OD_GLASS;
    case 'OD_FIRE':
      return _$requestedCoverageResponseCodeEnum_OD_FIRE;
    case 'OD_NATURE':
      return _$requestedCoverageResponseCodeEnum_OD_NATURE;
    case 'OD_THEFT':
      return _$requestedCoverageResponseCodeEnum_OD_THEFT;
    case 'OCCUPANTS':
      return _$requestedCoverageResponseCodeEnum_OCCUPANTS;
    case 'ASSISTANCE':
      return _$requestedCoverageResponseCodeEnum_ASSISTANCE;
    case 'LEGAL':
      return _$requestedCoverageResponseCodeEnum_LEGAL;
    case 'SRCC':
      return _$requestedCoverageResponseCodeEnum_SRCC;
    case 'CROSS_BORDER':
      return _$requestedCoverageResponseCodeEnum_CROSS_BORDER;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<RequestedCoverageResponseCodeEnum>
    _$requestedCoverageResponseCodeEnumValues = BuiltSet<
        RequestedCoverageResponseCodeEnum>(const <RequestedCoverageResponseCodeEnum>[
  _$requestedCoverageResponseCodeEnum_MTPL,
  _$requestedCoverageResponseCodeEnum_VTPL,
  _$requestedCoverageResponseCodeEnum_OD_COLLISION,
  _$requestedCoverageResponseCodeEnum_OD_GLASS,
  _$requestedCoverageResponseCodeEnum_OD_FIRE,
  _$requestedCoverageResponseCodeEnum_OD_NATURE,
  _$requestedCoverageResponseCodeEnum_OD_THEFT,
  _$requestedCoverageResponseCodeEnum_OCCUPANTS,
  _$requestedCoverageResponseCodeEnum_ASSISTANCE,
  _$requestedCoverageResponseCodeEnum_LEGAL,
  _$requestedCoverageResponseCodeEnum_SRCC,
  _$requestedCoverageResponseCodeEnum_CROSS_BORDER,
]);

Serializer<RequestedCoverageResponseCodeEnum>
    _$requestedCoverageResponseCodeEnumSerializer =
    _$RequestedCoverageResponseCodeEnumSerializer();

class _$RequestedCoverageResponseCodeEnumSerializer
    implements PrimitiveSerializer<RequestedCoverageResponseCodeEnum> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'MTPL': 'MTPL',
    'VTPL': 'VTPL',
    'OD_COLLISION': 'OD_COLLISION',
    'OD_GLASS': 'OD_GLASS',
    'OD_FIRE': 'OD_FIRE',
    'OD_NATURE': 'OD_NATURE',
    'OD_THEFT': 'OD_THEFT',
    'OCCUPANTS': 'OCCUPANTS',
    'ASSISTANCE': 'ASSISTANCE',
    'LEGAL': 'LEGAL',
    'SRCC': 'SRCC',
    'CROSS_BORDER': 'CROSS_BORDER',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'MTPL': 'MTPL',
    'VTPL': 'VTPL',
    'OD_COLLISION': 'OD_COLLISION',
    'OD_GLASS': 'OD_GLASS',
    'OD_FIRE': 'OD_FIRE',
    'OD_NATURE': 'OD_NATURE',
    'OD_THEFT': 'OD_THEFT',
    'OCCUPANTS': 'OCCUPANTS',
    'ASSISTANCE': 'ASSISTANCE',
    'LEGAL': 'LEGAL',
    'SRCC': 'SRCC',
    'CROSS_BORDER': 'CROSS_BORDER',
  };

  @override
  final Iterable<Type> types = const <Type>[RequestedCoverageResponseCodeEnum];
  @override
  final String wireName = 'RequestedCoverageResponseCodeEnum';

  @override
  Object serialize(
          Serializers serializers, RequestedCoverageResponseCodeEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  RequestedCoverageResponseCodeEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      RequestedCoverageResponseCodeEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$RequestedCoverageResponse extends RequestedCoverageResponse {
  @override
  final Money? capital;
  @override
  final RequestedCoverageResponseCodeEnum code;

  factory _$RequestedCoverageResponse(
          [void Function(RequestedCoverageResponseBuilder)? updates]) =>
      (RequestedCoverageResponseBuilder()..update(updates))._build();

  _$RequestedCoverageResponse._({this.capital, required this.code}) : super._();
  @override
  RequestedCoverageResponse rebuild(
          void Function(RequestedCoverageResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  RequestedCoverageResponseBuilder toBuilder() =>
      RequestedCoverageResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is RequestedCoverageResponse &&
        capital == other.capital &&
        code == other.code;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, capital.hashCode);
    _$hash = $jc(_$hash, code.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'RequestedCoverageResponse')
          ..add('capital', capital)
          ..add('code', code))
        .toString();
  }
}

class RequestedCoverageResponseBuilder
    implements
        Builder<RequestedCoverageResponse, RequestedCoverageResponseBuilder> {
  _$RequestedCoverageResponse? _$v;

  MoneyBuilder? _capital;
  MoneyBuilder get capital => _$this._capital ??= MoneyBuilder();
  set capital(MoneyBuilder? capital) => _$this._capital = capital;

  RequestedCoverageResponseCodeEnum? _code;
  RequestedCoverageResponseCodeEnum? get code => _$this._code;
  set code(RequestedCoverageResponseCodeEnum? code) => _$this._code = code;

  RequestedCoverageResponseBuilder() {
    RequestedCoverageResponse._defaults(this);
  }

  RequestedCoverageResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _capital = $v.capital?.toBuilder();
      _code = $v.code;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(RequestedCoverageResponse other) {
    _$v = other as _$RequestedCoverageResponse;
  }

  @override
  void update(void Function(RequestedCoverageResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  RequestedCoverageResponse build() => _build();

  _$RequestedCoverageResponse _build() {
    _$RequestedCoverageResponse _$result;
    try {
      _$result = _$v ??
          _$RequestedCoverageResponse._(
            capital: _capital?.build(),
            code: BuiltValueNullFieldError.checkNotNull(
                code, r'RequestedCoverageResponse', 'code'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'capital';
        _capital?.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'RequestedCoverageResponse', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
