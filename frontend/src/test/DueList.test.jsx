import React from 'react';
import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import DueVaccinationsPage from '../pages/DueVaccinationsPage';
import { api } from '../api/client';

vi.mock('../api/client', () => ({
  api: {
    getDueVaccinations: vi.fn(),
  }
}));

const mockDueData = [
  {
    vaccinationId: 101,
    animalName: 'Barnaby',
    species: 'Dog',
    breed: 'Golden Retriever',
    ownerName: 'Elena Rostova',
    ownerPhone: '+1-555-019-2834',
    ownerEmail: 'elena@farmfresh.org',
    vaccineName: 'Rabies Core',
    batchNo: 'RAB-2026-A1',
    givenOn: '2025-10-15',
    nextDueOn: '2026-10-15',
    daysRemaining: -15,
    overdue: true
  },
  {
    vaccinationId: 102,
    animalName: 'Cleo',
    species: 'Cat',
    breed: 'Domestic Shorthair',
    ownerName: 'James Miller',
    ownerPhone: '+1-555-014-9981',
    ownerEmail: 'jmiller@vetmail.com',
    vaccineName: 'FVRCP Feline Core',
    batchNo: 'FVRCP-2026-F1',
    givenOn: '2025-11-05',
    nextDueOn: '2026-11-05',
    daysRemaining: 6,
    overdue: false
  }
];

describe('DueVaccinationsPage Component', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    api.getDueVaccinations.mockResolvedValue(mockDueData);
  });

  it('renders header, filter pills, and export button', async () => {
    render(
      <BrowserRouter>
        <DueVaccinationsPage />
      </BrowserRouter>
    );

    expect(screen.getByText(/Vaccination Due & Recall Schedule/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /Within 7 Days/i })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /Within 14 Days/i })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /Within 30 Days/i })).toBeInTheDocument();
  });

  it('renders table rows and tags for due vaccinations', async () => {
    render(
      <BrowserRouter>
        <DueVaccinationsPage />
      </BrowserRouter>
    );

    await waitFor(() => {
      expect(screen.getByText('Barnaby')).toBeInTheDocument();
      expect(screen.getByText('Cleo')).toBeInTheDocument();
      expect(screen.getAllByText(/Overdue/i).length).toBeGreaterThan(0);
      expect(screen.getByText(/Due Soon/i)).toBeInTheDocument();
    });
  });

  it('filters table dynamically when typing into search bar', async () => {
    render(
      <BrowserRouter>
        <DueVaccinationsPage />
      </BrowserRouter>
    );

    await waitFor(() => {
      expect(screen.getByText('Barnaby')).toBeInTheDocument();
    });

    const searchInput = screen.getByPlaceholderText(/Filter by animal or owner/i);
    fireEvent.change(searchInput, { target: { value: 'Cleo' } });

    expect(screen.queryByText('Barnaby')).not.toBeInTheDocument();
    expect(screen.getByText('Cleo')).toBeInTheDocument();
  });
});
