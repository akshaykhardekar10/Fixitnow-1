import React, { useEffect, useState } from "react";
import api from "../api/axiosInstance";

export default function ProviderDashboard() {
  const [bookings, setBookings] = useState([]);

  const fetchBookings = () => {
    api.get("/api/provider/bookings", {
      headers: { Authorization: "Bearer " + localStorage.getItem("accessToken") },
    }).then(res => setBookings(res.data));
  };

  useEffect(() => { fetchBookings(); }, []);

  const updateStatus = async (id, status) => {
    await api.post(`/api/provider/bookings/${id}/${status}`, {}, {
      headers: { Authorization: "Bearer " + localStorage.getItem("accessToken") },
    });
    fetchBookings();
  };

  return (
    <div className="max-w-5xl mx-auto mt-8 p-6 bg-white rounded-xl shadow">
      <h2 className="text-2xl font-semibold text-green-700 mb-4">Booking Requests</h2>
      <div className="grid gap-4">
        {bookings.map(b=>(
          <div key={b.id} className="p-4 border rounded">
            <h3 className="font-semibold">{b.service}</h3>
            <p>{b.notes}</p>
            <p className="text-sm text-gray-500">📍 {b.location} | 🕒 {b.datetime}</p>
            <div className="flex gap-3 mt-2">
              <button onClick={()=>updateStatus(b.id,"accept")} className="bg-green-600 text-white px-3 py-1 rounded">Accept</button>
              <button onClick={()=>updateStatus(b.id,"reject")} className="bg-red-600 text-white px-3 py-1 rounded">Reject</button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
