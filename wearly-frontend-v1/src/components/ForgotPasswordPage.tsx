import { useState } from 'react';
import { Link } from 'react-router';
import { Input } from './ui/input';
import { Label } from './ui/label';
import { ArrowLeft, Mail } from 'lucide-react';

export default function ForgotPasswordPage() {
  const [email, setEmail] = useState('');
  const [emailError, setEmailError] = useState('');
  const [emailSent, setEmailSent] = useState(false);

  // Email validation
  const validateEmail = (email: string) => {
    if (!email) {
      return 'Email is required';
    }
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
      return 'Invalid email format';
    }
    return '';
  };

  // Handle send reset link
  const handleSendResetLink = (e: React.FormEvent) => {
    e.preventDefault();
    const error = validateEmail(email);
    if (error) {
      setEmailError(error);
      return;
    }
    setEmailError('');
    setEmailSent(true);
  };

  // Handle resend link
  const handleResendLink = () => {
    const error = validateEmail(email);
    if (error) {
      setEmailError(error);
      return;
    }
    setEmailError('');
    alert('Reset link has been resent to your email.');
  };

  return (
    <div className="min-h-[calc(100vh-80px)] bg-gray-50 py-12 px-4">
      <div className="max-w-md mx-auto">
        {!emailSent ? (
          // Email Input State
          <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-8">
            <h1 className="text-2xl text-gray-900 mb-2 text-center">
              Forgot your password?
            </h1>
            <p className="text-sm text-gray-600 text-center mb-8">
              Enter your email and we'll send you a reset link.
            </p>

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
                className="w-full h-12 bg-gray-900 text-white rounded-md hover:bg-gray-800 transition-colors mt-8"
              >
                Send Reset Link
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
        ) : (
          // Email Sent Confirmation State
          <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-8">
            <div className="text-center mb-6">
              <div className="inline-flex items-center justify-center w-16 h-16 bg-blue-100 rounded-full mb-4">
                <Mail className="w-8 h-8 text-blue-600" />
              </div>
              <h1 className="text-2xl text-gray-900 mb-2">
                Check your email
              </h1>
              <p className="text-sm text-gray-600">
                If an account exists for <span className="font-medium text-gray-900">{email}</span>, a reset link has been sent.
              </p>
            </div>

            <div className="space-y-4">
              {/* Resend Link Button */}
              <button
                onClick={handleResendLink}
                className="w-full h-12 bg-white text-gray-900 border-2 border-gray-300 rounded-md hover:bg-gray-50 transition-colors"
              >
                Resend Link
              </button>

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
        )}
      </div>
    </div>
  );
}
