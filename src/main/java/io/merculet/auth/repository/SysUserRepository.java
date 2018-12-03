package io.merculet.auth.repository;

import cn.magicwindow.score.common.domain.sys.SysUser;

/**
 * @author Edmund.Wang
 * @package cn.merculet.auth.repository
 * @class SysUserRepository
 * @email edmund.wang@magicwindow.cn
 * @date 2018/9/25 上午10:23
 * @description
 */
public interface SysUserRepository extends BaseEntityRepository<SysUser>{

    /**
     * 查找sys auth
     * @param sysUserId
     * @return
     */
    SysUser findOneByIdAndDeletedIsFalse(Long sysUserId);
}