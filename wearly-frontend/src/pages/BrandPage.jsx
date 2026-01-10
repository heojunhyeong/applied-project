import { useParams } from 'react-router-dom';
import './BrandPage.css';

function BrandPage() {
    const { brand } = useParams();

    const brandNames = {
        nike: '나이키',
        adidas: '아디다스',
        newbalance: '뉴발란스'
    };

    // 테스트용 상품 데이터 (나중에 API로 교체)
    const products = [
        {
            id: 1,
            name: `${brandNames[brand]} 에어맥스`,
            price: '219,000원',
            image: `/images/${brand}/나이키_에어맥스.png`
        },
        {
            id: 2,
            name: `${brandNames[brand]} 에어맥스 TL 2.5`,
            price: '229,000원',
            image: `/images/${brand}/나이키_에어맥스_TL_2.5.png`
        },
        {
            id: 3,
            name: `${brandNames[brand]} 에어 포스 1 GORE-TEX 비브람`,
            price: '229,000원',
            image: `/images/${brand}/에어_포스_1_GORE-TEX_비브람.png`
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