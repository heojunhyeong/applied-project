import { Fragment, useEffect, useState, type ChangeEvent } from "react";
import { useParams } from "react-router-dom";
import { ImageWithFallback } from "./figma/ImageWithFallback";
import { Package, ShoppingBag, Truck, X, Plus } from "lucide-react";

type Product = {
  id: string;
  thumbnail: string;
  name: string;
  brand: string;
  category: string;
  price: number;
  stock: number;
  status: "판매중" | "품절" | "삭제";
  detailImageUri: string;
  sizes: {
    S: boolean;
    M: boolean;
    L: boolean;
    XL: boolean;
  };
  description: string;
};

type Order = {
  id: string;
  buyerUserId: string;
  productName: string;
  quantity: number;
  price: number;
  orderStatus: "Pending" | "Completed" | "Cancelled";
  sellerProgressStatus: "WAIT_CHECK" | "CHECK" | "IN_DELIVERY" | "DELIVERY_COMPLETED";
  shippingCompany?: "CJ" | "LOTTE" | "HANJIN" | "ROZEN";
  trackingNumber?: string;
};

const mockProducts: Product[] = [
  {
    id: "P001",
    thumbnail:
      "https://images.unsplash.com/photo-1542291026-7eec264c27ff?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxuaWtlJTIwc2hvZXN8ZW58MXx8fHwxNzY4NDQ4ODQ4fDA&ixlib=rb-4.1.0&q=80&w=400&utm_source=figma&utm_medium=referral",
    name: "Air Max 90 - White/Red",
    brand: "Nike",
    category: "Shoes",
    price: 129000,
    stock: 45,
    status: "판매중",
    detailImageUri:
      "https://images.unsplash.com/photo-1542291026-7eec264c27ff?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxuaWtlJTIwc2hvZXN8ZW58MXx8fHwxNzY4NDQ4ODQ4fDA&ixlib=rb-4.1.0&q=80&w=400&utm_source=figma&utm_medium=referral",
    sizes: { S: true, M: true, L: true, XL: true },
    description:
      "The Air Max 90 is a classic sneaker that combines comfort and style. Its iconic design and cushioning make it a favorite among runners and fashion enthusiasts alike.",
  },
  {
    id: "P002",
    thumbnail:
      "https://images.unsplash.com/photo-1556906781-9a412961c28c?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxhZGlkYXMlMjBzaG9lc3xlbnwxfHx8fDE3Njg0NDg4NDh8MA&ixlib=rb-4.1.0&q=80&w=400&utm_source=figma&utm_medium=referral",
    name: "Ultraboost 22 - Black",
    brand: "Adidas",
    category: "Shoes",
    price: 189000,
    stock: 0,
    status: "품절",
    detailImageUri:
      "https://images.unsplash.com/photo-1556906781-9a412961c28c?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxhZGlkYXMlMjBzaG9lc3xlbnwxfHx8fDE3Njg0NDg4NDh8MA&ixlib=rb-4.1.0&q=80&w=400&utm_source=figma&utm_medium=referral",
    sizes: { S: true, M: true, L: true, XL: true },
    description:
      "The Ultraboost 22 is a high-performance running shoe that offers exceptional comfort and support. Its advanced cushioning system and sleek design make it a top choice for athletes and runners.",
  },
  {
    id: "P003",
    thumbnail:
      "https://images.unsplash.com/photo-1539185441755-769473a23570?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxydW5uaW5nJTIwc2hvZXN8ZW58MXx8fHwxNzY4NDQ4ODQ4fDA&ixlib=rb-4.1.0&q=80&w=400&utm_source=figma&utm_medium=referral",
    name: "Fresh Foam 1080v12",
    brand: "New Balance",
    category: "Shoes",
    price: 159000,
    stock: 28,
    status: "판매중",
    detailImageUri:
      "https://images.unsplash.com/photo-1539185441755-769473a23570?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxydW5uaW5nJTIwc2hvZXN8ZW58MXx8fHwxNzY4NDQ4ODQ4fDA&ixlib=rb-4.1.0&q=80&w=400&utm_source=figma&utm_medium=referral",
    sizes: { S: true, M: true, L: true, XL: true },
    description:
      "The Fresh Foam 1080v12 is a versatile running shoe that provides excellent cushioning and support. Its lightweight design and responsive foam make it a great choice for both training and racing.",
  },
  {
    id: "P004",
    thumbnail:
      "https://images.unsplash.com/photo-1600185365926-3a2ce3cdb9eb?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxob29kaWUlMjBmYXNoaW9ufGVufDF8fHx8MTc2ODQ0ODg0OHww&ixlib=rb-4.1.0&q=80&w=400&utm_source=figma&utm_medium=referral",
    name: "Classic Pullover Hoodie",
    brand: "The North Face",
    category: "Hoodie",
    price: 98000,
    stock: 15,
    status: "삭제",
    detailImageUri:
      "https://images.unsplash.com/photo-1600185365926-3a2ce3cdb9eb?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxob29kaWUlMjBmYXNoaW9ufGVufDF8fHx8MTc2ODQ0ODg0OHww&ixlib=rb-4.1.0&q=80&w=400&utm_source=figma&utm_medium=referral",
    sizes: { S: true, M: true, L: true, XL: true },
    description:
      "The Classic Pullover Hoodie is a warm and comfortable outerwear piece. Its soft fabric and stylish design make it a perfect choice for both casual wear and outdoor activities.",
  },
];

