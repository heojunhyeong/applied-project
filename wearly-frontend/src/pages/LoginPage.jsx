import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./LoginPage.css";

const LoginPage = () => {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        userId: "",
        userPassword: "",
    });
    const [errors, setErrors] = useState({});
    const [loading, setLoading] = useState(false);
    const [errorMessage, setErrorMessage] = useState("");

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({
            ...prev,
            [name]: value,
        }));
        if (errors[name]) {
            setErrors((prev) => ({
                ...prev,
                [name]: "",
            }));
        }
        setErrorMessage("");
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setErrors({});
        setErrorMessage("");

        try {
            const response = await fetch("/api/auth/login", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(formData),
            });

            const data = await response.json();

            if (!response.ok) {
                if (response.status === 400) {
                    if (data.error) {
                        setErrorMessage(data.message || "로그인에 실패했습니다.");
                    } else {
                        setErrors(data);
                    }
                } else {
                    setErrorMessage(data.message || "로그인에 실패했습니다.");
                }
                setLoading(false);
                return;
            }

            if (data.accessToken) {
                localStorage.setItem("token", data.accessToken);
                localStorage.setItem("userId", data.userId);
                localStorage.setItem("userEmail", data.userEmail);
                localStorage.setItem("nickName", data.nickName);
                localStorage.setItem("role", data.role);
                // [추가] 헤더 상태 업데이트를 위한 커스텀 이벤트 발생
                window.dispatchEvent(new Event("loginStatusChange"));
            }

            alert("로그인에 성공했습니다!");
            navigate("/");
        } catch (err) {
            console.error("로그인 오류:", err);
            setErrorMessage("서버 오류가 발생했습니다. 다시 시도해주세요.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="login-container">
            <div className="login-box">
                <h2>로그인</h2>
                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label>아이디</label>
                        <input
                            type="text"
                            name="userId"
                            value={formData.userId}
                            onChange={handleChange}
                            required
                        />
                        {errors.userId && (
                            <span className="error-text">{errors.userId}</span>
                        )}
                    </div>

                    <div className="form-group">
                        <label>비밀번호</label>
                        <input
                            type="password"
                            name="userPassword"
                            value={formData.userPassword}
                            onChange={handleChange}
                            required
                        />
                        {errors.userPassword && (
                            <span className="error-text">{errors.userPassword}</span>
                        )}
                    </div>

                    {errorMessage && <div className="error-message">{errorMessage}</div>}

                    <button type="submit" className="login-button" disabled={loading}>
                        {loading ? "로그인 중..." : "로그인"}
                    </button>
                </form>

                <div className="login-link">
                    계정이 없으신가요?{" "}
                    <a
                        href="/signup"
                        onClick={(e) => {
                            e.preventDefault();
                            navigate("/signup");
                        }}
                    >
                        회원가입
                    </a>
                </div>
            </div>
        </div>
    );
};

export default LoginPage;