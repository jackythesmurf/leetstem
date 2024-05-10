import { useNavigate } from "react-router-dom";
import { useState, useEffect } from "react";
import YellowButton from "../../components/YellowButton";
import api from "../../axiosConfig";
import { useError } from "../../context/ErrorContext.tsx";

{
  /* get all badges for a user
  GET `/api/user/badges`
{
    "error_code": 0,
    "badges": [
        { "badge_id": "85", "displayed": true, "badge_name": "Staff", "badge_icon": "/images/badges/staff.png" },
        { "badge_id": "43", "displayed": false, "badge_name": "Questions Murderer", "badge_icon": "/images/badges/qm.png" },
        { "badge_id": "11", "displayed": true, "badge_name": "Active commenter", "badge_icon": "/images/badges/ac.png" }
    ]
}
  */
}

{
  /* Don't need a 'done' button on badges, as their true/false is updated 1 at a time
   */
}

const MOCK_BADGES = {
  error_code: 0,
  badges: [
    {
      badge_id: "85",
      displayed: false,
      badge_name: "Staff",
      badge_icon: "/images/badges/Comp1.png",
    },
    {
      badge_id: "43",
      displayed: false,
      badge_name: "Questions Murderer",
      badge_icon: "/images/badges/Comp2.png",
    },
    {
      badge_id: "11",
      displayed: false,
      badge_name: "Active commenter",
      badge_icon: "/images/badges/Comp3.png",
    },
    {
      badge_id: "85",
      displayed: false,
      badge_name: "Staff",
      badge_icon: "/images/badges/Comp4.png",
    },
    {
      badge_id: "43",
      displayed: false,
      badge_name: "Questions Murderer",
      badge_icon: "/images/badges/Exe1.png",
    },
    {
      badge_id: "11",
      displayed: false,
      badge_name: "Active commenter",
      badge_icon: "/images/badges/Exe2.png",
    },
    {
      badge_id: "85",
      displayed: false,
      badge_name: "Staff",
      badge_icon: "/images/badges/Exe3.png",
    },
    {
      badge_id: "43",
      displayed: false,
      badge_name: "Questions Murderer",
      badge_icon: "/images/badges/Exe4.png",
    },
    {
      badge_id: "11",
      displayed: false,
      badge_name: "Active commenter",
      badge_icon: "/images/badges/Comp1.png",
    },
    {
      badge_id: "85",
      displayed: false,
      badge_name: "Staff",
      badge_icon: "/images/badges/Comp2.png",
    },
    {
      badge_id: "43",
      displayed: false,
      badge_name: "Questions Murderer",
      badge_icon: "/images/badges/Comp3.png",
    },
    {
      badge_id: "11",
      displayed: false,
      badge_name: "Active commenter",
      badge_icon: "/images/badges/Comp4.png",
    },
    {
      badge_id: "85",
      displayed: false,
      badge_name: "Staff",
      badge_icon: "/images/badges/Exe1.png",
    },
    {
      badge_id: "43",
      displayed: false,
      badge_name: "Questions Murderer",
      badge_icon: "/images/badges/Exe2.png",
    },
    {
      badge_id: "11",
      displayed: false,
      badge_name: "Active commenter",
      badge_icon: "/images/badges/Exe3.png",
    },
    {
      badge_id: "85",
      displayed: false,
      badge_name: "Staff",
      badge_icon: "/images/badges/Exe4.png",
    },
    {
      badge_id: "43",
      displayed: false,
      badge_name: "Questions Murderer",
      badge_icon: "/images/badges/Comp1.png",
    },
    {
      badge_id: "11",
      displayed: false,
      badge_name: "Active commenter",
      badge_icon: "/images/badges/Comp2.png",
    },
    {
      badge_id: "85",
      displayed: false,
      badge_name: "Staff",
      badge_icon: "/images/badges/Comp3.png",
    },
    {
      badge_id: "43",
      displayed: false,
      badge_name: "Questions Murderer",
      badge_icon: "/images/badges/Comp4.png",
    },
    {
      badge_id: "11",
      displayed: false,
      badge_name: "Active commenter",
      badge_icon: "/images/badges/Exe1.png",
    },
  ],
};

interface Badge {
  badge_id: string;
  displayed: boolean;
  badge_name: string;
  badge_icon: string;
}

