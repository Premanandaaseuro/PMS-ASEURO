import React from 'react';
import { Calendar, UserCheck } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

interface NavbarProps {
  title: string;
  cycleInfo?: string;
}

export const Navbar: React.FC<NavbarProps> = ({ title, cycleInfo }) => {
  const { user } = useAuth();

  return (
    <header className="top-navbar">
      <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
        <h1 className="navbar-title">{title}</h1>
      </div>

      <div className="navbar-actions">
        {cycleInfo && (
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', background: 'var(--color-primary-light)', padding: '0.4rem 0.85rem', borderRadius: 'var(--radius-full)', border: '1px solid var(--color-primary-border)', fontSize: '0.85rem', fontWeight: 600, color: 'var(--color-accent)' }}>
            <Calendar size={15} />
            <span>{cycleInfo}</span>
          </div>
        )}

        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', fontSize: '0.88rem', fontWeight: 600, color: 'var(--color-dark)' }}>
          <UserCheck size={18} color="var(--color-primary)" />
          <span>{user?.email}</span>
        </div>
      </div>
    </header>
  );
};
