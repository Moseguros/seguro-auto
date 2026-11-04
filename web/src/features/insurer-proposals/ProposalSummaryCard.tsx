import Box from '@mui/material/Box';
import Divider from '@mui/material/Divider';
import Stack from '@mui/material/Stack';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Typography from '@mui/material/Typography';
import type { ReactNode } from 'react';
import { useTranslation } from 'react-i18next';
import type { InsurerProposalResponse } from '../../api/generated';
import { formatMoney } from '../../format/money';
import { formatDate } from '../../i18n/format';
import { chargeLabel, deductibleValue } from '../quotes/quoteFormat';
import { Field } from './common';

function Group({ title, children }: { title: string; children: ReactNode }) {
  return (
    <Stack spacing={1}>
      <Typography variant="subtitle1" component="h3" fontWeight={600}>
        {title}
      </Typography>
      <Box
        component="dl"
        sx={{
          m: 0,
          display: 'grid',
          gap: 1.5,
          gridTemplateColumns: {
            xs: '1fr',
            sm: 'repeat(2, minmax(0, 1fr))',
            md: 'repeat(3, minmax(0, 1fr))',
          },
        }}
      >
        {children}
      </Box>
    </Stack>
  );
}

const money = { fontVariantNumeric: 'tabular-nums', whiteSpace: 'nowrap' } as const;

