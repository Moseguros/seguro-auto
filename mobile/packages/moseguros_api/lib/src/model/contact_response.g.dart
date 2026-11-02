// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'contact_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const ContactResponseTypeEnum _$contactResponseTypeEnum_MSISDN =
    const ContactResponseTypeEnum._('MSISDN');
const ContactResponseTypeEnum _$contactResponseTypeEnum_EMAIL =
    const ContactResponseTypeEnum._('EMAIL');

ContactResponseTypeEnum _$contactResponseTypeEnumValueOf(String name) {
  switch (name) {
    case 'MSISDN':
      return _$contactResponseTypeEnum_MSISDN;
    case 'EMAIL':
      return _$contactResponseTypeEnum_EMAIL;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<ContactResponseTypeEnum> _$contactResponseTypeEnumValues =
    BuiltSet<ContactResponseTypeEnum>(const <ContactResponseTypeEnum>[
  _$contactResponseTypeEnum_MSISDN,
  _$contactResponseTypeEnum_EMAIL,
]);

Serializer<ContactResponseTypeEnum> _$contactResponseTypeEnumSerializer =
    _$ContactResponseTypeEnumSerializer();

class _$ContactResponseTypeEnumSerializer
    implements PrimitiveSerializer<ContactResponseTypeEnum> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'MSISDN': 'MSISDN',
    'EMAIL': 'EMAIL',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'MSISDN': 'MSISDN',
    'EMAIL': 'EMAIL',
  };

  @override
  final Iterable<Type> types = const <Type>[ContactResponseTypeEnum];
  @override
  final String wireName = 'ContactResponseTypeEnum';

  @override
  Object serialize(Serializers serializers, ContactResponseTypeEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  ContactResponseTypeEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      ContactResponseTypeEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$ContactResponse extends ContactResponse {
  @override
  final ContactResponseTypeEnum type;
  @override
  final String value;
  @override
  final bool verified;

  factory _$ContactResponse([void Function(ContactResponseBuilder)? updates]) =>
      (ContactResponseBuilder()..update(updates))._build();

  _$ContactResponse._(
      {required this.type, required this.value, required this.verified})
      : super._();
  @override
  ContactResponse rebuild(void Function(ContactResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  ContactResponseBuilder toBuilder() => ContactResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is ContactResponse &&
        type == other.type &&
        value == other.value &&
        verified == other.verified;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, type.hashCode);
    _$hash = $jc(_$hash, value.hashCode);
    _$hash = $jc(_$hash, verified.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'ContactResponse')
          ..add('type', type)
          ..add('value', value)
          ..add('verified', verified))
        .toString();
  }
}

class ContactResponseBuilder
    implements Builder<ContactResponse, ContactResponseBuilder> {
  _$ContactResponse? _$v;

  ContactResponseTypeEnum? _type;
  ContactResponseTypeEnum? get type => _$this._type;
  set type(ContactResponseTypeEnum? type) => _$this._type = type;

  String? _value;
  String? get value => _$this._value;
  set value(String? value) => _$this._value = value;

  bool? _verified;
  bool? get verified => _$this._verified;
  set verified(bool? verified) => _$this._verified = verified;

  ContactResponseBuilder() {
    ContactResponse._defaults(this);
  }

  ContactResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _type = $v.type;
      _value = $v.value;
      _verified = $v.verified;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(ContactResponse other) {
    _$v = other as _$ContactResponse;
  }

  @override
  void update(void Function(ContactResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  ContactResponse build() => _build();

  _$ContactResponse _build() {
    final _$result = _$v ??
        _$ContactResponse._(
          type: BuiltValueNullFieldError.checkNotNull(
              type, r'ContactResponse', 'type'),
          value: BuiltValueNullFieldError.checkNotNull(
              value, r'ContactResponse', 'value'),
          verified: BuiltValueNullFieldError.checkNotNull(
              verified, r'ContactResponse', 'verified'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
