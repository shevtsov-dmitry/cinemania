// TODO use expo router instead
// import { Link } from 'react-router-dom';
// import { setVideoId } from '../../store/videoPlayerSlice';
import { useState } from "react";

/**
 * @typedef {Object} PosterMetadata
 * @property {string} filename
 * @property {string} contentType
 */

/**
 * @typedef {Object} VideoMetadata
 * @property {string} filename
 * @property {string} contentType
 */

export class ContentMetadata {
  /** @type {string} */
  id;
  /** @type {string} */
  title;
  /** @type {Date} */
  releaseDate;
  /** @type {string} */
  country;
  /** @type {string} */
  mainGenre;
  /** @type {[string]} */
  subGenres;
  /** @type {number} */
  age;
  /** @type {number} */
  rating;
  /**@type {PosterMetadata} */
  posterMetadata;
  /**@type {VideoMetadata} */
  videoMetadata;
}
