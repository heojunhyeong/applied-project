import { useState, useRef, useEffect } from 'react';
import { BrowserRouter, Routes, Route, Link } from 'react-router-dom';
import SearchPage from "./components/SearchPage";
import {
  ShoppingCart,
  Package,
  UserCircle,
  Shield,
  Store,
  LogOut,
  Users,
  ClipboardList,
  MessageSquareWarning,
} from 'lucide-react';
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
import PaymentSuccessPage from "./components/PaymentSuccessPage";
import MembershipPage from "./components/membership/MembershipPage";
import ReviewFormPage from "./components/ReviewFormPage";

import { BRANDS } from "./constants/brands";

type Role = "USER" | "SELLER" | "ADMIN";

function Header({ brandItems }: { brandItems: typeof BRANDS }) {
  const [isMyPageOpen, setIsMyPageOpen] = useState(false);
  const [isSellerPageOpen, setIsSellerPageOpen] = useState(false);
  const [isAdminPageOpen, setIsAdminPageOpen] = useState(false);
  const myPageRef = useRef<HTMLDivElement>(null);
  const sellerPageRef = useRef<HTMLDivElement>(null);
  const adminPageRef = useRef<HTMLDivElement>(null);

  // ✅ 로그인 상태/권한을 state로 관리하여 localStorage 변경 감지
  const [token, setToken] = useState<string | null>(localStorage.getItem("accessToken"));
  const [role, setRole] = useState<Role | null>(localStorage.getItem("role") as Role | null);
  const isLoggedIn = !!token;

  // localStorage 변경 감지 (로그인/로그아웃 시 반영)
  useEffect(() => {
    const handleStorageChange = () => {
      setToken(localStorage.getItem("accessToken"));
      setRole(localStorage.getItem("role") as Role | null);
    };

    // storage 이벤트 리스너 등록 (다른 탭에서 변경된 경우)
    window.addEventListener("storage", handleStorageChange);

    // 같은 탭에서 localStorage가 변경된 경우를 위한 커스텀 이벤트
    window.addEventListener("authStateChange", handleStorageChange);

    // 초기값 설정
    handleStorageChange();

    return () => {
      window.removeEventListener("storage", handleStorageChange);
      window.removeEventListener("authStateChange", handleStorageChange);
    };
  }, []);

  const handleLogout = async () => {
    try {
      const refreshToken = localStorage.getItem("refreshToken");

      // 백엔드에 로그아웃 요청 (Refresh Token 삭제)
      if (refreshToken) {
        try {
          await fetch("/api/auth/logout", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ refreshToken }),
          });
        } catch (err) {
          // 로그아웃 API 호출 실패해도 클라이언트에서는 토큰 제거
          console.error("Logout API error:", err);
        }
      }

      // localStorage에서 토큰 및 권한 제거
      localStorage.removeItem("accessToken");
      localStorage.removeItem("refreshToken");
      localStorage.removeItem("role");

      // 상태 업데이트
      setToken(null);
      setRole(null);

      // authStateChange 이벤트 발생 (다른 컴포넌트에서도 감지 가능)
      window.dispatchEvent(new Event("authStateChange"));

      // 홈페이지로 리다이렉트
      window.location.href = "/";
    } catch (err) {
      console.error("Logout error:", err);
      // 에러가 나도 토큰 제거는 수행
      localStorage.removeItem("accessToken");
      localStorage.removeItem("refreshToken");
      localStorage.removeItem("role");
      setToken(null);
      setRole(null);
      window.location.href = "/";
    }
  };

  // Close dropdown when clicking outside
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      const target = event.target as Node;
      const clickedMyPage = myPageRef.current?.contains(target);
      const clickedSellerPage = sellerPageRef.current?.contains(target);
      const clickedAdminPage = adminPageRef.current?.contains(target);

      if (!clickedMyPage && !clickedSellerPage && !clickedAdminPage) {
        setIsMyPageOpen(false);
        setIsSellerPageOpen(false);
        setIsAdminPageOpen(false);
      }
    };

    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  // 역할 변경 시 열려있는 드롭다운 모두 닫기
  useEffect(() => {
    setIsMyPageOpen(false);
    setIsSellerPageOpen(false);
    setIsAdminPageOpen(false);
  }, [role]);

  return (
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
          {brandItems.map((brand) => (
            <Link
              key={brand.id}
              to={`/brand/${brand.id}`}
              className="text-sm text-gray-700 hover:text-gray-900 transition-colors whitespace-nowrap"
            >
              {brand.name}
            </Link>
          ))}
        </nav>

        {/* ✅ Right: Role-based Navigation */}
        <div className="flex items-center gap-6 whitespace-nowrap">
          {/* 1) 비로그인: 회원가입 | 로그인만 */}
          {!isLoggedIn && (
            <>
              <Link
                to="/signup"
                className="text-sm text-gray-700 hover:text-gray-900 transition-colors"
              >
                Sign Up
              </Link>

              <Link
                to="/login"
                className="text-sm text-gray-700 hover:text-gray-900 transition-colors"
              >
                Login
              </Link>
            </>
          )}

          {/* 2) USER 로그인: 마이페이지(드롭다운), 카트, 로그아웃 */}
          {isLoggedIn && role === "USER" && (
            <>
              <div className="relative" ref={myPageRef}>
                <button
                  onClick={() => {
                    // 마이페이지 드롭다운 토글
                    setIsMyPageOpen((prev) => !prev);
                  }}
                  className="flex items-center gap-2 text-sm text-gray-700 hover:text-gray-900 transition-colors"
                >
                  <UserCircle className="w-5 h-5" />
                  <span>My Page</span>
                </button>

                {/* Dropdown Menu */}
                {isMyPageOpen && (
                  <div className="absolute top-full right-0 mt-2 w-48 bg-white border border-gray-200 rounded-lg shadow-lg overflow-hidden">
                    <Link
                      to="/orders"
                      className="flex items-center gap-3 px-4 py-3 text-sm text-gray-700 hover:bg-gray-50 transition-colors"
                      onClick={() => {
                        // 마이페이지 드롭다운 닫기
                        setIsMyPageOpen(false);
                      }}
                    >
                      <Package className="w-4 h-4" />
                      Order History
                    </Link>
                    <div className="border-t border-gray-100"></div>
                    <Link
                      // 공통 프로필 페이지로 이동
                      to="/profile"
                      className="flex items-center gap-3 px-4 py-3 text-sm text-gray-700 hover:bg-gray-50 transition-colors"
                      onClick={() => {
                        // 마이페이지 드롭다운 닫기
                        setIsMyPageOpen(false);
                      }}
                    >
                      <UserCircle className="w-4 h-4" />
                      My Profile
                    </Link>
                  </div>
                )}
              </div>

              <Link
                to="/cart"
                className="relative p-2 hover:bg-gray-100 rounded-full transition-colors"
                aria-label="cart"
              >
                <ShoppingCart className="w-5 h-5 text-gray-700" />
                <span className="absolute top-0 right-0 w-5 h-5 bg-red-500 text-white text-xs flex items-center justify-center rounded-full">
                  2
                </span>
              </Link>

              <button
                onClick={() => {
                  // 로그아웃 처리
                  handleLogout();
                }}
                className="flex items-center gap-2 text-sm text-gray-700 hover:text-gray-900 transition-colors"
              >
                <LogOut className="w-5 h-5" />
                <span>Logout</span>
              </button>
            </>
          )}

          {/* 3) SELLER 로그인: 판매자페이지(드롭다운), 로그아웃 */}
          {isLoggedIn && role === "SELLER" && (
            <>
              <div className="relative" ref={sellerPageRef}>
                <button
                  onClick={() => {
                    // 판매자페이지 드롭다운 토글
                    setIsSellerPageOpen((prev) => !prev);
                  }}
                  className="flex items-center gap-2 text-sm text-gray-700 hover:text-gray-900 transition-colors"
                >
                  <Store className="w-5 h-5" />
                  <span>Seller Page</span>
                </button>

                {/* Dropdown Menu */}
                {isSellerPageOpen && (
                  <div className="absolute top-full right-0 mt-2 w-56 bg-white border border-gray-200 rounded-lg shadow-lg overflow-hidden">
                    <Link
                      // 공통 프로필 페이지로 이동
                      to="/profile"
                      className="flex items-center gap-3 px-4 py-3 text-sm text-gray-700 hover:bg-gray-50 transition-colors"
                      onClick={() => {
                        // 판매자페이지 드롭다운 닫기
                        setIsSellerPageOpen(false);
                      }}
                    >
                      <UserCircle className="w-4 h-4" />
                      My Profile
                    </Link>
                    <div className="border-t border-gray-100"></div>
                    <Link
                      to="/seller/products"
                      className="flex items-center gap-3 px-4 py-3 text-sm text-gray-700 hover:bg-gray-50 transition-colors"
                      onClick={() => {
                        // 판매자페이지 드롭다운 닫기
                        setIsSellerPageOpen(false);
                      }}
                    >
                      <Package className="w-4 h-4" />
                      Product Management
                    </Link>
                    <div className="border-t border-gray-100"></div>
                    <Link
                      to="/seller/orders"
                      className="flex items-center gap-3 px-4 py-3 text-sm text-gray-700 hover:bg-gray-50 transition-colors"
                      onClick={() => {
                        // 판매자페이지 드롭다운 닫기
                        setIsSellerPageOpen(false);
                      }}
                    >
                      <Package className="w-4 h-4" />
                      Order Management
                    </Link>
                  </div>
                )}
              </div>

              <button
                onClick={() => {
                  // 로그아웃 처리
                  handleLogout();
                }}
                className="flex items-center gap-2 text-sm text-gray-700 hover:text-gray-900 transition-colors"
              >
                <LogOut className="w-5 h-5" />
                <span>Logout</span>
              </button>
            </>
          )}

          {/* 4) ADMIN 로그인: 관리자페이지(드롭다운), 로그아웃 */}
          {isLoggedIn && role === "ADMIN" && (
            <>
              <div className="relative" ref={adminPageRef}>
                <button
                  onClick={() => {
                    // 관리자페이지 드롭다운 토글
                    setIsAdminPageOpen((prev) => !prev);
                  }}
                  className="flex items-center gap-2 text-sm text-gray-700 hover:text-gray-900 transition-colors"
                >
                  <Shield className="w-5 h-5" />
                  <span>Admin Page</span>
                </button>

                {/* Dropdown Menu */}
                {isAdminPageOpen && (
                  <div className="absolute top-full right-0 mt-2 w-60 bg-white border border-gray-200 rounded-lg shadow-lg overflow-hidden">
                    <Link
                      // 공통 프로필 페이지로 이동
                      to="/profile"
                      className="flex items-center gap-3 px-4 py-3 text-sm text-gray-700 hover:bg-gray-50 transition-colors"
                      onClick={() => {
                        // 관리자페이지 드롭다운 닫기
                        setIsAdminPageOpen(false);
                      }}
                    >
                      <UserCircle className="w-4 h-4" />
                      My Profile
                    </Link>
                    <div className="border-t border-gray-100"></div>
                    <Link
                      to="/admin/users"
                      className="flex items-center gap-3 px-4 py-3 text-sm text-gray-700 hover:bg-gray-50 transition-colors"
                      onClick={() => {
                        // 관리자페이지 드롭다운 닫기
                        setIsAdminPageOpen(false);
                      }}
                    >
                      <Users className="w-4 h-4" />
                      User Management
                    </Link>
                    <div className="border-t border-gray-100"></div>
                    <Link
                      to="/admin/products"
                      className="flex items-center gap-3 px-4 py-3 text-sm text-gray-700 hover:bg-gray-50 transition-colors"
                      onClick={() => {
                        // 관리자페이지 드롭다운 닫기
                        setIsAdminPageOpen(false);
                      }}
                    >
                      <Package className="w-4 h-4" />
                      Product Management
                    </Link>
                    <div className="border-t border-gray-100"></div>
                    <Link
                      to="/admin/orders"
                      className="flex items-center gap-3 px-4 py-3 text-sm text-gray-700 hover:bg-gray-50 transition-colors"
                      onClick={() => {
                        // 관리자페이지 드롭다운 닫기
                        setIsAdminPageOpen(false);
                      }}
                    >
                      <ClipboardList className="w-4 h-4" />
                      Order Management
                    </Link>
                    <div className="border-t border-gray-100"></div>
                    <Link
                      to="/admin/reviews"
                      className="flex items-center gap-3 px-4 py-3 text-sm text-gray-700 hover:bg-gray-50 transition-colors"
                      onClick={() => {
                        // 관리자페이지 드롭다운 닫기
                        setIsAdminPageOpen(false);
                      }}
                    >
                      <MessageSquareWarning className="w-4 h-4" />
                      Review Management
                    </Link>
                  </div>
                )}
              </div>

              <button
                onClick={() => {
                  // 로그아웃 처리
                  handleLogout();
                }}
                className="flex items-center gap-2 text-sm text-gray-700 hover:text-gray-900 transition-colors"
              >
                <LogOut className="w-5 h-5" />
                <span>Logout</span>
              </button>
            </>
          )}
        </div>
      </div>
    </header>
  );
}

export default function App() {
  return (
    <BrowserRouter>
      <div className="min-h-screen bg-white">
        {/* Header */}
        <Header brandItems={BRANDS} />

        <Routes>
          <Route path="/search" element={<SearchPage />} />
          <Route path="/" element={<HomePage />} />
          <Route path="/brand/:brandName" element={<BrandPage />} />
          <Route path="/brand/:brandName" element={<BrandPage />} />
          <Route path="/product/:productId" element={<ProductDetailPage />} />
          <Route path="/product/:productId/review" element={<ReviewFormPage />} />
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
          <Route path="/seller/:tab" element={<SellerPage />} />
          <Route path="/seller" element={<SellerPage />} />
          <Route path="/payment/success" element={<PaymentSuccessPage />} />
          <Route path="/membership" element={<MembershipPage />} />
          {/* 없는 경로 접근 시 홈으로 fallback */}
          <Route path="*" element={<HomePage />} />
        </Routes>
      </div>
    </BrowserRouter>
  );
}
