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
  String get errorRefreshTokenInvalid =>
      'A sua sessão terminou. Entre novamente.';

  @override
  String get errorRefreshTokenReused =>
      'Por segurança, a sua sessão foi terminada. Entre novamente.';

  @override
  String get validationNotBlank => 'Campo obrigatório.';

  @override
  String get validationNotNull => 'Campo obrigatório.';

  @override
  String get errorConcurrentModification =>
      'Outra pessoa alterou estes dados entretanto. Actualize e tente de novo.';

  @override
  String get errorOtpChannelChoiceDisabled =>
      'Não é possível escolher o canal de envio do código.';

  @override
  String get errorOtpChannelNotAvailable =>
      'Este canal não está disponível. Use outro canal ou entre com o outro identificador.';

  @override
  String get errorOtpRateLimited =>
      'Pediu demasiados códigos. Tente mais tarde.';

  @override
  String get errorOtpResendTooSoon =>
      'Aguarde um pouco antes de pedir um novo código.';

  @override
  String get errorOtpDeliveryFailed =>
      'Não foi possível enviar o código. Tente de novo dentro de momentos.';

  @override
  String get errorOtpChallengeNotFound =>
      'Pedido de código desconhecido. Peça um novo código.';

  @override
  String get errorOtpAlreadyUsed =>
      'Este código já foi usado. Peça um novo código.';

  @override
  String get errorOtpSuperseded =>
      'Pediu um código mais recente. Use o último código que recebeu.';

  @override
  String get errorOtpExpired => 'O código expirou. Peça um novo código.';

  @override
  String get errorOtpInvalid => 'Código errado. Verifique e tente de novo.';

  @override
  String get errorOtpAttemptsExceeded =>
      'Demasiadas tentativas erradas. Peça um novo código.';

  @override
  String get errorAccountDisabled =>
      'Esta conta está desactivada. Contacte o apoio ao cliente.';

  @override
  String get errorOrganizationNotFound => 'Organização não encontrada.';

  @override
  String get errorOrganizationNotActive => 'A organização está suspensa.';

  @override
  String get errorNuitAlreadyInUse =>
      'Já existe uma organização com este NUIT.';

  @override
  String get errorShortCodeAlreadyInUse =>
      'Este código curto já é usado por outra seguradora.';

  @override
  String get errorOmbudsmanInvalid =>
      'Escolha um provedor do cliente existente.';

  @override
  String get errorRoleNotAllowedForOrganization =>
      'Um dos perfis escolhidos não pode ser atribuído nesta organização.';

  @override
  String get errorEmailAlreadyInUse =>
      'Este e-mail já pertence a outro utilizador.';

  @override
  String get errorInvitationInvalid =>
      'Este convite já não é válido. Peça um novo convite a quem o convidou.';

  @override
  String get errorInvitationNotAccepted =>
      'Defina primeiro a sua palavra-passe.';

  @override
  String get errorTotpInvalid =>
      'Código errado. Verifique a aplicação de autenticação e tente de novo.';

  @override
  String get errorPasswordTooWeak =>
      'A palavra-passe não cumpre as regras de segurança.';

  @override
  String get errorInvalidCredentials =>
      'E-mail, palavra-passe ou código de autenticação errados.';

  @override
  String get errorAccountLocked =>
      'Demasiadas tentativas erradas. A conta está bloqueada temporariamente; tente mais tarde.';

  @override
  String get errorOrganizationSuspended =>
      'A sua organização está suspensa. Contacte a administração da MoSeguros.';

  @override
  String get errorResetTokenInvalid =>
      'Este link de recuperação já não é válido. Peça um novo.';

  @override
  String get errorUserNotFound => 'Utilizador não encontrado.';

  @override
  String get errorCannotModifySelf => 'Não pode alterar a sua própria conta.';

  @override
  String get errorLastManager =>
      'A organização tem de ficar com pelo menos um gestor activo.';

  @override
  String get errorContactAlreadyInUse =>
      'Este contacto já está associado a outra conta.';

  @override
  String get errorContactAlreadyVerified =>
      'Este contacto já está confirmado na sua conta.';

  @override
  String get errorLastContact =>
      'A conta tem de ficar com pelo menos um contacto confirmado.';

  @override
  String get validationNotAllowed => 'Este campo não se aplica neste caso.';

  @override
  String get validationRequired => 'Campo obrigatório.';

  @override
  String get validationInvalidSort => 'Ordenação não suportada.';

  @override
  String get validationTooShort =>
      'A palavra-passe tem de ter pelo menos 12 caracteres.';

  @override
  String get validationTooLong =>
      'A palavra-passe pode ter no máximo 128 caracteres.';

  @override
  String get validationContainsEmail =>
      'A palavra-passe não pode conter o seu e-mail.';

  @override
  String get validationTooCommon =>
      'Esta palavra-passe é demasiado comum. Escolha outra.';

  @override
  String get validationSize => 'Comprimento fora dos limites permitidos.';

  @override
  String get validationPattern => 'Formato inválido.';

  @override
  String get validationTypeMismatch => 'Valor inválido.';

  @override
  String get validationInvalidName =>
      'Indique o nome completo (pelo menos duas palavras, só letras, espaços, \' e -).';

  @override
  String get validationUnderage => 'Tem de ter pelo menos 18 anos.';

  @override
  String get validationInvalidDate => 'Data inválida.';

  @override
  String get validationInvalidDocumentNumber =>
      'Número de documento inválido (5 a 20 letras maiúsculas ou dígitos).';

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
