// Game card component placeholder
// This file ensures the directory is tracked by Git

export default function GameCard({ game }) {
  return (
    <div className="game-card">
      <h3>{game?.title || 'Game Title'}</h3>
      <p>Game card component</p>
    </div>
  );
} 