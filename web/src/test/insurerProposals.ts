import type {
  InspectionResponse,
  InsurerKycCheckResponse,
  InsurerProposalResponse,
  InsurerProposalSummaryResponse,
} from '../api/generated';

/** Dados de teste das propostas da seguradora (S4-07). */

const mzn = (amount: number) => ({ amount, currency: 'MZN' as const });

export function inspectionFixture(overrides: Partial<InspectionResponse> = {}): InspectionResponse {
  return {
    id: 'insp1',
    required: true,
    mode: 'SELF_PHOTOS',
    status: 'SUBMITTED',
    odometerKm: 45210,
    submittedAt: '2027-01-08T09:00:00Z',
    version: 3,
    maxExtraPhotos: 6,
    missingSlots: [],
    photos: [
      {
        id: 'ph1',
        slot: 'FRONT',
        fileId: 'f-front',
        source: 'CAMERA',
        uploadedAt: '2027-01-08T08:00:00Z',
        takenAt: '2027-01-08T07:55:00Z',
        latitude: -25.9655,
        longitude: 32.5832,
      },
      {
        id: 'ph2',
        slot: 'REAR',
        fileId: 'f-rear',
        source: 'CAMERA',
        uploadedAt: '2027-01-08T08:01:00Z',
      },
      {
        id: 'ph3',
        slot: 'LEFT',
        fileId: 'f-left',
        source: 'CAMERA',
        uploadedAt: '2027-01-08T08:02:00Z',
      },
      {
        id: 'ph4',
        slot: 'RIGHT',
        fileId: 'f-right',
        source: 'CAMERA',
        uploadedAt: '2027-01-08T08:03:00Z',
      },
      {
        id: 'ph5',
        slot: 'ODOMETER',
        fileId: 'f-odo',
        source: 'CAMERA',
        uploadedAt: '2027-01-08T08:04:00Z',
      },
      {
        id: 'ph6',
        slot: 'CHASSIS',
        fileId: 'f-chassis',
        source: 'CAMERA',
        uploadedAt: '2027-01-08T08:05:00Z',
      },
    ],
    ...overrides,
  };
}

