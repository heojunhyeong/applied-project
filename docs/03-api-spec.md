# Wearly API 명세서

## 1. 인증 (Authentication)

### 1.1 로그인

- **Endpoint:** `/api/auth/login`
- **Method:** `POST`
- **Request Header:** `없음`
- **Request Body:**
```json
{
  "userId": "user123",
  "userPassword": "password123"
}
```
- **Response Body:**
```json
{
  "accessToken": "eyJhbG...",
  "refreshToken": "eyJhbG...",
  "tokenType": "Bearer",
  "userId": 1,
  "userEmail": "user@example.com",
  "nickName": "닉네임",
  "role": "USER",
  "message": "로그인 성공"
}
```
- **Status Code:** `200 OK`, `400 Bad Request`, `500 Internal Server Error`
- **Description:** 이메일과 비밀번호를 검증하여 JWT Access Token, Refresh Token 및 회원 정보를 발급

---

### 1.2 토큰 재발급

- **Endpoint:** `/api/auth/refresh`
- **Method:** `POST`
- **Request Header:** `없음`
- **Request Body:**
```json
{
  "refreshToken": "eyJhbG..."
}
```
- **Response Body:**
```json
{
  "accessToken": "eyJhbG...",
  "refreshToken": "eyJhbG...",
  "tokenType": "Bearer",
  "userId": 1,
  "userEmail": "user@example.com",
  "nickName": "닉네임",
  "role": "USER",
  "message": "토큰 갱신 성공"
}
```
- **Status Code:** `200 OK`, `400 Bad Request`, `500 Internal Server Error`
- **Description:** Refresh Token을 사용하여 새로운 Access Token을 발급 (Refresh Token Rotation 방식으로 새로운 Refresh Token도 함께 발급)

---

### 1.3 로그아웃

- **Endpoint:** `/api/auth/logout`
- **Method:** `POST`
- **Request Header:** `없음`
- **Request Body:**
```json
{
  "refreshToken": "eyJhbG..."
}
```
- **Response Body:** `없음`
- **Status Code:** `204 No Content`, `400 Bad Request`, `500 Internal Server Error`
- **Description:** Refresh Token을 데이터베이스에서 삭제하여 로그아웃 처리

---

## 2. 회원 관리 (User)

### 2.1 회원가입

- **Endpoint:** `/api/users/signup`
- **Method:** `POST`
- **Request Header:** `없음`
- **Request Body:**
```json
{
  "userId": "user123",
  "userPassword": "password123!",
  "confirmPassword": "password123!",
  "userEmail": "user@example.com",
  "nickName": "닉네임",
  "roleType": "USER"
}
```
- **Response Body:**
```json
{
  "id": 1,
  "userId": "user123",
  "userEmail": "user@example.com",
  "nickName": "닉네임",
  "roleType": "USER",
  "message": "회원가입 성공",
  "createdDate": "2026-01-15T10:00:00",
  "updatedDate": "2026-01-15T10:00:00"
}
```
- **Status Code:** `201 Created`, `400 Bad Request`, `500 Internal Server Error`
- **Description:** 신규 회원의 가입 정보를 받아 유효성 검사 후 계정을 생성 (일반 사용자(USER)와 판매자(SELLER) 타입 구분)

---

### 2.2 프로필 조회

- **Endpoint:** `/api/users/profile`
- **Method:** `GET`
- **Request Header:** `Authorization: Bearer {accessToken}`
- **Request Body:** `없음`
- **Response Body:**
```json
{
  "id": 1,
  "userName": "user123",
  "userEmail": "user@example.com",
  "userNickname": "닉네임",
  "introduction": "소개글",
  "phoneNumber": "010-1234-5678",
  "imageUrl": "https://...",
  "createdDate": "2026-01-15T10:00:00",
  "updatedDate": "2026-01-15T10:00:00"
}
```
- **Status Code:** `200 OK`
- **Description:** 사용자의 닉네임, 이메일, 프로필 이미지 경로 등 상세 프로필 정보를 조회

---

### 2.3 프로필 수정

- **Endpoint:** `/api/users/profile`
- **Method:** `PATCH`
- **Request Header:** `Authorization: Bearer {accessToken}`
- **Request Body:**
```json
{
  "userNickname": "새닉네임",
  "introduction": "새소개글",
  "phoneNumber": "010-9876-5432"
}
```
- **Response Body:**
```json
{
  "id": 1,
  "userName": "user123",
  "userEmail": "user@example.com",
  "userNickname": "새닉네임",
  "introduction": "새소개글",
  "phoneNumber": "010-9876-5432",
  "imageUrl": "https://...",
  "createdDate": "2026-01-15T10:00:00",
  "updatedDate": "2026-01-15T11:00:00"
}
```
- **Status Code:** `200 OK`
- **Description:** 사용자의 닉네임이나 기본 정보를 수정

---

### 2.4 프로필 이미지 Presigned URL 발급

- **Endpoint:** `/api/users/profile/presigned-url`
- **Method:** `POST`
- **Request Header:** `Authorization: Bearer {accessToken}`
- **Request Body:**
```json
{
  "contentType": "image/jpeg"
}
```
- **Response Body:**
```json
{
  "presignedUrl": "https://...",
  "imageKey": "users/1/..."
}
```
- **Status Code:** `200 OK`
- **Description:** S3 서버에 프로필 이미지를 직접 업로드할 수 있도록 유효시간이 포함된 임시 업로드 URL을 생성

---

### 2.5 프로필 이미지 업데이트

- **Endpoint:** `/api/users/profile/image`
- **Method:** `PATCH`
- **Request Header:** `Authorization: Bearer {accessToken}`
- **Request Body:**
```json
{
  "imageUrl": "https://..."
}
```
- **Response Body:**
```json
{
  "id": 1,
  "userName": "user123",
  "userEmail": "user@example.com",
  "userNickname": "닉네임",
  "introduction": "소개글",
  "phoneNumber": "010-1234-5678",
  "imageUrl": "https://...",
  "createdDate": "2026-01-15T10:00:00",
  "updatedDate": "2026-01-15T12:00:00"
}
```
- **Status Code:** `200 OK`
- **Description:** 클라이언트에서 S3 업로드를 마친 후, 해당 이미지의 접근 가능 URL을 사용자의 프로필 정보에 반영

---

## 3. 비밀번호 관리 (Password)

