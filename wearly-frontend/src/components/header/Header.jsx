import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "./Header.css";
import LoginModal from "./LoginModal";

const Header = () => {
  const navigate = useNavigate();
  const [searchQuery, setSearchQuery] = useState("");
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [showLoginModal, setShowLoginModal] = useState(false);
  const [cartCount, setCartCount] = useState(0);

  // 로그인 상태 확인
  useEffect(() => {
    const token = localStorage.getItem("token");
    setIsLoggedIn(!!token);
  }, []);

  // localStorage 변경 감지 (다른 컴포넌트에서 로그인/로그아웃 시)
  useEffect(() => {
    const handleStorageChange = () => {
      const token = localStorage.getItem("token");
      setIsLoggedIn(!!token);
      if (token) {
        fetchCartItems();
      } else {
        setCartCount(0);
      }
    };

    window.addEventListener("storage", handleStorageChange);
    window.addEventListener("loginStatusChange", handleStorageChange);
    window.addEventListener("cartUpdate", fetchCartItems);

    return () => {
      window.removeEventListener("storage", handleStorageChange);
      window.removeEventListener("loginStatusChange", handleStorageChange);
      window.removeEventListener("cartUpdate", fetchCartItems);
    };
  }, []);

  // 장바구니 아이템 가져오기
  const fetchCartItems = async () => {
    const token = localStorage.getItem("token");
    if (!token) {
      setCartCount(0);
      return;
    }

    try {
      const response = await fetch("/api/users/cart/items", {
        method: "GET",
        headers: {
          "Authorization": `Bearer ${token}`,
        },
      });

      if (response.ok) {
        const cartItems = await response.json();
        const totalQuantity = cartItems.reduce((sum, item) => sum + (item.quantity || 0), 0);
        setCartCount(totalQuantity);
      } else {
        setCartCount(0);
      }
    } catch (error) {
      console.error("장바구니 조회 오류:", error);
      setCartCount(0);
    }
  };

  // 로그인 상태가 변경되면 장바구니 조회
  useEffect(() => {
    if (isLoggedIn) {
      fetchCartItems();
    } else {
      setCartCount(0);
    }
  }, [isLoggedIn]);

  const handleSearch = (e) => {
    if (e.key === "Enter" && searchQuery.trim()) {
      // 검색 기능 구현 (필요시)
      console.log("검색:", searchQuery);
    }
  };

  const handleLoginClick = () => {
    setShowLoginModal(true);
  };

  const handleCloseLoginModal = () => {
    setShowLoginModal(false);
  };

  const handleProfile = () => {
    navigate("/profile/upload");
  };

  const handleCartClick = () => {
    if (isLoggedIn) {
      navigate("/cart");
    } else {
      if (window.confirm("로그인이 필요합니다. 로그인 페이지로 이동하시겠습니까?")) {
        navigate("/login");
      }
    }
  };

  return (
    <>
      <header className="header">
        <div className="header-container">
          <div className="logo" onClick={() => navigate("/")}>
            wearly
          </div>
          
          <div className="header-search">
            <div className="search-box">
              <span className="search-icon">Q</span>
              <input
                type="text"
                placeholder="검색..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                onKeyPress={handleSearch}
                className="search-input"
              />
            </div>
          </div>

          <div className="header-icons">
            <button 
              className="icon-button" 
              onClick={isLoggedIn ? handleProfile : handleLoginClick} 
              title={isLoggedIn ? "마이페이지" : "로그인"}
            >
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                <circle cx="12" cy="7" r="4"></circle>
              </svg>
            </button>
            <button 
              className="icon-button cart-button" 
              onClick={handleCartClick}
              title="장바구니"
            >
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M6 2L3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4z"></path>
                <line x1="3" y1="6" x2="21" y2="6"></line>
                <path d="M16 10a4 4 0 0 1-8 0"></path>
              </svg>
              {cartCount > 0 && <span className="cart-badge">{cartCount}</span>}
            </button>
          </div>
        </div>
      </header>
      
      {showLoginModal && (
        <LoginModal 
          onClose={handleCloseLoginModal}
          onLoginSuccess={() => {
            setIsLoggedIn(true);
            setShowLoginModal(false);
            fetchCartItems();
          }}
        />
      )}
    </>
  );
};

export default Header;
