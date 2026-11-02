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