### 3.1 비밀번호 재설정 요청

- **Endpoint:** `/api/password/reset/request`
- **Method:** `POST`
- **Request Header:** `없음`
- **Request Body:**
```json
{
  "email": "user@example.com"
}
```
- **Response Body:** `없음`
- **Status Code:** `200 OK`
- **Description:** 사용자의 이메일로 비밀번호 재설정 링크(인증 토큰 포함)를 포함한 메일을 발송

---

### 3.2 비밀번호 재설정

- **Endpoint:** `/api/password/reset`
- **Method:** `POST`
- **Request Header:** `없음`
- **Request Body:**
```json
{
  "token": "reset-token-here",
  "newPassword": "newPassword123!"
}
```
- **Response Body:** `없음`
- **Status Code:** `200 OK`
- **Description:** 메일로 발송된 인증 토큰의 유효성을 검증한 후, 사용자의 비밀번호를 새로운 비밀번호로 변경

---

## 4. 상품 관리 (Product)

### 4.1 상품 검색

- **Endpoint:** `/api/products`
- **Method:** `GET`
- **Request Header:** `없음`
- **Request Body:** `없음`
- **Request Query Parameters:**
  - `brand` (optional): 브랜드 필터
  - `category` (optional): 카테고리 필터
  - `page` (optional): 페이지 번호 (기본값: 0)
  - `size` (optional): 페이지 크기 (기본값: 20)
- **Response Body:**
```json
{
  "content": [
    {
      "id": 1,
      "name": "상품명",
      "price": 100000,
      "brand": "NIKE",
      "category": "SHOES",
      "thumbnailUrl": "https://...",
      "status": "ON_SALE"
    }
  ],
  "totalElements": 100,
  "totalPages": 5,
  "number": 0,
  "size": 20
}
```
- **Status Code:** `200 OK`
- **Description:** 상품 목록을 검색 조건에 따라 페이징하여 조회

---

### 4.2 카테고리 조회

- **Endpoint:** `/api/products/categories`
- **Method:** `GET`
- **Request Header:** `없음`
- **Request Body:** `없음`
- **Request Query Parameters:**
  - `brand` (required): 브랜드
- **Response Body:**
```json
["SHOES", "CLOTHING", "ACCESSORIES"]
```
- **Status Code:** `200 OK`
- **Description:** 특정 브랜드의 카테고리 목록을 조회

---

### 4.3 상품 상세 조회

- **Endpoint:** `/api/products/{productId}`
- **Method:** `GET`
- **Request Header:** `없음`
- **Request Body:** `없음`
- **Response Body:**
```json
{
  "id": 1,
  "name": "상품명",
  "price": 100000,
  "brand": "NIKE",
  "category": "SHOES",
  "description": "상품 설명",
  "thumbnailUrl": "https://...",
  "descriptionImages": ["https://..."],
  "sizes": ["250", "260", "270"],
  "stock": 10,
  "status": "ON_SALE",
  "sellerId": 1,
  "sellerName": "판매자명"
}
```
- **Status Code:** `200 OK`
- **Description:** 특정 상품의 상세 정보를 조회

---

### 4.4 상품 리뷰 목록 조회

- **Endpoint:** `/api/products/{productId}/reviews`
- **Method:** `GET`
- **Request Header:** `없음`
- **Request Body:** `없음`
- **Request Query Parameters:**
  - `page` (optional): 페이지 번호 (기본값: 0)
  - `size` (optional): 페이지 크기 (기본값: 10)
- **Response Body:**
```json
{
  "content": [
    {
      "id": 1,
      "productId": 1,
      "userId": 1,
      "userNickname": "리뷰어",
      "rating": 5,
      "content": "리뷰 내용",
      "createdDate": "2026-01-15T10:00:00"
    }
  ],
  "totalElements": 50,
  "totalPages": 5,
  "number": 0,
  "size": 10
}
```
- **Status Code:** `200 OK`
- **Description:** 특정 상품에 달린 리뷰 목록을 페이징하여 조회

---

## 5. 판매자 상품 관리 (Seller Product)

### 5.1 상품 등록

- **Endpoint:** `/api/seller/products`
- **Method:** `POST`
- **Request Header:** `Authorization: Bearer {accessToken}`
- **Request Body:**
```json
{
  "name": "상품명",
  "price": 100000,
  "brand": "NIKE",
  "category": "SHOES",
  "description": "상품 설명",
  "thumbnailUrl": "https://...",
  "descriptionImages": ["https://..."],
  "sizes": ["250", "260", "270"],
  "stock": 10
}
```
- **Response Body:**
```json
{
  "id": 1,
  "name": "상품명",
  "price": 100000,
  "brand": "NIKE",
  "category": "SHOES",
  "description": "상품 설명",
  "thumbnailUrl": "https://...",
  "descriptionImages": ["https://..."],
  "sizes": ["250", "260", "270"],
  "stock": 10,
  "status": "ON_SALE"
}
```
- **Status Code:** `200 OK`
- **Description:** 판매자가 새로운 상품을 등록

---

### 5.2 내 상품 목록 조회

- **Endpoint:** `/api/seller/products`
- **Method:** `GET`
- **Request Header:** `Authorization: Bearer {accessToken}`
- **Request Body:** `없음`
- **Request Query Parameters:**
  - `page` (optional): 페이지 번호
  - `size` (optional): 페이지 크기
- **Response Body:**
```json
{
  "content": [
    {
      "id": 1,
      "name": "상품명",
      "price": 100000,
      "brand": "NIKE",
      "category": "SHOES",
      "thumbnailUrl": "https://...",
      "status": "ON_SALE"
    }
  ],
  "totalElements": 20,
  "totalPages": 2,
  "number": 0,
  "size": 10
}
```
- **Status Code:** `200 OK`
- **Description:** 현재 로그인한 판매자의 상품 목록을 페이징하여 조회

---

### 5.3 내 상품 상세 조회

- **Endpoint:** `/api/seller/products/{productId}`
- **Method:** `GET`
- **Request Header:** `Authorization: Bearer {accessToken}`
- **Request Body:** `없음`
- **Response Body:**
```json
{
  "id": 1,
  "name": "상품명",
  "price": 100000,
  "brand": "NIKE",
  "category": "SHOES",
  "description": "상품 설명",
  "thumbnailUrl": "https://...",
  "descriptionImages": ["https://..."],
  "sizes": ["250", "260", "270"],
  "stock": 10,
  "status": "ON_SALE"
}
```
- **Status Code:** `200 OK`
- **Description:** 현재 로그인한 판매자의 특정 상품 상세 정보를 조회