interface YourBadgesProps {
  setBackground: (params: string) => void;
}

function YourBadges(props: YourBadgesProps) {
  let navigate = useNavigate();

  const { showError } = useError();

  const [selected, setSelected] = useState(0);

  //   For the data, setup a usestate
  const [badges, setBadges] = useState<Badge[]>([]); // array of badge states

  const setupState = (badges: Badge[]) => {
    // count how many have display true
    let count = 0;
    for (let i = 0; i < badges.length; i++) {
      if (badges[i].displayed) {
        count += 1;
      }
    }
    setSelected(count);
  };

  async function getBadges() {
    let customConfig = {
      headers: {
        "Content-Type": "application/json",
      },
    };
    // Make request
    const result = await api
      .get("user/badges", customConfig)
      .then((res) => {
        // Success
        console.log("badges", res);
        setBadges(res.data.badges);

        setupState(res.data.badges);
      })
      .catch((error) => {
        showError(error);
      });
  }

  useEffect(() => {
    props.setBackground("swirls");

    // request the badges for user
    getBadges();
  }, []);

  const modifyItemAtIndex = (newState: boolean, index: number) => {
    const updatedItems = [...badges];
    updatedItems[index] = { ...updatedItems[index], displayed: newState };
    setBadges(updatedItems);
  };

  async function postBadge(badge: Badge, index: number) {
    let customConfig = {
      headers: {
        "Content-Type": "application/json",
      },
    };
    // Make request
    const result = await api
      .post(
        "user/badges",
        { badge_id: badge.badge_id, is_displayed: !badge.displayed },
        customConfig
      )
      .then((res) => {
        // Success
        // update local state
        if (badge.displayed) {
          //removed it
          modifyItemAtIndex(!badge.displayed, index);
          setSelected(selected - 1);
        } else {
          // added it
          modifyItemAtIndex(!badge.displayed, index);
          setSelected(selected + 1);
        }
      })
      .catch((error) => {
        showError(error);
      });
  }

  const handleBadgeClick = (badge: Badge, index: number) => {
    if (badge.displayed) {
      // then removing it, so is allowed
      postBadge(badge, index);
    } else {
      // attempting to add a badge, check count
      if (selected < 3) {
        // then accept it - request to add it
        postBadge(badge, index);
      } else {
        showError("You can only display 3 badges at a time!");
      }
    }
  };

  return (
    <>
      <div className="w-full py-[3rem] px-[3rem]">
        <YellowButton link="/profile" text="Back To Profile" />

        <div className="flex max-w-[1480px] mx-auto">
          <h1 className="dark:text-custom-yellow text-custom-blue font-bold text-5xl lg:text-6xl py-5">
            Your Badges
            <hr className="h-1 mx-auto mt-2 bg-[#FCA311] border-0 rounded dark:bg-[#FFE8C1] opacity-70" />
          </h1>
        </div>

        <h1 className="text-xl italic font-bold mb-4 dark:text-[#FFE8C1]">
          Select 3 badges to display...
        </h1>

        <div className="max-w-[1280px] mx-auto grid xl:grid-cols-5 lg:grid-cols-4 md:grid-cols-3 grid-cols-2 gap-8">
          {badges.map((item, i) => {
            return (
              <button
                key={i}
                id={item.badge_id}
                onClick={() => handleBadgeClick(item, i)}
              >
                <div className="w-full flex flex-col p-4 my-1 rounded-lg hover:scale-105 duration-300 relative">
                  <img className="w-20 mx-auto" src={item.badge_icon} alt="/" />
                  <div className="h-5">
                    <h2 className="text-xl font-bold text-center dark:text-custom-white text-custom-blue mt-2">
                      {item.badge_name}
                    </h2>
                  </div>
                  <div className="w-[3rem] absolute top-0 right-0">
                    {item.displayed ? (
                      <img
                        src="/images/features/13_v3.png"
                        alt="tick"
                        className="h-[3rem]"
                      />
                    ) : (
                      <div className="m-1 h-[2.5rem] w-[2.5rem] rounded-full bg-white border-2 border-[#CED4DA]"></div>
                    )}
                  </div>
                </div>
              </button>
            );
          })}
        </div>
      </div>
    </>
  );
}

export default YourBadges;
