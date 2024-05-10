import { useNavigate } from "react-router-dom";
import { useState, useEffect } from "react";
import api from "../axiosConfig";
import { useError } from "../context/ErrorContext.tsx";

interface HomeProps {
  setBackground: (params: string) => void;
}

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

function Home(props: HomeProps) {
  const { showError } = useError();

  const [subjects, setSubjects] = useState<Subject[]>([]);

  let navigate = useNavigate();

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

        if (res.data.is_none_selected) {
          setSubjects(res.data.subjects);
        } else {
          //filter out subjects not selected
          const unfiltered = res.data.subjects as Subject[];
          const filteredSubjects = unfiltered.filter(
            (subject) => subject.is_selected
          );
          setSubjects(filteredSubjects);
        }
      })
      .catch((error) => {
        showError(error);
      });
  }

  useEffect(() => {
    props.setBackground("swirls");
    getSubjects();
  }, []);

  const handleSubjectClick = (item: Subject) => {
    console.log(item);
    let path = "/subject";
    navigate(path, { state: { id: item.subject_id, name: item.subject_name } });
  };

  return (
    <>
      <div className="w-full py-[3rem] px-[3rem] mb-12">
        <div className="flex max-w-[1480px] mx-auto">
          <h1 className="dark:text-custom-yellow text-custom-blue font-bold text-5xl lg:text-6xl py-5">
            Courses
            <hr className="h-1 mx-auto mt-2 bg-[#FCA311] border-0 rounded dark:bg-[#FFE8C1] opacity-70" />
          </h1>
        </div>
        <div className="max-w-[1280px] mx-auto grid xl:grid-cols-5 lg:grid-cols-4 md:grid-cols-3 grid-cols-2 gap-12">
          {subjects.map((item, i) => {
            return (
              <button
                key={i}
                id={item.subject_id}
                onClick={() => {
                  handleSubjectClick(item);
                }}
              >
                <div className="w-full flex flex-col p-4 my-1 rounded-lg hover:scale-105 duration-300">
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
                </div>
              </button>
            );
          })}
        </div>
      </div>
    </>
  );
}

export default Home;
