import React, { useState } from "react";
import api from "../api/axiosInstance";

export default function ProviderProfile() {
  const [form, setForm] = useState({
    categories: [],
    description: "",
  });
  const [msg, setMsg] = useState("");

  const toggleCategory = (cat) => {
    setForm(f => ({
      ...f,
      categories: f.categories.includes(cat)
        ? f.categories.filter(c => c !== cat)
        : [...f.categories, cat],
    }));
  };

  const save = async () => {
    try {
      await api.post("/api/provider/profile", form, {
        headers: { Authorization: "Bearer " + localStorage.getItem("accessToken") },
      });
      setMsg("Profile saved ✅");
    } catch {
      setMsg("Failed to save ❌");
    }
  };

  return (
    <div className="max-w-lg mx-auto mt-8 bg-white p-6 rounded-xl shadow">
      <h2 className="text-2xl font-semibold text-green-700 mb-4">Provider Profile</h2>
      <div className="space-y-3">
        <div>
          <p className="mb-2 font-medium">Select Categories</p>
          {["Electrician","Plumber","Carpenter","Cleaning"].map(cat=>(
            <label key={cat} className="block">
              <input type="checkbox" checked={form.categories.includes(cat)} onChange={()=>toggleCategory(cat)}/> {cat}
            </label>
          ))}
        </div>
        <textarea className="w-full border px-3 py-2 rounded" placeholder="Short description about your work" value={form.description} onChange={e=>setForm({...form, description:e.target.value})}/>
        <button onClick={save} className="w-full bg-blue-600 text-white py-2 rounded">Save Profile</button>
        {msg && <p className="text-sm text-red-600">{msg}</p>}
      </div>
    </div>
  );
}
