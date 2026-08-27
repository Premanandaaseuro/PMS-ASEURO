import React from 'react';
import { Outlet, useLocation } from 'react-router-dom';
import { Sidebar } from './Sidebar';
import { Navbar } from './Navbar';

export const AppLayout: React.FC = () => {
  const location = useLocation();

  const getPageTitle = (path: string) => {
    if (path.startsWith('/dashboard')) return 'Manager Performance Dashboard';
    if (path.startsWith('/my-kpis')) return 'View My KPIs (Self-Assessment)';
    if (path.includes('/review')) return 'Employee Performance Review';
    if (path.startsWith('/assigned-employees')) return 'Assigned Reporting Employees';
    if (path.startsWith('/reports')) return 'Manager Performance Reports';
    return 'Performance Management System';
  };

  return (
    <div className="app-container">
      <Sidebar />
      <div className="main-content">
        <Navbar title={getPageTitle(location.pathname)} cycleInfo="August 2026 PMS Cycle" />
        <main className="page-body">
          <Outlet />
        </main>
      </div>
    </div>
  );
};
