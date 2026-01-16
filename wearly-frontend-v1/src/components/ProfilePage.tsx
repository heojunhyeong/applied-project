import { useState } from "react";
import { User } from "lucide-react";

interface ProfileData {
  userId: string;
  email: string;
  nickname: string;
  introduction: string;
  phoneNumber: string;
  profileImage: string | null;
}

interface PasswordData {
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
}

export default function ProfilePage() {
  // Profile data
  const [profile, setProfile] = useState<ProfileData>({
    userId: "user12345",
    email: "user@example.com",
    nickname: "bomi123",
    introduction:
      "Fashion enthusiast who loves streetwear and vintage styles",
    phoneNumber: "010-1234-5678",
    profileImage: null,
  });

  // Edit states for each field
  const [editingPassword, setEditingPassword] = useState(false);
  const [editingEmail, setEditingEmail] = useState(false);
  const [editingNickname, setEditingNickname] = useState(false);
  const [editingIntroduction, setEditingIntroduction] =
    useState(false);
  const [editingPhoneNumber, setEditingPhoneNumber] =
    useState(false);

  // Temporary values during editing
  const [tempEmail, setTempEmail] = useState(profile.email);
  const [tempNickname, setTempNickname] = useState(
    profile.nickname,
  );
  const [tempIntroduction, setTempIntroduction] = useState(
    profile.introduction,
  );
  const [tempPhoneNumber, setTempPhoneNumber] = useState(
    profile.phoneNumber,
  );

  // Password editing values
  const [passwordData, setPasswordData] =
    useState<PasswordData>({
      currentPassword: "",
      newPassword: "",
      confirmPassword: "",
    });

  // Handle Edit/Cancel for Password
  const handleEditPassword = () => {
    if (editingPassword) {
      // Cancel - reset password fields
      setPasswordData({
        currentPassword: "",
        newPassword: "",
        confirmPassword: "",
      });
      setEditingPassword(false);
    } else {
      // Edit
      setEditingPassword(true);
    }
  };

  // Handle Save Password
  const handleSavePassword = () => {
    // Validate and save password (in real app)
    alert("Password updated successfully");
    setPasswordData({
      currentPassword: "",
      newPassword: "",
      confirmPassword: "",
    });
    setEditingPassword(false);
  };

  // Handle Cancel Password
  const handleCancelPassword = () => {
    setPasswordData({
      currentPassword: "",
      newPassword: "",
      confirmPassword: "",
    });
    setEditingPassword(false);
  };

  // Handle Edit/Cancel for other fields
  const handleEditEmail = () => {
    if (editingEmail) {
      setTempEmail(profile.email);
      setEditingEmail(false);
    } else {
      setEditingEmail(true);
    }
  };

  const handleEditNickname = () => {
    if (editingNickname) {
      setTempNickname(profile.nickname);
      setEditingNickname(false);
    } else {
      setEditingNickname(true);
    }
  };

  const handleEditIntroduction = () => {
    if (editingIntroduction) {
      setTempIntroduction(profile.introduction);
      setEditingIntroduction(false);
    } else {
      setEditingIntroduction(true);
    }
  };

  const handleEditPhoneNumber = () => {
    if (editingPhoneNumber) {
      setTempPhoneNumber(profile.phoneNumber);
      setEditingPhoneNumber(false);
    } else {
      setEditingPhoneNumber(true);
    }
  };

  // Handle Save (profile fields only, NOT password)
  const handleSave = () => {
    setProfile({
      ...profile,
      email: tempEmail,
      nickname: tempNickname,
      introduction: tempIntroduction,
      phoneNumber: tempPhoneNumber,
    });

    // Exit edit mode for all fields
    setEditingEmail(false);
    setEditingNickname(false);
    setEditingIntroduction(false);
    setEditingPhoneNumber(false);

    alert("Profile updated successfully");
  };

  // Handle Exit
  const handleExit = () => {
    // Reset all temp values
    setTempEmail(profile.email);
    setTempNickname(profile.nickname);
    setTempIntroduction(profile.introduction);
    setTempPhoneNumber(profile.phoneNumber);

    // Exit edit mode for all fields
    setEditingEmail(false);
    setEditingNickname(false);
    setEditingIntroduction(false);
    setEditingPhoneNumber(false);
    setEditingPassword(false);
  };

  // Handle profile image actions
  const handleChangePhoto = () => {
    alert("Change Photo functionality (UI only)");
  };

  const handleDeletePhoto = () => {
    alert("Delete Photo functionality (UI only)");
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-4xl mx-auto px-6 py-8">
        {/* Page Title */}
        <div className="mb-8">
          <h1 className="text-2xl text-gray-900">My Profile</h1>
          <p className="text-sm text-gray-600 mt-2">
            Manage your personal information
          </p>
        </div>

        {/* Profile Card */}
        <div className="bg-white border border-gray-200 rounded-lg overflow-hidden">
          {/* Section 1: Profile Image */}
          <div className="p-8 border-b border-gray-200">
            <div className="flex flex-col items-center">
              {/* Profile Image */}
              <div className="w-28 h-28 rounded-full bg-gray-200 flex items-center justify-center overflow-hidden mb-4">
                {profile.profileImage ? (
                  <img
                    src={profile.profileImage}
                    alt="Profile"
                    className="w-full h-full object-cover"
                  />
                ) : (
                  <User className="w-14 h-14 text-gray-400" />
                )}
              </div>
              {/* Image Action Buttons */}
              <div className="flex gap-3">
                <button
                  onClick={handleChangePhoto}
                  className="px-4 py-2 text-sm border border-gray-300 rounded-md hover:bg-gray-50 transition-colors"
                >
                  Change Photo
                </button>
                <button
                  onClick={handleDeletePhoto}
                  className="px-4 py-2 text-sm border border-gray-300 text-red-600 rounded-md hover:bg-red-50 transition-colors"
                >
                  Delete
                </button>
              </div>
            </div>
          </div>

          {/* Section 2: User Identity (Read-Only) */}
          <div className="p-6 border-b border-gray-200 bg-gray-50">
            <div className="flex items-center gap-6">
              <div className="w-32 flex-shrink-0">
                <label className="text-sm text-gray-600">
                  User ID
                </label>
              </div>
              <div className="flex-1 min-w-0">
                <p className="text-sm text-gray-900">
                  {profile.userId}
                </p>
              </div>
              <div className="w-24 flex-shrink-0"></div>
            </div>
          </div>

          {/* Section 4: Password (Separate Logic) */}
          <div className="p-6 border-b border-gray-200">
            <div className="flex items-start gap-6">
              <div className="w-32 flex-shrink-0">
                <label className="text-sm text-gray-700">
                  Password
                </label>
              </div>
              <div className="flex-1 min-w-0">
                <p className="text-sm text-gray-900 py-2">
                  ••••••••
                </p>

                {/* Password Edit Section (Expanded) */}
                {editingPassword && (
                  <div className="mt-4 space-y-4 pt-4 border-t border-gray-200">
                    <div>
                      <label className="text-xs text-gray-600 mb-1 block">
                        Current Password
                      </label>
                      <input
                        type="password"
                        value={passwordData.currentPassword}
                        onChange={(e) =>
                          setPasswordData({
                            ...passwordData,
                            currentPassword: e.target.value,
                          })
                        }
                        className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent text-sm"
                        placeholder="Enter current password"
                      />
                    </div>
                    <div>
                      <label className="text-xs text-gray-600 mb-1 block">
                        New Password
                      </label>
                      <input
                        type="password"
                        value={passwordData.newPassword}
                        onChange={(e) =>
                          setPasswordData({
                            ...passwordData,
                            newPassword: e.target.value,
                          })
                        }
                        className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent text-sm"
                        placeholder="Enter new password"
                      />
                    </div>
                    <div>
                      <label className="text-xs text-gray-600 mb-1 block">
                        Confirm New Password
                      </label>
                      <input
                        type="password"
                        value={passwordData.confirmPassword}
                        onChange={(e) =>
                          setPasswordData({
                            ...passwordData,
                            confirmPassword: e.target.value,
                          })
                        }
                        className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent text-sm"
                        placeholder="Confirm new password"
                      />
                    </div>
                    <div className="flex gap-3">
                      <button
                        onClick={handleSavePassword}
                        className="px-5 py-2 text-sm bg-gray-900 text-white rounded-md hover:bg-gray-800 transition-colors"
                      >
                        Save Password
                      </button>
                      <button
                        onClick={handleCancelPassword}
                        className="px-5 py-2 text-sm border border-gray-300 rounded-md hover:bg-gray-50 transition-colors"
                      >
                        Cancel
                      </button>
                    </div>
                  </div>
                )}
              </div>
              <div className="w-24 flex-shrink-0 flex justify-end">
                {!editingPassword && (
                  <button
                    onClick={handleEditPassword}
                    className="px-4 py-2 text-sm border border-gray-300 rounded-md hover:bg-gray-50 transition-colors"
                  >
                    Edit
                  </button>
                )}
              </div>
            </div>
          </div>

          {/* Section 3: Profile Information (Editable) */}
          {/* Email Field */}
          <div className="p-6 border-b border-gray-100">
            <div className="flex items-start gap-6">
              <div className="w-32 flex-shrink-0">
                <label className="text-sm text-gray-700">
                  Email <span className="text-red-500">*</span>
                </label>
              </div>
              <div className="flex-1 min-w-0">
                {editingEmail ? (
                  <div>
                    <input
                      type="email"
                      value={tempEmail}
                      onChange={(e) =>
                        setTempEmail(e.target.value)
                      }
                      className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent text-sm"
                      placeholder="Enter email"
                    />
                    <div className="mt-2 text-xs text-gray-500 space-y-1">
                      <p>• Required</p>
                      <p>• Max 30 characters</p>
                      <p>• Must be valid email format</p>
                      <p>• Must not contain the word "admin"</p>
                    </div>
                  </div>
                ) : (
                  <p className="text-sm text-gray-900 py-2">
                    {profile.email}
                  </p>
                )}
              </div>
              <div className="w-24 flex-shrink-0 flex justify-end">
                <button
                  onClick={handleEditEmail}
                  className="px-4 py-2 text-sm border border-gray-300 rounded-md hover:bg-gray-50 transition-colors"
                >
                  {editingEmail ? "Cancel" : "Edit"}
                </button>
              </div>
            </div>
          </div>

          {/* Nickname Field */}
          <div className="p-6 border-b border-gray-100">
            <div className="flex items-start gap-6">
              <div className="w-32 flex-shrink-0">
                <label className="text-sm text-gray-700">
                  Nickname{" "}
                  <span className="text-red-500">*</span>
                </label>
              </div>
              <div className="flex-1 min-w-0">
                {editingNickname ? (
                  <div>
                    <input
                      type="text"
                      value={tempNickname}
                      onChange={(e) =>
                        setTempNickname(e.target.value)
                      }
                      className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent text-sm"
                      placeholder="Enter nickname"
                    />
                    <div className="mt-2 text-xs text-gray-500 space-y-1">
                      <p>• Required</p>
                      <p>• Max 12 characters</p>
                      <p>• Must not contain the word "admin"</p>
                    </div>
                  </div>
                ) : (
                  <p className="text-sm text-gray-900 py-2">
                    {profile.nickname}
                  </p>
                )}
              </div>
              <div className="w-24 flex-shrink-0 flex justify-end">
                <button
                  onClick={handleEditNickname}
                  className="px-4 py-2 text-sm border border-gray-300 rounded-md hover:bg-gray-50 transition-colors"
                >
                  {editingNickname ? "Cancel" : "Edit"}
                </button>
              </div>
            </div>
          </div>

          {/* Phone Number Field */}
          <div className="p-6 border-b border-gray-100">
            <div className="flex items-start gap-6">
              <div className="w-32 flex-shrink-0">
                <label className="text-sm text-gray-700">
                  Phone Number
                </label>
              </div>
              <div className="flex-1 min-w-0">
                {editingPhoneNumber ? (
                  <div>
                    <input
                      type="text"
                      value={tempPhoneNumber}
                      onChange={(e) =>
                        setTempPhoneNumber(e.target.value)
                      }
                      className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent text-sm"
                      placeholder="010-1234-5678"
                    />
                    <div className="mt-2 text-xs text-gray-500 space-y-1">
                      <p>• Max 20 characters</p>
                      <p>
                        • Phone format validation (Korean phone
                        numbers)
                      </p>
                    </div>
                  </div>
                ) : (
                  <p className="text-sm text-gray-900 py-2">
                    {profile.phoneNumber}
                  </p>
                )}
              </div>
              <div className="w-24 flex-shrink-0 flex justify-end">
                <button
                  onClick={handleEditPhoneNumber}
                  className="px-4 py-2 text-sm border border-gray-300 rounded-md hover:bg-gray-50 transition-colors"
                >
                  {editingPhoneNumber ? "Cancel" : "Edit"}
                </button>
              </div>
            </div>
          </div>

          {/* Introduction Field */}
          <div className="p-6 border-b border-gray-100">
            <div className="flex items-start gap-6">
              <div className="w-32 flex-shrink-0">
                <label className="text-sm text-gray-700">
                  Introduction
                </label>
              </div>
              <div className="flex-1 min-w-0">
                {editingIntroduction ? (
                  <div>
                    <textarea
                      value={tempIntroduction}
                      onChange={(e) =>
                        setTempIntroduction(e.target.value)
                      }
                      rows={3}
                      className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent text-sm resize-none"
                      placeholder="Tell us about yourself"
                    />
                    <div className="mt-2 text-xs text-gray-500">
                      <p>• Max 255 characters</p>
                    </div>
                  </div>
                ) : (
                  <p className="text-sm text-gray-900 py-2">
                    {profile.introduction || "No introduction"}
                  </p>
                )}
              </div>
              <div className="w-24 flex-shrink-0 flex justify-end">
                <button
                  onClick={handleEditIntroduction}
                  className="px-4 py-2 text-sm border border-gray-300 rounded-md hover:bg-gray-50 transition-colors"
                >
                  {editingIntroduction ? "Cancel" : "Edit"}
                </button>
              </div>
            </div>
          </div>

          {/* Bottom Actions */}
          <div className="p-6 bg-gray-50">
            <div className="flex items-center justify-end gap-3">
              <button
                onClick={handleExit}
                className="px-6 py-2.5 text-sm border border-gray-300 rounded-md hover:bg-white transition-colors"
              >
                Exit
              </button>
              <button
                onClick={handleSave}
                className="px-6 py-2.5 text-sm bg-gray-900 text-white rounded-md hover:bg-gray-800 transition-colors"
              >
                Save
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}