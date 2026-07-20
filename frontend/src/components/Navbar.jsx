import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { useTraffic } from '../context/TrafficContext';
import { Activity, Play, Pause, Bell, User, LogOut, Moon, Sun, Shield } from 'lucide-react';

const Navbar = ({ darkMode, setDarkMode }) => {
  const { user, logout, isAdmin } = useAuth();
  const { isSimulating, setIsSimulating, showToast } = useTraffic();
  const [showDropdown, setShowDropdown] = useState(false);

  const toggleSimulation = () => {
    const next = !isSimulating;
    setIsSimulating(next);
    showToast(next ? 'Live traffic flux simulation ENABLED' : 'Live traffic simulation PAUSED', next ? 'warning' : 'info');
  };

  return (
    <header className="sticky top-0 z-30 h-16 border-b border-slate-800 glass-panel px-6 flex items-center justify-between">
      <div className="flex items-center gap-4">
        <h1 className="text-xl font-bold bg-gradient-to-r from-sky-400 via-indigo-400 to-emerald-400 bg-clip-text text-transparent">
          Dynamic Traffic Routing System
        </h1>
        <span className="hidden md:inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-semibold bg-sky-950/60 text-sky-400 border border-sky-800/50">
          <Activity className="w-3.5 h-3.5 animate-spin" /> Dijkstra v1.0
        </span>
      </div>

      <div className="flex items-center gap-3 md:gap-4">
        {/* Simulation Toggle */}
        <button
          onClick={toggleSimulation}
          className={`flex items-center gap-2 px-3.5 py-1.5 rounded-lg text-xs font-semibold transition-all ${
            isSimulating
              ? 'bg-amber-500/20 text-amber-300 border border-amber-500/40 animate-pulse'
              : 'bg-slate-800 hover:bg-slate-700 text-slate-300 border border-slate-700'
          }`}
        >
          {isSimulating ? <Pause className="w-3.5 h-3.5" /> : <Play className="w-3.5 h-3.5" />}
          <span>{isSimulating ? 'Simulating Traffic...' : 'Start Traffic Flux'}</span>
        </button>

        {/* Dark / Light Toggle */}
        <button
          onClick={() => setDarkMode(!darkMode)}
          className="p-2 rounded-lg bg-slate-800 hover:bg-slate-700 text-slate-300 border border-slate-700 transition"
          title="Toggle Theme"
        >
          {darkMode ? <Sun className="w-4 h-4 text-amber-400" /> : <Moon className="w-4 h-4 text-indigo-400" />}
        </button>

        {/* User Menu */}
        {user ? (
          <div className="relative">
            <button
              onClick={() => setShowDropdown(!showDropdown)}
              className="flex items-center gap-2.5 p-1.5 pr-3 rounded-xl bg-slate-800/80 hover:bg-slate-800 border border-slate-700 transition"
            >
              <div className="w-8 h-8 rounded-lg bg-gradient-to-tr from-sky-500 to-indigo-600 flex items-center justify-center font-bold text-white text-xs">
                {user.name ? user.name.charAt(0).toUpperCase() : 'U'}
              </div>
              <div className="hidden sm:block text-left">
                <div className="text-xs font-semibold text-slate-200">{user.name}</div>
                <div className="text-[10px] text-slate-400 flex items-center gap-1">
                  {isAdmin && <Shield className="w-2.5 h-2.5 text-amber-400" />}
                  {user.role}
                </div>
              </div>
            </button>

            {showDropdown && (
              <div className="absolute right-0 mt-2 w-48 rounded-xl glass-panel shadow-2xl border border-slate-700 py-1 z-50">
                <div className="px-4 py-2 border-b border-slate-800">
                  <p className="text-xs font-medium text-slate-300">{user.email}</p>
                </div>
                <button
                  onClick={() => {
                    setShowDropdown(false);
                    logout();
                  }}
                  className="w-full text-left px-4 py-2 text-xs text-rose-400 hover:bg-rose-500/10 flex items-center gap-2"
                >
                  <LogOut className="w-3.5 h-3.5" /> Sign Out
                </button>
              </div>
            )}
          </div>
        ) : (
          <a
            href="/login"
            className="px-4 py-1.5 rounded-lg bg-sky-600 hover:bg-sky-500 text-white text-xs font-semibold shadow-lg shadow-sky-600/20"
          >
            Sign In
          </a>
        )}
      </div>
    </header>
  );
};

export default Navbar;
