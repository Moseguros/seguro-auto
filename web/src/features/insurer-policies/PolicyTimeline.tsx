import Box from '@mui/material/Box';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useTranslation } from 'react-i18next';
import type { PolicyTimelineEvent } from '../../api/generated';
import { formatDateTime } from '../../i18n/format';

/** Linha temporal da apólice (S6-05 §6): data e hora, acontecimento e quem o fez. */
export function PolicyTimeline({ events }: { events: PolicyTimelineEvent[] }) {
  const { t, i18n } = useTranslation();
  if (events.length === 0) return <Typography>{t('insurerPolicies.timeline.empty')}</Typography>;
  return (
    <List disablePadding aria-label={t('insurerPolicies.tabs.history')}>
      {events.map((event, index) => (
        <ListItem
          key={`${event.type}-${event.occurredAt}-${index}`}
          disableGutters
          sx={{ alignItems: 'flex-start', gap: 1.5 }}
          data-testid="policy-event"
        >
          <Box
            aria-hidden
            sx={{
              mt: 0.75,
              width: 10,
              height: 10,
              borderRadius: '50%',
              bgcolor: 'primary.main',
              flexShrink: 0,
            }}
          />
          <Stack spacing={0.25} sx={{ minWidth: 0 }}>
            <Typography variant="body1" fontWeight={600}>
              {t(`policyEvent.${event.type}`)}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              {formatDateTime(event.occurredAt, i18n.language)}
              {' · '}
              {t('insurerPolicies.timeline.by', { actor: t(`actorType.${event.actorType}`) })}
            </Typography>
          </Stack>
        </ListItem>
      ))}
    </List>
  );
}
