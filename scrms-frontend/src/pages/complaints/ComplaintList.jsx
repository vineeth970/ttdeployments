import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../services/api';
import { useAuth } from '../../context/AuthContext';
import { 
  Plus, 
  Search, 
  Filter, 
  MoreVertical, 
  Calendar, 
  User as UserIcon,
  SearchCheck,
  Eye
} from 'lucide-react';
import './Complaints.css';

const StatusBadge = ({ status }) => {
  const statusMap = {
    'NEW': 'blue',
    'ASSIGNED': 'indigo',
    'IN_PROGRESS': 'yellow',
    'AWAITING_CUSTOMER': 'purple',
    'RESOLVED': 'green',
    'CLOSED': 'gray',
    'REJECTED': 'red'
  };
  
  return (
    <span className={`badge badge-${statusMap[status] || 'blue'}`}>
      {status.replace('_', ' ')}
    </span>
  );
};

const PriorityIndicator = ({ priority }) => {
  const priorityMap = {
    'LOW': 'bg-gray-500',
    'MEDIUM': 'bg-blue-500',
    'HIGH': 'bg-orange-500',
    'CRITICAL': 'bg-red-600'
  };
  
  return (
    <div className="priority-tag">
      <div className={`priority-dot ${priorityMap[priority]}`}></div>
      <span>{priority}</span>
    </div>
  );
};

const ComplaintList = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [complaints, setComplaints] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [filterStatus, setFilterStatus] = useState('ALL');

  useEffect(() => {
    fetchComplaints();
  }, [user.role]);

  const fetchComplaints = async () => {
    try {
      let endpoint = '/complaints';
      if (user.role === 'ROLE_CUSTOMER') endpoint = '/complaints/my';
      else if (user.role === 'ROLE_AGENT') endpoint = '/complaints/assigned';
      
      const response = await api.get(endpoint);
      setComplaints(response.data);
    } catch (err) {
      console.error('Error fetching complaints', err);
    } finally {
      setLoading(false);
    }
  };

  const filteredComplaints = complaints.filter(c => {
    const matchesSearch = c.title.toLowerCase().includes(searchTerm.toLowerCase()) || 
                         c.referenceNumber.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesStatus = filterStatus === 'ALL' || c.status === filterStatus;
    return matchesSearch && matchesStatus;
  });

  if (loading) return <div className="loading-container">Loading Complaints...</div>;

  return (
    <div className="complaints-page">
      <div className="complaints-header">
        <div className="header-info">
          <h1>Complaints</h1>
          <p>Manage and track all resolution requests.</p>
        </div>
        {user.role === 'ROLE_CUSTOMER' && (
          <button className="btn btn-primary" onClick={() => navigate('/submit-complaint')}>
            <Plus size={20} />
            <span>New Complaint</span>
          </button>
        )}
      </div>

      <div className="glass-card filter-bar">
        <div className="search-box">
          <Search size={18} className="search-icon" />
          <input 
            type="text" 
            placeholder="Search by ID or Title..." 
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>
        <div className="filter-options">
          <Filter size={18} className="filter-icon" />
          <select value={filterStatus} onChange={(e) => setFilterStatus(e.target.value)}>
            <option value="ALL">All Status</option>
            <option value="NEW">New</option>
            <option value="IN_PROGRESS">In Progress</option>
            <option value="RESOLVED">Resolved</option>
            <option value="CLOSED">Closed</option>
          </select>
        </div>
      </div>

      <div className="glass-card list-section">
        {filteredComplaints.length === 0 ? (
          <div className="empty-state">
            <SearchCheck size={60} />
            <h3>No complaints found</h3>
            <p>Try adjusting your search or filters.</p>
          </div>
        ) : (
          <div className="table-responsive">
            <table className="complaints-table">
              <thead>
                <tr>
                  <th>REF NUMBER</th>
                  <th>TITLE</th>
                  <th>STATUS</th>
                  <th>PRIORITY</th>
                  <th>{user.role === 'ROLE_CUSTOMER' ? 'ASSIGNED TO' : 'CUSTOMER'}</th>
                  <th>CREATED AT</th>
                  <th>ACTIONS</th>
                </tr>
              </thead>
              <tbody>
                {filteredComplaints.map(c => (
                  <tr key={c.id}>
                    <td className="ref-cell">#{c.referenceNumber}</td>
                    <td className="title-cell">
                      <div className="title-wrapper">
                        <span className="complaint-title">{c.title}</span>
                        <span className="category-tag">{c.category}</span>
                      </div>
                    </td>
                    <td><StatusBadge status={c.status} /></td>
                    <td><PriorityIndicator priority={c.priority} /></td>
                    <td>
                      <div className="user-cell">
                        <UserIcon size={14} />
                        <span>
                          {user.role === 'ROLE_CUSTOMER' 
                            ? (c.assignedAgent?.fullName || 'Pending')
                            : (c.customer?.fullName || 'N/A')}
                        </span>
                      </div>
                    </td>
                    <td className="date-cell">
                      <Calendar size={14} />
                      <span>{new Date(c.createdAt).toLocaleDateString()}</span>
                    </td>
                    <td className="actions-cell">
                      <button className="btn-icon" title="View Details" onClick={() => navigate(`/complaints/${c.id}`)}>
                        <Eye size={18} />
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
};

export default ComplaintList;
