  import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.jsx'
import { SearchProvider } from './components/SearchContext.jsx'

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <SearchProvider>
    <App />
    </SearchProvider>
  </StrictMode>,
)
