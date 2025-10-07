import React, { useEffect, useState } from "react";
import api from "../api/axiosInstance";

export default function MyBookings() {
  const [bookings, setBookings] = useState([]);

  useEffect(() => {
    api.get("/api/bookings/my", {
      headers: { Authorization: "Bearer " + localStorage.getItem("accessToken") },
    }).then(res => setBookings(res.data));
  }, []);

  return (
    <div className="max-w-5xl mx-auto mt-8 p-6 bg-white shadow rounded-xl">
      <h2 className="text-2xl font-semibold text-green-700 mb-4">My Bookings</h2>
      <div className="grid gap-4">
        {bookings.map(b => (
          <div key={b.id} className="p-4 border rounded flex justify-between items-center">
            <div>
              <h3 className="font-semibold">{b.service}</h3>
              <p className="text-gray-500">{b.notes}</p>
              <p className="text-sm">📍 {b.location} | 🕒 {b.datetime}</p>
            </div>
            <span className={`px-3 py-1 rounded text-white ${b.status==="ACCEPTED"?"bg-green-500":b.status==="REJECTED"?"bg-red-500":"bg-yellow-500"}`}>
              {b.status}
            </span>
          </div>
        ))}
      </div>
    </div>
  );
}
