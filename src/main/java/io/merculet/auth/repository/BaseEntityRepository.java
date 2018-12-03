package io.merculet.auth.repository;

import cn.magicwindow.score.common.entity.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * @author zhou liming
 * @package io.merculet.open.auth.repository
 * @date 2018/8/22 15:10
 * @description
 */
@NoRepositoryBean
public interface BaseEntityRepository<T extends BaseEntity> extends JpaRepository<T, Long> {

}
