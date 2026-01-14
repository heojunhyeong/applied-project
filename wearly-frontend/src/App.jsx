import { BrowserRouter, Routes, Route } from 'react-router-dom';

import MainPage from './pages/MainPage.jsx';
import BrandPage from './pages/BrandPage.jsx';
//import CheckoutPage from './pages/checkout/CheckoutPage';
import Profile from './pages/testpages/Profile.jsx';
import SuccessPage from './pages/payment/SuccessPage';
import './App.css'

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<MainPage />} />
                <Route path="/:brand" element={<BrandPage />} />
                {/* <Route path="/checkout" element={<CheckoutPage />} /> */}
                <Route path="/payment/success" element={<SuccessPage />} />
                <Route path="/profile/upload" element={<Profile />} />
            </Routes>

        </BrowserRouter>
    );
}

export default App
