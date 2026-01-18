import { useState, type FormEvent } from 'react';
import { Link } from 'react-router';
import { Input } from './ui/input';
import { Label } from './ui/label';
import { ArrowLeft, Mail } from 'lucide-react';

const API_BASE_URL = ''; // TODO: 환경변수로 관리

export default function ForgotPasswordPage() {
  const [email, setEmail] = useState('');
  const [emailError, setEmailError] = useState('');
  const [emailSent, setEmailSent] = useState(false);
  const [loading, setLoading] = useState(false);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);

  // Email validation
  const validateEmail = (email: string) => {
    if (!email) {
      return '이메일은 필수입니다.';
    }
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
      return '올바른 이메일 형식이 아닙니다.';
    }
    return '';
  };

  // Handle send reset link
  const handleSendResetLink = async (e: FormEvent) => {
    e.preventDefault();
    const error = validateEmail(email);
    if (error) {
      setEmailError(error);
      return;
    }
    setEmailError('');
    setErrorMsg(null);

    try {
      setLoading(true);

      const response = await fetch(`${API_BASE_URL}/api/password/reset/request`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ email }),
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => null);
        throw new Error(errorData?.message || '비밀번호 재설정 요청에 실패했습니다.');
      }

      setEmailSent(true);
    } catch (err) {
      setErrorMsg(err instanceof Error ? err.message : '비밀번호 재설정 요청 처리 중 오류가 발생했습니다.');
    } finally {
      setLoading(false);
    }
  };

  // Handle resend link
  const handleResendLink = async () => {
    const error = validateEmail(email);
    if (error) {
      setEmailError(error);
      return;
    }
    setEmailError('');
    setErrorMsg(null);

    try {
      setLoading(true);

      const response = await fetch(`${API_BASE_URL}/api/password/reset/request`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ email }),
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => null);
        throw new Error(errorData?.message || '재전송에 실패했습니다.');
      }

      alert('비밀번호 재설정 링크가 다시 전송되었습니다.');
    } catch (err) {
      setErrorMsg(err instanceof Error ? err.message : '재전송 처리 중 오류가 발생했습니다.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-[calc(100vh-80px)] bg-gray-50 py-12 px-4">
      <div className="max-w-md mx-auto">
        {!emailSent ? (
          // Email Input State
          <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-8">
            <h1 className="text-2xl text-gray-900 mb-2 text-center">
              비밀번호를 잊으셨나요?
            </h1>
            <p className="text-sm text-gray-600 text-center mb-8">
              이메일을 입력하시면 비밀번호 재설정 링크를 보내드립니다.
            </p>

            {errorMsg && (
              <div className="mb-4 rounded-md border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
                {errorMsg}
              </div>
            )}

            <form onSubmit={handleSendResetLink} className="space-y-6">
              {/* Email Field */}
              <div className="space-y-2">
                <Label htmlFor="email" className="text-gray-900">
                  Email
                </Label>
                <Input
                  id="email"
                  type="email"
                  placeholder="you@example.com"
                  value={email}
                  onChange={(e) => {
                    setEmail(e.target.value);
                    setEmailError('');
                    setErrorMsg(null);
                  }}
                  className={`w-full h-11 px-4 bg-gray-50 border rounded-md focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent ${
                    emailError ? 'border-red-500' : 'border-gray-300'
                  }`}
                />
                {emailError && (
                  <p className="text-sm text-red-600">{emailError}</p>
                )}
              </div>

              {/* Send Reset Link Button */}
              <button
                type="submit"
                disabled={loading}
                className="w-full h-12 bg-gray-900 text-white rounded-md hover:bg-gray-800 transition-colors mt-8 disabled:opacity-50 disabled:cursor-not-allowed"
              >
                {loading ? '전송 중...' : '재설정 링크 전송'}
              </button>

              {/* Back to Login */}
              <div className="text-center mt-4">
                <Link
                  to="/login"
                  className="text-sm text-gray-600 hover:text-gray-900 hover:underline inline-flex items-center gap-2"
                >
                  <ArrowLeft className="w-4 h-4" />
                  로그인으로 돌아가기
                </Link>
              </div>
            </form>
          </div>
        ) : (
          // Email Sent Confirmation State
          <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-8">
            <div className="text-center mb-6">
              <div className="inline-flex items-center justify-center w-16 h-16 bg-blue-100 rounded-full mb-4">
                <Mail className="w-8 h-8 text-blue-600" />
              </div>
              <h1 className="text-2xl text-gray-900 mb-2">
                이메일을 확인해주세요
              </h1>
              <p className="text-sm text-gray-600">
                <span className="font-medium text-gray-900">{email}</span>로 등록된 계정이 있다면, 비밀번호 재설정 링크를 보내드렸습니다.
              </p>
            </div>

            {errorMsg && (
              <div className="mb-4 rounded-md border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
                {errorMsg}
              </div>
            )}

            <div className="space-y-4">
              {/* Resend Link Button */}
              <button
                onClick={handleResendLink}
                disabled={loading}
                className="w-full h-12 bg-white text-gray-900 border-2 border-gray-300 rounded-md hover:bg-gray-50 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
              >
                {loading ? '재전송 중...' : '링크 다시 보내기'}
              </button>

              {/* Back to Login */}
              <div className="text-center pt-2">
                <Link
                  to="/login"
                  className="text-sm text-gray-600 hover:text-gray-900 hover:underline inline-flex items-center gap-2"
                >
                  <ArrowLeft className="w-4 h-4" />
                  로그인으로 돌아가기
                </Link>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
