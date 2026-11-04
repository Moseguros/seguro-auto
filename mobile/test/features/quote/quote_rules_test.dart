import 'package:flutter_test/flutter_test.dart';
import 'package:moseguros/features/quote/quote_rules.dart';

/// Regras do S3-02 §3 que se aplicam ao cliente, e o fim do período (§3.4).
void main() {
  final today = DateTime(2027, 1, 10);

  group('regra 1: tipo', () {
    test('MTPL_ONLY, COMPREHENSIVE e TEMPORARY são aceites', () {
      for (final type in QuoteProductTypes.all) {
        expect(productTypeError(type), isNull, reason: type);
      }
    });

    test('BORDER e FLEET → NOT_ALLOWED; sem tipo → REQUIRED', () {
      expect(productTypeError('BORDER'), 'NOT_ALLOWED');
      expect(productTypeError('FLEET'), 'NOT_ALLOWED');
      expect(productTypeError(null), 'REQUIRED');
    });
  });

  group('regra 2: duração', () {
    test('TEMPORARY aceita 7, 14, 30, 60 e 365 dias', () {
      for (final days in [7, 14, 30, 60, 365]) {
        expect(durationError(QuoteProductTypes.temporary, days), isNull);
      }
      expect(
        durationError(QuoteProductTypes.temporary, 15),
        'DURATION_NOT_ALLOWED',
      );
    });

    test('os outros tipos só aceitam 365', () {
      expect(durationError(QuoteProductTypes.mtplOnly, 365), isNull);
      expect(durationError(QuoteProductTypes.comprehensive, 365), isNull);
      expect(
        durationError(QuoteProductTypes.mtplOnly, 30),
        'DURATION_NOT_ALLOWED',
      );
      expect(
        durationError(QuoteProductTypes.comprehensive, 7),
        'DURATION_NOT_ALLOWED',
      );
      expect(durationError(QuoteProductTypes.mtplOnly, null), 'REQUIRED');
    });

    test('durações por tipo', () {
      expect(durationsFor(QuoteProductTypes.temporary), [7, 14, 30, 60, 365]);
      expect(durationsFor(QuoteProductTypes.comprehensive), [365]);
    });
  });

  group('regra 3: data de início', () {
    test('entre hoje e hoje + 60 dias', () {
      expect(startDateError(today, today), isNull);
      expect(startDateError(DateTime(2027, 3, 11), today), isNull);
      expect(latestStartDate(today), DateTime(2027, 3, 11));
    });

    test('ontem ou depois de hoje + 60 dias → START_DATE_OUT_OF_RANGE', () {
      expect(
        startDateError(DateTime(2027, 1, 9), today),
        'START_DATE_OUT_OF_RANGE',
      );
      expect(
        startDateError(DateTime(2027, 3, 12), today),
        'START_DATE_OUT_OF_RANGE',
      );
      expect(startDateError(null, today), 'REQUIRED');
    });

    test('o limite é o do backend (60 dias)', () {
      expect(quoteMaxStartDaysAhead, 60);
    });
  });

  group('regra 5: coberturas', () {
    const comprehensive = QuoteProductTypes.comprehensive;
    const mtplOnly = QuoteProductTypes.mtplOnly;

    test('lista válida (pode ser vazia fora de COMPREHENSIVE)', () {
      expect(coveragesError(mtplOnly, []), isNull);
      expect(coveragesError(mtplOnly, ['MTPL', 'VTPL']), isNull);
      expect(
        coveragesError(comprehensive, [
          'MTPL',
          'OD_COLLISION',
          'OD_FIRE',
          'OD_NATURE',
          'OD_THEFT',
        ]),
        isNull,
      );
    });

    test('cobertura repetida → DUPLICATE_COVERAGE', () {
      expect(coveragesError(mtplOnly, ['VTPL', 'VTPL']), 'DUPLICATE_COVERAGE');
    });

    test('código desconhecido → NOT_ALLOWED', () {
      expect(coveragesError(mtplOnly, ['XPTO']), 'NOT_ALLOWED');
    });

    test('OD_* fora de COMPREHENSIVE → COVERAGE_NOT_ALLOWED_FOR_TYPE', () {
      expect(
        coveragesError(mtplOnly, ['OD_GLASS']),
        'COVERAGE_NOT_ALLOWED_FOR_TYPE',
      );
      expect(
        coveragesError(QuoteProductTypes.temporary, ['OD_COLLISION']),
        'COVERAGE_NOT_ALLOWED_FOR_TYPE',
      );
    });

    test('COMPREHENSIVE sem OD_* → OWN_DAMAGE_REQUIRED', () {
      expect(
        coveragesError(comprehensive, ['MTPL', 'VTPL']),
        'OWN_DAMAGE_REQUIRED',
      );
    });

    test('OD_THEFT sem choque, incêndio e natureza → COVERAGE_REQUIRES', () {
      expect(
        coveragesError(comprehensive, ['OD_THEFT', 'OD_COLLISION', 'OD_FIRE']),
        'COVERAGE_REQUIRES',
      );
    });

    test('capital > 0', () {
      expect(capitalError(null), isNull);
      expect(capitalError(1), isNull);
      expect(capitalError(0), 'OUT_OF_RANGE');
    });
  });

  test('regra 6: OD_* precisa do valor comercial', () {
    expect(needsCommercialValue(['MTPL', 'VTPL']), isFalse);
    expect(needsCommercialValue(['OD_GLASS']), isTrue);
    expect(
      typeAllowedForVehicle(
        QuoteProductTypes.comprehensive,
        hasCommercialValue: false,
      ),
      isFalse,
    );
    expect(
      typeAllowedForVehicle(
        QuoteProductTypes.mtplOnly,
        hasCommercialValue: false,
      ),
      isTrue,
    );
  });

  group('regras automáticas de "Furto ou roubo"', () {
    test('marcar furto marca choque, incêndio e natureza', () {
      expect(toggleCoverage({}, 'OD_THEFT', checked: true), {
        'OD_THEFT',
        'OD_COLLISION',
        'OD_FIRE',
        'OD_NATURE',
      });
    });

    test('desmarcar uma das três desmarca o furto', () {
      final all = {'OD_THEFT', 'OD_COLLISION', 'OD_FIRE', 'OD_NATURE'};
      expect(toggleCoverage(all, 'OD_FIRE', checked: false), {
        'OD_COLLISION',
        'OD_NATURE',
      });
      expect(toggleCoverage(all, 'OD_THEFT', checked: false), {
        'OD_COLLISION',
        'OD_FIRE',
        'OD_NATURE',
      });
    });

    test('em COMPREHENSIVE começam marcadas as três, se existirem', () {
      expect(
        defaultCoverages(QuoteProductTypes.comprehensive, [
          'OD_COLLISION',
          'OD_NATURE',
          'VTPL',
        ]),
        ['OD_COLLISION', 'OD_NATURE'],
      );
      expect(defaultCoverages(QuoteProductTypes.mtplOnly, ['VTPL']), isEmpty);
    });
  });
}
