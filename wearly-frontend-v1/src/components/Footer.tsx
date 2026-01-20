export default function Footer() {
    return (
        <footer className="border-t border-gray-200 bg-white mt-auto">
            <div className="max-w-[1400px] mx-auto px-8 py-6">
                <div className="flex flex-col md:flex-row items-center justify-between gap-4 text-sm text-gray-600">
                    <div className="flex flex-wrap items-center justify-center gap-4">
                        <a href="#" className="hover:text-gray-900 transition-colors">
                            회사소개
                        </a>
                        <span className="text-gray-300">|</span>
                        <a href="#" className="hover:text-gray-900 transition-colors">
                            이용약관
                        </a>
                        <span className="text-gray-300">|</span>
                        <a href="#" className="hover:text-gray-900 transition-colors">
                            개인정보처리방침
                        </a>
                    </div>
                    <div className="flex items-center gap-2">
                        <span className="font-medium text-gray-700">고객센터 :</span>
                        <a href="tel:031-657-3308" className="hover:text-gray-900 transition-colors">
                            031-657-3308
                        </a>
                    </div>
                </div>
            </div>
        </footer>
    );
}