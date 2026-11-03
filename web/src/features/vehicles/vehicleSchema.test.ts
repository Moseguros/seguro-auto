import { afterEach, describe, expect, it, vi } from 'vitest';
import {
  emptyVehicleValues,
  toVehicleRequest,
  vehicleFieldOf,
  vehicleSchema,
  type VehicleFormValues,
} from './vehicleSchema';

const valid: VehicleFormValues = {
  ...emptyVehicleValues,
  plate: 'aaa 100 mc',
  chassisNumber: 'jtdbr32e-720012345',
  make: 'Toyota',
  model: 'Corolla',
  year: '2018',
  category: 'LIGHT_PASSENGER',
  engineCc: '1598',
  seats: '5',
  usage: 'PRIVATE',
  provinceOfCirculation: 'MAPUTO_CITY',
};

/** Erros (`campo` → código) de um formulário com as alterações dadas. */
function errorsOf(changes: Partial<VehicleFormValues>, currentYear = 2026) {
  const result = vehicleSchema({ currentYear }).safeParse({ ...valid, ...changes });
  return Object.fromEntries(
    (result.error?.issues ?? []).map((issue) => [issue.path[0], issue.message]),
  );
}

describe('vehicleSchema', () => {
  afterEach(() => vi.useRealTimers());

  it('um veículo válido passa', () => {
    expect(errorsOf({})).toEqual({});
  });

  it('matrícula: obrigatória e até 15 caracteres sem espaços nem hífens', () => {
    expect(errorsOf({ plate: '' })).toEqual({ plate: 'REQUIRED' });
    expect(errorsOf({ plate: ' - ' })).toEqual({ plate: 'REQUIRED' });
    expect(errorsOf({ plate: 'ABC-DEF-GHI-JKL-MN' })).toEqual({});
    expect(errorsOf({ plate: 'ABCDEFGHIJKLMNOP' })).toEqual({ plate: 'INVALID_PLATE' });
  });

  it('chassis: 5 a 30 letras ou algarismos depois de tirar espaços e hífens', () => {
    expect(errorsOf({ chassisNumber: '' })).toEqual({ chassisNumber: 'REQUIRED' });
    expect(errorsOf({ chassisNumber: 'ab-12' })).toEqual({ chassisNumber: 'INVALID_CHASSIS' });
    expect(errorsOf({ chassisNumber: 'ab 1-2 3' })).toEqual({});
    expect(errorsOf({ chassisNumber: 'A'.repeat(31) })).toEqual({
      chassisNumber: 'INVALID_CHASSIS',
    });
    expect(errorsOf({ chassisNumber: 'ABC*12345' })).toEqual({ chassisNumber: 'INVALID_CHASSIS' });
  });

  it('marca e modelo: 1 a 60 caracteres', () => {
    expect(errorsOf({ make: '  ', model: '' })).toEqual({ make: 'REQUIRED', model: 'REQUIRED' });
    expect(errorsOf({ make: 'x'.repeat(61), model: 'y'.repeat(60) })).toEqual({
      make: 'OUT_OF_RANGE',
    });
  });

  it('ano: de 1900 ao ano seguinte', () => {
    expect(errorsOf({ year: '' })).toEqual({ year: 'REQUIRED' });
    expect(errorsOf({ year: '1899' })).toEqual({ year: 'INVALID_YEAR' });
    expect(errorsOf({ year: '1900' })).toEqual({});
    expect(errorsOf({ year: '2027' })).toEqual({});
    expect(errorsOf({ year: '2028' })).toEqual({ year: 'INVALID_YEAR' });
    expect(errorsOf({ year: '20x8' })).toEqual({ year: 'INVALID_YEAR' });
  });

  it('ano máximo pelo ano de Africa/Maputo (31/12 22:30 UTC já é 2027)', () => {
    vi.useFakeTimers({ toFake: ['Date'] });
    vi.setSystemTime(new Date('2026-12-31T22:30:00Z'));
    const parse = (year: string) => vehicleSchema().safeParse({ ...valid, year }).success;
    expect(parse('2028')).toBe(true);
    expect(parse('2029')).toBe(false);
  });

  it('categoria, uso e província obrigatórios', () => {
    expect(errorsOf({ category: '', usage: 'X', provinceOfCirculation: '' })).toMatchObject({
      category: 'REQUIRED',
      usage: 'REQUIRED',
      provinceOfCirculation: 'REQUIRED',
    });
  });

  it('cilindrada: obrigatória de 1 a 30 000, excepto em reboques', () => {
    expect(errorsOf({ engineCc: '' })).toEqual({ engineCc: 'REQUIRED' });
    expect(errorsOf({ engineCc: '0' })).toEqual({ engineCc: 'OUT_OF_RANGE' });
    expect(errorsOf({ engineCc: '30001' })).toEqual({ engineCc: 'OUT_OF_RANGE' });
    expect(errorsOf({ engineCc: '30000' })).toEqual({});
    expect(
      errorsOf({ category: 'TRAILER', engineCc: '', seats: '0', grossWeightKg: '900' }),
    ).toEqual({});
  });

  it('lugares: motociclo 1–3, outras 1–100; reboque não valida (vai sempre 0)', () => {
    expect(errorsOf({ seats: '' })).toEqual({ seats: 'REQUIRED' });
    expect(errorsOf({ seats: '0' })).toEqual({ seats: 'OUT_OF_RANGE' });
    expect(errorsOf({ seats: '101' })).toEqual({ seats: 'OUT_OF_RANGE' });
    expect(errorsOf({ seats: '100' })).toEqual({});
    expect(errorsOf({ category: 'MOTORCYCLE', seats: '4' })).toEqual({ seats: 'OUT_OF_RANGE' });
    expect(errorsOf({ category: 'MOTORCYCLE', seats: '3' })).toEqual({});
    expect(errorsOf({ category: 'TRAILER', engineCc: '', seats: '7', grossWeightKg: '1' })).toEqual(
      {},
    );
  });

  it('peso bruto: obrigatório em pesados e reboques; 1 a 100 000', () => {
    expect(errorsOf({ category: 'HEAVY', grossWeightKg: '' })).toEqual({
      grossWeightKg: 'REQUIRED',
    });
    expect(errorsOf({ category: 'TRAILER', engineCc: '', seats: '0', grossWeightKg: '' })).toEqual({
      grossWeightKg: 'REQUIRED',
    });
    expect(errorsOf({ grossWeightKg: '' })).toEqual({});
    expect(errorsOf({ grossWeightKg: '0' })).toEqual({ grossWeightKg: 'OUT_OF_RANGE' });
    expect(errorsOf({ grossWeightKg: '100001' })).toEqual({ grossWeightKg: 'OUT_OF_RANGE' });
    expect(errorsOf({ category: 'HEAVY', grossWeightKg: '40000' })).toEqual({});
  });

  it('valor comercial: opcional e maior que zero', () => {
    expect(errorsOf({ commercialValue: null })).toEqual({});
    expect(errorsOf({ commercialValue: { amount: 0, currency: 'MZN' } })).toEqual({
      commercialValue: 'OUT_OF_RANGE',
    });
    expect(errorsOf({ commercialValue: { amount: 1, currency: 'MZN' } })).toEqual({});
  });
});

