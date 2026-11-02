// ignore: unused_import
import 'package:intl/intl.dart' as intl;
import 'app_localizations.dart';

// ignore_for_file: type=lint

/// The translations for Portuguese (`pt`).
class AppLocalizationsPt extends AppLocalizations {
  AppLocalizationsPt([String locale = 'pt']) : super(locale);

  @override
  String get appName => 'MoSeguros';

  @override
  String get homeTagline =>
      'Compare, compre e gira o seu seguro automóvel num só lugar.';

  @override
  String get systemStatusTitle => 'Estado do sistema';

  @override
  String get systemStatusLoading => 'A verificar a API…';

  @override
  String get systemStatusVersion => 'Versão';

  @override
  String get systemStatusCommit => 'Commit';

  @override
  String get systemStatusUnknown => 'desconhecido';

  @override
  String get systemStatusError => 'Não foi possível obter o estado da API.';

  @override
  String get retry => 'Tentar de novo';

  @override
  String get notFoundTitle => 'Página não encontrada';

  @override
  String get notFoundDescription => 'O endereço que procura não existe.';

  @override
  String get backHome => 'Voltar ao início';

  @override
  String get errorNetworkError =>
      'Sem ligação ao servidor. Verifique a sua ligação à Internet.';

  @override
  String get errorUnknown => 'Ocorreu um erro inesperado.';

  @override
  String get errorValidationError => 'Os dados enviados não são válidos.';

  @override
  String get errorNotFound => 'O recurso pedido não existe.';

  @override
  String get errorUnauthorized => 'Precisa de iniciar sessão.';

  @override
  String get errorForbidden => 'Não tem permissão para esta operação.';

  @override
  String get errorInternalError =>
      'Erro interno do servidor. Tente mais tarde.';

  @override
  String get errorInvalidCurrency => 'Moeda inválida.';

  @override
  String get errorInvalidMsisdn => 'Número de telemóvel inválido.';

  @override
  String get errorInvalidNuit => 'NUIT inválido.';

  @override
  String get errorInvalidPlate => 'Matrícula inválida.';

  @override
  String get validationInvalidCurrency => 'Moeda inválida.';

  @override
  String get validationInvalidMsisdn =>
      'Número de telemóvel inválido (ex.: +258 84 123 4567).';

  @override
  String get validationInvalidNuit => 'O NUIT tem 9 dígitos.';

  @override
  String get validationInvalidPlate => 'Matrícula inválida.';

  @override
  String get ctaQuote => 'Simular seguro';

  @override
  String get ctaCompare => 'Comparar propostas';

  @override
  String get ctaBuy => 'Adquirir apólice';

  @override
  String get ctaRenew => 'Renovar seguro';
}
