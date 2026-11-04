// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'request_proposal_info_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$RequestProposalInfoRequest extends RequestProposalInfoRequest {
  @override
  final BuiltList<String> items;
  @override
  final String message;
  @override
  final int version;

  factory _$RequestProposalInfoRequest(
          [void Function(RequestProposalInfoRequestBuilder)? updates]) =>
      (RequestProposalInfoRequestBuilder()..update(updates))._build();

  _$RequestProposalInfoRequest._(
      {required this.items, required this.message, required this.version})
      : super._();
  @override
  RequestProposalInfoRequest rebuild(
          void Function(RequestProposalInfoRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  RequestProposalInfoRequestBuilder toBuilder() =>
      RequestProposalInfoRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is RequestProposalInfoRequest &&
        items == other.items &&
        message == other.message &&
        version == other.version;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, items.hashCode);
    _$hash = $jc(_$hash, message.hashCode);
    _$hash = $jc(_$hash, version.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'RequestProposalInfoRequest')
          ..add('items', items)
          ..add('message', message)
          ..add('version', version))
        .toString();
  }
}

class RequestProposalInfoRequestBuilder
    implements
        Builder<RequestProposalInfoRequest, RequestProposalInfoRequestBuilder> {
  _$RequestProposalInfoRequest? _$v;

  ListBuilder<String>? _items;
  ListBuilder<String> get items => _$this._items ??= ListBuilder<String>();
  set items(ListBuilder<String>? items) => _$this._items = items;

  String? _message;
  String? get message => _$this._message;
  set message(String? message) => _$this._message = message;

  int? _version;
  int? get version => _$this._version;
  set version(int? version) => _$this._version = version;

  RequestProposalInfoRequestBuilder() {
    RequestProposalInfoRequest._defaults(this);
  }

  RequestProposalInfoRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _items = $v.items.toBuilder();
      _message = $v.message;
      _version = $v.version;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(RequestProposalInfoRequest other) {
    _$v = other as _$RequestProposalInfoRequest;
  }

  @override
  void update(void Function(RequestProposalInfoRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  RequestProposalInfoRequest build() => _build();

  _$RequestProposalInfoRequest _build() {
    _$RequestProposalInfoRequest _$result;
    try {
      _$result = _$v ??
          _$RequestProposalInfoRequest._(
            items: items.build(),
            message: BuiltValueNullFieldError.checkNotNull(
                message, r'RequestProposalInfoRequest', 'message'),
            version: BuiltValueNullFieldError.checkNotNull(
                version, r'RequestProposalInfoRequest', 'version'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'items';
        items.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'RequestProposalInfoRequest', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
