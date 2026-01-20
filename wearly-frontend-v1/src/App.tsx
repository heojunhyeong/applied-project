import { useState, useRef, useEffect, useCallback } from "react";
import { BrowserRouter, Routes, Route, Link, Navigate } from "react-router-dom";

import SearchPage from "./components/SearchPage";
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
import PurchasePage from "./components/PurchasePage";
import PaymentSuccessPage from "./components/PaymentSuccessPage";
import MembershipPage from "./components/membership/MembershipPage";
import ReviewFormPage from "./components/ReviewFormPage";

import UserManagementPage from "./components/admin/UserManagementPage";
import ProductManagementPage from "./components/admin/ProductManagementPage";
import OrderManagementPage from "./components/admin/OrderManagementPage";
import ReviewManagementPage from "./components/admin/ReviewManagementPage";

import SellerLayout from "./components/seller/SellerLayout";
import SellerProductManagementPage from "./components/seller/SellerProductManagementPage";
import SellerOrderManagementPage from "./components/seller/SellerOrderManagementPage";

import SettlementPage from './components/SettlementPage';
import Footer from "./components/Footer";

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
  Crown,
} from "lucide-react";

import { BRANDS } from "./constants/brands";
import { apiFetch } from "./api/http";

type Role = "USER" | "SELLER" | "ADMIN";

