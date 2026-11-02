// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'version_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$VersionResponse extends VersionResponse {
  @override
  final DateTime? builtAt;
  @override
  final String? commit;
  @override
  final String? name;
  @override
  final String? version;

  factory _$VersionResponse([void Function(VersionResponseBuilder)? updates]) =>
      (VersionResponseBuilder()..update(updates))._build();

  _$VersionResponse._({this.builtAt, this.commit, this.name, this.version})
      : super._();
  @override
  VersionResponse rebuild(void Function(VersionResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  VersionResponseBuilder toBuilder() => VersionResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is VersionResponse &&
        builtAt == other.builtAt &&
        commit == other.commit &&
        name == other.name &&
        version == other.version;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, builtAt.hashCode);
    _$hash = $jc(_$hash, commit.hashCode);
    _$hash = $jc(_$hash, name.hashCode);
    _$hash = $jc(_$hash, version.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'VersionResponse')
          ..add('builtAt', builtAt)
          ..add('commit', commit)
          ..add('name', name)
          ..add('version', version))
        .toString();
  }
}

class VersionResponseBuilder
    implements Builder<VersionResponse, VersionResponseBuilder> {
  _$VersionResponse? _$v;

  DateTime? _builtAt;
  DateTime? get builtAt => _$this._builtAt;
  set builtAt(DateTime? builtAt) => _$this._builtAt = builtAt;

  String? _commit;
  String? get commit => _$this._commit;
  set commit(String? commit) => _$this._commit = commit;

  String? _name;
  String? get name => _$this._name;
  set name(String? name) => _$this._name = name;

  String? _version;
  String? get version => _$this._version;
  set version(String? version) => _$this._version = version;

  VersionResponseBuilder() {
    VersionResponse._defaults(this);
  }

  VersionResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _builtAt = $v.builtAt;
      _commit = $v.commit;
      _name = $v.name;
      _version = $v.version;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(VersionResponse other) {
    _$v = other as _$VersionResponse;
  }

  @override
  void update(void Function(VersionResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  VersionResponse build() => _build();

  _$VersionResponse _build() {
    final _$result = _$v ??
        _$VersionResponse._(
          builtAt: builtAt,
          commit: commit,
          name: name,
          version: version,
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
