import { Link } from "react-router-dom";

interface Props {
  link: string;
  text: string;
}

function YellowButton(props: Props) {
  return (
    <>
      <Link to={props.link}>
        <button className="bg-[#ffe8c0] hover:bg-[#ffbf58] text-custom-blue font-bold py-1 px-2 rounded hover:scale-105">
          {props.text}
        </button>
      </Link>
    </>
  );
}

export default YellowButton;
