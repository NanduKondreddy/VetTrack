import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { api } from '../api/client';
import { useAuth } from '../context/AuthContext';
import { 
  Users, 
  Search, 
  Plus, 
  Phone, 
  Mail, 
  MapPin, 
  Edit2, 
  Trash2, 
  Dog, 
  ChevronLeft, 
  ChevronRight,
  X
} from 'lucide-react';

export default function OwnersPage() {
  const { isAdmin } = useAuth();
  const [owners, setOwners] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);

  // Modal State
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingOwner, setEditingOwner] = useState(null);
  const [formData, setFormData] = useState({ name: '', phone: '', email: '', city: '' });
  const [formError, setFormError] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  // Animal Register Modal for Owner
  const [isAnimalModalOpen, setIsAnimalModalOpen] = useState(false);
  const [selectedOwnerForAnimal, setSelectedOwnerForAnimal] = useState(null);
  const [animalFormData, setAnimalFormData] = useState({
    name: '',
    species: 'Dog',
    breed: '',
    birthDate: '',
    sex: 'Male'
  });

  const loadOwners = async (searchQuery = search, pageNumber = page) => {
    try {
      setLoading(true);
      const data = await api.getOwners({ query: searchQuery, page: pageNumber, size: 8 });
      setOwners(data.content || []);
      setTotalPages(data.totalPages || 1);
      setTotalElements(data.totalElements || 0);
    } catch (err) {
      console.error('Error fetching owners:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadOwners(search, page);
  }, [page]);

  const handleSearchSubmit = (e) => {
    e.preventDefault();
    setPage(0);
    loadOwners(search, 0);
  };

  const openCreateModal = () => {
    setEditingOwner(null);
    setFormData({ name: '', phone: '', email: '', city: '' });
    setFormError('');
    setIsModalOpen(true);
  };

  const openEditModal = (owner) => {
    setEditingOwner(owner);
    setFormData({
      name: owner.name,
      phone: owner.phone,
      email: owner.email || '',
      city: owner.city
    });
    setFormError('');
    setIsModalOpen(true);
  };

  const handleFormSubmit = async (e) => {
    e.preventDefault();
    setIsSubmitting(true);
    setFormError('');

    try {
      if (editingOwner) {
        await api.updateOwner(editingOwner.id, formData);
      } else {
        await api.createOwner(formData);
      }
      setIsModalOpen(false);
      loadOwners();
    } catch (err) {
      setFormError(err.message || 'Failed to save owner');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleDeleteOwner = async (id, name) => {
    if (!window.confirm(`Are you sure you want to delete owner '${name}' and all associated animals?`)) {
      return;
    }
    try {
      await api.deleteOwner(id);
      loadOwners();
    } catch (err) {
      alert(err.message || 'Failed to delete owner');
    }
  };

  const openAddAnimalModal = (owner) => {
    setSelectedOwnerForAnimal(owner);
    setAnimalFormData({
      name: '',
      species: 'Dog',
      breed: '',
      birthDate: new Date().toISOString().split('T')[0],
      sex: 'Male'
    });
    setFormError('');
    setIsAnimalModalOpen(true);
  };

  const handleAnimalSubmit = async (e) => {
    e.preventDefault();
    setIsSubmitting(true);
    setFormError('');

    try {
      await api.createAnimal({
        ownerId: selectedOwnerForAnimal.id,
        ...animalFormData
      });
      setIsAnimalModalOpen(false);
      loadOwners();
    } catch (err) {
      setFormError(err.message || 'Failed to register animal');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div>
      {/* Header & Controls */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '24px', flexWrap: 'wrap', gap: '16px' }}>
        <div>
          <h1 style={{ fontSize: '26px', color: 'var(--text-main)', marginBottom: '4px' }}>
            Clients &amp; Animal Records
          </h1>
          <p style={{ color: 'var(--text-muted)', fontSize: '14px' }}>
            Total clients registered: <strong>{totalElements}</strong>
          </p>
        </div>

        <button onClick={openCreateModal} className="btn btn-primary">
          <Plus size={18} /> Register New Client
        </button>
      </div>

      {/* Search Input Bar */}
      <div className="card" style={{ padding: '16px 20px', marginBottom: '24px' }}>
        <form onSubmit={handleSearchSubmit} style={{ display: 'flex', gap: '12px' }}>
          <div style={{ position: 'relative', flex: 1 }}>
            <Search size={18} style={{ position: 'absolute', left: '12px', top: '12px', color: '#94a3b8' }} />
            <input
              type="text"
              placeholder="Search by owner name, phone, email, or city..."
              className="input-control"
              style={{ paddingLeft: '38px' }}
              value={search}
              onChange={(e) => setSearch(e.target.value)}
            />
          </div>
          <button type="submit" className="btn btn-secondary">
            Search
          </button>
        </form>
      </div>

      {/* Owners Table */}
      <div className="table-responsive">
        <table className="data-table">
          <thead>
            <tr>
              <th>Client Name</th>
              <th>Phone</th>
              <th>Email</th>
              <th>City</th>
              <th>Animals Registered</th>
              <th style={{ textAlign: 'right' }}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan="6" style={{ textAlign: 'center', padding: '36px', color: 'var(--text-muted)' }}>
                  Loading clients...
                </td>
              </tr>
            ) : owners.length === 0 ? (
              <tr>
                <td colSpan="6" style={{ textAlign: 'center', padding: '36px', color: 'var(--text-muted)' }}>
                  No client records found.
                </td>
              </tr>
            ) : (
              owners.map((owner) => (
                <tr key={owner.id}>
                  <td>
                    <div style={{ fontWeight: '600', color: 'var(--text-main)' }}>{owner.name}</div>
                  </td>
                  <td>
                    <span style={{ display: 'flex', alignItems: 'center', gap: '6px', color: 'var(--text-muted)' }}>
                      <Phone size={14} /> {owner.phone}
                    </span>
                  </td>
                  <td>
                    {owner.email ? (
                      <span style={{ display: 'flex', alignItems: 'center', gap: '6px', color: 'var(--text-muted)' }}>
                        <Mail size={14} /> {owner.email}
                      </span>
                    ) : (
                      <span style={{ color: '#cbd5e1' }}>—</span>
                    )}
                  </td>
                  <td>
                    <span style={{ display: 'flex', alignItems: 'center', gap: '6px', color: 'var(--text-muted)' }}>
                      <MapPin size={14} /> {owner.city}
                    </span>
                  </td>
                  <td>
                    <span className="tag tag-info" style={{ fontWeight: '600' }}>
                      <Dog size={12} /> {owner.animalCount} animal(s)
                    </span>
                  </td>
                  <td style={{ textAlign: 'right' }}>
                    <div style={{ display: 'inline-flex', gap: '6px' }}>
                      <button 
                        onClick={() => openAddAnimalModal(owner)}
                        className="btn btn-secondary btn-sm"
                        title="Add Animal"
                        style={{ padding: '6px 10px', fontSize: '12px' }}
                      >
                        <Plus size={13} /> Add Animal
                      </button>
                      <button 
                        onClick={() => openEditModal(owner)}
                        className="btn btn-secondary btn-sm"
                        title="Edit Owner"
                        style={{ padding: '6px 8px' }}
                      >
                        <Edit2 size={14} />
                      </button>
                      {isAdmin && (
                        <button 
                          onClick={() => handleDeleteOwner(owner.id, owner.name)}
                          className="btn btn-danger btn-sm"
                          title="Delete Owner (Admin Only)"
                          style={{ padding: '6px 8px' }}
                        >
                          <Trash2 size={14} />
                        </button>
                      )}
                    </div>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {/* Pagination Controls */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginTop: '20px' }}>
        <span style={{ fontSize: '13px', color: 'var(--text-muted)' }}>
          Showing page <strong>{page + 1}</strong> of <strong>{totalPages}</strong>
        </span>
        <div style={{ display: 'flex', gap: '8px' }}>
          <button
            onClick={() => setPage((p) => Math.max(0, p - 1))}
            disabled={page === 0}
            className="btn btn-secondary btn-sm"
          >
            <ChevronLeft size={16} /> Previous
          </button>
          <button
            onClick={() => setPage((p) => Math.min(totalPages - 1, p + 1))}
            disabled={page >= totalPages - 1}
            className="btn btn-secondary btn-sm"
          >
            Next <ChevronRight size={16} />
          </button>
        </div>
      </div>

      {/* Modal: Create/Edit Owner */}
      {isModalOpen && (
        <div className="modal-overlay">
          <div className="modal-content">
            <div className="modal-header">
              <h3 style={{ fontSize: '18px' }}>{editingOwner ? 'Edit Client Profile' : 'Register New Client'}</h3>
              <button onClick={() => setIsModalOpen(false)} style={{ background: 'none', border: 'none', cursor: 'pointer', color: '#94a3b8' }}>
                <X size={20} />
              </button>
            </div>
            <form onSubmit={handleFormSubmit}>
              <div className="modal-body">
                {formError && (
                  <div style={{ background: '#fee2e2', color: '#991b1b', padding: '10px 14px', borderRadius: '8px', fontSize: '13px', marginBottom: '16px' }}>
                    {formError}
                  </div>
                )}
                <div className="form-group">
                  <label className="form-label">Full Name *</label>
                  <input
                    type="text"
                    required
                    className="input-control"
                    placeholder="e.g. Elena Rostova"
                    value={formData.name}
                    onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  />
                </div>
                <div className="form-group">
                  <label className="form-label">Phone Number *</label>
                  <input
                    type="text"
                    required
                    className="input-control"
                    placeholder="e.g. +1-555-019-2834"
                    value={formData.phone}
                    onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                  />
                </div>
                <div className="form-group">
                  <label className="form-label">Email Address</label>
                  <input
                    type="email"
                    className="input-control"
                    placeholder="e.g. client@vetmail.com"
                    value={formData.email}
                    onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                  />
                </div>
                <div className="form-group">
                  <label className="form-label">City *</label>
                  <input
                    type="text"
                    required
                    className="input-control"
                    placeholder="e.g. Austin"
                    value={formData.city}
                    onChange={(e) => setFormData({ ...formData, city: e.target.value })}
                  />
                </div>
              </div>
              <div className="modal-footer">
                <button type="button" onClick={() => setIsModalOpen(false)} className="btn btn-secondary">
                  Cancel
                </button>
                <button type="submit" disabled={isSubmitting} className="btn btn-primary">
                  {isSubmitting ? 'Saving...' : editingOwner ? 'Update Client' : 'Create Client'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Modal: Add Animal to Owner */}
      {isAnimalModalOpen && selectedOwnerForAnimal && (
        <div className="modal-overlay">
          <div className="modal-content">
            <div className="modal-header">
              <h3 style={{ fontSize: '18px' }}>Register Animal for {selectedOwnerForAnimal.name}</h3>
              <button onClick={() => setIsAnimalModalOpen(false)} style={{ background: 'none', border: 'none', cursor: 'pointer', color: '#94a3b8' }}>
                <X size={20} />
              </button>
            </div>
            <form onSubmit={handleAnimalSubmit}>
              <div className="modal-body">
                {formError && (
                  <div style={{ background: '#fee2e2', color: '#991b1b', padding: '10px 14px', borderRadius: '8px', fontSize: '13px', marginBottom: '16px' }}>
                    {formError}
                  </div>
                )}
                <div className="form-group">
                  <label className="form-label">Animal Name *</label>
                  <input
                    type="text"
                    required
                    className="input-control"
                    placeholder="e.g. Barnaby"
                    value={animalFormData.name}
                    onChange={(e) => setAnimalFormData({ ...animalFormData, name: e.target.value })}
                  />
                </div>
                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '14px' }}>
                  <div className="form-group">
                    <label className="form-label">Species *</label>
                    <select
                      className="input-control"
                      value={animalFormData.species}
                      onChange={(e) => setAnimalFormData({ ...animalFormData, species: e.target.value })}
                    >
                      <option value="Dog">Dog</option>
                      <option value="Cat">Cat</option>
                      <option value="Cattle">Cattle</option>
                      <option value="Poultry">Poultry</option>
                    </select>
                  </div>
                  <div className="form-group">
                    <label className="form-label">Sex *</label>
                    <select
                      className="input-control"
                      value={animalFormData.sex}
                      onChange={(e) => setAnimalFormData({ ...animalFormData, sex: e.target.value })}
                    >
                      <option value="Male">Male</option>
                      <option value="Female">Female</option>
                      <option value="Mixed">Mixed (Herd/Flock)</option>
                    </select>
                  </div>
                </div>
                <div className="form-group">
                  <label className="form-label">Breed</label>
                  <input
                    type="text"
                    className="input-control"
                    placeholder="e.g. Golden Retriever"
                    value={animalFormData.breed}
                    onChange={(e) => setAnimalFormData({ ...animalFormData, breed: e.target.value })}
                  />
                </div>
                <div className="form-group">
                  <label className="form-label">Birth Date *</label>
                  <input
                    type="date"
                    required
                    max={new Date().toISOString().split('T')[0]}
                    className="input-control"
                    value={animalFormData.birthDate}
                    onChange={(e) => setAnimalFormData({ ...animalFormData, birthDate: e.target.value })}
                  />
                </div>
              </div>
              <div className="modal-footer">
                <button type="button" onClick={() => setIsAnimalModalOpen(false)} className="btn btn-secondary">
                  Cancel
                </button>
                <button type="submit" disabled={isSubmitting} className="btn btn-primary">
                  {isSubmitting ? 'Registering...' : 'Register Animal'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
