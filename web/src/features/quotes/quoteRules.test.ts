import { describe, expect, it } from 'vitest';
import {
  brokerDriverSchema,
  brokerVehicleSchema,
  clientSchema,
  emptyBrokerVehicle,
  fieldErrors,
  quoteTermsSchema,
  vehicleValueMissing,
  type BrokerVehicleValues,
  type QuoteTermsValues,
} from './quoteRules';

const today = '2027-01-10';
const terms = (overrides: Partial<QuoteTermsValues> = {}) =>
  fieldErrors(
    quoteTermsSchema({ today }).safeParse({
      productType: 'COMPREHENSIVE',
      durationDays: 365,
      startDate: '2027-01-15',
      coverages: [
        { code: 'OD_COLLISION', capital: null },
        { code: 'OD_FIRE', capital: null },
      ],
      ...overrides,
    }),
  );

describe('quoteRules — termos (S3-02 §3)', () => {
  it('pedido válido não tem erros', () => {
    expect(terms()).toEqual({});
  });

  it('regra 1: tipo obrigatório; BORDER e FLEET → NOT_ALLOWED', () => {
    expect(terms({ productType: '' }).productType).toBe('REQUIRED');
    expect(terms({ productType: 'BORDER' }).productType).toBe('NOT_ALLOWED');
    expect(terms({ productType: 'FLEET' }).productType).toBe('NOT_ALLOWED');
    expect(terms({ productType: 'MTPL_ONLY', coverages: [] })).toEqual({});
  });

  it('regra 2: TEMPORARY aceita 7, 14, 30, 60 e 365; os outros só 365', () => {
    for (const days of [7, 14, 30, 60, 365]) {
      expect(terms({ productType: 'TEMPORARY', coverages: [], durationDays: days })).toEqual({});
    }
    expect(terms({ productType: 'TEMPORARY', coverages: [], durationDays: 10 }).durationDays).toBe(
      'DURATION_NOT_ALLOWED',
    );
    expect(terms({ durationDays: 30 }).durationDays).toBe('DURATION_NOT_ALLOWED');
    expect(terms({ durationDays: null }).durationDays).toBe('REQUIRED');
  });

  it('regra 3: data de início entre hoje e hoje + 60 dias', () => {
    expect(terms({ startDate: today })).toEqual({});
    expect(terms({ startDate: '2027-03-11' })).toEqual({});
    expect(terms({ startDate: '2027-01-09' }).startDate).toBe('START_DATE_OUT_OF_RANGE');
    expect(terms({ startDate: '2027-03-12' }).startDate).toBe('START_DATE_OUT_OF_RANGE');
    expect(terms({ startDate: '' }).startDate).toBe('REQUIRED');
  });

  it('regra 5: coberturas repetidas → DUPLICATE_COVERAGE', () => {
    expect(
      terms({
        coverages: [
          { code: 'OD_COLLISION', capital: null },
          { code: 'OD_COLLISION', capital: null },
        ],
      })['coverages.1.code'],
    ).toBe('DUPLICATE_COVERAGE');
  });

  it('regra 5: OD_* fora de COMPREHENSIVE → COVERAGE_NOT_ALLOWED_FOR_TYPE', () => {
    expect(
      terms({ productType: 'MTPL_ONLY', coverages: [{ code: 'OD_GLASS', capital: null }] })[
        'coverages.0.code'
      ],
    ).toBe('COVERAGE_NOT_ALLOWED_FOR_TYPE');
    expect(
      terms({ productType: 'MTPL_ONLY', coverages: [{ code: 'VTPL', capital: null }] }),
    ).toEqual({});
  });

  it('regra 5: COMPREHENSIVE sem OD_* → OWN_DAMAGE_REQUIRED', () => {
    expect(terms({ coverages: [{ code: 'VTPL', capital: null }] }).coverages).toBe(
      'OWN_DAMAGE_REQUIRED',
    );
  });

  it('regra 5: OD_THEFT sem choque, incêndio e natureza → COVERAGE_REQUIRES', () => {
    expect(
      terms({
        coverages: [
          { code: 'OD_COLLISION', capital: null },
          { code: 'OD_THEFT', capital: null },
        ],
      })['coverages.1.code'],
    ).toBe('COVERAGE_REQUIRES');
    expect(
      terms({
        coverages: ['OD_COLLISION', 'OD_FIRE', 'OD_NATURE', 'OD_THEFT'].map((code) => ({
          code,
          capital: null,
        })),
      }),
    ).toEqual({});
  });

  it('regra 5: capital tem de ser > 0; código desconhecido é recusado', () => {
    expect(
      terms({
        coverages: [
          { code: 'OD_COLLISION', capital: null },
          { code: 'VTPL', capital: { amount: 0, currency: 'MZN' } },
        ],
      })['coverages.1.capital'],
    ).toBe('OUT_OF_RANGE');
    expect(
      terms({
        coverages: [
          { code: 'OD_COLLISION', capital: null },
          { code: 'XPTO', capital: null },
        ],
      })['coverages.1.code'],
    ).toBe('INVALID_VALUE');
  });

  it('regra 6: danos próprios sem valor comercial', () => {
    expect(vehicleValueMissing([{ code: 'OD_FIRE' }], null)).toBe(true);
    expect(vehicleValueMissing([{ code: 'OD_FIRE' }], { amount: 1, currency: 'MZN' })).toBe(false);
    expect(vehicleValueMissing([{ code: 'VTPL' }], null)).toBe(false);
  });
});

