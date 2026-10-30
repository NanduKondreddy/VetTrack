import React, { useEffect, useState } from 'react';
import { api } from '../api/client';
import { 
  CalendarClock, 
  Download, 
  Search, 
  AlertCircle, 
  CheckCircle2, 
  Phone, 
  Mail, 
  Dog 
} from 'lucide-react';

export default function DueVaccinationsPage() {
  const [dueList, setDueList] = useState([]);
  const [loading, setLoading] = useState(true);
  const [withinDays, setWithinDays] = useState(14);
  const [search, setSearch] = useState('');

  const loadDueList = async (days = withinDays) => {
    try {
      setLoading(true);
      const data = await api.getDueVaccinations(days);
      setDueList(data || []);
    } catch (err) {
      console.error('Failed to load due vaccinations:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadDueList(withinDays);
  }, [withinDays]);

  // Client-side search filter
  const filteredList = dueList.filter((item) => {
    if (!search) return true;
    const q = search.toLowerCase();
    return (
      item.animalName?.toLowerCase().includes(q) ||
      item.ownerName?.toLowerCase().includes(q) ||
      item.vaccineName?.toLowerCase().includes(q) ||
      item.species?.toLowerCase().includes(q)
    );
  });

  // CSV Export
  const exportToCSV = () => {
    if (filteredList.length === 0) return;

    const headers = [
      'Animal Name',
      'Species',
      'Breed',
      'Owner Name',
      'Owner Phone',
      'Owner Email',
      'Vaccine Name',
      'Batch No',
      'Given On',
      'Next Due On',
      'Days Remaining',
      'Status'
    ];

    const rows = filteredList.map(item => [
      `"${item.animalName || ''}"`,
      `"${item.species || ''}"`,
      `"${item.breed || ''}"`,
      `"${item.ownerName || ''}"`,
      `"${item.ownerPhone || ''}"`,
      `"${item.ownerEmail || ''}"`,
      `"${item.vaccineName || ''}"`,
      `"${item.batchNo || ''}"`,
      `"${item.givenOn || ''}"`,
      `"${item.nextDueOn || ''}"`,
      item.daysRemaining,
      item.overdue ? 'OVERDUE' : 'DUE SOON'
    ]);

    const csvContent = [headers.join(','), ...rows.map(e => e.join(','))].join('\n');
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.setAttribute('href', url);
    link.setAttribute('download', `vettrack_due_vaccinations_${withinDays}days.csv`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  return (
    <div>
      {/* Header */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '24px', flexWrap: 'wrap', gap: '16px' }}>
        <div>
          <h1 style={{ fontSize: '26px', color: 'var(--text-main)', marginBottom: '4px' }}>
            Vaccination Due &amp; Recall Schedule
          </h1>
          <p style={{ color: 'var(--text-muted)', fontSize: '14px' }}>
            Monitor impending booster deadlines and contact pet and livestock owners.
          </p>
        </div>

        <button 
          onClick={exportToCSV} 
          disabled={filteredList.length === 0}
          className="btn btn-secondary"
        >
          <Download size={16} /> Export to CSV ({filteredList.length})
        </button>
      </div>

      {/* Filter Bar & Search */}
      <div className="card" style={{ padding: '16px 20px', marginBottom: '24px' }}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '16px' }}>
          {/* Interval Filter Pills */}
          <div style={{ display: 'flex', gap: '8px' }}>
            {[7, 14, 30, 60].map((days) => (
              <button
                key={days}
                onClick={() => setWithinDays(days)}
                style={{
                  padding: '6px 14px',
                  borderRadius: '9999px',
                  fontSize: '13px',
                  fontWeight: '600',
                  border: '1px solid',
                  cursor: 'pointer',
                  borderColor: withinDays === days ? 'var(--primary)' : 'var(--border-color)',
                  backgroundColor: withinDays === days ? 'var(--primary-light)' : 'white',
                  color: withinDays === days ? 'var(--primary)' : 'var(--text-muted)',
                  transition: 'all 0.15s ease'
                }}
              >
                Within {days} Days
              </button>
            ))}
          </div>

          {/* Search box */}
          <div style={{ position: 'relative', width: '280px' }}>
            <Search size={16} style={{ position: 'absolute', left: '12px', top: '11px', color: '#94a3b8' }} />
            <input
              type="text"
              placeholder="Filter by animal or owner..."
              className="input-control"
              style={{ paddingLeft: '36px', paddingBottom: '6px', paddingTop: '6px', fontSize: '13px' }}
              value={search}
              onChange={(e) => setSearch(e.target.value)}
            />
          </div>
        </div>
      </div>

      {/* Due Table */}
      <div className="table-responsive">
        <table className="data-table">
          <thead>
            <tr>
              <th>Patient</th>
              <th>Owner Contact</th>
              <th>Vaccine &amp; Batch</th>
              <th>Last Given</th>
              <th>Next Due Date</th>
              <th>Booster Status</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan="6" style={{ textAlign: 'center', padding: '36px', color: 'var(--text-muted)' }}>
                  Scanning due vaccinations...
                </td>
              </tr>
            ) : filteredList.length === 0 ? (
              <tr>
                <td colSpan="6" style={{ textAlign: 'center', padding: '36px', color: 'var(--text-muted)' }}>
                  No vaccinations due within {withinDays} days matching criteria.
                </td>
              </tr>
            ) : (
              filteredList.map((item) => (
                <tr key={item.vaccinationId}>
                  <td>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                      <div style={{ width: '32px', height: '32px', borderRadius: '8px', background: 'var(--primary-light)', color: 'var(--primary)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                        <Dog size={16} />
                      </div>
                      <div>
                        <div style={{ fontWeight: '600' }}>{item.animalName}</div>
                        <div style={{ fontSize: '12px', color: 'var(--text-muted)' }}>{item.species} &bull; {item.breed}</div>
                      </div>
                    </div>
                  </td>
                  <td>
                    <div style={{ fontWeight: '500' }}>{item.ownerName}</div>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '6px', fontSize: '12px', color: 'var(--text-muted)' }}>
                      <Phone size={11} /> {item.ownerPhone}
                    </div>
                  </td>
                  <td>
                    <div><strong>{item.vaccineName}</strong></div>
                    <code style={{ fontSize: '11px', color: 'var(--text-muted)' }}>#{item.batchNo}</code>
                  </td>
                  <td>{item.givenOn}</td>
                  <td>
                    <div style={{ fontWeight: '600', color: item.overdue ? 'var(--danger)' : 'var(--text-main)' }}>
                      {item.nextDueOn}
                    </div>
                    <div style={{ fontSize: '12px', color: 'var(--text-muted)' }}>
                      {item.daysRemaining < 0 
                        ? `${Math.abs(item.daysRemaining)} days overdue` 
                        : `${item.daysRemaining} days remaining`}
                    </div>
                  </td>
                  <td>
                    {item.overdue ? (
                      <span className="tag tag-danger">
                        <AlertCircle size={12} /> Overdue
                      </span>
                    ) : (
                      <span className="tag tag-warning">
                        <CalendarClock size={12} /> Due Soon
                      </span>
                    )}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
