import { useError } from "../context/ErrorContext";

function ErrorModal() {
  const { error, clearError } = useError();

  if (!error) {
    return null;
  }

  return (
    <div className="fixed top-0 left-0 w-full h-full flex items-center justify-center text-center z-50">
      <div
        className="bg-gray-900 bg-opacity-50 absolute inset-0"
        onClick={clearError}
      ></div>
      <div className="bg-white w-80 p-4 rounded-lg shadow-lg z-50">
        <div className="text-[#BD2E2E] text-lg mb-4">
          <strong>{error}</strong>
        </div>
        <button
          className="bg-[#ffe8c0] hover:bg-[#ffbf58] text-custom-blue text-2xl font-bold py-1 px-2 rounded hover:scale-105"
          onClick={clearError}
        >
          Close
        </button>
      </div>
    </div>
  );
}

export default ErrorModal;
