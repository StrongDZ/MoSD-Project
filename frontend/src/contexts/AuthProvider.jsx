import React, { createContext, useContext, useState } from "react";

// Tạo Context
const AuthContext = createContext();

// Provider chính
export const AuthProvider = ({ children }) => {
  const [token, setToken] = useState(null);
  const [role, setRole] = useState(null);
  const [username, setUsername] = useState(null);

  const login = (newToken, newRole, newUsername) => {
    setToken(newToken);
    setRole(newRole);
    setUsername(newUsername);
    // (Tuỳ chọn) Lưu vào localStorage nếu muốn giữ login khi reload
    localStorage.setItem("token", newToken);
    localStorage.setItem("role", newRole);
    localStorage.setItem("username", newUsername);
  };

  const logout = () => {
    setToken(null);
    setRole(null);
    localStorage.removeItem("token");
    localStorage.removeItem("role");
  };

  // (Tuỳ chọn) Lấy lại từ localStorage nếu người dùng đã login trước đó
  React.useEffect(() => {
    const savedToken = localStorage.getItem("token");
    const savedRole = localStorage.getItem("role");
    const savedUsername = localStorage.getItem("username");

    if (savedToken) {
      setToken(savedToken);
      setRole(savedRole);
      setUsername(savedUsername);
    }
  }, []);

  return (
    <AuthContext.Provider value={{ token, role, username, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

// Custom hook để dùng
export const useAuth = () => useContext(AuthContext);
