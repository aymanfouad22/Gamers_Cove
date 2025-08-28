// Common components placeholder
// This file ensures the directory is tracked by Git

export const Button = ({ children, ...props }) => (
  <button className="btn" {...props}>{children}</button>
);

export const Card = ({ children, ...props }) => (
  <div className="card" {...props}>{children}</div>
);

export const Loading = () => (
  <div className="loading">Loading...</div>
); 