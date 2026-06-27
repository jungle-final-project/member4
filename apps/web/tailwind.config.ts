import type { Config } from 'tailwindcss';

export default {
  content: ['./index.html', './src/**/*.{ts,tsx}'],
  theme: {
    extend: {
      colors: {
        brand: {
          navy: '#073558',
          blue: '#005bac',
          pale: '#eef6ff'
        }
      },
      boxShadow: {
        panel: '0 8px 24px rgba(15, 23, 42, 0.08)'
      }
    }
  },
  plugins: []
} satisfies Config;
