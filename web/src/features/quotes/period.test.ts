import { describe, expect, it } from 'vitest';
import { addYears, endDateOf, startDateRange, yearsBetween } from './period';

describe('period', () => {
  it('1 ano a partir de 2027-03-01 termina a 2028-02-29 (ano bissexto)', () => {
    expect(endDateOf('2027-03-01', 365)).toBe('2028-02-29');
  });

  it('30 dias a partir de 2027-01-15 terminam a 2027-02-13', () => {
    expect(endDateOf('2027-01-15', 30)).toBe('2027-02-13');
  });

  it('1 ano a partir de 2027-01-15 termina a 2028-01-14; 7 dias terminam ao 7.º dia', () => {
    expect(endDateOf('2027-01-15', 365)).toBe('2028-01-14');
    expect(endDateOf('2027-01-15', 7)).toBe('2027-01-21');
  });

  it('29 de Fevereiro mais um ano passa a 28 (como LocalDate.plusYears)', () => {
    expect(addYears('2028-02-29', 1)).toBe('2029-02-28');
  });

  it('data inválida → vazio', () => {
    expect(endDateOf('2027-02-30', 365)).toBe('');
  });

  it('janela da data de início: hoje até hoje + 60 dias', () => {
    expect(startDateRange('2027-01-01')).toEqual({ min: '2027-01-01', max: '2027-03-02' });
  });

  it('anos completos entre duas datas', () => {
    expect(yearsBetween('1990-05-14', '2027-05-13')).toBe(36);
    expect(yearsBetween('1990-05-14', '2027-05-14')).toBe(37);
  });
});
