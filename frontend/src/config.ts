/// <reference types="vite/client" />

// src/config.ts

// Define environment variables interface to match Vite's types
interface ImportMetaEnv {
    readonly VITE_BACKEND_HOST: string;
    readonly VITE_BACKEND_PORT: string;
    readonly PORT: string | undefined;
    readonly SERVER_NAME: string;
    readonly CORS_ALLOWED_ORIGIN: string;
    readonly MODE: string;
    readonly DEV: boolean;
    readonly PROD: boolean;
    readonly SSR: boolean;
}

interface ImportMeta {
    readonly env: ImportMetaEnv;
}

// Config interface for type safety
interface Config {
    api: {
        url: string;
        baseUrl: string;
        wsUrl: string;
    };
    server: {
        port: string | number;
        host: string;
        cors: {
            origin: string;
        };
    };
    env: {
        mode: string;
        isDevelopment: boolean;
        isProduction: boolean;
    };
}

// Determine the protocol based on the environment and hostname
const isLocalhost = (host: string) => {
    return host === "localhost" || host === "backend" || host.includes("127.0.0.1");
};

const getProtocol = (host: string) => {
    return isLocalhost(host) ? "http" : "https";
};

const getWsProtocol = (host: string) => {
    return isLocalhost(host) ? "ws" : "wss";
};

// Get environment variables with fallbacks
const isDev = import.meta.env.MODE === "development";
const backendHost = import.meta.env.VITE_BACKEND_HOST || (isDev ? "localhost" : "monkey-d-vuvi-backend.onrender.com");
const backendPort = import.meta.env.VITE_BACKEND_PORT || (isDev ? "8080" : "443");
const protocol = getProtocol(backendHost);
const wsProtocol = getWsProtocol(backendHost);

// Build the API URL
const buildApiUrl = () => {
    if (isDev) {
        return `${protocol}://${backendHost}:${backendPort}`;
    }
    // In production, don't include port 443 in the URL
    return `${protocol}://${backendHost}`;
};

const config: Config = {
    api: {
        url: buildApiUrl(),
        baseUrl: buildApiUrl(),
        wsUrl: `${wsProtocol}://${backendHost}${isDev ? `:${backendPort}` : ""}`,
    },
    server: {
        port: import.meta.env.PORT || (isDev ? 5173 : 80),
        host: import.meta.env.SERVER_NAME || (isDev ? "localhost" : "monkey-d-vuvi-frontend.onrender.com"),
        cors: {
            origin: import.meta.env.CORS_ALLOWED_ORIGIN || "*",
        },
    },
    env: {
        mode: import.meta.env.MODE,
        isDevelopment: isDev,
        isProduction: !isDev,
    },
};

export default config;
