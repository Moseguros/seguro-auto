import CheckCircle from '@mui/icons-material/CheckCircle';
import RadioButtonUnchecked from '@mui/icons-material/RadioButtonUnchecked';
import Visibility from '@mui/icons-material/Visibility';
import VisibilityOff from '@mui/icons-material/VisibilityOff';
import IconButton from '@mui/material/IconButton';
import InputAdornment from '@mui/material/InputAdornment';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemIcon from '@mui/material/ListItemIcon';
import ListItemText from '@mui/material/ListItemText';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import {
  forwardRef,
  useId,
  useState,
  type ChangeEventHandler,
  type FocusEventHandler,
} from 'react';
import { useTranslation } from 'react-i18next';
import { fieldMessage } from '../../errors/problem';

/** Mínimo da política de palavras-passe do backend (S1-04). */
export const passwordMinLength = 12;

export type PasswordRule = 'minLength' | 'noEmail' | 'notCommon';

/** Estado de cada regra: cumprida, por cumprir ou desconhecida (só o servidor sabe). */
export function passwordRuleStatus(
  password: string,
  email?: string,
): Record<PasswordRule, boolean | null> {
  const local = email?.split('@')[0]?.trim().toLowerCase() ?? '';
  return {
    minLength: password.length >= passwordMinLength,
    noEmail: local ? password.length > 0 && !password.toLowerCase().includes(local) : null,
    notCommon: null,
  };
}

export interface PasswordFieldProps {
  label: string;
  name: string;
  value?: string;
  onChange?: ChangeEventHandler<HTMLInputElement>;
  onBlur?: FocusEventHandler<HTMLInputElement>;
  autoComplete?: string;
  /** Mostra a lista de regras por baixo do campo. */
  showRules?: boolean;
  /** E-mail da conta, para a regra "não conter o e-mail". */
  email?: string;
  /** Códigos de erro (`validation.<CODE>`), do formulário ou do servidor. */
  errorCodes?: string[];
  /** Mensagem já traduzida (ex.: palavras-passe diferentes). */
  errorText?: string;
  disabled?: boolean;
}

/** Palavra-passe com mostrar/ocultar e, opcionalmente, as regras da política. */
export const PasswordField = forwardRef<HTMLInputElement, PasswordFieldProps>(
  function PasswordField(
    {
      label,
      name,
      value,
      onChange,
      onBlur,
      autoComplete = 'new-password',
      showRules = false,
      email,
      errorCodes = [],
      errorText,
      disabled,
    },
    ref,
  ) {
    const { t } = useTranslation();
    const [visible, setVisible] = useState(false);
    const rulesId = useId();
    const messages = errorCodes.map((code) => fieldMessage(t, code));
    if (errorText) messages.push(errorText);
    const status = passwordRuleStatus(value ?? '', email);
    const rules: PasswordRule[] = ['minLength', 'noEmail', 'notCommon'];

    return (
      <Stack spacing={0.5}>
        <TextField
          label={label}
          name={name}
          value={value}
          onChange={onChange}
          onBlur={onBlur}
          inputRef={ref}
          type={visible ? 'text' : 'password'}
          autoComplete={autoComplete}
          disabled={disabled}
          error={messages.length > 0}
          helperText={messages.length > 0 ? messages.join(' ') : undefined}
          fullWidth
          slotProps={{
            htmlInput: showRules ? { 'aria-describedby': rulesId } : undefined,
            input: {
              endAdornment: (
                <InputAdornment position="end">
                  <IconButton
                    edge="end"
                    aria-label={t(visible ? 'staffAuth.password.hide' : 'staffAuth.password.show')}
                    onClick={() => setVisible((v) => !v)}
                    onMouseDown={(event) => event.preventDefault()}
                  >
                    {visible ? <VisibilityOff /> : <Visibility />}
                  </IconButton>
                </InputAdornment>
              ),
            },
          }}
        />
        {showRules && (
          <div id={rulesId}>
            <Typography variant="body2" color="text.secondary" sx={{ mt: 0.5 }}>
              {t('staffAuth.password.rulesTitle')}
            </Typography>
            <List dense disablePadding aria-label={t('staffAuth.password.rulesTitle')}>
              {rules.map((rule) => {
                const met = status[rule] === true;
                return (
                  <ListItem key={rule} disableGutters data-testid={`rule-${rule}`} data-met={met}>
                    <ListItemIcon sx={{ minWidth: 32 }}>
                      {met ? (
                        <CheckCircle fontSize="small" color="success" />
                      ) : (
                        <RadioButtonUnchecked fontSize="small" color="disabled" />
                      )}
                    </ListItemIcon>
                    <ListItemText
                      primary={t(`staffAuth.password.rules.${rule}`)}
                      secondary={t(
                        met ? 'staffAuth.password.ruleMet' : 'staffAuth.password.ruleNotMet',
                      )}
                      slotProps={{ secondary: { sx: visuallyHidden } }}
                    />
                  </ListItem>
                );
              })}
            </List>
          </div>
        )}
      </Stack>
    );
  },
);

const visuallyHidden = {
  position: 'absolute',
  width: 1,
  height: 1,
  overflow: 'hidden',
  clip: 'rect(0 0 0 0)',
  whiteSpace: 'nowrap',
} as const;
