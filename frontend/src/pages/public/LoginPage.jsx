import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import config from "../../config";
import { useAuth } from "../../contexts/AuthProvider";
import { axiosRequest } from "../../utils/axiosUtils";
function LoginPage() {
  const navigate = useNavigate();
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [role, setRole] = useState("user"); // Default role
  const { login } = useAuth();
  const handleLoginSubmit = async (event) => {
    event.preventDefault();
    setError("");
    setLoading(true);

    const email = event.target.email.value;
    const password = event.target.password.value;

    try {
      const response = await axiosRequest({
        url: `${config.api.url}/api/auth/login/${role}`,
        method: "POST",
        data: {
          username: email,
          password: password,
        },
      });

      const metadata = response.data;
      const data = metadata.data;

      if (metadata.responseCode === 200) {
        login(data.token, data.role, data.username);
        if (role === "company") {
          navigate("/admin");
        } else {
          navigate("/");
        }
      } else {
        setError(metadata.message || "Đăng nhập thất bại");
      }
    } catch (error) {
      setError(
        error.response?.data?.message || "Email hoặc mật khẩu không đúng"
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="bg-gray-100 flex items-center justify-center min-h-screen font-[Inter]">
      <div className="bg-white p-8 rounded-lg shadow-md w-full max-w-sm">
        <h2 className="text-2xl font-semibold text-center text-gray-700 mb-6">
          Đăng nhập
        </h2>

        {error && (
          <div className="mb-4 p-3 bg-red-100 border border-red-400 text-red-700 rounded">
            {error}
          </div>
        )}

        <form onSubmit={handleLoginSubmit}>
          {/* Email/Username */}
          <div className="mb-4">
            <label
              htmlFor="login-email"
              className="block text-sm font-medium text-gray-600 mb-1"
            >
              Email hoặc tên đăng nhập
            </label>
            <input
              type="text"
              id="login-email"
              name="email"
              required
              placeholder="Nhập email hoặc tên đăng nhập"
              className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-pink-300 focus:border-transparent"
              disabled={loading}
            />
          </div>

          {/* Mật khẩu */}
          <div className="mb-4">
            <label
              htmlFor="login-password"
              className="block text-sm font-medium text-gray-600 mb-1"
            >
              Mật khẩu
            </label>
            <input
              type="password"
              id="login-password"
              name="password"
              required
              placeholder="••••••••"
              className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-pink-300 focus:border-transparent"
              disabled={loading}
            />
          </div>

          {/* Role chọn */}
          <div className="mb-6">
            <label
              htmlFor="login-role"
              className="block text-sm font-medium text-gray-600 mb-1"
            >
              Vai trò
            </label>
            <select
              id="login-role"
              name="role"
              className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-pink-300 focus:border-transparent"
              value={role}
              onChange={(e) => setRole(e.target.value)}
              disabled={loading}
            >
              <option value="user">Người dùng</option>
              <option value="company">Công ty</option>
            </select>
          </div>

          {/* Nút đăng nhập */}
          <button
            type="submit"
            className={`w-full bg-pink-200 text-gray-800 font-semibold py-2 px-4 rounded-md hover:bg-pink-300 focus:outline-none focus:ring-2 focus:ring-pink-400 focus:ring-opacity-50 transition duration-200 ${
              loading ? "opacity-50 cursor-not-allowed" : ""
            }`}
            disabled={loading}
          >
            {loading ? "Đang đăng nhập..." : "Đăng nhập"}
          </button>

          {/* Link đăng ký */}
          <p className="text-center text-sm text-gray-600 mt-4">
            Chưa có tài khoản?{" "}
            <button
              type="button"
              onClick={() => navigate("/register")}
              className="text-pink-500 hover:underline font-medium focus:outline-none"
              disabled={loading}
            >
              Đăng ký
            </button>
          </p>
        </form>
      </div>
    </div>
  );
}

export default LoginPage;