/** Dados congelados da proposta: produto, tomador, veículo, condutor, coberturas e prémio. */
export function ProposalSummaryCard({ proposal }: { proposal: InsurerProposalResponse }) {
  const { t, i18n } = useTranslation();
  const lang = i18n.language;
  const { policyholder: holder, vehicle, driver, premium } = proposal;
  const date = (value?: string | null) => formatDate(value ?? undefined, lang);

  return (
    <Stack spacing={3} divider={<Divider flexItem />}>
      <Group title={t('insurerProposals.summary.product')}>
        <Field label={t('insurerProposals.summary.insurer')}>{proposal.insurer?.displayName}</Field>
        <Field label={t('insurerProposals.summary.product')}>{proposal.product.name}</Field>
        <Field label={t('insurerProposals.summary.productType')}>
          {t(`productType.${proposal.product.type}`)}
        </Field>
        <Field label={t('insurerProposals.summary.versionId')}>
          <Box component="span" sx={{ fontFamily: 'monospace', fontSize: '0.8em' }}>
            {proposal.product.versionId}
          </Box>
        </Field>
        <Field label={t('insurerProposals.summary.channel')}>
          {t(`proposalChannel.${proposal.channel}`)}
        </Field>
        {proposal.broker && (
          <Field label={t('insurerProposals.summary.broker')}>{proposal.broker.displayName}</Field>
        )}
        <Field label={t('insurerProposals.summary.period')}>
          {t('insurerProposals.summary.periodValue', {
            start: date(proposal.startAt),
            end: proposal.endDate
              ? formatDate(`${proposal.endDate}T12:00:00Z`, lang)
              : date(proposal.endAt),
            days: proposal.durationDays,
          })}
        </Field>
      </Group>

      <Group title={t('insurerProposals.summary.policyholder')}>
        <Field label={t('insurerProposals.summary.fullName')}>{holder.fullName}</Field>
        <Field label={t('insurerProposals.summary.birthDate')}>
          {formatDate(`${holder.birthDate}T12:00:00Z`, lang)}
        </Field>
        <Field label={t('insurerProposals.summary.document')}>
          {`${t(`idDocType.${holder.idDocType}`, { defaultValue: holder.idDocType })} ${holder.idDocNumberMasked}`}
        </Field>
        <Field label={t('insurerProposals.summary.nuit')}>{holder.nuitMasked}</Field>
        <Field label={t('insurerProposals.summary.province')}>
          {t(`province.${holder.province}`, { defaultValue: holder.province })}
        </Field>
        {holder.address && (
          <Field label={t('insurerProposals.summary.address')}>{holder.address}</Field>
        )}
        {holder.msisdnMasked && (
          <Field label={t('insurerProposals.summary.msisdn')}>{holder.msisdnMasked}</Field>
        )}
        {holder.emailMasked && (
          <Field label={t('insurerProposals.summary.email')}>{holder.emailMasked}</Field>
        )}
      </Group>

      <Group title={t('insurerProposals.summary.vehicle')}>
        <Field label={t('insurerProposals.summary.plate')}>{vehicle.plate}</Field>
        <Field label={t('insurerProposals.summary.makeModel')}>
          {`${vehicle.make} ${vehicle.model}`}
        </Field>
        <Field label={t('insurerProposals.summary.year')}>{vehicle.year}</Field>
        <Field label={t('insurerProposals.summary.category')}>
          {t(`vehicleCategory.${vehicle.category}`)}
        </Field>
        <Field label={t('insurerProposals.summary.usage')}>
          {t(`vehicleUsage.${vehicle.usage}`)}
        </Field>
        <Field label={t('insurerProposals.summary.chassis')}>{vehicle.chassisNumber ?? ''}</Field>
        <Field label={t('insurerProposals.summary.seats')}>{vehicle.seats}</Field>
        <Field label={t('insurerProposals.summary.commercialValue')}>
          {vehicle.commercialValue ? formatMoney(vehicle.commercialValue, lang) : ''}
        </Field>
      </Group>

      <Group title={t('insurerProposals.summary.driver')}>
        <Field label={t('insurerProposals.summary.fullName')}>{driver.fullName}</Field>
        <Field label={t('insurerProposals.summary.birthDate')}>
          {driver.birthDate ? formatDate(`${driver.birthDate}T12:00:00Z`, lang) : ''}
        </Field>
        <Field label={t('insurerProposals.summary.licence')}>{driver.licenceNumberMasked}</Field>
        <Field label={t('insurerProposals.summary.licenceIssueDate')}>
          {formatDate(`${driver.licenceIssueDate}T12:00:00Z`, lang)}
        </Field>
        <Field label={t('insurerProposals.summary.licenceCategories')}>
          {driver.licenceCategories.join(', ')}
        </Field>
      </Group>

      <Stack spacing={1}>
        <Typography variant="subtitle1" component="h3" fontWeight={600}>
          {t('insurerProposals.summary.coverages')}
        </Typography>
        <TableContainer sx={{ maxWidth: '100%', overflowX: 'auto' }}>
          <Table size="small" aria-label={t('insurerProposals.summary.coverages')}>
            <TableHead>
              <TableRow>
                <TableCell>{t('insurerProposals.summary.coverage')}</TableCell>
                <TableCell align="right">{t('insurerProposals.summary.capital')}</TableCell>
                <TableCell>{t('insurerProposals.summary.deductible')}</TableCell>
                <TableCell align="right">{t('insurerProposals.summary.premium')}</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {proposal.coverages.map((coverage) => (
                <TableRow key={coverage.code}>
                  <TableCell>{t(`coverage.${coverage.code}`)}</TableCell>
                  <TableCell align="right" sx={money}>
                    {coverage.capital ? formatMoney(coverage.capital, lang) : '—'}
                  </TableCell>
                  <TableCell>
                    {deductibleValue(t, coverage.deductible) ??
                      t('insurerProposals.summary.noDeductible')}
                  </TableCell>
                  <TableCell align="right" sx={money}>
                    {formatMoney(coverage.premium, lang)}
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      </Stack>

      <Stack spacing={1}>
        <Typography variant="subtitle1" component="h3" fontWeight={600}>
          {t('insurerProposals.summary.premiumTitle')}
        </Typography>
        <Table size="small" aria-label={t('insurerProposals.summary.premiumTitle')}>
          <TableBody>
            <TableRow>
              <TableCell>{t('insurerProposals.summary.commercial')}</TableCell>
              <TableCell align="right" sx={money}>
                {formatMoney(premium.commercial, lang)}
              </TableCell>
            </TableRow>
            {premium.charges.map((charge) => (
              <TableRow key={charge.code}>
                <TableCell>{chargeLabel(charge)}</TableCell>
                <TableCell align="right" sx={money}>
                  {formatMoney(charge.amount, lang)}
                </TableCell>
              </TableRow>
            ))}
            {premium.roundingAdjustment.amount !== 0 && (
              <TableRow>
                <TableCell>{t('insurerProposals.summary.rounding')}</TableCell>
                <TableCell align="right" sx={money}>
                  {formatMoney(premium.roundingAdjustment, lang)}
                </TableCell>
              </TableRow>
            )}
            <TableRow>
              <TableCell sx={{ fontWeight: 700 }}>{t('insurerProposals.summary.total')}</TableCell>
              <TableCell align="right" sx={{ ...money, fontWeight: 700 }}>
                {formatMoney(premium.total, lang)}
              </TableCell>
            </TableRow>
          </TableBody>
        </Table>
      </Stack>
    </Stack>
  );
}
