import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import FormControl from '@mui/material/FormControl';
import FormControlLabel from '@mui/material/FormControlLabel';
import FormHelperText from '@mui/material/FormHelperText';
import FormLabel from '@mui/material/FormLabel';
import Paper from '@mui/material/Paper';
import Radio from '@mui/material/Radio';
import RadioGroup from '@mui/material/RadioGroup';
import Stack from '@mui/material/Stack';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { saveProposalSuitability, type ProposalResponse } from '../../api/generated';
import { fieldMessage, getProblem, problemMessage } from '../../errors/problem';
import { suitabilityQuestions } from './proposalQuestions';
import { StepActions } from './StepActions';
import { SuitabilityWarnings } from './SuitabilityWarnings';
import { useWizard } from './useProposalMutations';
import { proposalPaths } from './wizardSteps';

function savedAnswers(proposal: ProposalResponse): string[] {
  return suitabilityQuestions.map(
    (question) =>
      proposal.suitability?.answers.find((a) => a.questionCode === question.code)?.answer ?? '',
  );
}

/** Passo `adequacao`: as cinco perguntas do S4-02, os avisos e a confirmação. */
export function SuitabilityStep() {
  const { t } = useTranslation();
  const wizard = useWizard();
  const { proposal } = wizard;
  const saved = savedAnswers(proposal);
  const [answers, setAnswers] = useState<string[]>(saved);
  const [errors, setErrors] = useState<(string | undefined)[]>([]);
  const [message, setMessage] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);

  const unchanged = answers.every((answer, i) => answer === saved[i]);
  const status = proposal.checklist.suitability;
  // Avisos por confirmar e respostas iguais às gravadas: mostra os avisos.
  const showWarnings = unchanged && status === 'ACK_REQUIRED' && !!proposal.suitability;

  async function submit() {
    setMessage(null);
    const missing = answers.map((answer) => (answer ? undefined : 'REQUIRED'));
    setErrors(missing);
    if (missing.some(Boolean)) return;
    if (unchanged && status === 'DONE') {
      wizard.next();
      return;
    }
    setBusy(true);
    try {
      const result = await saveProposalSuitability(proposal.id, {
        answers: suitabilityQuestions.map((question, i) => ({
          questionCode: question.code,
          answer: answers[i]!,
        })),
        version: proposal.version,
      });
      wizard.update(result);
      if (result.suitability?.result !== 'WARNED') wizard.next();
    } catch (error) {
      const problem = getProblem(error);
      if (wizard.handleCommon(problem)) return;
      const next: (string | undefined)[] = [];
      for (const fieldError of problem.errors) {
        const match = /^answers\[(\d+)\]/.exec(fieldError.field);
        if (match) next[Number(match[1])] = fieldError.code;
      }
      if (next.some(Boolean)) {
        setErrors(next);
        return;
      }
      setMessage(problemMessage(t, problem));
    } finally {
      setBusy(false);
    }
  }

  return (
    <Stack spacing={2.5}>
      {suitabilityQuestions.map((question, index) => {
        const id = `suitability-${question.code}`;
        const error = errors[index];
        return (
          <Paper key={question.code} variant="outlined" sx={{ p: 2 }}>
            <FormControl error={!!error} sx={{ width: '100%' }}>
              <FormLabel id={id} sx={{ color: 'text.primary', mb: 0.5 }}>
                {t(`suitability.${question.code}.question`)}
              </FormLabel>
              <RadioGroup
                aria-labelledby={id}
                name={id}
                value={answers[index] ?? ''}
                onChange={(event) => {
                  const value = event.target.value;
                  setAnswers((current) => current.map((a, i) => (i === index ? value : a)));
                  setErrors((current) => current.map((e, i) => (i === index ? undefined : e)));
                }}
              >
                {question.options.map((option) => (
                  <FormControlLabel
                    key={option}
                    value={option}
                    control={<Radio />}
                    label={t(`suitability.${question.code}.option.${option}`)}
                  />
                ))}
              </RadioGroup>
              {error && <FormHelperText>{fieldMessage(t, error)}</FormHelperText>}
            </FormControl>
          </Paper>
        );
      })}

      {!unchanged && proposal.suitability?.acknowledgedAt && (
        <Alert severity="info">{t('proposal.suitability.reconfirm')}</Alert>
      )}

      {showWarnings && proposal.suitability && (
        <SuitabilityWarnings
          proposalId={proposal.id}
          version={proposal.version}
          reasons={proposal.suitability.reasons}
          quotesPath={proposalPaths[wizard.mode].quotes}
          onAcknowledged={(acknowledged) => {
            wizard.update(acknowledged);
            wizard.next();
          }}
          onError={wizard.handleCommon}
        />
      )}

      {message && (
        <Alert severity="error" role="alert">
          {message}
        </Alert>
      )}

      <StepActions onBack={wizard.back}>
        {!showWarnings && (
          <Button variant="contained" disabled={busy} onClick={() => void submit()}>
            {t('proposal.wizard.continue')}
          </Button>
        )}
      </StepActions>
    </Stack>
  );
}
