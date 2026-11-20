import Description from '@mui/icons-material/Description';
import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import CircularProgress from '@mui/material/CircularProgress';
import Stack from '@mui/material/Stack';
import Tab from '@mui/material/Tab';
import Tabs from '@mui/material/Tabs';
import Typography from '@mui/material/Typography';
import { useInfiniteQuery } from '@tanstack/react-query';
import type { ReactNode } from 'react';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink, useSearchParams } from 'react-router-dom';
import {
  getListMyPoliciesQueryKey,
  listMyPolicies,
  type ListMyPoliciesParams,
  type PageResponseMyPolicySummaryResponse,
} from '../../api/generated';
import { getProblem, problemMessage } from '../../errors/problem';
import { MyPolicyCard, MyPolicyCardSkeletons } from './MyPolicyCard';
import { customerPolicyPaths } from './paths';

export const POLICIES_PAGE_SIZE = 20;

type Filter = 'active' | 'all';

/** A página seguinte, ou `undefined` na última. */
export function nextPoliciesPage(last: PageResponseMyPolicySummaryResponse): number | undefined {
  return (last.page + 1) * last.size < last.totalElements ? last.page + 1 : undefined;
}

export function policiesParams(filter: Filter): ListMyPoliciesParams {
  return {
    ...(filter === 'active' ? { status: ['ACTIVE'] } : {}),
    size: POLICIES_PAGE_SIZE,
    sort: 'issuedAt,desc',
  };
}

/** Uma coluna abaixo de 900 px, duas a partir daí. */
function PolicyGrid({ children }: { children: ReactNode }) {
  return (
    <Box
      sx={{
        display: 'grid',
        gridTemplateColumns: { xs: 'minmax(0, 1fr)', md: 'repeat(2, minmax(0, 1fr))' },
        gap: 2,
      }}
    >
      {children}
    </Box>
  );
}

function EmptyPolicies({ filter }: { filter: Filter }) {
  const { t } = useTranslation();
  return (
    <Stack spacing={2} alignItems="center" sx={{ py: 6, textAlign: 'center' }}>
      <Description sx={{ fontSize: 64 }} color="disabled" aria-hidden />
      <Typography color="text.secondary">
        {t(filter === 'active' ? 'customerPolicies.emptyActive' : 'customerPolicies.empty')}
      </Typography>
      <Button variant="cta" component={RouterLink} to={customerPolicyPaths.quote}>
        {t('customerPolicies.simulate')}
      </Button>
    </Stack>
  );
}

/** `/cliente/apolices` (S6-06 §2): as apólices do cliente, as emitidas há menos tempo primeiro. */
export function MyPolicyListPage() {
  const { t } = useTranslation();
  const [search, setSearch] = useSearchParams();
  const filter: Filter = search.get('estado') === 'todas' ? 'all' : 'active';
  const params = policiesParams(filter);
  const query = useInfiniteQuery({
    queryKey: [...getListMyPoliciesQueryKey(params), 'infinite'],
    queryFn: ({ pageParam, signal }) =>
      listMyPolicies(
        { ...params, page: pageParam },
        { paramsSerializer: { indexes: null } },
        signal,
      ),
    initialPageParam: 0,
    getNextPageParam: nextPoliciesPage,
  });

  let content: ReactNode;
  if (query.isPending) {
    content = (
      <PolicyGrid>
        <MyPolicyCardSkeletons />
      </PolicyGrid>
    );
  } else if (query.isError) {
    content = (
      <Alert
        severity="error"
        role="alert"
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
    const items = query.data.pages.flatMap((p) => p.content);
    content =
      items.length === 0 ? (
        <EmptyPolicies filter={filter} />
      ) : (
        <Stack spacing={2}>
          <PolicyGrid>
            {items.map((policy) => (
              <MyPolicyCard key={policy.id} policy={policy} />
            ))}
          </PolicyGrid>
          {query.hasNextPage && (
            <Button
              variant="outlined"
              onClick={() => void query.fetchNextPage()}
              disabled={query.isFetchingNextPage}
              startIcon={query.isFetchingNextPage ? <CircularProgress size={16} /> : undefined}
              sx={{ alignSelf: 'center' }}
            >
              {t('customerPolicies.loadMore')}
            </Button>
          )}
        </Stack>
      );
  }

  return (
    <Stack spacing={2}>
      <Typography variant="h4" component="h1">
        {t('customerPolicies.title')}
      </Typography>
      <Tabs
        value={filter}
        onChange={(_, value: Filter) =>
          setSearch(value === 'all' ? { estado: 'todas' } : {}, { replace: true })
        }
        variant="fullWidth"
        sx={{ maxWidth: 400 }}
      >
        <Tab value="active" label={t('customerPolicies.tabs.active')} />
        <Tab value="all" label={t('customerPolicies.tabs.all')} />
      </Tabs>
      {content}
    </Stack>
  );
}
