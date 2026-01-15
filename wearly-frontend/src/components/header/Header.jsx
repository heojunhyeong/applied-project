import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "./Header.css";

const Header = () => {
  const navigate = useNavigate();
  const [isLoggedIn, setIsLoggedIn] = useState(false);

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
    };

    window.addEventListener("storage", handleStorageChange);
    // 커스텀 이벤트로 같은 탭 내에서도 감지
    window.addEventListener("loginStatusChange", handleStorageChange);

    return () => {
      window.removeEventListener("storage", handleStorageChange);
      window.removeEventListener("loginStatusChange", handleStorageChange);
    };
  }, []);

  const handleLogin = () => {
    navigate("/login");
  };

  const handleSignup = () => {
    navigate("/signup");
  };

  const handleProfile = () => {
    navigate("/profile/upload");
  };

  const handleLogout = () => {
    if (window.confirm("로그아웃 하시겠습니까?")) {
      localStorage.removeItem("token");
      localStorage.removeItem("userId");
      localStorage.removeItem("userEmail");
      localStorage.removeItem("nickName");
      localStorage.removeItem("role");
      setIsLoggedIn(false);
      // 같은 탭에서도 상태 업데이트를 위해 커스텀 이벤트 발생
      window.dispatchEvent(new Event("loginStatusChange"));
      navigate("/");
    }
  };

  return (
    <header className="header">
      <div className="header-container">
        <div className="logo" onClick={() => navigate("/")}>
          Wearly
        </div>
        <nav className="header-nav">
          {isLoggedIn ? (
            <>
              <button className="header-button" onClick={handleProfile}>
                프로필
              </button>
              <button className="header-button" onClick={handleLogout}>
                로그아웃
              </button>
            </>
          ) : (
            <>
              <button className="header-button" onClick={handleSignup}>
                회원가입
              </button>
              <button className="header-button" onClick={handleLogin}>
                로그인
              </button>
            </>
          )}
        </nav>
      </div>
    </header>
  );
};

export default Header;
