// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'update_me_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const UpdateMeRequestPreferredLanguageEnum
    _$updateMeRequestPreferredLanguageEnum_pt =
    const UpdateMeRequestPreferredLanguageEnum._('pt');
const UpdateMeRequestPreferredLanguageEnum
    _$updateMeRequestPreferredLanguageEnum_en =
    const UpdateMeRequestPreferredLanguageEnum._('en');

UpdateMeRequestPreferredLanguageEnum
    _$updateMeRequestPreferredLanguageEnumValueOf(String name) {
  switch (name) {
    case 'pt':
      return _$updateMeRequestPreferredLanguageEnum_pt;
    case 'en':
      return _$updateMeRequestPreferredLanguageEnum_en;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<UpdateMeRequestPreferredLanguageEnum>
    _$updateMeRequestPreferredLanguageEnumValues = BuiltSet<
        UpdateMeRequestPreferredLanguageEnum>(const <UpdateMeRequestPreferredLanguageEnum>[
  _$updateMeRequestPreferredLanguageEnum_pt,
  _$updateMeRequestPreferredLanguageEnum_en,
]);

Serializer<UpdateMeRequestPreferredLanguageEnum>
    _$updateMeRequestPreferredLanguageEnumSerializer =
    _$UpdateMeRequestPreferredLanguageEnumSerializer();

class _$UpdateMeRequestPreferredLanguageEnumSerializer
    implements PrimitiveSerializer<UpdateMeRequestPreferredLanguageEnum> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'pt': 'pt',
    'en': 'en',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'pt': 'pt',
    'en': 'en',
  };

  @override
  final Iterable<Type> types = const <Type>[
    UpdateMeRequestPreferredLanguageEnum
  ];
  @override
  final String wireName = 'UpdateMeRequestPreferredLanguageEnum';

  @override
  Object serialize(
          Serializers serializers, UpdateMeRequestPreferredLanguageEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  UpdateMeRequestPreferredLanguageEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      UpdateMeRequestPreferredLanguageEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$UpdateMeRequest extends UpdateMeRequest {
  @override
  final String? displayName;
  @override
  final UpdateMeRequestPreferredLanguageEnum preferredLanguage;

  factory _$UpdateMeRequest([void Function(UpdateMeRequestBuilder)? updates]) =>
      (UpdateMeRequestBuilder()..update(updates))._build();

  _$UpdateMeRequest._({this.displayName, required this.preferredLanguage})
      : super._();
  @override
  UpdateMeRequest rebuild(void Function(UpdateMeRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  UpdateMeRequestBuilder toBuilder() => UpdateMeRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is UpdateMeRequest &&
        displayName == other.displayName &&
        preferredLanguage == other.preferredLanguage;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, displayName.hashCode);
    _$hash = $jc(_$hash, preferredLanguage.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'UpdateMeRequest')
          ..add('displayName', displayName)
          ..add('preferredLanguage', preferredLanguage))
        .toString();
  }
}

class UpdateMeRequestBuilder
    implements Builder<UpdateMeRequest, UpdateMeRequestBuilder> {
  _$UpdateMeRequest? _$v;

  String? _displayName;
  String? get displayName => _$this._displayName;
  set displayName(String? displayName) => _$this._displayName = displayName;

  UpdateMeRequestPreferredLanguageEnum? _preferredLanguage;
  UpdateMeRequestPreferredLanguageEnum? get preferredLanguage =>
      _$this._preferredLanguage;
  set preferredLanguage(
          UpdateMeRequestPreferredLanguageEnum? preferredLanguage) =>
      _$this._preferredLanguage = preferredLanguage;

  UpdateMeRequestBuilder() {
    UpdateMeRequest._defaults(this);
  }

  UpdateMeRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _displayName = $v.displayName;
      _preferredLanguage = $v.preferredLanguage;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(UpdateMeRequest other) {
    _$v = other as _$UpdateMeRequest;
  }

  @override
  void update(void Function(UpdateMeRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  UpdateMeRequest build() => _build();

  _$UpdateMeRequest _build() {
    final _$result = _$v ??
        _$UpdateMeRequest._(
          displayName: displayName,
          preferredLanguage: BuiltValueNullFieldError.checkNotNull(
              preferredLanguage, r'UpdateMeRequest', 'preferredLanguage'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
