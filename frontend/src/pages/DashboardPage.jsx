import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { api } from '../api/client';
import { 
  Users, 
  Dog, 
  Syringe, 
  AlertTriangle, 
  CalendarClock, 
  ArrowRight,
  TrendingUp,
  PackageX
} from 'lucide-react';
import { 
  ResponsiveContainer, 
  BarChart, 
  Bar, 
  XAxis, 
  YAxis, 
  Tooltip, 
  CartesianGrid,
  PieChart,
  Pie,
  Cell
} from 'recharts';

const SPECIES_COLORS = {
  Dog: '#0F766E',
  Cat: '#0284C7',
  Cattle: '#F59E0B',
  Poultry: '#8B5CF6'
};

export default function DashboardPage() {
  const [summary, setSummary] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    async function loadDashboard() {
      try {
        setLoading(true);
        const data = await api.getSummary();
        setSummary(data);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    }
    loadDashboard();
  }, []);

  if (loading) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', padding: '60px' }}>
        <p style={{ color: 'var(--text-muted)' }}>Loading clinic statistics...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div style={{ padding: '20px', background: '#fee2e2', color: '#991b1b', borderRadius: '12px' }}>
        <p><strong>Failed to load dashboard:</strong> {error}</p>
      </div>
    );
  }

  // Monthly mockup trend data for live Recharts chart
  const monthlyData = [
    { month: 'Jun', doses: 110 },
    { month: 'Jul', doses: 145 },
    { month: 'Aug', doses: 160 },
    { month: 'Sep', doses: 135 },
    { month: 'Oct', doses: summary?.vaccinationsThisMonth || 85 },
  ];

  // Prepare species distribution data
  const speciesData = summary?.speciesBreakdown 
    ? Object.entries(summary.speciesBreakdown).map(([name, value]) => ({
        name,
        value,
        color: SPECIES_COLORS[name] || '#64748B'
      }))
    : [];

  return (
    <div>
      {/* Page Header */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '28px' }}>
        <div>
          <h1 style={{ fontSize: '28px', color: 'var(--text-main)', marginBottom: '4px' }}>
            Clinic Operations Dashboard
          </h1>
          <p style={{ color: 'var(--text-muted)', fontSize: '14px' }}>
            Real-time patient census, vaccination schedules, and pharmacy stock monitoring.
          </p>
        </div>
        <div style={{ display: 'flex', gap: '10px' }}>
          <Link to="/due" className="btn btn-secondary btn-sm">
            <CalendarClock size={16} /> Due List
          </Link>
          <Link to="/owners" className="btn btn-primary btn-sm">
            <Users size={16} /> Register Client
          </Link>
        </div>
      </div>

      {/* Key Metric Stat Cards */}
      <div className="stats-grid">
        <div className="stat-card teal">
          <div>
            <span className="stat-label">Total Clients</span>
            <div className="stat-val">{summary?.totalOwners || 0}</div>
          </div>
          <div className="stat-icon teal">
            <Users size={22} />
          </div>
        </div>

        <div className="stat-card blue">
          <div>
            <span className="stat-label">Registered Animals</span>
            <div className="stat-val">{summary?.totalAnimals || 0}</div>
          </div>
          <div className="stat-icon blue">
            <Dog size={22} />
          </div>
        </div>

        <div className="stat-card amber">
          <div>
            <span className="stat-label">Given This Month</span>
            <div className="stat-val">{summary?.vaccinationsThisMonth || 0}</div>
          </div>
          <div className="stat-icon amber">
            <Syringe size={22} />
          </div>
        </div>

        <div className="stat-card red">
          <div>
            <span className="stat-label">Overdue Boosters</span>
            <div className="stat-val" style={{ color: 'var(--danger)' }}>
              {summary?.overdueVaccinations || 0}
            </div>
          </div>
          <div className="stat-icon red">
            <AlertTriangle size={22} />
          </div>
        </div>
      </div>

      {/* Analytics Charts Section */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(420px, 1fr))', gap: '24px', marginBottom: '32px' }}>
        {/* Trend Bar Chart */}
        <div className="card">
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '20px' }}>
            <h3 style={{ fontSize: '16px', color: 'var(--text-main)' }}>Vaccination Administration Volume</h3>
            <span style={{ fontSize: '12px', color: 'var(--text-muted)', display: 'flex', alignItems: 'center', gap: '4px' }}>
              <TrendingUp size={14} color="var(--primary)" /> 5-Month Trend
            </span>
          </div>

          <div style={{ height: '240px', width: '100%' }}>
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={monthlyData} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
                <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#E2E8F0" />
                <XAxis dataKey="month" tick={{ fontSize: 12, fill: '#64748B' }} axisLine={false} tickLine={false} />
                <YAxis tick={{ fontSize: 12, fill: '#64748B' }} axisLine={false} tickLine={false} />
                <Tooltip 
                  contentStyle={{ backgroundColor: '#0F172A', borderRadius: '8px', color: '#FFF', border: 'none' }}
                  cursor={{ fill: 'rgba(15, 118, 110, 0.08)' }}
                />
                <Bar dataKey="doses" fill="var(--primary)" radius={[6, 6, 0, 0]} barSize={38} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Species Distribution Chart */}
        <div className="card">
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '20px' }}>
            <h3 style={{ fontSize: '16px', color: 'var(--text-main)' }}>Patient Population by Species</h3>
            <span style={{ fontSize: '12px', color: 'var(--text-muted)' }}>Census Breakdown</span>
          </div>

          <div style={{ height: '240px', width: '100%', display: 'flex', alignItems: 'center' }}>
            <ResponsiveContainer width="55%" height="100%">
              <PieChart>
                <Pie
                  data={speciesData}
                  cx="50%"
                  cy="50%"
                  innerRadius={50}
                  outerRadius={80}
                  paddingAngle={4}
                  dataKey="value"
                >
                  {speciesData.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={entry.color} />
                  ))}
                </Pie>
                <Tooltip contentStyle={{ backgroundColor: '#0F172A', borderRadius: '8px', color: '#FFF', border: 'none' }} />
              </PieChart>
            </ResponsiveContainer>

            <div style={{ flex: 1, paddingLeft: '16px' }}>
              {speciesData.map((item) => (
                <div key={item.name} style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '8px' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                    <div style={{ width: '10px', height: '10px', borderRadius: '3px', backgroundColor: item.color }} />
                    <span style={{ fontSize: '13px', fontWeight: '500', color: 'var(--text-main)' }}>{item.name}</span>
                  </div>
                  <span style={{ fontSize: '13px', fontWeight: '700', color: 'var(--text-muted)' }}>{item.value}</span>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>

      {/* Stock Alerts Notice Card */}
      {summary?.expiringBatchesCount > 0 && (
        <div style={{
          background: 'linear-gradient(135deg, #FEF3C7, #FFFBEB)',
          border: '1px solid #FDE68A',
          borderRadius: '16px',
          padding: '20px',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between'
        }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '14px' }}>
            <div style={{ width: '42px', height: '42px', borderRadius: '10px', background: '#F59E0B', color: 'white', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
              <PackageX size={22} />
            </div>
            <div>
              <h4 style={{ color: '#92400E', fontSize: '15px', marginBottom: '2px' }}>
                Inventory Risk Notice: {summary.expiringBatchesCount} Vaccine Batch(es) Expiring Within 30 Days
              </h4>
              <p style={{ color: '#B45309', fontSize: '13px' }}>
                Batches nearing expiration require priority usage or replacement to avoid disposal waste.
              </p>
            </div>
          </div>
          <Link to="/batches" className="btn btn-secondary btn-sm" style={{ borderColor: '#FCD34D', color: '#92400E' }}>
            View Stock Batches <ArrowRight size={14} />
          </Link>
        </div>
      )}
    </div>
  );
}
