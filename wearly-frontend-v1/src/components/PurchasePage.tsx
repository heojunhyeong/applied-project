import { useState } from 'react';

export default function PurchasePage() {
  const [address, setAddress] = useState('');
  const [detailedAddress, setDetailedAddress] = useState('');
  const [zipCode, setZipCode] = useState('');
  const [couponCode, setCouponCode] = useState('');
  const [appliedDiscount, setAppliedDiscount] = useState(0);

  const productTotal = 245000; // Example product total
  const finalAmount = productTotal - appliedDiscount;

  const handleApplyCoupon = () => {
    // UI-only behavior - simple mock discount
    if (couponCode.trim()) {
      setAppliedDiscount(25000);
    }
  };

  const handlePayment = () => {
    // UI-only behavior
    alert('Payment processing...');
  };

  return (
    <div className="max-w-[800px] mx-auto px-8 py-12">
      {/* Page Title */}
      <h1 className="text-3xl font-semibold text-gray-900 mb-12">Order / Payment</h1>

      {/* Section 1: Shipping Address */}
      <div className="mb-12">
        <h2 className="text-xl font-semibold text-gray-900 mb-6">Shipping Address</h2>
        <div className="space-y-4">
          <div>
            <label htmlFor="address" className="block text-sm text-gray-700 mb-2">
              Address
            </label>
            <input
              type="text"
              id="address"
              value={address}
              onChange={(e) => setAddress(e.target.value)}
              className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent"
              placeholder="Enter your address"
            />
          </div>
          <div>
            <label htmlFor="detailedAddress" className="block text-sm text-gray-700 mb-2">
              Detailed Address
            </label>
            <input
              type="text"
              id="detailedAddress"
              value={detailedAddress}
              onChange={(e) => setDetailedAddress(e.target.value)}
              className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent"
              placeholder="Enter detailed address (apartment, unit, etc.)"
            />
          </div>
          <div>
            <label htmlFor="zipCode" className="block text-sm text-gray-700 mb-2">
              Zip Code
            </label>
            <input
              type="text"
              id="zipCode"
              value={zipCode}
              onChange={(e) => setZipCode(e.target.value)}
              className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent"
              placeholder="Enter zip code"
            />
          </div>
        </div>
      </div>

      {/* Section 2: Coupon */}
      <div className="mb-12">
        <h2 className="text-xl font-semibold text-gray-900 mb-6">Coupon</h2>
        <div className="flex gap-3">
          <input
            type="text"
            value={couponCode}
            onChange={(e) => setCouponCode(e.target.value)}
            className="flex-1 px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent"
            placeholder="Enter coupon code"
          />
          <button
            onClick={handleApplyCoupon}
            className="px-6 py-3 bg-gray-200 text-gray-900 font-medium rounded-lg hover:bg-gray-300 transition-colors"
          >
            Apply Coupon
          </button>
        </div>
        {appliedDiscount > 0 && (
          <p className="text-sm text-green-600 mt-2">
            Coupon applied! {appliedDiscount.toLocaleString()}원 discount
          </p>
        )}
      </div>

      {/* Section 3: Final Payment Amount */}
      <div className="mb-12">
        <h2 className="text-xl font-semibold text-gray-900 mb-6">Final Payment Amount</h2>
        <div className="bg-gray-50 border border-gray-200 rounded-lg p-6">
          <div className="flex items-center justify-between mb-3">
            <span className="text-sm text-gray-600">Product Total</span>
            <span className="text-sm text-gray-900">{productTotal.toLocaleString()}원</span>
          </div>
          {appliedDiscount > 0 && (
            <div className="flex items-center justify-between mb-3">
              <span className="text-sm text-gray-600">Discount</span>
              <span className="text-sm text-red-600">-{appliedDiscount.toLocaleString()}원</span>
            </div>
          )}
          <div className="border-t border-gray-300 pt-4 mt-4">
            <div className="flex items-center justify-between">
              <span className="text-lg font-semibold text-gray-900">Final Amount</span>
              <span className="text-2xl font-bold text-gray-900">{finalAmount.toLocaleString()}원</span>
            </div>
          </div>
        </div>
      </div>

      {/* Section 4: Payment Action */}
      <div className="pt-8 border-t border-gray-200">
        <button
          onClick={handlePayment}
          className="w-full py-4 bg-gray-900 text-white text-lg font-semibold rounded-lg hover:bg-gray-800 transition-colors"
        >
          Pay Now
        </button>
      </div>
    </div>
  );
}
