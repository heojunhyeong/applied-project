export interface Product {
    id: number;
    sellerId: number;
    productName: string;
    price: number;
    status: 'ON_SALE' | 'SOLD_OUT' | 'STOPPED';
    stockQuantity: number;
    productCategory: string;
    description?: string;
    images?: string[]; // Assuming backend returns images
    discountRate?: number; // Optional, might not be in DB yet
    createdDate: string;
    updatedDate: string;
}

export interface ProductDetail extends Product {
    // Additional fields for detail view if any
}
