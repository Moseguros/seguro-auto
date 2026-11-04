// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'proposal_info_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$ProposalInfoRequest extends ProposalInfoRequest {
  @override
  final BuiltList<InfoRequestItem> items;
  @override
  final String message;
  @override
  final DateTime requestedAt;

  factory _$ProposalInfoRequest(
          [void Function(ProposalInfoRequestBuilder)? updates]) =>
      (ProposalInfoRequestBuilder()..update(updates))._build();

  _$ProposalInfoRequest._(
      {required this.items, required this.message, required this.requestedAt})
      : super._();
  @override
  ProposalInfoRequest rebuild(
          void Function(ProposalInfoRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  ProposalInfoRequestBuilder toBuilder() =>
      ProposalInfoRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is ProposalInfoRequest &&
        items == other.items &&
        message == other.message &&
        requestedAt == other.requestedAt;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, items.hashCode);
    _$hash = $jc(_$hash, message.hashCode);
    _$hash = $jc(_$hash, requestedAt.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'ProposalInfoRequest')
          ..add('items', items)
          ..add('message', message)
          ..add('requestedAt', requestedAt))
        .toString();
  }
}

class ProposalInfoRequestBuilder
    implements Builder<ProposalInfoRequest, ProposalInfoRequestBuilder> {
  _$ProposalInfoRequest? _$v;

  ListBuilder<InfoRequestItem>? _items;
  ListBuilder<InfoRequestItem> get items =>
      _$this._items ??= ListBuilder<InfoRequestItem>();
  set items(ListBuilder<InfoRequestItem>? items) => _$this._items = items;

  String? _message;
  String? get message => _$this._message;
  set message(String? message) => _$this._message = message;

  DateTime? _requestedAt;
  DateTime? get requestedAt => _$this._requestedAt;
  set requestedAt(DateTime? requestedAt) => _$this._requestedAt = requestedAt;

  ProposalInfoRequestBuilder() {
    ProposalInfoRequest._defaults(this);
  }

  ProposalInfoRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _items = $v.items.toBuilder();
      _message = $v.message;
      _requestedAt = $v.requestedAt;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(ProposalInfoRequest other) {
    _$v = other as _$ProposalInfoRequest;
  }

  @override
  void update(void Function(ProposalInfoRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  ProposalInfoRequest build() => _build();

  _$ProposalInfoRequest _build() {
    _$ProposalInfoRequest _$result;
    try {
      _$result = _$v ??
          _$ProposalInfoRequest._(
            items: items.build(),
            message: BuiltValueNullFieldError.checkNotNull(
                message, r'ProposalInfoRequest', 'message'),
            requestedAt: BuiltValueNullFieldError.checkNotNull(
                requestedAt, r'ProposalInfoRequest', 'requestedAt'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'items';
        items.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'ProposalInfoRequest', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
