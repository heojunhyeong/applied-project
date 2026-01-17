import { apiFetch } from './http';
import { Product } from '../types/product';

export const getProductDetail = async (id: string | number): Promise<Product> => {
    return await apiFetch<Product>(`/api/products/${id}`);
};
