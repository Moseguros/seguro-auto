import Add from '@mui/icons-material/Add';
import ArrowDownward from '@mui/icons-material/ArrowDownward';
import ArrowUpward from '@mui/icons-material/ArrowUpward';
import Close from '@mui/icons-material/Close';
import Delete from '@mui/icons-material/Delete';
import Autocomplete from '@mui/material/Autocomplete';
import Button from '@mui/material/Button';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Chip from '@mui/material/Chip';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import FormControlLabel from '@mui/material/FormControlLabel';
import FormHelperText from '@mui/material/FormHelperText';
import IconButton from '@mui/material/IconButton';
import Stack from '@mui/material/Stack';
import Switch from '@mui/material/Switch';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import { useId, useState } from 'react';
import { useTranslation } from 'react-i18next';
import type { Money } from '../../../api/generated';
import { MoneyField } from '../../../components/MoneyField';
import { formatMoney } from '../../../format/money';
import { fieldText } from '../../organizations/OrganizationForm';
import {
  capitalTypesFor,
  hasCapitalAmount,
  isOwnDamage,
  lockedFields,
  type CapitalType,
  type CoverageCode,
} from '../coverageRules';
import { countryName, homeCountry, sadcCountries } from '../countries';
import { errorAt, type CoverageDraft, type FieldErrors } from '../editorModel';

const maxOptions = 10;

export interface CoverageCardProps {
  index: number;
  count: number;
  coverage: CoverageDraft;
  /** Códigos das outras coberturas da lista (para "Exige as coberturas"). */
  others: CoverageCode[];
  onChange(coverage: CoverageDraft): void;
  onMove(offset: -1 | 1): void;
  onRemove(): void;
  readOnly: boolean;
  errors: FieldErrors;
  /** `MTPL_MIN_CAPITAL`, para as ajudas de `MTPL` e `VTPL`. */
  minCapital: Money | null;
  onValidityChange(key: string, valid: boolean): void;
}

