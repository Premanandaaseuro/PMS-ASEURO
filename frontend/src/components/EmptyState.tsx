import React from 'react';
import { Inbox } from 'lucide-react';

interface EmptyStateProps {
  title?: string;
  message: string;
  actionLabel?: string;
  onAction?: () => void;
  icon?: React.ReactNode;
}

export const EmptyState: React.FC<EmptyStateProps> = ({
  title = 'No Records Found',
  message,
  actionLabel,
  onAction,
  icon,
}) => {
  return (
    <div style={{ textAlign: 'center', padding: '3.5rem 1.5rem', background: '#FFFFFF', borderRadius: 'var(--radius-lg)', border: '1px dashed var(--border-strong)' }}>
      <div style={{ display: 'inline-flex', padding: '1rem', background: 'var(--color-primary-light)', borderRadius: 'var(--radius-full)', color: 'var(--color-accent)', marginBottom: '1rem' }}>
        {icon || <Inbox size={32} />}
      </div>
      <h4 style={{ fontSize: '1.1rem', fontWeight: 700, marginBottom: '0.4rem', color: 'var(--color-dark)' }}>{title}</h4>
      <p style={{ color: 'var(--color-dark-muted)', fontSize: '0.9rem', maxWidth: '420px', margin: '0 auto 1.5rem auto' }}>{message}</p>
      {actionLabel && onAction && (
        <button type="button" className="btn btn-primary btn-sm" onClick={onAction}>
          {actionLabel}
        </button>
      )}
    </div>
  );
};
