// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'quote_insurer.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$QuoteInsurer extends QuoteInsurer {
  @override
  final String displayName;
  @override
  final String id;
  @override
  final String? logoFileId;
  @override
  final String shortCode;

  factory _$QuoteInsurer([void Function(QuoteInsurerBuilder)? updates]) =>
      (QuoteInsurerBuilder()..update(updates))._build();

  _$QuoteInsurer._(
      {required this.displayName,
      required this.id,
      this.logoFileId,
      required this.shortCode})
      : super._();
  @override
  QuoteInsurer rebuild(void Function(QuoteInsurerBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  QuoteInsurerBuilder toBuilder() => QuoteInsurerBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is QuoteInsurer &&
        displayName == other.displayName &&
        id == other.id &&
        logoFileId == other.logoFileId &&
        shortCode == other.shortCode;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, displayName.hashCode);
    _$hash = $jc(_$hash, id.hashCode);
    _$hash = $jc(_$hash, logoFileId.hashCode);
    _$hash = $jc(_$hash, shortCode.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'QuoteInsurer')
          ..add('displayName', displayName)
          ..add('id', id)
          ..add('logoFileId', logoFileId)
          ..add('shortCode', shortCode))
        .toString();
  }
}

class QuoteInsurerBuilder
    implements Builder<QuoteInsurer, QuoteInsurerBuilder> {
  _$QuoteInsurer? _$v;

  String? _displayName;
  String? get displayName => _$this._displayName;
  set displayName(String? displayName) => _$this._displayName = displayName;

  String? _id;
  String? get id => _$this._id;
  set id(String? id) => _$this._id = id;

  String? _logoFileId;
  String? get logoFileId => _$this._logoFileId;
  set logoFileId(String? logoFileId) => _$this._logoFileId = logoFileId;

  String? _shortCode;
  String? get shortCode => _$this._shortCode;
  set shortCode(String? shortCode) => _$this._shortCode = shortCode;

  QuoteInsurerBuilder() {
    QuoteInsurer._defaults(this);
  }

  QuoteInsurerBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _displayName = $v.displayName;
      _id = $v.id;
      _logoFileId = $v.logoFileId;
      _shortCode = $v.shortCode;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(QuoteInsurer other) {
    _$v = other as _$QuoteInsurer;
  }

  @override
  void update(void Function(QuoteInsurerBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  QuoteInsurer build() => _build();

  _$QuoteInsurer _build() {
    final _$result = _$v ??
        _$QuoteInsurer._(
          displayName: BuiltValueNullFieldError.checkNotNull(
              displayName, r'QuoteInsurer', 'displayName'),
          id: BuiltValueNullFieldError.checkNotNull(id, r'QuoteInsurer', 'id'),
          logoFileId: logoFileId,
          shortCode: BuiltValueNullFieldError.checkNotNull(
              shortCode, r'QuoteInsurer', 'shortCode'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
