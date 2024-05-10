function ProfileBackground() {
    return (
      <>
        {/* The large, medium and small sizes */}
        {/* w-24 md:w-30 lg:w-36 xl:42 */}
        {/* w-16 md:w-22 lg:w-24 xl:34 */}
        {/* w-12 md:w-14 lg:w-16 xl:26 */}
        <div className="z-0">
          <div className="dark:hidden">
            {/* Light mode */}
            <div className="absolute flex justify-center mx-auto h-60 w-screen bg-[#FFC058] pt-4 shadow-lg"/>
            <div className="absolute w-100">
              <img src="/images/hex/profile-hex-right.png" alt="swirl" className="transform -scale-x-100" />
            </div>
            <div className="absolute right-[0%] w-100 ">
              <img src="/images/hex/profile-hex-right.png" alt="swirl" />
            </div>
          </div>
  
          <div className="hidden dark:block">
            {/* Dark mode */}
            <div className="absolute flex justify-center mx-auto h-60 w-screen bg-[#272e44] pt-4 shadow-lg"/>
            <div className="absolute w-100">
              <img src="/images/hex/profile-hex-right.png" alt="swirl" className="transform -scale-x-100" />
            </div>
            <div className="absolute right-[0%] w-100 ">
              <img src="/images/hex/profile-hex-right.png" alt="swirl" />
            </div>
          </div>
          </div>
      </>
    );
  }
  
  export default ProfileBackground;
  