import React, { useState } from 'react';
import { useTraffic } from '../context/TrafficContext';
import api from '../api/client';
import InteractiveMap from '../components/InteractiveMap';
import {
  Navigation,
  MapPin,
  Clock,
  Zap,
  ArrowRight,
  ShieldCheck,
  Compass,
  ListOrdered,
  AlertCircle
} from 'lucide-react';

const RoutePlannerView = () => {
  const {
    intersections,
    selectedSource,
    setSelectedSource,
    selectedDestination,
    setSelectedDestination,
    optimizationMode,
    setOptimizationMode,
    activeRoute,
    setActiveRoute,
    showToast
  } = useTraffic();

  const [calculating, setCalculating] = useState(false);

  const handleCalculateRoute = async (e) => {
    if (e) e.preventDefault();
    if (!selectedSource || !selectedDestination) {
      showToast('Please select both Source and Destination', 'warning');
      return;
    }
    if (selectedSource === selectedDestination) {
      showToast('Source and Destination cannot be identical', 'warning');
      return;
    }

    setCalculating(true);
    try {
      const res = await api.post('/routes/find', {
        source: selectedSource,
        destination: selectedDestination,
        optimizationMode: optimizationMode
      });
      setActiveRoute(res.data);
      if (res.data.reachable) {
        showToast(`Route optimized via Dijkstra (${res.data.optimizationMode})`, 'success');
      } else {
        showToast('Destination unreachable due to closed road corridors', 'error');
      }
    } catch (err) {
      console.error(err);
      showToast('Failed to calculate optimal route', 'error');
    } finally {
      setCalculating(false);
    }
  };

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div>
        <h2 className="text-2xl font-bold text-white tracking-tight flex items-center gap-2">
          <Navigation className="w-6 h-6 text-sky-400" /> Dijkstra Route Optimization Engine
        </h2>
        <p className="text-xs text-slate-400">Calculate shortest distance vs fastest travel time factoring real-time traffic levels</p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Left Form & Results */}
        <div className="space-y-6">
          {/* Controls Form */}
          <form onSubmit={handleCalculateRoute} className="p-5 rounded-2xl glass-card space-y-4">
            <h3 className="text-sm font-bold text-slate-200 flex items-center gap-2">
              <Compass className="w-4 h-4 text-indigo-400" /> Journey Parameters
            </h3>

            {/* Source */}
            <div className="space-y-1.5">
              <label className="text-xs font-semibold text-slate-300 flex items-center gap-1.5">
                <MapPin className="w-3.5 h-3.5 text-emerald-400" /> Source Intersection
              </label>
              <select
                value={selectedSource}
                onChange={(e) => setSelectedSource(e.target.value)}
                className="w-full bg-slate-900 border border-slate-700 rounded-xl px-3 py-2 text-xs font-medium text-slate-200 focus:outline-none focus:border-sky-500"
              >
                <option value="">Select Starting Point</option>
                {intersections.map((node) => (
                  <option key={node.code} value={node.code}>
                    {node.name} ({node.code})
                  </option>
                ))}
              </select>
            </div>

            {/* Destination */}
            <div className="space-y-1.5">
              <label className="text-xs font-semibold text-slate-300 flex items-center gap-1.5">
                <MapPin className="w-3.5 h-3.5 text-purple-400" /> Destination Intersection
              </label>
              <select
                value={selectedDestination}
                onChange={(e) => setSelectedDestination(e.target.value)}
                className="w-full bg-slate-900 border border-slate-700 rounded-xl px-3 py-2 text-xs font-medium text-slate-200 focus:outline-none focus:border-sky-500"
              >
                <option value="">Select Destination Point</option>
                {intersections.map((node) => (
                  <option key={node.code} value={node.code}>
                    {node.name} ({node.code})
                  </option>
                ))}
              </select>
            </div>

            {/* Optimization Mode Toggle */}
            <div className="space-y-1.5">
              <label className="text-xs font-semibold text-slate-300">Optimization Goal</label>
              <div className="grid grid-cols-2 gap-2 p-1 bg-slate-900 rounded-xl border border-slate-800">
                <button
                  type="button"
                  onClick={() => setOptimizationMode('FASTEST')}
                  className={`py-2 text-xs font-bold rounded-lg transition ${
                    optimizationMode === 'FASTEST'
                      ? 'bg-sky-600 text-white shadow-md'
                      : 'text-slate-400 hover:text-slate-200'
                  }`}
                >
                  ⚡ Fastest Route
                </button>
                <button
                  type="button"
                  onClick={() => setOptimizationMode('SHORTEST')}
                  className={`py-2 text-xs font-bold rounded-lg transition ${
                    optimizationMode === 'SHORTEST'
                      ? 'bg-indigo-600 text-white shadow-md'
                      : 'text-slate-400 hover:text-slate-200'
                  }`}
                >
                  📏 Shortest Route
                </button>
              </div>
            </div>

            <button
              type="submit"
              disabled={calculating}
              className="w-full py-2.5 rounded-xl bg-gradient-to-r from-sky-500 to-indigo-600 hover:opacity-90 text-white font-bold text-xs shadow-lg shadow-sky-500/20 transition flex items-center justify-center gap-2"
            >
              {calculating ? (
                <span>Executing Dijkstra...</span>
              ) : (
                <>
                  <Zap className="w-4 h-4" />
                  <span>Find Optimal Route</span>
                </>
              )}
            </button>
          </form>

          {/* Route Summary Cards */}
          {activeRoute && activeRoute.reachable && (
            <div className="p-5 rounded-2xl glass-card space-y-4 border-l-4 border-l-sky-500">
              <div className="flex items-center justify-between">
                <span className="text-xs font-bold uppercase tracking-wider text-sky-400">
                  Primary Path ({activeRoute.optimizationMode})
                </span>
                <span className="px-2.5 py-0.5 rounded-full text-[11px] font-bold bg-sky-950 text-sky-300 border border-sky-800">
                  Score: {activeRoute.trafficScore} / 5.0
                </span>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div className="p-3 rounded-xl bg-slate-900/60 border border-slate-800">
                  <div className="text-[11px] text-slate-400">Estimated Travel Time</div>
                  <div className="text-xl font-extrabold text-white mt-1 flex items-center gap-1.5">
                    <Clock className="w-4 h-4 text-emerald-400" />
                    {activeRoute.estimatedTimeMinutes} mins
                  </div>
                </div>

                <div className="p-3 rounded-xl bg-slate-900/60 border border-slate-800">
                  <div className="text-[11px] text-slate-400">Total Distance</div>
                  <div className="text-xl font-extrabold text-white mt-1 flex items-center gap-1.5">
                    <Compass className="w-4 h-4 text-indigo-400" />
                    {activeRoute.totalDistanceKm} km
                  </div>
                </div>
              </div>

              <div className="text-xs text-slate-300">
                Traffic Status: <strong className="text-emerald-400">{activeRoute.overallTrafficCondition}</strong>
              </div>

              {/* Alternate Route Comparison if available */}
              {activeRoute.alternateRoute && (
                <div className="mt-4 pt-4 border-t border-slate-800 space-y-2">
                  <div className="text-xs font-bold text-amber-400 flex items-center gap-1.5">
                    <AlertCircle className="w-3.5 h-3.5" /> Alternate Route Available
                  </div>
                  <div className="p-3 rounded-xl bg-amber-950/20 border border-amber-800/40 text-xs text-slate-300 flex justify-between items-center">
                    <span>{activeRoute.alternateRoute.estimatedTimeMinutes} mins ({activeRoute.alternateRoute.totalDistanceKm} km)</span>
                    <button
                      onClick={() => setActiveRoute(activeRoute.alternateRoute)}
                      className="px-2.5 py-1 rounded-lg bg-amber-500/20 hover:bg-amber-500/30 text-amber-300 text-[11px] font-bold"
                    >
                      Switch to Alternate
                    </button>
                  </div>
                </div>
              )}
            </div>
          )}
        </div>

        {/* Right Map & Turn-by-Turn Breakdown */}
        <div className="lg:col-span-2 space-y-6">
          <InteractiveMap height="440px" />

          {/* Turn-by-Turn Navigation Breakdown */}
          {activeRoute && activeRoute.segments && activeRoute.segments.length > 0 && (
            <div className="p-5 rounded-2xl glass-card space-y-4">
              <h3 className="text-sm font-bold text-slate-200 flex items-center gap-2">
                <ListOrdered className="w-4 h-4 text-sky-400" /> Turn-by-Turn Segment Guidance
              </h3>

              <div className="space-y-2">
                {activeRoute.segments.map((seg, idx) => (
                  <div key={idx} className="p-3 rounded-xl bg-slate-900/70 border border-slate-800 flex items-center justify-between text-xs">
                    <div className="flex items-center gap-3">
                      <div className="w-6 h-6 rounded-full bg-slate-800 text-sky-400 font-bold flex items-center justify-center text-[11px]">
                        {idx + 1}
                      </div>
                      <div>
                        <div className="font-semibold text-slate-200">{seg.roadName}</div>
                        <div className="text-[11px] text-slate-400">{seg.fromNode} ➔ {seg.toNode}</div>
                      </div>
                    </div>

                    <div className="flex items-center gap-4 text-right">
                      <div>
                        <div className="font-bold text-slate-300">{seg.distanceKm} km</div>
                        <div className="text-[10px] text-slate-500">Speed: {seg.speedLimitKmh} km/h</div>
                      </div>
                      <span className="px-2 py-1 rounded-md text-[10px] font-bold bg-slate-800 text-slate-300 border border-slate-700">
                        {seg.travelTimeMinutes} mins
                      </span>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default RoutePlannerView;
