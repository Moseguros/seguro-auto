import { describe, expect, it, vi } from 'vitest';
import {
  isTooLarge,
  JPEG_QUALITY,
  prepareImage,
  scaledSize,
  type ImageCodec,
} from './imageCompression';

const MB = 1024 * 1024;

function fileOf(bytes: number, name: string, type: string): File {
  return new File([new Uint8Array(bytes)], name, { type, lastModified: 1_700_000_000_000 });
}

function fakeCodec(width: number, height: number, outBytes: number): ImageCodec {
  return {
    decode: vi.fn(() => Promise.resolve({ width, height, source: {} as CanvasImageSource })),
    encode: vi.fn(() =>
      Promise.resolve(new Blob([new Uint8Array(outBytes)], { type: 'image/jpeg' })),
    ),
  };
}

describe('imageCompression', () => {
  it('reduz um JPEG de 6 MB para 2560 px no lado maior, qualidade 0,85', async () => {
    const codec = fakeCodec(4000, 3000, Math.round(1.2 * MB));
    const original = fileOf(6 * MB, 'frente.JPG', 'image/jpeg');
    const prepared = await prepareImage(original, codec);

    expect(codec.encode).toHaveBeenCalledWith(expect.anything(), 2560, 1920, JPEG_QUALITY);
    expect(prepared.size).toBeLessThan(original.size);
    expect(prepared.type).toBe('image/jpeg');
    expect(prepared.name).toBe('frente.jpg');
    expect(prepared.lastModified).toBe(original.lastModified);
    expect(isTooLarge(prepared)).toBe(false);
  });

  it('deixa um HEIC igual (os browsers não o desenham)', async () => {
    const codec = fakeCodec(4000, 3000, MB);
    const heic = fileOf(6 * MB, 'IMG_0001.HEIC', 'image/heic');
    expect(await prepareImage(heic, codec)).toBe(heic);
    expect(codec.decode).not.toHaveBeenCalled();
  });

  it('deixa iguais os ficheiros até 4 MB e os que não se conseguem desenhar', async () => {
    const small = fileOf(3 * MB, 'a.png', 'image/png');
    const codec = fakeCodec(1000, 800, 100);
    expect(await prepareImage(small, codec)).toBe(small);
    expect(codec.decode).not.toHaveBeenCalled();

    const broken: ImageCodec = {
      decode: () => Promise.reject(new Error('x')),
      encode: vi.fn(),
    };
    const big = fileOf(5 * MB, 'b.png', 'image/png');
    expect(await prepareImage(big, broken)).toBe(big);
  });

  it('mantém a proporção e não aumenta imagens pequenas', () => {
    expect(scaledSize(3000, 4000)).toEqual({ width: 1920, height: 2560 });
    expect(scaledSize(1200, 900)).toEqual({ width: 1200, height: 900 });
    expect(isTooLarge(fileOf(11 * MB, 'c.heic', 'image/heic'))).toBe(true);
  });
});
