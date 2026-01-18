import { useState, useEffect } from 'react';
import { apiFetch } from '../api/http';
import { DollarSign, CheckCircle, Clock, AlertCircle, Calendar } from 'lucide-react';
interface SettlementSummary {
    expectAmount: number;
    completedAmount: number;
}

export default function SettlementPage() {
    const [summary, setSummary] = useState<SettlementSummary | null>(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchSummary = async () => {
            try {
                // 백엔드 SellerSettlementController의 /summary API 호출
                const data = await apiFetch<SettlementSummary>('/api/seller/settlements/summary');
                setSummary(data);
            } catch (err) {
                console.error('정산 통계 로드 실패:', err);
            } finally {
                setLoading(false);
            }
        };
        fetchSummary();
    }, []);

    if (loading) return <div className="text-center py-20">데이터를 불러오는 중...</div>;

    return (
        <main className="bg-white min-h-screen">
            {/* Hero Section: 정산 요약 (HomePage의 Hero 스타일 계승) */}
            <section className="relative h-[400px] flex items-center justify-center overflow-hidden">
                <div className="absolute inset-0 bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900">
                    <div className="absolute inset-0 opacity-20 bg-[url('https://images.unsplash.com/photo-1554224155-6726b3ff858f?q=80&w=2000')] bg-cover bg-center" />
                </div>

                <div className="relative z-10 max-w-[1400px] w-full px-8 flex flex-col md:flex-row justify-between items-center gap-12">
                    <div className="text-left">
                        <h1 className="text-4xl md:text-5xl font-bold text-white mb-4 tracking-tight">
                            Settlement Management
                        </h1>
                        <p className="text-xl text-gray-300">
                            투명하고 정확한 판매 정산 현황을 확인하세요.
                        </p>
                    </div>

                    <div className="flex gap-6">
                        {/* 정산 예정 카드 */}
                        <div className="bg-white/10 backdrop-blur-md p-8 rounded-2xl border border-white/20 min-w-[280px]">
                            <div className="flex items-center gap-3 mb-4 text-gray-300">
                                <Clock className="w-5 h-5" />
                                <span className="text-sm font-medium uppercase tracking-wider">정산 예정</span>
                            </div>
                            <h2 className="text-3xl font-bold text-white">
                                ₩{summary?.expectAmount.toLocaleString()}
                            </h2>
                        </div>

                        {/* 정산 완료 카드 */}
                        <div className="bg-white/10 backdrop-blur-md p-8 rounded-2xl border border-white/20 min-w-[280px]">
                            <div className="flex items-center gap-3 mb-4 text-green-400">
                                <CheckCircle className="w-5 h-5" />
                                <span className="text-sm font-medium uppercase tracking-wider">정산 완료</span>
                            </div>
                            <h2 className="text-3xl font-bold text-white">
                                ₩{summary?.completedAmount.toLocaleString()}
                            </h2>
                        </div>
                    </div>
                </div>
            </section>

            {/* Info Section (HomePage의 Featured Brands 스타일 계승) */}
            <section className="max-w-[1400px] mx-auto px-8 py-20">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-8 mb-16">
                    <div className="p-8 bg-gray-50 rounded-2xl border border-gray-100 flex items-start gap-6">
                        <div className="p-3 bg-gray-900 rounded-lg text-white">
                            <Calendar className="w-6 h-6" />
                        </div>
                        <div>
                            <h3 className="text-lg font-bold text-gray-900 mb-2">정산 주기 안내</h3>
                            <p className="text-gray-600 leading-relaxed">
                                Wearly는 매달 10일 정산 확정(CONFIRMED) 상태의 대금을 일괄 지급합니다.
                                배치 시스템을 통해 안전하게 귀하의 계좌로 입금됩니다.
                            </p>
                        </div>
                    </div>

                    <div className="p-8 bg-gray-50 rounded-2xl border border-gray-100 flex items-start gap-6">
                        <div className="p-3 bg-gray-900 rounded-lg text-white">
                            <AlertCircle className="w-6 h-6" />
                        </div>
                        <div>
                            <h3 className="text-lg font-bold text-gray-900 mb-2">수수료 및 정책</h3>
                            <p className="text-gray-600 leading-relaxed">
                                판매 원가 기준 10%의 서비스 수수료가 적용됩니다.
                                취소된 주문(CANCELLED)은 정산 대상에서 제외됩니다.
                            </p>
                        </div>
                    </div>
                </div>

                <div className="text-center py-20 border-t border-gray-100">
                    <h2 className="text-2xl font-bold text-gray-900 mb-4">정산 상세 내역이 필요하신가요?</h2>
                    <p className="text-gray-500 mb-8">주문별 세부 정산 내역은 엑셀 다운로드 또는 상세 조회를 통해 확인 가능합니다.</p>
                    <button className="px-8 py-3 bg-gray-900 text-white rounded-lg font-medium hover:bg-gray-800 transition-all">
                        상세 내역 보기 →
                    </button>
                </div>
            </section>
        </main>
    );
}