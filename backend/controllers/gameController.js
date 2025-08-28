// Game controller placeholder
// This file ensures the directory is tracked by Git

const Game = require('../models/Game');

exports.getAllGames = async (req, res) => {
  try {
    const games = await Game.find({ isActive: true });
    res.json(games);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

exports.getGameById = async (req, res) => {
  try {
    const game = await Game.findById(req.params.id);
    if (!game) {
      return res.status(404).json({ message: 'Game not found' });
    }
    res.json(game);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
}; 