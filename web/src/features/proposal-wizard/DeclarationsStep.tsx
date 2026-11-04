import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Checkbox from '@mui/material/Checkbox';
import FormControl from '@mui/material/FormControl';
import FormControlLabel from '@mui/material/FormControlLabel';
import FormHelperText from '@mui/material/FormHelperText';
import FormLabel from '@mui/material/FormLabel';
import Paper from '@mui/material/Paper';
import Radio from '@mui/material/Radio';
import RadioGroup from '@mui/material/RadioGroup';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  saveProposalDeclarations,
  type DeclarationAnswerRequest,
  type ProposalResponse,
} from '../../api/generated';
import { fieldMessage, getProblem, problemMessage } from '../../errors/problem';
import { counterText } from '../insurer-proposals/common';
import {
  CLAIMS_COUNT_MAX,
  CLAIMS_COUNT_MIN,
  declarationQuestions,
  DETAILS_MAX,
  DETAILS_MIN,
} from './proposalQuestions';
import { StepActions } from './StepActions';
import { useWizard } from './useProposalMutations';

type Choice = '' | 'yes' | 'no';

interface AnswerState {
  answer: Choice;
  claimsCount: string;
  details: string;
}

/** Erros por pergunta (`answer`, `claimsCount`, `details`), com códigos `validation.*`. */
type AnswerErrors = Partial<Record<keyof AnswerState, string>>;

function initialAnswers(proposal: ProposalResponse): AnswerState[] {
  return declarationQuestions.map((question) => {
    const saved = proposal.declarations?.answers.find((a) => a.questionCode === question.code);
    if (!saved) return { answer: '', claimsCount: '', details: '' };
    return {
      answer: saved.answer ? 'yes' : 'no',
      claimsCount: saved.claimsCount === undefined ? '' : String(saved.claimsCount),
      details: saved.details ?? '',
    };
  });
}

function hasFollowUp(index: number, field: 'claimsCount' | 'details'): boolean {
  return (declarationQuestions[index]?.followUp as readonly string[]).includes(field);
}

/** Validação do cliente, com as regras do S4-02 (o servidor volta a validar). */
export function validateDeclarations(answers: AnswerState[]): AnswerErrors[] {
  return answers.map((state, index) => {
    const errors: AnswerErrors = {};
    if (!state.answer) errors.answer = 'REQUIRED';
    if (state.answer !== 'yes') return errors;
    const details = state.details.trim();
    if (!details) errors.details = 'REQUIRED';
    else if (details.length < DETAILS_MIN) errors.details = 'TOO_SHORT';
    else if (details.length > DETAILS_MAX) errors.details = 'TOO_LONG';
    if (hasFollowUp(index, 'claimsCount')) {
      const text = state.claimsCount.trim();
      const count = /^\d+$/.test(text) ? Number(text) : NaN;
      if (!text) errors.claimsCount = 'REQUIRED';
      else if (!(count >= CLAIMS_COUNT_MIN && count <= CLAIMS_COUNT_MAX)) {
        errors.claimsCount = 'OUT_OF_RANGE';
      }
    }
    return errors;
  });
}

const answerFieldPattern = /^answers\[(\d+)\](?:\.(answer|claimsCount|details|questionCode))?$/;

