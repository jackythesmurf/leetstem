import { Link } from "react-router-dom";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../axiosConfig";
import { useError } from "../context/ErrorContext.tsx";

const AVATARS = [
  "./images/avatars/1.png",
  "./images/avatars/2.png",
  "./images/avatars/3.png",
  "./images/avatars/4.png",
  "./images/avatars/5.png",
  "./images/avatars/6.png",
  "./images/avatars/7.png",
  "./images/avatars/8.png",
  "./images/avatars/9.png",
  "./images/avatars/10.png",
];

interface ProfileProps {
  setBackground: (params: string) => void;
}

{
  /*  
  GET `/api/user/details`
{
    "error_code": 0,
    "display_name": "'; DROP TABLE Users --",
    "avatar": "/images/users/30499/avatar.png",
    "email": "myname@mydomain.com"
}
  */
}
interface Profile {
  display_name: string;
  avatar: string;
  email: string;
  error_code: number;
}

{
  // 5.2 Update non-critical details of the signed-in user: POST /api/user/details/update/nc
  /* Can edit name and avatar
  POST `/api/user/details/update/nc`
{
    "display_name": "My new name"
    "avatar_id":"/images/users/30499/avatar.png"
}
  */
}

function Profile(props: ProfileProps) {
  let navigate = useNavigate();
  const { showError } = useError();

  const [profile, setProfile] = useState<Profile>({
    display_name: "...",
    avatar: "",
    email: "...",
    error_code: 0,
  });

  const [editName, setEditName] = useState(false);
  const [name, setName] = useState("");

  const handleNameChange = (value: string) => {
    setName(value);
  };

  async function getProfile() {
    // If is loggedin,
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
        setName(res.data.display_name);
      })
      .catch((error) => {
        showError(error);
      });
  }

  useEffect(() => {
    props.setBackground("proBack");
    getProfile();
  }, []);

  async function changeName() {
    let customConfig = {
      headers: {
        "Content-Type": "application/json",
      },
    };
    // Make request
    const result = await api
      .post("user/details/update/nc", { display_name: name }, customConfig)
      .then((res) => {
        // Success
        setEditName(false);
        setProfile({ ...profile, display_name: name });
      })
      .catch((error) => {
        if (error == 9004) {
          showError("Slow down! Please wait 1 minute between requests.");
        } else {
          showError(error);
        }
      });
  }

  const handleNameSubmit = () => {
    // make post request with new name from name
    changeName();
  };

  async function changeImg(img: string) {
    let customConfig = {
      headers: {
        "Content-Type": "application/json",
      },
    };
    // Make request
    const result = await api
      .post("user/details/update/nc", { avatar_id: img }, customConfig)
      .then((res) => {
        // Success
        console.log("success", res);
        setProfile({ ...profile, avatar: img });
      })
      .catch((error) => {
        if (error == 9004) {
          showError("Slow down! Please wait 1 minute between requests.");
        } else {
          showError(error);
        }
      });
  }

  const handleImageChange = (new_img: string) => {
    // make post request to set new img
    changeImg(new_img);
  };

  return (
    <div className="flex justify-center mx-auto h-44 w-screen pt-4">
      <div className="w-[82%]">
        <Link to="/">
          <button
            className="bg-[#ffe8c0] dark:bg-[#767d92] hover:bg-[#ffe8c0] 
          text-black font-bold py-1 px-2 rounded hover:scale-105 text-xs lg:text-xs shadow-lg"
          >
            Home
          </button>
        </Link>
      </div>
      {/* Main content */}
      <div
        className="container mx-auto h-[50rem] w-8/12 bg-[#FFE8C1] dark:bg-[#1b2b4a] 
      rounded-lg pt-10 shadow-md position: absolute"
      >
        <div className="flex">
          <div className="w-[25%] p-4 flex items-center justify-end">
            <img src={profile.avatar} className="w-24 rounded-full" />
          </div>
          <div className="w-[75%]/4 p-4 text-center items-center flex">
            <h1 className="text-custom-blue dark:text-custom-yellow text-center font-bold lg:text-6xl md:text-4xl text-2xl">
              {profile.display_name}
            </h1>
          </div>
        </div>
        <div
          className="mx-auto h-[35.5rem] w-10/12 bg-custom-white dark:bg-[#c8cddc] 
        dark:bg-opacity-20 rounded-lg shadow-md"
        >
          <div className="p-8">
            <div className="w-full">
              <h1 className=" text-custom-blue dark:text-[#ffe8c0] font-bold flex lg:text-3xl md:text-2xl text-xl">
                Basic Info
              </h1>
            </div>
            {/* display name */}
            <div className="mt-8 mr-8 ml-8">
              <div className="w-full">
                <h1 className="w-full font-bold dark:text-[#ffe8c0]">
                  Display Name
                </h1>
              </div>
              <div className="w-full justify-between flex dark:text-[#ffe8c0]">
                {editName ? (
                  <input
                    className="rounded-md pl-1 pr-1 w-[60%] dark:text-black"
                    autoFocus
                    type="text"
                    value={name}
                    onChange={(e) => handleNameChange(e.target.value)}
                  />
                ) : (
                  <h1>{profile.display_name}</h1>
                )}

                <div>
                  {editName ? (
                    <div>
                      <button
                        className="text-blue-500 hover:scale-105 font-bold mr-8"
                        onClick={() => {
                          setEditName(false);
                        }}
                      >
                        Cancel
                      </button>
                      <button
                        className="text-blue-500 hover:scale-105 font-bold"
                        onClick={() => {
                          handleNameSubmit();
                        }}
                      >
                        Submit
                      </button>
                    </div>
                  ) : (
                    <button
                      className="text-blue-500 hover:scale-105 font-bold"
                      onClick={() => {
                        setEditName(true);
                      }}
                    >
                      Edit
                    </button>
                  )}
                </div>
              </div>
            </div>
            <div className="w-[100%] h-[0.1rem] bg-[#14213D] mx-auto my-4 rounded-lg dark:bg-[#ffe8c0]" />

            {/* email */}
            <div className="mt-8 mr-8 ml-8">
              <div className="w-full">
                <h1 className="w-full font-bold dark:text-[#ffe8c0]">Email</h1>
              </div>
              <div className="w-full justify-between flex dark:text-[#ffe8c0]">
                <h1>{profile.email}</h1>
              </div>
            </div>
            <div className="w-[100%] h-[0.1rem] bg-[#14213D] mx-auto my-4 rounded-lg dark:bg-[#ffe8c0]" />

            {/* Profile picture */}
            <div className="mt-8 mr-8 ml-8 h-36">
              <div className="w-full">
                <h1 className="w-full font-bold dark:text-[#ffe8c0]">
                  Profile Picture
                </h1>
              </div>
              <div className="h-[80%] w-[full] overflow-auto border border-custom-yellow dark:border-[#FFE8C1] rounded-lg">
                <div className="mx-auto grid lg:grid-cols-4 md:grid-cols-3 grid-cols-2 gap-x-16 w-[80%] gap-y-4 py-4">
                  {AVATARS.map((item, i) => {
                    return (
                      <div
                        key={i}
                        className="w-full flex flex-col rounded-lg hover:scale-105 duration-300 relative hover:cursor-pointer"
                      >
                        {item == profile.avatar ? (
                          <img
                            className="w-10 mx-auto border-custom-yellow p-0.5 border-4 rounded-full scale-[120%]"
                            src={item}
                            alt="/"
                          />
                        ) : (
                          <img
                            className="w-10 mx-auto"
                            src={item}
                            alt="/"
                            onClick={() => handleImageChange(item)}
                          />
                        )}
                      </div>
                    );
                  })}
                </div>
              </div>
              {/* Widget selector of profile picture (with image selected highlighted) */}
            </div>
            <div className="w-[100%] h-[0.1rem] bg-[#14213D] mx-auto my-4 rounded-lg dark:bg-[#ffe8c0]" />

            {/* Edit subjects, badges, change password */}
            <div className="mt-8 mr-8 ml-8">
              <div className="flex w-full justify-between">
                <button
                  className="w-[25%] bg-[#FFC058] border-4 border-[#FFE8C1] 
                  text-xs lg:text-md md:text-sm
                rounded-2xl text-custom-black p-1 font-bold hover:scale-[102%]"
                  onClick={() => {
                    navigate("/profile/courses");
                  }}
                >
                  Edit Subjects
                </button>
                <button
                  className="w-[25%] bg-[#FFC058] border-4 border-[#FFE8C1] 
                                    text-xs lg:text-md md:text-sm

                rounded-2xl text-custom-black p-1 font-bold hover:scale-[102%]"
                  onClick={() => {
                    navigate("/profile/badges");
                  }}
                >
                  Edit Badges
                </button>
                <button
                  className="w-[25%] bg-[#FFC058] border-4 border-[#FFE8C1] 
                                    text-xs lg:text-md md:text-sm
                rounded-2xl text-custom-black p-1 font-bold hover:scale-[102%]"
                  onClick={() => {
                    navigate("/changePassword");
                  }}
                >
                  Change Password
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Profile;
