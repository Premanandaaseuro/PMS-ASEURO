export type UserRole = 'HR' | 'MANAGER' | 'EMPLOYEE';
export type RecordStatus = 'ACTIVE' | 'INACTIVE';

export type PmsStatus =
  | 'PMS_NOT_STARTED'
  | 'PMS_STARTED'
  | 'SELF_ASSESSMENT_DRAFT'
  | 'SELF_ASSESSMENT_SUBMITTED'
  | 'MANAGER_REVIEW_PENDING'
  | 'MANAGER_REVIEW_SUBMITTED'
  | 'HR_REVIEW_PENDING'
  | 'HR_REVIEW_COMPLETED'
  | 'RATING_AND_POINTS_CALCULATED'
  | 'FINAL_ANALYSIS'
  | 'FINAL_RESULT_PUBLISHED'
  | 'COMPLETED';

export interface UserInfoDto {
  userId: number;
  employeeId?: number;
  employeeCode?: string;
  fullName: string;
  email: string;
  role: UserRole;
  department?: string;
  designation?: string;
  team?: string;
}

export interface AuthResponse {
  token: string;
  tokenType: string;
  user: UserInfoDto;
}

export interface DashboardMetricsDto {
  managerName?: string;
  activeCycleId?: number;
  activeCycleName?: string;
  activeCycleMonth?: number;
  activeCycleYear?: number;
  activeCycleStartDate?: string;
  activeCycleEndDate?: string;
  assignedEmployeesCount: number;
  pendingReviewsCount: number;
  completedReviewsCount: number;
  selfPmsAssignmentId?: number;
  selfPmsStatus?: string;
  selfPmsSubmitted: boolean;
  selfPmsFinalized: boolean;
}

export interface KpiItemDto {
  pmsKpiId: number;
  kpiId: number;
  kpiName: string;
  measurementCriteria: string;
  weightage: number;
  selfRating?: number;
  selfComments?: string;
  managerRating?: number;
  managerComments?: string;
}

export interface MyKpisResponseDto {
  assignmentId: number;
  cycleId: number;
  cycleName: string;
  month: number;
  year: number;
  designation: string;
  reportingManagerName: string;
  status: string;
  reviewStatus: string;
  editable: boolean;
  submitted: boolean;
  finalized: boolean;
  submittedAt?: string;
  generalComments?: string;
  finalScore?: number;
  ratingCategory?: string;
  hrComments?: string;
  publishedAt?: string;
  kpis: KpiItemDto[];
}

export interface KpiRatingInputDto {
  pmsKpiId: number;
  rating: number;
  comments?: string;
}

export interface SaveSelfRatingRequestDto {
  generalComments?: string;
  ratings: KpiRatingInputDto[];
}

export interface AssignedEmployeeSummaryDto {
  employeeId: number;
  employeeCode: string;
  fullName: string;
  email: string;
  designation: string;
  department: string;
  team: string;
  assignmentId?: number;
  pmsStatus: string;
  managerReviewStatus: string;
  selfAssessmentSubmitted: boolean;
  managerReviewSubmitted: boolean;
}

export interface EmployeeKpiReviewItemDto {
  pmsKpiId: number;
  kpiId: number;
  kpiName: string;
  measurementCriteria: string;
  weightage: number;
  selfRating?: number;
  employeeComments?: string;
  managerRating?: number;
  managerComments?: string;
}

export interface EmployeePmsReviewDetailsDto {
  employee: AssignedEmployeeSummaryDto;
  assignmentId: number;
  cycleId: number;
  cycleName: string;
  month: number;
  year: number;
  status: string;
  editableByManager: boolean;
  managerSubmitted: boolean;
  managerSubmittedAt?: string;
  employeeGeneralComments?: string;
  managerGeneralComments?: string;
  kpis: EmployeeKpiReviewItemDto[];
}

export interface SaveManagerRatingRequestDto {
  generalComments?: string;
  ratings: KpiRatingInputDto[];
}

export interface EmployeeDropdownDto {
  employeeId: number;
  employeeCode: string;
  fullName: string;
  designation: string;
  team: string;
}

export interface RatingHistoryItemDto {
  assignmentId: number;
  cycleId: number;
  cycleName: string;
  month: number;
  year: number;
  pmsStatus: string;
  averageSelfRating?: number;
  averageManagerRating?: number;
  finalOverallScore?: number;
  ratingCategory?: string;
  publishedAt?: string;
  kpis: EmployeeKpiReviewItemDto[];
}

export interface MonthlyPmsStatusItemDto {
  employeeId: number;
  employeeCode: string;
  fullName: string;
  designation: string;
  team: string;
  assignmentId?: number;
  pmsStatus: string;
  selfRatingStatus: string;
  managerReviewStatus: string;
  averageSelfRating?: number;
  averageManagerRating?: number;
  finalScore?: number;
  ratingCategory?: string;
}

export interface MonthlyPmsStatusReportDto {
  cycleId: number;
  cycleName: string;
  month: number;
  year: number;
  totalAssigned: number;
  pendingSelfRatingCount: number;
  completedSelfRatingCount: number;
  pendingManagerReviewCount: number;
  completedManagerReviewCount: number;
  finalizedCount: number;
  employees: MonthlyPmsStatusItemDto[];
}

export interface ApiErrorResponse {
  status: number;
  error: string;
  message: string;
  details?: string[];
  timestamp: string;
}
