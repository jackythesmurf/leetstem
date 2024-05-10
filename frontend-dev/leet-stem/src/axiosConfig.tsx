import axios, { AxiosInstance, AxiosResponse } from "axios";

// Create an Axios instance with custom configurations
const api: AxiosInstance = axios.create({
  baseURL: "/api",
  timeout: 10000,
});

// Add a request interceptor to handle errors before sending the request
api.interceptors.request.use(
  (config) => {
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Add a response interceptor to handle errors after receiving the response
api.interceptors.response.use(
  (response: AxiosResponse) => {
    console.log("response", response);
    // If the response is successful, pass it through

    // may still be an error; check the error_code
    if (response.data.error_code != 0) {
      // then it failed
      return Promise.reject(response.data.error_code);
    }
    // else success, return data
    return response;
  },
  (error) => {
    console.log("error", error);
    const errorMessage =
      "HTTP Error: " +
      error.response.status +
      " : " +
      error.response.statusText;
    return Promise.reject(errorMessage);
  }
);

export default api;
