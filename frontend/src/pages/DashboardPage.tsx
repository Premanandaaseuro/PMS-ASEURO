import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Target, Users, BarChart3, ArrowRight, CheckCircle2, Clock, Calendar, AlertTriangle } from 'lucide-react';
import { managerService } from '../services/managerService';
import type { DashboardMetricsDto } from '../types';
import { LoadingSpinner } from '../components/LoadingSpinner';
import { AlertBanner } from '../components/AlertBanner';
import { StatusBadge } from '../components/StatusBadge';

export const DashboardPage: React.FC = () => {
  const [metrics, setMetrics] = useState<DashboardMetricsDto | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const navigate = useNavigate();

  useEffect(() => {
    const fetchDashboard = async () => {
      try {
        const data = await managerService.getDashboardMetrics();
        setMetrics(data);
      } catch (err) {
        setErrorMessage(err instanceof Error ? err.message : 'Failed to load dashboard metrics.');
      } finally {
        setIsLoading(false);
      }
    };

    fetchDashboard();
  }, []);

  if (isLoading) {
    return <LoadingSpinner message="Loading Manager Dashboard..." />;
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

      {/* Active PMS Cycle Banner */}
      <div
        className="card"
        style={{
          background: 'linear-gradient(135deg, #FFFFFF 0%, var(--color-primary-light) 100%)',
          border: '1px solid var(--color-primary-border)',
          marginBottom: '2rem',
          padding: '1.75rem 2rem',
        }}
      >
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '1rem' }}>
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.6rem', color: 'var(--color-accent)', fontWeight: 700, fontSize: '0.85rem', textTransform: 'uppercase', letterSpacing: '0.05em', marginBottom: '0.35rem' }}>
              <Calendar size={16} />
              <span>Active PMS Evaluation Cycle</span>
            </div>
            <h2 style={{ fontSize: '1.6rem', fontWeight: 800, color: 'var(--color-dark)' }}>
              {metrics?.activeCycleName || 'No Active Cycle'}
            </h2>
            <p style={{ color: 'var(--color-dark-muted)', fontSize: '0.9rem', marginTop: '0.2rem' }}>
              Evaluation Period: {metrics?.activeCycleStartDate ? new Date(metrics.activeCycleStartDate).toLocaleDateString() : 'N/A'} — {metrics?.activeCycleEndDate ? new Date(metrics.activeCycleEndDate).toLocaleDateString() : 'N/A'}
            </p>
          </div>

          <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
            <button
              type="button"
              className="btn btn-secondary btn-sm"
              onClick={() => navigate('/my-kpis')}
            >
              My Self-Rating
            </button>
            <button
              type="button"
              className="btn btn-primary btn-sm"
              onClick={() => navigate('/assigned-employees')}
            >
              Review Team ({metrics?.pendingReviewsCount || 0} Pending)
            </button>
          </div>
        </div>
      </div>

      {/* Overview Stat Cards */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(260px, 1fr))', gap: '1.5rem', marginBottom: '2.5rem' }}>
        <div className="card" style={{ display: 'flex', alignItems: 'center', gap: '1.25rem' }}>
          <div style={{ width: '52px', height: '52px', borderRadius: 'var(--radius-md)', background: 'var(--color-primary-light)', color: 'var(--color-accent)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            <Users size={26} />
          </div>
          <div>
            <div style={{ fontSize: '0.82rem', fontWeight: 700, color: 'var(--color-dark-muted)', textTransform: 'uppercase' }}>
              Total Assigned
            </div>
            <div style={{ fontSize: '1.85rem', fontWeight: 800, color: 'var(--color-dark)' }}>
              {metrics?.assignedEmployeesCount || 0}
            </div>
            <div style={{ fontSize: '0.78rem', color: 'var(--color-accent)', fontWeight: 600 }}>
              Reporting Employees
            </div>
          </div>
        </div>

        <div className="card" style={{ display: 'flex', alignItems: 'center', gap: '1.25rem' }}>
          <div style={{ width: '52px', height: '52px', borderRadius: 'var(--radius-md)', background: '#FFF8E6', color: '#B45309', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            <Clock size={26} />
          </div>
          <div>
            <div style={{ fontSize: '0.82rem', fontWeight: 700, color: 'var(--color-dark-muted)', textTransform: 'uppercase' }}>
              Pending Reviews
            </div>
            <div style={{ fontSize: '1.85rem', fontWeight: 800, color: '#B45309' }}>
              {metrics?.pendingReviewsCount || 0}
            </div>
            <div style={{ fontSize: '0.78rem', color: 'var(--color-dark-muted)', fontWeight: 500 }}>
              Awaiting Manager Rating
            </div>
          </div>
        </div>

        <div className="card" style={{ display: 'flex', alignItems: 'center', gap: '1.25rem' }}>
          <div style={{ width: '52px', height: '52px', borderRadius: 'var(--radius-md)', background: '#ECFDF5', color: '#047857', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            <CheckCircle2 size={26} />
          </div>
          <div>
            <div style={{ fontSize: '0.82rem', fontWeight: 700, color: 'var(--color-dark-muted)', textTransform: 'uppercase' }}>
              Completed Reviews
            </div>
            <div style={{ fontSize: '1.85rem', fontWeight: 800, color: '#047857' }}>
              {metrics?.completedReviewsCount || 0}
            </div>
            <div style={{ fontSize: '0.78rem', color: 'var(--color-dark-muted)', fontWeight: 500 }}>
              Submitted to HR
            </div>
          </div>
        </div>

        <div className="card" style={{ display: 'flex', alignItems: 'center', gap: '1.25rem' }}>
          <div style={{ width: '52px', height: '52px', borderRadius: 'var(--radius-md)', background: 'var(--bg-subtle)', color: 'var(--color-dark)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            <Target size={26} />
          </div>
          <div>
            <div style={{ fontSize: '0.82rem', fontWeight: 700, color: 'var(--color-dark-muted)', textTransform: 'uppercase' }}>
              My Self-Assessment
            </div>
            <div style={{ marginTop: '0.35rem' }}>
              <StatusBadge status={metrics?.selfPmsStatus || 'NOT_STARTED'} />
            </div>
            <div style={{ fontSize: '0.78rem', color: 'var(--color-dark-muted)', marginTop: '0.3rem' }}>
              {metrics?.selfPmsSubmitted ? 'Rating Locked & Submitted' : 'Pending Submission'}
            </div>
          </div>
        </div>
      </div>

      {/* Main Module Action Cards */}
      <h3 style={{ fontSize: '1.2rem', fontWeight: 700, marginBottom: '1.25rem', color: 'var(--color-dark)' }}>
        Manager Actions & Workflows
      </h3>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))', gap: '1.75rem' }}>
        {/* Card 1: View My KPIs */}
        <div className="card" style={{ display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
          <div>
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '1rem' }}>
              <div style={{ display: 'inline-flex', padding: '0.75rem', background: 'var(--color-primary-light)', borderRadius: 'var(--radius-md)', color: 'var(--color-accent)' }}>
                <Target size={24} />
              </div>
              <StatusBadge status={metrics?.selfPmsStatus || 'PMS_NOT_STARTED'} />
            </div>
            <h4 style={{ fontSize: '1.15rem', fontWeight: 700, marginBottom: '0.4rem' }}>View My KPIs</h4>
            <p style={{ color: 'var(--color-dark-muted)', fontSize: '0.9rem', lineHeight: 1.5, marginBottom: '1.5rem' }}>
              Rate your own key performance indicators for the active cycle, save drafts, add commentary, and submit self-assessment to your reporting manager.
            </p>
          </div>
          <button
            type="button"
            className="btn btn-primary"
            style={{ width: '100%', justifyContent: 'space-between' }}
            onClick={() => navigate('/my-kpis')}
          >
            <span>{metrics?.selfPmsSubmitted ? 'View My Self-Rating (Locked)' : 'Enter Self-Rating'}</span>
            <ArrowRight size={18} />
          </button>
        </div>

        {/* Card 2: View Assigned Employees */}
        <div className="card" style={{ display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
          <div>
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '1rem' }}>
              <div style={{ display: 'inline-flex', padding: '0.75rem', background: '#FFF8E6', borderRadius: 'var(--radius-md)', color: '#B45309' }}>
                <Users size={24} />
              </div>
              {metrics && metrics.pendingReviewsCount > 0 ? (
                <span className="badge badge-pending">
                  <AlertTriangle size={13} />
                  <span>{metrics.pendingReviewsCount} Awaiting Review</span>
                </span>
              ) : (
                <span className="badge badge-completed">
                  <CheckCircle2 size={13} />
                  <span>All Reviews Done</span>
                </span>
              )}
            </div>
            <h4 style={{ fontSize: '1.15rem', fontWeight: 700, marginBottom: '0.4rem' }}>View Assigned Employees</h4>
            <p style={{ color: 'var(--color-dark-muted)', fontSize: '0.9rem', lineHeight: 1.5, marginBottom: '1.5rem' }}>
              Review self-ratings from team members, assign manager performance scores (1.00–5.00), provide feedback, and submit reviews for HR analysis.
            </p>
          </div>
          <button
            type="button"
            className="btn btn-accent"
            style={{ width: '100%', justifyContent: 'space-between' }}
            onClick={() => navigate('/assigned-employees')}
          >
            <span>Review Team ({metrics?.assignedEmployeesCount || 0} Total)</span>
            <ArrowRight size={18} />
          </button>
        </div>

        {/* Card 3: Reports */}
        <div className="card" style={{ display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
          <div>
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '1rem' }}>
              <div style={{ display: 'inline-flex', padding: '0.75rem', background: 'var(--bg-subtle)', borderRadius: 'var(--radius-md)', color: 'var(--color-dark)' }}>
                <BarChart3 size={24} />
              </div>
              <span className="badge badge-submitted">Analytics</span>
            </div>
            <h4 style={{ fontSize: '1.15rem', fontWeight: 700, marginBottom: '0.4rem' }}>Performance Reports</h4>
            <p style={{ color: 'var(--color-dark-muted)', fontSize: '0.9rem', lineHeight: 1.5, marginBottom: '1.5rem' }}>
              Inspect multi-cycle rating histories per reporting employee, review weightage contributions, and view monthly team submission progress.
            </p>
          </div>
          <button
            type="button"
            className="btn btn-secondary"
            style={{ width: '100%', justifyContent: 'space-between' }}
            onClick={() => navigate('/reports')}
          >
            <span>Access Team Reports</span>
            <ArrowRight size={18} />
          </button>
        </div>
      </div>
    </div>
  );
};
