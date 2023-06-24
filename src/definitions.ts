export interface PreviewThumbnailPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
  thumbnailFile(options: ThumbnailOptions): Promise<ThumbnailFileResult>;
  thumbnailData(options: ThumbnailOptions): Promise<ThumbnailDataResult>;
}

export interface ThumbnailOptions {
  video: string;
  headers?: { [key: string]: string };
  thumbnailPath?: string;
  imageFormat?: ImageFormat;
  maxHeight?: number;
  maxWidth?: number;
  timeMs?: number;
  quality?: number;
}

export interface ThumbnailFileResult {
  value: string;
}

export interface ThumbnailDataResult {
  value: string;
}

export enum ImageFormat {
  JPEG,
  PNG,
  WEBP,
}
