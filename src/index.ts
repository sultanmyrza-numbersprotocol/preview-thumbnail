import { registerPlugin } from '@capacitor/core';

import type { PreviewThumbnailPlugin } from './definitions';

const PreviewThumbnail = registerPlugin<PreviewThumbnailPlugin>(
  'PreviewThumbnail',
  {
    web: () => import('./web').then(m => new m.PreviewThumbnailWeb()),
  },
);

export * from './definitions';
export { PreviewThumbnail };
