import React, { createContext, useContext, useState, useEffect } from 'react';
import api from '../api/client';

const TrafficContext = createContext();

export const TrafficProvider = ({ children }) => {
  const [roads, setRoads] = useState([]);
  const [intersections, setIntersections] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isSimulating, setIsSimulating] = useState(false);
  const [activeRoute, setActiveRoute] = useState(null);
  const [selectedSource, setSelectedSource] = useState('INT-01');
  const [selectedDestination, setSelectedDestination] = useState('INT-04');
  const [optimizationMode, setOptimizationMode] = useState('FASTEST'); // FASTEST or SHORTEST
  const [heatmapMode, setHeatmapMode] = useState(false);
  const [toast, setToast] = useState(null);

  const fetchRoadNetwork = async () => {
    try {
      const [roadsRes, nodesRes] = await Promise.all([
        api.get('/roads'),
        api.get('/intersections')
      ]);
      setRoads(roadsRes.data);
      setIntersections(nodesRes.data);
    } catch (err) {
      console.error("Failed to load traffic network:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchRoadNetwork();
  }, []);

  // Simulation timer loop
  useEffect(() => {
    let interval = null;
    if (isSimulating) {
      interval = setInterval(async () => {
        try {
          const res = await api.post('/traffic/simulate');
          setRoads(res.data);
          // If a route is active, re-calculate automatically
          if (activeRoute) {
            recalculateActiveRoute();
          }
        } catch (err) {
          console.error("Simulation tick error:", err);
        }
      }, 5000);
    }
    return () => {
      if (interval) clearInterval(interval);
    };
  }, [isSimulating, selectedSource, selectedDestination, optimizationMode]);

  const recalculateActiveRoute = async () => {
    if (!selectedSource || !selectedDestination || selectedSource === selectedDestination) return;
    try {
      const res = await api.post('/routes/find', {
        source: selectedSource,
        destination: selectedDestination,
        optimizationMode: optimizationMode
      });
      setActiveRoute(res.data);
    } catch (err) {
      console.error("Recalculation error:", err);
    }
  };

  const showToast = (message, type = 'info') => {
    setToast({ message, type });
    setTimeout(() => setToast(null), 4000);
  };

  return (
    <TrafficContext.Provider value={{
      roads,
      intersections,
      loading,
      isSimulating,
      setIsSimulating,
      activeRoute,
      setActiveRoute,
      selectedSource,
      setSelectedSource,
      selectedDestination,
      setSelectedDestination,
      optimizationMode,
      setOptimizationMode,
      heatmapMode,
      setHeatmapMode,
      fetchRoadNetwork,
      recalculateActiveRoute,
      toast,
      showToast
    }}>
      {children}
    </TrafficContext.Provider>
  );
};

export const useTraffic = () => useContext(TrafficContext);
