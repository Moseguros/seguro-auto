import { z } from 'zod';
import type {
  CreateOrganizationRequest,
  CreateOrganizationRequestType,
  OrganizationResponse,
  UpdateOrganizationRequest,
} from '../../api/generated';

/** As mesmas regras do backend (S1-04). Mensagens = códigos `validation.*`. */

export const organizationTypes = [
  'INSURER',
  'BROKER',
  'AGENT',
  'WORKSHOP',
  'ASSESSOR',
  'ASSISTANCE',
  'OMBUDSMAN',
  'REGULATOR',
  'POLICE',
  'CORPORATE_CLIENT',
] as const satisfies readonly CreateOrganizationRequestType[];

export type OrganizationType = (typeof organizationTypes)[number];

/** Tipos que exigem o número de autorização da ASFPM. */
export const licensedTypes: readonly OrganizationType[] = ['INSURER', 'BROKER', 'AGENT'];

export function requiresLicense(type: string): boolean {
  return (licensedTypes as readonly string[]).includes(type);
}

export const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const nuitPattern = /^\d{9}$/;
const phonePattern = /^\+?[0-9]{9,15}$/;
const shortCodePattern = /^[A-Z0-9]{2,10}$/;

export interface OrganizationFormValues {
  type: string;
  legalName: string;
  tradeName: string;
  nuit: string;
  licenseNumber: string;
  contactEmail: string;
  contactPhone: string;
  address: string;
  province: string;
  shortCode: string;
  complaintsEmail: string;
  ombudsmanOrganizationId: string;
  managerName: string;
  managerEmail: string;
}

export const organizationFields = [
  'type',
  'legalName',
  'tradeName',
  'nuit',
  'licenseNumber',
  'contactEmail',
  'contactPhone',
  'address',
  'province',
  'shortCode',
  'complaintsEmail',
  'ombudsmanOrganizationId',
  'managerName',
  'managerEmail',
] as const satisfies readonly (keyof OrganizationFormValues)[];

export type OrganizationField = (typeof organizationFields)[number];

export const emptyOrganization: OrganizationFormValues = {
  type: '',
  legalName: '',
  tradeName: '',
  nuit: '',
  licenseNumber: '',
  contactEmail: '',
  contactPhone: '',
  address: '',
  province: '',
  shortCode: '',
  complaintsEmail: '',
  ombudsmanOrganizationId: '',
  managerName: '',
  managerEmail: '',
};

export function valuesFrom(organization: OrganizationResponse): OrganizationFormValues {
  return {
    ...emptyOrganization,
    type: organization.type,
    legalName: organization.legalName,
    tradeName: organization.tradeName ?? '',
    nuit: organization.nuit,
    licenseNumber: organization.licenseNumber ?? '',
    contactEmail: organization.contactEmail,
    contactPhone: organization.contactPhone ?? '',
    address: organization.address ?? '',
    province: organization.province ?? '',
    shortCode: organization.insurerProfile?.shortCode ?? '',
    complaintsEmail: organization.insurerProfile?.complaintsEmail ?? '',
    ombudsmanOrganizationId: organization.insurerProfile?.ombudsmanOrganizationId ?? '',
  };
}

const compact = (value: string) => value.replace(/\s+/g, '');

/** `withManager`: o formulário de criação também pede o primeiro gestor. */
export function organizationSchema({ withManager }: { withManager: boolean }) {
  return z
    .object(
      Object.fromEntries(organizationFields.map((f) => [f, z.string()])) as Record<
        OrganizationField,
        z.ZodString
      >,
    )
    .superRefine((v, ctx) => {
      const issue = (path: OrganizationField, message: string) =>
        ctx.addIssue({ code: z.ZodIssueCode.custom, path: [path], message });
      const email = (path: OrganizationField, value: string) => {
        if (!value.trim()) issue(path, 'REQUIRED');
        else if (!emailPattern.test(value.trim())) issue(path, 'INVALID_EMAIL');
      };

      if (!(organizationTypes as readonly string[]).includes(v.type)) issue('type', 'REQUIRED');
      const legalName = v.legalName.trim();
      if (!legalName) issue('legalName', 'REQUIRED');
      else if (legalName.length < 2 || legalName.length > 200) issue('legalName', 'SIZE');
      if (v.tradeName.trim().length > 120) issue('tradeName', 'SIZE');
      const nuit = compact(v.nuit);
      if (!nuit) issue('nuit', 'REQUIRED');
      else if (!nuitPattern.test(nuit)) issue('nuit', 'INVALID_NUIT');
      const license = v.licenseNumber.trim();
      if (!license && requiresLicense(v.type)) issue('licenseNumber', 'LICENSE_NUMBER_REQUIRED');
      else if (license.length > 40) issue('licenseNumber', 'SIZE');
      email('contactEmail', v.contactEmail);
      const phone = compact(v.contactPhone);
      if (phone && !phonePattern.test(phone)) issue('contactPhone', 'INVALID_PHONE');
      if (v.address.trim().length > 300) issue('address', 'SIZE');

      if (v.type === 'INSURER') {
        const shortCode = v.shortCode.trim().toUpperCase();
        if (!shortCode) issue('shortCode', 'REQUIRED');
        else if (!shortCodePattern.test(shortCode)) issue('shortCode', 'PATTERN');
        email('complaintsEmail', v.complaintsEmail);
      }

      if (withManager) {
        const name = v.managerName.trim();
        if (!name) issue('managerName', 'REQUIRED');
        else if (name.length < 2 || name.length > 120) issue('managerName', 'SIZE');
        email('managerEmail', v.managerEmail);
      }
    });
}

type OrganizationPayload = Omit<CreateOrganizationRequest, 'type'>;

function payload(values: OrganizationFormValues): OrganizationPayload {
  const optional = (value: string) => value.trim() || undefined;
  return {
    legalName: values.legalName.trim(),
    tradeName: optional(values.tradeName),
    nuit: compact(values.nuit),
    licenseNumber: optional(values.licenseNumber),
    contactEmail: values.contactEmail.trim(),
    contactPhone: optional(compact(values.contactPhone)),
    address: optional(values.address),
    province: (values.province || undefined) as CreateOrganizationRequest['province'],
    insurerProfile:
      values.type === 'INSURER'
        ? {
            shortCode: values.shortCode.trim().toUpperCase(),
            complaintsEmail: values.complaintsEmail.trim(),
            ombudsmanOrganizationId: values.ombudsmanOrganizationId || undefined,
          }
        : undefined,
  };
}

export function toCreateRequest(values: OrganizationFormValues): CreateOrganizationRequest {
  return { ...payload(values), type: values.type as CreateOrganizationRequestType };
}

export function toUpdateRequest(
  values: OrganizationFormValues,
  version: number,
): UpdateOrganizationRequest {
  return { ...payload(values), version } as UpdateOrganizationRequest;
}

/** Campo do formulário de um erro do servidor (`errors[].field` ou o `code` de topo). */
export function organizationFieldOf(serverField: string): OrganizationField | null {
  const field = serverField.startsWith('insurerProfile.')
    ? serverField.slice('insurerProfile.'.length)
    : serverField;
  return (organizationFields as readonly string[]).includes(field)
    ? (field as OrganizationField)
    : null;
}

/** Erros de topo que pertencem a um campo. */
export const codeFields: Record<string, OrganizationField> = {
  LICENSE_NUMBER_REQUIRED: 'licenseNumber',
  NUIT_ALREADY_IN_USE: 'nuit',
  SHORT_CODE_ALREADY_IN_USE: 'shortCode',
  OMBUDSMAN_INVALID: 'ombudsmanOrganizationId',
};
