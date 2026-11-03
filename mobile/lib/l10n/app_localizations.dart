import 'dart:async';

import 'package:flutter/foundation.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:intl/intl.dart' as intl;

import 'app_localizations_en.dart';
import 'app_localizations_pt.dart';

// ignore_for_file: type=lint

/// Callers can lookup localized strings with an instance of AppLocalizations
/// returned by `AppLocalizations.of(context)`.
///
/// Applications need to include `AppLocalizations.delegate()` in their app's
/// `localizationDelegates` list, and the locales they support in the app's
/// `supportedLocales` list. For example:
///
/// ```dart
/// import 'l10n/app_localizations.dart';
///
/// return MaterialApp(
///   localizationsDelegates: AppLocalizations.localizationsDelegates,
///   supportedLocales: AppLocalizations.supportedLocales,
///   home: MyApplicationHome(),
/// );
/// ```
///
/// ## Update pubspec.yaml
///
/// Please make sure to update your pubspec.yaml to include the following
/// packages:
///
/// ```yaml
/// dependencies:
///   # Internationalization support.
///   flutter_localizations:
///     sdk: flutter
///   intl: any # Use the pinned version from flutter_localizations
///
///   # Rest of dependencies
/// ```
///
/// ## iOS Applications
///
/// iOS applications define key application metadata, including supported
/// locales, in an Info.plist file that is built into the application bundle.
/// To configure the locales supported by your app, you’ll need to edit this
/// file.
///
/// First, open your project’s ios/Runner.xcworkspace Xcode workspace file.
/// Then, in the Project Navigator, open the Info.plist file under the Runner
/// project’s Runner folder.
///
/// Next, select the Information Property List item, select Add Item from the
/// Editor menu, then select Localizations from the pop-up menu.
///
/// Select and expand the newly-created Localizations item then, for each
/// locale your application supports, add a new item and select the locale
/// you wish to add from the pop-up menu in the Value field. This list should
/// be consistent with the languages listed in the AppLocalizations.supportedLocales
/// property.
abstract class AppLocalizations {
  AppLocalizations(String locale)
    : localeName = intl.Intl.canonicalizedLocale(locale.toString());

  final String localeName;

  static AppLocalizations of(BuildContext context) {
    return Localizations.of<AppLocalizations>(context, AppLocalizations)!;
  }

  static const LocalizationsDelegate<AppLocalizations> delegate =
      _AppLocalizationsDelegate();

  /// A list of this localizations delegate along with the default localizations
  /// delegates.
  ///
  /// Returns a list of localizations delegates containing this delegate along with
  /// GlobalMaterialLocalizations.delegate, GlobalCupertinoLocalizations.delegate,
  /// and GlobalWidgetsLocalizations.delegate.
  ///
  /// Additional delegates can be added by appending to this list in
  /// MaterialApp. This list does not have to be used at all if a custom list
  /// of delegates is preferred or required.
  static const List<LocalizationsDelegate<dynamic>> localizationsDelegates =
      <LocalizationsDelegate<dynamic>>[
        delegate,
        GlobalMaterialLocalizations.delegate,
        GlobalCupertinoLocalizations.delegate,
        GlobalWidgetsLocalizations.delegate,
      ];

  /// A list of this localizations delegate's supported locales.
  static const List<Locale> supportedLocales = <Locale>[
    Locale('en'),
    Locale('pt'),
  ];

  /// Nome comercial da aplicação (não se traduz).
  ///
  /// In pt, this message translates to:
  /// **'MoSeguros'**
  String get appName;

  /// No description provided for @systemStatusLoading.
  ///
  /// In pt, this message translates to:
  /// **'A verificar a API…'**
  String get systemStatusLoading;

  /// No description provided for @systemStatusVersion.
  ///
  /// In pt, this message translates to:
  /// **'Versão'**
  String get systemStatusVersion;

  /// No description provided for @systemStatusCommit.
  ///
  /// In pt, this message translates to:
  /// **'Commit'**
  String get systemStatusCommit;

  /// No description provided for @systemStatusUnknown.
  ///
  /// In pt, this message translates to:
  /// **'desconhecido'**
  String get systemStatusUnknown;

  /// No description provided for @systemStatusError.
  ///
  /// In pt, this message translates to:
  /// **'Não foi possível obter o estado da API.'**
  String get systemStatusError;

  /// No description provided for @retry.
  ///
  /// In pt, this message translates to:
  /// **'Tentar de novo'**
  String get retry;

  /// No description provided for @notFoundTitle.
  ///
  /// In pt, this message translates to:
  /// **'Página não encontrada'**
  String get notFoundTitle;

  /// No description provided for @notFoundDescription.
  ///
  /// In pt, this message translates to:
  /// **'O endereço que procura não existe.'**
  String get notFoundDescription;

  /// No description provided for @backHome.
  ///
  /// In pt, this message translates to:
  /// **'Voltar ao início'**
  String get backHome;

  /// errors.NETWORK_ERROR
  ///
  /// In pt, this message translates to:
  /// **'Sem ligação ao servidor. Verifique a sua ligação à Internet.'**
  String get errorNetworkError;

  /// No description provided for @errorUnknown.
  ///
  /// In pt, this message translates to:
  /// **'Ocorreu um erro inesperado.'**
  String get errorUnknown;

  /// No description provided for @errorValidationError.
  ///
  /// In pt, this message translates to:
  /// **'Os dados enviados não são válidos.'**
  String get errorValidationError;

  /// No description provided for @errorNotFound.
  ///
  /// In pt, this message translates to:
  /// **'O recurso pedido não existe.'**
  String get errorNotFound;

  /// No description provided for @errorUnauthorized.
  ///
  /// In pt, this message translates to:
  /// **'Precisa de iniciar sessão.'**
  String get errorUnauthorized;

  /// No description provided for @errorForbidden.
  ///
  /// In pt, this message translates to:
  /// **'Não tem permissão para esta operação.'**
  String get errorForbidden;

  /// No description provided for @errorInternalError.
  ///
  /// In pt, this message translates to:
  /// **'Erro interno do servidor. Tente mais tarde.'**
  String get errorInternalError;

