import React from 'react';
import { Link } from 'react-router-dom';
import { MapPinOff, ArrowLeft } from 'lucide-react';

const NotFoundView = () => {
  return (
    <div className="min-h-screen flex flex-col items-center justify-center p-4 bg-slate-950 text-center space-y-4">
      <div className="p-4 rounded-3xl bg-rose-500/10 border border-rose-500/30 text-rose-400">
        <MapPinOff className="w-16 h-16" />
      </div>
      <h1 className="text-4xl font-extrabold text-white">404 - Corridor Not Found</h1>
      <p className="text-sm text-slate-400 max-w-sm">
        The traffic route or page segment you requested does not exist in our graph database.
      </p>
      <Link
        to="/"
        className="inline-flex items-center gap-2 px-5 py-2.5 rounded-xl bg-sky-600 hover:bg-sky-500 text-white font-bold text-xs shadow-lg"
      >
        <ArrowLeft className="w-4 h-4" /> Return to Dashboard
      </Link>
    </div>
  );
};

export default NotFoundView;
