package com.team.wearly.domain.product.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.wearly.domain.product.dto.request.ProductSearchCondition;
import com.team.wearly.domain.product.entity.Product;
import com.team.wearly.domain.product.entity.enums.Brand;
import com.team.wearly.domain.product.entity.enums.ProductCategory;
import com.team.wearly.domain.product.entity.enums.ProductStatus;
import com.team.wearly.domain.product.entity.enums.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

// QProduct static import
import static com.team.wearly.domain.product.entity.QProduct.product;
import com.querydsl.core.types.OrderSpecifier;
import com.team.wearly.domain.product.dto.request.ProductSortType;

@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Product> search(ProductSearchCondition condition, Pageable pageable) {
        List<Product> content = queryFactory
                .selectFrom(product)
                .where(
                        brandEq(condition.brand()),
                        categoryEq(condition.category()),
                        keywordLike(condition.keyword()),
                        isDisplayable())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifier(condition.sort()))
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(product.count())
                .from(product)
                .where(
                        brandEq(condition.brand()),
                        categoryEq(condition.category()),
                        keywordLike(condition.keyword()),
                        isDisplayable());

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public List<ProductCategory> findCategoriesByBrand(Brand brand) {
        return queryFactory
                .select(product.productCategory)
                .distinct()
                .from(product)
                .where(
                        brandEq(brand),
                        isDisplayable())
                .orderBy(product.productCategory.asc())
                .fetch();
    }

    private BooleanExpression isDisplayable() {
        return product.status.in(ProductStatus.ON_SALE, ProductStatus.SOLD_OUT);
    }

    private BooleanExpression brandEq(Brand brand) {
        return brand != null ? product.brand.eq(brand) : null;
    }

    private BooleanExpression categoryEq(ProductCategory category) {
        return category != null ? product.productCategory.eq(category) : null;
    }

    private BooleanExpression keywordLike(String keyword) {
        return (keyword != null && !keyword.isBlank())
                ? product.productName.containsIgnoreCase(keyword)
                : null;
    }

    private OrderSpecifier<?> getOrderSpecifier(ProductSortType sortType) {
        if (sortType == null) {
            return product.createdDate.desc(); // 기본값: 최신순
        }

        return switch (sortType) {
            case PRICE_LOW -> product.price.asc(); // 낮은 가격순
            case PRICE_HIGH -> product.price.desc(); // 높은 가격순
            case LATEST -> product.createdDate.desc(); // 최신순
        };
    }
}