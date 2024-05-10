declare function zxcvbn(password: string, userInputs?: string[]): {
    score;
    feedback;
    suggestions;
};

interface Window {
    zxcvbn: typeof zxcvbn;
}