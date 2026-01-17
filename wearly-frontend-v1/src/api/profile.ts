import { apiFetch } from "./http";
import { UserRole } from "../utils/auth";

export type ProfileResponse = {
  id: number;
  userName: string;
  userEmail: string;
  userNickname: string;
  introduction?: string | null;
  phoneNumber?: string | null;
  imageUrl?: string | null;
  createdDate?: string;
  updatedDate?: string;
};

export type ProfileFormState = {
  nickname: string;
  introduction: string;
  phoneNumber: string;
  imageUrl: string | null;
};

type PresignedUrlResponse = {
  presignedUrl: string;
  key?: string;
  fileUrl?: string;
  path?: string;
};

const PROFILE_ENDPOINTS: Record<UserRole, string> = {
  USER: "/api/users/profile",
  SELLER: "/api/seller/profile",
  ADMIN: "/api/admin/profile",
};

const getBaseEndpoint = (role: UserRole) => PROFILE_ENDPOINTS[role];

// 프로필 조회 API
export const fetchProfile = async (role: UserRole) => {
  const url = getBaseEndpoint(role);
  return apiFetch<ProfileResponse>(url, { method: "GET" });
};

// 프로필 수정 API
export const updateProfile = async (
  role: UserRole,
  form: ProfileFormState
) => {
  const url = getBaseEndpoint(role);
  const payload =
    role === "ADMIN"
      ? {
          adminNickname: form.nickname,
          introduction: form.introduction,
          phoneNumber: form.phoneNumber,
          imageUrl: form.imageUrl,
        }
      : {
          userNickname: form.nickname,
          introduction: form.introduction,
          phoneNumber: form.phoneNumber,
          imageUrl: form.imageUrl,
        };

  return apiFetch<ProfileResponse>(url, {
    method: "PATCH",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload),
  });
};

// 프로필 이미지 업로드용 Presigned URL 발급 API
export const requestProfilePresignedUrl = async (
  role: UserRole,
  contentType: string
) => {
  const url = `${getBaseEndpoint(role)}/presigned-url`;
  return apiFetch<PresignedUrlResponse>(url, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ contentType }),
  });
};

// 프로필 이미지 반영 API
export const updateProfileImage = async (
  role: UserRole,
  imageUrl: string | null
) => {
  const url = `${getBaseEndpoint(role)}/image`;
  const payload = { imageUrl };
  return apiFetch<ProfileResponse>(url, {
    method: "PATCH",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload),
  });
};
