import { request } from './api';
import type {
  EmployeeDropdownDto,
  RatingHistoryItemDto,
  MonthlyPmsStatusReportDto,
} from '../types';

export const reportService = {
  async getReportingEmployees(): Promise<EmployeeDropdownDto[]> {
    return request<EmployeeDropdownDto[]>('/manager/reports/employees');
  },

  async getEmployeeRatingHistory(employeeId: number): Promise<RatingHistoryItemDto[]> {
    return request<RatingHistoryItemDto[]>(`/manager/reports/rating-history?employeeId=${employeeId}`);
  },

  async getMonthlyPmsStatus(cycleId?: number): Promise<MonthlyPmsStatusReportDto> {
    const query = cycleId ? `?cycleId=${cycleId}` : '';
    return request<MonthlyPmsStatusReportDto>(`/manager/reports/monthly-status${query}`);
  },
};
