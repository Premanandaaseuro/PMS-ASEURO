import React, { useState, useEffect } from 'react';
import { Target, Save, Send, Lock, History, Award, CheckCircle2 } from 'lucide-react';
import { managerService } from '../services/managerService';
import type { MyKpisResponseDto, KpiItemDto } from '../types';
import { RatingInput } from '../components/RatingInput';
import { ConfirmationModal } from '../components/ConfirmationModal';
import { StatusBadge } from '../components/StatusBadge';
import { LoadingSpinner } from '../components/LoadingSpinner';
import { AlertBanner } from '../components/AlertBanner';
import { EmptyState } from '../components/EmptyState';
import { formatScore, hasValidScore } from '../utils/format';

export const MyKpisPage: React.FC = () => {
  const [activeTab, setActiveTab] = useState<'current' | 'history'>('current');
  const [pmsData, setPmsData] = useState<MyKpisResponseDto | null>(null);
  const [historyList, setHistoryList] = useState<MyKpisResponseDto[]>([]);
  const [selectedHistoryItem, setSelectedHistoryItem] = useState<MyKpisResponseDto | null>(null);

  const [ratingsState, setRatingsState] = useState<Record<number, { rating?: number; comments?: string }>>({});
  const [generalComments, setGeneralComments] = useState('');

  const [isLoading, setIsLoading] = useState(true);
  const [isSaving, setIsSaving] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [showConfirmModal, setShowConfirmModal] = useState(false);

  const [alert, setAlert] = useState<{ type: 'success' | 'error' | 'warning' | 'info'; message: string } | null>(null);

  const loadActiveKpis = async () => {
    setIsLoading(true);
    try {
      const data = await managerService.getMyActiveKpis();
      setPmsData(data);
      setGeneralComments(data.generalComments || '');

      const initialRatings: Record<number, { rating?: number; comments?: string }> = {};
      data.kpis.forEach((k: KpiItemDto) => {
        initialRatings[k.pmsKpiId] = {
          rating: k.selfRating,
          comments: k.selfComments || '',
        };
      });
      setRatingsState(initialRatings);
    } catch (err) {
      setAlert({
        type: 'error',
        message: err instanceof Error ? err.message : 'Failed to load active KPIs.',
      });
    } finally {
      setIsLoading(false);
    }
  };

  const loadHistory = async () => {
    try {
      const history = await managerService.getMyKpisHistory();
      setHistoryList(history);
      if (history.length > 0 && !selectedHistoryItem) {
        setSelectedHistoryItem(history[0]);
      }
    } catch (err) {
      console.error('Failed to load history:', err);
    }
  };

  useEffect(() => {
    loadActiveKpis();
    loadHistory();
  }, []);

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
    if (!pmsData) return { ratings: [], generalComments: '' };
    const ratings = pmsData.kpis.map((k: KpiItemDto) => ({
      pmsKpiId: k.pmsKpiId,
      rating: ratingsState[k.pmsKpiId]?.rating || 0,
      comments: ratingsState[k.pmsKpiId]?.comments || '',
    }));
    return { ratings, generalComments };
  };

  const handleSaveDraft = async () => {
    if (!pmsData) return;
    setIsSaving(true);
    setAlert(null);
    try {
      const updated = await managerService.saveDraftSelfRating(pmsData.assignmentId, buildPayload());
      setPmsData(updated);
      setAlert({ type: 'success', message: 'Self-assessment draft saved successfully!' });
    } catch (err) {
      setAlert({
        type: 'error',
        message: err instanceof Error ? err.message : 'Failed to save draft.',
      });
    } finally {
      setIsSaving(false);
    }
  };

  const handleConfirmSubmit = async () => {
    if (!pmsData) return;

    // Validate that all KPIs have a rating between 1 and 5
    for (const k of pmsData.kpis) {
      const r = ratingsState[k.pmsKpiId]?.rating;
      if (r === undefined || r < 1 || r > 5) {
        setAlert({
          type: 'warning',
          message: `Please provide a rating between 1.00 and 5.00 for KPI: "${k.kpiName}".`,
        });
        setShowConfirmModal(false);
        return;
      }
    }

    setIsSubmitting(true);
    setAlert(null);
    try {
      const updated = await managerService.submitSelfRating(pmsData.assignmentId, buildPayload());
      setPmsData(updated);
      setShowConfirmModal(false);
      setAlert({
        type: 'success',
        message: 'Self-assessment submitted successfully! Your ratings are now locked.',
      });
    } catch (err) {
      setAlert({
        type: 'error',
        message: err instanceof Error ? err.message : 'Failed to submit self-assessment.',
      });
      setShowConfirmModal(false);
    } finally {
      setIsSubmitting(false);
    }
  };

  if (isLoading) {
    return <LoadingSpinner message="Loading your assigned KPIs..." />;
  }

  const isEditable = pmsData?.editable && !pmsData?.submitted;

  return (
    <div>
      {/* Tabs */}
      <div style={{ display: 'flex', gap: '0.75rem', marginBottom: '1.75rem', borderBottom: '1px solid var(--border-subtle)', paddingBottom: '0.5rem' }}>
        <button
          type="button"
          className={`btn ${activeTab === 'current' ? 'btn-primary' : 'btn-secondary'} btn-sm`}
          onClick={() => setActiveTab('current')}
        >
          <Target size={16} />
          <span>Active Self-Assessment</span>
        </button>
        <button
          type="button"
          className={`btn ${activeTab === 'history' ? 'btn-primary' : 'btn-secondary'} btn-sm`}
          onClick={() => setActiveTab('history')}
        >
          <History size={16} />
          <span>PMS History & Final Results ({historyList.length})</span>
        </button>
      </div>

      {alert && (
        <AlertBanner
          type={alert.type}
          message={alert.message}
          onClose={() => setAlert(null)}
        />
      )}

      {activeTab === 'current' && (
        <>
          {!pmsData ? (
            <EmptyState
              title="No Active KPI Assignment"
              message="There is no active evaluation cycle assigned to your designation at this time."
            />
          ) : (
            <div>
              {/* Assignment Header Card */}
              <div className="card" style={{ marginBottom: '1.75rem' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: '1rem' }}>
                  <div>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '0.6rem', marginBottom: '0.4rem' }}>
                      <h2 style={{ fontSize: '1.4rem', fontWeight: 800 }}>{pmsData.cycleName}</h2>
                      <StatusBadge status={pmsData.status} />
                    </div>
                    <div style={{ display: 'flex', gap: '1.5rem', flexWrap: 'wrap', color: 'var(--color-dark-muted)', fontSize: '0.88rem' }}>
                      <div><strong>Designation:</strong> {pmsData.designation}</div>
                      <div><strong>Reporting Manager:</strong> {pmsData.reportingManagerName}</div>
                      {pmsData.submittedAt && (
                        <div><strong>Submitted At:</strong> {new Date(pmsData.submittedAt).toLocaleString()}</div>
                      )}
                    </div>
                  </div>

                  {!isEditable && (
                    <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', background: '#F1F5F9', padding: '0.5rem 1rem', borderRadius: 'var(--radius-md)', color: '#475569', fontWeight: 600, fontSize: '0.88rem' }}>
                      <Lock size={16} />
                      <span>Rating is Locked (Read-Only)</span>
                    </div>
                  )}
                </div>

                {/* Published Final Score Banner if available */}
                {pmsData.finalized && hasValidScore(pmsData.finalScore) && (
                  <div style={{ marginTop: '1.25rem', padding: '1rem', background: 'var(--color-primary-light)', borderRadius: 'var(--radius-md)', border: '1px solid var(--color-primary-border)', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                      <Award size={24} color="var(--color-accent)" />
                      <div>
                        <div style={{ fontWeight: 800, color: 'var(--color-accent)', fontSize: '1.1rem' }}>
                          Final Published Score: {formatScore(pmsData.finalScore)} / 5.00
                        </div>
                        <div style={{ fontSize: '0.82rem', color: 'var(--color-dark-muted)' }}>
                          Category: <strong>{pmsData.ratingCategory || 'N/A'}</strong>
                        </div>
                      </div>
                    </div>
                  </div>
                )}
              </div>

              {/* KPIs Evaluation List */}
              <div style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem', marginBottom: '2rem' }}>
                {pmsData.kpis.map((kpi: KpiItemDto, idx: number) => {
                  const currentVal = ratingsState[kpi.pmsKpiId]?.rating;
                  const currentComments = ratingsState[kpi.pmsKpiId]?.comments || '';

                  return (
                    <div key={kpi.pmsKpiId} className="card" style={{ borderLeft: '4px solid var(--color-primary)' }}>
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

                      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))', gap: '1.5rem', alignItems: 'flex-start' }}>
                        <div>
                          <RatingInput
                            id={`rating-${kpi.pmsKpiId}`}
                            label="Your Self Rating (Scale 1.00 - 5.00)"
                            value={currentVal}
                            onChange={(val) => handleRatingChange(kpi.pmsKpiId, val)}
                            disabled={!isEditable}
                            readOnly={!isEditable}
                          />
                        </div>

                        <div>
                          <label className="form-label" htmlFor={`comments-${kpi.pmsKpiId}`}>
                            Self Comments / Key Accomplishments
                          </label>
                          <textarea
                            id={`comments-${kpi.pmsKpiId}`}
                            className="form-textarea"
                            rows={2}
                            placeholder={isEditable ? 'Summarize your achievements for this KPI...' : 'No comments provided'}
                            value={currentComments}
                            onChange={(e) => handleCommentsChange(kpi.pmsKpiId, e.target.value)}
                            disabled={!isEditable}
                          />
                        </div>
                      </div>
                    </div>
                  );
                })}
              </div>

              {/* General Comments & Actions Card */}
              <div className="card" style={{ marginBottom: '2rem' }}>
                <h4 style={{ fontSize: '1.05rem', fontWeight: 700, marginBottom: '0.75rem' }}>
                  General Self-Assessment Remarks
                </h4>
                <div className="form-group">
                  <textarea
                    className="form-textarea"
                    rows={3}
                    placeholder={isEditable ? 'Provide any overall reflections, career goals, or context for this evaluation cycle...' : 'No general remarks entered.'}
                    value={generalComments}
                    onChange={(e) => setGeneralComments(e.target.value)}
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
                      <span>Submit Self-Assessment</span>
                    </button>
                  </div>
                )}
              </div>
            </div>
          )}
        </>
      )}

      {/* History Tab */}
      {activeTab === 'history' && (
        <div>
          {historyList.length === 0 ? (
            <EmptyState
              title="No Historical PMS Records"
              message="No previous evaluation cycles were found for your account."
            />
          ) : (
            <div style={{ display: 'grid', gridTemplateColumns: '320px 1fr', gap: '1.5rem', alignItems: 'start' }}>
              {/* History List Side */}
              <div className="card" style={{ padding: '1rem' }}>
                <h4 style={{ fontSize: '0.95rem', fontWeight: 700, marginBottom: '0.85rem', padding: '0 0.5rem' }}>
                  Evaluation Cycles
                </h4>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '0.4rem' }}>
                  {historyList.map((item: MyKpisResponseDto) => {
                    const isSelected = selectedHistoryItem?.assignmentId === item.assignmentId;
                    return (
                      <button
                        key={item.assignmentId}
                        type="button"
                        onClick={() => setSelectedHistoryItem(item)}
                        style={{
                          textAlign: 'left',
                          padding: '0.85rem',
                          borderRadius: 'var(--radius-md)',
                          background: isSelected ? 'var(--color-primary-light)' : 'transparent',
                          border: isSelected ? '1px solid var(--color-primary-border)' : '1px solid transparent',
                          cursor: 'pointer',
                          transition: 'all var(--transition-fast)',
                        }}
                      >
                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.2rem' }}>
                          <span style={{ fontWeight: 700, color: isSelected ? 'var(--color-accent)' : 'var(--color-dark)' }}>
                            {item.cycleName}
                          </span>
                        </div>
                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', fontSize: '0.78rem' }}>
                          <StatusBadge status={item.status} />
                          {hasValidScore(item.finalScore) && (
                            <span style={{ fontWeight: 800, color: 'var(--color-accent)' }}>
                              Score: {formatScore(item.finalScore)}
                            </span>
                          )}
                        </div>
                      </button>
                    );
                  })}
                </div>
              </div>

              {/* History Detail Side */}
              {selectedHistoryItem ? (
                <div className="card">
                  <div className="card-header">
                    <div>
                      <h3 style={{ fontSize: '1.3rem', fontWeight: 800 }}>{selectedHistoryItem.cycleName}</h3>
                      <p style={{ color: 'var(--color-dark-muted)', fontSize: '0.85rem', marginTop: '0.2rem' }}>
                        Designation: {selectedHistoryItem.designation} | Manager: {selectedHistoryItem.reportingManagerName}
                      </p>
                    </div>
                    <StatusBadge status={selectedHistoryItem.status} />
                  </div>

                  {hasValidScore(selectedHistoryItem.finalScore) && (
                    <div style={{ marginBottom: '1.5rem', padding: '1rem', background: '#ECFDF5', borderRadius: 'var(--radius-md)', display: 'flex', alignItems: 'center', gap: '1rem' }}>
                      <CheckCircle2 size={24} color="#047857" />
                      <div>
                        <div style={{ fontWeight: 800, color: '#047857', fontSize: '1.1rem' }}>
                          Final Result: {formatScore(selectedHistoryItem.finalScore)} / 5.00
                        </div>
                        <div style={{ fontSize: '0.82rem', color: '#065F46' }}>
                          Performance Category: <strong>{selectedHistoryItem.ratingCategory || 'N/A'}</strong>
                        </div>
                      </div>
                    </div>
                  )}

                  <div className="table-container" style={{ marginBottom: '1.5rem' }}>
                    <table className="data-table">
                      <thead>
                        <tr>
                          <th>KPI Name</th>
                          <th>Weightage</th>
                          <th>Self Rating</th>
                          <th>Manager Rating</th>
                          <th>Comments</th>
                        </tr>
                      </thead>
                      <tbody>
                        {selectedHistoryItem.kpis.map((kpi: KpiItemDto) => (
                          <tr key={kpi.pmsKpiId}>
                            <td style={{ fontWeight: 600 }}>{kpi.kpiName}</td>
                            <td>{kpi.weightage}%</td>
                            <td>
                              <span style={{ fontWeight: 700, color: 'var(--color-accent)' }}>
                                {formatScore(kpi.selfRating)}
                              </span>
                            </td>
                            <td>
                              <span style={{ fontWeight: 700, color: 'var(--color-dark)' }}>
                                {formatScore(kpi.managerRating)}
                              </span>
                            </td>
                            <td style={{ fontSize: '0.82rem', color: 'var(--color-dark-light)' }}>
                              {kpi.selfComments || 'No self comments'}
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>

                  {selectedHistoryItem.generalComments && (
                    <div style={{ padding: '0.85rem 1rem', background: 'var(--bg-subtle)', borderRadius: 'var(--radius-md)', fontSize: '0.88rem' }}>
                      <strong>Self Remarks:</strong> {selectedHistoryItem.generalComments}
                    </div>
                  )}
                </div>
              ) : (
                <EmptyState title="Select a Cycle" message="Select an evaluation cycle on the left to inspect detailed scores." />
              )}
            </div>
          )}
        </div>
      )}

      {/* Confirmation Modal */}
      <ConfirmationModal
        isOpen={showConfirmModal}
        title="Submit Self-Assessment"
        message="Once submitted, your rating cannot be changed. Do you want to continue?"
        confirmText="Confirm & Submit"
        cancelText="Review Again"
        onConfirm={handleConfirmSubmit}
        onCancel={() => setShowConfirmModal(false)}
        isLoading={isSubmitting}
      />
    </div>
  );
};
