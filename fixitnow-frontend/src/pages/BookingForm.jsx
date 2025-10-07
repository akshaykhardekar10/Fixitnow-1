import React, { useState } from "react";
import { useParams } from "react-router-dom";
import api from "../api/axiosInstance";

export default function BookingForm() {
  const { service } = useParams();
  const [form, setForm] = useState({
    service: service,
    subcategory: "",
    datetime: "",
    location: "",
    notes: "",
  });
  const [msg, setMsg] = useState("");

  const submit = async (e) => {
    e.preventDefault();
    try {
      await api.post("/api/bookings", form, {
        headers: { Authorization: "Bearer " + localStorage.getItem("accessToken") },
      });
      setMsg("Booking placed ✅");
    } catch {
      setMsg("Booking failed ❌");
    }
  };

  return (
    <div className="max-w-lg mx-auto bg-white p-6 rounded-xl shadow mt-8">
      <h2 className="text-2xl font-semibold text-green-700 mb-4">Book {service}</h2>
      <form onSubmit={submit} className="space-y-3">
        <input className="w-full border px-3 py-2 rounded" placeholder="Subcategory (e.g. Fan Repair)" value={form.subcategory} onChange={e=>setForm({...form, subcategory:e.target.value})}/>
        <input type="datetime-local" className="w-full border px-3 py-2 rounded" value={form.datetime} onChange={e=>setForm({...form, datetime:e.target.value})}/>
        <input className="w-full border px-3 py-2 rounded" placeholder="Location" value={form.location} onChange={e=>setForm({...form, location:e.target.value})}/>
        <textarea className="w-full border px-3 py-2 rounded" placeholder="Notes (Describe issue)" value={form.notes} onChange={e=>setForm({...form, notes:e.target.value})}/>
        <button className="w-full bg-green-600 text-white py-2 rounded">Book Service</button>
        {msg && <p className="text-sm text-red-600">{msg}</p>}
      </form>
    </div>
  );
}