  /// No description provided for @errorInvalidCurrency.
  ///
  /// In pt, this message translates to:
  /// **'Moeda inválida.'**
  String get errorInvalidCurrency;

  /// No description provided for @errorInvalidMsisdn.
  ///
  /// In pt, this message translates to:
  /// **'Número de telemóvel inválido.'**
  String get errorInvalidMsisdn;

  /// No description provided for @errorInvalidNuit.
  ///
  /// In pt, this message translates to:
  /// **'NUIT inválido.'**
  String get errorInvalidNuit;

  /// No description provided for @errorInvalidPlate.
  ///
  /// In pt, this message translates to:
  /// **'Matrícula inválida.'**
  String get errorInvalidPlate;

  /// No description provided for @validationInvalidCurrency.
  ///
  /// In pt, this message translates to:
  /// **'Moeda inválida.'**
  String get validationInvalidCurrency;

  /// No description provided for @validationInvalidMsisdn.
  ///
  /// In pt, this message translates to:
  /// **'Número de telemóvel inválido (ex.: +258 84 123 4567).'**
  String get validationInvalidMsisdn;

  /// No description provided for @validationInvalidNuit.
  ///
  /// In pt, this message translates to:
  /// **'O NUIT tem 9 dígitos.'**
  String get validationInvalidNuit;

  /// No description provided for @validationInvalidPlate.
  ///
  /// In pt, this message translates to:
  /// **'Matrícula inválida.'**
  String get validationInvalidPlate;

  /// No description provided for @ctaQuote.
  ///
  /// In pt, this message translates to:
  /// **'Simular seguro'**
  String get ctaQuote;

  /// No description provided for @ctaCompare.
  ///
  /// In pt, this message translates to:
  /// **'Comparar propostas'**
  String get ctaCompare;

  /// No description provided for @ctaBuy.
  ///
  /// In pt, this message translates to:
  /// **'Adquirir apólice'**
  String get ctaBuy;

  /// No description provided for @ctaRenew.
  ///
  /// In pt, this message translates to:
  /// **'Renovar seguro'**
  String get ctaRenew;

  /// No description provided for @errorInvalidEmail.
  ///
  /// In pt, this message translates to:
  /// **'Endereço de e-mail inválido.'**
  String get errorInvalidEmail;

  /// No description provided for @errorInvalidPhone.
  ///
  /// In pt, this message translates to:
  /// **'Número de telefone inválido.'**
  String get errorInvalidPhone;

  /// No description provided for @errorTenantRequired.
  ///
  /// In pt, this message translates to:
  /// **'Erro interno do servidor. Tente mais tarde.'**
  String get errorTenantRequired;

  /// No description provided for @errorDecryptionFailed.
  ///
  /// In pt, this message translates to:
  /// **'Erro interno do servidor. Tente mais tarde.'**
  String get errorDecryptionFailed;

  /// No description provided for @errorInvalidStateTransition.
  ///
  /// In pt, this message translates to:
  /// **'Esta operação não é possível no estado actual.'**
  String get errorInvalidStateTransition;

  /// No description provided for @errorLicenseNumberRequired.
  ///
  /// In pt, this message translates to:
  /// **'O número de autorização da ASFPM é obrigatório para seguradoras, corretores e agentes.'**
  String get errorLicenseNumberRequired;

  /// No description provided for @errorChannelsEmpty.
  ///
  /// In pt, this message translates to:
  /// **'Escolha pelo menos um canal.'**
  String get errorChannelsEmpty;

  /// No description provided for @errorChannelsDuplicated.
  ///
  /// In pt, this message translates to:
  /// **'Cada canal só pode aparecer uma vez.'**
  String get errorChannelsDuplicated;

  /// No description provided for @errorDefaultChannelNotEnabled.
  ///
  /// In pt, this message translates to:
  /// **'O canal por defeito tem de estar activo.'**
  String get errorDefaultChannelNotEnabled;

  /// No description provided for @errorFallbackOrderMismatch.
  ///
  /// In pt, this message translates to:
  /// **'A ordem alternativa tem de ter exactamente os canais activos.'**
  String get errorFallbackOrderMismatch;

  /// No description provided for @errorOutOfRange.
  ///
  /// In pt, this message translates to:
  /// **'Valor fora dos limites permitidos.'**
  String get errorOutOfRange;

  /// No description provided for @validationInvalidEmail.
  ///
  /// In pt, this message translates to:
  /// **'E-mail inválido (ex.: nome@exemplo.co.mz).'**
  String get validationInvalidEmail;

  /// No description provided for @validationInvalidPhone.
  ///
  /// In pt, this message translates to:
  /// **'Telefone inválido (9 a 15 dígitos).'**
  String get validationInvalidPhone;

  /// No description provided for @validationLicenseNumberRequired.
  ///
  /// In pt, this message translates to:
  /// **'Indique o número de autorização da ASFPM.'**
  String get validationLicenseNumberRequired;

  /// No description provided for @validationChannelsEmpty.
  ///
  /// In pt, this message translates to:
  /// **'Escolha pelo menos um canal.'**
  String get validationChannelsEmpty;

  /// No description provided for @validationChannelsDuplicated.
  ///
  /// In pt, this message translates to:
  /// **'Cada canal só pode aparecer uma vez.'**
  String get validationChannelsDuplicated;

  /// No description provided for @validationDefaultChannelNotEnabled.
  ///
  /// In pt, this message translates to:
  /// **'O canal por defeito tem de estar activo.'**
  String get validationDefaultChannelNotEnabled;

  /// No description provided for @validationFallbackOrderMismatch.
  ///
  /// In pt, this message translates to:
  /// **'A ordem alternativa tem de ter exactamente os canais activos.'**
  String get validationFallbackOrderMismatch;

  /// No description provided for @validationOutOfRange.
  ///
  /// In pt, this message translates to:
  /// **'Valor fora dos limites permitidos.'**
  String get validationOutOfRange;

  /// No description provided for @errorRefreshTokenInvalid.
  ///
  /// In pt, this message translates to:
  /// **'A sua sessão terminou. Entre novamente.'**
  String get errorRefreshTokenInvalid;

  /// No description provided for @errorRefreshTokenReused.
  ///
  /// In pt, this message translates to:
  /// **'Por segurança, a sua sessão foi terminada. Entre novamente.'**
  String get errorRefreshTokenReused;

