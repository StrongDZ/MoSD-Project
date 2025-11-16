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
            watch: {
                usePolling: false,
                ignored: ['**/node_modules/**', '**/dist/**', '**/.git/**', '**/coverage/**']
            },
            hmr: {
                overlay: isDev
            },
            fs: {
                cachedChecks: true
            },
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
            sourcemap: false,
            target: 'esnext',
            minify: 'esbuild',
            rollupOptions: {
                output: {
                    manualChunks: {
                        'react-vendor': ['react', 'react-dom', 'react-router-dom'],
                        'mui-vendor': ['@mui/material', '@mui/icons-material', '@mui/x-data-grid'],
                        'utils-vendor': ['axios', 'dayjs', 'react-toastify']
                    },
                },
            },
            chunkSizeWarningLimit: 1000,
        },
        define: {
            __DEV__: isDev,
            "process.env.VITE_BACKEND_HOST": JSON.stringify(backendHost),
            "process.env.VITE_BACKEND_PORT": JSON.stringify(backendPort),
            "process.env.NODE_ENV": JSON.stringify(mode),
        },
        css: {
            devSourcemap: false,
        },
        optimizeDeps: {
            include: [],
            exclude: [
                '@mui/material',
                '@mui/icons-material',
                '@mui/x-data-grid',
                '@mui/x-date-pickers',
                '@emotion/react',
                '@emotion/styled'
            ],
            entries: ['src/main.jsx'],
            esbuildOptions: {
                logLevel: 'error',
                treeShaking: true
            }
        },
        esbuild: {
            keepNames: isDev,
            minifyIdentifiers: !isDev,
            jsxInject: `import React from 'react'`,
            logOverride: { 'this-is-undefined-in-esm': 'silent' }
        },
    };
});

