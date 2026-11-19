//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/payment_insurer_ref.dart';
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/payment_method_option.dart';
import 'package:moseguros_api/src/model/intent_view.dart';
import 'package:moseguros_api/src/model/money.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'proposal_payment_methods_response.g.dart';

/// ProposalPaymentMethodsResponse
///
/// Properties:
/// * [activeIntent] 
/// * [amount] 
/// * [expiresAt] 
/// * [insurer] 
/// * [methods] 
/// * [paid] 
/// * [proposalId] 
/// * [proposalNumber] 
@BuiltValue()
abstract class ProposalPaymentMethodsResponse implements Built<ProposalPaymentMethodsResponse, ProposalPaymentMethodsResponseBuilder> {
  @BuiltValueField(wireName: r'activeIntent')
  IntentView? get activeIntent;

  @BuiltValueField(wireName: r'amount')
  Money get amount;

  @BuiltValueField(wireName: r'expiresAt')
  DateTime? get expiresAt;

  @BuiltValueField(wireName: r'insurer')
  PaymentInsurerRef get insurer;

  @BuiltValueField(wireName: r'methods')
  BuiltList<PaymentMethodOption> get methods;

  @BuiltValueField(wireName: r'paid')
  bool get paid;

  @BuiltValueField(wireName: r'proposalId')
  String get proposalId;

  @BuiltValueField(wireName: r'proposalNumber')
  String get proposalNumber;

  ProposalPaymentMethodsResponse._();

  factory ProposalPaymentMethodsResponse([void updates(ProposalPaymentMethodsResponseBuilder b)]) = _$ProposalPaymentMethodsResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(ProposalPaymentMethodsResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<ProposalPaymentMethodsResponse> get serializer => _$ProposalPaymentMethodsResponseSerializer();
}

class _$ProposalPaymentMethodsResponseSerializer implements PrimitiveSerializer<ProposalPaymentMethodsResponse> {
  @override
  final Iterable<Type> types = const [ProposalPaymentMethodsResponse, _$ProposalPaymentMethodsResponse];

  @override
  final String wireName = r'ProposalPaymentMethodsResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    ProposalPaymentMethodsResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.activeIntent != null) {
      yield r'activeIntent';
      yield serializers.serialize(
        object.activeIntent,
        specifiedType: const FullType(IntentView),
      );
    }
    yield r'amount';
    yield serializers.serialize(
      object.amount,
      specifiedType: const FullType(Money),
    );
    if (object.expiresAt != null) {
      yield r'expiresAt';
      yield serializers.serialize(
        object.expiresAt,
        specifiedType: const FullType(DateTime),
      );
    }
    yield r'insurer';
    yield serializers.serialize(
      object.insurer,
      specifiedType: const FullType(PaymentInsurerRef),
    );
    yield r'methods';
    yield serializers.serialize(
      object.methods,
      specifiedType: const FullType(BuiltList, [FullType(PaymentMethodOption)]),
    );
    yield r'paid';
    yield serializers.serialize(
      object.paid,
      specifiedType: const FullType(bool),
    );
    yield r'proposalId';
    yield serializers.serialize(
      object.proposalId,
      specifiedType: const FullType(String),
    );
    yield r'proposalNumber';
    yield serializers.serialize(
      object.proposalNumber,
      specifiedType: const FullType(String),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    ProposalPaymentMethodsResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required ProposalPaymentMethodsResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'activeIntent':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(IntentView),
          ) as IntentView;
          result.activeIntent.replace(valueDes);
          break;
        case r'amount':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Money),
          ) as Money;
          result.amount.replace(valueDes);
          break;
        case r'expiresAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.expiresAt = valueDes;
          break;
        case r'insurer':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(PaymentInsurerRef),
          ) as PaymentInsurerRef;
          result.insurer.replace(valueDes);
          break;
        case r'methods':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(PaymentMethodOption)]),
          ) as BuiltList<PaymentMethodOption>;
          result.methods.replace(valueDes);
          break;
        case r'paid':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.paid = valueDes;
          break;
        case r'proposalId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.proposalId = valueDes;
          break;
        case r'proposalNumber':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.proposalNumber = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  ProposalPaymentMethodsResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = ProposalPaymentMethodsResponseBuilder();
    final serializedList = (serialized as Iterable<Object?>).toList();
    final unhandled = <Object?>[];
    _deserializeProperties(
      serializers,
      serialized,
      specifiedType: specifiedType,
      serializedList: serializedList,
      unhandled: unhandled,
      result: result,
    );
    return result.build();
  }
}

