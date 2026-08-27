import React from 'react';
import { Star } from 'lucide-react';
import { formatScore, hasValidScore } from '../utils/format';

interface RatingInputProps {
  value?: number | null;
  onChange?: (val: number) => void;
  disabled?: boolean;
  readOnly?: boolean;
  label?: string;
  id?: string;
}

export const RatingInput: React.FC<RatingInputProps> = ({
  value,
  onChange,
  disabled = false,
  readOnly = false,
  label,
  id,
}) => {
  const isInputDisabled = disabled || readOnly;

  const handleButtonClick = (ratingVal: number) => {
    if (isInputDisabled || !onChange) return;
    onChange(ratingVal);
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (isInputDisabled || !onChange) return;
    const num = parseFloat(e.target.value);
    if (!isNaN(num)) {
      onChange(num);
    }
  };

  return (
    <div className="rating-control-container">
      {label && <label className="form-label" htmlFor={id}>{label}</label>}
      <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
        <div className="rating-btn-group" role="group" aria-label="Rating scale 1 to 5">
          {[1, 2, 3, 4, 5].map((star) => {
            const isSelected = hasValidScore(value) && Math.floor(value) === star;
            return (
              <button
                key={star}
                type="button"
                className={`rating-btn ${isSelected ? 'selected' : ''}`}
                onClick={() => handleButtonClick(star)}
                disabled={isInputDisabled}
                title={`Rate ${star} out of 5`}
                aria-pressed={isSelected}
              >
                {star}
              </button>
            );
          })}
        </div>

        {!readOnly && (
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.35rem' }}>
            <input
              id={id}
              type="number"
              min="1.0"
              max="5.0"
              step="0.5"
              value={hasValidScore(value) ? value : ''}
              onChange={handleInputChange}
              disabled={isInputDisabled}
              className="form-input"
              style={{ width: '85px', textAlign: 'center', fontWeight: 600 }}
              placeholder="1.0 - 5.0"
            />
            <span style={{ fontSize: '0.82rem', color: 'var(--color-dark-muted)' }}>/ 5.0</span>
          </div>
        )}

        {readOnly && (
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.4rem', fontWeight: 700, color: 'var(--color-accent)' }}>
            <Star size={16} fill="var(--color-primary)" color="var(--color-primary)" />
            <span>{formatScore(value)}</span>
            <span style={{ fontSize: '0.8rem', color: 'var(--color-dark-muted)', fontWeight: 500 }}>/ 5.0</span>
          </div>
        )}
      </div>
    </div>
  );
};
