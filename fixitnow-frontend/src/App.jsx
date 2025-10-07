// src/App.jsx
import React from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import NavBar from "./components/NavBar";
import Home from "./pages/Home";
import Register from "./pages/Register";
import Login from "./pages/Login";
import Services from "./pages/Services";
import BookingForm from "./pages/BookingForm";
import MyBookings from "./pages/MyBookings";
import ProviderProfile from "./pages/ProviderProfile";
import ProviderDashboard from "./pages/ProviderDashboard";
import AdminDashboard from "./pages/AdminDashboard";

function App() {
  return (
    <BrowserRouter>
      <NavBar />
      <div className="container mx-auto px-4">
        <Routes>
          {/* Public Routes */}
          <Route path="/" element={<Home />} />
          <Route path="/register" element={<Register />} />
          <Route path="/login" element={<Login />} />

          {/* Customer Routes */}
          <Route path="/services" element={<Services />} />
          <Route path="/book/:service" element={<BookingForm />} />
          <Route path="/my-bookings" element={<MyBookings />} />

          {/* Provider Routes */}
          <Route path="/provider-profile" element={<ProviderProfile />} />
          <Route path="/provider-dashboard" element={<ProviderDashboard />} />

          {/* Admin Route */}
          <Route path="/admin-dashboard" element={<AdminDashboard />} />
        </Routes>
      </div>
    </BrowserRouter>
  );
}

export default App;
