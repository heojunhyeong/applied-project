import { Link } from 'react-router';
import { Input } from './ui/input';
import { Label } from './ui/label';
import { RadioGroup, RadioGroupItem } from './ui/radio-group';

export default function SignUpPage() {
  return (
    <div className="min-h-[calc(100vh-80px)] bg-gray-50 py-12 px-4">
      <div className="max-w-md mx-auto">
        {/* Card Container */}
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-8">
          {/* Title */}
          <h1 className="text-2xl text-gray-900 mb-8 text-center">
            Create your Wearly account
          </h1>

          {/* Sign Up Form */}
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

            {/* Confirm Password Field */}
            <div className="space-y-2">
              <Label htmlFor="confirmPassword" className="text-gray-900">
                Confirm Password
              </Label>
              <Input
                id="confirmPassword"
                type="password"
                placeholder="Re-enter your password"
                className="w-full h-11 px-4 bg-gray-50 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent"
              />
            </div>

            {/* Email Field */}
            <div className="space-y-2">
              <Label htmlFor="email" className="text-gray-900">
                Email
              </Label>
              <Input
                id="email"
                type="email"
                placeholder="example@email.com"
                className="w-full h-11 px-4 bg-gray-50 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent"
              />
            </div>

            {/* Nickname Field */}
            <div className="space-y-2">
              <Label htmlFor="nickname" className="text-gray-900">
                Nickname
              </Label>
              <Input
                id="nickname"
                type="text"
                placeholder="Enter your nickname"
                className="w-full h-11 px-4 bg-gray-50 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent"
              />
            </div>

            {/* User Type Selection */}
            <div className="space-y-3">
              <Label className="text-gray-900">User Type</Label>
              <RadioGroup defaultValue="user" className="flex gap-4">
                <div className="flex items-center space-x-2 flex-1">
                  <RadioGroupItem value="user" id="user" />
                  <Label
                    htmlFor="user"
                    className="text-gray-700 cursor-pointer font-normal"
                  >
                    User
                  </Label>
                </div>
                <div className="flex items-center space-x-2 flex-1">
                  <RadioGroupItem value="seller" id="seller" />
                  <Label
                    htmlFor="seller"
                    className="text-gray-700 cursor-pointer font-normal"
                  >
                    Seller
                  </Label>
                </div>
              </RadioGroup>
            </div>

            {/* Sign Up Button */}
            <button
              type="submit"
              className="w-full h-12 bg-gray-900 text-white rounded-md hover:bg-gray-800 transition-colors mt-8"
            >
              Sign Up
            </button>

            {/* Login Link */}
            <div className="text-center mt-6">
              <p className="text-sm text-gray-600">
                Already have an account?{' '}
                <Link
                  to="/login"
                  className="text-gray-900 hover:underline font-medium"
                >
                  Login
                </Link>
              </p>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}
