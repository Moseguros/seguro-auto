/**
 * O subconjunto de JSON Schema (draft 2020-12) que os schemas das formas de pagamento usam
 * (`backend/src/main/resources/schemas/payment-method-*.json`) e os caminhos dos campos.
 *
 * Um campo tem um caminho com pontos (`sftp.port`). Nos erros do servidor os dados do método
 * vêm como `settings.<caminho>` e os segredos como `secrets.<campo>` (S5-01).
 */
export interface JsonSchema {
  type?: string;
  enum?: string[];
  const?: unknown;
  default?: unknown;
  pattern?: string;
  minLength?: number;
  maxLength?: number;
  minimum?: number;
  maximum?: number;
  description?: string;
  properties?: Record<string, JsonSchema>;
  required?: string[];
  $ref?: string;
  $defs?: Record<string, JsonSchema>;
}

export type Settings = Record<string, unknown>;

export type FieldKind = 'string' | 'enum' | 'const' | 'integer' | 'boolean' | 'object';

/** Um campo do formulário gerado. */
export interface SchemaField {
  /** Caminho com pontos (`sftp.port`). */
  path: string;
  /** Nome do campo dentro do objecto (`port`). */
  name: string;
  schema: JsonSchema;
  kind: FieldKind;
  required: boolean;
  /** Só nos objectos (um nível, como `ports` e `sftp`). */
  children?: SchemaField[];
}

const settingsPrefix = 'settings.';
const secretsPrefix = 'secrets.';

/** `$ref` local (`#/$defs/nome`) trocado pelo schema referido (com o que estiver ao lado). */
export function resolveSchema(root: JsonSchema, schema: JsonSchema): JsonSchema {
  if (!schema.$ref) return schema;
  const match = /^#\/\$defs\/(.+)$/.exec(schema.$ref);
  const target = match?.[1] ? root.$defs?.[match[1]] : undefined;
  const rest: JsonSchema = { ...schema };
  delete rest.$ref;
  return target ? { ...resolveSchema(root, target), ...rest } : rest;
}

function kindOf(schema: JsonSchema): FieldKind {
  if (schema.const !== undefined) return 'const';
  if (schema.type === 'object' || schema.properties) return 'object';
  if (schema.type === 'integer' || schema.type === 'number') return 'integer';
  if (schema.type === 'boolean') return 'boolean';
  if (Array.isArray(schema.enum)) return 'enum';
  return 'string';
}

/** Campos de um objecto do schema, pela ordem das `properties` (os objectos com os filhos). */
export function schemaFields(
  root: JsonSchema,
  object: JsonSchema = root,
  prefix = '',
): SchemaField[] {
  const required = new Set(object.required ?? []);
  return Object.entries(object.properties ?? {}).map(([name, raw]) => {
    const schema = resolveSchema(root, raw);
    const path = prefix ? `${prefix}.${name}` : name;
    const kind = kindOf(schema);
    return {
      path,
      name,
      schema,
      kind,
      required: required.has(name),
      ...(kind === 'object' ? { children: schemaFields(root, schema, path) } : {}),
    };
  });
}

/** Caminhos de todos os campos que não são objectos (os que têm rótulo próprio). */
export function leafPaths(root: JsonSchema): string[] {
  const walk = (fields: SchemaField[]): string[] =>
    fields.flatMap((f) => (f.children ? walk(f.children) : [f.path]));
  return walk(schemaFields(root));
}

/** Caminhos dos objectos (com legenda própria). */
export function objectPaths(root: JsonSchema): string[] {
  return schemaFields(root)
    .filter((f) => f.kind === 'object')
    .map((f) => f.path);
}

function isRecord(value: unknown): value is Record<string, unknown> {
  return !!value && typeof value === 'object' && !Array.isArray(value);
}

/** Valor no caminho (ou `undefined`). */
export function getAt(settings: Settings, path: string): unknown {
  let current: unknown = settings;
  for (const part of path.split('.')) {
    if (!isRecord(current)) return undefined;
    current = current[part];
  }
  return current;
}

/** Cópia com o valor no caminho (cria os objectos que faltarem). */
export function setAt(settings: Settings, path: string, value: unknown): Settings {
  const [head, ...rest] = path.split('.');
  if (head === undefined) return settings;
  if (rest.length === 0) return { ...settings, [head]: value };
  const child = isRecord(settings[head]) ? (settings[head] as Settings) : {};
  return { ...settings, [head]: setAt(child, rest.join('.'), value) };
}

/** Cópia sem o caminho. */
export function deleteAt(settings: Settings, path: string): Settings {
  const [head, ...rest] = path.split('.');
  if (head === undefined || !(head in settings)) return settings;
  if (rest.length === 0) {
    const others = { ...settings };
    delete others[head];
    return others;
  }
  const child = settings[head];
  if (!isRecord(child)) return settings;
  return { ...settings, [head]: deleteAt(child, rest.join('.')) };
}

/** Os `default` do schema nos campos em falta (valores iniciais de um método por configurar). */
export function applyDefaults(root: JsonSchema, settings: Settings): Settings {
  let result = settings;
  const walk = (fields: SchemaField[]) => {
    for (const field of fields) {
      if (field.children) walk(field.children);
      else if (field.schema.default !== undefined && getAt(result, field.path) === undefined) {
        result = setAt(result, field.path, field.schema.default);
      }
    }
  };
  walk(schemaFields(root));
  return result;
}

/**
 * `settings` para enviar: sem os campos escondidos, sem os textos vazios e com os inteiros
 * escritos como número (um texto que não é número segue como está e o servidor recusa-o).
 */
export function cleanSettings(
  root: JsonSchema,
  settings: Settings,
  hidden: (path: string) => boolean,
): Settings {
  let result: Settings = {};
  const walk = (fields: SchemaField[]) => {
    for (const field of fields) {
      if (hidden(field.path)) continue;
      if (field.children) {
        walk(field.children);
        continue;
      }
      let value = getAt(settings, field.path);
      if (value === undefined || value === null) continue;
      if (typeof value === 'string') {
        if (value.trim() === '') continue;
        if (field.kind === 'integer' && /^-?\d+$/.test(value.trim())) value = Number(value.trim());
      }
      result = setAt(result, field.path, value);
    }
  };
  walk(schemaFields(root));
  // Objectos obrigatórios sem nenhum campo preenchido seguem vazios (o servidor diz o que falta).
  for (const field of schemaFields(root)) {
    if (field.kind === 'object' && !hidden(field.path) && result[field.name] === undefined) {
      const current = settings[field.name];
      if (isRecord(current)) result = { ...result, [field.name]: {} };
    }
  }
  return result;
}

/** Campo de erro do servidor de um dado do método. */
export function settingsField(path: string): string {
  return `${settingsPrefix}${path}`;
}

/** Campo de erro do servidor de um segredo. */
export function secretField(name: string): string {
  return `${secretsPrefix}${name}`;
}

/**
 * Onde mostrar um erro do servidor: num dado do método (`settings.sftp.port` → `sftp.port`), num
 * segredo (`secrets.apiKey` → `apiKey`) ou nos dados gerais (`minAmount`).
 */
export function errorTarget(field: string): {
  scope: 'settings' | 'secrets' | 'general';
  path: string;
} {
  if (field.startsWith(settingsPrefix)) {
    return { scope: 'settings', path: field.slice(settingsPrefix.length) };
  }
  if (field.startsWith(secretsPrefix)) {
    return { scope: 'secrets', path: field.slice(secretsPrefix.length) };
  }
  return { scope: 'general', path: field };
}