---

### 5.4 내 상품 수정

- **Endpoint:** `/api/seller/products/{productId}`
- **Method:** `PUT`
- **Request Header:** `Authorization: Bearer {accessToken}`
- **Request Body:**
```json
{
  "name": "수정된 상품명",
  "price": 120000,
  "brand": "NIKE",
  "category": "SHOES",
  "description": "수정된 상품 설명",
  "thumbnailUrl": "https://...",
  "descriptionImages": ["https://..."],
  "sizes": ["250", "260", "270"],
  "stock": 5
}
```
- **Response Body:**
```json
{
  "id": 1,
  "name": "수정된 상품명",
  "price": 120000,
  "brand": "NIKE",
  "category": "SHOES",
  "description": "수정된 상품 설명",
  "thumbnailUrl": "https://...",
  "descriptionImages": ["https://..."],
  "sizes": ["250", "260", "270"],
  "stock": 5,
  "status": "ON_SALE"
}
```
- **Status Code:** `200 OK`
- **Description:** 현재 로그인한 판매자의 상품 정보를 전체 수정

---

### 5.5 내 상품 삭제

- **Endpoint:** `/api/seller/products/{productId}`
- **Method:** `DELETE`
- **Request Header:** `Authorization: Bearer {accessToken}`
- **Request Body:** `없음`
- **Response Body:** `없음`
- **Status Code:** `204 No Content`
- **Description:** 현재 로그인한 판매자의 상품을 소프트 삭제 처리 (status=DELETED)

---

### 5.6 상품 이미지 Presigned URL 발급

- **Endpoint:** `/api/seller/products/presigned-url`
- **Method:** `POST`
- **Request Header:** `Authorization: Bearer {accessToken}`
- **Request Body:** `없음`
- **Request Query Parameters:**
  - `extension` (required): 파일 확장자 (예: "jpg", "png")
  - `type` (required): 이미지 타입 ("THUMBNAIL" 또는 "DESCRIPTION")
- **Response Body:**
```json
{
  "presignedUrl": "https://...",
  "key": "products/thumbnail/..."
}
```
- **Status Code:** `200 OK`
- **Description:** 상품 썸네일 또는 설명 이미지 업로드를 위한 Presigned URL 발급

---

## 6. 주문 관리 (Order)

### 6.1 주문 생성

- **Endpoint:** `/api/users/orders`
- **Method:** `POST`
- **Request Header:** `Authorization: Bearer {accessToken}`
- **Request Body:**
```json
{
  "orderId": "order-12345",
  "orderItems": [
    {
      "productId": 1,
      "quantity": 2,
      "size": "260",
      "price": 100000
    }
  ],
  "shippingAddress": "서울시 강남구...",
  "totalAmount": 200000
}
```
- **Response Body:**
```json
{
  "id": 1,
  "orderId": "order-12345",
  "userId": 1,
  "orderStatus": "PENDING",
  "totalAmount": 200000,
  "createdDate": "2026-01-15T10:00:00"
}
```
- **Status Code:** `200 OK`
- **Description:** 새로운 주문을 생성

---

### 6.2 주문 내역 조회

- **Endpoint:** `/api/users/orders`
- **Method:** `GET`
- **Request Header:** `Authorization: Bearer {accessToken}`
- **Request Body:** `없음`
- **Response Body:**
```json
[
  {
    "orderId": "order-12345",
    "orderDate": "2026-01-15T10:00:00",
    "totalAmount": 200000,
    "orderStatus": "CONFIRMED",
    "itemCount": 2
  }
]
```
- **Status Code:** `200 OK`
- **Description:** 현재 로그인한 사용자의 전체 주문 내역을 조회

---

### 6.3 주문 상세 조회

- **Endpoint:** `/api/users/orders/{orderId}`
- **Method:** `GET`
- **Request Header:** `없음`
- **Request Body:** `없음`
- **Response Body:**
```json
{
  "orderId": "order-12345",
  "orderDate": "2026-01-15T10:00:00",
  "totalAmount": 200000,
  "orderStatus": "CONFIRMED",
  "shippingAddress": "서울시 강남구...",
  "orderItems": [
    {
      "productId": 1,
      "productName": "상품명",
      "quantity": 2,
      "size": "260",
      "price": 100000,
      "status": "SHIPPED"
    }
  ]
}
```
- **Status Code:** `200 OK`
- **Description:** 특정 주문 번호에 대한 상세 정보를 조회

---

### 6.4 주문 상세 검색

- **Endpoint:** `/api/users/orders/search`
- **Method:** `GET`
- **Request Header:** `Authorization: Bearer {accessToken}`
- **Request Body:** `없음`
- **Request Query Parameters:**
  - `keyword` (optional): 상품명 검색 키워드
- **Response Body:**
```json
[
  {
    "productId": 1,
    "productName": "상품명",
    "quantity": 2,
    "size": "260",
    "price": 100000,
    "orderDate": "2026-01-15T10:00:00",
    "status": "SHIPPED"
  }
]
```
- **Status Code:** `200 OK`
- **Description:** 상품명 키워드로 주문 상세를 검색 (키워드가 포함된 상품과 같은 날짜에 주문된 모든 상품 반환)

---

### 6.5 구매 확정

- **Endpoint:** `/api/users/orders/{orderId}/details/{orderDetailId}/confirm`
- **Method:** `POST`
- **Request Header:** `Authorization: Bearer {accessToken}`
- **Request Body:** `없음`
- **Response Body:**
```json
{
  "message": "구매 확정이 완료되었습니다. 정산 프로세스가 시작됩니다."
}
```
- **Status Code:** `200 OK`
- **Description:** 사용자가 상품 수령 후 구매를 최종 확정

---

### 6.6 주문서 조회

- **Endpoint:** `/api/users/orders/sheet`
- **Method:** `GET`
- **Request Header:** `Authorization: Bearer {accessToken}`
- **Request Body:** `없음`
- **Request Query Parameters:**
  - `cartItemIds` (optional): 장바구니 아이템 ID 리스트
  - `productId` (optional): 상품 ID
  - `quantity` (optional): 수량
  - `size` (optional): 사이즈
