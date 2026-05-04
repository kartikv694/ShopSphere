  import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.jsx'
import { SearchProvider } from './components/SearchContext.jsx'
import "leaflet/dist/leaflet.css";

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <SearchProvider>
    <App />
    </SearchProvider>
  </StrictMode>,
)
