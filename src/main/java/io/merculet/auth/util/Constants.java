package io.merculet.auth.util;

import java.util.HashSet;

/**
 * @author zhou liming
 * @package io.merculet.auth.util
 * @date 2018-09-05 15:58
 * @description
 */
public class Constants {
    /**
     * 发送验证码类型
     */
    public static final String VALIDATE_TYPE_LOGIN = "LOGIN";
    public static final String VALIDATE_TYPE_RED_PACKET = "RED_PACKET";
    public static final String VALIDATE_TYPE_PAYMENT = "PAYMENT";
    public static final String VALIDATE_TYPE_UPDATE_PAYMENT_PWD = "UPDATE_PAYMENT_PWD";
    public static final String VALIDATE_TYPE_WITHDRAW = "WITHDRAW";
    public static final HashSet<String> VALIDATE_TYPES = new HashSet<String>() {{
        add(Constants.VALIDATE_TYPE_LOGIN);
        add(Constants.VALIDATE_TYPE_RED_PACKET);
        add(Constants.VALIDATE_TYPE_PAYMENT);
        add(Constants.VALIDATE_TYPE_UPDATE_PAYMENT_PWD);
        add(Constants.VALIDATE_TYPE_WITHDRAW);

    }};

    public static final String DEFAULT_USER_NAME = "****";
    public static final String DEFAULT_USER_AVATAR = "https://mwimg.mlinks.cc/wallet_image_152_1525870476284.png";
}
