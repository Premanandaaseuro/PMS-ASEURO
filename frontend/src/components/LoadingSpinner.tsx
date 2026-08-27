import React from 'react';

interface LoadingSpinnerProps {
  message?: string;
}

export const LoadingSpinner: React.FC<LoadingSpinnerProps> = ({ message = 'Loading...' }) => {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', padding: '3rem 1rem', gap: '1rem' }}>
      <div className="spinner" style={{ width: '36px', height: '36px', borderWidth: '3px' }} />
      <p style={{ color: 'var(--color-dark-muted)', fontSize: '0.92rem', fontWeight: 500 }}>{message}</p>
    </div>
  );
};
