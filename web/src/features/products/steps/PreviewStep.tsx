import Alert from '@mui/material/Alert';
import AlertTitle from '@mui/material/AlertTitle';
import Button from '@mui/material/Button';
import CircularProgress from '@mui/material/CircularProgress';
import Link from '@mui/material/Link';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useQuery } from '@tanstack/react-query';
import type { TFunction } from 'i18next';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  validateProductVersion,
  type ProductVersionResponse,
  type RuleError,
} from '../../../api/generated';
import { getProblem, problemMessage } from '../../../errors/problem';
import { fieldText } from '../../organizations/OrganizationForm';
import { TariffMissingWarning } from '../../tariffs/TariffMissingWarning';
import { CatalogProductPreview } from '../CatalogProductPreview';
import { PublishDialog } from '../PublishDialog';
import { coverageIndex, groupByStep, type EditorStep } from '../publicationErrors';

export interface PreviewStepProps {
  version: ProductVersionResponse;
  /** Versão só de leitura: mostra só a pré-visualização. */
  readOnly: boolean;
  insurerName: string;
  productDescription?: string;
  onFix(step: EditorStep): void;
  onPublished(version: ProductVersionResponse): void;
}

/** De que trata o erro: o documento, a cobertura ou o campo. */
function subject(t: TFunction, error: RuleError, version: ProductVersionResponse): string {
  const { field } = error;
  if (field.startsWith('documents.')) {
    return t(`productDocumentType.${field.slice('documents.'.length)}`);
  }
  if (field === 'vehicleCategories') return t('products.general.categories');
  if (field === 'usages') return t('products.general.usages');
  const index = coverageIndex(field);
  const coverage = index === undefined ? undefined : version.coverages[index];
  if (coverage) return t(`coverage.${coverage.code}`);
  return t('products.steps.coberturas');
}

/** Passo 5: erros de publicação, pré-visualização e publicação. */
export function PreviewStep({
  version,
  readOnly,
  insurerName,
  productDescription,
  onFix,
  onPublished,
}: PreviewStepProps) {
  const { t } = useTranslation();
  const [publishing, setPublishing] = useState(false);
  const [publishErrors, setPublishErrors] = useState<RuleError[] | null>(null);
  const validation = useQuery({
    queryKey: ['productVersion', version.id, 'validate', version.version],
    queryFn: ({ signal }) => validateProductVersion(version.id, undefined, signal),
    enabled: !readOnly,
    refetchOnWindowFocus: false,
  });

  const errors = publishErrors ?? validation.data?.errors ?? [];
  const publishable = !publishErrors && validation.data?.publishable === true;

  return (
    <Stack spacing={3}>
      {!readOnly && validation.isPending && (
        <Stack direction="row" spacing={2} alignItems="center">
          <CircularProgress size={20} />
          <Typography>{t('products.preview.validating')}</Typography>
        </Stack>
      )}
      {!readOnly && validation.isError && (
        <Alert severity="error">{problemMessage(t, getProblem(validation.error))}</Alert>
      )}
      {!readOnly && errors.length > 0 && (
        <Alert severity="warning" data-testid="publication-errors">
          <AlertTitle>{t('products.preview.errorsTitle')}</AlertTitle>
          <Stack spacing={1.5}>
            {groupByStep(errors).map(([step, list]) => (
              <section key={step} aria-label={t(`products.steps.${step}`)}>
                <Typography variant="subtitle2" component="h3">
                  {t(`products.steps.${step}`)}
                </Typography>
                <List dense disablePadding>
                  {list.map((error, index) => (
                    <ListItem
                      key={`${error.field}-${error.code}-${index}`}
                      disableGutters
                      sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}
                      data-testid="publication-error"
                      data-code={error.code}
                    >
                      <span>
                        {subject(t, error, version)}: {fieldText(t, error.code)}
                      </span>
                      <Link component="button" type="button" onClick={() => onFix(step)}>
                        {t('products.preview.fix')}
                      </Link>
                    </ListItem>
                  ))}
                </List>
              </section>
            ))}
          </Stack>
        </Alert>
      )}
      {!readOnly && validation.isSuccess && publishable && (
        <Alert severity="success">{t('products.preview.ready')}</Alert>
      )}
      {!readOnly && <TariffMissingWarning versionId={version.id} />}
      <Stack spacing={1}>
        <Typography variant="h6" component="h3">
          {t('products.preview.title')}
        </Typography>
        <CatalogProductPreview
          insurerName={insurerName}
          productName={version.productName}
          productType={version.productType}
          description={productDescription}
          coverages={version.coverages}
          documents={version.documents}
        />
      </Stack>
      {!readOnly && (
        <div>
          <Button variant="contained" disabled={!publishable} onClick={() => setPublishing(true)}>
            {t('products.publish.button')}
          </Button>
        </div>
      )}
      {publishing && (
        <PublishDialog
          version={version}
          onClose={() => setPublishing(false)}
          onPublished={(next) => {
            setPublishing(false);
            onPublished(next);
          }}
          onNotPublishable={(list) => {
            setPublishing(false);
            setPublishErrors(list);
          }}
        />
      )}
    </Stack>
  );
}
