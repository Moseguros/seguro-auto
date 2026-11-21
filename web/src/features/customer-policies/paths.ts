import { menus } from '../../layout/menus';

function menuPath(key: string): string {
  const item = menus.cliente.find((i) => i.key === key);
  if (!item) throw new Error(`menus.cliente sem o item ${key}`);
  return item.path;
}

/** Caminhos das apólices do cliente (S6-06). */
export const customerPolicyPaths = {
  list: menuPath('policies'),
  detail: (id: string) => `${menuPath('policies')}/${id}`,
  /** "Simular seguro" do estado vazio: o item `quote` do menu do cliente. */
  quote: menuPath('quote'),
} as const;
