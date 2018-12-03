package io.merculet.auth.repository;

import cn.magicwindow.score.common.entity.WalletUser;

/**
 * 钱包用户数据操作
 *
 * @author zhou liming
 * @package io.merculet.open.auth.repository
 * @date 2018-09-04 14:49
 * @description
 */
public interface WalletUserRepository extends BaseEntityRepository<WalletUser> {

    /**
     * 依据主键查询
     *
     * @param walletUserId
     * @return
     */
    WalletUser findOneByIdAndDeletedIsFalse(Long walletUserId);

    /**
     * 依据手机号查询
     *
     * @param phoneNo
     * @return
     */
    WalletUser findOneByPhoneNumberAndDeletedIsFalse(String phoneNo);

    /**
     * 依据邀请码查询
     *
     * @param inviteCode
     * @return
     */
    WalletUser findOneByInviteCodeAndDeletedIsFalse(String inviteCode);

}
