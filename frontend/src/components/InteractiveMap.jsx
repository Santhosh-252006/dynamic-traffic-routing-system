import React, { useState } from 'react';
import { useTraffic } from '../context/TrafficContext';
import { Flame, Layers, MapPin, Navigation } from 'lucide-react';

const InteractiveMap = ({ height = "520px", interactive = true }) => {
  const {
    intersections,
    roads,
    activeRoute,
    selectedSource,
    setSelectedSource,
    selectedDestination,
    setSelectedDestination,
    heatmapMode,
    setHeatmapMode,
    isSimulating
  } = useTraffic();

  const [hoveredRoad, setHoveredRoad] = useState(null);
  const [hoveredNode, setHoveredNode] = useState(null);

  const nodeMap = {};
  intersections.forEach((node) => {
    nodeMap[node.code] = node;
    nodeMap[node.name] = node;
  });

  const getTrafficColor = (level, status) => {
    if (status === 'CLOSED') return '#6B7280';
    switch (level) {
      case 'LOW': return '#10B981';
      case 'MEDIUM': return '#F59E0B';
      case 'HIGH': return '#F97316';
      case 'HEAVY': return '#EF4444';
      default: return '#3B82F6';
    }
  };

  const handleNodeClick = (nodeCode) => {
    if (!interactive) return;
    if (selectedSource === nodeCode) {
      setSelectedSource('');
    } else if (!selectedSource) {
      setSelectedSource(nodeCode);
    } else if (selectedDestination === nodeCode) {
      setSelectedDestination('');
    } else {
      setSelectedDestination(nodeCode);
    }
  };

  // Check if a road is part of activeRoute
  const isRoadInActivePath = (road) => {
    if (!activeRoute || !activeRoute.nodePath) return false;
    const path = activeRoute.nodePath;

    for (let i = 0; i < path.length - 1; i++) {
      const uName = path[i];
      const vName = path[i + 1];

      const uNode = nodeMap[uName];
      const vNode = nodeMap[vName];

      if (!uNode || !vNode) continue;

      if (
        (road.source === uNode.code && road.destination === vNode.code) ||
        (road.source === vNode.code && road.destination === uNode.code) ||
        (road.source === uName && road.destination === vName) ||
        (road.source === vName && road.destination === uName)
      ) {
        return true;
      }
    }
    return false;
  };

  return (
    <div className="relative rounded-2xl glass-panel border border-slate-800 overflow-hidden shadow-2xl">
      {/* Map Controls Header */}
      <div className="absolute top-4 right-4 z-20 flex items-center gap-2 bg-slate-900/90 backdrop-blur-md p-1.5 rounded-xl border border-slate-700/80 shadow-lg">
        <button
          onClick={() => setHeatmapMode(!heatmapMode)}
          className={`flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-medium transition ${
            heatmapMode ? 'bg-orange-500/20 text-orange-400 border border-orange-500/40' : 'text-slate-400 hover:text-slate-200'
          }`}
        >
          <Flame className="w-3.5 h-3.5" />
          <span>Heatmap Overlay</span>
        </button>
      </div>

      {/* SVG Canvas Map */}
      <svg
        viewBox="0 0 1000 620"
        className="w-full w-full bg-slate-950/80"
        style={{ height }}
      >
        <defs>
          {/* Neon Glow Filters */}
          <filter id="glow-route" x="-20%" y="-20%" width="140%" height="140%">
            <feGaussianBlur stdDeviation="4" result="blur" />
            <feComposite in="SourceGraphic" in2="blur" operator="over" />
          </filter>
          <filter id="glow-heavy" x="-30%" y="-30%" width="160%" height="160%">
            <feGaussianBlur stdDeviation="8" result="blur" />
            <feComposite in="SourceGraphic" in2="blur" operator="over" />
          </filter>

          {/* Grid Pattern */}
          <pattern id="grid" width="40" height="40" patternUnits="userSpaceOnUse">
            <path d="M 40 0 L 0 0 0 40" fill="none" stroke="rgba(255, 255, 255, 0.03)" strokeWidth="1" />
          </pattern>
        </defs>

        {/* Background Grid */}
        <rect width="1000" height="620" fill="url(#grid)" />

        {/* Heatmap Layer */}
        {heatmapMode && roads.map((road) => {
          if (road.trafficLevel !== 'HIGH' && road.trafficLevel !== 'HEAVY') return null;
          const u = nodeMap[road.source];
          const v = nodeMap[road.destination];
          if (!u || !v) return null;
          const midX = (u.xCoord + v.xCoord) / 2;
          const midY = (u.yCoord + v.yCoord) / 2;
          return (
            <circle
              key={`heat-${road.id}`}
              cx={midX}
              cy={midY}
              r={road.trafficLevel === 'HEAVY' ? "90" : "60"}
              fill={road.trafficLevel === 'HEAVY' ? "rgba(239, 68, 68, 0.25)" : "rgba(249, 115, 22, 0.18)"}
              filter="url(#glow-heavy)"
            />
          );
        })}

        {/* Road Edges */}
        {roads.map((road) => {
          const u = nodeMap[road.source];
          const v = nodeMap[road.destination];
          if (!u || !v) return null;

          const isPath = isRoadInActivePath(road);
          const color = getTrafficColor(road.trafficLevel, road.roadStatus);
          const isClosed = road.roadStatus === 'CLOSED';

          return (
            <g key={`road-${road.id}`} className="group cursor-pointer" onMouseEnter={() => setHoveredRoad(road)} onMouseLeave={() => setHoveredRoad(null)}>
              {/* Underlying Base Edge */}
              <line
                x1={u.xCoord}
                y1={u.yCoord}
                x2={v.xCoord}
                y2={v.yCoord}
                stroke={color}
                strokeWidth={isPath ? "8" : "4"}
                strokeDasharray={isClosed ? "6 6" : "none"}
                opacity={isPath ? "0.9" : "0.5"}
                className="transition-all duration-300"
              />

              {/* Animated Traffic Particle Stream */}
              {!isClosed && (
                <line
                  x1={u.xCoord}
                  y1={u.yCoord}
                  x2={v.xCoord}
                  y2={v.yCoord}
                  stroke={color}
                  strokeWidth={isPath ? "6" : "3"}
                  strokeDasharray="4 16"
                  className="animate-flow"
                  opacity="0.9"
                />
              )}

              {/* Active Dijkstra Route Overlay */}
              {isPath && (
                <line
                  x1={u.xCoord}
                  y1={u.yCoord}
                  x2={v.xCoord}
                  y2={v.yCoord}
                  stroke="#38BDF8"
                  strokeWidth="5"
                  filter="url(#glow-route)"
                  opacity="0.9"
                />
              )}
            </g>
          );
        })}

        {/* Intersections (Graph Nodes) */}
        {intersections.map((node) => {
          const isSource = selectedSource === node.code || selectedSource === node.name;
          const isDest = selectedDestination === node.code || selectedDestination === node.name;
          const isHovered = hoveredNode === node.code;

          let nodeColor = "#0EA5E9"; // Sky blue default
          if (isSource) nodeColor = "#10B981"; // Emerald Green
          if (isDest) nodeColor = "#A855F7"; // Purple

          return (
            <g
              key={`node-${node.id}`}
              transform={`translate(${node.xCoord}, ${node.yCoord})`}
              className="cursor-pointer transition-transform duration-200 hover:scale-125"
              onClick={() => handleNodeClick(node.code)}
              onMouseEnter={() => setHoveredNode(node.code)}
              onMouseLeave={() => setHoveredNode(null)}
            >
              {/* Outer Pulse Ring */}
              {(isSource || isDest) && (
                <circle
                  r="22"
                  fill={nodeColor}
                  opacity="0.25"
                  className="animate-ping"
                />
              )}

              {/* Node Outer Circle */}
              <circle
                r="12"
                fill="#0F172A"
                stroke={nodeColor}
                strokeWidth={isSource || isDest ? "4" : "3"}
                className="shadow-xl"
              />

              {/* Inner Core */}
              <circle
                r="5"
                fill={nodeColor}
              />

              {/* Label Tag */}
              <text
                y="-18"
                textAnchor="middle"
                fill={isSource || isDest ? "#FFFFFF" : "#94A3B8"}
                fontSize="11"
                fontWeight={isSource || isDest ? "700" : "500"}
                className="pointer-events-none select-none tracking-wide"
              >
                {node.name}
              </text>
            </g>
          );
        })}
      </svg>

      {/* Hover Information Banner */}
      {hoveredRoad && (
        <div className="absolute bottom-4 left-4 z-20 bg-slate-900/95 backdrop-blur-md p-3 rounded-xl border border-slate-700 shadow-2xl text-xs space-y-1">
          <div className="font-bold text-slate-200">{hoveredRoad.name}</div>
          <div className="flex items-center gap-3 text-slate-400">
            <span>Dist: <strong className="text-slate-200">{hoveredRoad.distance} km</strong></span>
            <span>Speed Limit: <strong className="text-slate-200">{hoveredRoad.speedLimit} km/h</strong></span>
            <span>Traffic: <strong style={{ color: getTrafficColor(hoveredRoad.trafficLevel, hoveredRoad.roadStatus) }}>{hoveredRoad.trafficLevel}</strong></span>
          </div>
        </div>
      )}
    </div>
  );
};

export default InteractiveMap;
