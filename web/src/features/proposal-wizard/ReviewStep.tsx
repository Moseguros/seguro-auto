import Description from '@mui/icons-material/Description';
import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Checkbox from '@mui/material/Checkbox';
import Divider from '@mui/material/Divider';
import FormControlLabel from '@mui/material/FormControlLabel';
import FormHelperText from '@mui/material/FormHelperText';
import Link from '@mui/material/Link';
import Stack from '@mui/material/Stack';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Typography from '@mui/material/Typography';
import { useState, type ReactNode } from 'react';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink } from 'react-router-dom';
import {
  requestProposalSignature,
  submitProposal,
  type ProposalResponse,
  type SignatureRequestResponse,
} from '../../api/generated';
import { getProblem, problemMessage, type Problem } from '../../errors/problem';
import { downloadProtectedFile } from '../../files/downloadProtectedFile';
import { formatMoney } from '../../format/money';
import { Field } from '../insurer-proposals/common';
import { chargeLabel, deductibleValue } from '../quotes/quoteFormat';
import { formatLocalDate, PolicyholderData, proposalStartDate } from './PolicyholderStep';
import { declarationQuestions, problemList, suitabilityQuestions } from './proposalQuestions';
import { SignDialog } from './SignDialog';
import { StepActions } from './StepActions';
import { useWizard } from './useProposalMutations';
import { missingFromChecklist, missingItemStep, stepPath } from './wizardSteps';

const money = { fontVariantNumeric: 'tabular-nums', whiteSpace: 'nowrap' } as const;

function Group({ title, children }: { title: string; children: ReactNode }) {
  return (
    <Stack spacing={1}>
      <Typography variant="subtitle1" component="h3" fontWeight={600}>
        {title}
      </Typography>
      {children}
    </Stack>
  );
}

function Grid({ children }: { children: ReactNode }) {
  return (
    <Box
      component="dl"
      sx={{
        m: 0,
        display: 'grid',
        gap: 1.5,
        gridTemplateColumns: { xs: '1fr', sm: 'repeat(2, minmax(0, 1fr))' },
      }}
    >
      {children}
    </Box>
  );
}

