import { BrowserRouter, Routes, Route } from 'react-router-dom';

import MainPage from './pages/MainPage.jsx';
import BrandPage from './pages/BrandPage.jsx';
import './App.css'

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<MainPage />} />
                <Route path="/:brand" element={<BrandPage />} />
            </Routes>

        </BrowserRouter>
    );
}

export default App
