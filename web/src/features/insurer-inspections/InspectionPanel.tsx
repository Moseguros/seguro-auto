import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Chip from '@mui/material/Chip';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import type { InspectionResponse, ProposalStatus } from '../../api/generated';
import { Permissions, usePermission } from '../../auth/permissions';
import { formatDateTime } from '../../i18n/format';
import { Field } from '../insurer-proposals/common';
import { ApproveInspectionDialog } from './ApproveInspectionDialog';
import { PhotoGallery } from './PhotoGallery';
import { RejectInspectionDialog } from './RejectInspectionDialog';

export interface InspectionPanelProps {
  inspection: InspectionResponse;
  /** Estado da proposta; `undefined` quando o utilizador só vê a vistoria (decide o backend). */
  proposalStatus?: ProposalStatus;
  onChanged(kind: 'approved' | 'rejected'): void;
  onConflict(message: string): void;
}

/** Vistoria da proposta: cabeçalho, galeria e acções do revisor. */
export function InspectionPanel({
  inspection,
  proposalStatus,
  onChanged,
  onConflict,
}: InspectionPanelProps) {
  const { t, i18n } = useTranslation();
  const canReview = usePermission(Permissions.inspectionReview);
  const [open, setOpen] = useState<'approve' | 'reject' | null>(null);

  if (!inspection.required) {
    return (
      <Typography color="text.secondary">{t('insurerInspections.panel.notRequired')}</Typography>
    );
  }
  const actions =
    canReview &&
    inspection.status === 'SUBMITTED' &&
    (proposalStatus === undefined || proposalStatus === 'UNDER_REVIEW');
  const review = inspection.review;

  return (
    <Stack spacing={2}>
      <Box
        component="dl"
        sx={{
          m: 0,
          display: 'grid',
          gap: 1.5,
          gridTemplateColumns: { xs: '1fr 1fr', md: 'repeat(4, minmax(0, 1fr))' },
        }}
      >
        <Field label={t('insurerInspections.panel.mode')}>
          {inspection.mode ? t(`inspection.mode.${inspection.mode}`) : ''}
        </Field>
        <Field label={t('insurerInspections.panel.status')}>
          {inspection.status && (
            <Chip
              size="small"
              variant="outlined"
              color={
                inspection.status === 'APPROVED'
                  ? 'success'
                  : inspection.status === 'REJECTED'
                    ? 'error'
                    : 'default'
              }
              label={t(`inspection.status.${inspection.status}`)}
            />
          )}
        </Field>
        <Field label={t('insurerInspections.panel.odometer')}>
          {inspection.odometerKm != null
            ? t('insurerInspections.panel.odometerValue', {
                km: new Intl.NumberFormat(i18n.language === 'en' ? 'en' : 'pt-MZ').format(
                  inspection.odometerKm,
                ),
              })
            : ''}
        </Field>
        <Field label={t('insurerInspections.panel.submittedAt')}>
          {formatDateTime(inspection.submittedAt, i18n.language)}
        </Field>
      </Box>
      {inspection.customerNotes && (
        <Box component="dl" sx={{ m: 0 }}>
          <Field label={t('insurerInspections.panel.customerNotes')}>
            {inspection.customerNotes}
          </Field>
        </Box>
      )}
      {review && (
        <Stack spacing={1} sx={{ p: 1.5, border: 1, borderColor: 'divider', borderRadius: 1 }}>
          <Typography variant="caption" color="text.secondary">
            {t('insurerInspections.panel.reviewedAt', {
              date: formatDateTime(review.reviewedAt, i18n.language),
            })}
          </Typography>
          <Box component="dl" sx={{ m: 0, display: 'grid', gap: 1 }}>
            {review.notes && (
              <Field label={t('insurerInspections.panel.reviewNotes')}>{review.notes}</Field>
            )}
            {review.rejectionReason && (
              <Field label={t('insurerInspections.panel.rejectionReason')}>
                {review.rejectionReason}
              </Field>
            )}
            {review.retakeSlots.length > 0 && (
              <Field label={t('insurerInspections.panel.retakeSlots')}>
                {review.retakeSlots.map((slot) => t(`inspection.slot.${slot}`)).join(', ')}
              </Field>
            )}
          </Box>
        </Stack>
      )}
      <PhotoGallery inspection={inspection} />
      {actions && (
        <Stack direction="row" spacing={1} useFlexGap flexWrap="wrap">
          <Button variant="contained" onClick={() => setOpen('approve')}>
            {t('insurerInspections.panel.approve')}
          </Button>
          <Button variant="outlined" onClick={() => setOpen('reject')}>
            {t('insurerInspections.panel.reject')}
          </Button>
        </Stack>
      )}
      {open === 'approve' && (
        <ApproveInspectionDialog
          inspection={inspection}
          onClose={() => setOpen(null)}
          onDone={() => {
            setOpen(null);
            onChanged('approved');
          }}
          onConflict={(message) => {
            setOpen(null);
            onConflict(message);
          }}
        />
      )}
      {open === 'reject' && (
        <RejectInspectionDialog
          inspection={inspection}
          onClose={() => setOpen(null)}
          onDone={() => {
            setOpen(null);
            onChanged('rejected');
          }}
          onConflict={(message) => {
            setOpen(null);
            onConflict(message);
          }}
        />
      )}
    </Stack>
  );
}