const mockOrders: Order[] = [
  {
    id: "ORD001",
    buyerUserId: "user123",
    productName: "Air Max 90 - White/Red",
    quantity: 2,
    price: 258000,
    orderStatus: "Completed",
    sellerProgressStatus: "WAIT_CHECK",
  },
  {
    id: "ORD002",
    buyerUserId: "user456",
    productName: "Ultraboost 22 - Black",
    quantity: 1,
    price: 189000,
    orderStatus: "Pending",
    sellerProgressStatus: "CHECK",
  },
  {
    id: "ORD003",
    buyerUserId: "user789",
    productName: "Fresh Foam 1080v12",
    quantity: 1,
    price: 159000,
    orderStatus: "Completed",
    sellerProgressStatus: "IN_DELIVERY",
    shippingCompany: "CJ",
    trackingNumber: "1234567890",
  },
  {
    id: "ORD004",
    buyerUserId: "user101",
    productName: "Air Max 90 - White/Red",
    quantity: 1,
    price: 129000,
    orderStatus: "Completed",
    sellerProgressStatus: "DELIVERY_COMPLETED",
    shippingCompany: "HANJIN",
    trackingNumber: "9876543210",
  },
  {
    id: "ORD005",
    buyerUserId: "user202",
    productName: "Classic Pullover Hoodie",
    quantity: 3,
    price: 294000,
    orderStatus: "Cancelled",
    sellerProgressStatus: "WAIT_CHECK",
  },
];

