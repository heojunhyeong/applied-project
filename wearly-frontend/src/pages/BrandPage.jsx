import { useParams } from 'react-router-dom';
import './BrandPage.css';

function BrandPage() {
    const { brand } = useParams();

    // 브랜드명 매핑 (URL → 표시명)
    const brandNames = {
        nike: '나이키',
        adidas: '아디다스',
        newbalance: '뉴발란스',
        'the-north-face': '더 노스 페이스'
    };

    // 테스트용 상품 데이터 (나중에 API로 교체)
    const products = [
        {
            id: 1,
            name: `${brandNames[brand] || brand} 상품 1`,
            price: '159,000원',
            image: `/images/${brand}/product1.jpg`
        },
        {
            id: 2,
            name: `${brandNames[brand] || brand} 상품 2`,
            price: '129,000원',
            image: `/images/${brand}/product2.jpg`
        },
        {
            id: 3,
            name: `${brandNames[brand] || brand} 상품 3`,
            price: '139,000원',
            image: `/images/${brand}/product3.jpg`
        }
    ];

    return (
        <div className="brand-page">
            <h1 className="page-title">{brandNames[brand] || brand}</h1>
            <div className="products-grid">
                {products.map((product) => (
                    <div key={product.id} className="product-card">
                        <div className="product-image-container">
                            <img
                                src={product.image}
                                alt={product.name}
                                className="product-image"
                            />
                        </div>
                        <div className="product-info">
                            <h3 className="product-name">{product.name}</h3>
                            <p className="product-price">{product.price}</p>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
}

export default BrandPage;