  /// No description provided for @validationNotBlank.
  ///
  /// In pt, this message translates to:
  /// **'Campo obrigatório.'**
  String get validationNotBlank;

  /// No description provided for @validationNotNull.
  ///
  /// In pt, this message translates to:
  /// **'Campo obrigatório.'**
  String get validationNotNull;

  /// No description provided for @errorConcurrentModification.
  ///
  /// In pt, this message translates to:
  /// **'Outra pessoa alterou estes dados entretanto. Actualize e tente de novo.'**
  String get errorConcurrentModification;

  /// No description provided for @errorOtpChannelChoiceDisabled.
  ///
  /// In pt, this message translates to:
  /// **'Não é possível escolher o canal de envio do código.'**
  String get errorOtpChannelChoiceDisabled;

  /// No description provided for @errorOtpChannelNotAvailable.
  ///
  /// In pt, this message translates to:
  /// **'Este canal não está disponível. Use outro canal ou entre com o outro identificador.'**
  String get errorOtpChannelNotAvailable;

  /// No description provided for @errorOtpRateLimited.
  ///
  /// In pt, this message translates to:
  /// **'Pediu demasiados códigos. Tente mais tarde.'**
  String get errorOtpRateLimited;

  /// No description provided for @errorOtpResendTooSoon.
  ///
  /// In pt, this message translates to:
  /// **'Aguarde um pouco antes de pedir um novo código.'**
  String get errorOtpResendTooSoon;

  /// No description provided for @errorOtpDeliveryFailed.
  ///
  /// In pt, this message translates to:
  /// **'Não foi possível enviar o código. Tente de novo dentro de momentos.'**
  String get errorOtpDeliveryFailed;

  /// No description provided for @errorOtpChallengeNotFound.
  ///
  /// In pt, this message translates to:
  /// **'Pedido de código desconhecido. Peça um novo código.'**
  String get errorOtpChallengeNotFound;

  /// No description provided for @errorOtpAlreadyUsed.
  ///
  /// In pt, this message translates to:
  /// **'Este código já foi usado. Peça um novo código.'**
  String get errorOtpAlreadyUsed;

  /// No description provided for @errorOtpSuperseded.
  ///
  /// In pt, this message translates to:
  /// **'Pediu um código mais recente. Use o último código que recebeu.'**
  String get errorOtpSuperseded;

  /// No description provided for @errorOtpExpired.
  ///
  /// In pt, this message translates to:
  /// **'O código expirou. Peça um novo código.'**
  String get errorOtpExpired;

  /// No description provided for @errorOtpInvalid.
  ///
  /// In pt, this message translates to:
  /// **'Código errado. Verifique e tente de novo.'**
  String get errorOtpInvalid;

  /// No description provided for @errorOtpAttemptsExceeded.
  ///
  /// In pt, this message translates to:
  /// **'Demasiadas tentativas erradas. Peça um novo código.'**
  String get errorOtpAttemptsExceeded;

  /// No description provided for @errorAccountDisabled.
  ///
  /// In pt, this message translates to:
  /// **'Esta conta está desactivada. Contacte o apoio ao cliente.'**
  String get errorAccountDisabled;

  /// No description provided for @errorOrganizationNotFound.
  ///
  /// In pt, this message translates to:
  /// **'Organização não encontrada.'**
  String get errorOrganizationNotFound;

  /// No description provided for @errorOrganizationNotActive.
  ///
  /// In pt, this message translates to:
  /// **'A organização está suspensa.'**
  String get errorOrganizationNotActive;

  /// No description provided for @errorNuitAlreadyInUse.
  ///
  /// In pt, this message translates to:
  /// **'Já existe uma organização com este NUIT.'**
  String get errorNuitAlreadyInUse;

  /// No description provided for @errorShortCodeAlreadyInUse.
  ///
  /// In pt, this message translates to:
  /// **'Este código curto já é usado por outra seguradora.'**
  String get errorShortCodeAlreadyInUse;

  /// No description provided for @errorOmbudsmanInvalid.
  ///
  /// In pt, this message translates to:
  /// **'Escolha um provedor do cliente existente.'**
  String get errorOmbudsmanInvalid;

  /// No description provided for @errorRoleNotAllowedForOrganization.
  ///
  /// In pt, this message translates to:
  /// **'Um dos perfis escolhidos não pode ser atribuído nesta organização.'**
  String get errorRoleNotAllowedForOrganization;

  /// No description provided for @errorEmailAlreadyInUse.
  ///
  /// In pt, this message translates to:
  /// **'Este e-mail já pertence a outro utilizador.'**
  String get errorEmailAlreadyInUse;

  /// No description provided for @errorInvitationInvalid.
  ///
  /// In pt, this message translates to:
  /// **'Este convite já não é válido. Peça um novo convite a quem o convidou.'**
  String get errorInvitationInvalid;

  /// No description provided for @errorInvitationNotAccepted.
  ///
  /// In pt, this message translates to:
  /// **'Defina primeiro a sua palavra-passe.'**
  String get errorInvitationNotAccepted;

  /// No description provided for @errorTotpInvalid.
  ///
  /// In pt, this message translates to:
  /// **'Código errado. Verifique a aplicação de autenticação e tente de novo.'**
  String get errorTotpInvalid;

  /// No description provided for @errorPasswordTooWeak.
  ///
  /// In pt, this message translates to:
  /// **'A palavra-passe não cumpre as regras de segurança.'**
  String get errorPasswordTooWeak;

  /// No description provided for @errorInvalidCredentials.
  ///
  /// In pt, this message translates to:
  /// **'E-mail, palavra-passe ou código de autenticação errados.'**
  String get errorInvalidCredentials;

  /// No description provided for @errorAccountLocked.
  ///
  /// In pt, this message translates to:
  /// **'Demasiadas tentativas erradas. A conta está bloqueada temporariamente; tente mais tarde.'**
  String get errorAccountLocked;

  /// No description provided for @errorOrganizationSuspended.
  ///
  /// In pt, this message translates to:
  /// **'A sua organização está suspensa. Contacte a administração da MoSeguros.'**
  String get errorOrganizationSuspended;

