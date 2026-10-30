import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { describe, it, expect, vi } from 'vitest';
import LoginPage from '../pages/LoginPage';
import { AuthProvider } from '../context/AuthContext';

describe('LoginPage Component', () => {
  it('renders login form with fields and buttons', () => {
    render(
      <BrowserRouter>
        <AuthProvider>
          <LoginPage />
        </AuthProvider>
      </BrowserRouter>
    );

    expect(screen.getByText(/VetTrack Portal/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/Username/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/Password/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /Sign In to VetTrack/i })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /Demo Admin/i })).toBeInTheDocument();
  });

  it('populates fields when Demo Admin button is clicked', () => {
    render(
      <BrowserRouter>
        <AuthProvider>
          <LoginPage />
        </AuthProvider>
      </BrowserRouter>
    );

    const demoAdminBtn = screen.getByRole('button', { name: /Demo Admin/i });
    fireEvent.click(demoAdminBtn);

    const usernameInput = screen.getByLabelText(/Username/i);
    const passwordInput = screen.getByLabelText(/Password/i);

    expect(usernameInput.value).toBe('admin');
    expect(passwordInput.value).toBe('admin123');
  });
});
