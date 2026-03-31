import React, { useState } from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { 
  LayoutDashboard, 
  FileText, 
  PlusCircle, 
  User, 
  Shield, 
  LogOut, 
  Menu, 
  X,
  LifeBuoy
} from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import './Layout.css';

const Sidebar = ({ isOpen, toggleSidebar }) => {
  const { user, logout } = useAuth();

  const getNavItems = () => {
    const items = [
      { path: '/', label: 'Dashboard', icon: <LayoutDashboard size={20} /> },
      { path: '/complaints', label: 'All Complaints', icon: <FileText size={20} /> },
    ];

    if (user?.role === 'ROLE_CUSTOMER') {
      items.push({ path: '/submit-complaint', label: 'New Complaint', icon: <PlusCircle size={20} /> });
    }

    if (user?.role === 'ROLE_ADMIN' || user?.role === 'ROLE_MANAGER') {
      items.push({ path: '/users', label: 'User Management', icon: <User size={20} /> });
    }

    return items;
  };

  return (
    <aside className={`sidebar glass ${isOpen ? 'open' : ''}`}>
      <div className="sidebar-header">
        <LifeBuoy className="logo-icon" />
        <h2>SCRMS</h2>
        <button className="mobile-close" onClick={toggleSidebar}><X /></button>
      </div>
      
      <nav className="nav-menu">
        {getNavItems().map(item => (
          <NavLink 
            key={item.path} 
            to={item.path} 
            className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}
            onClick={() => window.innerWidth < 768 && toggleSidebar()}
          >
            {item.icon}
            <span>{item.label}</span>
          </NavLink>
        ))}
      </nav>

      <div className="sidebar-footer">
        <div className="user-info">
          <div className="avatar">{user?.fullName?.charAt(0)}</div>
          <div className="user-details">
            <p className="name">{user?.fullName}</p>
            <p className="role">{user?.role?.replace('ROLE_', '')}</p>
          </div>
        </div>
        <button className="btn-logout" onClick={logout}>
          <LogOut size={18} />
          <span>Logout</span>
        </button>
      </div>
    </aside>
  );
};

const Navbar = ({ toggleSidebar }) => {
  const { user } = useAuth();

  return (
    <header className="navbar glass">
      <button className="mobile-toggle" onClick={toggleSidebar}>
        <Menu />
      </button>
      <div className="nav-brand">
        <h3>Smart Complaint Resolution Management</h3>
      </div>
      <div className="nav-actions">
        <div className="badge-notification">
          <span>Role: {user?.role?.replace('ROLE_', '')}</span>
        </div>
      </div>
    </header>
  );
};

const MainLayout = ({ children }) => {
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);

  const toggleSidebar = () => setIsSidebarOpen(!isSidebarOpen);

  return (
    <div className="layout-container">
      <Sidebar isOpen={isSidebarOpen} toggleSidebar={toggleSidebar} />
      <div className="main-content-wrapper">
        <Navbar toggleSidebar={toggleSidebar} />
        <main className="main-body">
          {children}
        </main>
      </div>
    </div>
  );
};

export default MainLayout;