export default function SellerPage() {
  const [activeMenu, setActiveMenu] = useState<"products" | "orders">("products");
  const { tab } = useParams<{ tab?: string }>();
  const [products, setProducts] = useState<Product[]>(mockProducts);
  const [orders, setOrders] = useState<Order[]>(mockOrders);
  const [isProductModalOpen, setIsProductModalOpen] = useState(false);
  const [editingProductId, setEditingProductId] = useState<string | null>(null);
  const [orderStatusFilter, setOrderStatusFilter] = useState<"All" | Order["orderStatus"]>("All");
  const [editingOrderId, setEditingOrderId] = useState<string | null>(null);
  const [shippingInfo, setShippingInfo] = useState({
    shippingCompany: "",
    trackingNumber: "",
  });

  const setActiveMenuFromTab = (currentTab?: string) => {
    // URL tab 값에 따라 activeMenu를 설정
    if (currentTab === "orders") {
      setActiveMenu("orders");
      return;
    }
    setActiveMenu("products");
  };

  useEffect(() => {
    // URL tab 변경을 감지해 메뉴를 자동 전환
    setActiveMenuFromTab(tab);
  }, [tab]);

  // Form state
  const [formData, setFormData] = useState({
    brand: "",
    category: "",
    productName: "",
    price: "",
    stock: "",
    detailImageUri: "",
    thumbnailImageUri: "",
    sizes: {
      S: false,
      M: false,
      L: false,
      XL: false,
    },
    description: "",
  });

  const brands = [
    "Nike",
    "Adidas",
    "New Balance",
    "Reebok",
    "The North Face",
    "Vans",
  ];

  const categories = [
    "Padding",
    "Shirt",
    "Coat",
    "Hoodie",
    "Sweatshirt",
    "Jeans",
    "Shorts",
    "Muffler",
  ];

  const handleInputChange = (
    e: ChangeEvent<
      HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement
    >,
  ) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSizeChange = (size: "S" | "M" | "L" | "XL") => {
    setFormData((prev) => ({
      ...prev,
      sizes: {
        ...prev.sizes,
        [size]: !prev.sizes[size],
      },
    }));
  };

  const handleOpenProductModal = (product?: Product) => {
    if (product) {
      // Edit mode
      setEditingProductId(product.id);
      setFormData({
        brand: product.brand,
        category: product.category,
        productName: product.name,
        price: product.price.toString(),
        stock: product.stock.toString(),
        detailImageUri: product.detailImageUri,
        thumbnailImageUri: product.thumbnail,
        sizes: product.sizes,
        description: product.description,
      });
    } else {
      // Register mode
      setEditingProductId(null);
      setFormData({
        brand: "",
        category: "",
        productName: "",
        price: "",
        stock: "",
        detailImageUri: "",
        thumbnailImageUri: "",
        sizes: { S: false, M: false, L: false, XL: false },
        description: "",
      });
    }
    setIsProductModalOpen(true);
  };

  const handleCloseProductModal = () => {
    setIsProductModalOpen(false);
    setEditingProductId(null);
    setFormData({
      brand: "",
      category: "",
      productName: "",
      price: "",
      stock: "",
      detailImageUri: "",
      thumbnailImageUri: "",
      sizes: { S: false, M: false, L: false, XL: false },
      description: "",
    });
  };

  const handleSaveProduct = () => {
    if (editingProductId) {
      // Update existing product
      setProducts((prev) =>
        prev.map((product) =>
          product.id === editingProductId
            ? {
                ...product,
                brand: formData.brand,
                category: formData.category,
                name: formData.productName,
                price: Number(formData.price),
                stock: Number(formData.stock),
                detailImageUri: formData.detailImageUri,
                thumbnail: formData.thumbnailImageUri,
                sizes: formData.sizes,
                description: formData.description,
              }
            : product,
        ),
      );
      alert("Product updated successfully!");
    } else {
      // Register new product
      const newProduct: Product = {
        id: `P${String(products.length + 1).padStart(3, "0")}`,
        thumbnail: formData.thumbnailImageUri,
        name: formData.productName,
        brand: formData.brand,
        category: formData.category,
        price: Number(formData.price),
        stock: Number(formData.stock),
        status: "판매중",
        detailImageUri: formData.detailImageUri,
        sizes: formData.sizes,
        description: formData.description,
      };
      setProducts((prev) => [...prev, newProduct]);
      alert("Product registered successfully!");
    }
    handleCloseProductModal();
  };

  const handleStatusChange = (
    productId: string,
    newStatus: Product["status"],
  ) => {
    setProducts((prev) =>
      prev.map((product) =>
        product.id === productId ? { ...product, status: newStatus } : product,
      ),
    );
  };

  const getStatusColor = (status: Product["status"]) => {
    switch (status) {
      case "판매중":
        return "bg-green-100 text-green-700";
      case "품절":
        return "bg-red-100 text-red-700";
      case "삭제":
        return "bg-gray-100 text-gray-700";
      default:
        return "bg-gray-100 text-gray-700";
    }
  };

  const getOrderStatusColor = (status: Order["orderStatus"]) => {
    switch (status) {
      case "Pending":
        return "bg-yellow-100 text-yellow-700";
      case "Completed":
        return "bg-green-100 text-green-700";
      case "Cancelled":
        return "bg-red-100 text-red-700";
      default:
        return "bg-gray-100 text-gray-700";
    }
  };

  const getProgressStatusColor = (status: Order["sellerProgressStatus"]) => {
    switch (status) {
      case "WAIT_CHECK":
        return "bg-gray-100 text-gray-700";
      case "CHECK":
        return "bg-blue-100 text-blue-700";
      case "IN_DELIVERY":
        return "bg-purple-100 text-purple-700";
      case "DELIVERY_COMPLETED":
        return "bg-green-100 text-green-700";
      default:
        return "bg-gray-100 text-gray-700";
    }
  };

  const handleEnterShippingInfo = (orderId: string) => {
    const order = orders.find((o) => o.id === orderId);
    if (!order) return;

    setEditingOrderId(orderId);
    setShippingInfo({
      shippingCompany: order.shippingCompany || "",
      trackingNumber: order.trackingNumber || "",
    });
  };

  const handleSaveShippingInfo = () => {
    if (!editingOrderId) return;

    if (!shippingInfo.shippingCompany || !shippingInfo.trackingNumber) {
      alert("Please fill in all shipping information!");
      return;
    }

    setOrders((prev) =>
      prev.map((order) =>
        order.id === editingOrderId
          ? {
              ...order,
              shippingCompany: shippingInfo.shippingCompany as Order["shippingCompany"],
              trackingNumber: shippingInfo.trackingNumber,
              sellerProgressStatus: "IN_DELIVERY",
            }
          : order,
      ),
    );

    setShippingInfo({
      shippingCompany: "",
      trackingNumber: "",
    });
    setEditingOrderId(null);
    alert("Shipping info saved successfully!");
  };

  const handleCancelShippingInfo = () => {
    setShippingInfo({
      shippingCompany: "",
      trackingNumber: "",
    });
    setEditingOrderId(null);
  };

  const handleProgressStatusChange = (
    orderId: string,
    newStatus: Order["sellerProgressStatus"],
  ) => {
    const order = orders.find((o) => o.id === orderId);
    if (!order) return;

    // IN_DELIVERY로 변경하려면 shipping info가 필수
    if (newStatus === "IN_DELIVERY") {
      if (!order.shippingCompany || !order.trackingNumber) {
        alert("Shipping information is required to change status to IN_DELIVERY!");
        return;
      }
    }

    setOrders((prev) =>
      prev.map((o) =>
        o.id === orderId ? { ...o, sellerProgressStatus: newStatus } : o,
      ),
    );
  };

  const filteredOrders =
    orderStatusFilter === "All"
      ? orders
      : orders.filter((order) => order.orderStatus === orderStatusFilter);

  return (
    <div className="flex min-h-screen bg-gray-50">
      {/* Left Sidebar */}
      <aside className="w-64 bg-gray-900 text-white flex-shrink-0">
        <div className="p-6 border-b border-gray-800">
          <h2 className="text-xl font-semibold">Seller Dashboard</h2>
        </div>
        <nav className="p-4">
          <ul className="space-y-2">
            <li>
              <button
                onClick={() => setActiveMenu("products")}
                className={`w-full flex items-center gap-3 px-4 py-3 rounded-md text-left transition-colors ${
                  activeMenu === "products"
                    ? "bg-gray-800 text-white"
                    : "text-gray-300 hover:bg-gray-800 hover:text-white"
                }`}
              >
                <Package className="w-5 h-5" />
                <span className="text-sm">Product Management</span>
              </button>
            </li>
            <li>
              <button
                onClick={() => setActiveMenu("orders")}
                className={`w-full flex items-center gap-3 px-4 py-3 rounded-md text-left transition-colors ${
                  activeMenu === "orders"
                    ? "bg-gray-800 text-white"
                    : "text-gray-300 hover:bg-gray-800 hover:text-white"
                }`}
              >
                <ShoppingBag className="w-5 h-5" />
                <span className="text-sm">Order Management</span>
              </button>
            </li>
          </ul>
        </nav>
      </aside>

      {/* Main Content */}
      <main className="flex-1 p-8">
        {/* Product Management */}
        {activeMenu === "products" && (
          <div>
            <div className="flex justify-between items-center mb-6">
              <div>
                <h1 className="text-2xl font-bold text-gray-900">
                  Product Management
                </h1>
                <p className="text-gray-600 mt-1">상품 조회</p>
              </div>
              <button
                onClick={() => handleOpenProductModal()}
                className="flex items-center gap-2 px-6 py-3 bg-gray-900 text-white font-medium rounded-lg hover:bg-gray-800 transition-colors"
              >
                <Plus className="w-5 h-5" />
                Register Product
              </button>
            </div>

            <div className="bg-white rounded-lg shadow-sm overflow-hidden">
              <div className="overflow-x-auto">
                <table className="w-full">
                  <thead className="bg-gray-50 border-b border-gray-200">
                    <tr>
                      <th className="px-6 py-4 text-left text-sm font-semibold text-gray-900">
                        Product ID
                      </th>
                      <th className="px-6 py-4 text-left text-sm font-semibold text-gray-900">
                        Thumbnail
                      </th>
                      <th className="px-6 py-4 text-left text-sm font-semibold text-gray-900">
                        Product Name
                      </th>
                      <th className="px-6 py-4 text-left text-sm font-semibold text-gray-900">
                        Price
                      </th>
                      <th className="px-6 py-4 text-left text-sm font-semibold text-gray-900">
                        Stock
                      </th>
                      <th className="px-6 py-4 text-left text-sm font-semibold text-gray-900">
                        Status
                      </th>
                      <th className="px-6 py-4 text-left text-sm font-semibold text-gray-900">
                        Action
                      </th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-gray-200">
                    {products.map((product) => (
                      <tr
                        key={product.id}
                        className="hover:bg-gray-50 transition-colors"
                      >
                        <td className="px-6 py-4">
                          <span className="text-sm font-medium text-gray-900">
                            {product.id}
                          </span>
                        </td>
                        <td className="px-6 py-4">
                          <div className="w-16 h-16 rounded-lg overflow-hidden bg-gray-100">
                            <ImageWithFallback
                              src={product.thumbnail}
                              alt={product.name}
                              className="w-full h-full object-cover"
                            />
                          </div>
                        </td>
                        <td className="px-6 py-4">
                          <div>
                            <div className="text-sm font-medium text-gray-900">
                              {product.name}
                            </div>
                            <div className="text-xs text-gray-500">
                              {product.brand}
                            </div>
                          </div>
                        </td>
                        <td className="px-6 py-4">
                          <span className="text-sm text-gray-900">
                            {product.price.toLocaleString()}원
                          </span>
                        </td>
                        <td className="px-6 py-4">
                          <span className="text-sm text-gray-900">
                            {product.stock}
                          </span>
                        </td>
                        <td className="px-6 py-4">
                          <select
                            value={product.status}
                            onChange={(e) =>
                              handleStatusChange(
                                product.id,
                                e.target.value as Product["status"],
                              )
                            }
                            className={`px-3 py-1 text-xs font-medium rounded-full border-2 focus:outline-none focus:ring-2 focus:ring-gray-900 ${getStatusColor(
                              product.status,
                            )}`}
                          >
                            <option value="판매중">판매중</option>
                            <option value="품절">품절</option>
                            <option value="삭제">삭제</option>
                          </select>
                        </td>
                        <td className="px-6 py-4">
                          <button
                            className="px-4 py-2 bg-gray-900 text-white text-sm rounded-lg hover:bg-gray-800 transition-colors"
                            onClick={() => handleOpenProductModal(product)}
                          >
                            Edit
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>

              {products.length === 0 && (
                <div className="py-20 text-center">
                  <Package className="w-16 h-16 text-gray-300 mx-auto mb-4" />
                  <p className="text-gray-500">No products found</p>
                </div>
              )}
            </div>
          </div>
        )}

        {/* Order Management */}
        {activeMenu === "orders" && (
          <div>
            <div className="mb-6">
              <h1 className="text-2xl font-bold text-gray-900">
                Order Management
              </h1>
              <p className="text-gray-600 mt-1">주문 조회</p>
            </div>

            {/* Filter Section */}
            <div className="bg-white rounded-lg shadow-sm p-6 mb-6">
              <label className="block text-sm font-medium text-gray-900 mb-2">
                Order Status Filter
              </label>
              <select
                value={orderStatusFilter}
                onChange={(e) =>
                  setOrderStatusFilter(
                    e.target.value as "All" | Order["orderStatus"],
                  )
                }
                className="w-full md:w-64 px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent"
              >
                <option value="All">All</option>
                <option value="Pending">Pending</option>
                <option value="Completed">Completed</option>
                <option value="Cancelled">Cancelled</option>
              </select>
            </div>

            {/* Order List Table */}
            <div className="bg-white rounded-lg shadow-sm overflow-hidden">
              <div className="overflow-x-auto">
                <table className="w-full">
                  <thead className="bg-gray-50 border-b border-gray-200">
                    <tr>
                      <th className="px-6 py-4 text-left text-sm font-semibold text-gray-900">
                        Order ID
                      </th>
                      <th className="px-6 py-4 text-left text-sm font-semibold text-gray-900">
                        Buyer User ID
                      </th>
                      <th className="px-6 py-4 text-left text-sm font-semibold text-gray-900">
                        Product Name
                      </th>
                      <th className="px-6 py-4 text-left text-sm font-semibold text-gray-900">
                        Quantity
                      </th>
                      <th className="px-6 py-4 text-left text-sm font-semibold text-gray-900">
                        Price
                      </th>
                      <th className="px-6 py-4 text-left text-sm font-semibold text-gray-900">
                        Order Status
                      </th>
                      <th className="px-6 py-4 text-left text-sm font-semibold text-gray-900">
                        Seller Progress
                      </th>
                      <th className="px-6 py-4 text-left text-sm font-semibold text-gray-900">
                        Action
                      </th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-gray-200">
                    {filteredOrders.map((order) => (
                      <Fragment key={order.id}>
                        <tr
                          className="hover:bg-gray-50 transition-colors"
                        >
                          <td className="px-6 py-4">
                            <span className="text-sm font-medium text-gray-900">
                              {order.id}
                            </span>
                          </td>
                          <td className="px-6 py-4">
                            <span className="text-sm text-gray-900">
                              {order.buyerUserId}
                            </span>
                          </td>
                          <td className="px-6 py-4">
                            <span className="text-sm text-gray-900">
                              {order.productName}
                            </span>
                          </td>
                          <td className="px-6 py-4">
                            <span className="text-sm text-gray-900">
                              {order.quantity}
                            </span>
                          </td>
                          <td className="px-6 py-4">
                            <span className="text-sm text-gray-900">
                              {order.price.toLocaleString()}원
                            </span>
                          </td>
                          <td className="px-6 py-4">
                            <span
                              className={`px-3 py-1 text-xs font-medium rounded-full ${getOrderStatusColor(
                                order.orderStatus,
                              )}`}
                            >
                              {order.orderStatus}
                            </span>
                          </td>
                          <td className="px-6 py-4">
                            <select
                              value={order.sellerProgressStatus}
                              onChange={(e) =>
                                handleProgressStatusChange(
                                  order.id,
                                  e.target.value as Order["sellerProgressStatus"],
                                )
                              }
                              className={`px-3 py-1 text-xs font-medium rounded-full border-2 focus:outline-none focus:ring-2 focus:ring-gray-900 ${getProgressStatusColor(
                                order.sellerProgressStatus,
                              )}`}
                            >
                              <option value="WAIT_CHECK">WAIT_CHECK</option>
                              <option value="CHECK">CHECK</option>
                              <option value="IN_DELIVERY">IN_DELIVERY</option>
                              <option value="DELIVERY_COMPLETED">DELIVERY_COMPLETED</option>
                            </select>
                          </td>
                          <td className="px-6 py-4">
                            <button
                              className="px-4 py-2 bg-gray-900 text-white text-sm rounded-lg hover:bg-gray-800 transition-colors flex items-center gap-2"
                              onClick={() => handleEnterShippingInfo(order.id)}
                            >
                              <Truck className="w-4 h-4" />
                              Enter Shipping Info
                            </button>
                          </td>
                        </tr>

                        {/* 선택된 주문의 배송 정보를 입력하는 영역 */}
                        {editingOrderId === order.id && (
                          <tr>
                            <td colSpan={8} className="px-6 py-6 bg-gray-50">
                              <div className="max-w-3xl">
                                <h4 className="text-sm font-semibold text-gray-900 mb-4">
                                  Shipping Information for {order.id}
                                </h4>
                                <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
                                  <div>
                                    <label className="block text-sm font-medium text-gray-900 mb-2">
                                      Shipping Company{" "}
                                      <span className="text-red-500">*</span>
                                    </label>
                                    <select
                                      value={shippingInfo.shippingCompany}
                                      onChange={(e) =>
                                        setShippingInfo((prev) => ({
                                          ...prev,
                                          shippingCompany: e.target.value,
                                        }))
                                      }
                                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-gray-900"
                                    >
                                      <option value="">Select Shipping Company</option>
                                      <option value="CJ">CJ</option>
                                      <option value="LOTTE">LOTTE</option>
                                      <option value="HANJIN">HANJIN</option>
                                      <option value="ROZEN">ROZEN</option>
                                    </select>
                                  </div>
                                  <div>
                                    <label className="block text-sm font-medium text-gray-900 mb-2">
                                      Tracking Number{" "}
                                      <span className="text-red-500">*</span>
                                    </label>
                                    <input
                                      type="text"
                                      value={shippingInfo.trackingNumber}
                                      onChange={(e) =>
                                        setShippingInfo((prev) => ({
                                          ...prev,
                                          trackingNumber: e.target.value,
                                        }))
                                      }
                                      placeholder="e.g., 1234567890"
                                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-gray-900"
                                    />
                                  </div>
                                </div>
                                <div className="flex gap-3">
                                  <button
                                    onClick={handleSaveShippingInfo}
                                    className="px-6 py-2 bg-gray-900 text-white text-sm font-medium rounded-lg hover:bg-gray-800 transition-colors"
                                  >
                                    Save
                                  </button>
                                  <button
                                    onClick={handleCancelShippingInfo}
                                    className="px-6 py-2 bg-white text-gray-900 text-sm font-medium border-2 border-gray-300 rounded-lg hover:bg-gray-50 transition-colors"
                                  >
                                    Cancel
                                  </button>
                                </div>
                              </div>
                            </td>
                          </tr>
                        )}
                      </Fragment>
                    ))}
                  </tbody>
                </table>
              </div>

              {filteredOrders.length === 0 && (
                <div className="py-20 text-center">
                  <ShoppingBag className="w-16 h-16 text-gray-300 mx-auto mb-4" />
                  <p className="text-gray-500">No orders found</p>
                </div>
              )}
            </div>
          </div>
        )}
      </main>

      {/* Product Registration/Edit Modal */}
      {isProductModalOpen && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-lg shadow-xl max-w-4xl w-full max-h-[90vh] overflow-y-auto">
            {/* Modal Header */}
            <div className="sticky top-0 bg-white border-b border-gray-200 px-8 py-6 flex justify-between items-center">
              <h2 className="text-2xl font-bold text-gray-900">
                {editingProductId ? "상품 수정" : "상품 등록"}
              </h2>
              <button
                onClick={handleCloseProductModal}
                className="p-2 hover:bg-gray-100 rounded-full transition-colors"
              >
                <X className="w-6 h-6 text-gray-500" />
              </button>
            </div>

            {/* Modal Content */}
            <div className="px-8 py-6">
              {/* Basic Information Section */}
              <div className="mb-8">
                <h3 className="text-lg font-semibold text-gray-900 mb-4 pb-3 border-b border-gray-200">
                  Basic Information
                </h3>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  {/* Brand */}
                  <div>
                    <label className="block text-sm font-medium text-gray-900 mb-2">
                      Brand <span className="text-red-500">*</span>
                    </label>
                    <select
                      name="brand"
                      value={formData.brand}
                      onChange={handleInputChange}
                      className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent"
                    >
                      <option value="">Select Brand</option>
                      {brands.map((brand) => (
                        <option key={brand} value={brand}>
                          {brand}
                        </option>
                      ))}
                    </select>
                  </div>

                  {/* Category */}
                  <div>
                    <label className="block text-sm font-medium text-gray-900 mb-2">
                      Category <span className="text-red-500">*</span>
                    </label>
                    <select
                      name="category"
                      value={formData.category}
                      onChange={handleInputChange}
                      className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent"
                    >
                      <option value="">Select Category</option>
                      {categories.map((category) => (
                        <option key={category} value={category}>
                          {category}
                        </option>
                      ))}
                    </select>
                  </div>

                  {/* Product Name */}
                  <div className="md:col-span-2">
                    <label className="block text-sm font-medium text-gray-900 mb-2">
                      Product Name <span className="text-red-500">*</span>
                    </label>
                    <input
                      type="text"
                      name="productName"
                      value={formData.productName}
                      onChange={handleInputChange}
                      placeholder="Enter product name"
                      className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent"
                    />
                  </div>

                  {/* Price */}
                  <div>
                    <label className="block text-sm font-medium text-gray-900 mb-2">
                      Price (₩) <span className="text-red-500">*</span>
                    </label>
                    <input
                      type="number"
                      name="price"
                      value={formData.price}
                      onChange={handleInputChange}
                      placeholder="0"
                      className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent"
                    />
                  </div>

                  {/* Stock */}
                  <div>
                    <label className="block text-sm font-medium text-gray-900 mb-2">
                      Stock <span className="text-red-500">*</span>
                    </label>
                    <input
                      type="number"
                      name="stock"
                      value={formData.stock}
                      onChange={handleInputChange}
                      placeholder="0"
                      className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent"
                    />
                  </div>
                </div>
              </div>

              {/* Images Section */}
              <div className="mb-8">
                <h3 className="text-lg font-semibold text-gray-900 mb-4 pb-3 border-b border-gray-200">
                  Images
                </h3>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  {/* Detail Image URI */}
                  <div>
                    <label className="block text-sm font-medium text-gray-900 mb-2">
                      Detail Image URI <span className="text-red-500">*</span>
                    </label>
                    <input
                      type="text"
                      name="detailImageUri"
                      value={formData.detailImageUri}
                      onChange={handleInputChange}
                      placeholder="https://example.com/detail-image.jpg"
                      className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent"
                    />
                  </div>

                  {/* Thumbnail Image URI */}
                  <div>
                    <label className="block text-sm font-medium text-gray-900 mb-2">
                      Thumbnail Image URI{" "}
                      <span className="text-red-500">*</span>
                    </label>
                    <input
                      type="text"
                      name="thumbnailImageUri"
                      value={formData.thumbnailImageUri}
                      onChange={handleInputChange}
                      placeholder="https://example.com/thumbnail.jpg"
                      className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent"
                    />
                  </div>
                </div>
              </div>

              {/* Size Selection Section */}
              <div className="mb-8">
                <h3 className="text-lg font-semibold text-gray-900 mb-4 pb-3 border-b border-gray-200">
                  Size Selection
                </h3>
                <div className="flex gap-4">
                  {(["S", "M", "L", "XL"] as const).map((size) => (
                    <label
                      key={size}
                      className="flex items-center gap-3 px-6 py-3 border-2 border-gray-300 rounded-lg cursor-pointer hover:border-gray-900 transition-colors"
                    >
                      <input
                        type="checkbox"
                        checked={formData.sizes[size]}
                        onChange={() => handleSizeChange(size)}
                        className="w-5 h-5 text-gray-900 border-gray-300 rounded focus:ring-2 focus:ring-gray-900"
                      />
                      <span className="text-sm font-medium text-gray-900">
                        {size}
                      </span>
                    </label>
                  ))}
                </div>
              </div>

              {/* Description Section */}
              <div className="mb-8">
                <h3 className="text-lg font-semibold text-gray-900 mb-4 pb-3 border-b border-gray-200">
                  Description
                </h3>
                <div>
                  <label className="block text-sm font-medium text-gray-900 mb-2">
                    Product Description{" "}
                    <span className="text-red-500">*</span>
                  </label>
                  <textarea
                    name="description"
                    value={formData.description}
                    onChange={handleInputChange}
                    placeholder="Enter detailed product description..."
                    rows={6}
                    className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent resize-none"
                  />
                </div>
              </div>
            </div>

            {/* Modal Footer */}
            <div className="sticky bottom-0 bg-white border-t border-gray-200 px-8 py-6 flex gap-4">
              <button
                onClick={handleSaveProduct}
                className="flex-1 md:flex-none px-8 py-3 bg-gray-900 text-white font-medium rounded-lg hover:bg-gray-800 transition-colors"
              >
                {editingProductId ? "Update Product" : "Register"}
              </button>
              <button
                onClick={handleCloseProductModal}
                className="flex-1 md:flex-none px-8 py-3 bg-white text-gray-900 font-medium border-2 border-gray-300 rounded-lg hover:bg-gray-50 transition-colors"
              >
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}