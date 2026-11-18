// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'last_test_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const LastTestResponseResultEnum _$lastTestResponseResultEnum_OK =
    const LastTestResponseResultEnum._('OK');
const LastTestResponseResultEnum _$lastTestResponseResultEnum_FAILED =
    const LastTestResponseResultEnum._('FAILED');
const LastTestResponseResultEnum _$lastTestResponseResultEnum_NOT_SUPPORTED =
    const LastTestResponseResultEnum._('NOT_SUPPORTED');

LastTestResponseResultEnum _$lastTestResponseResultEnumValueOf(String name) {
  switch (name) {
    case 'OK':
      return _$lastTestResponseResultEnum_OK;
    case 'FAILED':
      return _$lastTestResponseResultEnum_FAILED;
    case 'NOT_SUPPORTED':
      return _$lastTestResponseResultEnum_NOT_SUPPORTED;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<LastTestResponseResultEnum> _$lastTestResponseResultEnumValues =
    BuiltSet<LastTestResponseResultEnum>(const <LastTestResponseResultEnum>[
  _$lastTestResponseResultEnum_OK,
  _$lastTestResponseResultEnum_FAILED,
  _$lastTestResponseResultEnum_NOT_SUPPORTED,
]);

Serializer<LastTestResponseResultEnum> _$lastTestResponseResultEnumSerializer =
    _$LastTestResponseResultEnumSerializer();

class _$LastTestResponseResultEnumSerializer
    implements PrimitiveSerializer<LastTestResponseResultEnum> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'OK': 'OK',
    'FAILED': 'FAILED',
    'NOT_SUPPORTED': 'NOT_SUPPORTED',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'OK': 'OK',
    'FAILED': 'FAILED',
    'NOT_SUPPORTED': 'NOT_SUPPORTED',
  };

  @override
  final Iterable<Type> types = const <Type>[LastTestResponseResultEnum];
  @override
  final String wireName = 'LastTestResponseResultEnum';

  @override
  Object serialize(Serializers serializers, LastTestResponseResultEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  LastTestResponseResultEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      LastTestResponseResultEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$LastTestResponse extends LastTestResponse {
  @override
  final DateTime at;
  @override
  final String? message;
  @override
  final LastTestResponseResultEnum result;

  factory _$LastTestResponse(
          [void Function(LastTestResponseBuilder)? updates]) =>
      (LastTestResponseBuilder()..update(updates))._build();

  _$LastTestResponse._({required this.at, this.message, required this.result})
      : super._();
  @override
  LastTestResponse rebuild(void Function(LastTestResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  LastTestResponseBuilder toBuilder() =>
      LastTestResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is LastTestResponse &&
        at == other.at &&
        message == other.message &&
        result == other.result;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, at.hashCode);
    _$hash = $jc(_$hash, message.hashCode);
    _$hash = $jc(_$hash, result.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'LastTestResponse')
          ..add('at', at)
          ..add('message', message)
          ..add('result', result))
        .toString();
  }
}

class LastTestResponseBuilder
    implements Builder<LastTestResponse, LastTestResponseBuilder> {
  _$LastTestResponse? _$v;

  DateTime? _at;
  DateTime? get at => _$this._at;
  set at(DateTime? at) => _$this._at = at;

  String? _message;
  String? get message => _$this._message;
  set message(String? message) => _$this._message = message;

  LastTestResponseResultEnum? _result;
  LastTestResponseResultEnum? get result => _$this._result;
  set result(LastTestResponseResultEnum? result) => _$this._result = result;

  LastTestResponseBuilder() {
    LastTestResponse._defaults(this);
  }

  LastTestResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _at = $v.at;
      _message = $v.message;
      _result = $v.result;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(LastTestResponse other) {
    _$v = other as _$LastTestResponse;
  }

  @override
  void update(void Function(LastTestResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  LastTestResponse build() => _build();

  _$LastTestResponse _build() {
    final _$result = _$v ??
        _$LastTestResponse._(
          at: BuiltValueNullFieldError.checkNotNull(
              at, r'LastTestResponse', 'at'),
          message: message,
          result: BuiltValueNullFieldError.checkNotNull(
              result, r'LastTestResponse', 'result'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
