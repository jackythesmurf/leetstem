import { useState, useEffect } from "react";
import DeleteOutlineIcon from "@mui/icons-material/DeleteOutline";
import katex from "katex";
import "katex/dist/katex.min.css";
import api from "../axiosConfig";
import { useError } from "../context/ErrorContext.tsx";

{
  /*  Need to be able to:
      -> Upvote a comment
      -> Endorse a comment
      -> Delete a comment
      */
}

// 4.3 Vote a comment: POST /api/data/comments/vote

// 4.4 Endorse a comment: POST /api/data/comments/endorse

// 4.5 Delete a comment: POST /api/data/comments/delete

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

interface CommentProps {
  comment: Comment;
  staff: boolean;
  getComments: (clear: boolean, page: number) => void;
}

function Comment(props: CommentProps) {
  const { showError } = useError();

  useEffect(() => {
    if (props.comment.comment_type == 1) {
      // latex render
      try {
        katex.render(
          props.comment.comment_body,
          document.getElementById(props.comment.comment_id)!
        );
      } catch (error) {
        console.log(error);
      }
    }
  }, [props.comment]);

  async function likeComment() {
    // headers
    let customConfig = {
      headers: {
        "Content-Type": "application/json",
      },
    };

    let body = {
      comment_id: props.comment.comment_id,
      direction: 1,
    };
    if (props.comment.is_liked) {
      // remove it
      body = {
        comment_id: props.comment.comment_id,
        direction: 0,
      };
    }
    console.log(body);

    // Make request
    const result = await api
      .post("data/comments/vote", body, customConfig)
      .then((res) => {
        console.log(res);
        // reload comments
        props.getComments(true, 0);
      })
      .catch((error) => {
        showError(error);
      });
  }

  const handleLike = () => {
    console.log("like");
    if (localStorage.getItem("user")) {
      likeComment();
    } else {
      showError("You must be logged in to like a comment");
    }
  };

  async function deleteComment() {
    // headers
    let customConfig = {
      headers: {
        "Content-Type": "application/json",
      },
    };

    let body = {
      comment_id: props.comment.comment_id,
    };
    console.log(body);

    // Make request
    const result = await api
      .post("data/comments/delete", body, customConfig)
      .then((res) => {
        console.log(res);
        // reload comments
        props.getComments(true, 0);
      })
      .catch((error) => {
        showError(error);
      });
  }

  const handleDelete = () => {
    console.log("delete");
    deleteComment();
  };

  async function endorseComment() {
    // headers
    let customConfig = {
      headers: {
        "Content-Type": "application/json",
      },
    };

    let body = {
      comment_id: props.comment.comment_id,
      endorsed: !props.comment.is_endorsed,
    };
    console.log(body);

    // Make request
    const result = await api
      .post("data/comments/endorse", body, customConfig)
      .then((res) => {
        console.log(res);
        // reload comments
        props.getComments(true, 0);
      })
      .catch((error) => {
        showError(error);
      });
  }

  const handleEndorse = () => {
    console.log("endorse");
    endorseComment();
  };

  return (
    <>
      <div className="w-full relative mb-16">
        <div className="ml-4 mr-4">
          {/* The top right icons that are absolutely positioned */}
          <div className="absolute top-0 right-4">
            <div className="flex">
              {props.staff ? (
                <span className="flex justify-center items-center">
                  <button onClick={handleDelete}>
                    <DeleteOutlineIcon
                      fontSize="large"
                      className="mr-4 hover:scale-105 hover:cursor-pointer dark:text-[#ffe8c0]"
                    />
                  </button>
                  {props.comment.is_endorsed ? null : (
                    <button
                      className="bg-gray-300 text-black font-bold py-1 px-2 rounded hover:scale-105"
                      onClick={handleEndorse}
                    >
                      Endorse
                    </button>
                  )}
                </span>
              ) : null}
              <div>
                {props.comment.is_endorsed ? (
                  <img
                    src="./images/features/14.png"
                    className="h-14 ml-4 mr-4"
                  />
                ) : null}
              </div>
            </div>
          </div>

          {/* The comment name & content */}
          <h1 className="text-3xl font-bold text-custom-yellow">
            {props.comment.commenter_display_name}
          </h1>

          {/* if it is latex comment_type=1, render as latex */}
          <div className="mt-6">
            {props.comment.comment_type == 1 ? (
              <div
                id={props.comment.comment_id}
                className="dark:text-[#ffe8c0]"
              ></div>
            ) : (
              <h1 className="text-lg dark:text-[#ffe8c0]">
                {props.comment.comment_body}
              </h1>
            )}
          </div>

          {/* Like button */}
          {/* No like count currently in endpoint so uses mock value */}
          <div className="flex mt-6">
            <h1 className="text-xl text-center dark:text-[#ffe8c0]">
              {props.comment.likes}
            </h1>
            <div className="dark:hidden">
              {props.comment.is_liked ? (
                <img
                  src="./images/features/18_selected.png"
                  className="h-6 ml-2 hover:scale-105 cursor-pointer"
                  onClick={handleLike}
                />
              ) : (
                <img
                  src="./images/features/18.png"
                  className="h-6 ml-2 hover:scale-105 cursor-pointer"
                  onClick={handleLike}
                />
              )}
              {/* Light mode */}
            </div>

            <div className="hidden dark:block">
              {/* Dark mode */}
              {props.comment.is_liked ? (
                <img
                  src="./images/features/16_selected.png"
                  className="h-6 ml-2 hover:scale-105 cursor-pointer"
                  onClick={handleLike}
                />
              ) : (
                <img
                  src="./images/features/16.png"
                  className="h-6 ml-2 hover:scale-105 cursor-pointer"
                  onClick={handleLike}
                />
              )}
            </div>
          </div>
        </div>

        {/* Bottom line */}
        <div className="w-full h-[0.15rem] bg-[#14213D] mx-auto my-4 rounded-lg dark:bg-[#ffe8c0]" />
      </div>
    </>
  );
}

export default Comment;
