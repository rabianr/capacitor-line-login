import { registerPlugin } from '@capacitor/core'

import type { LineLoginPlugin } from './definitions'

const LineLogin = registerPlugin<LineLoginPlugin>('LineLogin', {
  web: () => import('./web').then(m => new m.LineLoginWeb()),
})

export * from './definitions'
export { LineLogin }
