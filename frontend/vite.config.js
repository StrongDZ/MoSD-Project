import { defineConfig, loadEnv } from "vite";
import react from "@vitejs/plugin-react";
import tailwindcss from "@tailwindcss/vite";

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
        plugins: [tailwindcss(), react()],
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
        build: {
            sourcemap: isDev,
            rollupOptions: {
                output: {
                    manualChunks: undefined,
                },
            },
        },
        define: {
            __DEV__: isDev,
            // Inject environment variables during build
            "process.env.VITE_BACKEND_HOST": JSON.stringify(backendHost),
            "process.env.VITE_BACKEND_PORT": JSON.stringify(backendPort),
            "process.env.NODE_ENV": JSON.stringify(mode),
        },
        css: {
            devSourcemap: isDev,
        },
        optimizeDeps: {
            include: ["react", "react-dom"],
        },
        esbuild: {
            keepNames: isDev,
            minifyIdentifiers: !isDev,
        },
    };
});
