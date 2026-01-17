import { useState, type FormEvent } from 'react';
import { Link, useNavigate, useSearchParams } from 'react-router';
import { Input } from './ui/input';
import { Label } from './ui/label';
import { ArrowLeft, AlertCircle, CheckCircle2 } from 'lucide-react';

const API_BASE_URL = 'http://localhost:8080'; // TODO: 환경변수로 관리

export default function ResetPasswordPage() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const token = searchParams.get('token');

  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [passwordError, setPasswordError] = useState('');
  const [loading, setLoading] = useState(false);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);
  
  // 토큰이 없으면 유효하지 않음
  const isTokenValid = !!token;

  // Handle reset password
  const handleResetPassword = async (e: FormEvent) => {
    e.preventDefault();
    setPasswordError('');
    setErrorMsg(null);
    
    // Validate password
    if (!newPassword) {
      setPasswordError('비밀번호는 필수입니다.');
      return;
    }
    if (newPassword.length < 8) {
      setPasswordError('비밀번호는 최소 8자 이상이어야 합니다.');
      return;
    }
    if (newPassword !== confirmPassword) {
      setPasswordError('비밀번호가 일치하지 않습니다.');
      return;
    }
    
    if (!token) {
      setPasswordError('유효하지 않은 링크입니다.');
      return;
    }

    try {
      setLoading(true);

      const response = await fetch(`${API_BASE_URL}/api/password/reset`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          token: token,
          newPassword: newPassword,
        }),
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => null);
        throw new Error(errorData?.message || '비밀번호 재설정에 실패했습니다.');
      }

      // 성공 시 성공 페이지로 이동
      navigate('/reset-password/success');
    } catch (err) {
      setErrorMsg(err instanceof Error ? err.message : '비밀번호 재설정 처리 중 오류가 발생했습니다.');
    } finally {
      setLoading(false);
    }
  };

  // If token is invalid or expired
  if (!isTokenValid) {
    return (
      <div className="min-h-[calc(100vh-80px)] bg-gray-50 py-12 px-4">
        <div className="max-w-md mx-auto">
          <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-8">
            <div className="text-center mb-6">
              <div className="inline-flex items-center justify-center w-16 h-16 bg-red-100 rounded-full mb-4">
                <AlertCircle className="w-8 h-8 text-red-600" />
              </div>
              <h1 className="text-2xl text-gray-900 mb-2">
                유효하지 않은 링크입니다
              </h1>
              <p className="text-sm text-gray-600">
                이 재설정 링크는 유효하지 않거나 만료되었습니다.
              </p>
            </div>

            <div className="space-y-4">
              {/* Request New Link Button */}
              <Link
                to="/forgot-password"
                className="block w-full h-12 bg-gray-900 text-white text-center leading-[3rem] rounded-md hover:bg-gray-800 transition-colors"
              >
                새 링크 요청하기
              </Link>

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
        </div>
      </div>
    );
  }

  // Valid token - show password reset form
  return (
    <div className="min-h-[calc(100vh-80px)] bg-gray-50 py-12 px-4">
      <div className="max-w-md mx-auto">
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-8">
          {/* Token Verified Badge */}
          <div className="flex items-center justify-center gap-2 mb-6">
            <CheckCircle2 className="w-5 h-5 text-green-600" />
            <span className="text-sm text-green-600 font-medium">링크 확인됨</span>
          </div>

          <h1 className="text-2xl text-gray-900 mb-2 text-center">
            새 비밀번호 설정
          </h1>
          <p className="text-sm text-gray-600 text-center mb-8">
            새로운 비밀번호를 입력해주세요.
          </p>

          {errorMsg && (
            <div className="mb-4 rounded-md border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
              {errorMsg}
            </div>
          )}

          <form onSubmit={handleResetPassword} className="space-y-6">
            {/* New Password Field */}
            <div className="space-y-2">
              <Label htmlFor="newPassword" className="text-gray-900">
                새 비밀번호
              </Label>
              <Input
                id="newPassword"
                type="password"
                placeholder="새 비밀번호를 입력하세요"
                value={newPassword}
                onChange={(e) => {
                  setNewPassword(e.target.value);
                  setPasswordError('');
                  setErrorMsg(null);
                }}
                className={`w-full h-11 px-4 bg-gray-50 border rounded-md focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent ${
                  passwordError ? 'border-red-500' : 'border-gray-300'
                }`}
              />
            </div>

            {/* Confirm Password Field */}
            <div className="space-y-2">
              <Label htmlFor="confirmPassword" className="text-gray-900">
                새 비밀번호 확인
              </Label>
              <Input
                id="confirmPassword"
                type="password"
                placeholder="새 비밀번호를 다시 입력하세요"
                value={confirmPassword}
                onChange={(e) => {
                  setConfirmPassword(e.target.value);
                  setPasswordError('');
                  setErrorMsg(null);
                }}
                className={`w-full h-11 px-4 bg-gray-50 border rounded-md focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent ${
                  passwordError ? 'border-red-500' : 'border-gray-300'
                }`}
              />
            </div>

            {/* Password Rules Hint */}
            <div className="bg-gray-50 border border-gray-200 rounded-md p-4">
              <p className="text-xs text-gray-600">
                비밀번호는 최소 8자 이상이어야 합니다.
              </p>
            </div>

            {/* Error Message */}
            {passwordError && (
              <div className="bg-red-50 border border-red-200 rounded-md p-4">
                <p className="text-sm text-red-600">{passwordError}</p>
              </div>
            )}

            {/* Reset Password Button */}
            <button
              type="submit"
              disabled={loading}
              className="w-full h-12 bg-gray-900 text-white rounded-md hover:bg-gray-800 transition-colors mt-8 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {loading ? '처리 중...' : '비밀번호 재설정'}
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
      </div>
    </div>
  );
}
