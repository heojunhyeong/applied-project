package com.team.wearly.global.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;


/**
 * 데이터가 생성된 시간(createdDate)와 수정된 시간(updatedDate)를 JPA가 자동으로 넣어주게 만드는 설정
 * 해당 클래스와 JpaAuditingConfig를 활용하여 Entity마다 createdDate와 updatedDate를 사용하지 않고
 * 해당 클래스를 상속하는 것으로 사용 가능하다
 *
 * @MappedSuperclass 어노테이션은 테이블로 생성되지 않고 상속받는 자식 클래스에게 매핑 정보만 제공한다
 *
 * TODO: 지금까지 작성했던 Entity 클래스들에 있는 시간 관련 필드를 삭제하고 해당 클래스를 상속받게 수정
 *
 * @author 허준형
 * @DateOfCreated 2026-01-10
 * @DateOfEdit 2025-01-10
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseTimeEntity {

    @CreatedDate
    // 생성 시간은 수정되지 않도록 설정한다
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime updatedDate;
}
