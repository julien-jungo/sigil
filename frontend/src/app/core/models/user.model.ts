export interface User {
  id: string;
  username: string;
  role: 'ADMIN' | 'VIEWER';
  enabled: boolean;
  created_at: string;
}