  /// No description provided for @errorResetTokenInvalid.
  ///
  /// In pt, this message translates to:
  /// **'Este link de recuperação já não é válido. Peça um novo.'**
  String get errorResetTokenInvalid;

  /// No description provided for @errorUserNotFound.
  ///
  /// In pt, this message translates to:
  /// **'Utilizador não encontrado.'**
  String get errorUserNotFound;

  /// No description provided for @errorCannotModifySelf.
  ///
  /// In pt, this message translates to:
  /// **'Não pode alterar a sua própria conta.'**
  String get errorCannotModifySelf;

  /// No description provided for @errorLastManager.
  ///
  /// In pt, this message translates to:
  /// **'A organização tem de ficar com pelo menos um gestor activo.'**
  String get errorLastManager;

  /// No description provided for @errorContactAlreadyInUse.
  ///
  /// In pt, this message translates to:
  /// **'Este contacto já está associado a outra conta.'**
  String get errorContactAlreadyInUse;

  /// No description provided for @errorContactAlreadyVerified.
  ///
  /// In pt, this message translates to:
  /// **'Este contacto já está confirmado na sua conta.'**
  String get errorContactAlreadyVerified;

  /// No description provided for @errorLastContact.
  ///
  /// In pt, this message translates to:
  /// **'A conta tem de ficar com pelo menos um contacto confirmado.'**
  String get errorLastContact;

  /// No description provided for @errorFileNotFound.
  ///
  /// In pt, this message translates to:
  /// **'O ficheiro não existe ou não tem acesso a ele.'**
  String get errorFileNotFound;

  /// No description provided for @errorFileEmpty.
  ///
  /// In pt, this message translates to:
  /// **'O ficheiro está vazio.'**
  String get errorFileEmpty;

  /// No description provided for @errorFileTooLarge.
  ///
  /// In pt, this message translates to:
  /// **'O ficheiro tem mais de 10 MB.'**
  String get errorFileTooLarge;

  /// No description provided for @errorFileTypeNotAllowed.
  ///
  /// In pt, this message translates to:
  /// **'Tipo de ficheiro não aceite. Use JPEG, PNG, HEIC ou PDF.'**
  String get errorFileTypeNotAllowed;

  /// No description provided for @errorFileStorageError.
  ///
  /// In pt, this message translates to:
  /// **'Não foi possível ler o ficheiro. Tente mais tarde.'**
  String get errorFileStorageError;

  /// No description provided for @errorReferenceDomainUnknown.
  ///
  /// In pt, this message translates to:
  /// **'Esta lista de referência não existe.'**
  String get errorReferenceDomainUnknown;

  /// No description provided for @errorReferenceValueNotFound.
  ///
  /// In pt, this message translates to:
  /// **'O valor não existe nesta lista.'**
  String get errorReferenceValueNotFound;

  /// No description provided for @errorReferenceCodeAlreadyExists.
  ///
  /// In pt, this message translates to:
  /// **'Já existe um valor com este código nesta lista.'**
  String get errorReferenceCodeAlreadyExists;

  /// No description provided for @errorPlatformSettingNotFound.
  ///
  /// In pt, this message translates to:
  /// **'Este parâmetro não existe.'**
  String get errorPlatformSettingNotFound;

  /// No description provided for @errorPlateFormatNotFound.
  ///
  /// In pt, this message translates to:
  /// **'O formato de matrícula não existe.'**
  String get errorPlateFormatNotFound;

  /// No description provided for @errorPlateFormatCodeAlreadyExists.
  ///
  /// In pt, this message translates to:
  /// **'Já existe um formato de matrícula com este código.'**
  String get errorPlateFormatCodeAlreadyExists;

  /// No description provided for @errorLastActivePlateFormat.
  ///
  /// In pt, this message translates to:
  /// **'Tem de ficar pelo menos um formato de matrícula activo.'**
  String get errorLastActivePlateFormat;

  /// No description provided for @errorProductNotFound.
  ///
  /// In pt, this message translates to:
  /// **'Produto não encontrado.'**
  String get errorProductNotFound;

  /// No description provided for @errorProductVersionNotFound.
  ///
  /// In pt, this message translates to:
  /// **'Versão do produto não encontrada.'**
  String get errorProductVersionNotFound;

  /// No description provided for @errorProductDocumentNotFound.
  ///
  /// In pt, this message translates to:
  /// **'Documento não encontrado.'**
  String get errorProductDocumentNotFound;

  /// No description provided for @errorProductCodeAlreadyExists.
  ///
  /// In pt, this message translates to:
  /// **'Já existe um produto com este código.'**
  String get errorProductCodeAlreadyExists;

  /// No description provided for @errorProductTypeLocked.
  ///
  /// In pt, this message translates to:
  /// **'O tipo não pode mudar depois de o produto ter sido publicado.'**
  String get errorProductTypeLocked;

  /// No description provided for @errorProductRetired.
  ///
  /// In pt, this message translates to:
  /// **'Este produto foi retirado e já não pode ser alterado.'**
  String get errorProductRetired;

  /// No description provided for @errorProductDraftExists.
  ///
  /// In pt, this message translates to:
  /// **'Já existe uma versão em preparação deste produto.'**
  String get errorProductDraftExists;

  /// No description provided for @errorProductVersionNotEditable.
  ///
  /// In pt, this message translates to:
  /// **'Esta versão já foi publicada e não pode ser alterada. Crie uma nova versão.'**
  String get errorProductVersionNotEditable;

  /// No description provided for @errorProductDocumentLimitReached.
  ///
  /// In pt, this message translates to:
  /// **'Cada versão pode ter no máximo 10 documentos.'**
  String get errorProductDocumentLimitReached;

  /// No description provided for @errorProductVersionNotPublishable.
  ///
  /// In pt, this message translates to:
  /// **'A versão ainda não cumpre as regras de publicação.'**
  String get errorProductVersionNotPublishable;

  /// No description provided for @validationNotAllowed.
  ///
  /// In pt, this message translates to:
  /// **'Este campo não se aplica neste caso.'**
  String get validationNotAllowed;

  /// No description provided for @validationRequired.
  ///
  /// In pt, this message translates to:
  /// **'Campo obrigatório.'**
  String get validationRequired;

