import { useState } from 'react';
import { ChevronDown, ChevronRight } from 'lucide-react';
import AdminLayout from './AdminLayout';

interface Review {
  reviewId: string;
  userId: string;
  sellerId: string;
  status: 'Public' | 'Private';
  commentContent: string;
  reportReason: string;
}

export default function ReviewManagementPage() {
  const [expandedReviewId, setExpandedReviewId] = useState<string | null>(null);
  const [reviews, setReviews] = useState<Review[]>([
    {
      reviewId: 'R001',
      userId: 'U001',
      sellerId: 'S001',
      status: 'Public',
      commentContent: 'This product is terrible quality. Complete waste of money. The material feels cheap and started falling apart after one wear.',
      reportReason: 'Offensive language and misleading information',
    },
    {
      reviewId: 'R002',
      userId: 'U003',
      sellerId: 'S002',
      status: 'Public',
      commentContent: 'Seller is a scammer! Never buy from them. They sent me a fake product.',
      reportReason: 'Defamatory content against seller',
    },
    {
      reviewId: 'R003',
      userId: 'U005',
      sellerId: 'S001',
      status: 'Public',
      commentContent: 'Do not buy!!! This is a SCAM!!!',
      reportReason: 'Spam and excessive capitalization',
    },
    {
      reviewId: 'R004',
      userId: 'U002',
      sellerId: 'S003',
      status: 'Private',
      commentContent: 'Horrible service. I will never shop here again. Customer service was rude and unhelpful.',
      reportReason: 'Inappropriate language',
    },
    {
      reviewId: 'R005',
      userId: 'U004',
      sellerId: 'S002',
      status: 'Public',
      commentContent: 'The worst shopping experience ever. Product arrived damaged and seller refused to refund.',
      reportReason: 'Potential false claim',
    },
    {
      reviewId: 'R006',
      userId: 'U001',
      sellerId: 'S004',
      status: 'Public',
      commentContent: 'This brand is trash. Don\'t waste your time or money here.',
      reportReason: 'Offensive language',
    },
    {
      reviewId: 'R007',
      userId: 'U003',
      sellerId: 'S005',
      status: 'Private',
      commentContent: 'Seller sent wrong size and won\'t respond to messages. Avoid at all costs!',
      reportReason: 'Unverified claims',
    },
    {
      reviewId: 'R008',
      userId: 'U005',
      sellerId: 'S001',
      status: 'Public',
      commentContent: 'FAKE PRODUCTS!!! REPORT THIS SELLER!!!',
      reportReason: 'False accusations and spam',
    },
  ]);

  const toggleReviewExpand = (reviewId: string) => {
    if (expandedReviewId === reviewId) {
      setExpandedReviewId(null);
    } else {
      setExpandedReviewId(reviewId);
    }
  };

  const handleDelete = (reviewId: string) => {
    setReviews((prev) =>
      prev.map((review) =>
        review.reviewId === reviewId ? { ...review, status: 'Private' } : review
      )
    );
    setExpandedReviewId(null);
  };

  const handleReject = (reviewId: string) => {
    setReviews((prev) =>
      prev.map((review) =>
        review.reviewId === reviewId ? { ...review, status: 'Public' } : review
      )
    );
    setExpandedReviewId(null);
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'Public':
        return 'bg-green-100 text-green-800';
      case 'Private':
        return 'bg-gray-100 text-gray-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

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
                  Review ID
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  User ID
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  Seller ID
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  Status
                </th>
              </tr>
            </thead>
            <tbody>
              {reviews.map((review) => (
                <>
                  {/* Review Row */}
                  <tr
                    key={review.reviewId}
                    onClick={() => toggleReviewExpand(review.reviewId)}
                    className="hover:bg-gray-50 transition-colors cursor-pointer border-b border-gray-200"
                  >
                    <td className="px-6 py-4 text-sm text-gray-500">
                      {expandedReviewId === review.reviewId ? (
                        <ChevronDown className="w-4 h-4" />
                      ) : (
                        <ChevronRight className="w-4 h-4" />
                      )}
                    </td>
                    <td className="px-6 py-4 text-sm text-gray-900 font-medium">
                      {review.reviewId}
                    </td>
                    <td className="px-6 py-4 text-sm text-gray-900">{review.userId}</td>
                    <td className="px-6 py-4 text-sm text-gray-900">{review.sellerId}</td>
                    <td className="px-6 py-4 text-sm">
                      <span
                        className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getStatusColor(
                          review.status
                        )}`}
                      >
                        {review.status}
                      </span>
                    </td>
                  </tr>

                  {/* Expanded Review Detail */}
                  {expandedReviewId === review.reviewId && (
                    <tr className="bg-blue-50/30">
                      <td></td>
                      <td colSpan={4} className="px-6 py-4">
                        <div className="border border-gray-300 rounded bg-white p-4">
                          {/* Comment Content */}
                          <div className="mb-4">
                            <h4 className="text-xs font-semibold text-gray-700 uppercase tracking-wider mb-2">
                              Comment Content
                            </h4>
                            <p className="text-sm text-gray-900 leading-relaxed">
                              {review.commentContent}
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
                          <div className="flex items-center gap-3 pt-2">
                            <button
                              onClick={(e) => {
                                e.stopPropagation();
                                handleDelete(review.reviewId);
                              }}
                              className="px-4 py-2 bg-red-600 text-white text-sm font-medium rounded hover:bg-red-700 transition-colors"
                            >
                              Delete
                            </button>
                            <button
                              onClick={(e) => {
                                e.stopPropagation();
                                handleReject(review.reviewId);
                              }}
                              className="px-4 py-2 bg-gray-200 text-gray-900 text-sm font-medium rounded hover:bg-gray-300 transition-colors"
                            >
                              Reject
                            </button>
                          </div>
                        </div>
                      </td>
                    </tr>
                  )}
                </>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </AdminLayout>
  );
}
