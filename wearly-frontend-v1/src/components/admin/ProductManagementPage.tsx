import { useState } from 'react';
import AdminLayout from './AdminLayout';

interface Product {
  id: string;
  image: string;
  name: string;
  sellerId: string;
  price: number;
  stockQuantity: number;
  status: 'Selling' | 'Out of Stock' | 'Stopped';
}

export default function ProductManagementPage() {
  const [products, setProducts] = useState<Product[]>([
    {
      id: 'P001',
      image: 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400',
      name: 'NIKE Air Max 270',
      sellerId: 'S001',
      price: 150,
      stockQuantity: 10,
      status: 'Selling',
    },
    {
      id: 'P002',
      image: 'https://images.unsplash.com/photo-1556821840-3a63f95609a7?w=400',
      name: 'ADIDAS Originals Hoodie',
      sellerId: 'S002',
      price: 50,
      stockQuantity: 5,
      status: 'Selling',
    },
    {
      id: 'P003',
      image: 'https://images.unsplash.com/photo-1542272604-787c3835535d?w=400',
      name: 'LEVIS 501 Original Jeans',
      sellerId: 'S001',
      price: 70,
      stockQuantity: 0,
      status: 'Out of Stock',
    },
    {
      id: 'P004',
      image: 'https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?w=400',
      name: 'NEW BALANCE 990v5',
      sellerId: 'S003',
      price: 120,
      stockQuantity: 8,
      status: 'Selling',
    },
    {
      id: 'P005',
      image: 'https://images.unsplash.com/photo-1551028719-00167b16eac5?w=400',
      name: 'THE NORTH FACE Nuptse Jacket',
      sellerId: 'S002',
      price: 200,
      stockQuantity: 0,
      status: 'Stopped',
    },
    {
      id: 'P006',
      image: 'https://images.unsplash.com/photo-1460353581641-37baddab0fa2?w=400',
      name: 'NIKE Sportswear Tech Fleece',
      sellerId: 'S001',
      price: 80,
      stockQuantity: 12,
      status: 'Selling',
    },
    {
      id: 'P007',
      image: 'https://images.unsplash.com/photo-1515955656352-a1fa3ffcd111?w=400',
      name: 'ADIDAS Ultraboost 22',
      sellerId: 'S002',
      price: 180,
      stockQuantity: 0,
      status: 'Out of Stock',
    },
    {
      id: 'P008',
      image: 'https://images.unsplash.com/photo-1605518216938-7c31b7b14ad0?w=400',
      name: 'LEVIS Trucker Jacket',
      sellerId: 'S001',
      price: 100,
      stockQuantity: 0,
      status: 'Stopped',
    },
  ]);

  // Handle stopping a product
  const handleStopSelling = (id: string) => {
    setProducts((prevProducts) =>
      prevProducts.map((product) =>
        product.id === id ? { ...product, status: 'Stopped' as const } : product
      )
    );
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'Selling':
        return 'bg-green-100 text-green-800';
      case 'Out of Stock':
        return 'bg-yellow-100 text-yellow-800';
      case 'Stopped':
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  return (
    <AdminLayout>
      <div className="p-8">
        {/* Page Header */}
        <div className="mb-8">
          <h1 className="text-2xl font-semibold text-gray-900">Product Management</h1>
          <p className="text-sm text-gray-600 mt-2">
            Manage all products listed on the platform
          </p>
        </div>

        {/* Product Table */}
        <div className="bg-white border border-gray-200 rounded-lg overflow-hidden">
          <table className="w-full">
            <thead className="bg-gray-50 border-b border-gray-200">
              <tr>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  Product ID
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  Product Image
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  Product Name
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  Seller ID
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  Price
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  Stock Quantity
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  Status
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  Action
                </th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-200">
              {products.map((product) => (
                <tr key={product.id} className="hover:bg-gray-50 transition-colors">
                  <td className="px-6 py-4 text-sm text-gray-900">{product.id}</td>
                  <td className="px-6 py-4">
                    <img
                      src={product.image}
                      alt={product.name}
                      className="w-16 h-16 object-cover rounded-md"
                    />
                  </td>
                  <td className="px-6 py-4 text-sm text-gray-900">{product.name}</td>
                  <td className="px-6 py-4 text-sm text-gray-900">{product.sellerId}</td>
                  <td className="px-6 py-4 text-sm text-gray-900">${product.price}</td>
                  <td className="px-6 py-4 text-sm text-gray-900">{product.stockQuantity}</td>
                  <td className="px-6 py-4 text-sm">
                    <span
                      className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getStatusColor(
                        product.status
                      )}`}
                    >
                      {product.status}
                    </span>
                  </td>
                  <td className="px-6 py-4 text-sm">
                    {product.status === 'Selling' || product.status === 'Out of Stock' ? (
                      <button
                        onClick={() => handleStopSelling(product.id)}
                        className="px-4 py-2 text-xs font-medium text-red-600 border border-red-300 rounded-md hover:bg-red-50 transition-colors"
                      >
                        Stop Selling
                      </button>
                    ) : (
                      <span className="px-4 py-2 text-xs font-medium text-gray-400 border border-gray-200 rounded-md bg-gray-50 cursor-not-allowed inline-block">
                        Stopped
                      </span>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </AdminLayout>
  );
}