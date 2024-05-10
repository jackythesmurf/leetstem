import { useNavigate, useLocation } from "react-router-dom";
import { useState, useEffect } from "react";
import { AiOutlineQuestionCircle } from "react-icons/ai";
import { FaRegLightbulb } from "react-icons/fa";

import logoLight from "/images/logo/leet-stem-logo-light.png";
import logoDark from "/images/logo/leet-stem-logo-dark.png";
import ProfileDropdownMenu from "./ProfileDropdownMenu";
import api from "../axiosConfig";
import {USER} from "../services/auth.service";

interface Props {
  updateDarkMode: () => void;
}

function Navbar({ updateDarkMode }: Props) {
  let navigate = useNavigate();

  const routeChange = () => {
    let path = "/";
    navigate(path);
  };

  const loginChange = () => {
    let path = "/login";
    navigate(path);
  };

  const registerChange = () => {
    let path = "/register";
    navigate(path);
  };

  // 'anon': login register ? dark
  // 'logged-in': ? dark image
  const [navFormat, setNavFormat] = useState("anon");
  const location = useLocation();

  const [isProfileDropdownOpen, setIsProfileDropdownOpen] = useState(false);

  async function checkProfile() {
    // used to check if user is logged in
    let customConfig = {
      headers: {
        "Content-Type": "application/json",
      },
    };
    // Make request
    const result = await api
      .get("user/details", customConfig)
      .then((res) => {
        if (res.data.error_code === 1005) {
          setNavFormat("anon");
          localStorage.removeItem(USER);
          localStorage.removeItem("staff");
        } else {
          // Success
          setNavFormat("logged-in");
        }
      })
      .catch((error) => {
        setNavFormat("anon");
        localStorage.removeItem(USER);
        localStorage.removeItem("staff");
      });
  }

  useEffect(() => {
    // make a request to get user info to set logged-in or anon
    checkProfile();
  }, [location]);

  return (
    <>
      <div className="flex justify-between items-center h-[4rem] mx-auto px-6 dark:text-white text-black dark:bg-[#1a2c51] bg-[#F1F1F1] shadow-md">
        {/* Leet Stem logo with button to home page */}
        <button onClick={routeChange}>
          <div className="w-44 h-auto">
            <div className="dark:hidden">
              <img src={logoLight} alt="LeetSTEM" />
            </div>

            <div className="hidden dark:block">
              <img src={logoDark} alt="LeetSTEM" />
            </div>
          </div>
        </button>

        {/* The Links */}
        <ul className="flex space-x-10">
          {navFormat === "anon" ? (
            <li className="self-center font-sans font-semibold">
              <button onClick={loginChange}>Login</button>
            </li>
          ) : null}
          {navFormat === "anon" ? (
            <li className="self-center font-sans font-semibold">
              <button onClick={registerChange}>Register</button>
            </li>
          ) : null}
          <span className="self-center flex">
            <button
              onClick={() => {
                navigate("/help");
              }}
            >
              <AiOutlineQuestionCircle size={30} />
            </button>
          </span>
          <span className="self-center flex">
            <button onClick={updateDarkMode}>
              <FaRegLightbulb size={26} />
            </button>
          </span>
          {navFormat === "logged-in" ? (
            <span className="self-center flex">
              <button
                onClick={() => setIsProfileDropdownOpen(!isProfileDropdownOpen)}
              >
                <div className="dark:hidden">
                  <img
                    className="self-center w-7 h-7"
                    src={"/images/icons/1.png"}
                    alt="profile icon"
                  />
                </div>
                <div className="hidden dark:block">
                  <img
                    className="self-center w-7 h-7"
                    src={"/images/icons/2.png"}
                    alt="profile icon"
                  />
                </div>
              </button>
            </span>
          ) : null}
        </ul>
      </div>
      <ProfileDropdownMenu
        isOpen={isProfileDropdownOpen}
        setIsOpen={setIsProfileDropdownOpen}
      />
    </>
  );
}

export default Navbar;
