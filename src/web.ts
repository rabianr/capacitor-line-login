import { WebPlugin } from '@capacitor/core'

import type {
  LineLoginPlugin,
  SetupOptions,
  Result,
  LoginResult,
  GetAccessTokenResult,
} from './definitions'

export class LineLoginWeb extends WebPlugin implements LineLoginPlugin {
  async setup(options: SetupOptions): Promise<void> {
    console.log(options)
    throw this.unimplemented('Not implemented on web.')
  }

  async login(): Promise<LoginResult> {
    throw this.unimplemented('Not implemented on web.')
  }

  async logout(): Promise<Result> {
    throw this.unimplemented('Not implemented on web.')
  }

  async getAccessToken(): Promise<GetAccessTokenResult> {
    throw this.unimplemented('Not implemented on web.')
  }

  async refreshAccessToken(): Promise<GetAccessTokenResult> {
    throw this.unimplemented('Not implemented on web.')
  }

  async verifyAccessToken(): Promise<Result> {
    throw this.unimplemented('Not implemented on web.')
  }
}
