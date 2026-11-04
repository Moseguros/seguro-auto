import 'package:built_value/serializer.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:moseguros/core/api/api_serializers.dart';
import 'package:moseguros/features/quote/coverage_options.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../support/quotes.dart';

List<CatalogProductResponse> products() {
  final page = QuoteFixtures.catalog()['content']! as List<Object?>;
  return [
    for (final json in page)
      apiSerializers.deserialize(
            json,
            specifiedType: const FullType(CatalogProductResponse),
          )!
          as CatalogProductResponse,
  ];
}

void main() {
  test('só entram os produtos do tipo escolhido', () {
    final rc = buildCoverageOptions(products(), 'MTPL_ONLY');
    expect(rc.total, 1);
    expect([for (final o in rc.all) o.code], ['MTPL']);
    expect(buildCoverageOptions(products(), 'TEMPORARY').isEmpty, isTrue);
  });

  test('união das coberturas de dois produtos, com "n de total"', () {
    final options = buildCoverageOptions(products(), 'COMPREHENSIVE');
    expect(options.total, 2);
    expect([for (final o in options.mandatory) o.code], ['MTPL']);
    expect(options.mandatory.single.capital, 300000000);
    expect(
      {for (final o in options.optional) o.code: o.availableIn},
      {
        'VTPL': 2,
        'OD_COLLISION': 2,
        'OD_GLASS': 1,
        'OD_FIRE': 2,
        'OD_NATURE': 2,
        'OD_THEFT': 1,
      },
    );
    // Pela ordem dos códigos (S2-04), não pela dos produtos.
    expect(
      [for (final o in options.optional) o.code],
      ['VTPL', 'OD_COLLISION', 'OD_GLASS', 'OD_FIRE', 'OD_NATURE', 'OD_THEFT'],
    );
  });

  test('OPTIONS: união dos capitais por ordem crescente, defeito o menor', () {
    final vtpl = buildCoverageOptions(
      products(),
      'COMPREHENSIVE',
    ).find('VTPL')!;
    expect(vtpl.hasCapitalChoice, isTrue);
    expect(vtpl.capitalOptions, [250000000, 500000000, 1000000000]);
    expect(vtpl.defaultCapital, 250000000);
  });

  test('cobertura obrigatória só num produto conta como opcional', () {
    final list = products();
    final options = buildCoverageOptions([
      list.first,
      list[1].rebuild(
        (b) => b.coverages.map(
          (c) => c.code == CoverageResponseCodeEnum.MTPL
              ? c.rebuild((c) => c..mandatory = false)
              : c,
        ),
      ),
    ], 'COMPREHENSIVE');
    expect(options.mandatory, isEmpty);
    expect(options.find('MTPL')!.mandatory, isFalse);
  });
}
