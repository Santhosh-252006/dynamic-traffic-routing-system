import React, { useState } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import { TrafficProvider } from './context/TrafficContext';

import Navbar from './components/Navbar';
import Sidebar from './components/Sidebar';
import Toast from './components/Toast';

import DashboardView from './views/DashboardView';
import RoutePlannerView from './views/RoutePlannerView';
import RoadManagementView from './views/RoadManagementView';
import TrafficSimulationView from './views/TrafficSimulationView';
import RouteHistoryView from './views/RouteHistoryView';
import AnalyticsReportView from './views/AnalyticsReportView';
import AuditLogView from './views/AuditLogView';
import LoginView from './views/LoginView';
import RegisterView from './views/RegisterView';
import NotFoundView from './views/NotFoundView';

const ProtectedLayout = ({ children, darkMode, setDarkMode }) => {
  const { user, loading } = useAuth();

  if (loading) {
    return (
      <div className="min-h-screen bg-slate-950 flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-sky-500"></div>
      </div>
    );
  }

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  return (
    <div className={`min-h-screen flex flex-col ${darkMode ? 'dark bg-slate-950 text-slate-100' : 'bg-slate-50 text-slate-900'}`}>
      <Navbar darkMode={darkMode} setDarkMode={setDarkMode} />
      <div className="flex flex-1 overflow-hidden">
        <Sidebar />
        <main className="flex-1 overflow-y-auto p-4 sm:p-6 lg:p-8">
          {children}
        </main>
      </div>
      <Toast />
    </div>
  );
};

const App = () => {
  const [darkMode, setDarkMode] = useState(true);

  return (
    <AuthProvider>
      <TrafficProvider>
        <BrowserRouter>
          <Routes>
            {/* Public Auth Routes */}
            <Route path="/login" element={<LoginView />} />
            <Route path="/register" element={<RegisterView />} />

            {/* Protected Core Routes */}
            <Route path="/" element={<ProtectedLayout darkMode={darkMode} setDarkMode={setDarkMode}><DashboardView /></ProtectedLayout>} />
            <Route path="/route-planner" element={<ProtectedLayout darkMode={darkMode} setDarkMode={setDarkMode}><RoutePlannerView /></ProtectedLayout>} />
            <Route path="/roads" element={<ProtectedLayout darkMode={darkMode} setDarkMode={setDarkMode}><RoadManagementView /></ProtectedLayout>} />
            <Route path="/simulation" element={<ProtectedLayout darkMode={darkMode} setDarkMode={setDarkMode}><TrafficSimulationView /></ProtectedLayout>} />
            <Route path="/history" element={<ProtectedLayout darkMode={darkMode} setDarkMode={setDarkMode}><RouteHistoryView /></ProtectedLayout>} />
            <Route path="/analytics" element={<ProtectedLayout darkMode={darkMode} setDarkMode={setDarkMode}><AnalyticsReportView /></ProtectedLayout>} />
            <Route path="/audit-logs" element={<ProtectedLayout darkMode={darkMode} setDarkMode={setDarkMode}><AuditLogView /></ProtectedLayout>} />

            {/* Error 404 */}
            <Route path="*" element={<NotFoundView />} />
          </Routes>
        </BrowserRouter>
      </TrafficProvider>
    </AuthProvider>
  );
};

export default App;
