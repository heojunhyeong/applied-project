import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./MainPage.css";

function MainPage() {
    const navigate = useNavigate();
    const [selectedCategory, setSelectedCategory] = useState("all");

    const handleCategoryClick = (category) => {
        setSelectedCategory(category);
    };


    return (
        <div className="main-page">
            {/* 메인 배너 */}
            <div className="banner-container">
                <div className="banner"></div>
            </div>

            {/* 카테고리 필터 */}
            <div className="category-filters">
                <button
                    className={`category-filter ${selectedCategory === "all" ? "active" : ""}`}
                    onClick={() => handleCategoryClick("all")}
                >
                    전체
                </button>
                <button
                    className={`category-filter ${selectedCategory === "shoes" ? "active" : ""}`}
                    onClick={() => handleCategoryClick("shoes")}
                >
                    신발
                </button>
                <button
                    className={`category-filter ${selectedCategory === "apparel" ? "active" : ""}`}
                    onClick={() => handleCategoryClick("apparel")}
                >
                    의류
                </button>
            </div>

            {/* 제품 표시 영역 */}
            <div className="products-area">
                <p className="no-products-message">등록된 제품이 없습니다.</p>
            </div>
        </div>
    );
}

export default MainPage;