import React, { useState, useEffect } from 'react';
import { BarChart3, User } from 'lucide-react';
import { reportService } from '../services/reportService';
import type {
  EmployeeDropdownDto,
  RatingHistoryItemDto,
  MonthlyPmsStatusReportDto,
  EmployeeKpiReviewItemDto,
  MonthlyPmsStatusItemDto,
} from '../types';
import { StatusBadge } from '../components/StatusBadge';
import { LoadingSpinner } from '../components/LoadingSpinner';
import { AlertBanner } from '../components/AlertBanner';
import { EmptyState } from '../components/EmptyState';
import { formatScore, hasValidScore } from '../utils/format';

export const ReportsPage: React.FC = () => {
  const [activeReportTab, setActiveReportTab] = useState<'employee-history' | 'monthly-status'>('employee-history');

  // Report A: Employee Rating History
  const [employeesDropdown, setEmployeesDropdown] = useState<EmployeeDropdownDto[]>([]);
  const [selectedEmployeeId, setSelectedEmployeeId] = useState<number | null>(null);
  const [ratingHistory, setRatingHistory] = useState<RatingHistoryItemDto[]>([]);
  const [isHistoryLoading, setIsHistoryLoading] = useState(false);

  // Report B: Monthly PMS Status
  const [monthlyStatusReport, setMonthlyStatusReport] = useState<MonthlyPmsStatusReportDto | null>(null);
  const [isStatusLoading, setIsStatusLoading] = useState(false);

  const [alert, setAlert] = useState<{ type: 'success' | 'error' | 'warning' | 'info'; message: string } | null>(null);

  // Load dropdown list of reporting employees
  useEffect(() => {
    const loadDropdown = async () => {
      try {
        const list = await reportService.getReportingEmployees();
        setEmployeesDropdown(list);
        if (list.length > 0) {
          setSelectedEmployeeId(list[0].employeeId);
        }
      } catch (err) {
        setAlert({
          type: 'error',
          message: err instanceof Error ? err.message : 'Failed to load employee list.',
        });
      }
    };

    loadDropdown();
  }, []);

  // Fetch rating history when employee is selected
  useEffect(() => {
    if (!selectedEmployeeId) return;

    const fetchHistory = async () => {
      setIsHistoryLoading(true);
      try {
        const history = await reportService.getEmployeeRatingHistory(selectedEmployeeId);
        setRatingHistory(history);
      } catch (err) {
        setAlert({
          type: 'error',
          message: err instanceof Error ? err.message : 'Failed to load employee rating history.',
        });
      } finally {
        setIsHistoryLoading(false);
      }
    };

    fetchHistory();
  }, [selectedEmployeeId]);

  // Fetch monthly status report
  useEffect(() => {
    if (activeReportTab !== 'monthly-status') return;

    const fetchMonthlyStatus = async () => {
      setIsStatusLoading(true);
      try {
        const report = await reportService.getMonthlyPmsStatus();
        setMonthlyStatusReport(report);
      } catch (err) {
        setAlert({
          type: 'error',
          message: err instanceof Error ? err.message : 'Failed to load monthly PMS status report.',
        });
      } finally {
        setIsStatusLoading(false);
      }
    };

    fetchMonthlyStatus();
  }, [activeReportTab]);

  return (
    <div>
      {/* Report Navigation Tabs */}
      <div style={{ display: 'flex', gap: '0.75rem', marginBottom: '1.75rem', borderBottom: '1px solid var(--border-subtle)', paddingBottom: '0.5rem' }}>
        <button
          type="button"
          className={`btn ${activeReportTab === 'employee-history' ? 'btn-primary' : 'btn-secondary'} btn-sm`}
          onClick={() => setActiveReportTab('employee-history')}
        >
          <User size={16} />
          <span>Employee Multi-Cycle Rating History</span>
        </button>
        <button
          type="button"
          className={`btn ${activeReportTab === 'monthly-status' ? 'btn-primary' : 'btn-secondary'} btn-sm`}
          onClick={() => setActiveReportTab('monthly-status')}
        >
          <BarChart3 size={16} />
          <span>Monthly Team PMS Status Report</span>
        </button>
      </div>

      {alert && (
        <AlertBanner
          type={alert.type}
          message={alert.message}
          onClose={() => setAlert(null)}
        />
      )}

      {/* REPORT A: Employee Multi-Cycle Rating History */}
      {activeReportTab === 'employee-history' && (
        <div>
          {/* Employee Selector Bar */}
          <div className="card" style={{ marginBottom: '1.75rem', padding: '1.25rem 1.5rem' }}>
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '1rem' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', width: '38px', height: '38px', borderRadius: 'var(--radius-md)', background: 'var(--color-primary-light)', color: 'var(--color-accent)' }}>
                  <User size={20} />
                </div>
                <div>
                  <div style={{ fontSize: '0.82rem', fontWeight: 700, color: 'var(--color-dark-muted)', textTransform: 'uppercase' }}>
                    Select Reporting Employee
                  </div>
                  <div style={{ fontSize: '0.9rem', fontWeight: 600 }}>
                    Choose a team member to inspect historical ratings
                  </div>
                </div>
              </div>

              <div style={{ minWidth: '300px' }}>
                <select
                  className="form-select"
                  value={selectedEmployeeId || ''}
                  onChange={(e) => setSelectedEmployeeId(Number(e.target.value))}
                  disabled={isHistoryLoading || employeesDropdown.length === 0}
                >
                  {employeesDropdown.map((emp: EmployeeDropdownDto) => (
                    <option key={emp.employeeId} value={emp.employeeId}>
                      {emp.fullName} ({emp.employeeCode}) — {emp.designation}
                    </option>
                  ))}
                </select>
              </div>
            </div>
          </div>

          {isHistoryLoading ? (
            <LoadingSpinner message="Fetching multi-cycle rating history..." />
          ) : ratingHistory.length === 0 ? (
            <EmptyState
              title="No Evaluation History"
              message="No previous evaluation cycles were found for this employee."
            />
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '1.75rem' }}>
              {ratingHistory.map((cycle: RatingHistoryItemDto) => (
                <div key={cycle.assignmentId} className="card">
                  <div className="card-header">
                    <div>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '0.6rem' }}>
                        <h3 style={{ fontSize: '1.25rem', fontWeight: 800 }}>{cycle.cycleName}</h3>
                        <StatusBadge status={cycle.pmsStatus} />
                      </div>
                      <div style={{ display: 'flex', gap: '1.5rem', flexWrap: 'wrap', color: 'var(--color-dark-muted)', fontSize: '0.85rem', marginTop: '0.25rem' }}>
                        <div>Avg Self Rating: <strong>{formatScore(cycle.averageSelfRating)} / 5.00</strong></div>
                        <div>Avg Manager Rating: <strong>{formatScore(cycle.averageManagerRating)} / 5.00</strong></div>
                        {hasValidScore(cycle.finalOverallScore) && (
                          <div style={{ color: 'var(--color-accent)', fontWeight: 700 }}>
                            Final Score: <strong>{formatScore(cycle.finalOverallScore)}</strong> ({cycle.ratingCategory || 'N/A'})
                          </div>
                        )}
                      </div>
                    </div>
                  </div>

                  <div className="table-container">
                    <table className="data-table">
                      <thead>
                        <tr>
                          <th>KPI Name</th>
                          <th>Weightage</th>
                          <th>Self Rating</th>
                          <th>Self Comments</th>
                          <th>Manager Rating</th>
                          <th>Manager Feedback</th>
                        </tr>
                      </thead>
                      <tbody>
                        {cycle.kpis.map((kpi: EmployeeKpiReviewItemDto) => (
                          <tr key={kpi.pmsKpiId}>
                            <td style={{ fontWeight: 600 }}>{kpi.kpiName}</td>
                            <td>{kpi.weightage}%</td>
                            <td>
                              <span style={{ fontWeight: 700, color: 'var(--color-accent)' }}>
                                {formatScore(kpi.selfRating)}
                              </span>
                            </td>
                            <td style={{ fontSize: '0.82rem', color: 'var(--color-dark-light)', maxWidth: '240px' }}>
                              {kpi.employeeComments || '—'}
                            </td>
                            <td>
                              <span style={{ fontWeight: 700, color: 'var(--color-dark)' }}>
                                {formatScore(kpi.managerRating)}
                              </span>
                            </td>
                            <td style={{ fontSize: '0.82rem', color: 'var(--color-dark-light)', maxWidth: '240px' }}>
                              {kpi.managerComments || '—'}
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* REPORT B: Monthly Team PMS Status Report */}
      {activeReportTab === 'monthly-status' && (
        <div>
          {isStatusLoading ? (
            <LoadingSpinner message="Calculating monthly team PMS status..." />
          ) : !monthlyStatusReport ? (
            <EmptyState
              title="No Report Data"
              message="Could not load monthly PMS status report for the active cycle."
            />
          ) : (
            <div>
              {/* Summary Stats Grid */}
              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '1.25rem', marginBottom: '2rem' }}>
                <div className="card" style={{ padding: '1.25rem' }}>
                  <div style={{ fontSize: '0.78rem', fontWeight: 700, color: 'var(--color-dark-muted)', textTransform: 'uppercase' }}>
                    Total Assigned
                  </div>
                  <div style={{ fontSize: '1.75rem', fontWeight: 800, marginTop: '0.2rem' }}>
                    {monthlyStatusReport.totalAssigned}
                  </div>
                </div>

                <div className="card" style={{ padding: '1.25rem' }}>
                  <div style={{ fontSize: '0.78rem', fontWeight: 700, color: '#B45309', textTransform: 'uppercase' }}>
                    Pending Self-Rating
                  </div>
                  <div style={{ fontSize: '1.75rem', fontWeight: 800, color: '#B45309', marginTop: '0.2rem' }}>
                    {monthlyStatusReport.pendingSelfRatingCount}
                  </div>
                </div>

                <div className="card" style={{ padding: '1.25rem' }}>
                  <div style={{ fontSize: '0.78rem', fontWeight: 700, color: '#047857', textTransform: 'uppercase' }}>
                    Completed Self-Rating
                  </div>
                  <div style={{ fontSize: '1.75rem', fontWeight: 800, color: '#047857', marginTop: '0.2rem' }}>
                    {monthlyStatusReport.completedSelfRatingCount}
                  </div>
                </div>

                <div className="card" style={{ padding: '1.25rem' }}>
                  <div style={{ fontSize: '0.78rem', fontWeight: 700, color: '#B45309', textTransform: 'uppercase' }}>
                    Pending Manager Review
                  </div>
                  <div style={{ fontSize: '1.75rem', fontWeight: 800, color: '#B45309', marginTop: '0.2rem' }}>
                    {monthlyStatusReport.pendingManagerReviewCount}
                  </div>
                </div>

                <div className="card" style={{ padding: '1.25rem' }}>
                  <div style={{ fontSize: '0.78rem', fontWeight: 700, color: 'var(--color-accent)', textTransform: 'uppercase' }}>
                    Completed Manager Review
                  </div>
                  <div style={{ fontSize: '1.75rem', fontWeight: 800, color: 'var(--color-accent)', marginTop: '0.2rem' }}>
                    {monthlyStatusReport.completedManagerReviewCount}
                  </div>
                </div>

                <div className="card" style={{ padding: '1.25rem' }}>
                  <div style={{ fontSize: '0.78rem', fontWeight: 700, color: '#047857', textTransform: 'uppercase' }}>
                    Finalized Results
                  </div>
                  <div style={{ fontSize: '1.75rem', fontWeight: 800, color: '#047857', marginTop: '0.2rem' }}>
                    {monthlyStatusReport.finalizedCount}
                  </div>
                </div>
              </div>

              {/* Team Breakdown Table */}
              <div className="card">
                <div className="card-header">
                  <div>
                    <h3 style={{ fontSize: '1.25rem', fontWeight: 800 }}>{monthlyStatusReport.cycleName}</h3>
                    <p style={{ color: 'var(--color-dark-muted)', fontSize: '0.85rem' }}>
                      Detailed submission progress for team members reporting to you.
                    </p>
                  </div>
                </div>

                <div className="table-container">
                  <table className="data-table">
                    <thead>
                      <tr>
                        <th>Code</th>
                        <th>Employee Name</th>
                        <th>Designation</th>
                        <th>Team</th>
                        <th>Self Rating</th>
                        <th>Avg Self</th>
                        <th>Manager Review</th>
                        <th>Avg Mgr</th>
                        <th>PMS Status</th>
                      </tr>
                    </thead>
                    <tbody>
                      {monthlyStatusReport.employees.map((emp: MonthlyPmsStatusItemDto) => (
                        <tr key={emp.employeeId}>
                          <td style={{ fontFamily: 'monospace', fontWeight: 700, color: 'var(--color-accent)' }}>
                            {emp.employeeCode}
                          </td>
                          <td style={{ fontWeight: 700 }}>{emp.fullName}</td>
                          <td>{emp.designation}</td>
                          <td>{emp.team}</td>
                          <td>
                            {emp.selfRatingStatus === 'COMPLETED' ? (
                              <span style={{ color: '#047857', fontWeight: 700, fontSize: '0.82rem' }}>Submitted</span>
                            ) : (
                              <span style={{ color: '#B45309', fontWeight: 700, fontSize: '0.82rem' }}>Pending</span>
                            )}
                          </td>
                          <td>
                            <span style={{ fontWeight: 700, color: 'var(--color-accent)' }}>
                              {formatScore(emp.averageSelfRating)}
                            </span>
                          </td>
                          <td>
                            {emp.managerReviewStatus === 'COMPLETED' ? (
                              <span style={{ color: '#047857', fontWeight: 700, fontSize: '0.82rem' }}>Submitted</span>
                            ) : (
                              <span style={{ color: '#B45309', fontWeight: 700, fontSize: '0.82rem' }}>Pending</span>
                            )}
                          </td>
                          <td>
                            <span style={{ fontWeight: 700, color: 'var(--color-dark)' }}>
                              {formatScore(emp.averageManagerRating)}
                            </span>
                          </td>
                          <td>
                            <StatusBadge status={emp.pmsStatus} />
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  );
};
