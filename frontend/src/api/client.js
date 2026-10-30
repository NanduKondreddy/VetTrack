/**
 * VetTrack API Client
 * Automatically attaches JWT authentication tokens and handles error responses.
 */

const BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api/v1';

export async function apiRequest(endpoint, options = {}) {
  const token = localStorage.getItem('vettrack_token');
  
  const headers = {
    'Content-Type': 'application/json',
    ...(token ? { 'Authorization': `Bearer ${token}` } : {}),
    ...(options.headers || {})
  };

  const config = {
    ...options,
    headers
  };

  const response = await fetch(`${BASE_URL}${endpoint}`, config);

  if (response.status === 204) {
    return null;
  }

  const data = await response.json().catch(() => null);

  if (!response.ok) {
    if (response.status === 401 && !endpoint.includes('/auth/login')) {
      // Clear token and redirect to login if session expired
      localStorage.removeItem('vettrack_token');
      localStorage.removeItem('vettrack_user');
      window.location.href = '/login';
    }
    const errorMsg = data?.message || data?.error || `HTTP error ${response.status}`;
    const err = new Error(errorMsg);
    err.status = response.status;
    err.details = data;
    throw err;
  }

  return data;
}

export const api = {
  // Auth
  login: (credentials) => apiRequest('/auth/login', { method: 'POST', body: JSON.stringify(credentials) }),

  // Dashboard & Reports
  getSummary: () => apiRequest('/reports/summary'),

  // Owners
  getOwners: (params = {}) => {
    const query = new URLSearchParams(params).toString();
    return apiRequest(`/owners?${query}`);
  },
  getOwner: (id) => apiRequest(`/owners/${id}`),
  createOwner: (data) => apiRequest('/owners', { method: 'POST', body: JSON.stringify(data) }),
  updateOwner: (id, data) => apiRequest(`/owners/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
  deleteOwner: (id) => apiRequest(`/owners/${id}`, { method: 'DELETE' }),

  // Animals
  getAnimals: (params = {}) => {
    const query = new URLSearchParams(params).toString();
    return apiRequest(`/animals?${query}`);
  },
  getAnimal: (id) => apiRequest(`/animals/${id}`),
  createAnimal: (data) => apiRequest('/animals', { method: 'POST', body: JSON.stringify(data) }),
  updateAnimal: (id, data) => apiRequest(`/animals/${id}`, { method: 'PUT', body: JSON.stringify(data) }),

  // Visits
  getAnimalVisits: (animalId) => apiRequest(`/animals/${animalId}/visits`),
  createVisit: (animalId, data) => apiRequest(`/animals/${animalId}/visits`, { method: 'POST', body: JSON.stringify(data) }),

  // Vaccinations & Batches
  getAnimalVaccinations: (animalId) => apiRequest(`/animals/${animalId}/vaccinations`),
  recordVaccination: (animalId, data) => apiRequest(`/animals/${animalId}/vaccinations`, { method: 'POST', body: JSON.stringify(data) }),
  getDueVaccinations: (withinDays = 14) => apiRequest(`/vaccinations/due?withinDays=${withinDays}`),
  getExpiringBatches: (withinDays = 30) => apiRequest(`/batches/expiring?withinDays=${withinDays}`),
  getVaccines: () => apiRequest('/vaccines'),
};
