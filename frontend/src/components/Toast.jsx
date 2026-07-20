import React from 'react';
import { CheckCircle, AlertTriangle, Info, X } from 'lucide-react';
import { useTraffic } from '../context/TrafficContext';

const Toast = () => {
  const { toast } = useTraffic();

  if (!toast) return null;

  const getIcon = () => {
    switch (toast.type) {
      case 'success': return <CheckCircle className="w-5 h-5 text-emerald-400" />;
      case 'warning': return <AlertTriangle className="w-5 h-5 text-amber-400" />;
      case 'error': return <AlertTriangle className="w-5 h-5 text-rose-400" />;
      default: return <Info className="w-5 h-5 text-sky-400" />;
    }
  };

  return (
    <div className="fixed bottom-6 right-6 z-50 flex items-center gap-3 px-4 py-3 rounded-xl shadow-2xl glass-panel text-white animate-bounce">
      {getIcon()}
      <span className="text-sm font-medium">{toast.message}</span>
    </div>
  );
};

export default Toast;
