import type { ReactNode } from "react";
import { Link, useLocation } from "react-router";
import { UserCircle, Package, CreditCard } from "lucide-react";

interface SellerLayoutProps {
  children: ReactNode;
}

export default function SellerLayout({ children }: SellerLayoutProps) {
  const location = useLocation();

  const menuItems = [
    {
      name: "My Profile",
      path: "/seller/profile",
      icon: UserCircle,
    },
    {
      name: "Product Management",
      path: "/seller/products",
      icon: Package,
    },
    {
      name: "Order Management",
      path: "/seller/orders",
      icon: CreditCard,
    },
  ];

  return (
    <div className="flex min-h-screen bg-gray-50">
      {/* Left Sidebar */}
      <aside className="w-64 bg-gray-900 text-white flex-shrink-0">
        <div className="p-6 border-b border-gray-800">
          <h2 className="text-xl font-semibold">Seller Dashboard</h2>
        </div>
        <nav className="p-4">
          <ul className="space-y-2">
            {menuItems.map((item) => {
              const Icon = item.icon;
              const isActive = location.pathname === item.path;
              return (
                <li key={item.path}>
                  <Link
                    to={item.path}
                    className={`flex items-center gap-3 px-4 py-3 rounded-md transition-colors ${
                      isActive
                        ? "bg-gray-800 text-white"
                        : "text-gray-300 hover:bg-gray-800 hover:text-white"
                    }`}
                  >
                    <Icon className="w-5 h-5" />
                    <span className="text-sm">{item.name}</span>
                  </Link>
                </li>
              );
            })}
          </ul>
        </nav>
      </aside>

      {/* Main Content Area */}
      <main className="flex-1">{children}</main>
    </div>
  );
}
