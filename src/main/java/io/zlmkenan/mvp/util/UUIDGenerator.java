package io.zlmkenan.mvp.util;

import java.util.UUID;

/**
 * @author zhou liming
 * @package io.zlmkenan.mvp
 * @date 2018/12/3 16:54
 * @description
 */
public class UUIDGenerator {
    public static final String generate() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "");
    }

}
