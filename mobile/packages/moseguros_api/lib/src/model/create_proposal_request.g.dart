// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'create_proposal_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$CreateProposalRequest extends CreateProposalRequest {
  @override
  final ProposalChannel channel;
  @override
  final String quoteId;

  factory _$CreateProposalRequest(
          [void Function(CreateProposalRequestBuilder)? updates]) =>
      (CreateProposalRequestBuilder()..update(updates))._build();

  _$CreateProposalRequest._({required this.channel, required this.quoteId})
      : super._();
  @override
  CreateProposalRequest rebuild(
          void Function(CreateProposalRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  CreateProposalRequestBuilder toBuilder() =>
      CreateProposalRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is CreateProposalRequest &&
        channel == other.channel &&
        quoteId == other.quoteId;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, channel.hashCode);
    _$hash = $jc(_$hash, quoteId.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'CreateProposalRequest')
          ..add('channel', channel)
          ..add('quoteId', quoteId))
        .toString();
  }
}

class CreateProposalRequestBuilder
    implements Builder<CreateProposalRequest, CreateProposalRequestBuilder> {
  _$CreateProposalRequest? _$v;

  ProposalChannel? _channel;
  ProposalChannel? get channel => _$this._channel;
  set channel(ProposalChannel? channel) => _$this._channel = channel;

  String? _quoteId;
  String? get quoteId => _$this._quoteId;
  set quoteId(String? quoteId) => _$this._quoteId = quoteId;

  CreateProposalRequestBuilder() {
    CreateProposalRequest._defaults(this);
  }

  CreateProposalRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _channel = $v.channel;
      _quoteId = $v.quoteId;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(CreateProposalRequest other) {
    _$v = other as _$CreateProposalRequest;
  }

  @override
  void update(void Function(CreateProposalRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  CreateProposalRequest build() => _build();

  _$CreateProposalRequest _build() {
    final _$result = _$v ??
        _$CreateProposalRequest._(
          channel: BuiltValueNullFieldError.checkNotNull(
              channel, r'CreateProposalRequest', 'channel'),
          quoteId: BuiltValueNullFieldError.checkNotNull(
              quoteId, r'CreateProposalRequest', 'quoteId'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
