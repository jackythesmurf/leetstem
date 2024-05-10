import { useNavigate } from "react-router-dom";
import { useState, useEffect } from "react";
import { FaEye, FaEyeSlash } from "react-icons/fa";

import AuthService from "../services/auth.service";

interface ResetPasswordProps {
  setBackground: (params: string) => void;
}

function ResetPassword(props: ResetPasswordProps) {
  let navigate = useNavigate();

  useEffect(() => {
    props.setBackground("hex2");
  }, []);

  const token = new URL(window.location.href).searchParams.get("token");

  const [newPassword, setNewPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");
  const [error, setError] = useState(false);
  const [successful, setSuccessful] = useState(false);
  const [showNewPassword, setShowNewPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [confirmPassword, setConfirmPassword] = useState("");

  const [passwordStrength, setPasswordStrength] = useState(null);
  const [passwordSuggestions, setPasswordSuggestions] = useState<string[]>([]);

  useEffect(() => {
    const currentUser = AuthService.getCurrentUser();
    if (currentUser) {
      navigate("/profile");
    }
  }, []);

  const handleRegister = async () => {
    //validate
    if (newPassword == "" || confirmPassword == "") {
      setMessage("All fields need to be filled in");
      setError(true);
      return;
    }
    if (newPassword !== confirmPassword) {
      setMessage("Password do not match");
      setError(true);
      return;
    }

    //loading
    setLoading(true);

    AuthService.pwd_reset(token, newPassword).then(
      () => {
        navigate("/login");
      },
      (error) => {
        const resMessage =
          (error.response &&
            error.response.data &&
            error.response.data.message) ||
          error.message ||
          error.toString();

        setSuccessful(false);
        setLoading(false);
        setMessage(resMessage);
        setError(true);
        setNewPassword("");
        setConfirmPassword("");
      }
    );
  };

  const handleNewPasswordChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    //get rid of the error if there is one
    if (error) {
      setError(false);
    }
    const newPassword = event.target.value;
    setNewPassword(newPassword);

    const result = window.zxcvbn(newPassword);
    setPasswordStrength(result.score);
    setPasswordSuggestions(result.feedback.suggestions);
  };

  const handleConfirmPasswordChange = (
    event: React.ChangeEvent<HTMLInputElement>
  ) => {
    if (error) {
      setError(false);
    }
    setConfirmPassword(event.target.value);
  };

  const toggleNewPasswordVisibility = () => {
    setShowNewPassword((prev) => !prev);
  };

  const toggleConfirmPasswordVisibility = () => {
    setShowConfirmPassword((prev) => !prev);
  };

  return (
    <div className="flex items-center justify-center m-[7%]">
      {!successful && (
        <div className="w-[80%] sm:w-[70%] md:w-[60%] xl:w-[50%]">
          <div className="flex items-center justify-center mb-6">
            <h1 className="dark:text-[#ffe8c0] text-custom-blue font-bold text-6xl">
              Reset Password
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
            <div className="flex items-center justify-center">
              <div className="w-1/3">
                <label className="block text-custom-blue dark:text-[#ffe8c0] font-bold text-left">
                  New Password
                </label>
              </div>
              <div className="w-2/3 flex flex-col">
                <div className="relative">
                  <input
                    className="bg-white appearance-none border border-[#c4c4c4] rounded w-full py-2 
                    px-4 text-gray-700 leading-tight focus:outline-none focus:bg-white focus:border-[#ffd186]"
                    id="inline-full-name"
                    type={showNewPassword ? "text" : "password"}
                    value={newPassword}
                    onChange={handleNewPasswordChange}
                    required
                  />
                  <button
                    type="button"
                    onClick={toggleNewPasswordVisibility}
                    className="absolute right-3 top-1/2 transform -translate-y-1/2"
                  >
                    {showNewPassword ? <FaEye /> : <FaEyeSlash />}
                  </button>
                </div>
              </div>
            </div>

            <div className="flex justify-center mt-1 h-[2rem]">
              <div className="w-1/3"></div>
              <div className="w-2/3">
                {newPassword && (
                  <div className="flex items-center justify-center">
                    <span className="mr-[1rem] block text-xs text-custom-blue dark:text-custom-yellow font-bold">
                      Strength:
                    </span>
                    <div className="w-2/3 border border-gray-300 rounded overflow-hidden">
                      <div
                        className={`h-2 ${passwordStrengthBarColor(
                          passwordStrength
                        )}`}
                        style={{ width: `${(passwordStrength + 1) * 20}%` }}
                      ></div>
                      <div className="bg-gray-200 flex-1"></div>
                    </div>
                  </div>
                )}
              </div>
            </div>

            {/* <div className="ml-[8rem] w-2/3 mt-2 mb-4 flex items-center justify-left"></div> */}

            <div className="flex items-center justify-center mb-10">
              <div className="w-1/3">
                <label className="block text-custom-blue dark:text-[#ffe8c0] font-bold text-left">
                  Confirm Password
                </label>
              </div>
              <div className="w-2/3 flex flex-col relative">
                <input
                  className="bg-white appearance-none border border-[#c4c4c4] rounded w-full py-2 px-4 
                  text-gray-700 leading-tight focus:outline-none focus:bg-white focus:border-[#ffd186]"
                  type={showConfirmPassword ? "text" : "password"}
                  value={confirmPassword}
                  onChange={handleConfirmPasswordChange}
                  required
                />
                <button
                  type="button"
                  onClick={toggleConfirmPasswordVisibility}
                  className="absolute right-3 top-1/2 transform -translate-y-1/2"
                >
                  {showConfirmPassword ? <FaEye /> : <FaEyeSlash />}
                </button>
              </div>
            </div>

            <div className="flex items-center justify-center mb-6 relative">
              <button
                className={`flex items-center justify-center bg-[#ffd186] shadow-md
                                ${
                                  loading
                                    ? "bg-opacity-50 cursor-not-allowed"
                                    : "enabled:hover:bg-custom-yellow enabled:hover:scale-105"
                                } 
                                duration-300 focus:shadow-outline focus:outline-none text-black font-bold py-2 px-4 rounded w-2/5 h-12 text-2xl`}
                type="button"
                onClick={handleRegister}
                disabled={loading}
              >
                {loading ? (
                  <span className="absolute w-6 h-6 border-t-2 border-white rounded-full animate-spin-button"></span>
                ) : (
                  "Submit"
                )}
              </button>
            </div>
          </form>
        </div>
      )}

      <div className="flex items-center justify-center mb-8 h-6">
        {successful && (
          <label className="dark:text-custom-yellow text-custom-blue font-bold text-2xl">
            {message}
          </label>
        )}
      </div>
    </div>
  );
}

const passwordStrengthBarColor = (strength: number | null) => {
  switch (strength) {
    case 0:
      return "bg-red-500";
    case 1:
      return "bg-orange-500";
    case 2:
      return "bg-yellow-500";
    case 3:
      return "bg-green-400";
    case 4:
      return "bg-green-500";
    default:
      return "bg-gray-200";
  }
};

export default ResetPassword;
