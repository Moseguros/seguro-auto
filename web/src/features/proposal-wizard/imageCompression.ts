/**
 * Redução das fotografias antes do envio (S4-08 §3, vistoria e KYC): JPEG e PNG acima de 4 MB
 * passam a JPEG com o lado maior até 2560 px e qualidade 0,85 (canvas). O HEIC vai sem
 * alteração (os browsers não o desenham). Acima de 10 MB depois disto não se envia.
 */

export const COMPRESS_ABOVE_BYTES = 4 * 1024 * 1024;
export const MAX_UPLOAD_BYTES = 10 * 1024 * 1024;
export const MAX_SIDE_PX = 2560;
export const JPEG_QUALITY = 0.85;

/** Tipos aceites pela API (fotografias da vistoria e do KYC). */
export const acceptedImageTypes = 'image/jpeg,image/png,image/heic';

export interface DecodedImage {
  width: number;
  height: number;
  source: CanvasImageSource;
  /** Liberta a imagem descodificada. */
  close?: () => void;
}

/** Descodificação e codificação das imagens (o browser; os testes passam uma falsa). */
export interface ImageCodec {
  decode(file: Blob): Promise<DecodedImage>;
  encode(image: DecodedImage, width: number, height: number, quality: number): Promise<Blob>;
}

export const browserCodec: ImageCodec = {
  async decode(file) {
    if (typeof createImageBitmap === 'function') {
      const bitmap = await createImageBitmap(file);
      return {
        width: bitmap.width,
        height: bitmap.height,
        source: bitmap,
        close: () => bitmap.close(),
      };
    }
    const url = URL.createObjectURL(file);
    try {
      const image = new Image();
      image.src = url;
      await image.decode();
      return { width: image.naturalWidth, height: image.naturalHeight, source: image };
    } finally {
      URL.revokeObjectURL(url);
    }
  },
  encode(image, width, height, quality) {
    const canvas = document.createElement('canvas');
    canvas.width = width;
    canvas.height = height;
    const context = canvas.getContext('2d');
    if (!context) return Promise.reject(new Error('canvas 2d indisponível'));
    context.drawImage(image.source, 0, 0, width, height);
    return new Promise((resolve, reject) =>
      canvas.toBlob(
        (blob) => (blob ? resolve(blob) : reject(new Error('toBlob falhou'))),
        'image/jpeg',
        quality,
      ),
    );
  },
};

/** Dimensões com o lado maior até `maxSide`, mantendo a proporção. */
export function scaledSize(width: number, height: number, maxSide = MAX_SIDE_PX) {
  const longest = Math.max(width, height);
  if (longest <= maxSide) return { width, height };
  const ratio = maxSide / longest;
  return { width: Math.round(width * ratio), height: Math.round(height * ratio) };
}

function isCompressible(file: File): boolean {
  return file.type === 'image/jpeg' || file.type === 'image/png';
}

function jpegName(name: string): string {
  const base = name.replace(/\.[^./\\]+$/, '');
  return `${base || 'foto'}.jpg`;
}

/**
 * Ficheiro a enviar: reduzido se for JPEG/PNG acima de 4 MB; o original nos outros casos (ou se
 * o browser não o conseguir desenhar).
 */
export async function prepareImage(file: File, codec: ImageCodec = browserCodec): Promise<File> {
  if (!isCompressible(file) || file.size <= COMPRESS_ABOVE_BYTES) return file;
  let image: DecodedImage | undefined;
  try {
    image = await codec.decode(file);
    const size = scaledSize(image.width, image.height);
    const blob = await codec.encode(image, size.width, size.height, JPEG_QUALITY);
    return new File([blob], jpegName(file.name), {
      type: 'image/jpeg',
      lastModified: file.lastModified,
    });
  } catch {
    return file;
  } finally {
    image?.close?.();
  }
}

/** O ficheiro preparado passa do limite da API? */
export function isTooLarge(file: Blob): boolean {
  return file.size > MAX_UPLOAD_BYTES;
}
