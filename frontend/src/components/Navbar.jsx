import React from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { 
  Activity, 
  Users, 
  AlertCircle, 
  Boxes, 
  LogOut, 
  ShieldCheck, 
  UserCheck 
} from 'lucide-react';

export default function Navbar() {
  const { user, logout, isAdmin } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <nav className="navbar">
      <div className="navbar-inner">
        {/* Brand */}
        <NavLink to="/dashboard" className="nav-brand">
          <div className="nav-brand-icon">
            <Activity size={22} />
          </div>
          <span>VetTrack</span>
        </NavLink>

        {/* Navigation Links */}
        <ul className="nav-links">
          <li>
            <NavLink to="/dashboard" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
              <Activity size={18} />
              <span>Dashboard</span>
            </NavLink>
          </li>
          <li>
            <NavLink to="/owners" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
              <Users size={18} />
              <span>Clients &amp; Animals</span>
            </NavLink>
          </li>
          <li>
            <NavLink to="/due" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
              <AlertCircle size={18} />
              <span>Due Reminders</span>
            </NavLink>
          </li>
          <li>
            <NavLink to="/batches" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
              <Boxes size={18} />
              <span>Vaccine Stock</span>
            </NavLink>
          </li>
        </ul>

        {/* User Session Profile & Logout */}
        <div className="nav-user">
          <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-end' }}>
            <span style={{ fontSize: '14px', fontWeight: '600', color: 'var(--text-main)' }}>
              {user?.username}
            </span>
            <span className={`badge-role ${isAdmin ? 'admin' : 'staff'}`}>
              {isAdmin ? (
                <span style={{ display: 'flex', alignItems: 'center', gap: '3px' }}>
                  <ShieldCheck size={12} /> Admin
                </span>
              ) : (
                <span style={{ display: 'flex', alignItems: 'center', gap: '3px' }}>
                  <UserCheck size={12} /> Staff
                </span>
              )}
            </span>
          </div>

          <button 
            onClick={handleLogout} 
            className="btn btn-secondary btn-sm"
            title="Log out"
            style={{ padding: '8px' }}
          >
            <LogOut size={16} />
          </button>
        </div>
      </div>
    </nav>
  );
}