- **Response Body:**
```json
{
  "orderItems": [
    {
      "productId": 1,
      "productName": "상품명",
      "price": 100000,
      "quantity": 2,
      "size": "260",
      "thumbnailUrl": "https://..."
    }
  ],
  "totalAmount": 200000,
  "shippingFee": 3000
}
```
- **Status Code:** `200 OK`
- **Description:** 주문서 정보를 조회

---

## 7. 장바구니 관리 (Cart)

### 7.1 장바구니 조회

- **Endpoint:** `/api/users/cart/items`
- **Method:** `GET`
- **Request Header:** `Authorization: Bearer {accessToken}`
- **Request Body:** `없음`
- **Response Body:**
```json
[
  {
    "productId": 1,
    "productName": "상품명",
    "price": 100000,
    "quantity": 2,
    "size": "260",
    "thumbnailUrl": "https://..."
  }
]
```
- **Status Code:** `200 OK`
- **Description:** 현재 로그인한 사용자의 장바구니에 담긴 전체 상품 목록을 조회

---

### 7.2 장바구니 추가

- **Endpoint:** `/api/users/cart/items`
- **Method:** `POST`
- **Request Header:** `Authorization: Bearer {accessToken}`
- **Request Body:**
```json
{
  "productId": 1,
  "quantity": 2,
  "size": "260"
}
```
- **Response Body:**
```json
{
  "productId": 1,
  "productName": "상품명",
  "price": 100000,
  "quantity": 2,
  "size": "260",
  "thumbnailUrl": "https://..."
}
```
- **Status Code:** `201 Created`
- **Description:** 장바구니에 새로운 상품을 추가하거나 기존 상품의 수량을 변경

---

### 7.3 장바구니 아이템 삭제

- **Endpoint:** `/api/users/cart/items/{productId}`
- **Method:** `DELETE`
- **Request Header:** `Authorization: Bearer {accessToken}`
- **Request Body:** `없음`
- **Response Body:** `없음`
- **Status Code:** `204 No Content`
- **Description:** 장바구니에서 특정 상품 하나를 선택하여 삭제

---

### 7.4 장바구니 전체 삭제

- **Endpoint:** `/api/users/cart/items`
- **Method:** `DELETE`
- **Request Header:** `Authorization: Bearer {accessToken}`
- **Request Body:** `없음`
- **Response Body:** `없음`
- **Status Code:** `204 No Content`
- **Description:** 사용자의 장바구니에 담긴 모든 상품을 일괄 삭제

---

## 8. 판매자 주문 관리 (Seller Order)

### 8.1 판매자 주문 목록 조회

- **Endpoint:** `/api/seller/orders`
- **Method:** `GET`
- **Request Header:** `Authorization: Bearer {accessToken}`
- **Request Body:** `없음`
- **Request Query Parameters:**
  - `status` (optional): 주문 상태 필터
  - `page` (optional): 페이지 번호
  - `size` (optional): 페이지 크기
- **Response Body:**
```json
{
  "content": [
    {
      "orderDetailId": 1,
      "orderId": "order-12345",
      "productName": "상품명",
      "quantity": 2,
      "price": 100000,
      "status": "SHIPPED",
      "orderDate": "2026-01-15T10:00:00"
    }
  ],
  "totalElements": 50,
  "totalPages": 5,
  "number": 0,
  "size": 10
}
```
- **Status Code:** `200 OK`
- **Description:** 현재 로그인한 판매자의 주문 목록을 상태별로 필터링하여 페이징 조회

---

### 8.2 판매자 주문 상세 조회

- **Endpoint:** `/api/seller/orders/{orderDetailId}`
- **Method:** `GET`
- **Request Header:** `Authorization: Bearer {accessToken}`
- **Request Body:** `없음`
- **Response Body:**
```json
{
  "orderDetailId": 1,
  "orderId": "order-12345",
  "productId": 1,
  "productName": "상품명",
  "quantity": 2,
  "size": "260",
  "price": 100000,
  "status": "SHIPPED",
  "deliveryCompany": "CJ대한통운",
  "trackingNumber": "1234567890",
  "buyerName": "구매자명",
  "shippingAddress": "서울시 강남구...",
  "orderDate": "2026-01-15T10:00:00"
}
```
- **Status Code:** `200 OK`
- **Description:** 판매자의 특정 주문 상세 정보를 조회

---

### 8.3 배송 정보 수정

- **Endpoint:** `/api/seller/orders/{orderDetailId}/delivery`
- **Method:** `PATCH`
- **Request Header:** `Authorization: Bearer {accessToken}`
- **Request Body:**
```json
{
  "deliveryCompany": "CJ대한통운",
  "trackingNumber": "1234567890"
}
```
- **Response Body:** `없음`
- **Status Code:** `204 No Content`
- **Description:** 주문의 송장번호 및 택배사 정보를 입력 또는 수정 (CHECK 상태에서만 허용)

---

### 8.4 주문 상태 변경

- **Endpoint:** `/api/seller/orders/{orderDetailId}/status`
- **Method:** `PATCH`
- **Request Header:** `Authorization: Bearer {accessToken}`
- **Request Body:**
```json
{
  "status": "SHIPPED"
}
```
- **Response Body:** `없음`
- **Status Code:** `204 No Content`
- **Description:** 주문 상태를 변경 (송장 필수 검증 및 전이 규칙 적용)

---

## 9. 리뷰 관리 (Review)

### 9.1 리뷰 작성

- **Endpoint:** `/api/users/reviews`
- **Method:** `POST`
- **Request Header:** `Authorization: Bearer {accessToken}`
- **Request Body:**
```json
{
  "productId": 1,
  "orderDetailId": 1,
  "rating": 5,
  "content": "리뷰 내용"
}
```
- **Response Body:**
```json
{
  "message": "리뷰가 성공적으로 등록되었습니다."
}
```
- **Status Code:** `200 OK`
- **Description:** 사용자가 구매한 상품에 대해 별점과 텍스트 리뷰를 등록

---

### 9.2 리뷰 삭제

- **Endpoint:** `/api/users/reviews/{reviewId}`
- **Method:** `DELETE`
- **Request Header:** `Authorization: Bearer {accessToken}`
- **Request Body:** `없음`
- **Response Body:**
```json
{
  "message": "리뷰가 삭제되었습니다."
}
```
- **Status Code:** `200 OK`
- **Description:** 본인이 작성한 리뷰를 삭제 (작성자 일치 여부 검증)

