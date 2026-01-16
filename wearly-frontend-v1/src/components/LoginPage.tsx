import { Link } from 'react-router';
import { Input } from './ui/input';
import { Label } from './ui/label';

export default function LoginPage() {
  return (
    <div className="min-h-[calc(100vh-80px)] bg-gray-50 py-12 px-4">
      <div className="max-w-md mx-auto">
        {/* Card Container */}
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-8">
          {/* Title */}
          <h1 className="text-2xl text-gray-900 mb-8 text-center">
            Login to Wearly
          </h1>

          {/* Login Form */}
          <form className="space-y-6">
            {/* ID Field */}
            <div className="space-y-2">
              <Label htmlFor="username" className="text-gray-900">
                ID
              </Label>
              <Input
                id="username"
                type="text"
                placeholder="Enter your username"
                className="w-full h-11 px-4 bg-gray-50 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent"
              />
            </div>

            {/* Password Field */}
            <div className="space-y-2">
              <Label htmlFor="password" className="text-gray-900">
                Password
              </Label>
              <Input
                id="password"
                type="password"
                placeholder="Enter your password"
                className="w-full h-11 px-4 bg-gray-50 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent"
              />
            </div>

            {/* Forgot Password Link */}
            <div className="text-right">
              <Link
                to="/forgot-password"
                className="text-sm text-gray-600 hover:text-gray-900 hover:underline"
              >
                Forgot your password?
              </Link>
            </div>

            {/* Login Button */}
            <button
              type="submit"
              className="w-full h-12 bg-gray-900 text-white rounded-md hover:bg-gray-800 transition-colors mt-8"
            >
              Login
            </button>

            {/* Sign Up Link */}
            <div className="text-center mt-6">
              <p className="text-sm text-gray-600">
                Don't have an account?{' '}
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
