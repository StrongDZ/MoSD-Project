// utils/axiosHelper.js
import axios from "axios";
import config from "../config";
export const axiosRequest = async ({
    url,
    method = "GET",
    data = null,
    params = null,
    signal = null, // từ AbortController
    headers = {},
}) => {
    const token = localStorage.getItem("token");
    console.log("Token:", token);
    const finalHeaders = {
        "Content-Type": "application/json",
        ...headers,
    };

    if (token) {
        finalHeaders["Authorization"] = `Bearer ${token}`;
    }

    const fullUrl = url.startsWith("http") ? url : `${config.api.url}${url}`;
    console.log({
        url: fullUrl,
        method,
        data,
        params,
        headers: finalHeaders,
        signal,
    });
    const response = await axios({
        url: fullUrl,
        method,
        data,
        params,
        headers: finalHeaders,
        signal,
    });
    console.log("Response from server:", response);

    return response;
};
