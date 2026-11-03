// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'requested_coverage_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const RequestedCoverageRequestCodeEnum _$requestedCoverageRequestCodeEnum_MTPL =
    const RequestedCoverageRequestCodeEnum._('MTPL');
const RequestedCoverageRequestCodeEnum _$requestedCoverageRequestCodeEnum_VTPL =
    const RequestedCoverageRequestCodeEnum._('VTPL');
const RequestedCoverageRequestCodeEnum
    _$requestedCoverageRequestCodeEnum_OD_COLLISION =
    const RequestedCoverageRequestCodeEnum._('OD_COLLISION');
const RequestedCoverageRequestCodeEnum
    _$requestedCoverageRequestCodeEnum_OD_GLASS =
    const RequestedCoverageRequestCodeEnum._('OD_GLASS');
const RequestedCoverageRequestCodeEnum
    _$requestedCoverageRequestCodeEnum_OD_FIRE =
    const RequestedCoverageRequestCodeEnum._('OD_FIRE');
const RequestedCoverageRequestCodeEnum
    _$requestedCoverageRequestCodeEnum_OD_NATURE =
    const RequestedCoverageRequestCodeEnum._('OD_NATURE');
const RequestedCoverageRequestCodeEnum
    _$requestedCoverageRequestCodeEnum_OD_THEFT =
    const RequestedCoverageRequestCodeEnum._('OD_THEFT');
const RequestedCoverageRequestCodeEnum
    _$requestedCoverageRequestCodeEnum_OCCUPANTS =
    const RequestedCoverageRequestCodeEnum._('OCCUPANTS');
const RequestedCoverageRequestCodeEnum
    _$requestedCoverageRequestCodeEnum_ASSISTANCE =
    const RequestedCoverageRequestCodeEnum._('ASSISTANCE');
const RequestedCoverageRequestCodeEnum
    _$requestedCoverageRequestCodeEnum_LEGAL =
    const RequestedCoverageRequestCodeEnum._('LEGAL');
const RequestedCoverageRequestCodeEnum _$requestedCoverageRequestCodeEnum_SRCC =
    const RequestedCoverageRequestCodeEnum._('SRCC');
const RequestedCoverageRequestCodeEnum
    _$requestedCoverageRequestCodeEnum_CROSS_BORDER =
    const RequestedCoverageRequestCodeEnum._('CROSS_BORDER');

