import { useParams, Link } from "react-router";
import {
  ArrowLeft,
  Search,
  X,
  ArrowUpDown,
} from "lucide-react";
import { ImageWithFallback } from "../components/figma/ImageWithFallback";
import { useState, type FormEvent, type KeyboardEvent } from "react";

const brandData: Record<
  string,
  {
    name: string;
    products: Array<{
      id: number;
      name: string;
      price: number;
      category: string;
      createdAt: string;
    }>;
  }
> = {
  levis: {
    name: "LEVI'S",
    products: [
      {
        id: 1,
        name: "501 Original Fit Jeans",
        price: 98,
        category: "JEANS",
        createdAt: "2024-01-15",
      },
      {
        id: 2,
        name: "511 Slim Fit Jeans",
        price: 88,
        category: "JEANS",
        createdAt: "2024-01-20",
      },
      {
        id: 3,
        name: "Classic Trucker Jacket",
        price: 128,
        category: "COAT",
        createdAt: "2024-01-10",
      },
      {
        id: 4,
        name: "Vintage Fit T-Shirt",
        price: 32,
        category: "SHIRT",
        createdAt: "2024-01-25",
      },
      {
        id: 5,
        name: "505 Regular Fit Jeans",
        price: 98,
        category: "JEANS",
        createdAt: "2024-01-18",
      },
      {
        id: 6,
        name: "Sherpa Lined Jacket",
        price: 148,
        category: "COAT",
        createdAt: "2024-01-12",
      },
      {
        id: 7,
        name: "Western Denim Shirt",
        price: 78,
        category: "SHIRT",
        createdAt: "2024-01-22",
      },
      {
        id: 8,
        name: "Ribcage Straight Ankle",
        price: 98,
        category: "JEANS",
        createdAt: "2024-01-28",
      },
    ],
  },
  nike: {
    name: "NIKE",
    products: [
      {
        id: 1,
        name: "Air Max 270",
        price: 160,
        category: "SHORTS",
        createdAt: "2024-01-14",
      },
      {
        id: 2,
        name: "Sportswear Club Hoodie",
        price: 55,
        category: "HOODIE",
        createdAt: "2024-01-19",
      },
      {
        id: 3,
        name: "Dri-FIT Running Shirt",
        price: 35,
        category: "SHIRT",
        createdAt: "2024-01-11",
      },
      {
        id: 4,
        name: "Air Force 1",
        price: 110,
        category: "SHORTS",
        createdAt: "2024-01-24",
      },
      {
        id: 5,
        name: "Tech Fleece Joggers",
        price: 110,
        category: "JEANS",
        createdAt: "2024-01-17",
      },
      {
        id: 6,
        name: "Swoosh Sports Bra",
        price: 45,
        category: "SHIRT",
        createdAt: "2024-01-21",
      },
      {
        id: 7,
        name: "React Infinity Run",
        price: 160,
        category: "SHORTS",
        createdAt: "2024-01-27",
      },
      {
        id: 8,
        name: "Therma Training Hoodie",
        price: 70,
        category: "HOODIE",
        createdAt: "2024-01-29",
      },
    ],
  },
  adidas: {
    name: "ADIDAS",
    products: [
      {
        id: 1,
        name: "Ultraboost 22",
        price: 190,
        category: "SHORTS",
        createdAt: "2024-01-13",
      },
      {
        id: 2,
        name: "Essentials Hoodie",
        price: 65,
        category: "HOODIE",
        createdAt: "2024-01-18",
      },
      {
        id: 3,
        name: "Tiro Track Pants",
        price: 50,
        category: "JEANS",
        createdAt: "2024-01-16",
      },
      {
        id: 4,
        name: "Stan Smith Sneakers",
        price: 90,
        category: "SHORTS",
        createdAt: "2024-01-23",
      },
      {
        id: 5,
        name: "Adicolor Classics T-Shirt",
        price: 30,
        category: "SHIRT",
        createdAt: "2024-01-26",
      },
      {
        id: 6,
        name: "Windbreaker Jacket",
        price: 85,
        category: "COAT",
        createdAt: "2024-01-15",
      },
      {
        id: 7,
        name: "Forum Low Sneakers",
        price: 100,
        category: "SHORTS",
        createdAt: "2024-01-20",
      },
      {
        id: 8,
        name: "Originals Firebird Track Top",
        price: 75,
        category: "COAT",
        createdAt: "2024-01-28",
      },
    ],
  },
  "new-balance": {
    name: "NEW BALANCE",
    products: [
      {
        id: 1,
        name: "574 Core",
        price: 85,
        category: "SHORTS",
        createdAt: "2024-01-12",
      },
      {
        id: 2,
        name: "990v5",
        price: 185,
        category: "SHORTS",
        createdAt: "2024-01-17",
      },
      {
        id: 3,
        name: "Essentials Hoodie",
        price: 70,
        category: "HOODIE",
        createdAt: "2024-01-14",
      },
      {
        id: 4,
        name: "Athletics Windbreaker",
        price: 90,
        category: "COAT",
        createdAt: "2024-01-22",
      },
      {
        id: 5,
        name: "Impact Run Shorts",
        price: 45,
        category: "SHORTS",
        createdAt: "2024-01-25",
      },
      {
        id: 6,
        name: "Classic Logo T-Shirt",
        price: 28,
        category: "SHIRT",
        createdAt: "2024-01-19",
      },
      {
        id: 7,
        name: "Fresh Foam 1080v12",
        price: 165,
        category: "SHORTS",
        createdAt: "2024-01-27",
      },
      {
        id: 8,
        name: "Q Speed Fuel Jacquard",
        price: 80,
        category: "SWEATSHIRT",
        createdAt: "2024-01-29",
      },
    ],
  },
  "the-north-face": {
    name: "THE NORTH FACE",
    products: [
      {
        id: 1,
        name: "Nuptse Jacket",
        price: 299,
        category: "PADDING",
        createdAt: "2024-01-11",
      },
      {
        id: 2,
        name: "Denali Fleece",
        price: 179,
        category: "HOODIE",
        createdAt: "2024-01-16",
      },
      {
        id: 3,
        name: "Base Camp Duffel",
        price: 149,
        category: "COAT",
        createdAt: "2024-01-13",
      },
      {
        id: 4,
        name: "Borealis Backpack",
        price: 99,
        category: "COAT",
        createdAt: "2024-01-21",
      },
      {
        id: 5,
        name: "Essential Hoodie",
        price: 75,
        category: "HOODIE",
        createdAt: "2024-01-24",
      },
      {
        id: 6,
        name: "Hiking Pants",
        price: 89,
        category: "JEANS",
        createdAt: "2024-01-18",
      },
      {
        id: 7,
        name: "Thermoball Eco Jacket",
        price: 229,
        category: "PADDING",
        createdAt: "2024-01-26",
      },
      {
        id: 8,
        name: "Recon Backpack",
        price: 119,
        category: "COAT",
        createdAt: "2024-01-28",
      },
    ],
  },
};

