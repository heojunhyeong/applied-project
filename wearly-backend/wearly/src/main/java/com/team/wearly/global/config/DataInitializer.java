package com.team.wearly.global.config;

import com.team.wearly.domain.user.entity.Admin;
import com.team.wearly.domain.user.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {
    
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private static final String DEFAULT_ADMIN_USERNAME = "admin123";
    
    /**
     * 애플리케이션 시작 시 기본 admin 계정 생성 (admin 테이블에 저장)
     * 아이디: admin123
     * 비밀번호: admin123!
     * 사용자가 직접 삭제하지 않는 이상 삭제되지 않습니다.
     */
    @Bean
    public CommandLineRunner initDefaultAdmin() {
        return args -> {
            // admin123 계정이 이미 존재하는지 확인 (admin 테이블에서)
            boolean adminExists = adminRepository.findByUserName(DEFAULT_ADMIN_USERNAME)
                    .isPresent();
            
            if (!adminExists) {
                // admin123 계정 생성 (admin 테이블에 저장)
                Admin admin = Admin.builder()
                        .userName(DEFAULT_ADMIN_USERNAME)
                        .userPassword(passwordEncoder.encode("admin123!"))  // 비밀번호: admin123!
                        .userEmail("admin@wearly.com")
                        .userNickname("관리자")
                        .build();
                
                Admin savedAdmin = adminRepository.save(admin);
                logger.info("기본 관리자 계정이 생성되었습니다. 테이블: admin, 사용자명: admin123, 이메일: admin@wearly.com, 닉네임: 관리자");
            } else {
                logger.info("기본 관리자 계정(admin123)이 이미 admin 테이블에 존재합니다.");
            }
        };
    }

    /**
     * 애플리케이션 시작 시 상품 샘플 데이터 10개 삽입
     * 이미 상품이 존재하는 경우 삽입하지 않습니다.
     */
    @Bean
    public CommandLineRunner initSampleProducts() {
        return args -> {
            // 이미 상품이 존재하는지 확인
            long productCount = productRepository.count();
            
            if (productCount == 0) {
                logger.info("상품 샘플 데이터를 삽입합니다...");
                
                // 상품 1: NIKE 에어 윈드러너 재킷
                Product product1 = Product.builder()
                        .productName("NIKE 에어 윈드러너 재킷")
                        .price(129000L)
                        .stockQuantity(25L)
                        .description("https://picsum.photos/id/1011/1200/800")
                        .imageUrl("https://picsum.photos/id/1011/600/600")
                        .brand(Brand.NIKE)
                        .productCategory(ProductCategory.COAT)
                        .status(ProductStatus.ON_SALE)
                        .availableSizes(Set.of(Size.SMALL, Size.MEDIUM, Size.LARGE))
                        .build();
                productRepository.save(product1);

                // 상품 2: ADIDAS 클래식 후디
                Product product2 = Product.builder()
                        .productName("ADIDAS 클래식 후디")
                        .price(89000L)
                        .stockQuantity(40L)
                        .description("https://picsum.photos/id/1012/1200/800")
                        .imageUrl("https://picsum.photos/id/1012/600/600")
                        .brand(Brand.ADIDAS)
                        .productCategory(ProductCategory.HOODIE)
                        .status(ProductStatus.ON_SALE)
                        .availableSizes(Set.of(Size.MEDIUM, Size.LARGE, Size.EXTRA_LARGE))
                        .build();
                productRepository.save(product2);

                // 상품 3: NEW_BALANCE 플리스 맨투맨
                Product product3 = Product.builder()
                        .productName("NEW_BALANCE 플리스 맨투맨")
                        .price(79000L)
                        .stockQuantity(0L)
                        .description("https://picsum.photos/id/1013/1200/800")
                        .imageUrl("https://picsum.photos/id/1013/600/600")
                        .brand(Brand.NEW_BALANCE)
                        .productCategory(ProductCategory.SWEATSHIRT)
                        .status(ProductStatus.SOLD_OUT)
                        .availableSizes(Set.of(Size.SMALL, Size.MEDIUM))
                        .build();
                productRepository.save(product3);

                // 상품 4: REEBOK 베이직 셔츠
                Product product4 = Product.builder()
                        .productName("REEBOK 베이직 셔츠")
                        .price(39000L)
                        .stockQuantity(60L)
                        .description("https://picsum.photos/id/1014/1200/800")
                        .imageUrl("https://picsum.photos/id/1014/600/600")
                        .brand(Brand.REEBOK)
                        .productCategory(ProductCategory.SHIRT)
                        .status(ProductStatus.ON_SALE)
                        .availableSizes(Set.of(Size.SMALL, Size.MEDIUM, Size.LARGE, Size.EXTRA_LARGE))
                        .build();
                productRepository.save(product4);

                // 상품 5: THE_NORTH_FACE 눕시 패딩
                Product product5 = Product.builder()
                        .productName("THE_NORTH_FACE 눕시 패딩")
                        .price(299000L)
                        .stockQuantity(12L)
                        .description("https://picsum.photos/id/1015/1200/800")
                        .imageUrl("https://picsum.photos/id/1015/600/600")
                        .brand(Brand.THE_NORTH_FACE)
                        .productCategory(ProductCategory.PADDING)
                        .status(ProductStatus.ON_SALE)
                        .availableSizes(Set.of(Size.MEDIUM, Size.LARGE))
                        .build();
                productRepository.save(product5);

                // 상품 6: VANS 데님 쇼츠
                Product product6 = Product.builder()
                        .productName("VANS 데님 쇼츠")
                        .price(49000L)
                        .stockQuantity(30L)
                        .description("https://picsum.photos/id/1016/1200/800")
                        .imageUrl("https://picsum.photos/id/1016/600/600")
                        .brand(Brand.VANS)
                        .productCategory(ProductCategory.SHORTS)
                        .status(ProductStatus.ON_SALE)
                        .availableSizes(Set.of(Size.SMALL, Size.MEDIUM, Size.LARGE))
                        .build();
                productRepository.save(product6);

                // 상품 7: NIKE 스트레이트 진
                Product product7 = Product.builder()
                        .productName("NIKE 스트레이트 진")
                        .price(99000L)
                        .stockQuantity(18L)
                        .description("https://picsum.photos/id/1018/1200/800")
                        .imageUrl("https://picsum.photos/id/1018/600/600")
                        .brand(Brand.NIKE)
                        .productCategory(ProductCategory.JEANS)
                        .status(ProductStatus.ON_SALE)
                        .availableSizes(Set.of(Size.LARGE, Size.EXTRA_LARGE))
                        .build();
                productRepository.save(product7);

                // 상품 8: ADIDAS 롱 코트
                Product product8 = Product.builder()
                        .productName("ADIDAS 롱 코트")
                        .price(179000L)
                        .stockQuantity(5L)
                        .description("https://picsum.photos/id/1019/1200/800")
                        .imageUrl("https://picsum.photos/id/1019/600/600")
                        .brand(Brand.ADIDAS)
                        .productCategory(ProductCategory.COAT)
                        .status(ProductStatus.ON_SALE)
                        .availableSizes(Set.of(Size.SMALL, Size.MEDIUM))
                        .build();
                productRepository.save(product8);

                // 상품 9: NEW_BALANCE 머플러
                Product product9 = Product.builder()
                        .productName("NEW_BALANCE 머플러")
                        .price(29000L)
                        .stockQuantity(0L)
                        .description("https://picsum.photos/id/1020/1200/800")
                        .imageUrl("https://picsum.photos/id/1020/600/600")
                        .brand(Brand.NEW_BALANCE)
                        .productCategory(ProductCategory.MUFFLER)
                        .status(ProductStatus.SOLD_OUT)
                        .availableSizes(Set.of(Size.SMALL, Size.MEDIUM, Size.LARGE))
                        .build();
                productRepository.save(product9);

                // 상품 10: REEBOK 스탠다드 패딩
                Product product10 = Product.builder()
                        .productName("REEBOK 스탠다드 패딩")
                        .price(159000L)
                        .stockQuantity(0L)
                        .description("https://picsum.photos/id/1021/1200/800")
                        .imageUrl("https://picsum.photos/id/1021/600/600")
                        .brand(Brand.REEBOK)
                        .productCategory(ProductCategory.PADDING)
                        .status(ProductStatus.DELETED)
                        .availableSizes(Set.of(Size.MEDIUM))
                        .build();
                productRepository.save(product10);

                logger.info("상품 샘플 데이터 10개가 성공적으로 삽입되었습니다.");
            } else {
                logger.info("이미 상품 데이터가 존재합니다. (현재 상품 수: {})", productCount);
            }
        };
    }
}
