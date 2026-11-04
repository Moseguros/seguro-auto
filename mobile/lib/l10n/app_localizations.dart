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

  /// No description provided for @errorVehicleNotFound.
  ///
  /// In pt, this message translates to:
  /// **'Veículo não encontrado.'**
  String get errorVehicleNotFound;

  /// No description provided for @errorVehicleAlreadyRegistered.
  ///
  /// In pt, this message translates to:
  /// **'Já tem um veículo com esta matrícula.'**
  String get errorVehicleAlreadyRegistered;

  /// No description provided for @errorVehicleLimitReached.
  ///
  /// In pt, this message translates to:
  /// **'Atingiu o máximo de 20 veículos.'**
  String get errorVehicleLimitReached;

  /// No description provided for @errorVehicleRemovalBlocked.
  ///
  /// In pt, this message translates to:
  /// **'Este veículo tem uma apólice em vigor e não pode ser retirado.'**
  String get errorVehicleRemovalBlocked;

  /// No description provided for @errorVehicleRegistryNotFound.
  ///
  /// In pt, this message translates to:
  /// **'Não encontrámos esta matrícula no registo. Preencha os dados do livrete.'**
  String get errorVehicleRegistryNotFound;

  /// No description provided for @errorVehicleRegistryUnavailable.
  ///
  /// In pt, this message translates to:
  /// **'O registo automóvel não está disponível. Preencha os dados do livrete.'**
  String get errorVehicleRegistryUnavailable;

  /// No description provided for @errorDriverNotFound.
  ///
  /// In pt, this message translates to:
  /// **'Condutor não encontrado.'**
  String get errorDriverNotFound;

  /// No description provided for @errorDriverAlreadyRegistered.
  ///
  /// In pt, this message translates to:
  /// **'Já tem um condutor com esta carta.'**
  String get errorDriverAlreadyRegistered;

  /// No description provided for @errorDriverLimitReached.
  ///
  /// In pt, this message translates to:
  /// **'Atingiu o máximo de 10 condutores.'**
  String get errorDriverLimitReached;

  /// No description provided for @errorOwnerDriverAlreadyExists.
  ///
  /// In pt, this message translates to:
  /// **'Já se registou como condutor.'**
  String get errorOwnerDriverAlreadyExists;

  /// No description provided for @errorDriverIsHabitual.
  ///
  /// In pt, this message translates to:
  /// **'Este condutor é o habitual de um veículo. Escolha outro habitual primeiro.'**
  String get errorDriverIsHabitual;

  /// No description provided for @errorRatingRulesTooLarge.
  ///
  /// In pt, this message translates to:
  /// **'A tarifa é demasiado grande (máximo 256 KB).'**
  String get errorRatingRulesTooLarge;

  /// No description provided for @validationInvalidChassis.
  ///
  /// In pt, this message translates to:
  /// **'N.º de chassis inválido.'**
  String get validationInvalidChassis;

  /// No description provided for @validationInvalidYear.
  ///
  /// In pt, this message translates to:
  /// **'Ano inválido.'**
  String get validationInvalidYear;

  /// No description provided for @validationInvalidLicenceNumber.
  ///
  /// In pt, this message translates to:
  /// **'N.º da carta inválido.'**
  String get validationInvalidLicenceNumber;

  /// No description provided for @validationLicenceCategoryUnknown.
  ///
  /// In pt, this message translates to:
  /// **'Categoria de carta desconhecida.'**
  String get validationLicenceCategoryUnknown;

  /// No description provided for @validationLicenceIssueDateInvalid.
  ///
  /// In pt, this message translates to:
  /// **'A data de emissão tem de ser depois do nascimento e até hoje.'**
  String get validationLicenceIssueDateInvalid;

  /// No description provided for @validationHabitualDriverRequired.
  ///
  /// In pt, this message translates to:
  /// **'Escolha o condutor habitual.'**
  String get validationHabitualDriverRequired;

  /// No description provided for @validationMultipleHabitualDrivers.
  ///
  /// In pt, this message translates to:
  /// **'Só pode haver um condutor habitual.'**
  String get validationMultipleHabitualDrivers;

  /// No description provided for @validationSchemaViolation.
  ///
  /// In pt, this message translates to:
  /// **'Formato inválido.'**
  String get validationSchemaViolation;

  /// No description provided for @validationCoverageTariffMissing.
  ///
  /// In pt, this message translates to:
  /// **'Falta a tarifa de uma cobertura do produto.'**
  String get validationCoverageTariffMissing;

  /// No description provided for @validationCoverageNotInProduct.
  ///
  /// In pt, this message translates to:
  /// **'Esta cobertura não existe no produto.'**
  String get validationCoverageNotInProduct;

  /// No description provided for @validationDuplicateCoverage.
  ///
  /// In pt, this message translates to:
  /// **'Cobertura repetida.'**
  String get validationDuplicateCoverage;

  /// No description provided for @validationBaseTypeNotAllowed.
  ///
  /// In pt, this message translates to:
  /// **'Este tipo de base não serve para o capital desta cobertura.'**
  String get validationBaseTypeNotAllowed;

  /// No description provided for @validationValueOutOfRange.
  ///
  /// In pt, this message translates to:
  /// **'Valor fora dos limites permitidos.'**
  String get validationValueOutOfRange;

  /// No description provided for @validationInvalidRange.
  ///
  /// In pt, this message translates to:
  /// **'O limite inferior tem de ser menor que o superior.'**
  String get validationInvalidRange;

  /// No description provided for @validationDuplicateFactor.
  ///
  /// In pt, this message translates to:
  /// **'Factor repetido.'**
  String get validationDuplicateFactor;

  /// No description provided for @validationReservedFactorCode.
  ///
  /// In pt, this message translates to:
  /// **'Este código está reservado ao sistema.'**
  String get validationReservedFactorCode;

  /// No description provided for @validationDuplicateDuration.
  ///
  /// In pt, this message translates to:
  /// **'Duração repetida.'**
  String get validationDuplicateDuration;

  /// No description provided for @validationDuration365NotOne.
  ///
  /// In pt, this message translates to:
  /// **'O factor de 365 dias tem de ser 1.'**
  String get validationDuration365NotOne;

  /// No description provided for @validationDurationFactorsRequired.
  ///
  /// In pt, this message translates to:
  /// **'Um produto temporário precisa da tabela de duração.'**
  String get validationDurationFactorsRequired;

  /// No description provided for @validationDurationNotAllowed.
  ///
  /// In pt, this message translates to:
  /// **'Duração não permitida para este produto.'**
  String get validationDurationNotAllowed;

  /// No description provided for @validationDuplicateCharge.
  ///
  /// In pt, this message translates to:
  /// **'Encargo repetido.'**
  String get validationDuplicateCharge;

  /// No description provided for @save.
  ///
  /// In pt, this message translates to:
  /// **'Guardar'**
  String get save;

  /// No description provided for @close.
  ///
  /// In pt, this message translates to:
  /// **'Fechar'**
  String get close;

  /// No description provided for @reload.
  ///
  /// In pt, this message translates to:
  /// **'Recarregar'**
  String get reload;

  /// No description provided for @yes.
  ///
  /// In pt, this message translates to:
  /// **'Sim'**
  String get yes;

  /// No description provided for @no.
  ///
  /// In pt, this message translates to:
  /// **'Não'**
  String get no;

  /// Valor em falta numa lista de dados.
  ///
  /// In pt, this message translates to:
  /// **'—'**
  String get valueMissing;

  /// No description provided for @moreOptions.
  ///
  /// In pt, this message translates to:
  /// **'Mais opções'**
  String get moreOptions;

  /// Destino da barra inferior.
  ///
  /// In pt, this message translates to:
  /// **'Início'**
  String get navHome;

  /// Destino da barra inferior e segmento "Veículos".
  ///
  /// In pt, this message translates to:
  /// **'Veículos'**
  String get navVehicles;

  /// Destino da barra inferior.
  ///
  /// In pt, this message translates to:
  /// **'Perfil'**
  String get navProfile;

  /// Só nos ecrãs (MoneyField, S2-05 §2); não é um código da API.
  ///
  /// In pt, this message translates to:
  /// **'Valor inválido. Use no máximo 2 casas decimais.'**
  String get validationInvalidMoney;

  /// vehicleCategory.LIGHT_PASSENGER (S2-02 §8).
  ///
  /// In pt, this message translates to:
  /// **'Ligeiro de passageiros'**
  String get vehicleCategoryLightPassenger;

  /// No description provided for @vehicleCategoryLightMixed.
  ///
  /// In pt, this message translates to:
  /// **'Ligeiro misto'**
  String get vehicleCategoryLightMixed;

  /// No description provided for @vehicleCategoryHeavy.
  ///
  /// In pt, this message translates to:
  /// **'Pesado'**
  String get vehicleCategoryHeavy;

  /// No description provided for @vehicleCategoryMotorcycle.
  ///
  /// In pt, this message translates to:
  /// **'Motociclo'**
  String get vehicleCategoryMotorcycle;

  /// No description provided for @vehicleCategoryTrailer.
  ///
  /// In pt, this message translates to:
  /// **'Reboque'**
  String get vehicleCategoryTrailer;

  /// No description provided for @vehicleCategoryBus.
  ///
  /// In pt, this message translates to:
  /// **'Autocarro'**
  String get vehicleCategoryBus;

  /// vehicleUsage.PRIVATE (S2-02 §8).
  ///
  /// In pt, this message translates to:
  /// **'Particular'**
  String get vehicleUsagePrivate;

  /// No description provided for @vehicleUsageRental.
  ///
  /// In pt, this message translates to:
  /// **'Aluguer sem condutor'**
  String get vehicleUsageRental;

  /// No description provided for @vehicleUsagePassengerTransport.
  ///
  /// In pt, this message translates to:
  /// **'Transporte de passageiros'**
  String get vehicleUsagePassengerTransport;

  /// No description provided for @vehicleUsageGoods.
  ///
  /// In pt, this message translates to:
  /// **'Transporte de mercadorias'**
  String get vehicleUsageGoods;

  /// No description provided for @vehicleUsageTaxi.
  ///
  /// In pt, this message translates to:
  /// **'Táxi'**
  String get vehicleUsageTaxi;

  /// No description provided for @vehicleUsageDrivingSchool.
  ///
  /// In pt, this message translates to:
  /// **'Escola de condução'**
  String get vehicleUsageDrivingSchool;

  /// No description provided for @vehiclesAddButton.
  ///
  /// In pt, this message translates to:
  /// **'Acrescentar veículo'**
  String get vehiclesAddButton;

  /// No description provided for @vehiclesEmpty.
  ///
  /// In pt, this message translates to:
  /// **'Ainda não registou nenhum veículo.'**
  String get vehiclesEmpty;

  /// No description provided for @vehiclesBackToList.
  ///
  /// In pt, this message translates to:
  /// **'Ver os meus veículos'**
  String get vehiclesBackToList;

  /// No description provided for @vehicleMakeModelYear.
  ///
  /// In pt, this message translates to:
  /// **'{make} {model} · {year}'**
  String vehicleMakeModelYear(String make, String model, String year);

  /// No description provided for @vehicleHabitualDriver.
  ///
  /// In pt, this message translates to:
  /// **'Condutor habitual: {name}'**
  String vehicleHabitualDriver(String name);

  /// No description provided for @vehicleNoHabitualDriver.
  ///
  /// In pt, this message translates to:
  /// **'Sem condutor habitual'**
  String get vehicleNoHabitualDriver;

  /// No description provided for @vehicleDocMissing.
  ///
  /// In pt, this message translates to:
  /// **'Livrete em falta'**
  String get vehicleDocMissing;

  /// No description provided for @vehicleRegistryVerified.
  ///
  /// In pt, this message translates to:
  /// **'Confirmado no registo'**
  String get vehicleRegistryVerified;

  /// No description provided for @vehicleRegistryNotVerified.
  ///
  /// In pt, this message translates to:
  /// **'Não confirmado no registo'**
  String get vehicleRegistryNotVerified;

  /// No description provided for @vehicleNewTitle.
  ///
  /// In pt, this message translates to:
  /// **'Acrescentar veículo'**
  String get vehicleNewTitle;

  /// No description provided for @vehicleEditTitle.
  ///
  /// In pt, this message translates to:
  /// **'Alterar veículo'**
  String get vehicleEditTitle;

  /// No description provided for @vehicleFieldPlate.
  ///
  /// In pt, this message translates to:
  /// **'Matrícula'**
  String get vehicleFieldPlate;

  /// No description provided for @vehiclePlateHelper.
  ///
  /// In pt, this message translates to:
  /// **'Ex.: {examples}'**
  String vehiclePlateHelper(String examples);

  /// Separador dos exemplos de matrícula.
  ///
  /// In pt, this message translates to:
  /// **' ou '**
  String get vehicleOr;

  /// No description provided for @vehicleRegistryLookup.
  ///
  /// In pt, this message translates to:
  /// **'Procurar no registo'**
  String get vehicleRegistryLookup;

  /// No description provided for @vehicleRegistryFilled.
  ///
  /// In pt, this message translates to:
  /// **'Preenchemos os dados a partir do registo automóvel. Confirme-os com o seu livrete.'**
  String get vehicleRegistryFilled;

  /// No description provided for @vehicleFieldChassis.
  ///
  /// In pt, this message translates to:
  /// **'N.º de chassis'**
  String get vehicleFieldChassis;

  /// No description provided for @vehicleFieldMake.
  ///
  /// In pt, this message translates to:
  /// **'Marca'**
  String get vehicleFieldMake;

  /// No description provided for @vehicleFieldModel.
  ///
  /// In pt, this message translates to:
  /// **'Modelo'**
  String get vehicleFieldModel;

  /// No description provided for @vehicleFieldYear.
  ///
  /// In pt, this message translates to:
  /// **'Ano'**
  String get vehicleFieldYear;

  /// No description provided for @vehicleFieldCategory.
  ///
  /// In pt, this message translates to:
  /// **'Categoria'**
  String get vehicleFieldCategory;

  /// No description provided for @vehicleFieldEngineCc.
  ///
  /// In pt, this message translates to:
  /// **'Cilindrada (cm³)'**
  String get vehicleFieldEngineCc;

  /// No description provided for @vehicleFieldSeats.
  ///
  /// In pt, this message translates to:
  /// **'Lugares'**
  String get vehicleFieldSeats;

  /// No description provided for @vehicleFieldGrossWeight.
  ///
  /// In pt, this message translates to:
  /// **'Peso bruto (kg)'**
  String get vehicleFieldGrossWeight;

  /// No description provided for @vehicleFieldUsage.
  ///
  /// In pt, this message translates to:
  /// **'Uso'**
  String get vehicleFieldUsage;

  /// No description provided for @vehicleFieldProvince.
  ///
  /// In pt, this message translates to:
  /// **'Província de circulação'**
  String get vehicleFieldProvince;

  /// No description provided for @vehicleFieldCommercialValue.
  ///
  /// In pt, this message translates to:
  /// **'Valor comercial'**
  String get vehicleFieldCommercialValue;

  /// No description provided for @vehicleCommercialValueHelper.
  ///
  /// In pt, this message translates to:
  /// **'Necessário para seguros de danos próprios.'**
  String get vehicleCommercialValueHelper;

  /// No description provided for @vehicleFieldHasTracker.
  ///
  /// In pt, this message translates to:
  /// **'Tem localizador (GPS)'**
  String get vehicleFieldHasTracker;

  /// No description provided for @vehicleCreated.
  ///
  /// In pt, this message translates to:
  /// **'Veículo acrescentado. Envie agora a foto do livrete.'**
  String get vehicleCreated;

  /// No description provided for @vehicleConflict.
  ///
  /// In pt, this message translates to:
  /// **'Este veículo foi alterado noutro dispositivo'**
  String get vehicleConflict;

  /// No description provided for @vehicleEdit.
  ///
  /// In pt, this message translates to:
  /// **'Alterar'**
  String get vehicleEdit;

  /// No description provided for @vehicleRemove.
  ///
  /// In pt, this message translates to:
  /// **'Retirar veículo'**
  String get vehicleRemove;

  /// No description provided for @vehicleRemoveConfirmTitle.
  ///
  /// In pt, this message translates to:
  /// **'Retirar veículo?'**
  String get vehicleRemoveConfirmTitle;

  /// No description provided for @vehicleRemoveConfirmText.
  ///
  /// In pt, this message translates to:
  /// **'O veículo deixa de aparecer na sua conta.'**
  String get vehicleRemoveConfirmText;

  /// No description provided for @vehicleRemoveConfirm.
  ///
  /// In pt, this message translates to:
  /// **'Retirar'**
  String get vehicleRemoveConfirm;

  /// No description provided for @vehicleRemoved.
  ///
  /// In pt, this message translates to:
  /// **'Veículo retirado'**
  String get vehicleRemoved;

  /// No description provided for @vehicleSectionData.
  ///
  /// In pt, this message translates to:
  /// **'Dados'**
  String get vehicleSectionData;

  /// No description provided for @vehicleSectionDoc.
  ///
  /// In pt, this message translates to:
  /// **'Livrete'**
  String get vehicleSectionDoc;

  /// No description provided for @vehicleSectionDrivers.
  ///
  /// In pt, this message translates to:
  /// **'Condutores'**
  String get vehicleSectionDrivers;

  /// No description provided for @vehicleDocNone.
  ///
  /// In pt, this message translates to:
  /// **'Ainda não enviou o livrete.'**
  String get vehicleDocNone;

  /// No description provided for @vehicleDocUpload.
  ///
  /// In pt, this message translates to:
  /// **'Enviar livrete'**
  String get vehicleDocUpload;

  /// No description provided for @vehicleDocReplace.
  ///
  /// In pt, this message translates to:
  /// **'Substituir'**
  String get vehicleDocReplace;

  /// No description provided for @vehicleDocOpen.
  ///
  /// In pt, this message translates to:
  /// **'Abrir'**
  String get vehicleDocOpen;

  /// No description provided for @vehicleDocUploading.
  ///
  /// In pt, this message translates to:
  /// **'A enviar…'**
  String get vehicleDocUploading;

  /// No description provided for @vehicleDocSaved.
  ///
  /// In pt, this message translates to:
  /// **'Livrete guardado'**
  String get vehicleDocSaved;

  /// No description provided for @fileOpenFailed.
  ///
  /// In pt, this message translates to:
  /// **'Não há nenhuma aplicação para abrir este ficheiro.'**
  String get fileOpenFailed;

  /// No description provided for @vehicleNoDrivers.
  ///
  /// In pt, this message translates to:
  /// **'Nenhum condutor associado.'**
  String get vehicleNoDrivers;

  /// No description provided for @vehicleDriverHabitual.
  ///
  /// In pt, this message translates to:
  /// **'Habitual'**
  String get vehicleDriverHabitual;

  /// No description provided for @vehicleManageDrivers.
  ///
  /// In pt, this message translates to:
  /// **'Gerir condutores'**
  String get vehicleManageDrivers;

  /// No description provided for @manageDriversHelp.
  ///
  /// In pt, this message translates to:
  /// **'Marque até {max} condutores e escolha o habitual.'**
  String manageDriversHelp(int max);

  /// No description provided for @docSourceCamera.
  ///
  /// In pt, this message translates to:
  /// **'Tirar foto'**
  String get docSourceCamera;

  /// No description provided for @docSourceGallery.
  ///
  /// In pt, this message translates to:
  /// **'Escolher da galeria'**
  String get docSourceGallery;

  /// No description provided for @docSourcePdf.
  ///
  /// In pt, this message translates to:
  /// **'Escolher PDF'**
  String get docSourcePdf;

  /// No description provided for @cameraFrameHint.
  ///
  /// In pt, this message translates to:
  /// **'Enquadre o livrete dentro do rectângulo, com boa luz e sem reflexos.'**
  String get cameraFrameHint;

  /// No description provided for @cameraPermissionDenied.
  ///
  /// In pt, this message translates to:
  /// **'Para fazer a vistoria, a MoSeguros precisa de acesso à câmara.'**
  String get cameraPermissionDenied;

  /// No description provided for @cameraOpenSettings.
  ///
  /// In pt, this message translates to:
  /// **'Abrir definições'**
  String get cameraOpenSettings;

  /// No description provided for @cameraUnavailable.
  ///
  /// In pt, this message translates to:
  /// **'Não foi possível abrir a câmara. Escolha uma foto da galeria.'**
  String get cameraUnavailable;

  /// No description provided for @cameraCapture.
  ///
  /// In pt, this message translates to:
  /// **'Tirar foto'**
  String get cameraCapture;

  /// No description provided for @cameraFlashOff.
  ///
  /// In pt, this message translates to:
  /// **'Flash desligado'**
  String get cameraFlashOff;

  /// No description provided for @cameraFlashAuto.
  ///
  /// In pt, this message translates to:
  /// **'Flash automático'**
  String get cameraFlashAuto;

  /// No description provided for @cameraFlashOn.
  ///
  /// In pt, this message translates to:
  /// **'Flash sempre ligado'**
  String get cameraFlashOn;

  /// No description provided for @photoRetake.
  ///
  /// In pt, this message translates to:
  /// **'Repetir'**
  String get photoRetake;

  /// No description provided for @photoUse.
  ///
  /// In pt, this message translates to:
  /// **'Usar foto'**
  String get photoUse;

  /// No description provided for @driversTitle.
  ///
  /// In pt, this message translates to:
  /// **'Condutores'**
  String get driversTitle;

  /// No description provided for @driversAddButton.
  ///
  /// In pt, this message translates to:
  /// **'Registar condutor'**
  String get driversAddButton;

  /// No description provided for @driversEmpty.
  ///
  /// In pt, this message translates to:
  /// **'Ainda não registou condutores.'**
  String get driversEmpty;

  /// No description provided for @driversBackToList.
  ///
  /// In pt, this message translates to:
  /// **'Ver os meus condutores'**
  String get driversBackToList;

  /// No description provided for @driverEditTitle.
  ///
  /// In pt, this message translates to:
  /// **'Alterar condutor'**
  String get driverEditTitle;

  /// No description provided for @driverOwnerTag.
  ///
  /// In pt, this message translates to:
  /// **'Eu'**
  String get driverOwnerTag;

  /// No description provided for @driverCategories.
  ///
  /// In pt, this message translates to:
  /// **'Categorias: {categories}'**
  String driverCategories(String categories);

  /// No description provided for @driverLicenceMasked.
  ///
  /// In pt, this message translates to:
  /// **'Carta: {masked}'**
  String driverLicenceMasked(String masked);

  /// No description provided for @driverVehicleCount.
  ///
  /// In pt, this message translates to:
  /// **'{count, plural, =0{Sem veículos} =1{1 veículo} other{{count} veículos}}'**
  String driverVehicleCount(int count);

  /// No description provided for @driverEdit.
  ///
  /// In pt, this message translates to:
  /// **'Alterar'**
  String get driverEdit;

  /// No description provided for @driverRemove.
  ///
  /// In pt, this message translates to:
  /// **'Retirar'**
  String get driverRemove;

  /// No description provided for @driverRemoveConfirmTitle.
  ///
  /// In pt, this message translates to:
  /// **'Retirar condutor?'**
  String get driverRemoveConfirmTitle;

  /// No description provided for @driverRemoveConfirmText.
  ///
  /// In pt, this message translates to:
  /// **'O condutor deixa de aparecer na sua conta.'**
  String get driverRemoveConfirmText;

  /// No description provided for @driverRemoved.
  ///
  /// In pt, this message translates to:
  /// **'Condutor retirado'**
  String get driverRemoved;

  /// No description provided for @driverFieldIsMe.
  ///
  /// In pt, this message translates to:
  /// **'Sou eu'**
  String get driverFieldIsMe;

  /// No description provided for @driverFieldLicenceNumber.
  ///
  /// In pt, this message translates to:
  /// **'N.º da carta'**
  String get driverFieldLicenceNumber;

  /// No description provided for @driverChangeLicence.
  ///
  /// In pt, this message translates to:
  /// **'Alterar o n.º da carta'**
  String get driverChangeLicence;

  /// No description provided for @driverFieldLicenceCategories.
  ///
  /// In pt, this message translates to:
  /// **'Categorias'**
  String get driverFieldLicenceCategories;

  /// No description provided for @driverFieldLicenceIssueDate.
  ///
  /// In pt, this message translates to:
  /// **'Data de emissão da carta'**
  String get driverFieldLicenceIssueDate;

  /// No description provided for @driverConflict.
  ///
  /// In pt, this message translates to:
  /// **'Este condutor foi alterado noutro dispositivo'**
  String get driverConflict;

  /// No description provided for @errorQuoteRequestNotFound.
  ///
  /// In pt, this message translates to:
  /// **'Cotação não encontrada.'**
  String get errorQuoteRequestNotFound;

  /// No description provided for @errorQuoteNotFound.
  ///
  /// In pt, this message translates to:
  /// **'Cotação não encontrada.'**
  String get errorQuoteNotFound;

  /// No description provided for @errorQuoteExpired.
  ///
  /// In pt, this message translates to:
  /// **'Esta cotação já expirou. Faça uma nova simulação.'**
  String get errorQuoteExpired;

  /// No description provided for @errorQuoteNotAvailable.
  ///
  /// In pt, this message translates to:
  /// **'Esta seguradora não apresentou proposta.'**
  String get errorQuoteNotAvailable;

  /// No description provided for @errorQuoteAlreadyUsed.
  ///
  /// In pt, this message translates to:
  /// **'Esta cotação já foi usada numa proposta.'**
  String get errorQuoteAlreadyUsed;

  /// No description provided for @errorQuoteRateLimited.
  ///
  /// In pt, this message translates to:
  /// **'Fez muitas simulações na última hora. Tente mais tarde.'**
  String get errorQuoteRateLimited;

  /// No description provided for @errorVehicleValueRequired.
  ///
  /// In pt, this message translates to:
  /// **'Para danos próprios, indique o valor comercial do veículo.'**
  String get errorVehicleValueRequired;

  /// No description provided for @errorOrganizationNotInsurer.
  ///
  /// In pt, this message translates to:
  /// **'Esta organização não é uma seguradora.'**
  String get errorOrganizationNotInsurer;

  /// No description provided for @validationStartDateOutOfRange.
  ///
  /// In pt, this message translates to:
  /// **'A data de início tem de ser entre hoje e daqui a 60 dias.'**
  String get validationStartDateOutOfRange;

  /// No description provided for @validationCoverageNotAllowedForType.
  ///
  /// In pt, this message translates to:
  /// **'Esta cobertura não existe neste tipo de seguro.'**
  String get validationCoverageNotAllowedForType;

  /// No description provided for @validationOwnDamageRequired.
  ///
  /// In pt, this message translates to:
  /// **'Escolha pelo menos uma cobertura de danos próprios.'**
  String get validationOwnDamageRequired;

  /// No description provided for @validationCoverageRequires.
  ///
  /// In pt, this message translates to:
  /// **'Furto ou roubo exige choque, incêndio e fenómenos da natureza.'**
  String get validationCoverageRequires;

  /// No description provided for @validationInvalidUrl.
  ///
  /// In pt, this message translates to:
  /// **'Endereço inválido (tem de começar por https://).'**
  String get validationInvalidUrl;

  /// No description provided for @validationInvalidSecretRef.
  ///
  /// In pt, this message translates to:
  /// **'Referência do segredo inválida.'**
  String get validationInvalidSecretRef;

  /// No description provided for @errorProfileIncomplete.
  ///
  /// In pt, this message translates to:
  /// **'Complete o seu perfil antes de continuar.'**
  String get errorProfileIncomplete;

  /// No description provided for @errorKycAlreadyPending.
  ///
  /// In pt, this message translates to:
  /// **'Já tem uma verificação de identidade em análise.'**
  String get errorKycAlreadyPending;

  /// No description provided for @errorKycAlreadyVerified.
  ///
  /// In pt, this message translates to:
  /// **'A sua identidade já está verificada.'**
  String get errorKycAlreadyVerified;

  /// No description provided for @errorKycNotFound.
  ///
  /// In pt, this message translates to:
  /// **'Verificação de identidade não encontrada.'**
  String get errorKycNotFound;

  /// No description provided for @validationInvalidInsurer.
  ///
  /// In pt, this message translates to:
  /// **'Seguradora inválida.'**
  String get validationInvalidInsurer;

  /// No description provided for @errorQuoteDataChanged.
  ///
  /// In pt, this message translates to:
  /// **'Os dados do veículo ou do condutor mudaram depois da simulação. Faça uma nova simulação.'**
  String get errorQuoteDataChanged;

  /// No description provided for @errorProposalAlreadyExists.
  ///
  /// In pt, this message translates to:
  /// **'Já existe uma proposta para esta cotação.'**
  String get errorProposalAlreadyExists;

  /// No description provided for @errorProposalNotFound.
  ///
  /// In pt, this message translates to:
  /// **'Proposta não encontrada.'**
  String get errorProposalNotFound;

  /// No description provided for @errorProposalNotEditable.
  ///
  /// In pt, this message translates to:
  /// **'Esta proposta já não pode ser alterada.'**
  String get errorProposalNotEditable;

  /// No description provided for @errorInsurerNotActive.
  ///
  /// In pt, this message translates to:
  /// **'Esta seguradora não está a aceitar propostas neste momento.'**
  String get errorInsurerNotActive;

  /// No description provided for @errorSuitabilityNotWarned.
  ///
  /// In pt, this message translates to:
  /// **'Não há avisos de adequação para confirmar.'**
  String get errorSuitabilityNotWarned;

  /// No description provided for @errorPdfTemplateNotFound.
  ///
  /// In pt, this message translates to:
  /// **'Não foi possível gerar o documento. Tente de novo.'**
  String get errorPdfTemplateNotFound;

  /// No description provided for @errorPdfRenderFailed.
  ///
  /// In pt, this message translates to:
  /// **'Não foi possível gerar o documento. Tente de novo.'**
  String get errorPdfRenderFailed;

  /// No description provided for @errorSignerContactMissing.
  ///
  /// In pt, this message translates to:
  /// **'Não há telemóvel nem e-mail verificado para enviar o código.'**
  String get errorSignerContactMissing;

  /// No description provided for @errorProposalIncomplete.
  ///
  /// In pt, this message translates to:
  /// **'Ainda faltam passos para concluir a proposta.'**
  String get errorProposalIncomplete;

  /// No description provided for @errorSignRequestNotFound.
  ///
  /// In pt, this message translates to:
  /// **'Este código já não é válido. Peça um novo.'**
  String get errorSignRequestNotFound;

  /// No description provided for @errorProposalChangedSinceSignRequest.
  ///
  /// In pt, this message translates to:
  /// **'A proposta mudou depois de pedir o código. Peça um novo.'**
  String get errorProposalChangedSinceSignRequest;

  /// No description provided for @proposalMissingDeclarations.
  ///
  /// In pt, this message translates to:
  /// **'Declarações de risco'**
  String get proposalMissingDeclarations;

  /// No description provided for @proposalMissingSuitability.
  ///
  /// In pt, this message translates to:
  /// **'Questionário de adequação'**
  String get proposalMissingSuitability;

  /// No description provided for @proposalMissingSuitabilityAcknowledgement.
  ///
  /// In pt, this message translates to:
  /// **'Confirmar os avisos de adequação'**
  String get proposalMissingSuitabilityAcknowledgement;

  /// No description provided for @proposalMissingInspection.
  ///
  /// In pt, this message translates to:
  /// **'Vistoria do veículo'**
  String get proposalMissingInspection;

  /// No description provided for @proposalMissingStartDate.
  ///
  /// In pt, this message translates to:
  /// **'Data de início'**
  String get proposalMissingStartDate;

  /// No description provided for @proposalMissingSignature.
  ///
  /// In pt, this message translates to:
  /// **'Assinatura'**
  String get proposalMissingSignature;

  /// No description provided for @validationAnswersIncomplete.
  ///
  /// In pt, this message translates to:
  /// **'Responda a todas as perguntas.'**
  String get validationAnswersIncomplete;

  /// No description provided for @validationAnswerDuplicated.
  ///
  /// In pt, this message translates to:
  /// **'Pergunta repetida.'**
  String get validationAnswerDuplicated;

  /// No description provided for @validationUnknownQuestion.
  ///
  /// In pt, this message translates to:
  /// **'Pergunta desconhecida.'**
  String get validationUnknownQuestion;

  /// No description provided for @validationInvalidAnswer.
  ///
  /// In pt, this message translates to:
  /// **'Resposta inválida.'**
  String get validationInvalidAnswer;

  /// No description provided for @validationMustBeTrue.
  ///
  /// In pt, this message translates to:
  /// **'Tem de confirmar para continuar.'**
  String get validationMustBeTrue;

  /// No description provided for @errorInspectionNotRequired.
  ///
  /// In pt, this message translates to:
  /// **'Esta proposta não precisa de vistoria.'**
  String get errorInspectionNotRequired;

  /// No description provided for @errorInspectionNotFound.
  ///
  /// In pt, this message translates to:
  /// **'Vistoria não encontrada.'**
  String get errorInspectionNotFound;

  /// No description provided for @errorInspectionNotEditable.
  ///
  /// In pt, this message translates to:
  /// **'A vistoria já foi enviada e não pode ser alterada.'**
  String get errorInspectionNotEditable;

  /// No description provided for @errorInspectionPhotosMissing.
  ///
  /// In pt, this message translates to:
  /// **'Faltam fotografias obrigatórias.'**
  String get errorInspectionPhotosMissing;

  /// No description provided for @errorPhotoNotFound.
  ///
  /// In pt, this message translates to:
  /// **'Fotografia não encontrada.'**
  String get errorPhotoNotFound;

  /// No description provided for @errorProposalNotUnderReview.
  ///
  /// In pt, this message translates to:
  /// **'A proposta não está em análise.'**
  String get errorProposalNotUnderReview;

  /// No description provided for @validationInvalidSlot.
  ///
  /// In pt, this message translates to:
  /// **'Ângulo inválido.'**
  String get validationInvalidSlot;

  /// No description provided for @validationExtraPhotosLimit.
  ///
  /// In pt, this message translates to:
  /// **'Já enviou o número máximo de fotografias extra.'**
  String get validationExtraPhotosLimit;

  /// No description provided for @validationPhotoTooOld.
  ///
  /// In pt, this message translates to:
  /// **'Esta fotografia foi tirada há mais de 7 dias. Tire uma nova.'**
  String get validationPhotoTooOld;

  /// No description provided for @validationPhotoInFuture.
  ///
  /// In pt, this message translates to:
  /// **'A data da fotografia é inválida. Confirme a hora do telemóvel.'**
  String get validationPhotoInFuture;

  /// No description provided for @validationInvalidCoordinates.
  ///
  /// In pt, this message translates to:
  /// **'Localização inválida.'**
  String get validationInvalidCoordinates;

  /// No description provided for @inspectionSlotFront.
  ///
  /// In pt, this message translates to:
  /// **'Frente'**
  String get inspectionSlotFront;

  /// No description provided for @inspectionSlotRear.
  ///
  /// In pt, this message translates to:
  /// **'Traseira'**
  String get inspectionSlotRear;

  /// No description provided for @inspectionSlotLeft.
  ///
  /// In pt, this message translates to:
  /// **'Lado esquerdo'**
  String get inspectionSlotLeft;

  /// No description provided for @inspectionSlotRight.
  ///
  /// In pt, this message translates to:
  /// **'Lado direito'**
  String get inspectionSlotRight;

  /// No description provided for @inspectionSlotOdometer.
  ///
  /// In pt, this message translates to:
  /// **'Conta-quilómetros'**
  String get inspectionSlotOdometer;

  /// No description provided for @inspectionSlotChassis.
  ///
  /// In pt, this message translates to:
  /// **'Número do chassis'**
  String get inspectionSlotChassis;

  /// No description provided for @inspectionSlotExtra.
  ///
  /// In pt, this message translates to:
  /// **'Outra fotografia'**
  String get inspectionSlotExtra;

  /// No description provided for @quoteUnavailableTariffMissing.
  ///
  /// In pt, this message translates to:
  /// **'Esta seguradora ainda não tem preço para este produto.'**
  String get quoteUnavailableTariffMissing;

  /// No description provided for @quoteUnavailableTariffInvalid.
  ///
  /// In pt, this message translates to:
  /// **'Não foi possível calcular o preço deste produto.'**
  String get quoteUnavailableTariffInvalid;

  /// No description provided for @quoteUnavailableCoverageNotOffered.
  ///
  /// In pt, this message translates to:
  /// **'Este produto não inclui todas as coberturas escolhidas.'**
  String get quoteUnavailableCoverageNotOffered;

  /// No description provided for @quoteUnavailableCoverageRequirementsNotMet.
  ///
  /// In pt, this message translates to:
  /// **'Neste produto, as coberturas escolhidas exigem outras que não escolheu.'**
  String get quoteUnavailableCoverageRequirementsNotMet;

  /// No description provided for @quoteUnavailableCapitalNotOffered.
  ///
  /// In pt, this message translates to:
  /// **'Este produto não oferece o capital escolhido.'**
  String get quoteUnavailableCapitalNotOffered;

  /// No description provided for @quoteUnavailableVehicleValueRequired.
  ///
  /// In pt, this message translates to:
  /// **'É preciso indicar o valor comercial do veículo.'**
  String get quoteUnavailableVehicleValueRequired;

  /// No description provided for @quoteUnavailableDurationNotRated.
  ///
  /// In pt, this message translates to:
  /// **'Este produto não está disponível para a duração escolhida.'**
  String get quoteUnavailableDurationNotRated;

  /// No description provided for @quoteUnavailableNoMatchingRow.
  ///
  /// In pt, this message translates to:
  /// **'Esta seguradora não cobre este veículo ou condutor nestas condições.'**
  String get quoteUnavailableNoMatchingRow;

  /// No description provided for @quoteUnavailableInsurerTimeout.
  ///
  /// In pt, this message translates to:
  /// **'A seguradora não respondeu a tempo.'**
  String get quoteUnavailableInsurerTimeout;

  /// No description provided for @quoteUnavailableInsurerUnavailable.
  ///
  /// In pt, this message translates to:
  /// **'A seguradora está indisponível neste momento.'**
  String get quoteUnavailableInsurerUnavailable;

  /// No description provided for @quoteUnavailableInsurerDeclined.
  ///
  /// In pt, this message translates to:
  /// **'A seguradora não apresentou proposta.'**
  String get quoteUnavailableInsurerDeclined;

  /// No description provided for @quoteUnavailableExternalInvalidResponse.
  ///
  /// In pt, this message translates to:
  /// **'A seguradora devolveu uma resposta inválida.'**
  String get quoteUnavailableExternalInvalidResponse;

  /// No description provided for @quoteUnavailableEvaluationError.
  ///
  /// In pt, this message translates to:
  /// **'Não foi possível calcular o preço.'**
  String get quoteUnavailableEvaluationError;

  /// No description provided for @coverageMtpl.
  ///
  /// In pt, this message translates to:
  /// **'Responsabilidade civil obrigatória'**
  String get coverageMtpl;

  /// No description provided for @coverageVtpl.
  ///
  /// In pt, this message translates to:
  /// **'Responsabilidade civil facultativa'**
  String get coverageVtpl;

  /// No description provided for @coverageOdCollision.
  ///
  /// In pt, this message translates to:
  /// **'Choque, colisão e capotamento'**
  String get coverageOdCollision;

  /// No description provided for @coverageOdGlass.
  ///
  /// In pt, this message translates to:
  /// **'Quebra isolada de vidros'**
  String get coverageOdGlass;

  /// No description provided for @coverageOdFire.
  ///
  /// In pt, this message translates to:
  /// **'Incêndio, raio e explosão'**
  String get coverageOdFire;

  /// No description provided for @coverageOdNature.
  ///
  /// In pt, this message translates to:
  /// **'Fenómenos da natureza'**
  String get coverageOdNature;

  /// No description provided for @coverageOdTheft.
  ///
  /// In pt, this message translates to:
  /// **'Furto ou roubo'**
  String get coverageOdTheft;

  /// No description provided for @coverageOccupants.
  ///
  /// In pt, this message translates to:
  /// **'Ocupantes'**
  String get coverageOccupants;

  /// No description provided for @coverageAssistance.
  ///
  /// In pt, this message translates to:
  /// **'Assistência em viagem'**
  String get coverageAssistance;

  /// No description provided for @coverageLegal.
  ///
  /// In pt, this message translates to:
  /// **'Protecção jurídica'**
  String get coverageLegal;

  /// No description provided for @coverageSrcc.
  ///
  /// In pt, this message translates to:
  /// **'Greves, tumultos e alterações da ordem pública'**
  String get coverageSrcc;

  /// No description provided for @coverageCrossBorder.
  ///
  /// In pt, this message translates to:
  /// **'Circulação no estrangeiro'**
  String get coverageCrossBorder;

  /// No description provided for @quoteWizardTitle.
  ///
  /// In pt, this message translates to:
  /// **'Simular seguro'**
  String get quoteWizardTitle;

  /// No description provided for @quoteStepOf.
  ///
  /// In pt, this message translates to:
  /// **'Passo {step} de {total}'**
  String quoteStepOf(int step, int total);

  /// No description provided for @quoteStepVehicle.
  ///
  /// In pt, this message translates to:
  /// **'Veículo e condutor'**
  String get quoteStepVehicle;

  /// No description provided for @quoteStepType.
  ///
  /// In pt, this message translates to:
  /// **'Tipo de seguro'**
  String get quoteStepType;

  /// No description provided for @quoteStepCoverages.
  ///
  /// In pt, this message translates to:
  /// **'Coberturas'**
  String get quoteStepCoverages;

  /// No description provided for @quoteStepPeriod.
  ///
  /// In pt, this message translates to:
  /// **'Início e duração'**
  String get quoteStepPeriod;

  /// No description provided for @quoteStepSummary.
  ///
  /// In pt, this message translates to:
  /// **'Resumo'**
  String get quoteStepSummary;

  /// No description provided for @quoteBack.
  ///
  /// In pt, this message translates to:
  /// **'Voltar'**
  String get quoteBack;

  /// No description provided for @quoteContinue.
  ///
  /// In pt, this message translates to:
  /// **'Continuar'**
  String get quoteContinue;

  /// No description provided for @quoteVehicleLabel.
  ///
  /// In pt, this message translates to:
  /// **'Veículo'**
  String get quoteVehicleLabel;

  /// No description provided for @quoteVehicleLine.
  ///
  /// In pt, this message translates to:
  /// **'{make} {model} · {year}'**
  String quoteVehicleLine(String make, String model, int year);

  /// No description provided for @quoteNoVehicles.
  ///
  /// In pt, this message translates to:
  /// **'Ainda não tem veículos registados.'**
  String get quoteNoVehicles;

  /// No description provided for @quoteAddVehicle.
  ///
  /// In pt, this message translates to:
  /// **'Acrescentar veículo'**
  String get quoteAddVehicle;

  /// No description provided for @quoteDriverLabel.
  ///
  /// In pt, this message translates to:
  /// **'Condutor'**
  String get quoteDriverLabel;

  /// No description provided for @quoteNoDrivers.
  ///
  /// In pt, this message translates to:
  /// **'Ainda não tem condutores registados.'**
  String get quoteNoDrivers;

  /// No description provided for @quoteAddDriver.
  ///
  /// In pt, this message translates to:
  /// **'Acrescentar condutor'**
  String get quoteAddDriver;

  /// No description provided for @quoteStartDateMoved.
  ///
  /// In pt, this message translates to:
  /// **'Actualizámos a data de início para hoje.'**
  String get quoteStartDateMoved;

  /// No description provided for @quoteTypeMtplOnly.
  ///
  /// In pt, this message translates to:
  /// **'Só responsabilidade civil'**
  String get quoteTypeMtplOnly;

  /// No description provided for @quoteTypeMtplOnlyText.
  ///
  /// In pt, this message translates to:
  /// **'O seguro obrigatório: cobre os danos que causar a outros.'**
  String get quoteTypeMtplOnlyText;

  /// No description provided for @quoteTypeComprehensive.
  ///
  /// In pt, this message translates to:
  /// **'Responsabilidade civil + danos próprios'**
  String get quoteTypeComprehensive;

  /// No description provided for @quoteTypeComprehensiveText.
  ///
  /// In pt, this message translates to:
  /// **'Cobre também os danos no seu veículo.'**
  String get quoteTypeComprehensiveText;

  /// No description provided for @quoteTypeTemporary.
  ///
  /// In pt, this message translates to:
  /// **'Seguro temporário'**
  String get quoteTypeTemporary;

  /// No description provided for @quoteTypeTemporaryText.
  ///
  /// In pt, this message translates to:
  /// **'Responsabilidade civil por 7 a 60 dias, ou um ano.'**
  String get quoteTypeTemporaryText;

  /// No description provided for @quoteNeedsCommercialValue.
  ///
  /// In pt, this message translates to:
  /// **'Precisa do valor comercial do veículo'**
  String get quoteNeedsCommercialValue;

  /// No description provided for @quoteSetCommercialValue.
  ///
  /// In pt, this message translates to:
  /// **'Indicar valor'**
  String get quoteSetCommercialValue;

  /// No description provided for @quoteCoverageMandatory.
  ///
  /// In pt, this message translates to:
  /// **'Obrigatória'**
  String get quoteCoverageMandatory;

  /// No description provided for @quoteCoverageAvailableIn.
  ///
  /// In pt, this message translates to:
  /// **'Disponível em {count} de {total} produtos'**
  String quoteCoverageAvailableIn(int count, int total);

  /// No description provided for @quoteCoverageCapital.
  ///
  /// In pt, this message translates to:
  /// **'Capital'**
  String get quoteCoverageCapital;

  /// No description provided for @quoteCapitalPerPerson.
  ///
  /// In pt, this message translates to:
  /// **'{capital} por pessoa'**
  String quoteCapitalPerPerson(String capital);

  /// No description provided for @quoteTheftNotice.
  ///
  /// In pt, this message translates to:
  /// **'Furto ou roubo inclui obrigatoriamente choque, incêndio e fenómenos da natureza.'**
  String get quoteTheftNotice;

  /// No description provided for @quoteNoProducts.
  ///
  /// In pt, this message translates to:
  /// **'De momento não há produtos deste tipo para este veículo.'**
  String get quoteNoProducts;

  /// No description provided for @quoteStartDate.
  ///
  /// In pt, this message translates to:
  /// **'Data de início'**
  String get quoteStartDate;

  /// No description provided for @quoteDuration.
  ///
  /// In pt, this message translates to:
  /// **'Duração'**
  String get quoteDuration;

  /// No description provided for @quoteDurationDays.
  ///
  /// In pt, this message translates to:
  /// **'{count} dias'**
  String quoteDurationDays(int count);

  /// No description provided for @quoteDurationYear.
  ///
  /// In pt, this message translates to:
  /// **'1 ano'**
  String get quoteDurationYear;

  /// No description provided for @quoteEndsOn.
  ///
  /// In pt, this message translates to:
  /// **'Termina a {date}'**
  String quoteEndsOn(String date);

  /// No description provided for @quoteChange.
  ///
  /// In pt, this message translates to:
  /// **'Alterar'**
  String get quoteChange;

  /// No description provided for @quoteDriverAges.
  ///
  /// In pt, this message translates to:
  /// **'{age} anos, carta há {years} anos'**
  String quoteDriverAges(int age, int years);

  /// No description provided for @quotePeriodRange.
  ///
  /// In pt, this message translates to:
  /// **'{start} a {end}'**
  String quotePeriodRange(String start, String end);

  /// No description provided for @quoteRequesting.
  ///
  /// In pt, this message translates to:
  /// **'A pedir propostas às seguradoras…'**
  String get quoteRequesting;

  /// No description provided for @quoteRetryInMinutes.
  ///
  /// In pt, this message translates to:
  /// **'{count, plural, =1{Tente de novo dentro de 1 minuto.} other{Tente de novo dentro de {count} minutos.}}'**
  String quoteRetryInMinutes(int count);

  /// No description provided for @quoteResultsTitle.
  ///
  /// In pt, this message translates to:
  /// **'Propostas'**
  String get quoteResultsTitle;

  /// No description provided for @quoteChangeSimulation.
  ///
  /// In pt, this message translates to:
  /// **'Alterar simulação'**
  String get quoteChangeSimulation;

  /// No description provided for @quoteSavedUntil.
  ///
  /// In pt, this message translates to:
  /// **'Esta simulação fica guardada em As minhas cotações e é válida até {date}.'**
  String quoteSavedUntil(String date);

  /// No description provided for @quoteByBroker.
  ///
  /// In pt, this message translates to:
  /// **'Cotação preparada pelo seu mediador.'**
  String get quoteByBroker;

  /// No description provided for @quoteGoToMyQuotes.
  ///
  /// In pt, this message translates to:
  /// **'Ver as minhas cotações'**
  String get quoteGoToMyQuotes;

  /// No description provided for @quoteIncludesCharges.
  ///
  /// In pt, this message translates to:
  /// **'Inclui {amount} de encargos'**
  String quoteIncludesCharges(String amount);

  /// No description provided for @quoteDeductible.
  ///
  /// In pt, this message translates to:
  /// **'Franquia {value}'**
  String quoteDeductible(String value);

  /// No description provided for @quoteDeductiblePercent.
  ///
  /// In pt, this message translates to:
  /// **'{percent} %'**
  String quoteDeductiblePercent(String percent);

  /// No description provided for @quoteDeductiblePercentMin.
  ///
  /// In pt, this message translates to:
  /// **'{percent} % (mín. {min})'**
  String quoteDeductiblePercentMin(String percent, String min);

  /// No description provided for @quoteNoDeductible.
  ///
  /// In pt, this message translates to:
  /// **'Sem franquia'**
  String get quoteNoDeductible;

  /// No description provided for @quoteValidUntil.
  ///
  /// In pt, this message translates to:
  /// **'Válida até {date}'**
  String quoteValidUntil(String date);

  /// No description provided for @quoteCheapest.
  ///
  /// In pt, this message translates to:
  /// **'Mais barata'**
  String get quoteCheapest;

  /// No description provided for @quoteViewDetail.
  ///
  /// In pt, this message translates to:
  /// **'Ver detalhe'**
  String get quoteViewDetail;

  /// No description provided for @quoteCompare.
  ///
  /// In pt, this message translates to:
  /// **'Comparar'**
  String get quoteCompare;

  /// No description provided for @quoteUsed.
  ///
  /// In pt, this message translates to:
  /// **'Usada numa proposta'**
  String get quoteUsed;

  /// No description provided for @quoteExpiredLabel.
  ///
  /// In pt, this message translates to:
  /// **'Expirada'**
  String get quoteExpiredLabel;

  /// No description provided for @quoteNotAvailableLabel.
  ///
  /// In pt, this message translates to:
  /// **'Sem proposta'**
  String get quoteNotAvailableLabel;

  /// No description provided for @quoteCompareSelected.
  ///
  /// In pt, this message translates to:
  /// **'{count} seleccionadas (máximo 3)'**
  String quoteCompareSelected(int count);

  /// No description provided for @quoteCompareMax.
  ///
  /// In pt, this message translates to:
  /// **'Pode comparar até 3 propostas'**
  String get quoteCompareMax;

  /// No description provided for @quoteUnavailableTitle.
  ///
  /// In pt, this message translates to:
  /// **'Sem proposta ({count})'**
  String quoteUnavailableTitle(int count);

  /// No description provided for @quoteNoOffers.
  ///
  /// In pt, this message translates to:
  /// **'Nenhuma seguradora apresentou proposta para estas condições.'**
  String get quoteNoOffers;

  /// No description provided for @quoteExpiredOn.
  ///
  /// In pt, this message translates to:
  /// **'Esta simulação expirou em {date}.'**
  String quoteExpiredOn(String date);

  /// No description provided for @quoteGeneralConditions.
  ///
  /// In pt, this message translates to:
  /// **'Condições gerais'**
  String get quoteGeneralConditions;

  /// No description provided for @quotePreContractInfo.
  ///
  /// In pt, this message translates to:
  /// **'Informação pré-contratual'**
  String get quotePreContractInfo;

  /// No description provided for @compareTitle.
  ///
  /// In pt, this message translates to:
  /// **'Comparar propostas'**
  String get compareTitle;

  /// No description provided for @compareInsurer.
  ///
  /// In pt, this message translates to:
  /// **'Seguradora'**
  String get compareInsurer;

  /// No description provided for @compareProduct.
  ///
  /// In pt, this message translates to:
  /// **'Produto'**
  String get compareProduct;

  /// No description provided for @compareTotal.
  ///
  /// In pt, this message translates to:
  /// **'Prémio total'**
  String get compareTotal;

  /// No description provided for @compareCommercialPremium.
  ///
  /// In pt, this message translates to:
  /// **'Prémio comercial'**
  String get compareCommercialPremium;

  /// No description provided for @compareNotIncluded.
  ///
  /// In pt, this message translates to:
  /// **'Não incluída'**
  String get compareNotIncluded;

  /// No description provided for @compareValidUntil.
  ///
  /// In pt, this message translates to:
  /// **'Válida até'**
  String get compareValidUntil;

  /// No description provided for @compareDocuments.
  ///
  /// In pt, this message translates to:
  /// **'Documentos'**
  String get compareDocuments;

  /// No description provided for @compareCoverageWithDeductible.
  ///
  /// In pt, this message translates to:
  /// **'{capital} · franquia {deductible} · {premium}'**
  String compareCoverageWithDeductible(
    String capital,
    String deductible,
    String premium,
  );

  /// No description provided for @compareCoverageNoDeductible.
  ///
  /// In pt, this message translates to:
  /// **'{capital} · {premium}'**
  String compareCoverageNoDeductible(String capital, String premium);

  /// No description provided for @quoteDetailTitle.
  ///
  /// In pt, this message translates to:
  /// **'Detalhe da proposta'**
  String get quoteDetailTitle;

  /// No description provided for @quoteProductVersion.
  ///
  /// In pt, this message translates to:
  /// **'{product} · versão {version}'**
  String quoteProductVersion(String product, int version);

  /// No description provided for @quoteDetailCoverages.
  ///
  /// In pt, this message translates to:
  /// **'Coberturas'**
  String get quoteDetailCoverages;

  /// No description provided for @quoteDetailCapital.
  ///
  /// In pt, this message translates to:
  /// **'Capital: {value}'**
  String quoteDetailCapital(String value);

  /// No description provided for @quoteDetailPremium.
  ///
  /// In pt, this message translates to:
  /// **'Prémio'**
  String get quoteDetailPremium;

  /// No description provided for @quoteRoundingAdjustment.
  ///
  /// In pt, this message translates to:
  /// **'Ajuste de arredondamento'**
  String get quoteRoundingAdjustment;

  /// No description provided for @quoteDetailPeriod.
  ///
  /// In pt, this message translates to:
  /// **'Período'**
  String get quoteDetailPeriod;

  /// No description provided for @quoteDetailValidity.
  ///
  /// In pt, this message translates to:
  /// **'Validade'**
  String get quoteDetailValidity;

  /// No description provided for @myQuotesTitle.
  ///
  /// In pt, this message translates to:
  /// **'As minhas cotações'**
  String get myQuotesTitle;

  /// No description provided for @myQuotesEmpty.
  ///
  /// In pt, this message translates to:
  /// **'Ainda não fez simulações.'**
  String get myQuotesEmpty;

  /// No description provided for @myQuotesByBroker.
  ///
  /// In pt, this message translates to:
  /// **'Pelo mediador'**
  String get myQuotesByBroker;

  /// No description provided for @myQuotesStart.
  ///
  /// In pt, this message translates to:
  /// **'Início a {date}'**
  String myQuotesStart(String date);

  /// No description provided for @myQuotesOffers.
  ///
  /// In pt, this message translates to:
  /// **'{count, plural, =0{Sem propostas} =1{1 proposta} other{{count} propostas}}'**
  String myQuotesOffers(int count);

  /// No description provided for @myQuotesFrom.
  ///
  /// In pt, this message translates to:
  /// **'desde {amount}'**
  String myQuotesFrom(String amount);

  /// No description provided for @myQuotesSeeAll.
  ///
  /// In pt, this message translates to:
  /// **'Ver todas'**
  String get myQuotesSeeAll;

  /// No description provided for @errorKycNotVerified.
  ///
  /// In pt, this message translates to:
  /// **'A identidade do cliente ainda não foi verificada.'**
  String get errorKycNotVerified;

  /// No description provided for @errorInspectionNotApproved.
  ///
  /// In pt, this message translates to:
  /// **'A vistoria ainda não foi aprovada.'**
  String get errorInspectionNotApproved;

  /// No description provided for @validationInvalidRules.
  ///
  /// In pt, this message translates to:
  /// **'Regra inválida.'**
  String get validationInvalidRules;

  /// No description provided for @proposalUnderwritingFailureAutoAcceptDisabled.
  ///
  /// In pt, this message translates to:
  /// **'Aceitação automática desligada no produto'**
  String get proposalUnderwritingFailureAutoAcceptDisabled;

  /// No description provided for @proposalUnderwritingFailureKycRequired.
  ///
  /// In pt, this message translates to:
  /// **'Identidade por verificar e prémio acima do limite do KYC simplificado'**
  String get proposalUnderwritingFailureKycRequired;

  /// No description provided for @proposalUnderwritingFailureDeclarationPositive.
  ///
  /// In pt, this message translates to:
  /// **'Há declarações de risco com resposta \"sim\"'**
  String get proposalUnderwritingFailureDeclarationPositive;

  /// No description provided for @proposalUnderwritingFailureInspectionNotApproved.
  ///
  /// In pt, this message translates to:
  /// **'Vistoria por aprovar'**
  String get proposalUnderwritingFailureInspectionNotApproved;

  /// No description provided for @proposalUnderwritingFailureVehicleValueMissing.
  ///
  /// In pt, this message translates to:
  /// **'Valor do veículo não declarado'**
  String get proposalUnderwritingFailureVehicleValueMissing;

  /// No description provided for @proposalUnderwritingFailureVehicleValueAboveLimit.
  ///
  /// In pt, this message translates to:
  /// **'Valor do veículo acima do limite de aceitação automática'**
  String get proposalUnderwritingFailureVehicleValueAboveLimit;

  /// No description provided for @proposalRejectionReasonRiskNotAccepted.
  ///
  /// In pt, this message translates to:
  /// **'Risco não aceite'**
  String get proposalRejectionReasonRiskNotAccepted;

  /// No description provided for @proposalRejectionReasonKycFailed.
  ///
  /// In pt, this message translates to:
  /// **'Identidade não confirmada'**
  String get proposalRejectionReasonKycFailed;

  /// No description provided for @proposalRejectionReasonInspectionFailed.
  ///
  /// In pt, this message translates to:
  /// **'Vistoria não aprovada'**
  String get proposalRejectionReasonInspectionFailed;

  /// No description provided for @proposalRejectionReasonFalseDeclaration.
  ///
  /// In pt, this message translates to:
  /// **'Declarações incorrectas'**
  String get proposalRejectionReasonFalseDeclaration;

  /// No description provided for @proposalRejectionReasonDocumentsInvalid.
  ///
  /// In pt, this message translates to:
  /// **'Documentos inválidos'**
  String get proposalRejectionReasonDocumentsInvalid;

  /// No description provided for @proposalRejectionReasonOther.
  ///
  /// In pt, this message translates to:
  /// **'Outro motivo'**
  String get proposalRejectionReasonOther;

  /// No description provided for @proposalInfoRequestItemDeclarations.
  ///
  /// In pt, this message translates to:
  /// **'Declarações de risco'**
  String get proposalInfoRequestItemDeclarations;

  /// No description provided for @proposalInfoRequestItemSuitability.
  ///
  /// In pt, this message translates to:
  /// **'Questionário de adequação'**
  String get proposalInfoRequestItemSuitability;

  /// No description provided for @proposalInfoRequestItemInspection.
  ///
  /// In pt, this message translates to:
  /// **'Vistoria'**
  String get proposalInfoRequestItemInspection;

  /// No description provided for @proposalInfoRequestItemKyc.
  ///
  /// In pt, this message translates to:
  /// **'Verificação de identidade'**
  String get proposalInfoRequestItemKyc;

  /// No description provided for @proposalInfoRequestItemPolicyholder.
  ///
  /// In pt, this message translates to:
  /// **'Dados do tomador'**
  String get proposalInfoRequestItemPolicyholder;

  /// No description provided for @proposalInfoRequestItemOther.
  ///
  /// In pt, this message translates to:
  /// **'Outro'**
  String get proposalInfoRequestItemOther;

  /// No description provided for @proposalSlaStatusOnTime.
  ///
  /// In pt, this message translates to:
  /// **'No prazo'**
  String get proposalSlaStatusOnTime;

  /// No description provided for @proposalSlaStatusAlert.
  ///
  /// In pt, this message translates to:
  /// **'Prazo a terminar'**
  String get proposalSlaStatusAlert;

  /// No description provided for @proposalSlaStatusOverdue.
  ///
  /// In pt, this message translates to:
  /// **'Prazo ultrapassado'**
  String get proposalSlaStatusOverdue;

  /// No description provided for @proposalStatusDraft.
  ///
  /// In pt, this message translates to:
  /// **'Rascunho'**
  String get proposalStatusDraft;

  /// No description provided for @proposalStatusUnderReview.
  ///
  /// In pt, this message translates to:
  /// **'Em análise'**
  String get proposalStatusUnderReview;

  /// No description provided for @proposalStatusInfoRequested.
  ///
  /// In pt, this message translates to:
  /// **'Informação pedida'**
  String get proposalStatusInfoRequested;

  /// No description provided for @proposalStatusAccepted.
  ///
  /// In pt, this message translates to:
  /// **'Aceite'**
  String get proposalStatusAccepted;

  /// No description provided for @proposalStatusRejected.
  ///
  /// In pt, this message translates to:
  /// **'Não aceite'**
  String get proposalStatusRejected;

  /// No description provided for @proposalStatusExpired.
  ///
  /// In pt, this message translates to:
  /// **'Expirada'**
  String get proposalStatusExpired;

  /// No description provided for @proposalStatusPaid.
  ///
  /// In pt, this message translates to:
  /// **'Paga'**
  String get proposalStatusPaid;

  /// No description provided for @proposalStatusConverted.
  ///
  /// In pt, this message translates to:
  /// **'Apólice emitida'**
  String get proposalStatusConverted;

  /// No description provided for @homeProposalsTitle.
  ///
  /// In pt, this message translates to:
  /// **'Propostas em curso'**
  String get homeProposalsTitle;

  /// No description provided for @homeProposalsSeeAll.
  ///
  /// In pt, this message translates to:
  /// **'Ver todas'**
  String get homeProposalsSeeAll;

  /// No description provided for @inspectionPendingUploads.
  ///
  /// In pt, this message translates to:
  /// **'{n, plural, =1{1 fotografia por enviar} other{{n} fotografias por enviar}}'**
  String inspectionPendingUploads(int n);

  /// No description provided for @proposalsTitle.
  ///
  /// In pt, this message translates to:
  /// **'Propostas'**
  String get proposalsTitle;

  /// No description provided for @proposalsEmpty.
  ///
  /// In pt, this message translates to:
  /// **'Ainda não tem propostas.'**
  String get proposalsEmpty;

  /// No description provided for @proposalsFilterActive.
  ///
  /// In pt, this message translates to:
  /// **'Em curso'**
  String get proposalsFilterActive;

  /// No description provided for @proposalsFilterAll.
  ///
  /// In pt, this message translates to:
  /// **'Todas'**
  String get proposalsFilterAll;

  /// No description provided for @proposalExpiresOn.
  ///
  /// In pt, this message translates to:
  /// **'Expira em {date}'**
  String proposalExpiresOn(String date);

  /// No description provided for @proposalContinue.
  ///
  /// In pt, this message translates to:
  /// **'Continuar'**
  String get proposalContinue;

  /// No description provided for @proposalView.
  ///
  /// In pt, this message translates to:
  /// **'Ver'**
  String get proposalView;

  /// No description provided for @proposalCreating.
  ///
  /// In pt, this message translates to:
  /// **'A preparar a proposta…'**
  String get proposalCreating;

  /// No description provided for @proposalTitle.
  ///
  /// In pt, this message translates to:
  /// **'Proposta {number}'**
  String proposalTitle(String number);

  /// No description provided for @proposalChangedElsewhere.
  ///
  /// In pt, this message translates to:
  /// **'A proposta foi alterada noutro lado.'**
  String get proposalChangedElsewhere;

  /// No description provided for @quoteAcquire.
  ///
  /// In pt, this message translates to:
  /// **'Adquirir apólice'**
  String get quoteAcquire;

  /// No description provided for @proposalStepOf.
  ///
  /// In pt, this message translates to:
  /// **'Passo {step} de {total} · {name}'**
  String proposalStepOf(int step, int total, String name);

  /// No description provided for @proposalStepPolicyholder.
  ///
  /// In pt, this message translates to:
  /// **'Tomador e data'**
  String get proposalStepPolicyholder;

  /// No description provided for @proposalStepDeclarations.
  ///
  /// In pt, this message translates to:
  /// **'Declarações'**
  String get proposalStepDeclarations;

  /// No description provided for @proposalStepSuitability.
  ///
  /// In pt, this message translates to:
  /// **'Adequação'**
  String get proposalStepSuitability;

  /// No description provided for @proposalStepInspection.
  ///
  /// In pt, this message translates to:
  /// **'Vistoria'**
  String get proposalStepInspection;

  /// No description provided for @proposalStepIdentity.
  ///
  /// In pt, this message translates to:
  /// **'Identidade'**
  String get proposalStepIdentity;

  /// No description provided for @proposalStepReview.
  ///
  /// In pt, this message translates to:
  /// **'Resumo'**
  String get proposalStepReview;

  /// No description provided for @proposalBack.
  ///
  /// In pt, this message translates to:
  /// **'Voltar'**
  String get proposalBack;

  /// No description provided for @proposalNext.
  ///
  /// In pt, this message translates to:
  /// **'Continuar'**
  String get proposalNext;

  /// No description provided for @proposalCoverages.
  ///
  /// In pt, this message translates to:
  /// **'Coberturas'**
  String get proposalCoverages;

  /// No description provided for @proposalTotal.
  ///
  /// In pt, this message translates to:
  /// **'Total'**
  String get proposalTotal;

  /// No description provided for @proposalPolicyholderTitle.
  ///
  /// In pt, this message translates to:
  /// **'Tomador do seguro'**
  String get proposalPolicyholderTitle;

  /// No description provided for @proposalPolicyholderFromProfile.
  ///
  /// In pt, this message translates to:
  /// **'Estes dados vêm do seu perfil.'**
  String get proposalPolicyholderFromProfile;

  /// No description provided for @proposalEditProfile.
  ///
  /// In pt, this message translates to:
  /// **'Editar perfil'**
  String get proposalEditProfile;

  /// No description provided for @proposalRefreshFromProfile.
  ///
  /// In pt, this message translates to:
  /// **'Actualizar com o perfil'**
  String get proposalRefreshFromProfile;

  /// No description provided for @proposalStartDate.
  ///
  /// In pt, this message translates to:
  /// **'Data de início'**
  String get proposalStartDate;

  /// No description provided for @proposalEndDate.
  ///
  /// In pt, this message translates to:
  /// **'Fim: {date}'**
  String proposalEndDate(String date);

  /// No description provided for @proposalVehicle.
  ///
  /// In pt, this message translates to:
  /// **'Veículo'**
  String get proposalVehicle;

  /// No description provided for @proposalDriver.
  ///
  /// In pt, this message translates to:
  /// **'Condutor'**
  String get proposalDriver;

  /// No description provided for @declarationClaimsLast3YearsQuestion.
  ///
  /// In pt, this message translates to:
  /// **'Nos últimos 3 anos, teve algum acidente ou sinistro com um veículo, como condutor ou como proprietário?'**
  String get declarationClaimsLast3YearsQuestion;

  /// No description provided for @declarationClaimsLast3YearsClaimsCount.
  ///
  /// In pt, this message translates to:
  /// **'Quantos?'**
  String get declarationClaimsLast3YearsClaimsCount;

  /// No description provided for @declarationClaimsLast3YearsDetails.
  ///
  /// In pt, this message translates to:
  /// **'Descreva brevemente: quando, o que aconteceu e se houve feridos.'**
  String get declarationClaimsLast3YearsDetails;

  /// No description provided for @declarationPreviousRefusalQuestion.
  ///
  /// In pt, this message translates to:
  /// **'Alguma seguradora já recusou, anulou ou não quis renovar um seguro automóvel seu?'**
  String get declarationPreviousRefusalQuestion;

  /// No description provided for @declarationPreviousRefusalDetails.
  ///
  /// In pt, this message translates to:
  /// **'Que seguradora e porquê?'**
  String get declarationPreviousRefusalDetails;

  /// No description provided for @declarationUsageDifferentQuestion.
  ///
  /// In pt, this message translates to:
  /// **'O veículo vai ser usado para outro fim além de «{usage}»? Por exemplo, transportar passageiros ou mercadorias a troco de pagamento.'**
  String declarationUsageDifferentQuestion(String usage);

  /// No description provided for @declarationUsageDifferentDetails.
  ///
  /// In pt, this message translates to:
  /// **'Para que outro fim?'**
  String get declarationUsageDifferentDetails;

  /// No description provided for @declarationVehicleModifiedQuestion.
  ///
  /// In pt, this message translates to:
  /// **'O veículo tem alterações às características de fábrica (motor, suspensão, carroçaria, gás ou outras)?'**
  String get declarationVehicleModifiedQuestion;

  /// No description provided for @declarationVehicleModifiedDetails.
  ///
  /// In pt, this message translates to:
  /// **'Que alterações?'**
  String get declarationVehicleModifiedDetails;

  /// No description provided for @declarationNoviceDriversQuestion.
  ///
  /// In pt, this message translates to:
  /// **'Alguém com carta de condução há menos de 2 anos vai conduzir este veículo com regularidade?'**
  String get declarationNoviceDriversQuestion;

  /// No description provided for @declarationNoviceDriversDetails.
  ///
  /// In pt, this message translates to:
  /// **'Quem, e há quanto tempo tem carta?'**
  String get declarationNoviceDriversDetails;

  /// No description provided for @declarationTruthfulness.
  ///
  /// In pt, this message translates to:
  /// **'Declaro que as respostas são verdadeiras e completas. Sei que declarações falsas ou omissões podem levar à anulação do contrato ou à recusa do pagamento de sinistros.'**
  String get declarationTruthfulness;

  /// No description provided for @proposalAnswerRequired.
  ///
  /// In pt, this message translates to:
  /// **'Escolha uma resposta.'**
  String get proposalAnswerRequired;

  /// No description provided for @proposalDetailsLength.
  ///
  /// In pt, this message translates to:
  /// **'Escreva entre 10 e 500 caracteres.'**
  String get proposalDetailsLength;

  /// No description provided for @proposalClaimsCountRange.
  ///
  /// In pt, this message translates to:
  /// **'Indique um número de 1 a 20.'**
  String get proposalClaimsCountRange;

  /// No description provided for @proposalTruthfulnessRequired.
  ///
  /// In pt, this message translates to:
  /// **'Confirme a declaração para continuar.'**
  String get proposalTruthfulnessRequired;

  /// No description provided for @suitabilityIntro.
  ///
  /// In pt, this message translates to:
  /// **'Estas perguntas ajudam a confirmar se a proposta corresponde ao que procura.'**
  String get suitabilityIntro;

  /// No description provided for @suitabilityNeedQuestion.
  ///
  /// In pt, this message translates to:
  /// **'Que protecção procura para este veículo?'**
  String get suitabilityNeedQuestion;

  /// No description provided for @suitabilityNeedOptionMandatoryOnly.
  ///
  /// In pt, this message translates to:
  /// **'Só o seguro obrigatório: danos que eu cause a outras pessoas'**
  String get suitabilityNeedOptionMandatoryOnly;

  /// No description provided for @suitabilityNeedOptionOwnDamage.
  ///
  /// In pt, this message translates to:
  /// **'Também os danos no meu veículo num acidente'**
  String get suitabilityNeedOptionOwnDamage;

  /// No description provided for @suitabilityNeedOptionFull.
  ///
  /// In pt, this message translates to:
  /// **'Também roubo, incêndio e fenómenos da natureza'**
  String get suitabilityNeedOptionFull;

  /// No description provided for @suitabilityNeedOptionUnsure.
  ///
  /// In pt, this message translates to:
  /// **'Não sei'**
  String get suitabilityNeedOptionUnsure;

  /// No description provided for @suitabilityFinancedQuestion.
  ///
  /// In pt, this message translates to:
  /// **'O veículo ainda está a ser pago (crédito ou leasing)?'**
  String get suitabilityFinancedQuestion;

  /// No description provided for @suitabilityCanAffordRepairQuestion.
  ///
  /// In pt, this message translates to:
  /// **'Se o seu veículo ficasse danificado num acidente causado por si, conseguiria pagar a reparação sem a ajuda do seguro?'**
  String get suitabilityCanAffordRepairQuestion;

  /// No description provided for @suitabilityCrossBorderQuestion.
  ///
  /// In pt, this message translates to:
  /// **'Vai conduzir este veículo fora de Moçambique durante o período do seguro?'**
  String get suitabilityCrossBorderQuestion;

  /// No description provided for @suitabilityDeductibleQuestion.
  ///
  /// In pt, this message translates to:
  /// **'Aceita pagar uma parte de cada reparação (franquia) em troca de um prémio mais baixo?'**
  String get suitabilityDeductibleQuestion;

  /// No description provided for @suitabilityAnswerYes.
  ///
  /// In pt, this message translates to:
  /// **'Sim'**
  String get suitabilityAnswerYes;

  /// No description provided for @suitabilityAnswerNo.
  ///
  /// In pt, this message translates to:
  /// **'Não'**
  String get suitabilityAnswerNo;

  /// No description provided for @suitabilityAnswerUnsure.
  ///
  /// In pt, this message translates to:
  /// **'Não sei'**
  String get suitabilityAnswerUnsure;

  /// No description provided for @suitabilityReasonNeedNotCoveredOwnDamage.
  ///
  /// In pt, this message translates to:
  /// **'Disse que quer protecção para o seu veículo, mas esta proposta não cobre danos no próprio veículo.'**
  String get suitabilityReasonNeedNotCoveredOwnDamage;

  /// No description provided for @suitabilityReasonNeedNotCoveredTheftFire.
  ///
  /// In pt, this message translates to:
  /// **'Disse que quer protecção contra roubo, incêndio e fenómenos da natureza, mas esta proposta não inclui todas essas coberturas.'**
  String get suitabilityReasonNeedNotCoveredTheftFire;

  /// No description provided for @suitabilityReasonCoverAboveNeed.
  ///
  /// In pt, this message translates to:
  /// **'Disse que só quer o seguro obrigatório, mas esta proposta inclui coberturas de danos próprios, que aumentam o prémio.'**
  String get suitabilityReasonCoverAboveNeed;

  /// No description provided for @suitabilityReasonNeedUnsure.
  ///
  /// In pt, this message translates to:
  /// **'Não tem a certeza da protecção de que precisa. Leia o resumo das coberturas e a informação pré-contratual antes de continuar.'**
  String get suitabilityReasonNeedUnsure;

  /// No description provided for @suitabilityReasonFinancedWithoutOwnDamage.
  ///
  /// In pt, this message translates to:
  /// **'O veículo ainda está a ser pago e esta proposta não cobre danos no próprio veículo. Confirme se o contrato de crédito ou leasing exige essa cobertura.'**
  String get suitabilityReasonFinancedWithoutOwnDamage;

  /// No description provided for @suitabilityReasonCannotAffordRepair.
  ///
  /// In pt, this message translates to:
  /// **'Disse que não conseguiria pagar a reparação do seu veículo, mas esta proposta não cobre danos no próprio veículo.'**
  String get suitabilityReasonCannotAffordRepair;

  /// No description provided for @suitabilityReasonCrossBorderNotCovered.
  ///
  /// In pt, this message translates to:
  /// **'Vai conduzir fora de Moçambique, mas esta proposta não inclui cobertura fora do país.'**
  String get suitabilityReasonCrossBorderNotCovered;

  /// No description provided for @suitabilityReasonDeductibleNotWanted.
  ///
  /// In pt, this message translates to:
  /// **'Disse que não quer franquia, mas pelo menos uma cobertura de danos próprios desta proposta tem franquia.'**
  String get suitabilityReasonDeductibleNotWanted;

  /// No description provided for @suitabilityAcknowledge.
  ///
  /// In pt, this message translates to:
  /// **'Li os avisos e quero continuar com esta proposta'**
  String get suitabilityAcknowledge;

  /// No description provided for @suitabilityRequestOtherQuote.
  ///
  /// In pt, this message translates to:
  /// **'Pedir outra cotação'**
  String get suitabilityRequestOtherQuote;

  /// No description provided for @suitabilityAcknowledged.
  ///
  /// In pt, this message translates to:
  /// **'Confirmou que leu os avisos.'**
  String get suitabilityAcknowledged;

  /// No description provided for @inspectionTitle.
  ///
  /// In pt, this message translates to:
  /// **'Vistoria'**
  String get inspectionTitle;

  /// No description provided for @inspectionIntro.
  ///
  /// In pt, this message translates to:
  /// **'Para os danos próprios, a seguradora precisa de fotografias do veículo. Tire-as de dia, com o veículo limpo e inteiro na imagem.'**
  String get inspectionIntro;

  /// No description provided for @inspectionInstructionFront.
  ///
  /// In pt, this message translates to:
  /// **'A frente do veículo inteira, com a matrícula legível.'**
  String get inspectionInstructionFront;

  /// No description provided for @inspectionInstructionRear.
  ///
  /// In pt, this message translates to:
  /// **'A traseira inteira, com a matrícula legível.'**
  String get inspectionInstructionRear;

  /// No description provided for @inspectionInstructionLeft.
  ///
  /// In pt, this message translates to:
  /// **'O lado esquerdo inteiro, de uma ponta à outra.'**
  String get inspectionInstructionLeft;

  /// No description provided for @inspectionInstructionRight.
  ///
  /// In pt, this message translates to:
  /// **'O lado direito inteiro, de uma ponta à outra.'**
  String get inspectionInstructionRight;

  /// No description provided for @inspectionInstructionOdometer.
  ///
  /// In pt, this message translates to:
  /// **'O painel com o conta-quilómetros legível, com a ignição ligada.'**
  String get inspectionInstructionOdometer;

  /// No description provided for @inspectionInstructionChassis.
  ///
  /// In pt, this message translates to:
  /// **'O número do chassis gravado no veículo (normalmente no pára-brisas ou na porta do condutor).'**
  String get inspectionInstructionChassis;

  /// No description provided for @inspectionInstructionExtra.
  ///
  /// In pt, this message translates to:
  /// **'Danos que o veículo já tenha, ou outros pormenores.'**
  String get inspectionInstructionExtra;

  /// No description provided for @inspectionSideHint.
  ///
  /// In pt, this message translates to:
  /// **'Afaste-se até o veículo caber inteiro'**
  String get inspectionSideHint;

  /// No description provided for @inspectionToTake.
  ///
  /// In pt, this message translates to:
  /// **'Por tirar'**
  String get inspectionToTake;

  /// No description provided for @inspectionQueued.
  ///
  /// In pt, this message translates to:
  /// **'Na fila'**
  String get inspectionQueued;

  /// No description provided for @inspectionUploading.
  ///
  /// In pt, this message translates to:
  /// **'A enviar'**
  String get inspectionUploading;

  /// No description provided for @inspectionUploaded.
  ///
  /// In pt, this message translates to:
  /// **'Enviada'**
  String get inspectionUploaded;

  /// No description provided for @inspectionFailed.
  ///
  /// In pt, this message translates to:
  /// **'Erro'**
  String get inspectionFailed;

  /// No description provided for @inspectionStart.
  ///
  /// In pt, this message translates to:
  /// **'Começar vistoria'**
  String get inspectionStart;

  /// No description provided for @inspectionOpen.
  ///
  /// In pt, this message translates to:
  /// **'Fazer a vistoria'**
  String get inspectionOpen;

  /// No description provided for @inspectionProgress.
  ///
  /// In pt, this message translates to:
  /// **'{done} de {total} ângulos'**
  String inspectionProgress(int done, int total);

  /// No description provided for @inspectionOdometer.
  ///
  /// In pt, this message translates to:
  /// **'Quilometragem actual'**
  String get inspectionOdometer;

  /// No description provided for @inspectionOdometerInvalid.
  ///
  /// In pt, this message translates to:
  /// **'Indique um valor de 0 a 2 000 000.'**
  String get inspectionOdometerInvalid;

  /// No description provided for @inspectionSubmit.
  ///
  /// In pt, this message translates to:
  /// **'Enviar vistoria'**
  String get inspectionSubmit;

  /// No description provided for @inspectionWillSubmitWhenOnline.
  ///
  /// In pt, this message translates to:
  /// **'A vistoria será enviada quando houver ligação.'**
  String get inspectionWillSubmitWhenOnline;

  /// No description provided for @inspectionSubmitQueued.
  ///
  /// In pt, this message translates to:
  /// **'A vistoria é enviada à seguradora logo que as fotografias acabem de ser enviadas.'**
  String get inspectionSubmitQueued;

  /// No description provided for @inspectionStatusPending.
  ///
  /// In pt, this message translates to:
  /// **'Por enviar'**
  String get inspectionStatusPending;

  /// No description provided for @inspectionStatusSubmitted.
  ///
  /// In pt, this message translates to:
  /// **'Enviada à seguradora'**
  String get inspectionStatusSubmitted;

  /// No description provided for @inspectionStatusApproved.
  ///
  /// In pt, this message translates to:
  /// **'Aprovada'**
  String get inspectionStatusApproved;

  /// No description provided for @inspectionStatusRejected.
  ///
  /// In pt, this message translates to:
  /// **'Novas fotografias pedidas'**
  String get inspectionStatusRejected;

  /// No description provided for @inspectionOnSiteRequested.
  ///
  /// In pt, this message translates to:
  /// **'Pediu vistoria presencial. A seguradora vai contactá-lo.'**
  String get inspectionOnSiteRequested;

  /// No description provided for @inspectionRejectedBanner.
  ///
  /// In pt, this message translates to:
  /// **'A seguradora pediu novas fotografias: {reason}'**
  String inspectionRejectedBanner(String reason);

  /// No description provided for @inspectionRetakeMarker.
  ///
  /// In pt, this message translates to:
  /// **'Tirar de novo'**
  String get inspectionRetakeMarker;

  /// No description provided for @inspectionTakeAgain.
  ///
  /// In pt, this message translates to:
  /// **'Tirar de novo'**
  String get inspectionTakeAgain;

  /// No description provided for @inspectionDiscard.
  ///
  /// In pt, this message translates to:
  /// **'Descartar'**
  String get inspectionDiscard;

  /// No description provided for @inspectionExtras.
  ///
  /// In pt, this message translates to:
  /// **'Fotografias extra'**
  String get inspectionExtras;

  /// No description provided for @inspectionAddExtra.
  ///
  /// In pt, this message translates to:
  /// **'Acrescentar fotografia'**
  String get inspectionAddExtra;

  /// No description provided for @inspectionOnSiteLink.
  ///
  /// In pt, this message translates to:
  /// **'Prefiro vistoria presencial'**
  String get inspectionOnSiteLink;

  /// No description provided for @inspectionOnSiteTitle.
  ///
  /// In pt, this message translates to:
  /// **'Vistoria presencial'**
  String get inspectionOnSiteTitle;

  /// No description provided for @inspectionOnSiteNotes.
  ///
  /// In pt, this message translates to:
  /// **'Local e horas preferidas'**
  String get inspectionOnSiteNotes;

  /// No description provided for @inspectionOnSiteSend.
  ///
  /// In pt, this message translates to:
  /// **'Pedir vistoria presencial'**
  String get inspectionOnSiteSend;

  /// No description provided for @inspectionOfflineBanner.
  ///
  /// In pt, this message translates to:
  /// **'Sem ligação. As fotografias ficam guardadas no telemóvel e são enviadas quando a ligação voltar.'**
  String get inspectionOfflineBanner;

  /// No description provided for @inspectionMissingSlots.
  ///
  /// In pt, this message translates to:
  /// **'Faltam fotografias: {slots}'**
  String inspectionMissingSlots(String slots);

  /// No description provided for @inspectionUsePhoto.
  ///
  /// In pt, this message translates to:
  /// **'Usar fotografia'**
  String get inspectionUsePhoto;

  /// No description provided for @inspectionNotEditable.
  ///
  /// In pt, this message translates to:
  /// **'A vistoria já foi enviada à seguradora.'**
  String get inspectionNotEditable;

  /// No description provided for @registrationDocCameraPermissionDenied.
  ///
  /// In pt, this message translates to:
  /// **'Precisamos da câmara para fotografar o livrete.'**
  String get registrationDocCameraPermissionDenied;

  /// No description provided for @locationRationale.
  ///
  /// In pt, this message translates to:
  /// **'A localização ajuda a seguradora a confirmar onde as fotografias foram tiradas. É opcional.'**
  String get locationRationale;

  /// No description provided for @locationContinue.
  ///
  /// In pt, this message translates to:
  /// **'Continuar'**
  String get locationContinue;

  /// No description provided for @kycTitle.
  ///
  /// In pt, this message translates to:
  /// **'Verificação de identidade'**
  String get kycTitle;

  /// No description provided for @kycStateVerified.
  ///
  /// In pt, this message translates to:
  /// **'Identidade verificada'**
  String get kycStateVerified;

  /// No description provided for @kycStatePending.
  ///
  /// In pt, this message translates to:
  /// **'Em verificação'**
  String get kycStatePending;

  /// No description provided for @kycStateRejected.
  ///
  /// In pt, this message translates to:
  /// **'Não foi possível verificar a sua identidade.'**
  String get kycStateRejected;

  /// No description provided for @kycStateOutdated.
  ///
  /// In pt, this message translates to:
  /// **'Os dados do seu perfil mudaram. Verifique de novo a sua identidade.'**
  String get kycStateOutdated;

  /// No description provided for @kycRequiredForAutoAcceptance.
  ///
  /// In pt, this message translates to:
  /// **'Verifique a sua identidade para a proposta poder ser aceite logo. Sem isto, a seguradora analisa-a e pode demorar até 15 dias.'**
  String get kycRequiredForAutoAcceptance;

  /// No description provided for @kycOptional.
  ///
  /// In pt, this message translates to:
  /// **'Opcional: a verificação acelera propostas futuras.'**
  String get kycOptional;

  /// No description provided for @kycReasonDocumentUnreadable.
  ///
  /// In pt, this message translates to:
  /// **'As fotografias do documento não estão legíveis.'**
  String get kycReasonDocumentUnreadable;

  /// No description provided for @kycReasonDocumentExpired.
  ///
  /// In pt, this message translates to:
  /// **'O documento está fora da validade.'**
  String get kycReasonDocumentExpired;

  /// No description provided for @kycReasonDataMismatch.
  ///
  /// In pt, this message translates to:
  /// **'Os dados do documento não coincidem com os do seu perfil.'**
  String get kycReasonDataMismatch;

  /// No description provided for @kycReasonSelfieMismatch.
  ///
  /// In pt, this message translates to:
  /// **'A selfie não permite confirmar que é o titular do documento.'**
  String get kycReasonSelfieMismatch;

  /// No description provided for @kycReasonOther.
  ///
  /// In pt, this message translates to:
  /// **'Outro motivo.'**
  String get kycReasonOther;

  /// No description provided for @kycDocFront.
  ///
  /// In pt, this message translates to:
  /// **'Frente do documento'**
  String get kycDocFront;

  /// No description provided for @kycDocBack.
  ///
  /// In pt, this message translates to:
  /// **'Verso do documento'**
  String get kycDocBack;

  /// No description provided for @kycSelfie.
  ///
  /// In pt, this message translates to:
  /// **'Selfie'**
  String get kycSelfie;

  /// No description provided for @kycTakePhoto.
  ///
  /// In pt, this message translates to:
  /// **'Tirar fotografia'**
  String get kycTakePhoto;

  /// No description provided for @kycRetakePhoto.
  ///
  /// In pt, this message translates to:
  /// **'Repetir'**
  String get kycRetakePhoto;

  /// No description provided for @kycSubmit.
  ///
  /// In pt, this message translates to:
  /// **'Enviar'**
  String get kycSubmit;

  /// No description provided for @kycNotNow.
  ///
  /// In pt, this message translates to:
  /// **'Agora não'**
  String get kycNotNow;

  /// No description provided for @kycNoProposal.
  ///
  /// In pt, this message translates to:
  /// **'A verificação de identidade é pedida quando faz uma proposta.'**
  String get kycNoProposal;

  /// No description provided for @kycDocumentInstruction.
  ///
  /// In pt, this message translates to:
  /// **'Coloque o documento dentro da moldura, sem reflexos, com todos os cantos visíveis.'**
  String get kycDocumentInstruction;

  /// No description provided for @kycSelfieInstruction.
  ///
  /// In pt, this message translates to:
  /// **'Olhe de frente para a câmara, sem óculos escuros nem chapéu, num sítio com boa luz.'**
  String get kycSelfieInstruction;

  /// No description provided for @kycCameraPermissionDenied.
  ///
  /// In pt, this message translates to:
  /// **'Para verificar a sua identidade, a MoSeguros precisa de acesso à câmara.'**
  String get kycCameraPermissionDenied;

  /// No description provided for @kycPhotosMissing.
  ///
  /// In pt, this message translates to:
  /// **'Tire as fotografias pedidas antes de enviar.'**
  String get kycPhotosMissing;

  /// No description provided for @proposalDeclarationsAnswers.
  ///
  /// In pt, this message translates to:
  /// **'Respostas às declarações'**
  String get proposalDeclarationsAnswers;

  /// No description provided for @proposalSuitabilityAnswers.
  ///
  /// In pt, this message translates to:
  /// **'Respostas de adequação'**
  String get proposalSuitabilityAnswers;

  /// No description provided for @proposalDocuments.
  ///
  /// In pt, this message translates to:
  /// **'Documentos do produto'**
  String get proposalDocuments;

  /// No description provided for @proposalMissingTitle.
  ///
  /// In pt, this message translates to:
  /// **'Falta completar:'**
  String get proposalMissingTitle;

  /// No description provided for @proposalDocumentsAcknowledged.
  ///
  /// In pt, this message translates to:
  /// **'Recebi e li as Condições Gerais e a Informação Pré-contratual.'**
  String get proposalDocumentsAcknowledged;

  /// No description provided for @proposalSignAndSend.
  ///
  /// In pt, this message translates to:
  /// **'Assinar e enviar à seguradora'**
  String get proposalSignAndSend;

  /// No description provided for @proposalSendOnly.
  ///
  /// In pt, this message translates to:
  /// **'Enviar à seguradora'**
  String get proposalSendOnly;

  /// No description provided for @proposalWaitUploads.
  ///
  /// In pt, this message translates to:
  /// **'Aguarde o envio das fotografias da vistoria.'**
  String get proposalWaitUploads;

  /// No description provided for @errorNoConnection.
  ///
  /// In pt, this message translates to:
  /// **'Sem ligação à internet.'**
  String get errorNoConnection;

  /// No description provided for @signTitle.
  ///
  /// In pt, this message translates to:
  /// **'Assinar a proposta'**
  String get signTitle;

  /// No description provided for @signCodeSent.
  ///
  /// In pt, this message translates to:
  /// **'Enviámos um código por {channel} para {destination}.'**
  String signCodeSent(String channel, String destination);

  /// No description provided for @signViewPdf.
  ///
  /// In pt, this message translates to:
  /// **'Ver a proposta (PDF)'**
  String get signViewPdf;

  /// No description provided for @signFallback.
  ///
  /// In pt, this message translates to:
  /// **'Não foi possível enviar pelo canal habitual. Enviámos o código por {channel}.'**
  String signFallback(String channel);

  /// No description provided for @signSubmitting.
  ///
  /// In pt, this message translates to:
  /// **'A enviar à seguradora…'**
  String get signSubmitting;

  /// No description provided for @proposalTimelineCreated.
  ///
  /// In pt, this message translates to:
  /// **'Criada'**
  String get proposalTimelineCreated;

  /// No description provided for @proposalTimelineSubmitted.
  ///
  /// In pt, this message translates to:
  /// **'Enviada à seguradora'**
  String get proposalTimelineSubmitted;

  /// No description provided for @proposalTimelineDecided.
  ///
  /// In pt, this message translates to:
  /// **'Decisão'**
  String get proposalTimelineDecided;

  /// No description provided for @proposalUnderReviewText.
  ///
  /// In pt, this message translates to:
  /// **'A {insurer} está a analisar a sua proposta. Tem até {date} para responder.'**
  String proposalUnderReviewText(String insurer, String date);

  /// No description provided for @proposalUnderReviewNoDue.
  ///
  /// In pt, this message translates to:
  /// **'A {insurer} está a analisar a sua proposta.'**
  String proposalUnderReviewNoDue(String insurer);

  /// No description provided for @proposalAcceptedText.
  ///
  /// In pt, this message translates to:
  /// **'Proposta aceite.'**
  String get proposalAcceptedText;

  /// No description provided for @proposalPaymentSoon.
  ///
  /// In pt, this message translates to:
  /// **'O pagamento fica disponível em breve.'**
  String get proposalPaymentSoon;

  /// No description provided for @proposalRejectedText.
  ///
  /// In pt, this message translates to:
  /// **'A proposta não foi aceite.'**
  String get proposalRejectedText;

  /// No description provided for @proposalExpiredText.
  ///
  /// In pt, this message translates to:
  /// **'Esta proposta expirou.'**
  String get proposalExpiredText;

  /// No description provided for @proposalDownloadSigned.
  ///
  /// In pt, this message translates to:
  /// **'Descarregar a proposta assinada (PDF)'**
  String get proposalDownloadSigned;

  /// No description provided for @proposalInfoRequestedTitle.
  ///
  /// In pt, this message translates to:
  /// **'A seguradora pediu mais informação'**
  String get proposalInfoRequestedTitle;

  /// No description provided for @logoutPendingUploads.
  ///
  /// In pt, this message translates to:
  /// **'{n, plural, =1{Tem 1 fotografia por enviar. Se terminar a sessão, perde-a.} other{Tem {n} fotografias por enviar. Se terminar a sessão, perde-as.}}'**
  String logoutPendingUploads(int n);

  /// No description provided for @logoutAnyway.
  ///
  /// In pt, this message translates to:
  /// **'Terminar na mesma'**
  String get logoutAnyway;

  /// No description provided for @cameraUnavailableNoGallery.
  ///
  /// In pt, this message translates to:
  /// **'Não foi possível abrir a câmara.'**
  String get cameraUnavailableNoGallery;
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