describe('toVehicleRequest', () => {
  it('normaliza e omite os campos vazios', () => {
    expect(toVehicleRequest(valid)).toEqual({
      plate: 'AAA 100 MC',
      chassisNumber: 'JTDBR32E720012345',
      make: 'Toyota',
      model: 'Corolla',
      year: 2018,
      category: 'LIGHT_PASSENGER',
      engineCc: 1598,
      seats: 5,
      usage: 'PRIVATE',
      provinceOfCirculation: 'MAPUTO_CITY',
      hasTracker: false,
    });
  });

  it('reboque: sem cilindrada e com 0 lugares', () => {
    const request = toVehicleRequest({
      ...valid,
      category: 'TRAILER',
      engineCc: '1598',
      seats: '4',
      grossWeightKg: '1200',
      commercialValue: { amount: 5000000, currency: 'MZN' },
    });
    expect(request).not.toHaveProperty('engineCc');
    expect(request).toMatchObject({
      seats: 0,
      grossWeightKg: 1200,
      commercialValue: { amount: 5000000, currency: 'MZN' },
    });
  });

  it('erros de campo do servidor vão para o campo do formulário', () => {
    expect(vehicleFieldOf('commercialValue.amount')).toBe('commercialValue');
    expect(vehicleFieldOf('plate')).toBe('plate');
    expect(vehicleFieldOf('version')).toBeUndefined();
  });
});
