//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

import 'package:dio/dio.dart';
import 'package:built_value/serializer.dart';
import 'package:moseguros_api/src/serializers.dart';
import 'package:moseguros_api/src/auth/api_key_auth.dart';
import 'package:moseguros_api/src/auth/basic_auth.dart';
import 'package:moseguros_api/src/auth/bearer_auth.dart';
import 'package:moseguros_api/src/auth/oauth.dart';
import 'package:moseguros_api/src/api/catalog_api.dart';
import 'package:moseguros_api/src/api/document_api.dart';
import 'package:moseguros_api/src/api/identity_api.dart';
import 'package:moseguros_api/src/api/meta_api.dart';
import 'package:moseguros_api/src/api/notification_api.dart';
import 'package:moseguros_api/src/api/organization_api.dart';
import 'package:moseguros_api/src/api/payment_api.dart';
import 'package:moseguros_api/src/api/policy_api.dart';
import 'package:moseguros_api/src/api/proposal_api.dart';
import 'package:moseguros_api/src/api/rating_api.dart';
import 'package:moseguros_api/src/api/vehicle_api.dart';
import 'package:moseguros_api/src/api/verification_api.dart';

class MosegurosApi {
  static const String basePath = r'http://localhost';

  final Dio dio;
  final Serializers serializers;

  MosegurosApi({
    Dio? dio,
    Serializers? serializers,
    String? basePathOverride,
    List<Interceptor>? interceptors,
  })  : this.serializers = serializers ?? standardSerializers,
        this.dio = dio ??
            Dio(BaseOptions(
              baseUrl: basePathOverride ?? basePath,
              connectTimeout: const Duration(milliseconds: 5000),
              receiveTimeout: const Duration(milliseconds: 3000),
            )) {
    if (interceptors == null) {
      this.dio.interceptors.addAll([
        OAuthInterceptor(),
        BasicAuthInterceptor(),
        BearerAuthInterceptor(),
        ApiKeyAuthInterceptor(),
      ]);
    } else {
      this.dio.interceptors.addAll(interceptors);
    }
  }

  void setOAuthToken(String name, String token) {
    if (this.dio.interceptors.any((i) => i is OAuthInterceptor)) {
      (this.dio.interceptors.firstWhere((i) => i is OAuthInterceptor) as OAuthInterceptor).tokens[name] = token;
    }
  }

  void setBearerAuth(String name, String token) {
    if (this.dio.interceptors.any((i) => i is BearerAuthInterceptor)) {
      (this.dio.interceptors.firstWhere((i) => i is BearerAuthInterceptor) as BearerAuthInterceptor).tokens[name] = token;
    }
  }

  void setBasicAuth(String name, String username, String password) {
    if (this.dio.interceptors.any((i) => i is BasicAuthInterceptor)) {
      (this.dio.interceptors.firstWhere((i) => i is BasicAuthInterceptor) as BasicAuthInterceptor).authInfo[name] = BasicAuthInfo(username, password);
    }
  }

  void setApiKey(String name, String apiKey) {
    if (this.dio.interceptors.any((i) => i is ApiKeyAuthInterceptor)) {
      (this.dio.interceptors.firstWhere((element) => element is ApiKeyAuthInterceptor) as ApiKeyAuthInterceptor).apiKeys[name] = apiKey;
    }
  }

  /// Get CatalogApi instance, base route and serializer can be overridden by a given but be careful,
  /// by doing that all interceptors will not be executed
  CatalogApi getCatalogApi() {
    return CatalogApi(dio, serializers);
  }

  /// Get DocumentApi instance, base route and serializer can be overridden by a given but be careful,
  /// by doing that all interceptors will not be executed
  DocumentApi getDocumentApi() {
    return DocumentApi(dio, serializers);
  }

  /// Get IdentityApi instance, base route and serializer can be overridden by a given but be careful,
  /// by doing that all interceptors will not be executed
  IdentityApi getIdentityApi() {
    return IdentityApi(dio, serializers);
  }

  /// Get MetaApi instance, base route and serializer can be overridden by a given but be careful,
  /// by doing that all interceptors will not be executed
  MetaApi getMetaApi() {
    return MetaApi(dio, serializers);
  }

  /// Get NotificationApi instance, base route and serializer can be overridden by a given but be careful,
  /// by doing that all interceptors will not be executed
  NotificationApi getNotificationApi() {
    return NotificationApi(dio, serializers);
  }

  /// Get OrganizationApi instance, base route and serializer can be overridden by a given but be careful,
  /// by doing that all interceptors will not be executed
  OrganizationApi getOrganizationApi() {
    return OrganizationApi(dio, serializers);
  }

  /// Get PaymentApi instance, base route and serializer can be overridden by a given but be careful,
  /// by doing that all interceptors will not be executed
  PaymentApi getPaymentApi() {
    return PaymentApi(dio, serializers);
  }

  /// Get PolicyApi instance, base route and serializer can be overridden by a given but be careful,
  /// by doing that all interceptors will not be executed
  PolicyApi getPolicyApi() {
    return PolicyApi(dio, serializers);
  }

  /// Get ProposalApi instance, base route and serializer can be overridden by a given but be careful,
  /// by doing that all interceptors will not be executed
  ProposalApi getProposalApi() {
    return ProposalApi(dio, serializers);
  }

  /// Get RatingApi instance, base route and serializer can be overridden by a given but be careful,
  /// by doing that all interceptors will not be executed
  RatingApi getRatingApi() {
    return RatingApi(dio, serializers);
  }

  /// Get VehicleApi instance, base route and serializer can be overridden by a given but be careful,
  /// by doing that all interceptors will not be executed
  VehicleApi getVehicleApi() {
    return VehicleApi(dio, serializers);
  }

  /// Get VerificationApi instance, base route and serializer can be overridden by a given but be careful,
  /// by doing that all interceptors will not be executed
  VerificationApi getVerificationApi() {
    return VerificationApi(dio, serializers);
  }
}
