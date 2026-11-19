import { screen } from '@testing-library/react';
import { describe, expect, it } from 'vitest';
import { renderWithProviders } from '../../test/render';
import { sortSources, SourcesTable } from './SourcesTable';
import { queueProblem, WorkerStatusCard } from './WorkerStatusCard';

const queues = [
  { name: 'core.reference-payments', messages: 0, consumers: 1 },
  { name: 'core.reference-payments.dlq', messages: 3, consumers: 0 },
  { name: 'core.payment-file-events', messages: 2, consumers: 0 },
  { name: 'core.payment-file-events.dlq', messages: 0, consumers: 0 },
];

describe('WorkerStatusCard', () => {
  it('destaca a .dlq com mensagens e a fila principal sem consumidores', () => {
    renderWithProviders(
      <WorkerStatusCard
        narrow={false}
        status={{ brokerReachable: true, lastMessageAt: '2027-01-11T08:00:00Z', queues }}
      />,
    );
    expect(screen.getByText('Broker ligado')).toBeInTheDocument();
    const rows = screen.getAllByTestId('queue');
    expect(rows.map((r) => r.getAttribute('data-problem'))).toEqual([
      null,
      'dlq',
      'noConsumers',
      null,
    ]);
    expect(rows[1]).toHaveTextContent(
      "Há mensagens que falharam o processamento. Siga o procedimento 'Reprocessar mensagens' do infra/README.md.",
    );
    expect(rows[2]).toHaveTextContent('O backend não está a consumir esta fila.');
    expect(screen.getByText('Última mensagem recebida: 11/01/2027 10:00')).toBeInTheDocument();
  });

  it('broker inacessível', () => {
    renderWithProviders(
      <WorkerStatusCard narrow status={{ brokerReachable: false, queues: [] }} />,
    );
    expect(screen.getByText('Broker inacessível')).toBeInTheDocument();
    expect(screen.getByText('Sem informação das filas.')).toBeInTheDocument();
  });

  it('queueProblem', () => {
    expect(queueProblem({ name: 'x.dlq', messages: 0, consumers: 0 })).toBeNull();
    expect(queueProblem({ name: 'x.dlq', messages: 1, consumers: 0 })).toBe('dlq');
    expect(queueProblem({ name: 'x', messages: 0, consumers: 0 })).toBe('noConsumers');
    expect(queueProblem({ name: 'x', messages: 0, consumers: -1 })).toBeNull();
  });
});

describe('SourcesTable', () => {
  const source = (name: string, staleSince?: string) => ({
    sourceId: name,
    insurer: { id: name, displayName: name },
    entity: '11223',
    fileFormat: 'MEPS' as const,
    enabled: true,
    lastPollAt: '2027-01-11T08:00:00Z',
    lastPollOutcome: staleSince ? ('ERROR' as const) : ('OK' as const),
    lastPollError: staleSince ? 'SFTP_AUTH_FAILED' : undefined,
    filesLast24h: 2,
    rejectedLast24h: 0,
    staleSince,
  });

  it('põe as fontes paradas primeiro e destaca-as', () => {
    const sources = [
      source('Seguradora A'),
      source('Seguradora C', '2027-01-11T06:00:00Z'),
      source('Seguradora B'),
    ];
    expect(sortSources(sources).map((s) => s.insurer.displayName)).toEqual([
      'Seguradora C',
      'Seguradora A',
      'Seguradora B',
    ]);
    renderWithProviders(<SourcesTable sources={sources} narrow={false} />);
    const rows = screen.getAllByTestId('source');
    expect(rows[0]).toHaveAttribute('data-stale', 'true');
    expect(rows[0]).toHaveTextContent('Autenticação SFTP recusada');
    expect(rows[0]).toHaveTextContent('Parada · 11/01/2027 08:00');
  });
});
