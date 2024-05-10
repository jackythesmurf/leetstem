import { useNavigate } from "react-router-dom";
import { useState, useEffect } from "react";
import { FaEye, FaEyeSlash } from "react-icons/fa";

import AuthService from "../services/auth.service";

interface ResetPasswordEmailProps {
  setBackground: (params: string) => void;
}

function ResetPasswordEmail(props: ResetPasswordEmailProps) {
  let navigate = useNavigate();

  useEffect(() => {
    props.setBackground("hex2");
  }, []);

  const [email, setEmail] = useState("");
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");
  const [error, setError] = useState(false);

  const [successful, setSuccessful] = useState(false);

  useEffect(() => {
    const currentUser = AuthService.getCurrentUser();
    if (currentUser) {
      navigate("/profile");
    }
  }, []);

  const handlePwdResetRequest = () => {
    //validate
    if (email == "") {
      setMessage("Email is required");
      setError(true);
      return;
    }

    //loading
    setLoading(true);

    AuthService.pwd_reset_request(email).then(
      () => {
        setSuccessful(true);
        setMessage(
            "Password Reset Request successfully!\n" +
            "Please check your email to change your password."
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

  return (
    <div className="flex items-center justify-center m-[7%]">
      {!successful && (
          <div className="w-[80%] sm:w-[70%] md:w-[60%] xl:w-[50%]">
            <div className="flex items-center justify-center mb-6">
              <h1 className="dark:text-[#ffe8c0] text-custom-blue font-bold text-6xl">
                Reset Password
              </h1>
            </div>
            <div className="lex items-center justify-center w-full">
              <h1 className="text-custom-yellow font-bold text-md text-center mt-14">
                Please enter the email registered with your account. <br></br>
                We will email you the link to reset the password.
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
                    Your Email
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

              <div className="flex items-center justify-center mt-20 relative">
                <button
                    className={`flex items-center justify-center shadow bg-[#ffd186] shadow-md mb-8
                                ${
                        loading
                            ? "bg-opacity-50 cursor-not-allowed"
                            : "enabled:hover:bg-custom-yellow enabled:hover:scale-105"
                    } 
                                duration-300 focus:shadow-outline focus:outline-none text-black font-bold py-2 px-4 rounded w-2/5 h-12 text-2xl`}
                    type="button"
                    onClick={handlePwdResetRequest}
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

export default ResetPasswordEmail;
