// src/pages/AdminDashboard.jsx
import React, { useEffect, useState } from "react";
import api from "../api/axiosInstance";

export default function AdminDashboard() {
  const [stats, setStats] = useState(null);
  const [users, setUsers] = useState([]);

  useEffect(() => {
    // sample: get summary/stats if backend has endpoint. If not, it will fail silently.
    (async () => {
      try {
        const res = await api.get("/api/admin/summary"); // optional endpoint
        setStats(res.data);
      } catch (e) {
        // ignore if endpoint not present
      }
      try {
        const resp = await api.get("/api/admin/users"); // optional admin users endpoint
        setUsers(resp.data || []);
      } catch (e) {
        // ignore
      }
    })();
  }, []);

  return (
    <div className="min-h-screen bg-gradient-to-br from-green-50 to-blue-50 py-10">
      <div className="max-w-6xl mx-auto px-6">
        <div className="bg-white rounded-2xl shadow p-6 mb-6">
          <h1 className="text-3xl font-bold text-green-700">Admin Dashboard</h1>
          <p className="text-gray-600 mt-1">Manage users, providers and bookings from here.</p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-6">
          <div className="bg-white rounded-xl shadow p-4">
            <h3 className="text-sm text-gray-500">Total Users</h3>
            <p className="text-2xl font-semibold">{stats?.totalUsers ?? "—"}</p>
          </div>
          <div className="bg-white rounded-xl shadow p-4">
            <h3 className="text-sm text-gray-500">Providers</h3>
            <p className="text-2xl font-semibold">{stats?.providers ?? "—"}</p>
          </div>
          <div className="bg-white rounded-xl shadow p-4">
            <h3 className="text-sm text-gray-500">Bookings</h3>
            <p className="text-2xl font-semibold">{stats?.bookings ?? "—"}</p>
          </div>
        </div>

        <div className="bg-white rounded-xl shadow p-4">
          <h2 className="text-lg font-semibold mb-3">Recent users</h2>
          <div className="space-y-2">
            {users.length === 0 && <div className="text-gray-500">No user data (or endpoint not implemented).</div>}
            {users.map((u) => (
              <div key={u.id} className="flex items-center justify-between border p-3 rounded">
                <div>
                  <div className="font-medium">{u.name}</div>
                  <div className="text-sm text-gray-500">{u.email} • {u.role}</div>
                </div>
                <div className="text-sm text-gray-500">{new Date(u.created_at || u.createdAt || Date.now()).toLocaleString()}</div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
