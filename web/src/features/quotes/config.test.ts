import { describe, expect, it } from 'vitest';
import { customerReturnPath } from './config';

describe('customerReturnPath', () => {
  it('aceita só caminhos da área do cliente', () => {
    expect(customerReturnPath('/cliente/simular')).toBe('/cliente/simular');
    expect(customerReturnPath(null)).toBeNull();
    expect(customerReturnPath('')).toBeNull();
    expect(customerReturnPath('/cliente')).toBeNull();
    expect(customerReturnPath('//evil.example/cliente/')).toBeNull();
    expect(customerReturnPath('https://evil.example/cliente/x')).toBeNull();
    expect(customerReturnPath('/admin/organizacoes')).toBeNull();
    expect(customerReturnPath('/cliente/\\evil')).toBeNull();
  });
});
