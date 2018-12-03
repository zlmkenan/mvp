package io.merculet.auth.repository;

import cn.magicwindow.score.common.entity.UserSysWallet;

import java.util.List;

/** 
 * 
 * @author : zhouliming 
 * @class UserSysWalletRepository
 * @date 创建时间：2018年9月11日 上午10:23:35
 */
public interface UserSysWalletRepository extends BaseEntityRepository<UserSysWallet>{

    /**
     * 通过sysUserId以及walletUserId唯一确定用户
     * @param sysUserId
     * @param walletUserId
     * @return
     */
    UserSysWallet findOneBySysUserIdAndWalletUserIdAndDeletedIsFalse(Long sysUserId, Long walletUserId);
    
    /**
     * 查询wallet绑定的所有sys auth
     * @param walletUserId
     * @return
     */
    List<UserSysWallet> findAllByWalletUserIdAndDeletedIsFalse(Long walletUserId);
    
    /**
     * 查询wallet绑定的所有sys auth
     * @param sysUserId
     * @return
     */
    List<UserSysWallet> findAllBySysUserIdAndDeletedIsFalse(Long sysUserId);
}
