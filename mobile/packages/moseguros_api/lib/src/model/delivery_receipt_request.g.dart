// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'delivery_receipt_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const DeliveryReceiptRequestStatusEnum
    _$deliveryReceiptRequestStatusEnum_DELIVERED =
    const DeliveryReceiptRequestStatusEnum._('DELIVERED');
const DeliveryReceiptRequestStatusEnum
    _$deliveryReceiptRequestStatusEnum_FAILED =
    const DeliveryReceiptRequestStatusEnum._('FAILED');

DeliveryReceiptRequestStatusEnum _$deliveryReceiptRequestStatusEnumValueOf(
    String name) {
  switch (name) {
    case 'DELIVERED':
      return _$deliveryReceiptRequestStatusEnum_DELIVERED;
    case 'FAILED':
      return _$deliveryReceiptRequestStatusEnum_FAILED;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<DeliveryReceiptRequestStatusEnum>
    _$deliveryReceiptRequestStatusEnumValues = BuiltSet<
        DeliveryReceiptRequestStatusEnum>(const <DeliveryReceiptRequestStatusEnum>[
  _$deliveryReceiptRequestStatusEnum_DELIVERED,
  _$deliveryReceiptRequestStatusEnum_FAILED,
]);

Serializer<DeliveryReceiptRequestStatusEnum>
    _$deliveryReceiptRequestStatusEnumSerializer =
    _$DeliveryReceiptRequestStatusEnumSerializer();

class _$DeliveryReceiptRequestStatusEnumSerializer
    implements PrimitiveSerializer<DeliveryReceiptRequestStatusEnum> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'DELIVERED': 'DELIVERED',
    'FAILED': 'FAILED',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'DELIVERED': 'DELIVERED',
    'FAILED': 'FAILED',
  };

  @override
  final Iterable<Type> types = const <Type>[DeliveryReceiptRequestStatusEnum];
  @override
  final String wireName = 'DeliveryReceiptRequestStatusEnum';

  @override
  Object serialize(
          Serializers serializers, DeliveryReceiptRequestStatusEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  DeliveryReceiptRequestStatusEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      DeliveryReceiptRequestStatusEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$DeliveryReceiptRequest extends DeliveryReceiptRequest {
  @override
  final String providerMessageId;
  @override
  final String? reason;
  @override
  final DeliveryReceiptRequestStatusEnum status;

  factory _$DeliveryReceiptRequest(
          [void Function(DeliveryReceiptRequestBuilder)? updates]) =>
      (DeliveryReceiptRequestBuilder()..update(updates))._build();

  _$DeliveryReceiptRequest._(
      {required this.providerMessageId, this.reason, required this.status})
      : super._();
  @override
  DeliveryReceiptRequest rebuild(
          void Function(DeliveryReceiptRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  DeliveryReceiptRequestBuilder toBuilder() =>
      DeliveryReceiptRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is DeliveryReceiptRequest &&
        providerMessageId == other.providerMessageId &&
        reason == other.reason &&
        status == other.status;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, providerMessageId.hashCode);
    _$hash = $jc(_$hash, reason.hashCode);
    _$hash = $jc(_$hash, status.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'DeliveryReceiptRequest')
          ..add('providerMessageId', providerMessageId)
          ..add('reason', reason)
          ..add('status', status))
        .toString();
  }
}

class DeliveryReceiptRequestBuilder
    implements Builder<DeliveryReceiptRequest, DeliveryReceiptRequestBuilder> {
  _$DeliveryReceiptRequest? _$v;

  String? _providerMessageId;
  String? get providerMessageId => _$this._providerMessageId;
  set providerMessageId(String? providerMessageId) =>
      _$this._providerMessageId = providerMessageId;

  String? _reason;
  String? get reason => _$this._reason;
  set reason(String? reason) => _$this._reason = reason;

  DeliveryReceiptRequestStatusEnum? _status;
  DeliveryReceiptRequestStatusEnum? get status => _$this._status;
  set status(DeliveryReceiptRequestStatusEnum? status) =>
      _$this._status = status;

  DeliveryReceiptRequestBuilder() {
    DeliveryReceiptRequest._defaults(this);
  }

  DeliveryReceiptRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _providerMessageId = $v.providerMessageId;
      _reason = $v.reason;
      _status = $v.status;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(DeliveryReceiptRequest other) {
    _$v = other as _$DeliveryReceiptRequest;
  }

  @override
  void update(void Function(DeliveryReceiptRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  DeliveryReceiptRequest build() => _build();

  _$DeliveryReceiptRequest _build() {
    final _$result = _$v ??
        _$DeliveryReceiptRequest._(
          providerMessageId: BuiltValueNullFieldError.checkNotNull(
              providerMessageId,
              r'DeliveryReceiptRequest',
              'providerMessageId'),
          reason: reason,
          status: BuiltValueNullFieldError.checkNotNull(
              status, r'DeliveryReceiptRequest', 'status'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
