import { readFileSync } from 'node:fs';
import { resolve } from 'node:path';
import { describe, expect, it } from 'vitest';
import { proposalFixture } from '../../test/proposals';
import { declarationQuestions, suitabilityQuestions } from './proposalQuestions';
import {
  firstPendingStep,
  isStepDone,
  missingFromChecklist,
  nextStep,
  previousStep,
  visibleSteps,
} from './wizardSteps';

const notRequired = { required: false };

describe('wizardSteps', () => {
  it('esconde a vistoria sem inspection.required', () => {
    expect(visibleSteps(proposalFixture())).toEqual([
      'tomador',
      'declaracoes',
      'adequacao',
      'vistoria',
      'identidade',
      'resumo',
    ]);
    expect(visibleSteps(proposalFixture({ inspection: notRequired }))).toEqual([
      'tomador',
      'declaracoes',
      'adequacao',
      'identidade',
      'resumo',
    ]);
    expect(nextStep('adequacao', proposalFixture({ inspection: notRequired }))).toBe('identidade');
    expect(previousStep('identidade', proposalFixture({ inspection: notRequired }))).toBe(
      'adequacao',
    );
    expect(previousStep('tomador', proposalFixture())).toBeNull();
  });

  it('calcula o primeiro passo por concluir a partir do checklist', () => {
    expect(firstPendingStep(proposalFixture())).toBe('declaracoes');

    const declared = proposalFixture({
      checklist: {
        declarations: 'DONE',
        suitability: 'ACK_REQUIRED',
        inspection: 'MISSING',
        signature: 'MISSING',
      },
    });
    expect(firstPendingStep(declared)).toBe('adequacao');

    const suitable = proposalFixture({
      checklist: {
        declarations: 'DONE',
        suitability: 'DONE',
        inspection: 'REJECTED',
        signature: 'MISSING',
      },
    });
    expect(firstPendingStep(suitable)).toBe('vistoria');

    const inspected = {
      ...suitable,
      checklist: { ...suitable.checklist, inspection: 'DONE' as const },
    };
    expect(firstPendingStep(inspected)).toBe('identidade');
    expect(firstPendingStep(inspected, true)).toBe('resumo');
    expect(
      firstPendingStep({
        ...inspected,
        kyc: { state: 'PENDING', requiredForAutomaticAcceptance: true },
      }),
    ).toBe('resumo');

    // Sem vistoria, o passo nem conta.
    const rcOnly = proposalFixture({
      inspection: notRequired,
      checklist: {
        declarations: 'DONE',
        suitability: 'DONE',
        inspection: 'NOT_REQUIRED',
        signature: 'MISSING',
      },
      kyc: { state: 'VERIFIED', requiredForAutomaticAcceptance: false },
    });
    expect(firstPendingStep(rcOnly)).toBe('resumo');
    expect(isStepDone('vistoria', rcOnly)).toBe(true);
    expect(isStepDone('resumo', rcOnly)).toBe(false);
  });

  it('lista o que falta pelo checklist com os códigos de PROPOSAL_INCOMPLETE', () => {
    expect(missingFromChecklist(proposalFixture())).toEqual([
      'DECLARATIONS',
      'SUITABILITY',
      'INSPECTION',
    ]);
    expect(
      missingFromChecklist(
        proposalFixture({
          checklist: {
            declarations: 'DONE',
            suitability: 'ACK_REQUIRED',
            inspection: 'DONE',
            signature: 'MISSING',
          },
        }),
      ),
    ).toEqual(['SUITABILITY_ACKNOWLEDGEMENT']);
  });

  it('as perguntas são as de api/proposal-questions.json', () => {
    const questions = JSON.parse(
      readFileSync(resolve(__dirname, '../../../../api/proposal-questions.json'), 'utf-8'),
    ) as {
      declarations: { code: string; followUp: string[] }[];
      suitability: { code: string; options: string[] }[];
    };
    expect(declarationQuestions).toEqual(questions.declarations);
    expect(suitabilityQuestions).toEqual(
      questions.suitability.map(({ code, options }) => ({ code, options })),
    );
  });
});
