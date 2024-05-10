import React, { useState, useEffect } from "react";
import katex from "katex";
import "katex/dist/katex.min.css";
import CommentComponent from "./Comment";
import api from "../axiosConfig";
import { useError } from "../context/ErrorContext.tsx";

{
  /*  Need to be able to:
      -> Post normal comment
      -> Post latex comment
      -> View more comments button at the bottom
      */
}

// 4.1 List comments of a question: GET /api/data/comments/list?question_id=${question_id}&page_size=${page_size}&page_no=${page_no}

// 4.2 Post a comment: POST /api/data/comments/post

interface Comment {
  comment_id: string;
  commenter_id: string;
  commenter_display_name: string;
  commenter_avatar: string;
  is_endorsed: boolean;
  comment_type: number;
  comment_body: string;
  commented_at: number;
  likes: number;
  is_liked: boolean;
}

interface DiscussionSectionProps {
  question_id: string;
}

function DiscussionSection(props: DiscussionSectionProps) {
  const [input, setInput] = useState("");

  const { showError } = useError();

  const handleInputChange = (event: React.ChangeEvent<HTMLTextAreaElement>) => {
    setInput(event.target.value);
  };

  // This is simply a flag to cause the below use effect to render
  // To do a new render, simply do setPreviewLatex(!previewLatex)
  // and the useEffect will run.
  const [previewLatex, setPreviewLatex] = useState(false);
  useEffect(() => {
    // latex render
    try {
      katex.render(
        // whatever is in the input field
        input,
        document.getElementById("latex-preview")!
      );
    } catch (error) {
      console.log(error);
    }
  }, [previewLatex]);

  const [latex, setLatex] = useState(false);

  const handleCheckboxChange = () => {
    setLatex(!latex);
  };

  const [page, setPage] = useState(0); //this is the page we are currently viewing

  const showMoreItems = () => {
    const pageNumber = page + 1;
    setPage(pageNumber);
    getComments(false, pageNumber);
  };

  const [staff, setStaff] = useState(false);

  const [comments, setComments] = useState<Comment[]>([]);

  async function getComments(clear: boolean, pageNumber: number) {
    // data/comments/list?question_id=${question_id}&page_size=${page_size}&page_no=${page_no}

    //construct query string
    let queryString = "data/comments/list?question_id=" + props.question_id;

    queryString += "&page_size=10&page_no=" + pageNumber;

    // headers
    let customConfig = {
      headers: {
        "Content-Type": "application/json",
      },
    };

    // Make request
    const result = await api
      .get(queryString, customConfig)
      .then((res) => {
        console.log("get comments", res);
        if (clear) {
          setComments(res.data.comments);
        } else {
          setComments([...comments, ...res.data.comments]);
        }
      })
      .catch((error) => {
        showError(error);
      });
  }

  useEffect(() => {
    // staff checker
    if (localStorage.getItem("staff") == "true") {
      setStaff(true);
    } else {
      setStaff(false);
    }

    // and then get the comments for first page
    getComments(true, 0);
  }, []);

  async function postComment() {
    // headers
    let customConfig = {
      headers: {
        "Content-Type": "application/json",
      },
    };

    let body = {
      question_id: props.question_id,
      comment_type: latex ? 1 : 0,
      comment_body: input,
    };
    console.log(body);

    // Make request
    const result = await api
      .post("data/comments/post", body, customConfig)
      .then((res) => {
        console.log(res);
        getComments(true, 0);
      })
      .catch((error) => {
        if (error == "9004") {
          showError("Slow down! Please wait 1 minute between posts.");
        } else if (error == "9005") {
          showError("Please keep comments appropriate.");
        } else {
          showError(error);
        }
      });
  }

  const handleCommentPost = () => {
    // send a post request with the input
    console.log(input);
    postComment();
    // data/comments/post
  };

  return (
    <>
      <div className="m-5">
        <div className="w-full ">
          {/* if not logged in, don't show discussion post box */}
          {localStorage.getItem("user") != null ? (
            <div>
              {latex ? (
                <div className="flex">
                  <div className="w-1/2 mr-2">
                    <div className="w-full p-4 shadow-lg border border-solid border-custom-blue rounded-md dark:border-custom-yellow dark:bg-[#ffe8c0]">
                      <div className="h-28">
                        <textarea
                          className="w-full h-full outline-none dark:bg-[#ffe8c0] dark:placeholder:text-black"
                          placeholder="Type comment here..."
                          value={input}
                          onChange={handleInputChange}
                          rows={4} // Adjust the number of rows to set the initial height
                          style={{ resize: "none" }} // This disables the textarea resizing
                        />
                      </div>
                    </div>
                    {/* Content for the first column */}
                  </div>
                  <div className="w-1/2 ml-2">
                    <div className="w-full p-4 shadow-lg border border-solid border-custom-blue rounded-md dark:border-custom-yellow dark:bg-[#ffe8c0]">
                      <div className="h-28">
                        <div className="w-full block">
                          <h1 className="font-bold text-xl dark:bg-[#ffe8c0] pb-1">
                            Preview Latex:
                          </h1>
                        </div>
                        <div
                          id="latex-preview"
                          className="overflow-auto w-full h-20 dark:bg-[#ffe8c0]"
                        />
                      </div>
                    </div>
                  </div>
                </div>
              ) : (
                <div className="w-full shadow-lg p-4 border border-solid border-custom-blue rounded-md dark:border-custom-yellow dark:bg-[#ffe8c0]">
                  <div className="h-28">
                    <textarea
                      className="w-full h-full outline-none dark:text-black dark:bg-[#ffe8c0] dark:placeholder:text-black"
                      placeholder="Type comment here..."
                      value={input}
                      onChange={handleInputChange}
                      rows={4} // Adjust the number of rows to set the initial height
                      style={{ resize: "none", color: "black" }} // This disables the textarea resizing
                    />
                  </div>
                </div>
              )}
              <div className="w-full flex justify-between">
                {/* checkbox for latex or not */}
                {/* input comment box if  */}
                <div className="ml-4 text-center items-center flex ">
                  <input
                    type="checkbox"
                    className="mr-2 h-6 w-6 accent-custom-yellow"
                    onChange={handleCheckboxChange}
                    checked={latex}
                  />
                  <h1 className="dark:text-[#ffe8c0]">
                    <strong>Use Latex</strong>
                  </h1>
                </div>

                <div className="">
                  {latex ? (
                    <button
                      className="m-4 py-2 px-4 font-bold bg-[#FFD186] rounded-md shadow-md hover:scale-105"
                      onClick={() => {
                        setPreviewLatex(!previewLatex);
                      }}
                    >
                      Preview Latex
                    </button>
                  ) : null}

                  <button
                    className="m-4 py-2 px-4 font-bold bg-[#FFD186] rounded-md shadow-md hover:scale-105"
                    onClick={handleCommentPost}
                  >
                    Post
                  </button>
                </div>
              </div>
            </div>
          ) : null}
          {/* Bottom line */}
          <div className="w-full h-[0.15rem] bg-custom-yellow mx-auto my-4 rounded-lg" />

          {comments.length == 0 ? (
            <>
              <h1 className="text-4xl text-center text-custom-blue dark:text-custom-yellow my-20">
                <strong>No Comments. Be the first!</strong>
              </h1>
            </>
          ) : null}
          <ul>
            {comments.map((item, index) => (
              <li key={index} className="m-6">
                <CommentComponent
                  comment={item}
                  staff={staff}
                  getComments={getComments}
                />
              </li>
            ))}
          </ul>
          {comments.length != 0 ? (
            <button
              className="w-[30%] flex justify-center items-center mx-auto mt-10 text-2xl bg-[#FFC058] border-4 border-[#FFE8C1] rounded-2xl text-custom-black p-1 font-bold"
              onClick={showMoreItems}
            >
              View More
            </button>
          ) : null}
        </div>
      </div>
    </>
  );
}

export default DiscussionSection;
