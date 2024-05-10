declare function isEmail(str: string, options?: any): boolean;

interface Window {
    validator: {
        isEmail: typeof isEmail;
    };
}