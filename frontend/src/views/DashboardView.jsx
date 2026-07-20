import React, { useEffect, useState } from 'react';
import { useTraffic } from '../context/TrafficContext';
import api from '../api/client';
import InteractiveMap from '../components/InteractiveMap';
import {
  MapPin,
  Route as RouteIcon,
  Clock,
  AlertTriangle,
  Zap,
  TrendingUp,
  ArrowRight,
  ShieldCheck
} from 'lucide-react';
import { Link } from 'react-router-dom';

const DashboardView = () => {
  const { roads, intersections, loading } = useTraffic();
  const [metrics, setMetrics] = useState(null);

  useEffect(() => {
    const loadMetrics = async () => {
      try {
        const res = await api.get('/reports/dashboard');
        setMetrics(res.data);
      } catch (err) {
        console.error("Failed to load dashboard metrics", err);
      }
    };
    loadMetrics();
  }, [roads]);

  if (loading || !metrics) {
    return (
      <div className="flex items-center justify-center h-96">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-sky-500"></div>
      </div>
    );
  }

  const statCards = [
    { title: 'Total Road Edges', value: metrics.totalRoads, sub: `${metrics.openRoads} Open / ${metrics.closedRoads} Closed`, icon: MapPin, color: 'from-sky-500 to-blue-600' },
    { title: 'Graph Intersections', value: metrics.totalIntersections, sub: 'Connected Grid Nodes', icon: RouteIcon, color: 'from-indigo-500 to-purple-600' },
    { title: 'Average Travel Time', value: `${metrics.averageTravelTimeMinutes} Mins`, sub: 'Across System Corridors', icon: Clock, color: 'from-emerald-500 to-teal-600' },
    { title: 'Active Route History', value: metrics.totalSearchedRoutes, sub: 'Optimized via Dijkstra', icon: Zap, color: 'from-amber-500 to-orange-600' },
  ];

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h2 className="text-2xl font-bold text-white tracking-tight">Smart City Traffic Operations</h2>
          <p className="text-xs text-slate-400">Real-time graph monitoring & Dijkstra path optimization</p>
        </div>
        <Link
          to="/route-planner"
          className="inline-flex items-center gap-2 px-4 py-2 rounded-xl bg-gradient-to-r from-sky-500 to-indigo-600 text-white font-semibold text-xs shadow-lg shadow-sky-500/20 hover:opacity-90 transition"
        >
          <span>Open Route Planner</span>
          <ArrowRight className="w-4 h-4" />
        </Link>
      </div>

      {/* Summary Cards Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        {statCards.map((card, idx) => {
          const Icon = card.icon;
          return (
            <div key={idx} className="p-5 rounded-2xl glass-card relative overflow-hidden group hover:border-slate-700 transition">
              <div className="flex items-center justify-between">
                <span className="text-xs font-semibold text-slate-400">{card.title}</span>
                <div className={`p-2.5 rounded-xl bg-gradient-to-br ${card.color} text-white shadow-md`}>
                  <Icon className="w-4 h-4" />
                </div>
              </div>
              <div className="mt-3">
                <div className="text-2xl font-extrabold text-white tracking-tight">{card.value}</div>
                <div className="text-[11px] text-slate-400 mt-1">{card.sub}</div>
              </div>
            </div>
          );
        })}
      </div>

      {/* Main Grid Section: Map Preview & Live Traffic Feed */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Left Column: Interactive Map */}
        <div className="lg:col-span-2 space-y-4">
          <div className="flex items-center justify-between">
            <h3 className="text-base font-bold text-slate-200 flex items-center gap-2">
              <TrendingUp className="w-4 h-4 text-sky-400" /> Live Graph Network
            </h3>
            <span className="text-xs text-slate-400">Click nodes to route</span>
          </div>
          <InteractiveMap height="480px" />
        </div>

        {/* Right Column: Congested Roads & Status Distribution */}
        <div className="space-y-6">
          <div className="p-5 rounded-2xl glass-card space-y-4">
            <h3 className="text-sm font-bold text-slate-200 flex items-center gap-2">
              <AlertTriangle className="w-4 h-4 text-amber-400" /> Traffic Congestion Breakdown
            </h3>

            <div className="space-y-3">
              {metrics.trafficLevelDistribution && Object.entries(metrics.trafficLevelDistribution).map(([level, count]) => {
                let colorClass = 'bg-emerald-500';
                if (level === 'MEDIUM') colorClass = 'bg-amber-500';
                if (level === 'HIGH') colorClass = 'bg-orange-500';
                if (level === 'HEAVY') colorClass = 'bg-rose-500';

                const pct = Math.round((count / metrics.totalRoads) * 100) || 0;

                return (
                  <div key={level} className="space-y-1">
                    <div className="flex justify-between text-xs font-medium">
                      <span className="text-slate-300">{level} Congestion</span>
                      <span className="text-slate-400">{count} Roads ({pct}%)</span>
                    </div>
                    <div className="h-2 w-full bg-slate-800 rounded-full overflow-hidden">
                      <div className={`h-full ${colorClass} transition-all duration-500`} style={{ width: `${pct}%` }}></div>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>

          <div className="p-5 rounded-2xl glass-card space-y-3">
            <h3 className="text-sm font-bold text-slate-200 flex items-center gap-2">
              <ShieldCheck className="w-4 h-4 text-emerald-400" /> Dijkstra Engine Health
            </h3>
            <p className="text-xs text-slate-400 leading-relaxed">
              Calculates shortest distance and fastest travel time considering speed limits and real-time congestion multipliers. Closed roads are automatically assigned infinite weight.
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default DashboardView;
