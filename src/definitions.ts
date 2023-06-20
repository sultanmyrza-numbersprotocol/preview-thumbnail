export interface PreviewThumbnailPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
