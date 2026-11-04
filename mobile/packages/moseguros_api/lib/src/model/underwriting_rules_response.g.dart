// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'underwriting_rules_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$UnderwritingRulesResponse extends UnderwritingRulesResponse {
  @override
  final bool isDefault;
  @override
  final JsonObject rules;
  @override
  final int version;

  factory _$UnderwritingRulesResponse(
          [void Function(UnderwritingRulesResponseBuilder)? updates]) =>
      (UnderwritingRulesResponseBuilder()..update(updates))._build();

  _$UnderwritingRulesResponse._(
      {required this.isDefault, required this.rules, required this.version})
      : super._();
  @override
  UnderwritingRulesResponse rebuild(
          void Function(UnderwritingRulesResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  UnderwritingRulesResponseBuilder toBuilder() =>
      UnderwritingRulesResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is UnderwritingRulesResponse &&
        isDefault == other.isDefault &&
        rules == other.rules &&
        version == other.version;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, isDefault.hashCode);
    _$hash = $jc(_$hash, rules.hashCode);
    _$hash = $jc(_$hash, version.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'UnderwritingRulesResponse')
          ..add('isDefault', isDefault)
          ..add('rules', rules)
          ..add('version', version))
        .toString();
  }
}

class UnderwritingRulesResponseBuilder
    implements
        Builder<UnderwritingRulesResponse, UnderwritingRulesResponseBuilder> {
  _$UnderwritingRulesResponse? _$v;

  bool? _isDefault;
  bool? get isDefault => _$this._isDefault;
  set isDefault(bool? isDefault) => _$this._isDefault = isDefault;

  JsonObject? _rules;
  JsonObject? get rules => _$this._rules;
  set rules(JsonObject? rules) => _$this._rules = rules;

  int? _version;
  int? get version => _$this._version;
  set version(int? version) => _$this._version = version;

  UnderwritingRulesResponseBuilder() {
    UnderwritingRulesResponse._defaults(this);
  }

  UnderwritingRulesResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _isDefault = $v.isDefault;
      _rules = $v.rules;
      _version = $v.version;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(UnderwritingRulesResponse other) {
    _$v = other as _$UnderwritingRulesResponse;
  }

  @override
  void update(void Function(UnderwritingRulesResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  UnderwritingRulesResponse build() => _build();

  _$UnderwritingRulesResponse _build() {
    final _$result = _$v ??
        _$UnderwritingRulesResponse._(
          isDefault: BuiltValueNullFieldError.checkNotNull(
              isDefault, r'UnderwritingRulesResponse', 'isDefault'),
          rules: BuiltValueNullFieldError.checkNotNull(
              rules, r'UnderwritingRulesResponse', 'rules'),
          version: BuiltValueNullFieldError.checkNotNull(
              version, r'UnderwritingRulesResponse', 'version'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
