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
  String get errorFileNotFound =>
      'O ficheiro não existe ou não tem acesso a ele.';

  @override
  String get errorFileEmpty => 'O ficheiro está vazio.';

  @override
  String get errorFileTooLarge => 'O ficheiro tem mais de 10 MB.';

  @override
  String get errorFileTypeNotAllowed =>
      'Tipo de ficheiro não aceite. Use JPEG, PNG, HEIC ou PDF.';

  @override
  String get errorFileStorageError =>
      'Não foi possível ler o ficheiro. Tente mais tarde.';

  @override
  String get errorReferenceDomainUnknown =>
      'Esta lista de referência não existe.';

  @override
  String get errorReferenceValueNotFound => 'O valor não existe nesta lista.';

  @override
  String get errorReferenceCodeAlreadyExists =>
      'Já existe um valor com este código nesta lista.';

  @override
  String get errorPlatformSettingNotFound => 'Este parâmetro não existe.';

  @override
  String get errorPlateFormatNotFound => 'O formato de matrícula não existe.';

  @override
  String get errorPlateFormatCodeAlreadyExists =>
      'Já existe um formato de matrícula com este código.';

  @override
  String get errorLastActivePlateFormat =>
      'Tem de ficar pelo menos um formato de matrícula activo.';

  @override
  String get errorProductNotFound => 'Produto não encontrado.';

  @override
  String get errorProductVersionNotFound => 'Versão do produto não encontrada.';

  @override
  String get errorProductDocumentNotFound => 'Documento não encontrado.';

  @override
  String get errorProductCodeAlreadyExists =>
      'Já existe um produto com este código.';

  @override
  String get errorProductTypeLocked =>
      'O tipo não pode mudar depois de o produto ter sido publicado.';

  @override
  String get errorProductRetired =>
      'Este produto foi retirado e já não pode ser alterado.';

  @override
  String get errorProductDraftExists =>
      'Já existe uma versão em preparação deste produto.';

  @override
  String get errorProductVersionNotEditable =>
      'Esta versão já foi publicada e não pode ser alterada. Crie uma nova versão.';

  @override
  String get errorProductDocumentLimitReached =>
      'Cada versão pode ter no máximo 10 documentos.';

  @override
  String get errorProductVersionNotPublishable =>
      'A versão ainda não cumpre as regras de publicação.';

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
  String get validationInvalidCode => 'Use só maiúsculas, algarismos e _.';

  @override
  String get validationParentNotFound => 'A marca indicada não existe.';

  @override
  String get validationInvalidValue => 'Valor inválido para este parâmetro.';

  @override
  String get validationInvalidPattern => 'A expressão regular não é válida.';

  @override
  String get validationPatternNotAnchored =>
      'A expressão tem de começar por ^ e acabar em \$.';

  @override
  String get validationPatternWithoutGroups =>
      'A expressão precisa de pelo menos um grupo com nome.';

  @override
  String get validationTemplateGroupUnknown =>
      'O modelo usa um grupo que não existe na expressão.';

  @override
  String get validationExampleDoesNotMatch =>
      'O exemplo não corresponde ao formato.';

  @override
  String get validationDuplicated => 'Valor repetido.';

  @override
  String get validationInvalidRequirement =>
      'Uma cobertura não pode exigir-se a si própria.';

  @override
  String get validationCapitalOptionsInvalid =>
      'Indique 2 a 10 valores, do menor para o maior.';

  @override
  String get validationInvalidCountryCode => 'Código de país inválido.';

  @override
  String get validationMzScopeRequired => 'O âmbito tem de incluir Moçambique.';

  @override
  String get validationInvalidCapitalType =>
      'Tipo de capital não permitido para esta cobertura.';

  @override
  String get validationMtplRequired =>
      'O produto tem de incluir a responsabilidade civil obrigatória.';

  @override
  String get validationMtplMustBeMandatory =>
      'A responsabilidade civil obrigatória não pode ser opcional.';

  @override
  String get validationMtplCapitalBelowMinimum =>
      'O capital é inferior ao capital mínimo legal configurado.';

  @override
  String get validationBorderOnlyMtpl =>
      'O seguro de fronteira só pode ter a responsabilidade civil obrigatória.';

  @override
  String get validationOdNotAllowedForType =>
      'Este tipo de produto não admite coberturas de danos próprios.';

  @override
  String get validationOdRequiredForComprehensive =>
      'Um produto de danos próprios precisa de pelo menos uma cobertura de danos próprios.';

  @override
  String get validationOdCapitalMustBeVehicleValue =>
      'As coberturas de danos próprios usam o valor do veículo como capital.';

  @override
  String get validationOdTheftRequiresOdCoverages =>
      'Furto ou roubo exige choque, incêndio e fenómenos da natureza.';

  @override
  String get validationRequiredCoverageMissing =>
      'Falta uma cobertura que esta exige.';

  @override
  String get validationVtplCapitalTooLow =>
      'O capital da RC facultativa tem de ser superior ao capital mínimo legal.';

  @override
  String get validationCrossBorderScopeRequired =>
      'Indique pelo menos um país além de Moçambique.';

  @override
  String get validationDocumentRequired => 'Falta este documento em português.';

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

  @override
  String get aboutTitle => 'Sobre';

  @override
  String get loading => 'A carregar…';

  @override
  String get cancel => 'Cancelar';

  @override
  String get onboardingSkip => 'Saltar';

  @override
  String get onboardingNext => 'Seguinte';

  @override
  String get onboardingStart => 'Começar';

  @override
  String onboardingPage(int current, int total) {
    return 'Página $current de $total';
  }

  @override
  String get onboarding1Title => 'Compare seguradoras';

  @override
  String get onboarding1Text =>
      'Veja as propostas de várias seguradoras lado a lado e escolha a que lhe serve.';

  @override
  String get onboarding2Title => 'Compre em minutos';

  @override
  String get onboarding2Text =>
      'Simule, pague com M-Pesa ou por referência e receba a apólice no telemóvel.';

  @override
  String get onboarding3Title => 'Tudo num só lugar';

  @override
  String get onboarding3Text =>
      'Apólices, pagamentos e sinistros sempre consigo.';

  @override
  String get authLoginTitle => 'Entrar na área do cliente';

  @override
  String get authLoginSubtitle =>
      'Use o seu telemóvel ou e-mail. Enviamos-lhe um código para entrar, sem palavra-passe.';

  @override
  String get authIdentifierTypeLabel => 'Entrar com';

  @override
  String get authIdentifierTypeMsisdn => 'Telemóvel';

  @override
  String get authIdentifierTypeEmail => 'E-mail';

  @override
  String get authMsisdnLabel => 'Número de telemóvel';

  @override
  String get authMsisdnHelper => '9 dígitos, por exemplo 84 123 4567.';

  @override
  String get authEmailLabel => 'E-mail';

  @override
  String get authChannelLabel => 'Receber o código por';

  @override
  String get authSubmit => 'Receber código';

  @override
  String authRetryIn(int seconds) {
    return 'Tente de novo dentro de $seconds s.';
  }

  @override
  String get authCodeSubtitle => 'Escreva o código que recebeu.';

  @override
  String get authCodeInputLabel => 'Código recebido';

  @override
  String authCodeSentTo(String channel, String destination) {
    return 'Enviámos um código por $channel para $destination.';
  }

  @override
  String get authCodeChange => 'Alterar';

  @override
  String authCodeFallback(String requested, String used) {
    return 'Não conseguimos enviar por $requested. Enviámos o código por $used.';
  }

  @override
  String authCodeWrongCode(int count) {
    String _temp0 = intl.Intl.pluralLogic(
      count,
      locale: localeName,
      other: 'Código errado. Restam $count tentativas.',
      one: 'Código errado. Resta 1 tentativa.',
    );
    return '$_temp0';
  }

  @override
  String get authCodeResent => 'Enviámos um código novo.';

  @override
  String get authCodeResend => 'Reenviar código';

  @override
  String authCodeResendIn(int seconds) {
    return 'Reenviar código ($seconds s)';
  }

  @override
  String get channelSms => 'SMS';

  @override
  String get channelWhatsapp => 'WhatsApp';

  @override
  String get channelEmail => 'E-mail';

  @override
  String get logout => 'Terminar sessão';

  @override
  String get logoutConfirmTitle => 'Terminar sessão?';

  @override
  String get logoutConfirmText =>
      'Para voltar a entrar vai precisar de um código novo.';

  @override
  String homeGreeting(String name) {
    return 'Olá, $name';
  }

  @override
  String get homeGreetingNoName => 'Olá';

  @override
  String get homeIncompleteProfile =>
      'Complete o seu perfil para poder comprar um seguro.';

  @override
  String get homeCompleteProfile => 'Completar perfil';

  @override
  String get homeQuoteTitle => 'Seguro automóvel';

  @override
  String get homeQuoteText =>
      'Compare os preços das seguradoras para o seu veículo.';

  @override
  String get comingSoon => 'Disponível em breve';

  @override
  String get profileOpen => 'Perfil';

  @override
  String get profileTitle => 'Perfil';

  @override
  String get profilePersonalData => 'Dados pessoais';

  @override
  String get profileCompleteNotice =>
      'Precisamos destes dados para emitir as suas apólices.';

  @override
  String get profileFieldFullName => 'Nome completo';

  @override
  String get profileFieldBirthDate => 'Data de nascimento';

  @override
  String get profileFieldIdDocType => 'Tipo de documento';

  @override
  String get profileFieldIdDocNumber => 'Número do documento';

  @override
  String get profileFieldNuit => 'NUIT';

  @override
  String get profileFieldAddress => 'Morada';

  @override
  String get profileFieldProvince => 'Província';

  @override
  String get profileChoose => 'Escolha…';

  @override
  String get profileChange => 'Alterar';

  @override
  String get profileChangeDocument => 'Alterar o número do documento';

  @override
  String get profileChangeNuit => 'Alterar o NUIT';

  @override
  String get profileNuitHelper => '9 dígitos.';

  @override
  String get profileOptional => 'Opcional.';

  @override
  String get profileSave => 'Guardar';

  @override
  String get profileSaved => 'Perfil guardado';

  @override
  String get idDocTypeBi => 'Bilhete de Identidade';

  @override
  String get idDocTypePassport => 'Passaporte';

  @override
  String get idDocTypeDire => 'DIRE';

  @override
  String get contactsTitle => 'Contactos';

  @override
  String get contactTypeMsisdn => 'Telemóvel';

  @override
  String get contactTypeEmail => 'E-mail';

  @override
  String get contactsVerified => 'Verificado';

  @override
  String get contactsAddMsisdn => 'Acrescentar telemóvel';

  @override
  String get contactsAddEmail => 'Acrescentar e-mail';

  @override
  String get contactsChangeMsisdn => 'Alterar telemóvel';

  @override
  String get contactsChangeEmail => 'Alterar e-mail';

  @override
  String get contactsChangeShort => 'Alterar';

  @override
  String get contactsRemoveMsisdn => 'Remover telemóvel';

  @override
  String get contactsRemoveEmail => 'Remover e-mail';

  @override
  String get contactsRemoveShort => 'Remover';

  @override
  String get contactsSaved => 'Contacto confirmado';

  @override
  String get contactsRemoved => 'Contacto removido';

  @override
  String get contactsConfirmRemoveTitle => 'Remover contacto?';

  @override
  String contactsConfirmRemoveText(String value) {
    return 'Deixará de poder entrar e receber avisos em $value.';
  }

  @override
  String get contactsConfirmRemove => 'Remover';

  @override
  String get errorVehicleNotFound => 'Veículo não encontrado.';

  @override
  String get errorVehicleAlreadyRegistered =>
      'Já tem um veículo com esta matrícula.';

  @override
  String get errorVehicleLimitReached => 'Atingiu o máximo de 20 veículos.';

  @override
  String get errorVehicleRemovalBlocked =>
      'Este veículo tem uma apólice em vigor e não pode ser retirado.';

  @override
  String get errorVehicleRegistryNotFound =>
      'Não encontrámos esta matrícula no registo. Preencha os dados do livrete.';

  @override
  String get errorVehicleRegistryUnavailable =>
      'O registo automóvel não está disponível. Preencha os dados do livrete.';

  @override
  String get errorDriverNotFound => 'Condutor não encontrado.';

  @override
  String get errorDriverAlreadyRegistered =>
      'Já tem um condutor com esta carta.';

  @override
  String get errorDriverLimitReached => 'Atingiu o máximo de 10 condutores.';

  @override
  String get errorOwnerDriverAlreadyExists => 'Já se registou como condutor.';

  @override
  String get errorDriverIsHabitual =>
      'Este condutor é o habitual de um veículo. Escolha outro habitual primeiro.';

  @override
  String get validationInvalidChassis => 'N.º de chassis inválido.';

  @override
  String get validationInvalidYear => 'Ano inválido.';

  @override
  String get validationInvalidLicenceNumber => 'N.º da carta inválido.';

  @override
  String get validationLicenceCategoryUnknown =>
      'Categoria de carta desconhecida.';

  @override
  String get validationLicenceIssueDateInvalid =>
      'A data de emissão tem de ser depois do nascimento e até hoje.';

  @override
  String get validationHabitualDriverRequired => 'Escolha o condutor habitual.';

  @override
  String get validationMultipleHabitualDrivers =>
      'Só pode haver um condutor habitual.';

  @override
  String get save => 'Guardar';

  @override
  String get close => 'Fechar';

  @override
  String get reload => 'Recarregar';

  @override
  String get yes => 'Sim';

  @override
  String get no => 'Não';

  @override
  String get valueMissing => '—';

  @override
  String get moreOptions => 'Mais opções';

  @override
  String get navHome => 'Início';

  @override
  String get navVehicles => 'Veículos';

  @override
  String get navProfile => 'Perfil';

  @override
  String get validationInvalidMoney =>
      'Valor inválido. Use no máximo 2 casas decimais.';

  @override
  String get vehicleCategoryLightPassenger => 'Ligeiro de passageiros';

  @override
  String get vehicleCategoryLightMixed => 'Ligeiro misto';

  @override
  String get vehicleCategoryHeavy => 'Pesado';

  @override
  String get vehicleCategoryMotorcycle => 'Motociclo';

  @override
  String get vehicleCategoryTrailer => 'Reboque';

  @override
  String get vehicleCategoryBus => 'Autocarro';

  @override
  String get vehicleUsagePrivate => 'Particular';

  @override
  String get vehicleUsageRental => 'Aluguer sem condutor';

  @override
  String get vehicleUsagePassengerTransport => 'Transporte de passageiros';

  @override
  String get vehicleUsageGoods => 'Transporte de mercadorias';

  @override
  String get vehicleUsageTaxi => 'Táxi';

  @override
  String get vehicleUsageDrivingSchool => 'Escola de condução';

  @override
  String get vehiclesAddButton => 'Acrescentar veículo';

  @override
  String get vehiclesEmpty => 'Ainda não registou nenhum veículo.';

  @override
  String get vehiclesBackToList => 'Ver os meus veículos';

  @override
  String vehicleMakeModelYear(String make, String model, String year) {
    return '$make $model · $year';
  }

  @override
  String vehicleHabitualDriver(String name) {
    return 'Condutor habitual: $name';
  }

  @override
  String get vehicleNoHabitualDriver => 'Sem condutor habitual';

  @override
  String get vehicleDocMissing => 'Livrete em falta';

  @override
  String get vehicleRegistryVerified => 'Confirmado no registo';

  @override
  String get vehicleRegistryNotVerified => 'Não confirmado no registo';

  @override
  String get vehicleNewTitle => 'Acrescentar veículo';

  @override
  String get vehicleEditTitle => 'Alterar veículo';

  @override
  String get vehicleFieldPlate => 'Matrícula';

  @override
  String vehiclePlateHelper(String examples) {
    return 'Ex.: $examples';
  }

  @override
  String get vehicleOr => ' ou ';

  @override
  String get vehicleRegistryLookup => 'Procurar no registo';

  @override
  String get vehicleRegistryFilled =>
      'Preenchemos os dados a partir do registo automóvel. Confirme-os com o seu livrete.';

  @override
  String get vehicleFieldChassis => 'N.º de chassis';

  @override
  String get vehicleFieldMake => 'Marca';

  @override
  String get vehicleFieldModel => 'Modelo';

  @override
  String get vehicleFieldYear => 'Ano';

  @override
  String get vehicleFieldCategory => 'Categoria';

  @override
  String get vehicleFieldEngineCc => 'Cilindrada (cm³)';

  @override
  String get vehicleFieldSeats => 'Lugares';

  @override
  String get vehicleFieldGrossWeight => 'Peso bruto (kg)';

  @override
  String get vehicleFieldUsage => 'Uso';

  @override
  String get vehicleFieldProvince => 'Província de circulação';

  @override
  String get vehicleFieldCommercialValue => 'Valor comercial';

  @override
  String get vehicleCommercialValueHelper =>
      'Necessário para seguros de danos próprios.';

  @override
  String get vehicleFieldHasTracker => 'Tem localizador (GPS)';

  @override
  String get vehicleCreated =>
      'Veículo acrescentado. Envie agora a foto do livrete.';

  @override
  String get vehicleConflict => 'Este veículo foi alterado noutro dispositivo';

  @override
  String get vehicleEdit => 'Alterar';

  @override
  String get vehicleRemove => 'Retirar veículo';

  @override
  String get vehicleRemoveConfirmTitle => 'Retirar veículo?';

  @override
  String get vehicleRemoveConfirmText =>
      'O veículo deixa de aparecer na sua conta.';

  @override
  String get vehicleRemoveConfirm => 'Retirar';

  @override
  String get vehicleRemoved => 'Veículo retirado';

  @override
  String get vehicleSectionData => 'Dados';

  @override
  String get vehicleSectionDoc => 'Livrete';

  @override
  String get vehicleSectionDrivers => 'Condutores';

  @override
  String get vehicleDocNone => 'Ainda não enviou o livrete.';

  @override
  String get vehicleDocUpload => 'Enviar livrete';

  @override
  String get vehicleDocReplace => 'Substituir';

  @override
  String get vehicleDocOpen => 'Abrir';

  @override
  String get vehicleDocUploading => 'A enviar…';

  @override
  String get vehicleDocSaved => 'Livrete guardado';

  @override
  String get fileOpenFailed =>
      'Não há nenhuma aplicação para abrir este ficheiro.';

  @override
  String get vehicleNoDrivers => 'Nenhum condutor associado.';

  @override
  String get vehicleDriverHabitual => 'Habitual';

  @override
  String get vehicleManageDrivers => 'Gerir condutores';

  @override
  String manageDriversHelp(int max) {
    return 'Marque até $max condutores e escolha o habitual.';
  }

  @override
  String get docSourceCamera => 'Tirar foto';

  @override
  String get docSourceGallery => 'Escolher da galeria';

  @override
  String get docSourcePdf => 'Escolher PDF';

  @override
  String get cameraFrameHint =>
      'Enquadre o livrete dentro do rectângulo, com boa luz e sem reflexos.';

  @override
  String get cameraPermissionDenied =>
      'Precisamos da câmara para fotografar o livrete.';

  @override
  String get cameraOpenSettings => 'Abrir definições';

  @override
  String get cameraUnavailable =>
      'Não foi possível abrir a câmara. Escolha uma foto da galeria.';

  @override
  String get cameraCapture => 'Tirar foto';

  @override
  String get cameraFlashOff => 'Flash desligado';

  @override
  String get cameraFlashAuto => 'Flash automático';

  @override
  String get cameraFlashOn => 'Flash sempre ligado';

  @override
  String get photoRetake => 'Repetir';

  @override
  String get photoUse => 'Usar foto';

  @override
  String get driversTitle => 'Condutores';

  @override
  String get driversAddButton => 'Registar condutor';

  @override
  String get driversEmpty => 'Ainda não registou condutores.';

  @override
  String get driversBackToList => 'Ver os meus condutores';

  @override
  String get driverEditTitle => 'Alterar condutor';

  @override
  String get driverOwnerTag => 'Eu';

  @override
  String driverCategories(String categories) {
    return 'Categorias: $categories';
  }

  @override
  String driverLicenceMasked(String masked) {
    return 'Carta: $masked';
  }

  @override
  String driverVehicleCount(int count) {
    String _temp0 = intl.Intl.pluralLogic(
      count,
      locale: localeName,
      other: '$count veículos',
      one: '1 veículo',
      zero: 'Sem veículos',
    );
    return '$_temp0';
  }

  @override
  String get driverEdit => 'Alterar';

  @override
  String get driverRemove => 'Retirar';

  @override
  String get driverRemoveConfirmTitle => 'Retirar condutor?';

  @override
  String get driverRemoveConfirmText =>
      'O condutor deixa de aparecer na sua conta.';

  @override
  String get driverRemoved => 'Condutor retirado';

  @override
  String get driverFieldIsMe => 'Sou eu';

  @override
  String get driverFieldLicenceNumber => 'N.º da carta';

  @override
  String get driverChangeLicence => 'Alterar o n.º da carta';

  @override
  String get driverFieldLicenceCategories => 'Categorias';

  @override
  String get driverFieldLicenceIssueDate => 'Data de emissão da carta';

  @override
  String get driverConflict => 'Este condutor foi alterado noutro dispositivo';
}
