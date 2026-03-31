import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../../services/api';
import { useAuth } from '../../context/AuthContext';
import { 
  ChevronLeft, 
  User, 
  Calendar, 
  MessageSquare, 
  History, 
  Send,
  Trash2,
  UserPlus,
  CheckCircle,
  AlertOctagon,
  Clock
} from 'lucide-react';
import './Complaints.css';

const ComplaintDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  
  const [complaint, setComplaint] = useState(null);
  const [comments, setComments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [commentText, setCommentText] = useState('');
  const [submittingComment, setSubmittingComment] = useState(false);
  const [updatingStatus, setUpdatingStatus] = useState(false);
  const [activeTab, setActiveTab] = useState('comments'); // 'comments' or 'audit'

  useEffect(() => {
    fetchComplaint();
  }, [id]);

  const fetchComplaint = async () => {
    try {
      const response = await api.get(`/complaints/${id}`);
      setComplaint(response.data);
      setComments(response.data.comments || []);
    } catch (err) {
      console.error('Error fetching complaint details', err);
    } finally {
      setLoading(false);
    }
  };

  const handleAddComment = async (e) => {
    e.preventDefault();
    if (!commentText.trim()) return;
    
    setSubmittingComment(true);
    try {
      const response = await api.post(`/complaints/${id}/comments`, { 
        content: commentText,
        internal: false 
      });
      setComments([...comments, response.data]);
      setCommentText('');
    } catch (err) {
      alert('Failed to add comment');
    } finally {
      setSubmittingComment(false);
    }
  };

  const updateStatus = async (newStatus) => {
    if (!window.confirm(`Change status to ${newStatus}?`)) return;
    
    setUpdatingStatus(true);
    try {
      await api.patch(`/complaints/${id}/status`, { status: newStatus });
      fetchComplaint();
    } catch (err) {
      alert('Failed to update status');
    } finally {
      setUpdatingStatus(false);
    }
  };

  if (loading) return <div className="loading-container">Loading Details...</div>;
  if (!complaint) return <div>Complaint not found.</div>;

  return (
    <div className="detail-page">
      <button className="btn-back mb-6" onClick={() => navigate('/complaints')}>
        <ChevronLeft size={18} />
        Back to List
      </button>

      <div className="detail-header glass-card p-6 mb-6">
        <div className="d-flex justify-between align-center mb-6">
          <div className="header-meta">
            <span className="ref-number">#{complaint.referenceNumber}</span>
            <h1 className="detail-title">{complaint.title}</h1>
          </div>
          <div className="header-status">
            <div className="status-indicator">
              <span className="label">Status:</span>
              <span className={`badge badge-large badge-blue`}>{complaint.status}</span>
            </div>
          </div>
        </div>

        <div className="detail-stats-bar">
          <div className="stat-item">
            <Calendar size={16} />
            <span>Created: {new Date(complaint.createdAt).toLocaleString()}</span>
          </div>
          <div className="stat-item">
            <Clock size={16} />
            <span>SLA Deadline: {new Date(complaint.slaDeadline).toLocaleString()}</span>
          </div>
          <div className="stat-item">
            <AlertOctagon size={16} />
            <span>Priority: {complaint.priority}</span>
          </div>
        </div>
      </div>

      <div className="row">
        <div className="col-8">
          <div className="glass-card mb-6 p-6">
            <h3>Description</h3>
            <div className="description-text mt-4">
              {complaint.description}
            </div>
          </div>

          <div className="glass-card tabs-section">
            <div className="tabs-header">
              <button 
                className={`tab-btn ${activeTab === 'comments' ? 'active' : ''}`}
                onClick={() => setActiveTab('comments')}
              >
                <MessageSquare size={18} />
                Comments ({comments.length})
              </button>
              <button 
                className={`tab-btn ${activeTab === 'audit' ? 'active' : ''}`}
                onClick={() => setActiveTab('audit')}
              >
                <History size={18} />
                Audit Logs ({complaint.auditLogs?.length || 0})
              </button>
            </div>

            <div className="tab-content p-6">
              {activeTab === 'comments' ? (
                <div className="comments-tab">
                  <div className="comments-list mb-6">
                    {comments.map(comment => (
                      <div key={comment.id} className="comment-item">
                        <div className="comment-header">
                          <div className="comment-author">
                            <div className="mini-avatar">{comment.author?.fullName?.charAt(0)}</div>
                            <span className="author-name">{comment.author?.fullName}</span>
                          </div>
                          <span className="comment-time">{new Date(comment.createdAt).toLocaleString()}</span>
                        </div>
                        <div className="comment-body">
                          {comment.content}
                        </div>
                      </div>
                    ))}
                  </div>

                  <form onSubmit={handleAddComment} className="comment-form-box">
                    <textarea 
                      placeholder="Add a comment or update..." 
                      value={commentText}
                      onChange={(e) => setCommentText(e.target.value)}
                    ></textarea>
                    <button type="submit" className="btn btn-primary mt-2" disabled={submittingComment}>
                      <Send size={16} />
                      Post Comment
                    </button>
                  </form>
                </div>
              ) : (
                <div className="audit-tab">
                  <div className="timeline">
                    {complaint.auditLogs?.map(log => (
                      <div key={log.id} className="timeline-item">
                        <div className="timeline-marker"></div>
                        <div className="timeline-content">
                          <p className="log-action">{log.action}</p>
                          <p className="log-msg">{log.details}</p>
                          <div className="log-meta">
                            <span>{log.performedBy?.fullName}</span>
                            <span>•</span>
                            <span>{new Date(log.performedAt).toLocaleString()}</span>
                          </div>
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              )}
            </div>
          </div>
        </div>

        <div className="col-4">
          <div className="glass-card mb-6 p-6">
            <h3>Assignments</h3>
            <div className="assignment-info mt-4">
              <div className="assign-item">
                <span className="label">Customer:</span>
                <p className="val">{complaint.customer?.fullName}</p>
              </div>
              <div className="assign-item">
                <span className="label">Assigned Agent:</span>
                <p className="val">{complaint.assignedAgent?.fullName || 'Not Assigned'}</p>
              </div>
              <div className="assign-item">
                <span className="label">Manager:</span>
                <p className="val">{complaint.manager?.fullName || 'Unassigned'}</p>
              </div>
            </div>
          </div>

          {(user.role === 'ROLE_ADMIN' || user.role === 'ROLE_MANAGER' || user.role === 'ROLE_AGENT') && (
            <div className="glass-card p-6 action-card">
              <h3>Management Actions</h3>
              <div className="action-buttons mt-4 d-flex flex-column gap-3">
                {complaint.status !== 'RESOLVED' && (
                  <button className="btn btn-success btn-block" onClick={() => updateStatus('RESOLVED')} disabled={updatingStatus}>
                    <CheckCircle size={18} />
                    Mark as Resolved
                  </button>
                )}
                {user.role !== 'ROLE_AGENT' && !complaint.assignedAgent && (
                   <button className="btn btn-primary btn-block" onClick={() => api.patch(`/complaints/${id}/auto-assign`).then(fetchComplaint)}>
                    <UserPlus size={18} />
                    Auto-Assign Agent
                   </button>
                )}
                <button className="btn btn-outline btn-block text-danger border-danger" style={{color:'#ef4444', borderColor:'#ef4444'}}>
                  <Trash2 size={18} />
                  Delete (Admin)
                </button>
              </div>
            </div>
          )}
        </div>
      </div>

      <style dangerouslySetInnerHTML={{ __html: `
        .detail-page { animation: fadeIn 0.4s ease-out; }
        .detail-title { font-size: 2.2rem; font-weight: 700; margin-top: 5px; }
        .ref-number { color: var(--primary); font-family: monospace; font-weight: 600; font-size: 1.1rem; }
        .detail-stats-bar { display: flex; gap: 30px; padding-top: 20px; border-top: 1px solid var(--glass-border); }
        .stat-item { display: flex; align-items: center; gap: 10px; color: var(--text-muted); font-size: 0.9rem; }
        
        .description-text { line-height: 1.7; color: var(--text-main); font-size: 1.1rem; white-space: pre-wrap; }

        /* Tabs */
        .tabs-header { display: flex; border-bottom: 1px solid var(--glass-border); }
        .tab-btn { 
          flex: 1; padding: 20px; background: none; color: var(--text-muted); 
          display: flex; align-items: center; justify-content: center; gap: 10px;
          font-weight: 600; border-bottom: 2px solid transparent; transition: var(--transition);
        }
        .tab-btn.active { color: var(--primary); border-bottom-color: var(--primary); background: rgba(0, 210, 255, 0.05); }

        /* Comments */
        .comment-item { 
          margin-bottom: 20px; padding-bottom: 20px; border-bottom: 1px solid var(--glass-border);
        }
        .comment-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
        .comment-author { display: flex; align-items: center; gap: 10px; }
        .mini-avatar { width: 28px; height: 28px; border-radius: 8px; background: var(--secondary); display: flex; align-items: center; justify-content: center; font-size: 0.8rem; font-weight: bold; }
        .author-name { font-weight: 600; font-size: 0.95rem; }
        .comment-time { font-size: 0.8rem; color: var(--text-muted); }
        .comment-body { color: var(--text-main); line-height: 1.5; }

        .comment-form-box textarea { width: 100%; height: 100px; padding: 15px; border-radius: 12px; background: var(--bg-dark); border: 1px solid var(--glass-border); color: #fff; outline: none; }
        
        /* Timeline */
        .timeline { position: relative; padding-left: 30px; margin-top: 20px; }
        .timeline::before { content: ''; position: absolute; left: 7px; top: 0; bottom: 0; width: 2px; background: var(--glass-border); }
        .timeline-item { position: relative; margin-bottom: 30px; }
        .timeline-marker { position: absolute; left: -28px; top: 5px; width: 12px; height: 12px; border-radius: 50%; background: var(--primary); box-shadow: 0 0 10px var(--primary); }
        .log-action { font-weight: 700; color: var(--text-main); margin-bottom: 4px; }
        .log-msg { font-size: 0.95rem; color: var(--text-muted); margin-bottom: 8px; }
        .log-meta { display: flex; gap: 10px; font-size: 0.8rem; color: var(--text-muted); }

        /* Assignment Info */
        .assignment-info { display: flex; flex-direction: column; gap: 15px; }
        .assign-item .label { font-size: 0.8rem; color: var(--text-muted); text-transform: uppercase; letter-spacing: 0.5px; }
        .assign-item .val { font-weight: 600; color: var(--text-main); margin-top: 4px; }

        .action-buttons .btn { justify-content: center; padding: 12px; }
      `}} />
    </div>
  );
};

export default ComplaintDetail;