/** Uma cobertura no passo 2, com as ajudas das regras de publicação. */
export function CoverageCard({
  index,
  count,
  coverage,
  others,
  onChange,
  onMove,
  onRemove,
  readOnly,
  errors,
  minCapital,
  onValidityChange,
}: CoverageCardProps) {
  const { t, i18n } = useTranslation();
  const titleId = useId();
  const [confirmRemove, setConfirmRemove] = useState(false);
  const name = t(`coverage.${coverage.code}`);
  const locked = lockedFields(coverage.code);
  const path = `coverages[${index}]`;
  const errorOf = (field: string) => errorAt(errors, `${path}.${field}`);
  const helper = (field: string, fallback?: string) => {
    const code = errorOf(field);
    return code ? fieldText(t, code) : fallback;
  };
  const minimum = minCapital ? formatMoney(minCapital) : '—';
  const capitalHelp =
    coverage.code === 'MTPL'
      ? t('products.coverages.mtplMinimum', { value: minimum })
      : coverage.code === 'VTPL'
        ? t('products.coverages.vtplMinimum', { value: minimum })
        : undefined;

  const set = (patch: Partial<CoverageDraft>) => onChange({ ...coverage, ...patch });

  const changeCapitalType = (capitalType: CapitalType) => {
    set({
      capitalType,
      capital: hasCapitalAmount(capitalType) ? coverage.capital : null,
      capitalOptions:
        capitalType === 'OPTIONS'
          ? coverage.capitalOptions.length >= 2
            ? coverage.capitalOptions
            : [null, null]
          : [],
    });
  };

  const setOption = (position: number, value: Money | null) =>
    set({
      capitalOptions: coverage.capitalOptions.map((option, i) => (i === position ? value : option)),
    });

  const requiresChoices = Array.from(new Set([...others, ...coverage.requiresCodes])).filter(
    (code) => code !== coverage.code,
  );

  return (
    <Card component="section" variant="outlined" aria-labelledby={titleId}>
      <CardContent>
        <Stack spacing={2}>
          <Stack direction="row" spacing={1} alignItems="center" justifyContent="space-between">
            <Typography id={titleId} variant="h6" component="h3" sx={{ minWidth: 0 }}>
              {name}
            </Typography>
            {!readOnly && (
              <Stack direction="row" spacing={0.5} sx={{ flexShrink: 0 }}>
                <IconButton
                  aria-label={t('products.coverages.moveUp', { name })}
                  disabled={index === 0}
                  onClick={() => onMove(-1)}
                  size="small"
                >
                  <ArrowUpward fontSize="small" />
                </IconButton>
                <IconButton
                  aria-label={t('products.coverages.moveDown', { name })}
                  disabled={index === count - 1}
                  onClick={() => onMove(1)}
                  size="small"
                >
                  <ArrowDownward fontSize="small" />
                </IconButton>
                <IconButton
                  aria-label={t('products.coverages.remove', { name })}
                  onClick={() => setConfirmRemove(true)}
                  size="small"
                >
                  <Delete fontSize="small" />
                </IconButton>
              </Stack>
            )}
          </Stack>
          {errorOf('code') && <FormHelperText error>{helper('code')}</FormHelperText>}
          <div>
            <FormControlLabel
              label={t('products.coverages.mandatory')}
              control={
                <Switch
                  checked={coverage.mandatory}
                  disabled={readOnly || locked.mandatory}
                  onChange={(event) => set({ mandatory: event.target.checked })}
                />
              }
            />
            {errorOf('mandatory') && <FormHelperText error>{helper('mandatory')}</FormHelperText>}
          </div>
          <TextField
            select
            label={t('products.coverages.capitalType')}
            value={coverage.capitalType}
            onChange={(event) => changeCapitalType(event.target.value as CapitalType)}
            disabled={readOnly || locked.capitalType}
            error={!!errorOf('capitalType')}
            helperText={helper(
              'capitalType',
              isOwnDamage(coverage.code) ? t('products.coverages.vehicleValueHelp') : undefined,
            )}
            fullWidth
            slotProps={{ select: { native: true }, inputLabel: { shrink: true } }}
          >
            {capitalTypesFor(coverage.code).map((type) => (
              <option key={type} value={type}>
                {t(`capitalType.${type}`)}
              </option>
            ))}
          </TextField>
          {hasCapitalAmount(coverage.capitalType) && (
            <MoneyField
              label={
                coverage.capitalType === 'PER_PERSON'
                  ? t('products.coverages.capitalPerPerson')
                  : t('products.coverages.capital')
              }
              value={coverage.capital}
              onChange={(capital) => set({ capital })}
              onValidityChange={(valid) => onValidityChange(`${path}.capital`, valid)}
              disabled={readOnly}
              error={!!errorOf('capital')}
              helperText={helper('capital', capitalHelp)}
            />
          )}
          {coverage.capitalType === 'OPTIONS' && (
            <Stack spacing={1.5}>
              <Typography variant="subtitle2" component="p">
                {t('products.coverages.options')}
              </Typography>
              {coverage.capitalOptions.map((option, position) => (
                <Stack key={position} direction="row" spacing={1} alignItems="flex-start">
                  <MoneyField
                    label={t('products.coverages.option', { n: position + 1 })}
                    value={option}
                    onChange={(value) => setOption(position, value)}
                    onValidityChange={(valid) =>
                      onValidityChange(`${path}.capitalOptions[${position}]`, valid)
                    }
                    disabled={readOnly}
                    error={!!errorAt(errors, `${path}.capitalOptions[${position}]`)}
                  />
                  {!readOnly && coverage.capitalOptions.length > 2 && (
                    <IconButton
                      aria-label={t('products.coverages.removeOption', { n: position + 1 })}
                      onClick={() =>
                        set({
                          capitalOptions: coverage.capitalOptions.filter((_, i) => i !== position),
                        })
                      }
                      sx={{ mt: 1 }}
                    >
                      <Close />
                    </IconButton>
                  )}
                </Stack>
              ))}
              {(errorOf('capitalOptions') || capitalHelp) && (
                <FormHelperText error={!!errorOf('capitalOptions')}>
                  {helper('capitalOptions', capitalHelp)}
                </FormHelperText>
              )}
              {!readOnly && coverage.capitalOptions.length < maxOptions && (
                <div>
                  <Button
                    size="small"
                    startIcon={<Add />}
                    onClick={() => set({ capitalOptions: [...coverage.capitalOptions, null] })}
                  >
                    {t('products.coverages.addOption')}
                  </Button>
                </div>
              )}
            </Stack>
          )}
          <Autocomplete
            multiple
            options={requiresChoices}
            value={coverage.requiresCodes}
            onChange={(_, value) => set({ requiresCodes: value as CoverageCode[] })}
            getOptionLabel={(code) => t(`coverage.${code}`)}
            disabled={readOnly}
            filterSelectedOptions
            renderInput={(params) => (
              <TextField
                {...params}
                label={t('products.coverages.requires')}
                error={!!errorOf('requiresCodes')}
                helperText={helper('requiresCodes')}
              />
            )}
          />
          <Autocomplete
            multiple
            options={[...sadcCountries]}
            value={coverage.territorialScope}
            onChange={(_, value) =>
              set({
                territorialScope: value.includes(homeCountry) ? value : [homeCountry, ...value],
              })
            }
            getOptionLabel={(code) => countryName(code, i18n.language)}
            disabled={readOnly}
            filterSelectedOptions
            disableClearable
            renderTags={(value, getTagProps) =>
              value.map((code, position) => {
                const { key, onDelete, ...tagProps } = getTagProps({ index: position });
                return (
                  <Chip
                    key={key}
                    {...tagProps}
                    size="small"
                    label={countryName(code, i18n.language)}
                    onDelete={code === homeCountry || readOnly ? undefined : onDelete}
                  />
                );
              })
            }
            renderInput={(params) => (
              <TextField
                {...params}
                label={t('products.coverages.scope')}
                error={!!errorOf('territorialScope')}
                helperText={helper('territorialScope')}
              />
            )}
          />
          <TextField
            label={t('products.coverages.description')}
            value={coverage.description}
            onChange={(event) => set({ description: event.target.value })}
            disabled={readOnly}
            multiline
            minRows={2}
            fullWidth
            error={!!errorOf('description')}
            helperText={helper('description')}
            slotProps={{ htmlInput: { maxLength: 1000 } }}
          />
          <TextField
            label={t('products.coverages.exclusions')}
            value={coverage.exclusions}
            onChange={(event) => set({ exclusions: event.target.value })}
            disabled={readOnly}
            multiline
            minRows={3}
            fullWidth
            error={!!errorOf('exclusions')}
            helperText={helper('exclusions')}
            slotProps={{ htmlInput: { maxLength: 4000 } }}
          />
        </Stack>
      </CardContent>
      <Dialog
        open={confirmRemove}
        onClose={() => setConfirmRemove(false)}
        aria-labelledby={`${titleId}-remove`}
      >
        <DialogTitle id={`${titleId}-remove`}>
          {t('products.coverages.removeDialog.title', { name })}
        </DialogTitle>
        <DialogContent>
          <DialogContentText>{t('products.coverages.removeDialog.text')}</DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setConfirmRemove(false)}>{t('common.cancel')}</Button>
          <Button
            color="error"
            variant="contained"
            onClick={() => {
              setConfirmRemove(false);
              onRemove();
            }}
          >
            {t('products.coverages.removeDialog.confirm')}
          </Button>
        </DialogActions>
      </Dialog>
    </Card>
  );
}
