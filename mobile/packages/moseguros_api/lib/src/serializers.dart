//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_import

import 'package:one_of_serializer/any_of_serializer.dart';
import 'package:one_of_serializer/one_of_serializer.dart';
import 'package:built_collection/built_collection.dart';
import 'package:built_value/json_object.dart';
import 'package:built_value/serializer.dart';
import 'package:built_value/standard_json_plugin.dart';
import 'package:built_value/iso_8601_date_time_serializer.dart';
import 'package:moseguros_api/src/date_serializer.dart';
import 'package:moseguros_api/src/model/date.dart';

import 'package:moseguros_api/src/model/delivery_receipt_request.dart';
import 'package:moseguros_api/src/model/field_error.dart';
import 'package:moseguros_api/src/model/jwk.dart';
import 'package:moseguros_api/src/model/jwks_response.dart';
import 'package:moseguros_api/src/model/logout_request.dart';
import 'package:moseguros_api/src/model/me_organization.dart';
import 'package:moseguros_api/src/model/me_response.dart';
import 'package:moseguros_api/src/model/money.dart';
import 'package:moseguros_api/src/model/otp_challenge_response.dart';
import 'package:moseguros_api/src/model/otp_options_response.dart';
import 'package:moseguros_api/src/model/otp_request_response.dart';
import 'package:moseguros_api/src/model/otp_settings_response.dart';
import 'package:moseguros_api/src/model/otp_test_request.dart';
import 'package:moseguros_api/src/model/otp_test_response.dart';
import 'package:moseguros_api/src/model/otp_verify_response.dart';
import 'package:moseguros_api/src/model/problem_detail.dart';
import 'package:moseguros_api/src/model/refresh_token_request.dart';
import 'package:moseguros_api/src/model/request_otp_request.dart';
import 'package:moseguros_api/src/model/token_response.dart';
import 'package:moseguros_api/src/model/update_otp_settings_request.dart';
import 'package:moseguros_api/src/model/verify_otp_request.dart';
import 'package:moseguros_api/src/model/version_response.dart';

part 'serializers.g.dart';

@SerializersFor([
  DeliveryReceiptRequest,
  FieldError,
  Jwk,
  JwksResponse,
  LogoutRequest,
  MeOrganization,
  MeResponse,
  Money,
  OtpChallengeResponse,
  OtpOptionsResponse,
  OtpRequestResponse,
  OtpSettingsResponse,
  OtpTestRequest,
  OtpTestResponse,
  OtpVerifyResponse,
  ProblemDetail,
  RefreshTokenRequest,
  RequestOtpRequest,
  TokenResponse,
  UpdateOtpSettingsRequest,
  VerifyOtpRequest,
  VersionResponse,
])
Serializers serializers = (_$serializers.toBuilder()
      ..add(const OneOfSerializer())
      ..add(const AnyOfSerializer())
      ..add(const DateSerializer())
      ..add(Iso8601DateTimeSerializer()))
    .build();

Serializers standardSerializers =
    (serializers.toBuilder()..addPlugin(StandardJsonPlugin())).build();
