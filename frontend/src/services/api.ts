import axios from 'axios';

/**
 * Shared Axios instance.
 * Base URL is driven by the NEXT_PUBLIC_API_BASE_URL env variable
 * (set in .env.local for development, injected at build time for production).
 */
const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080',
  headers: {
    'Content-Type': 'application/json',
  },
});

export default api;
