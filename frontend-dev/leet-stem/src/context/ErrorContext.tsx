import React, { createContext, useContext, useState } from "react";

interface MyContextType {
  error: string;
  showError: (params: string) => void;
  clearError: () => void;
}
const ErrorContext = createContext<MyContextType | undefined>(undefined);

export function useError() {
  const context = useContext(ErrorContext);
  if (context === undefined) {
    throw new Error("useMyContext must be used within a MyContextProvider");
  }
  return context;
}

export function ErrorProvider({ children }: React.PropsWithChildren<{}>) {
  const [error, setError] = useState<string>("");

  const showError = (errorMessage: string) => {
    setError(errorMessage);
    if (errorMessage === 1005 || errorMessage === 9002) {
      localStorage.removeItem("user");
      localStorage.removeItem("staff");
      document.cookie = 'accessToken' + '=; expires=Thu, 01 Jan 1970 00:00:01 GMT; path=/api;';
      window.location.href = "/";
    }
  };

  const clearError = () => {
    setError("");
  };

  return (
    <ErrorContext.Provider value={{ error, showError, clearError }}>
      {children}
    </ErrorContext.Provider>
  );
}
