import { ChangeEvent, useEffect, useRef, useState } from "react";
import { User } from "lucide-react";
import AdminLayout from "./admin/AdminLayout";
import SellerLayout from "./seller/SellerLayout"; // // SELLER 레이아웃 추가
import {
  fetchProfile,
  ProfileFormState,
  ProfileResponse,
  requestProfilePresignedUrl,
  updateProfile,
  updateProfileImage,
} from "../api/profile";
import { getAccessToken, getRoleFromToken, UserRole } from "../utils/auth";
import { downloadCoupon } from "../api/coupon";

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
  const [showCouponModal, setShowCouponModal] = useState(false);
  const [couponLoading, setCouponLoading] = useState(false);
  const [couponError, setCouponError] = useState<string | null>(null);

  const fileInputRef = useRef<HTMLInputElement | null>(null);

  // // 프로필 데이터를 폼 상태로 변환
  const toFormState = (data: ProfileResponse): ProfileFormState => ({
    nickname: data.userNickname ?? "",
    introduction: data.introduction ?? "",
    phoneNumber: data.phoneNumber ?? "",
    imageUrl: data.imageUrl ?? null,
  });

  const isAdmin = role === "ADMIN";
  const isSeller = role === "SELLER"; // // SELLER 여부
  const isUser = role === "USER";

  // // 프로필 조회
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

  // // 편집 시작
  const handleStartEdit = () => {
    if (!profile || !role) return;
    setForm(toFormState(profile));
    setIsEditing(true);
  };

  // // 편집 취소
  const handleCancelEdit = () => {
    if (!profile || !role) return;
    setForm(toFormState(profile));
    setIsEditing(false);
  };

  // // 프로필 저장
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

  // // 프로필 이미지 변경 버튼 처리
  const handleChangePhoto = () => {
    if (saving || imageUploading) return;
    fileInputRef.current?.click();
  };

  // // 프로필 이미지 파일 선택
  const handlePhotoSelected = async (event: ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (!file || !role || !profile) return;

    setImageUploading(true);
    setErrorMessage(null);

    try {
      // // 1. Presigned URL 요청
      const { presignedUrl } = await requestProfilePresignedUrl(role, file.type);

      // // 2. S3에 파일 업로드
      const uploadResponse = await fetch(presignedUrl, {
        method: "PUT",
        headers: {
          "Content-Type": file.type,
        },
        body: file,
      });

      if (!uploadResponse.ok) {
        throw new Error(`S3 업로드 실패: ${uploadResponse.status}`);
      }

      // // 3. 업로드된 이미지의 실제 URL(쿼리 제거)
      const uploadedUrl = presignedUrl.split("?")[0];

      // // 4. 프로필 이미지 URL 업데이트
      const updated = await updateProfileImage(
        role,
        uploadedUrl,
        role === "USER" && profile.userNickname ? profile.userNickname : undefined
      );

      setProfile(updated);
      setForm(toFormState(updated));
    } catch (e: any) {
      if (e.status === 401 || e.status === 403) {
        setErrorMessage("인증에 실패했습니다. 다시 로그인해주세요.");
      } else {
        setErrorMessage(e.message ?? "프로필 이미지 업로드 실패");
      }
    } finally {
      setImageUploading(false);
      event.target.value = "";
    }
  };

  // // 프로필 이미지 삭제
  const handleDeletePhoto = async () => {
    if (!role || !profile) return;

    setImageUploading(true);
    setErrorMessage(null);

    try {
      const updated = await updateProfileImage(
        role,
        null,
        role === "USER" ? profile.userNickname : undefined
      );

      setProfile(updated);
      setForm(toFormState(updated));
    } catch (e: any) {
      setErrorMessage(e.message ?? "프로필 이미지 삭제 실패");
    } finally {
      setImageUploading(false);
    }
  };

  // // 쿠폰 발급
  const handleDownloadCoupon = async (benefitId: number) => {
    setCouponLoading(true);
    setCouponError(null);

    try {
      await downloadCoupon(benefitId);
      setShowCouponModal(false);
      alert("쿠폰이 발급되었습니다!");
    } catch (e: any) {
      setCouponError(e.message ?? "쿠폰 발급 중 오류가 발생했습니다.");
    } finally {
      setCouponLoading(false);
    }
  };

  // =========================
  // // 로딩 상태 UI
  // =========================
  if (loadingProfile) {
    // // ADMIN / SELLER는 대시보드 레이아웃으로 로딩 표시
    if (isAdmin) {
      return (
        <AdminLayout>
          <div className="p-8">
            <p className="text-gray-600">Loading profile...</p>
          </div>
        </AdminLayout>
      );
    }

    if (isSeller) {
      return (
        <SellerLayout>
          <div className="p-8">
            <p className="text-gray-600">Loading profile...</p>
          </div>
        </SellerLayout>
      );
    }

    // // USER는 기존 그대로
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <p className="text-gray-600">Loading profile...</p>
      </div>
    );
  }

  // =========================
  // // 프로필 없을 때 UI
  // =========================
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

    if (isAdmin) {
      return (
        <AdminLayout>
          <div className="p-8">{emptyState}</div>
        </AdminLayout>
      );
    }

    if (isSeller) {
      return (
        <SellerLayout>
          <div className="p-8">{emptyState}</div>
        </SellerLayout>
      );
    }

    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        {emptyState}
      </div>
    );
  }

  // =========================
  // // 프로필 카드 UI(공통)
  // =========================
  const currentImageUrl = isEditing ? form.imageUrl : profile.imageUrl;

  const profileCard = (
    <div className="bg-white border border-gray-200 rounded-lg overflow-hidden">
      {/* // Profile Image */}
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

      {/* // Read-Only Info */}
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
            <p className="text-sm text-gray-900">{profile.userName || "-"}</p>
          </div>
        </div>

        <div className="flex items-center gap-6">
          <div className="w-32 flex-shrink-0">
            <label className="text-sm text-gray-600">Email</label>
          </div>
          <div className="flex-1 min-w-0">
            <p className="text-sm text-gray-900">{profile.userEmail || "-"}</p>
          </div>
        </div>
      </div>

      {/* // Editable: Nickname */}
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

      {/* // Editable: Phone */}
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

      {/* // Editable: Introduction */}
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

      {/* // Coupon */}
      {isUser && (
        <div className="p-6 border-b border-gray-100">
          <div className="flex items-start gap-6">
            <div className="w-32 flex-shrink-0">
              <label className="text-sm text-gray-700">Coupon</label>
            </div>

            <div className="flex-1 min-w-0">
              <button
                onClick={() => setShowCouponModal(true)}
                className="px-4 py-2 text-sm bg-gray-900 text-white rounded-md hover:bg-gray-800 transition-colors"
              >
                쿠폰 발급받기
              </button>
            </div>
          </div>
        </div>
      )}

      {/* // Bottom Actions */}
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

  const couponModal =
    isUser &&
    showCouponModal && (
      <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
        <div className="bg-white rounded-lg p-6 max-w-md w-full mx-4">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-xl font-semibold text-gray-900">쿠폰 발급</h2>
            <button
              onClick={() => {
                setShowCouponModal(false);
                setCouponError(null);
              }}
              className="text-gray-400 hover:text-gray-600"
              disabled={couponLoading}
            >
              <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>

          <p className="text-sm text-gray-600 mb-6">발급받을 쿠폰을 선택해주세요.</p>

          {couponError && (
            <div className="mb-4 p-3 bg-red-50 border border-red-200 rounded-md">
              <p className="text-sm text-red-600">{couponError}</p>
            </div>
          )}

          <div className="space-y-3">
            <button
              onClick={() => handleDownloadCoupon(1)}
              disabled={couponLoading}
              className="w-full px-4 py-3 text-left border border-gray-300 rounded-md hover:bg-gray-50 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <div className="font-semibold text-gray-900">10% 할인 쿠폰</div>
              <div className="text-sm text-gray-600 mt-1">
                최소 주문금액: 10,000원 이상
              </div>
            </button>

            <button
              onClick={() => handleDownloadCoupon(2)}
              disabled={couponLoading}
              className="w-full px-4 py-3 text-left border border-gray-300 rounded-md hover:bg-gray-50 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <div className="font-semibold text-gray-900">5,000원 할인 쿠폰</div>
              <div className="text-sm text-gray-600 mt-1">
                최소 주문금액: 30,000원 이상
              </div>
            </button>
          </div>

          {couponLoading && (
            <div className="mt-4 text-center">
              <p className="text-sm text-gray-600">쿠폰 발급 중...</p>
            </div>
          )}
        </div>
      </div>
    );

  // =========================
  // // 공통 헤더 영역 (ADMIN/SELLER 같이 쓰게)
  // =========================
  const pageHeader = (
    <div className="mb-8 flex items-center justify-between">
      <div>
        <h1 className="text-2xl font-semibold text-gray-900">My Profile</h1>
        <p className="text-sm text-gray-600 mt-2">
          {isAdmin
            ? "Manage admin profile information"
            : isSeller
            ? "Manage seller profile information"
            : "Manage your personal information"}
        </p>
        {errorMessage && <p className="text-xs text-red-600 mt-2">{errorMessage}</p>}
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
  );

  // =========================
  // // 최종 렌더링: ADMIN / SELLER / USER 분기
  // =========================
  if (isAdmin) {
    return (
      <AdminLayout>
        <div className="p-8">
          {pageHeader}
          {profileCard}
          {couponModal}
        </div>
      </AdminLayout>
    );
  }

  if (isSeller) {
    return (
      <SellerLayout>
        <div className="p-8">
          {pageHeader}
          {profileCard}
          {couponModal}
        </div>
      </SellerLayout>
    );
  }

  // // USER는 기존 UI 유지
  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-4xl mx-auto px-6 py-8">
        {pageHeader}
        {profileCard}
        {couponModal}
      </div>
    </div>
  );
}
