package com.team.wearly.domain.product.repository;

import com.team.wearly.domain.product.dto.request.ProductSearchCondition;
import com.team.wearly.domain.product.entity.Product;
import com.team.wearly.domain.product.entity.enums.Brand;
import com.team.wearly.domain.product.entity.enums.ProductCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ProductRepositoryCustom {
    Page<Product> search(ProductSearchCondition condition, Pageable pageable);
    List<ProductCategory> findCategoriesByBrand(Brand brand);
}