import { useState, useRef, useEffect } from 'react';
import { BrowserRouter, Routes, Route, Link } from 'react-router';
import { ShoppingCart, User, Package, UserCircle, Shield, Store } from 'lucide-react';
import HomePage from "./components/HomePage";
import BrandPage from "./components/BrandPage";
import ProductDetailPage from "./components/ProductDetailPage";
import SignUpPage from "./components/SignUpPage";
import LoginPage from "./components/LoginPage";
import ForgotPasswordPage from "./components/ForgotPasswordPage";
import ResetPasswordPage from "./components/ResetPasswordPage";
import ResetPasswordSuccessPage from "./components/ResetPasswordSuccessPage";
import OrderHistoryPage from "./components/OrderHistoryPage";
import DeliveryTrackingPage from "./components/DeliveryTrackingPage";
import ProfilePage from "./components/ProfilePage";
import CartPage from "./components/CartPage";
import UserManagementPage from "./components/admin/UserManagementPage";
import ProductManagementPage from "./components/admin/ProductManagementPage";
import OrderManagementPage from "./components/admin/OrderManagementPage";
import ReviewManagementPage from "./components/admin/ReviewManagementPage";
import PurchasePage from "./components/PurchasePage";
import SellerPage from "./components/SellerPage";

const brands = [
  { name: "LEVI'S", id: "levis" },
  { name: "NIKE", id: "nike" },
  { name: "ADIDAS", id: "adidas" },
  { name: "NEW BALANCE", id: "new-balance" },
  { name: "THE NORTH FACE", id: "the-north-face" },
];

export default function App() {
  const [isMyPageOpen, setIsMyPageOpen] = useState(false);
  const myPageRef = useRef<HTMLDivElement>(null);

  // Close dropdown when clicking outside
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (
        myPageRef.current &&
        !myPageRef.current.contains(event.target as Node)
      ) {
        setIsMyPageOpen(false);
      }
    };

    document.addEventListener("mousedown", handleClickOutside);
    return () =>
      document.removeEventListener(
        "mousedown",
        handleClickOutside,
      );
  }, []);

  return (
    <BrowserRouter>
      <div className="min-h-screen bg-white">
        {/* Header */}
        <header className="border-b border-gray-200 sticky top-0 z-50 bg-white">
          <div className="max-w-[1400px] mx-auto px-8 py-4 flex items-center justify-between gap-8">
            {/* Left: Logo */}
            <Link
              to="/"
              className="text-2xl font-bold text-gray-900 tracking-tight whitespace-nowrap"
            >
              Wearly
            </Link>

            {/* Center: Brand Navigation */}
            <nav className="hidden md:flex items-center gap-8">
              {brands.map((brand) => (
                <Link
                  key={brand.id}
                  to={`/brand/${brand.id}`}
                  className="text-sm text-gray-700 hover:text-gray-900 transition-colors whitespace-nowrap"
                >
                  {brand.name}
                </Link>
              ))}
            </nav>

            {/* Right: My Page & Cart */}
            <div className="flex items-center gap-6 whitespace-nowrap">
              {/* Sign Up */}
              <Link
                to="/signup"
                className="text-sm text-gray-700 hover:text-gray-900 transition-colors"
              >
                Sign Up
              </Link>

              {/* Login */}
              <Link
                to="/login"
                className="text-sm text-gray-700 hover:text-gray-900 transition-colors"
              >
                Login
              </Link>

              {/* My Page Dropdown */}
              <div className="relative" ref={myPageRef}>
                <button
                  onClick={() => setIsMyPageOpen(!isMyPageOpen)}
                  onMouseEnter={() => setIsMyPageOpen(true)}
                  className="flex items-center gap-2 text-sm text-gray-700 hover:text-gray-900 transition-colors"
                >
                  <User className="w-5 h-5" />
                  <span>My Page</span>
                </button>

                {/* Dropdown Menu */}
                {isMyPageOpen && (
                  <div className="absolute top-full right-0 mt-2 w-48 bg-white border border-gray-200 rounded-lg shadow-lg overflow-hidden">
                    <Link
                      to="/orders"
                      className="flex items-center gap-3 px-4 py-3 text-sm text-gray-700 hover:bg-gray-50 transition-colors"
                      onClick={() => setIsMyPageOpen(false)}
                    >
                      <Package className="w-4 h-4" />
                      Order History
                    </Link>
                    <div className="border-t border-gray-100"></div>
                    <Link
                      to="/profile"
                      className="flex items-center gap-3 px-4 py-3 text-sm text-gray-700 hover:bg-gray-50 transition-colors"
                      onClick={() => setIsMyPageOpen(false)}
                    >
                      <UserCircle className="w-4 h-4" />
                      My Profile
                    </Link>
                  </div>
                )}
              </div>

              {/* Admin Page */}
              <Link
                to="/admin/users"
                className="flex items-center gap-2 text-sm text-gray-700 hover:text-gray-900 transition-colors"
              >
                <Shield className="w-5 h-5" />
                <span>Admin Page</span>
              </Link>

              {/* Seller Page */}
              <Link
                to="/seller"
                className="flex items-center gap-2 text-sm text-gray-700 hover:text-gray-900 transition-colors"
              >
                <Store className="w-5 h-5" />
                <span>Seller Page</span>
              </Link>

              {/* Cart */}
              <Link to="/cart" className="relative p-2 hover:bg-gray-100 rounded-full transition-colors">
                <ShoppingCart className="w-5 h-5 text-gray-700" />
                <span className="absolute top-0 right-0 w-5 h-5 bg-red-500 text-white text-xs flex items-center justify-center rounded-full">
                  2
                </span>
              </Link>
            </div>
          </div>
        </header>

        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route
            path="/brand/:brandName"
            element={<BrandPage />}
          />
          <Route
            path="/product/:productId"
            element={<ProductDetailPage />}
          />
          <Route path="/signup" element={<SignUpPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/forgot-password" element={<ForgotPasswordPage />} />
          <Route path="/reset-password" element={<ResetPasswordPage />} />
          <Route path="/reset-password/success" element={<ResetPasswordSuccessPage />} />
          <Route path="/orders" element={<OrderHistoryPage />} />
          <Route path="/tracking/:orderNumber" element={<DeliveryTrackingPage />} />
          <Route path="/profile" element={<ProfilePage />} />
          <Route path="/cart" element={<CartPage />} />
          <Route path="/admin/users" element={<UserManagementPage />} />
          <Route path="/admin/products" element={<ProductManagementPage />} />
          <Route path="/admin/orders" element={<OrderManagementPage />} />
          <Route path="/admin/reviews" element={<ReviewManagementPage />} />
          <Route path="/checkout" element={<PurchasePage />} />
          <Route path="/seller" element={<SellerPage />} />
        </Routes>
      </div>
    </BrowserRouter>
  );
}