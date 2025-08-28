const mongoose = require('mongoose');

const gameSchema = new mongoose.Schema({
  title: {
    type: String,
    required: true,
    trim: true
  },
  description: {
    type: String,
    required: true
  },
  genre: [{
    type: String,
    required: true
  }],
  platform: [{
    type: String,
    required: true
  }],
  releaseDate: {
    type: Date,
    required: true
  },
  developer: {
    type: String,
    required: true
  },
  publisher: {
    type: String,
    required: true
  },
  rating: {
    type: Number,
    min: 0,
    max: 10,
    default: 0
  },
  coverImage: {
    type: String,
    default: ''
  },
  screenshots: [{
    type: String
  }],
  trailer: {
    type: String
  },
  price: {
    type: Number,
    min: 0,
    default: 0
  },
  isActive: {
    type: Boolean,
    default: true
  }
}, {
  timestamps: true
});

module.exports = mongoose.model('Game', gameSchema); 