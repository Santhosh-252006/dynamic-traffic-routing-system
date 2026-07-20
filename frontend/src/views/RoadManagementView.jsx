import React, { useState } from 'react';
import { useTraffic } from '../context/TrafficContext';
import { useAuth } from '../context/AuthContext';
import api from '../api/client';
import {
  MapPin,
  Plus,
  Edit2,
  Trash2,
  Search,
  CheckCircle,
  XCircle,
  Sliders,
  AlertTriangle
} from 'lucide-react';

const RoadManagementView = () => {
  const { roads, fetchRoadNetwork, showToast } = useTraffic();
  const { isAdmin } = useAuth();

  const [searchTerm, setSearchTerm] = useState('');
  const [filterStatus, setFilterStatus] = useState('ALL');
  const [filterTraffic, setFilterTraffic] = useState('ALL');

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingRoad, setEditingRoad] = useState(null);

  const [formData, setFormData] = useState({
    name: '',
    source: 'INT-01',
    destination: 'INT-02',
    distance: 3.0,
    speedLimit: 60,
    trafficLevel: 'LOW',
    roadStatus: 'OPEN',
    twoWay: true
  });

  const filteredRoads = roads.filter((road) => {
    const matchesSearch =
      road.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      road.source.toLowerCase().includes(searchTerm.toLowerCase()) ||
      road.destination.toLowerCase().includes(searchTerm.toLowerCase());

    const matchesStatus = filterStatus === 'ALL' || road.roadStatus === filterStatus;
    const matchesTraffic = filterTraffic === 'ALL' || road.trafficLevel === filterTraffic;

    return matchesSearch && matchesStatus && matchesTraffic;
  });

  const handleOpenAdd = () => {
    setEditingRoad(null);
    setFormData({
      name: '',
      source: 'INT-01',
      destination: 'INT-02',
      distance: 3.0,
      speedLimit: 60,
      trafficLevel: 'LOW',
      roadStatus: 'OPEN',
      twoWay: true
    });
    setIsModalOpen(true);
  };

  const handleOpenEdit = (road) => {
    setEditingRoad(road);
    setFormData({
      name: road.name,
      source: road.source,
      destination: road.destination,
      distance: road.distance,
      speedLimit: road.speedLimit,
      trafficLevel: road.trafficLevel,
      roadStatus: road.roadStatus,
      twoWay: road.twoWay
    });
    setIsModalOpen(true);
  };

  const handleSaveRoad = async (e) => {
    e.preventDefault();
    if (!isAdmin) {
      showToast('Admin privileges required to modify road network', 'warning');
      return;
    }

    try {
      if (editingRoad) {
        await api.put(`/roads/${editingRoad.id}`, formData);
        showToast('Road corridor updated successfully', 'success');
      } else {
        await api.post('/roads', formData);
        showToast('New road corridor created successfully', 'success');
      }
      setIsModalOpen(false);
      fetchRoadNetwork();
    } catch (err) {
      console.error(err);
      showToast('Failed to save road details', 'error');
    }
  };

  const handleToggleStatus = async (road) => {
    if (!isAdmin) {
      showToast('Admin privileges required', 'warning');
      return;
    }
    const newStatus = road.roadStatus === 'OPEN' ? 'CLOSED' : 'OPEN';
    try {
      await api.put(`/traffic/${road.id}`, { roadStatus: newStatus });
      showToast(`Road status changed to ${newStatus}`, newStatus === 'CLOSED' ? 'warning' : 'success');
      fetchRoadNetwork();
    } catch (err) {
      showToast('Failed to toggle status', 'error');
    }
  };

  const handleDeleteRoad = async (id) => {
    if (!isAdmin) {
      showToast('Admin privileges required', 'warning');
      return;
    }
    if (!window.confirm('Are you sure you want to delete this road edge from the graph?')) return;
    try {
      await api.delete(`/roads/${id}`);
      showToast('Road edge deleted', 'success');
      fetchRoadNetwork();
    } catch (err) {
      showToast('Failed to delete road', 'error');
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-2xl font-bold text-white tracking-tight flex items-center gap-2">
            <MapPin className="w-6 h-6 text-indigo-400" /> Road Network Management
          </h2>
          <p className="text-xs text-slate-400">Configure road edges, distance weights, speed limits, and status</p>
        </div>
        {isAdmin && (
          <button
            onClick={handleOpenAdd}
            className="px-4 py-2 rounded-xl bg-gradient-to-r from-sky-500 to-indigo-600 text-white font-semibold text-xs shadow-lg shadow-sky-500/20 hover:opacity-90 transition flex items-center gap-1.5"
          >
            <Plus className="w-4 h-4" /> Add New Road Corridor
          </button>
        )}
      </div>

      {/* Filter Bar */}
      <div className="p-4 rounded-2xl glass-card flex flex-col md:flex-row gap-4 items-center justify-between">
        <div className="relative w-full md:w-80">
          <Search className="w-4 h-4 absolute left-3 top-2.5 text-slate-400" />
          <input
            type="text"
            placeholder="Search by road name or intersection..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="w-full bg-slate-900 border border-slate-700 rounded-xl pl-9 pr-3 py-1.5 text-xs text-slate-200 focus:outline-none focus:border-sky-500"
          />
        </div>

        <div className="flex items-center gap-3 w-full md:w-auto">
          <select
            value={filterStatus}
            onChange={(e) => setFilterStatus(e.target.value)}
            className="bg-slate-900 border border-slate-700 rounded-xl px-3 py-1.5 text-xs text-slate-300 focus:outline-none"
          >
            <option value="ALL">All Status (Open/Closed)</option>
            <option value="OPEN">Open Only</option>
            <option value="CLOSED">Closed Only</option>
          </select>

          <select
            value={filterTraffic}
            onChange={(e) => setFilterTraffic(e.target.value)}
            className="bg-slate-900 border border-slate-700 rounded-xl px-3 py-1.5 text-xs text-slate-300 focus:outline-none"
          >
            <option value="ALL">All Traffic Levels</option>
            <option value="LOW">Low (Smooth)</option>
            <option value="MEDIUM">Medium</option>
            <option value="HIGH">High</option>
            <option value="HEAVY">Heavy (Gridlock)</option>
          </select>
        </div>
      </div>

      {/* Roads Data Table */}
      <div className="rounded-2xl glass-panel border border-slate-800 overflow-hidden shadow-2xl">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs text-slate-300">
            <thead className="bg-slate-900/90 text-slate-400 font-semibold uppercase tracking-wider text-[10px] border-b border-slate-800">
              <tr>
                <th className="px-5 py-3.5">Road Name</th>
                <th className="px-5 py-3.5">Source ➔ Destination</th>
                <th className="px-5 py-3.5">Distance</th>
                <th className="px-5 py-3.5">Speed Limit</th>
                <th className="px-5 py-3.5">Traffic Level</th>
                <th className="px-5 py-3.5">Est. Travel Time</th>
                <th className="px-5 py-3.5">Status</th>
                {isAdmin && <th className="px-5 py-3.5 text-right">Actions</th>}
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800/60">
              {filteredRoads.map((road) => (
                <tr key={road.id} className="hover:bg-slate-800/40 transition">
                  <td className="px-5 py-3.5 font-bold text-white">{road.name}</td>
                  <td className="px-5 py-3.5 text-slate-300">{road.source} ➔ {road.destination}</td>
                  <td className="px-5 py-3.5">{road.distance} km</td>
                  <td className="px-5 py-3.5">{road.speedLimit} km/h</td>
                  <td className="px-5 py-3.5">
                    <span className="px-2.5 py-0.5 rounded-full text-[10px] font-bold" style={{
                      backgroundColor: road.trafficLevel === 'LOW' ? '#065F46' : road.trafficLevel === 'MEDIUM' ? '#92400E' : road.trafficLevel === 'HIGH' ? '#9A3412' : '#991B1B',
                      color: '#FFFFFF'
                    }}>
                      {road.trafficLevel}
                    </span>
                  </td>
                  <td className="px-5 py-3.5 font-semibold text-slate-200">
                    {road.roadStatus === 'CLOSED' ? 'Infinity (Closed)' : `${road.estimatedTravelTimeMinutes} mins`}
                  </td>
                  <td className="px-5 py-3.5">
                    <button
                      onClick={() => handleToggleStatus(road)}
                      disabled={!isAdmin}
                      className={`inline-flex items-center gap-1 px-2.5 py-1 rounded-lg text-[11px] font-bold transition ${
                        road.roadStatus === 'OPEN'
                          ? 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/30'
                          : 'bg-rose-500/10 text-rose-400 border border-rose-500/30'
                      }`}
                    >
                      {road.roadStatus === 'OPEN' ? <CheckCircle className="w-3 h-3" /> : <XCircle className="w-3 h-3" />}
                      <span>{road.roadStatus}</span>
                    </button>
                  </td>
                  {isAdmin && (
                    <td className="px-5 py-3.5 text-right space-x-2">
                      <button
                        onClick={() => handleOpenEdit(road)}
                        className="p-1.5 rounded-lg bg-slate-800 hover:bg-slate-700 text-sky-400 transition"
                      >
                        <Edit2 className="w-3.5 h-3.5" />
                      </button>
                      <button
                        onClick={() => handleDeleteRoad(road.id)}
                        className="p-1.5 rounded-lg bg-slate-800 hover:bg-rose-500/20 text-rose-400 transition"
                      >
                        <Trash2 className="w-3.5 h-3.5" />
                      </button>
                    </td>
                  )}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Add / Edit Road Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-md">
          <div className="w-full max-w-md p-6 rounded-2xl glass-panel border border-slate-700 shadow-2xl space-y-4">
            <h3 className="text-base font-bold text-white">
              {editingRoad ? 'Edit Road Corridor' : 'Add New Road Corridor'}
            </h3>

            <form onSubmit={handleSaveRoad} className="space-y-3">
              <div>
                <label className="text-xs font-semibold text-slate-300">Road Name</label>
                <input
                  type="text"
                  required
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  className="w-full bg-slate-900 border border-slate-700 rounded-xl px-3 py-2 text-xs text-white"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="text-xs font-semibold text-slate-300">Source Node</label>
                  <input
                    type="text"
                    required
                    value={formData.source}
                    onChange={(e) => setFormData({ ...formData, source: e.target.value })}
                    className="w-full bg-slate-900 border border-slate-700 rounded-xl px-3 py-2 text-xs text-white"
                  />
                </div>
                <div>
                  <label className="text-xs font-semibold text-slate-300">Destination Node</label>
                  <input
                    type="text"
                    required
                    value={formData.destination}
                    onChange={(e) => setFormData({ ...formData, destination: e.target.value })}
                    className="w-full bg-slate-900 border border-slate-700 rounded-xl px-3 py-2 text-xs text-white"
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="text-xs font-semibold text-slate-300">Distance (km)</label>
                  <input
                    type="number"
                    step="0.1"
                    required
                    value={formData.distance}
                    onChange={(e) => setFormData({ ...formData, distance: parseFloat(e.target.value) })}
                    className="w-full bg-slate-900 border border-slate-700 rounded-xl px-3 py-2 text-xs text-white"
                  />
                </div>
                <div>
                  <label className="text-xs font-semibold text-slate-300">Speed Limit (km/h)</label>
                  <input
                    type="number"
                    required
                    value={formData.speedLimit}
                    onChange={(e) => setFormData({ ...formData, speedLimit: parseFloat(e.target.value) })}
                    className="w-full bg-slate-900 border border-slate-700 rounded-xl px-3 py-2 text-xs text-white"
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="text-xs font-semibold text-slate-300">Traffic Level</label>
                  <select
                    value={formData.trafficLevel}
                    onChange={(e) => setFormData({ ...formData, trafficLevel: e.target.value })}
                    className="w-full bg-slate-900 border border-slate-700 rounded-xl px-3 py-2 text-xs text-white"
                  >
                    <option value="LOW">LOW</option>
                    <option value="MEDIUM">MEDIUM</option>
                    <option value="HIGH">HIGH</option>
                    <option value="HEAVY">HEAVY</option>
                  </select>
                </div>
                <div>
                  <label className="text-xs font-semibold text-slate-300">Status</label>
                  <select
                    value={formData.roadStatus}
                    onChange={(e) => setFormData({ ...formData, roadStatus: e.target.value })}
                    className="w-full bg-slate-900 border border-slate-700 rounded-xl px-3 py-2 text-xs text-white"
                  >
                    <option value="OPEN">OPEN</option>
                    <option value="CLOSED">CLOSED</option>
                  </select>
                </div>
              </div>

              <div className="flex justify-end gap-3 pt-3">
                <button
                  type="button"
                  onClick={() => setIsModalOpen(false)}
                  className="px-4 py-2 rounded-xl bg-slate-800 text-slate-300 text-xs font-semibold"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 rounded-xl bg-sky-600 text-white text-xs font-semibold shadow-lg"
                >
                  Save Road
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default RoadManagementView;
