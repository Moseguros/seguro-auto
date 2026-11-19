// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'admin_payment_file_sources.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$AdminPaymentFileSources extends AdminPaymentFileSources {
  @override
  final BuiltList<AdminPaymentFileSource> sources;

  factory _$AdminPaymentFileSources(
          [void Function(AdminPaymentFileSourcesBuilder)? updates]) =>
      (AdminPaymentFileSourcesBuilder()..update(updates))._build();

  _$AdminPaymentFileSources._({required this.sources}) : super._();
  @override
  AdminPaymentFileSources rebuild(
          void Function(AdminPaymentFileSourcesBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  AdminPaymentFileSourcesBuilder toBuilder() =>
      AdminPaymentFileSourcesBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is AdminPaymentFileSources && sources == other.sources;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, sources.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'AdminPaymentFileSources')
          ..add('sources', sources))
        .toString();
  }
}

class AdminPaymentFileSourcesBuilder
    implements
        Builder<AdminPaymentFileSources, AdminPaymentFileSourcesBuilder> {
  _$AdminPaymentFileSources? _$v;

  ListBuilder<AdminPaymentFileSource>? _sources;
  ListBuilder<AdminPaymentFileSource> get sources =>
      _$this._sources ??= ListBuilder<AdminPaymentFileSource>();
  set sources(ListBuilder<AdminPaymentFileSource>? sources) =>
      _$this._sources = sources;

  AdminPaymentFileSourcesBuilder() {
    AdminPaymentFileSources._defaults(this);
  }

  AdminPaymentFileSourcesBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _sources = $v.sources.toBuilder();
      _$v = null;
    }
    return this;
  }

  @override
  void replace(AdminPaymentFileSources other) {
    _$v = other as _$AdminPaymentFileSources;
  }

  @override
  void update(void Function(AdminPaymentFileSourcesBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  AdminPaymentFileSources build() => _build();

  _$AdminPaymentFileSources _build() {
    _$AdminPaymentFileSources _$result;
    try {
      _$result = _$v ??
          _$AdminPaymentFileSources._(
            sources: sources.build(),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'sources';
        sources.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'AdminPaymentFileSources', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
