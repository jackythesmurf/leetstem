import { useNavigate } from "react-router-dom";
import { useState, useEffect } from "react";
import { FaEye, FaEyeSlash } from "react-icons/fa";

import AuthService from "../../services/auth.service";

interface RegisterProps {
  setBackground: (params: string) => void;
}

function Register(props: RegisterProps) {
  let navigate = useNavigate();

  useEffect(() => {
    props.setBackground("hex2");
  }, []);

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");
  const [error, setError] = useState(false);
  const [successful, setSuccessful] = useState(false);

  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [confirmPassword, setConfirmPassword] = useState("");

  const [passwordStrength, setPasswordStrength] = useState(null);
  const [passwordSuggestions, setPasswordSuggestions] = useState<string[]>([]);

  const [isEmailInvalid, setIsEmailInvalid] = useState(false);

  useEffect(() => {
    const currentUser = AuthService.getCurrentUser();
    if (currentUser) {
      navigate("/profile");
    }
  }, []);

  const handleRegister = async () => {
    //validate
    if (email == "" || password == "") {
      setMessage("Email and Password are required");
      setError(true);
      return;
    }
    if (password !== confirmPassword) {
      setMessage("Password do not match");
      setError(true);
      return;
    }

    //loading
    setLoading(true);

    //logging (REMOVE LATER)
    console.log(email, password);

    AuthService.signup(email, password).then(
      () => {
        setSuccessful(true);
        setMessage(
          "User registered successfully!\n" +
            "Please check your email to verify your account."
        );
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
        setEmail("");
        setPassword("");
        setConfirmPassword("");
        setIsEmailInvalid(false);
      }
    );
  };

  const handleEmailChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    //get rid of the error if there is one
    if (error) {
      setError(false);
    }

    const newEmail = event.target.value;
    setEmail(newEmail);

    if (newEmail.length === 0 || window.validator.isEmail(newEmail)) {
      setIsEmailInvalid(false);
    } else {
      setIsEmailInvalid(true);
    }
  };

  const handlePasswordChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    //get rid of the error if there is one
    if (error) {
      setError(false);
    }
    const newPassword = event.target.value;
    setPassword(newPassword);

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

  const togglePasswordVisibility = () => {
    setShowPassword((prev) => !prev);
  };

  const toggleConfirmPasswordVisibility = () => {
    setShowConfirmPassword((prev) => !prev);
  };

  return (
    <div className="flex items-center justify-center m-[7%]">
      {!successful && (
        <div className="w-[60%] sm:w-[50%] md:w-[40%] xl:w-[30%]">
          <div className="flex items-center justify-center mb-6">
            <h1 className="dark:text-[#ffe8c0] text-custom-blue font-bold text-6xl">
              Registration
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
            <div className="flex items-center justify-center mb-10">
              <div className="w-1/3">
                <label className="block text-custom-blue dark:text-[#ffe8c0] font-bold text-left">
                  Email
                </label>
              </div>
              <div className="w-2/3 relative flex items-center">
                <input
                  className="bg-white appearance-none border border-[#c4c4c4] rounded w-full py-2 px-4 text-gray-700 leading-tight focus:outline-none focus:bg-white focus:border-[#ffd186] "
                  id="inline-full-name"
                  type="text"
                  value={email}
                  onChange={handleEmailChange}
                  required
                />
                {isEmailInvalid && (
                  <div
                    className="absolute left-full ml-3 p-2 border-custom-error-red border-2 
                  text-custom-error-red font-bold text-xs rounded text-center"
                  >
                    Invalid Email Format
                  </div>
                )}
              </div>
            </div>

            <div className="flex items-center justify-center">
              <div className="w-1/3">
                <label className="block text-custom-blue dark:text-[#ffe8c0] font-bold text-left">
                  Password
                </label>
              </div>
              <div className="w-2/3 flex flex-col">
                <div className="relative">
                  <input
                    className="bg-white appearance-none border border-[#c4c4c4] rounded w-full py-2 
                    px-4 text-gray-700 leading-tight focus:outline-none focus:bg-white focus:border-[#ffd186]"
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
            </div>

            <div className="flex justify-center mt-1 h-[2rem]">
              <div className="w-1/3"></div>
              <div className="w-2/3">
                {password && (
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
                  "Sign Up"
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

export default Register;
