import { Box, IconButton, useTheme } from "@mui/material";
import { useContext, useState, useEffect } from "react";
import { useAuth } from "../../contexts/AuthProvider";
import { useNavigate } from "react-router-dom";
import { ColorModeContext, tokens } from "../../theme";
import InputBase from "@mui/material/InputBase";
import LightModeOutlinedIcon from "@mui/icons-material/LightModeOutlined";
import DarkModeOutlinedIcon from "@mui/icons-material/DarkModeOutlined";
import NotificationsOutlinedIcon from "@mui/icons-material/NotificationsOutlined";
import SettingsOutlinedIcon from "@mui/icons-material/SettingsOutlined";
import PersonOutlinedIcon from "@mui/icons-material/PersonOutlined";
import SearchIcon from "@mui/icons-material/Search";

const Topbar = () => {
  const theme = useTheme();
  const colors = tokens(theme.palette.mode);
  const colorMode = useContext(ColorModeContext);
  const [isUserMenuOpen, setIsUserMenuOpen] = useState(false);
  const navigate = useNavigate();
  const { logout, token, username } = useAuth();
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  useEffect(() => {
    setIsAuthenticated(token !== null);
  }, [token]);

  const handleLogout = () => {
    logout();
    setIsAuthenticated(false);
    navigate("/");
  };

  const handleNavigation = (path) => {
    setIsUserMenuOpen(false);
    navigate(path);
  };

  return (
    <Box display="flex" justifyContent="space-between" p={2}>
      {/* SEARCH BAR */}
      <Box
        display="flex"
        backgroundColor={colors.primary[400]}
        borderRadius="3px"
      >
        <InputBase sx={{ ml: 2, flex: 1 }} placeholder="Search" />
        <IconButton type="button" sx={{ p: 1 }}>
          <SearchIcon />
        </IconButton>
      </Box>

      {/* ICONS */}
      <Box display="flex">
        <IconButton onClick={colorMode.toggleColorMode}>
          {theme.palette.mode === "dark" ? (
            <DarkModeOutlinedIcon />
          ) : (
            <LightModeOutlinedIcon />
          )}
        </IconButton>
        <IconButton>
          <NotificationsOutlinedIcon />
        </IconButton>
        <IconButton>
          <SettingsOutlinedIcon />
        </IconButton>
        <Box position="relative">
          <IconButton onClick={() => setIsUserMenuOpen(!isUserMenuOpen)}>
            <PersonOutlinedIcon />
          </IconButton>

          {isUserMenuOpen && (
            <Box
              position="absolute"
              right={0}
              mt={2}
              bgcolor="white"
              boxShadow={3}
              borderRadius={1}
              zIndex={10}
              minWidth={150}
            >
              {isAuthenticated ? (
                <>
                  <Box
                    onClick={handleLogout}
                    px={2}
                    py={1}
                    sx={{
                      cursor: "pointer",
                      backgroundColor: colors.primary[400],
                    }}
                  >
                    Đăng xuất
                  </Box>
                </>
              ) : (
                <>
                  <Box
                    onClick={() => handleNavigation("/login")}
                    px={2}
                    py={1}
                    sx={{
                      cursor: "pointer",
                      "&:hover": { backgroundColor: "#f0f0f0" },
                    }}
                  >
                    Đăng nhập
                  </Box>
                  <Box
                    onClick={() => handleNavigation("/register")}
                    px={2}
                    py={1}
                    sx={{
                      cursor: "pointer",
                      "&:hover": { backgroundColor: "#f0f0f0" },
                    }}
                  >
                    Đăng ký
                  </Box>
                </>
              )}
            </Box>
          )}
        </Box>
      </Box>
    </Box>
  );
};

export default Topbar;
