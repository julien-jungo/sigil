export interface IntrospectResponse {
  active: boolean;
  sub: string | null;
  role: string | null;
  exp: string | null;
}

export interface JwkSet {
  keys: unknown[];
}