---

### 9.3 상품 리뷰 목록 조회

- **Endpoint:** `/api/users/reviews/product/{productId}`
- **Method:** `GET`
- **Request Header:** `없음`
- **Request Body:** `없음`
- **Response Body:**
```json
[
  {
    "id": 1,
    "productId": 1,
    "userId": 1,
    "rating": 5,
    "content": "리뷰 내용",
    "createdDate": "2026-01-15T10:00:00"
  }
]
```
- **Status Code:** `200 OK`
- **Description:** 특정 상품에 달린 모든 리뷰 목록을 조회

---

## 10. 판매자 리뷰 관리 (Seller Review)

### 10.1 판매자 리뷰 목록 조회

- **Endpoint:** `/api/seller/reviews`
- **Method:** `GET`
- **Request Header:** `Authorization: Bearer {accessToken}`
- **Request Body:** `없음`
- **Request Query Parameters:**
  - `productId` (optional): 상품 ID 필터
  - `status` (optional): 리뷰 상태 필터
  - `page` (optional): 페이지 번호 (기본값: 0)
  - `size` (optional): 페이지 크기 (기본값: 20)
- **Response Body:**
```json
{
  "content": [
    {
      "reviewId": 1,
      "productId": 1,
      "productName": "상품명",
      "userNickname": "리뷰어",
      "rating": 5,
      "content": "리뷰 내용",
      "status": "ACTIVE",
      "createdDate": "2026-01-15T10:00:00"
    }
  ],
  "totalElements": 30,
  "totalPages": 2,
  "number": 0,
  "size": 20
}
```
- **Status Code:** `200 OK`
- **Description:** 판매자의 상품 리뷰 목록을 상품ID 및 상태별로 필터링하여 페이징 조회

---

### 10.2 판매자 리뷰 요약 조회

- **Endpoint:** `/api/seller/reviews/summary`
- **Method:** `GET`
- **Request Header:** `Authorization: Bearer {accessToken}`
- **Request Body:** `없음`
- **Request Query Parameters:**
  - `productId` (optional): 상품 ID 필터
- **Response Body:**
```json
{
  "averageRating": 4.5,
  "totalCount": 50,
  "ratingDistribution": {
    "5": 30,
    "4": 15,
    "3": 5,
    "2": 0,
    "1": 0
  }
}
```
- **Status Code:** `200 OK`
- **Description:** 판매자의 리뷰 요약 정보(평균 평점, 총 개수)를 조회 (전체 또는 특정 상품)

---

### 10.3 상품별 리뷰 요약 조회

- **Endpoint:** `/api/seller/reviews/summary/products`
- **Method:** `GET`
- **Request Header:** `Authorization: Bearer {accessToken}`
- **Request Body:** `없음`
- **Response Body:**
```json
[
  {
    "productId": 1,
    "productName": "상품명",
    "averageRating": 4.5,
    "reviewCount": 30
  }
]
```
- **Status Code:** `200 OK`
- **Description:** 판매자의 상품별 리뷰 요약 리스트를 조회

---

### 10.4 리뷰 신고

- **Endpoint:** `/api/seller/reviews/{reviewId}/reports`
- **Method:** `POST`
- **Request Header:** `Authorization: Bearer {accessToken}`
- **Request Body:**
```json
{
  "reason": "부적절한 내용"
}
```
- **Response Body:** `없음`
- **Status Code:** `204 No Content`
- **Description:** 판매자가 특정 리뷰를 신고 접수

---

### 10.5 판매자 신고 내역 조회

- **Endpoint:** `/api/seller/review-reports`
- **Method:** `GET`
- **Request Header:** `Authorization: Bearer {accessToken}`
- **Request Body:** `없음`
- **Request Query Parameters:**
  - `status` (optional): 신고 상태 필터
  - `page` (optional): 페이지 번호 (기본값: 0)
  - `size` (optional): 페이지 크기 (기본값: 20)
- **Response Body:**
```json
{
  "content": [
    {
      "reportId": 1,
      "reviewId": 1,
      "productName": "상품명",
      "reason": "부적절한 내용",
      "status": "PENDING",
      "createdDate": "2026-01-15T10:00:00"
    }
  ],
  "totalElements": 10,
  "totalPages": 1,
  "number": 0,
  "size": 20
}
```
- **Status Code:** `200 OK`
- **Description:** 판매자가 신고한 리뷰 신고 내역을 상태별로 필터링하여 페이징 조회

---

## 11. 관리자 리뷰 관리 (Admin Review)

### 11.1 신고된 리뷰 목록 조회

- **Endpoint:** `/api/admin/reviews/reports`
- **Method:** `GET`
- **Request Header:** `Authorization: Bearer {accessToken}`
- **Request Body:** `없음`
- **Request Query Parameters:**
  - `status` (optional): 신고 상태 필터 (PENDING, RESOLVED, REJECTED)
  - `page` (optional): 페이지 번호 (기본값: 0)
  - `size` (optional): 페이지 크기 (기본값: 20)
- **Response Body:**
```json
{
  "content": [
    {
      "reportId": 1,
      "reviewId": 1,
      "productId": 1,
      "productName": "상품명",
      "sellerId": 1,
      "sellerName": "판매자명",
      "reporterId": 1,
      "reason": "부적절한 내용",
      "reviewContent": "리뷰 내용",
      "status": "PENDING",
      "createdDate": "2026-01-15T10:00:00"
    }
  ],
  "totalElements": 50,
  "totalPages": 3,
  "number": 0,
  "size": 20
}
```
- **Status Code:** `200 OK`
- **Description:** 관리자가 신고된 리뷰 목록을 상태별로 필터링하여 페이징 조회

---

### 11.2 신고 승인

- **Endpoint:** `/api/admin/reviews/reports/{reportId}/approve`
- **Method:** `PATCH`
- **Request Header:** `Authorization: Bearer {accessToken}`
- **Request Body:** `없음`
- **Response Body:** `없음`
- **Status Code:** `204 No Content`
- **Description:** 관리자가 신고를 승인 처리 (리뷰를 HIDDEN 상태로 변경)

---

### 11.3 신고 반려

