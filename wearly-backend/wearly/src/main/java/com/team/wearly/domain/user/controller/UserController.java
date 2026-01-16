package com.team.wearly.domain.user.controller;
import com.team.wearly.domain.user.dto.request.SignupRequest;
import com.team.wearly.domain.user.dto.response.ErrorResponse;
import com.team.wearly.domain.user.dto.response.SignupResponse;
import com.team.wearly.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    /**
     * 신규 회원의 가입 정보를 받아 유효성 검사 후 계정을 생성함
     * 일반 사용자(USER)와 판매자(SELLER) 타입을 구분하여 처리하며, 보안 정책상 "admin" 단어 포함을 제한함
     *
     * @param request 이메일, 비밀번호, 닉네임, 역할(Role) 등이 포함된 가입 요청 DTO
     * @return 성공 시 201 Created와 가입 완료 정보 반환
     * @author 최윤혁
     * @DateOfCreated 2026-01-13
     * @DateOfEdit 2026-01-13
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request) {
        try {
            SignupResponse response = userService.signup(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            ErrorResponse errorResponse = new ErrorResponse("BAD_REQUEST", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse("INTERNAL_SERVER_ERROR", "서버 오류가 발생했습니다");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    /**
     * {@code @Valid} 검증 실패 시 발생하는 예외를 가로채어 필드별 에러 메시지를 응답함
     * "admin" 사용 금지 정책(@NoAdminWord 등) 위반 시의 메시지도 포함됨
     *
     * @param ex 유효성 검증 실패 예외 객체
     * @return 에러가 발생한 필드명과 메시지를 담은 Map (400 Bad Request)
     * @author 최윤혁
     * @DateOfCreated 2026-01-13
     * @DateOfEdit 2026-01-13
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}
