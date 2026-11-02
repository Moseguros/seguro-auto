import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Container from '@mui/material/Container';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import type { ReactNode } from 'react';

/** Cartão centrado das páginas públicas de staff (entrar, recuperar, activar). */
export function AuthCard({
  title,
  subtitle,
  children,
}: {
  title: string;
  subtitle?: ReactNode;
  children: ReactNode;
}) {
  return (
    <Container maxWidth="xs" disableGutters>
      <Card>
        <CardContent sx={{ p: { xs: 2, sm: 3 } }}>
          <Stack spacing={1} sx={{ mb: 3 }}>
            <Typography variant="h5" component="h1">
              {title}
            </Typography>
            {subtitle && <Typography color="text.secondary">{subtitle}</Typography>}
          </Stack>
          {children}
        </CardContent>
      </Card>
    </Container>
  );
}
