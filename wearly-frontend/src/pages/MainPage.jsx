import { useNavigate } from "react-router-dom";
import "./MainPage.css";

function MainPage() {
  const navigate = useNavigate();

  const handleBrandClick = (brand) => {
    navigate(`/${brand}`);
  };

  return (
    <div className="main-page">
      {/* 환영 문구 영역 */}
      <div className="welcome-container">
        <div className="welcome-box">
          <h1 className="welcome-text">Wearly에 오신걸 환영합니다</h1>
        </div>
      </div>

      {/* 브랜드 선택 영역 */}
      <div className="brands-container">
        <button
          className="brand-button nike"
          onClick={() => handleBrandClick("nike")}
        >
          나이키
        </button>
        <button
          className="brand-button adidas"
          onClick={() => handleBrandClick("adidas")}
        >
          아디다스
        </button>
        <button
          className="brand-button newbalance"
          onClick={() => handleBrandClick("newbalance")}
        >
          뉴발란스
        </button>
      </div>
    </div>
  );
}

export default MainPage;
