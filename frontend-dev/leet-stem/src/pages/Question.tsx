import { useNavigate, useLocation } from "react-router-dom";
import { useState, useEffect } from "react";
import DiscussionSection from "../components/DiscussionSection";
import api from "../axiosConfig";
import { useError } from "../context/ErrorContext.tsx";

// 3.3 Details of a question: GET /api/data/questions/details?question_id=${question_id}
interface QuestionDetails {
  attempted: boolean;
  question_difficulty: number;
  question_id: string;
  question_title: string;
  question_type: number;
  body_image: string;
  options: Option[];
  num_blanks: number;
}

interface Option {
  option_id: string;
  option_body_image: string;
}

// 3.4 Submit an attempt: POST /api/data/questions/attempt

// 3.5 Fetching last attempt of a question: GET /api/data/questions/attempt?question_id=${question_id}

const alphabet = ["A", "B", "C", "D", "E", "F", "G"];

interface QuestionProps {
  setBackground: (params: string) => void;
}

function Question(props: QuestionProps) {
  let navigate = useNavigate();
  const { state } = useLocation();
  const { id, name, subject_id, subject_name } = state; // Read values passed on state

  const { showError } = useError();

  const [unlocked, setUnlocked] = useState(false);
  const [answered, setAnswered] = useState(false);
  const [correct, setCorrect] = useState(false);
  const [showDiscussion, setShowDiscussion] = useState(false);

  useEffect(() => {
    props.setBackground("none");
  }, []);

  const handleBackClick = () => {
    //  Need to return to subject page you came from
    let path = "/subject";
    navigate(path, {
      state: { id: subject_id, name: subject_name },
    });
  };

  const [questionDetails, setQuestionDetails] = useState<QuestionDetails>({
    attempted: false,
    question_difficulty: 0,
    question_id: "...",
    question_title: "...",
    question_type: 0,
    body_image: "",
    options: [],
    num_blanks: -1,
  });

  async function getQuestionDetails() {
    let customConfig = {
      headers: {
        "Content-Type": "application/json",
      },
    };
    // Make request
    const result = await api
      .get("data/questions/details?question_id=" + id, customConfig)
      .then((res) => {
        console.log("get question details", res);
        // construct the QuestionDetails object
        if (res.data.question_type === 0) {
          // multi
          setQuestionDetails({
            attempted: res.data.attempted,
            question_difficulty: res.data.question_difficulty,
            question_id: res.data.question_id,
            question_title: res.data.question_title,
            question_type: res.data,
            body_image: res.data.question_body.body_image,
            options: res.data.question_body.options,
            num_blanks: -1,
          });

          const temp_multi_array = Array(
            res.data.question_body.options.length
          ).fill(false);

          setIsMulti(true);
          setMultiResponseArray(temp_multi_array);

          // now check if has been attempted before
          // Must also set unlocked, answered, correct and showDiscussion states
          if (res.data.attempted) {
            // need to request
            getQuestionAttempt(true, temp_multi_array, []);
          }
        } else {
          //fill blanks
          setQuestionDetails({
            attempted: res.data.attempted,
            question_difficulty: res.data.question_difficulty,
            question_id: res.data.question_id,
            question_title: res.data.question_title,
            question_type: res.data,
            body_image: res.data.question_body.body_image,
            options: [],
            num_blanks: res.data.question_body.num_blanks,
          });

          const fill_blank_array = Array(
            res.data.question_body.num_blanks
          ).fill("");

          setIsMulti(false);
          setFillBlankResponseArray(fill_blank_array);

          // now check if has been attempted before
          // Must also set unlocked, answered, correct and showDiscussion states
          if (res.data.attempted) {
            // need to request attempt
            getQuestionAttempt(false, [], fill_blank_array);
          }
        }
      })
      .catch((error) => {
        showError(error);
      });
  }

  async function getQuestionAttempt(
    multi: boolean,
    multi_arr: Array<boolean>,
    blank_arr: Array<string>
  ) {
    let customConfig = {
      headers: {
        "Content-Type": "application/json",
      },
    };
    // Make request
    const result = await api
      .get("data/questions/attempt?question_id=" + id, customConfig)
      .then((res) => {
        console.log("get question attempt", res);
        // TODO: need to update the states of unlocked, answered, correct and showDiscussion
        // based on the following fields from the response:
        // attempt_body: string; //eg. "2" for multichoice, "{blank1}{blank2}{}{blank4}" for fill in the blanks
        // was_correct: boolean;

        // Also need to fill in the multiResponseArray or fillBlankResponseArray based on
        // the attempt
        setAnswered(true);
        setCorrect(res.data.was_correct);
        setUnlocked(true);
        setShowDiscussion(true);

        // now update the correct array
        if (multi) {
          const newArray = [...multi_arr];
          let index = parseInt(res.data.attempt_body, 10);
          newArray[index] = !newArray[index];
          console.log(newArray);
          setMultiResponseArray(newArray);
        } else {
          const arr = res.data.attempt_body
            .split(/[{}]+/)
            .filter((option: string) => option.trim() !== "");
          setFillBlankResponseArray(arr);
        }
      })
      .catch((error) => {
        showError(error);
      });
  }

  const [multiResponseArray, setMultiResponseArray] = useState<Array<boolean>>(
    []
  ); // an array of either multi choice true/false

  const [fillBlankResponseArray, setFillBlankResponseArray] = useState<
    Array<string>
  >([]); // an array of strings for fill-in-the-blank

  const [isMulti, setIsMulti] = useState(false);

  useEffect(() => {
    // TODO:
    // When page loads, need to load in question as well as the possible answer.
    // Depending on the question type, either make an array
    // of true/false where index is the multi-choice option,
    // or an array of strings where the string is the fill-in-the-blank choices.
    // If it has been answered before, setAnswered(true) and isCorrect(?), as well
    // as populating the responseState array with the answered state.

    getQuestionDetails();
  }, []);

  const handleMultiResponseChange = (index: number) => {
    const updatedArray = Array(multiResponseArray.length).fill(false);
    updatedArray[index] = !updatedArray[index]; // Toggle the value at the specified index
    setMultiResponseArray(updatedArray); // Update the state with the new array
    console.log(updatedArray);
  };

  const handleFillBlankResponseChange = (index: number, value: string) => {
    const updatedArray = [...fillBlankResponseArray];
    updatedArray[index] = value;
    setFillBlankResponseArray(updatedArray);
    console.log(updatedArray);
  };

  async function attemptQuestion() {
    let customConfig = {
      headers: {
        "Content-Type": "application/json",
      },
    };
    let attempt_body_string = "";
    // construct attempt body string
    if (isMulti) {
      const index = multiResponseArray.findIndex((value) => value === true);

      if (index !== -1) {
        attempt_body_string = index.toString();
      } else {
        showError("Please select an option");
        return;
      }
    } else {
      // fill in the blanks
      for (let i = 0; i < fillBlankResponseArray.length; i++) {
        if (fillBlankResponseArray[i] === "") {
          showError("Please fill in all blanks");
          return;
        }
      }
      attempt_body_string = fillBlankResponseArray
        .map((value) => `{${value}}`)
        .join("");
    }
    console.log(attempt_body_string);
    // Make request
    const result = await api
      .post(
        "data/questions/attempt",
        {
          question_id: id.toString(),
          attempt_body: attempt_body_string.trimEnd(),
        },
        customConfig
      )
      .then((res) => {
        console.log("answer question attempt", res);
        setAnswered(true);
        setCorrect(res.data.passed);
        setUnlocked(true);
        setShowDiscussion(true);
      })
      .catch((error) => {
        showError(error);
      });
  }

  const handleSubmit = () => {
    // send a request
    attemptQuestion();
  };

  return (
    <>
      <div className="flex justify-center m-5 h-[75vh]">
        {/* First column */}
        <div className="w-2/3 p-4 flex flex-col max-w-3xl h-full">
          <div className="w-[100%] justify-items-start mb-5">
            <button
              className="bg-[#ffe8c0] hover:bg-[#ffbf58] text-black 
            font-bold py-1 px-2 rounded hover:scale-105"
              onClick={handleBackClick}
            >
              Back
            </button>
          </div>
          <div className="mx-auto justify-center items-center flex">
            <img
              src={questionDetails.body_image}
              className="max-h-[63vh] max-w-full text-center"
            />
          </div>
        </div>

        {/* Second column */}
        <div className="w-1/3 flex flex-col items-center max-w-lg p-4 h-full">
          <h1 className="text-xl lg:text-2xl xl:text-3xl text-custom-blue font-bold mb-3 dark:text-custom-yellow">
            {name}
          </h1>
          {/* <h1>{state.id}</h1> */}
          <div className="h-[65vh] w-full bg-[#FFE8C1] rounded-lg relative">
            <div className="h-[15%]">
              <h1 className="font-bold text-lg lg:text-xl xl:text-2xl p-6">
                Your Answer:
              </h1>
            </div>
            {/* Absolute positioned white circle */}
            <div className="w-[2rem] lg:w-[2.5rem] xl:w-[3rem] absolute top-4 right-4">
              {answered ? (
                correct ? (
                  <img
                    src="/images/features/13_v3.png"
                    alt="tick"
                    className="w-[2rem] lg:w-[2.5rem] xl:w-[3rem] h-[2rem] lg:h-[2.5rem] xl:h-[3rem]"
                  />
                ) : (
                  <img
                    src="/images/features/7.png"
                    alt="cross"
                    className="w-[2rem] lg:w-[2.5rem] xl:w-[3rem] h-[2rem] lg:h-[2.5rem] xl:h-[3rem]"
                  />
                )
              ) : (
                <div className="m-1 h-[2.5rem] w-[2.5rem] rounded-full bg-white"></div>
              )}
            </div>

            <div className="h-[70%] pb-4">
              {/* The useEffect has set answered, correct and either multiResponseArray or fillBlankResponseArray.
              Populate this section based on the outcome, as well as the  */}
              {/* Regardless of if it is answered or not, populate question section
              with either the state in multiResponseArray or fillBlankResponseArray
              for if isMulti is true or not */}
              <div className="flex flex-col justify-center h-full items-center">
                {isMulti ? (
                  <ul>
                    {multiResponseArray.map((value, index) => (
                      <li key={index}>
                        <span>
                          <label className="flex my-2 xl:my-4 text-center justify-center items-center xl:text-xl text-lg font-bold">
                            {alphabet[index]}
                            <img
                              className="flex mx-4 max-w-[40%] 2xl:max-w-[50%] max-h-[4rem]"
                              src={
                                questionDetails.options[index].option_body_image
                              }
                            />
                            <input
                              className="mx-4 w-4 xl:w-6 h-4 xl:h-6 accent-custom-yellow"
                              type="checkbox"
                              checked={value}
                              onChange={() => handleMultiResponseChange(index)}
                            />
                          </label>
                        </span>
                      </li>
                    ))}
                  </ul>
                ) : (
                  <ul className="w-[70%] flex-col items-center justify-center flex">
                    {fillBlankResponseArray.map((value, index) => (
                      <li key={index}>
                        <div className="w-full">
                          <span className="my-2 xl:my-4">
                            <strong>{alphabet[index]}</strong>
                            <input
                              className="m-2 xl: m-4 rounded-md text-md lg:text-lg xl:text-xl w-[70%]"
                              type="text"
                              value={value}
                              onChange={(e) =>
                                handleFillBlankResponseChange(
                                  index,
                                  e.target.value
                                )
                              }
                            />
                          </span>
                        </div>
                      </li>
                    ))}
                  </ul>
                )}
              </div>
            </div>
            <div className="h-[7%] pl-8">
              <button
                className="text-base py-1 px-4 bg-white rounded-lg shadow-md hover:scale-105"
                onClick={handleSubmit}
              >
                Submit
              </button>
            </div>
            {/* Answer options here */}
          </div>
        </div>
      </div>

      {/* Discussion banner at the bottom */}
      <div className="flex m-2 h-[8vh] bg-[#FFC058] rounded-lg justify-between">
        <div className="flex justify-center items-center">
          <h1 className="text-2xl text-custom-blue font-bold p-2 ml-8">
            Discussion
          </h1>
        </div>
        <div className="flex text-center justify-center items-center mr-8">
          {unlocked ? (
            <h1 className="text-sm p-4">Scroll To View Discussion</h1>
          ) : (
            <h1 className="text-sm p-4">Unlock After First Attempt</h1>
          )}
          <div className="h-[2rem] w-[1.5rem]">
            {unlocked ? (
              // True, is unlocked
              <img src="/images/features/23.png" />
            ) : (
              // False, is locked
              <img src="/images/features/21.png" />
            )}
          </div>
        </div>
      </div>

      {/* Discussion section */}
      {showDiscussion ? <DiscussionSection question_id={id} /> : null}
    </>
  );
}

export default Question;
