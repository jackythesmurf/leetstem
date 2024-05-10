/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        'custom-black': '#14213D',
        'custom-blue': '#14213D',
        'custom-yellow': '#FCA311',
        'custom-grey': '#E5E5E5',
        'custom-white': '#FFFFFF',
        'custom-error-red': '#bd2e2e',
      },
      animation: {
        'reverse-spin': 'reverse-spin 60s linear infinite',
        'reverse-spin-slow': 'reverse-spin 120s linear infinite',
        'spin': 'spin 60s linear infinite',
        'spin-slow': 'spin 120s linear infinite',
        'spin-button': 'spin 1s linear infinite'
      },
      keyframes: {
        'reverse-spin': {
          from: {
            transform: 'rotate(360deg)'
          },
        }
      },
      spacing: {
        '150': '44.5rem',
        '120': '30.5rem',
        '500': '100rem'
      }
    },
  },
  variants: {
    extend: {
      display: ['dark']
    },
  },
  plugins: [],


  
  
}
