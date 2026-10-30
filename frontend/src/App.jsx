import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { useAuth } from './context/AuthContext';
import Navbar from './components/Navbar';
import LoginPage from './pages/LoginPage';
import DashboardPage from './pages/DashboardPage';
import OwnersPage from './pages/OwnersPage';
import AnimalDetailPage from './pages/AnimalDetailPage';
import DueVaccinationsPage from './pages/DueVaccinationsPage';
import BatchesPage from './pages/BatchesPage';

function ProtectedLayout() {
  const { isAuthenticated } = useAuth();

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  return (
    <div className="app-container">
      <Navbar />
      <main className="main-content">
        <Routes>
          <Route path="/dashboard" element={<DashboardPage />} />
          <Route path="/owners" element={<OwnersPage />} />
          <Route path="/animals/:id" element={<AnimalDetailPage />} />
          <Route path="/due" element={<DueVaccinationsPage />} />
          <Route path="/batches" element={<BatchesPage />} />
          <Route path="*" element={<Navigate to="/dashboard" replace />} />
        </Routes>
      </main>
    </div>
  );
}

export default function App() {
  const { isAuthenticated } = useAuth();

  return (
    <Routes>
      <Route 
        path="/login" 
        element={isAuthenticated ? <Navigate to="/dashboard" replace /> : <LoginPage />} 
      />
      <Route path="/*" element={<ProtectedLayout />} />
    </Routes>
  );
}
