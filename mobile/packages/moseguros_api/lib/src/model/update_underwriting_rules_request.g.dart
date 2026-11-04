// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'update_underwriting_rules_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$UpdateUnderwritingRulesRequest extends UpdateUnderwritingRulesRequest {
  @override
  final JsonObject rules;
  @override
  final int version;

  factory _$UpdateUnderwritingRulesRequest(
          [void Function(UpdateUnderwritingRulesRequestBuilder)? updates]) =>
      (UpdateUnderwritingRulesRequestBuilder()..update(updates))._build();

  _$UpdateUnderwritingRulesRequest._(
      {required this.rules, required this.version})
      : super._();
  @override
  UpdateUnderwritingRulesRequest rebuild(
          void Function(UpdateUnderwritingRulesRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  UpdateUnderwritingRulesRequestBuilder toBuilder() =>
      UpdateUnderwritingRulesRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is UpdateUnderwritingRulesRequest &&
        rules == other.rules &&
        version == other.version;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, rules.hashCode);
    _$hash = $jc(_$hash, version.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'UpdateUnderwritingRulesRequest')
          ..add('rules', rules)
          ..add('version', version))
        .toString();
  }
}

class UpdateUnderwritingRulesRequestBuilder
    implements
        Builder<UpdateUnderwritingRulesRequest,
            UpdateUnderwritingRulesRequestBuilder> {
  _$UpdateUnderwritingRulesRequest? _$v;

  JsonObject? _rules;
  JsonObject? get rules => _$this._rules;
  set rules(JsonObject? rules) => _$this._rules = rules;

  int? _version;
  int? get version => _$this._version;
  set version(int? version) => _$this._version = version;

  UpdateUnderwritingRulesRequestBuilder() {
    UpdateUnderwritingRulesRequest._defaults(this);
  }

  UpdateUnderwritingRulesRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _rules = $v.rules;
      _version = $v.version;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(UpdateUnderwritingRulesRequest other) {
    _$v = other as _$UpdateUnderwritingRulesRequest;
  }

  @override
  void update(void Function(UpdateUnderwritingRulesRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  UpdateUnderwritingRulesRequest build() => _build();

  _$UpdateUnderwritingRulesRequest _build() {
    final _$result = _$v ??
        _$UpdateUnderwritingRulesRequest._(
          rules: BuiltValueNullFieldError.checkNotNull(
              rules, r'UpdateUnderwritingRulesRequest', 'rules'),
          version: BuiltValueNullFieldError.checkNotNull(
              version, r'UpdateUnderwritingRulesRequest', 'version'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
