import { useState, type FormEvent } from 'react';
import { Link, useNavigate, useSearchParams } from 'react-router';
import { Input } from './ui/input';
import { Label } from './ui/label';
import { ArrowLeft, AlertCircle, CheckCircle2 } from 'lucide-react';

export default function ResetPasswordPage() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const token = searchParams.get('token');

  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [passwordError, setPasswordError] = useState('');
  
  // Simulate token validation (UI only - for demo, tokens starting with "valid" are considered valid)
  const isTokenValid = token && token.startsWith('valid');

  // Handle reset password
  const handleResetPassword = (e: FormEvent) => {
    e.preventDefault();
    
    // Validate password
    if (!newPassword) {
      setPasswordError('Password is required');
      return;
    }
    if (newPassword.length < 8) {
      setPasswordError('Password must be at least 8 characters');
      return;
    }
    if (newPassword !== confirmPassword) {
      setPasswordError('Passwords do not match');
      return;
    }
    
    setPasswordError('');
    // Navigate to success page
    navigate('/reset-password/success');
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
                Invalid reset link
              </h1>
              <p className="text-sm text-gray-600">
                This reset link is invalid or expired.
              </p>
            </div>

            <div className="space-y-4">
              {/* Request New Link Button */}
              <Link
                to="/forgot-password"
                className="block w-full h-12 bg-gray-900 text-white text-center leading-[3rem] rounded-md hover:bg-gray-800 transition-colors"
              >
                Request New Link
              </Link>

              {/* Back to Login */}
              <div className="text-center pt-2">
                <Link
                  to="/login"
                  className="text-sm text-gray-600 hover:text-gray-900 hover:underline inline-flex items-center gap-2"
                >
                  <ArrowLeft className="w-4 h-4" />
                  Back to Login
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
            <span className="text-sm text-green-600 font-medium">Reset link verified</span>
          </div>

          <h1 className="text-2xl text-gray-900 mb-2 text-center">
            Set a new password
          </h1>
          <p className="text-sm text-gray-600 text-center mb-8">
            Enter your new password below.
          </p>

          <form onSubmit={handleResetPassword} className="space-y-6">
            {/* New Password Field */}
            <div className="space-y-2">
              <Label htmlFor="newPassword" className="text-gray-900">
                New Password
              </Label>
              <Input
                id="newPassword"
                type="password"
                placeholder="Enter new password"
                value={newPassword}
                onChange={(e) => {
                  setNewPassword(e.target.value);
                  setPasswordError('');
                }}
                className={`w-full h-11 px-4 bg-gray-50 border rounded-md focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent ${
                  passwordError ? 'border-red-500' : 'border-gray-300'
                }`}
              />
            </div>

            {/* Confirm Password Field */}
            <div className="space-y-2">
              <Label htmlFor="confirmPassword" className="text-gray-900">
                Confirm New Password
              </Label>
              <Input
                id="confirmPassword"
                type="password"
                placeholder="Confirm new password"
                value={confirmPassword}
                onChange={(e) => {
                  setConfirmPassword(e.target.value);
                  setPasswordError('');
                }}
                className={`w-full h-11 px-4 bg-gray-50 border rounded-md focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent ${
                  passwordError ? 'border-red-500' : 'border-gray-300'
                }`}
              />
            </div>

            {/* Password Rules Hint */}
            <div className="bg-gray-50 border border-gray-200 rounded-md p-4">
              <p className="text-xs text-gray-600">
                Password must be at least 8 characters long
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
              className="w-full h-12 bg-gray-900 text-white rounded-md hover:bg-gray-800 transition-colors mt-8"
            >
              Reset Password
            </button>

            {/* Back to Login */}
            <div className="text-center mt-4">
              <Link
                to="/login"
                className="text-sm text-gray-600 hover:text-gray-900 hover:underline inline-flex items-center gap-2"
              >
                <ArrowLeft className="w-4 h-4" />
                Back to Login
              </Link>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}
