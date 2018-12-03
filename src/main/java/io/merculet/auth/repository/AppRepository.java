package io.merculet.auth.repository;

import cn.magicwindow.score.common.entity.App;

/**
 * @author : zhouliming
 * @class AppRepository
 * @date 创建时间：2018年9月4日 下午4:33:02
 */
public interface AppRepository extends BaseEntityRepository<App> {
    /**
     * 依据appId查询app
     *
     * @param appId
     * @return
     */
    App findOneByIdAndDeletedIsFalse(Long appId);

    /**
     * 依据appKey查询app
     *
     * @param appKey
     * @return
     */
    App findOneByAppKeyAndDeletedIsFalse(String appKey);
}
