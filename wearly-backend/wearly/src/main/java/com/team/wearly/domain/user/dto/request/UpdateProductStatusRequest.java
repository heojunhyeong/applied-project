package com.team.wearly.domain.user.dto.request;

import com.team.wearly.domain.product.entity.enums.ProductStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductStatusRequest {
    @NotNull(message = "판매 상태는 필수입니다")
    private ProductStatus status;  // 판매 중 또는 판매 중단
}
