import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./SignupPage.css";

const SignupPage = () => {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        userId: "",
        userPassword: "",
        confirmPassword: "",
        userEmail: "",
        nickName: "",
        roleType: "USER",
    });
    const [errors, setErrors] = useState({});
    const [loading, setLoading] = useState(false);
    const [errorMessage, setErrorMessage] = useState("");

    const handleChange = (e) => {
        const { name, value } = e.target;

        // 길이 제한 적용 (User 엔티티 기준)
        let processedValue = value;
        if (name === "userId" && value.length > 30) {
            processedValue = value.slice(0, 30);
        } else if (name === "userEmail" && value.length > 30) {
            processedValue = value.slice(0, 30);
        } else if (name === "nickName" && value.length > 12) {
            processedValue = value.slice(0, 12);
        }

        setFormData((prev) => ({
            ...prev,
            [name]: processedValue,
        }));

        if (errors[name]) {
            setErrors((prev) => ({
                ...prev,
                [name]: "",
            }));
        }
        setErrorMessage("");
    };

    const validateForm = () => {
        const newErrors = {};

        // 아이디 검증 (필수, 최대 30자)
        if (!formData.userId || formData.userId.trim() === "") {
            newErrors.userId = "아이디는 필수입니다";
        } else if (formData.userId.length > 30) {
            newErrors.userId = "아이디는 30자 이하여야 합니다";
        }

        // 비밀번호 검증 (필수)
        if (!formData.userPassword || formData.userPassword.trim() === "") {
            newErrors.userPassword = "비밀번호는 필수입니다";
        } else if (formData.userPassword.length < 8) {
            newErrors.userPassword = "비밀번호는 최소 8자 이상이어야 합니다";
        }

        // 비밀번호 재확인 검증 (필수)
        if (!formData.confirmPassword || formData.confirmPassword.trim() === "") {
            newErrors.confirmPassword = "비밀번호 재확인은 필수입니다";
        } else if (formData.userPassword !== formData.confirmPassword) {
            newErrors.confirmPassword = "비밀번호가 일치하지 않습니다";
        }

        // 이메일 검증 (필수, 최대 30자)
        if (!formData.userEmail || formData.userEmail.trim() === "") {
            newErrors.userEmail = "이메일은 필수입니다";
        } else if (formData.userEmail.length > 30) {
            newErrors.userEmail = "이메일은 30자 이하여야 합니다";
        } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.userEmail)) {
            newErrors.userEmail = "올바른 이메일 형식이 아닙니다";
        }

        // 닉네임 검증 (필수, 최대 12자)
        if (!formData.nickName || formData.nickName.trim() === "") {
            newErrors.nickName = "닉네임은 필수입니다";
        } else if (formData.nickName.length > 12) {
            newErrors.nickName = "닉네임은 12자 이하여야 합니다";
        }

        // 회원 유형 검증 (필수)
        if (!formData.roleType) {
            newErrors.roleType = "회원 유형을 선택해주세요";
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        // 클라이언트 측 검증
        if (!validateForm()) {
            return;
        }

        setLoading(true);
        setErrorMessage("");

        try {
            const response = await fetch("/api/users/signup", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    userId: formData.userId.trim(),
                    userPassword: formData.userPassword,
                    confirmPassword: formData.confirmPassword,
                    userEmail: formData.userEmail.trim(),
                    nickName: formData.nickName.trim(),
                    roleType: formData.roleType,
                }),
            });

            const data = await response.json();

            if (!response.ok) {
                if (response.status === 400) {
                    if (data.error) {
                        setErrorMessage(data.message || "회원가입에 실패했습니다.");
                    } else {
                        // Validation 에러 처리
                        setErrors(data);
                    }
                } else {
                    setErrorMessage(data.message || "회원가입에 실패했습니다.");
                }
                setLoading(false);
                return;
            }

            alert("회원가입이 완료되었습니다!");
            navigate("/login");
        } catch (err) {
            console.error("회원가입 오류:", err);
            setErrorMessage("서버 오류가 발생했습니다. 다시 시도해주세요.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="signup-container">
            <div className="signup-box">
                <h2>회원가입</h2>
                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label>
                            아이디 <span className="required">*</span>
                        </label>
                        <input
                            type="text"
                            name="userId"
                            value={formData.userId}
                            onChange={handleChange}
                            maxLength={30}
                            placeholder="최대 30자"
                            required
                        />
                        {errors.userId && (
                            <span className="error-text">{errors.userId}</span>
                        )}
                        <span className="char-count">{formData.userId.length}/30</span>
                    </div>

                    <div className="form-group">
                        <label>
                            비밀번호 <span className="required">*</span>
                        </label>
                        <input
                            type="password"
                            name="userPassword"
                            value={formData.userPassword}
                            onChange={handleChange}
                            placeholder="최소 8자, 특수문자 포함"
                            required
                        />
                        {errors.userPassword && (
                            <span className="error-text">{errors.userPassword}</span>
                        )}
                    </div>

                    <div className="form-group">
                        <label>
                            비밀번호 재확인 <span className="required">*</span>
                        </label>
                        <input
                            type="password"
                            name="confirmPassword"
                            value={formData.confirmPassword}
                            onChange={handleChange}
                            placeholder="비밀번호를 다시 입력하세요"
                            required
                        />
                        {errors.confirmPassword && (
                            <span className="error-text">{errors.confirmPassword}</span>
                        )}
                    </div>

                    <div className="form-group">
                        <label>
                            이메일 <span className="required">*</span>
                        </label>
                        <input
                            type="email"
                            name="userEmail"
                            value={formData.userEmail}
                            onChange={handleChange}
                            maxLength={30}
                            placeholder="최대 30자"
                            required
                        />
                        {errors.userEmail && (
                            <span className="error-text">{errors.userEmail}</span>
                        )}
                        <span className="char-count">{formData.userEmail.length}/30</span>
                    </div>

                    <div className="form-group">
                        <label>
                            닉네임 <span className="required">*</span>
                        </label>
                        <input
                            type="text"
                            name="nickName"
                            value={formData.nickName}
                            onChange={handleChange}
                            maxLength={12}
                            placeholder="최대 12자"
                            required
                        />
                        {errors.nickName && (
                            <span className="error-text">{errors.nickName}</span>
                        )}
                        <span className="char-count">{formData.nickName.length}/12</span>
                    </div>

                    <div className="form-group">
                        <label>
                            회원 유형 <span className="required">*</span>
                        </label>
                        <select
                            name="roleType"
                            value={formData.roleType}
                            onChange={handleChange}
                            required
                        >
                            <option value="USER">일반 사용자</option>
                            <option value="SELLER">판매자</option>
                        </select>
                        {errors.roleType && (
                            <span className="error-text">{errors.roleType}</span>
                        )}
                    </div>

                    {errorMessage && <div className="error-message">{errorMessage}</div>}

                    <button type="submit" className="signup-button" disabled={loading}>
                        {loading ? "처리 중..." : "회원가입"}
                    </button>
                </form>

                <div className="signup-link">
                    이미 계정이 있으신가요?{" "}
                    <a
                        href="/login"
                        onClick={(e) => {
                            e.preventDefault();
                            navigate("/login");
                        }}
                    >
                        로그인
                    </a>
                </div>
            </div>
        </div>
    );
};

export default SignupPage;