import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Award, Lock, Mail, ShieldAlert } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { ApiError } from '../services/api';

export const LoginPage: React.FC = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrorMessage(null);

    if (!email.trim()) {
      setErrorMessage('Invalid email ID.');
      return;
    }

    if (!password) {
      setErrorMessage('Please enter your password.');
      return;
    }

    setIsLoading(true);
    try {
      await login(email.trim(), password);
      navigate('/dashboard');
    } catch (err) {
      if (err instanceof ApiError) {
        setErrorMessage(err.message || 'Authentication failed.');
      } else {
        setErrorMessage('Failed to connect to the PMS server. Please try again.');
      }
    } finally {
      setIsLoading(false);
    }
  };

  const handleQuickLogin = (testEmail: string) => {
    setEmail(testEmail);
    setPassword('Password@123');
    setErrorMessage(null);
  };

  return (
    <div style={{ minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center', backgroundColor: 'var(--bg-page)', padding: '1.5rem' }}>
      <div style={{ maxWidth: '440px', width: '100%' }}>
        <div style={{ textAlign: 'center', marginBottom: '2rem' }}>
          <div style={{ display: 'inline-flex', width: '56px', height: '56px', background: 'linear-gradient(135deg, var(--color-primary), var(--color-accent))', borderRadius: 'var(--radius-lg)', alignItems: 'center', justifyContent: 'center', color: 'white', boxShadow: '0 8px 18px rgba(111, 192, 74, 0.4)', marginBottom: '1rem' }}>
            <Award size={32} />
          </div>
          <h1 style={{ fontSize: '1.75rem', fontWeight: 800, color: 'var(--color-dark)', letterSpacing: '-0.02em' }}>PMS Portal</h1>
          <p style={{ color: 'var(--color-accent)', fontWeight: 700, fontSize: '0.9rem', marginTop: '0.2rem', textTransform: 'uppercase', letterSpacing: '0.05em' }}>
            Manager Module Login
          </p>
        </div>

        <div className="card" style={{ padding: '2.25rem' }}>
          {errorMessage && (
            <div className="alert alert-error" style={{ marginBottom: '1.5rem' }}>
              <ShieldAlert size={18} style={{ flexShrink: 0 }} />
              <div>{errorMessage}</div>
            </div>
          )}

          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label className="form-label" htmlFor="manager-email">Manager Email ID</label>
              <div style={{ position: 'relative' }}>
                <input
                  id="manager-email"
                  type="email"
                  className="form-input"
                  style={{ paddingLeft: '2.5rem' }}
                  placeholder="manager1@company.com"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  disabled={isLoading}
                  autoComplete="email"
                  required
                />
                <Mail size={18} style={{ position: 'absolute', left: '0.85rem', top: '50%', transform: 'translateY(-50%)', color: 'var(--color-dark-muted)' }} />
              </div>
            </div>

            <div className="form-group" style={{ marginBottom: '1.75rem' }}>
              <label className="form-label" htmlFor="manager-password">Password</label>
              <div style={{ position: 'relative' }}>
                <input
                  id="manager-password"
                  type="password"
                  className="form-input"
                  style={{ paddingLeft: '2.5rem' }}
                  placeholder="••••••••"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  disabled={isLoading}
                  autoComplete="current-password"
                  required
                />
                <Lock size={18} style={{ position: 'absolute', left: '0.85rem', top: '50%', transform: 'translateY(-50%)', color: 'var(--color-dark-muted)' }} />
              </div>
            </div>

            <button
              type="submit"
              className="btn btn-primary"
              style={{ width: '100%', padding: '0.8rem' }}
              disabled={isLoading}
            >
              {isLoading ? (
                <>
                  <div className="spinner" style={{ width: '18px', height: '18px', borderWidth: '2px', borderTopColor: '#FFFFFF' }} />
                  <span>Validating Credentials...</span>
                </>
              ) : (
                'Sign In as Manager'
              )}
            </button>
          </form>

          <div style={{ marginTop: '1.75rem', paddingTop: '1.25rem', borderTop: '1px solid var(--border-subtle)' }}>
            <p style={{ fontSize: '0.78rem', fontWeight: 600, color: 'var(--color-dark-muted)', marginBottom: '0.6rem', textAlign: 'center' }}>
              Quick Demo Accounts (Password: Password@123)
            </p>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.4rem' }}>
              <button
                type="button"
                className="btn btn-secondary btn-sm"
                style={{ justifyContent: 'space-between', fontSize: '0.8rem' }}
                onClick={() => handleQuickLogin('manager1@company.com')}
              >
                <span>Sarah Connor (Manager 1)</span>
                <span style={{ color: 'var(--color-accent)', fontWeight: 700 }}>manager1@company.com</span>
              </button>
              <button
                type="button"
                className="btn btn-secondary btn-sm"
                style={{ justifyContent: 'space-between', fontSize: '0.8rem' }}
                onClick={() => handleQuickLogin('manager2@company.com')}
              >
                <span>John Doe (Manager 2)</span>
                <span style={{ color: 'var(--color-accent)', fontWeight: 700 }}>manager2@company.com</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