  /// No description provided for @validationInvalidSort.
  ///
  /// In pt, this message translates to:
  /// **'Ordenação não suportada.'**
  String get validationInvalidSort;

  /// No description provided for @validationTooShort.
  ///
  /// In pt, this message translates to:
  /// **'A palavra-passe tem de ter pelo menos 12 caracteres.'**
  String get validationTooShort;

  /// No description provided for @validationTooLong.
  ///
  /// In pt, this message translates to:
  /// **'A palavra-passe pode ter no máximo 128 caracteres.'**
  String get validationTooLong;

  /// No description provided for @validationContainsEmail.
  ///
  /// In pt, this message translates to:
  /// **'A palavra-passe não pode conter o seu e-mail.'**
  String get validationContainsEmail;

  /// No description provided for @validationTooCommon.
  ///
  /// In pt, this message translates to:
  /// **'Esta palavra-passe é demasiado comum. Escolha outra.'**
  String get validationTooCommon;

  /// No description provided for @validationSize.
  ///
  /// In pt, this message translates to:
  /// **'Comprimento fora dos limites permitidos.'**
  String get validationSize;

  /// No description provided for @validationPattern.
  ///
  /// In pt, this message translates to:
  /// **'Formato inválido.'**
  String get validationPattern;

  /// No description provided for @validationTypeMismatch.
  ///
  /// In pt, this message translates to:
  /// **'Valor inválido.'**
  String get validationTypeMismatch;

  /// No description provided for @validationInvalidName.
  ///
  /// In pt, this message translates to:
  /// **'Indique o nome completo (pelo menos duas palavras, só letras, espaços, \' e -).'**
  String get validationInvalidName;

  /// No description provided for @validationUnderage.
  ///
  /// In pt, this message translates to:
  /// **'Tem de ter pelo menos 18 anos.'**
  String get validationUnderage;

  /// No description provided for @validationInvalidDate.
  ///
  /// In pt, this message translates to:
  /// **'Data inválida.'**
  String get validationInvalidDate;

  /// No description provided for @validationInvalidDocumentNumber.
  ///
  /// In pt, this message translates to:
  /// **'Número de documento inválido (5 a 20 letras maiúsculas ou dígitos).'**
  String get validationInvalidDocumentNumber;

  /// No description provided for @validationInvalidCode.
  ///
  /// In pt, this message translates to:
  /// **'Use só maiúsculas, algarismos e _.'**
  String get validationInvalidCode;

  /// No description provided for @validationParentNotFound.
  ///
  /// In pt, this message translates to:
  /// **'A marca indicada não existe.'**
  String get validationParentNotFound;

  /// No description provided for @validationInvalidValue.
  ///
  /// In pt, this message translates to:
  /// **'Valor inválido para este parâmetro.'**
  String get validationInvalidValue;

  /// No description provided for @validationInvalidPattern.
  ///
  /// In pt, this message translates to:
  /// **'A expressão regular não é válida.'**
  String get validationInvalidPattern;

  /// No description provided for @validationPatternNotAnchored.
  ///
  /// In pt, this message translates to:
  /// **'A expressão tem de começar por ^ e acabar em \$.'**
  String get validationPatternNotAnchored;

  /// No description provided for @validationPatternWithoutGroups.
  ///
  /// In pt, this message translates to:
  /// **'A expressão precisa de pelo menos um grupo com nome.'**
  String get validationPatternWithoutGroups;

  /// No description provided for @validationTemplateGroupUnknown.
  ///
  /// In pt, this message translates to:
  /// **'O modelo usa um grupo que não existe na expressão.'**
  String get validationTemplateGroupUnknown;

  /// No description provided for @validationExampleDoesNotMatch.
  ///
  /// In pt, this message translates to:
  /// **'O exemplo não corresponde ao formato.'**
  String get validationExampleDoesNotMatch;

  /// No description provided for @validationDuplicated.
  ///
  /// In pt, this message translates to:
  /// **'Valor repetido.'**
  String get validationDuplicated;

  /// No description provided for @validationInvalidRequirement.
  ///
  /// In pt, this message translates to:
  /// **'Uma cobertura não pode exigir-se a si própria.'**
  String get validationInvalidRequirement;

  /// No description provided for @validationCapitalOptionsInvalid.
  ///
  /// In pt, this message translates to:
  /// **'Indique 2 a 10 valores, do menor para o maior.'**
  String get validationCapitalOptionsInvalid;

  /// No description provided for @validationInvalidCountryCode.
  ///
  /// In pt, this message translates to:
  /// **'Código de país inválido.'**
  String get validationInvalidCountryCode;

  /// No description provided for @validationMzScopeRequired.
  ///
  /// In pt, this message translates to:
  /// **'O âmbito tem de incluir Moçambique.'**
  String get validationMzScopeRequired;

  /// No description provided for @validationInvalidCapitalType.
  ///
  /// In pt, this message translates to:
  /// **'Tipo de capital não permitido para esta cobertura.'**
  String get validationInvalidCapitalType;

  /// No description provided for @validationMtplRequired.
  ///
  /// In pt, this message translates to:
  /// **'O produto tem de incluir a responsabilidade civil obrigatória.'**
  String get validationMtplRequired;

  /// No description provided for @validationMtplMustBeMandatory.
  ///
  /// In pt, this message translates to:
  /// **'A responsabilidade civil obrigatória não pode ser opcional.'**
  String get validationMtplMustBeMandatory;

  /// No description provided for @validationMtplCapitalBelowMinimum.
  ///
  /// In pt, this message translates to:
  /// **'O capital é inferior ao capital mínimo legal configurado.'**
  String get validationMtplCapitalBelowMinimum;

  /// No description provided for @validationBorderOnlyMtpl.
  ///
  /// In pt, this message translates to:
  /// **'O seguro de fronteira só pode ter a responsabilidade civil obrigatória.'**
  String get validationBorderOnlyMtpl;

  /// No description provided for @validationOdNotAllowedForType.
  ///
  /// In pt, this message translates to:
  /// **'Este tipo de produto não admite coberturas de danos próprios.'**
  String get validationOdNotAllowedForType;

