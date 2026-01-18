import { useState, useEffect } from 'react';
import AdminLayout from './AdminLayout';
import { apiFetch } from '../../api/http';

interface ProductResponse {
  id: number | null;
  sellerId: number | null;
  productName: string | null;
  price: number | null;
  status: 'ON_SALE' | 'SOLD_OUT' | 'DELETED' | null;
  stockQuantity: number | null;
  productCategory: string | null;
  imageUrl: string | null;
  createdDate: string | null;
  updatedDate: string | null;
}

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
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Fetch products from API
  useEffect(() => {
    const fetchProducts = async () => {
      try {
        setLoading(true);
        setError(null);

        const response = await apiFetch<ProductResponse[]>('/api/admin/products');
        const mappedProducts = (response || []).map((p) => ({
          id: (p.id ?? 0).toString(),
          image: p.imageUrl || 'https://via.placeholder.com/400',
          name: p.productName || '',
          sellerId: (p.sellerId ?? 0).toString(),
          price: p.price ?? 0,
          stockQuantity: p.stockQuantity ?? 0,
          status:
            p.status === 'ON_SALE'
              ? ('Selling' as const)
              : p.status === 'SOLD_OUT'
              ? ('Out of Stock' as const)
              : ('Stopped' as const),
        }));

        setProducts(mappedProducts);
      } catch (err: any) {
        setError(err.message || '상품 목록을 불러오는데 실패했습니다.');
        console.error('Failed to fetch products:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchProducts();
  }, []);

  // Handle stopping a product
  const handleStopSelling = async (id: string) => {
    if (!confirm('정말 판매를 중지하시겠습니까?')) {
      return;
    }

    try {
      await apiFetch(`/api/admin/products/${id}/status`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ status: 'DELETED' }),
      });

      // Update local state
      setProducts((prevProducts) =>
        prevProducts.map((product) =>
          product.id === id ? { ...product, status: 'Stopped' as const } : product
        )
      );

      alert('판매가 중지되었습니다.');
    } catch (err: any) {
      alert(`판매 중지 실패: ${err.message || '알 수 없는 오류가 발생했습니다.'}`);
      console.error('Failed to stop selling:', err);
    }
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
                      onError={(e) => {
                        (e.target as HTMLImageElement).src = 'https://via.placeholder.com/400';
                      }}
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