import React, { useState, useEffect } from 'react';
import api from '../services/api';
import { useAuth } from '../context/AuthContext';
import { 
  ClipboardList, 
  Clock, 
  CheckCircle2, 
  AlertCircle, 
  Users, 
  TrendingUp,
  BarChart3
} from 'lucide-react';

const StatCard = ({ title, value, icon, color, trend }) => (
  <div className="glass-card stat-card">
    <div className={`icon-wrapper bg-${color}-soft`}>
      {React.cloneElement(icon, { size: 24, className: `text-${color}` })}
    </div>
    <div className="stat-content">
      <p className="stat-title">{title}</p>
      <h3 className="stat-value">{value}</h3>
      {trend && <span className="stat-trend">+{trend}% from yesterday</span>}
    </div>
  </div>
);

const Dashboard = () => {
  const { user } = useAuth();
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchStats();
  }, []);

  const fetchStats = async () => {
    try {
      const rolePath = user.role.replace('ROLE_', '').toLowerCase();
      const response = await api.get(`/dashboard/${rolePath}`);
      setStats(response.data);
    } catch (err) {
      console.error('Error fetching dashboard stats', err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <div className="loading-container">Loading Stats...</div>;

  return (
    <div className="dashboard-container">
      <div className="dashboard-header">
        <h1>Welcome Back, {user.fullName}</h1>
        <p>Here's what's happening with your complaints today.</p>
      </div>

      <div className="stats-grid">
        <StatCard 
          title="Total Complaints" 
          value={stats?.totalComplaints || 0} 
          icon={<ClipboardList />} 
          color="primary" 
        />
        <StatCard 
          title="In Progress" 
          value={stats?.inProgressComplaints || 0} 
          icon={<Clock />} 
          color="warning" 
        />
        <StatCard 
          title="Resolved" 
          value={stats?.resolvedComplaints || 0} 
          icon={<CheckCircle2 />} 
          color="success" 
        />
        <StatCard 
          title="SLA Breached" 
          value={stats?.slaBreachedComplaints || 0} 
          icon={<AlertCircle />} 
          color="danger" 
        />
      </div>

      <div className="row mt-4">
        <div className="col-8">
          <div className="glass-card table-section p-4">
            <div className="d-flex justify-between align-center mb-4">
              <h3>Recent Complaints</h3>
              <button className="btn btn-primary-outline">View All</button>
            </div>
            <div className="placeholder-content">
              <BarChart3 size={40} className="text-muted mb-2" />
              <p>Detailed analytics and charts will appear here.</p>
            </div>
          </div>
        </div>
        <div className="col-4">
          <div className="glass-card stats-summary p-4">
            <h3>Summary</h3>
            <div className="summary-list">
              <div className="summary-item">
                <Users size={18} />
                <span>Total Customers: {stats?.totalCustomers || 0}</span>
              </div>
              <div className="summary-item">
                <Users size={18} />
                <span>Total Agents: {stats?.totalAgents || 0}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <style dangerouslySetInnerHTML={{ __html: `
        .dashboard-container { animation: fadeIn 0.5s ease-out; }
        .dashboard-header { margin-bottom: 30px; }
        .dashboard-header h1 { font-size: 2rem; margin-bottom: 5px; }
        .dashboard-header p { color: var(--text-muted); }

        .stats-grid {
          display: grid;
          grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
          gap: 20px;
        }

        .stat-card {
          padding: 24px;
          display: flex;
          align-items: center;
          gap: 20px;
          transition: transform 0.3s ease;
        }

        .stat-card:hover { transform: translateY(-5px); }

        .icon-wrapper {
          width: 56px;
          height: 56px;
          border-radius: 16px;
          display: flex;
          align-items: center;
          justify-content: center;
        }

        .bg-primary-soft { background: rgba(0, 210, 255, 0.1); }
        .bg-warning-soft { background: rgba(245, 158, 11, 0.1); }
        .bg-success-soft { background: rgba(16, 185, 129, 0.1); }
        .bg-danger-soft { background: rgba(239, 68, 68, 0.1); }

        .text-primary { color: var(--primary); }
        .text-warning { color: var(--warning); }
        .text-success { color: var(--success); }
        .text-danger { color: var(--danger); }

        .stat-title { font-size: 0.9rem; color: var(--text-muted); margin-bottom: 4px; }
        .stat-value { font-size: 1.8rem; font-weight: 700; }
        .stat-trend { font-size: 0.75rem; color: var(--success); }

        .row { display: flex; gap: 20px; }
        .mt-4 { margin-top: 30px; }
        .col-8 { flex: 2; }
        .col-4 { flex: 1; }
        .d-flex { display: flex; }
        .flex-column { flex-direction: column; }
        .justify-between { justify-content: space-between; }
        .align-center { align-items: center; }
        .mb-4 { margin-bottom: 20px; }
        .p-4 { padding: 24px; }

        .btn-primary-outline { 
          background: transparent; 
          border: 1px solid var(--primary); 
          color: var(--primary);
          padding: 8px 16px;
          border-radius: 8px;
          font-size: 0.9rem;
        }

        .placeholder-content {
          height: 200px;
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          color: var(--text-muted);
          border: 1px dashed var(--glass-border);
          border-radius: 12px;
        }

        .summary-list { display: flex; flex-direction: column; gap: 15px; margin-top: 20px; }
        .summary-item { display: flex; align-items: center; gap: 12px; font-size: 1rem; }

        @keyframes fadeIn {
          from { opacity: 0; transform: translateY(10px); }
          to { opacity: 1; transform: translateY(0); }
        }

        @media (max-width: 1200px) {
          .row { flex-direction: column; }
        }
      `}} />
    </div>
  );
};

export default Dashboard;
