const mongoose = require('mongoose');

const reviewSchema = new mongoose.Schema({
  user: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  game: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Game',
    required: true
  },
  rating: {
    type: Number,
    required: true,
    min: 1,
    max: 10
  },
  title: {
    type: String,
    required: true,
    trim: true,
    maxlength: 100
  },
  content: {
    type: String,
    required: true,
    maxlength: 2000
  },
  pros: [{
    type: String,
    maxlength: 100
  }],
  cons: [{
    type: String,
    maxlength: 100
  }],
  isVerified: {
    type: Boolean,
    default: false
  },
  helpful: [{
    user: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User'
    },
    isHelpful: Boolean
  }],
  tags: [{
    type: String,
    trim: true
  }]
}, {
  timestamps: true
});

// Ensure one review per user per game
reviewSchema.index({ user: 1, game: 1 }, { unique: true });

// Virtual for helpful count
reviewSchema.virtual('helpfulCount').get(function() {
  return this.helpful.filter(h => h.isHelpful).length;
});

// Virtual for unhelpful count
reviewSchema.virtual('unhelpfulCount').get(function() {
  return this.helpful.filter(h => !h.isHelpful).length;
});

reviewSchema.set('toJSON', { virtuals: true });
reviewSchema.set('toObject', { virtuals: true });

module.exports = mongoose.model('Review', reviewSchema); 