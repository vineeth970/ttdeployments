import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../services/api';
import { AlertCircle, CheckCircle2, ChevronLeft, Send } from 'lucide-react';

const CreateComplaint = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    category: 'TECHNICAL',
    priority: 'MEDIUM'
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    if (formData.description.length < 20) {
      setError('Description must be at least 20 characters long.');
      setLoading(false);
      return;
    }

    try {
      await api.post('/complaints', formData);
      setSuccess(true);
      setTimeout(() => navigate('/complaints'), 2000);
    } catch (err) {
      setError(err?.message || 'Failed to submit complaint. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  if (success) {
    return (
      <div className="success-state glass-card p-10 mt-10 text-center animate-fadeIn">
        <CheckCircle2 size={80} className="text-success mb-4" />
        <h2>Complaint Submitted Successfully!</h2>
        <p className="text-muted mt-2">Redirecting you to your complaints list...</p>
      </div>
    );
  }

  return (
    <div className="create-complaint-container max-w-2xl mx-auto">
      <button className="btn-back mb-6" onClick={() => navigate(-1)}>
        <ChevronLeft size={18} />
        Back
      </button>

      <div className="glass-card p-8">
        <div className="form-header mb-8">
          <h1>Submit New Complaint</h1>
          <p className="text-muted">Fill in the details below to help us resolve your issue.</p>
        </div>

        {error && (
          <div className="error-alert mb-6">
            <AlertCircle size={20} />
            <span>{error}</span>
          </div>
        )}

        <form onSubmit={handleSubmit} className="complaint-form">
          <div className="form-group mb-6">
            <label>Subject / Title</label>
            <input 
              name="title"
              type="text" 
              placeholder="E.g., Internet not working, Billing discrepancy"
              value={formData.title}
              onChange={handleChange}
              required
            />
          </div>

          <div className="row mb-6">
            <div className="col-6 pr-2">
              <div className="form-group">
                <label>Category</label>
                <select name="category" value={formData.category} onChange={handleChange}>
                  <option value="TECHNICAL">Technical Support</option>
                  <option value="BILLING">Billing Issue</option>
                  <option value="SERVICE">Service Request</option>
                  <option value="PRODUCT">Product Defect</option>
                  <option value="DELIVERY">Delivery Problem</option>
                  <option value="REFUND">Refund Request</option>
                  <option value="OTHER">Other</option>
                </select>
              </div>
            </div>
            <div className="col-6 pl-2">
              <div className="form-group">
                <label>Priority</label>
                <select name="priority" value={formData.priority} onChange={handleChange}>
                  <option value="LOW">Low</option>
                  <option value="MEDIUM">Medium</option>
                  <option value="HIGH">High</option>
                  <option value="CRITICAL">Critical</option>
                </select>
              </div>
            </div>
          </div>

          <div className="form-group mb-8">
            <label>Detailed Description</label>
            <textarea 
              name="description"
              rows="6"
              placeholder="Please provide as much detail as possible (min. 20 characters)..."
              value={formData.description}
              onChange={handleChange}
              required
            ></textarea>
            <span className="char-count">{formData.description.length} / 20 characters min</span>
          </div>

          <div className="form-actions d-flex gap-4">
            <button 
              type="button" 
              className="btn btn-outline flex-1" 
              onClick={() => navigate(-1)}
            >
              Cancel
            </button>
            <button 
              type="submit" 
              className="btn btn-primary flex-2" 
              disabled={loading}
            >
              {loading ? 'Submitting...' : (
                <>
                  <Send size={18} />
                  Submit Complaint
                </>
              )}
            </button>
          </div>
        </form>
      </div>

      <style dangerouslySetInnerHTML={{ __html: `
        .max-w-2xl { max-width: 800px; margin: 0 auto; }
        .mx-auto { margin-left: auto; margin-right: auto; }
        .p-8 { padding: 32px; }
        .p-10 { padding: 40px; }
        .mt-10 { margin-top: 40px; }
        .mb-6 { margin-bottom: 24px; }
        .mb-8 { margin-bottom: 32px; }
        .pr-2 { padding-right: 8px; }
        .pl-2 { padding-left: 8px; }
        .flex-1 { flex: 1; }
        .flex-2 { flex: 2; }
        .gap-4 { gap: 16px; }

        .btn-back {
          background: none;
          display: flex;
          align-items: center;
          gap: 6px;
          color: var(--text-muted);
          font-weight: 500;
          transition: color 0.3s;
        }
        .btn-back:hover { color: var(--primary); }

        .form-header h1 { font-size: 1.8rem; margin-bottom: 8px; }

        .error-alert {
          background: rgba(239, 68, 68, 0.1);
          color: var(--danger);
          padding: 12px 16px;
          border-radius: 12px;
          display: flex;
          align-items: center;
          gap: 12px;
          font-size: 0.95rem;
          border: 1px solid rgba(239, 68, 68, 0.2);
        }

        .complaint-form label {
          display: block;
          font-size: 0.9rem;
          color: var(--text-muted);
          margin-bottom: 10px;
          font-weight: 500;
        }

        .complaint-form input, 
        .complaint-form select, 
        .complaint-form textarea {
          width: 100%;
          background: rgba(255, 255, 255, 0.05);
          border: 1px solid var(--glass-border);
          border-radius: 12px;
          padding: 12px 16px;
          color: var(--text-main);
          outline: none;
          transition: border-color 0.3s;
        }

        .complaint-form input:focus, 
        .complaint-form select:focus, 
        .complaint-form textarea:focus {
          border-color: var(--primary);
        }

        .complaint-form textarea { resize: vertical; min-height: 120px; }

        .char-count {
          display: block;
          text-align: right;
          font-size: 0.8rem;
          color: var(--text-muted);
          margin-top: 8px;
        }

        .success-state {
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
        }

        .animate-fadeIn {
          animation: fadeIn 0.4s ease-out;
        }

        @media (max-width: 600px) {
          .row { flex-direction: column; }
          .pr-2, .pl-2 { padding: 0; }
          .mb-6 { margin-bottom: 15px; }
        }
      `}} />
    </div>
  );
};

export default CreateComplaint;
