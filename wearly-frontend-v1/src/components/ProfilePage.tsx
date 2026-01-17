import { ChangeEvent, useEffect, useRef, useState } from "react";
import { User } from "lucide-react";
import AdminLayout from "./admin/AdminLayout";
import {
  fetchProfile,
  ProfileFormState,
  ProfileResponse,
  requestProfilePresignedUrl,
  updateProfile,
  updateProfileImage,
} from "../api/profile";
import { getAccessToken, getRoleFromToken, UserRole } from "../utils/auth";

export default function ProfilePage() {
  const [profile, setProfile] = useState<ProfileResponse | null>(null);
  const [form, setForm] = useState<ProfileFormState>({
    nickname: "",
    introduction: "",
    phoneNumber: "",
    imageUrl: null,
  });

  const [isEditing, setIsEditing] = useState(false);
  const [loadingProfile, setLoadingProfile] = useState(false);
  const [saving, setSaving] = useState(false);
  const [imageUploading, setImageUploading] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [role, setRole] = useState<UserRole | null>(null);

  const fileInputRef = useRef<HTMLInputElement | null>(null);

  // 프로필 데이터를 폼 상태로 변환
  const toFormState = (data: ProfileResponse): ProfileFormState => ({
    nickname: data.userNickname ?? "",
    introduction: data.introduction ?? "",
    phoneNumber: data.phoneNumber ?? "",
    imageUrl: data.imageUrl ?? null,
  });

  const isAdmin = role === "ADMIN";

  // 프로필 조회
  const loadProfile = async (resolvedRole?: UserRole) => {
    const token = getAccessToken();
    const roleFromToken = resolvedRole ?? getRoleFromToken(token);
    if (!token || !roleFromToken) {
      setErrorMessage("로그인이 필요합니다.");
      setProfile(null);
      return;
    }

    setRole(roleFromToken);
    setLoadingProfile(true);
    setErrorMessage(null);
    try {
      const data = await fetchProfile(roleFromToken);
      setProfile(data);
      setForm(toFormState(data));
    } catch (e: any) {
      setErrorMessage(e.message ?? "프로필 조회 중 오류");
    } finally {
      setLoadingProfile(false);
    }
  };

  useEffect(() => {
    loadProfile();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // 편집 시작
  const handleStartEdit = () => {
    if (!profile || !role) return;
    setForm(toFormState(profile));
    setIsEditing(true);
  };

  // 편집 취소
  const handleCancelEdit = () => {
    if (!profile || !role) return;
    setForm(toFormState(profile));
    setIsEditing(false);
  };

  // 프로필 저장
  const handleSave = async () => {
    if (!profile || !role) return;

    setSaving(true);
    setErrorMessage(null);
    try {
      const updated = await updateProfile(role, form);
      setProfile(updated);
      setForm(toFormState(updated));
      setIsEditing(false);
    } catch (e: any) {
      setErrorMessage(e.message ?? "프로필 수정 중 오류");
    } finally {
      setSaving(false);
    }
  };

  // 프로필 이미지 변경 버튼 처리
  const handleChangePhoto = () => {
    if (saving || imageUploading) return;
    fileInputRef.current?.click();
  };

  // 프로필 이미지 파일 선택
  const handlePhotoSelected = async (event: ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (!file || !role) return;

    setImageUploading(true);
    setErrorMessage(null);

    try {
      const { presignedUrl, key, fileUrl, path } =
        await requestProfilePresignedUrl(role, file.type);

      await fetch(presignedUrl, {
        method: "PUT",
        headers: {
          "Content-Type": file.type,
        },
        body: file,
      });

      const uploadedUrl =
        fileUrl || path || key || presignedUrl.split("?")[0];

      const updated = await updateProfileImage(role, uploadedUrl);
      setProfile(updated);
      setForm(toFormState(updated));
    } catch (e: any) {
      setErrorMessage(e.message ?? "프로필 이미지 업로드 실패");
    } finally {
      setImageUploading(false);
      event.target.value = "";
    }
  };

  // 프로필 이미지 삭제
  const handleDeletePhoto = async () => {
    if (!role) return;
    setImageUploading(true);
    setErrorMessage(null);

    try {
      const updated = await updateProfileImage(role, null);
      setProfile(updated);
      setForm(toFormState(updated));
    } catch (e: any) {
      setErrorMessage(e.message ?? "프로필 이미지 삭제 실패");
    } finally {
      setImageUploading(false);
    }
  };

  if (loadingProfile) {
    return isAdmin ? (
      <AdminLayout>
        <div className="p-8">
          <p className="text-gray-600">Loading profile...</p>
        </div>
      </AdminLayout>
    ) : (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <p className="text-gray-600">Loading profile...</p>
      </div>
    );
  }

  if (!profile) {
    const emptyState = (
      <div className="text-center">
        <p className="text-gray-700 mb-4">
          {errorMessage || "프로필 정보를 불러오지 못했습니다."}
        </p>
        <button
          onClick={() => loadProfile(role ?? undefined)}
          className="px-4 py-2 text-sm border border-gray-300 rounded-md hover:bg-gray-50 transition-colors"
        >
          Retry
        </button>
      </div>
    );

    return isAdmin ? (
      <AdminLayout>
        <div className="p-8">{emptyState}</div>
      </AdminLayout>
    ) : (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        {emptyState}
      </div>
    );
  }

  const currentImageUrl = isEditing ? form.imageUrl : profile.imageUrl;
  const profileCard = (
    <div className="bg-white border border-gray-200 rounded-lg overflow-hidden">
          {/* Profile Image */}
          <div className="p-8 border-b border-gray-200">
            <div className="flex flex-col items-center">
              <div className="w-28 h-28 rounded-full bg-gray-200 flex items-center justify-center overflow-hidden mb-4">
                {currentImageUrl ? (
                  <img
                    src={currentImageUrl}
                    alt="Profile"
                    className="w-full h-full object-cover"
                  />
                ) : (
                  <User className="w-14 h-14 text-gray-400" />
                )}
              </div>

              {isEditing && (
                <div className="flex gap-3">
                  <button
                    onClick={handleChangePhoto}
                    className="px-4 py-2 text-sm border border-gray-300 rounded-md hover:bg-gray-50 transition-colors"
                    disabled={saving || imageUploading}
                  >
                    {imageUploading ? "Uploading..." : "Change Photo"}
                  </button>
                  <button
                    onClick={handleDeletePhoto}
                    className="px-4 py-2 text-sm border border-gray-300 text-red-600 rounded-md hover:bg-red-50 transition-colors"
                    disabled={saving || imageUploading}
                  >
                    Delete
                  </button>
                </div>
              )}
              <input
                ref={fileInputRef}
                type="file"
                accept="image/*"
                className="hidden"
                onChange={handlePhotoSelected}
              />
            </div>
          </div>

          {/* Read-Only Info */}
          <div className="p-6 border-b border-gray-200 bg-gray-50 space-y-4">
            <div className="flex items-center gap-6">
              <div className="w-32 flex-shrink-0">
                <label className="text-sm text-gray-600">ID</label>
              </div>
              <div className="flex-1 min-w-0">
                <p className="text-sm text-gray-900">{profile.id}</p>
              </div>
            </div>

            <div className="flex items-center gap-6">
              <div className="w-32 flex-shrink-0">
                <label className="text-sm text-gray-600">Name</label>
              </div>
              <div className="flex-1 min-w-0">
                <p className="text-sm text-gray-900">
                  {profile.userName || "-"}
                </p>
              </div>
            </div>

            <div className="flex items-center gap-6">
              <div className="w-32 flex-shrink-0">
                <label className="text-sm text-gray-600">Email</label>
              </div>
              <div className="flex-1 min-w-0">
                <p className="text-sm text-gray-900">
                  {profile.userEmail || "-"}
                </p>
              </div>
            </div>
          </div>

          {/* Editable: Nickname */}
          <div className="p-6 border-b border-gray-100">
            <div className="flex items-start gap-6">
              <div className="w-32 flex-shrink-0">
                <label className="text-sm text-gray-700">
                  Nickname <span className="text-red-500">*</span>
                </label>
              </div>

              <div className="flex-1 min-w-0">
                {isEditing ? (
                  <input
                    type="text"
                    value={form.nickname}
                    onChange={(e) =>
                      setForm((prev) => ({
                        ...prev,
                        nickname: e.target.value,
                      }))
                    }
                    className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent text-sm"
                    placeholder="Enter nickname"
                    disabled={saving}
                  />
                ) : (
                  <p className="text-sm text-gray-900 py-2">
                    {profile.userNickname || "-"}
                  </p>
                )}

                {isEditing && (
                  <div className="mt-2 text-xs text-gray-500 space-y-1">
                    <p>• Required</p>
                    <p>• Max 12 characters</p>
                    <p>• Must not contain the word "admin"</p>
                  </div>
                )}
              </div>
            </div>
          </div>

          {/* Editable: Phone */}
          <div className="p-6 border-b border-gray-100">
            <div className="flex items-start gap-6">
              <div className="w-32 flex-shrink-0">
                <label className="text-sm text-gray-700">Phone Number</label>
              </div>

              <div className="flex-1 min-w-0">
                {isEditing ? (
                  <input
                    type="text"
                    value={form.phoneNumber}
                    onChange={(e) =>
                      setForm((prev) => ({
                        ...prev,
                        phoneNumber: e.target.value,
                      }))
                    }
                    className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent text-sm"
                    placeholder="010-1234-5678"
                    disabled={saving}
                  />
                ) : (
                  <p className="text-sm text-gray-900 py-2">
                    {profile.phoneNumber || "-"}
                  </p>
                )}
              </div>
            </div>
          </div>

          {/* Editable: Introduction */}
          <div className="p-6 border-b border-gray-100">
            <div className="flex items-start gap-6">
              <div className="w-32 flex-shrink-0">
                <label className="text-sm text-gray-700">Introduction</label>
              </div>

              <div className="flex-1 min-w-0">
                {isEditing ? (
                  <textarea
                    value={form.introduction}
                    onChange={(e) =>
                      setForm((prev) => ({
                        ...prev,
                        introduction: e.target.value,
                      }))
                    }
                    rows={3}
                    className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent text-sm resize-none"
                    placeholder="Tell us about yourself"
                    disabled={saving}
                  />
                ) : (
                  <p className="text-sm text-gray-900 py-2">
                    {profile.introduction || "No introduction"}
                  </p>
                )}

                {isEditing && (
                  <div className="mt-2 text-xs text-gray-500">
                    <p>• Max 255 characters</p>
                  </div>
                )}
              </div>
            </div>
          </div>

          {/* Bottom Actions */}
          {isEditing && (
            <div className="p-6 bg-gray-50">
              <div className="flex items-center justify-end gap-3">
                <button
                  onClick={handleCancelEdit}
                  className="px-6 py-2.5 text-sm border border-gray-300 rounded-md hover:bg-white transition-colors"
                  disabled={saving}
                >
                  Exit
                </button>
                <button
                  onClick={handleSave}
                  className="px-6 py-2.5 text-sm bg-gray-900 text-white rounded-md hover:bg-gray-800 transition-colors"
                  disabled={saving}
                >
                  {saving ? "Saving..." : "Save"}
                </button>
              </div>
            </div>
          )}
        </div>
  );

  // 페이지 레이아웃 구성
  return isAdmin ? (
    <AdminLayout>
      <div className="p-8">
        {/* Page Header */}
        <div className="mb-8 flex items-center justify-between">
          <div>
            <h1 className="text-2xl font-semibold text-gray-900">My Profile</h1>
            <p className="text-sm text-gray-600 mt-2">
              Manage admin profile information
            </p>
            {errorMessage && (
              <p className="text-xs text-red-600 mt-2">{errorMessage}</p>
            )}
          </div>

          {!isEditing ? (
            <button
              onClick={handleStartEdit}
              className="px-4 py-2 text-sm border border-gray-300 rounded-md hover:bg-gray-50 transition-colors"
            >
              Edit
            </button>
          ) : (
            <button
              onClick={handleCancelEdit}
              className="px-4 py-2 text-sm border border-gray-300 rounded-md hover:bg-gray-50 transition-colors"
              disabled={saving}
            >
              Cancel
            </button>
          )}
        </div>

        {profileCard}
      </div>
    </AdminLayout>
  ) : (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-4xl mx-auto px-6 py-8">
        {/* Page Title */}
        <div className="mb-8 flex items-center justify-between">
          <div>
            <h1 className="text-2xl text-gray-900">My Profile</h1>
            <p className="text-sm text-gray-600 mt-2">
              Manage your personal information
            </p>
            {errorMessage && (
              <p className="text-xs text-red-600 mt-2">{errorMessage}</p>
            )}
          </div>

          {!isEditing ? (
            <button
              onClick={handleStartEdit}
              className="px-4 py-2 text-sm border border-gray-300 rounded-md hover:bg-gray-50 transition-colors"
            >
              Edit
            </button>
          ) : (
            <button
              onClick={handleCancelEdit}
              className="px-4 py-2 text-sm border border-gray-300 rounded-md hover:bg-gray-50 transition-colors"
              disabled={saving}
            >
              Cancel
            </button>
          )}
        </div>

        {profileCard}
      </div>
    </div>
  );
}
