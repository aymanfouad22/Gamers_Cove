// Game routes placeholder
// This file ensures the directory is tracked by Git

const express = require('express');
const router = express.Router();
const gameController = require('../controllers/gameController');

// GET /api/games - Get all games
router.get('/', gameController.getAllGames);

// GET /api/games/:id - Get game by ID
router.get('/:id', gameController.getGameById);

module.exports = router; 