function Header({ brandItems }: { brandItems: typeof BRANDS }) {
  const [isMyPageOpen, setIsMyPageOpen] = useState(false);
  const [isSellerPageOpen, setIsSellerPageOpen] = useState(false);
  const [isAdminPageOpen, setIsAdminPageOpen] = useState(false);

  const myPageRef = useRef<HTMLDivElement>(null);
  const sellerPageRef = useRef<HTMLDivElement>(null);
  const adminPageRef = useRef<HTMLDivElement>(null);

  const [token, setToken] = useState<string | null>(
    localStorage.getItem("accessToken"),
  );
  const [role, setRole] = useState<Role | null>(
    localStorage.getItem("role") as Role | null,
  );
  const isLoggedIn = !!token;

  // 장바구니 고유 상품 개수 상태
  const [cartItemCount, setCartItemCount] = useState<number>(0);

  // // localStorage 변경 감지
  useEffect(() => {
    const handleStorageChange = () => {
      setToken(localStorage.getItem("accessToken"));
      setRole(localStorage.getItem("role") as Role | null);
    };

    window.addEventListener("storage", handleStorageChange);
    window.addEventListener("authStateChange", handleStorageChange);
    handleStorageChange();

    return () => {
      window.removeEventListener("storage", handleStorageChange);
      window.removeEventListener("authStateChange", handleStorageChange);
    };
  }, []);

  // 장바구니 고유 상품 개수 가져오기
  const fetchCartItemCount = useCallback(async () => {
    if (!isLoggedIn || role !== "USER") {
      setCartItemCount(0);
      return;
    }

    try {
      interface CartResponseDto {
        cartId: number;
        productId: number;
        productName: string;
        price: number;
        quantity: number;
        size: string;
        imageUrl: string;
      }

      const data = await apiFetch<CartResponseDto[]>(`/api/users/cart/items`);

      // productId를 기준으로 고유한 상품 개수 계산 (같은 상품은 1개로 처리)
      const uniqueProductIds = new Set(data.map(item => item.productId));
      setCartItemCount(uniqueProductIds.size);
    } catch (err) {
      // 에러 발생 시 0으로 설정
      setCartItemCount(0);
    }
  }, [isLoggedIn, role]);

  // 로그인 상태나 역할이 변경될 때 장바구니 개수 업데이트
  useEffect(() => {
    fetchCartItemCount();
  }, [fetchCartItemCount]);

  // 페이지 포커스 시 장바구니 개수 업데이트
  useEffect(() => {
    const handleFocus = () => {
      fetchCartItemCount();
    };

    window.addEventListener("focus", handleFocus);
    return () => window.removeEventListener("focus", handleFocus);
  }, [fetchCartItemCount]);

  // 장바구니 변경 이벤트 리스너
  useEffect(() => {
    const handleCartChange = () => {
      fetchCartItemCount();
    };

    window.addEventListener("cartChange", handleCartChange);
    return () => window.removeEventListener("cartChange", handleCartChange);
  }, [fetchCartItemCount]);

  // // 로그아웃 처리
  const handleLogout = async () => {
    try {
      const refreshToken = localStorage.getItem("refreshToken");

      if (refreshToken) {
        try {
          await fetch("/api/auth/logout", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ refreshToken }),
          });
        } catch (err) {
          console.error("Logout API error:", err);
        }
      }

      localStorage.removeItem("accessToken");
      localStorage.removeItem("refreshToken");
      localStorage.removeItem("role");

      setToken(null);
      setRole(null);

      window.dispatchEvent(new Event("authStateChange"));
      window.location.href = "/";
    } catch (err) {
      console.error("Logout error:", err);
      localStorage.removeItem("accessToken");
      localStorage.removeItem("refreshToken");
      localStorage.removeItem("role");
      setToken(null);
      setRole(null);
      window.location.href = "/";
    }
  };

  // // 바깥 클릭하면 드롭다운 닫기
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

  // // 역할 변경 시 드롭다운 닫기
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

        {/* Right: Role-based Navigation */}
        <div className="flex items-center gap-6 whitespace-nowrap">
          {/* 비로그인 */}
          {!isLoggedIn && (
            <>
              <Link
                to="/signup"
                className="text-sm text-gray-700 hover:text-gray-900 transition-colors"
              >
                회원가입
              </Link>
              <Link
                to="/login"
                className="text-sm text-gray-700 hover:text-gray-900 transition-colors"
              >
                로그인
              </Link>
            </>
          )}

          {/* USER */}
          {isLoggedIn && role === "USER" && (
            <>
              <div className="relative" ref={myPageRef}>
                <button
                  onClick={() => setIsMyPageOpen((prev) => !prev)}
                  className="flex items-center gap-2 text-sm text-gray-700 hover:text-gray-900 transition-colors"
                >
                  <UserCircle className="w-5 h-5" />
                  <span>마이페이지</span>
                </button>

                {isMyPageOpen && (
                  <div className="absolute top-full right-0 mt-2 w-48 bg-white border border-gray-200 rounded-lg shadow-lg overflow-hidden">
                    <Link
                      to="/orders"
                      className="flex items-center gap-3 px-4 py-3 text-sm text-gray-700 hover:bg-gray-50 transition-colors"
                      onClick={() => setIsMyPageOpen(false)}
                    >
                      <Package className="w-4 h-4" />
                      주문 내역
                    </Link>
                    <div className="border-t border-gray-100" />
                    <Link
                      to="/profile" // // 공통 프로필 페이지로 이동
                      className="flex items-center gap-3 px-4 py-3 text-sm text-gray-700 hover:bg-gray-50 transition-colors"
                      onClick={() => setIsMyPageOpen(false)}
                    >
                      <UserCircle className="w-4 h-4" />
                      내 프로필
                    </Link>
                    <div className="border-t border-gray-100" />
                    <Link
                      to="/membership"
                      className="flex items-center gap-3 px-4 py-3 text-sm text-gray-700 hover:bg-gray-50 transition-colors"
                      onClick={() => setIsMyPageOpen(false)}
                    >
                      <Crown className="w-4 h-4" />
                      멤버십
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
                {cartItemCount > 0 && (
                  <span className="absolute top-0 right-0 w-5 h-5 bg-red-500 text-white text-xs flex items-center justify-center rounded-full">
                    {cartItemCount}
                  </span>
                )}
              </Link>

              <button
                onClick={handleLogout}
                className="flex items-center gap-2 text-sm text-gray-700 hover:text-gray-900 transition-colors"
              >
                <LogOut className="w-5 h-5" />
                <span>로그아웃</span>
              </button>
            </>
          )}

          {/* SELLER */}
          {isLoggedIn && role === "SELLER" && (
            <>
              <div className="relative" ref={sellerPageRef}>
                <button
                  onClick={() => setIsSellerPageOpen((prev) => !prev)}
                  className="flex items-center gap-2 text-sm text-gray-700 hover:text-gray-900 transition-colors"
                >
                  <Store className="w-5 h-5" />
                  <span>판매자 페이지</span>
                </button>

                {isSellerPageOpen && (
                  <div className="absolute top-full right-0 mt-2 w-56 bg-white border border-gray-200 rounded-lg shadow-lg overflow-hidden">
                    <Link
                      to="/profile" // // SELLER도 공통 프로필 페이지로 이동
                      className="flex items-center gap-3 px-4 py-3 text-sm text-gray-700 hover:bg-gray-50 transition-colors"
                      onClick={() => setIsSellerPageOpen(false)}
                    >
                      <UserCircle className="w-4 h-4" />
                      내 프로필
                    </Link>
                    <div className="border-t border-gray-100" />
                    <Link
                      to="/seller/products"
                      className="flex items-center gap-3 px-4 py-3 text-sm text-gray-700 hover:bg-gray-50 transition-colors"
                      onClick={() => setIsSellerPageOpen(false)}
                    >
                      <Package className="w-4 h-4" />
                      상품 관리
                    </Link>
                    <div className="border-t border-gray-100" />
                    <Link
                      to="/seller/orders"
                      className="flex items-center gap-3 px-4 py-3 text-sm text-gray-700 hover:bg-gray-50 transition-colors"
                      onClick={() => setIsSellerPageOpen(false)}
                    >
                      <Package className="w-4 h-4" />
                      주문 관리
                    </Link>
                  </div>
                )}
              </div>

              <button
                onClick={handleLogout}
                className="flex items-center gap-2 text-sm text-gray-700 hover:text-gray-900 transition-colors"
              >
                <LogOut className="w-5 h-5" />
                <span>로그아웃</span>
              </button>
            </>
          )}

          {/* ADMIN */}
          {isLoggedIn && role === "ADMIN" && (
            <>
              <div className="relative" ref={adminPageRef}>
                <button
                  onClick={() => setIsAdminPageOpen((prev) => !prev)}
                  className="flex items-center gap-2 text-sm text-gray-700 hover:text-gray-900 transition-colors"
                >
                  <Shield className="w-5 h-5" />
                  <span>관리자 페이지</span>
                </button>

                {isAdminPageOpen && (
                  <div className="absolute top-full right-0 mt-2 w-60 bg-white border border-gray-200 rounded-lg shadow-lg overflow-hidden">
                    <Link
                      to="/profile" // // 공통 프로필 페이지로 이동
                      className="flex items-center gap-3 px-4 py-3 text-sm text-gray-700 hover:bg-gray-50 transition-colors"
                      onClick={() => setIsAdminPageOpen(false)}
                    >
                      <UserCircle className="w-4 h-4" />
                      내 프로필
                    </Link>
                    <div className="border-t border-gray-100" />
                    <Link
                      to="/admin/users"
                      className="flex items-center gap-3 px-4 py-3 text-sm text-gray-700 hover:bg-gray-50 transition-colors"
                      onClick={() => setIsAdminPageOpen(false)}
                    >
                      <Users className="w-4 h-4" />
                      회원 관리
                    </Link>
                    <div className="border-t border-gray-100" />
                    <Link
                      to="/admin/products"
                      className="flex items-center gap-3 px-4 py-3 text-sm text-gray-700 hover:bg-gray-50 transition-colors"
                      onClick={() => setIsAdminPageOpen(false)}
                    >
                      <Package className="w-4 h-4" />
                      상품 관리
                    </Link>
                    <div className="border-t border-gray-100" />
                    <Link
                      to="/admin/orders"
                      className="flex items-center gap-3 px-4 py-3 text-sm text-gray-700 hover:bg-gray-50 transition-colors"
                      onClick={() => setIsAdminPageOpen(false)}
                    >
                      <ClipboardList className="w-4 h-4" />
                      주문 관리
                    </Link>
                    <div className="border-t border-gray-100" />
                    <Link
                      to="/admin/reviews"
                      className="flex items-center gap-3 px-4 py-3 text-sm text-gray-700 hover:bg-gray-50 transition-colors"
                      onClick={() => setIsAdminPageOpen(false)}
                    >
                      <MessageSquareWarning className="w-4 h-4" />
                      리뷰 관리
                    </Link>
                  </div>
                )}
              </div>

              <button
                onClick={handleLogout}
                className="flex items-center gap-2 text-sm text-gray-700 hover:text-gray-900 transition-colors"
              >
                <LogOut className="w-5 h-5" />
                <span>로그아웃</span>
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
      <div className="min-h-screen bg-white flex flex-col">
        <Header brandItems={BRANDS} />

        <Routes>
          <Route path="/search" element={<SearchPage />} />
          <Route path="/" element={<HomePage />} />
          <Route path="/brand/:brandName" element={<BrandPage />} />
          <Route path="/product/:productId" element={<ProductDetailPage />} />
          <Route path="/product/:productId/review" element={<ReviewFormPage />} />

          <Route path="/signup" element={<SignUpPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/forgot-password" element={<ForgotPasswordPage />} />
          <Route path="/reset-password" element={<ResetPasswordPage />} />
          <Route
            path="/reset-password/success"
            element={<ResetPasswordSuccessPage />}
          />

          <Route path="/orders" element={<OrderHistoryPage />} />
          <Route
            path="/tracking/:orderNumber"
            element={<DeliveryTrackingPage />}
          />
          <Route path="/profile" element={<ProfilePage />} />
          <Route path="/cart" element={<CartPage />} />

          {/* Admin */}
          <Route path="/admin/users" element={<UserManagementPage />} />
          <Route path="/admin/products" element={<ProductManagementPage />} />
          <Route path="/admin/orders" element={<OrderManagementPage />} />
          <Route path="/admin/reviews" element={<ReviewManagementPage />} />

          {/* Checkout */}
          <Route path="/checkout" element={<PurchasePage />} />

          {/* Seller - Flat Routes (SellerLayout children 방식) */}
          <Route
            path="/seller"
            element={<Navigate to="/seller/products" replace />}
          />
          <Route
            path="/seller/products"
            element={
              <SellerLayout>
                <SellerProductManagementPage />
              </SellerLayout>
            }
          />
          <Route
            path="/seller/orders"
            element={
              <SellerLayout>
                <SellerOrderManagementPage />
              </SellerLayout>
            }
          />

          <Route path="/payment/success" element={<PaymentSuccessPage />} />
          <Route path="/membership" element={<MembershipPage />} />

          <Route path="*" element={<HomePage />} />

          <Route path="/seller/settlement" element={<SettlementPage />} />

        </Routes>
          <Footer/>
      </div>
    </BrowserRouter>
  );
}
