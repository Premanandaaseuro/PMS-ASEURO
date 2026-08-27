import React, { useState, useEffect } from 'react';
import aseuroLogo from '../assets/aseuro-logo.png';
import type { AuthUser } from '../types';
import { MyKpisPage } from '../pages/MyKpisPage';
import { AssignedEmployeesPage } from '../pages/AssignedEmployeesPage';
import { EmployeeReviewPage } from '../pages/EmployeeReviewPage';
import { ReportsPage } from '../pages/ReportsPage';
import { managerApi } from '../services/managerService';
import type { DashboardMetricsDto } from '../types';
import {
  LayoutDashboard,
  Target,
  Users,
  BarChart3,
  LogOut,
  Calendar,
  Clock,
  CheckCircle2,
  Award,
  ArrowRight
} from 'lucide-react';

interface ManagerDashboardProps {
  user: AuthUser;
  onLogout: () => void;
}

type TabType = 'overview' | 'my-kpis' | 'assigned-employees' | 'reports';

export const ManagerDashboard: React.FC<ManagerDashboardProps> = ({ user, onLogout }) => {
  const [activeTab, setActiveTab] = useState<TabType>('overview');
  const [selectedReview, setSelectedReview] = useState<{ employeeId: number; assignmentId: number } | null>(null);
  const [metrics, setMetrics] = useState<DashboardMetricsDto | null>(null);
  const [loading, setLoading] = useState(true);

  // Sync token to localStorage if not present
  useEffect(() => {
    if (user.token) {
      localStorage.setItem('pms_token', user.token);
      localStorage.setItem('pms_jwt_token', user.token);
    }
  }, [user.token]);

  const loadMetrics = async () => {
    try {
      setLoading(true);
      const data = await managerApi.getDashboardMetrics();
      setMetrics(data);
    } catch (err) {
      console.error('Failed to load dashboard metrics:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadMetrics();
  }, []);

  const handleStartReview = (employeeId: number, assignmentId: number) => {
    setSelectedReview({ employeeId, assignmentId });
    setActiveTab('assigned-employees');
  };

  const handleBackToAssignedList = () => {
    setSelectedReview(null);
    loadMetrics();
  };

  return (
    <div className="dashboard-layout" style={{ minHeight: '100vh', display: 'flex', flexDirection: 'column' }}>
      {/* Top Navigation Bar */}
      <header className="dashboard-navbar" style={{
        background: '#3A3A3A',
        color: '#FFFFFF',
        padding: '0.75rem 2rem',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        boxShadow: '0 2px 8px rgba(0,0,0,0.15)',
        position: 'sticky',
        top: 0,
        zIndex: 100
      }}>
        <div className="nav-brand" style={{ display: 'flex', alignItems: 'center', gap: '0.85rem' }}>
          <img src={aseuroLogo} alt="Aseuro Logo" style={{ width: '32px', height: '32px', objectFit: 'contain' }} />
          <div style={{ display: 'flex', flexDirection: 'column' }}>
            <span style={{ fontSize: '1.25rem', fontWeight: 800, letterSpacing: '-0.02em', color: '#6FC04A' }}>aseuro</span>
            <span style={{ fontSize: '0.7rem', textTransform: 'uppercase', letterSpacing: '0.08em', color: '#d4ebd0' }}>PMS Manager Module</span>
          </div>
          <span style={{
            background: 'rgba(111, 192, 74, 0.2)',
            color: '#6FC04A',
            border: '1px solid rgba(111, 192, 74, 0.4)',
            padding: '0.2rem 0.6rem',
            borderRadius: '9999px',
            fontSize: '0.75rem',
            fontWeight: 700,
            marginLeft: '0.5rem'
          }}>
            MANAGER
          </span>
        </div>

        {/* Tab Navigation Menu */}
        <nav style={{ display: 'flex', gap: '0.5rem' }}>
          <button
            onClick={() => { setActiveTab('overview'); setSelectedReview(null); }}
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '0.5rem',
              padding: '0.5rem 1rem',
              borderRadius: '8px',
              border: 'none',
              background: activeTab === 'overview' ? '#6FC04A' : 'transparent',
              color: activeTab === 'overview' ? '#FFFFFF' : '#D1D5DB',
              fontWeight: activeTab === 'overview' ? 700 : 500,
              cursor: 'pointer',
              transition: 'all 0.2s ease'
            }}
          >
            <LayoutDashboard size={18} />
            Overview
          </button>

          <button
            onClick={() => { setActiveTab('my-kpis'); setSelectedReview(null); }}
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '0.5rem',
              padding: '0.5rem 1rem',
              borderRadius: '8px',
              border: 'none',
              background: activeTab === 'my-kpis' ? '#6FC04A' : 'transparent',
              color: activeTab === 'my-kpis' ? '#FFFFFF' : '#D1D5DB',
              fontWeight: activeTab === 'my-kpis' ? 700 : 500,
              cursor: 'pointer',
              transition: 'all 0.2s ease'
            }}
          >
            <Target size={18} />
            My KPIs
          </button>

          <button
            onClick={() => { setActiveTab('assigned-employees'); }}
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '0.5rem',
              padding: '0.5rem 1rem',
              borderRadius: '8px',
              border: 'none',
              background: activeTab === 'assigned-employees' ? '#6FC04A' : 'transparent',
              color: activeTab === 'assigned-employees' ? '#FFFFFF' : '#D1D5DB',
              fontWeight: activeTab === 'assigned-employees' ? 700 : 500,
              cursor: 'pointer',
              transition: 'all 0.2s ease'
            }}
          >
            <Users size={18} />
            Assigned Employees
          </button>

          <button
            onClick={() => { setActiveTab('reports'); setSelectedReview(null); }}
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '0.5rem',
              padding: '0.5rem 1rem',
              borderRadius: '8px',
              border: 'none',
              background: activeTab === 'reports' ? '#6FC04A' : 'transparent',
              color: activeTab === 'reports' ? '#FFFFFF' : '#D1D5DB',
              fontWeight: activeTab === 'reports' ? 700 : 500,
              cursor: 'pointer',
              transition: 'all 0.2s ease'
            }}
          >
            <BarChart3 size={18} />
            Reports
          </button>
        </nav>

        {/* User Info & Logout */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '1.25rem' }}>
          <div style={{ textAlign: 'right' }}>
            <div style={{ fontWeight: 600, fontSize: '0.9rem', color: '#FFFFFF' }}>{user.fullName}</div>
            <div style={{ fontSize: '0.75rem', color: '#9CA3AF' }}>{user.email}</div>
          </div>
          <button
            onClick={onLogout}
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '0.4rem',
              padding: '0.45rem 0.9rem',
              background: 'rgba(239, 68, 68, 0.15)',
              color: '#FCA5A5',
              border: '1px solid rgba(239, 68, 68, 0.3)',
              borderRadius: '6px',
              fontWeight: 600,
              fontSize: '0.85rem',
              cursor: 'pointer',
              transition: 'all 0.2s ease'
            }}
          >
            <LogOut size={16} />
            Logout
          </button>
        </div>
      </header>

      {/* Main Content Area */}
      <main style={{ flex: 1, padding: '2rem', maxWidth: '1400px', margin: '0 auto', width: '100%' }}>
        {activeTab === 'overview' && (
          <div>
            {/* Welcome Banner */}
            <div style={{
              background: 'linear-gradient(135deg, #3A3A3A 0%, #4A7637 100%)',
              color: '#FFFFFF',
              borderRadius: '16px',
              padding: '2rem',
              marginBottom: '2rem',
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'center',
              boxShadow: '0 8px 24px rgba(0,0,0,0.1)'
            }}>
              <div>
                <h1 style={{ fontSize: '1.85rem', fontWeight: 800, margin: '0 0 0.5rem 0', letterSpacing: '-0.02em' }}>
                  Welcome back, {user.fullName}!
                </h1>
                <p style={{ color: '#E2E8DF', fontSize: '1rem', margin: 0, maxWidth: '650px', lineHeight: 1.5 }}>
                  Enterprise Performance Management Hub — Manage your direct team appraisals, submit your own self-assessment, and track multi-cycle performance analytics.
                </p>
              </div>
              <div style={{
                background: 'rgba(255, 255, 255, 0.1)',
                backdropFilter: 'blur(8px)',
                padding: '1rem 1.5rem',
                borderRadius: '12px',
                border: '1px solid rgba(255, 255, 255, 0.2)',
                textAlign: 'right'
              }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: '#6FC04A', fontWeight: 700, fontSize: '0.85rem', textTransform: 'uppercase' }}>
                  <Calendar size={16} />
                  Active PMS Cycle
                </div>
                <div style={{ fontSize: '1.25rem', fontWeight: 700, marginTop: '0.25rem' }}>
                  {metrics?.activeCycleName || 'August 2026 Cycle'}
                </div>
              </div>
            </div>

            {/* Metrics Statistics Row */}
            <div style={{
              display: 'grid',
              gridTemplateColumns: 'repeat(auto-fit, minmax(260px, 1fr))',
              gap: '1.5rem',
              marginBottom: '2.5rem'
            }}>
              {/* Stat 1 */}
              <div style={{
                background: '#FFFFFF',
                borderRadius: '12px',
                padding: '1.5rem',
                border: '1px solid #E2E8DF',
                boxShadow: '0 2px 8px rgba(0,0,0,0.04)',
                display: 'flex',
                alignItems: 'center',
                gap: '1.25rem'
              }}>
                <div style={{ background: '#f0f9ec', color: '#6FC04A', padding: '1rem', borderRadius: '12px' }}>
                  <Users size={28} />
                </div>
                <div>
                  <div style={{ fontSize: '0.85rem', color: '#737373', fontWeight: 600, textTransform: 'uppercase' }}>Assigned Team</div>
                  <div style={{ fontSize: '2rem', fontWeight: 800, color: '#3A3A3A', lineHeight: 1.1 }}>
                    {loading ? '...' : (metrics?.assignedEmployeesCount ?? 0)}
                  </div>
                  <div style={{ fontSize: '0.8rem', color: '#5ea83d', marginTop: '0.25rem', fontWeight: 600 }}>Direct Reportees</div>
                </div>
              </div>

              {/* Stat 2 */}
              <div style={{
                background: '#FFFFFF',
                borderRadius: '12px',
                padding: '1.5rem',
                border: '1px solid #E2E8DF',
                boxShadow: '0 2px 8px rgba(0,0,0,0.04)',
                display: 'flex',
                alignItems: 'center',
                gap: '1.25rem'
              }}>
                <div style={{ background: '#FFF8E6', color: '#B45309', padding: '1rem', borderRadius: '12px' }}>
                  <Clock size={28} />
                </div>
                <div>
                  <div style={{ fontSize: '0.85rem', color: '#737373', fontWeight: 600, textTransform: 'uppercase' }}>Pending Reviews</div>
                  <div style={{ fontSize: '2rem', fontWeight: 800, color: '#3A3A3A', lineHeight: 1.1 }}>
                    {loading ? '...' : (metrics?.pendingReviewsCount ?? 0)}
                  </div>
                  <div style={{ fontSize: '0.8rem', color: '#B45309', marginTop: '0.25rem', fontWeight: 600 }}>Awaiting Scoring</div>
                </div>
              </div>

              {/* Stat 3 */}
              <div style={{
                background: '#FFFFFF',
                borderRadius: '12px',
                padding: '1.5rem',
                border: '1px solid #E2E8DF',
                boxShadow: '0 2px 8px rgba(0,0,0,0.04)',
                display: 'flex',
                alignItems: 'center',
                gap: '1.25rem'
              }}>
                <div style={{ background: '#ECFDF5', color: '#047857', padding: '1rem', borderRadius: '12px' }}>
                  <CheckCircle2 size={28} />
                </div>
                <div>
                  <div style={{ fontSize: '0.85rem', color: '#737373', fontWeight: 600, textTransform: 'uppercase' }}>Completed Reviews</div>
                  <div style={{ fontSize: '2rem', fontWeight: 800, color: '#3A3A3A', lineHeight: 1.1 }}>
                    {loading ? '...' : (metrics?.completedReviewsCount ?? 0)}
                  </div>
                  <div style={{ fontSize: '0.8rem', color: '#047857', marginTop: '0.25rem', fontWeight: 600 }}>Submitted to HR</div>
                </div>
              </div>

              {/* Stat 4 */}
              <div style={{
                background: '#FFFFFF',
                borderRadius: '12px',
                padding: '1.5rem',
                border: '1px solid #E2E8DF',
                boxShadow: '0 2px 8px rgba(0,0,0,0.04)',
                display: 'flex',
                alignItems: 'center',
                gap: '1.25rem'
              }}>
                <div style={{ background: '#F1F5F9', color: '#475569', padding: '1rem', borderRadius: '12px' }}>
                  <Award size={28} />
                </div>
                <div>
                  <div style={{ fontSize: '0.85rem', color: '#737373', fontWeight: 600, textTransform: 'uppercase' }}>My Self-Assessment</div>
                  <div style={{ fontSize: '1.15rem', fontWeight: 800, color: '#3A3A3A', lineHeight: 1.2, marginTop: '0.2rem' }}>
                    {metrics?.selfPmsSubmitted ? 'Submitted' : 'Pending Submission'}
                  </div>
                  <div style={{ fontSize: '0.8rem', color: metrics?.selfPmsSubmitted ? '#047857' : '#B45309', marginTop: '0.25rem', fontWeight: 600 }}>
                    {metrics?.selfPmsStatus || 'Active Cycle'}
                  </div>
                </div>
              </div>
            </div>

            {/* Quick Action Navigation Cards */}
            <h2 style={{ fontSize: '1.35rem', fontWeight: 700, color: '#3A3A3A', marginBottom: '1rem' }}>
              Manager Quick Actions
            </h2>

            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))', gap: '1.5rem' }}>
              {/* Card 1 */}
              <div
                onClick={() => setActiveTab('assigned-employees')}
                style={{
                  background: '#FFFFFF',
                  borderRadius: '12px',
                  padding: '1.75rem',
                  border: '1px solid #E2E8DF',
                  cursor: 'pointer',
                  transition: 'all 0.2s ease',
                  boxShadow: '0 2px 8px rgba(0,0,0,0.04)'
                }}
                onMouseEnter={(e) => (e.currentTarget.style.transform = 'translateY(-3px)')}
                onMouseLeave={(e) => (e.currentTarget.style.transform = 'translateY(0)')}
              >
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                  <div style={{ background: '#f0f9ec', color: '#6FC04A', padding: '0.75rem', borderRadius: '10px' }}>
                    <Users size={24} />
                  </div>
                  <ArrowRight size={20} color="#6FC04A" />
                </div>
                <h3 style={{ fontSize: '1.15rem', fontWeight: 700, color: '#3A3A3A', marginTop: '1.25rem', marginBottom: '0.5rem' }}>
                  Review Assigned Employees
                </h3>
                <p style={{ color: '#737373', fontSize: '0.9rem', lineHeight: 1.5, margin: 0 }}>
                  Evaluate self-ratings, score KPIs with weighted 1.0–5.0 marks, provide qualitative feedback, and submit evaluations.
                </p>
              </div>

              {/* Card 2 */}
              <div
                onClick={() => setActiveTab('my-kpis')}
                style={{
                  background: '#FFFFFF',
                  borderRadius: '12px',
                  padding: '1.75rem',
                  border: '1px solid #E2E8DF',
                  cursor: 'pointer',
                  transition: 'all 0.2s ease',
                  boxShadow: '0 2px 8px rgba(0,0,0,0.04)'
                }}
                onMouseEnter={(e) => (e.currentTarget.style.transform = 'translateY(-3px)')}
                onMouseLeave={(e) => (e.currentTarget.style.transform = 'translateY(0)')}
              >
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                  <div style={{ background: '#FFF8E6', color: '#B45309', padding: '0.75rem', borderRadius: '10px' }}>
                    <Target size={24} />
                  </div>
                  <ArrowRight size={20} color="#B45309" />
                </div>
                <h3 style={{ fontSize: '1.15rem', fontWeight: 700, color: '#3A3A3A', marginTop: '1.25rem', marginBottom: '0.5rem' }}>
                  My Self-Assessment (KPIs)
                </h3>
                <p style={{ color: '#737373', fontSize: '0.9rem', lineHeight: 1.5, margin: 0 }}>
                  View manager-level assigned KPIs, save draft self-evaluations, submit ratings to your reporting manager, and view appraisal history.
                </p>
              </div>

              {/* Card 3 */}
              <div
                onClick={() => setActiveTab('reports')}
                style={{
                  background: '#FFFFFF',
                  borderRadius: '12px',
                  padding: '1.75rem',
                  border: '1px solid #E2E8DF',
                  cursor: 'pointer',
                  transition: 'all 0.2s ease',
                  boxShadow: '0 2px 8px rgba(0,0,0,0.04)'
                }}
                onMouseEnter={(e) => (e.currentTarget.style.transform = 'translateY(-3px)')}
                onMouseLeave={(e) => (e.currentTarget.style.transform = 'translateY(0)')}
              >
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                  <div style={{ background: '#ECFDF5', color: '#047857', padding: '0.75rem', borderRadius: '10px' }}>
                    <BarChart3 size={24} />
                  </div>
                  <ArrowRight size={20} color="#047857" />
                </div>
                <h3 style={{ fontSize: '1.15rem', fontWeight: 700, color: '#3A3A3A', marginTop: '1.25rem', marginBottom: '0.5rem' }}>
                  Performance Reports & History
                </h3>
                <p style={{ color: '#737373', fontSize: '0.9rem', lineHeight: 1.5, margin: 0 }}>
                  Access team monthly status reports, review multi-cycle employee rating trajectories, score comparisons, and finalized results.
                </p>
              </div>
            </div>
          </div>
        )}

        {activeTab === 'my-kpis' && <MyKpisPage />}

        {activeTab === 'assigned-employees' && (
          <div>
            {selectedReview ? (
              <EmployeeReviewPage
                employeeId={selectedReview.employeeId}
                assignmentId={selectedReview.assignmentId}
                onBack={handleBackToAssignedList}
              />
            ) : (
              <AssignedEmployeesPage onSelectEmployee={handleStartReview} />
            )}
          </div>
        )}

        {activeTab === 'reports' && <ReportsPage />}
      </main>
    </div>
  );
};
