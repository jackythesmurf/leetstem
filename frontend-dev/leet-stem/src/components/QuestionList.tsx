import { useNavigate } from "react-router-dom";

interface Question {
  question_id: string;
  question_title: string;
  question_type: number;
  question_difficulty: number;
  question_topic: string;
  passed: boolean;
}

interface MenuItem {
  id: number;
  name: string;
}

interface QuestionListProps {
  questions: Question[];
  subject_id: string;
  subject_name: string;
  difficulties: MenuItem[];
  topics: MenuItem[];
}

function QuestionList(props: QuestionListProps) {
  let navigate = useNavigate();

  const handleQuestionClick = (item: string, id: string) => {
    // Navigate to question page with this id as a prop
    navigate("/question", {
      state: {
        id: id,
        name: item,
        subject_id: props.subject_id,
        subject_name: props.subject_name,
      },
    });
  };

  const getTopicName = (id: string) => {
    const topic = props.topics.find((topic) => topic.id === parseInt(id));
    return topic ? topic.name : "";
  };

  return (
    <>
      <ul className="w-full">
        {props.questions.map((item, index) => (
          <li
            key={index}
            className={`w-[full] flex flex-col ${
              index % 2 === 0
                ? "bg-[#e5e5e5] dark:bg-[#c9c9c9]"
                : "bg-[#ffe8c0]"
            } p-2 cursor-pointer text-[#14213d] font-bold rounded-md mb-3 shadow-xl hover:scale-[101%]`}
            onClick={() => {
              handleQuestionClick(item.question_title, item.question_id);
            }}
          >
            <div className="flex">
              <div className="flex w-[94%] items-center">
                {/* <span> */}

                <h1 className="mr-4">{item.question_title}</h1>
                {/* Easy */}
                {props.difficulties[item.question_difficulty].name == "Easy" ? (
                  <h1 className="mr-4 text-xs text-green-700 border border-green-700 px-1 rounded-md">
                    {props.difficulties[item.question_difficulty].name}
                  </h1>
                ) : null}
                {/* Medium */}
                {props.difficulties[item.question_difficulty].name ==
                "Medium" ? (
                  <h1 className="mr-4 text-xs text-orange-500 border border-orange-500 px-1 rounded-md">
                    {props.difficulties[item.question_difficulty].name}
                  </h1>
                ) : null}
                {/* Hard */}
                {props.difficulties[item.question_difficulty].name == "Hard" ? (
                  <h1 className="mr-4 text-xs text-custom-error-red border border-custom-error-red px-1 rounded-md">
                    {props.difficulties[item.question_difficulty].name}
                  </h1>
                ) : null}

                <h1 className="mr-4 text-xs text-custom-blue border border-custom-blue px-1 rounded-md">
                  {getTopicName(item.question_topic)}
                </h1>
                {/* </span> */}
              </div>
              {/* icon */}
              <div className="w-[6%] justify-end h-full">
                {item.passed ? (
                  <img
                    src="/images/features/13_v3.png"
                    alt="tick"
                    className="h-[1.5rem]"
                  />
                ) : (
                  <div className="m-1 h-[1.25rem] w-[1.25rem] rounded-full bg-white"></div>
                )}
              </div>
            </div>
          </li>
        ))}
      </ul>
    </>
  );
}

export default QuestionList;
