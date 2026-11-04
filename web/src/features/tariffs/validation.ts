import type { RatingCoverage, RatingRulesResponse } from '../../api/generated';
import { isPositiveUpTo, tariffDecimalPattern, toDecimal } from './decimal';
import { factorCodePattern, reservedFactorCode } from './factorPresets';
import { allowedBaseTypes } from './initialDraft';
import type {
  Condition,
  Rating,
  TariffError,
  TariffRow,
  ValueKind,
  ValueRange,
} from './ratingTypes';
import { valueKindOf } from './ratingTypes';
import { intLimits, maxCentavos, variableDef } from './variables';

/** Erro do valor de uma linha, ou `null` (S3-01 §7.4). */
export function rowValueError(value: TariffRow['value'], kind: ValueKind): string | null {
  if (value === null || value === undefined || value === '') return 'REQUIRED';
  if (kind === 'AMOUNT') {
    return typeof value === 'number' &&
      Number.isInteger(value) &&
      value >= 0 &&
      value <= maxCentavos
      ? null
      : 'VALUE_OUT_OF_RANGE';
  }
  if (typeof value !== 'string' || !tariffDecimalPattern.test(value)) return 'VALUE_OUT_OF_RANGE';
  const decimal = toDecimal(value)!;
  return isPositiveUpTo(decimal, kind === 'RATE' ? 1 : 100) ? null : 'VALUE_OUT_OF_RANGE';
}

/** Erros das condições de uma linha, com o ponteiro relativo (`/when/<variável>`). */
export function conditionErrors(when: Condition): TariffError[] {
  const errors: TariffError[] = [];
  for (const [name, condition] of Object.entries(when)) {
    const def = variableDef(name);
    const field = `/when/${name}`;
    if (!def) {
      errors.push({ field, code: 'SCHEMA_VIOLATION' });
      continue;
    }
    if (def.kind === 'set') {
      const list = (condition as { in?: unknown[] } | undefined)?.in;
      if (!Array.isArray(list) || list.length === 0) errors.push({ field, code: 'REQUIRED' });
    } else if (def.kind === 'int' || def.kind === 'money') {
      const range = condition as ValueRange;
      if (range.gte === undefined && range.lt === undefined) {
        errors.push({ field, code: 'REQUIRED' });
        continue;
      }
      const max = def.kind === 'int' ? intLimits.max : maxCentavos;
      const outOfRange =
        (range.gte !== undefined && (range.gte < 0 || range.gte > max)) ||
        (range.lt !== undefined &&
          (range.lt < (def.kind === 'int' ? intLimits.ltMin : 0) || range.lt > max));
      if (outOfRange) errors.push({ field, code: 'VALUE_OUT_OF_RANGE' });
      else if (range.gte !== undefined && range.lt !== undefined && range.gte >= range.lt) {
        errors.push({ field, code: 'INVALID_RANGE' });
      }
    }
  }
  return errors;
}

function rowsErrors(rows: TariffRow[], kind: ValueKind, prefix: string): TariffError[] {
  if (rows.length === 0) return [{ field: `${prefix}/rows`, code: 'REQUIRED' }];
  return rows.flatMap((row, r) => {
    const at = `${prefix}/rows/${r}`;
    const errors = conditionErrors(row.when).map((e) => ({ ...e, field: at + e.field }));
    const valueError = rowValueError(row.value, kind);
    if (valueError) errors.push({ field: `${at}/value`, code: valueError });
    return errors;
  });
}

/**
 * Validação do cliente igual à do servidor (S3-01 §7). Devolve todos os erros, com o ponteiro
 * JSON dentro de `rating`.
 */
export function validateRating(
  rating: Rating,
  context: { coverages: RatingCoverage[]; productType: RatingRulesResponse['productType'] },
): TariffError[] {
  const errors: TariffError[] = [];
  const codes = rating.coverages.map((c) => c.coverageCode);

  if (context.coverages.some((c) => !codes.includes(c.code))) {
    errors.push({ field: '/coverages', code: 'COVERAGE_TARIFF_MISSING' });
  }

  rating.coverages.forEach((tariff, c) => {
    const at = `/coverages/${c}`;
    const coverage = context.coverages.find((x) => x.code === tariff.coverageCode);
    if (!coverage) errors.push({ field: `${at}/coverageCode`, code: 'COVERAGE_NOT_IN_PRODUCT' });
    else if (codes.indexOf(tariff.coverageCode) !== c) {
      errors.push({ field: `${at}/coverageCode`, code: 'DUPLICATE_COVERAGE' });
    }
    if (coverage && !allowedBaseTypes(coverage.capitalType).includes(tariff.base.type)) {
      errors.push({ field: `${at}/base/type`, code: 'BASE_TYPE_NOT_ALLOWED' });
    }
    errors.push(...rowsErrors(tariff.base.rows, valueKindOf(tariff.base.type), `${at}/base`));

    const factorCodes = (tariff.factors ?? []).map((f) => f.code);
    (tariff.factors ?? []).forEach((factor, f) => {
      const fat = `${at}/factors/${f}`;
      if (factor.code === reservedFactorCode) {
        errors.push({ field: `${fat}/code`, code: 'RESERVED_FACTOR_CODE' });
      } else if (!factorCodePattern.test(factor.code)) {
        errors.push({ field: `${fat}/code`, code: 'INVALID_CODE' });
      } else if (factorCodes.indexOf(factor.code) !== f) {
        errors.push({ field: `${fat}/code`, code: 'DUPLICATE_FACTOR' });
      }
      errors.push(...rowsErrors(factor.rows, 'FACTOR', fat));
    });
  });

  const durations = rating.durationFactors ?? [];
  const temporary = context.productType === 'TEMPORARY';
  if (temporary && durations.length === 0) {
    errors.push({ field: '/durationFactors', code: 'DURATION_FACTORS_REQUIRED' });
  }
  durations.forEach((entry, i) => {
    const at = `/durationFactors/${i}`;
    if (durations.findIndex((d) => d.days === entry.days) !== i) {
      errors.push({ field: `${at}/days`, code: 'DUPLICATE_DURATION' });
    }
    if (!temporary && entry.days !== 365) {
      errors.push({ field: `${at}/days`, code: 'DURATION_NOT_ALLOWED' });
    }
    const factor =
      typeof entry.factor === 'string' && tariffDecimalPattern.test(entry.factor)
        ? toDecimal(entry.factor)
        : null;
    if (factor === null || !isPositiveUpTo(factor, 1)) {
      errors.push({ field: `${at}/factor`, code: 'VALUE_OUT_OF_RANGE' });
    } else if (entry.days === 365 && !factor.equals(1)) {
      errors.push({ field: `${at}/factor`, code: 'DURATION_365_NOT_ONE' });
    }
  });
  return errors;
}

/** Tarifa a enviar: sem `durationFactors` vazio fora dos temporários. */
export function ratingForServer(rating: Rating): Rating {
  if (rating.durationFactors && rating.durationFactors.length === 0) {
    const { durationFactors: _omit, ...rest } = rating;
    void _omit;
    return rest as Rating;
  }
  return rating;
}
