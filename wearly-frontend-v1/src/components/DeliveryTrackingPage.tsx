import { useParams, Link } from 'react-router';
import { Package, CheckCircle, ArrowLeft } from 'lucide-react';

// Mock delivery tracking data
const mockTrackingData = {
  'WE20250115001': {
    orderNumber: 'WE20250115001',
    orderDate: '2025-01-15',
    deliveryStatus: 'Delivered',
    arrivalDate: '2025-01-18',
    carrier: 'CJ Logistics',
    trackingNumber: '123456789012',
    recipient: {
      name: 'Kim Minjun',
      address: '123-45 Gangnam-daero, Gangnam-gu, Seoul, 06000',
    },
  },
  'WE20250110002': {
    orderNumber: 'WE20250110002',
    orderDate: '2025-01-10',
    deliveryStatus: 'Delivered',
    arrivalDate: '2025-01-14',
    carrier: 'Hanjin Express',
    trackingNumber: '987654321098',
    recipient: {
      name: 'Lee Soyeon',
      address: '67-89 Teheran-ro, Gangnam-gu, Seoul, 06134',
    },
  },
  'WE20241228003': {
    orderNumber: 'WE20241228003',
    orderDate: '2024-12-28',
    deliveryStatus: 'Delivered',
    arrivalDate: '2025-01-03',
    carrier: 'Lotte Logistics',
    trackingNumber: '456789123456',
    recipient: {
      name: 'Park Jihoon',
      address: '234-56 Apgujeong-ro, Gangnam-gu, Seoul, 06009',
    },
  },
};

export default function DeliveryTrackingPage() {
  const { orderNumber } = useParams<{ orderNumber: string }>();
  const trackingData = orderNumber ? mockTrackingData[orderNumber as keyof typeof mockTrackingData] : null;

  if (!trackingData) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="bg-white border border-gray-200 rounded-lg p-12 text-center max-w-md">
          <p className="text-gray-500 mb-4">Tracking information not found</p>
          <Link
            to="/orders"
            className="inline-flex items-center gap-2 text-sm text-gray-900 hover:underline"
          >
            <ArrowLeft className="w-4 h-4" />
            Back to Order History
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-3xl mx-auto px-6 py-8">
        {/* Back Button */}
        <Link
          to="/orders"
          className="inline-flex items-center gap-2 text-sm text-gray-600 hover:text-gray-900 mb-6 transition-colors"
        >
          <ArrowLeft className="w-4 h-4" />
          Back to Order History
        </Link>

        {/* Page Title */}
        <div className="mb-6">
          <h1 className="text-2xl text-gray-900">Delivery Tracking</h1>
          <p className="text-sm text-gray-600 mt-2">Order No. {trackingData.orderNumber}</p>
        </div>

        {/* Delivery Status Banner */}
        <div className="bg-green-50 border border-green-200 rounded-lg p-6 mb-6">
          <div className="flex items-start gap-4">
            <CheckCircle className="w-6 h-6 text-green-600 flex-shrink-0 mt-0.5" />
            <div>
              <h2 className="text-lg text-green-900 mb-1">Delivery Completed</h2>
              <p className="text-sm text-green-700">
                Your order has been delivered on {trackingData.arrivalDate}
              </p>
            </div>
          </div>
        </div>

        {/* Delivery Information */}
        <div className="bg-white border border-gray-200 rounded-lg p-6 mb-4">
          <div className="flex items-center gap-3 mb-6">
            <Package className="w-5 h-5 text-gray-700" />
            <h2 className="text-lg text-gray-900">Delivery Information</h2>
          </div>

          <div className="space-y-4">
            {/* Carrier Name */}
            <div className="flex py-3 border-b border-gray-100">
              <span className="text-sm text-gray-600 w-36">Carrier</span>
              <span className="text-sm text-gray-900">{trackingData.carrier}</span>
            </div>

            {/* Tracking Number */}
            <div className="flex py-3 border-b border-gray-100">
              <span className="text-sm text-gray-600 w-36">Tracking Number</span>
              <span className="text-sm text-gray-900 font-mono">{trackingData.trackingNumber}</span>
            </div>

            {/* Delivery Status */}
            <div className="flex py-3">
              <span className="text-sm text-gray-600 w-36">Status</span>
              <span className="inline-flex items-center px-3 py-1 rounded-full text-sm bg-green-50 text-green-700 border border-green-200">
                {trackingData.deliveryStatus}
              </span>
            </div>
          </div>
        </div>

        {/* Recipient Information */}
        <div className="bg-white border border-gray-200 rounded-lg p-6">
          <h2 className="text-lg text-gray-900 mb-6">Recipient Information</h2>

          <div className="space-y-4">
            {/* Recipient Name */}
            <div className="flex py-3 border-b border-gray-100">
              <span className="text-sm text-gray-600 w-36">Recipient</span>
              <span className="text-sm text-gray-900">{trackingData.recipient.name}</span>
            </div>

            {/* Delivery Address */}
            <div className="flex py-3">
              <span className="text-sm text-gray-600 w-36">Address</span>
              <span className="text-sm text-gray-900">{trackingData.recipient.address}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
