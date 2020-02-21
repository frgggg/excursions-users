package com.excursions.users.repository;

import com.excursions.users.model.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

public interface UserRepository extends CrudRepository<User, Long> {

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Modifying(clearAutomatically = true)
    @Query(value = "update User u set u.coins=?4, u.coinsLastUpdate = ?5 where u.id=?1 and u.coins = ?2 and u.coinsLastUpdate = ?3")
    int updateCoins(Long id, Long oldCoins, LocalDateTime oldCoinsLastUpdate, Long newCoins, LocalDateTime newCoinsLastUpdate);

    @Transactional
    @Modifying
    @Query(value = "delete from User u where u.coins=0 and u.coinsLastUpdate = ?2 and u.id=?1")
    void deleteByCoinsLastUpdateAndZeroCoins(Long id, LocalDateTime newCoinsLastUpdate);
}
