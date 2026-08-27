import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeft, Save, Send, Lock, CheckCircle2, MessageSquare } from 'lucide-react';
import { managerService } from '../services/managerService';
import type { EmployeePmsReviewDetailsDto, EmployeeKpiReviewItemDto } from '../types';
import { RatingInput } from '../components/RatingInput';
import { ConfirmationModal } from '../components/ConfirmationModal';
import { StatusBadge } from '../components/StatusBadge';
import { LoadingSpinner } from '../components/LoadingSpinner';
import { AlertBanner } from '../components/AlertBanner';
import { ApiError } from '../services/api';

interface EmployeeReviewPageProps {
  employeeId?: number;
  assignmentId?: number;
  onBack?: () => void;
}

export const EmployeeReviewPage: React.FC<EmployeeReviewPageProps> = ({
  employeeId: propEmployeeId,
  assignmentId: propAssignmentId,
  onBack,
}) => {
  const params = useParams<{ employeeId: string }>();
  const navigate = useNavigate();

  const effectiveEmployeeId = propEmployeeId ? propEmployeeId.toString() : params.employeeId;

  const [reviewDetails, setReviewDetails] = useState<EmployeePmsReviewDetailsDto | null>(null);
  const [ratingsState, setRatingsState] = useState<Record<number, { rating?: number; comments?: string }>>({});
  const [managerGeneralComments, setManagerGeneralComments] = useState('');

  const [isLoading, setIsLoading] = useState(true);
  const [isSaving, setIsSaving] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [showConfirmModal, setShowConfirmModal] = useState(false);

  const [alert, setAlert] = useState<{ type: 'success' | 'error' | 'warning' | 'info'; message: string } | null>(null);

  const loadDetails = async () => {
    if (!effectiveEmployeeId) return;
    setIsLoading(true);
    try {
      let data: EmployeePmsReviewDetailsDto;
      if (propAssignmentId) {
        data = await managerService.getEmployeePmsDetailsByAssignmentId(
          parseInt(effectiveEmployeeId, 10),
          propAssignmentId
        );
      } else {
        data = await managerService.getEmployeePmsDetails(parseInt(effectiveEmployeeId, 10));
      }
      setReviewDetails(data);
      setManagerGeneralComments(data.managerGeneralComments || '');

      const initialRatings: Record<number, { rating?: number; comments?: string }> = {};
      data.kpis.forEach((k: EmployeeKpiReviewItemDto) => {
        initialRatings[k.pmsKpiId] = {
          rating: k.managerRating,
          comments: k.managerComments || '',
        };
      });
      setRatingsState(initialRatings);
    } catch (err) {
      if (err instanceof ApiError && err.status === 403) {
        setAlert({
          type: 'error',
          message: 'Access Denied: This employee is not assigned to you for evaluation.',
        });
      } else {
        setAlert({
          type: 'error',
          message: err instanceof Error ? err.message : 'Failed to load employee PMS details.',
        });
      }
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadDetails();
  }, [effectiveEmployeeId, propAssignmentId]);

  const handleRatingChange = (pmsKpiId: number, rating: number) => {
    setRatingsState((prev) => ({
      ...prev,
      [pmsKpiId]: {
        ...prev[pmsKpiId],
        rating,
      },
    }));
  };

  const handleCommentsChange = (pmsKpiId: number, comments: string) => {
    setRatingsState((prev) => ({
      ...prev,
      [pmsKpiId]: {
        ...prev[pmsKpiId],
        comments,
      },
    }));
  };

  const buildPayload = () => {
    if (!reviewDetails) return { ratings: [], generalComments: '' };
    const ratings = reviewDetails.kpis.map((k: EmployeeKpiReviewItemDto) => ({
      pmsKpiId: k.pmsKpiId,
      rating: ratingsState[k.pmsKpiId]?.rating || 0,
      comments: ratingsState[k.pmsKpiId]?.comments || '',
    }));
    return { ratings, generalComments: managerGeneralComments };
  };

  const handleSaveDraft = async () => {
    if (!reviewDetails || !effectiveEmployeeId) return;
    setIsSaving(true);
    setAlert(null);
    try {
      const updated = await managerService.saveDraftManagerRating(
        parseInt(effectiveEmployeeId, 10),
        reviewDetails.assignmentId,
        buildPayload()
      );
      setReviewDetails(updated);
      setAlert({ type: 'success', message: 'Manager evaluation draft saved successfully!' });
    } catch (err) {
      setAlert({
        type: 'error',
        message: err instanceof Error ? err.message : 'Failed to save manager draft.',
      });
    } finally {
      setIsSaving(false);
    }
  };

  const handleConfirmSubmit = async () => {
    if (!reviewDetails || !effectiveEmployeeId) return;

    // Validate that all KPIs have a manager rating between 1 and 5
    for (const k of reviewDetails.kpis) {
      const r = ratingsState[k.pmsKpiId]?.rating;
      if (r === undefined || r < 1 || r > 5) {
        setAlert({
          type: 'warning',
          message: `Please provide a valid manager rating (1.00 - 5.00) for KPI: "${k.kpiName}".`,
        });
        setShowConfirmModal(false);
        return;
      }
    }

    setIsSubmitting(true);
    setAlert(null);
    try {
      const updated = await managerService.submitManagerRating(
        parseInt(effectiveEmployeeId, 10),
        reviewDetails.assignmentId,
        buildPayload()
      );
      setReviewDetails(updated);
      setShowConfirmModal(false);
      setAlert({
        type: 'success',
        message: 'Manager evaluation submitted successfully! Ratings have been locked.',
      });
    } catch (err) {
      setAlert({
        type: 'error',
        message: err instanceof Error ? err.message : 'Failed to submit manager evaluation.',
      });
      setShowConfirmModal(false);
    } finally {
      setIsSubmitting(false);
    }
  };

  if (isLoading) {
    return <LoadingSpinner message="Loading employee evaluation form..." />;
  }

  if (!reviewDetails) {
    return (
      <div>
        {alert && <AlertBanner type={alert.type} message={alert.message} />}
        <button
          type="button"
          className="btn btn-secondary btn-sm"
          onClick={onBack ? onBack : () => navigate('/assigned-employees')}
        >
          <ArrowLeft size={16} />
          <span>Back to Assigned Employees</span>
        </button>
      </div>
    );
  }

  const isEditable = reviewDetails.editableByManager;

  return (
    <div>
      {/* Back Button & Header */}
      <div style={{ display: 'flex', alignItems: 'center', gap: '0.85rem', marginBottom: '1.5rem' }}>
        <button
          type="button"
          className="btn btn-secondary btn-sm"
          onClick={onBack ? onBack : () => navigate('/assigned-employees')}
        >
          <ArrowLeft size={16} />
          <span>Back to Team Roster</span>
        </button>
      </div>

      {alert && (
        <AlertBanner
          type={alert.type}
          message={alert.message}
          onClose={() => setAlert(null)}
        />
      )}

      {/* Employee & Assignment Header Card */}
      <div className="card" style={{ marginBottom: '1.5rem' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: '1rem' }}>
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.65rem' }}>
              <h2 style={{ margin: 0, fontSize: '1.4rem', fontWeight: 800 }}>
                {reviewDetails.employee.fullName}
              </h2>
              <span className="code-pill">{reviewDetails.employee.employeeCode}</span>
            </div>
            <p style={{ color: 'var(--color-dark-light)', fontSize: '0.88rem', margin: '0.35rem 0 0 0' }}>
              {reviewDetails.employee.designation} &bull; {reviewDetails.employee.team} ({reviewDetails.employee.department})
            </p>
          </div>
          <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-end', gap: '0.4rem' }}>
            <StatusBadge status={reviewDetails.status} />
            <div style={{ fontSize: '0.78rem', color: 'var(--color-dark-muted)' }}>
              {reviewDetails.cycleName}
            </div>
          </div>
        </div>

        {reviewDetails.employeeGeneralComments && (
          <div style={{ marginTop: '1.25rem', padding: '0.85rem 1rem', background: 'var(--bg-subtle)', borderRadius: 'var(--radius-sm)', border: '1px solid var(--border-subtle)' }}>
            <div style={{ fontWeight: 700, color: 'var(--color-accent)', display: 'flex', alignItems: 'center', gap: '0.4rem', marginBottom: '0.2rem' }}>
              <MessageSquare size={14} />
              <span>Employee's Overall Self Remarks:</span>
            </div>
            <p style={{ color: 'var(--color-dark)' }}>{reviewDetails.employeeGeneralComments}</p>
          </div>
        )}
      </div>

      {/* KPI Review Cards */}
      <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem', marginBottom: '2rem' }}>
        {reviewDetails.kpis.map((kpi: EmployeeKpiReviewItemDto, idx: number) => {
          const currentMgrVal = ratingsState[kpi.pmsKpiId]?.rating;
          const currentMgrComments = ratingsState[kpi.pmsKpiId]?.comments || '';

          return (
            <div key={kpi.pmsKpiId} className="card" style={{ borderLeft: '4px solid var(--color-accent)' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '0.85rem' }}>
                <div>
                  <div style={{ fontSize: '0.78rem', fontWeight: 700, color: 'var(--color-accent)', textTransform: 'uppercase', letterSpacing: '0.04em' }}>
                    KPI #{idx + 1}
                  </div>
                  <h3 style={{ fontSize: '1.15rem', fontWeight: 700, marginTop: '0.15rem' }}>{kpi.kpiName}</h3>
                </div>
                <div style={{ background: 'var(--color-primary-light)', padding: '0.3rem 0.75rem', borderRadius: 'var(--radius-full)', fontWeight: 700, fontSize: '0.82rem', color: 'var(--color-accent)', border: '1px solid var(--color-primary-border)' }}>
                  Weightage: {kpi.weightage}%
                </div>
              </div>

              <div style={{ marginBottom: '1.25rem', padding: '0.75rem 1rem', background: 'var(--bg-subtle)', borderRadius: 'var(--radius-md)', fontSize: '0.88rem' }}>
                <strong style={{ color: 'var(--color-dark)' }}>Measurement Criteria:</strong>
                <p style={{ color: 'var(--color-dark-light)', marginTop: '0.2rem' }}>{kpi.measurementCriteria}</p>
              </div>

              {/* Side-by-Side: Employee Self-Rating (Read-Only) vs Manager Rating (Editable) */}
              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))', gap: '1.5rem', background: '#FAFCF9', padding: '1.25rem', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-subtle)' }}>
                {/* Left Column: Read-Only Employee Self Assessment */}
                <div style={{ borderRight: '1px solid var(--border-subtle)', paddingRight: '1rem' }}>
                  <div style={{ fontSize: '0.82rem', fontWeight: 700, color: 'var(--color-dark-muted)', textTransform: 'uppercase', marginBottom: '0.75rem', display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
                    <Lock size={13} />
                    <span>Employee Self Assessment (Read-Only)</span>
                  </div>

                  <div style={{ marginBottom: '0.75rem' }}>
                    <RatingInput
                      value={kpi.selfRating}
                      readOnly={true}
                      disabled={true}
                      label="Employee Self Rating"
                    />
                  </div>

                  <div>
                    <label className="form-label" style={{ fontSize: '0.8rem', color: 'var(--color-dark-muted)' }}>
                      Employee Comments / Notes
                    </label>
                    <div style={{ padding: '0.6rem 0.85rem', background: '#FFFFFF', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-strong)', fontSize: '0.85rem', color: kpi.employeeComments ? 'var(--color-dark)' : 'var(--color-dark-muted)', minHeight: '56px' }}>
                      {kpi.employeeComments || 'No employee self-comments provided.'}
                    </div>
                  </div>
                </div>

                {/* Right Column: Manager Evaluation (Editable) */}
                <div style={{ paddingLeft: '0.5rem' }}>
                  <div style={{ fontSize: '0.82rem', fontWeight: 700, color: 'var(--color-accent)', textTransform: 'uppercase', marginBottom: '0.75rem', display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
                    <CheckCircle2 size={14} />
                    <span>Manager Performance Evaluation</span>
                  </div>

                  <div style={{ marginBottom: '0.75rem' }}>
                    <RatingInput
                      id={`mgr-rating-${kpi.pmsKpiId}`}
                      label="Manager Score (Scale 1.00 - 5.00)"
                      value={currentMgrVal}
                      onChange={(val) => handleRatingChange(kpi.pmsKpiId, val)}
                      disabled={!isEditable}
                      readOnly={!isEditable}
                    />
                  </div>

                  <div>
                    <label className="form-label" htmlFor={`mgr-comments-${kpi.pmsKpiId}`} style={{ fontSize: '0.8rem' }}>
                      Manager Feedback & Justification
                    </label>
                    <textarea
                      id={`mgr-comments-${kpi.pmsKpiId}`}
                      className="form-textarea"
                      rows={2}
                      placeholder={isEditable ? 'Enter your evaluation notes and constructive feedback...' : 'No manager notes.'}
                      value={currentMgrComments}
                      onChange={(e) => handleCommentsChange(kpi.pmsKpiId, e.target.value)}
                      disabled={!isEditable}
                    />
                  </div>
                </div>
              </div>
            </div>
          );
        })}
      </div>

      {/* Overall Manager Feedback & Action Buttons */}
      <div className="card" style={{ marginBottom: '2rem' }}>
        <h4 style={{ fontSize: '1.05rem', fontWeight: 700, marginBottom: '0.75rem' }}>
          Overall Manager Evaluation Remarks
        </h4>
        <div className="form-group">
          <textarea
            className="form-textarea"
            rows={3}
            placeholder={isEditable ? 'Summarize overall performance, strengths, growth areas, and recommendations...' : 'No overall manager remarks.'}
            value={managerGeneralComments}
            onChange={(e) => setManagerGeneralComments(e.target.value)}
            disabled={!isEditable}
          />
        </div>

        {isEditable && (
          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '1rem', marginTop: '1.5rem', paddingTop: '1.25rem', borderTop: '1px solid var(--border-subtle)' }}>
            <button
              type="button"
              className="btn btn-secondary"
              onClick={handleSaveDraft}
              disabled={isSaving || isSubmitting}
            >
              <Save size={16} />
              <span>{isSaving ? 'Saving Draft...' : 'Save Draft'}</span>
            </button>
            <button
              type="button"
              className="btn btn-primary"
              onClick={() => setShowConfirmModal(true)}
              disabled={isSaving || isSubmitting}
            >
              <Send size={16} />
              <span>Submit Manager Review</span>
            </button>
          </div>
        )}
      </div>

      {/* Confirmation Modal */}
      <ConfirmationModal
        isOpen={showConfirmModal}
        title="Submit Manager Evaluation"
        message="Once submitted, the manager rating cannot be changed. Do you want to continue?"
        confirmText="Confirm & Submit Review"
        cancelText="Review Again"
        onConfirm={handleConfirmSubmit}
        onCancel={() => setShowConfirmModal(false)}
        isLoading={isSubmitting}
      />
    </div>
  );
};
