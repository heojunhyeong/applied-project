import { useState, type ChangeEvent, type FormEvent } from "react";
import { Link, useNavigate } from "react-router";
import { Input } from "./ui/input";
import { Label } from "./ui/label";
import { RadioGroup, RadioGroupItem } from "./ui/radio-group";

type UserType = "user" | "seller";

export default function SignUpPage() {
  const navigate = useNavigate();

  const [form, setForm] = useState({
    userId: "",
    userPassword: "",
    confirmPassword: "",
    userEmail: "",
    nickName: "",
    userType: "user" as UserType,
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

    // 프론트 1차 검증 (안내 문구는 정중한 서술형)
    if (!form.userId.trim()) return setErrorMsg("아이디는 필수입니다.");
    if (!form.userPassword) return setErrorMsg("비밀번호는 필수입니다.");
    if (!form.confirmPassword) return setErrorMsg("비밀번호 재확인은 필수입니다.");
    if (form.userPassword !== form.confirmPassword)
      return setErrorMsg("비밀번호와 비밀번호 재확인이 일치하지 않습니다.");
    if (!form.userEmail.trim()) return setErrorMsg("이메일은 필수입니다.");
    if (!form.nickName.trim()) return setErrorMsg("닉네임은 필수입니다.");

    // ✅ 백엔드 SignupRequest DTO 필드명 그대로 맞춤
    const payload = {
      userId: form.userId,
      userPassword: form.userPassword,
      confirmPassword: form.confirmPassword,
      userEmail: form.userEmail,
      nickName: form.nickName,
      roleType: form.userType === "seller" ? "SELLER" : "USER",
    };

    try {
      setLoading(true);

      const res = await fetch("/api/users/signup", {
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
        // 백엔드 validation 메시지/에러 포맷 대응
        const msg =
          data?.message ||
          data?.error ||
          data?.errors?.[0]?.defaultMessage ||
          data?.errors?.[0]?.message ||
          `회원가입에 실패했습니다. (HTTP ${res.status})`;
        throw new Error(msg);
      }

      // 성공 안내 후 이동
      alert("회원가입이 완료되었습니다. 로그인 페이지로 이동합니다.");
      navigate("/login");
    } catch (err: any) {
      setErrorMsg(err?.message ?? "회원가입 처리 중 오류가 발생했습니다.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-[calc(100vh-80px)] bg-gray-50 py-12 px-4">
      <div className="max-w-md mx-auto">
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-8">
          <h1 className="text-2xl text-gray-900 mb-8 text-center">
            Wearly 회원가입
          </h1>

          {errorMsg && (
            <div className="mb-4 rounded-md border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
              {errorMsg}
            </div>
          )}

          <form className="space-y-6" onSubmit={onSubmit}>
            <div className="space-y-2">
              <Label htmlFor="userId" className="text-gray-900">
                아이디
              </Label>
              <Input
                id="userId"
                type="text"
                value={form.userId}
                onChange={onChange("userId")}
                placeholder="아이디를 입력하세요"
                className="w-full h-11 px-4 bg-gray-50 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent"
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="userPassword" className="text-gray-900">
                비밀번호
              </Label>
              <Input
                id="userPassword"
                type="password"
                value={form.userPassword}
                onChange={onChange("userPassword")}
                placeholder="비밀번호를 입력하세요"
                className="w-full h-11 px-4 bg-gray-50 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent"
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="confirmPassword" className="text-gray-900">
                비밀번호 확인
              </Label>
              <Input
                id="confirmPassword"
                type="password"
                value={form.confirmPassword}
                onChange={onChange("confirmPassword")}
                placeholder="비밀번호를 다시 입력하세요"
                className="w-full h-11 px-4 bg-gray-50 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent"
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="userEmail" className="text-gray-900">
                이메일
              </Label>
              <Input
                id="userEmail"
                type="email"
                value={form.userEmail}
                onChange={onChange("userEmail")}
                placeholder="example@email.com"
                className="w-full h-11 px-4 bg-gray-50 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent"
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="nickName" className="text-gray-900">
                닉네임
              </Label>
              <Input
                id="nickName"
                type="text"
                value={form.nickName}
                onChange={onChange("nickName")}
                placeholder="닉네임을 입력하세요"
                className="w-full h-11 px-4 bg-gray-50 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent"
              />
            </div>

            <div className="space-y-3">
              <Label className="text-gray-900">회원 유형</Label>
              <RadioGroup
                value={form.userType}
                onValueChange={(v) =>
                  setForm((prev) => ({ ...prev, userType: v as UserType }))
                }
                className="flex gap-4"
              >
                <div className="flex items-center space-x-2 flex-1">
                  <RadioGroupItem value="user" id="user" />
                  <Label htmlFor="user" className="text-gray-700 cursor-pointer font-normal">
                    구매자
                  </Label>
                </div>
                <div className="flex items-center space-x-2 flex-1">
                  <RadioGroupItem value="seller" id="seller" />
                  <Label htmlFor="seller" className="text-gray-700 cursor-pointer font-normal">
                    판매자
                  </Label>
                </div>
              </RadioGroup>
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full h-12 bg-gray-900 text-white rounded-md hover:bg-gray-800 transition-colors mt-8 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {loading ? "가입 중..." : "회원가입"}
            </button>

            <div className="text-center mt-6">
              <p className="text-sm text-gray-600">
                이미 계정이 있으신가요?{" "}
                <Link to="/login" className="text-gray-900 hover:underline font-medium">
                  로그인
                </Link>
              </p>
              <p className="text-sm text-gray-600 mt-2">
                <Link to="/forgot-password" className="text-gray-600 hover:text-gray-900 hover:underline">
                  비밀번호를 잊으셨나요?
                </Link>
              </p>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}