/** Resumo completo da proposta (como o `ProposalSummaryCard` do S4-07, sem chassis nem carta). */
export function ProposalFullSummary({ proposal }: { proposal: ProposalResponse }) {
  const { t, i18n } = useTranslation();
  const lang = i18n.language;
  const { vehicle, driver, premium } = proposal;
  const usage = t(`vehicleUsage.${vehicle.usage}`);

  return (
    <Stack spacing={3} divider={<Divider flexItem />}>
      <Group title={t('proposal.summary.productTitle')}>
        <Grid>
          <Field label={t('proposal.summary.insurer')}>{proposal.insurer?.displayName}</Field>
          <Field label={t('proposal.summary.product')}>{proposal.product.name}</Field>
          <Field label={t('proposal.summary.productType')}>
            {t(`productType.${proposal.product.type}`)}
          </Field>
          <Field label={t('proposal.summary.period')}>
            {t('proposal.summary.periodValue', {
              start: formatLocalDate(proposalStartDate(proposal), lang),
              end: formatLocalDate(proposal.endDate, lang),
            })}
          </Field>
          {proposal.broker && (
            <Field label={t('proposal.summary.broker')}>{proposal.broker.displayName}</Field>
          )}
        </Grid>
      </Group>

      <Group title={t('proposal.policyholder.title')}>
        <PolicyholderData proposal={proposal} />
      </Group>

      <Group title={t('proposal.summary.vehicle')}>
        <Grid>
          <Field label={t('proposal.summary.plate')}>{vehicle.plate}</Field>
          <Field
            label={t('proposal.summary.makeModel')}
          >{`${vehicle.make} ${vehicle.model}`}</Field>
          <Field label={t('proposal.summary.year')}>{vehicle.year}</Field>
          <Field label={t('proposal.summary.category')}>
            {t(`vehicleCategory.${vehicle.category}`)}
          </Field>
          <Field label={t('proposal.summary.usage')}>{usage}</Field>
          <Field label={t('proposal.summary.commercialValue')}>
            {vehicle.commercialValue ? formatMoney(vehicle.commercialValue, lang) : ''}
          </Field>
        </Grid>
      </Group>

      <Group title={t('proposal.summary.driver')}>
        <Grid>
          <Field label={t('proposal.summary.fullName')}>{driver.fullName}</Field>
          <Field label={t('proposal.summary.licenceIssueDate')}>
            {formatLocalDate(driver.licenceIssueDate, lang)}
          </Field>
        </Grid>
      </Group>

      <Group title={t('proposal.summary.coverages')}>
        <TableContainer sx={{ maxWidth: '100%', overflowX: 'auto' }}>
          <Table size="small" aria-label={t('proposal.summary.coverages')}>
            <TableHead>
              <TableRow>
                <TableCell>{t('proposal.summary.coverage')}</TableCell>
                <TableCell>{t('proposal.summary.deductible')}</TableCell>
                <TableCell align="right">{t('proposal.summary.premium')}</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {proposal.coverages.map((coverage) => (
                <TableRow key={coverage.code}>
                  <TableCell>
                    {t(`coverage.${coverage.code}`)}
                    {coverage.capital && (
                      <Typography variant="caption" color="text.secondary" display="block">
                        {t('proposal.summary.capital', {
                          amount: formatMoney(coverage.capital, lang),
                        })}
                      </Typography>
                    )}
                  </TableCell>
                  <TableCell>
                    {deductibleValue(t, coverage.deductible) ?? t('proposal.summary.noDeductible')}
                  </TableCell>
                  <TableCell align="right" sx={money}>
                    {formatMoney(coverage.premium, lang)}
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
        <Table size="small" aria-label={t('proposal.summary.premiumTitle')}>
          <TableBody>
            <TableRow>
              <TableCell>{t('proposal.summary.commercial')}</TableCell>
              <TableCell align="right" sx={money}>
                {formatMoney(premium.commercial, lang)}
              </TableCell>
            </TableRow>
            {premium.charges.map((charge) => (
              <TableRow key={charge.code}>
                <TableCell>{chargeLabel(charge)}</TableCell>
                <TableCell align="right" sx={money}>
                  {formatMoney(charge.amount, lang)}
                </TableCell>
              </TableRow>
            ))}
            {premium.roundingAdjustment.amount !== 0 && (
              <TableRow>
                <TableCell>{t('proposal.summary.rounding')}</TableCell>
                <TableCell align="right" sx={money}>
                  {formatMoney(premium.roundingAdjustment, lang)}
                </TableCell>
              </TableRow>
            )}
            <TableRow>
              <TableCell sx={{ fontWeight: 700 }}>{t('proposal.summary.total')}</TableCell>
              <TableCell align="right" sx={{ ...money, fontWeight: 700 }}>
                {formatMoney(premium.total, lang)}
              </TableCell>
            </TableRow>
          </TableBody>
        </Table>
      </Group>

      {proposal.declarations && (
        <Group title={t('proposal.steps.declaracoes')}>
          <Stack spacing={1}>
            {declarationQuestions.map((question) => {
              const answer = proposal.declarations?.answers.find(
                (a) => a.questionCode === question.code,
              );
              if (!answer) return null;
              return (
                <Box key={question.code}>
                  <Typography variant="body2">
                    {t(`declaration.${question.code}.short`)}:{' '}
                    <strong>{answer.answer ? t('common.yes') : t('common.no')}</strong>
                    {answer.claimsCount !== undefined && ` (${answer.claimsCount})`}
                  </Typography>
                  {answer.details && (
                    <Typography
                      variant="body2"
                      color="text.secondary"
                      sx={{ wordBreak: 'break-word', whiteSpace: 'pre-line' }}
                    >
                      {answer.details}
                    </Typography>
                  )}
                </Box>
              );
            })}
          </Stack>
        </Group>
      )}

      {proposal.suitability && (
        <Group title={t('proposal.steps.adequacao')}>
          <Stack spacing={1}>
            {suitabilityQuestions.map((question) => {
              const answer = proposal.suitability?.answers.find(
                (a) => a.questionCode === question.code,
              );
              if (!answer) return null;
              return (
                <Typography key={question.code} variant="body2">
                  {t(`suitability.${question.code}.question`)}{' '}
                  <strong>{t(`suitability.${question.code}.option.${answer.answer}`)}</strong>
                </Typography>
              );
            })}
            {proposal.suitability.result === 'WARNED' &&
              proposal.suitability.reasons.map((reason) => (
                <Alert key={reason} severity="warning">
                  {t(`suitability.reason.${reason}`)}
                </Alert>
              ))}
          </Stack>
        </Group>
      )}

      {proposal.inspection.required && (
        <Group title={t('proposal.steps.vistoria')}>
          <Typography variant="body2">
            {proposal.inspection.status
              ? t(`inspection.status.${proposal.inspection.status}`)
              : t('inspection.status.PENDING')}
            {proposal.inspection.mode && ` · ${t(`inspection.mode.${proposal.inspection.mode}`)}`}
            {proposal.inspection.photos &&
              ` · ${t('proposal.summary.photos', { count: proposal.inspection.photos.length })}`}
          </Typography>
        </Group>
      )}
    </Stack>
  );
}

/** Passo `resumo`: resumo, documentos, o que falta, assinatura e envio. */
export function ReviewStep() {
  const { t } = useTranslation();
  const wizard = useWizard();
  const { proposal, mode } = wizard;
  const [acknowledged, setAcknowledged] = useState(false);
  const [ackError, setAckError] = useState(false);
  const [serverMissing, setServerMissing] = useState<string[] | null>(null);
  const [message, setMessage] = useState<{ text: string; profileLink?: boolean } | null>(null);
  const [signRequest, setSignRequest] = useState<SignatureRequestResponse | null>(null);
  const [busy, setBusy] = useState(false);

  const missing = serverMissing ?? missingFromChecklist(proposal);
  const signed = proposal.checklist.signature === 'DONE' && !!proposal.signature;
  const documents = proposal.product.documents.filter(
    (doc) => doc.type === 'GENERAL_CONDITIONS' || doc.type === 'PRE_CONTRACT_INFO',
  );

  function fail(error: unknown): Problem | null {
    const problem = getProblem(error);
    if (wizard.handleCommon(problem)) return null;
    if (problem.code === 'PROPOSAL_INCOMPLETE') {
      setServerMissing(problemList(error, 'missing'));
    }
    if (problem.errors.some((e) => e.field === 'documentsAcknowledged')) setAckError(true);
    setMessage({
      text: problemMessage(t, problem),
      profileLink: problem.code === 'SIGNER_CONTACT_MISSING' && mode === 'customer',
    });
    return problem;
  }

  async function sign() {
    setMessage(null);
    if (!acknowledged) {
      setAckError(true);
      return;
    }
    setBusy(true);
    try {
      const request = await requestProposalSignature(proposal.id, {
        documentsAcknowledged: true,
        version: proposal.version,
      });
      setServerMissing(null);
      setSignRequest(request);
    } catch (error) {
      fail(error);
    } finally {
      setBusy(false);
    }
  }

  async function submitOnly() {
    setMessage(null);
    setBusy(true);
    try {
      wizard.update(await submitProposal(proposal.id, { version: proposal.version }));
    } catch (error) {
      fail(error);
    } finally {
      setBusy(false);
    }
  }

  async function download(fileId: string, type: string) {
    try {
      await downloadProtectedFile(fileId, `${t(`productDocumentType.${type}`)}.pdf`);
    } catch {
      setMessage({ text: t('files.loadError') });
    }
  }

  return (
    <Stack spacing={3}>
      <Card variant="outlined" elevation={0}>
        <CardContent>
          <ProposalFullSummary proposal={proposal} />
        </CardContent>
      </Card>

      <Stack spacing={1}>
        <Typography variant="h6" component="h2">
          {t('proposal.review.documents')}
        </Typography>
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={1}>
          {documents.map((doc) => (
            <Button
              key={doc.fileId}
              variant="outlined"
              startIcon={<Description />}
              onClick={() => void download(doc.fileId, doc.type)}
            >
              {t(`proposal.review.document.${doc.type}`)}
            </Button>
          ))}
        </Stack>
      </Stack>

      {missing.length > 0 && (
        <Alert severity="warning" data-testid="review-missing">
          <Typography variant="body2" sx={{ mb: 0.5 }}>
            {t('proposal.review.missingTitle')}
          </Typography>
          <Stack component="ul" spacing={0.5} sx={{ m: 0, pl: 2.5 }}>
            {missing.map((item) => (
              <li key={item}>
                <Link
                  component={RouterLink}
                  to={stepPath(mode, proposal.id, missingItemStep[item] ?? 'resumo')}
                >
                  {t(`proposal.missing.${item}`, { defaultValue: item })}
                </Link>
              </li>
            ))}
          </Stack>
        </Alert>
      )}

      {!signed && (
        <Stack>
          <FormControlLabel
            control={
              <Checkbox
                checked={acknowledged}
                onChange={(event) => {
                  setAcknowledged(event.target.checked);
                  setAckError(false);
                }}
              />
            }
            label={t('proposal.review.acknowledge')}
          />
          {ackError && (
            <FormHelperText error>{t('proposal.review.acknowledgeRequired')}</FormHelperText>
          )}
        </Stack>
      )}

      {message && (
        <Alert
          severity="error"
          role="alert"
          action={
            message.profileLink ? (
              <Button color="inherit" size="small" component={RouterLink} to="/cliente/perfil">
                {t('proposal.policyholder.editProfile')}
              </Button>
            ) : undefined
          }
        >
          {message.text}
        </Alert>
      )}

      <StepActions onBack={wizard.back}>
        {signed ? (
          <Button variant="contained" disabled={busy} onClick={() => void submitOnly()}>
            {t('proposal.review.submitOnly')}
          </Button>
        ) : (
          <Button variant="contained" disabled={busy} onClick={() => void sign()}>
            {t('proposal.review.signAndSubmit')}
          </Button>
        )}
      </StepActions>

      {signRequest && (
        <SignDialog
          open
          mode={mode}
          proposal={proposal}
          request={signRequest}
          onClose={() => setSignRequest(null)}
          onSubmitted={(submitted) => {
            setSignRequest(null);
            wizard.update(submitted);
          }}
          onSubmitFailed={(signedProposal, problem) => {
            setSignRequest(null);
            wizard.update(signedProposal);
            setMessage({ text: problemMessage(t, problem) });
          }}
        />
      )}
    </Stack>
  );
}
