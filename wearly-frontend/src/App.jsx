import { BrowserRouter, Routes, Route } from 'react-router-dom';

import Header from './components/header/Header.jsx';
import MainPage from './pages/MainPage.jsx';
import BrandPage from './pages/BrandPage.jsx';
//import CheckoutPage from './pages/checkout/CheckoutPage';
import SignupPage from './pages/SignupPage.jsx';
import LoginPage from './pages/LoginPage.jsx';
import Profile from './pages/Profile.jsx';
import SuccessPage from './pages/payment/SuccessPage';
import './App.css'

function App() {
    return (
        <BrowserRouter>
            <Header />
            <Routes>
                <Route path="/" element={<MainPage />} />
                <Route path="/:brand" element={<BrandPage />} />
                {/* <Route path="/checkout" element={<CheckoutPage />} /> */}
                <Route path="/signup" element={<SignupPage />} />
                <Route path="/login" element={<LoginPage />} />
                <Route path="/payment/success" element={<SuccessPage />} />
                <Route path="/profile/upload" element={<Profile />} />
                <Route path="/cart" element={<CartPage />} />
            </Routes>
        </BrowserRouter>
    );
}

export default App