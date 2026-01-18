import type { ReactNode } from "react";
import { NavLink } from "react-router-dom";
import { UserCircle, Package, CreditCard } from "lucide-react";

// // 판매자 대시보드 공통 레이아웃(사이드바 + 컨텐츠 영역)
type SellerLayoutProps = {
  children: ReactNode;
};

export default function SellerLayout({ children }: SellerLayoutProps) {
  const menuItems = [
    {
      name: "My Profile",
      path: "/profile", // // sellerProfile 페이지 없으니 공용 ProfilePage 재사용 (Admin과 동일)
      icon: UserCircle,
    },
    {
      name: "Product Management",
      path: "/seller/products", // // 판매자 상품 관리
      icon: Package,
    },
    {
      name: "Order Management",
      path: "/seller/orders", // // 판매자 주문 관리
      icon: CreditCard,
    },
  ];

  return (
    <div className="flex min-h-screen bg-gray-50">
      {/* // Left Sidebar */}
      <aside className="w-64 bg-gray-900 text-white flex-shrink-0 sticky top-0 h-screen">
        <div className="p-6 border-b border-gray-800">
          <h2 className="text-xl font-semibold">Seller Dashboard</h2>
        </div>

        {/* // Sidebar Menu */}
        <nav className="p-4">
          <ul className="space-y-2">
            {menuItems.map((item) => {
              const Icon = item.icon;
              return (
                <li key={item.path}>
                  <NavLink
                    to={item.path}
                    className={({ isActive }) =>
                      `relative flex items-center gap-3 px-4 py-3 rounded-md transition-colors ${
                        isActive
                          ? "bg-gray-800 text-white"
                          : "text-gray-300 hover:bg-gray-800 hover:text-white"
                      }`
                    }
                  >
                    {({ isActive }) => (
                      <>
                        {isActive && (
                          <span className="absolute left-0 top-2 bottom-2 w-1 bg-gray-500 rounded-r" />
                        )}
                        <Icon className="w-5 h-5" />
                        <span className="text-sm">{item.name}</span>
                      </>
                    )}
                  </NavLink>
                </li>
              );
            })}
          </ul>
        </nav>
      </aside>

      {/* // Main Content Area */}
      <main className="flex-1 overflow-y-auto">{children}</main>
    </div>
  );
}
