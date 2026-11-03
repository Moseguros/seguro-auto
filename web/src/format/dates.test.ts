import { describe, expect, it } from 'vitest';
import { currentYearInMaputo, todayInMaputo } from './dates';

describe('datas em Africa/Maputo', () => {
  it('31/12 às 22:30 UTC já é 1/1 em Maputo', () => {
    const now = new Date('2026-12-31T22:30:00Z');
    expect(todayInMaputo(now)).toBe('2027-01-01');
    expect(currentYearInMaputo(now)).toBe(2027);
  });

  it('a meio do dia é a mesma data', () => {
    expect(todayInMaputo(new Date('2026-11-03T10:00:00Z'))).toBe('2026-11-03');
    expect(currentYearInMaputo(new Date('2026-11-03T10:00:00Z'))).toBe(2026);
  });
});
