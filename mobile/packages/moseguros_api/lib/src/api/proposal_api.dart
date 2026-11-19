//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

import 'dart:async';

import 'package:built_value/json_object.dart';
import 'package:built_value/serializer.dart';
import 'package:dio/dio.dart';

import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/api_util.dart';
import 'package:moseguros_api/src/model/accept_proposal_request.dart';
import 'package:moseguros_api/src/model/acknowledge_suitability_request.dart';
import 'package:moseguros_api/src/model/approve_inspection_request.dart';
import 'package:moseguros_api/src/model/confirm_signature_request.dart';
import 'package:moseguros_api/src/model/create_broker_proposal_request.dart';
import 'package:moseguros_api/src/model/create_proposal_payment_intent_request.dart';
import 'package:moseguros_api/src/model/create_proposal_request.dart';
import 'package:moseguros_api/src/model/date.dart';
import 'package:moseguros_api/src/model/inspection_mode.dart';
import 'package:moseguros_api/src/model/inspection_photo_response.dart';
import 'package:moseguros_api/src/model/inspection_response.dart';
import 'package:moseguros_api/src/model/inspection_status.dart';
import 'package:moseguros_api/src/model/insurer_proposal_response.dart';
import 'package:moseguros_api/src/model/intent_view.dart';
import 'package:moseguros_api/src/model/page_response_broker_proposal_summary_response.dart';
import 'package:moseguros_api/src/model/page_response_insurer_inspection_summary_response.dart';
import 'package:moseguros_api/src/model/page_response_insurer_proposal_summary_response.dart';
import 'package:moseguros_api/src/model/page_response_proposal_summary_response.dart';
import 'package:moseguros_api/src/model/payment_link_response.dart';
import 'package:moseguros_api/src/model/photo_slot.dart';
import 'package:moseguros_api/src/model/photo_source.dart';
import 'package:moseguros_api/src/model/problem_detail.dart';
import 'package:moseguros_api/src/model/proposal_channel.dart';
import 'package:moseguros_api/src/model/proposal_payment_methods_response.dart';
import 'package:moseguros_api/src/model/proposal_response.dart';
import 'package:moseguros_api/src/model/proposal_status.dart';
import 'package:moseguros_api/src/model/reject_inspection_request.dart';
import 'package:moseguros_api/src/model/reject_proposal_request.dart';
import 'package:moseguros_api/src/model/request_on_site_inspection_request.dart';
import 'package:moseguros_api/src/model/request_proposal_info_request.dart';
import 'package:moseguros_api/src/model/request_signature_request.dart';
import 'package:moseguros_api/src/model/save_declarations_request.dart';
import 'package:moseguros_api/src/model/save_suitability_request.dart';
import 'package:moseguros_api/src/model/signature_request_response.dart';
import 'package:moseguros_api/src/model/sla_status.dart';
import 'package:moseguros_api/src/model/submit_inspection_request.dart';
import 'package:moseguros_api/src/model/submit_proposal_request.dart';
import 'package:moseguros_api/src/model/update_broker_proposal_request.dart';
import 'package:moseguros_api/src/model/update_proposal_request.dart';

class ProposalApi {

  final Dio _dio;

  final Serializers _serializers;

  const ProposalApi(this._dio, this._serializers);