  /// No description provided for @validationOdRequiredForComprehensive.
  ///
  /// In pt, this message translates to:
  /// **'Um produto de danos próprios precisa de pelo menos uma cobertura de danos próprios.'**
  String get validationOdRequiredForComprehensive;

  /// No description provided for @validationOdCapitalMustBeVehicleValue.
  ///
  /// In pt, this message translates to:
  /// **'As coberturas de danos próprios usam o valor do veículo como capital.'**
  String get validationOdCapitalMustBeVehicleValue;

  /// No description provided for @validationOdTheftRequiresOdCoverages.
  ///
  /// In pt, this message translates to:
  /// **'Furto ou roubo exige choque, incêndio e fenómenos da natureza.'**
  String get validationOdTheftRequiresOdCoverages;

  /// No description provided for @validationRequiredCoverageMissing.
  ///
  /// In pt, this message translates to:
  /// **'Falta uma cobertura que esta exige.'**
  String get validationRequiredCoverageMissing;

  /// No description provided for @validationVtplCapitalTooLow.
  ///
  /// In pt, this message translates to:
  /// **'O capital da RC facultativa tem de ser superior ao capital mínimo legal.'**
  String get validationVtplCapitalTooLow;

  /// No description provided for @validationCrossBorderScopeRequired.
  ///
  /// In pt, this message translates to:
  /// **'Indique pelo menos um país além de Moçambique.'**
  String get validationCrossBorderScopeRequired;

  /// No description provided for @validationDocumentRequired.
  ///
  /// In pt, this message translates to:
  /// **'Falta este documento em português.'**
  String get validationDocumentRequired;

  /// province.MAPUTO_CITY (Province no backend).
  ///
  /// In pt, this message translates to:
  /// **'Maputo Cidade'**
  String get provinceMaputoCity;

  /// No description provided for @provinceMaputo.
  ///
  /// In pt, this message translates to:
  /// **'Maputo Província'**
  String get provinceMaputo;

  /// No description provided for @provinceGaza.
  ///
  /// In pt, this message translates to:
  /// **'Gaza'**
  String get provinceGaza;

  /// No description provided for @provinceInhambane.
  ///
  /// In pt, this message translates to:
  /// **'Inhambane'**
  String get provinceInhambane;

  /// No description provided for @provinceSofala.
  ///
  /// In pt, this message translates to:
  /// **'Sofala'**
  String get provinceSofala;

  /// No description provided for @provinceManica.
  ///
  /// In pt, this message translates to:
  /// **'Manica'**
  String get provinceManica;

  /// No description provided for @provinceTete.
  ///
  /// In pt, this message translates to:
  /// **'Tete'**
  String get provinceTete;

  /// No description provided for @provinceZambezia.
  ///
  /// In pt, this message translates to:
  /// **'Zambézia'**
  String get provinceZambezia;

  /// No description provided for @provinceNampula.
  ///
  /// In pt, this message translates to:
  /// **'Nampula'**
  String get provinceNampula;

  /// No description provided for @provinceCaboDelgado.
  ///
  /// In pt, this message translates to:
  /// **'Cabo Delgado'**
  String get provinceCaboDelgado;

  /// No description provided for @provinceNiassa.
  ///
  /// In pt, this message translates to:
  /// **'Niassa'**
  String get provinceNiassa;

  /// Cartão com a versão da API, no fundo do perfil.
  ///
  /// In pt, this message translates to:
  /// **'Sobre'**
  String get aboutTitle;

  /// No description provided for @loading.
  ///
  /// In pt, this message translates to:
  /// **'A carregar…'**
  String get loading;

  /// No description provided for @cancel.
  ///
  /// In pt, this message translates to:
  /// **'Cancelar'**
  String get cancel;

  /// No description provided for @onboardingSkip.
  ///
  /// In pt, this message translates to:
  /// **'Saltar'**
  String get onboardingSkip;

  /// No description provided for @onboardingNext.
  ///
  /// In pt, this message translates to:
  /// **'Seguinte'**
  String get onboardingNext;

  /// No description provided for @onboardingStart.
  ///
  /// In pt, this message translates to:
  /// **'Começar'**
  String get onboardingStart;

  /// Indicador de página da apresentação (leitores de ecrã).
  ///
  /// In pt, this message translates to:
  /// **'Página {current} de {total}'**
  String onboardingPage(int current, int total);

  /// No description provided for @onboarding1Title.
  ///
  /// In pt, this message translates to:
  /// **'Compare seguradoras'**
  String get onboarding1Title;

  /// No description provided for @onboarding1Text.
  ///
  /// In pt, this message translates to:
  /// **'Veja as propostas de várias seguradoras lado a lado e escolha a que lhe serve.'**
  String get onboarding1Text;

  /// No description provided for @onboarding2Title.
  ///
  /// In pt, this message translates to:
  /// **'Compre em minutos'**
  String get onboarding2Title;

  /// No description provided for @onboarding2Text.
  ///
  /// In pt, this message translates to:
  /// **'Simule, pague com M-Pesa ou por referência e receba a apólice no telemóvel.'**
  String get onboarding2Text;

  /// No description provided for @onboarding3Title.
  ///
  /// In pt, this message translates to:
  /// **'Tudo num só lugar'**
  String get onboarding3Title;

  /// No description provided for @onboarding3Text.
  ///
  /// In pt, this message translates to:
  /// **'Apólices, pagamentos e sinistros sempre consigo.'**
  String get onboarding3Text;

  /// auth.login.title na web.
  ///
  /// In pt, this message translates to:
  /// **'Entrar na área do cliente'**
  String get authLoginTitle;

  /// No description provided for @authLoginSubtitle.
  ///
  /// In pt, this message translates to:
  /// **'Use o seu telemóvel ou e-mail. Enviamos-lhe um código para entrar, sem palavra-passe.'**
  String get authLoginSubtitle;

  /// No description provided for @authIdentifierTypeLabel.
  ///
  /// In pt, this message translates to:
  /// **'Entrar com'**
  String get authIdentifierTypeLabel;

  /// No description provided for @authIdentifierTypeMsisdn.
  ///
  /// In pt, this message translates to:
  /// **'Telemóvel'**
  String get authIdentifierTypeMsisdn;

