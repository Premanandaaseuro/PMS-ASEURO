import React, { useEffect } from 'react';
import { AlertTriangle, X } from 'lucide-react';

interface ConfirmationModalProps {
  isOpen: boolean;
  title?: string;
  message: string;
  confirmText?: string;
  cancelText?: string;
  onConfirm: () => void;
  onCancel: () => void;
  isLoading?: boolean;
}

export const ConfirmationModal: React.FC<ConfirmationModalProps> = ({
  isOpen,
  title = 'Confirm Submission',
  message,
  confirmText = 'Submit & Lock',
  cancelText = 'Cancel',
  onConfirm,
  onCancel,
  isLoading = false,
}) => {
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Escape' && isOpen && !isLoading) {
        onCancel();
      }
    };
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [isOpen, isLoading, onCancel]);

  if (!isOpen) return null;

  return (
    <div className="modal-overlay" role="dialog" aria-modal="true" aria-labelledby="modal-title">
      <div className="modal-card">
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
          <div className="modal-header">
            <div className="modal-icon-warning">
              <AlertTriangle size={24} />
            </div>
            <div>
              <h3 id="modal-title" className="modal-title">{title}</h3>
              <p style={{ fontSize: '0.85rem', color: 'var(--color-dark-muted)', marginTop: '0.2rem' }}>
                Irreversible Action Warning
              </p>
            </div>
          </div>
          <button
            type="button"
            onClick={onCancel}
            disabled={isLoading}
            style={{ background: 'none', border: 'none', cursor: 'pointer', color: 'var(--color-dark-muted)' }}
            aria-label="Close dialog"
          >
            <X size={20} />
          </button>
        </div>

        <div style={{ padding: '0.5rem 0', color: 'var(--color-dark)', fontSize: '0.96rem', lineHeight: '1.6' }}>
          {message}
        </div>

        <div className="modal-actions">
          <button
            type="button"
            className="btn btn-secondary"
            onClick={onCancel}
            disabled={isLoading}
          >
            {cancelText}
          </button>
          <button
            type="button"
            className="btn btn-primary"
            onClick={onConfirm}
            disabled={isLoading}
          >
            {isLoading ? (
              <>
                <div className="spinner" style={{ width: '16px', height: '16px', borderWidth: '2px' }} />
                <span>Processing...</span>
              </>
            ) : (
              confirmText
            )}
          </button>
        </div>
      </div>
    </div>
  );
};
