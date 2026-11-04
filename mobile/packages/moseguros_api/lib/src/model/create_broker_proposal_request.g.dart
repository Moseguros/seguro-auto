// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'create_broker_proposal_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$CreateBrokerProposalRequest extends CreateBrokerProposalRequest {
  @override
  final BrokerProposalDriverRequest driver;
  @override
  final BrokerPolicyholderRequest? policyholder;
  @override
  final String quoteId;
  @override
  final BrokerProposalVehicleRequest vehicle;

  factory _$CreateBrokerProposalRequest(
          [void Function(CreateBrokerProposalRequestBuilder)? updates]) =>
      (CreateBrokerProposalRequestBuilder()..update(updates))._build();

  _$CreateBrokerProposalRequest._(
      {required this.driver,
      this.policyholder,
      required this.quoteId,
      required this.vehicle})
      : super._();
  @override
  CreateBrokerProposalRequest rebuild(
          void Function(CreateBrokerProposalRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  CreateBrokerProposalRequestBuilder toBuilder() =>
      CreateBrokerProposalRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is CreateBrokerProposalRequest &&
        driver == other.driver &&
        policyholder == other.policyholder &&
        quoteId == other.quoteId &&
        vehicle == other.vehicle;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, driver.hashCode);
    _$hash = $jc(_$hash, policyholder.hashCode);
    _$hash = $jc(_$hash, quoteId.hashCode);
    _$hash = $jc(_$hash, vehicle.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'CreateBrokerProposalRequest')
          ..add('driver', driver)
          ..add('policyholder', policyholder)
          ..add('quoteId', quoteId)
          ..add('vehicle', vehicle))
        .toString();
  }
}

class CreateBrokerProposalRequestBuilder
    implements
        Builder<CreateBrokerProposalRequest,
            CreateBrokerProposalRequestBuilder> {
  _$CreateBrokerProposalRequest? _$v;

  BrokerProposalDriverRequestBuilder? _driver;
  BrokerProposalDriverRequestBuilder get driver =>
      _$this._driver ??= BrokerProposalDriverRequestBuilder();
  set driver(BrokerProposalDriverRequestBuilder? driver) =>
      _$this._driver = driver;

  BrokerPolicyholderRequestBuilder? _policyholder;
  BrokerPolicyholderRequestBuilder get policyholder =>
      _$this._policyholder ??= BrokerPolicyholderRequestBuilder();
  set policyholder(BrokerPolicyholderRequestBuilder? policyholder) =>
      _$this._policyholder = policyholder;

  String? _quoteId;
  String? get quoteId => _$this._quoteId;
  set quoteId(String? quoteId) => _$this._quoteId = quoteId;

  BrokerProposalVehicleRequestBuilder? _vehicle;
  BrokerProposalVehicleRequestBuilder get vehicle =>
      _$this._vehicle ??= BrokerProposalVehicleRequestBuilder();
  set vehicle(BrokerProposalVehicleRequestBuilder? vehicle) =>
      _$this._vehicle = vehicle;

  CreateBrokerProposalRequestBuilder() {
    CreateBrokerProposalRequest._defaults(this);
  }

  CreateBrokerProposalRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _driver = $v.driver.toBuilder();
      _policyholder = $v.policyholder?.toBuilder();
      _quoteId = $v.quoteId;
      _vehicle = $v.vehicle.toBuilder();
      _$v = null;
    }
    return this;
  }

  @override
  void replace(CreateBrokerProposalRequest other) {
    _$v = other as _$CreateBrokerProposalRequest;
  }

  @override
  void update(void Function(CreateBrokerProposalRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  CreateBrokerProposalRequest build() => _build();

  _$CreateBrokerProposalRequest _build() {
    _$CreateBrokerProposalRequest _$result;
    try {
      _$result = _$v ??
          _$CreateBrokerProposalRequest._(
            driver: driver.build(),
            policyholder: _policyholder?.build(),
            quoteId: BuiltValueNullFieldError.checkNotNull(
                quoteId, r'CreateBrokerProposalRequest', 'quoteId'),
            vehicle: vehicle.build(),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'driver';
        driver.build();
        _$failedField = 'policyholder';
        _policyholder?.build();

        _$failedField = 'vehicle';
        vehicle.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'CreateBrokerProposalRequest', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
