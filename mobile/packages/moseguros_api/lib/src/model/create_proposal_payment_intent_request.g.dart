// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'create_proposal_payment_intent_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const CreateProposalPaymentIntentRequestMethodEnum
    _$createProposalPaymentIntentRequestMethodEnum_MPESA =
    const CreateProposalPaymentIntentRequestMethodEnum._('MPESA');
const CreateProposalPaymentIntentRequestMethodEnum
    _$createProposalPaymentIntentRequestMethodEnum_BANK_REFERENCE =
    const CreateProposalPaymentIntentRequestMethodEnum._('BANK_REFERENCE');

CreateProposalPaymentIntentRequestMethodEnum
    _$createProposalPaymentIntentRequestMethodEnumValueOf(String name) {
  switch (name) {
    case 'MPESA':
      return _$createProposalPaymentIntentRequestMethodEnum_MPESA;
    case 'BANK_REFERENCE':
      return _$createProposalPaymentIntentRequestMethodEnum_BANK_REFERENCE;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<CreateProposalPaymentIntentRequestMethodEnum>
    _$createProposalPaymentIntentRequestMethodEnumValues = BuiltSet<
        CreateProposalPaymentIntentRequestMethodEnum>(const <CreateProposalPaymentIntentRequestMethodEnum>[
  _$createProposalPaymentIntentRequestMethodEnum_MPESA,
  _$createProposalPaymentIntentRequestMethodEnum_BANK_REFERENCE,
]);

Serializer<CreateProposalPaymentIntentRequestMethodEnum>
    _$createProposalPaymentIntentRequestMethodEnumSerializer =
    _$CreateProposalPaymentIntentRequestMethodEnumSerializer();

class _$CreateProposalPaymentIntentRequestMethodEnumSerializer
    implements
        PrimitiveSerializer<CreateProposalPaymentIntentRequestMethodEnum> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'MPESA': 'MPESA',
    'BANK_REFERENCE': 'BANK_REFERENCE',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'MPESA': 'MPESA',
    'BANK_REFERENCE': 'BANK_REFERENCE',
  };

  @override
  final Iterable<Type> types = const <Type>[
    CreateProposalPaymentIntentRequestMethodEnum
  ];
  @override
  final String wireName = 'CreateProposalPaymentIntentRequestMethodEnum';

  @override
  Object serialize(Serializers serializers,
          CreateProposalPaymentIntentRequestMethodEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  CreateProposalPaymentIntentRequestMethodEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      CreateProposalPaymentIntentRequestMethodEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$CreateProposalPaymentIntentRequest
    extends CreateProposalPaymentIntentRequest {
  @override
  final ProposalPaymentChannel channel;
  @override
  final CreateProposalPaymentIntentRequestMethodEnum method;
  @override
  final String? payerMsisdn;

  factory _$CreateProposalPaymentIntentRequest(
          [void Function(CreateProposalPaymentIntentRequestBuilder)?
              updates]) =>
      (CreateProposalPaymentIntentRequestBuilder()..update(updates))._build();

  _$CreateProposalPaymentIntentRequest._(
      {required this.channel, required this.method, this.payerMsisdn})
      : super._();
  @override
  CreateProposalPaymentIntentRequest rebuild(
          void Function(CreateProposalPaymentIntentRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  CreateProposalPaymentIntentRequestBuilder toBuilder() =>
      CreateProposalPaymentIntentRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is CreateProposalPaymentIntentRequest &&
        channel == other.channel &&
        method == other.method &&
        payerMsisdn == other.payerMsisdn;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, channel.hashCode);
    _$hash = $jc(_$hash, method.hashCode);
    _$hash = $jc(_$hash, payerMsisdn.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'CreateProposalPaymentIntentRequest')
          ..add('channel', channel)
          ..add('method', method)
          ..add('payerMsisdn', payerMsisdn))
        .toString();
  }
}

class CreateProposalPaymentIntentRequestBuilder
    implements
        Builder<CreateProposalPaymentIntentRequest,
            CreateProposalPaymentIntentRequestBuilder> {
  _$CreateProposalPaymentIntentRequest? _$v;

  ProposalPaymentChannel? _channel;
  ProposalPaymentChannel? get channel => _$this._channel;
  set channel(ProposalPaymentChannel? channel) => _$this._channel = channel;

  CreateProposalPaymentIntentRequestMethodEnum? _method;
  CreateProposalPaymentIntentRequestMethodEnum? get method => _$this._method;
  set method(CreateProposalPaymentIntentRequestMethodEnum? method) =>
      _$this._method = method;

  String? _payerMsisdn;
  String? get payerMsisdn => _$this._payerMsisdn;
  set payerMsisdn(String? payerMsisdn) => _$this._payerMsisdn = payerMsisdn;

  CreateProposalPaymentIntentRequestBuilder() {
    CreateProposalPaymentIntentRequest._defaults(this);
  }

  CreateProposalPaymentIntentRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _channel = $v.channel;
      _method = $v.method;
      _payerMsisdn = $v.payerMsisdn;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(CreateProposalPaymentIntentRequest other) {
    _$v = other as _$CreateProposalPaymentIntentRequest;
  }

  @override
  void update(
      void Function(CreateProposalPaymentIntentRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  CreateProposalPaymentIntentRequest build() => _build();

  _$CreateProposalPaymentIntentRequest _build() {
    final _$result = _$v ??
        _$CreateProposalPaymentIntentRequest._(
          channel: BuiltValueNullFieldError.checkNotNull(
              channel, r'CreateProposalPaymentIntentRequest', 'channel'),
          method: BuiltValueNullFieldError.checkNotNull(
              method, r'CreateProposalPaymentIntentRequest', 'method'),
          payerMsisdn: payerMsisdn,
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
