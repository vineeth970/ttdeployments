import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../services/api';
import { 
  UserPlus, 
  Search, 
  Mail, 
  Phone, 
  MapPin, 
  Shield, 
  User,
  MoreVertical,
  CheckCircle2
} from 'lucide-react';

const UserList = () => {
  const navigate = useNavigate();
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    try {
      const response = await api.get('/users');
      setUsers(response.data);
    } catch (err) {
      setError('Failed to fetch users');
    } finally {
      setLoading(false);
    }
  };

  const toggleStatus = async (id) => {
    try {
      await api.patch(`/users/${id}/toggle-status`);
      fetchUsers();
    } catch (err) {
      alert('Failed to toggle status');
    }
  };

  if (loading) return <div className="loading-container">Loading Users...</div>;

  return (
    <div className="users-page">
      <div className="complaints-header">
        <div className="header-info">
          <h1>User Management</h1>
          <p>Manage access levels and departments for all staff members.</p>
        </div>
        <button className="btn btn-primary" onClick={() => navigate('/users/new')}>
          <UserPlus size={18} />
          <span>Add New User</span>
        </button>
      </div>

      <div className="glass-card table-section">
         <div className="table-responsive">
            <table className="complaints-table">
              <thead>
                <tr>
                  <th>NAME & PROFILE</th>
                  <th>EMAIL</th>
                  <th>ROLE</th>
                  <th>DEPARTMENT</th>
                  <th>STATUS</th>
                  <th>ACTIONS</th>
                </tr>
              </thead>
              <tbody>
                {users.map(u => (
                  <tr key={u.id}>
                    <td>
                      <div className="user-cell">
                        <div className="avatar">{u.fullName?.charAt(0)}</div>
                        <span>{u.fullName}</span>
                      </div>
                    </td>
                    <td>
                       <div className="user-cell text-muted">
                        <Mail size={14} />
                        <span>{u.email}</span>
                       </div>
                    </td>
                    <td>
                      <div className="badge badge-indigo">
                        {u.role?.replace('ROLE_', '')}
                      </div>
                    </td>
                    <td>
                      <div className="user-cell">
                        <MapPin size={14} />
                        <span>{u.department}</span>
                      </div>
                    </td>
                    <td>
                       <span className={`badge ${u.active ? 'badge-green' : 'badge-red'}`}>
                         {u.active ? 'Active' : 'Deactivated'}
                       </span>
                    </td>
                    <td className="actions-cell">
                       <button className="btn-icon" onClick={() => toggleStatus(u.id)}>
                         {u.active ? <Shield size={18} /> : <CheckCircle2 size={18} />}
                       </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
         </div>
      </div>

      <style dangerouslySetInnerHTML={{ __html: `
        .users-page { animation: fadeIn 0.4s ease-out; }
        .text-muted { color: var(--text-muted); }
      `}} />
    </div>
  );
};

export default UserList;
