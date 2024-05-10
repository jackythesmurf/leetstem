function SwirlsBackground() {
  return (
    <>
      {/* The large, medium and small sizes */}
      {/* w-24 md:w-30 lg:w-36 xl:42 */}
      {/* w-16 md:w-22 lg:w-24 xl:34 */}
      {/* w-12 md:w-14 lg:w-16 xl:26 */}
      <div className="z-0">
        <div className="dark:hidden">
          {/* Light mode */}
          {/* left */}
          <div className="absolute top-[55%] -left-[2%] opacity-40 w-16 md:w-22 lg:w-24 xl:34 animate-reverse-spin-slow">
            <img src="/images/swirl/yellow-swirl.png" alt="swirl" />
          </div>
          <div className="absolute top-[35%] -left-[3%] opacity-40 w-12 md:w-14 lg:w-16 xl:26 animate-reverse-spin-slow">
            <img src="/images/swirl/blue-swirl.png" alt="swirl" />
          </div>
          <div className="absolute top-[5%] -left-[5%] opacity-40 w-24 md:w-30 lg:w-36 xl:42 animate-spin">
            <img src="/images/swirl/blue-swirl.png" alt="swirl" />
          </div>
          <div className="absolute top-[85%] -left-[3%] opacity-40 w-24 md:w-30 lg:w-36 xl:42 animate-spin-slow">
            <img src="/images/swirl/blue-swirl.png" alt="swirl" />
          </div>
          <div className="absolute top-[5%] left-[5%] opacity-40 w-12 md:w-14 lg:w-16 xl:26 animate-spin-slow">
            <img src="/images/swirl/yellow-swirl.png" alt="swirl" />
          </div>
          <div className="absolute top-[1%] left-[20%] opacity-40 w-24 md:w-30 lg:w-36 xl:42 animate-spin-slow">
            <img src="/images/swirl/yellow-swirl.png" alt="swirl" />
          </div>

          {/* middle */}
          <div className="absolute top-[10%] left-[58%] opacity-40 w-12 md:w-14 lg:w-16 xl:26 animate-spin-slow">
            <img src="/images/swirl/yellow-swirl.png" alt="swirl" />
          </div>
          <div className="absolute top-[10%] left-[42%] opacity-40 w-24 md:w-30 lg:w-36 xl:42 animate-spin-slow">
            <img src="/images/swirl/blue-swirl.png" alt="swirl" />
          </div>

          {/* right */}
          <div className="absolute top-[16%] right-[1%] opacity-40 w-12 md:w-14 lg:w-16 xl:26 animate-spin">
            <img src="/images/swirl/yellow-swirl.png" alt="swirl" />
          </div>
          <div className="absolute top-[6%] right-[15%] opacity-40 w-12 md:w-14 lg:w-16 xl:26 animate-spin">
            <img src="/images/swirl/blue-swirl.png" alt="swirl" />
          </div>
          <div className="absolute top-[15%] right-[6%] opacity-40 w-24 md:w-30 lg:w-36 xl:42 animate-reverse-spin">
            <img src="/images/swirl/blue-swirl.png" alt="swirl" />
          </div>
          <div className="absolute bottom-[5%] right-[10%] opacity-40 w-12 md:w-14 lg:w-16 xl:26 animate-spin-slow">
            <img src="/images/swirl/blue-swirl.png" alt="swirl" />
          </div>
          <div className="absolute bottom-[5%] right-[3%] opacity-40 w-24 md:w-30 lg:w-36 xl:42 animate-reverse-spin-slow">
            <img src="/images/swirl/blue-swirl.png" alt="swirl" />
          </div>
          <div className="absolute bottom-[40%] right-[4%] opacity-40 w-16 md:w-22 lg:w-24 xl:34 animate-spin">
            <img src="/images/swirl/yellow-swirl.png" alt="swirl" />
          </div>
        </div>

        <div className="hidden dark:block">
          {/* Dark mode */}
          {/* left */}
          <div className="absolute top-[55%] -left-[2%] opacity-40 w-16 md:w-22 lg:w-24 xl:34 animate-reverse-spin-slow">
            <img src="/images/swirl/yellow-swirl.png" alt="swirl" />
          </div>
          <div className="absolute top-[35%] -left-[3%] opacity-40 w-12 md:w-14 lg:w-16 xl:26 animate-reverse-spin-slow">
            <img src="/images/swirl/grey-swirl.png" alt="swirl" />
          </div>
          <div className="absolute top-[5%] -left-[5%] opacity-40 w-24 md:w-30 lg:w-36 xl:42 animate-spin">
            <img src="/images/swirl/grey-swirl.png" alt="swirl" />
          </div>
          <div className="absolute top-[85%] -left-[3%] opacity-40 w-24 md:w-30 lg:w-36 xl:42 animate-spin-slow">
            <img src="/images/swirl/grey-swirl.png" alt="swirl" />
          </div>
          <div className="absolute top-[5%] left-[5%] opacity-40 w-12 md:w-14 lg:w-16 xl:26 animate-spin-slow">
            <img src="/images/swirl/yellow-swirl.png" alt="swirl" />
          </div>
          <div className="absolute top-[1%] left-[20%] opacity-40 w-24 md:w-30 lg:w-36 xl:42 animate-spin-slow">
            <img src="/images/swirl/yellow-swirl.png" alt="swirl" />
          </div>

          {/* middle */}
          <div className="absolute top-[10%] left-[58%] opacity-40 w-12 md:w-14 lg:w-16 xl:26 animate-spin-slow">
            <img src="/images/swirl/yellow-swirl.png" alt="swirl" />
          </div>
          <div className="absolute top-[10%] left-[42%] opacity-40 w-24 md:w-30 lg:w-36 xl:42 animate-spin-slow">
            <img src="/images/swirl/grey-swirl.png" alt="swirl" />
          </div>

          {/* right */}
          <div className="absolute top-[16%] right-[1%] opacity-40 w-12 md:w-14 lg:w-16 xl:26 animate-spin">
            <img src="/images/swirl/yellow-swirl.png" alt="swirl" />
          </div>
          <div className="absolute top-[6%] right-[15%] opacity-40 w-12 md:w-14 lg:w-16 xl:26 animate-spin">
            <img src="/images/swirl/grey-swirl.png" alt="swirl" />
          </div>
          <div className="absolute top-[15%] right-[6%] opacity-40 w-24 md:w-30 lg:w-36 xl:42 animate-reverse-spin">
            <img src="/images/swirl/grey-swirl.png" alt="swirl" />
          </div>
          <div className="absolute bottom-[5%] right-[10%] opacity-40 w-12 md:w-14 lg:w-16 xl:26 animate-spin-slow">
            <img src="/images/swirl/grey-swirl.png" alt="swirl" />
          </div>
          <div className="absolute bottom-[5%] right-[3%] opacity-40 w-24 md:w-30 lg:w-36 xl:42 animate-reverse-spin-slow">
            <img src="/images/swirl/grey-swirl.png" alt="swirl" />
          </div>
          <div className="absolute bottom-[40%] right-[4%] opacity-40 w-16 md:w-22 lg:w-24 xl:34 animate-spin">
            <img src="/images/swirl/yellow-swirl.png" alt="swirl" />
          </div>
        </div>
      </div>
    </>
  );
}

export default SwirlsBackground;
