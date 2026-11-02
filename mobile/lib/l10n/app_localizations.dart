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

  /// Frase do ecrã inicial (home.tagline na web).
  ///
  /// In pt, this message translates to:
  /// **'Compare, compre e gira o seu seguro automóvel num só lugar.'**
  String get homeTagline;

  /// No description provided for @systemStatusTitle.
  ///
  /// In pt, this message translates to:
  /// **'Estado do sistema'**
  String get systemStatusTitle;

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
