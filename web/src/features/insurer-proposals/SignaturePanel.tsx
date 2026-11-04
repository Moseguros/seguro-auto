import ContentCopy from '@mui/icons-material/ContentCopy';
import Box from '@mui/material/Box';
import Chip from '@mui/material/Chip';
import IconButton from '@mui/material/IconButton';
import Paper from '@mui/material/Paper';
import Stack from '@mui/material/Stack';
import Tooltip from '@mui/material/Tooltip';
import Typography from '@mui/material/Typography';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import type { InsurerProposalSignature } from '../../api/generated';
import { formatDateTime } from '../../i18n/format';
import { Field } from './common';

/** Hash abreviado: os 4 primeiros e os 2 últimos caracteres (`9f2c…e1`). */
export function shortHash(hash: string): string {
  return hash.length > 8 ? `${hash.slice(0, 4)}…${hash.slice(-2)}` : hash;
}

function HashField({ hash }: { hash: string }) {
  const { t } = useTranslation();
  const [copied, setCopied] = useState(false);
  const copy = async () => {
    try {
      await navigator.clipboard.writeText(hash);
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    } catch {
      // Sem acesso à área de transferência: o hash continua no `title`.
    }
  };
  return (
    <Stack direction="row" spacing={0.5} alignItems="center">
      <Box component="span" title={hash} sx={{ fontFamily: 'monospace' }}>
        {shortHash(hash)}
      </Box>
      <Tooltip title={copied ? t('common.copied') : t('insurerProposals.signature.copyHash')}>
        <IconButton
          size="small"
          aria-label={t('insurerProposals.signature.copyHash')}
          onClick={() => void copy()}
        >
          <ContentCopy fontSize="inherit" />
        </IconButton>
      </Tooltip>
    </Stack>
  );
}

/** Histórico das assinaturas (válida e invalidadas), da mais recente para a mais antiga. */
export function SignaturePanel({ signatures }: { signatures: InsurerProposalSignature[] }) {
  const { t, i18n } = useTranslation();
  if (signatures.length === 0) {
    return <Typography color="text.secondary">{t('insurerProposals.signature.none')}</Typography>;
  }
  return (
    <Stack spacing={1.5} component="ol" sx={{ listStyle: 'none', p: 0, m: 0 }}>
      {signatures.map((signature) => (
        <Paper
          component="li"
          variant="outlined"
          key={`${signature.signedAt}-${signature.documentHash}`}
          sx={{ p: 1.5 }}
        >
          <Stack spacing={1}>
            <div>
              {signature.invalidatedAt ? (
                <Chip
                  size="small"
                  variant="outlined"
                  label={t('insurerProposals.signature.invalidated', {
                    date: formatDateTime(signature.invalidatedAt, i18n.language),
                    reason: signature.invalidatedReason
                      ? t(
                          `insurerProposals.signature.invalidatedReason.${signature.invalidatedReason}`,
                        )
                      : '—',
                  })}
                  sx={{
                    maxWidth: '100%',
                    height: 'auto',
                    '& .MuiChip-label': { whiteSpace: 'normal' },
                  }}
                />
              ) : (
                <Chip size="small" color="success" label={t('insurerProposals.signature.valid')} />
              )}
            </div>
            <Box
              component="dl"
              sx={{
                m: 0,
                display: 'grid',
                gap: 1.5,
                gridTemplateColumns: { xs: '1fr 1fr', md: 'repeat(5, minmax(0, 1fr))' },
              }}
            >
              <Field label={t('insurerProposals.signature.signedAt')}>
                {formatDateTime(signature.signedAt, i18n.language)}
              </Field>
              <Field label={t('insurerProposals.signature.channel')}>
                {t(`channel.${signature.channel}`)}
              </Field>
              <Field label={t('insurerProposals.signature.destination')}>
                {signature.destinationMasked}
              </Field>
              <Field label={t('insurerProposals.signature.assisted')}>
                {signature.assisted ? t('common.yes') : t('common.no')}
              </Field>
              <Field label={t('insurerProposals.signature.hash')}>
                <HashField hash={signature.documentHash} />
              </Field>
            </Box>
          </Stack>
        </Paper>
      ))}
    </Stack>
  );
}