/** Passo `declaracoes`: as cinco perguntas do S4-02 e a confirmação de veracidade. */
export function DeclarationsStep() {
  const { t } = useTranslation();
  const wizard = useWizard();
  const { proposal } = wizard;
  const [answers, setAnswers] = useState<AnswerState[]>(() => initialAnswers(proposal));
  const [errors, setErrors] = useState<AnswerErrors[]>(() => answers.map(() => ({})));
  // Confirma-se de novo a cada gravação (o servidor exige-o sempre).
  const [truthful, setTruthful] = useState(false);
  const [truthError, setTruthError] = useState(false);
  const [message, setMessage] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);
  const usage = t(`vehicleUsage.${proposal.vehicle.usage}`);

  function change(index: number, patch: Partial<AnswerState>) {
    setAnswers((current) =>
      current.map((state, i) => {
        if (i !== index) return state;
        const next = { ...state, ...patch };
        // "Não" limpa os seguimentos.
        return next.answer === 'no' ? { ...next, claimsCount: '', details: '' } : next;
      }),
    );
    setErrors((current) => current.map((e, i) => (i === index ? {} : e)));
  }

  async function submit() {
    setMessage(null);
    const found = validateDeclarations(answers);
    setErrors(found);
    setTruthError(!truthful);
    if (!truthful || found.some((e) => Object.keys(e).length > 0)) return;

    const request: DeclarationAnswerRequest[] = answers.map((state, index) => {
      const yes = state.answer === 'yes';
      return {
        questionCode: declarationQuestions[index]!.code,
        answer: yes,
        ...(yes && hasFollowUp(index, 'claimsCount')
          ? { claimsCount: Number(state.claimsCount.trim()) }
          : {}),
        ...(yes ? { details: state.details.trim() } : {}),
      };
    });
    setBusy(true);
    try {
      const saved = await saveProposalDeclarations(proposal.id, {
        answers: request,
        truthfulnessConfirmed: true,
        version: proposal.version,
      });
      wizard.update(saved);
      wizard.next();
    } catch (error) {
      const problem = getProblem(error);
      if (wizard.handleCommon(problem)) return;
      if (problem.code === 'VALIDATION_ERROR') {
        const next = answers.map((): AnswerErrors => ({}));
        let applied = false;
        for (const fieldError of problem.errors) {
          if (fieldError.field === 'truthfulnessConfirmed') {
            setTruthError(true);
            applied = true;
            continue;
          }
          const match = answerFieldPattern.exec(fieldError.field);
          const index = match ? Number(match[1]) : -1;
          if (!match || !next[index]) continue;
          const field = (
            match[2] === 'claimsCount' || match[2] === 'details' ? match[2] : 'answer'
          ) as keyof AnswerState;
          next[index]![field] = fieldError.code;
          applied = true;
        }
        if (applied) {
          setErrors(next);
          return;
        }
      }
      setMessage(problemMessage(t, problem));
    } finally {
      setBusy(false);
    }
  }

  return (
    <Stack spacing={2.5}>
      {declarationQuestions.map((question, index) => {
        const state = answers[index]!;
        const error = errors[index] ?? {};
        const id = `declaration-${question.code}`;
        return (
          <Paper key={question.code} variant="outlined" sx={{ p: 2 }}>
            <FormControl error={!!error.answer} sx={{ width: '100%' }}>
              <FormLabel id={id} sx={{ color: 'text.primary', mb: 0.5 }}>
                {t(`declaration.${question.code}.question`, { usage })}
              </FormLabel>
              <RadioGroup
                row
                aria-labelledby={id}
                name={id}
                value={state.answer}
                onChange={(event) => change(index, { answer: event.target.value as Choice })}
              >
                <FormControlLabel value="yes" control={<Radio />} label={t('common.yes')} />
                <FormControlLabel value="no" control={<Radio />} label={t('common.no')} />
              </RadioGroup>
              {error.answer && <FormHelperText>{fieldMessage(t, error.answer)}</FormHelperText>}
            </FormControl>
            {state.answer === 'yes' && (
              <Stack spacing={2} sx={{ mt: 1.5 }}>
                {hasFollowUp(index, 'claimsCount') && (
                  <TextField
                    label={t(`declaration.${question.code}.claimsCount`)}
                    value={state.claimsCount}
                    onChange={(event) => change(index, { claimsCount: event.target.value })}
                    error={!!error.claimsCount}
                    helperText={error.claimsCount ? fieldMessage(t, error.claimsCount) : undefined}
                    slotProps={{
                      htmlInput: {
                        inputMode: 'numeric',
                        min: CLAIMS_COUNT_MIN,
                        max: CLAIMS_COUNT_MAX,
                      },
                    }}
                    sx={{ maxWidth: 200 }}
                  />
                )}
                <TextField
                  label={t(`declaration.${question.code}.details`)}
                  value={state.details}
                  onChange={(event) => change(index, { details: event.target.value })}
                  error={!!error.details}
                  helperText={
                    error.details
                      ? `${fieldMessage(t, error.details)} · ${counterText(state.details, DETAILS_MAX)}`
                      : counterText(state.details, DETAILS_MAX)
                  }
                  multiline
                  minRows={3}
                  fullWidth
                  slotProps={{ htmlInput: { maxLength: DETAILS_MAX + 50 } }}
                />
              </Stack>
            )}
          </Paper>
        );
      })}

      <FormControl error={truthError}>
        <FormControlLabel
          control={
            <Checkbox
              checked={truthful}
              onChange={(event) => {
                setTruthful(event.target.checked);
                setTruthError(false);
              }}
            />
          }
          label={t('declaration.truthfulness')}
        />
        {truthError && <FormHelperText>{t('proposal.declarations.truthRequired')}</FormHelperText>}
      </FormControl>

      {message && (
        <Alert severity="error" role="alert">
          {message}
        </Alert>
      )}

      <StepActions onBack={wizard.back}>
        <Button variant="contained" disabled={busy} onClick={() => void submit()}>
          {t('proposal.wizard.continue')}
        </Button>
      </StepActions>
    </Stack>
  );
}
