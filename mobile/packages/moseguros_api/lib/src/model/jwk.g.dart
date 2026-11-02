// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'jwk.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$Jwk extends Jwk {
  @override
  final String alg;
  @override
  final String e;
  @override
  final String kid;
  @override
  final String kty;
  @override
  final String n;
  @override
  final String use;

  factory _$Jwk([void Function(JwkBuilder)? updates]) =>
      (JwkBuilder()..update(updates))._build();

  _$Jwk._(
      {required this.alg,
      required this.e,
      required this.kid,
      required this.kty,
      required this.n,
      required this.use})
      : super._();
  @override
  Jwk rebuild(void Function(JwkBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  JwkBuilder toBuilder() => JwkBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is Jwk &&
        alg == other.alg &&
        e == other.e &&
        kid == other.kid &&
        kty == other.kty &&
        n == other.n &&
        use == other.use;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, alg.hashCode);
    _$hash = $jc(_$hash, e.hashCode);
    _$hash = $jc(_$hash, kid.hashCode);
    _$hash = $jc(_$hash, kty.hashCode);
    _$hash = $jc(_$hash, n.hashCode);
    _$hash = $jc(_$hash, use.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'Jwk')
          ..add('alg', alg)
          ..add('e', e)
          ..add('kid', kid)
          ..add('kty', kty)
          ..add('n', n)
          ..add('use', use))
        .toString();
  }
}

class JwkBuilder implements Builder<Jwk, JwkBuilder> {
  _$Jwk? _$v;

  String? _alg;
  String? get alg => _$this._alg;
  set alg(String? alg) => _$this._alg = alg;

  String? _e;
  String? get e => _$this._e;
  set e(String? e) => _$this._e = e;

  String? _kid;
  String? get kid => _$this._kid;
  set kid(String? kid) => _$this._kid = kid;

  String? _kty;
  String? get kty => _$this._kty;
  set kty(String? kty) => _$this._kty = kty;

  String? _n;
  String? get n => _$this._n;
  set n(String? n) => _$this._n = n;

  String? _use;
  String? get use => _$this._use;
  set use(String? use) => _$this._use = use;

  JwkBuilder() {
    Jwk._defaults(this);
  }

  JwkBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _alg = $v.alg;
      _e = $v.e;
      _kid = $v.kid;
      _kty = $v.kty;
      _n = $v.n;
      _use = $v.use;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(Jwk other) {
    _$v = other as _$Jwk;
  }

  @override
  void update(void Function(JwkBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  Jwk build() => _build();

  _$Jwk _build() {
    final _$result = _$v ??
        _$Jwk._(
          alg: BuiltValueNullFieldError.checkNotNull(alg, r'Jwk', 'alg'),
          e: BuiltValueNullFieldError.checkNotNull(e, r'Jwk', 'e'),
          kid: BuiltValueNullFieldError.checkNotNull(kid, r'Jwk', 'kid'),
          kty: BuiltValueNullFieldError.checkNotNull(kty, r'Jwk', 'kty'),
          n: BuiltValueNullFieldError.checkNotNull(n, r'Jwk', 'n'),
          use: BuiltValueNullFieldError.checkNotNull(use, r'Jwk', 'use'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
