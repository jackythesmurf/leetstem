import { useNavigate } from "react-router-dom";
import { useState, useEffect } from "react";
import { FaEye, FaEyeSlash } from "react-icons/fa";

import AuthService from "../../services/auth.service";

interface LoginProps {
  setBackground: (params: string) => void;
}

function Login(props: LoginProps) {
  let navigate = useNavigate();

  useEffect(() => {
    props.setBackground("hex1");
  }, []);

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");
  const [error, setError] = useState(false);

  const [showPassword, setShowPassword] = useState(false);

  useEffect(() => {
    const currentUser = AuthService.getCurrentUser();
    if (currentUser) {
      navigate("/profile");
    }
  }, []);

  const handleLogin = () => {
    //validate
    if (email == "" || password == "") {
      setMessage("Email and Password are required");
      setError(true);
      return;
    }

    // TODO: Need to validate the password here

    //loading
    setLoading(true);

    //logging (REMOVE LATER)
    console.log(email, password);

    AuthService.login(email, password).then(
      () => {
        navigate("/profile");
      },
      (error) => {
        const resMessage =
          (error.response &&
            error.response.data &&
            error.response.data.message) ||
          error.message ||
          error.toString();

        setLoading(false);
        setMessage(resMessage);
        setError(true);
        setEmail("");
        setPassword("");
      }
    );
  };

  const handleEmailChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    //get rid of the error if there is one
    if (error) {
      setError(false);
    }
    setEmail(event.target.value);
  };

  const handlePasswordChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    //get rid of the error if there is one
    if (error) {
      setError(false);
    }
    setPassword(event.target.value);
  };

  const togglePasswordVisibility = () => {
    setShowPassword((prev) => !prev);
  };

  return (
    <div className="flex items-center justify-center m-[7%]">
      <div className="w-[60%] sm:w-[50%] md:w-[40%] xl:w-[30%]">
        <div className="flex items-center justify-center mb-6">
          <h1 className="dark:text-[#ffe8c0] text-custom-blue font-bold text-6xl">
            Login
          </h1>
        </div>

        <div className="flex items-center justify-center mb-8 h-6">
          {error && (
            <label className="block text-custom-error-red font-bold h-6">
              {message}
            </label>
          )}
        </div>

        <form className="w-full mb-6">
          <div className="flex items-center justify-center mb-8">
            <div className="w-1/3">
              <label className="block text-custom-blue dark:text-[#ffe8c0] font-bold text-left">
                Email
              </label>
            </div>
            <div className="w-2/3">
              <input
                className="bg-white appearance-none border border-[#c4c4c4] rounded w-full py-2 px-4 text-gray-700 leading-tight focus:outline-none focus:bg-white focus:border-[#ffd186] "
                id="inline-full-name"
                type="text"
                value={email}
                onChange={handleEmailChange}
                required
              />
            </div>
          </div>

          <div className="flex items-center justify-center mb-10">
            <div className="w-1/3">
              <label className="block text-custom-blue dark:text-[#ffe8c0] font-bold text-left">
                Password
              </label>
            </div>
            <div className="w-2/3 relative">
              <input
                className="bg-white appearance-none border border-[#c4c4c4] rounded w-full py-2 px-4 text-gray-700 leading-tight focus:outline-none focus:bg-white focus:border-[#ffd186]"
                id="inline-full-name"
                type={showPassword ? "text" : "password"}
                value={password}
                onChange={handlePasswordChange}
                required
              />
              <button
                type="button"
                onClick={togglePasswordVisibility}
                className="absolute right-3 top-1/2 transform -translate-y-1/2"
              >
                {showPassword ? <FaEye /> : <FaEyeSlash />}
              </button>
            </div>
          </div>

          <div className="flex items-center justify-center mb-6 relative">
            <button
              className={`flex items-center justify-center shadow bg-[#ffd186] shadow-md
                                ${
                                  loading
                                    ? "bg-opacity-50 cursor-not-allowed"
                                    : "enabled:hover:bg-custom-yellow enabled:hover:scale-105"
                                } 
                                duration-300 focus:shadow-outline focus:outline-none text-black font-bold py-2 px-4 rounded w-2/5 h-12 text-2xl`}
              type="button"
              onClick={handleLogin}
              disabled={loading}
            >
              {loading ? (
                <span className="absolute w-6 h-6 border-t-2 border-white rounded-full animate-spin-button"></span>
              ) : (
                "Login"
              )}
            </button>
          </div>
        </form>

        <div className="flex items-center justify-center mb-6">
          <button
            className="text-blue-600 dark:text-white hover:scale-105 duration-300 underline"
            onClick={() => navigate("/resetPasswordRequest")}
          >
            Forgot Password?
          </button>
        </div>
      </div>
    </div>
  );
}

export default Login;
