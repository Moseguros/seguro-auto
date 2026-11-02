import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { configureHttp } from './api/http';
import { App } from './App';
import './i18n';

configureHttp({ baseURL: import.meta.env.VITE_API_BASE_URL ?? '' });

const root = document.getElementById('root');
if (!root) throw new Error('#root not found');

createRoot(root).render(
  <StrictMode>
    <App />
  </StrictMode>,
);