  /// No description provided for @authIdentifierTypeEmail.
  ///
  /// In pt, this message translates to:
  /// **'E-mail'**
  String get authIdentifierTypeEmail;

  /// No description provided for @authMsisdnLabel.
  ///
  /// In pt, this message translates to:
  /// **'Número de telemóvel'**
  String get authMsisdnLabel;

  /// No description provided for @authMsisdnHelper.
  ///
  /// In pt, this message translates to:
  /// **'9 dígitos, por exemplo 84 123 4567.'**
  String get authMsisdnHelper;

  /// No description provided for @authEmailLabel.
  ///
  /// In pt, this message translates to:
  /// **'E-mail'**
  String get authEmailLabel;

  /// No description provided for @authChannelLabel.
  ///
  /// In pt, this message translates to:
  /// **'Receber o código por'**
  String get authChannelLabel;

  /// No description provided for @authSubmit.
  ///
  /// In pt, this message translates to:
  /// **'Receber código'**
  String get authSubmit;

  /// No description provided for @authRetryIn.
  ///
  /// In pt, this message translates to:
  /// **'Tente de novo dentro de {seconds} s.'**
  String authRetryIn(int seconds);

  /// No description provided for @authCodeSubtitle.
  ///
  /// In pt, this message translates to:
  /// **'Escreva o código que recebeu.'**
  String get authCodeSubtitle;

  /// No description provided for @authCodeInputLabel.
  ///
  /// In pt, this message translates to:
  /// **'Código recebido'**
  String get authCodeInputLabel;

  /// No description provided for @authCodeSentTo.
  ///
  /// In pt, this message translates to:
  /// **'Enviámos um código por {channel} para {destination}.'**
  String authCodeSentTo(String channel, String destination);

  /// No description provided for @authCodeChange.
  ///
  /// In pt, this message translates to:
  /// **'Alterar'**
  String get authCodeChange;

  /// No description provided for @authCodeFallback.
  ///
  /// In pt, this message translates to:
  /// **'Não conseguimos enviar por {requested}. Enviámos o código por {used}.'**
  String authCodeFallback(String requested, String used);

  /// OTP_INVALID com attemptsRemaining.
  ///
  /// In pt, this message translates to:
  /// **'{count, plural, =1{Código errado. Resta 1 tentativa.} other{Código errado. Restam {count} tentativas.}}'**
  String authCodeWrongCode(int count);

  /// No description provided for @authCodeResent.
  ///
  /// In pt, this message translates to:
  /// **'Enviámos um código novo.'**
  String get authCodeResent;

  /// No description provided for @authCodeResend.
  ///
  /// In pt, this message translates to:
  /// **'Reenviar código'**
  String get authCodeResend;

  /// No description provided for @authCodeResendIn.
  ///
  /// In pt, this message translates to:
  /// **'Reenviar código ({seconds} s)'**
  String authCodeResendIn(int seconds);

  /// No description provided for @channelSms.
  ///
  /// In pt, this message translates to:
  /// **'SMS'**
  String get channelSms;

  /// No description provided for @channelWhatsapp.
  ///
  /// In pt, this message translates to:
  /// **'WhatsApp'**
  String get channelWhatsapp;

  /// No description provided for @channelEmail.
  ///
  /// In pt, this message translates to:
  /// **'E-mail'**
  String get channelEmail;

  /// No description provided for @logout.
  ///
  /// In pt, this message translates to:
  /// **'Terminar sessão'**
  String get logout;

  /// No description provided for @logoutConfirmTitle.
  ///
  /// In pt, this message translates to:
  /// **'Terminar sessão?'**
  String get logoutConfirmTitle;

  /// No description provided for @logoutConfirmText.
  ///
  /// In pt, this message translates to:
  /// **'Para voltar a entrar vai precisar de um código novo.'**
  String get logoutConfirmText;

  /// No description provided for @homeGreeting.
  ///
  /// In pt, this message translates to:
  /// **'Olá, {name}'**
  String homeGreeting(String name);

  /// No description provided for @homeGreetingNoName.
  ///
  /// In pt, this message translates to:
  /// **'Olá'**
  String get homeGreetingNoName;

  /// No description provided for @homeIncompleteProfile.
  ///
  /// In pt, this message translates to:
  /// **'Complete o seu perfil para poder comprar um seguro.'**
  String get homeIncompleteProfile;

  /// No description provided for @homeCompleteProfile.
  ///
  /// In pt, this message translates to:
  /// **'Completar perfil'**
  String get homeCompleteProfile;

  /// No description provided for @homeQuoteTitle.
  ///
  /// In pt, this message translates to:
  /// **'Seguro automóvel'**
  String get homeQuoteTitle;

  /// No description provided for @homeQuoteText.
  ///
  /// In pt, this message translates to:
  /// **'Compare os preços das seguradoras para o seu veículo.'**
  String get homeQuoteText;

  /// No description provided for @comingSoon.
  ///
  /// In pt, this message translates to:
  /// **'Disponível em breve'**
  String get comingSoon;

  /// No description provided for @profileOpen.
  ///
  /// In pt, this message translates to:
  /// **'Perfil'**
  String get profileOpen;

  /// No description provided for @profileTitle.
  ///
  /// In pt, this message translates to:
  /// **'Perfil'**
  String get profileTitle;

  /// No description provided for @profilePersonalData.
  ///
  /// In pt, this message translates to:
  /// **'Dados pessoais'**
  String get profilePersonalData;

  /// No description provided for @profileCompleteNotice.
  ///
  /// In pt, this message translates to:
  /// **'Precisamos destes dados para emitir as suas apólices.'**
  String get profileCompleteNotice;

  /// No description provided for @profileFieldFullName.
  ///
  /// In pt, this message translates to:
  /// **'Nome completo'**
  String get profileFieldFullName;

  /// No description provided for @profileFieldBirthDate.
  ///
  /// In pt, this message translates to:
  /// **'Data de nascimento'**
  String get profileFieldBirthDate;

  /// No description provided for @profileFieldIdDocType.
  ///
  /// In pt, this message translates to:
  /// **'Tipo de documento'**
  String get profileFieldIdDocType;

