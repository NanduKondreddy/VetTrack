import React, { useEffect, useState } from 'react';
import { api } from '../api/client';
import { 
  Boxes, 
  AlertTriangle, 
  CheckCircle2, 
  XCircle, 
  Calendar, 
  ShieldAlert,
  Package
} from 'lucide-react';

export default function BatchesPage() {
  const [batches, setBatches] = useState([]);
  const [loading, setLoading] = useState(true);
  const [withinDays, setWithinDays] = useState(60);

  useEffect(() => {
    async function loadBatches() {
      try {
        setLoading(true);
        const data = await api.getExpiringBatches(withinDays);
        setBatches(data || []);
      } catch (err) {
        console.error('Failed to load vaccine batches:', err);
      } finally {
        setLoading(false);
      }
    }
    loadBatches();
  }, [withinDays]);

  return (
    <div>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '24px', flexWrap: 'wrap', gap: '16px' }}>
        <div>
          <h1 style={{ fontSize: '26px', color: 'var(--text-main)', marginBottom: '4px' }}>
            Vaccine Inventory &amp; Expiry Risk
          </h1>
          <p style={{ color: 'var(--text-muted)', fontSize: '14px' }}>
            Track batch safety thresholds, remaining doses, and impending shelf-life expirations.
          </p>
        </div>

        <div style={{ display: 'flex', gap: '8px' }}>
          {[30, 60, 90, 180].map((days) => (
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
                borderColor: withinDays === days ? 'var(--warning)' : 'var(--border-color)',
                backgroundColor: withinDays === days ? 'var(--warning-light)' : 'white',
                color: withinDays === days ? '#92400e' : 'var(--text-muted)',
                transition: 'all 0.15s ease'
              }}
            >
              Expiring in {days} Days
            </button>
          ))}
        </div>
      </div>

      {/* Batches Table */}
      <div className="table-responsive">
        <table className="data-table">
          <thead>
            <tr>
              <th>Vaccine Name</th>
              <th>Batch Number</th>
              <th>Doses Remaining</th>
              <th>Expiration Date</th>
              <th>Inventory Status</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan="5" style={{ textAlign: 'center', padding: '36px', color: 'var(--text-muted)' }}>
                  Loading pharmacy stock...
                </td>
              </tr>
            ) : batches.length === 0 ? (
              <tr>
                <td colSpan="5" style={{ textAlign: 'center', padding: '36px', color: 'var(--text-muted)' }}>
                  No batches expiring within {withinDays} days.
                </td>
              </tr>
            ) : (
              batches.map((batch) => (
                <tr key={batch.id}>
                  <td>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                      <Package size={16} color="var(--primary)" />
                      <strong>{batch.vaccineName}</strong>
                    </div>
                  </td>
                  <td>
                    <code style={{ background: '#f1f5f9', padding: '3px 8px', borderRadius: '4px', fontSize: '13px' }}>
                      {batch.batchNo}
                    </code>
                  </td>
                  <td>
                    <span style={{
                      fontWeight: '700',
                      color: batch.quantityRemaining <= 10 ? 'var(--danger)' : 'var(--text-main)'
                    }}>
                      {batch.quantityRemaining} units
                    </span>
                  </td>
                  <td>{batch.expiryDate}</td>
                  <td>
                    {batch.expired ? (
                      <span className="tag tag-danger">
                        <XCircle size={12} /> Expired
                      </span>
                    ) : batch.lowStock ? (
                      <span className="tag tag-warning">
                        <AlertTriangle size={12} /> Low Stock (&le;10)
                      </span>
                    ) : (
                      <span className="tag tag-info">
                        <CheckCircle2 size={12} /> Priority Usage
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
