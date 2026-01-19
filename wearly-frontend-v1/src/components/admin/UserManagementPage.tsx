import { useState, useEffect } from 'react';
import { Search } from 'lucide-react';
import AdminLayout from './AdminLayout';
import { apiFetch } from '../../api/http';

interface UserResponse {
  id: number;
  userName: string;
  userEmail: string;
  userNickname: string;
  introduction?: string;
  phoneNumber?: string;
  imageUrl?: string;
  createdDate: string;
  updatedDate: string;
  deletedAt?: string;
}

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
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [userTypeFilter, setUserTypeFilter] = useState<'All' | 'User' | 'Seller'>('All');
  const [searchQuery, setSearchQuery] = useState('');

  // Fetch users from API
  useEffect(() => {
    const fetchUsers = async () => {
      try {
        setLoading(true);
        setError(null);

        const allUsers: User[] = [];

        // Fetch USER type
        if (userTypeFilter === 'All' || userTypeFilter === 'User') {
          const userResponse = await apiFetch<UserResponse[]>(
            `/api/admin/users${searchQuery ? `?keyword=${encodeURIComponent(searchQuery)}` : ''}`
          );
          // 차단된 사용자는 이미 백엔드에서 필터링되므로 모두 Active로 처리
          const mappedUsers = (userResponse || []).map((u) => ({
            id: u.id,
            userId: u.userName,
            email: u.userEmail,
            nickname: u.userNickname,
            userType: 'User' as const,
            createdDate: new Date(u.createdDate).toLocaleDateString(),
            status: 'Active' as const,
          }));
          allUsers.push(...mappedUsers);
        }

        // Fetch SELLER type
        if (userTypeFilter === 'All' || userTypeFilter === 'Seller') {
          const sellerResponse = await apiFetch<UserResponse[]>(
            `/api/admin/users?userType=SELLER${searchQuery ? `&keyword=${encodeURIComponent(searchQuery)}` : ''}`
          );
          // 차단된 판매자는 이미 백엔드에서 필터링되므로 모두 Active로 처리
          const mappedSellers = (sellerResponse || []).map((s) => ({
            id: s.id,
            userId: s.userName,
            email: s.userEmail,
            nickname: s.userNickname,
            userType: 'Seller' as const,
            createdDate: new Date(s.createdDate).toLocaleDateString(),
            status: 'Active' as const,
          }));
          allUsers.push(...mappedSellers);
        }

        setUsers(allUsers);
      } catch (err: any) {
        setError(err.message || '회원 목록을 불러오는데 실패했습니다.');
        console.error('Failed to fetch users:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchUsers();
  }, [userTypeFilter, searchQuery]);

  // Handle blocking a user (soft delete)
  const handleBlockUser = async (id: number, userType: 'User' | 'Seller') => {
    if (!confirm('정말 이 회원을 차단하시겠습니까?')) {
      return;
    }

    try {
      const endpoint =
        userType === 'User'
          ? `/api/admin/users/${id}`
          : `/api/admin/users/sellers/${id}`;

      await apiFetch(endpoint, {
        method: 'DELETE',
      });

      // Remove user from local state (차단된 사용자는 목록에서 제거)
      setUsers((prevUsers) => prevUsers.filter((user) => user.id !== id));

      alert('회원이 차단되었습니다.');
    } catch (err: any) {
      alert(`차단 실패: ${err.message || '알 수 없는 오류가 발생했습니다.'}`);
      console.error('Failed to block user:', err);
    }
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

  if (loading) {
    return (
      <AdminLayout>
        <div className="p-8">
          <div className="flex items-center justify-center py-12">
            <p className="text-gray-600">회원 목록을 불러오는 중...</p>
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
          <h1 className="text-2xl font-semibold text-gray-900">회원 관리</h1>
          <p className="text-sm text-gray-600 mt-2">
            플랫폼의 모든 회원 및 판매자를 관리하세요
          </p>
        </div>

        {/* Search & Filter Section */}
        <div className="bg-white border border-gray-200 rounded-lg p-6 mb-6">
          <div className="flex items-center gap-4">
            {/* User Type Filter */}
            <div className="flex items-center gap-3">
              <label className="text-sm font-medium text-gray-700 whitespace-nowrap">
                회원 유형
              </label>
              <select
                value={userTypeFilter}
                onChange={(e) =>
                  setUserTypeFilter(e.target.value as 'All' | 'User' | 'Seller')
                }
                className="px-4 py-2 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent"
              >
                <option value="All">전체</option>
                <option value="User">일반 회원</option>
                <option value="Seller">판매자</option>
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
                  placeholder="아이디, 이메일, 또는 닉네임 검색"
                  className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-gray-900 focus:border-transparent"
                />
              </div>
            </div>

            {/* Results Count */}
            <div className="ml-auto text-sm text-gray-600">
              총 {filteredUsers.length} 명
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
                  사용자 ID
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  이메일
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  닉네임
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  유형
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  가입일
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  상태
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider">
                  관리
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
                        className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${user.userType === 'Seller'
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
                        className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${user.status === 'Active'
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
                          onClick={() => handleBlockUser(user.id, user.userType)}
                          className="px-4 py-2 text-xs font-medium text-red-600 border border-red-300 rounded-md hover:bg-red-50 transition-colors"
                        >
                          차단
                        </button>
                      ) : (
                        <span className="px-4 py-2 text-xs font-medium text-gray-400 border border-gray-200 rounded-md bg-gray-50 cursor-not-allowed inline-block">
                          차단됨
                        </span>
                      )}
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan={8} className="px-6 py-12 text-center text-sm text-gray-500">
                    검색된 회원이 없습니다
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
