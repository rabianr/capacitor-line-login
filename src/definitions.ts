export interface LineLoginPlugin {
  setup(options: SetupOptions): Promise<void>;
  login(): Promise<LoginResult>;
  logout(): Promise<Result>;
  getAccessToken(): Promise<GetAccessTokenResult>;
  refreshAccessToken(): Promise<GetAccessTokenResult>;
  verifyAccessToken(): Promise<Result>;
}

export interface SetupOptions {
  channelId: string;
}

export interface Result {
  code: 'SUCCESS' | 'CANCEL' | 'UNKNOWN_ERROR';
}

export interface LoginResult extends Result {
  data: {
    accessToken: string;
    estimatedExpirationTimeMillis: number;
    userID: string;
    email?: string;
    displayName: string;
    pictureUrl?: string;
  };
}

export interface GetAccessTokenResult extends Result {
  data: {
    accessToken: string;
    estimatedExpirationTimeMillis: number;
  };
}
