import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import ProtectedRoute from './components/ProtectedRoute';
import MainLayout from './components/layout/MainLayout';

// Pages
import Login from './pages/auth/Login';
import PublicTrack from './pages/auth/PublicTrack';
import Dashboard from './pages/Dashboard';
import ComplaintList from './pages/complaints/ComplaintList';
import ComplaintDetail from './pages/complaints/ComplaintDetail';
import CreateComplaint from './pages/complaints/CreateComplaint';
import UserList from './pages/auth/UserList';
import UserRegistration from './pages/auth/UserRegistration';

function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/track-public" element={<PublicTrack />} />
      
      {/* Protected Routes wrapped in MainLayout */}
      <Route path="/" element={
        <ProtectedRoute>
          <MainLayout>
            <Dashboard />
          </MainLayout>
        </ProtectedRoute>
      } />

      <Route path="/complaints" element={
        <ProtectedRoute>
          <MainLayout>
            <ComplaintList />
          </MainLayout>
        </ProtectedRoute>
      } />

      <Route path="/complaints/:id" element={
        <ProtectedRoute>
          <MainLayout>
            <ComplaintDetail />
          </MainLayout>
        </ProtectedRoute>
      } />

      <Route path="/submit-complaint" element={
        <ProtectedRoute allowedRoles={['ROLE_CUSTOMER']}>
          <MainLayout>
            <CreateComplaint />
          </MainLayout>
        </ProtectedRoute>
      } />

      <Route path="/users" element={
        <ProtectedRoute allowedRoles={['ROLE_ADMIN', 'ROLE_MANAGER']}>
          <MainLayout>
            <UserList />
          </MainLayout>
        </ProtectedRoute>
      } />

      <Route path="/users/new" element={
        <ProtectedRoute allowedRoles={['ROLE_ADMIN']}>
          <MainLayout>
            <UserRegistration />
          </MainLayout>
        </ProtectedRoute>
      } />

      {/* Wildcard redirect */}
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}

export default App;
