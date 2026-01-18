import { apiFetch } from "./http";

type CouponDownloadResponse = {
    message: string;
};

/**
 * 쿠폰 발급 API
 * @param benefitId 1: 10% 할인 쿠폰, 2: 5,000원 할인 쿠폰
 */
export const downloadCoupon = async (benefitId: number): Promise<CouponDownloadResponse> => {
    return apiFetch<CouponDownloadResponse>(`/api/coupons/download/${benefitId}`, {
        method: "POST",
    });
};