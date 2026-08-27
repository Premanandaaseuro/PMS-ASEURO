import type { ApiErrorResponse } from '../types';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api';

export class ApiError extends Error {
  status: number;
  errorData: ApiErrorResponse;

  constructor(errorData: ApiErrorResponse) {
    super(errorData.message || 'An error occurred');
    this.name = 'ApiError';
    this.status = errorData.status;
    this.errorData = errorData;
  }
}

export async function request<T>(endpoint: string, options: RequestInit = {}): Promise<T> {
  const token = localStorage.getItem('pms_token') || localStorage.getItem('pms_jwt_token');

  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    Accept: 'application/json',
    ...(options.headers as Record<string, string>),
  };

  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  const url = `${API_BASE_URL}${endpoint.startsWith('/') ? endpoint : `/${endpoint}`}`;

  try {
    const response = await fetch(url, {
      ...options,
      headers,
    });

    if (!response.ok) {
      let errorData: ApiErrorResponse;
      try {
        errorData = await response.json();
      } catch {
        errorData = {
          status: response.status,
          error: response.statusText,
          message: `Request failed with status ${response.status}`,
          timestamp: new Date().toISOString(),
        };
      }

      if (response.status === 401 && !endpoint.includes('/auth/login')) {
        localStorage.removeItem('pms_jwt_token');
        localStorage.removeItem('pms_user');
        window.location.href = '/login';
      }

      throw new ApiError(errorData);
    }

    if (response.status === 204) {
      return {} as T;
    }

    return await response.json();
  } catch (error) {
    if (error instanceof ApiError) {
      throw error;
    }
    throw new ApiError({
      status: 0,
      error: 'Network Error',
      message: error instanceof Error ? error.message : 'Network connection failed',
      timestamp: new Date().toISOString(),
    });
  }
}
