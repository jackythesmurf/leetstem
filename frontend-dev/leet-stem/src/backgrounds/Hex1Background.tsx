function Hex1Background() {
  return (
    <>
      <div className="z-0">
        <div className="dark:hidden">
          {/* Light mode */}
          <div className="absolute top-[33.3%]">
            <img src="/images/hex/Hex1.png" alt="hex1" />
          </div>
        </div>
        <div className="hidden dark:block">
          {/* Dark mode */}
          <div className="absolute top-[33.3%]">
            <img
              src="/images/hex/Hex1.png"
              alt="swirl"
              className="transform -scale-x-100"
            />
          </div>
        </div>
      </div>
    </>
  );
}

export default Hex1Background;
