import { useState } from 'react';
import { Minus, Plus, X } from 'lucide-react';
import { useNavigate } from 'react-router';

interface CartItem {
  id: string;
  name: string;
  price: number;
  quantity: number;
  image: string;
  option?: string;
  available: boolean;
}

export default function CartPage() {
  const navigate = useNavigate();
  const [cartItems, setCartItems] = useState<CartItem[]>([
    {
      id: '1',
      name: 'NIKE Air Max 270',
      price: 159000,
      quantity: 1,
      image: 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400',
      option: 'Size: 270mm, Color: Black/White',
      available: true,
    },
    {
      id: '2',
      name: 'ADIDAS Originals Hoodie',
      price: 89000,
      quantity: 2,
      image: 'https://images.unsplash.com/photo-1556821840-3a63f95609a7?w=400',
      option: 'Size: L, Color: Navy',
      available: true,
    },
    {
      id: '3',
      name: 'LEVIS 501 Original Jeans',
      price: 128000,
      quantity: 1,
      image: 'https://images.unsplash.com/photo-1542272604-787c3835535d?w=400',
      option: 'Size: 32, Color: Dark Blue',
      available: false,
    },
  ]);

  const [selectedItems, setSelectedItems] = useState<Set<string>>(
    new Set(['1', '2'])
  );

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
  const handleQuantityChange = (id: string, delta: number) => {
    setCartItems((items) =>
      items.map((item) => {
        if (item.id === id) {
          const newQuantity = Math.max(1, item.quantity + delta);
          return { ...item, quantity: newQuantity };
        }
        return item;
      })
    );
  };

  // Handle delete item
  const handleDeleteItem = (id: string) => {
    setCartItems((items) => items.filter((item) => item.id !== id));
    setSelectedItems((selected) => {
      const newSelected = new Set(selected);
      newSelected.delete(id);
      return newSelected;
    });
  };

  // Handle delete selected
  const handleDeleteSelected = () => {
    if (selectedItems.size === 0) return;
    setCartItems((items) => items.filter((item) => !selectedItems.has(item.id)));
    setSelectedItems(new Set());
  };

  // Handle delete sold-out items
  const handleDeleteSoldOut = () => {
    setCartItems((items) => items.filter((item) => item.available));
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
                onClick={() => navigate('/checkout')}
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