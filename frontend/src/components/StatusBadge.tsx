import React from 'react';
import { Clock, CheckCircle2, FileEdit, Award, AlertCircle } from 'lucide-react';

interface StatusBadgeProps {
  status: string;
}

export const StatusBadge: React.FC<StatusBadgeProps> = ({ status }) => {
  const getBadgeConfig = () => {
    switch (status) {
      case 'PMS_NOT_STARTED':
      case 'NOT_STARTED':
        return {
          className: 'badge badge-draft',
          label: 'Not Started',
          icon: <Clock size={13} />,
        };
      case 'PMS_STARTED':
      case 'SELF_ASSESSMENT_DRAFT':
      case 'DRAFT':
        return {
          className: 'badge badge-draft',
          label: 'Self-Rating Draft',
          icon: <FileEdit size={13} />,
        };
      case 'SELF_ASSESSMENT_SUBMITTED':
      case 'MANAGER_REVIEW_PENDING':
      case 'PENDING_REVIEW':
        return {
          className: 'badge badge-pending',
          label: 'Manager Review Pending',
          icon: <Clock size={13} />,
        };
      case 'MANAGER_REVIEW_SUBMITTED':
      case 'HR_REVIEW_PENDING':
        return {
          className: 'badge badge-submitted',
          label: 'Manager Review Submitted',
          icon: <CheckCircle2 size={13} />,
        };
      case 'HR_REVIEW_COMPLETED':
      case 'RATING_AND_POINTS_CALCULATED':
      case 'FINAL_ANALYSIS':
        return {
          className: 'badge badge-pending',
          label: 'HR Finalizing',
          icon: <Clock size={13} />,
        };
      case 'FINAL_RESULT_PUBLISHED':
      case 'COMPLETED':
      case 'PUBLISHED':
        return {
          className: 'badge badge-published',
          label: 'Final Published',
          icon: <Award size={13} />,
        };
      default:
        return {
          className: 'badge badge-draft',
          label: status.replace(/_/g, ' '),
          icon: <AlertCircle size={13} />,
        };
    }
  };

  const config = getBadgeConfig();

  return (
    <span className={config.className}>
      {config.icon}
      <span>{config.label}</span>
    </span>
  );
};
