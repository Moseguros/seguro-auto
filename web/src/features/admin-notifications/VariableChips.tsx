import Chip from '@mui/material/Chip';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useTranslation } from 'react-i18next';

/** Texto inserido por uma variável. */
export function variableToken(name: string): string {
  return `{{${name}}}`;
}

/**
 * Insere `{{variável}}` em `text` entre `start` e `end` (a selecção, ou o cursor quando são
 * iguais). Devolve o texto novo e a posição do cursor depois da variável.
 */
export function insertAt(
  text: string,
  name: string,
  start: number | null | undefined,
  end: number | null | undefined,
): { text: string; caret: number } {
  const token = variableToken(name);
  const from = Math.max(0, Math.min(start ?? text.length, text.length));
  const to = Math.max(from, Math.min(end ?? from, text.length));
  return { text: `${text.slice(0, from)}${token}${text.slice(to)}`, caret: from + token.length };
}

/** Uma etiqueta por variável do modelo; clicar insere-a no último campo de texto usado. */
export function VariableChips({
  variables,
  onInsert,
}: {
  variables: string[];
  onInsert(name: string): void;
}) {
  const { t } = useTranslation();
  return (
    <Stack spacing={1}>
      <Typography variant="subtitle2" component="h2">
        {t('notificationTemplates.editor.variables')}
      </Typography>
      <Stack direction="row" spacing={1} useFlexGap flexWrap="wrap">
        {variables.map((name) => (
          <Chip
            key={name}
            label={variableToken(name)}
            variant="outlined"
            size="small"
            onClick={() => onInsert(name)}
            // Não tira o foco ao campo de texto (o cursor fica onde estava).
            onMouseDown={(event) => event.preventDefault()}
            aria-label={t('notificationTemplates.editor.insertVariable', { name })}
            sx={{ fontFamily: 'monospace' }}
          />
        ))}
      </Stack>
      <Typography variant="caption" color="text.secondary">
        {t('notificationTemplates.editor.variablesNote')}
      </Typography>
    </Stack>
  );
}
