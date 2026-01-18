package com.team.wearly.global.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PresignedUrlVo {
    private String url;
    private String key;
}