import React from 'react';
import { NavLink } from 'react-router-dom';
import { LayoutDashboard, Target, Users, BarChart3, LogOut, Award } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

export const Sidebar: React.FC = () => {
  const { user, logout } = useAuth();

  const getInitials = (name?: string) => {
    if (!name) return 'M';
    const parts = name.split(' ');
    if (parts.length > 1) {
      return (parts[0][0] + parts[1][0]).toUpperCase();
    }
    return name.slice(0, 2).toUpperCase();
  };

  return (
    <aside className="sidebar">
      <div className="sidebar-header">
        <div className="brand-icon">
          <Award size={22} />
        </div>
        <div>
          <div className="brand-title">PMS Manager</div>
          <div className="brand-subtitle">Performance Portal</div>
        </div>
      </div>

      <nav className="sidebar-nav">
        <NavLink to="/dashboard" className={({ isActive }: { isActive: boolean }) => `nav-link ${isActive ? 'active' : ''}`}>
          <LayoutDashboard size={19} />
          <span className="nav-label">Dashboard</span>
        </NavLink>

        <NavLink to="/my-kpis" className={({ isActive }: { isActive: boolean }) => `nav-link ${isActive ? 'active' : ''}`}>
          <Target size={19} />
          <span className="nav-label">View My KPIs</span>
        </NavLink>

        <NavLink to="/assigned-employees" className={({ isActive }: { isActive: boolean }) => `nav-link ${isActive ? 'active' : ''}`}>
          <Users size={19} />
          <span className="nav-label">View Assigned Employees</span>
        </NavLink>

        <NavLink to="/reports" className={({ isActive }: { isActive: boolean }) => `nav-link ${isActive ? 'active' : ''}`}>
          <BarChart3 size={19} />
          <span className="nav-label">Reports</span>
        </NavLink>
      </nav>

      <div className="sidebar-footer">
        <div className="user-profile-badge" style={{ marginBottom: '1rem' }}>
          <div className="user-avatar">
            {getInitials(user?.fullName)}
          </div>
          <div className="user-info-text" style={{ minWidth: 0 }}>
            <div className="user-info-name" style={{ overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
              {user?.fullName || 'Manager'}
            </div>
            <div className="user-info-role">
              <span className="role-tag">MANAGER</span>
              <span style={{ fontSize: '0.75rem', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                {user?.designation || 'Engineering'}
              </span>
            </div>
          </div>
        </div>

        <button
          type="button"
          className="btn btn-outline-danger btn-sm"
          onClick={logout}
          style={{ width: '100%' }}
        >
          <LogOut size={15} />
          <span>Sign Out</span>
        </button>
      </div>
    </aside>
  );
};
