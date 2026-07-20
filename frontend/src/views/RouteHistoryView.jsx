import React, { useEffect, useState } from 'react';
import api from '../api/client';
import { useTraffic } from '../context/TrafficContext';
import { History, Download, Trash2, Search, FileText } from 'lucide-react';

const RouteHistoryView = () => {
  const { showToast } = useTraffic();
  const [historyList, setHistoryList] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');

  const fetchHistory = async () => {
    try {
      const res = await api.get('/routes/history');
      setHistoryList(res.data);
    } catch (err) {
      console.error("Failed to fetch history", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchHistory();
  }, []);

  const handleDeleteItem = async (id) => {
    try {
      await api.delete(`/routes/history/${id}`);
      showToast('History item deleted', 'success');
      fetchHistory();
    } catch (err) {
      showToast('Failed to delete item', 'error');
    }
  };

  const handleClearAll = async () => {
    if (!window.confirm('Clear all your saved route calculation history?')) return;
    try {
      await api.delete('/routes/history/clear');
      showToast('Route history cleared', 'success');
      fetchHistory();
    } catch (err) {
      showToast('Failed to clear history', 'error');
    }
  };

  const handleExportCsv = async () => {
    try {
      const response = await api.get('/reports/export/csv', { responseType: 'blob' });
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', 'traffic_route_history.csv');
      document.body.appendChild(link);
      link.click();
      link.remove();
      showToast('CSV export downloaded successfully', 'success');
    } catch (err) {
      showToast('Failed to export CSV', 'error');
    }
  };

  const filtered = historyList.filter((item) => {
    return (
      item.source.toLowerCase().includes(searchTerm.toLowerCase()) ||
      item.destination.toLowerCase().includes(searchTerm.toLowerCase()) ||
      item.userEmail.toLowerCase().includes(searchTerm.toLowerCase())
    );
  });

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-2xl font-bold text-white tracking-tight flex items-center gap-2">
            <History className="w-6 h-6 text-sky-400" /> Route Calculation History
          </h2>
          <p className="text-xs text-slate-400">View, search, and export past Dijkstra optimized journey logs</p>
        </div>

        <div className="flex items-center gap-3">
          <button
            onClick={handleExportCsv}
            className="px-4 py-2 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-200 font-semibold text-xs border border-slate-700 flex items-center gap-2"
          >
            <Download className="w-4 h-4 text-emerald-400" /> Export CSV
          </button>
          <button
            onClick={handleClearAll}
            className="px-4 py-2 rounded-xl bg-rose-600/20 hover:bg-rose-600/30 text-rose-300 font-semibold text-xs border border-rose-500/30 flex items-center gap-2"
          >
            <Trash2 className="w-4 h-4" /> Clear All History
          </button>
        </div>
      </div>

      {/* Filter Bar */}
      <div className="p-4 rounded-2xl glass-card flex items-center justify-between">
        <div className="relative w-full sm:w-80">
          <Search className="w-4 h-4 absolute left-3 top-2.5 text-slate-400" />
          <input
            type="text"
            placeholder="Search history by source or destination..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="w-full bg-slate-900 border border-slate-700 rounded-xl pl-9 pr-3 py-1.5 text-xs text-slate-200 focus:outline-none focus:border-sky-500"
          />
        </div>
      </div>

      {/* History Table */}
      <div className="rounded-2xl glass-panel border border-slate-800 overflow-hidden shadow-2xl">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs text-slate-300">
            <thead className="bg-slate-900/90 text-slate-400 font-semibold uppercase tracking-wider text-[10px] border-b border-slate-800">
              <tr>
                <th className="px-5 py-3.5">Date / Time</th>
                <th className="px-5 py-3.5">User</th>
                <th className="px-5 py-3.5">Source ➔ Destination</th>
                <th className="px-5 py-3.5">Goal</th>
                <th className="px-5 py-3.5">Distance</th>
                <th className="px-5 py-3.5">Est. Time</th>
                <th className="px-5 py-3.5">Path</th>
                <th className="px-5 py-3.5 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800/60">
              {filtered.map((item) => (
                <tr key={item.id} className="hover:bg-slate-800/40 transition">
                  <td className="px-5 py-3.5 text-slate-400">{new Date(item.createdAt).toLocaleString()}</td>
                  <td className="px-5 py-3.5 font-medium text-slate-300">{item.userEmail}</td>
                  <td className="px-5 py-3.5 font-bold text-white">{item.source} ➔ {item.destination}</td>
                  <td className="px-5 py-3.5">
                    <span className="px-2 py-0.5 rounded text-[10px] font-bold bg-sky-950 text-sky-300 border border-sky-800">
                      {item.optimizationMode}
                    </span>
                  </td>
                  <td className="px-5 py-3.5 font-semibold text-slate-200">{item.totalDistance} km</td>
                  <td className="px-5 py-3.5 font-semibold text-emerald-400">{item.estimatedTimeMinutes} mins</td>
                  <td className="px-5 py-3.5 text-slate-400 truncate max-w-xs">{item.routePath}</td>
                  <td className="px-5 py-3.5 text-right">
                    <button
                      onClick={() => handleDeleteItem(item.id)}
                      className="p-1.5 rounded-lg bg-slate-800 hover:bg-rose-500/20 text-rose-400 transition"
                    >
                      <Trash2 className="w-3.5 h-3.5" />
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default RouteHistoryView;
