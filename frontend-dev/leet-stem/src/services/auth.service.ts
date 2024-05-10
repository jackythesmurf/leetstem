import axios from "axios";

export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;
export const ACCESS_TOKEN = import.meta.env.VITE_ACCESS_TOKEN;
export const USER = import.meta.env.VITE_USER;

const auth_endpoint = `${API_BASE_URL}/auth/`;
const user_endpoint = `${API_BASE_URL}/user/`

class AuthService {
  login(email: string, password: string) {
    return axios
      .post(
        auth_endpoint + "signin",
        {
          email,
          password,
        },
        {
          withCredentials: true,
        }
      )
      .then((response) => {
        console.log();
        if (response.data.error_code === 1001) {
          console.log(response.data.error_code);
          return Promise.reject("Email and password don't match");
        } else if (response.data.error_code === 1002) {
          console.log(response.data.error_code);
          return Promise.reject("Email is malformed");
        } else if (response.data.error_code === 1003) {
          console.log(response.data.error_code);
          return Promise.reject("Password is empty");
        } else if (response.data.error_code === 0) {
          localStorage.setItem(USER, email);
          if (response.data.role == 1) {
            localStorage.setItem("staff", "true");
          }
          return 0;
        }
        return response.data;
      });
  }

  signup(email: string, password: string) {
    return axios
      .post(auth_endpoint + "signup-request", {
        email,
        password,
      })
      .then((response) => {
        if (response.data.error_code === 1002) {
          console.log(response.data.error_code);
          return Promise.reject("Email is malformed");
        } else if (response.data.error_code === 1003) {
          console.log(response.data.error_code);
          return Promise.reject("Password is empty");
        } else if (response.data.error_code === 0) {
          return 0;
        }
        return response.data;
      });
  }

  verify(token: string) {
    return axios
      .post(auth_endpoint + "signup-verify", {
        token,
      })
      .then((response) => {
        if (response.data.error_code === 9002) {
          console.log(response.data.error_code);
          return Promise.reject("Verification Link Expired");
        } else if (response.data.error_code === 0) {
          return 0;
        }
        return response.data;
      });
  }

  pwd_reset_request(email: string) {
    return axios
        .post(auth_endpoint + "passwd-reset-request", {
          email
        })
        .then((response) => {
          if (response.data.error_code === 1002) {
            console.log(response.data.error_code);
            return Promise.reject("Email is empty or malformed");
          } else if (response.data.error_code === 0) {
            return 0;
          }
          return response.data;
        });
  }

  pwd_reset(token: string, new_password: string) {
    return axios
        .post(auth_endpoint + "passwd-reset", {
          token,
          new_password
        })
        .then((response) => {
          if (response.data.error_code === 9002) {
            console.log(response.data.error_code);
            return Promise.reject("Verification Link Expired");
          } else if (response.data.error_code === 1003) {
            console.log(response.data.error_code);
            return Promise.reject("The new password is empty or weak");
          } else if (response.data.error_code === 0) {
            return 0;
          }
          return response.data;
        });
  }

  pwd_change(current_password: string, new_password: string) {
    return axios
        .post(user_endpoint + "chpasswd", {
          current_password,
          new_password,
        })
        .then((response) => {
          if (response.data.error_code === 1001) {
            console.log(response.data.error_code);
            return Promise.reject("The current password does not match the signed-in account");
          }
          else if (response.data.error_code === 1003) {
            console.log(response.data.error_code);
            return Promise.reject("The new password is empty or weak");
          } else if (response.data.error_code === 1005) {
            console.log(response.data.error_code);
            return Promise.reject("The session doesn't have a signed-in user");
          } else if (response.data.error_code === 0) {
            return 0;
          }
          return response.data;
        });
  }

  signout() {
    return axios.post(auth_endpoint + "signout").then((response) => {
      localStorage.removeItem(USER);
      localStorage.removeItem("staff");
    });
  }

  getCurrentUser() {
    const userStr = localStorage.getItem(USER);
    if (userStr) return userStr;
    return null;
  }
}

export default new AuthService();
