import { useNavigate } from "react-router-dom";
import { useState, useEffect } from "react";
import YellowButton from "../../components/YellowButton";
import api from "../../axiosConfig";
import { useError } from "../../context/ErrorContext.tsx";

interface YourCoursesProps {
  setBackground: (params: string) => void;
}

// Get subjects
interface Subject {
  is_selected: boolean;
  subject_id: string;
  subject_name: string;
}

const IMAGES = [
  "/images/course/1.png",
  "/images/course/2.png",
  "/images/course/3.png",
  "/images/course/4.png",
  "/images/course/5.png",
  "/images/course/6.png",
  "/images/course/7.png",
  "/images/course/8.png",
  "/images/course/9.png",
  "/images/course/10.png",
];

function YourCourses(props: YourCoursesProps) {
  const { showError } = useError();

  let navigate = useNavigate();

  const [subjects, setSubjects] = useState<Subject[]>([]);

  async function getSubjects() {
    // If is loggedin,
    let customConfig = {
      headers: {
        "Content-Type": "application/json",
      },
    };
    // Make request
    const result = await api
      .get("data/subjects", customConfig)
      .then((res) => {
        // Success
        setSubjects(res.data.subjects);
      })
      .catch((error) => {
        showError(error);
        navigate("/");
      });
  }

  useEffect(() => {
    props.setBackground("swirls");
    getSubjects();
  }, []);

  async function requestSubjectChange(
    subject_id: string,
    is_selected: boolean,
    index: number
  ) {
    // If is loggedin,
    let customConfig = {
      headers: {
        "Content-Type": "application/json",
      },
    };

    const apiRequestBody = {
      subject_id: subject_id,
      is_selected: is_selected,
    };

    // Make request
    const result = await api
      .post("user/subjects", JSON.stringify(apiRequestBody), customConfig)
      .then((res) => {
        // Success
        console.log("success", res);

        // if success, update it in the local state (rather than rerequest subjects)
        modifyItemAtIndex(is_selected, index);
      })
      .catch((error) => {
        console.log("error", error);
        // showError(error);
        // navigate("/");
      });
  }

  const modifyItemAtIndex = (newState: boolean, index: number) => {
    const updatedItems = [...subjects];
    updatedItems[index] = { ...updatedItems[index], is_selected: newState };
    setSubjects(updatedItems);
  };

  const handleSubjectClick = (subject: Subject, index: number) => {
    // make a POST request to update
    requestSubjectChange(subject.subject_id, !subject.is_selected, index);
  };

  return (
    <>
      <div className="w-full py-[3rem] px-[3rem] mb-12">
        <YellowButton link="/profile" text="Back To Profile" />

        <div className="flex max-w-[1480px] mx-auto">
          <h1 className="dark:text-custom-yellow text-custom-blue font-bold text-5xl lg:text-6xl py-5">
            Your Courses
            <hr className="h-1 mx-auto mt-2 bg-[#FCA311] border-0 rounded dark:bg-[#FFE8C1] opacity-70" />
          </h1>
        </div>

        <div className="max-w-[1280px] mx-auto grid xl:grid-cols-5 lg:grid-cols-4 md:grid-cols-3 grid-cols-2 gap-12">
          {subjects.map((item, i) => {
            return (
              <button
                key={i}
                id={item.subject_id}
                onClick={() => handleSubjectClick(item, i)}
              >
                <div className="w-full flex flex-col p-4 my-1 rounded-lg hover:scale-105 duration-300 relative">
                  <img
                    className="w-30 mx-auto"
                    src={IMAGES[i % IMAGES.length]}
                    alt="/"
                  />
                  <div className="h-5">
                    <h2 className="lg:text-2xl md:text-xl text-lg font-bold text-center dark:text-custom-white text-custom-blue">
                      {item.subject_name}
                    </h2>
                  </div>
                  <div className="w-[3rem] absolute top-0 right-0">
                    {item.is_selected ? (
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

export default YourCourses;
