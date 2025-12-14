import React, { useState } from "react";
import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import {
  Routes,
  Route,
  BrowserRouter as Router,
  useLocation,
} from "react-router-dom";
import NavBar from "./components/public/NavBar";
import ScrollToTop from "./components/public/ScrollToTop";
import Home from "./pages/public/Home";
import TimDuThuyen from "./pages/public/TimDuThuyen";
import TimKhachSan from "./pages/public/TimKhachSan";
import ChiTietKhachSan from "./pages/public/ChiTietKhachSan";
import DoanhNghiep from "./pages/public/DoanhNghiep";
import DuThuyen from "./pages/public/DuThuyen";
import LoginPage from "./pages/public/LoginPage";
import RegisterPage from "./pages/public/RegisterPage";
import ChatBotWidget from "./components/public/ChatBotWidget";
import Footer from "./components/public/Footer";
import AdminLayout from "./components/admin/AdminLayout";
import ManageBooking from "./pages/admin/ManageBooking";
import ManageRoom from "./pages/admin/ManageRoom";
import { AuthProvider } from "./contexts/AuthProvider";
import BookingHistoryPage from "./pages/public/BookingHistoryPage";
import CompanyDashboard from "./pages/admin/CompanyDashboard";

const AppRoutes = () => {
  const [isChatOpen, setIsChatOpen] = useState(false);
  const location = useLocation();
  const isAdminPath = location.pathname.startsWith("/admin");

  return (
    <>
      <ScrollToTop />
      <div className="app min-h-screen flex flex-col">
        {!isAdminPath && <NavBar />}

        <div className="flex-1 flex transition-all duration-300 ease-in-out">
          <div className={`w-full transition-all duration-300 ease-in-out`}>
            <Routes>
              {/* Admin */}
              <Route path="/admin" element={<AdminLayout />}>
                <Route index element={<CompanyDashboard />} />
                <Route path="booking" element={<ManageBooking />} />
                <Route path="manage-room" element={<ManageRoom />} />
              </Route>

              {/* User */}
              <Route path="/" element={<Home />} />
              <Route path="/tim-du-thuyen" element={<TimDuThuyen />} />
              <Route path="/tim-khach-san" element={<TimKhachSan />} />
              <Route path="/khach-san/:id" element={<ChiTietKhachSan />} />
              <Route path="/doanh-nghiep" element={<DoanhNghiep />} />
              <Route path="/blog" element={<div>Blog</div>} />
              <Route path="/login" element={<LoginPage />} />
              <Route path="/register" element={<RegisterPage />} />
              <Route path="/du-thuyen/:id" element={<DuThuyen />} />

              {/* User Profile Routes */}
              <Route path="/profile" element={<div>Thông tin cá nhân</div>} />
              <Route path="/bookings" element={<BookingHistoryPage />} />
            </Routes>
          </div>
        </div>
        <ToastContainer position="top-right" autoClose={3000} />
        {!isAdminPath && (
          <ChatBotWidget isOpen={isChatOpen} setIsOpen={setIsChatOpen} />
        )}

        {!isAdminPath && <Footer />}
      </div>
    </>
  );
};

const App = () => {
  return (
    <AuthProvider>
      <Router>
        <AppRoutes />
      </Router>
    </AuthProvider>
  );
};

export default App;
