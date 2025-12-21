import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { UserCircleIcon } from "@heroicons/react/24/outline";
import { useAuth } from "../../contexts/AuthProvider";
const UserMenu = () => {
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const navigate = useNavigate();
  const { logout } = useAuth();
  const { token, username } = useAuth();

  useEffect(() => {
    // Check if user is authenticated
    setIsAuthenticated(token !== null);
  }, [token]);

  const handleLogout = () => {
    // Clear authentication data
    logout();
    setIsAuthenticated(false);
    // Chuyển về trang chủ sau khi đăng xuất
    navigate("/");
  };

  const handleNavigation = (path) => {
    setIsMenuOpen(false);
    navigate(path);
  };

  return (
    <div className="relative">
      {/* User Icon Button */}
      <button
        onClick={() => setIsMenuOpen(!isMenuOpen)}
        className="flex items-center gap-2 text-gray-900 hover:text-[#FFB8E5] transition-colors duration-200"
      >
        <UserCircleIcon className="h-7 w-7" />
        {isAuthenticated && (
          <span className="text-base font-semibold">{username}</span>
        )}
      </button>

      {/* Dropdown Menu */}
      {isMenuOpen && (
        <div className="absolute right-0 mt-2 w-48 bg-white rounded-md shadow-lg py-1 z-50 ring-1 ring-black ring-opacity-5">
          {isAuthenticated ? (
            <>
              <button
                onClick={() => handleNavigation("/profile")}
                className="block w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
              >
                Thông tin cá nhân
              </button>
              <button
                onClick={() => handleNavigation("/bookings")}
                className="block w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
              >
                Đơn đặt phòng
              </button>
              <button
                onClick={() => {
                  handleLogout();
                  setIsMenuOpen(false);
                }}
                className="block w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
              >
                Đăng xuất
              </button>
            </>
          ) : (
            <>
              <button
                onClick={() => handleNavigation("/login")}
                className="block w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
              >
                Đăng nhập
              </button>
              <button
                onClick={() => handleNavigation("/register")}
                className="block w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
              >
                Đăng ký
              </button>
            </>
          )}
        </div>
      )}
    </div>
  );
};

export default UserMenu;
