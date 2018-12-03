package io.merculet.auth.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 设置用户密码
 *
 * @author zhou liming
 * @package io.merculet.auth.bean
 * @date 2018-09-06 10:35
 * @description
 */
@Data
public class SetUserPwdBean implements Serializable {

    @JsonProperty("wallet_user_id")
    @JSONField(name = "wallet_user_id")
    private String walletUserId;

    @JsonProperty("new_password")
    @JSONField(name = "new_password")
    private String newPassword;

    @JsonProperty("new_password_again")
    @JSONField(name = "new_password_again")
    private String newPasswordAgain;

    @JsonProperty("origin_password")
    @JSONField(name = "origin_password")
    private String originPassword;

    /**
     * 手机号
     */
    @JsonProperty("phone_no")
    @JSONField(name = "phone_no")
    private String phoneNumber;

    /**
     * 区域
     */
    @JsonProperty("zone_code")
    @JSONField(name = "zone_code")
    private String zoneCode;
}