- **Endpoint:** `/api/admin/reviews/reports/{reportId}/reject`
- **Method:** `PATCH`
- **Request Header:** `Authorization: Bearer {accessToken}`
- **Request Body:** `없음`
- **Response Body:** `없음`
- **Status Code:** `204 No Content`
- **Description:** 관리자가 신고를 반려 처리 (리뷰는 ACTIVE 상태 유지)

---

## 12. 결제 관리 (Payment)

### 12.1 결제 승인

- **Endpoint:** `/api/payment/toss/confirm`
- **Method:** `POST`
- **Request Header:** `Authorization: Bearer {accessToken}`
- **Request Body:**
```json
{
  "paymentKey": "tgen.xxx",
  "orderId": "order-12345",
  "amount": 200000
}
```
- **Response Body:** `없음`
- **Status Code:** `200 OK`
- **Description:** 토스 페이먼츠의 결제 승인 API를 호출하여 최종적으로 주문 결제를 완료

---

### 12.2 결제 취소

- **Endpoint:** `/api/payment/{orderId}/cancel`
- **Method:** `POST`
- **Request Header:** `없음`
- **Request Body:**
```json
{
  "cancelReason": "구매 의사 취소"
}
```
- **Response Body:**
```json
{
  "message": "결제가 취소되었습니다."
}
```
- **Status Code:** `200 OK`
- **Description:** 이미 완료된 주문 결제에 대해 취소 사유를 받아 환불 및 결제 취소를 처리

---

### 12.3 빌링키 발급 및 멤버십 활성화

- **Endpoint:** `/api/payment/billing/confirm`
- **Method:** `POST`
- **Request Header:** `Authorization: Bearer {accessToken}`
- **Request Body:**
```json
{
  "authKey": "auth-key-here",
  "customerKey": "customer-key-here"
}
```
- **Response Body:**
```json
{
  "message": "멤버십 정기 결제가 성공적으로 등록되었습니다."
}
```
- **Status Code:** `200 OK`
- **Description:** 카드 인증 후 받은 authKey를 이용하여 정기 결제용 빌링키를 발급받고 멤버십을 활성화

---

### 12.4 멤버십 해지 예약

- **Endpoint:** `/api/payment/membership/terminate`
- **Method:** `POST`
- **Request Header:** `Authorization: Bearer {accessToken}`
- **Request Body:** `없음`
- **Response Body:**
```json
{
  "message": "다음 결제일부터 멤버십이 갱신되지 않습니다. 이번 달 혜택은 유지됩니다."
}
```
- **Status Code:** `200 OK`
- **Description:** 사용자의 멤버십을 즉시 종료하지 않고 다음 결제일에 갱신되지 않도록 해지 예약을 설정

---

### 12.5 내 멤버십 조회

- **Endpoint:** `/api/payment/membership/me`
- **Method:** `GET`
- **Request Header:** `Authorization: Bearer {accessToken}`
- **Request Body:** `없음`
- **Response Body:**
```json
{
  "id": 1,
  "userId": 1,
  "status": "ACTIVE",
  "billingKey": "billing-key-here",
  "startDate": "2026-01-01T00:00:00",
  "expiryDate": "2026-02-01T00:00:00",
  "nextBillingDate": "2026-02-01T00:00:00",
  "terminationReserved": false
}
```
- **Status Code:** `200 OK`
- **Description:** 현재 로그인한 사용자의 멤버십 상태(활성, 해지 예약 등) 및 만료 예정일을 조회

---

## 13. 판매자 정산 (Seller Settlement)

### 13.1 정산 요약 조회

- **Endpoint:** `/api/seller/settlements/summary`
- **Method:** `GET`
- **Request Header:** `Authorization: Bearer {accessToken}`
- **Request Body:** `없음`
- **Response Body:**
```json
{
  "expectAmount": 5000000,
  "completedAmount": 10000000
}
```
- **Status Code:** `200 OK`
- **Description:** 판매자의 정산 상태별 금액을 합산하여 정산 예정 금액과 정산 완료 총액을 반환

---

## 14. 쿠폰 관리 (Coupon)

### 14.1 쿠폰 다운로드

- **Endpoint:** `/api/coupons/download/{benefitId}`
- **Method:** `POST`
- **Request Header:** `Authorization: Bearer {accessToken}`
- **Request Body:** `없음`
- **Response Body:**
```json
{
  "message": "쿠폰 발급 완료!"
}
```
- **Status Code:** `200 OK`
- **Description:** 멤버십 회원을 대상으로 특정 혜택 쿠폰을 발급

---

## 15. 판매자 프로필 관리 (Seller Profile)

### 15.1 판매자 프로필 조회

- **Endpoint:** `/api/seller/profile`
- **Method:** `GET`
- **Request Header:** `Authorization: Bearer {accessToken}`
- **Request Body:** `없음`
- **Response Body:**
```json
{
  "id": 1,
  "userName": "seller123",
  "userEmail": "seller@example.com",
  "userNickname": "판매자닉네임",
  "brandName": "브랜드명",
  "businessNumber": "123-45-67890",
  "phoneNumber": "010-1234-5678",
  "imageUrl": "https://...",
  "createdDate": "2026-01-15T10:00:00",
  "updatedDate": "2026-01-15T10:00:00"
}
```
- **Status Code:** `200 OK`
- **Description:** 현재 로그인한 판매자의 프로필 정보를 조회

---

### 15.2 판매자 프로필 수정

- **Endpoint:** `/api/seller/profile`
- **Method:** `PATCH`
- **Request Header:** `Authorization: Bearer {accessToken}`
- **Request Body:**
```json
{
  "userNickname": "새닉네임",
  "brandName": "새브랜드명",
  "phoneNumber": "010-9876-5432"
}
```
- **Response Body:**
```json
{
  "id": 1,
  "userName": "seller123",
  "userEmail": "seller@example.com",
  "userNickname": "새닉네임",
  "brandName": "새브랜드명",
  "businessNumber": "123-45-67890",
  "phoneNumber": "010-9876-5432",
  "imageUrl": "https://...",
  "createdDate": "2026-01-15T10:00:00",
  "updatedDate": "2026-01-15T12:00:00"
}
```
- **Status Code:** `200 OK`
- **Description:** 판매자의 프로필 정보를 수정

---

### 15.3 판매자 비밀번호 변경

