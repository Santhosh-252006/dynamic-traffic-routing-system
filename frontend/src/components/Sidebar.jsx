import React from 'react';
import { NavLink } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import {
  LayoutDashboard,
  Navigation,
  MapPin,
  Sliders,
  History,
  BarChart3,
  ShieldAlert
} from 'lucide-react';

const Sidebar = () => {
  const { isAdmin } = useAuth();

  const navItems = [
    { path: '/', label: 'Dashboard', icon: LayoutDashboard },
    { path: '/route-planner', label: 'Route Optimization', icon: Navigation },
    { path: '/roads', label: 'Road Network', icon: MapPin },
    { path: '/simulation', label: 'Traffic Simulation', icon: Sliders },
    { path: '/history', label: 'Route History', icon: History },
    { path: '/analytics', label: 'Reports & Analytics', icon: BarChart3 },
  ];

  if (isAdmin) {
    navItems.push({ path: '/audit-logs', label: 'Audit Logs', icon: ShieldAlert });
  }

  return (
    <aside className="w-64 border-r border-slate-800 glass-panel flex flex-col justify-between hidden md:flex">
      <div className="p-4 space-y-1.5">
        <div className="px-3 py-2 text-[10px] font-bold tracking-wider text-slate-500 uppercase">
          Core Navigation
        </div>

        {navItems.map((item) => {
          const Icon = item.icon;
          return (
            <NavLink
              key={item.path}
              to={item.path}
              className={({ isActive }) =>
                `flex items-center gap-3 px-3.5 py-2.5 rounded-xl text-xs font-semibold transition-all ${
                  isActive
                    ? 'bg-gradient-to-r from-sky-600/90 to-indigo-600/90 text-white shadow-lg shadow-sky-600/20 border border-sky-400/30'
                    : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/60'
                }`
              }
            >
              <Icon className="w-4 h-4" />
              <span>{item.label}</span>
            </NavLink>
          );
        })}
      </div>

      <div className="p-4 border-t border-slate-800/80">
        <div className="p-3 rounded-xl bg-slate-800/40 border border-slate-800 text-xs text-slate-400 space-y-1">
          <div className="font-semibold text-slate-300">Dijkstra Engine</div>
          <div className="text-[11px] text-slate-500">
            Real-time graph calculation for Shortest & Fastest routes.
          </div>
        </div>
      </div>
    </aside>
  );
};

export default Sidebar;
