package com.excursions.users.service.impl;

import com.excursions.users.exception.ServiceException;
import com.excursions.users.model.User;
import com.excursions.users.repository.UserRepository;
import com.excursions.users.service.ExcursionService;
import com.excursions.users.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.excursions.users.exception.message.UserServiceExceptionMessages.*;
import static com.excursions.users.log.message.UserServiceLogMessages.*;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private static final String USER_CACHE_NAME = "userCache";

    private static final boolean IS_UP_FLAG = true;
    private static final boolean IS_NOT_UP_FLAG = false;

    private UserRepository userRepository;
    private UserServiceImpl self;
    private ExcursionService excursionService;

    @Lazy
    @Autowired
    protected UserServiceImpl(UserRepository userRepository, UserServiceImpl self, ExcursionService excursionService) {
        this.userRepository = userRepository;
        this.self = self;
        this.excursionService = excursionService;
    }

    @Caching(put= {@CachePut(value= USER_CACHE_NAME, key= "#result.id")})
    @Override
    public User create(String name) {
        User savedUser;
        try {
            savedUser = saveOrUpdateUtil(new User(name));
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
        log.info(USER_SERVICE_LOG_NEW_USER, savedUser);
        return savedUser;
    }

    @Caching(put= {@CachePut(value= USER_CACHE_NAME, key= "#result.id")})
    @Override
    public User update(Long id, String name) {
        User userForUpdate = self.findById(id);
        userForUpdate.setName(name);
        User updatedUser;
        try {
            updatedUser = saveOrUpdateUtil(userForUpdate);
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
        log.info(USER_SERVICE_LOG_UPDATE_USER, userForUpdate, updatedUser);
        return updatedUser;
    }

    @Override
    public List<User> findAll() {
        log.info(USER_SERVICE_LOG_GET_ALL_USERS);
        return StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Cacheable(value = USER_CACHE_NAME, key = "#id")
    @Override
    public User findById(Long id) {
        Optional<User> optionalPlace = userRepository.findById(id);
        optionalPlace.orElseThrow(() -> new ServiceException(String.format(USER_SERVICE_EXCEPTION_NOT_EXIST_USER, id)));
        log.info(USER_SERVICE_LOG_GET_USER, optionalPlace.get());
        return optionalPlace.get();
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Caching(evict= {@CacheEvict(value= USER_CACHE_NAME, key= "#id")})
    @Override
    public void deleteById(Long id) {
        User userForDelete = self.findById(id);

        if(excursionService.userTicketsCount(id) > 0){
            throw new ServiceException(String.format(USER_SERVICE_EXCEPTION_USER_HAVE_TICKETS, id));
        }

        userRepository.deleteById(id);
        log.info(USER_SERVICE_LOG_DELETE_USER, userForDelete);
    }

    @Caching(put= {@CachePut(value= USER_CACHE_NAME, key= "#result.id")})
    @Override
    public User coinsDownByExcursion(Long id, Long coins) {
        User updatedUser = updateCoins(id, coins, IS_NOT_UP_FLAG);
        log.info(USER_SERVICE_LOG_DOWN_COINS_BY_EXCURSION, id, coins);
        return updatedUser;
    }

    @Caching(put= {@CachePut(value= USER_CACHE_NAME, key= "#result.id")})
    @Override
    public User coinsDownByUser(Long id, Long coins) {
        User updatedUser = updateCoins(id, coins, IS_NOT_UP_FLAG);
        log.info(USER_SERVICE_LOG_DOWN_COINS_BY_USER, id, coins);
        return updatedUser;
    }

    @Caching(put= {@CachePut(value= USER_CACHE_NAME, key= "#result.id")})
    @Override
    public User coinsUpByExcursion(Long id, Long coins) {
        User updatedUser = updateCoins(id, coins, IS_UP_FLAG);
        log.info(USER_SERVICE_LOG_UP_COINS_BY_EXCURSION, id, coins);
        return updatedUser;
    }

    @Caching(put= {@CachePut(value= USER_CACHE_NAME, key= "#result.id")})
    @Override
    public User coinsUpByUser(Long id, Long coins) {
        User updatedUser = updateCoins(id, coins, IS_UP_FLAG);
        log.info(USER_SERVICE_LOG_UP_COINS_BY_USER, id, coins);
        return updatedUser;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    private User updateCoins(Long id, Long coins, boolean isUp) {
        User user = self.findById(id);

        Long oldCoins = user.getCoins();
        Long newCoins = getNewCoins(coins, isUp, oldCoins);

        LocalDateTime oldCoinsLastUpdate = user.getCoinsLastUpdate();
        LocalDateTime newCoinsLastUpdate = LocalDateTime.now();

        if(userRepository.updateCoins(id, oldCoins, oldCoinsLastUpdate, newCoins, newCoinsLastUpdate) == 0) {
            throw new ServiceException(String.format(USER_SERVICE_EXCEPTION_WRONG_COINS_UPDATE, user));
        }

        user.setCoins(newCoins);
        user.setCoinsLastUpdate(newCoinsLastUpdate);
        return user;
    }

    private Long getNewCoins(Long coins, boolean isUp, Long currentCoins) {
        Long newCoins = currentCoins;
        boolean needException = false;
        if(coins == null) {
            needException = true;
        } else if(coins <= 0) {
            needException = true;
        } else {
            if (isUp) {
                if ((Long.MAX_VALUE - currentCoins) < coins) {
                    needException = true;
                }
                newCoins += coins;
            } else {
                if ((currentCoins - coins) < 0l) {
                    needException = true;
                }
                newCoins -= coins;
            }
        }

        if(needException) {
            throw new ServiceException(USER_SERVICE_EXCEPTION_WRONG_COINS_ARGS);
        }
        return newCoins;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    private User saveOrUpdateUtil(User user) {
        User userForSave = new User(user.getName());
        userForSave.setId(user.getId());
        userForSave.setCoins(user.getCoins());
        userForSave.setCoinsLastUpdate(user.getCoinsLastUpdate());

        return userRepository.save(userForSave);
    }
}
