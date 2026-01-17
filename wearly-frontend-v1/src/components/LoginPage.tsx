import { useState, type ChangeEvent, type FormEvent } from "react";
import { Link, useNavigate } from "react-router";
import { Input } from "./ui/input";
import { Label } from "./ui/label";

export default function LoginPage() {
  const navigate = useNavigate();

  const [form, setForm] = useState({
    userId: "",
    userPassword: "",
  });

  const [loading, setLoading] = useState(false);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);

  const onChange =
    (key: keyof typeof form) => (e: ChangeEvent<HTMLInputElement>) => {
      setForm((prev) => ({ ...prev, [key]: e.target.value }));
    };

  const onSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setErrorMsg(null);

    // 1) 프론트 1차 검증
    if (!form.userId.trim()) {
      setErrorMsg("아이디는 필수입니다.");
      return;
    }
    if (!form.userPassword) {
      setErrorMsg("비밀번호는 필수입니다.");
      return;
    }

    // 2) 백엔드 요청 DTO에 맞춤
    const payload = {
      userId: form.userId,
      userPassword: form.userPassword,
    };

    try {
      setLoading(true);

      const res = await fetch("/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      });

      const text = await res.text();
      let data: any = null;
      try {
        data = text ? JSON.parse(text) : null;
      } catch {
        // JSON이 아닐 수 있으므로 무시
      }

      if (!res.ok) {
        const msg =
          data?.message ||
          data?.error ||
          data?.errors?.[0]?.defaultMessage ||
          data?.errors?.[0]?.message ||
          `로그인에 실패했습니다. (HTTP ${res.status})`;
        throw new Error(msg);
      }

      // ✅ A안: 응답에 accessToken/refreshToken/role이 내려온다고 가정
      // 백엔드 응답 필드명이 다르면 여기만 바꾸면 된다.
      const accessToken = data?.accessToken;
      const refreshToken = data?.refreshToken;
      const role = data?.role; // "USER" | "SELLER" | "ADMIN"

      if (!accessToken || !role) {
        throw new Error("로그인 응답에 토큰 또는 권한 정보가 포함되어 있지 않습니다.");
      }

      localStorage.setItem("accessToken", accessToken);
      if (refreshToken) localStorage.setItem("refreshToken", refreshToken);
      localStorage.setItem("role", role);

      alert("로그인이 완료되었습니다.");
      // 헤더 즉시 반영 목적: 새로고침 포함 이동
      window.location.href = "/";
      // 또는 SPA 이동만 원하면 아래 사용:
      // navigate("/");
    } catch (err: any) {
      setErrorMsg(err?.message ?? "로그인 처리 중 오류가 발생했습니다.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-[calc(100vh-80px)] bg-gray-50 py-12 px-4">
      <div className="max-w-md mx-auto">
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-8">
          <h1 className="text-2xl text-gray-900 mb-8 text-center">
            Login to Wearly
          </h1>

          {errorMsg && (
            <div className="mb-4 rounded-md border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
              {errorMsg}
            </div>
          )}

          <form className="space-y-6" onSubmit={onSubmit}>
            <div className="space-y-2">
              <Label htmlFor="userId" className="text-gray-900">
                ID
              </Label>
              <Input
                id="userId"
                type="text"
                value={form.userId}
                onChange={onChange("userId")}
                placeholder="Enter your username"
                className="w-full h-11 px-4 bg-gray-50 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent"
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="userPassword" className="text-gray-900">
                Password
              </Label>
              <Input
                id="userPassword"
                type="password"
                value={form.userPassword}
                onChange={onChange("userPassword")}
                placeholder="Enter your password"
                className="w-full h-11 px-4 bg-gray-50 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent"
              />
            </div>

            <div className="text-right">
              <Link
                to="/forgot-password"
                className="text-sm text-gray-600 hover:text-gray-900 hover:underline"
              >
                Forgot your password?
              </Link>
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full h-12 bg-gray-900 text-white rounded-md hover:bg-gray-800 transition-colors mt-8 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {loading ? "Logging in..." : "Login"}
            </button>

            <div className="text-center mt-6">
              <p className="text-sm text-gray-600">
                Don&apos;t have an account?{" "}
                <Link
                  to="/signup"
                  className="text-gray-900 hover:underline font-medium"
                >
                  Sign Up
                </Link>
              </p>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}
