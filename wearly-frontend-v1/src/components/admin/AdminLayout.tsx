import type { ReactNode } from 'react';
import { Link, useLocation } from 'react-router';
import { UserCircle, Users, Package, CreditCard } from 'lucide-react';

interface AdminLayoutProps {
  children: ReactNode;
}

export default function AdminLayout({ children }: AdminLayoutProps) {
  const location = useLocation();

  const menuItems = [
    {
      name: '내 프로필',
      path: '/profile',
      icon: UserCircle,
    },
    {
      name: '회원 관리',
      path: '/admin/users',
      icon: Users,
    },
    {
      name: '상품 관리',
      path: '/admin/products',
      icon: Package,
    },
    {
      name: '주문 관리',
      path: '/admin/orders',
      icon: CreditCard,
    },
    {
      name: '리뷰 관리',
      path: '/admin/reviews',
      icon: Users,
    },
  ];

  return (
    <div className="flex min-h-screen bg-gray-50">
      {/* Left Sidebar */}
      <aside className="w-64 bg-gray-900 text-white flex-shrink-0">
        <div className="p-6 border-b border-gray-800">
          <h2 className="text-xl font-semibold">관리자 대시보드</h2>
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
                    className={`flex items-center gap-3 px-4 py-3 rounded-md transition-colors ${isActive
                        ? 'bg-gray-800 text-white'
                        : 'text-gray-300 hover:bg-gray-800 hover:text-white'
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
      <main className="flex-1">
        {children}
      </main>
    </div>
  );
}