RequestedCoverageRequestCodeEnum _$requestedCoverageRequestCodeEnumValueOf(
    String name) {
  switch (name) {
    case 'MTPL':
      return _$requestedCoverageRequestCodeEnum_MTPL;
    case 'VTPL':
      return _$requestedCoverageRequestCodeEnum_VTPL;
    case 'OD_COLLISION':
      return _$requestedCoverageRequestCodeEnum_OD_COLLISION;
    case 'OD_GLASS':
      return _$requestedCoverageRequestCodeEnum_OD_GLASS;
    case 'OD_FIRE':
      return _$requestedCoverageRequestCodeEnum_OD_FIRE;
    case 'OD_NATURE':
      return _$requestedCoverageRequestCodeEnum_OD_NATURE;
    case 'OD_THEFT':
      return _$requestedCoverageRequestCodeEnum_OD_THEFT;
    case 'OCCUPANTS':
      return _$requestedCoverageRequestCodeEnum_OCCUPANTS;
    case 'ASSISTANCE':
      return _$requestedCoverageRequestCodeEnum_ASSISTANCE;
    case 'LEGAL':
      return _$requestedCoverageRequestCodeEnum_LEGAL;
    case 'SRCC':
      return _$requestedCoverageRequestCodeEnum_SRCC;
    case 'CROSS_BORDER':
      return _$requestedCoverageRequestCodeEnum_CROSS_BORDER;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<RequestedCoverageRequestCodeEnum>
    _$requestedCoverageRequestCodeEnumValues = BuiltSet<
        RequestedCoverageRequestCodeEnum>(const <RequestedCoverageRequestCodeEnum>[
  _$requestedCoverageRequestCodeEnum_MTPL,
  _$requestedCoverageRequestCodeEnum_VTPL,
  _$requestedCoverageRequestCodeEnum_OD_COLLISION,
  _$requestedCoverageRequestCodeEnum_OD_GLASS,
  _$requestedCoverageRequestCodeEnum_OD_FIRE,
  _$requestedCoverageRequestCodeEnum_OD_NATURE,
  _$requestedCoverageRequestCodeEnum_OD_THEFT,
  _$requestedCoverageRequestCodeEnum_OCCUPANTS,
  _$requestedCoverageRequestCodeEnum_ASSISTANCE,
  _$requestedCoverageRequestCodeEnum_LEGAL,
  _$requestedCoverageRequestCodeEnum_SRCC,
  _$requestedCoverageRequestCodeEnum_CROSS_BORDER,
]);

Serializer<RequestedCoverageRequestCodeEnum>
    _$requestedCoverageRequestCodeEnumSerializer =
    _$RequestedCoverageRequestCodeEnumSerializer();

class _$RequestedCoverageRequestCodeEnumSerializer
    implements PrimitiveSerializer<RequestedCoverageRequestCodeEnum> {
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
  final Iterable<Type> types = const <Type>[RequestedCoverageRequestCodeEnum];
  @override
  final String wireName = 'RequestedCoverageRequestCodeEnum';

  @override
  Object serialize(
          Serializers serializers, RequestedCoverageRequestCodeEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  RequestedCoverageRequestCodeEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      RequestedCoverageRequestCodeEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$RequestedCoverageRequest extends RequestedCoverageRequest {
  @override
  final Money? capital;
  @override
  final RequestedCoverageRequestCodeEnum code;

  factory _$RequestedCoverageRequest(
          [void Function(RequestedCoverageRequestBuilder)? updates]) =>
      (RequestedCoverageRequestBuilder()..update(updates))._build();

  _$RequestedCoverageRequest._({this.capital, required this.code}) : super._();
  @override
  RequestedCoverageRequest rebuild(
          void Function(RequestedCoverageRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  RequestedCoverageRequestBuilder toBuilder() =>
      RequestedCoverageRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is RequestedCoverageRequest &&
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
    return (newBuiltValueToStringHelper(r'RequestedCoverageRequest')
          ..add('capital', capital)
          ..add('code', code))
        .toString();
  }
}

class RequestedCoverageRequestBuilder
    implements
        Builder<RequestedCoverageRequest, RequestedCoverageRequestBuilder> {
  _$RequestedCoverageRequest? _$v;

  MoneyBuilder? _capital;
  MoneyBuilder get capital => _$this._capital ??= MoneyBuilder();
  set capital(MoneyBuilder? capital) => _$this._capital = capital;

  RequestedCoverageRequestCodeEnum? _code;
  RequestedCoverageRequestCodeEnum? get code => _$this._code;
  set code(RequestedCoverageRequestCodeEnum? code) => _$this._code = code;

  RequestedCoverageRequestBuilder() {
    RequestedCoverageRequest._defaults(this);
  }

  RequestedCoverageRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _capital = $v.capital?.toBuilder();
      _code = $v.code;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(RequestedCoverageRequest other) {
    _$v = other as _$RequestedCoverageRequest;
  }

  @override
  void update(void Function(RequestedCoverageRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  RequestedCoverageRequest build() => _build();

  _$RequestedCoverageRequest _build() {
    _$RequestedCoverageRequest _$result;
    try {
      _$result = _$v ??
          _$RequestedCoverageRequest._(
            capital: _capital?.build(),
            code: BuiltValueNullFieldError.checkNotNull(
                code, r'RequestedCoverageRequest', 'code'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'capital';
        _capital?.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'RequestedCoverageRequest', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
