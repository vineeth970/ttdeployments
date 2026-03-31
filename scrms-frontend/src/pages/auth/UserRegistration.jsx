import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../services/api';
import { UserPlus, ChevronLeft, MapPin, Phone, Mail, Lock, ShieldCheck } from 'lucide-react';

const UserRegistration = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    fullName: '',
    email: '',
    password: '',
    role: 'ROLE_CUSTOMER',
    department: '',
    phone: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    
    try {
      await api.post('/auth/register', formData);
      setSuccess(true);
      setTimeout(() => navigate('/users'), 2000);
    } catch (err) {
      setError(err?.message || 'Failed to register user. Email might already exist.');
    } finally {
      setLoading(false);
    }
  };

  if (success) {
    return (
      <div className="glass-card p-10 text-center animate-fadeIn">
        <ShieldCheck size={80} className="text-success mb-4" />
        <h2>User Registered Successfully!</h2>
        <p className="text-muted mt-2">New user account has been created.</p>
      </div>
    );
  }

  return (
    <div className="max-w-2xl mx-auto">
      <button className="btn-back mb-6" onClick={() => navigate(-1)}>
        <ChevronLeft size={18} />
        Back
      </button>

      <div className="glass-card p-8">
        <div className="form-header mb-8">
          <h1>Register New User</h1>
          <p className="text-muted">Create a new system user with specific privileges.</p>
        </div>

        {error && <div className="error-alert mb-6">{error}</div>}

        <form onSubmit={handleSubmit} className="registration-form grid grid-cols-2 gap-x-6 gap-y-4">
          <div className="form-group col-span-2">
            <label>Full Name</label>
            <div className="input-with-icon">
              <User size={18} />
              <input 
                name="fullName"
                type="text" 
                placeholder="John Doe"
                value={formData.fullName}
                onChange={(e) => setFormData({...formData, fullName: e.target.value})}
                required
              />
            </div>
          </div>

          <div className="form-group">
            <label>Email Address</label>
            <div className="input-with-icon">
              <Mail size={18} />
              <input 
                name="email"
                type="email" 
                placeholder="john@example.com"
                value={formData.email}
                onChange={(e) => setFormData({...formData, email: e.target.value})}
                required
              />
            </div>
          </div>

          <div className="form-group">
            <label>Password</label>
            <div className="input-with-icon">
              <Lock size={18} />
              <input 
                name="password"
                type="password" 
                placeholder="••••••••"
                value={formData.password}
                onChange={(e) => setFormData({...formData, password: e.target.value})}
                required
              />
            </div>
          </div>

          <div className="form-group">
            <label>System Role</label>
            <div className="input-with-icon">
              <ShieldCheck size={18} />
              <select 
                value={formData.role}
                onChange={(e) => setFormData({...formData, role: e.target.value})}
              >
                <option value="ROLE_CUSTOMER">Customer</option>
                <option value="ROLE_AGENT">Support Agent</option>
                <option value="ROLE_MANAGER">Department Manager</option>
                <option value="ROLE_ADMIN">Administrator</option>
              </select>
            </div>
          </div>

          <div className="form-group">
            <label>Department</label>
            <div className="input-with-icon">
              <MapPin size={18} />
              <input 
                name="department"
                type="text" 
                placeholder="E.g., Tech, Billing"
                value={formData.department}
                onChange={(e) => setFormData({...formData, department: e.target.value})}
                required
              />
            </div>
          </div>

          <div className="form-group col-span-2">
            <label>Phone Number (Optional)</label>
            <div className="input-with-icon">
              <Phone size={18} />
              <input 
                name="phone"
                type="tel" 
                placeholder="+1 234 567 890"
                value={formData.phone}
                onChange={(e) => setFormData({...formData, phone: e.target.value})}
              />
            </div>
          </div>

          <div className="form-actions d-flex gap-4 col-span-2 mt-6">
             <button type="submit" className="btn btn-primary btn-block" disabled={loading}>
                <UserPlus size={18} />
                {loading ? 'Creating Account...' : 'Register User'}
             </button>
          </div>
        </form>
      </div>

      <style dangerouslySetInnerHTML={{ __html: `
        .col-span-2 { grid-column: span 2; }
        .input-with-icon { 
          display: flex; align-items: center; gap: 10px; 
          background: rgba(255, 255, 255, 0.05); border: 1px solid var(--glass-border);
          border-radius: 12px; padding: 0 16px;
        }
        .input-with-icon input, .input-with-icon select {
          flex: 1; background: none !important; border: none !important; padding: 12px 0 !important;
        }
        .input-with-icon svg { color: var(--text-muted); }
      `}} />
    </div>
  );
};

export default UserRegistration;
