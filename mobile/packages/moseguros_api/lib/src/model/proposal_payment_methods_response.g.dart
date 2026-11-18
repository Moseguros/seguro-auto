// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'proposal_payment_methods_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$ProposalPaymentMethodsResponse extends ProposalPaymentMethodsResponse {
  @override
  final IntentView? activeIntent;
  @override
  final Money amount;
  @override
  final DateTime? expiresAt;
  @override
  final PaymentInsurerRef insurer;
  @override
  final BuiltList<PaymentMethodOption> methods;
  @override
  final bool paid;
  @override
  final String proposalId;
  @override
  final String proposalNumber;

  factory _$ProposalPaymentMethodsResponse(
          [void Function(ProposalPaymentMethodsResponseBuilder)? updates]) =>
      (ProposalPaymentMethodsResponseBuilder()..update(updates))._build();

  _$ProposalPaymentMethodsResponse._(
      {this.activeIntent,
      required this.amount,
      this.expiresAt,
      required this.insurer,
      required this.methods,
      required this.paid,
      required this.proposalId,
      required this.proposalNumber})
      : super._();
  @override
  ProposalPaymentMethodsResponse rebuild(
          void Function(ProposalPaymentMethodsResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  ProposalPaymentMethodsResponseBuilder toBuilder() =>
      ProposalPaymentMethodsResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is ProposalPaymentMethodsResponse &&
        activeIntent == other.activeIntent &&
        amount == other.amount &&
        expiresAt == other.expiresAt &&
        insurer == other.insurer &&
        methods == other.methods &&
        paid == other.paid &&
        proposalId == other.proposalId &&
        proposalNumber == other.proposalNumber;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, activeIntent.hashCode);
    _$hash = $jc(_$hash, amount.hashCode);
    _$hash = $jc(_$hash, expiresAt.hashCode);
    _$hash = $jc(_$hash, insurer.hashCode);
    _$hash = $jc(_$hash, methods.hashCode);
    _$hash = $jc(_$hash, paid.hashCode);
    _$hash = $jc(_$hash, proposalId.hashCode);
    _$hash = $jc(_$hash, proposalNumber.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'ProposalPaymentMethodsResponse')
          ..add('activeIntent', activeIntent)
          ..add('amount', amount)
          ..add('expiresAt', expiresAt)
          ..add('insurer', insurer)
          ..add('methods', methods)
          ..add('paid', paid)
          ..add('proposalId', proposalId)
          ..add('proposalNumber', proposalNumber))
        .toString();
  }
}

class ProposalPaymentMethodsResponseBuilder
    implements
        Builder<ProposalPaymentMethodsResponse,
            ProposalPaymentMethodsResponseBuilder> {
  _$ProposalPaymentMethodsResponse? _$v;

  IntentViewBuilder? _activeIntent;
  IntentViewBuilder get activeIntent =>
      _$this._activeIntent ??= IntentViewBuilder();
  set activeIntent(IntentViewBuilder? activeIntent) =>
      _$this._activeIntent = activeIntent;

  MoneyBuilder? _amount;
  MoneyBuilder get amount => _$this._amount ??= MoneyBuilder();
  set amount(MoneyBuilder? amount) => _$this._amount = amount;

  DateTime? _expiresAt;
  DateTime? get expiresAt => _$this._expiresAt;
  set expiresAt(DateTime? expiresAt) => _$this._expiresAt = expiresAt;

  PaymentInsurerRefBuilder? _insurer;
  PaymentInsurerRefBuilder get insurer =>
      _$this._insurer ??= PaymentInsurerRefBuilder();
  set insurer(PaymentInsurerRefBuilder? insurer) => _$this._insurer = insurer;

  ListBuilder<PaymentMethodOption>? _methods;
  ListBuilder<PaymentMethodOption> get methods =>
      _$this._methods ??= ListBuilder<PaymentMethodOption>();
  set methods(ListBuilder<PaymentMethodOption>? methods) =>
      _$this._methods = methods;

  bool? _paid;
  bool? get paid => _$this._paid;
  set paid(bool? paid) => _$this._paid = paid;

  String? _proposalId;
  String? get proposalId => _$this._proposalId;
  set proposalId(String? proposalId) => _$this._proposalId = proposalId;

  String? _proposalNumber;
  String? get proposalNumber => _$this._proposalNumber;
  set proposalNumber(String? proposalNumber) =>
      _$this._proposalNumber = proposalNumber;

  ProposalPaymentMethodsResponseBuilder() {
    ProposalPaymentMethodsResponse._defaults(this);
  }

  ProposalPaymentMethodsResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _activeIntent = $v.activeIntent?.toBuilder();
      _amount = $v.amount.toBuilder();
      _expiresAt = $v.expiresAt;
      _insurer = $v.insurer.toBuilder();
      _methods = $v.methods.toBuilder();
      _paid = $v.paid;
      _proposalId = $v.proposalId;
      _proposalNumber = $v.proposalNumber;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(ProposalPaymentMethodsResponse other) {
    _$v = other as _$ProposalPaymentMethodsResponse;
  }

  @override
  void update(void Function(ProposalPaymentMethodsResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  ProposalPaymentMethodsResponse build() => _build();

  _$ProposalPaymentMethodsResponse _build() {
    _$ProposalPaymentMethodsResponse _$result;
    try {
      _$result = _$v ??
          _$ProposalPaymentMethodsResponse._(
            activeIntent: _activeIntent?.build(),
            amount: amount.build(),
            expiresAt: expiresAt,
            insurer: insurer.build(),
            methods: methods.build(),
            paid: BuiltValueNullFieldError.checkNotNull(
                paid, r'ProposalPaymentMethodsResponse', 'paid'),
            proposalId: BuiltValueNullFieldError.checkNotNull(
                proposalId, r'ProposalPaymentMethodsResponse', 'proposalId'),
            proposalNumber: BuiltValueNullFieldError.checkNotNull(
                proposalNumber,
                r'ProposalPaymentMethodsResponse',
                'proposalNumber'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'activeIntent';
        _activeIntent?.build();
        _$failedField = 'amount';
        amount.build();

        _$failedField = 'insurer';
        insurer.build();
        _$failedField = 'methods';
        methods.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'ProposalPaymentMethodsResponse', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
