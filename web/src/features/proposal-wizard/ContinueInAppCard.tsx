import PhoneAndroid from '@mui/icons-material/PhoneAndroid';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useTheme } from '@mui/material/styles';
import useMediaQuery from '@mui/material/useMediaQuery';
import { QRCodeSVG } from 'qrcode.react';
import { useTranslation } from 'react-i18next';

/** Ligação da app para a vistoria guiada (deep link `moseguros://`, AGENTS.md §1). */
export function inspectionAppLink(proposalId: string): string {
  return `moseguros://proposal/${proposalId}/inspection`;
}

/** "Continuar na app": QR em ecrã largo (≥ 900 px), botão no telemóvel. */
export function ContinueInAppCard({ proposalId }: { proposalId: string }) {
  const { t } = useTranslation();
  const theme = useTheme();
  const wide = useMediaQuery(theme.breakpoints.up('md'), { noSsr: true });
  const link = inspectionAppLink(proposalId);

  return (
    <Card variant="outlined" elevation={0} data-testid="continue-in-app">
      <CardContent>
        {wide ? (
          <Stack direction="row" spacing={2.5} alignItems="center">
            <Box sx={{ p: 1, bgcolor: '#fff', borderRadius: 1, lineHeight: 0 }}>
              <QRCodeSVG value={link} size={128} aria-label={t('proposal.inspection.qrLabel')} />
            </Box>
            <Typography>{t('proposal.inspection.qrText')}</Typography>
          </Stack>
        ) : (
          <Stack spacing={1.5}>
            <Typography>{t('proposal.inspection.appText')}</Typography>
            <Button
              variant="outlined"
              href={link}
              startIcon={<PhoneAndroid />}
              sx={{ alignSelf: 'flex-start' }}
            >
              {t('proposal.inspection.openInApp')}
            </Button>
          </Stack>
        )}
      </CardContent>
    </Card>
  );
}
