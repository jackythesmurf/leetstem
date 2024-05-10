import { useNavigate } from "react-router-dom";
import { useState, useEffect } from "react";

import AuthService from "../../services/auth.service";

interface VerifyProps {
  setBackground: (params: string) => void;
}

const sleep = (ms: number): Promise<void> => {
  return new Promise((resolve) => setTimeout(resolve, ms));
};

function Verify(props: VerifyProps) {
  let navigate = useNavigate();

  useEffect(() => {
    props.setBackground("hex1");
  }, []);

  const token = new URL(window.location.href).searchParams.get("token");
  const [message, setMessage] = useState("");
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    AuthService.verify(token).then(
      async () => {
        await sleep(1000 * 5);
        setIsLoading(false);
        setMessage("Verification Success, Redirecting...");
        await sleep(1000 * 1);
        navigate("/login");
      },
      async (error) => {
        await sleep(1000 * 5);
        const resMessage =
          (error.response &&
            error.response.data &&
            error.response.data.message) ||
          error.message ||
          error.toString();

        setIsLoading(false);
        setMessage(resMessage);
      }
    );
  }, [token]);

  return (
    <div className="flex items-center justify-center m-[7%]">
      <div className="w-[60%] sm:w-[50%] md:w-[40%] xl:w-[30%]">
        <div className="flex items-center justify-center mb-6">
          <h1 className="dark:text-custom-yellow text-custom-blue font-bold text-4xl">
            Email Verification
          </h1>
        </div>
        <div className="flex items-center justify-center mb-6">
          {isLoading ? (
            <span className="flex items-center">
              <span className="w-6 h-6 border-t-4 dark:border-custom-yellow border-custom-blue rounded-full animate-spin-button mr-4"></span>
              <p className="dark:text-custom-yellow text-custom-blue text-2xl">
                Verifying Email
              </p>
            </span>
          ) : (
            <p className="dark:text-custom-yellow text-custom-blue text-2xl">
              {message}
            </p>
          )}
        </div>
      </div>
    </div>
  );
}

export default Verify;
