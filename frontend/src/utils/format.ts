export const formatScore = (val?: number | null, fallback = '—'): string => {
  if (val === null || val === undefined || isNaN(val)) {
    return fallback;
  }
  return Number(val).toFixed(2);
};

export const hasValidScore = (val?: number | null): val is number => {
  return val !== null && val !== undefined && !isNaN(val);
};
