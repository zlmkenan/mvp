package io.merculet.auth.repository;

import cn.magicwindow.score.common.entity.UserDeviceInfo;

/**
 * @author Zhou Tao
 * @package cn.merculet.auth.repository
 * @class UserDeviceInfoRepository
 * @email tao.zhou@magicwindow.cn
 * @date 2018/10/30 下午3:11
 * @description
 */
public interface UserDeviceInfoRepository extends BaseEntityRepository<UserDeviceInfo> {

    UserDeviceInfo findByUserIdAndDeletedIsFalse(Long userId);

}
