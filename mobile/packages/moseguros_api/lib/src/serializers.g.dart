// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'serializers.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

Serializers _$serializers = (Serializers().toBuilder()
      ..add(AcceptInvitationRequest.serializer)
      ..add(AcknowledgeSuitabilityRequest.serializer)
      ..add(ActivateInvitationRequest.serializer)
      ..add(AssignableRolesResponse.serializer)
      ..add(BrokerVehicleRequest.serializer)
      ..add(BrokerVehicleRequestProvinceOfCirculationEnum.serializer)
      ..add(CatalogInsurerResponse.serializer)
      ..add(CatalogProductResponse.serializer)
      ..add(CatalogProductResponseStatusEnum.serializer)
      ..add(CatalogProductResponseTypeEnum.serializer)
      ..add(Charge.serializer)
      ..add(ChargeTypeEnum.serializer)
      ..add(ChargeValue.serializer)
      ..add(ConfirmSignatureRequest.serializer)
      ..add(ContactResponse.serializer)
      ..add(ContactResponseTypeEnum.serializer)
      ..add(ContactsResponse.serializer)
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
      ..add(CreateBrokerQuoteRequest.serializer)
      ..add(CreateBrokerQuoteRequestProductTypeEnum.serializer)
      ..add(CreateDriverRequest.serializer)
      ..add(CreateOrganizationRequest.serializer)
      ..add(CreateOrganizationRequestProvinceEnum.serializer)
      ..add(CreateOrganizationRequestTypeEnum.serializer)
      ..add(CreatePlateFormatRequest.serializer)
      ..add(CreateProductRequest.serializer)
      ..add(CreateProductRequestTypeEnum.serializer)
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
      ..add(DriverResponse.serializer)
      ..add(FieldError.serializer)
      ..add(ForgotPasswordRequest.serializer)
      ..add(InsurerBrandingResponse.serializer)
      ..add(InsurerKycCheckResponse.serializer)
      ..add(InsurerProfileRequest.serializer)
      ..add(InsurerProfileResponse.serializer)
      ..add(InsurerRatingSettingsResponse.serializer)
      ..add(InsurerRatingSettingsResponseRatingModeEnum.serializer)
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
      ..add(LogoutRequest.serializer)
      ..add(MeOrganization.serializer)
      ..add(MeOrganizationTypeEnum.serializer)
      ..add(MeResponse.serializer)
      ..add(MeResponseTypeEnum.serializer)
      ..add(Money.serializer)
      ..add(MoneyCurrencyEnum.serializer)
      ..add(MyKycResponse.serializer)
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
      ..add(PageResponseCatalogProductResponse.serializer)
      ..add(PageResponseDriverResponse.serializer)
      ..add(PageResponseInvitationResponse.serializer)
      ..add(PageResponseKycCheckSummaryResponse.serializer)
      ..add(PageResponseOrganizationSummaryResponse.serializer)
      ..add(PageResponseProductSummaryResponse.serializer)
      ..add(PageResponseProposalSummaryResponse.serializer)
      ..add(PageResponseQuoteRequestSummaryResponse.serializer)
      ..add(PageResponseReferenceValueResponse.serializer)
      ..add(PageResponseStaffUserResponse.serializer)
      ..add(PageResponseVehicleResponse.serializer)
      ..add(PlateFormatResponse.serializer)
      ..add(PlateFormatsResponse.serializer)
      ..add(PlatformSettingResponse.serializer)
      ..add(PlatformSettingResponseValueTypeEnum.serializer)
      ..add(PlatformSettingsResponse.serializer)
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
      ..add(ProposalDeclarations.serializer)
      ..add(ProposalDeductible.serializer)
      ..add(ProposalDeductibleTypeEnum.serializer)
      ..add(ProposalDriver.serializer)
      ..add(ProposalInspection.serializer)
      ..add(ProposalKyc.serializer)
      ..add(ProposalOrganization.serializer)
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
      ..add(ReferenceValueResponse.serializer)
      ..add(ReferenceValueResponseDomainEnum.serializer)
      ..add(RefreshTokenRequest.serializer)
      ..add(RegistrationDocResponse.serializer)
      ..add(RegistryLookupResponse.serializer)
      ..add(RejectKycCheckRequest.serializer)
      ..add(ReplaceCoveragesRequest.serializer)
      ..add(RequestOtpRequest.serializer)
      ..add(RequestOtpRequestChannelEnum.serializer)
      ..add(RequestOtpRequestIdentifierTypeEnum.serializer)
      ..add(RequestSignatureRequest.serializer)
      ..add(RequestSignatureRequestChannelEnum.serializer)
      ..add(RequestedCoverageRequest.serializer)
      ..add(RequestedCoverageRequestCodeEnum.serializer)
      ..add(RequestedCoverageResponse.serializer)
      ..add(RequestedCoverageResponseCodeEnum.serializer)
      ..add(ResetPasswordRequest.serializer)
      ..add(RiskDriverRequest.serializer)
      ..add(RuleError.serializer)
      ..add(SaveDeclarationsRequest.serializer)
      ..add(SaveSuitabilityRequest.serializer)
      ..add(SignatureRequestResponse.serializer)
      ..add(SignatureRequestResponseChannelEnum.serializer)
      ..add(SimulateRatingRequest.serializer)
      ..add(SimulationVehicleRequest.serializer)
      ..add(SimulationVehicleRequestProvinceOfCirculationEnum.serializer)
      ..add(StaffLoginRequest.serializer)
      ..add(StaffUserResponse.serializer)
      ..add(StaffUserResponseStatusEnum.serializer)
      ..add(StartContactVerificationRequest.serializer)
      ..add(StartContactVerificationRequestChannelEnum.serializer)
      ..add(StartContactVerificationRequestTypeEnum.serializer)
      ..add(SuitabilityAnswerRequest.serializer)
      ..add(SuitabilityAnswerResponse.serializer)
      ..add(SuitabilityQuestion.serializer)
      ..add(SuitabilityReason.serializer)
      ..add(SuitabilityResult.serializer)
      ..add(SuspendOrganizationRequest.serializer)
      ..add(TestPlateFormatRequest.serializer)
      ..add(TestPlateFormatResponse.serializer)
      ..add(TokenResponse.serializer)
      ..add(TotpSetupResponse.serializer)
      ..add(UnavailableReason.serializer)
      ..add(UpdateDriverRequest.serializer)
      ..add(UpdateInsurerRatingSettingsRequest.serializer)
      ..add(UpdateMeRequest.serializer)
      ..add(UpdateMeRequestPreferredLanguageEnum.serializer)
      ..add(UpdateMyProfileRequest.serializer)
      ..add(UpdateMyProfileRequestIdDocTypeEnum.serializer)
      ..add(UpdateMyProfileRequestProvinceEnum.serializer)
      ..add(UpdateOrganizationRequest.serializer)
      ..add(UpdateOrganizationRequestProvinceEnum.serializer)
      ..add(UpdateOtpSettingsRequest.serializer)
      ..add(UpdateOtpSettingsRequestDefaultChannelEnum.serializer)
      ..add(UpdateOtpSettingsRequestEnabledChannelsEnum.serializer)
      ..add(UpdateOtpSettingsRequestFallbackOrderEnum.serializer)
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
          const FullType(BuiltList, const [const FullType(DriverResponse)]),
          () => ListBuilder<DriverResponse>())
      ..addBuilderFactory(
          const FullType(BuiltList, const [const FullType(FieldError)]),
          () => ListBuilder<FieldError>())
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
              BuiltList, const [const FullType(PlateFormatResponse)]),
          () => ListBuilder<PlateFormatResponse>())
      ..addBuilderFactory(
          const FullType(
              BuiltList, const [const FullType(PlatformSettingResponse)]),
          () => ListBuilder<PlatformSettingResponse>())
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
          const FullType(BuiltMap, const [
            const FullType(String),
            const FullType.nullable(JsonObject)
          ]),
          () => MapBuilder<String, JsonObject?>())
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
            const FullType.nullable(JsonObject)
          ]),
          () => MapBuilder<String, JsonObject?>())
      ..addBuilderFactory(
          const FullType(BuiltMap, const [
            const FullType(String),
            const FullType.nullable(JsonObject)
          ]),
          () => MapBuilder<String, JsonObject?>()))
    .build();

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
