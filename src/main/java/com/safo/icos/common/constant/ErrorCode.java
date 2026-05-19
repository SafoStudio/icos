package com.safo.icos.common.constant;

public final class ErrorCode {
    public static final String USER_NOT_FOUND = "USER_001";
    public static final String EMAIL_ALREADY_EXISTS = "USER_002";
    public static final String INVALID_CREDENTIALS = "AUTH_001";
    public static final String TOKEN_EXPIRED = "AUTH_002";
    public static final String TOKEN_REVOKED = "AUTH_003";
    public static final String TOKEN_NOT_FOUND = "AUTH_004";
    public static final String MEMBERSHIP_NOT_FOUND = "MEMBERSHIP_001";
    public static final String USER_NOT_ACTIVE = "MEMBERSHIP_002";

    private ErrorCode() {}
}