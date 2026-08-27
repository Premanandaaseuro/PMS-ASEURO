import { request } from './api';
import type { AuthResponse, UserInfoDto } from '../types';

export const authService = {
  async login(email: string, password: string): Promise<AuthResponse> {
    const data = await request<AuthResponse>('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, password }),
    });

    if (data.token) {
      localStorage.setItem('pms_jwt_token', data.token);
      localStorage.setItem('pms_user', JSON.stringify(data.user));
    }
    return data;
  },

  async getMe(): Promise<UserInfoDto> {
    return request<UserInfoDto>('/auth/me');
  },

  logout(): void {
    localStorage.removeItem('pms_jwt_token');
    localStorage.removeItem('pms_user');
  },

  getStoredUser(): UserInfoDto | null {
    const userJson = localStorage.getItem('pms_user');
    if (userJson) {
      try {
        return JSON.parse(userJson);
      } catch {
        return null;
      }
    }
    return null;
  },

  isAuthenticated(): boolean {
    return !!localStorage.getItem('pms_jwt_token');
  },
};
