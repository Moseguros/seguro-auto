import { downloadFile } from '../api/generated';

/**
 * Descarrega um ficheiro protegido (com o token da sessão, nunca na URL) e abre o diálogo de
 * gravação do browser com o nome dado.
 */
export async function downloadProtectedFile(fileId: string, fileName: string): Promise<void> {
  const blob = await downloadFile(fileId);
  const url = URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = url;
  link.download = fileName;
  link.rel = 'noopener';
  link.style.display = 'none';
  document.body.appendChild(link);
  link.click();
  link.remove();
  // Alguns browsers só lêem o URL depois do clique: revoga-se mais tarde.
  setTimeout(() => URL.revokeObjectURL(url), 60_000);
}
