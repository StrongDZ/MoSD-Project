import { defineConfig, loadEnv } from "vite";
import react from "@vitejs/plugin-react";

// https://vitejs.dev/config/
export default defineConfig(({ command, mode }) => {
    // Load env file based on `mode` in the current working directory.
    const env = loadEnv(mode, process.cwd(), "");
    const isDev = mode === "development";

    // Determine backend URL based on environment
    const backendProtocol = isDev ? "http" : "https";
    const backendHost = env.VITE_BACKEND_HOST || (isDev ? "localhost" : "monkey-d-vuvi-backend.onrender.com");
    const backendPort = env.VITE_BACKEND_PORT || (isDev ? "8080" : "443");
    const backendUrl = `${backendProtocol}://${backendHost}${backendPort !== "443" ? `:${backendPort}` : ""}`;

    return {
        plugins: [react()],
        server: {
            port: parseInt(process.env.PORT || "5173"),
            host: true,
            proxy: {
                "/api": {
                    target: backendUrl,
                    changeOrigin: true,
                    secure: !isDev,
                    ws: true,
                },
            },
        },
    };
});

