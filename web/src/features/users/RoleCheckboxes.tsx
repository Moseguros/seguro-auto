import Checkbox from '@mui/material/Checkbox';
import FormControl from '@mui/material/FormControl';
import FormControlLabel from '@mui/material/FormControlLabel';
import FormGroup from '@mui/material/FormGroup';
import FormHelperText from '@mui/material/FormHelperText';
import FormLabel from '@mui/material/FormLabel';
import { useTranslation } from 'react-i18next';
import { fieldMessage } from '../../errors/problem';

/** Caixas de escolha dos perfis atribuíveis (`role.<CODE>`). */
export function RoleCheckboxes({
  roles,
  value,
  onChange,
  errorCode,
}: {
  roles: string[];
  value: string[];
  onChange(value: string[]): void;
  errorCode?: string | null;
}) {
  const { t } = useTranslation();
  return (
    <FormControl component="fieldset" error={!!errorCode}>
      <FormLabel component="legend">{t('users.inviteDialog.roles')}</FormLabel>
      <FormGroup>
        {roles.map((role) => (
          <FormControlLabel
            key={role}
            label={t(`role.${role}`, { defaultValue: role })}
            control={
              <Checkbox
                name={`role-${role}`}
                checked={value.includes(role)}
                onChange={(event) =>
                  onChange(
                    event.target.checked
                      ? roles.filter((r) => r === role || value.includes(r))
                      : value.filter((r) => r !== role),
                  )
                }
              />
            }
          />
        ))}
      </FormGroup>
      {errorCode && <FormHelperText>{fieldMessage(t, errorCode)}</FormHelperText>}
    </FormControl>
  );
}
