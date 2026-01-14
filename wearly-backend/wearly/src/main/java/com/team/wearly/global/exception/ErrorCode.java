package com.team.wearly.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {


    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),

    MAIL_SEND_ERROR(HttpStatus.BAD_REQUEST, "잘못된 요청입니다"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND,"존재하지 않는 유저입니다." ),
    RESET_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND,"존재하지 않는 토큰입니다."),
    RESET_TOKEN_ALREADY_USED(HttpStatus.BAD_REQUEST,"이미 사용된 토큰입니다."),
    RESET_TOKEN_EXPIRED(HttpStatus.BAD_REQUEST,"유효기간이 지난 토큰입니다.");



    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
