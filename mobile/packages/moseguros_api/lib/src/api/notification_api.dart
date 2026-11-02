//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

import 'dart:async';

import 'package:built_value/json_object.dart';
import 'package:built_value/serializer.dart';
import 'package:dio/dio.dart';

import 'package:moseguros_api/src/api_util.dart';
import 'package:moseguros_api/src/model/delivery_receipt_request.dart';
import 'package:moseguros_api/src/model/problem_detail.dart';

class NotificationApi {

  final Dio _dio;

  final Serializers _serializers;

  const NotificationApi(this._dio, this._serializers);

  /// Recibo de entrega de um fornecedor
  /// Actualiza o estado da mensagem. Segredo errado → 401 UNAUTHORIZED; fornecedor desconhecido → 404 NOT_FOUND; mensagem desconhecida → 204 (ignorado). Uma falha de um código OTP faz o backend tentar o canal seguinte.
  ///
  /// Parameters:
  /// * [provider] - Valor da coluna provider (mock-sms, mock-whatsapp)
  /// * [deliveryReceiptRequest] 
  /// * [xWebhookSecret] - Segredo partilhado com o fornecedor
  /// * [cancelToken] - A [CancelToken] that can be used to cancel the operation
  /// * [headers] - Can be used to add additional headers to the request
  /// * [extras] - Can be used to add flags to the request
  /// * [validateStatus] - A [ValidateStatus] callback that can be used to determine request success based on the HTTP status of the response
  /// * [onSendProgress] - A [ProgressCallback] that can be used to get the send progress
  /// * [onReceiveProgress] - A [ProgressCallback] that can be used to get the receive progress
  ///
  /// Returns a [Future]
  /// Throws [DioException] if API call or serialization fails
  Future<Response<void>> receiveDeliveryReceipt({ 
    required String provider,
    required DeliveryReceiptRequest deliveryReceiptRequest,
    String? xWebhookSecret,
    CancelToken? cancelToken,
    Map<String, dynamic>? headers,
    Map<String, dynamic>? extra,
    ValidateStatus? validateStatus,
    ProgressCallback? onSendProgress,
    ProgressCallback? onReceiveProgress,
  }) async {
    final _path = r'/api/v1/integrations/delivery-receipts/{provider}'.replaceAll('{' r'provider' '}', encodeQueryParameter(_serializers, provider, const FullType(String)).toString());
    final _options = Options(
      method: r'POST',
      headers: <String, dynamic>{
        if (xWebhookSecret != null) r'X-Webhook-Secret': xWebhookSecret,
        ...?headers,
      },
      extra: <String, dynamic>{
        'secure': <Map<String, String>>[],
        ...?extra,
      },
      contentType: 'application/json',
      validateStatus: validateStatus,
    );

    dynamic _bodyData;

    try {
      const _type = FullType(DeliveryReceiptRequest);
      _bodyData = _serializers.serialize(deliveryReceiptRequest, specifiedType: _type);

    } catch(error, stackTrace) {
      throw DioException(
         requestOptions: _options.compose(
          _dio.options,
          _path,
        ),
        type: DioExceptionType.unknown,
        error: error,
        stackTrace: stackTrace,
      );
    }

    final _response = await _dio.request<Object>(
      _path,
      data: _bodyData,
      options: _options,
      cancelToken: cancelToken,
      onSendProgress: onSendProgress,
      onReceiveProgress: onReceiveProgress,
    );

    return _response;
  }

}
