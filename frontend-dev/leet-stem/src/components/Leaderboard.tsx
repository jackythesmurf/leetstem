import { useState, useEffect } from "react";

// interface LeaderboardEntry {
//   name: string;
//   image: string;
//   badge_1: string;
//   badge_2: string;
//   badge_3: string;
//   points: number;
//   position: number;
// }

interface LeaderboardProps {
  data: LeaderboardUser[];
  you: CurrentUser;
}

interface CurrentUser {
  current_user_display_name: string;
  current_user_image: string;
  current_user_points: number;
  current_user_rank: number;
}

interface LeaderboardUser {
  display_name: string;
  points: number;
  rank: number;
  user_image: string;
  badges: Badge[];
}

interface Badge {
  badge_icon: string;
  badge_id: string;
  badge_name: string;
}

function Leaderboard(props: LeaderboardProps) {
  const [visibleItems, setVisibleItems] = useState(5);

  const showMoreItems = () => {
    setVisibleItems(visibleItems + 5);
  };

  return (
    <>
      {/* Weekly rankings */}
      <div className="text-xl lg:text-2xl xl:text-3xl font-bold text-custom-blue text-center bg-[#FFC058] p-1 rounded-2xl border-4 border-[#FFE8C1]">
        <h1>Weekly Rankings</h1>
      </div>

      {/* List of top 6 */}
      <div className="flex items-center justify-center w-full mt-2">
        {props.data.length != 0 ? (
          <ul className="w-[93%]">
            {props.data.slice(0, visibleItems).map((item, index) => (
              <li
                key={index}
                className={
                  "w-full flex flex-col bg-[#293D69] p-2 cursor-pointer text-[#FFECCC] font-bold rounded-md mb-3 shadow-xl hover:scale-[101%]"
                }
              >
                <div className="flex ml-4">
                  <div className="w-[80%] flex">
                    <div className="w-[12%] h-full items-center text-center flex">
                      <h1 className="flex text-center text-lg md:text-1xl lg:text-2xl xl:text-3xl justify-center items-center mr-4 text-[#F5F5F5]">
                        {item.rank}
                      </h1>
                    </div>

                    <div className="flex items-center justify-center mr-4">
                      <img
                        src={item.user_image}
                        alt="user"
                        className="xl:h-[2.5rem] xl:w-[2.5rem] lg:h-[2rem] lg:w-[2rem] h-[1.5rem] w-[1.5rem]"
                      />
                    </div>

                    <div className="text-sm md:text-md lg:text-lg xl:text-xl">
                      <ul>
                        <h1>{item.display_name}</h1>
                        {/* add the 3 badges */}
                        <div className="flex">
                          {item.badges.map((badge, index) => (
                            <li key={index}>
                              <div className="relative group">
                                <img
                                  src={badge.badge_icon}
                                  alt={badge.badge_name}
                                  id={badge.badge_id}
                                  className="h-[0.75rem] m-0.5"
                                />
                                <div className="absolute w-[5rem] inset-x-0 top-3 hidden group-hover:block bg-custom-yellow bg-opacity-50 text-custom-blue text-[10px] leading-snug rounded-md text-center">
                                  <p>{badge.badge_name}</p>
                                </div>
                              </div>
                            </li>
                          ))}
                        </div>
                      </ul>
                    </div>
                  </div>

                  <div className="w-[18%] flex items-center justify-center text-[#FFC86D]">
                    <h1 className="text-xs lg:text-sm xl:text-md ">
                      {item.points} points
                    </h1>
                  </div>
                </div>
              </li>
            ))}
          </ul>
        ) : (
          <h1 className="text-lg lg:text-xl xl:text-2xl  text-custom-error-red mt-2 mb-4">
            <strong>No Leaderboard Data</strong>
          </h1>
        )}
      </div>

      {/* Your position */}
      {props.you.current_user_display_name != null ? (
        <li
          className={
            "w-full flex flex-col bg-[#ffcf82] p-2 cursor-pointer text-[#FFECCC] font-bold rounded-md mb-3 shadow-xl hover:scale-[101%]"
          }
        >
          <div className="flex ml-4">
            <div className="w-[80%] flex">
              <div className="w-[12%] h-full items-center text-center flex">
                <h1 className="flex text-center text-xl md:text-2xl lg:text-3xl xl:text-4xl justify-center items-center text-custom-blue">
                  {`${
                    props.you.current_user_rank > 1000 ||
                    props.you.current_user_rank == 0
                      ? "-"
                      : props.you.current_user_rank
                  } `}
                </h1>
              </div>

              <div className="flex items-center justify-center mr-4">
                <img
                  src={props.you.current_user_image}
                  alt="user"
                  className="xl:h-[2.5rem] xl:w-[2.5rem] lg:h-[2rem] lg:w-[2rem] h-[1.5rem] w-[1.5rem]"
                />
              </div>

              <div className="flex items-center justify-center text-custom-blue text-sm md:text-md lg:text-lg xl:text-xl">
                <h1 className="">
                  {props.you.current_user_display_name} (You)
                </h1>
                {/* add the 3 badges */}
              </div>
            </div>

            <div className="w-[18%] flex items-center justify-center text-custom-blue text-sm">
              <h1>
                {props.you.current_user_points}{" "}
                {`${props.you.current_user_points == 1 ? "point" : "points"} `}
              </h1>
            </div>
          </div>
        </li>
      ) : (
        <li
          className={
            "w-full flex flex-col bg-[#ffcf82] p-2 cursor-pointer text-[#FFECCC] font-bold rounded-md mb-3 shadow-xl hover:scale-[101%]"
          }
        >
          <div className="flex ml-4 mr-4">
            <h1 className="text-custom-blue text-lg text-center italic">
              Create an account to see where you place!
            </h1>
          </div>
        </li>
      )}

      {props.data.length != 0 ? (
        <button
          className="w-[70%] flex justify-center items-center mx-auto mt-10 text-2xl bg-[#FFC058] border-4 border-[#FFE8C1] rounded-2xl text-custom-black p-1 font-bold"
          onClick={showMoreItems}
        >
          View More
        </button>
      ) : (
        <></>
      )}
    </>
  );
}

export default Leaderboard;
