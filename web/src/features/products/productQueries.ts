/** Chaves das consultas de produtos (para ler e invalidar sempre as mesmas). */
export const productQueryKey = (id: string) => ['product', id] as const;
export const productVersionQueryKey = (id: string) => ['productVersion', id] as const;
export const productsQueryKey = ['products'] as const;
export const brandingQueryKey = ['insurerBranding'] as const;
