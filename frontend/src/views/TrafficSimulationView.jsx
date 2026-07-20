import React from 'react';
import { useTraffic } from '../context/TrafficContext';
import { useAuth } from '../context/AuthContext';
import api from '../api/client';
import InteractiveMap from '../components/InteractiveMap';
import { Sliders, Play, Pause, RefreshCw, AlertOctagon } from 'lucide-react';

const TrafficSimulationView = () => {
  const { roads, isSimulating, setIsSimulating, fetchRoadNetwork, showToast } = useTraffic();
  const { isAdmin } = useAuth();

  const handleManualFlux = async () => {
    if (!isAdmin) {
      showToast('Admin privileges required to trigger manual simulation flux', 'warning');
      return;
    }
    try {
      await api.post('/traffic/simulate');
      fetchRoadNetwork();
      showToast('Manual traffic flux event simulated across network', 'info');
    } catch (err) {
      showToast('Failed to trigger simulation', 'error');
    }
  };

  const handleLevelChange = async (roadId, newLevel) => {
    if (!isAdmin) {
      showToast('Admin privileges required', 'warning');
      return;
    }
    try {
      await api.put(`/traffic/${roadId}`, { trafficLevel: newLevel });
      fetchRoadNetwork();
      showToast('Traffic congestion level updated', 'success');
    } catch (err) {
      showToast('Failed to update traffic level', 'error');
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-2xl font-bold text-white tracking-tight flex items-center gap-2">
            <Sliders className="w-6 h-6 text-amber-400" /> Real-Time Traffic Simulation Control
          </h2>
          <p className="text-xs text-slate-400">Simulate rush hour congestion fluctuations and incident closures live</p>
        </div>

        <div className="flex items-center gap-3">
          <button
            onClick={() => setIsSimulating(!isSimulating)}
            className={`px-4 py-2 rounded-xl text-xs font-bold transition-all flex items-center gap-2 ${
              isSimulating
                ? 'bg-amber-500/20 text-amber-300 border border-amber-500/40 animate-pulse'
                : 'bg-slate-800 hover:bg-slate-700 text-slate-200 border border-slate-700'
            }`}
          >
            {isSimulating ? <Pause className="w-4 h-4" /> : <Play className="w-4 h-4" />}
            <span>{isSimulating ? 'Pause Auto-Simulation' : 'Start Auto-Simulation (5s)'}</span>
          </button>

          {isAdmin && (
            <button
              onClick={handleManualFlux}
              className="px-4 py-2 rounded-xl bg-sky-600 hover:bg-sky-500 text-white font-bold text-xs shadow-lg flex items-center gap-2"
            >
              <RefreshCw className="w-4 h-4" /> Trigger Random Flux
            </button>
          )}
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2">
          <InteractiveMap height="500px" />
        </div>

        {/* Live Traffic Control Panel */}
        <div className="p-5 rounded-2xl glass-card space-y-4 max-h-[500px] overflow-y-auto">
          <h3 className="text-sm font-bold text-slate-200 flex items-center gap-2">
            <AlertOctagon className="w-4 h-4 text-rose-400" /> Live Corridor Adjustments
          </h3>

          <div className="space-y-3">
            {roads.map((road) => (
              <div key={road.id} className="p-3 rounded-xl bg-slate-900/80 border border-slate-800 space-y-2">
                <div className="flex items-center justify-between text-xs">
                  <span className="font-bold text-slate-200">{road.name}</span>
                  <span className="text-[10px] text-slate-400">{road.source} ➔ {road.destination}</span>
                </div>

                <div className="grid grid-cols-4 gap-1">
                  {['LOW', 'MEDIUM', 'HIGH', 'HEAVY'].map((lvl) => (
                    <button
                      key={lvl}
                      onClick={() => handleLevelChange(road.id, lvl)}
                      disabled={!isAdmin}
                      className={`py-1 rounded text-[10px] font-bold transition ${
                        road.trafficLevel === lvl
                          ? lvl === 'LOW' ? 'bg-emerald-600 text-white' : lvl === 'MEDIUM' ? 'bg-amber-600 text-white' : lvl === 'HIGH' ? 'bg-orange-600 text-white' : 'bg-rose-600 text-white'
                          : 'bg-slate-800 text-slate-400 hover:bg-slate-700'
                      }`}
                    >
                      {lvl}
                    </button>
                  ))}
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};

export default TrafficSimulationView;
