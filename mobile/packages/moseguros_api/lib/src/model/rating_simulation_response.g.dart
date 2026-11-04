// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'rating_simulation_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const RatingSimulationResponseStatusEnum
    _$ratingSimulationResponseStatusEnum_OFFERED =
    const RatingSimulationResponseStatusEnum._('OFFERED');
const RatingSimulationResponseStatusEnum
    _$ratingSimulationResponseStatusEnum_NOT_AVAILABLE =
    const RatingSimulationResponseStatusEnum._('NOT_AVAILABLE');
const RatingSimulationResponseStatusEnum
    _$ratingSimulationResponseStatusEnum_EXPIRED =
    const RatingSimulationResponseStatusEnum._('EXPIRED');
const RatingSimulationResponseStatusEnum
    _$ratingSimulationResponseStatusEnum_USED =
    const RatingSimulationResponseStatusEnum._('USED');

RatingSimulationResponseStatusEnum _$ratingSimulationResponseStatusEnumValueOf(
    String name) {
  switch (name) {
    case 'OFFERED':
      return _$ratingSimulationResponseStatusEnum_OFFERED;
    case 'NOT_AVAILABLE':
      return _$ratingSimulationResponseStatusEnum_NOT_AVAILABLE;
    case 'EXPIRED':
      return _$ratingSimulationResponseStatusEnum_EXPIRED;
    case 'USED':
      return _$ratingSimulationResponseStatusEnum_USED;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<RatingSimulationResponseStatusEnum>
    _$ratingSimulationResponseStatusEnumValues = BuiltSet<
        RatingSimulationResponseStatusEnum>(const <RatingSimulationResponseStatusEnum>[
  _$ratingSimulationResponseStatusEnum_OFFERED,
  _$ratingSimulationResponseStatusEnum_NOT_AVAILABLE,
  _$ratingSimulationResponseStatusEnum_EXPIRED,
  _$ratingSimulationResponseStatusEnum_USED,
]);

Serializer<RatingSimulationResponseStatusEnum>
    _$ratingSimulationResponseStatusEnumSerializer =
    _$RatingSimulationResponseStatusEnumSerializer();

class _$RatingSimulationResponseStatusEnumSerializer
    implements PrimitiveSerializer<RatingSimulationResponseStatusEnum> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'OFFERED': 'OFFERED',
    'NOT_AVAILABLE': 'NOT_AVAILABLE',
    'EXPIRED': 'EXPIRED',
    'USED': 'USED',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'OFFERED': 'OFFERED',
    'NOT_AVAILABLE': 'NOT_AVAILABLE',
    'EXPIRED': 'EXPIRED',
    'USED': 'USED',
  };

  @override
  final Iterable<Type> types = const <Type>[RatingSimulationResponseStatusEnum];
  @override
  final String wireName = 'RatingSimulationResponseStatusEnum';

  @override
  Object serialize(
          Serializers serializers, RatingSimulationResponseStatusEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  RatingSimulationResponseStatusEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      RatingSimulationResponseStatusEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$RatingSimulationResponse extends RatingSimulationResponse {
  @override
  final BuiltMap<String, JsonObject?>? breakdown;
  @override
  final RatingSimulationResponseStatusEnum status;
  @override
  final BuiltMap<String, JsonObject?>? unavailableDetail;
  @override
  final UnavailableReason? unavailableReason;

  factory _$RatingSimulationResponse(
          [void Function(RatingSimulationResponseBuilder)? updates]) =>
      (RatingSimulationResponseBuilder()..update(updates))._build();

  _$RatingSimulationResponse._(
      {this.breakdown,
      required this.status,
      this.unavailableDetail,
      this.unavailableReason})
      : super._();
  @override
  RatingSimulationResponse rebuild(
          void Function(RatingSimulationResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  RatingSimulationResponseBuilder toBuilder() =>
      RatingSimulationResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is RatingSimulationResponse &&
        breakdown == other.breakdown &&
        status == other.status &&
        unavailableDetail == other.unavailableDetail &&
        unavailableReason == other.unavailableReason;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, breakdown.hashCode);
    _$hash = $jc(_$hash, status.hashCode);
    _$hash = $jc(_$hash, unavailableDetail.hashCode);
    _$hash = $jc(_$hash, unavailableReason.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'RatingSimulationResponse')
          ..add('breakdown', breakdown)
          ..add('status', status)
          ..add('unavailableDetail', unavailableDetail)
          ..add('unavailableReason', unavailableReason))
        .toString();
  }
}

class RatingSimulationResponseBuilder
    implements
        Builder<RatingSimulationResponse, RatingSimulationResponseBuilder> {
  _$RatingSimulationResponse? _$v;

  MapBuilder<String, JsonObject?>? _breakdown;
  MapBuilder<String, JsonObject?> get breakdown =>
      _$this._breakdown ??= MapBuilder<String, JsonObject?>();
  set breakdown(MapBuilder<String, JsonObject?>? breakdown) =>
      _$this._breakdown = breakdown;

  RatingSimulationResponseStatusEnum? _status;
  RatingSimulationResponseStatusEnum? get status => _$this._status;
  set status(RatingSimulationResponseStatusEnum? status) =>
      _$this._status = status;

  MapBuilder<String, JsonObject?>? _unavailableDetail;
  MapBuilder<String, JsonObject?> get unavailableDetail =>
      _$this._unavailableDetail ??= MapBuilder<String, JsonObject?>();
  set unavailableDetail(MapBuilder<String, JsonObject?>? unavailableDetail) =>
      _$this._unavailableDetail = unavailableDetail;

  UnavailableReason? _unavailableReason;
  UnavailableReason? get unavailableReason => _$this._unavailableReason;
  set unavailableReason(UnavailableReason? unavailableReason) =>
      _$this._unavailableReason = unavailableReason;

  RatingSimulationResponseBuilder() {
    RatingSimulationResponse._defaults(this);
  }

  RatingSimulationResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _breakdown = $v.breakdown?.toBuilder();
      _status = $v.status;
      _unavailableDetail = $v.unavailableDetail?.toBuilder();
      _unavailableReason = $v.unavailableReason;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(RatingSimulationResponse other) {
    _$v = other as _$RatingSimulationResponse;
  }

  @override
  void update(void Function(RatingSimulationResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  RatingSimulationResponse build() => _build();

  _$RatingSimulationResponse _build() {
    _$RatingSimulationResponse _$result;
    try {
      _$result = _$v ??
          _$RatingSimulationResponse._(
            breakdown: _breakdown?.build(),
            status: BuiltValueNullFieldError.checkNotNull(
                status, r'RatingSimulationResponse', 'status'),
            unavailableDetail: _unavailableDetail?.build(),
            unavailableReason: unavailableReason,
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'breakdown';
        _breakdown?.build();

        _$failedField = 'unavailableDetail';
        _unavailableDetail?.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'RatingSimulationResponse', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
