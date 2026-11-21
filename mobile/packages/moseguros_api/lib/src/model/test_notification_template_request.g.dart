// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'test_notification_template_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$TestNotificationTemplateRequest
    extends TestNotificationTemplateRequest {
  @override
  final String destination;

  factory _$TestNotificationTemplateRequest(
          [void Function(TestNotificationTemplateRequestBuilder)? updates]) =>
      (TestNotificationTemplateRequestBuilder()..update(updates))._build();

  _$TestNotificationTemplateRequest._({required this.destination}) : super._();
  @override
  TestNotificationTemplateRequest rebuild(
          void Function(TestNotificationTemplateRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  TestNotificationTemplateRequestBuilder toBuilder() =>
      TestNotificationTemplateRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is TestNotificationTemplateRequest &&
        destination == other.destination;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, destination.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'TestNotificationTemplateRequest')
          ..add('destination', destination))
        .toString();
  }
}

class TestNotificationTemplateRequestBuilder
    implements
        Builder<TestNotificationTemplateRequest,
            TestNotificationTemplateRequestBuilder> {
  _$TestNotificationTemplateRequest? _$v;

  String? _destination;
  String? get destination => _$this._destination;
  set destination(String? destination) => _$this._destination = destination;

  TestNotificationTemplateRequestBuilder() {
    TestNotificationTemplateRequest._defaults(this);
  }

  TestNotificationTemplateRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _destination = $v.destination;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(TestNotificationTemplateRequest other) {
    _$v = other as _$TestNotificationTemplateRequest;
  }

  @override
  void update(void Function(TestNotificationTemplateRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  TestNotificationTemplateRequest build() => _build();

  _$TestNotificationTemplateRequest _build() {
    final _$result = _$v ??
        _$TestNotificationTemplateRequest._(
          destination: BuiltValueNullFieldError.checkNotNull(
              destination, r'TestNotificationTemplateRequest', 'destination'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
