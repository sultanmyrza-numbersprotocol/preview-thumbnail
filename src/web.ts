import { WebPlugin } from '@capacitor/core';

import type { PreviewThumbnailPlugin } from './definitions';

export class PreviewThumbnailWeb
  extends WebPlugin
  implements PreviewThumbnailPlugin
{
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