- **Endpoint:** `/api/seller/profile/password`
- **Method:** `PATCH`
- **Request Header:** `Authorization: Bearer {accessToken}`
- **Request Body:**
```json
{
  "currentPassword": "oldPassword123!",
  "newPassword": "newPassword123!",
  "confirmPassword": "newPassword123!"
}
```
- **Response Body:** `없음`
- **Status Code:** `204 No Content`
- **Description:** 판매자의 비밀번호를 변경

---

### 15.4 판매자 프로필 이미지 Presigned URL 발급

- **Endpoint:** `/api/seller/profile/presigned-url`
- **Method:** `POST`
- **Request Header:** `Authorization: Bearer {accessToken}`
- **Request Body:**
```json
{
  "contentType": "image/jpeg"
}
```
- **Response Body:**
```json
{
  "presignedUrl": "https://...",
  "imageKey": "seller/1/..."
}
```
- **Status Code:** `200 OK`
- **Description:** S3에 판매자 프로필 이미지를 업로드하기 위한 Presigned URL 발급

---

### 15.5 판매자 프로필 이미지 업데이트

- **Endpoint:** `/api/seller/profile/image`
- **Method:** `PATCH`
- **Request Header:** `Authorization: Bearer {accessToken}`
- **Request Body:**
```json
{
  "imageUrl": "https://..."
}
```
- **Response Body:**
```json
{
  "id": 1,
  "userName": "seller123",
  "userEmail": "seller@example.com",
  "userNickname": "판매자닉네임",
  "brandName": "브랜드명",
  "businessNumber": "123-45-67890",
  "phoneNumber": "010-1234-5678",
  "imageUrl": "https://...",
  "createdDate": "2026-01-15T10:00:00",
  "updatedDate": "2026-01-15T13:00:00"
}
```
- **Status Code:** `200 OK`
- **Description:** 판매자 프로필 이미지 URL을 업데이트

---

## 16. 관리자 회원 관리 (Admin User)

### 16.1 회원 목록 조회

- **Endpoint:** `/api/admin/users`
- **Method:** `GET`
- **Request Header:** `Authorization: Bearer {accessToken}`
- **Request Body:** `없음`
- **Request Query Parameters:**
  - `keyword` (optional): 검색어 (닉네임, 이름 등)
  - `userType` (optional): 회원 구분 (USER, SELLER)
- **Response Body:**
```json
[
  {
    "id": 1,
    "userName": "user123",
    "userEmail": "user@example.com",
    "userNickname": "닉네임",
    "introduction": "소개글",
    "phoneNumber": "010-1234-5678",
    "imageUrl": "https://...",
    "createdDate": "2026-01-15T10:00:00",
    "updatedDate": "2026-01-15T10:00:00"
  }
]
```
- **Status Code:** `200 OK`
- **Description:** 관리자 권한으로 회원(User/Seller) 목록을 조회 (역할 필터링 및 키워드 검색 지원)

---

### 16.2 회원 상세 조회

- **Endpoint:** `/api/admin/users/{userId}`
- **Method:** `GET`
- **Request Header:** `Authorization: Bearer {accessToken}`
- **Request Body:** `없음`
- **Request Query Parameters:**
  - `userType` (required): 회원 구분 (USER, SELLER)
- **Response Body:**
```json
{
  "id": 1,
  "userName": "user123",
  "userEmail": "user@example.com",
  "userNickname": "닉네임",
  "introduction": "소개글",
  "phoneNumber": "010-1234-5678",
  "imageUrl": "https://...",
  "createdDate": "2026-01-15T10:00:00",
  "updatedDate": "2026-01-15T10:00:00"
}
```
- **Status Code:** `200 OK`
- **Description:** 특정 회원(일반 사용자 또는 판매자)의 상세 가입 정보와 계정 상태를 조회

---

### 16.3 회원 삭제

- **Endpoint:** `/api/admin/users/{userId}`
- **Method:** `DELETE`
- **Request Header:** `Authorization: Bearer {accessToken}`
- **Request Body:** `없음`
- **Response Body:** `없음`
- **Status Code:** `204 No Content`
- **Description:** 일반 사용자 계정을 소프트 삭제 처리하여 플랫폼 이용을 중단

---

### 16.4 회원 정보 수정

- **Endpoint:** `/api/admin/users/{userId}`
- **Method:** `PUT`
- **Request Header:** `Authorization: Bearer {accessToken}`
- **Request Body:**
```json
{
  "userNickname": "수정된닉네임",
  "userEmail": "newemail@example.com",
  "phoneNumber": "010-9876-5432"
}
```
- **Response Body:** `없음`
- **Status Code:** `200 OK`
- **Description:** 관리자가 일반 사용자의 프로필 정보나 계정 설정을 강제로 수정

---

### 16.5 판매자 삭제

- **Endpoint:** `/api/admin/users/sellers/{sellerId}`
- **Method:** `DELETE`
- **Request Header:** `Authorization: Bearer {accessToken}`
- **Request Body:** `없음`
- **Response Body:** `없음`
- **Status Code:** `204 No Content`
- **Description:** 판매자 계정을 소프트 삭제 처리

---

### 16.6 판매자 정보 수정

- **Endpoint:** `/api/admin/users/sellers/{sellerId}`
- **Method:** `PUT`
- **Request Header:** `Authorization: Bearer {accessToken}`
- **Request Body:**
```json
{
  "userNickname": "수정된닉네임",
  "brandName": "수정된브랜드명",
  "phoneNumber": "010-9876-5432"
}
```
- **Response Body:** `없음`
- **Status Code:** `200 OK`
- **Description:** 관리자가 판매자의 브랜드 정보나 연락처 등 사업자 정보를 강제로 수정

---

## 17. 관리자 상품 관리 (Admin Product)

### 17.1 상품 목록 조회

- **Endpoint:** `/api/admin/products`
- **Method:** `GET`
- **Request Header:** `Authorization: Bearer {accessToken}`
- **Request Body:** `없음`
- **Response Body:**
```json
[
  {
    "id": 1,
    "name": "상품명",
    "price": 100000,
    "brand": "NIKE",
    "category": "SHOES",
    "status": "ON_SALE",
    "sellerId": 1,
    "sellerName": "판매자명",
    "stock": 10,
    "createdDate": "2026-01-15T10:00:00"
  }
]
```
- **Status Code:** `200 OK`
- **Description:** 플랫폼에 등록된 모든 상품의 목록과 핵심 정보를 조회 (ROLE_ADMIN 권한 필요)

---

### 17.2 상품 상세 조회

