import { WebPlugin } from '@capacitor/core';

import type {
  PreviewThumbnailPlugin,
  ThumbnailDataResult,
  ThumbnailFileResult,
  ThumbnailOptions,
} from './definitions';

export class PreviewThumbnailWeb
  extends WebPlugin
  implements PreviewThumbnailPlugin
{
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
  thumbnailFile(_: ThumbnailOptions): Promise<ThumbnailFileResult> {
    throw new Error('Method not implemented.');
  }
  thumbnailData(_: ThumbnailOptions): Promise<ThumbnailDataResult> {
    throw new Error('Method not implemented.');
  }
}
