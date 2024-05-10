import { useRef, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import AuthService from "../services/auth.service";

import api from "../axiosConfig";
import { useError } from "../context/ErrorContext.tsx";

interface ProfileDropdownMenuProps {
  isOpen: boolean;
  setIsOpen: (status: boolean) => void;
}

interface Profile {
  display_name: string;
  avatar: string;
  email: string;
  error_code: number;
  badges: Badge[];
}

interface Badge {
  badge_icon: string;
  badge_id: string;
  badge_name: string;
}

function ProfileDropdownMenu(props: ProfileDropdownMenuProps) {
  const dropdownMenuRef = useRef<HTMLInputElement>(null);
  let navigate = useNavigate();

  const { showError } = useError();

  const closeMenu = (e: MouseEvent) => {
    if (
      dropdownMenuRef.current &&
      !dropdownMenuRef.current.contains(e.target as Node)
    ) {
      props.setIsOpen(false);
    }
  };

  const handleProfileClick = () => {
    navigate("/profile");
    props.setIsOpen(false);
  };

  useEffect(() => {
    document.addEventListener("mousedown", closeMenu);
    return () => {
      document.removeEventListener("mousedown", closeMenu);
    };
  }, []);

  const handleSubjectsClick = () => {
    navigate("/profile/courses");
    props.setIsOpen(false);
  };

  const handleBadgesClick = () => {
    navigate("/profile/badges");
    props.setIsOpen(false);
  };

  const handleLogout = () => {
    console.log("logout clicked");
    AuthService.signout().then(
      () => {
        props.setIsOpen(false);

        // set profile to default
        setProfile({
          display_name: "...",
          avatar: "",
          email: "...",
          error_code: 0,
          badges: [],
        });

        navigate("/");
        window.location.reload();
      },
      (error) => {
        props.setIsOpen(false);

        console.log(error);
      }
    );
  };

  const [profile, setProfile] = useState<Profile>({
    display_name: "...",
    avatar: "",
    email: "...",
    error_code: 0,
    badges: [],
  });

  async function loadProfile() {
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
        // Success
        console.log(res);
        setProfile(res.data);
      })
      .catch((error) => {
        // showError(error);
      });
  }

  useEffect(() => {
    // get profile
    loadProfile();
  }, [props.isOpen]);

  return (
    <>
      <div ref={dropdownMenuRef}>
        {props.isOpen && (
          <div
            className="z-50 bg-[#FEFEFE] dark:bg-[#1a2c51] absolute flex flex-col mt-1 mr-8 right-0 rounded-lg p-1
        text-xs md:text-sm lg:text-base border-[#CED4DA] border-4 border-opacity-60
        w-[25rem] h-[23rem]
          shadow-2xl"
          >
            <div className="w-full flex flex-col">
              {/* User stuff */}
              <div className="m-4 flex">
                <div className="w-1/3 flex items-center">
                  <img
                    src={"." + profile.avatar}
                    className="w-24 flex rounded-full justify-center items-center mx-auto"
                  />
                </div>
                <div className="w-2/3 justify-center items-center flex flex-col">
                  <div className="w-full flex mb-2">
                    <h1 className="text-custom-blue dark:text-[#FFEBCB] text-2xl font-bold">
                      {profile.display_name}
                    </h1>
                  </div>
                  <div className="w-full flex">
                    <div className="w-2/3 flex items-center">
                      <ul className="flex">
                        {profile.badges.map((badge, index) => (
                          <li key={index}>
                            <div className="relative group">
                              <img
                                src={badge.badge_icon}
                                alt={badge.badge_name}
                                id={badge.badge_id}
                                className="h-[1.75rem] mr-1.5"
                              />
                              <div className="absolute w-[5rem] inset-x-0 top-8 hidden group-hover:block bg-custom-yellow bg-opacity-50 leading-tight text-custom-blue text-[16px] rounded-md text-center">
                                <p>{badge.badge_name}</p>
                              </div>
                            </div>
                          </li>
                        ))}
                      </ul>
                    </div>
                  </div>
                </div>
              </div>

              {/* 3 buttons */}
              <div className="mb-2 mx-8 justify-between flex">
                <div className="">
                  <button
                    key="buttonSubject"
                    id="buttonSubject"
                    onClick={handleSubjectsClick}
                  >
                    <div className="w-24 flex flex-col p-4 my-1 rounded-lg hover:scale-105 duration-300">
                      <img
                        className="w-20 mx-auto"
                        src="/images/features/small_icon_1.png"
                        alt="/"
                      />
                      <div className="h-5 pt-1">
                        <h2 className="text-sm font-bold text-center dark:text-custom-white text-black">
                          Subjects
                        </h2>
                      </div>
                    </div>
                  </button>
                </div>
                <div className="">
                  <button
                    key="buttonStatistics"
                    id="buttonStatistics"
                    onClick={handleBadgesClick}
                  >
                    <div className="w-24 flex flex-col p-4 my-1 rounded-lg hover:scale-105 duration-300">
                      <img
                        className="w-20 mx-auto"
                        src="/images/features/small_icon_2.png"
                        alt="/"
                      />
                      <div className="h-5 pt-1">
                        <h2 className="text-sm font-bold text-center dark:text-custom-white text-black">
                          Badges
                        </h2>
                      </div>
                    </div>
                  </button>
                </div>
                <div className="">
                  <button
                    key="buttonBadges"
                    id="buttonBadges"
                    onClick={handleProfileClick}
                  >
                    <div className="w-24 flex flex-col p-4 my-1 rounded-lg hover:scale-105 duration-300">
                      <img
                        className="w-20 mx-auto"
                        src="/images/features/small_icon_3.png"
                        alt="/"
                      />
                      <div className="h-5 pt-1">
                        <h2 className="text-sm font-bold text-center dark:text-custom-white text-black">
                          Profile
                        </h2>
                      </div>
                    </div>
                  </button>
                </div>
              </div>

              {/* Darkmode and logout */}
              <div className="m-2 mt-4">
                <div className="w-full flex">
                  <button
                    className="flex hover:scale-105"
                    onClick={handleLogout}
                  >
                    <div className="dark:hidden">
                      <img
                        src="/images/features/leave.png"
                        className="ml-10 w-8 flex dark:text-[#FFEBCB]"
                      />
                    </div>
                    <div className="hidden dark:block">
                      <img
                        src="/images/features/leave_light.png"
                        className="ml-10 w-8 flex dark:text-[#FFFFFF]"
                      />
                    </div>

                    <div className="ml-4 h-8 flex text-center justify-center items-center font-bold dark:text-[#FFFFFF]">
                      <h1>Logout</h1>
                    </div>
                  </button>
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </>
  );
}

export default ProfileDropdownMenu;
