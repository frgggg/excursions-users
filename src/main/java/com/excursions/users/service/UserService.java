package com.excursions.users.service;

import com.excursions.users.exception.ServiceException;
import com.excursions.users.model.User;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserService {

    String USER_CACHE_NAME = "userCache";

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = ServiceException.class)
    @Caching(put= {@CachePut(value= USER_CACHE_NAME, key= "#result.id")})
    User create(String name);

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = ServiceException.class)
    @Caching(put= {@CachePut(value= USER_CACHE_NAME, key= "#result.id")})
    User update(Long id, String name);

    @Cacheable(value = USER_CACHE_NAME, key = "#id")
    User findById(Long id);

    List<User> findAll();

    @Caching(evict= {@CacheEvict(value= USER_CACHE_NAME, key= "#id")})
    void deleteById(Long id);

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = ServiceException.class)
    void coinsUpByUser(Long id, Long coins);
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = ServiceException.class)
    void coinsDownByUser(Long id, Long coins);

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = ServiceException.class)
    void coinsUpByExcursion(Long id, Long coins);
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = ServiceException.class)
    void coinsDownByExcursion(Long id, Long coins);
}
