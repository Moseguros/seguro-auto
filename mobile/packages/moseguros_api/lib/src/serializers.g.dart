// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'serializers.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

Serializers _$serializers = (Serializers().toBuilder()
      ..add(AcceptInvitationRequest.serializer)
      ..add(AcceptProposalRequest.serializer)
      ..add(AcceptanceMode.serializer)
      ..add(AcknowledgeSuitabilityRequest.serializer)
      ..add(ActivateInvitationRequest.serializer)
      ..add(AdminPaymentFileSource.serializer)
      ..add(AdminPaymentFileSourceFileFormatEnum.serializer)
      ..add(AdminPaymentFileSourceLastPollOutcomeEnum.serializer)
      ..add(AdminPaymentFileSources.serializer)
      ..add(ApproveInspectionRequest.serializer)
      ..add(AssignableRolesResponse.serializer)
      ..add(BrokerPolicyholderRequest.serializer)
      ..add(BrokerPolicyholderRequestIdDocTypeEnum.serializer)
      ..add(BrokerPolicyholderRequestProvinceEnum.serializer)
      ..add(BrokerProposalDriverRequest.serializer)
      ..add(BrokerProposalSummaryResponse.serializer)
      ..add(BrokerProposalVehicleRequest.serializer)
      ..add(BrokerProposalVehicleRequestProvinceOfCirculationEnum.serializer)
      ..add(BrokerVehicleRequest.serializer)
      ..add(BrokerVehicleRequestProvinceOfCirculationEnum.serializer)
      ..add(CatalogInsurerResponse.serializer)
      ..add(CatalogProductResponse.serializer)
      ..add(CatalogProductResponseStatusEnum.serializer)
      ..add(CatalogProductResponseTypeEnum.serializer)
      ..add(CertificateStatus.serializer)
      ..add(Charge.serializer)
      ..add(ChargeTypeEnum.serializer)
      ..add(ChargeValue.serializer)
      ..add(ConfirmSignatureRequest.serializer)
      ..add(ContactResponse.serializer)
      ..add(ContactResponseTypeEnum.serializer)
      ..add(ContactsResponse.serializer)
      ..add(CoverStatus.serializer)
      ..add(CoverageRequest.serializer)
      ..add(CoverageRequestCapitalTypeEnum.serializer)
      ..add(CoverageRequestCodeEnum.serializer)
      ..add(CoverageRequestDeductibleTypeEnum.serializer)
      ..add(CoverageRequestRequiresCodesEnum.serializer)
      ..add(CoverageResponse.serializer)
      ..add(CoverageResponseCapitalTypeEnum.serializer)
      ..add(CoverageResponseCodeEnum.serializer)
      ..add(CoverageResponseDeductibleTypeEnum.serializer)
      ..add(CoverageResponseRequiresCodesEnum.serializer)
      ..add(CoverageType.serializer)
      ..add(CreateBrokerProposalRequest.serializer)
      ..add(CreateBrokerQuoteRequest.serializer)
      ..add(CreateBrokerQuoteRequestProductTypeEnum.serializer)
      ..add(CreateDriverRequest.serializer)
      ..add(CreateOrganizationRequest.serializer)
      ..add(CreateOrganizationRequestProvinceEnum.serializer)
      ..add(CreateOrganizationRequestTypeEnum.serializer)
      ..add(CreatePlateFormatRequest.serializer)
      ..add(CreateProductRequest.serializer)
      ..add(CreateProductRequestTypeEnum.serializer)
      ..add(CreateProposalPaymentIntentRequest.serializer)
      ..add(CreateProposalPaymentIntentRequestMethodEnum.serializer)
      ..add(CreateProposalRequest.serializer)
      ..add(CreateQuoteRequest.serializer)
      ..add(CreateQuoteRequestProductTypeEnum.serializer)
      ..add(CreateReferenceValueRequest.serializer)
      ..add(CreateVehicleRequest.serializer)
      ..add(CreateVehicleRequestProvinceOfCirculationEnum.serializer)
      ..add(CustomerProfileResponse.serializer)
      ..add(CustomerProfileResponseIdDocTypeEnum.serializer)
      ..add(CustomerProfileResponseProvinceEnum.serializer)
      ..add(DeclarationAnswerRequest.serializer)
      ..add(DeclarationAnswerResponse.serializer)
      ..add(DeclarationQuestion.serializer)
      ..add(DeliveryReceiptRequest.serializer)
      ..add(DeliveryReceiptRequestStatusEnum.serializer)
      ..add(DevicePlatform.serializer)
      ..add(DeviceResponse.serializer)
      ..add(DriverResponse.serializer)
      ..add(FailureCode.serializer)
      ..add(FieldError.serializer)
      ..add(ForgotPasswordRequest.serializer)
      ..add(InfoRequestItem.serializer)
      ..add(InspectionMode.serializer)
      ..add(InspectionPhotoResponse.serializer)
      ..add(InspectionResponse.serializer)
      ..add(InspectionReview.serializer)
      ..add(InspectionStatus.serializer)
      ..add(InstalmentStatus.serializer)
      ..add(InsurerBrandingResponse.serializer)
      ..add(InsurerInspectionSummaryResponse.serializer)
      ..add(InsurerKycCheckResponse.serializer)
      ..add(InsurerPaymentBankReference.serializer)
      ..add(InsurerPaymentDetail.serializer)
      ..add(InsurerPaymentDetailMethodEnum.serializer)
      ..add(InsurerPaymentEvent.serializer)
      ..add(InsurerPaymentSummary.serializer)
      ..add(InsurerPaymentSummaryMethodEnum.serializer)
      ..add(InsurerPolicySummaryResponse.serializer)
      ..add(InsurerProfileRequest.serializer)
      ..add(InsurerProfileResponse.serializer)
      ..add(InsurerProposalDriver.serializer)
      ..add(InsurerProposalKyc.serializer)
      ..add(InsurerProposalResponse.serializer)
      ..add(InsurerProposalSignature.serializer)
      ..add(InsurerProposalSignatureChannelEnum.serializer)
      ..add(InsurerProposalSignatureInvalidatedReasonEnum.serializer)
      ..add(InsurerProposalSla.serializer)
      ..add(InsurerProposalSummaryResponse.serializer)
      ..add(InsurerProposalSummaryResponseProductTypeEnum.serializer)
      ..add(InsurerProposalVehicle.serializer)
      ..add(InsurerRatingSettingsResponse.serializer)
      ..add(InsurerRatingSettingsResponseRatingModeEnum.serializer)
      ..add(IntentView.serializer)
      ..add(IntentViewMethodEnum.serializer)
      ..add(InvitationDetailsResponse.serializer)
      ..add(InvitationOrganization.serializer)
      ..add(InvitationOrganizationTypeEnum.serializer)
      ..add(InvitationResponse.serializer)
      ..add(InvitationResponseStatusEnum.serializer)
      ..add(InviteUserRequest.serializer)
      ..add(Jwk.serializer)
      ..add(JwksResponse.serializer)
      ..add(KycCheckResponse.serializer)
      ..add(KycCheckResponseIdDocTypeEnum.serializer)
      ..add(KycCheckStatus.serializer)
      ..add(KycCheckSummaryResponse.serializer)
      ..add(KycCheckSummaryResponseIdDocTypeEnum.serializer)
      ..add(KycCustomerResponse.serializer)
      ..add(KycCustomerResponseIdDocTypeEnum.serializer)
      ..add(KycFilesResponse.serializer)
      ..add(KycPreviousCheckResponse.serializer)
      ..add(KycRejectionReason.serializer)
      ..add(KycState.serializer)
      ..add(LastErrorResponse.serializer)
      ..add(LastTestResponse.serializer)
      ..add(LastTestResponseResultEnum.serializer)
      ..add(LogoutRequest.serializer)
      ..add(MeOrganization.serializer)
      ..add(MeOrganizationTypeEnum.serializer)
      ..add(MeResponse.serializer)
      ..add(MeResponseTypeEnum.serializer)
      ..add(Money.serializer)
      ..add(MoneyCurrencyEnum.serializer)
      ..add(MyKycResponse.serializer)
      ..add(MyNotificationResponse.serializer)
      ..add(MyPolicySummaryResponse.serializer)
      ..add(NotificationCategory.serializer)
      ..add(NotificationCategoryPreferences.serializer)
      ..add(NotificationCategoryPreferencesChange.serializer)
      ..add(NotificationChannel.serializer)
      ..add(NotificationChannelPreference.serializer)
      ..add(NotificationChannelPreferenceChange.serializer)
      ..add(NotificationPreferencesResponse.serializer)
      ..add(NotificationTemplateDetail.serializer)
      ..add(NotificationTemplatePreview.serializer)
      ..add(NotificationTemplateSummary.serializer)
      ..add(NotificationTemplateTestResult.serializer)
      ..add(OrganizationResponse.serializer)
      ..add(OrganizationResponseProvinceEnum.serializer)
      ..add(OrganizationResponseStatusEnum.serializer)
      ..add(OrganizationResponseTypeEnum.serializer)
      ..add(OrganizationSummaryResponse.serializer)
      ..add(OrganizationSummaryResponseStatusEnum.serializer)
      ..add(OrganizationSummaryResponseTypeEnum.serializer)
      ..add(OtpChallengeResponse.serializer)
      ..add(OtpChallengeResponseChannelEnum.serializer)
      ..add(OtpChallengeResponseDeliveryStatusEnum.serializer)
      ..add(OtpOptionsResponse.serializer)
      ..add(OtpOptionsResponseDefaultChannelsEnum.serializer)
      ..add(OtpOptionsResponseIdentifierTypesEnum.serializer)
      ..add(OtpRequestResponse.serializer)
      ..add(OtpRequestResponseChannelEnum.serializer)
      ..add(OtpSettingsResponse.serializer)
      ..add(OtpSettingsResponseDefaultChannelEnum.serializer)
      ..add(OtpSettingsResponseEnabledChannelsEnum.serializer)
      ..add(OtpSettingsResponseFallbackOrderEnum.serializer)
      ..add(OtpTestRequest.serializer)
      ..add(OtpTestRequestChannelEnum.serializer)
      ..add(OtpTestRequestIdentifierTypeEnum.serializer)
      ..add(OtpTestResponse.serializer)
      ..add(OtpTestResponseChannelEnum.serializer)
      ..add(OtpTestResponseDeliveryStatusEnum.serializer)
      ..add(OtpVerifyResponse.serializer)
      ..add(PageResponseBrokerProposalSummaryResponse.serializer)
      ..add(PageResponseCatalogProductResponse.serializer)
      ..add(PageResponseDriverResponse.serializer)
      ..add(PageResponseInsurerInspectionSummaryResponse.serializer)
      ..add(PageResponseInsurerPaymentSummary.serializer)
      ..add(PageResponseInsurerPolicySummaryResponse.serializer)
      ..add(PageResponseInsurerProposalSummaryResponse.serializer)
      ..add(PageResponseIntentView.serializer)
      ..add(PageResponseInvitationResponse.serializer)
      ..add(PageResponseKycCheckSummaryResponse.serializer)
      ..add(PageResponseMyNotificationResponse.serializer)
      ..add(PageResponseMyPolicySummaryResponse.serializer)
      ..add(PageResponseNotificationTemplateSummary.serializer)
      ..add(PageResponseOrganizationSummaryResponse.serializer)
      ..add(PageResponsePaymentExceptionSummary.serializer)
      ..add(PageResponsePaymentFileImport.serializer)
      ..add(PageResponseProductSummaryResponse.serializer)
      ..add(PageResponseProposalSummaryResponse.serializer)
      ..add(PageResponseQuoteRequestSummaryResponse.serializer)
      ..add(PageResponseReferenceValueResponse.serializer)
      ..add(PageResponseStaffUserResponse.serializer)
      ..add(PageResponseVehicleResponse.serializer)
      ..add(PaymentConfirmedBy.serializer)
      ..add(PaymentEventType.serializer)
      ..add(PaymentExceptionCandidate.serializer)
      ..add(PaymentExceptionCandidates.serializer)
      ..add(PaymentExceptionDetail.serializer)
      ..add(PaymentExceptionDetailStatusEnum.serializer)
      ..add(PaymentExceptionIntent.serializer)
      ..add(PaymentExceptionSummary.serializer)
      ..add(PaymentExceptionSummaryStatusEnum.serializer)
      ..add(PaymentExceptionType.serializer)
      ..add(PaymentFileImport.serializer)
      ..add(PaymentFileImportFileFormatEnum.serializer)
      ..add(PaymentFileImportStatusEnum.serializer)
      ..add(PaymentFileSourceInsurer.serializer)
      ..add(PaymentFileSourceStatus.serializer)
      ..add(PaymentFileSourceStatusFileFormatEnum.serializer)
      ..add(PaymentFileSourceStatusLastPollOutcomeEnum.serializer)
      ..add(PaymentInsurerRef.serializer)
      ..add(PaymentLinkResponse.serializer)
      ..add(PaymentMethodOption.serializer)
      ..add(PaymentMethodOptionMethodEnum.serializer)
      ..add(PaymentMethodResponse.serializer)
      ..add(PaymentMethodResponseEnvironmentEnum.serializer)
      ..add(PaymentMethodResponseMethodEnum.serializer)
      ..add(PaymentMethodTestResponse.serializer)
      ..add(PaymentMethodTestResponseResultEnum.serializer)
      ..add(PaymentMethodVersionRequest.serializer)
      ..add(PaymentMethodsResponse.serializer)
      ..add(PaymentNextAction.serializer)
      ..add(PaymentNextActionType.serializer)
      ..add(PaymentPlan.serializer)
      ..add(PaymentPurpose.serializer)
      ..add(PaymentQueueStatus.serializer)
      ..add(PaymentResolution.serializer)
      ..add(PaymentStatus.serializer)
      ..add(PaymentTargetRef.serializer)
      ..add(PaymentTargetType.serializer)
      ..add(PaymentWorkerStatus.serializer)
      ..add(PhotoSlot.serializer)
      ..add(PhotoSource.serializer)
      ..add(PlateFormatResponse.serializer)
      ..add(PlateFormatsResponse.serializer)
      ..add(PlatformSettingResponse.serializer)
      ..add(PlatformSettingResponseValueTypeEnum.serializer)
      ..add(PlatformSettingsResponse.serializer)
      ..add(PolicyCertificate.serializer)
      ..add(PolicyCoverage.serializer)
      ..add(PolicyCoverageDeductibleTypeEnum.serializer)
      ..add(PolicyDetailResponse.serializer)
      ..add(PolicyDocument.serializer)
      ..add(PolicyDocumentStatus.serializer)
      ..add(PolicyDocumentType.serializer)
      ..add(PolicyEventType.serializer)
      ..add(PolicyInstalment.serializer)
      ..add(PolicyInsurerRef.serializer)
      ..add(PolicyProductRef.serializer)
      ..add(PolicyProductRefTypeEnum.serializer)
      ..add(PolicyProposalRef.serializer)
      ..add(PolicyStatus.serializer)
      ..add(PolicyTimelineEvent.serializer)
      ..add(PolicyTimelineEventActorTypeEnum.serializer)
      ..add(PolicyVehicleRef.serializer)
      ..add(PolicyholderSource.serializer)
      ..add(PreviewNotificationTemplateRequest.serializer)
      ..add(ProblemDetail.serializer)
      ..add(ProductDocumentResponse.serializer)
      ..add(ProductDocumentResponseDocTypeEnum.serializer)
      ..add(ProductDocumentResponseLanguageEnum.serializer)
      ..add(ProductResponse.serializer)
      ..add(ProductResponseStatusEnum.serializer)
      ..add(ProductResponseTypeEnum.serializer)
      ..add(ProductSummaryResponse.serializer)
      ..add(ProductSummaryResponseStatusEnum.serializer)
      ..add(ProductSummaryResponseTypeEnum.serializer)
      ..add(ProductVersionRefResponse.serializer)
      ..add(ProductVersionRefResponseStatusEnum.serializer)
      ..add(ProductVersionResponse.serializer)
      ..add(ProductVersionResponseProductTypeEnum.serializer)
      ..add(ProductVersionResponseStatusEnum.serializer)
      ..add(ProductVersionValidationResponse.serializer)
      ..add(ProposalChannel.serializer)
      ..add(ProposalCharge.serializer)
      ..add(ProposalChecklist.serializer)
      ..add(ProposalChecklistDeclarationsEnum.serializer)
      ..add(ProposalChecklistInspectionEnum.serializer)
      ..add(ProposalChecklistSignatureEnum.serializer)
      ..add(ProposalChecklistSuitabilityEnum.serializer)
      ..add(ProposalCoverage.serializer)
      ..add(ProposalCoverageCodeEnum.serializer)
      ..add(ProposalDecision.serializer)
      ..add(ProposalDeclarations.serializer)
      ..add(ProposalDeductible.serializer)
      ..add(ProposalDeductibleTypeEnum.serializer)
      ..add(ProposalDriver.serializer)
      ..add(ProposalInfoRequest.serializer)
      ..add(ProposalKyc.serializer)
      ..add(ProposalOrganization.serializer)
      ..add(ProposalPaymentChannel.serializer)
      ..add(ProposalPaymentMethodsResponse.serializer)
      ..add(ProposalPolicyholder.serializer)
      ..add(ProposalPremium.serializer)
      ..add(ProposalProduct.serializer)
      ..add(ProposalProductDocument.serializer)
      ..add(ProposalProductDocumentTypeEnum.serializer)
      ..add(ProposalProductTypeEnum.serializer)
      ..add(ProposalResponse.serializer)
      ..add(ProposalSignature.serializer)
      ..add(ProposalSignatureChannelEnum.serializer)
      ..add(ProposalStatus.serializer)
      ..add(ProposalSuitability.serializer)
      ..add(ProposalSummaryResponse.serializer)
      ..add(ProposalSummaryResponseProductTypeEnum.serializer)
      ..add(ProposalVehicle.serializer)
      ..add(PublicPlateFormatResponse.serializer)
      ..add(PublicPlateFormatsResponse.serializer)
      ..add(PublicPlatformSettingResponse.serializer)
      ..add(PublicPlatformSettingResponseValueTypeEnum.serializer)
      ..add(PublicPlatformSettingsResponse.serializer)
      ..add(PublicReferenceValueResponse.serializer)
      ..add(PublicReferenceValuesResponse.serializer)
      ..add(PublishProductVersionRequest.serializer)
      ..add(QuoteChargeResponse.serializer)
      ..add(QuoteCoverageResponse.serializer)
      ..add(QuoteCoverageResponseCodeEnum.serializer)
      ..add(QuoteDeductible.serializer)
      ..add(QuoteDeductibleTypeEnum.serializer)
      ..add(QuoteInsurer.serializer)
      ..add(QuoteProduct.serializer)
      ..add(QuoteProductTypeEnum.serializer)
      ..add(QuoteRequestResponse.serializer)
      ..add(QuoteRequestResponseOriginEnum.serializer)
      ..add(QuoteRequestResponseProductTypeEnum.serializer)
      ..add(QuoteRequestSummaryResponse.serializer)
      ..add(QuoteRequestSummaryResponseOriginEnum.serializer)
      ..add(QuoteRequestSummaryResponseProductTypeEnum.serializer)
      ..add(QuoteResponse.serializer)
      ..add(QuoteResponseStatusEnum.serializer)
      ..add(QuoteSummaryVehicle.serializer)
      ..add(QuotedDriver.serializer)
      ..add(QuotedVehicle.serializer)
      ..add(QuotedVehicleProvinceOfCirculationEnum.serializer)
      ..add(RatingCoverage.serializer)
      ..add(RatingCoverageCapitalTypeEnum.serializer)
      ..add(RatingCoverageCodeEnum.serializer)
      ..add(RatingIntegrationResponse.serializer)
      ..add(RatingIntegrationResponseRatingModeEnum.serializer)
      ..add(RatingRuleError.serializer)
      ..add(RatingRulesResponse.serializer)
      ..add(RatingRulesResponseProductTypeEnum.serializer)
      ..add(RatingRulesValidationResponse.serializer)
      ..add(RatingSimulationResponse.serializer)
      ..add(RatingSimulationResponseStatusEnum.serializer)
      ..add(ReferenceDocumentResponse.serializer)
      ..add(ReferenceSmsRequest.serializer)
      ..add(ReferenceSmsResponse.serializer)
      ..add(ReferenceStatus.serializer)
      ..add(ReferenceValueResponse.serializer)
      ..add(ReferenceValueResponseDomainEnum.serializer)
      ..add(RefreshTokenRequest.serializer)
      ..add(RegisterDeviceRequest.serializer)
      ..add(RegistrationDocResponse.serializer)
      ..add(RegistryLookupResponse.serializer)
      ..add(RejectInspectionRequest.serializer)
      ..add(RejectKycCheckRequest.serializer)
      ..add(RejectProposalRequest.serializer)
      ..add(RejectProposalRequestReasonCodeEnum.serializer)
      ..add(RejectionReasonCode.serializer)
      ..add(RenewalType.serializer)
      ..add(ReplaceCoveragesRequest.serializer)
      ..add(RequestOnSiteInspectionRequest.serializer)
      ..add(RequestOtpRequest.serializer)
      ..add(RequestOtpRequestChannelEnum.serializer)
      ..add(RequestOtpRequestIdentifierTypeEnum.serializer)
      ..add(RequestProposalInfoRequest.serializer)
      ..add(RequestSignatureRequest.serializer)
      ..add(RequestSignatureRequestChannelEnum.serializer)
      ..add(RequestedCoverageRequest.serializer)
      ..add(RequestedCoverageRequestCodeEnum.serializer)
      ..add(RequestedCoverageResponse.serializer)
      ..add(RequestedCoverageResponseCodeEnum.serializer)
      ..add(ResetPasswordRequest.serializer)
      ..add(ResolvePaymentExceptionRequest.serializer)
      ..add(ResolvePaymentExceptionRequestResolutionEnum.serializer)
      ..add(RiskDriverRequest.serializer)
      ..add(RuleError.serializer)
      ..add(SaveDeclarationsRequest.serializer)
      ..add(SaveSuitabilityRequest.serializer)
      ..add(SecretStatusResponse.serializer)
      ..add(SignatureRequestResponse.serializer)
      ..add(SignatureRequestResponseChannelEnum.serializer)
      ..add(SimulateRatingRequest.serializer)
      ..add(SimulationVehicleRequest.serializer)
      ..add(SimulationVehicleRequestProvinceOfCirculationEnum.serializer)
      ..add(SlaStatus.serializer)
      ..add(StaffLoginRequest.serializer)
      ..add(StaffUserResponse.serializer)
      ..add(StaffUserResponseStatusEnum.serializer)
      ..add(StartContactVerificationRequest.serializer)
      ..add(StartContactVerificationRequestChannelEnum.serializer)
      ..add(StartContactVerificationRequestTypeEnum.serializer)
      ..add(SubmitInspectionRequest.serializer)
      ..add(SubmitProposalRequest.serializer)
      ..add(SuitabilityAnswerRequest.serializer)
      ..add(SuitabilityAnswerResponse.serializer)
      ..add(SuitabilityQuestion.serializer)
      ..add(SuitabilityReason.serializer)
      ..add(SuitabilityResult.serializer)
      ..add(SuspendOrganizationRequest.serializer)
      ..add(TestNotificationTemplateRequest.serializer)
      ..add(TestPlateFormatRequest.serializer)
      ..add(TestPlateFormatResponse.serializer)
      ..add(TokenResponse.serializer)
      ..add(TotpSetupResponse.serializer)
      ..add(UnavailableReason.serializer)
      ..add(UnderwritingFailure.serializer)
      ..add(UnderwritingInputs.serializer)
      ..add(UnderwritingOutcome.serializer)
      ..add(UnderwritingResult.serializer)
      ..add(UnderwritingResultRulesSourceEnum.serializer)
      ..add(UnderwritingRulesResponse.serializer)
      ..add(UnreadCountResponse.serializer)
      ..add(UpdateBrokerProposalRequest.serializer)
      ..add(UpdateDriverRequest.serializer)
      ..add(UpdateInsurerRatingSettingsRequest.serializer)
      ..add(UpdateMeRequest.serializer)
      ..add(UpdateMeRequestPreferredLanguageEnum.serializer)
      ..add(UpdateMyProfileRequest.serializer)
      ..add(UpdateMyProfileRequestIdDocTypeEnum.serializer)
      ..add(UpdateMyProfileRequestProvinceEnum.serializer)
      ..add(UpdateNotificationPreferencesRequest.serializer)
      ..add(UpdateNotificationTemplateRequest.serializer)
      ..add(UpdateOrganizationRequest.serializer)
      ..add(UpdateOrganizationRequestProvinceEnum.serializer)
      ..add(UpdateOtpSettingsRequest.serializer)
      ..add(UpdateOtpSettingsRequestDefaultChannelEnum.serializer)
      ..add(UpdateOtpSettingsRequestEnabledChannelsEnum.serializer)
      ..add(UpdateOtpSettingsRequestFallbackOrderEnum.serializer)
      ..add(UpdatePaymentMethodRequest.serializer)
      ..add(UpdatePaymentMethodRequestEnvironmentEnum.serializer)
      ..add(UpdatePlateFormatRequest.serializer)
      ..add(UpdatePlatformSettingRequest.serializer)
      ..add(UpdateProductRequest.serializer)
      ..add(UpdateProductRequestTypeEnum.serializer)
      ..add(UpdateProductVersionRequest.serializer)
      ..add(UpdateProposalRequest.serializer)
      ..add(UpdateRatingIntegrationRequest.serializer)
      ..add(UpdateRatingIntegrationRequestRatingModeEnum.serializer)
      ..add(UpdateRatingRulesRequest.serializer)
      ..add(UpdateReferenceValueRequest.serializer)
      ..add(UpdateUnderwritingRulesRequest.serializer)
      ..add(UpdateUserRolesRequest.serializer)
      ..add(UpdateVehicleDriversRequest.serializer)
      ..add(UpdateVehicleRequest.serializer)
      ..add(UpdateVehicleRequestProvinceOfCirculationEnum.serializer)
      ..add(ValidateRatingRulesRequest.serializer)
      ..add(VehicleCategory.serializer)
      ..add(VehicleDriverAssignment.serializer)
      ..add(VehicleDriverResponse.serializer)
      ..add(VehicleResponse.serializer)
      ..add(VehicleResponseProvinceOfCirculationEnum.serializer)
      ..add(VehicleUsage.serializer)
      ..add(VerifyContactRequest.serializer)
      ..add(VerifyKycCheckRequest.serializer)
      ..add(VerifyOtpRequest.serializer)
      ..add(VersionResponse.serializer)
      ..addBuilderFactory(
          const FullType(
              BuiltList, const [const FullType(AdminPaymentFileSource)]),
          () => ListBuilder<AdminPaymentFileSource>())
      ..addBuilderFactory(
          const FullType(
              BuiltList, const [const FullType(BrokerProposalSummaryResponse)]),
          () => ListBuilder<BrokerProposalSummaryResponse>())
      ..addBuilderFactory(
          const FullType(
              BuiltList, const [const FullType(CatalogProductResponse)]),
          () => ListBuilder<CatalogProductResponse>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(Charge)]),
          () => ListBuilder<Charge>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(Charge)]),
          () => ListBuilder<Charge>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(Charge)]),
          () => ListBuilder<Charge>())
      ..addBuilderFactory(
          const FullType(
              BuiltList, const [const FullType(RequestedCoverageRequest)]),
          () => ListBuilder<RequestedCoverageRequest>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(ContactResponse)]),
          () => ListBuilder<ContactResponse>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(CoverageRequest)]),
          () => ListBuilder<CoverageRequest>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(CoverageResponse)]),
          () => ListBuilder<CoverageResponse>())
      ..addBuilderFactory(
          const FullType(
              BuiltList, const [const FullType(ProductDocumentResponse)]),
          () => ListBuilder<ProductDocumentResponse>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(VehicleUsage)]),
          () => ListBuilder<VehicleUsage>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(VehicleCategory)]),
          () => ListBuilder<VehicleCategory>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(CoverageResponse)]),
          () => ListBuilder<CoverageResponse>())
      ..addBuilderFactory(
          const FullType(
              BuiltList, const [const FullType(ProductDocumentResponse)]),
          () => ListBuilder<ProductDocumentResponse>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(VehicleUsage)]),
          () => ListBuilder<VehicleUsage>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(VehicleCategory)]),
          () => ListBuilder<VehicleCategory>())
      ..addBuilderFactory(
          const FullType(
              BuiltList, const [const FullType(DeclarationAnswerRequest)]),
          () => ListBuilder<DeclarationAnswerRequest>())
      ..addBuilderFactory(
          const FullType(
              BuiltList, const [const FullType(DeclarationAnswerResponse)]),
          () => ListBuilder<DeclarationAnswerResponse>())
      ..addBuilderFactory(
          const FullType(
              BuiltList, const [const FullType(DeclarationQuestion)]),
          () => ListBuilder<DeclarationQuestion>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(DriverResponse)]),
          () => ListBuilder<DriverResponse>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(FieldError)]),
          () => ListBuilder<FieldError>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(InfoRequestItem)]),
          () => ListBuilder<InfoRequestItem>())
      ..addBuilderFactory(
          const FullType(BuiltList,
              const [const FullType(InsurerInspectionSummaryResponse)]),
          () => ListBuilder<InsurerInspectionSummaryResponse>())
      ..addBuilderFactory(
          const FullType(
              BuiltList, const [const FullType(InsurerPaymentEvent)]),
          () => ListBuilder<InsurerPaymentEvent>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(String)]),
          () => ListBuilder<String>())
      ..addBuilderFactory(
          const FullType(
              BuiltList, const [const FullType(InsurerPaymentSummary)]),
          () => ListBuilder<InsurerPaymentSummary>())
      ..addBuilderFactory(
          const FullType(
              BuiltList, const [const FullType(InsurerPolicySummaryResponse)]),
          () => ListBuilder<InsurerPolicySummaryResponse>())
      ..addBuilderFactory(
          const FullType(BuiltList,
              const [const FullType(InsurerProposalSummaryResponse)]),
          () => ListBuilder<InsurerProposalSummaryResponse>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(IntentView)]),
          () => ListBuilder<IntentView>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(InvitationResponse)]),
          () => ListBuilder<InvitationResponse>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(Jwk)]),
          () => ListBuilder<Jwk>())
      ..addBuilderFactory(
          const FullType(
              BuiltList, const [const FullType(KycCheckSummaryResponse)]),
          () => ListBuilder<KycCheckSummaryResponse>())
      ..addBuilderFactory(
          const FullType(
              BuiltList, const [const FullType(KycPreviousCheckResponse)]),
          () => ListBuilder<KycPreviousCheckResponse>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(Money)]),
          () => ListBuilder<Money>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(Money)]),
          () => ListBuilder<Money>())
      ..addBuilderFactory(
          const FullType(BuiltList,
              const [const FullType(CoverageRequestRequiresCodesEnum)]),
          () => ListBuilder<CoverageRequestRequiresCodesEnum>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(String)]),
          () => ListBuilder<String>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(Money)]),
          () => ListBuilder<Money>())
      ..addBuilderFactory(
          const FullType(BuiltList,
              const [const FullType(CoverageResponseRequiresCodesEnum)]),
          () => ListBuilder<CoverageResponseRequiresCodesEnum>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(String)]),
          () => ListBuilder<String>())
      ..addBuilderFactory(
          const FullType(
              BuiltList, const [const FullType(MyNotificationResponse)]),
          () => ListBuilder<MyNotificationResponse>())
      ..addBuilderFactory(
          const FullType(
              BuiltList, const [const FullType(MyPolicySummaryResponse)]),
          () => ListBuilder<MyPolicySummaryResponse>())
      ..addBuilderFactory(
          const FullType(BuiltList,
              const [const FullType(NotificationCategoryPreferences)]),
          () => ListBuilder<NotificationCategoryPreferences>())
      ..addBuilderFactory(
          const FullType(BuiltList,
              const [const FullType(NotificationCategoryPreferencesChange)]),
          () => ListBuilder<NotificationCategoryPreferencesChange>())
      ..addBuilderFactory(
          const FullType(
              BuiltList, const [const FullType(NotificationChannelPreference)]),
          () => ListBuilder<NotificationChannelPreference>())
      ..addBuilderFactory(
          const FullType(BuiltList,
              const [const FullType(NotificationChannelPreferenceChange)]),
          () => ListBuilder<NotificationChannelPreferenceChange>())
      ..addBuilderFactory(
          const FullType(
              BuiltList, const [const FullType(NotificationTemplateSummary)]),
          () => ListBuilder<NotificationTemplateSummary>())
      ..addBuilderFactory(
          const FullType(
              BuiltList, const [const FullType(OrganizationSummaryResponse)]),
          () => ListBuilder<OrganizationSummaryResponse>())
      ..addBuilderFactory(
          const FullType(BuiltList,
              const [const FullType(OtpSettingsResponseEnabledChannelsEnum)]),
          () => ListBuilder<OtpSettingsResponseEnabledChannelsEnum>())
      ..addBuilderFactory(
          const FullType(BuiltList,
              const [const FullType(OtpSettingsResponseFallbackOrderEnum)]),
          () => ListBuilder<OtpSettingsResponseFallbackOrderEnum>())
      ..addBuilderFactory(
          const FullType(
              BuiltList, const [const FullType(PaymentExceptionCandidate)]),
          () => ListBuilder<PaymentExceptionCandidate>())
      ..addBuilderFactory(
          const FullType(
              BuiltList, const [const FullType(PaymentExceptionSummary)]),
          () => ListBuilder<PaymentExceptionSummary>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(PaymentFileImport)]),
          () => ListBuilder<PaymentFileImport>())
      ..addBuilderFactory(
          const FullType(
              BuiltList, const [const FullType(PaymentMethodOption)]),
          () => ListBuilder<PaymentMethodOption>())
      ..addBuilderFactory(
          const FullType(
              BuiltList, const [const FullType(PaymentMethodResponse)]),
          () => ListBuilder<PaymentMethodResponse>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(PaymentQueueStatus)]),
          () => ListBuilder<PaymentQueueStatus>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(PaymentResolution)]),
          () => ListBuilder<PaymentResolution>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(PhotoSlot)]),
          () => ListBuilder<PhotoSlot>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(PhotoSlot)]),
          () => ListBuilder<PhotoSlot>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(PhotoSlot)]),
          () => ListBuilder<PhotoSlot>())
      ..addBuilderFactory(
          const FullType(
              BuiltList, const [const FullType(InspectionPhotoResponse)]),
          () => ListBuilder<InspectionPhotoResponse>())
      ..addBuilderFactory(
          const FullType(
              BuiltList, const [const FullType(PlateFormatResponse)]),
          () => ListBuilder<PlateFormatResponse>())
      ..addBuilderFactory(
          const FullType(
              BuiltList, const [const FullType(PlatformSettingResponse)]),
          () => ListBuilder<PlatformSettingResponse>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(PolicyCoverage)]),
          () => ListBuilder<PolicyCoverage>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(PolicyDocument)]),
          () => ListBuilder<PolicyDocument>())
      ..addBuilderFactory(
          const FullType(
              BuiltList, const [const FullType(PolicyTimelineEvent)]),
          () => ListBuilder<PolicyTimelineEvent>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(PolicyInstalment)]),
          () => ListBuilder<PolicyInstalment>())
      ..addBuilderFactory(
          const FullType(
              BuiltList, const [const FullType(ProductSummaryResponse)]),
          () => ListBuilder<ProductSummaryResponse>())
      ..addBuilderFactory(
          const FullType(
              BuiltList, const [const FullType(ProductVersionRefResponse)]),
          () => ListBuilder<ProductVersionRefResponse>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(ProposalCharge)]),
          () => ListBuilder<ProposalCharge>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(ProposalCoverage)]),
          () => ListBuilder<ProposalCoverage>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(ProposalCoverage)]),
          () => ListBuilder<ProposalCoverage>())
      ..addBuilderFactory(
          const FullType(
              BuiltList, const [const FullType(InsurerProposalSignature)]),
          () => ListBuilder<InsurerProposalSignature>())
      ..addBuilderFactory(
          const FullType(
              BuiltList, const [const FullType(ProposalProductDocument)]),
          () => ListBuilder<ProposalProductDocument>())
      ..addBuilderFactory(
          const FullType(
              BuiltList, const [const FullType(ProposalSummaryResponse)]),
          () => ListBuilder<ProposalSummaryResponse>())
      ..addBuilderFactory(
          const FullType(
              BuiltList, const [const FullType(PublicPlateFormatResponse)]),
          () => ListBuilder<PublicPlateFormatResponse>())
      ..addBuilderFactory(
          const FullType(
              BuiltList, const [const FullType(PublicPlatformSettingResponse)]),
          () => ListBuilder<PublicPlatformSettingResponse>())
      ..addBuilderFactory(
          const FullType(
              BuiltList, const [const FullType(PublicReferenceValueResponse)]),
          () => ListBuilder<PublicReferenceValueResponse>())
      ..addBuilderFactory(
          const FullType(
              BuiltList, const [const FullType(QuoteChargeResponse)]),
          () => ListBuilder<QuoteChargeResponse>())
      ..addBuilderFactory(
          const FullType(
              BuiltList, const [const FullType(QuoteCoverageResponse)]),
          () => ListBuilder<QuoteCoverageResponse>())
      ..addBuilderFactory(
          const FullType(BuiltMap, const [
            const FullType(String),
            const FullType.nullable(JsonObject)
          ]),
          () => MapBuilder<String, JsonObject?>())
      ..addBuilderFactory(
          const FullType(
              BuiltList, const [const FullType(QuoteRequestSummaryResponse)]),
          () => ListBuilder<QuoteRequestSummaryResponse>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(QuoteResponse)]),
          () => ListBuilder<QuoteResponse>())
      ..addBuilderFactory(
          const FullType(
              BuiltList, const [const FullType(RequestedCoverageResponse)]),
          () => ListBuilder<RequestedCoverageResponse>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(RatingCoverage)]),
          () => ListBuilder<RatingCoverage>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(RatingRuleError)]),
          () => ListBuilder<RatingRuleError>())
      ..addBuilderFactory(
          const FullType(
              BuiltList, const [const FullType(ReferenceValueResponse)]),
          () => ListBuilder<ReferenceValueResponse>())
      ..addBuilderFactory(
          const FullType(
              BuiltList, const [const FullType(RequestedCoverageRequest)]),
          () => ListBuilder<RequestedCoverageRequest>())
      ..addBuilderFactory(
          const FullType(
              BuiltList, const [const FullType(RequestedCoverageRequest)]),
          () => ListBuilder<RequestedCoverageRequest>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(RuleError)]),
          () => ListBuilder<RuleError>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(StaffUserResponse)]),
          () => ListBuilder<StaffUserResponse>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(String)]),
          () => ListBuilder<String>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(String)]),
          () => ListBuilder<String>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(String)]),
          () => ListBuilder<String>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(String)]),
          () => ListBuilder<String>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(String)]),
          () => ListBuilder<String>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(String)]),
          () => ListBuilder<String>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(String)]),
          () => ListBuilder<String>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(String)]),
          () => ListBuilder<String>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(String)]),
          () => ListBuilder<String>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(String)]),
          () => ListBuilder<String>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(String)]),
          () => ListBuilder<String>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(String)]),
          () => ListBuilder<String>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(String)]),
          () => ListBuilder<String>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(String)]),
          () => ListBuilder<String>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(String)]),
          () => ListBuilder<String>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(String)]),
          () => ListBuilder<String>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(String)]),
          () => ListBuilder<String>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(String)]),
          () => ListBuilder<String>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(String)]),
          () => ListBuilder<String>())
      ..addBuilderFactory(
          const FullType(
              BuiltList, const [const FullType(SuitabilityAnswerRequest)]),
          () => ListBuilder<SuitabilityAnswerRequest>())
      ..addBuilderFactory(
          const FullType(
              BuiltList, const [const FullType(SuitabilityAnswerResponse)]),
          () => ListBuilder<SuitabilityAnswerResponse>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(SuitabilityReason)]),
          () => ListBuilder<SuitabilityReason>())
      ..addBuilderFactory(
          const FullType(
              BuiltList, const [const FullType(UnderwritingFailure)]),
          () => ListBuilder<UnderwritingFailure>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [
            const FullType(UpdateOtpSettingsRequestEnabledChannelsEnum)
          ]),
          () => ListBuilder<UpdateOtpSettingsRequestEnabledChannelsEnum>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [
            const FullType(UpdateOtpSettingsRequestFallbackOrderEnum)
          ]),
          () => ListBuilder<UpdateOtpSettingsRequestFallbackOrderEnum>())
      ..addBuilderFactory(
          const FullType(
              BuiltList, const [const FullType(VehicleDriverAssignment)]),
          () => ListBuilder<VehicleDriverAssignment>())
      ..addBuilderFactory(
          const FullType(
              BuiltList, const [const FullType(VehicleDriverResponse)]),
          () => ListBuilder<VehicleDriverResponse>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(VehicleResponse)]),
          () => ListBuilder<VehicleResponse>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(VehicleUsage)]),
          () => ListBuilder<VehicleUsage>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(VehicleCategory)]),
          () => ListBuilder<VehicleCategory>())
      ..addBuilderFactory(
          const FullType(BuiltMap, const [
            const FullType(String),
            const FullType(BuiltList, const [const FullType(InnerEnum)])
          ]),
          () => MapBuilder<String, BuiltList<InnerEnum>>())
      ..addBuilderFactory(
          const FullType(BuiltMap, const [
            const FullType(String),
            const FullType(OtpOptionsResponseDefaultChannelsEnum)
          ]),
          () => MapBuilder<String, OtpOptionsResponseDefaultChannelsEnum>())
      ..addBuilderFactory(
          const FullType(BuiltList,
              const [const FullType(OtpOptionsResponseIdentifierTypesEnum)]),
          () => ListBuilder<OtpOptionsResponseIdentifierTypesEnum>())
      ..addBuilderFactory(
          const FullType(BuiltMap, const [
            const FullType(String),
            const FullType(SecretStatusResponse)
          ]),
          () => MapBuilder<String, SecretStatusResponse>())
      ..addBuilderFactory(
          const FullType(
              BuiltMap, const [const FullType(String), const FullType(String)]),
          () => MapBuilder<String, String>())
      ..addBuilderFactory(
          const FullType(
              BuiltMap, const [const FullType(String), const FullType(String)]),
          () => MapBuilder<String, String>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(String)]),
          () => ListBuilder<String>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(String)]),
          () => ListBuilder<String>())
      ..addBuilderFactory(
          const FullType(BuiltMap, const [
            const FullType(String),
            const FullType.nullable(JsonObject)
          ]),
          () => MapBuilder<String, JsonObject?>())
      ..addBuilderFactory(
          const FullType(BuiltMap, const [
            const FullType(String),
            const FullType.nullable(JsonObject)
          ]),
          () => MapBuilder<String, JsonObject?>())
      ..addBuilderFactory(
          const FullType(BuiltMap,
              const [const FullType(String), const FullType.nullable(String)]),
          () => MapBuilder<String, String?>()))
    .build();

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
