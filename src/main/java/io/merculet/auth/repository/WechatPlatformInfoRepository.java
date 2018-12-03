package io.merculet.auth.repository;

import cn.magicwindow.score.common.entity.WechatPlatformInfo;

/**
 * @author cuixing
 * @package io.merculet.community.repository
 * @class WechatPlatformInfoRepository
 * @email xing.cui@magicwindow.cn
 * @date 2018/11/21 下午2:26
 * @description
 */
public interface WechatPlatformInfoRepository extends BaseEntityRepository<WechatPlatformInfo> {

    WechatPlatformInfo findByWxAppIdAndDeletedIsFalse(String wid);
}
