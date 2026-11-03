import Add from '@mui/icons-material/Add';
import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Menu from '@mui/material/Menu';
import MenuItem from '@mui/material/MenuItem';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import type { Money } from '../../../api/generated';
import { fieldText } from '../../organizations/OrganizationForm';
import { addableCoverages, type CoverageCode, type ProductType } from '../coverageRules';
import { newCoverage, type CoverageDraft, type FieldErrors } from '../editorModel';
import { CoverageCard } from './CoverageCard';

const maxCoverages = 12;

export interface CoveragesStepProps {
  productType: ProductType;
  coverages: CoverageDraft[];
  onChange(coverages: CoverageDraft[]): void;
  readOnly: boolean;
  errors: FieldErrors;
  minCapital: Money | null;
  onValidityChange(key: string, valid: boolean): void;
}

/** Passo 2: as coberturas da versão, um cartão por cobertura. */
export function CoveragesStep({
  productType,
  coverages,
  onChange,
  readOnly,
  errors,
  minCapital,
  onValidityChange,
}: CoveragesStepProps) {
  const { t } = useTranslation();
  const [anchor, setAnchor] = useState<HTMLElement | null>(null);
  const codes = coverages.map((coverage) => coverage.code);
  const addable = addableCoverages(productType, codes);

  const add = (code: CoverageCode) => {
    setAnchor(null);
    onChange([...coverages, newCoverage(code)]);
  };

  const move = (index: number, offset: -1 | 1) => {
    const target = index + offset;
    if (target < 0 || target >= coverages.length) return;
    const next = [...coverages];
    const [item] = next.splice(index, 1);
    if (item) next.splice(target, 0, item);
    onChange(next);
  };

  return (
    <Stack spacing={2}>
      {errors.coverages && <Alert severity="error">{fieldText(t, errors.coverages)}</Alert>}
      {coverages.length === 0 && (
        <Typography color="text.secondary">{t('products.coverages.empty')}</Typography>
      )}
      {coverages.map((coverage, index) => (
        <CoverageCard
          key={coverage.code}
          index={index}
          count={coverages.length}
          coverage={coverage}
          others={codes.filter((code) => code !== coverage.code)}
          onChange={(next) => onChange(coverages.map((c, i) => (i === index ? next : c)))}
          onMove={(offset) => move(index, offset)}
          onRemove={() => onChange(coverages.filter((_, i) => i !== index))}
          readOnly={readOnly}
          errors={errors}
          minCapital={minCapital}
          onValidityChange={onValidityChange}
        />
      ))}
      {!readOnly && (
        <div>
          <Button
            variant="outlined"
            startIcon={<Add />}
            disabled={addable.length === 0 || coverages.length >= maxCoverages}
            onClick={(event) => setAnchor(event.currentTarget)}
            aria-haspopup="menu"
          >
            {t('products.coverages.add')}
          </Button>
          <Menu anchorEl={anchor} open={!!anchor} onClose={() => setAnchor(null)}>
            {addable.map((code) => (
              <MenuItem key={code} onClick={() => add(code)}>
                {t(`coverage.${code}`)}
              </MenuItem>
            ))}
          </Menu>
        </div>
      )}
    </Stack>
  );
}
