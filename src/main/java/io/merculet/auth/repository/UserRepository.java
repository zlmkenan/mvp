package io.merculet.auth.repository;

import cn.magicwindow.score.common.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author zhou liming
 * @package io.merculet.open.auth.repository
 * @date 2018-08-22 15:14
 * @description
 */
public interface UserRepository extends BaseEntityRepository<User> {

    /**
     * 通过appId以及externalUserId唯一确定用户
     *
     * @param appId
     * @param externalUserId
     * @return
     */
    User findOneByAppIdAndExternalUserIdAndDeletedIsFalse(Long appId, String externalUserId);

    /**
     * 依据walletUserId查找所有用户
     *
     * @param walletUserId
     * @return
     */
    List<User> findAllByWalletUserIdAndDeletedIsFalse(Long walletUserId);

    /**
     * 依据主键查找用户
     *
     * @param userId
     * @return
     */
    User findOneByIdAndDeletedIsFalse(Long userId);

    /**
     * 查找app下所有用户
     *
     * @param appId
     * @return
     */
    List<User> findAllByAppIdAndDeletedIsFalse(Long appId);

    /**
     * 依据openId查询用户
     *
     * @param openId
     * @return
     */
    User findOneByExternalUserOpenIdAndDeletedIsFalse(String openId);

    /**
     * 依据secretKey查询用户
     *
     * @param secretKey
     * @return
     */
    User findOneByExternalUserSecretKeyAndDeletedIsFalse(String secretKey);

    /**
     * 依据externalUserId查询用户
     *
     * @param externalUserId
     * @return
     */
    User findOneByExternalUserIdAndDeletedIsFalse(String externalUserId);

    /**
     * 通过walletUserId,channel,appId查询唯一用户
     *
     * @param walletUserId
     * @param channel
     * @param appId
     * @return
     */
    User findOneByWalletUserIdAndChannelAndAppIdAndDeletedIsFalse(Long walletUserId, Integer channel, Long appId);

    /**
     * 依据openPlatformId查询用户
     *
     * @param openPlatformId
     * @return
     */
    User findOneByOpenPlatformIdAndDeletedIsFalse(String openPlatformId);

    /**
     * 通过openPlatformId分页查询用户
     * @param openPlatformId
     * @param pageable
     * @return
     */
    Page<User> findAllByOpenPlatformIdAndDeletedIsFalse(String openPlatformId, Pageable pageable);

    /**
     * 统计openPlatformId未处理的场景总数
     * @param openPlatformId
     * @return
     */
    Long countUserByOpenPlatformIdAndDeletedIsFalse(String openPlatformId);

    /**
     * 通过appId以及externalUserOpenId唯一确定用户
     *
     * @param appId
     * @param externalUserOpenId
     * @return
     */
    User findOneByAppIdAndExternalUserOpenIdAndDeletedIsFalse(Long appId, String externalUserOpenId);

    /**
     * 通过appId以及walletUserId唯一确定用户
     *
     * @param appId
     * @param walletUserId
     * @return
     */
    List<User> findAllByAppIdAndWalletUserIdAndDeletedIsFalse(Long appId, Long walletUserId);
}