export function insurerProposalFixture(
  overrides: Partial<InsurerProposalResponse> = {},
): InsurerProposalResponse {
  return {
    id: 'p1',
    number: 'PR-2027-000123',
    status: 'UNDER_REVIEW',
    channel: 'WEB',
    broker: null,
    insurer: { id: 'i1', displayName: 'Seguradora Demo' },
    quoteId: 'q1',
    createdAt: '2027-01-05T09:30:00Z',
    updatedAt: '2027-01-08T09:00:00Z',
    submittedAt: '2027-01-08T10:00:00Z',
    startAt: '2027-01-14T22:00:00Z',
    endAt: '2028-01-14T22:00:00Z',
    endDate: '2028-01-14',
    durationDays: 365,
    version: 7,
    checklist: {
      declarations: 'DONE',
      suitability: 'DONE',
      inspection: 'DONE',
      signature: 'DONE',
    },
    product: {
      id: 'prod1',
      name: 'Auto Completo',
      type: 'COMPREHENSIVE',
      versionId: 'pv1',
      documents: [],
    },
    policyholder: {
      fullName: 'Ana Maria Cossa',
      birthDate: '1990-04-12',
      idDocType: 'BI',
      idDocNumberMasked: '********123A',
      nuitMasked: '*****6789',
      province: 'MAPUTO_CITY',
      msisdnMasked: '+25884****001',
    },
    vehicle: {
      id: 'v1',
      plate: 'AAA-100-MC',
      make: 'Toyota',
      model: 'Corolla',
      year: 2019,
      category: 'LIGHT_PASSENGER',
      usage: 'PRIVATE',
      chassisNumber: 'JTDBR32E720123456',
      seats: 5,
      commercialValue: mzn(95000000),
    },
    driver: {
      id: 'd1',
      fullName: 'Ana Maria Cossa',
      birthDate: '1990-04-12',
      licenceNumberMasked: '****1234',
      licenceIssueDate: '2012-06-01',
      licenceCategories: ['B'],
    },
    coverages: [
      {
        code: 'MTPL',
        mandatory: true,
        capital: mzn(300000000),
        deductible: { type: 'NONE' },
        premium: mzn(450000),
      },
      {
        code: 'OD_COLLISION',
        mandatory: false,
        capital: mzn(95000000),
        deductible: { type: 'PERCENT', percent: '10', minAmount: mzn(2500000) },
        premium: mzn(1050000),
      },
    ],
    premium: {
      commercial: mzn(1500000),
      charges: [
        { code: 'STAMP', labelPt: 'Imposto de selo', labelEn: 'Stamp duty', amount: mzn(30000) },
      ],
      roundingAdjustment: mzn(0),
      total: mzn(1530000),
    },
    declarations: {
      confirmedAt: '2027-01-06T10:00:00Z',
      answers: [
        {
          questionCode: 'CLAIMS_LAST_3_YEARS',
          answer: true,
          claimsCount: 1,
          details: 'Pequeno toque num parque em 2025, sem feridos.',
        },
        { questionCode: 'PREVIOUS_REFUSAL', answer: false },
        { questionCode: 'USAGE_DIFFERENT', answer: false },
        { questionCode: 'VEHICLE_MODIFIED', answer: false },
        { questionCode: 'NOVICE_DRIVERS', answer: false },
      ],
    },
    suitability: {
      answeredAt: '2027-01-06T10:05:00Z',
      result: 'SUITABLE',
      reasons: [],
      acknowledgedAt: null,
      answers: [
        { questionCode: 'SUIT_NEED', answer: 'FULL' },
        { questionCode: 'SUIT_FINANCED', answer: 'NO' },
        { questionCode: 'SUIT_CAN_AFFORD_REPAIR', answer: 'NO' },
        { questionCode: 'SUIT_CROSS_BORDER', answer: 'NO' },
        { questionCode: 'SUIT_DEDUCTIBLE', answer: 'YES' },
      ],
    },
    inspection: inspectionFixture(),
    kyc: {
      state: 'PENDING',
      checkId: 'k1',
      verifiedAt: null,
      verifiedByThisInsurer: false,
      requiredForAutomaticAcceptance: true,
    },
    signature: {
      assisted: false,
      channel: 'SMS',
      destinationMasked: '+25884****001',
      documentFileId: 'f-pdf',
      documentHash: '9f2c0000000000000000000000000000000000000000000000000000000000e1',
      signedAt: '2027-01-08T09:50:00Z',
    },
    signatures: [
      {
        assisted: false,
        channel: 'SMS',
        destinationMasked: '+25884****001',
        documentFileId: 'f-pdf',
        documentHash: '9f2c0000000000000000000000000000000000000000000000000000000000e1',
        signedAt: '2027-01-08T09:50:00Z',
        invalidatedAt: null,
        invalidatedReason: null,
      },
    ],
    sla: {
      status: 'ON_TIME',
      reviewStartedAt: '2027-01-08T10:00:00Z',
      alertAt: '2027-01-18T10:00:00Z',
      dueAt: '2027-01-23T10:00:00Z',
    },
    underwriting: {
      evaluatedAt: '2027-01-08T10:00:00Z',
      outcome: 'MANUAL_REVIEW',
      rulesSchemaVersion: 1,
      rulesSource: 'DEFAULT',
      failures: ['KYC_REQUIRED', 'DECLARATION_POSITIVE', 'INSPECTION_NOT_APPROVED'],
      inputs: {
        hasOwnDamage: true,
        kycState: 'PENDING',
        inspectionStatus: 'SUBMITTED',
        maxVehicleValue: 150000000,
        positiveDeclarations: ['CLAIMS_LAST_3_YEARS'],
        simplifiedKycMaxPremium: 1000000,
        totalAmount: 1530000,
        vehicleValue: 95000000,
      },
    },
    decision: null,
    ...overrides,
  };
}

export function proposalSummaryFixture(
  overrides: Partial<InsurerProposalSummaryResponse> = {},
): InsurerProposalSummaryResponse {
  return {
    id: 'p1',
    number: 'PR-2027-000123',
    customerName: 'Ana Maria Cossa',
    productName: 'Auto Completo',
    productType: 'COMPREHENSIVE',
    vehiclePlate: 'AAA-100-MC',
    total: mzn(1530000),
    channel: 'WEB',
    brokerName: null,
    status: 'UNDER_REVIEW',
    submittedAt: '2027-01-08T10:00:00Z',
    slaDueAt: '2027-01-23T10:00:00Z',
    slaStatus: 'ALERT',
    kycState: 'PENDING',
    inspectionStatus: 'SUBMITTED',
    ...overrides,
  };
}

export function kycCheckFixture(
  overrides: Partial<InsurerKycCheckResponse> = {},
): InsurerKycCheckResponse {
  return {
    id: 'k1',
    status: 'PENDING',
    submittedAt: '2027-01-07T08:00:00Z',
    version: 0,
    customer: {
      userId: 'c1',
      fullName: 'Ana Maria Cossa',
      birthDate: '1990-04-12',
      idDocType: 'BI',
      idDocNumber: '110100123456A',
      msisdnMasked: '+25884****001',
    },
    files: { docFrontFileId: 'f-doc-front', docBackFileId: 'f-doc-back', selfieFileId: 'f-selfie' },
    previousChecks: [],
    ...overrides,
  };
}
