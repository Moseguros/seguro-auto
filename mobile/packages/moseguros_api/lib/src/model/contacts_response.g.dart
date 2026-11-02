// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'contacts_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$ContactsResponse extends ContactsResponse {
  @override
  final BuiltList<ContactResponse> contacts;

  factory _$ContactsResponse(
          [void Function(ContactsResponseBuilder)? updates]) =>
      (ContactsResponseBuilder()..update(updates))._build();

  _$ContactsResponse._({required this.contacts}) : super._();
  @override
  ContactsResponse rebuild(void Function(ContactsResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  ContactsResponseBuilder toBuilder() =>
      ContactsResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is ContactsResponse && contacts == other.contacts;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, contacts.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'ContactsResponse')
          ..add('contacts', contacts))
        .toString();
  }
}

class ContactsResponseBuilder
    implements Builder<ContactsResponse, ContactsResponseBuilder> {
  _$ContactsResponse? _$v;

  ListBuilder<ContactResponse>? _contacts;
  ListBuilder<ContactResponse> get contacts =>
      _$this._contacts ??= ListBuilder<ContactResponse>();
  set contacts(ListBuilder<ContactResponse>? contacts) =>
      _$this._contacts = contacts;

  ContactsResponseBuilder() {
    ContactsResponse._defaults(this);
  }

  ContactsResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _contacts = $v.contacts.toBuilder();
      _$v = null;
    }
    return this;
  }

  @override
  void replace(ContactsResponse other) {
    _$v = other as _$ContactsResponse;
  }

  @override
  void update(void Function(ContactsResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  ContactsResponse build() => _build();

  _$ContactsResponse _build() {
    _$ContactsResponse _$result;
    try {
      _$result = _$v ??
          _$ContactsResponse._(
            contacts: contacts.build(),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'contacts';
        contacts.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'ContactsResponse', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