- **Endpoint:** `/api/admin/products/{productId}`
- **Method:** `GET`
- **Request Header:** `Authorization: Bearer {accessToken}`
- **Request Body:** `없음`
- **Response Body:**
```json
{
  "id": 1,
  "name": "상품명",
  "price": 100000,
  "brand": "NIKE",
  "category": "SHOES",
  "description": "상품 설명",
  "thumbnailUrl": "https://...",
  "descriptionImages": ["https://..."],
  "sizes": ["250", "260", "270"],
  "stock": 10,
  "status": "ON_SALE",
  "sellerId": 1,
  "sellerName": "판매자명",
  "createdDate": "2026-01-15T10:00:00",
  "updatedDate": "2026-01-15T10:00:00"
}
```
- **Status Code:** `200 OK`
- **Description:** 특정 상품의 상세 규격, 재고 상태 및 판매자 정보를 포함한 전체 데이터를 조회 (ROLE_ADMIN 권한 필요)

---

### 17.3 상품 상태 수정

- **Endpoint:** `/api/admin/products/{productId}/status`
- **Method:** `PUT`
- **Request Header:** `Authorization: Bearer {accessToken}`
- **Request Body:**
```json
{
  "status": "SOLD_OUT"
}
```
- **Response Body:** `없음`
- **Status Code:** `200 OK`
- **Description:** 운영 정책 위반 또는 재고 관리 등의 사유로 관리자가 상품의 판매 상태를 강제 수정

---

### 17.4 상품 삭제

- **Endpoint:** `/api/admin/products/{productId}`
- **Method:** `DELETE`
- **Request Header:** `Authorization: Bearer {accessToken}`
- **Request Body:** `없음`
- **Response Body:** `없음`
- **Status Code:** `204 No Content`
- **Description:** 플랫폼 운영상 부적절한 상품을 시스템에서 영구적 또는 소프트 삭제 처리

---

## 18. 관리자 주문 관리 (Admin Order)

### 18.1 주문 목록 조회

- **Endpoint:** `/api/admin/orders`
- **Method:** `GET`
- **Request Header:** `Authorization: Bearer {accessToken}`
- **Request Body:** `없음`
- **Request Query Parameters:**
  - `nickname` (optional): 사용자 닉네임 검색 조건
- **Response Body:**
```json
[
  {
    "id": 1,
    "orderId": "order-12345",
    "userId": 1,
    "userNickname": "구매자명",
    "totalAmount": 200000,
    "paymentStatus": "PAID",
    "orderDate": "2026-01-15T10:00:00"
  }
]
```
- **Status Code:** `200 OK`
- **Description:** 전체 주문 목록을 조회하거나 특정 닉네임을 가진 사용자의 주문을 검색 (ROLE_ADMIN 권한 필요)

---

### 18.2 주문 상세 조회

- **Endpoint:** `/api/admin/orders/{orderId}`
- **Method:** `GET`
- **Request Header:** `Authorization: Bearer {accessToken}`
- **Request Body:** `없음`
- **Response Body:**
```json
{
  "orderId": "order-12345",
  "userId": 1,
  "userNickname": "구매자명",
  "userEmail": "user@example.com",
  "shippingAddress": "서울시 강남구...",
  "totalAmount": 200000,
  "paymentStatus": "PAID",
  "orderStatus": "CONFIRMED",
  "orderDate": "2026-01-15T10:00:00",
  "orderItems": [
    {
      "productId": 1,
      "productName": "상품명",
      "quantity": 2,
      "price": 100000,
      "status": "SHIPPED"
    }
  ]
}
```
- **Status Code:** `200 OK`
- **Description:** 특정 주문의 상세 정보(구매자 인적사항, 결제 정보, 주문 상품 목록 등)를 조회 (ROLE_ADMIN 권한 필요)

---

## 19. 관리자 프로필 관리 (Admin Profile)

### 19.1 관리자 프로필 조회

- **Endpoint:** `/api/admin/profile`
- **Method:** `GET`
- **Request Header:** `Authorization: Bearer {accessToken}`
- **Request Body:** `없음`
- **Response Body:**
```json
{
  "id": 1,
  "userName": "admin",
  "userEmail": "admin@example.com",
  "imageUrl": "https://...",
  "createdDate": "2026-01-15T10:00:00",
  "updatedDate": "2026-01-15T10:00:00"
}
```
- **Status Code:** `200 OK`
- **Description:** 현재 로그인한 관리자의 프로필 정보를 조회

---

### 19.2 관리자 프로필 수정

- **Endpoint:** `/api/admin/profile`
- **Method:** `PATCH`
- **Request Header:** `Authorization: Bearer {accessToken}`
- **Request Body:**
```json
{
  "userEmail": "newadmin@example.com"
}
```
- **Response Body:**
```json
{
  "id": 1,
  "userName": "admin",
  "userEmail": "newadmin@example.com",
  "imageUrl": "https://...",
  "createdDate": "2026-01-15T10:00:00",
  "updatedDate": "2026-01-15T12:00:00"
}
```
- **Status Code:** `200 OK`
- **Description:** 관리자의 프로필 정보를 수정

---

### 19.3 관리자 프로필 이미지 Presigned URL 발급

- **Endpoint:** `/api/admin/profile/presigned-url`
- **Method:** `POST`
- **Request Header:** `Authorization: Bearer {accessToken}`
- **Request Body:**
```json
{
  "contentType": "image/jpeg"
}
```
- **Response Body:**
```json
{
  "presignedUrl": "https://...",
  "imageKey": "admin/1/..."
}
```
- **Status Code:** `200 OK`
- **Description:** S3에 관리자 프로필 이미지를 업로드하기 위한 Presigned URL 발급

---

### 19.4 관리자 프로필 이미지 업데이트

- **Endpoint:** `/api/admin/profile/image`
- **Method:** `PATCH`
- **Request Header:** `Authorization: Bearer {accessToken}`
- **Request Body:**
```json
{
  "imageUrl": "https://..."
}
```
- **Response Body:**
```json
{
  "id": 1,
  "userName": "admin",
  "userEmail": "admin@example.com",
  "imageUrl": "https://...",
  "createdDate": "2026-01-15T10:00:00",
  "updatedDate": "2026-01-15T13:00:00"
}
```
- **Status Code:** `200 OK`
- **Description:** 관리자 프로필 이미지 URL을 업데이트

---