import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Card from '@mui/material/Card';
import CardActions from '@mui/material/CardActions';
import CardContent from '@mui/material/CardContent';
import Chip from '@mui/material/Chip';
import Container from '@mui/material/Container';
import Stack from '@mui/material/Stack';
import ToggleButton from '@mui/material/ToggleButton';
import ToggleButtonGroup from '@mui/material/ToggleButtonGroup';
import Typography from '@mui/material/Typography';
import { useInfiniteQuery } from '@tanstack/react-query';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink } from 'react-router-dom';
import {
  listMyProposals,
  type ProposalStatus,
  type ProposalSummaryResponse,
} from '../../api/generated';
import { LoadingScreen } from '../../auth/RequireAuth';
import { getProblem, problemMessage } from '../../errors/problem';
import { formatMoney } from '../../format/money';
import { formatDate } from '../../i18n/format';
import { myProposalsKey } from '../proposal-wizard/useProposalMutations';
import { isEditable, proposalPaths } from '../proposal-wizard/wizardSteps';

/** Filtro "Em curso" (S4-08 §4). */
export const openStatuses: ProposalStatus[] = [
  'DRAFT',
  'INFO_REQUESTED',
  'UNDER_REVIEW',
  'ACCEPTED',
];

const PAGE_SIZE = 20;

type Filter = 'open' | 'all';

export function proposalStatusColor(status: ProposalStatus) {
  if (status === 'ACCEPTED' || status === 'PAID' || status === 'CONVERTED') return 'success';
  if (status === 'INFO_REQUESTED') return 'warning';
  if (status === 'REJECTED') return 'error';
  if (status === 'UNDER_REVIEW') return 'info';
  return 'default';
}

function ProposalCard({ proposal }: { proposal: ProposalSummaryResponse }) {
  const { t, i18n } = useTranslation();
  const editable = isEditable(proposal.status);
  return (
    <Card variant="outlined" elevation={0} data-testid="proposal-card">
      <CardContent>
        <Stack spacing={1}>
          <Stack direction="row" justifyContent="space-between" spacing={1} alignItems="flex-start">
            <Typography
              variant="subtitle1"
              component="h2"
              fontWeight={600}
              sx={{ wordBreak: 'break-word' }}
            >
              {proposal.number}
            </Typography>
            <Chip
              size="small"
              color={proposalStatusColor(proposal.status)}
              label={t(`proposalStatus.${proposal.status}`)}
            />
          </Stack>
          <Typography color="text.secondary">
            {proposal.insurer?.displayName} · {proposal.productName}
          </Typography>
          <Typography>
            {proposal.vehiclePlate} · <strong>{formatMoney(proposal.total, i18n.language)}</strong>
          </Typography>
          {proposal.expiresAt && (
            <Typography variant="body2" color="text.secondary">
              {t('proposal.list.expiresAt', {
                date: formatDate(proposal.expiresAt, i18n.language),
              })}
            </Typography>
          )}
        </Stack>
      </CardContent>
      <CardActions sx={{ px: 2, pb: 2 }}>
        <Button
          variant={editable ? 'contained' : 'outlined'}
          component={RouterLink}
          to={`${proposalPaths.customer.list}/${proposal.id}`}
        >
          {editable ? t('proposal.list.continue') : t('proposal.list.view')}
        </Button>
      </CardActions>
    </Card>
  );
}

/** `/cliente/propostas`: as propostas do cliente (incluindo as do mediador). */
export function MyProposalsPage() {
  const { t } = useTranslation();
  const [filter, setFilter] = useState<Filter>('open');
  const base = {
    size: PAGE_SIZE,
    sort: 'createdAt,desc',
    ...(filter === 'open' ? { status: openStatuses } : {}),
  };
  const query = useInfiniteQuery({
    queryKey: [...myProposalsKey, filter],
    queryFn: ({ pageParam, signal }) =>
      listMyProposals(
        { ...base, page: pageParam },
        { paramsSerializer: { indexes: null } },
        signal,
      ),
    initialPageParam: 0,
    getNextPageParam: (last) =>
      (last.page + 1) * last.size < last.totalElements ? last.page + 1 : undefined,
  });

  let content;
  if (query.isPending) content = <LoadingScreen />;
  else if (query.isError) {
    content = (
      <Alert
        severity="error"
        action={
          <Button color="inherit" size="small" onClick={() => void query.refetch()}>
            {t('common.retry')}
          </Button>
        }
      >
        {problemMessage(t, getProblem(query.error))}
      </Alert>
    );
  } else {
    const items = query.data.pages.flatMap((page) => page.content);
    content =
      items.length === 0 ? (
        <Stack spacing={2} alignItems="flex-start">
          <Typography>
            {filter === 'open' ? t('proposal.list.emptyOpen') : t('proposal.list.empty')}
          </Typography>
          <Button
            variant="cta"
            size="large"
            component={RouterLink}
            to={proposalPaths.customer.quotes}
          >
            {t('proposal.status.simulate')}
          </Button>
        </Stack>
      ) : (
        <Stack spacing={1.5}>
          {items.map((proposal) => (
            <ProposalCard key={proposal.id} proposal={proposal} />
          ))}
          {query.hasNextPage && (
            <Button
              variant="outlined"
              onClick={() => void query.fetchNextPage()}
              disabled={query.isFetchingNextPage}
              sx={{ alignSelf: 'center' }}
            >
              {t('proposal.list.loadMore')}
            </Button>
          )}
        </Stack>
      );
  }

  return (
    <Container maxWidth="md" disableGutters>
      <Stack spacing={3}>
        <Typography variant="h4" component="h1">
          {t('proposal.list.title')}
        </Typography>
        <ToggleButtonGroup
          exclusive
          size="small"
          value={filter}
          onChange={(_, value: Filter | null) => value && setFilter(value)}
          aria-label={t('proposal.list.filter')}
        >
          <ToggleButton value="open">{t('proposal.list.open')}</ToggleButton>
          <ToggleButton value="all">{t('proposal.list.all')}</ToggleButton>
        </ToggleButtonGroup>
        {content}
      </Stack>
    </Container>
  );
}