  /// No description provided for @profileFieldIdDocNumber.
  ///
  /// In pt, this message translates to:
  /// **'Número do documento'**
  String get profileFieldIdDocNumber;

  /// No description provided for @profileFieldNuit.
  ///
  /// In pt, this message translates to:
  /// **'NUIT'**
  String get profileFieldNuit;

  /// No description provided for @profileFieldAddress.
  ///
  /// In pt, this message translates to:
  /// **'Morada'**
  String get profileFieldAddress;

  /// No description provided for @profileFieldProvince.
  ///
  /// In pt, this message translates to:
  /// **'Província'**
  String get profileFieldProvince;

  /// No description provided for @profileChoose.
  ///
  /// In pt, this message translates to:
  /// **'Escolha…'**
  String get profileChoose;

  /// No description provided for @profileChange.
  ///
  /// In pt, this message translates to:
  /// **'Alterar'**
  String get profileChange;

  /// No description provided for @profileChangeDocument.
  ///
  /// In pt, this message translates to:
  /// **'Alterar o número do documento'**
  String get profileChangeDocument;

  /// No description provided for @profileChangeNuit.
  ///
  /// In pt, this message translates to:
  /// **'Alterar o NUIT'**
  String get profileChangeNuit;

  /// No description provided for @profileNuitHelper.
  ///
  /// In pt, this message translates to:
  /// **'9 dígitos.'**
  String get profileNuitHelper;

  /// No description provided for @profileOptional.
  ///
  /// In pt, this message translates to:
  /// **'Opcional.'**
  String get profileOptional;

  /// No description provided for @profileSave.
  ///
  /// In pt, this message translates to:
  /// **'Guardar'**
  String get profileSave;

  /// No description provided for @profileSaved.
  ///
  /// In pt, this message translates to:
  /// **'Perfil guardado'**
  String get profileSaved;

  /// idDocType.BI na web.
  ///
  /// In pt, this message translates to:
  /// **'Bilhete de Identidade'**
  String get idDocTypeBi;

  /// No description provided for @idDocTypePassport.
  ///
  /// In pt, this message translates to:
  /// **'Passaporte'**
  String get idDocTypePassport;

  /// No description provided for @idDocTypeDire.
  ///
  /// In pt, this message translates to:
  /// **'DIRE'**
  String get idDocTypeDire;

  /// No description provided for @contactsTitle.
  ///
  /// In pt, this message translates to:
  /// **'Contactos'**
  String get contactsTitle;

  /// No description provided for @contactTypeMsisdn.
  ///
  /// In pt, this message translates to:
  /// **'Telemóvel'**
  String get contactTypeMsisdn;

  /// No description provided for @contactTypeEmail.
  ///
  /// In pt, this message translates to:
  /// **'E-mail'**
  String get contactTypeEmail;

  /// No description provided for @contactsVerified.
  ///
  /// In pt, this message translates to:
  /// **'Verificado'**
  String get contactsVerified;

  /// No description provided for @contactsAddMsisdn.
  ///
  /// In pt, this message translates to:
  /// **'Acrescentar telemóvel'**
  String get contactsAddMsisdn;

  /// No description provided for @contactsAddEmail.
  ///
  /// In pt, this message translates to:
  /// **'Acrescentar e-mail'**
  String get contactsAddEmail;

  /// No description provided for @contactsChangeMsisdn.
  ///
  /// In pt, this message translates to:
  /// **'Alterar telemóvel'**
  String get contactsChangeMsisdn;

  /// No description provided for @contactsChangeEmail.
  ///
  /// In pt, this message translates to:
  /// **'Alterar e-mail'**
  String get contactsChangeEmail;

  /// No description provided for @contactsChangeShort.
  ///
  /// In pt, this message translates to:
  /// **'Alterar'**
  String get contactsChangeShort;

  /// No description provided for @contactsRemoveMsisdn.
  ///
  /// In pt, this message translates to:
  /// **'Remover telemóvel'**
  String get contactsRemoveMsisdn;

  /// No description provided for @contactsRemoveEmail.
  ///
  /// In pt, this message translates to:
  /// **'Remover e-mail'**
  String get contactsRemoveEmail;

  /// No description provided for @contactsRemoveShort.
  ///
  /// In pt, this message translates to:
  /// **'Remover'**
  String get contactsRemoveShort;

  /// No description provided for @contactsSaved.
  ///
  /// In pt, this message translates to:
  /// **'Contacto confirmado'**
  String get contactsSaved;

  /// No description provided for @contactsRemoved.
  ///
  /// In pt, this message translates to:
  /// **'Contacto removido'**
  String get contactsRemoved;

  /// No description provided for @contactsConfirmRemoveTitle.
  ///
  /// In pt, this message translates to:
  /// **'Remover contacto?'**
  String get contactsConfirmRemoveTitle;

  /// No description provided for @contactsConfirmRemoveText.
  ///
  /// In pt, this message translates to:
  /// **'Deixará de poder entrar e receber avisos em {value}.'**
  String contactsConfirmRemoveText(String value);

  /// No description provided for @contactsConfirmRemove.
  ///
  /// In pt, this message translates to:
  /// **'Remover'**
  String get contactsConfirmRemove;
}

class _AppLocalizationsDelegate
    extends LocalizationsDelegate<AppLocalizations> {
  const _AppLocalizationsDelegate();

  @override
  Future<AppLocalizations> load(Locale locale) {
    return SynchronousFuture<AppLocalizations>(lookupAppLocalizations(locale));
  }

  @override
  bool isSupported(Locale locale) =>
      <String>['en', 'pt'].contains(locale.languageCode);

  @override
  bool shouldReload(_AppLocalizationsDelegate old) => false;
}

AppLocalizations lookupAppLocalizations(Locale locale) {
  // Lookup logic when only language code is specified.
  switch (locale.languageCode) {
    case 'en':
      return AppLocalizationsEn();
    case 'pt':
      return AppLocalizationsPt();
  }

  throw FlutterError(
    'AppLocalizations.delegate failed to load unsupported locale "$locale". This is likely '
    'an issue with the localizations generation tool. Please file an issue '
    'on GitHub with a reproducible sample app and the gen-l10n configuration '
    'that was used.',
  );
}
