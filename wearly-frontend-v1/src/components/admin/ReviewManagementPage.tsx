import { useState, useEffect } from 'react';
import { ChevronDown, ChevronRight } from 'lucide-react';
import AdminLayout from './AdminLayout';
import { apiFetch } from '../../api/http';

interface ReviewReportResponse {
  reportId: number;
  reviewId: number;
  productId: number;
  reviewerId: number;
  reporterId: number;
  reviewContent: string;
  reviewRating: number;
  reviewStatus: 'ACTIVE' | 'HIDDEN';
  reason: string;
  status: 'PENDING' | 'RESOLVED' | 'REJECTED';
  reportCreatedDate: string;
  reviewCreatedDate: string;
}

interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

interface Review {
  reportId: number;
  reviewId: number;
  productId: number;
  reviewerId: number;
  reporterId: number;
  reviewContent: string;
  reviewRating: number;
  status: 'PENDING' | 'RESOLVED' | 'REJECTED';
  reviewStatus: 'ACTIVE' | 'HIDDEN';
  reportReason: string;
  reportCreatedDate: string;
}

export default function ReviewManagementPage() {
  const [expandedReviewId, setExpandedReviewId] = useState<number | null>(null);
  const [reviews, setReviews] = useState<Review[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  // Fetch reviews from API
  useEffect(() => {
    const fetchReviews = async () => {
      try {
        setLoading(true);
        setError(null);

        const response = await apiFetch<PageResponse<ReviewReportResponse>>(
          `/api/admin/reviews/reports?page=${page}&size=20`
        );

        const mappedReviews: Review[] = (response.content || []).map((r) => ({
          reportId: r.reportId,
          reviewId: r.reviewId,
          productId: r.productId,
          reviewerId: r.reviewerId,
          reporterId: r.reporterId,
          reviewContent: r.reviewContent,
          reviewRating: r.reviewRating,
          status: r.status,
          reviewStatus: r.reviewStatus,
          reportReason: r.reason || 'Unknown',
          reportCreatedDate: r.reportCreatedDate || r.reviewCreatedDate,
        }));

        setReviews(mappedReviews);
        setTotalPages(response.totalPages || 0);
      } catch (err: any) {
        setError(err.message || '리뷰 신고 목록을 불러오는데 실패했습니다.');
        console.error('Failed to fetch review reports:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchReviews();
  }, [page]);

  const toggleReviewExpand = (reportId: number) => {
    if (expandedReviewId === reportId) {
      setExpandedReviewId(null);
    } else {
      setExpandedReviewId(reportId);
    }
  };

  const handleApprove = async (reportId: number) => {
    if (!confirm('정말 이 신고를 승인하시겠습니까? 리뷰가 숨김 처리됩니다.')) {
      return;
    }

    try {
      await apiFetch(`/api/admin/reviews/reports/${reportId}/approve`, {
        method: 'PATCH',
      });

      // Update local state
      setReviews((prev) =>
        prev.map((review) =>
          review.reportId === reportId
            ? { ...review, status: 'RESOLVED' as const, reviewStatus: 'HIDDEN' as const }
            : review
        )
      );
      setExpandedReviewId(null);

      alert('신고가 승인되었고 리뷰가 숨김 처리되었습니다.');
    } catch (err: any) {
      alert(`승인 실패: ${err.message || '알 수 없는 오류가 발생했습니다.'}`);
      console.error('Failed to approve report:', err);
    }
  };

  const handleReject = async (reportId: number) => {
    if (!confirm('정말 이 신고를 반려하시겠습니까?')) {
      return;
    }

    try {
      await apiFetch(`/api/admin/reviews/reports/${reportId}/reject`, {
        method: 'PATCH',
      });

      // Update local state
      setReviews((prev) =>
        prev.map((review) =>
          review.reportId === reportId ? { ...review, status: 'REJECTED' as const } : review
        )
      );
      setExpandedReviewId(null);

      alert('신고가 반려되었습니다.');
    } catch (err: any) {
      alert(`반려 실패: ${err.message || '알 수 없는 오류가 발생했습니다.'}`);
      console.error('Failed to reject report:', err);
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'PENDING':
        return 'bg-yellow-100 text-yellow-800';
      case 'RESOLVED':
        return 'bg-green-100 text-green-800';
      case 'REJECTED':
        return 'bg-gray-100 text-gray-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  const getReviewStatusColor = (status: string) => {
    switch (status) {
      case 'ACTIVE':
        return 'bg-green-100 text-green-800';
      case 'HIDDEN':
        return 'bg-gray-100 text-gray-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  if (loading) {
    return (
      <AdminLayout>
        <div className="p-8">
          <div className="flex items-center justify-center py-12">
            <p className="text-gray-600">로딩 중...</p>
          </div>
        </div>
      </AdminLayout>
    );
  }

  if (error) {
    return (
      <AdminLayout>
        <div className="p-8">
          <div className="bg-red-50 border border-red-200 rounded-lg p-4">
            <p className="text-red-800">{error}</p>
          </div>
        </div>
      </AdminLayout>
    );
  }

  return (
    <AdminLayout>
      <div className="p-8">
        {/* Page Header */}
        <div className="mb-8">
          <h1 className="text-2xl font-semibold text-gray-900">Review Management</h1>
          <p className="text-sm text-gray-600 mt-2">
            View and manage reported reviews
          </p>
        </div>

        {/* Review Table */}
        <div className="bg-white border border-gray-200 rounded-lg overflow-hidden">
          <table className="w-full">
            <thead className="bg-gray-50 border-b border-gray-200">
              <tr>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider w-12">
                  
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  Report ID
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  Review ID
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  Product ID
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  Reviewer ID
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  Report Status
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  Review Status
                </th>
              </tr>
            </thead>
            <tbody>
              {reviews.length > 0 ? (
                reviews.map((review) => (
                  <>
                    {/* Review Row */}
                    <tr
                      key={review.reportId}
                      onClick={() => toggleReviewExpand(review.reportId)}
                      className="hover:bg-gray-50 transition-colors cursor-pointer border-b border-gray-200"
                    >
                      <td className="px-6 py-4 text-sm text-gray-500">
                        {expandedReviewId === review.reportId ? (
                          <ChevronDown className="w-4 h-4" />
                        ) : (
                          <ChevronRight className="w-4 h-4" />
                        )}
                      </td>
                      <td className="px-6 py-4 text-sm text-gray-900 font-medium">
                        {review.reportId}
                      </td>
                      <td className="px-6 py-4 text-sm text-gray-900">{review.reviewId}</td>
                      <td className="px-6 py-4 text-sm text-gray-900">{review.productId}</td>
                      <td className="px-6 py-4 text-sm text-gray-900">{review.reviewerId}</td>
                      <td className="px-6 py-4 text-sm">
                        <span
                          className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getStatusColor(
                            review.status
                          )}`}
                        >
                          {review.status}
                        </span>
                      </td>
                      <td className="px-6 py-4 text-sm">
                        <span
                          className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getReviewStatusColor(
                            review.reviewStatus
                          )}`}
                        >
                          {review.reviewStatus}
                        </span>
                      </td>
                    </tr>

                    {/* Expanded Review Detail */}
                    {expandedReviewId === review.reportId && (
                      <tr className="bg-blue-50/30">
                        <td></td>
                        <td colSpan={6} className="px-6 py-4">
                          <div className="border border-gray-300 rounded bg-white p-4">
                            {/* Comment Content */}
                            <div className="mb-4">
                              <h4 className="text-xs font-semibold text-gray-700 uppercase tracking-wider mb-2">
                                Review Content
                              </h4>
                              <p className="text-sm text-gray-900 leading-relaxed">
                                {review.reviewContent}
                              </p>
                              <p className="text-xs text-gray-500 mt-1">
                                Rating: {review.reviewRating}/5
                              </p>
                            </div>

                            {/* Report Reason */}
                            <div className="mb-4">
                              <h4 className="text-xs font-semibold text-gray-700 uppercase tracking-wider mb-2">
                                Report Reason
                              </h4>
                              <p className="text-sm text-gray-900">
                                {review.reportReason}
                              </p>
                            </div>

                            {/* Action Buttons */}
                            {review.status === 'PENDING' && (
                              <div className="flex items-center gap-3 pt-2">
                                <button
                                  onClick={(e) => {
                                    e.stopPropagation();
                                    handleApprove(review.reportId);
                                  }}
                                  className="px-4 py-2 bg-red-600 text-white text-sm font-medium rounded hover:bg-red-700 transition-colors"
                                >
                                  Approve (Hide Review)
                                </button>
                                <button
                                  onClick={(e) => {
                                    e.stopPropagation();
                                    handleReject(review.reportId);
                                  }}
                                  className="px-4 py-2 bg-gray-200 text-gray-900 text-sm font-medium rounded hover:bg-gray-300 transition-colors"
                                >
                                  Reject
                                </button>
                              </div>
                            )}
                          </div>
                        </td>
                      </tr>
                    )}
                  </>
                ))
              ) : (
                <tr>
                  <td colSpan={7} className="px-6 py-12 text-center text-sm text-gray-500">
                    No review reports found
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </AdminLayout>
  );
}
