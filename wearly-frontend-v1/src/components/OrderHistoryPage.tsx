import { Link } from 'react-router';
import { Truck, Edit3, Search } from 'lucide-react';
import { useState } from 'react';

// Mock order data
const orders = [
  {
    orderDate: '2025-01-15',
    orderNumber: 'WE20250115001',
    items: [
      {
        id: 1,
        productId: 'nike-001',
        image: 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400',
        name: 'Nike Air Max 90',
        brand: 'NIKE',
        price: 129000,
        quantity: 1,
        status: 'Delivered',
        deliveryDate: '2025-01-18',
      },
    ],
  },
  {
    orderDate: '2025-01-10',
    orderNumber: 'WE20250110002',
    items: [
      {
        id: 2,
        productId: 'levis-001',
        image: 'https://images.unsplash.com/photo-1542272604-787c3835535d?w=400',
        name: "Levi's 501 Original Jeans",
        brand: "LEVI'S",
        price: 98000,
        quantity: 1,
        status: 'Delivered',
        deliveryDate: '2025-01-14',
      },
      {
        id: 3,
        productId: 'adidas-001',
        image: 'https://images.unsplash.com/photo-1515955656352-a1fa3ffcd111?w=400',
        name: 'Adidas Ultraboost 22',
        brand: 'ADIDAS',
        price: 189000,
        quantity: 1,
        status: 'Delivered',
        deliveryDate: '2025-01-14',
      },
    ],
  },
  {
    orderDate: '2024-12-28',
    orderNumber: 'WE20241228003',
    items: [
      {
        id: 4,
        productId: 'north-face-001',
        image: 'https://images.unsplash.com/photo-1551028719-00167b16eac5?w=400',
        name: 'The North Face Nuptse Jacket',
        brand: 'THE NORTH FACE',
        price: 329000,
        quantity: 1,
        status: 'Delivered',
        deliveryDate: '2025-01-03',
      },
    ],
  },
];

export default function OrderHistoryPage() {
  const [searchTerm, setSearchTerm] = useState('');

  // Filter orders by product name
  const filteredOrders = orders
    .map((order) => ({
      ...order,
      items: order.items.filter((item) =>
        item.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
        item.brand.toLowerCase().includes(searchTerm.toLowerCase())
      ),
    }))
    .filter((order) => order.items.length > 0);

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-5xl mx-auto px-6 py-8">
        {/* Page Title */}
        <div className="mb-6">
          <h1 className="text-2xl text-gray-900">Order History</h1>
        </div>

        {/* Search Bar */}
        <div className="mb-6 relative">
          <input
            type="text"
            placeholder="Search ordered products"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="w-full px-4 py-3 pr-12 bg-white border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent text-sm"
          />
          <Search className="absolute right-4 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
        </div>

        {/* Order List */}
        <div className="space-y-8">
          {filteredOrders.map((order) => (
            <div key={order.orderNumber} className="space-y-4">
              {/* Order Date Header */}
              <div className="flex items-center justify-between px-4 py-3 bg-gray-100 rounded-lg">
                <div className="flex items-center gap-4">
                  <span className="text-sm text-gray-900">
                    Order Date: {order.orderDate}
                  </span>
                  <span className="text-sm text-gray-500">
                    Order No. {order.orderNumber}
                  </span>
                </div>
              </div>

              {/* Order Items */}
              <div className="bg-white border border-gray-200 rounded-lg overflow-hidden">
                {order.items.map((item, index) => (
                  <div key={item.id}>
                    {index > 0 && (
                      <div className="border-t border-gray-100"></div>
                    )}
                    <div className="p-6">
                      <div className="flex gap-6">
                        {/* Product Image */}
                        <Link to={`/product/${item.productId}`}>
                          <img
                            src={item.image}
                            alt={item.name}
                            className="w-24 h-24 object-cover rounded-md border border-gray-200 hover:opacity-80 transition-opacity"
                          />
                        </Link>

                        {/* Product Info & Status */}
                        <div className="flex-1 min-w-0">
                          <div className="flex items-start justify-between gap-4">
                            {/* Left: Product Details */}
                            <div className="flex-1">
                              <Link
                                to={`/product/${item.productId}`}
                                className="text-sm text-gray-500 hover:text-gray-700"
                              >
                                {item.brand}
                              </Link>
                              <h3 className="text-base text-gray-900 mt-1">
                                {item.name}
                              </h3>
                              <div className="flex items-center gap-4 mt-2">
                                <span className="text-sm text-gray-700">
                                  ₩{item.price.toLocaleString()}
                                </span>
                                <span className="text-sm text-gray-500">
                                  Quantity: {item.quantity}
                                </span>
                              </div>

                              {/* Delivery Status */}
                              <div className="mt-4 flex items-center gap-3">
                                <span className="inline-flex items-center px-3 py-1 rounded-full text-sm bg-green-50 text-green-700 border border-green-200">
                                  {item.status}
                                </span>
                                <span className="text-sm text-gray-600">
                                  Arrived on {item.deliveryDate}
                                </span>
                              </div>
                            </div>

                            {/* Right: Action Buttons */}
                            <div className="flex flex-col gap-2 min-w-[140px]">
                              <Link
                                to={`/tracking/${order.orderNumber}`}
                                className="flex items-center justify-center gap-2 px-4 py-2.5 bg-gray-900 text-white text-sm rounded-md hover:bg-gray-800 transition-colors"
                              >
                                <Truck className="w-4 h-4" />
                                Delivery Tracking
                              </Link>
                              <button className="flex items-center justify-center gap-2 px-4 py-2.5 bg-white text-gray-700 text-sm rounded-md border border-gray-300 hover:bg-gray-50 transition-colors">
                                <Edit3 className="w-4 h-4" />
                                Write Review
                              </button>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          ))}
        </div>

        {/* Empty State (if no orders) */}
        {filteredOrders.length === 0 && (
          <div className="bg-white border border-gray-200 rounded-lg p-12 text-center">
            <p className="text-gray-500">
              {searchTerm ? 'No matching products found' : 'No orders yet'}
            </p>
            {!searchTerm && (
              <Link
                to="/"
                className="inline-block mt-4 text-sm text-gray-900 hover:underline"
              >
                Start Shopping
              </Link>
            )}
          </div>
        )}
      </div>
    </div>
  );
}