/**
 * Abre o diálogo de gravação do browser para um conteúdo já descarregado (ex.: a exportação CSV
 * dos pagamentos, S5-12).
 */
export function saveBlob(blob: Blob, fileName: string): void {
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
