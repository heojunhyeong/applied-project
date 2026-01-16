import { useState } from 'react';
import { Search } from 'lucide-react';
import AdminLayout from './AdminLayout';

interface User {
  id: number;
  userId: string;
  email: string;
  nickname: string;
  userType: 'User' | 'Seller';
  createdDate: string;
  status: 'Active' | 'Blocked';
}

export default function UserManagementPage() {
  const [users, setUsers] = useState<User[]>([
    {
      id: 1,
      userId: 'U001',
      email: 'user1@example.com',
      nickname: 'bomi123',
      userType: 'User',
      createdDate: '2024-01-15',
      status: 'Active',
    },
    {
      id: 2,
      userId: 'U002',
      email: 'seller1@example.com',
      nickname: 'fashionseller',
      userType: 'Seller',
      createdDate: '2024-01-20',
      status: 'Active',
    },
    {
      id: 3,
      userId: 'U003',
      email: 'user2@example.com',
      nickname: 'shopper_kim',
      userType: 'User',
      createdDate: '2024-02-05',
      status: 'Active',
    },
    {
      id: 4,
      userId: 'U004',
      email: 'blocked@example.com',
      nickname: 'baduser',
      userType: 'User',
      createdDate: '2024-03-10',
      status: 'Blocked',
    },
    {
      id: 5,
      userId: 'U005',
      email: 'seller2@example.com',
      nickname: 'sneaker_store',
      userType: 'Seller',
      createdDate: '2024-03-15',
      status: 'Active',
    },
    {
      id: 6,
      userId: 'U006',
      email: 'user3@example.com',
      nickname: 'fashion_lover',
      userType: 'User',
      createdDate: '2024-04-01',
      status: 'Active',
    },
    {
      id: 7,
      userId: 'U007',
      email: 'seller3@example.com',
      nickname: 'streetwear_shop',
      userType: 'Seller',
      createdDate: '2024-04-10',
      status: 'Blocked',
    },
  ]);

  const [userTypeFilter, setUserTypeFilter] = useState<'All' | 'User' | 'Seller'>('All');
  const [searchQuery, setSearchQuery] = useState('');

  // Handle blocking a user
  const handleBlockUser = (id: number) => {
    setUsers((prevUsers) =>
      prevUsers.map((user) =>
        user.id === id ? { ...user, status: 'Blocked' as const } : user
      )
    );
  };

  // Filter users based on user type and search query
  const filteredUsers = users.filter((user) => {
    // User type filter
    const matchesType =
      userTypeFilter === 'All' || user.userType === userTypeFilter;

    // Search filter
    const matchesSearch =
      searchQuery === '' ||
      user.userId.toLowerCase().includes(searchQuery.toLowerCase()) ||
      user.email.toLowerCase().includes(searchQuery.toLowerCase()) ||
      user.nickname.toLowerCase().includes(searchQuery.toLowerCase());

    return matchesType && matchesSearch;
  });

  return (
    <AdminLayout>
      <div className="p-8">
        {/* Page Header */}
        <div className="mb-8">
          <h1 className="text-2xl font-semibold text-gray-900">User Management</h1>
          <p className="text-sm text-gray-600 mt-2">
            Manage all users and sellers on the platform
          </p>
        </div>

        {/* Search & Filter Section */}
        <div className="bg-white border border-gray-200 rounded-lg p-6 mb-6">
          <div className="flex items-center gap-4">
            {/* User Type Filter */}
            <div className="flex items-center gap-3">
              <label className="text-sm font-medium text-gray-700 whitespace-nowrap">
                User Type
              </label>
              <select
                value={userTypeFilter}
                onChange={(e) =>
                  setUserTypeFilter(e.target.value as 'All' | 'User' | 'Seller')
                }
                className="px-4 py-2 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent"
              >
                <option value="All">All</option>
                <option value="User">User</option>
                <option value="Seller">Seller</option>
              </select>
            </div>

            {/* Search Input */}
            <div className="flex-1 max-w-md">
              <div className="relative">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gray-400" />
                <input
                  type="text"
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  placeholder="Search by ID, Email, or Nickname"
                  className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent"
                />
              </div>
            </div>

            {/* Results Count */}
            <div className="ml-auto text-sm text-gray-600">
              {filteredUsers.length} {filteredUsers.length === 1 ? 'user' : 'users'} found
            </div>
          </div>
        </div>

        {/* User Table */}
        <div className="bg-white border border-gray-200 rounded-lg overflow-hidden">
          <table className="w-full">
            <thead className="bg-gray-50 border-b border-gray-200">
              <tr>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  ID
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  User ID
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  Email
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  Nickname
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  User Type
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  Created Date
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
              {filteredUsers.length > 0 ? (
                filteredUsers.map((user) => (
                  <tr key={user.id} className="hover:bg-gray-50 transition-colors">
                    <td className="px-6 py-4 text-sm text-gray-900">{user.id}</td>
                    <td className="px-6 py-4 text-sm text-gray-900">{user.userId}</td>
                    <td className="px-6 py-4 text-sm text-gray-900">{user.email}</td>
                    <td className="px-6 py-4 text-sm text-gray-900">{user.nickname}</td>
                    <td className="px-6 py-4 text-sm">
                      <span
                        className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                          user.userType === 'Seller'
                            ? 'bg-blue-100 text-blue-800'
                            : 'bg-gray-100 text-gray-800'
                        }`}
                      >
                        {user.userType}
                      </span>
                    </td>
                    <td className="px-6 py-4 text-sm text-gray-900">{user.createdDate}</td>
                    <td className="px-6 py-4 text-sm">
                      <span
                        className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                          user.status === 'Active'
                            ? 'bg-green-100 text-green-800'
                            : 'bg-red-100 text-red-800'
                        }`}
                      >
                        {user.status}
                      </span>
                    </td>
                    <td className="px-6 py-4 text-sm">
                      {user.status === 'Active' ? (
                        <button
                          onClick={() => handleBlockUser(user.id)}
                          className="px-4 py-2 text-xs font-medium text-red-600 border border-red-300 rounded-md hover:bg-red-50 transition-colors"
                        >
                          Block
                        </button>
                      ) : (
                        <span className="px-4 py-2 text-xs font-medium text-gray-400 border border-gray-200 rounded-md bg-gray-50 cursor-not-allowed inline-block">
                          Blocked
                        </span>
                      )}
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan={8} className="px-6 py-12 text-center text-sm text-gray-500">
                    No users found
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </AdminLayout>
  );
}
