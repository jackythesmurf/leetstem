import { useState } from "react";
import { IoIosArrowDown, IoIosArrowUp } from "react-icons/io";

interface MenuItem {
  id: number;
  name: string;
}

interface DropdownMenuProps {
  name: string;
  menuItems: Array<MenuItem>;
  handleChange: (item: MenuItem, index: number) => void;
}

function DropdownMenu(props: DropdownMenuProps) {
  const [isOpen, setIsOpen] = useState(false);

  const [isSelected, setIsSelected] = useState(-1);

  const handleDifficultyChange = (item: MenuItem, i: number) => {
    setIsOpen(false);
    if (isSelected == i) {
      setIsSelected(-1);
    } else {
      setIsSelected(i);
    }
    props.handleChange(item, i);
  };

  const handleMouseLeave = () => {
    setIsOpen(false);
  };

  return (
    <>
      <div onMouseLeave={handleMouseLeave}>
        <button
          className={`bg-[#ffbf58] p-2 flex items-center justify-between font-bold 
          rounded-lg tracking-wider 
          hover:scale-[102%]
          text-xs md:text-sm lg:text-base
          w-[6rem] lg:w-[8rem] xl:w-[10rem]
          shadow-lg
          ${isOpen ? "scale-[102%]" : ""}`}
          onClick={() => {
            setIsOpen(!isOpen);
          }}
        >
          {props.name}
          {isOpen ? (
            <IoIosArrowDown className="text-md md:text-lg lg:text-3xl" />
          ) : (
            <IoIosArrowUp className="text-md md:text-lg lg:text-3xl" />
          )}
        </button>

        {isOpen && (
          <div
            className="bg-[#ffd287] absolute flex flex-col items-start rounded-lg p-1
        text-xs md:text-sm lg:text-base
          w-[6rem] lg:w-[8rem] xl:w-[10rem]
          shadow-lg"
          >
            {props.menuItems.map((item, i) =>
              isSelected == i ? (
                <div
                  className="flex w-full font-bold justify-between bg-[#ffbf58] 
                cursor-pointer rounded-md rounded-t-md border-b-2 p-1 m-1 mx-auto 
                border-[#fca312]"
                  key={i}
                  onClick={() => {
                    handleDifficultyChange(item, i);
                  }}
                >
                  <h3>{item.name}</h3>
                </div>
              ) : (
                <div
                  className="flex w-full font-bold justify-between hover:bg-[#ffbf58] 
                cursor-pointer hover:rounded-md rounded-t-md border-b-2 p-1 m-1 mx-auto 
                border-[#fca312]"
                  key={i}
                  onClick={() => {
                    handleDifficultyChange(item, i);
                  }}
                >
                  <h3>{item.name}</h3>
                </div>
              )
            )}
          </div>
        )}
      </div>
    </>
  );
}

export default DropdownMenu;