const categories = [
  { id: "ALL", label: "전체" },
  { id: "PADDING", label: "패딩" },
  { id: "SHIRT", label: "셔츠" },
  { id: "COAT", label: "코트" },
  { id: "HOODIE", label: "후드" },
  { id: "SWEATSHIRT", label: "맨투맨" },
  { id: "JEANS", label: "청바지" },
  { id: "SHORTS", label: "반바지" },
];

export default function BrandPage() {
  const { brandName } = useParams<{ brandName: string }>();
  const brand = brandName ? brandData[brandName] : null;

  if (!brand) {
    return (
      <div className="max-w-[1400px] mx-auto px-8 py-20">
        <p className="text-gray-600">Brand not found</p>
      </div>
    );
  }

  const [selectedCategory, setSelectedCategory] =
    useState("ALL");
  const [searchQuery, setSearchQuery] = useState("");
  const [searchInput, setSearchInput] = useState("");
  const [isSearchActive, setIsSearchActive] = useState(false);
  const [sortBy, setSortBy] = useState<
    "latest" | "price-low" | "price-high"
  >("latest");

  // Filter by category first
  const categoryFilteredProducts =
    selectedCategory === "ALL"
      ? brand.products
      : brand.products.filter(
          (product) => product.category === selectedCategory,
        );

  // Then filter by search query if search is active
  const searchFilteredProducts =
    isSearchActive && searchQuery
      ? categoryFilteredProducts.filter((product) =>
          product.name
            .toLowerCase()
            .includes(searchQuery.toLowerCase()),
        )
      : categoryFilteredProducts;

  // Sort products
  const filteredProducts = [...searchFilteredProducts].sort(
    (a, b) => {
      if (sortBy === "latest") {
        return (
          new Date(b.createdAt).getTime() -
          new Date(a.createdAt).getTime()
        );
      } else if (sortBy === "price-low") {
        return a.price - b.price;
      } else if (sortBy === "price-high") {
        return b.price - a.price;
      }
      return 0;
    },
  );

  // Handle search
  const handleSearch = (e?: FormEvent) => {
    if (e) e.preventDefault();
    setSearchQuery(searchInput);
    setIsSearchActive(true);
  };

  // Handle clear search
  const handleClearSearch = () => {
    setSearchInput("");
    setSearchQuery("");
    setIsSearchActive(false);
  };

  // Handle Enter key
  const handleKeyDown = (e: KeyboardEvent) => {
    if (e.key === "Enter") {
      handleSearch();
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Brand Header */}
      <div className="bg-white border-b border-gray-200">
        <div className="max-w-[1400px] mx-auto px-8 py-12">
          <Link
            to="/"
            className="inline-flex items-center gap-2 text-sm text-gray-600 hover:text-gray-900 mb-6 transition-colors"
          >
            <ArrowLeft className="w-4 h-4" />
            Back to brands
          </Link>

          <h1 className="text-5xl font-bold text-gray-900 tracking-tight">
            {brand.name}
          </h1>
          <p className="text-lg text-gray-600 mt-3 mb-6">
            Explore our collection of {brand.name.toLowerCase()} products
          </p>

          {/* Search Bar */}
          <form onSubmit={handleSearch} className="mt-6">
            <div className="relative max-w-xl">
              <input
                type="text"
                value={searchInput}
                onChange={(e) => setSearchInput(e.target.value)}
                onKeyDown={handleKeyDown}
                placeholder="Search by product name"
                className="w-full h-12 pl-4 pr-24 text-gray-900 bg-gray-50 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent"
              />
              {searchInput && (
                <button
                  type="button"
                  onClick={() => setSearchInput('')}
                  className="absolute right-20 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600"
                >
                  <X className="w-5 h-5" />
                </button>
              )}
              <button
                type="submit"
                className="absolute right-1 top-1/2 -translate-y-1/2 h-10 px-6 bg-gray-900 text-white text-sm rounded-md hover:bg-gray-800 transition-colors"
              >
                <Search className="w-4 h-4" />
              </button>
            </div>
          </form>
        </div>
      </div>

      {/* Category Filter */}
      <div className="bg-white border-b border-gray-200">
        <div className="max-w-[1400px] mx-auto px-8 py-6">
          <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
            {/* Category Filters */}
            <div className="flex flex-wrap gap-3">
              {categories.map((category) => (
                <button
                  key={category.id}
                  onClick={() =>
                    setSelectedCategory(category.id)
                  }
                  className={`px-6 py-2.5 text-sm font-medium rounded-full transition-all duration-200 ${
                    selectedCategory === category.id
                      ? "bg-gray-900 text-white shadow-sm"
                      : "bg-gray-100 text-gray-700 hover:bg-gray-200"
                  }`}
                >
                  {category.label}
                </button>
              ))}
            </div>

            {/* Sort Dropdown */}
            <div className="flex items-center gap-2">
              <ArrowUpDown className="w-4 h-4 text-gray-600" />
              <select
                value={sortBy}
                onChange={(e) =>
                  setSortBy(
                    e.target.value as
                      | "latest"
                      | "price-low"
                      | "price-high",
                  )
                }
                className="h-10 px-4 pr-10 text-sm text-gray-900 bg-white border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent cursor-pointer"
              >
                <option value="latest">최신순</option>
                <option value="price-low">낮은가격순</option>
                <option value="price-high">높은가격순</option>
              </select>
            </div>
          </div>
        </div>
      </div>

      {/* Products Grid */}
      <div className="max-w-[1400px] mx-auto px-8 py-12">
        {filteredProducts.length > 0 ? (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
            {filteredProducts.map((product) => (
              <Link
                key={product.id}
                to={`/product/${product.id}`}
                className="group bg-white rounded-lg overflow-hidden shadow-sm hover:shadow-lg transition-all duration-300 border border-gray-100"
              >
                <div className="aspect-[3/4] bg-gradient-to-br from-gray-100 to-gray-50 overflow-hidden">
                  <ImageWithFallback
                    src="https://images.unsplash.com/photo-1630948688037-aa88dc433a57?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxmYXNoaW9uJTIwY2xvdGhpbmclMjBwcm9kdWN0fGVufDF8fHx8MTc2ODQ1ODUxM3ww&ixlib=rb-4.1.0&q=80&w=1080&utm_source=figma&utm_medium=referral"
                    alt={product.name}
                    className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
                  />
                </div>
                <div className="p-5">
                  <p className="text-xs text-gray-500 mb-2 uppercase tracking-wider">
                    {product.category}
                  </p>
                  <h3 className="text-sm font-medium text-gray-900 mb-3 line-clamp-2">
                    {product.name}
                  </h3>
                  <div className="flex items-center justify-between">
                    <p className="text-lg font-bold text-gray-900">
                      ${product.price}
                    </p>
                    <span className="px-4 py-2 bg-gray-900 text-white text-xs rounded group-hover:bg-gray-800 transition-colors">
                      View Details
                    </span>
                  </div>
                </div>
              </Link>
            ))}
          </div>
        ) : (
          <div className="flex flex-col items-center justify-center py-20">
            <div className="w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center mb-4">
              <Search className="w-8 h-8 text-gray-400" />
            </div>
            <p className="text-lg text-gray-600">
              {isSearchActive
                ? "No products found for your search."
                : "No products found in this category."}
            </p>
            <button
              onClick={() => {
                if (isSearchActive) {
                  handleClearSearch();
                } else {
                  setSelectedCategory("ALL");
                }
              }}
              className="mt-6 px-6 py-2 bg-gray-900 text-white text-sm rounded-lg hover:bg-gray-800 transition-colors"
            >
              {isSearchActive
                ? "Clear Search"
                : "View All Products"}
            </button>
          </div>
        )}
      </div>
    </div>
  );
}