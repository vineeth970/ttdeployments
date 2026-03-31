import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../services/api';
import { Search, ChevronLeft, Calendar, User, Clock, AlertTriangle } from 'lucide-react';
import './Auth.css';

const PublicTrack = () => {
  const [refNumber, setRefNumber] = useState('');
  const [complaint, setComplaint] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleTrack = async (e) => {
    e.preventDefault();
    if (!refNumber.trim()) return;
    
    setLoading(true);
    setError('');
    setComplaint(null);
    
    try {
      const response = await api.get(`/complaints/track/${refNumber}`);
      setComplaint(response.data);
    } catch (err) {
      setError('Track ID not found. Please verify the reference number.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container p-6">
      <div className="auth-bg-blob"></div>
      
      <div className="auth-card glass-card" style={{maxWidth: '600px'}}>
        <button className="btn-back mb-4" onClick={() => navigate('/login')}>
          <ChevronLeft size={18} />
          Back to Login
        </button>

        <div className="auth-header text-center">
          <h1>Track Complaint</h1>
          <p>Instantly check the status of your request.</p>
        </div>

        <form onSubmit={handleTrack} className="track-form d-flex gap-2 mb-8">
          <input 
            type="text" 
            placeholder="Enter Ref Number (e.g. COMP-XXXXX)" 
            value={refNumber}
            onChange={(e) => setRefNumber(e.target.value)}
            className="flex-1"
            style={{padding: '14px', borderRadius: '12px'}}
          />
          <button type="submit" className="btn btn-primary" disabled={loading}>
            <Search size={20} />
            {loading ? 'Searching...' : 'Track'}
          </button>
        </form>

        {error && (
          <div className="error-alert animate-fadeIn mb-0">
            <AlertTriangle size={20} />
            <span>{error}</span>
          </div>
        )}

        {complaint && (
          <div className="track-result animate-slideIn">
            <div className="result-header p-4 glass mb-4">
              <span className="badge badge-purple mb-2">{complaint.status}</span>
              <h3>{complaint.title}</h3>
            </div>
            
            <div className="result-details grid grid-cols-2 gap-4">
              <div className="result-item">
                <Calendar size={14} className="text-muted" />
                <span className="label">Created:</span>
                <span className="val">{new Date(complaint.createdAt).toLocaleDateString()}</span>
              </div>
              <div className="result-item">
                <Clock size={14} className="text-muted" />
                <span className="label">Deadline:</span>
                <span className="val">{new Date(complaint.slaDeadline).toLocaleDateString()}</span>
              </div>
              <div className="result-item">
                <User size={14} className="text-muted" />
                <span className="label">Assigned:</span>
                <span className="val">{complaint.assignedAgent?.fullName || 'Not Assigned'}</span>
              </div>
            </div>
            
            <div className="resolution-box mt-6 p-4 glass-card border-success" style={{background: 'rgba(16, 185, 129, 0.05)'}}>
              <h4 className="text-success mb-2">Current Resolution status</h4>
              <p className="text-muted">{complaint.resolutionNote || 'The status is currently in progress. Please check back later for updates.'}</p>
            </div>
          </div>
        )}
      </div>

      <style dangerouslySetInnerHTML={{ __html: `
        .text-center { text-align: center; }
        .grid { display: grid; }
        .grid-cols-2 { grid-template-columns: 1fr 1fr; }
        .track-result { border-top: 1px solid var(--glass-border); padding-top: 30px; }
        .result-details { background: rgba(255, 255, 255, 0.02); padding: 15px; border-radius: 12px; }
        .result-item { display: flex; align-items: center; gap: 8px; font-size: 0.9rem; }
        .result-item .label { color: var(--text-muted); }
        .result-item .val { font-weight: 500; }
        
        .animate-slideIn { animation: slideIn 0.5s ease-out; }
      `}} />
    </div>
  );
};

export default PublicTrack;
