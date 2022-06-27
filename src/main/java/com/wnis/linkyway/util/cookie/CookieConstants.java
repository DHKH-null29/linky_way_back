package com.wnis.linkyway.util.cookie;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CookieConstants {

    public static final int VERIFICATION_COOKIE_EXPIRATION_MINUTE = 300; //SECOND 인데 MINUTE으로 네이밍 되어있 음
    public static final String VERIFICATION_COOKIE_NAME = "evc";

}
