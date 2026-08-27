import { request } from './api';
import type {
  DashboardMetricsDto,
  MyKpisResponseDto,
  SaveSelfRatingRequestDto,
  AssignedEmployeeSummaryDto,
  EmployeePmsReviewDetailsDto,
  SaveManagerRatingRequestDto,
} from '../types';

export const managerService = {
  // MG001 - Dashboard
  async getDashboardMetrics(): Promise<DashboardMetricsDto> {
    return request<DashboardMetricsDto>('/manager/dashboard');
  },

  // MG002 - My KPIs
  async getMyActiveKpis(): Promise<MyKpisResponseDto> {
    return request<MyKpisResponseDto>('/manager/my-kpis');
  },

  async getMyKpisHistory(): Promise<MyKpisResponseDto[]> {
    return request<MyKpisResponseDto[]>('/manager/my-kpis/history');
  },

  async getMyKpisByAssignmentId(assignmentId: number): Promise<MyKpisResponseDto> {
    return request<MyKpisResponseDto>(`/manager/my-kpis/${assignmentId}`);
  },

  async saveDraftSelfRating(assignmentId: number, data: SaveSelfRatingRequestDto): Promise<MyKpisResponseDto> {
    return request<MyKpisResponseDto>(`/manager/my-kpis/${assignmentId}/self-rating`, {
      method: 'PUT',
      body: JSON.stringify(data),
    });
  },

  async submitSelfRating(assignmentId: number, data: SaveSelfRatingRequestDto): Promise<MyKpisResponseDto> {
    return request<MyKpisResponseDto>(`/manager/my-kpis/${assignmentId}/submit`, {
      method: 'POST',
      body: JSON.stringify(data),
    });
  },

  // MG003 - Assigned Employees
  async getAssignedEmployees(): Promise<AssignedEmployeeSummaryDto[]> {
    return request<AssignedEmployeeSummaryDto[]>('/manager/employees');
  },

  async getEmployeePmsDetails(employeeId: number): Promise<EmployeePmsReviewDetailsDto> {
    return request<EmployeePmsReviewDetailsDto>(`/manager/employees/${employeeId}/pms`);
  },

  async getEmployeePmsDetailsByAssignmentId(employeeId: number, assignmentId: number): Promise<EmployeePmsReviewDetailsDto> {
    return request<EmployeePmsReviewDetailsDto>(`/manager/employees/${employeeId}/pms/${assignmentId}`);
  },

  async saveDraftManagerRating(
    employeeId: number,
    assignmentId: number,
    data: SaveManagerRatingRequestDto
  ): Promise<EmployeePmsReviewDetailsDto> {
    return request<EmployeePmsReviewDetailsDto>(`/manager/employees/${employeeId}/pms/${assignmentId}/ratings`, {
      method: 'PUT',
      body: JSON.stringify(data),
    });
  },

  async submitManagerRating(
    employeeId: number,
    assignmentId: number,
    data: SaveManagerRatingRequestDto
  ): Promise<EmployeePmsReviewDetailsDto> {
    return request<EmployeePmsReviewDetailsDto>(`/manager/employees/${employeeId}/pms/${assignmentId}/submit`, {
      method: 'POST',
      body: JSON.stringify(data),
    });
  },
};

export const managerApi = managerService;
