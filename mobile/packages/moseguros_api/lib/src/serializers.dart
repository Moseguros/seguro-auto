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

import 'package:moseguros_api/src/model/accept_invitation_request.dart';
import 'package:moseguros_api/src/model/activate_invitation_request.dart';
import 'package:moseguros_api/src/model/assignable_roles_response.dart';
import 'package:moseguros_api/src/model/create_organization_request.dart';
import 'package:moseguros_api/src/model/delivery_receipt_request.dart';
import 'package:moseguros_api/src/model/field_error.dart';
import 'package:moseguros_api/src/model/insurer_profile_request.dart';
import 'package:moseguros_api/src/model/insurer_profile_response.dart';
import 'package:moseguros_api/src/model/invitation_details_response.dart';
import 'package:moseguros_api/src/model/invitation_organization.dart';
import 'package:moseguros_api/src/model/invitation_response.dart';
import 'package:moseguros_api/src/model/invite_user_request.dart';
import 'package:moseguros_api/src/model/jwk.dart';
import 'package:moseguros_api/src/model/jwks_response.dart';
import 'package:moseguros_api/src/model/logout_request.dart';
import 'package:moseguros_api/src/model/me_organization.dart';
import 'package:moseguros_api/src/model/me_response.dart';
import 'package:moseguros_api/src/model/money.dart';
import 'package:moseguros_api/src/model/organization_response.dart';
import 'package:moseguros_api/src/model/organization_summary_response.dart';
import 'package:moseguros_api/src/model/otp_challenge_response.dart';
import 'package:moseguros_api/src/model/otp_options_response.dart';
import 'package:moseguros_api/src/model/otp_request_response.dart';
import 'package:moseguros_api/src/model/otp_settings_response.dart';
import 'package:moseguros_api/src/model/otp_test_request.dart';
import 'package:moseguros_api/src/model/otp_test_response.dart';
import 'package:moseguros_api/src/model/otp_verify_response.dart';
import 'package:moseguros_api/src/model/page_response_invitation_response.dart';
import 'package:moseguros_api/src/model/page_response_organization_summary_response.dart';
import 'package:moseguros_api/src/model/problem_detail.dart';
import 'package:moseguros_api/src/model/refresh_token_request.dart';
import 'package:moseguros_api/src/model/request_otp_request.dart';
import 'package:moseguros_api/src/model/suspend_organization_request.dart';
import 'package:moseguros_api/src/model/token_response.dart';
import 'package:moseguros_api/src/model/totp_setup_response.dart';
import 'package:moseguros_api/src/model/update_organization_request.dart';
import 'package:moseguros_api/src/model/update_otp_settings_request.dart';
import 'package:moseguros_api/src/model/verify_otp_request.dart';
import 'package:moseguros_api/src/model/version_response.dart';

part 'serializers.g.dart';

@SerializersFor([
  AcceptInvitationRequest,
  ActivateInvitationRequest,
  AssignableRolesResponse,
  CreateOrganizationRequest,
  DeliveryReceiptRequest,
  FieldError,
  InsurerProfileRequest,
  InsurerProfileResponse,
  InvitationDetailsResponse,
  InvitationOrganization,
  InvitationResponse,
  InviteUserRequest,
  Jwk,
  JwksResponse,
  LogoutRequest,
  MeOrganization,
  MeResponse,
  Money,
  OrganizationResponse,
  OrganizationSummaryResponse,
  OtpChallengeResponse,
  OtpOptionsResponse,
  OtpRequestResponse,
  OtpSettingsResponse,
  OtpTestRequest,
  OtpTestResponse,
  OtpVerifyResponse,
  PageResponseInvitationResponse,
  PageResponseOrganizationSummaryResponse,
  ProblemDetail,
  RefreshTokenRequest,
  RequestOtpRequest,
  SuspendOrganizationRequest,
  TokenResponse,
  TotpSetupResponse,
  UpdateOrganizationRequest,
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
