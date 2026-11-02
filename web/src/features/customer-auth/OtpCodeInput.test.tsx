import { fireEvent, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it, vi } from 'vitest';
import { renderWithProviders } from '../../test/render';
import { OtpCodeInput } from './OtpCodeInput';

function boxes() {
  return screen.getAllByRole('textbox') as HTMLInputElement[];
}

describe('OtpCodeInput', () => {
  it('tem uma caixa por dígito, com one-time-code e teclado numérico', () => {
    renderWithProviders(<OtpCodeInput length={6} onComplete={vi.fn()} />);
    expect(boxes()).toHaveLength(6);
    expect(boxes()[0]).toHaveAttribute('autocomplete', 'one-time-code');
    for (const box of boxes()) expect(box).toHaveAttribute('inputmode', 'numeric');
  });

  it('colar o código preenche todas as caixas e submete', () => {
    const onComplete = vi.fn();
    renderWithProviders(<OtpCodeInput length={6} onComplete={onComplete} />);
    fireEvent.paste(boxes()[2]!, { clipboardData: { getData: () => ' 123-456 ' } });
    expect(boxes().map((box) => box.value)).toEqual(['1', '2', '3', '4', '5', '6']);
    expect(onComplete).toHaveBeenCalledTimes(1);
    expect(onComplete).toHaveBeenCalledWith('123456');
  });

  it('o preenchimento automático (vários dígitos numa caixa) também preenche todas', () => {
    const onComplete = vi.fn();
    renderWithProviders(<OtpCodeInput length={4} onComplete={onComplete} />);
    fireEvent.change(boxes()[0]!, { target: { value: '9876' } });
    expect(onComplete).toHaveBeenCalledWith('9876');
  });

  it('escrever dígito a dígito avança e só submete no fim; ignora letras', async () => {
    const onComplete = vi.fn();
    renderWithProviders(<OtpCodeInput length={4} onComplete={onComplete} />);
    await userEvent.click(boxes()[0]!);
    await userEvent.keyboard('1a2');
    expect(onComplete).not.toHaveBeenCalled();
    expect(boxes().map((box) => box.value)).toEqual(['1', '2', '', '']);
    expect(boxes()[2]).toHaveFocus();
    await userEvent.keyboard('34');
    expect(onComplete).toHaveBeenCalledWith('1234');
  });

  it('Backspace numa caixa vazia apaga a anterior', async () => {
    renderWithProviders(<OtpCodeInput length={4} onComplete={vi.fn()} />);
    await userEvent.click(boxes()[0]!);
    await userEvent.keyboard('12');
    await userEvent.keyboard('{Backspace}');
    expect(boxes().map((box) => box.value)).toEqual(['1', '', '', '']);
    expect(boxes()[1]).toHaveFocus();
  });
});
