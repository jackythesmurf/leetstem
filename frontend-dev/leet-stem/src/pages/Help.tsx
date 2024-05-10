import { useNavigate } from "react-router-dom";
import { useEffect } from "react";
import YellowButton from "../components/YellowButton";
import { MdOutlineMailOutline } from "react-icons/md";

const FAQ = [
  {
    question: "What high school curriculum is LeetSTEM made for?",
    answer:
      "LeetSTEM has been designed around the New South Wales and ACT Higher School Certificate (HSC) curriculum.",
  },
  {
    question: "Can I use LeetSTEM without creating an account?",
    answer:
      "Yes! You are free to use all Q&A features of LeetSTEM without creating an account, however, your answers and progress will not be saved.",
  },
  {
    question:
      "What benefit does LeetSTEM provide over doing past HSC questions myself?",
    answer:
      "Many benefits! LeetSTEM allows you to keep track of the questions you have answered, as well as the ones you may have got wrong in the past. LeetSTEM also provides a collaborative discussion section for every questions where you can discuss answers in greater detail than the HSC provided answers. The LeetSTEM discussion section also provides LaTeX formatting, allowing you to communicate mathematics more effecively than other discussion forums.",
  },
  {
    question: "What does it mean if an answer has been 'Enorsed'?",
    answer:
      "An endorsed answer are responses that our professional staff team have identified as being of high quality. These endorsed answers should be your preferred answer resource.",
  },
];

interface HelpProps {
  setBackground: (params: string) => void;
}

function Help(props: HelpProps) {
  let navigate = useNavigate();

  useEffect(() => {
    props.setBackground("helpBack");
  }, []);

  return (
    <>
      <div className="w-full py-[3rem] px-[3rem]">
        <div className="flex max-w-[1480px] mx-auto justify-center mt-4">
          <div className="w-[90%]">
            <YellowButton link="/" text="Home" />
          </div>
        </div>
        <div className="w-full max-w-[1480px] mx-auto mt-0">
          <h1 className="text-center text-6xl text-custom-blue dark:text-[#ffe8c0] font-bold">
            Help Centre
          </h1>
          <div className="mt-8 ml-16 mr-16 mb-8">
            <div className="w-full ">
              <h1 className="text-xl text-center mb-5 italic font-bold text-custom-yellow">
                LeetSTEM is a platform for HSC students to study and collaborate
                with other students.
              </h1>
              <h1 className="text-5xl text-custom-yellow font-bold mb-5">
                FAQ
              </h1>
              {/* Map FAQ to Q&A pairs */}

              {FAQ.map((item, index) => (
                <li key={index} className={"w-full flex flex-col p-8"}>
                  <div>
                    <h1 className="text-custom-blue dark:text-[#ffe8c0] font-bold text-xl">
                      Q: {item.question}
                    </h1>
                  </div>
                  <div>
                    <h1 className="text-custom-blue dark:text-custom-white text-lg mt-2 ml-4">
                      {item.answer}
                    </h1>
                  </div>
                </li>
              ))}

              <h1 className="text-5xl text-custom-yellow font-bold mt-10">
                Contact Us
              </h1>

              <h1 className="flex text-custom-blue dark:text-[#ffe8c0] text-lg mt-2 ml-4 flex">
                <MdOutlineMailOutline className="pt-1 pr-2 items-center text-center justify-center text-2xl" />
                enquiries@leetstem.com
              </h1>
            </div>
          </div>
        </div>
      </div>
    </>
  );
}

export default Help;
