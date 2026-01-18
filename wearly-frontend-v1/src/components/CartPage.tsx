import { useState, useEffect } from 'react';
import { Minus, Plus, X } from 'lucide-react';
import { useNavigate } from 'react-router';
import { apiFetch } from '../api/http';

interface CartItem {
    id: string;
    productId: number; // productId 추가
    name: string;
    price: number;
    quantity: number;
    image: string;
    option?: string;
    available: boolean;
    size: string; // size 추가 (수량 업데이트에 필요)
}

// API 응답 타입
interface CartResponseDto {
    cartId: number;
    productId: number;
    productName: string;
    price: number;
    quantity: number;
    size: string; // "SMALL" | "MEDIUM" | "LARGE" | "EXTRA_LARGE"
    imageUrl: string;
}

// 사이즈 매핑
const SIZE_MAP: Record<string, string> = {
    SMALL: "S",
    MEDIUM: "M",
    LARGE: "L",
    EXTRA_LARGE: "XL"
};

// 역매핑 (S -> SMALL)
const REVERSE_SIZE_MAP: Record<string, string> = {
    "S": "SMALL",
    "M": "MEDIUM",
    "L": "LARGE",
    "XL": "EXTRA_LARGE"
};

export default function CartPage() {
    const navigate = useNavigate();
    const [cartItems, setCartItems] = useState<CartItem[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [selectedItems, setSelectedItems] = useState<Set<string>>(new Set());

    // API에서 장바구니 데이터 가져오기
    useEffect(() => {
        const fetchCartItems = async () => {
            try {
                setLoading(true);
                const data = await apiFetch<CartResponseDto[]>(`/api/users/cart/items`);

                // API 응답을 CartItem 형식으로 변환
                const transformedItems: CartItem[] = data.map((item) => ({
                    id: item.cartId.toString(),
                    productId: item.productId, // productId 추가
                    name: item.productName,
                    price: item.price,
                    quantity: item.quantity,
                    image: item.imageUrl || 'https://via.placeholder.com/400',
                    option: `Size: ${SIZE_MAP[item.size] || item.size}`,
                    available: true,
                    size: item.size, // size 추가
                }));

                setCartItems(transformedItems);
                setError(null);
            } catch (err: any) {
                setError(err.message || '장바구니를 불러오는데 실패했습니다.');
                console.error('Failed to fetch cart items:', err);
            } finally {
                setLoading(false);
            }
        };

        fetchCartItems();
        
        // 페이지 포커스 시 다시 불러오기
        const handleFocus = () => {
            fetchCartItems();
        };
        
        window.addEventListener('focus', handleFocus);
        return () => window.removeEventListener('focus', handleFocus);
    }, []);

    // Get available and unavailable items
    const availableItems = cartItems.filter((item) => item.available);
    const unavailableItems = cartItems.filter((item) => !item.available);

    // Handle select all
    const handleSelectAll = () => {
        if (selectedItems.size === availableItems.length) {
            setSelectedItems(new Set());
        } else {
            setSelectedItems(new Set(availableItems.map((item) => item.id)));
        }
    };

    // Handle individual item selection
    const handleItemSelect = (id: string) => {
        const newSelected = new Set(selectedItems);
        if (newSelected.has(id)) {
            newSelected.delete(id);
        } else {
            newSelected.add(id);
        }
        setSelectedItems(newSelected);
    };

    // Handle quantity change
    const handleQuantityChange = async (id: string, delta: number) => {
        const item = cartItems.find((item) => item.id === id);
        if (!item) return;

        const newQuantity = Math.max(1, item.quantity + delta);

        // 수량이 1보다 작아지면 삭제
        if (newQuantity < 1) {
            await handleDeleteItem(id);
            return;
        }

        try {
            // 수량이 변경된 경우 API 호출
            if (newQuantity !== item.quantity) {
                // 현재 수량과 새 수량의 차이를 계산
                const quantityDelta = newQuantity - item.quantity;

                // POST API는 합산만 지원하므로, 증가/감소에 따라 처리
                if (quantityDelta > 0) {
                    // 수량 증가: POST로 추가
                    await apiFetch(`/api/users/cart/items`, {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json',
                        },
                        body: JSON.stringify({
                            productId: item.productId,
                            quantity: quantityDelta,
                            size: item.size,
                        }),
                    });
                } else {
                    // 수량 감소: 삭제 후 재추가 (API 제한으로 인한 우회 방법)
                    // 또는 더 나은 방법: 삭제 후 새 수량으로 추가
                    await apiFetch(`/api/users/cart/items/${item.productId}`, {
                        method: 'DELETE',
                    });

                    if (newQuantity > 0) {
                        await apiFetch(`/api/users/cart/items`, {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/json',
                            },
                            body: JSON.stringify({
                                productId: item.productId,
                                quantity: newQuantity,
                                size: item.size,
                            }),
                        });
                    }
                }

                // 장바구니 다시 불러오기
                const data = await apiFetch<CartResponseDto[]>(`/api/users/cart/items`);
                const transformedItems: CartItem[] = data.map((item) => ({
                    id: item.cartId.toString(),
                    productId: item.productId,
                    name: item.productName,
                    price: item.price,
                    quantity: item.quantity,
                    image: item.imageUrl || 'https://via.placeholder.com/400',
                    option: `Size: ${SIZE_MAP[item.size] || item.size}`,
                    available: true,
                    size: item.size,
                }));

                setCartItems(transformedItems);

                // 장바구니 변경 이벤트 발생
                window.dispatchEvent(new Event('cartChange'));
            }
        } catch (err: any) {
            alert(err.message || '수량 변경에 실패했습니다.');
            // 에러 발생 시 장바구니 다시 불러오기
            const data = await apiFetch<CartResponseDto[]>(`/api/users/cart/items`);
            const transformedItems: CartItem[] = data.map((item) => ({
                id: item.cartId.toString(),
                productId: item.productId,
                name: item.productName,
                price: item.price,
                quantity: item.quantity,
                image: item.imageUrl || 'https://via.placeholder.com/400',
                option: `Size: ${SIZE_MAP[item.size] || item.size}`,
                available: true,
                size: item.size,
            }));
            setCartItems(transformedItems);
        }
    };

    // Handle delete item
    const handleDeleteItem = async (id: string) => {
        const item = cartItems.find((item) => item.id === id);
        if (!item) return;

        try {
            // API 호출로 장바구니에서 삭제 (productId 사용)
            await apiFetch(`/api/users/cart/items/${item.productId}`, {
                method: 'DELETE',
            });

            // 장바구니 다시 불러오기
            const data = await apiFetch<CartResponseDto[]>(`/api/users/cart/items`);
            const transformedItems: CartItem[] = data.map((item) => ({
                id: item.cartId.toString(),
                productId: item.productId,
                name: item.productName,
                price: item.price,
                quantity: item.quantity,
                image: item.imageUrl || 'https://via.placeholder.com/400',
                option: `Size: ${SIZE_MAP[item.size] || item.size}`,
                available: true,
                size: item.size,
            }));

            setCartItems(transformedItems);
            setSelectedItems((selected) => {
                const newSelected = new Set(selected);
                newSelected.delete(id);
                return newSelected;
            });

            // 장바구니 변경 이벤트 발생
            window.dispatchEvent(new Event('cartChange'));
        } catch (err: any) {
            alert(err.message || '상품 삭제에 실패했습니다.');
        }
    };

    // Handle delete selected
    const handleDeleteSelected = async () => {
        if (selectedItems.size === 0) return;

        try {
            // 선택된 항목들을 하나씩 삭제
            const deletePromises = Array.from(selectedItems).map((id) => {
                const item = cartItems.find((item) => item.id === id);
                if (item) {
                    return apiFetch(`/api/users/cart/items/${item.productId}`, {
                        method: 'DELETE',
                    });
                }
                return Promise.resolve();
            });

            await Promise.all(deletePromises);

            // 장바구니 다시 불러오기
            const data = await apiFetch<CartResponseDto[]>(`/api/users/cart/items`);
            const transformedItems: CartItem[] = data.map((item) => ({
                id: item.cartId.toString(),
                productId: item.productId,
                name: item.productName,
                price: item.price,
                quantity: item.quantity,
                image: item.imageUrl || 'https://via.placeholder.com/400',
                option: `Size: ${SIZE_MAP[item.size] || item.size}`,
                available: true,
                size: item.size,
            }));

            setCartItems(transformedItems);
            setSelectedItems(new Set());

            // 장바구니 변경 이벤트 발생
            window.dispatchEvent(new Event('cartChange'));
        } catch (err: any) {
            alert(err.message || '선택한 상품 삭제에 실패했습니다.');
        }
    };

    // Handle delete sold-out items
    const handleDeleteSoldOut = async () => {
        try {
            // 품절된 항목들을 하나씩 삭제
            const deletePromises = unavailableItems.map((item) => {
                return apiFetch(`/api/users/cart/items/${item.productId}`, {
                    method: 'DELETE',
                });
            });

            await Promise.all(deletePromises);

            // 장바구니 다시 불러오기
            const data = await apiFetch<CartResponseDto[]>(`/api/users/cart/items`);
            const transformedItems: CartItem[] = data.map((item) => ({
                id: item.cartId.toString(),
                productId: item.productId,
                name: item.productName,
                price: item.price,
                quantity: item.quantity,
                image: item.imageUrl || 'https://via.placeholder.com/400',
                option: `Size: ${SIZE_MAP[item.size] || item.size}`,
                available: true,
                size: item.size,
            }));

            setCartItems(transformedItems);

            // 장바구니 변경 이벤트 발생
            window.dispatchEvent(new Event('cartChange'));
        } catch (err: any) {
            alert(err.message || '품절 상품 삭제에 실패했습니다.');
        }
    };

    // Calculate total
    const calculateTotal = () => {
        return cartItems
            .filter((item) => selectedItems.has(item.id))
            .reduce((sum, item) => sum + item.price * item.quantity, 0);
    };

    const totalPrice = calculateTotal();
    const hasSelectedItems = selectedItems.size > 0;
    const allAvailableSelected = selectedItems.size === availableItems.length && availableItems.length > 0;

    // 로딩 상태
    if (loading) {
        return (
            <div className="min-h-screen flex items-center justify-center bg-gray-50">
                <div className="text-xl text-gray-600">장바구니를 불러오는 중...</div>
            </div>
        );
    }

    // 에러 상태
    if (error) {
        return (
            <div className="min-h-screen flex items-center justify-center bg-gray-50">
                <div className="text-xl text-red-600">{error}</div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gray-50">
            <div className="max-w-7xl mx-auto px-6 py-8">
                {/* Top Section */}
                <div className="mb-8">
                    <div className="flex items-center justify-between mb-4">
                        <h1 className="text-2xl text-gray-900">
                            Cart ({availableItems.length})
                        </h1>
                        <div className="text-sm text-gray-600">
                            01 Options &gt; <span className="font-semibold text-gray-900">02 Cart</span> &gt; 03 Order / Payment &gt; 04 Order Complete
                        </div>
                    </div>
                </div>

                {/* Two Column Layout */}
                <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
                    {/* Left Section - Cart Items */}
                    <div className="lg:col-span-2">
                        {/* Empty Cart */}
                        {cartItems.length === 0 && (
                            <div className="bg-white border border-gray-200 rounded-lg p-12 text-center">
                                <p className="text-gray-600 text-lg mb-4">장바구니가 비어있습니다.</p>
                                <button
                                    onClick={() => navigate('/')}
                                    className="px-6 py-2 bg-gray-900 text-white rounded-md hover:bg-gray-800 transition-colors"
                                >
                                    쇼핑하러 가기
                                </button>
                            </div>
                        )}

                        {/* Available Items */}
                        {availableItems.length > 0 && (
                            <div className="bg-white border border-gray-200 rounded-lg overflow-hidden mb-6">
                                {availableItems.map((item, index) => (
                                    <div
                                        key={item.id}
                                        className={`p-6 flex gap-4 ${
                                            index !== availableItems.length - 1 ? 'border-b border-gray-100' : ''
                                        }`}
                                    >
                                        {/* Checkbox */}
                                        <div className="flex items-start pt-1">
                                            <input
                                                type="checkbox"
                                                checked={selectedItems.has(item.id)}
                                                onChange={() => handleItemSelect(item.id)}
                                                className="w-5 h-5 rounded border-gray-300 cursor-pointer"
                                            />
                                        </div>

                                        {/* Product Image */}
                                        <div className="w-24 h-24 flex-shrink-0 bg-gray-100 rounded-md overflow-hidden">
                                            <img
                                                src={item.image}
                                                alt={item.name}
                                                className="w-full h-full object-cover"
                                            />
                                        </div>

                                        {/* Product Info */}
                                        <div className="flex-1 min-w-0">
                                            <h3 className="text-sm font-medium text-gray-900 mb-1">
                                                {item.name}
                                            </h3>
                                            {item.option && (
                                                <p className="text-xs text-gray-600 mb-3">{item.option}</p>
                                            )}
                                            <p className="text-base font-semibold text-gray-900">
                                                {item.price.toLocaleString()}원
                                            </p>
                                        </div>

                                        {/* Quantity Selector */}
                                        <div className="flex items-center gap-2">
                                            <button
                                                onClick={() => handleQuantityChange(item.id, -1)}
                                                className="w-8 h-8 flex items-center justify-center border border-gray-300 rounded-md hover:bg-gray-50 transition-colors"
                                            >
                                                <Minus className="w-4 h-4 text-gray-600" />
                                            </button>
                                            <span className="w-12 text-center text-sm text-gray-900">
                        {item.quantity}
                      </span>
                                            <button
                                                onClick={() => handleQuantityChange(item.id, 1)}
                                                className="w-8 h-8 flex items-center justify-center border border-gray-300 rounded-md hover:bg-gray-50 transition-colors"
                                            >
                                                <Plus className="w-4 h-4 text-gray-600" />
                                            </button>
                                        </div>

                                        {/* Delete Button */}
                                        <div className="flex items-start">
                                            <button
                                                onClick={() => handleDeleteItem(item.id)}
                                                className="p-2 text-gray-400 hover:text-gray-600 transition-colors"
                                            >
                                                <X className="w-5 h-5" />
                                            </button>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        )}

                        {/* Unavailable Items */}
                        {unavailableItems.length > 0 && (
                            <div className="bg-white border border-gray-200 rounded-lg overflow-hidden mb-6">
                                <div className="px-6 py-3 bg-gray-100 border-b border-gray-200">
                                    <p className="text-sm text-gray-700">Sold Out Items ({unavailableItems.length})</p>
                                </div>
                                {unavailableItems.map((item, index) => (
                                    <div
                                        key={item.id}
                                        className={`p-6 flex gap-4 opacity-50 ${
                                            index !== unavailableItems.length - 1 ? 'border-b border-gray-100' : ''
                                        }`}
                                    >
                                        {/* Checkbox (disabled) */}
                                        <div className="flex items-start pt-1">
                                            <input
                                                type="checkbox"
                                                disabled
                                                className="w-5 h-5 rounded border-gray-300"
                                            />
                                        </div>

                                        {/* Product Image */}
                                        <div className="w-24 h-24 flex-shrink-0 bg-gray-100 rounded-md overflow-hidden">
                                            <img
                                                src={item.image}
                                                alt={item.name}
                                                className="w-full h-full object-cover"
                                            />
                                        </div>

                                        {/* Product Info */}
                                        <div className="flex-1 min-w-0">
                                            <h3 className="text-sm font-medium text-gray-900 mb-1">
                                                {item.name}
                                            </h3>
                                            {item.option && (
                                                <p className="text-xs text-gray-600 mb-2">{item.option}</p>
                                            )}
                                            <p className="text-xs text-red-600 font-medium">Sold Out</p>
                                        </div>

                                        {/* Delete Button */}
                                        <div className="flex items-start ml-auto">
                                            <button
                                                onClick={() => handleDeleteItem(item.id)}
                                                className="p-2 text-gray-400 hover:text-gray-600 transition-colors"
                                            >
                                                <X className="w-5 h-5" />
                                            </button>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        )}

                        {/* Bottom Controls */}
                        {cartItems.length > 0 && (
                            <div className="bg-white border border-gray-200 rounded-lg p-4">
                                <div className="flex items-center gap-4">
                                    <label className="flex items-center gap-2 cursor-pointer">
                                        <input
                                            type="checkbox"
                                            checked={allAvailableSelected}
                                            onChange={handleSelectAll}
                                            className="w-5 h-5 rounded border-gray-300"
                                        />
                                        <span className="text-sm text-gray-700">Select All</span>
                                    </label>
                                    <div className="flex gap-2 ml-auto">
                                        <button
                                            onClick={handleDeleteSelected}
                                            disabled={!hasSelectedItems}
                                            className="px-4 py-2 text-sm border border-gray-300 rounded-md hover:bg-gray-50 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                                        >
                                            Delete Selected
                                        </button>
                                        <button
                                            onClick={handleDeleteSoldOut}
                                            disabled={unavailableItems.length === 0}
                                            className="px-4 py-2 text-sm border border-gray-300 rounded-md hover:bg-gray-50 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                                        >
                                            Delete Sold-out Items
                                        </button>
                                    </div>
                                </div>
                            </div>
                        )}
                    </div>

                    {/* Right Section - Order Summary */}
                    <div className="lg:col-span-1">
                        <div className="bg-white border border-gray-200 rounded-lg p-6 sticky top-6">
                            <h2 className="text-base font-semibold text-gray-900 mb-6">
                                Order Summary
                            </h2>

                            {/* Total Product Price */}
                            <div className="space-y-4 mb-6">
                                <div className="flex items-center justify-between">
                                    <span className="text-sm text-gray-600">Total Product Price</span>
                                    <span className="text-2xl font-bold text-gray-900">
                    {totalPrice.toLocaleString()}원
                  </span>
                                </div>
                            </div>

                            {/* Buy Now Button */}
                            <button
                                disabled={!hasSelectedItems}
                                onClick={() => {
                                    // 선택된 항목들의 cartId를 배열로 변환
                                    const selectedCartIds = Array.from(selectedItems).map(id => Number(id));

                                    // URL 파라미터로 전달
                                    const params = new URLSearchParams();
                                    params.append('cartItemIds', selectedCartIds.join(','));

                                    navigate(`/checkout?${params.toString()}`);
                                }}
                                className="w-full py-4 bg-gray-900 text-white rounded-md hover:bg-gray-800 transition-colors disabled:bg-gray-300 disabled:cursor-not-allowed text-base font-medium"
                            >
                                Buy Now
                            </button>

                            {/* Info Text */}
                            {!hasSelectedItems && (
                                <p className="text-xs text-gray-500 text-center mt-3">
                                    Please select at least one item
                                </p>
                            )}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}