import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Users, Search, ChevronRight, FileCheck, Clock, CheckCircle2 } from 'lucide-react';
import { managerService } from '../services/managerService';
import type { AssignedEmployeeSummaryDto } from '../types';
import { StatusBadge } from '../components/StatusBadge';
import { LoadingSpinner } from '../components/LoadingSpinner';
import { AlertBanner } from '../components/AlertBanner';
import { EmptyState } from '../components/EmptyState';

interface AssignedEmployeesPageProps {
  onSelectEmployee?: (employeeId: number, assignmentId: number) => void;
}

export const AssignedEmployeesPage: React.FC<AssignedEmployeesPageProps> = ({ onSelectEmployee }) => {
  const [employees, setEmployees] = useState<AssignedEmployeeSummaryDto[]>([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [isLoading, setIsLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const navigate = useNavigate();

  useEffect(() => {
    const fetchEmployees = async () => {
      try {
        const data = await managerService.getAssignedEmployees();
        setEmployees(data);
      } catch (err) {
        setErrorMessage(err instanceof Error ? err.message : 'Failed to load assigned employees.');
      } finally {
        setIsLoading(false);
      }
    };

    fetchEmployees();
  }, []);

  const filteredEmployees = employees.filter((emp: AssignedEmployeeSummaryDto) => {
    const q = searchQuery.toLowerCase();
    return (
      emp.fullName.toLowerCase().includes(q) ||
      emp.employeeCode.toLowerCase().includes(q) ||
      emp.designation.toLowerCase().includes(q) ||
      emp.team.toLowerCase().includes(q)
    );
  });

  if (isLoading) {
    return <LoadingSpinner message="Loading assigned reporting employees..." />;
  }

  return (
    <div>
      {errorMessage && (
        <AlertBanner
          type="error"
          message={errorMessage}
          onClose={() => setErrorMessage(null)}
        />
      )}

      {/* Header & Search Bar */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '1rem', marginBottom: '1.75rem' }}>
        <div>
          <h2 style={{ fontSize: '1.45rem', fontWeight: 800 }}>Reporting Team Members</h2>
          <p style={{ color: 'var(--color-dark-muted)', fontSize: '0.88rem' }}>
            Employees assigned to you for performance evaluation in the active cycle.
          </p>
        </div>

        <div style={{ position: 'relative', minWidth: '280px' }}>
          <input
            type="text"
            className="form-input"
            style={{ paddingLeft: '2.5rem' }}
            placeholder="Search by name, code, or role..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
          <Search size={18} style={{ position: 'absolute', left: '0.85rem', top: '50%', transform: 'translateY(-50%)', color: 'var(--color-dark-muted)' }} />
        </div>
      </div>

      {employees.length === 0 ? (
        <EmptyState
          icon={<Users size={36} />}
          title="No Employees Assigned"
          message="No employees are currently mapped as reporting to your manager account."
        />
      ) : filteredEmployees.length === 0 ? (
        <EmptyState
          title="No Matching Employees"
          message={`No employees matched your search query "${searchQuery}".`}
          actionLabel="Clear Filter"
          onAction={() => setSearchQuery('')}
        />
      ) : (
        <div className="table-container">
          <table className="data-table">
            <thead>
              <tr>
                <th>Code</th>
                <th>Employee Name</th>
                <th>Designation & Team</th>
                <th>Self Rating</th>
                <th>Manager Review</th>
                <th>PMS Status</th>
                <th style={{ textAlign: 'right' }}>Action</th>
              </tr>
            </thead>
            <tbody>
              {filteredEmployees.map((emp: AssignedEmployeeSummaryDto) => {
                const isAwaitingManager = emp.selfAssessmentSubmitted && !emp.managerReviewSubmitted;

                return (
                  <tr key={emp.employeeId}>
                    <td>
                      <span style={{ fontWeight: 700, color: 'var(--color-accent)', fontFamily: 'monospace', fontSize: '0.9rem' }}>
                        {emp.employeeCode}
                      </span>
                    </td>
                    <td>
                      <div style={{ fontWeight: 700, color: 'var(--color-dark)' }}>{emp.fullName}</div>
                      <div style={{ fontSize: '0.78rem', color: 'var(--color-dark-muted)' }}>{emp.email}</div>
                    </td>
                    <td>
                      <div style={{ fontWeight: 600 }}>{emp.designation}</div>
                      <div style={{ fontSize: '0.78rem', color: 'var(--color-dark-muted)' }}>{emp.team} ({emp.department})</div>
                    </td>
                    <td>
                      {emp.selfAssessmentSubmitted ? (
                        <span style={{ display: 'inline-flex', alignItems: 'center', gap: '0.35rem', color: '#047857', fontWeight: 600, fontSize: '0.82rem' }}>
                          <CheckCircle2 size={14} />
                          <span>Submitted</span>
                        </span>
                      ) : (
                        <span style={{ display: 'inline-flex', alignItems: 'center', gap: '0.35rem', color: '#B45309', fontWeight: 600, fontSize: '0.82rem' }}>
                          <Clock size={14} />
                          <span>Pending</span>
                        </span>
                      )}
                    </td>
                    <td>
                      {emp.managerReviewSubmitted ? (
                        <span style={{ display: 'inline-flex', alignItems: 'center', gap: '0.35rem', color: '#047857', fontWeight: 600, fontSize: '0.82rem' }}>
                          <FileCheck size={14} />
                          <span>Completed</span>
                        </span>
                      ) : (
                        <span style={{ display: 'inline-flex', alignItems: 'center', gap: '0.35rem', color: '#B45309', fontWeight: 600, fontSize: '0.82rem' }}>
                          <Clock size={14} />
                          <span>Pending</span>
                        </span>
                      )}
                    </td>
                    <td>
                      <StatusBadge status={emp.pmsStatus} />
                    </td>
                    <td style={{ textAlign: 'right' }}>
                      {emp.pmsStatus === 'NOT_ASSIGNED' || !emp.assignmentId ? (
                        <span style={{ fontSize: '0.82rem', color: 'var(--color-dark-muted)', fontStyle: 'italic', paddingRight: '0.5rem' }}>
                          Awaiting Cycle Setup
                        </span>
                      ) : (
                        <button
                          type="button"
                          className={`btn ${isAwaitingManager ? 'btn-primary' : 'btn-secondary'} btn-sm`}
                          onClick={() =>
                            onSelectEmployee
                              ? onSelectEmployee(emp.employeeId, emp.assignmentId || 0)
                              : navigate(`/assigned-employees/${emp.employeeId}/review`)
                          }
                        >
                          <span>{emp.managerReviewSubmitted ? 'View Review' : 'Review PMS'}</span>
                          <ChevronRight size={15} />
                        </button>
                      )}
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};
