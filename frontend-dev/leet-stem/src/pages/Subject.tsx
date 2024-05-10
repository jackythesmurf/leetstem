import { useLocation } from "react-router-dom";
import YellowButton from "../components/YellowButton";
import { useState, useEffect } from "react";
import DropdownMenu from "../components/DropdownMenu";
import QuestionList from "../components/QuestionList";
import Leaderboard from "../components/Leaderboard";
import api from "../axiosConfig";
import { useError } from "../context/ErrorContext.tsx";

interface Topic {
  topic_id: number;
  topic_name: string;
}

interface MenuItem {
  id: number;
  name: string;
}

interface Question {
  question_id: string;
  question_title: string;
  question_type: number;
  question_difficulty: number;
  question_topic: string;
  passed: boolean;
}

interface Filters {
  question_difficulty: number;
  question_topic: number;
  question_status: boolean | null;
  page: number;
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

interface SubjectProps {
  setBackground: (params: string) => void;
}

const DIFFICULTIES = [
  { id: 0, name: "Easy" },
  { id: 1, name: "Medium" },
  { id: 2, name: "Hard" },
];

const STATUSES = [
  { id: 0, name: "Complete" },
  { id: 1, name: "Incomplete" },
];

function Subject(props: SubjectProps) {
  const { state } = useLocation();
  const { id, name } = state; // Read values passed on state

  const { showError } = useError();

  const [difficulty, setDifficulty] = useState(-1); //0: Easy, 1: Medium, 2: Hard, or don't include if -1
  const [topic, setTopic] = useState(-1); //is a topic id, is optional
  const [status, setStatus] = useState<boolean | null>(null); //optional (null), including nothing for all questions, true for correct, false for not attempted

  const handleDifficultyChange = (item: MenuItem, index: number) => {
    console.log("Difficult change:" + item, index);
    let newDifficulty = -1;
    if (difficulty == index) {
      setDifficulty(-1);
      newDifficulty = -1;
    } else {
      setDifficulty(index);
      newDifficulty = index;
    }
    // reset page, and re-request quesitons with clear set to true
    // due to useState not updating, need to pass arguments
    const filter = {
      question_difficulty: newDifficulty,
      question_topic: topic,
      question_status: status,
      page: page,
    };
    setPage(0);
    getQuestions(true, filter);
  };

  const handleTopicChange = (item: MenuItem, index: number) => {
    console.log("Topic change:" + item, index);
    let newTopic = -1;
    if (topic == index) {
      // de-selected topic
      setTopic(-1);
      newTopic = -1;
    } else {
      // new topic selected
      setTopic(index);
      newTopic = index;
    }
    // reset page, and re-request quesitons with clear set to true
    // due to useState not updating, need to pass arguments
    const filter = {
      question_difficulty: difficulty,
      question_topic: newTopic,
      question_status: status,
      page: page,
    };
    setPage(0);
    getQuestions(true, filter);
  };

  const handleStatusChange = (item: MenuItem, index: number) => {
    console.log("Status change:" + item, index);
    let newStatus = null;
    if (status == null) {
      if (index == 0) {
        setStatus(true);
        newStatus = true;
      } else {
        setStatus(false);
        newStatus = false;
      }
    } else {
      if (index == 0) {
        // selected true
        if (status) {
          // de-selected true
          setStatus(null);
          newStatus = null;
        } else {
          // is false, switch to true
          setStatus(true);
          newStatus = true;
        }
      } else {
        // selected false
        if (!status) {
          // de-selected false
          setStatus(null);
          newStatus = null;
        } else {
          // is true, so switch to false
          setStatus(false);
          newStatus = false;
        }
      }
    }
    // due to useState not updating, need to pass arguments
    const filter = {
      question_difficulty: difficulty,
      question_topic: topic,
      question_status: newStatus,
      page: page,
    };

    // reset page, and re-request quesitons with clear set to true
    setPage(0);
    getQuestions(true, filter);
  };

  const [topics, setTopics] = useState<MenuItem[]>([]);

  async function getTopics() {
    let customConfig = {
      headers: {
        "Content-Type": "application/json",
      },
    };
    // Make request
    const result = await api
      .get("data/topics?subject_id=" + id, customConfig)
      .then((res) => {
        // convert {topic_id, topic_name} to MenuItem
        console.log(res.data.topics);
        const mappedTopics = res.data.topics.map((t: Topic) => ({
          id: t.topic_id,
          name: t.topic_name,
        }));
        console.log(mappedTopics);
        setTopics(mappedTopics);
      })
      .catch((error) => {
        showError(error);
      });
  }

  const [questions, setQuestions] = useState<Question[]>([]);
  const [page, setPage] = useState(0); //this is the page we are currently viewing

  const viewNextPage = () => {
    let newPageCount = page + 1;
    setPage(newPageCount);
    const filter = {
      question_difficulty: difficulty,
      question_topic: topic,
      question_status: status,
      page: newPageCount,
    };
    getQuestions(false, filter); //want to append to the questions list, so clear is false
  };

  async function getQuestions(clear: boolean, filter: Filters) {
    // if clear is true, then clear the questions list before requesting

    // Build query string
    // /api/data/questions/list?subject_id=${subject_id}&topic_id=${topic_id}&difficulty=${difficulty}
    // &passed=${passed}&page_size=${page_size}&page_no=${page_no}
    let queryString = "data/questions/list?subject_id=" + id;
    if (filter.question_topic != -1) {
      queryString += "&topic_id=" + filter.question_topic;
    }
    if (filter.question_difficulty != -1) {
      queryString += "&difficulty=" + filter.question_difficulty;
    }
    if (filter.question_status != null) {
      if (filter.question_status) {
        queryString += "&passed=true";
      } else {
        queryString += "&passed=false";
      }
    }
    queryString += "&page_size=10&page_no=" + filter.page;
    let customConfig = {
      headers: {
        "Content-Type": "application/json",
      },
    };
    console.log(queryString);
    // Make request
    const result = await api
      .get(queryString, customConfig)
      .then((res) => {
        console.log("getquestions", res);
        if (clear) {
          setQuestions(res.data.questions);
        } else {
          setQuestions([...questions, ...res.data.questions]);
        }
      })
      .catch((error) => {
        showError(error);
      });
  }

  const [leaderboard, setLeaderboard] = useState<LeaderboardUser[]>([]);
  const [currentUser, setCurrentUser] = useState<CurrentUser>({
    current_user_display_name: "name",
    current_user_image: "/images/avatars/1.png",
    current_user_points: 0,
    current_user_rank: 0,
  });

  async function getLeaderboard() {
    let customConfig = {
      headers: {
        "Content-Type": "application/json",
      },
    };
    // Make request
    const result = await api
      .get("stats/leaderboard?subject_id=" + id, customConfig)
      .then((res) => {
        console.log("leaderboard", res);

        // split into current user data and leaderboard data
        // leaderboard data
        setLeaderboard(res.data.users);

        // current user data
        setCurrentUser({
          current_user_display_name: res.data.current_user_display_name,
          current_user_image: res.data.current_user_image,
          current_user_points: res.data.current_user_points,
          current_user_rank: res.data.current_user_rank,
        });
      })
      .catch((error) => {
        showError(error);
      });
  }

  useEffect(() => {
    props.setBackground("none");

    //  first request the topics list for this subject
    getTopics();

    // then request the questions list for this subject (using the topic id, passed, difficulty if set), and giving
    // the results to the QuestionList component
    const filter = {
      question_difficulty: difficulty,
      question_topic: topic,
      question_status: status,
      page: page,
    };
    getQuestions(false, filter);

    // Then request leaderboard
    getLeaderboard();
  }, []);

  return (
    <div className="flex justify-center m-5">
      {/* First column */}
      <div className="w-2/3 p-4 flex flex-col max-w-3xl">
        <div className="w-[100%] justify-items-start mb-5">
          <YellowButton link="/" text="Home" />
        </div>

        <div className="flex w-[100%] h-[3rem] mb-5 justify-between">
          {/* Difficulty dropdown menu */}
          <div className="w-[22%] h-[3rem]">
            <DropdownMenu
              name="Difficulty"
              menuItems={DIFFICULTIES}
              handleChange={handleDifficultyChange}
            />
          </div>

          {/* Topic dropdown menu */}
          <div className="w-[22%] h-[3rem]">
            <DropdownMenu
              name="Topic"
              menuItems={topics}
              handleChange={handleTopicChange}
            />
          </div>

          {/* Status dropdown menu */}
          <div className="w-[22%] h-[3rem]">
            <DropdownMenu
              name="Status"
              menuItems={STATUSES}
              handleChange={handleStatusChange}
            />
          </div>
        </div>

        {/* Questions list */}
        <QuestionList
          // handleClick={handleQuestionClick}
          questions={questions}
          subject_id={id}
          subject_name={name}
          difficulties={DIFFICULTIES}
          topics={topics}
        />

        <button
          className="w-[30%] flex justify-center items-center mx-auto mt-10 text-2xl bg-[#FFC058] border-4 border-[#FFE8C1] rounded-2xl text-custom-black p-1 font-bold"
          onClick={viewNextPage}
        >
          View More
        </button>
      </div>

      {/* Second column */}
      <div className="w-1/3 flex flex-col items-center max-w-lg p-4 ">
        <div className="text-4xl lg:text-5xl xl:text-6xl font-bold mb-16 text-custom-blue dark:text-custom-yellow text-center">
          <h1 className="pl-4 pr-4">{name}</h1>
          <hr className="w-full h-1 mx-auto mt-2 bg-[#FCA311] border-0 rounded dark:bg-[#FFE8C1] opacity-70" />
        </div>

        <div className="w-[100%] p-4">
          <Leaderboard data={leaderboard} you={currentUser} />
        </div>
      </div>
    </div>
  );
}

export default Subject;
