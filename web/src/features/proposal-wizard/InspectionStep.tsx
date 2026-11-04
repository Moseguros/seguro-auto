import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import Link from '@mui/material/Link';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  removeInspectionPhoto,
  submitInspection,
  uploadInspectionPhoto,
  type InspectionPhotoResponse,
  type PhotoSlot,
} from '../../api/generated';
import { fieldMessage, getProblem, problemMessage } from '../../errors/problem';
import { ContinueInAppCard } from './ContinueInAppCard';
import { isTooLarge, prepareImage, type ImageCodec } from './imageCompression';
import { OnSiteDialog } from './OnSiteDialog';
import { PhotoPickerButton, PhotoSlotCard, PhotoUploadError } from './PhotoSlotCard';
import { problemList } from './proposalQuestions';
import { StepActions } from './StepActions';
import { useWizard } from './useProposalMutations';

/** Ângulos obrigatórios, pela ordem do S4-03. */
export const requiredSlots: PhotoSlot[] = ['FRONT', 'REAR', 'LEFT', 'RIGHT', 'ODOMETER', 'CHASSIS'];

const ODOMETER_MAX = 2_000_000;

/** Passo `vistoria`: fotografias por ângulo, quilometragem e envio (S4-08 §3). */
export function InspectionStep({ codec }: { codec?: ImageCodec } = {}) {
  const { t } = useTranslation();
  const wizard = useWizard();
  const { proposal } = wizard;
  const inspection = proposal.inspection;
  const photos = inspection.photos ?? [];
  const [odometer, setOdometer] = useState(
    inspection.odometerKm === undefined ? '' : String(inspection.odometerKm),
  );
  const [odometerError, setOdometerError] = useState<string | null>(null);
  const [flagged, setFlagged] = useState<PhotoSlot[]>([]);
  const [message, setMessage] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);
  const [onSiteOpen, setOnSiteOpen] = useState(false);
  const [removing, setRemoving] = useState<InspectionPhotoResponse | null>(null);
  const [extraError, setExtraError] = useState<string | null>(null);

  const editable = inspection.status === 'PENDING' || inspection.status === 'REJECTED';
  const missingSlots = inspection.missingSlots ?? [];
  const markedMissing = new Set<PhotoSlot>([...missingSlots, ...flagged]);
  const retakeSlots =
    inspection.status === 'REJECTED' ? (inspection.review?.retakeSlots ?? []) : [];
  const extras = photos.filter((photo) => photo.slot === 'EXTRA');
  const maxExtras = inspection.maxExtraPhotos ?? 0;
  const photoOf = (slot: PhotoSlot) => photos.find((photo) => photo.slot === slot);

  async function upload(
    slot: PhotoSlot,
    file: File,
    clientPhotoId: string,
    onProgress: (percent: number) => void,
  ) {
    const prepared = await prepareImage(file, codec);
    if (isTooLarge(prepared)) throw new PhotoUploadError(t('errors.FILE_TOO_LARGE'));
    try {
      await uploadInspectionPhoto(
        proposal.id,
        {
          file: prepared,
          slot,
          source: 'UPLOAD',
          clientPhotoId,
          ...(file.lastModified ? { takenAt: new Date(file.lastModified).toISOString() } : {}),
        },
        {
          onUploadProgress: (event) => {
            if (event.total) onProgress(Math.round((event.loaded / event.total) * 100));
          },
        },
      );
    } catch (error) {
      const problem = getProblem(error);
      if (wizard.handleCommon(problem)) throw new PhotoUploadError(problemMessage(t, problem));
      const fieldError = problem.errors[0];
      throw new PhotoUploadError(
        fieldError ? fieldMessage(t, fieldError.code) : problemMessage(t, problem),
      );
    }
    setFlagged((current) => current.filter((s) => s !== slot));
    await wizard.reload();
  }

  async function remove(photo: InspectionPhotoResponse) {
    setRemoving(null);
    setMessage(null);
    try {
      await removeInspectionPhoto(proposal.id, photo.id);
      await wizard.reload();
    } catch (error) {
      const problem = getProblem(error);
      if (!wizard.handleCommon(problem)) setMessage(problemMessage(t, problem));
    }
  }

  async function submit() {
    setMessage(null);
    const text = odometer.trim();
    const km = /^\d+$/.test(text) ? Number(text) : NaN;
    if (!text) {
      setOdometerError('REQUIRED');
      return;
    }
    if (!(km >= 0 && km <= ODOMETER_MAX)) {
      setOdometerError('OUT_OF_RANGE');
      return;
    }
    setBusy(true);
    try {
      const saved = await submitInspection(proposal.id, {
        odometerKm: km,
        version: proposal.version,
      });
      wizard.update(saved);
      wizard.next();
    } catch (error) {
      const problem = getProblem(error);
      if (wizard.handleCommon(problem)) return;
      if (problem.code === 'INSPECTION_PHOTOS_MISSING') {
        setFlagged(problemList(error, 'missingSlots') as PhotoSlot[]);
        setMessage(problemMessage(t, problem));
        return;
      }
      const odometerFieldError = problem.errors.find((e) => e.field === 'odometerKm');
      if (odometerFieldError) {
        setOdometerError(odometerFieldError.code);
        return;
      }
      setMessage(problemMessage(t, problem));
    } finally {
      setBusy(false);
    }
  }

  let statusAlert = null;
  if (inspection.status === 'SUBMITTED') {
    statusAlert = (
      <Alert severity="success">
        {inspection.mode === 'ON_SITE'
          ? t('proposal.inspection.onSiteRequested')
          : t('proposal.inspection.submitted')}
      </Alert>
    );
  } else if (inspection.status === 'APPROVED') {
    statusAlert = <Alert severity="success">{t('proposal.inspection.approved')}</Alert>;
  } else if (inspection.status === 'REJECTED') {
    statusAlert = (
      <Alert severity="error">
        {t('proposal.inspection.rejected', {
          reason: inspection.review?.rejectionReason ?? '',
        })}
        {retakeSlots.length > 0 && (
          <>
            {' '}
            {t('proposal.inspection.retakeList', {
              slots: retakeSlots.map((slot) => t(`inspection.slot.${slot}`)).join(', '),
            })}
          </>
        )}
      </Alert>
    );
  }

  return (
    <Stack spacing={2.5}>
      <Typography>{t('proposal.inspection.intro')}</Typography>
      {statusAlert}
      {editable && <ContinueInAppCard proposalId={proposal.id} />}

      <Stack spacing={2}>
        {requiredSlots.map((slot) => (
          <PhotoSlotCard
            key={slot}
            slot={slot}
            photo={photoOf(slot)}
            missing={editable && markedMissing.has(slot)}
            retake={retakeSlots.includes(slot)}
            readOnly={!editable}
            upload={(file, id, onProgress) => upload(slot, file, id, onProgress)}
          />
        ))}
      </Stack>

      <Stack spacing={1.5}>
        <Typography variant="h6" component="h2">
          {t('proposal.inspection.extraTitle')}
        </Typography>
        {extras.map((photo, index) => (
          <PhotoSlotCard
            key={photo.id}
            slot="EXTRA"
            title={t('proposal.inspection.extraN', { n: index + 1 })}
            photo={photo}
            readOnly={!editable}
            upload={(file, id, onProgress) => upload('EXTRA', file, id, onProgress)}
            onRemove={() => setRemoving(photo)}
          />
        ))}
        {editable && extras.length < maxExtras && (
          <Stack spacing={1}>
            <Typography variant="body2" color="text.secondary">
              {t('inspection.instruction.EXTRA')}
            </Typography>
            <PhotoPickerButton
              label={t('proposal.inspection.addExtra')}
              variant="text"
              onFile={(file) => {
                setExtraError(null);
                upload('EXTRA', file, crypto.randomUUID(), () => undefined).catch((failure) =>
                  setExtraError(
                    failure instanceof PhotoUploadError
                      ? failure.message
                      : t('proposal.inspection.uploadFailed'),
                  ),
                );
              }}
            />
            {extraError && (
              <Alert severity="error" role="alert">
                {extraError}
              </Alert>
            )}
          </Stack>
        )}
      </Stack>

      {editable && (
        <TextField
          label={t('proposal.inspection.odometer')}
          value={odometer}
          onChange={(event) => {
            setOdometer(event.target.value);
            setOdometerError(null);
          }}
          error={!!odometerError}
          helperText={
            odometerError ? fieldMessage(t, odometerError) : t('proposal.inspection.odometerHelp')
          }
          slotProps={{ htmlInput: { inputMode: 'numeric' } }}
          sx={{ maxWidth: 320 }}
        />
      )}

      {editable && (
        <Link
          component="button"
          type="button"
          onClick={() => setOnSiteOpen(true)}
          sx={{ alignSelf: 'flex-start' }}
        >
          {t('proposal.inspection.preferOnSite')}
        </Link>
      )}

      {message && (
        <Alert severity="error" role="alert">
          {message}
        </Alert>
      )}

      <StepActions onBack={wizard.back}>
        {editable ? (
          <Button
            variant="contained"
            disabled={busy || missingSlots.length > 0}
            onClick={() => void submit()}
          >
            {t('proposal.inspection.submit')}
          </Button>
        ) : (
          <Button variant="contained" onClick={wizard.next}>
            {t('proposal.wizard.continue')}
          </Button>
        )}
      </StepActions>

      <OnSiteDialog
        open={onSiteOpen}
        proposal={proposal}
        onClose={() => setOnSiteOpen(false)}
        onSaved={(saved) => {
          setOnSiteOpen(false);
          wizard.update(saved);
        }}
        onError={wizard.handleCommon}
      />

      <Dialog open={!!removing} onClose={() => setRemoving(null)}>
        <DialogContent>
          <DialogContentText>{t('proposal.inspection.removeConfirm')}</DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setRemoving(null)}>{t('common.cancel')}</Button>
          <Button color="error" onClick={() => removing && void remove(removing)}>
            {t('proposal.inspection.remove')}
          </Button>
        </DialogActions>
      </Dialog>
    </Stack>
  );
}
