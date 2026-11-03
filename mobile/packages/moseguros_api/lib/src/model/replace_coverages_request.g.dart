// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'replace_coverages_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$ReplaceCoveragesRequest extends ReplaceCoveragesRequest {
  @override
  final BuiltList<CoverageRequest> coverages;
  @override
  final int version;

  factory _$ReplaceCoveragesRequest(
          [void Function(ReplaceCoveragesRequestBuilder)? updates]) =>
      (ReplaceCoveragesRequestBuilder()..update(updates))._build();

  _$ReplaceCoveragesRequest._({required this.coverages, required this.version})
      : super._();
  @override
  ReplaceCoveragesRequest rebuild(
          void Function(ReplaceCoveragesRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  ReplaceCoveragesRequestBuilder toBuilder() =>
      ReplaceCoveragesRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is ReplaceCoveragesRequest &&
        coverages == other.coverages &&
        version == other.version;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, coverages.hashCode);
    _$hash = $jc(_$hash, version.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'ReplaceCoveragesRequest')
          ..add('coverages', coverages)
          ..add('version', version))
        .toString();
  }
}

class ReplaceCoveragesRequestBuilder
    implements
        Builder<ReplaceCoveragesRequest, ReplaceCoveragesRequestBuilder> {
  _$ReplaceCoveragesRequest? _$v;

  ListBuilder<CoverageRequest>? _coverages;
  ListBuilder<CoverageRequest> get coverages =>
      _$this._coverages ??= ListBuilder<CoverageRequest>();
  set coverages(ListBuilder<CoverageRequest>? coverages) =>
      _$this._coverages = coverages;

  int? _version;
  int? get version => _$this._version;
  set version(int? version) => _$this._version = version;

  ReplaceCoveragesRequestBuilder() {
    ReplaceCoveragesRequest._defaults(this);
  }

  ReplaceCoveragesRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _coverages = $v.coverages.toBuilder();
      _version = $v.version;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(ReplaceCoveragesRequest other) {
    _$v = other as _$ReplaceCoveragesRequest;
  }

  @override
  void update(void Function(ReplaceCoveragesRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  ReplaceCoveragesRequest build() => _build();

  _$ReplaceCoveragesRequest _build() {
    _$ReplaceCoveragesRequest _$result;
    try {
      _$result = _$v ??
          _$ReplaceCoveragesRequest._(
            coverages: coverages.build(),
            version: BuiltValueNullFieldError.checkNotNull(
                version, r'ReplaceCoveragesRequest', 'version'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'coverages';
        coverages.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'ReplaceCoveragesRequest', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
