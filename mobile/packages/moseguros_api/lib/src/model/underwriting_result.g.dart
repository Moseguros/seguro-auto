// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'underwriting_result.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const UnderwritingResultRulesSourceEnum
    _$underwritingResultRulesSourceEnum_PRODUCT =
    const UnderwritingResultRulesSourceEnum._('PRODUCT');
const UnderwritingResultRulesSourceEnum
    _$underwritingResultRulesSourceEnum_DEFAULT =
    const UnderwritingResultRulesSourceEnum._('DEFAULT');

UnderwritingResultRulesSourceEnum _$underwritingResultRulesSourceEnumValueOf(
    String name) {
  switch (name) {
    case 'PRODUCT':
      return _$underwritingResultRulesSourceEnum_PRODUCT;
    case 'DEFAULT':
      return _$underwritingResultRulesSourceEnum_DEFAULT;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<UnderwritingResultRulesSourceEnum>
    _$underwritingResultRulesSourceEnumValues = BuiltSet<
        UnderwritingResultRulesSourceEnum>(const <UnderwritingResultRulesSourceEnum>[
  _$underwritingResultRulesSourceEnum_PRODUCT,
  _$underwritingResultRulesSourceEnum_DEFAULT,
]);

Serializer<UnderwritingResultRulesSourceEnum>
    _$underwritingResultRulesSourceEnumSerializer =
    _$UnderwritingResultRulesSourceEnumSerializer();

class _$UnderwritingResultRulesSourceEnumSerializer
    implements PrimitiveSerializer<UnderwritingResultRulesSourceEnum> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'PRODUCT': 'PRODUCT',
    'DEFAULT': 'DEFAULT',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'PRODUCT': 'PRODUCT',
    'DEFAULT': 'DEFAULT',
  };

  @override
  final Iterable<Type> types = const <Type>[UnderwritingResultRulesSourceEnum];
  @override
  final String wireName = 'UnderwritingResultRulesSourceEnum';

  @override
  Object serialize(
          Serializers serializers, UnderwritingResultRulesSourceEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  UnderwritingResultRulesSourceEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      UnderwritingResultRulesSourceEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$UnderwritingResult extends UnderwritingResult {
  @override
  final DateTime evaluatedAt;
  @override
  final BuiltList<UnderwritingFailure> failures;
  @override
  final UnderwritingInputs inputs;
  @override
  final UnderwritingOutcome outcome;
  @override
  final int rulesSchemaVersion;
  @override
  final UnderwritingResultRulesSourceEnum rulesSource;

  factory _$UnderwritingResult(
          [void Function(UnderwritingResultBuilder)? updates]) =>
      (UnderwritingResultBuilder()..update(updates))._build();

  _$UnderwritingResult._(
      {required this.evaluatedAt,
      required this.failures,
      required this.inputs,
      required this.outcome,
      required this.rulesSchemaVersion,
      required this.rulesSource})
      : super._();
  @override
  UnderwritingResult rebuild(
          void Function(UnderwritingResultBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  UnderwritingResultBuilder toBuilder() =>
      UnderwritingResultBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is UnderwritingResult &&
        evaluatedAt == other.evaluatedAt &&
        failures == other.failures &&
        inputs == other.inputs &&
        outcome == other.outcome &&
        rulesSchemaVersion == other.rulesSchemaVersion &&
        rulesSource == other.rulesSource;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, evaluatedAt.hashCode);
    _$hash = $jc(_$hash, failures.hashCode);
    _$hash = $jc(_$hash, inputs.hashCode);
    _$hash = $jc(_$hash, outcome.hashCode);
    _$hash = $jc(_$hash, rulesSchemaVersion.hashCode);
    _$hash = $jc(_$hash, rulesSource.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'UnderwritingResult')
          ..add('evaluatedAt', evaluatedAt)
          ..add('failures', failures)
          ..add('inputs', inputs)
          ..add('outcome', outcome)
          ..add('rulesSchemaVersion', rulesSchemaVersion)
          ..add('rulesSource', rulesSource))
        .toString();
  }
}

class UnderwritingResultBuilder
    implements Builder<UnderwritingResult, UnderwritingResultBuilder> {
  _$UnderwritingResult? _$v;

  DateTime? _evaluatedAt;
  DateTime? get evaluatedAt => _$this._evaluatedAt;
  set evaluatedAt(DateTime? evaluatedAt) => _$this._evaluatedAt = evaluatedAt;

  ListBuilder<UnderwritingFailure>? _failures;
  ListBuilder<UnderwritingFailure> get failures =>
      _$this._failures ??= ListBuilder<UnderwritingFailure>();
  set failures(ListBuilder<UnderwritingFailure>? failures) =>
      _$this._failures = failures;

  UnderwritingInputsBuilder? _inputs;
  UnderwritingInputsBuilder get inputs =>
      _$this._inputs ??= UnderwritingInputsBuilder();
  set inputs(UnderwritingInputsBuilder? inputs) => _$this._inputs = inputs;

  UnderwritingOutcome? _outcome;
  UnderwritingOutcome? get outcome => _$this._outcome;
  set outcome(UnderwritingOutcome? outcome) => _$this._outcome = outcome;

  int? _rulesSchemaVersion;
  int? get rulesSchemaVersion => _$this._rulesSchemaVersion;
  set rulesSchemaVersion(int? rulesSchemaVersion) =>
      _$this._rulesSchemaVersion = rulesSchemaVersion;

  UnderwritingResultRulesSourceEnum? _rulesSource;
  UnderwritingResultRulesSourceEnum? get rulesSource => _$this._rulesSource;
  set rulesSource(UnderwritingResultRulesSourceEnum? rulesSource) =>
      _$this._rulesSource = rulesSource;

  UnderwritingResultBuilder() {
    UnderwritingResult._defaults(this);
  }

  UnderwritingResultBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _evaluatedAt = $v.evaluatedAt;
      _failures = $v.failures.toBuilder();
      _inputs = $v.inputs.toBuilder();
      _outcome = $v.outcome;
      _rulesSchemaVersion = $v.rulesSchemaVersion;
      _rulesSource = $v.rulesSource;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(UnderwritingResult other) {
    _$v = other as _$UnderwritingResult;
  }

  @override
  void update(void Function(UnderwritingResultBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  UnderwritingResult build() => _build();

  _$UnderwritingResult _build() {
    _$UnderwritingResult _$result;
    try {
      _$result = _$v ??
          _$UnderwritingResult._(
            evaluatedAt: BuiltValueNullFieldError.checkNotNull(
                evaluatedAt, r'UnderwritingResult', 'evaluatedAt'),
            failures: failures.build(),
            inputs: inputs.build(),
            outcome: BuiltValueNullFieldError.checkNotNull(
                outcome, r'UnderwritingResult', 'outcome'),
            rulesSchemaVersion: BuiltValueNullFieldError.checkNotNull(
                rulesSchemaVersion,
                r'UnderwritingResult',
                'rulesSchemaVersion'),
            rulesSource: BuiltValueNullFieldError.checkNotNull(
                rulesSource, r'UnderwritingResult', 'rulesSource'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'failures';
        failures.build();
        _$failedField = 'inputs';
        inputs.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'UnderwritingResult', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
