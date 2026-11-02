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

  @override
  String get errorInvalidEmail => 'Endereço de e-mail inválido.';

  @override
  String get errorInvalidPhone => 'Número de telefone inválido.';

  @override
  String get errorTenantRequired =>
      'Erro interno do servidor. Tente mais tarde.';

  @override
  String get errorDecryptionFailed =>
      'Erro interno do servidor. Tente mais tarde.';

  @override
  String get errorInvalidStateTransition =>
      'Esta operação não é possível no estado actual.';

  @override
  String get errorLicenseNumberRequired =>
      'O número de autorização da ASFPM é obrigatório para seguradoras, corretores e agentes.';

  @override
  String get errorChannelsEmpty => 'Escolha pelo menos um canal.';

  @override
  String get errorChannelsDuplicated => 'Cada canal só pode aparecer uma vez.';

  @override
  String get errorDefaultChannelNotEnabled =>
      'O canal por defeito tem de estar activo.';

  @override
  String get errorFallbackOrderMismatch =>
      'A ordem alternativa tem de ter exactamente os canais activos.';

  @override
  String get errorOutOfRange => 'Valor fora dos limites permitidos.';

  @override
  String get validationInvalidEmail =>
      'E-mail inválido (ex.: nome@exemplo.co.mz).';

  @override
  String get validationInvalidPhone => 'Telefone inválido (9 a 15 dígitos).';

  @override
  String get validationLicenseNumberRequired =>
      'Indique o número de autorização da ASFPM.';

  @override
  String get validationChannelsEmpty => 'Escolha pelo menos um canal.';

  @override
  String get validationChannelsDuplicated =>
      'Cada canal só pode aparecer uma vez.';

  @override
  String get validationDefaultChannelNotEnabled =>
      'O canal por defeito tem de estar activo.';

  @override
  String get validationFallbackOrderMismatch =>
      'A ordem alternativa tem de ter exactamente os canais activos.';

  @override
  String get validationOutOfRange => 'Valor fora dos limites permitidos.';

  @override
  String get provinceMaputoCity => 'Maputo Cidade';

  @override
  String get provinceMaputo => 'Maputo Província';

  @override
  String get provinceGaza => 'Gaza';

  @override
  String get provinceInhambane => 'Inhambane';

  @override
  String get provinceSofala => 'Sofala';

  @override
  String get provinceManica => 'Manica';

  @override
  String get provinceTete => 'Tete';

  @override
  String get provinceZambezia => 'Zambézia';

  @override
  String get provinceNampula => 'Nampula';

  @override
  String get provinceCaboDelgado => 'Cabo Delgado';

  @override
  String get provinceNiassa => 'Niassa';
}
