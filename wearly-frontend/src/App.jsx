import { BrowserRouter, Routes, Route } from 'react-router-dom';

import MainPage from './pages/MainPage.jsx';
import BrandPage from './pages/BrandPage.jsx';
import CheckoutPage from './pages/checkout/CheckoutPage';
import SuccessPage from './pages/payment/SuccessPage';
import './App.css'

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<MainPage />} />
                <Route path="/:brand" element={<BrandPage />} />
                <Route path="/checkout" element={<CheckoutPage />} />
                <Route path="/payment/success" element={<SuccessPage />} />
            </Routes>

        </BrowserRouter>
    );
}

export default App
