// Utility functions placeholder
// This file ensures the directory is tracked by Git

export const formatDate = (date) => {
  return new Date(date).toLocaleDateString();
};

export const truncateText = (text, maxLength) => {
  return text.length > maxLength ? text.substring(0, maxLength) + '...' : text;
};

export const generateId = () => {
  return Math.random().toString(36).substr(2, 9);
}; 