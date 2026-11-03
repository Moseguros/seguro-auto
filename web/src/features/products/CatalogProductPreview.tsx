import Description from '@mui/icons-material/Description';
import Avatar from '@mui/material/Avatar';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Chip from '@mui/material/Chip';
import Divider from '@mui/material/Divider';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemIcon from '@mui/material/ListItemIcon';
import ListItemText from '@mui/material/ListItemText';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import type { TFunction } from 'i18next';
import { useTranslation } from 'react-i18next';
import type { Money } from '../../api/generated';
import { formatMoney } from '../../format/money';

/** Cobertura como o cliente a vê (mesmos campos do catálogo público). */
export interface PreviewCoverage {
  code: string;
  mandatory: boolean;
  capitalType: string;
  capital?: Money | null;
  capitalOptions?: (Money | null)[];
  deductibleType: string;
  deductibleAmount?: Money | null;
  deductiblePercent?: number | null;
  deductibleMinAmount?: Money | null;
}

export interface PreviewDocument {
  docType: string;
  language: string;
  title?: string;
}

export interface CatalogProductPreviewProps {
  insurerName: string;
  productName: string;
  productType: string;
  description?: string;
  coverages: PreviewCoverage[];
  documents: PreviewDocument[];
}

function initials(name: string): string {
  return name
    .split(/\s+/)
    .filter(Boolean)
    .slice(0, 2)
    .map((word) => word[0]?.toUpperCase() ?? '')
    .join('');
}

export function capitalText(t: TFunction, coverage: PreviewCoverage): string {
  switch (coverage.capitalType) {
    case 'VEHICLE_VALUE':
      return t('capitalType.VEHICLE_VALUE');
    case 'OPTIONS': {
      const values = (coverage.capitalOptions ?? []).filter((m): m is Money => !!m);
      return values.length ? values.map(formatMoney).join(' · ') : '—';
    }
    case 'PER_PERSON':
      return coverage.capital
        ? t('products.preview.perPerson', { value: formatMoney(coverage.capital) })
        : '—';
    default:
      return coverage.capital ? formatMoney(coverage.capital) : '—';
  }
}

export function deductibleText(t: TFunction, coverage: PreviewCoverage): string {
  switch (coverage.deductibleType) {
    case 'FIXED':
      return coverage.deductibleAmount ? formatMoney(coverage.deductibleAmount) : '—';
    case 'PERCENT': {
      const percent =
        coverage.deductiblePercent == null
          ? '—'
          : `${String(coverage.deductiblePercent).replace('.', ',')} %`;
      return coverage.deductibleMinAmount
        ? t('products.preview.percentWithMin', {
            percent,
            min: formatMoney(coverage.deductibleMinAmount),
          })
        : percent;
    }
    default:
      return t('deductibleType.NONE');
  }
}

/**
 * O produto como o cliente o vai ver no catálogo. Só apresentação (sem chamadas à API), para o
 * catálogo do cliente (S3) o reutilizar.
 */
export function CatalogProductPreview({
  insurerName,
  productName,
  productType,
  description,
  coverages,
  documents,
}: CatalogProductPreviewProps) {
  const { t } = useTranslation();
  return (
    <Card variant="outlined" elevation={0} data-testid="catalog-preview">
      <CardContent>
        <Stack spacing={2}>
          <Stack direction="row" spacing={2} alignItems="center" sx={{ minWidth: 0 }}>
            <Avatar sx={{ bgcolor: 'primary.main' }}>{initials(insurerName)}</Avatar>
            <Stack sx={{ minWidth: 0 }}>
              <Typography variant="body2" color="text.secondary">
                {insurerName}
              </Typography>
              <Typography variant="h6" component="p" sx={{ wordBreak: 'break-word' }}>
                {productName}
              </Typography>
            </Stack>
          </Stack>
          <div>
            <Chip size="small" label={t(`productType.${productType}`)} />
          </div>
          {description && <Typography variant="body2">{description}</Typography>}
          <Divider />
          <Typography variant="subtitle2" component="h3">
            {t('products.preview.coverages')}
          </Typography>
          {coverages.length === 0 && (
            <Typography variant="body2" color="text.secondary">
              {t('products.preview.noCoverages')}
            </Typography>
          )}
          <Stack spacing={1.5}>
            {coverages.map((coverage) => (
              <Stack key={coverage.code} spacing={0.25}>
                <Stack direction="row" spacing={1} alignItems="center" flexWrap="wrap" useFlexGap>
                  <Typography variant="body1" sx={{ fontWeight: 600 }}>
                    {t(`coverage.${coverage.code}`)}
                  </Typography>
                  {coverage.mandatory && (
                    <Chip size="small" variant="outlined" label={t('products.preview.included')} />
                  )}
                </Stack>
                <Typography variant="body2" color="text.secondary">
                  {t('products.preview.capital')}: {capitalText(t, coverage)}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  {t('products.preview.deductible')}: {deductibleText(t, coverage)}
                </Typography>
              </Stack>
            ))}
          </Stack>
          <Divider />
          <Typography variant="subtitle2" component="h3">
            {t('products.preview.documents')}
          </Typography>
          <List dense disablePadding>
            {documents.map((document, index) => (
              <ListItem key={`${document.docType}-${document.language}-${index}`} disableGutters>
                <ListItemIcon sx={{ minWidth: 32 }}>
                  <Description fontSize="small" />
                </ListItemIcon>
                <ListItemText
                  primary={document.title || t(`productDocumentType.${document.docType}`)}
                  secondary={t(`products.documents.language.${document.language}`)}
                />
              </ListItem>
            ))}
          </List>
        </Stack>
      </CardContent>
    </Card>
  );
}
