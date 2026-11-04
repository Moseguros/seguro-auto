// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'kyc_customer_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const KycCustomerResponseIdDocTypeEnum _$kycCustomerResponseIdDocTypeEnum_BI =
    const KycCustomerResponseIdDocTypeEnum._('BI');
const KycCustomerResponseIdDocTypeEnum
    _$kycCustomerResponseIdDocTypeEnum_PASSPORT =
    const KycCustomerResponseIdDocTypeEnum._('PASSPORT');
const KycCustomerResponseIdDocTypeEnum _$kycCustomerResponseIdDocTypeEnum_DIRE =
    const KycCustomerResponseIdDocTypeEnum._('DIRE');

KycCustomerResponseIdDocTypeEnum _$kycCustomerResponseIdDocTypeEnumValueOf(
    String name) {
  switch (name) {
    case 'BI':
      return _$kycCustomerResponseIdDocTypeEnum_BI;
    case 'PASSPORT':
      return _$kycCustomerResponseIdDocTypeEnum_PASSPORT;
    case 'DIRE':
      return _$kycCustomerResponseIdDocTypeEnum_DIRE;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<KycCustomerResponseIdDocTypeEnum>
    _$kycCustomerResponseIdDocTypeEnumValues = BuiltSet<
        KycCustomerResponseIdDocTypeEnum>(const <KycCustomerResponseIdDocTypeEnum>[
  _$kycCustomerResponseIdDocTypeEnum_BI,
  _$kycCustomerResponseIdDocTypeEnum_PASSPORT,
  _$kycCustomerResponseIdDocTypeEnum_DIRE,
]);

Serializer<KycCustomerResponseIdDocTypeEnum>
    _$kycCustomerResponseIdDocTypeEnumSerializer =
    _$KycCustomerResponseIdDocTypeEnumSerializer();

class _$KycCustomerResponseIdDocTypeEnumSerializer
    implements PrimitiveSerializer<KycCustomerResponseIdDocTypeEnum> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'BI': 'BI',
    'PASSPORT': 'PASSPORT',
    'DIRE': 'DIRE',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'BI': 'BI',
    'PASSPORT': 'PASSPORT',
    'DIRE': 'DIRE',
  };

  @override
  final Iterable<Type> types = const <Type>[KycCustomerResponseIdDocTypeEnum];
  @override
  final String wireName = 'KycCustomerResponseIdDocTypeEnum';

  @override
  Object serialize(
          Serializers serializers, KycCustomerResponseIdDocTypeEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  KycCustomerResponseIdDocTypeEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      KycCustomerResponseIdDocTypeEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$KycCustomerResponse extends KycCustomerResponse {
  @override
  final Date birthDate;
  @override
  final String fullName;
  @override
  final String idDocNumber;
  @override
  final KycCustomerResponseIdDocTypeEnum idDocType;
  @override
  final String? msisdnMasked;
  @override
  final String userId;

  factory _$KycCustomerResponse(
          [void Function(KycCustomerResponseBuilder)? updates]) =>
      (KycCustomerResponseBuilder()..update(updates))._build();

  _$KycCustomerResponse._(
      {required this.birthDate,
      required this.fullName,
      required this.idDocNumber,
      required this.idDocType,
      this.msisdnMasked,
      required this.userId})
      : super._();
  @override
  KycCustomerResponse rebuild(
          void Function(KycCustomerResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  KycCustomerResponseBuilder toBuilder() =>
      KycCustomerResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is KycCustomerResponse &&
        birthDate == other.birthDate &&
        fullName == other.fullName &&
        idDocNumber == other.idDocNumber &&
        idDocType == other.idDocType &&
        msisdnMasked == other.msisdnMasked &&
        userId == other.userId;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, birthDate.hashCode);
    _$hash = $jc(_$hash, fullName.hashCode);
    _$hash = $jc(_$hash, idDocNumber.hashCode);
    _$hash = $jc(_$hash, idDocType.hashCode);
    _$hash = $jc(_$hash, msisdnMasked.hashCode);
    _$hash = $jc(_$hash, userId.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'KycCustomerResponse')
          ..add('birthDate', birthDate)
          ..add('fullName', fullName)
          ..add('idDocNumber', idDocNumber)
          ..add('idDocType', idDocType)
          ..add('msisdnMasked', msisdnMasked)
          ..add('userId', userId))
        .toString();
  }
}

class KycCustomerResponseBuilder
    implements Builder<KycCustomerResponse, KycCustomerResponseBuilder> {
  _$KycCustomerResponse? _$v;

  Date? _birthDate;
  Date? get birthDate => _$this._birthDate;
  set birthDate(Date? birthDate) => _$this._birthDate = birthDate;

  String? _fullName;
  String? get fullName => _$this._fullName;
  set fullName(String? fullName) => _$this._fullName = fullName;

  String? _idDocNumber;
  String? get idDocNumber => _$this._idDocNumber;
  set idDocNumber(String? idDocNumber) => _$this._idDocNumber = idDocNumber;

  KycCustomerResponseIdDocTypeEnum? _idDocType;
  KycCustomerResponseIdDocTypeEnum? get idDocType => _$this._idDocType;
  set idDocType(KycCustomerResponseIdDocTypeEnum? idDocType) =>
      _$this._idDocType = idDocType;

  String? _msisdnMasked;
  String? get msisdnMasked => _$this._msisdnMasked;
  set msisdnMasked(String? msisdnMasked) => _$this._msisdnMasked = msisdnMasked;

  String? _userId;
  String? get userId => _$this._userId;
  set userId(String? userId) => _$this._userId = userId;

  KycCustomerResponseBuilder() {
    KycCustomerResponse._defaults(this);
  }

  KycCustomerResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _birthDate = $v.birthDate;
      _fullName = $v.fullName;
      _idDocNumber = $v.idDocNumber;
      _idDocType = $v.idDocType;
      _msisdnMasked = $v.msisdnMasked;
      _userId = $v.userId;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(KycCustomerResponse other) {
    _$v = other as _$KycCustomerResponse;
  }

  @override
  void update(void Function(KycCustomerResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  KycCustomerResponse build() => _build();

  _$KycCustomerResponse _build() {
    final _$result = _$v ??
        _$KycCustomerResponse._(
          birthDate: BuiltValueNullFieldError.checkNotNull(
              birthDate, r'KycCustomerResponse', 'birthDate'),
          fullName: BuiltValueNullFieldError.checkNotNull(
              fullName, r'KycCustomerResponse', 'fullName'),
          idDocNumber: BuiltValueNullFieldError.checkNotNull(
              idDocNumber, r'KycCustomerResponse', 'idDocNumber'),
          idDocType: BuiltValueNullFieldError.checkNotNull(
              idDocType, r'KycCustomerResponse', 'idDocType'),
          msisdnMasked: msisdnMasked,
          userId: BuiltValueNullFieldError.checkNotNull(
              userId, r'KycCustomerResponse', 'userId'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