  /// Aceita uma proposta
  /// Permissão proposal:decide. UNDER_REVIEW → ACCEPTED (acceptanceMode &#x3D; MANUAL), com o prazo para pagar (expiresAt). Com vistoria, tem de estar APPROVED (409 INSPECTION_NOT_APPROVED); KYC VERIFIED ou total abaixo do limiar do KYC simplificado (409 KYC_NOT_VERIFIED). note até 1000 (TOO_LONG). Só em UNDER_REVIEW (senão 409 PROPOSAL_NOT_UNDER_REVIEW); version diferente → 409 CONCURRENT_MODIFICATION; o cliente é avisado por e-mail (se o tiver verificado).
  ///
  /// Parameters:
  /// * [id] 
  /// * [acceptProposalRequest] 
  /// * [cancelToken] - A [CancelToken] that can be used to cancel the operation
  /// * [headers] - Can be used to add additional headers to the request
  /// * [extras] - Can be used to add flags to the request
  /// * [validateStatus] - A [ValidateStatus] callback that can be used to determine request success based on the HTTP status of the response
  /// * [onSendProgress] - A [ProgressCallback] that can be used to get the send progress
  /// * [onReceiveProgress] - A [ProgressCallback] that can be used to get the receive progress
  ///
  /// Returns a [Future] containing a [Response] with a [InsurerProposalResponse] as data
  /// Throws [DioException] if API call or serialization fails
  Future<Response<InsurerProposalResponse>> acceptProposal({ 
    required String id,
    required AcceptProposalRequest acceptProposalRequest,
    CancelToken? cancelToken,
    Map<String, dynamic>? headers,
    Map<String, dynamic>? extra,
    ValidateStatus? validateStatus,
    ProgressCallback? onSendProgress,
    ProgressCallback? onReceiveProgress,
  }) async {
    final _path = r'/api/v1/insurer/proposals/{id}/accept'.replaceAll('{' r'id' '}', encodeQueryParameter(_serializers, id, const FullType(String)).toString());
    final _options = Options(
      method: r'POST',
      headers: <String, dynamic>{
        ...?headers,
      },
      extra: <String, dynamic>{
        'secure': <Map<String, String>>[
          {
            'type': 'http',
            'scheme': 'bearer',
            'name': 'bearerAuth',
          },
        ],
        ...?extra,
      },
      contentType: 'application/json',
      validateStatus: validateStatus,
    );

    dynamic _bodyData;

    try {
      const _type = FullType(AcceptProposalRequest);
      _bodyData = _serializers.serialize(acceptProposalRequest, specifiedType: _type);

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

    InsurerProposalResponse? _responseData;

    try {
      final rawResponse = _response.data;
      _responseData = rawResponse == null ? null : _serializers.deserialize(
        rawResponse,
        specifiedType: const FullType(InsurerProposalResponse),
      ) as InsurerProposalResponse;

    } catch (error, stackTrace) {
      throw DioException(
        requestOptions: _response.requestOptions,
        response: _response,
        type: DioExceptionType.unknown,
        error: error,
        stackTrace: stackTrace,
      );
    }

    return Response<InsurerProposalResponse>(
      data: _responseData,
      headers: _response.headers,
      isRedirect: _response.isRedirect,
      requestOptions: _response.requestOptions,
      redirects: _response.redirects,
      statusCode: _response.statusCode,
      statusMessage: _response.statusMessage,
      extra: _response.extra,
    );
  }

  /// Confirma os avisos de adequação
  /// Permissão self:proposals ou proposal:create-for-client. O cliente leu os avisos e quer continuar: preenche suitability.acknowledgedAt. Sem WARNED → 409 SUITABILITY_NOT_WARNED. Só em DRAFT ou INFO_REQUESTED (senão 409 PROPOSAL_NOT_EDITABLE); version diferente → 409 CONCURRENT_MODIFICATION; proposta de outro cliente → 404 PROPOSAL_NOT_FOUND. Aumenta contentRevision e renova expiresAt (+15 dias).
  ///
  /// Parameters:
  /// * [id] 
  /// * [acknowledgeSuitabilityRequest] 
  /// * [cancelToken] - A [CancelToken] that can be used to cancel the operation
  /// * [headers] - Can be used to add additional headers to the request
  /// * [extras] - Can be used to add flags to the request
  /// * [validateStatus] - A [ValidateStatus] callback that can be used to determine request success based on the HTTP status of the response
  /// * [onSendProgress] - A [ProgressCallback] that can be used to get the send progress
  /// * [onReceiveProgress] - A [ProgressCallback] that can be used to get the receive progress
  ///
  /// Returns a [Future] containing a [Response] with a [ProposalResponse] as data
  /// Throws [DioException] if API call or serialization fails
  Future<Response<ProposalResponse>> acknowledgeProposalSuitability({ 
    required String id,
    required AcknowledgeSuitabilityRequest acknowledgeSuitabilityRequest,
    CancelToken? cancelToken,
    Map<String, dynamic>? headers,
    Map<String, dynamic>? extra,
    ValidateStatus? validateStatus,
    ProgressCallback? onSendProgress,
    ProgressCallback? onReceiveProgress,
  }) async {
    final _path = r'/api/v1/proposals/{id}/suitability/acknowledge'.replaceAll('{' r'id' '}', encodeQueryParameter(_serializers, id, const FullType(String)).toString());
    final _options = Options(
      method: r'POST',
      headers: <String, dynamic>{
        ...?headers,
      },
      extra: <String, dynamic>{
        'secure': <Map<String, String>>[
          {
            'type': 'http',
            'scheme': 'bearer',
            'name': 'bearerAuth',
          },
        ],
        ...?extra,
      },
      contentType: 'application/json',
      validateStatus: validateStatus,
    );

    dynamic _bodyData;

    try {
      const _type = FullType(AcknowledgeSuitabilityRequest);
      _bodyData = _serializers.serialize(acknowledgeSuitabilityRequest, specifiedType: _type);

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

    ProposalResponse? _responseData;

    try {
      final rawResponse = _response.data;
      _responseData = rawResponse == null ? null : _serializers.deserialize(
        rawResponse,
        specifiedType: const FullType(ProposalResponse),
      ) as ProposalResponse;

    } catch (error, stackTrace) {
      throw DioException(
        requestOptions: _response.requestOptions,
        response: _response,
        type: DioExceptionType.unknown,
        error: error,
        stackTrace: stackTrace,
      );
    }

    return Response<ProposalResponse>(
      data: _responseData,
      headers: _response.headers,
      isRedirect: _response.isRedirect,
      requestOptions: _response.requestOptions,
      redirects: _response.redirects,
      statusCode: _response.statusCode,
      statusMessage: _response.statusMessage,
      extra: _response.extra,
    );
  }

  /// Aprova uma vistoria
  /// Permissão inspection:review. SUBMITTED → APPROVED. mode &#x3D; ON_SITE regista uma vistoria presencial e exige notes (REQUIRED); SELF_PHOTOS (ou o modo da vistoria, sem mode) exige os seis ângulos (409 INSPECTION_PHOTOS_MISSING); notes até 1000 (TOO_LONG). Proposta fora de UNDER_REVIEW → 409 PROPOSAL_NOT_UNDER_REVIEW; version da vistoria diferente → 409 CONCURRENT_MODIFICATION; vistoria fora de SUBMITTED → 409 INVALID_STATE_TRANSITION.
  ///
  /// Parameters:
  /// * [id] 
  /// * [approveInspectionRequest] 
  /// * [cancelToken] - A [CancelToken] that can be used to cancel the operation
  /// * [headers] - Can be used to add additional headers to the request
  /// * [extras] - Can be used to add flags to the request
  /// * [validateStatus] - A [ValidateStatus] callback that can be used to determine request success based on the HTTP status of the response
  /// * [onSendProgress] - A [ProgressCallback] that can be used to get the send progress
  /// * [onReceiveProgress] - A [ProgressCallback] that can be used to get the receive progress
  ///
  /// Returns a [Future] containing a [Response] with a [InspectionResponse] as data
  /// Throws [DioException] if API call or serialization fails
  Future<Response<InspectionResponse>> approveInspection({ 
    required String id,
    required ApproveInspectionRequest approveInspectionRequest,
    CancelToken? cancelToken,
    Map<String, dynamic>? headers,
    Map<String, dynamic>? extra,
    ValidateStatus? validateStatus,
    ProgressCallback? onSendProgress,
    ProgressCallback? onReceiveProgress,
  }) async {
    final _path = r'/api/v1/insurer/inspections/{id}/approve'.replaceAll('{' r'id' '}', encodeQueryParameter(_serializers, id, const FullType(String)).toString());
    final _options = Options(
      method: r'POST',
      headers: <String, dynamic>{
        ...?headers,
      },
      extra: <String, dynamic>{
        'secure': <Map<String, String>>[
          {
            'type': 'http',
            'scheme': 'bearer',
            'name': 'bearerAuth',
          },
        ],
        ...?extra,
      },
      contentType: 'application/json',
      validateStatus: validateStatus,
    );

    dynamic _bodyData;

    try {
      const _type = FullType(ApproveInspectionRequest);
      _bodyData = _serializers.serialize(approveInspectionRequest, specifiedType: _type);

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

    InspectionResponse? _responseData;

    try {
      final rawResponse = _response.data;
      _responseData = rawResponse == null ? null : _serializers.deserialize(
        rawResponse,
        specifiedType: const FullType(InspectionResponse),
      ) as InspectionResponse;

    } catch (error, stackTrace) {
      throw DioException(
        requestOptions: _response.requestOptions,
        response: _response,
        type: DioExceptionType.unknown,
        error: error,
        stackTrace: stackTrace,
      );
    }

    return Response<InspectionResponse>(
      data: _responseData,
      headers: _response.headers,
      isRedirect: _response.isRedirect,
      requestOptions: _response.requestOptions,
      redirects: _response.redirects,
      statusCode: _response.statusCode,
      statusMessage: _response.statusMessage,
      extra: _response.extra,
    );
  }

  /// Confirma a assinatura com o código recebido
  /// Permissão self:proposals ou proposal:create-for-client. Proposta em DRAFT ou INFO_REQUESTED (senão 409 PROPOSAL_NOT_EDITABLE); sem pedido em curso ou com outro challengeId → 409 SIGN_REQUEST_NOT_FOUND; proposta alterada depois do pedido → 409 PROPOSAL_CHANGED_SINCE_SIGN_REQUEST (pedir de novo); erros do código como no login (OTP_INVALID com attemptsRemaining, OTP_EXPIRED, OTP_SUPERSEDED, OTP_ALREADY_USED, OTP_ATTEMPTS_EXCEEDED). Grava a assinatura ligada ao hash do PDF (bloco signature, checklist.signature &#x3D; DONE) e renova expiresAt. Qualquer alteração posterior da proposta invalida a assinatura. Confirmada por um utilizador do mediador (S4-06) → signature.assisted &#x3D; true; pelo próprio cliente → não assistida.
  ///
  /// Parameters:
  /// * [id] 
  /// * [confirmSignatureRequest] 
  /// * [cancelToken] - A [CancelToken] that can be used to cancel the operation
  /// * [headers] - Can be used to add additional headers to the request
  /// * [extras] - Can be used to add flags to the request
  /// * [validateStatus] - A [ValidateStatus] callback that can be used to determine request success based on the HTTP status of the response
  /// * [onSendProgress] - A [ProgressCallback] that can be used to get the send progress
  /// * [onReceiveProgress] - A [ProgressCallback] that can be used to get the receive progress
  ///
  /// Returns a [Future] containing a [Response] with a [ProposalResponse] as data
  /// Throws [DioException] if API call or serialization fails
  Future<Response<ProposalResponse>> confirmProposalSignature({ 
    required String id,
    required ConfirmSignatureRequest confirmSignatureRequest,
    CancelToken? cancelToken,
    Map<String, dynamic>? headers,
    Map<String, dynamic>? extra,
    ValidateStatus? validateStatus,
    ProgressCallback? onSendProgress,
    ProgressCallback? onReceiveProgress,
  }) async {
    final _path = r'/api/v1/proposals/{id}/sign/confirm'.replaceAll('{' r'id' '}', encodeQueryParameter(_serializers, id, const FullType(String)).toString());
    final _options = Options(
      method: r'POST',
      headers: <String, dynamic>{
        ...?headers,
      },
      extra: <String, dynamic>{
        'secure': <Map<String, String>>[
          {
            'type': 'http',
            'scheme': 'bearer',
            'name': 'bearerAuth',
          },
        ],
        ...?extra,
      },
      contentType: 'application/json',
      validateStatus: validateStatus,
    );

    dynamic _bodyData;

    try {
      const _type = FullType(ConfirmSignatureRequest);
      _bodyData = _serializers.serialize(confirmSignatureRequest, specifiedType: _type);

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

    ProposalResponse? _responseData;

    try {
      final rawResponse = _response.data;
      _responseData = rawResponse == null ? null : _serializers.deserialize(
        rawResponse,
        specifiedType: const FullType(ProposalResponse),
      ) as ProposalResponse;

    } catch (error, stackTrace) {
      throw DioException(
        requestOptions: _response.requestOptions,
        response: _response,
        type: DioExceptionType.unknown,
        error: error,
        stackTrace: stackTrace,
      );
    }

    return Response<ProposalResponse>(
      data: _responseData,
      headers: _response.headers,
      isRedirect: _response.isRedirect,
      requestOptions: _response.requestOptions,
      redirects: _response.redirects,
      statusCode: _response.statusCode,
      statusMessage: _response.statusMessage,
      extra: _response.extra,
    );
  }

  /// Cria uma proposta a partir de uma cotação
  /// Permissão self:proposals. Proposta DRAFT numerada (PR-AAAA-NNNNNN) com os dados da cotação congelados e o tomador do perfil; a cotação passa a USED. Cotação não visível ou de mediador → 404 QUOTE_NOT_FOUND; já usada numa proposta → 409 PROPOSAL_ALREADY_EXISTS (proposalId); QUOTE_NOT_AVAILABLE, QUOTE_ALREADY_USED, QUOTE_EXPIRED (409); seguradora não activa → 409 INSURER_NOT_ACTIVE; perfil incompleto → 409 PROFILE_INCOMPLETE; veículo ou condutor mudados desde a cotação → 409 QUOTE_DATA_CHANGED (fields); removidos → 404 VEHICLE_NOT_FOUND / DRIVER_NOT_FOUND.
  ///
  /// Parameters:
  /// * [createProposalRequest] 
  /// * [cancelToken] - A [CancelToken] that can be used to cancel the operation
  /// * [headers] - Can be used to add additional headers to the request
  /// * [extras] - Can be used to add flags to the request
  /// * [validateStatus] - A [ValidateStatus] callback that can be used to determine request success based on the HTTP status of the response
  /// * [onSendProgress] - A [ProgressCallback] that can be used to get the send progress
  /// * [onReceiveProgress] - A [ProgressCallback] that can be used to get the receive progress
  ///
  /// Returns a [Future] containing a [Response] with a [ProposalResponse] as data
  /// Throws [DioException] if API call or serialization fails
  Future<Response<ProposalResponse>> createProposal({ 
    required CreateProposalRequest createProposalRequest,
    CancelToken? cancelToken,
    Map<String, dynamic>? headers,
    Map<String, dynamic>? extra,
    ValidateStatus? validateStatus,
    ProgressCallback? onSendProgress,
    ProgressCallback? onReceiveProgress,
  }) async {
    final _path = r'/api/v1/proposals';
    final _options = Options(
      method: r'POST',
      headers: <String, dynamic>{
        ...?headers,
      },
      extra: <String, dynamic>{
        'secure': <Map<String, String>>[
          {
            'type': 'http',
            'scheme': 'bearer',
            'name': 'bearerAuth',
          },
        ],
        ...?extra,
      },
      contentType: 'application/json',
      validateStatus: validateStatus,
    );

    dynamic _bodyData;

    try {
      const _type = FullType(CreateProposalRequest);
      _bodyData = _serializers.serialize(createProposalRequest, specifiedType: _type);

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

    ProposalResponse? _responseData;

    try {
      final rawResponse = _response.data;
      _responseData = rawResponse == null ? null : _serializers.deserialize(
        rawResponse,
        specifiedType: const FullType(ProposalResponse),
      ) as ProposalResponse;

    } catch (error, stackTrace) {
      throw DioException(
        requestOptions: _response.requestOptions,
        response: _response,
        type: DioExceptionType.unknown,
        error: error,
        stackTrace: stackTrace,
      );
    }

    return Response<ProposalResponse>(
      data: _responseData,
      headers: _response.headers,
      isRedirect: _response.isRedirect,
      requestOptions: _response.requestOptions,
      redirects: _response.redirects,
      statusCode: _response.statusCode,
      statusMessage: _response.statusMessage,
      extra: _response.extra,
    );
  }

  /// Cria uma proposta a partir da cotação do mediador
  /// Permissão proposal:create-for-client (corretor ou agente). Por esta ordem: cotação não visível, do cliente ou de outra organização → 404 QUOTE_NOT_FOUND; já usada numa proposta → 409 PROPOSAL_ALREADY_EXISTS; QUOTE_NOT_AVAILABLE, QUOTE_ALREADY_USED, QUOTE_EXPIRED (409); seguradora não activa → 409 INSURER_NOT_ACTIVE; vehicle ou driver em falta → 400 REQUIRED; campos usados na tarifa diferentes dos da cotação → 409 QUOTE_DATA_CHANGED (fields, ex.: vehicle.year); conta do cliente desactivada → 409 CLIENT_ACCOUNT_DISABLED; perfil do cliente completo e policyholder enviado → 400 NOT_ALLOWED (policyholder); perfil incompleto sem policyholder → 400 REQUIRED (policyholder), com as regras do perfil (errors[].field policyholder.*); regras do veículo e do condutor do S2-03 (errors[].field vehicle.* e driver.*; VEHICLE_ALREADY_REGISTERED com a mesma matrícula e outro chassis, VEHICLE_LIMIT_REACHED, DRIVER_LIMIT_REACHED). Regista o veículo e o condutor na conta do cliente (reutiliza os iguais), grava a proposta DRAFT com channel &#x3D; BROKER e a cotação passa a USED. Qualquer erro desfaz tudo. O cliente vê a proposta em listMyProposals e recebe um e-mail se tiver e-mail verificado.
  ///
  /// Parameters:
  /// * [createBrokerProposalRequest] 
  /// * [cancelToken] - A [CancelToken] that can be used to cancel the operation
  /// * [headers] - Can be used to add additional headers to the request
  /// * [extras] - Can be used to add flags to the request
  /// * [validateStatus] - A [ValidateStatus] callback that can be used to determine request success based on the HTTP status of the response
  /// * [onSendProgress] - A [ProgressCallback] that can be used to get the send progress
  /// * [onReceiveProgress] - A [ProgressCallback] that can be used to get the receive progress
  ///
  /// Returns a [Future] containing a [Response] with a [ProposalResponse] as data
  /// Throws [DioException] if API call or serialization fails
  Future<Response<ProposalResponse>> createProposalForClient({ 
    required CreateBrokerProposalRequest createBrokerProposalRequest,
    CancelToken? cancelToken,
    Map<String, dynamic>? headers,
    Map<String, dynamic>? extra,
    ValidateStatus? validateStatus,
    ProgressCallback? onSendProgress,
    ProgressCallback? onReceiveProgress,
  }) async {
    final _path = r'/api/v1/broker/proposals';
    final _options = Options(
      method: r'POST',
      headers: <String, dynamic>{
        ...?headers,
      },
      extra: <String, dynamic>{
        'secure': <Map<String, String>>[
          {
            'type': 'http',
            'scheme': 'bearer',
            'name': 'bearerAuth',
          },
        ],
        ...?extra,
      },
      contentType: 'application/json',
      validateStatus: validateStatus,
    );

    dynamic _bodyData;

    try {
      const _type = FullType(CreateBrokerProposalRequest);
      _bodyData = _serializers.serialize(createBrokerProposalRequest, specifiedType: _type);

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

    ProposalResponse? _responseData;

    try {
      final rawResponse = _response.data;
      _responseData = rawResponse == null ? null : _serializers.deserialize(
        rawResponse,
        specifiedType: const FullType(ProposalResponse),
      ) as ProposalResponse;

    } catch (error, stackTrace) {
      throw DioException(
        requestOptions: _response.requestOptions,
        response: _response,
        type: DioExceptionType.unknown,
        error: error,
        stackTrace: stackTrace,
      );
    }

    return Response<ProposalResponse>(
      data: _responseData,
      headers: _response.headers,
      isRedirect: _response.isRedirect,
      requestOptions: _response.requestOptions,
      redirects: _response.redirects,
      statusCode: _response.statusCode,
      statusMessage: _response.statusMessage,
      extra: _response.extra,
    );
  }

  /// Paga uma proposta aceite
  /// Permissão self:payments. Cabeçalho Idempotency-Key obrigatório (UUID; a mesma chave devolve a mesma resposta durante 24 h; outro pedido com a mesma chave → 422 IDEMPOTENCY_KEY_REUSED). MPESA exige payerMsisdn a começar por 84 ou 85 (REQUIRED, MSISDN_NOT_MPESA); BANK_REFERENCE devolve já a entidade e a referência (nextAction PAY_BY_REFERENCE) e reutiliza a referência activa com o mesmo montante. Erros: 404 PROPOSAL_NOT_FOUND; 409 PAYMENT_TARGET_NOT_PAYABLE, PAYMENT_TARGET_ALREADY_PAID, PAYMENT_METHOD_NOT_AVAILABLE, PAYMENT_IN_PROGRESS (intentId).
  ///
  /// Parameters:
  /// * [id] 
  /// * [idempotencyKey] 
  /// * [createProposalPaymentIntentRequest] 
  /// * [cancelToken] - A [CancelToken] that can be used to cancel the operation
  /// * [headers] - Can be used to add additional headers to the request
  /// * [extras] - Can be used to add flags to the request
  /// * [validateStatus] - A [ValidateStatus] callback that can be used to determine request success based on the HTTP status of the response
  /// * [onSendProgress] - A [ProgressCallback] that can be used to get the send progress
  /// * [onReceiveProgress] - A [ProgressCallback] that can be used to get the receive progress
  ///
  /// Returns a [Future] containing a [Response] with a [IntentView] as data
  /// Throws [DioException] if API call or serialization fails
  Future<Response<IntentView>> createProposalPaymentIntent({ 
    required String id,
    required String idempotencyKey,
    required CreateProposalPaymentIntentRequest createProposalPaymentIntentRequest,
    CancelToken? cancelToken,
    Map<String, dynamic>? headers,
    Map<String, dynamic>? extra,
    ValidateStatus? validateStatus,
    ProgressCallback? onSendProgress,
    ProgressCallback? onReceiveProgress,
  }) async {
    final _path = r'/api/v1/proposals/{id}/payment-intents'.replaceAll('{' r'id' '}', encodeQueryParameter(_serializers, id, const FullType(String)).toString());
    final _options = Options(
      method: r'POST',
      headers: <String, dynamic>{
        r'Idempotency-Key': idempotencyKey,
        ...?headers,
      },
      extra: <String, dynamic>{
        'secure': <Map<String, String>>[
          {
            'type': 'http',
            'scheme': 'bearer',
            'name': 'bearerAuth',
          },
        ],
        ...?extra,
      },
      contentType: 'application/json',
      validateStatus: validateStatus,
    );

    dynamic _bodyData;

    try {
      const _type = FullType(CreateProposalPaymentIntentRequest);
      _bodyData = _serializers.serialize(createProposalPaymentIntentRequest, specifiedType: _type);

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

    IntentView? _responseData;

    try {
      final rawResponse = _response.data;
      _responseData = rawResponse == null ? null : _serializers.deserialize(
        rawResponse,
        specifiedType: const FullType(IntentView),
      ) as IntentView;

    } catch (error, stackTrace) {
      throw DioException(
        requestOptions: _response.requestOptions,
        response: _response,
        type: DioExceptionType.unknown,
        error: error,
        stackTrace: stackTrace,
      );
    }

    return Response<IntentView>(
      data: _responseData,
      headers: _response.headers,
      isRedirect: _response.isRedirect,
      requestOptions: _response.requestOptions,
      redirects: _response.redirects,
      statusCode: _response.statusCode,
      statusMessage: _response.statusMessage,
      extra: _response.extra,
    );
  }

  /// Consulta uma vistoria
  /// Permissão inspection:review. As fotografias activas (fileId para GET /api/v1/files/{id}) e a version para aprovar ou recusar.
  ///
  /// Parameters:
  /// * [id] 
  /// * [cancelToken] - A [CancelToken] that can be used to cancel the operation
  /// * [headers] - Can be used to add additional headers to the request
  /// * [extras] - Can be used to add flags to the request
  /// * [validateStatus] - A [ValidateStatus] callback that can be used to determine request success based on the HTTP status of the response
  /// * [onSendProgress] - A [ProgressCallback] that can be used to get the send progress
  /// * [onReceiveProgress] - A [ProgressCallback] that can be used to get the receive progress
  ///
  /// Returns a [Future] containing a [Response] with a [InspectionResponse] as data
  /// Throws [DioException] if API call or serialization fails
  Future<Response<InspectionResponse>> getInsurerInspection({ 
    required String id,
    CancelToken? cancelToken,
    Map<String, dynamic>? headers,
    Map<String, dynamic>? extra,
    ValidateStatus? validateStatus,
    ProgressCallback? onSendProgress,
    ProgressCallback? onReceiveProgress,
  }) async {
    final _path = r'/api/v1/insurer/inspections/{id}'.replaceAll('{' r'id' '}', encodeQueryParameter(_serializers, id, const FullType(String)).toString());
    final _options = Options(
      method: r'GET',
      headers: <String, dynamic>{
        ...?headers,
      },
      extra: <String, dynamic>{
        'secure': <Map<String, String>>[
          {
            'type': 'http',
            'scheme': 'bearer',
            'name': 'bearerAuth',
          },
        ],
        ...?extra,
      },
      validateStatus: validateStatus,
    );

    final _response = await _dio.request<Object>(
      _path,
      options: _options,
      cancelToken: cancelToken,
      onSendProgress: onSendProgress,
      onReceiveProgress: onReceiveProgress,
    );

    InspectionResponse? _responseData;

    try {
      final rawResponse = _response.data;
      _responseData = rawResponse == null ? null : _serializers.deserialize(
        rawResponse,
        specifiedType: const FullType(InspectionResponse),
      ) as InspectionResponse;

    } catch (error, stackTrace) {
      throw DioException(
        requestOptions: _response.requestOptions,
        response: _response,
        type: DioExceptionType.unknown,
        error: error,
        stackTrace: stackTrace,
      );
    }

    return Response<InspectionResponse>(
      data: _responseData,
      headers: _response.headers,
      isRedirect: _response.isRedirect,
      requestOptions: _response.requestOptions,
      redirects: _response.redirects,
      statusCode: _response.statusCode,
      statusMessage: _response.statusMessage,
      extra: _response.extra,
    );
  }

  /// Consulta uma proposta submetida
  /// Permissão proposal:read. A proposta com o condutor e o veículo completos, o KYC, o registo das regras de aceitação (underwriting), o SLA e o histórico das assinaturas.
  ///
  /// Parameters:
  /// * [id] 
  /// * [cancelToken] - A [CancelToken] that can be used to cancel the operation
  /// * [headers] - Can be used to add additional headers to the request
  /// * [extras] - Can be used to add flags to the request
  /// * [validateStatus] - A [ValidateStatus] callback that can be used to determine request success based on the HTTP status of the response
  /// * [onSendProgress] - A [ProgressCallback] that can be used to get the send progress
  /// * [onReceiveProgress] - A [ProgressCallback] that can be used to get the receive progress
  ///
  /// Returns a [Future] containing a [Response] with a [InsurerProposalResponse] as data
  /// Throws [DioException] if API call or serialization fails
  Future<Response<InsurerProposalResponse>> getInsurerProposal({ 
    required String id,
    CancelToken? cancelToken,
    Map<String, dynamic>? headers,
    Map<String, dynamic>? extra,
    ValidateStatus? validateStatus,
    ProgressCallback? onSendProgress,
    ProgressCallback? onReceiveProgress,
  }) async {
    final _path = r'/api/v1/insurer/proposals/{id}'.replaceAll('{' r'id' '}', encodeQueryParameter(_serializers, id, const FullType(String)).toString());
    final _options = Options(
      method: r'GET',
      headers: <String, dynamic>{
        ...?headers,
      },
      extra: <String, dynamic>{
        'secure': <Map<String, String>>[
          {
            'type': 'http',
            'scheme': 'bearer',
            'name': 'bearerAuth',
          },
        ],
        ...?extra,
      },
      validateStatus: validateStatus,
    );

    final _response = await _dio.request<Object>(
      _path,
      options: _options,
      cancelToken: cancelToken,
      onSendProgress: onSendProgress,
      onReceiveProgress: onReceiveProgress,
    );

    InsurerProposalResponse? _responseData;

    try {
      final rawResponse = _response.data;
      _responseData = rawResponse == null ? null : _serializers.deserialize(
        rawResponse,
        specifiedType: const FullType(InsurerProposalResponse),
      ) as InsurerProposalResponse;

    } catch (error, stackTrace) {
      throw DioException(
        requestOptions: _response.requestOptions,
        response: _response,
        type: DioExceptionType.unknown,
        error: error,
        stackTrace: stackTrace,
      );
    }

    return Response<InsurerProposalResponse>(
      data: _responseData,
      headers: _response.headers,
      isRedirect: _response.isRedirect,
      requestOptions: _response.requestOptions,
      redirects: _response.redirects,
      statusCode: _response.statusCode,
      statusMessage: _response.statusMessage,
      extra: _response.extra,
    );
  }

  /// Consulta uma proposta
  /// Permissão self:proposals (o cliente dono) ou proposal:create-for-client (o mediador da proposta, S4-06). Outra → 404 PROPOSAL_NOT_FOUND. A seguradora usa getInsurerProposal (S4-05).
  ///
  /// Parameters:
  /// * [id] 
  /// * [cancelToken] - A [CancelToken] that can be used to cancel the operation
  /// * [headers] - Can be used to add additional headers to the request
  /// * [extras] - Can be used to add flags to the request
  /// * [validateStatus] - A [ValidateStatus] callback that can be used to determine request success based on the HTTP status of the response
  /// * [onSendProgress] - A [ProgressCallback] that can be used to get the send progress
  /// * [onReceiveProgress] - A [ProgressCallback] that can be used to get the receive progress
  ///
  /// Returns a [Future] containing a [Response] with a [ProposalResponse] as data
  /// Throws [DioException] if API call or serialization fails
  Future<Response<ProposalResponse>> getProposal({ 
    required String id,
    CancelToken? cancelToken,
    Map<String, dynamic>? headers,
    Map<String, dynamic>? extra,
    ValidateStatus? validateStatus,
    ProgressCallback? onSendProgress,
    ProgressCallback? onReceiveProgress,
  }) async {
    final _path = r'/api/v1/proposals/{id}'.replaceAll('{' r'id' '}', encodeQueryParameter(_serializers, id, const FullType(String)).toString());
    final _options = Options(
      method: r'GET',
      headers: <String, dynamic>{
        ...?headers,
      },
      extra: <String, dynamic>{
        'secure': <Map<String, String>>[
          {
            'type': 'http',
            'scheme': 'bearer',
            'name': 'bearerAuth',
          },
        ],
        ...?extra,
      },
      validateStatus: validateStatus,
    );

    final _response = await _dio.request<Object>(
      _path,
      options: _options,
      cancelToken: cancelToken,
      onSendProgress: onSendProgress,
      onReceiveProgress: onReceiveProgress,
    );

    ProposalResponse? _responseData;

    try {
      final rawResponse = _response.data;
      _responseData = rawResponse == null ? null : _serializers.deserialize(
        rawResponse,
        specifiedType: const FullType(ProposalResponse),
      ) as ProposalResponse;

    } catch (error, stackTrace) {
      throw DioException(
        requestOptions: _response.requestOptions,
        response: _response,
        type: DioExceptionType.unknown,
        error: error,
        stackTrace: stackTrace,
      );
    }

    return Response<ProposalResponse>(
      data: _responseData,
      headers: _response.headers,
      isRedirect: _response.isRedirect,
      requestOptions: _response.requestOptions,
      redirects: _response.redirects,
      statusCode: _response.statusCode,
      statusMessage: _response.statusMessage,
      extra: _response.extra,
    );
  }

  /// Lista as propostas da organização do mediador
  /// Permissão proposal:create-for-client (corretor ou agente). Propostas criadas por qualquer utilizador da organização; status repetível (vazio &#x3D; todos); q &#x3D; número exacto ou parte do nome do tomador. Ordenação por createdAt (defeito createdAt,desc).
  ///
  /// Parameters:
  /// * [status] - Estados a incluir (repetível); vazio = todos
  /// * [q] - Número exacto ou parte do nome do tomador
  /// * [page] 
  /// * [size] 
  /// * [sort] - createdAt, com ,asc ou ,desc
  /// * [cancelToken] - A [CancelToken] that can be used to cancel the operation
  /// * [headers] - Can be used to add additional headers to the request
  /// * [extras] - Can be used to add flags to the request
  /// * [validateStatus] - A [ValidateStatus] callback that can be used to determine request success based on the HTTP status of the response
  /// * [onSendProgress] - A [ProgressCallback] that can be used to get the send progress
  /// * [onReceiveProgress] - A [ProgressCallback] that can be used to get the receive progress
  ///
  /// Returns a [Future] containing a [Response] with a [PageResponseBrokerProposalSummaryResponse] as data
  /// Throws [DioException] if API call or serialization fails
  Future<Response<PageResponseBrokerProposalSummaryResponse>> listBrokerProposals({ 
    BuiltList<ProposalStatus>? status,
    String? q,
    int? page,
    int? size,
    String? sort,
    CancelToken? cancelToken,
    Map<String, dynamic>? headers,
    Map<String, dynamic>? extra,
    ValidateStatus? validateStatus,
    ProgressCallback? onSendProgress,
    ProgressCallback? onReceiveProgress,
  }) async {
    final _path = r'/api/v1/broker/proposals';
    final _options = Options(
      method: r'GET',
      headers: <String, dynamic>{
        ...?headers,
      },
      extra: <String, dynamic>{
        'secure': <Map<String, String>>[
          {
            'type': 'http',
            'scheme': 'bearer',
            'name': 'bearerAuth',
          },
        ],
        ...?extra,
      },
      validateStatus: validateStatus,
    );

    final _queryParameters = <String, dynamic>{
      if (status != null) r'status': encodeCollectionQueryParameter<ProposalStatus>(_serializers, status, const FullType(BuiltList, [FullType(ProposalStatus)]), format: ListFormat.multi,),
      if (q != null) r'q': encodeQueryParameter(_serializers, q, const FullType(String)),
      if (page != null) r'page': encodeQueryParameter(_serializers, page, const FullType(int)),
      if (size != null) r'size': encodeQueryParameter(_serializers, size, const FullType(int)),
      if (sort != null) r'sort': encodeQueryParameter(_serializers, sort, const FullType(String)),
    };

    final _response = await _dio.request<Object>(
      _path,
      options: _options,
      queryParameters: _queryParameters,
      cancelToken: cancelToken,
      onSendProgress: onSendProgress,
      onReceiveProgress: onReceiveProgress,
    );

    PageResponseBrokerProposalSummaryResponse? _responseData;

    try {
      final rawResponse = _response.data;
      _responseData = rawResponse == null ? null : _serializers.deserialize(
        rawResponse,
        specifiedType: const FullType(PageResponseBrokerProposalSummaryResponse),
      ) as PageResponseBrokerProposalSummaryResponse;

    } catch (error, stackTrace) {
      throw DioException(
        requestOptions: _response.requestOptions,
        response: _response,
        type: DioExceptionType.unknown,
        error: error,
        stackTrace: stackTrace,
      );
    }

    return Response<PageResponseBrokerProposalSummaryResponse>(
      data: _responseData,
      headers: _response.headers,
      isRedirect: _response.isRedirect,
      requestOptions: _response.requestOptions,
      redirects: _response.redirects,
      statusCode: _response.statusCode,
      statusMessage: _response.statusMessage,
      extra: _response.extra,
    );
  }

  /// Lista as vistorias da seguradora
  /// Permissão inspection:review. Só vistorias de propostas já submetidas. status por defeito SUBMITTED; mode opcional. Ordenação por submittedAt (defeito, asc) ou createdAt.
  ///
  /// Parameters:
  /// * [status] 
  /// * [mode] 
  /// * [page] 
  /// * [size] 
  /// * [sort] - submittedAt ou createdAt, com ,asc ou ,desc
  /// * [cancelToken] - A [CancelToken] that can be used to cancel the operation
  /// * [headers] - Can be used to add additional headers to the request
  /// * [extras] - Can be used to add flags to the request
  /// * [validateStatus] - A [ValidateStatus] callback that can be used to determine request success based on the HTTP status of the response
  /// * [onSendProgress] - A [ProgressCallback] that can be used to get the send progress
  /// * [onReceiveProgress] - A [ProgressCallback] that can be used to get the receive progress
  ///
  /// Returns a [Future] containing a [Response] with a [PageResponseInsurerInspectionSummaryResponse] as data
  /// Throws [DioException] if API call or serialization fails
  Future<Response<PageResponseInsurerInspectionSummaryResponse>> listInsurerInspections({ 
    InspectionStatus? status,
    InspectionMode? mode,
    int? page,
    int? size,
    String? sort,
    CancelToken? cancelToken,
    Map<String, dynamic>? headers,
    Map<String, dynamic>? extra,
    ValidateStatus? validateStatus,
    ProgressCallback? onSendProgress,
    ProgressCallback? onReceiveProgress,
  }) async {
    final _path = r'/api/v1/insurer/inspections';
    final _options = Options(
      method: r'GET',
      headers: <String, dynamic>{
        ...?headers,
      },
      extra: <String, dynamic>{
        'secure': <Map<String, String>>[
          {
            'type': 'http',
            'scheme': 'bearer',
            'name': 'bearerAuth',
          },
        ],
        ...?extra,
      },
      validateStatus: validateStatus,
    );

    final _queryParameters = <String, dynamic>{
      if (status != null) r'status': encodeQueryParameter(_serializers, status, const FullType(InspectionStatus)),
      if (mode != null) r'mode': encodeQueryParameter(_serializers, mode, const FullType(InspectionMode)),
      if (page != null) r'page': encodeQueryParameter(_serializers, page, const FullType(int)),
      if (size != null) r'size': encodeQueryParameter(_serializers, size, const FullType(int)),
      if (sort != null) r'sort': encodeQueryParameter(_serializers, sort, const FullType(String)),
    };

    final _response = await _dio.request<Object>(
      _path,
      options: _options,
      queryParameters: _queryParameters,
      cancelToken: cancelToken,
      onSendProgress: onSendProgress,
      onReceiveProgress: onReceiveProgress,
    );

    PageResponseInsurerInspectionSummaryResponse? _responseData;

    try {
      final rawResponse = _response.data;
      _responseData = rawResponse == null ? null : _serializers.deserialize(
        rawResponse,
        specifiedType: const FullType(PageResponseInsurerInspectionSummaryResponse),
      ) as PageResponseInsurerInspectionSummaryResponse;

    } catch (error, stackTrace) {
      throw DioException(
        requestOptions: _response.requestOptions,
        response: _response,
        type: DioExceptionType.unknown,
        error: error,
        stackTrace: stackTrace,
      );
    }

    return Response<PageResponseInsurerInspectionSummaryResponse>(
      data: _responseData,
      headers: _response.headers,
      isRedirect: _response.isRedirect,
      requestOptions: _response.requestOptions,
      redirects: _response.redirects,
      statusCode: _response.statusCode,
      statusMessage: _response.statusMessage,
      extra: _response.extra,
    );
  }

  /// Lista as propostas submetidas à seguradora
  /// Permissão proposal:read. status repetível (por defeito UNDER_REVIEW); slaStatus (ON_TIME, ALERT, OVERDUE; só propostas UNDER_REVIEW); productType; channel; q &#x3D; número exacto ou parte do nome do tomador; submittedFrom e submittedTo em datas de Africa/Maputo (inclusivas). Ordenação por submittedAt (defeito, asc) ou slaDueAt.
  ///
  /// Parameters:
  /// * [status] 
  /// * [slaStatus] 
  /// * [productType] 
  /// * [channel] 
  /// * [q] 
  /// * [submittedFrom] 
  /// * [submittedTo] 
  /// * [page] 
  /// * [size] 
  /// * [sort] - submittedAt ou slaDueAt, com ,asc ou ,desc
  /// * [cancelToken] - A [CancelToken] that can be used to cancel the operation
  /// * [headers] - Can be used to add additional headers to the request
  /// * [extras] - Can be used to add flags to the request
  /// * [validateStatus] - A [ValidateStatus] callback that can be used to determine request success based on the HTTP status of the response
  /// * [onSendProgress] - A [ProgressCallback] that can be used to get the send progress
  /// * [onReceiveProgress] - A [ProgressCallback] that can be used to get the receive progress
  ///
  /// Returns a [Future] containing a [Response] with a [PageResponseInsurerProposalSummaryResponse] as data
  /// Throws [DioException] if API call or serialization fails
  Future<Response<PageResponseInsurerProposalSummaryResponse>> listInsurerProposals({ 
    BuiltList<ProposalStatus>? status,
    SlaStatus? slaStatus,
    String? productType,
    ProposalChannel? channel,
    String? q,
    Date? submittedFrom,
    Date? submittedTo,
    int? page,
    int? size,
    String? sort,
    CancelToken? cancelToken,
    Map<String, dynamic>? headers,
    Map<String, dynamic>? extra,
    ValidateStatus? validateStatus,
    ProgressCallback? onSendProgress,
    ProgressCallback? onReceiveProgress,
  }) async {
    final _path = r'/api/v1/insurer/proposals';
    final _options = Options(
      method: r'GET',
      headers: <String, dynamic>{
        ...?headers,
      },
      extra: <String, dynamic>{
        'secure': <Map<String, String>>[
          {
            'type': 'http',
            'scheme': 'bearer',
            'name': 'bearerAuth',
          },
        ],
        ...?extra,
      },
      validateStatus: validateStatus,
    );

    final _queryParameters = <String, dynamic>{
      if (status != null) r'status': encodeCollectionQueryParameter<ProposalStatus>(_serializers, status, const FullType(BuiltList, [FullType(ProposalStatus)]), format: ListFormat.multi,),
      if (slaStatus != null) r'slaStatus': encodeQueryParameter(_serializers, slaStatus, const FullType(SlaStatus)),
      if (productType != null) r'productType': encodeQueryParameter(_serializers, productType, const FullType(String)),
      if (channel != null) r'channel': encodeQueryParameter(_serializers, channel, const FullType(ProposalChannel)),
      if (q != null) r'q': encodeQueryParameter(_serializers, q, const FullType(String)),
      if (submittedFrom != null) r'submittedFrom': encodeQueryParameter(_serializers, submittedFrom, const FullType(Date)),
      if (submittedTo != null) r'submittedTo': encodeQueryParameter(_serializers, submittedTo, const FullType(Date)),
      if (page != null) r'page': encodeQueryParameter(_serializers, page, const FullType(int)),
      if (size != null) r'size': encodeQueryParameter(_serializers, size, const FullType(int)),
      if (sort != null) r'sort': encodeQueryParameter(_serializers, sort, const FullType(String)),
    };

    final _response = await _dio.request<Object>(
      _path,
      options: _options,
      queryParameters: _queryParameters,
      cancelToken: cancelToken,
      onSendProgress: onSendProgress,
      onReceiveProgress: onReceiveProgress,
    );

    PageResponseInsurerProposalSummaryResponse? _responseData;

    try {
      final rawResponse = _response.data;
      _responseData = rawResponse == null ? null : _serializers.deserialize(
        rawResponse,
        specifiedType: const FullType(PageResponseInsurerProposalSummaryResponse),
      ) as PageResponseInsurerProposalSummaryResponse;

    } catch (error, stackTrace) {
      throw DioException(
        requestOptions: _response.requestOptions,
        response: _response,
        type: DioExceptionType.unknown,
        error: error,
        stackTrace: stackTrace,
      );
    }

    return Response<PageResponseInsurerProposalSummaryResponse>(
      data: _responseData,
      headers: _response.headers,
      isRedirect: _response.isRedirect,
      requestOptions: _response.requestOptions,
      redirects: _response.redirects,
      statusCode: _response.statusCode,
      statusMessage: _response.statusMessage,
      extra: _response.extra,
    );
  }

  /// Lista as propostas do cliente
  /// Permissão self:proposals. Inclui as que um mediador criou em nome do cliente (S4-06). Filtro status repetível; ordenação por createdAt (defeito createdAt,desc).
  ///
  /// Parameters:
  /// * [status] - Estados a incluir (repetível); vazio = todos
  /// * [page] 
  /// * [size] 
  /// * [sort] - createdAt, com ,asc ou ,desc
  /// * [cancelToken] - A [CancelToken] that can be used to cancel the operation
  /// * [headers] - Can be used to add additional headers to the request
  /// * [extras] - Can be used to add flags to the request
  /// * [validateStatus] - A [ValidateStatus] callback that can be used to determine request success based on the HTTP status of the response
  /// * [onSendProgress] - A [ProgressCallback] that can be used to get the send progress
  /// * [onReceiveProgress] - A [ProgressCallback] that can be used to get the receive progress
  ///
  /// Returns a [Future] containing a [Response] with a [PageResponseProposalSummaryResponse] as data
  /// Throws [DioException] if API call or serialization fails
  Future<Response<PageResponseProposalSummaryResponse>> listMyProposals({ 
    BuiltList<ProposalStatus>? status,
    int? page,
    int? size,
    String? sort,
    CancelToken? cancelToken,
    Map<String, dynamic>? headers,
    Map<String, dynamic>? extra,
    ValidateStatus? validateStatus,
    ProgressCallback? onSendProgress,
    ProgressCallback? onReceiveProgress,
  }) async {
    final _path = r'/api/v1/me/proposals';
    final _options = Options(
      method: r'GET',
      headers: <String, dynamic>{
        ...?headers,
      },
      extra: <String, dynamic>{
        'secure': <Map<String, String>>[
          {
            'type': 'http',
            'scheme': 'bearer',
            'name': 'bearerAuth',
          },
        ],
        ...?extra,
      },
      validateStatus: validateStatus,
    );

    final _queryParameters = <String, dynamic>{
      if (status != null) r'status': encodeCollectionQueryParameter<ProposalStatus>(_serializers, status, const FullType(BuiltList, [FullType(ProposalStatus)]), format: ListFormat.multi,),
      if (page != null) r'page': encodeQueryParameter(_serializers, page, const FullType(int)),
      if (size != null) r'size': encodeQueryParameter(_serializers, size, const FullType(int)),
      if (sort != null) r'sort': encodeQueryParameter(_serializers, sort, const FullType(String)),
    };

    final _response = await _dio.request<Object>(
      _path,
      options: _options,
      queryParameters: _queryParameters,
      cancelToken: cancelToken,
      onSendProgress: onSendProgress,
      onReceiveProgress: onReceiveProgress,
    );

    PageResponseProposalSummaryResponse? _responseData;

    try {
      final rawResponse = _response.data;
      _responseData = rawResponse == null ? null : _serializers.deserialize(
        rawResponse,
        specifiedType: const FullType(PageResponseProposalSummaryResponse),
      ) as PageResponseProposalSummaryResponse;

    } catch (error, stackTrace) {
      throw DioException(
        requestOptions: _response.requestOptions,
        response: _response,
        type: DioExceptionType.unknown,
        error: error,
        stackTrace: stackTrace,
      );
    }

    return Response<PageResponseProposalSummaryResponse>(
      data: _responseData,
      headers: _response.headers,
      isRedirect: _response.isRedirect,
      requestOptions: _response.requestOptions,
      redirects: _response.redirects,
      statusCode: _response.statusCode,
      statusMessage: _response.statusMessage,
      extra: _response.extra,
    );
  }

  /// Formas de pagamento de uma proposta aceite
  /// Permissão self:payments. As formas de pagamento activas da seguradora, dentro dos limites do montante, por sortOrder, e a intenção em curso (activeIntent) para retomar. Proposta paga → paid &#x3D; true sem métodos; fora de ACCEPTED → 409 PAYMENT_TARGET_NOT_PAYABLE; de outro cliente → 404 PROPOSAL_NOT_FOUND.
  ///
  /// Parameters:
  /// * [id] 
  /// * [cancelToken] - A [CancelToken] that can be used to cancel the operation
  /// * [headers] - Can be used to add additional headers to the request
  /// * [extras] - Can be used to add flags to the request
  /// * [validateStatus] - A [ValidateStatus] callback that can be used to determine request success based on the HTTP status of the response
  /// * [onSendProgress] - A [ProgressCallback] that can be used to get the send progress
  /// * [onReceiveProgress] - A [ProgressCallback] that can be used to get the receive progress
  ///
  /// Returns a [Future] containing a [Response] with a [ProposalPaymentMethodsResponse] as data
  /// Throws [DioException] if API call or serialization fails
  Future<Response<ProposalPaymentMethodsResponse>> listProposalPaymentMethods({ 
    required String id,
    CancelToken? cancelToken,
    Map<String, dynamic>? headers,
    Map<String, dynamic>? extra,
    ValidateStatus? validateStatus,
    ProgressCallback? onSendProgress,
    ProgressCallback? onReceiveProgress,
  }) async {
    final _path = r'/api/v1/proposals/{id}/payment-methods'.replaceAll('{' r'id' '}', encodeQueryParameter(_serializers, id, const FullType(String)).toString());
    final _options = Options(
      method: r'GET',
      headers: <String, dynamic>{
        ...?headers,
      },
      extra: <String, dynamic>{
        'secure': <Map<String, String>>[
          {
            'type': 'http',
            'scheme': 'bearer',
            'name': 'bearerAuth',
          },
        ],
        ...?extra,
      },
      validateStatus: validateStatus,
    );

    final _response = await _dio.request<Object>(
      _path,
      options: _options,
      cancelToken: cancelToken,
      onSendProgress: onSendProgress,
      onReceiveProgress: onReceiveProgress,
    );

    ProposalPaymentMethodsResponse? _responseData;

    try {
      final rawResponse = _response.data;
      _responseData = rawResponse == null ? null : _serializers.deserialize(
        rawResponse,
        specifiedType: const FullType(ProposalPaymentMethodsResponse),
      ) as ProposalPaymentMethodsResponse;

    } catch (error, stackTrace) {
      throw DioException(
        requestOptions: _response.requestOptions,
        response: _response,
        type: DioExceptionType.unknown,
        error: error,
        stackTrace: stackTrace,
      );
    }

    return Response<ProposalPaymentMethodsResponse>(
      data: _responseData,
      headers: _response.headers,
      isRedirect: _response.isRedirect,
      requestOptions: _response.requestOptions,
      redirects: _response.redirects,
      statusCode: _response.statusCode,
      statusMessage: _response.statusMessage,
      extra: _response.extra,
    );
  }

  /// Recusa uma vistoria
  /// Permissão inspection:review. SUBMITTED → REJECTED com reason 10–1000 (REQUIRED, TOO_SHORT, TOO_LONG) e os ângulos a repetir (INVALID_SLOT, DUPLICATED). A proposta continua UNDER_REVIEW: para o cliente refazer, pede-se informação (S4-05) ou recusa-se a proposta. Proposta fora de UNDER_REVIEW → 409 PROPOSAL_NOT_UNDER_REVIEW; version da vistoria diferente → 409 CONCURRENT_MODIFICATION; vistoria fora de SUBMITTED → 409 INVALID_STATE_TRANSITION.
  ///
  /// Parameters:
  /// * [id] 
  /// * [rejectInspectionRequest] 
  /// * [cancelToken] - A [CancelToken] that can be used to cancel the operation
  /// * [headers] - Can be used to add additional headers to the request
  /// * [extras] - Can be used to add flags to the request
  /// * [validateStatus] - A [ValidateStatus] callback that can be used to determine request success based on the HTTP status of the response
  /// * [onSendProgress] - A [ProgressCallback] that can be used to get the send progress
  /// * [onReceiveProgress] - A [ProgressCallback] that can be used to get the receive progress
  ///
  /// Returns a [Future] containing a [Response] with a [InspectionResponse] as data
  /// Throws [DioException] if API call or serialization fails
  Future<Response<InspectionResponse>> rejectInspection({ 
    required String id,
    required RejectInspectionRequest rejectInspectionRequest,
    CancelToken? cancelToken,
    Map<String, dynamic>? headers,
    Map<String, dynamic>? extra,
    ValidateStatus? validateStatus,
    ProgressCallback? onSendProgress,
    ProgressCallback? onReceiveProgress,
  }) async {
    final _path = r'/api/v1/insurer/inspections/{id}/reject'.replaceAll('{' r'id' '}', encodeQueryParameter(_serializers, id, const FullType(String)).toString());
    final _options = Options(
      method: r'POST',
      headers: <String, dynamic>{
        ...?headers,
      },
      extra: <String, dynamic>{
        'secure': <Map<String, String>>[
          {
            'type': 'http',
            'scheme': 'bearer',
            'name': 'bearerAuth',
          },
        ],
        ...?extra,
      },
      contentType: 'application/json',
      validateStatus: validateStatus,
    );

    dynamic _bodyData;

    try {
      const _type = FullType(RejectInspectionRequest);
      _bodyData = _serializers.serialize(rejectInspectionRequest, specifiedType: _type);

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

    InspectionResponse? _responseData;

    try {
      final rawResponse = _response.data;
      _responseData = rawResponse == null ? null : _serializers.deserialize(
        rawResponse,
        specifiedType: const FullType(InspectionResponse),
      ) as InspectionResponse;

    } catch (error, stackTrace) {
      throw DioException(
        requestOptions: _response.requestOptions,
        response: _response,
        type: DioExceptionType.unknown,
        error: error,
        stackTrace: stackTrace,
      );
    }

    return Response<InspectionResponse>(
      data: _responseData,
      headers: _response.headers,
      isRedirect: _response.isRedirect,
      requestOptions: _response.requestOptions,
      redirects: _response.redirects,
      statusCode: _response.statusCode,
      statusMessage: _response.statusMessage,
      extra: _response.extra,
    );
  }

  /// Recusa uma proposta
  /// Permissão proposal:decide. UNDER_REVIEW → REJECTED. reasonCode obrigatório (REQUIRED, INVALID_VALUE); reason 10–1000 (REQUIRED, TOO_SHORT, TOO_LONG); o motivo vai no e-mail ao cliente. Só em UNDER_REVIEW (senão 409 PROPOSAL_NOT_UNDER_REVIEW); version diferente → 409 CONCURRENT_MODIFICATION; o cliente é avisado por e-mail (se o tiver verificado).
  ///
  /// Parameters:
  /// * [id] 
  /// * [rejectProposalRequest] 
  /// * [cancelToken] - A [CancelToken] that can be used to cancel the operation
  /// * [headers] - Can be used to add additional headers to the request
  /// * [extras] - Can be used to add flags to the request
  /// * [validateStatus] - A [ValidateStatus] callback that can be used to determine request success based on the HTTP status of the response
  /// * [onSendProgress] - A [ProgressCallback] that can be used to get the send progress
  /// * [onReceiveProgress] - A [ProgressCallback] that can be used to get the receive progress
  ///
  /// Returns a [Future] containing a [Response] with a [InsurerProposalResponse] as data
  /// Throws [DioException] if API call or serialization fails
  Future<Response<InsurerProposalResponse>> rejectProposal({ 
    required String id,
    required RejectProposalRequest rejectProposalRequest,
    CancelToken? cancelToken,
    Map<String, dynamic>? headers,
    Map<String, dynamic>? extra,
    ValidateStatus? validateStatus,
    ProgressCallback? onSendProgress,
    ProgressCallback? onReceiveProgress,
  }) async {
    final _path = r'/api/v1/insurer/proposals/{id}/reject'.replaceAll('{' r'id' '}', encodeQueryParameter(_serializers, id, const FullType(String)).toString());
    final _options = Options(
      method: r'POST',
      headers: <String, dynamic>{
        ...?headers,
      },
      extra: <String, dynamic>{
        'secure': <Map<String, String>>[
          {
            'type': 'http',
            'scheme': 'bearer',
            'name': 'bearerAuth',
          },
        ],
        ...?extra,
      },
      contentType: 'application/json',
      validateStatus: validateStatus,
    );

    dynamic _bodyData;

    try {
      const _type = FullType(RejectProposalRequest);
      _bodyData = _serializers.serialize(rejectProposalRequest, specifiedType: _type);

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

    InsurerProposalResponse? _responseData;

    try {
      final rawResponse = _response.data;
      _responseData = rawResponse == null ? null : _serializers.deserialize(
        rawResponse,
        specifiedType: const FullType(InsurerProposalResponse),
      ) as InsurerProposalResponse;

    } catch (error, stackTrace) {
      throw DioException(
        requestOptions: _response.requestOptions,
        response: _response,
        type: DioExceptionType.unknown,
        error: error,
        stackTrace: stackTrace,
      );
    }

    return Response<InsurerProposalResponse>(
      data: _responseData,
      headers: _response.headers,
      isRedirect: _response.isRedirect,
      requestOptions: _response.requestOptions,
      redirects: _response.redirects,
      statusCode: _response.statusCode,
      statusMessage: _response.statusMessage,
      extra: _response.extra,
    );
  }

  /// Retira uma fotografia da vistoria
  /// Permissão self:proposals ou proposal:create-for-client. Fica guardada com removedAt. Inexistente ou já retirada → 404 PHOTO_NOT_FOUND. Proposta de outro cliente → 404 PROPOSAL_NOT_FOUND; sem coberturas OD_* → 409 INSPECTION_NOT_REQUIRED; proposta fora de DRAFT/INFO_REQUESTED → 409 PROPOSAL_NOT_EDITABLE; vistoria fora de PENDING/REJECTED → 409 INSPECTION_NOT_EDITABLE. Aumenta contentRevision e renova expiresAt.
  ///
  /// Parameters:
  /// * [id] 
  /// * [photoId] 
  /// * [cancelToken] - A [CancelToken] that can be used to cancel the operation
  /// * [headers] - Can be used to add additional headers to the request
  /// * [extras] - Can be used to add flags to the request
  /// * [validateStatus] - A [ValidateStatus] callback that can be used to determine request success based on the HTTP status of the response
  /// * [onSendProgress] - A [ProgressCallback] that can be used to get the send progress
  /// * [onReceiveProgress] - A [ProgressCallback] that can be used to get the receive progress
  ///
  /// Returns a [Future]
  /// Throws [DioException] if API call or serialization fails
  Future<Response<void>> removeInspectionPhoto({ 
    required String id,
    required String photoId,
    CancelToken? cancelToken,
    Map<String, dynamic>? headers,
    Map<String, dynamic>? extra,
    ValidateStatus? validateStatus,
    ProgressCallback? onSendProgress,
    ProgressCallback? onReceiveProgress,
  }) async {
    final _path = r'/api/v1/proposals/{id}/inspection/photos/{photoId}'.replaceAll('{' r'id' '}', encodeQueryParameter(_serializers, id, const FullType(String)).toString()).replaceAll('{' r'photoId' '}', encodeQueryParameter(_serializers, photoId, const FullType(String)).toString());
    final _options = Options(
      method: r'DELETE',
      headers: <String, dynamic>{
        ...?headers,
      },
      extra: <String, dynamic>{
        'secure': <Map<String, String>>[
          {
            'type': 'http',
            'scheme': 'bearer',
            'name': 'bearerAuth',
          },
        ],
        ...?extra,
      },
      validateStatus: validateStatus,
    );

    final _response = await _dio.request<Object>(
      _path,
      options: _options,
      cancelToken: cancelToken,
      onSendProgress: onSendProgress,
      onReceiveProgress: onReceiveProgress,
    );

    return _response;
  }

  /// Pede uma vistoria presencial
  /// Permissão self:proposals ou proposal:create-for-client. PENDING ou REJECTED → SUBMITTED com mode &#x3D; ON_SITE; notes 10–500 (REQUIRED, TOO_SHORT, TOO_LONG); as fotografias já enviadas ficam. A seguradora contacta o cliente para marcar a visita; version da proposta diferente → 409 CONCURRENT_MODIFICATION. Proposta de outro cliente → 404 PROPOSAL_NOT_FOUND; sem coberturas OD_* → 409 INSPECTION_NOT_REQUIRED; proposta fora de DRAFT/INFO_REQUESTED → 409 PROPOSAL_NOT_EDITABLE; vistoria fora de PENDING/REJECTED → 409 INSPECTION_NOT_EDITABLE. Aumenta contentRevision e renova expiresAt.
  ///
  /// Parameters:
  /// * [id] 
  /// * [requestOnSiteInspectionRequest] 
  /// * [cancelToken] - A [CancelToken] that can be used to cancel the operation
  /// * [headers] - Can be used to add additional headers to the request
  /// * [extras] - Can be used to add flags to the request
  /// * [validateStatus] - A [ValidateStatus] callback that can be used to determine request success based on the HTTP status of the response
  /// * [onSendProgress] - A [ProgressCallback] that can be used to get the send progress
  /// * [onReceiveProgress] - A [ProgressCallback] that can be used to get the receive progress
  ///
  /// Returns a [Future] containing a [Response] with a [ProposalResponse] as data
  /// Throws [DioException] if API call or serialization fails
  Future<Response<ProposalResponse>> requestOnSiteInspection({ 
    required String id,
    required RequestOnSiteInspectionRequest requestOnSiteInspectionRequest,
    CancelToken? cancelToken,
    Map<String, dynamic>? headers,
    Map<String, dynamic>? extra,
    ValidateStatus? validateStatus,
    ProgressCallback? onSendProgress,
    ProgressCallback? onReceiveProgress,
  }) async {
    final _path = r'/api/v1/proposals/{id}/inspection/request-on-site'.replaceAll('{' r'id' '}', encodeQueryParameter(_serializers, id, const FullType(String)).toString());
    final _options = Options(
      method: r'POST',
      headers: <String, dynamic>{
        ...?headers,
      },
      extra: <String, dynamic>{
        'secure': <Map<String, String>>[
          {
            'type': 'http',
            'scheme': 'bearer',
            'name': 'bearerAuth',
          },
        ],
        ...?extra,
      },
      contentType: 'application/json',
      validateStatus: validateStatus,
    );

    dynamic _bodyData;

    try {
      const _type = FullType(RequestOnSiteInspectionRequest);
      _bodyData = _serializers.serialize(requestOnSiteInspectionRequest, specifiedType: _type);

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

    ProposalResponse? _responseData;

    try {
      final rawResponse = _response.data;
      _responseData = rawResponse == null ? null : _serializers.deserialize(
        rawResponse,
        specifiedType: const FullType(ProposalResponse),
      ) as ProposalResponse;

    } catch (error, stackTrace) {
      throw DioException(
        requestOptions: _response.requestOptions,
        response: _response,
        type: DioExceptionType.unknown,
        error: error,
        stackTrace: stackTrace,
      );
    }

    return Response<ProposalResponse>(
      data: _responseData,
      headers: _response.headers,
      isRedirect: _response.isRedirect,
      requestOptions: _response.requestOptions,
      redirects: _response.redirects,
      statusCode: _response.statusCode,
      statusMessage: _response.statusMessage,
      extra: _response.extra,
    );
  }

  /// Pede informação ao cliente
  /// Permissão proposal:decide. UNDER_REVIEW → INFO_REQUESTED, com o prazo do cliente (expiresAt). message 10–1000 (REQUIRED, TOO_SHORT, TOO_LONG); items 1+ sem repetidos (REQUIRED, INVALID_VALUE, ANSWER_DUPLICATED). A assinatura deixa de valer (o cliente assina de novo); com o item INSPECTION a vistoria volta a PENDING. Só em UNDER_REVIEW (senão 409 PROPOSAL_NOT_UNDER_REVIEW); version diferente → 409 CONCURRENT_MODIFICATION; o cliente é avisado por e-mail (se o tiver verificado).
  ///
  /// Parameters:
  /// * [id] 
  /// * [requestProposalInfoRequest] 
  /// * [cancelToken] - A [CancelToken] that can be used to cancel the operation
  /// * [headers] - Can be used to add additional headers to the request
  /// * [extras] - Can be used to add flags to the request
  /// * [validateStatus] - A [ValidateStatus] callback that can be used to determine request success based on the HTTP status of the response
  /// * [onSendProgress] - A [ProgressCallback] that can be used to get the send progress
  /// * [onReceiveProgress] - A [ProgressCallback] that can be used to get the receive progress
  ///
  /// Returns a [Future] containing a [Response] with a [InsurerProposalResponse] as data
  /// Throws [DioException] if API call or serialization fails
  Future<Response<InsurerProposalResponse>> requestProposalInfo({ 
    required String id,
    required RequestProposalInfoRequest requestProposalInfoRequest,
    CancelToken? cancelToken,
    Map<String, dynamic>? headers,
    Map<String, dynamic>? extra,
    ValidateStatus? validateStatus,
    ProgressCallback? onSendProgress,
    ProgressCallback? onReceiveProgress,
  }) async {
    final _path = r'/api/v1/insurer/proposals/{id}/request-info'.replaceAll('{' r'id' '}', encodeQueryParameter(_serializers, id, const FullType(String)).toString());
    final _options = Options(
      method: r'POST',
      headers: <String, dynamic>{
        ...?headers,
      },
      extra: <String, dynamic>{
        'secure': <Map<String, String>>[
          {
            'type': 'http',
            'scheme': 'bearer',
            'name': 'bearerAuth',
          },
        ],
        ...?extra,
      },
      contentType: 'application/json',
      validateStatus: validateStatus,
    );

    dynamic _bodyData;

    try {
      const _type = FullType(RequestProposalInfoRequest);
      _bodyData = _serializers.serialize(requestProposalInfoRequest, specifiedType: _type);

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

    InsurerProposalResponse? _responseData;

    try {
      final rawResponse = _response.data;
      _responseData = rawResponse == null ? null : _serializers.deserialize(
        rawResponse,
        specifiedType: const FullType(InsurerProposalResponse),
      ) as InsurerProposalResponse;

    } catch (error, stackTrace) {
      throw DioException(
        requestOptions: _response.requestOptions,
        response: _response,
        type: DioExceptionType.unknown,
        error: error,
        stackTrace: stackTrace,
      );
    }

    return Response<InsurerProposalResponse>(
      data: _responseData,
      headers: _response.headers,
      isRedirect: _response.isRedirect,
      requestOptions: _response.requestOptions,
      redirects: _response.redirects,
      statusCode: _response.statusCode,
      statusMessage: _response.statusMessage,
      extra: _response.extra,
    );
  }

  /// Gera o PDF da proposta e envia o código de assinatura
  /// Permissão self:proposals ou proposal:create-for-client. Por esta ordem: proposta em DRAFT ou INFO_REQUESTED (senão 409 PROPOSAL_NOT_EDITABLE); version diferente → 409 CONCURRENT_MODIFICATION; documentsAcknowledged ≠ true → 400 (campo documentsAcknowledged, MUST_BE_TRUE); passos em falta → 409 PROPOSAL_INCOMPLETE com missing (DECLARATIONS, SUITABILITY, SUITABILITY_ACKNOWLEDGEMENT, INSPECTION, START_DATE); sem telemóvel nem e-mail verificado → 409 SIGNER_CONTACT_MISSING. Gera o PDF (SHA-256 em documentHash) e envia ao tomador um código que só assina esse PDF; os limites do código são os do login (429 OTP_RATE_LIMITED / OTP_RESEND_TOO_SOON com Retry-After). Pedir de novo substitui o pedido anterior; não altera contentRevision. Proposta de outro cliente → 404 PROPOSAL_NOT_FOUND. Pedido pelo mediador da proposta (S4-06): assinatura assistida, o código vai para o telemóvel da conta do cliente com o nome do mediador na mensagem.
  ///
  /// Parameters:
  /// * [id] 
  /// * [requestSignatureRequest] 
  /// * [cancelToken] - A [CancelToken] that can be used to cancel the operation
  /// * [headers] - Can be used to add additional headers to the request
  /// * [extras] - Can be used to add flags to the request
  /// * [validateStatus] - A [ValidateStatus] callback that can be used to determine request success based on the HTTP status of the response
  /// * [onSendProgress] - A [ProgressCallback] that can be used to get the send progress
  /// * [onReceiveProgress] - A [ProgressCallback] that can be used to get the receive progress
  ///
  /// Returns a [Future] containing a [Response] with a [SignatureRequestResponse] as data
  /// Throws [DioException] if API call or serialization fails
  Future<Response<SignatureRequestResponse>> requestProposalSignature({ 
    required String id,
    required RequestSignatureRequest requestSignatureRequest,
    CancelToken? cancelToken,
    Map<String, dynamic>? headers,
    Map<String, dynamic>? extra,
    ValidateStatus? validateStatus,
    ProgressCallback? onSendProgress,
    ProgressCallback? onReceiveProgress,
  }) async {
    final _path = r'/api/v1/proposals/{id}/sign/request'.replaceAll('{' r'id' '}', encodeQueryParameter(_serializers, id, const FullType(String)).toString());
    final _options = Options(
      method: r'POST',
      headers: <String, dynamic>{
        ...?headers,
      },
      extra: <String, dynamic>{
        'secure': <Map<String, String>>[
          {
            'type': 'http',
            'scheme': 'bearer',
            'name': 'bearerAuth',
          },
        ],
        ...?extra,
      },
      contentType: 'application/json',
      validateStatus: validateStatus,
    );

    dynamic _bodyData;

    try {
      const _type = FullType(RequestSignatureRequest);
      _bodyData = _serializers.serialize(requestSignatureRequest, specifiedType: _type);

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

    SignatureRequestResponse? _responseData;

    try {
      final rawResponse = _response.data;
      _responseData = rawResponse == null ? null : _serializers.deserialize(
        rawResponse,
        specifiedType: const FullType(SignatureRequestResponse),
      ) as SignatureRequestResponse;

    } catch (error, stackTrace) {
      throw DioException(
        requestOptions: _response.requestOptions,
        response: _response,
        type: DioExceptionType.unknown,
        error: error,
        stackTrace: stackTrace,
      );
    }

    return Response<SignatureRequestResponse>(
      data: _responseData,
      headers: _response.headers,
      isRedirect: _response.isRedirect,
      requestOptions: _response.requestOptions,
      redirects: _response.redirects,
      statusCode: _response.statusCode,
      statusMessage: _response.statusMessage,
      extra: _response.extra,
    );
  }

  /// Grava as declarações de risco
  /// Permissão self:proposals ou proposal:create-for-client. As cinco perguntas de api/proposal-questions.json, os campos de seguimento com resposta sim e truthfulnessConfirmed &#x3D; true; erros juntos em errors[] (400 VALIDATION_ERROR). Só em DRAFT ou INFO_REQUESTED (senão 409 PROPOSAL_NOT_EDITABLE); version diferente → 409 CONCURRENT_MODIFICATION; proposta de outro cliente → 404 PROPOSAL_NOT_FOUND. Aumenta contentRevision e renova expiresAt (+15 dias).
  ///
  /// Parameters:
  /// * [id] 
  /// * [saveDeclarationsRequest] 
  /// * [cancelToken] - A [CancelToken] that can be used to cancel the operation
  /// * [headers] - Can be used to add additional headers to the request
  /// * [extras] - Can be used to add flags to the request
  /// * [validateStatus] - A [ValidateStatus] callback that can be used to determine request success based on the HTTP status of the response
  /// * [onSendProgress] - A [ProgressCallback] that can be used to get the send progress
  /// * [onReceiveProgress] - A [ProgressCallback] that can be used to get the receive progress
  ///
  /// Returns a [Future] containing a [Response] with a [ProposalResponse] as data
  /// Throws [DioException] if API call or serialization fails
  Future<Response<ProposalResponse>> saveProposalDeclarations({ 
    required String id,
    required SaveDeclarationsRequest saveDeclarationsRequest,
    CancelToken? cancelToken,
    Map<String, dynamic>? headers,
    Map<String, dynamic>? extra,
    ValidateStatus? validateStatus,
    ProgressCallback? onSendProgress,
    ProgressCallback? onReceiveProgress,
  }) async {
    final _path = r'/api/v1/proposals/{id}/declarations'.replaceAll('{' r'id' '}', encodeQueryParameter(_serializers, id, const FullType(String)).toString());
    final _options = Options(
      method: r'POST',
      headers: <String, dynamic>{
        ...?headers,
      },
      extra: <String, dynamic>{
        'secure': <Map<String, String>>[
          {
            'type': 'http',
            'scheme': 'bearer',
            'name': 'bearerAuth',
          },
        ],
        ...?extra,
      },
      contentType: 'application/json',
      validateStatus: validateStatus,
    );

    dynamic _bodyData;

    try {
      const _type = FullType(SaveDeclarationsRequest);
      _bodyData = _serializers.serialize(saveDeclarationsRequest, specifiedType: _type);

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

    ProposalResponse? _responseData;

    try {
      final rawResponse = _response.data;
      _responseData = rawResponse == null ? null : _serializers.deserialize(
        rawResponse,
        specifiedType: const FullType(ProposalResponse),
      ) as ProposalResponse;

    } catch (error, stackTrace) {
      throw DioException(
        requestOptions: _response.requestOptions,
        response: _response,
        type: DioExceptionType.unknown,
        error: error,
        stackTrace: stackTrace,
      );
    }

    return Response<ProposalResponse>(
      data: _responseData,
      headers: _response.headers,
      isRedirect: _response.isRedirect,
      requestOptions: _response.requestOptions,
      redirects: _response.redirects,
      statusCode: _response.statusCode,
      statusMessage: _response.statusMessage,
      extra: _response.extra,
    );
  }

  /// Grava o questionário de adequação
  /// Permissão self:proposals ou proposal:create-for-client. Devolve suitability.result (SUITABLE ou WARNED) e os motivos; limpa uma confirmação anterior dos avisos; erros juntos em errors[] (400 VALIDATION_ERROR). Só em DRAFT ou INFO_REQUESTED (senão 409 PROPOSAL_NOT_EDITABLE); version diferente → 409 CONCURRENT_MODIFICATION; proposta de outro cliente → 404 PROPOSAL_NOT_FOUND. Aumenta contentRevision e renova expiresAt (+15 dias).
  ///
  /// Parameters:
  /// * [id] 
  /// * [saveSuitabilityRequest] 
  /// * [cancelToken] - A [CancelToken] that can be used to cancel the operation
  /// * [headers] - Can be used to add additional headers to the request
  /// * [extras] - Can be used to add flags to the request
  /// * [validateStatus] - A [ValidateStatus] callback that can be used to determine request success based on the HTTP status of the response
  /// * [onSendProgress] - A [ProgressCallback] that can be used to get the send progress
  /// * [onReceiveProgress] - A [ProgressCallback] that can be used to get the receive progress
  ///
  /// Returns a [Future] containing a [Response] with a [ProposalResponse] as data
  /// Throws [DioException] if API call or serialization fails
  Future<Response<ProposalResponse>> saveProposalSuitability({ 
    required String id,
    required SaveSuitabilityRequest saveSuitabilityRequest,
    CancelToken? cancelToken,
    Map<String, dynamic>? headers,
    Map<String, dynamic>? extra,
    ValidateStatus? validateStatus,
    ProgressCallback? onSendProgress,
    ProgressCallback? onReceiveProgress,
  }) async {
    final _path = r'/api/v1/proposals/{id}/suitability'.replaceAll('{' r'id' '}', encodeQueryParameter(_serializers, id, const FullType(String)).toString());
    final _options = Options(
      method: r'POST',
      headers: <String, dynamic>{
        ...?headers,
      },
      extra: <String, dynamic>{
        'secure': <Map<String, String>>[
          {
            'type': 'http',
            'scheme': 'bearer',
            'name': 'bearerAuth',
          },
        ],
        ...?extra,
      },
      contentType: 'application/json',
      validateStatus: validateStatus,
    );

    dynamic _bodyData;

    try {
      const _type = FullType(SaveSuitabilityRequest);
      _bodyData = _serializers.serialize(saveSuitabilityRequest, specifiedType: _type);

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

    ProposalResponse? _responseData;

    try {
      final rawResponse = _response.data;
      _responseData = rawResponse == null ? null : _serializers.deserialize(
        rawResponse,
        specifiedType: const FullType(ProposalResponse),
      ) as ProposalResponse;

    } catch (error, stackTrace) {
      throw DioException(
        requestOptions: _response.requestOptions,
        response: _response,
        type: DioExceptionType.unknown,
        error: error,
        stackTrace: stackTrace,
      );
    }

    return Response<ProposalResponse>(
      data: _responseData,
      headers: _response.headers,
      isRedirect: _response.isRedirect,
      requestOptions: _response.requestOptions,
      redirects: _response.redirects,
      statusCode: _response.statusCode,
      statusMessage: _response.statusMessage,
      extra: _response.extra,
    );
  }

  /// Envia ao cliente o link de pagamento
  /// Permissão payment:link-for-client. SMS ao telemóvel do cliente com o link {web}/cliente/propostas/{id}/pagar?origem&#x3D;mediador. Só propostas deste mediador (senão 404 PROPOSAL_NOT_FOUND) em ACCEPTED (409 PAYMENT_TARGET_NOT_PAYABLE); cliente sem telemóvel → 409 CUSTOMER_HAS_NO_MSISDN; no máximo 3 envios por proposta em 24 h (429 PAYMENT_LINK_RATE_LIMITED, retryAfterSeconds).
  ///
  /// Parameters:
  /// * [id] 
  /// * [cancelToken] - A [CancelToken] that can be used to cancel the operation
  /// * [headers] - Can be used to add additional headers to the request
  /// * [extras] - Can be used to add flags to the request
  /// * [validateStatus] - A [ValidateStatus] callback that can be used to determine request success based on the HTTP status of the response
  /// * [onSendProgress] - A [ProgressCallback] that can be used to get the send progress
  /// * [onReceiveProgress] - A [ProgressCallback] that can be used to get the receive progress
  ///
  /// Returns a [Future] containing a [Response] with a [PaymentLinkResponse] as data
  /// Throws [DioException] if API call or serialization fails
  Future<Response<PaymentLinkResponse>> sendProposalPaymentLink({ 
    required String id,
    CancelToken? cancelToken,
    Map<String, dynamic>? headers,
    Map<String, dynamic>? extra,
    ValidateStatus? validateStatus,
    ProgressCallback? onSendProgress,
    ProgressCallback? onReceiveProgress,
  }) async {
    final _path = r'/api/v1/broker/proposals/{id}/payment-link'.replaceAll('{' r'id' '}', encodeQueryParameter(_serializers, id, const FullType(String)).toString());
    final _options = Options(
      method: r'POST',
      headers: <String, dynamic>{
        ...?headers,
      },
      extra: <String, dynamic>{
        'secure': <Map<String, String>>[
          {
            'type': 'http',
            'scheme': 'bearer',
            'name': 'bearerAuth',
          },
        ],
        ...?extra,
      },
      validateStatus: validateStatus,
    );

    final _response = await _dio.request<Object>(
      _path,
      options: _options,
      cancelToken: cancelToken,
      onSendProgress: onSendProgress,
      onReceiveProgress: onReceiveProgress,
    );

    PaymentLinkResponse? _responseData;

    try {
      final rawResponse = _response.data;
      _responseData = rawResponse == null ? null : _serializers.deserialize(
        rawResponse,
        specifiedType: const FullType(PaymentLinkResponse),
      ) as PaymentLinkResponse;

    } catch (error, stackTrace) {
      throw DioException(
        requestOptions: _response.requestOptions,
        response: _response,
        type: DioExceptionType.unknown,
        error: error,
        stackTrace: stackTrace,
      );
    }

    return Response<PaymentLinkResponse>(
      data: _responseData,
      headers: _response.headers,
      isRedirect: _response.isRedirect,
      requestOptions: _response.requestOptions,
      redirects: _response.redirects,
      statusCode: _response.statusCode,
      statusMessage: _response.statusMessage,
      extra: _response.extra,
    );
  }

  /// Submete a vistoria por fotografias
  /// Permissão self:proposals ou proposal:create-for-client. PENDING → SUBMITTED com a quilometragem (REQUIRED, OUT_OF_RANGE). Faltam ângulos → 409 INSPECTION_PHOTOS_MISSING (missingSlots); REJECTED sem fotografia nova → 409 INVALID_STATE_TRANSITION; version da proposta diferente → 409 CONCURRENT_MODIFICATION. Proposta de outro cliente → 404 PROPOSAL_NOT_FOUND; sem coberturas OD_* → 409 INSPECTION_NOT_REQUIRED; proposta fora de DRAFT/INFO_REQUESTED → 409 PROPOSAL_NOT_EDITABLE; vistoria fora de PENDING/REJECTED → 409 INSPECTION_NOT_EDITABLE. Aumenta contentRevision e renova expiresAt.
  ///
  /// Parameters:
  /// * [id] 
  /// * [submitInspectionRequest] 
  /// * [cancelToken] - A [CancelToken] that can be used to cancel the operation
  /// * [headers] - Can be used to add additional headers to the request
  /// * [extras] - Can be used to add flags to the request
  /// * [validateStatus] - A [ValidateStatus] callback that can be used to determine request success based on the HTTP status of the response
  /// * [onSendProgress] - A [ProgressCallback] that can be used to get the send progress
  /// * [onReceiveProgress] - A [ProgressCallback] that can be used to get the receive progress
  ///
  /// Returns a [Future] containing a [Response] with a [ProposalResponse] as data
  /// Throws [DioException] if API call or serialization fails
  Future<Response<ProposalResponse>> submitInspection({ 
    required String id,
    required SubmitInspectionRequest submitInspectionRequest,
    CancelToken? cancelToken,
    Map<String, dynamic>? headers,
    Map<String, dynamic>? extra,
    ValidateStatus? validateStatus,
    ProgressCallback? onSendProgress,
    ProgressCallback? onReceiveProgress,
  }) async {
    final _path = r'/api/v1/proposals/{id}/inspection/submit'.replaceAll('{' r'id' '}', encodeQueryParameter(_serializers, id, const FullType(String)).toString());
    final _options = Options(
      method: r'POST',
      headers: <String, dynamic>{
        ...?headers,
      },
      extra: <String, dynamic>{
        'secure': <Map<String, String>>[
          {
            'type': 'http',
            'scheme': 'bearer',
            'name': 'bearerAuth',
          },
        ],
        ...?extra,
      },
      contentType: 'application/json',
      validateStatus: validateStatus,
    );

    dynamic _bodyData;

    try {
      const _type = FullType(SubmitInspectionRequest);
      _bodyData = _serializers.serialize(submitInspectionRequest, specifiedType: _type);

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

    ProposalResponse? _responseData;

    try {
      final rawResponse = _response.data;
      _responseData = rawResponse == null ? null : _serializers.deserialize(
        rawResponse,
        specifiedType: const FullType(ProposalResponse),
      ) as ProposalResponse;

    } catch (error, stackTrace) {
      throw DioException(
        requestOptions: _response.requestOptions,
        response: _response,
        type: DioExceptionType.unknown,
        error: error,
        stackTrace: stackTrace,
      );
    }

    return Response<ProposalResponse>(
      data: _responseData,
      headers: _response.headers,
      isRedirect: _response.isRedirect,
      requestOptions: _response.requestOptions,
      redirects: _response.redirects,
      statusCode: _response.statusCode,
      statusMessage: _response.statusMessage,
      extra: _response.extra,
    );
  }

  /// Submete a proposta à seguradora
  /// Permissão self:proposals ou proposal:create-for-client. Só em DRAFT ou INFO_REQUESTED (senão 409 INVALID_STATE_TRANSITION); version diferente → 409 CONCURRENT_MODIFICATION; proposta incompleta ou sem assinatura válida → 409 PROPOSAL_INCOMPLETE (missing); seguradora não activa → 409 INSURER_NOT_ACTIVE. As regras de aceitação do produto decidem logo: ACCEPTED (aceitação automática, expiresAt &#x3D; prazo para pagar) ou UNDER_REVIEW (decision.insurerDueAt &#x3D; prazo da seguradora). Depois de submetida, a proposta só se altera se a seguradora pedir informação.
  ///
  /// Parameters:
  /// * [id] 
  /// * [submitProposalRequest] 
  /// * [cancelToken] - A [CancelToken] that can be used to cancel the operation
  /// * [headers] - Can be used to add additional headers to the request
  /// * [extras] - Can be used to add flags to the request
  /// * [validateStatus] - A [ValidateStatus] callback that can be used to determine request success based on the HTTP status of the response
  /// * [onSendProgress] - A [ProgressCallback] that can be used to get the send progress
  /// * [onReceiveProgress] - A [ProgressCallback] that can be used to get the receive progress
  ///
  /// Returns a [Future] containing a [Response] with a [ProposalResponse] as data
  /// Throws [DioException] if API call or serialization fails
  Future<Response<ProposalResponse>> submitProposal({ 
    required String id,
    required SubmitProposalRequest submitProposalRequest,
    CancelToken? cancelToken,
    Map<String, dynamic>? headers,
    Map<String, dynamic>? extra,
    ValidateStatus? validateStatus,
    ProgressCallback? onSendProgress,
    ProgressCallback? onReceiveProgress,
  }) async {
    final _path = r'/api/v1/proposals/{id}/submit'.replaceAll('{' r'id' '}', encodeQueryParameter(_serializers, id, const FullType(String)).toString());
    final _options = Options(
      method: r'POST',
      headers: <String, dynamic>{
        ...?headers,
      },
      extra: <String, dynamic>{
        'secure': <Map<String, String>>[
          {
            'type': 'http',
            'scheme': 'bearer',
            'name': 'bearerAuth',
          },
        ],
        ...?extra,
      },
      contentType: 'application/json',
      validateStatus: validateStatus,
    );

    dynamic _bodyData;

    try {
      const _type = FullType(SubmitProposalRequest);
      _bodyData = _serializers.serialize(submitProposalRequest, specifiedType: _type);

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

    ProposalResponse? _responseData;

    try {
      final rawResponse = _response.data;
      _responseData = rawResponse == null ? null : _serializers.deserialize(
        rawResponse,
        specifiedType: const FullType(ProposalResponse),
      ) as ProposalResponse;

    } catch (error, stackTrace) {
      throw DioException(
        requestOptions: _response.requestOptions,
        response: _response,
        type: DioExceptionType.unknown,
        error: error,
        stackTrace: stackTrace,
      );
    }

    return Response<ProposalResponse>(
      data: _responseData,
      headers: _response.headers,
      isRedirect: _response.isRedirect,
      requestOptions: _response.requestOptions,
      redirects: _response.redirects,
      statusCode: _response.statusCode,
      statusMessage: _response.statusMessage,
      extra: _response.extra,
    );
  }

  /// Altera a data de início ou o tomador
  /// Permissão proposal:create-for-client (corretor ou agente). Só em DRAFT ou INFO_REQUESTED (senão 409 PROPOSAL_NOT_EDITABLE); version diferente → 409 CONCURRENT_MODIFICATION; proposta de outra organização → 404 PROPOSAL_NOT_FOUND. startDate entre hoje e hoje + 60 dias (START_DATE_OUT_OF_RANGE); policyholder só com policyholderSource &#x3D; BROKER (senão 400 NOT_ALLOWED), com as regras do perfil (idDocNumber e nuit omitidos mantêm os actuais); sem nenhum dos dois → 400 REQUIRED (startDate). Com policyholderSource &#x3D; PROFILE o tomador é copiado de novo do perfil. Aumenta contentRevision, invalida a assinatura e renova expiresAt. O veículo e o condutor não mudam (mudar o risco exige nova cotação).
  ///
  /// Parameters:
  /// * [id] 
  /// * [updateBrokerProposalRequest] 
  /// * [cancelToken] - A [CancelToken] that can be used to cancel the operation
  /// * [headers] - Can be used to add additional headers to the request
  /// * [extras] - Can be used to add flags to the request
  /// * [validateStatus] - A [ValidateStatus] callback that can be used to determine request success based on the HTTP status of the response
  /// * [onSendProgress] - A [ProgressCallback] that can be used to get the send progress
  /// * [onReceiveProgress] - A [ProgressCallback] that can be used to get the receive progress
  ///
  /// Returns a [Future] containing a [Response] with a [ProposalResponse] as data
  /// Throws [DioException] if API call or serialization fails
  Future<Response<ProposalResponse>> updateBrokerProposal({ 
    required String id,
    required UpdateBrokerProposalRequest updateBrokerProposalRequest,
    CancelToken? cancelToken,
    Map<String, dynamic>? headers,
    Map<String, dynamic>? extra,
    ValidateStatus? validateStatus,
    ProgressCallback? onSendProgress,
    ProgressCallback? onReceiveProgress,
  }) async {
    final _path = r'/api/v1/broker/proposals/{id}'.replaceAll('{' r'id' '}', encodeQueryParameter(_serializers, id, const FullType(String)).toString());
    final _options = Options(
      method: r'PUT',
      headers: <String, dynamic>{
        ...?headers,
      },
      extra: <String, dynamic>{
        'secure': <Map<String, String>>[
          {
            'type': 'http',
            'scheme': 'bearer',
            'name': 'bearerAuth',
          },
        ],
        ...?extra,
      },
      contentType: 'application/json',
      validateStatus: validateStatus,
    );

    dynamic _bodyData;

    try {
      const _type = FullType(UpdateBrokerProposalRequest);
      _bodyData = _serializers.serialize(updateBrokerProposalRequest, specifiedType: _type);

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

    ProposalResponse? _responseData;

    try {
      final rawResponse = _response.data;
      _responseData = rawResponse == null ? null : _serializers.deserialize(
        rawResponse,
        specifiedType: const FullType(ProposalResponse),
      ) as ProposalResponse;

    } catch (error, stackTrace) {
      throw DioException(
        requestOptions: _response.requestOptions,
        response: _response,
        type: DioExceptionType.unknown,
        error: error,
        stackTrace: stackTrace,
      );
    }

    return Response<ProposalResponse>(
      data: _responseData,
      headers: _response.headers,
      isRedirect: _response.isRedirect,
      requestOptions: _response.requestOptions,
      redirects: _response.redirects,
      statusCode: _response.statusCode,
      statusMessage: _response.statusMessage,
      extra: _response.extra,
    );
  }

  /// Altera a data de início da proposta
  /// Permissão self:proposals ou proposal:create-for-client. startDate entre hoje e hoje + 60 dias (Africa/Maputo; senão START_DATE_OUT_OF_RANGE); a duração e o prémio não mudam; o tomador é copiado de novo do perfil (perfil incompleto → 409 PROFILE_INCOMPLETE); com policyholderSource &#x3D; BROKER (S4-06) mantém o tomador preenchido pelo mediador. Só em DRAFT ou INFO_REQUESTED (senão 409 PROPOSAL_NOT_EDITABLE); version diferente → 409 CONCURRENT_MODIFICATION; proposta de outro cliente → 404 PROPOSAL_NOT_FOUND. Aumenta contentRevision e renova expiresAt (+15 dias).
  ///
  /// Parameters:
  /// * [id] 
  /// * [updateProposalRequest] 
  /// * [cancelToken] - A [CancelToken] that can be used to cancel the operation
  /// * [headers] - Can be used to add additional headers to the request
  /// * [extras] - Can be used to add flags to the request
  /// * [validateStatus] - A [ValidateStatus] callback that can be used to determine request success based on the HTTP status of the response
  /// * [onSendProgress] - A [ProgressCallback] that can be used to get the send progress
  /// * [onReceiveProgress] - A [ProgressCallback] that can be used to get the receive progress
  ///
  /// Returns a [Future] containing a [Response] with a [ProposalResponse] as data
  /// Throws [DioException] if API call or serialization fails
  Future<Response<ProposalResponse>> updateProposal({ 
    required String id,
    required UpdateProposalRequest updateProposalRequest,
    CancelToken? cancelToken,
    Map<String, dynamic>? headers,
    Map<String, dynamic>? extra,
    ValidateStatus? validateStatus,
    ProgressCallback? onSendProgress,
    ProgressCallback? onReceiveProgress,
  }) async {
    final _path = r'/api/v1/proposals/{id}'.replaceAll('{' r'id' '}', encodeQueryParameter(_serializers, id, const FullType(String)).toString());
    final _options = Options(
      method: r'PUT',
      headers: <String, dynamic>{
        ...?headers,
      },
      extra: <String, dynamic>{
        'secure': <Map<String, String>>[
          {
            'type': 'http',
            'scheme': 'bearer',
            'name': 'bearerAuth',
          },
        ],
        ...?extra,
      },
      contentType: 'application/json',
      validateStatus: validateStatus,
    );

    dynamic _bodyData;

    try {
      const _type = FullType(UpdateProposalRequest);
      _bodyData = _serializers.serialize(updateProposalRequest, specifiedType: _type);

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

    ProposalResponse? _responseData;

    try {
      final rawResponse = _response.data;
      _responseData = rawResponse == null ? null : _serializers.deserialize(
        rawResponse,
        specifiedType: const FullType(ProposalResponse),
      ) as ProposalResponse;

    } catch (error, stackTrace) {
      throw DioException(
        requestOptions: _response.requestOptions,
        response: _response,
        type: DioExceptionType.unknown,
        error: error,
        stackTrace: stackTrace,
      );
    }

    return Response<ProposalResponse>(
      data: _responseData,
      headers: _response.headers,
      isRedirect: _response.isRedirect,
      requestOptions: _response.requestOptions,
      redirects: _response.redirects,
      statusCode: _response.statusCode,
      statusMessage: _response.statusMessage,
      extra: _response.extra,
    );
  }

  /// Envia uma fotografia da vistoria
  /// Permissão self:proposals ou proposal:create-for-client. file: JPEG, PNG ou HEIC até 10 MB (em falta → REQUIRED; vazio → 400 FILE_EMPTY; maior → 413 FILE_TOO_LARGE; outro tipo → 415 FILE_TYPE_NOT_ALLOWED). slot obrigatório (INVALID_SLOT); EXTRA além do máximo → EXTRA_PHOTOS_LIMIT; uma fotografia nova num ângulo fixo substitui a anterior. takenAt no futuro (+5 min) → PHOTO_IN_FUTURE, com mais de 7 dias → PHOTO_TOO_OLD; latitude e longitude juntas e dentro dos limites (INVALID_COORDINATES). Com um clientPhotoId já enviado devolve 200 com a mesma fotografia, sem gravar nada. Numa vistoria REJECTED passa-a a PENDING. Proposta de outro cliente → 404 PROPOSAL_NOT_FOUND; sem coberturas OD_* → 409 INSPECTION_NOT_REQUIRED; proposta fora de DRAFT/INFO_REQUESTED → 409 PROPOSAL_NOT_EDITABLE; vistoria fora de PENDING/REJECTED → 409 INSPECTION_NOT_EDITABLE. Aumenta contentRevision e renova expiresAt.
  ///
  /// Parameters:
  /// * [id] 
  /// * [clientPhotoId] - Gerado pelo cliente; torna o envio idempotente
  /// * [file] 
  /// * [latitude] 
  /// * [longitude] 
  /// * [slot] 
  /// * [source_] 
  /// * [takenAt] - Hora da fotografia (ISO 8601)
  /// * [cancelToken] - A [CancelToken] that can be used to cancel the operation
  /// * [headers] - Can be used to add additional headers to the request
  /// * [extras] - Can be used to add flags to the request
  /// * [validateStatus] - A [ValidateStatus] callback that can be used to determine request success based on the HTTP status of the response
  /// * [onSendProgress] - A [ProgressCallback] that can be used to get the send progress
  /// * [onReceiveProgress] - A [ProgressCallback] that can be used to get the receive progress
  ///
  /// Returns a [Future] containing a [Response] with a [InspectionPhotoResponse] as data
  /// Throws [DioException] if API call or serialization fails
  Future<Response<InspectionPhotoResponse>> uploadInspectionPhoto({ 
    required String id,
    String? clientPhotoId,
    MultipartFile? file,
    num? latitude,
    num? longitude,
    PhotoSlot? slot,
    PhotoSource? source_,
    DateTime? takenAt,
    CancelToken? cancelToken,
    Map<String, dynamic>? headers,
    Map<String, dynamic>? extra,
    ValidateStatus? validateStatus,
    ProgressCallback? onSendProgress,
    ProgressCallback? onReceiveProgress,
  }) async {
    final _path = r'/api/v1/proposals/{id}/inspection/photos'.replaceAll('{' r'id' '}', encodeQueryParameter(_serializers, id, const FullType(String)).toString());
    final _options = Options(
      method: r'POST',
      headers: <String, dynamic>{
        ...?headers,
      },
      extra: <String, dynamic>{
        'secure': <Map<String, String>>[
          {
            'type': 'http',
            'scheme': 'bearer',
            'name': 'bearerAuth',
          },
        ],
        ...?extra,
      },
      contentType: 'multipart/form-data',
      validateStatus: validateStatus,
    );

    dynamic _bodyData;

    try {
      _bodyData = FormData.fromMap(<String, dynamic>{
        if (clientPhotoId != null) r'clientPhotoId': encodeFormParameter(_serializers, clientPhotoId, const FullType(String)),
        if (file != null) r'file': file,
        if (latitude != null) r'latitude': encodeFormParameter(_serializers, latitude, const FullType(num)),
        if (longitude != null) r'longitude': encodeFormParameter(_serializers, longitude, const FullType(num)),
        if (slot != null) r'slot': encodeFormParameter(_serializers, slot, const FullType(PhotoSlot)),
        if (source_ != null) r'source': encodeFormParameter(_serializers, source_, const FullType(PhotoSource)),
        if (takenAt != null) r'takenAt': encodeFormParameter(_serializers, takenAt, const FullType(DateTime)),
      });

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

    InspectionPhotoResponse? _responseData;

    try {
      final rawResponse = _response.data;
      _responseData = rawResponse == null ? null : _serializers.deserialize(
        rawResponse,
        specifiedType: const FullType(InspectionPhotoResponse),
      ) as InspectionPhotoResponse;

    } catch (error, stackTrace) {
      throw DioException(
        requestOptions: _response.requestOptions,
        response: _response,
        type: DioExceptionType.unknown,
        error: error,
        stackTrace: stackTrace,
      );
    }

    return Response<InspectionPhotoResponse>(
      data: _responseData,
      headers: _response.headers,
      isRedirect: _response.isRedirect,
      requestOptions: _response.requestOptions,
      redirects: _response.redirects,
      statusCode: _response.statusCode,
      statusMessage: _response.statusMessage,
      extra: _response.extra,
    );
  }

}
