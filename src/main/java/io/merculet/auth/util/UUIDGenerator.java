package io.merculet.auth.util;

import java.util.UUID;

/**
 * @author zhou liming
 * @package io.merculet.auth.util
 * @date 2018-09-04 19:49
 * @description
 */
public class UUIDGenerator {
    public static final String generate() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "");
    }

}
