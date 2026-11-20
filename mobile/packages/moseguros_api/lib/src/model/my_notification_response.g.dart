// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'my_notification_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$MyNotificationResponse extends MyNotificationResponse {
  @override
  final String body;
  @override
  final String category;
  @override
  final DateTime createdAt;
  @override
  final String? deepLink;
  @override
  final String id;
  @override
  final DateTime? readAt;
  @override
  final String templateCode;
  @override
  final String title;
  @override
  final String? webPath;

  factory _$MyNotificationResponse(
          [void Function(MyNotificationResponseBuilder)? updates]) =>
      (MyNotificationResponseBuilder()..update(updates))._build();

  _$MyNotificationResponse._(
      {required this.body,
      required this.category,
      required this.createdAt,
      this.deepLink,
      required this.id,
      this.readAt,
      required this.templateCode,
      required this.title,
      this.webPath})
      : super._();
  @override
  MyNotificationResponse rebuild(
          void Function(MyNotificationResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  MyNotificationResponseBuilder toBuilder() =>
      MyNotificationResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is MyNotificationResponse &&
        body == other.body &&
        category == other.category &&
        createdAt == other.createdAt &&
        deepLink == other.deepLink &&
        id == other.id &&
        readAt == other.readAt &&
        templateCode == other.templateCode &&
        title == other.title &&
        webPath == other.webPath;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, body.hashCode);
    _$hash = $jc(_$hash, category.hashCode);
    _$hash = $jc(_$hash, createdAt.hashCode);
    _$hash = $jc(_$hash, deepLink.hashCode);
    _$hash = $jc(_$hash, id.hashCode);
    _$hash = $jc(_$hash, readAt.hashCode);
    _$hash = $jc(_$hash, templateCode.hashCode);
    _$hash = $jc(_$hash, title.hashCode);
    _$hash = $jc(_$hash, webPath.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'MyNotificationResponse')
          ..add('body', body)
          ..add('category', category)
          ..add('createdAt', createdAt)
          ..add('deepLink', deepLink)
          ..add('id', id)
          ..add('readAt', readAt)
          ..add('templateCode', templateCode)
          ..add('title', title)
          ..add('webPath', webPath))
        .toString();
  }
}

class MyNotificationResponseBuilder
    implements Builder<MyNotificationResponse, MyNotificationResponseBuilder> {
  _$MyNotificationResponse? _$v;

  String? _body;
  String? get body => _$this._body;
  set body(String? body) => _$this._body = body;

  String? _category;
  String? get category => _$this._category;
  set category(String? category) => _$this._category = category;

  DateTime? _createdAt;
  DateTime? get createdAt => _$this._createdAt;
  set createdAt(DateTime? createdAt) => _$this._createdAt = createdAt;

  String? _deepLink;
  String? get deepLink => _$this._deepLink;
  set deepLink(String? deepLink) => _$this._deepLink = deepLink;

  String? _id;
  String? get id => _$this._id;
  set id(String? id) => _$this._id = id;

  DateTime? _readAt;
  DateTime? get readAt => _$this._readAt;
  set readAt(DateTime? readAt) => _$this._readAt = readAt;

  String? _templateCode;
  String? get templateCode => _$this._templateCode;
  set templateCode(String? templateCode) => _$this._templateCode = templateCode;

  String? _title;
  String? get title => _$this._title;
  set title(String? title) => _$this._title = title;

  String? _webPath;
  String? get webPath => _$this._webPath;
  set webPath(String? webPath) => _$this._webPath = webPath;

  MyNotificationResponseBuilder() {
    MyNotificationResponse._defaults(this);
  }

  MyNotificationResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _body = $v.body;
      _category = $v.category;
      _createdAt = $v.createdAt;
      _deepLink = $v.deepLink;
      _id = $v.id;
      _readAt = $v.readAt;
      _templateCode = $v.templateCode;
      _title = $v.title;
      _webPath = $v.webPath;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(MyNotificationResponse other) {
    _$v = other as _$MyNotificationResponse;
  }

  @override
  void update(void Function(MyNotificationResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  MyNotificationResponse build() => _build();

  _$MyNotificationResponse _build() {
    final _$result = _$v ??
        _$MyNotificationResponse._(
          body: BuiltValueNullFieldError.checkNotNull(
              body, r'MyNotificationResponse', 'body'),
          category: BuiltValueNullFieldError.checkNotNull(
              category, r'MyNotificationResponse', 'category'),
          createdAt: BuiltValueNullFieldError.checkNotNull(
              createdAt, r'MyNotificationResponse', 'createdAt'),
          deepLink: deepLink,
          id: BuiltValueNullFieldError.checkNotNull(
              id, r'MyNotificationResponse', 'id'),
          readAt: readAt,
          templateCode: BuiltValueNullFieldError.checkNotNull(
              templateCode, r'MyNotificationResponse', 'templateCode'),
          title: BuiltValueNullFieldError.checkNotNull(
              title, r'MyNotificationResponse', 'title'),
          webPath: webPath,
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
