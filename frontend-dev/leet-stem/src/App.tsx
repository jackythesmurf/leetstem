import { useState, useEffect } from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Navbar from "./components/Navbar";
import Home from "./pages/Home";
import Subject from "./pages/Subject";
import SwirlsBackground from "./backgrounds/SwirlsBackground";
import Hex1Background from "./backgrounds/Hex1Background";
import Hex2Background from "./backgrounds/Hex2Background";
import Login from "./pages/login/login";
import Register from "./pages/login/register";
import Verify from "./pages/login/verify";
import ScrollToTop from "./components/ScrollToTop";
import Question from "./pages/Question";
import Profile from "./pages/Profile";
import YourCourses from "./pages/profile/YourCourses";
import ProfileBackground from "./backgrounds/ProfileBackground";
import Help from "./pages/Help";
import HelpSwirlsBackground from "./backgrounds/HelpSwirlsaBackground";
import YourBadges from "./pages/profile/YourBadges";
import ChangePassword from "./pages/ChangePassword";
import ResetPassword from "./pages/ResetPassword";
import ResetPasswordEmail from "./pages/ResetPasswordEmail";
import { ErrorProvider } from "./context/ErrorContext.tsx";
import ErrorModal from "./components/ErrorModal.tsx";

function App() {
  const [background, setBackground] = useState("");

  const [theme, setTheme] = useState(() => {
    const initialTheme = localStorage.getItem("theme");
    return initialTheme ? initialTheme : "light";
  });

  function updateTheme() {
    setTheme((prevTheme) => {
      const newTheme = prevTheme === "light" ? "dark" : "light";
      localStorage.setItem("theme", newTheme);
      return newTheme;
    });
  }

  // Dark/Light mode theme updater:
  useEffect(() => {
    const savedTheme = localStorage.getItem("theme");
    if (savedTheme) {
      setTheme(savedTheme);
      if (savedTheme === "dark") {
        document.body.style.backgroundColor = "#14213D";
      } else {
        document.body.style.backgroundColor = "#FFFFFF";
      }
    }
  }, [theme]);

  return (
    <>
      <ErrorProvider>
        <ErrorModal /> {/* Display the ErrorModal globally */}
        <Router>
          <ScrollToTop />
          <div className={theme === "dark" ? "dark" : ""}>
            {/* Switch statement for the background options */}
            {(() => {
              switch (background) {
                case "swirls":
                  return <SwirlsBackground />;
                case "hex1":
                  return <Hex1Background />;
                case "hex2":
                  return <Hex2Background />;
                case "proBack":
                  return <ProfileBackground />;
                case "helpBack":
                  return <HelpSwirlsBackground />;
                default:
                  return null;
              }
            })()}

            <div className="z-1 relative">
              <Navbar updateDarkMode={updateTheme} />
              <Routes>
                <Route
                  path="/"
                  element={<Home setBackground={setBackground} />}
                />
                <Route
                  path="/subject"
                  element={<Subject setBackground={setBackground} />}
                />
                <Route
                  path="/login"
                  element={<Login setBackground={setBackground} />}
                />
                <Route
                  path="/register"
                  element={<Register setBackground={setBackground} />}
                />
                <Route
                  path="/signup/verify"
                  element={<Verify setBackground={setBackground} />}
                />
                <Route
                  path="/question"
                  element={<Question setBackground={setBackground} />}
                />
                <Route
                  path="/profile"
                  element={<Profile setBackground={setBackground} />}
                />
                <Route
                  path="/profile/courses"
                  element={<YourCourses setBackground={setBackground} />}
                />
                <Route
                  path="/help"
                  element={<Help setBackground={setBackground} />}
                />
                <Route
                  path="/profile/badges"
                  element={<YourBadges setBackground={setBackground} />}
                />
                <Route
                  path="/changePassword"
                  element={<ChangePassword setBackground={setBackground} />}
                />
                <Route
                  path="/password/reset"
                  element={<ResetPassword setBackground={setBackground} />}
                />
                <Route
                  path="/resetPasswordRequest"
                  element={<ResetPasswordEmail setBackground={setBackground} />}
                />
              </Routes>
            </div>
          </div>
        </Router>
      </ErrorProvider>
    </>
  );
}

export default App;
