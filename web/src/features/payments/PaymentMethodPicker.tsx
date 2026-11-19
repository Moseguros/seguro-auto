import AccountBalance from '@mui/icons-material/AccountBalance';
import PhoneAndroid from '@mui/icons-material/PhoneAndroid';
import Box from '@mui/material/Box';
import Card from '@mui/material/Card';
import FormControl from '@mui/material/FormControl';
import FormLabel from '@mui/material/FormLabel';
import Radio from '@mui/material/Radio';
import RadioGroup from '@mui/material/RadioGroup';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useId } from 'react';
import { useTranslation } from 'react-i18next';
import type { PaymentMethodOption, PaymentMethodOptionMethod } from '../../api/generated';
import { PayerMsisdnField } from './PayerMsisdnField';

const icons: Record<PaymentMethodOptionMethod, typeof PhoneAndroid> = {
  MPESA: PhoneAndroid,
  BANK_REFERENCE: AccountBalance,
};

/**
 * Uma opção por forma de pagamento, pela ordem recebida (S5-11 §3), com o campo extra da
 * escolhida: o número M-Pesa ou o texto da entidade e referência.
 */
export function PaymentMethodPicker({
  methods,
  value,
  onChange,
  msisdn,
  onMsisdnChange,
  msisdnError,
  onMsisdnBlur,
  disabled,
}: {
  methods: PaymentMethodOption[];
  value: PaymentMethodOptionMethod | null;
  onChange: (method: PaymentMethodOptionMethod) => void;
  msisdn: string;
  onMsisdnChange: (national: string) => void;
  msisdnError: string | null;
  onMsisdnBlur?: () => void;
  disabled?: boolean;
}) {
  const { t } = useTranslation();
  const labelId = useId();
  return (
    <FormControl component="fieldset" fullWidth disabled={disabled}>
      <FormLabel id={labelId} component="legend" sx={{ mb: 1 }}>
        {t('payments.method.label')}
      </FormLabel>
      <RadioGroup
        aria-labelledby={labelId}
        value={value ?? ''}
        onChange={(e) => onChange(e.target.value as PaymentMethodOptionMethod)}
      >
        <Stack spacing={1.5}>
          {methods.map((option) => {
            const Icon = icons[option.method];
            const selected = value === option.method;
            const name = t(`paymentMethod.${option.method}`);
            return (
              <Card
                key={option.method}
                variant="outlined"
                elevation={0}
                data-testid={`method-${option.method}`}
                sx={{
                  borderColor: selected ? 'primary.main' : undefined,
                  borderWidth: selected ? 2 : 1,
                }}
              >
                <Box
                  component="label"
                  sx={{
                    display: 'flex',
                    alignItems: 'center',
                    gap: 1.5,
                    px: 1.5,
                    py: 1,
                    minHeight: 56,
                    cursor: disabled ? 'default' : 'pointer',
                  }}
                >
                  <Radio value={option.method} inputProps={{ 'aria-label': name }} />
                  <Icon color="primary" aria-hidden />
                  <Box sx={{ minWidth: 0 }}>
                    <Typography fontWeight={600}>{name}</Typography>
                    {option.displayName && (
                      <Typography variant="body2" color="text.secondary">
                        {option.displayName}
                      </Typography>
                    )}
                  </Box>
                </Box>
                {selected && (
                  <Box sx={{ px: 2, pb: 2 }}>
                    {option.method === 'MPESA' ? (
                      <PayerMsisdnField
                        label={t('payments.method.mpesaNumber')}
                        value={msisdn}
                        onChange={onMsisdnChange}
                        onBlur={onMsisdnBlur}
                        errorCode={msisdnError}
                      />
                    ) : (
                      <Typography variant="body2" color="text.secondary">
                        {t('payments.method.referenceHelp')}
                      </Typography>
                    )}
                  </Box>
                )}
              </Card>
            );
          })}
        </Stack>
      </RadioGroup>
    </FormControl>
  );
}
