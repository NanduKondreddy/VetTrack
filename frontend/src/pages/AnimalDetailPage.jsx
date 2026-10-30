import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { api } from '../api/client';
import { 
  Dog, 
  Calendar, 
  Syringe, 
  ClipboardList, 
  Plus, 
  ArrowLeft, 
  AlertCircle, 
  CheckCircle2, 
  Phone, 
  Mail, 
  Clock, 
  X,
  ShieldAlert
} from 'lucide-react';

export default function AnimalDetailPage() {
  const { id } = useParams();
  const [animal, setAnimal] = useState(null);
  const [vaccinations, setVaccinations] = useState([]);
  const [visits, setVisits] = useState([]);
  const [vaccines, setVaccines] = useState([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState('vaccinations');

  // Modal: Record Vaccination
  const [isVacModalOpen, setIsVacModalOpen] = useState(false);
  const [selectedVaccineId, setSelectedVaccineId] = useState('');
  const [selectedBatchId, setSelectedBatchId] = useState('');
  const [givenDate, setGivenDate] = useState(new Date().toISOString().split('T')[0]);
  const [vacError, setVacError] = useState('');
  const [submittingVac, setSubmittingVac] = useState(false);

  // Modal: Add Visit
  const [isVisitModalOpen, setIsVisitModalOpen] = useState(false);
  const [visitDate, setVisitDate] = useState(new Date().toISOString().split('T')[0]);
  const [visitReason, setVisitReason] = useState('');
  const [visitNotes, setVisitNotes] = useState('');
  const [visitError, setVisitError] = useState('');
  const [submittingVisit, setSubmittingVisit] = useState(false);

  const loadData = async () => {
    try {
      setLoading(true);
      const [animalData, vacData, visitsData, vaccineCatalog] = await Promise.all([
        api.getAnimal(id),
        api.getAnimalVaccinations(id),
        api.getAnimalVisits(id),
        api.getVaccines()
      ]);
      setAnimal(animalData);
      setVaccinations(vacData || []);
      setVisits(visitsData || []);
      setVaccines(vaccineCatalog || []);
      if (vaccineCatalog?.length > 0) {
        setSelectedVaccineId(vaccineCatalog[0].id);
        if (vaccineCatalog[0].batches?.length > 0) {
          setSelectedBatchId(vaccineCatalog[0].batches[0].id);
        }
      }
    } catch (err) {
      console.error('Failed to load animal records:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [id]);

  const handleVaccineChange = (vacId) => {
    setSelectedVaccineId(vacId);
    const selected = vaccines.find(v => v.id === parseInt(vacId));
    if (selected && selected.batches?.length > 0) {
      setSelectedBatchId(selected.batches[0].id);
    } else {
      setSelectedBatchId('');
    }
  };

  const handleRecordVaccination = async (e) => {
    e.preventDefault();
    setVacError('');
    setSubmittingVac(true);

    try {
      await api.recordVaccination(id, {
        vaccineId: parseInt(selectedVaccineId),
        batchId: parseInt(selectedBatchId),
        givenOn: givenDate
      });
      setIsVacModalOpen(false);
      loadData();
    } catch (err) {
      setVacError(err.message || 'Failed to record vaccination');
    } finally {
      setSubmittingVac(false);
    }
  };

  const handleAddVisit = async (e) => {
    e.preventDefault();
    setVisitError('');
    setSubmittingVisit(true);

    try {
      await api.createVisit(id, {
        visitDate,
        reason: visitReason,
        notes: visitNotes
      });
      setIsVisitModalOpen(false);
      setVisitReason('');
      setVisitNotes('');
      loadData();
    } catch (err) {
      setVisitError(err.message || 'Failed to record visit');
    } finally {
      setSubmittingVisit(false);
    }
  };

  if (loading) {
    return <div style={{ padding: '60px', textAlign: 'center', color: 'var(--text-muted)' }}>Loading animal profile...</div>;
  }

  if (!animal) {
    return (
      <div style={{ padding: '40px', textAlign: 'center' }}>
        <p>Animal not found.</p>
        <Link to="/owners" className="btn btn-secondary btn-sm" style={{ marginTop: '12px' }}>
          Back to Clients
        </Link>
      </div>
    );
  }

  const selectedVaccineObj = vaccines.find(v => v.id === parseInt(selectedVaccineId));
  const availableBatches = selectedVaccineObj?.batches || [];

  return (
    <div>
      {/* Back Link */}
      <div style={{ marginBottom: '16px' }}>
        <Link to="/owners" style={{ display: 'inline-flex', alignItems: 'center', gap: '6px', fontSize: '14px', color: 'var(--text-muted)' }}>
          <ArrowLeft size={16} /> Back to Clients List
        </Link>
      </div>

      {/* Animal Header Profile Card */}
      <div className="card" style={{ padding: '24px', marginBottom: '24px' }}>
        <div style={{ display: 'flex', alignItems: 'flex-start', justifyContent: 'space-between', flexWrap: 'wrap', gap: '20px' }}>
          <div style={{ display: 'flex', gap: '16px', alignItems: 'center' }}>
            <div style={{
              width: '64px',
              height: '64px',
              borderRadius: '16px',
              background: 'var(--primary-light)',
              color: 'var(--primary)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center'
            }}>
              <Dog size={34} />
            </div>
            <div>
              <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '4px' }}>
                <h1 style={{ fontSize: '26px', color: 'var(--text-main)' }}>{animal.name}</h1>
                <span className="tag tag-info">{animal.species}</span>
                <span style={{ fontSize: '13px', color: 'var(--text-muted)' }}>({animal.sex})</span>
              </div>
              <p style={{ color: 'var(--text-muted)', fontSize: '14px' }}>
                Breed: <strong>{animal.breed || 'Unknown'}</strong> &bull; Born: <strong>{animal.birthDate}</strong>
              </p>
            </div>
          </div>

          {/* Owner details pill */}
          <div style={{
            background: 'var(--bg-main)',
            border: '1px solid var(--border-color)',
            borderRadius: '12px',
            padding: '12px 18px',
            fontSize: '13px'
          }}>
            <span style={{ fontSize: '11px', fontWeight: '700', textTransform: 'uppercase', color: 'var(--text-muted)' }}>
              OWNER CONTACT
            </span>
            <div style={{ fontWeight: '600', color: 'var(--text-main)', marginTop: '2px' }}>
              {animal.ownerName}
            </div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '6px', color: 'var(--text-muted)', marginTop: '4px' }}>
              <Phone size={13} /> {animal.ownerPhone}
            </div>
          </div>
        </div>

        {/* Action Buttons */}
        <div style={{ display: 'flex', gap: '12px', marginTop: '20px', paddingTop: '16px', borderTop: '1px solid #f1f5f9' }}>
          <button onClick={() => setIsVacModalOpen(true)} className="btn btn-primary btn-sm">
            <Syringe size={15} /> Record Vaccination
          </button>
          <button onClick={() => setIsVisitModalOpen(true)} className="btn btn-secondary btn-sm">
            <ClipboardList size={15} /> Add Clinical Visit
          </button>
        </div>
      </div>

      {/* Tabs */}
      <div style={{ display: 'flex', gap: '12px', borderBottom: '1px solid var(--border-color)', marginBottom: '20px' }}>
        <button
          onClick={() => setActiveTab('vaccinations')}
          style={{
            padding: '10px 16px',
            fontSize: '14px',
            fontWeight: '600',
            background: 'none',
            border: 'none',
            borderBottom: activeTab === 'vaccinations' ? '3px solid var(--primary)' : '3px solid transparent',
            color: activeTab === 'vaccinations' ? 'var(--primary)' : 'var(--text-muted)',
            cursor: 'pointer',
            display: 'flex',
            alignItems: 'center',
            gap: '8px'
          }}
        >
          <Syringe size={16} /> Vaccination History ({vaccinations.length})
        </button>

        <button
          onClick={() => setActiveTab('visits')}
          style={{
            padding: '10px 16px',
            fontSize: '14px',
            fontWeight: '600',
            background: 'none',
            border: 'none',
            borderBottom: activeTab === 'visits' ? '3px solid var(--primary)' : '3px solid transparent',
            color: activeTab === 'visits' ? 'var(--primary)' : 'var(--text-muted)',
            cursor: 'pointer',
            display: 'flex',
            alignItems: 'center',
            gap: '8px'
          }}
        >
          <ClipboardList size={16} /> Clinical Visits ({visits.length})
        </button>
      </div>

      {/* Tab 1: Vaccinations Table */}
      {activeTab === 'vaccinations' && (
        <div className="table-responsive">
          <table className="data-table">
            <thead>
              <tr>
                <th>Vaccine Name</th>
                <th>Batch Number</th>
                <th>Administered Date</th>
                <th>Next Due Date</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {vaccinations.length === 0 ? (
                <tr>
                  <td colSpan="5" style={{ textAlign: 'center', padding: '36px', color: 'var(--text-muted)' }}>
                    No vaccination history recorded for this animal.
                  </td>
                </tr>
              ) : (
                vaccinations.map((vac) => (
                  <tr key={vac.id}>
                    <td>
                      <strong>{vac.vaccineName}</strong>
                    </td>
                    <td>
                      <code style={{ background: '#f1f5f9', padding: '2px 6px', borderRadius: '4px', fontSize: '12px' }}>
                        {vac.batchNo}
                      </code>
                    </td>
                    <td>{vac.givenOn}</td>
                    <td>{vac.nextDueOn}</td>
                    <td>
                      {vac.overdue ? (
                        <span className="tag tag-danger">
                          <AlertCircle size={12} /> Overdue Booster
                        </span>
                      ) : (
                        <span className="tag tag-success">
                          <CheckCircle2 size={12} /> Current
                        </span>
                      )}
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      )}

      {/* Tab 2: Visits Table */}
      {activeTab === 'visits' && (
        <div className="table-responsive">
          <table className="data-table">
            <thead>
              <tr>
                <th>Visit Date</th>
                <th>Reason</th>
                <th>Attending Veterinarian</th>
                <th>Clinical Notes</th>
              </tr>
            </thead>
            <tbody>
              {visits.length === 0 ? (
                <tr>
                  <td colSpan="4" style={{ textAlign: 'center', padding: '36px', color: 'var(--text-muted)' }}>
                    No visit history logged for this animal.
                  </td>
                </tr>
              ) : (
                visits.map((vis) => (
                  <tr key={vis.id}>
                    <td style={{ fontWeight: '600' }}>{vis.visitDate}</td>
                    <td>{vis.reason}</td>
                    <td>{vis.vetName || 'Assigned Vet'}</td>
                    <td style={{ color: 'var(--text-muted)' }}>{vis.notes || '—'}</td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      )}

      {/* Modal: Record Vaccination */}
      {isVacModalOpen && (
        <div className="modal-overlay">
          <div className="modal-content">
            <div className="modal-header">
              <h3 style={{ fontSize: '18px' }}>Record Vaccination for {animal.name}</h3>
              <button onClick={() => setIsVacModalOpen(false)} style={{ background: 'none', border: 'none', cursor: 'pointer', color: '#94a3b8' }}>
                <X size={20} />
              </button>
            </div>
            <form onSubmit={handleRecordVaccination}>
              <div className="modal-body">
                {vacError && (
                  <div style={{ background: '#fee2e2', color: '#991b1b', padding: '12px 14px', borderRadius: '8px', fontSize: '13px', marginBottom: '16px', display: 'flex', gap: '8px' }}>
                    <ShieldAlert size={18} style={{ flexShrink: 0 }} />
                    <span>{vacError}</span>
                  </div>
                )}

                <div className="form-group">
                  <label className="form-label">Select Vaccine *</label>
                  <select
                    className="input-control"
                    value={selectedVaccineId}
                    onChange={(e) => handleVaccineChange(e.target.value)}
                  >
                    {vaccines.map((v) => (
                      <option key={v.id} value={v.id}>
                        {v.name} (Target: {v.speciesTarget})
                      </option>
                    ))}
                  </select>
                </div>

                <div className="form-group">
                  <label className="form-label">Select Vaccine Batch *</label>
                  <select
                    className="input-control"
                    value={selectedBatchId}
                    onChange={(e) => setSelectedBatchId(e.target.value)}
                    required
                  >
                    {availableBatches.length === 0 ? (
                      <option value="">No batches available</option>
                    ) : (
                      availableBatches.map((b) => (
                        <option 
                          key={b.id} 
                          value={b.id} 
                          disabled={b.quantityRemaining <= 0 || b.expired}
                        >
                          Batch #{b.batchNo} — Stock: {b.quantityRemaining} units — Expires: {b.expiryDate} {b.expired ? '(EXPIRED)' : b.quantityRemaining <= 0 ? '(EMPTY)' : ''}
                        </option>
                      ))
                    )}
                  </select>
                </div>

                <div className="form-group">
                  <label className="form-label">Date Administered *</label>
                  <input
                    type="date"
                    required
                    max={new Date().toISOString().split('T')[0]}
                    className="input-control"
                    value={givenDate}
                    onChange={(e) => setGivenDate(e.target.value)}
                  />
                </div>
              </div>
              <div className="modal-footer">
                <button type="button" onClick={() => setIsVacModalOpen(false)} className="btn btn-secondary">
                  Cancel
                </button>
                <button type="submit" disabled={submittingVac || !selectedBatchId} className="btn btn-primary">
                  {submittingVac ? 'Recording & Decrementing Stock...' : 'Confirm Vaccination'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Modal: Add Visit */}
      {isVisitModalOpen && (
        <div className="modal-overlay">
          <div className="modal-content">
            <div className="modal-header">
              <h3 style={{ fontSize: '18px' }}>Log Clinical Visit</h3>
              <button onClick={() => setIsVisitModalOpen(false)} style={{ background: 'none', border: 'none', cursor: 'pointer', color: '#94a3b8' }}>
                <X size={20} />
              </button>
            </div>
            <form onSubmit={handleAddVisit}>
              <div className="modal-body">
                {visitError && (
                  <div style={{ background: '#fee2e2', color: '#991b1b', padding: '10px 14px', borderRadius: '8px', fontSize: '13px', marginBottom: '16px' }}>
                    {visitError}
                  </div>
                )}
                <div className="form-group">
                  <label className="form-label">Visit Date *</label>
                  <input
                    type="date"
                    required
                    className="input-control"
                    value={visitDate}
                    onChange={(e) => setVisitDate(e.target.value)}
                  />
                </div>
                <div className="form-group">
                  <label className="form-label">Reason for Visit *</label>
                  <input
                    type="text"
                    required
                    className="input-control"
                    placeholder="e.g. Annual physical, Lameness evaluation"
                    value={visitReason}
                    onChange={(e) => setVisitReason(e.target.value)}
                  />
                </div>
                <div className="form-group">
                  <label className="form-label">Clinical Notes</label>
                  <textarea
                    rows={3}
                    className="input-control"
                    placeholder="Physical examination notes, treatment administered, diagnostic recommendations..."
                    value={visitNotes}
                    onChange={(e) => setVisitNotes(e.target.value)}
                  />
                </div>
              </div>
              <div className="modal-footer">
                <button type="button" onClick={() => setIsVisitModalOpen(false)} className="btn btn-secondary">
                  Cancel
                </button>
                <button type="submit" disabled={submittingVisit} className="btn btn-primary">
                  {submittingVisit ? 'Logging...' : 'Save Visit'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
