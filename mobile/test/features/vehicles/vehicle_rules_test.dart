import 'package:flutter_test/flutter_test.dart';
import 'package:moseguros/features/vehicles/vehicle_rules.dart';

void main() {
  test('matrícula: obrigatória e ≤ 15 caracteres sem espaços nem hífens', () {
    expect(plateError(''), 'REQUIRED');
    expect(plateError(' - '), 'REQUIRED');
    expect(plateError('aaa-100-mc'), isNull);
    expect(plateError('ABCDEFGHIJKLMNO'), isNull);
    expect(plateError('ABCDEFGHIJKLMNOP'), 'INVALID_PLATE');
    expect(plateError('AB-CD-EF-GH-IJ-KL-MN-O'), isNull);
    expect(normalizePlate('aaa 100-mc'), 'AAA100MC');
  });

  test('chassis: ^[A-Z0-9]{5,30}\$ depois de normalizar', () {
    expect(chassisError(''), 'REQUIRED');
    expect(chassisError('jtd br-32e'), isNull);
    expect(normalizeChassis('jtd br-32e'), 'JTDBR32E');
    expect(chassisError('ABCD'), 'INVALID_CHASSIS');
    expect(chassisError('A' * 30), isNull);
    expect(chassisError('A' * 31), 'INVALID_CHASSIS');
    expect(chassisError('ABC*123'), 'INVALID_CHASSIS');
  });

  test('marca e modelo: 1–60', () {
    expect(makeOrModelError('  '), 'REQUIRED');
    expect(makeOrModelError('Toyota'), isNull);
    expect(makeOrModelError('x' * 60), isNull);
    expect(makeOrModelError('x' * 61), 'OUT_OF_RANGE');
  });

  test('ano: 1900 até ao ano actual em Maputo + 1', () {
    expect(yearError('', 2027), 'REQUIRED');
    expect(yearError('1899', 2027), 'INVALID_YEAR');
    expect(yearError('1900', 2027), isNull);
    expect(yearError('2028', 2027), isNull);
    expect(yearError('2029', 2027), 'INVALID_YEAR');
    expect(yearError('20a8', 2027), 'INVALID_YEAR');
  });

  test('categoria, uso e província obrigatórios', () {
    expect(categoryError(null), 'REQUIRED');
    expect(categoryError('HEAVY'), isNull);
    expect(usageError(null), 'REQUIRED');
    expect(usageError('TAXI'), isNull);
    expect(provinceOfCirculationError(null, ['GAZA']), 'REQUIRED');
    expect(provinceOfCirculationError('GAZA', ['GAZA']), isNull);
  });

  test('cilindrada: 1–30 000; escondida em TRAILER', () {
    expect(hasEngine('TRAILER'), isFalse);
    expect(hasEngine('HEAVY'), isTrue);
    expect(engineCcError('', 'TRAILER'), isNull);
    expect(engineCcError('', 'LIGHT_PASSENGER'), 'REQUIRED');
    expect(engineCcError('0', 'LIGHT_PASSENGER'), 'OUT_OF_RANGE');
    expect(engineCcError('1', 'LIGHT_PASSENGER'), isNull);
    expect(engineCcError('30000', 'HEAVY'), isNull);
    expect(engineCcError('30001', 'HEAVY'), 'OUT_OF_RANGE');
  });

  test('lugares: TRAILER 0, MOTORCYCLE 1–3, outras 1–100', () {
    expect(hasFixedSeats('TRAILER'), isTrue);
    expect(seatsError('0', 'TRAILER'), isNull);
    expect(seatsError('1', 'TRAILER'), 'OUT_OF_RANGE');
    expect(seatsError('', 'MOTORCYCLE'), 'REQUIRED');
    expect(seatsError('0', 'MOTORCYCLE'), 'OUT_OF_RANGE');
    expect(seatsError('3', 'MOTORCYCLE'), isNull);
    expect(seatsError('4', 'MOTORCYCLE'), 'OUT_OF_RANGE');
    expect(seatsError('0', 'BUS'), 'OUT_OF_RANGE');
    expect(seatsError('100', 'BUS'), isNull);
    expect(seatsError('101', 'BUS'), 'OUT_OF_RANGE');
  });

  test('peso bruto: obrigatório em HEAVY e TRAILER; 1–100 000', () {
    expect(grossWeightError('', 'HEAVY'), 'REQUIRED');
    expect(grossWeightError('', 'TRAILER'), 'REQUIRED');
    expect(grossWeightError('', 'LIGHT_PASSENGER'), isNull);
    expect(grossWeightError('0', 'LIGHT_PASSENGER'), 'OUT_OF_RANGE');
    expect(grossWeightError('40000', 'HEAVY'), isNull);
    expect(grossWeightError('100001', 'HEAVY'), 'OUT_OF_RANGE');
  });

  test('valor comercial: opcional, > 0, ≤ 1 000 000 000 MT, 2 casas', () {
    expect(commercialValueError(''), isNull);
    expect(commercialValueError('850 000,00'), isNull);
    expect(commercialValueError('0'), 'OUT_OF_RANGE');
    expect(commercialValueError('1 000 000 000'), isNull);
    expect(commercialValueError('1 000 000 000,01'), 'OUT_OF_RANGE');
    expect(commercialValueError('1,234'), 'INVALID_MONEY');
  });
}