const vehicle = (overrides: Partial<BrokerVehicleValues> = {}) =>
  fieldErrors(
    brokerVehicleSchema({ currentYear: 2027 }).safeParse({
      ...emptyBrokerVehicle,
      year: '2024',
      category: 'LIGHT_PASSENGER',
      usage: 'PRIVATE',
      seats: '5',
      provinceOfCirculation: 'MAPUTO_CITY',
      ...overrides,
    }),
  );

describe('quoteRules — mediador (S3-02 §6)', () => {
  it('telemóvel do cliente: 9 dígitos começados por 82 a 87', () => {
    const msisdn = (value: string) =>
      fieldErrors(clientSchema.safeParse({ clientMsisdn: value })).clientMsisdn;
    expect(msisdn('841234567')).toBeUndefined();
    expect(msisdn('')).toBe('REQUIRED');
    expect(msisdn('881234567')).toBe('INVALID_MSISDN');
    expect(msisdn('84123456')).toBe('INVALID_MSISDN');
  });

  it('veículo mínimo (sem matrícula, marca, modelo nem cilindrada) é válido', () => {
    expect(vehicle()).toEqual({});
  });

  it('matrícula opcional mas, se vier, com tamanho válido', () => {
    expect(vehicle({ plate: 'AAA-100-MC' })).toEqual({});
    expect(vehicle({ plate: 'A'.repeat(16) }).plate).toBe('INVALID_PLATE');
  });

  it('marca e modelo opcionais até 60 caracteres', () => {
    expect(vehicle({ make: 'Toyota', model: 'Corolla' })).toEqual({});
    expect(vehicle({ make: 'x'.repeat(61) }).make).toBe('OUT_OF_RANGE');
    expect(vehicle({ model: 'x'.repeat(61) }).model).toBe('OUT_OF_RANGE');
  });

  it('ano obrigatório entre 1900 e o próximo ano', () => {
    expect(vehicle({ year: '' }).year).toBe('REQUIRED');
    expect(vehicle({ year: '1899' }).year).toBe('INVALID_YEAR');
    expect(vehicle({ year: '2028' })).toEqual({});
    expect(vehicle({ year: '2029' }).year).toBe('INVALID_YEAR');
  });

  it('categoria, uso e província obrigatórios', () => {
    expect(vehicle({ category: '' }).category).toBe('REQUIRED');
    expect(vehicle({ usage: '' }).usage).toBe('REQUIRED');
    expect(vehicle({ provinceOfCirculation: '' }).provinceOfCirculation).toBe('REQUIRED');
  });

  it('cilindrada opcional 1–30 000; em reboques tem de faltar', () => {
    expect(vehicle({ engineCc: '1600' })).toEqual({});
    expect(vehicle({ engineCc: '0' }).engineCc).toBe('OUT_OF_RANGE');
    expect(vehicle({ engineCc: '30001' }).engineCc).toBe('OUT_OF_RANGE');
    expect(
      vehicle({ category: 'TRAILER', seats: '0', grossWeightKg: '900', engineCc: '100' }).engineCc,
    ).toBe('NOT_ALLOWED');
  });

  it('lugares obrigatórios com os limites da categoria', () => {
    expect(vehicle({ seats: '' }).seats).toBe('REQUIRED');
    expect(vehicle({ category: 'MOTORCYCLE', seats: '4' }).seats).toBe('OUT_OF_RANGE');
    expect(vehicle({ category: 'TRAILER', seats: '0', grossWeightKg: '900' })).toEqual({});
  });

  it('peso bruto obrigatório em pesados e reboques', () => {
    expect(vehicle({ category: 'HEAVY' }).grossWeightKg).toBe('REQUIRED');
    expect(vehicle({ category: 'HEAVY', grossWeightKg: '12000' })).toEqual({});
    expect(vehicle({ grossWeightKg: '100001' }).grossWeightKg).toBe('OUT_OF_RANGE');
  });

  it('valor comercial opcional, > 0', () => {
    expect(vehicle({ commercialValue: { amount: 120000000, currency: 'MZN' } })).toEqual({});
    expect(vehicle({ commercialValue: { amount: 0, currency: 'MZN' } }).commercialValue).toBe(
      'OUT_OF_RANGE',
    );
  });

  it('condutor: nascimento obrigatório; carta e categorias opcionais', () => {
    const driver = (
      values: Partial<Parameters<ReturnType<typeof brokerDriverSchema>['parse']>[0]>,
    ) =>
      fieldErrors(
        brokerDriverSchema({ today }).safeParse({
          birthDate: '1991-09-09',
          licenceIssueDate: '',
          licenceCategories: [],
          ...values,
        }),
      );
    expect(driver({})).toEqual({});
    expect(driver({ birthDate: '' }).birthDate).toBe('REQUIRED');
    expect(driver({ birthDate: '2027-02-01' }).birthDate).toBe('INVALID_DATE');
    expect(driver({ birthDate: '1920-01-01' }).birthDate).toBe('INVALID_DATE');
    expect(driver({ licenceIssueDate: '2011-04-04', licenceCategories: ['B'] })).toEqual({});
    expect(driver({ licenceIssueDate: '1990-01-01' }).licenceIssueDate).toBe(
      'LICENCE_ISSUE_DATE_INVALID',
    );
    expect(driver({ licenceIssueDate: '2027-02-01' }).licenceIssueDate).toBe(
      'LICENCE_ISSUE_DATE_INVALID',
    );
    expect(driver({ licenceCategories: ['B', 'B'] }).licenceCategories).toBe('DUPLICATED');
    expect(
      driver({ licenceCategories: Array.from({ length: 13 }, (_, i) => `C${i}`) })
        .licenceCategories,
    ).toBe('OUT_OF_RANGE');
  });
});
