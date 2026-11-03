import InputAdornment from '@mui/material/InputAdornment';
import Paper from '@mui/material/Paper';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import { useTranslation } from 'react-i18next';
import { MoneyField } from '../../../components/MoneyField';
import { fieldText } from '../../organizations/OrganizationForm';
import { deductibleTypes, type DeductibleType } from '../coverageRules';
import { errorAt, parsePercent, type CoverageDraft, type FieldErrors } from '../editorModel';

export interface DeductiblesStepProps {
  coverages: CoverageDraft[];
  onChange(coverages: CoverageDraft[]): void;
  readOnly: boolean;
  errors: FieldErrors;
  onValidityChange(key: string, valid: boolean): void;
}

/** Passo 3: a franquia de cada cobertura (uma linha, ou um cartão em ecrãs estreitos). */
export function DeductiblesStep({
  coverages,
  onChange,
  readOnly,
  errors,
  onValidityChange,
}: DeductiblesStepProps) {
  const { t } = useTranslation();

  if (coverages.length === 0) {
    return <Typography color="text.secondary">{t('products.deductibles.empty')}</Typography>;
  }

  return (
    <Stack spacing={2}>
      {coverages.map((coverage, index) => {
        const path = `coverages[${index}]`;
        const name = t(`coverage.${coverage.code}`);
        const set = (patch: Partial<CoverageDraft>) =>
          onChange(coverages.map((c, i) => (i === index ? { ...c, ...patch } : c)));
        const errorOf = (field: string) => errorAt(errors, `${path}.${field}`);
        const percentInvalid = parsePercent(coverage.deductiblePercent) === 'invalid';
        const percentError = percentInvalid ? 'OUT_OF_RANGE' : errorOf('deductiblePercent');
        return (
          <Paper
            key={coverage.code}
            component="section"
            variant="outlined"
            aria-label={name}
            sx={{ p: 2 }}
          >
            <Stack
              direction={{ xs: 'column', md: 'row' }}
              spacing={2}
              alignItems={{ md: 'flex-start' }}
            >
              <Typography sx={{ fontWeight: 600, flex: { md: '0 0 220px' }, pt: { md: 2 } }}>
                {name}
              </Typography>
              <TextField
                select
                label={t('products.deductibles.type')}
                value={coverage.deductibleType}
                onChange={(event) =>
                  set({
                    deductibleType: event.target.value as DeductibleType,
                    deductibleAmount: null,
                    deductiblePercent: '',
                    deductibleMinAmount: null,
                  })
                }
                disabled={readOnly}
                error={!!errorOf('deductibleType')}
                helperText={errorOf('deductibleType') && fieldText(t, errorOf('deductibleType')!)}
                sx={{ minWidth: { md: 200 } }}
                fullWidth
                slotProps={{ select: { native: true }, inputLabel: { shrink: true } }}
              >
                {deductibleTypes.map((type) => (
                  <option key={type} value={type}>
                    {t(`deductibleType.${type}`)}
                  </option>
                ))}
              </TextField>
              {coverage.deductibleType === 'FIXED' && (
                <MoneyField
                  label={t('products.deductibles.amount')}
                  value={coverage.deductibleAmount}
                  onChange={(deductibleAmount) => set({ deductibleAmount })}
                  onValidityChange={(valid) => onValidityChange(`${path}.deductibleAmount`, valid)}
                  disabled={readOnly}
                  error={!!errorOf('deductibleAmount')}
                  helperText={
                    errorOf('deductibleAmount') && fieldText(t, errorOf('deductibleAmount')!)
                  }
                />
              )}
              {coverage.deductibleType === 'PERCENT' && (
                <>
                  <TextField
                    label={t('products.deductibles.percent')}
                    value={coverage.deductiblePercent}
                    onChange={(event) => set({ deductiblePercent: event.target.value })}
                    disabled={readOnly}
                    error={!!percentError}
                    helperText={
                      percentError
                        ? percentInvalid
                          ? t('products.deductibles.percentInvalid')
                          : fieldText(t, percentError)
                        : undefined
                    }
                    fullWidth
                    autoComplete="off"
                    slotProps={{
                      htmlInput: { inputMode: 'decimal' },
                      input: { endAdornment: <InputAdornment position="end">%</InputAdornment> },
                    }}
                  />
                  <MoneyField
                    label={t('products.deductibles.minimum')}
                    value={coverage.deductibleMinAmount}
                    onChange={(deductibleMinAmount) => set({ deductibleMinAmount })}
                    onValidityChange={(valid) =>
                      onValidityChange(`${path}.deductibleMinAmount`, valid)
                    }
                    disabled={readOnly}
                    error={!!errorOf('deductibleMinAmount')}
                    helperText={
                      errorOf('deductibleMinAmount')
                        ? fieldText(t, errorOf('deductibleMinAmount')!)
                        : t('products.deductibles.minimumHelp')
                    }
                  />
                </>
              )}
            </Stack>
          </Paper>
        );
      })}
    </Stack>
  );
}
