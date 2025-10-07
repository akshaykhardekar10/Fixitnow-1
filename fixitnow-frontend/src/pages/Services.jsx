import React from "react";
import { useNavigate } from "react-router-dom";

const services = [
  { name: "Electrician", desc: "Wiring, repairs & more", img: "https://img.icons8.com/fluency/96/electricity.png" },
  { name: "Plumber", desc: "Pipes, leaks & bathrooms", img: "https://img.icons8.com/fluency/96/plumber.png" },
  { name: "Carpenter", desc: "Woodwork & furniture fixes", img: "https://img.icons8.com/fluency/96/woodworker.png" },
  { name: "Cleaning", desc: "Home & office cleaning", img: "https://img.icons8.com/fluency/96/cleaning-a-surface.png" },
];

export default function Services() {
  const nav = useNavigate();

  return (
    <div className="min-h-screen bg-gradient-to-r from-green-50 to-blue-50 py-10">
      <div className="max-w-6xl mx-auto text-center mb-10">
        <h1 className="text-4xl font-bold text-green-700 mb-2">Available Services</h1>
        <p className="text-gray-600">Choose a service and book instantly</p>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-4 gap-6 max-w-6xl mx-auto px-6">
        {services.map((s, i) => (
          <div
            key={i}
            onClick={() => nav(`/book/${s.name.toLowerCase()}`)}
            className="bg-white rounded-xl shadow-lg p-6 flex flex-col items-center text-center hover:scale-105 transition cursor-pointer"
          >
            <img src={s.img} alt={s.name} className="w-20 h-20 mb-4" />
            <h2 className="text-xl font-semibold text-green-700">{s.name}</h2>
            <p className="text-gray-500">{s.desc}</p>
          </div>
        ))}
      </div>
    </div>
  );
}
