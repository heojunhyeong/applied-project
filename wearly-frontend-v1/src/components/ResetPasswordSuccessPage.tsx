import { Link } from 'react-router';
import { CheckCircle } from 'lucide-react';

export default function ResetPasswordSuccessPage() {
  return (
    <div className="min-h-[calc(100vh-80px)] bg-gray-50 py-12 px-4">
      <div className="max-w-md mx-auto">
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-8">
          <div className="text-center">
            {/* Success Icon */}
            <div className="inline-flex items-center justify-center w-16 h-16 bg-green-100 rounded-full mb-6">
              <CheckCircle className="w-10 h-10 text-green-600" />
            </div>
            
            {/* Title */}
            <h1 className="text-2xl text-gray-900 mb-2">
              Password updated
            </h1>

            {/* Message */}
            <p className="text-sm text-gray-600 mb-2">
              Your password has been successfully changed.
            </p>

            {/* Additional Info */}
            <p className="text-xs text-gray-500 mb-8">
              You can now log in with your new password.
            </p>

            {/* Go to Login Button */}
            <Link
              to="/login"
              className="block w-full h-12 bg-gray-900 text-white text-center leading-[3rem] rounded-md hover:bg-gray-800 transition-colors"
            >
              Go to Login
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
}
