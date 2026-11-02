import 'package:built_collection/built_collection.dart';
import 'package:built_value/serializer.dart';
import 'package:moseguros_api/moseguros_api.dart';

/// Serializadores do cliente gerado, com o que falta ao `dart-dio`.
///
/// O gerador não regista o serializador de `InnerEnum` nem a fábrica de
/// `BuiltList<InnerEnum>` (os canais por tipo de identificador em
/// `OtpOptionsResponse.channels`), e `getOtpOptions` falha a ler a resposta.
/// Acrescenta-se aqui para não editar o pacote gerado.
final Serializers apiSerializers =
    (standardSerializers.toBuilder()
          ..add(InnerEnum.serializer)
          ..addBuilderFactory(
            const FullType(BuiltList, [FullType(InnerEnum)]),
            ListBuilder<InnerEnum>.new,
          ))
        .build();
