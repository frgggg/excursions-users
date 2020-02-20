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

import javax.persistence.EntityManager;
import javax.validation.ConstraintViolationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.excursions.users.exception.message.UserServiceExceptionMessages.*;
import static com.excursions.users.log.message.UserServiceLogMessages.*;
import static com.excursions.users.model.User.USER_COINS_VALIDATION_MESSAGE;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private static final String SERVICE_NAME = "UserServiceImpl";

    private static final String USER_CACHE_NAME = "userCache";

    private static final boolean IS_UP_FLAG = true;
    private static final boolean IS_NOT_UP_FLAG = false;

    private UserRepository userRepository;
    private EntityManager entityManager;
    private UserServiceImpl self;
    private ExcursionService excursionService;

    @Lazy
    @Autowired
    protected UserServiceImpl(UserRepository userRepository, EntityManager entityManager, UserServiceImpl self, ExcursionService excursionService) {
        this.userRepository = userRepository;
        this.entityManager = entityManager;
        this.self = self;
        this.excursionService = excursionService;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = ServiceException.class)
    @Caching(put= {@CachePut(value= USER_CACHE_NAME, key= "#result.id")})
    @Override
    public User create(String name) {
        User savedUser = saveUtil(null, name, null);
        log.info(USER_SERVICE_LOG_NEW_USER, savedUser);
        return savedUser;
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);

        log.info(USER_SERVICE_LOG_GET_ALL_USERS);
        return users;
    }

    @Cacheable(value = USER_CACHE_NAME, key = "#id")
    @Override
    public User findById(Long id) {
        Optional<User> optionalPlace = userRepository.findById(id);
        if(!optionalPlace.isPresent()) {
            throw new ServiceException(SERVICE_NAME, String.format(USER_SERVICE_EXCEPTION_NOT_EXIST_USER, id));
        }
        User findByIdUser = optionalPlace.get();
        log.info(USER_SERVICE_LOG_GET_USER, findByIdUser);
        return findByIdUser;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = ServiceException.class)
    @Caching(put= {@CachePut(value= USER_CACHE_NAME, key= "#result.id")})
    @Override
    public User update(Long id, String name) {
        User userForUpdate = self.findById(id);
        User updatedUser = saveUtil(id, name, userForUpdate.getCoins());
        log.info(USER_SERVICE_LOG_UPDATE_USER, userForUpdate, updatedUser);
        return updatedUser;
    }

    @Caching(evict= {@CacheEvict(value= USER_CACHE_NAME, key= "#id")})
    @Override
    public void deleteById(Long id) {
        User userForDelete = self.findById(id);

        if(excursionService.userTicketsCount(id) > 0){
            throw new ServiceException(SERVICE_NAME, String.format(USER_SERVICE_EXCEPTION_USER_HAVE_TICKETS, id));
        }

        userRepository.deleteById(id);
        log.info(USER_SERVICE_LOG_DELETE_USER, userForDelete);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = ServiceException.class)
    @Caching(put= {@CachePut(value= USER_CACHE_NAME, key= "#result.id")})
    @Override
    public User coinsDownByExcursion(Long id, Long coins) {
        User updatedUser = updateCoins(id, coins, IS_NOT_UP_FLAG);
        log.info(USER_SERVICE_LOG_DOWN_COINS_BY_EXCURSION, id, coins);
        return updatedUser;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = ServiceException.class)
    @Caching(put= {@CachePut(value= USER_CACHE_NAME, key= "#result.id")})
    @Override
    public User coinsDownByUser(Long id, Long coins) {
        User updatedUser = updateCoins(id, coins, IS_NOT_UP_FLAG);
        log.info(USER_SERVICE_LOG_DOWN_COINS_BY_USER, id, coins);
        return updatedUser;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = ServiceException.class)
    @Caching(put= {@CachePut(value= USER_CACHE_NAME, key= "#result.id")})
    @Override
    public User coinsUpByExcursion(Long id, Long coins) {
        User updatedUser = updateCoins(id, coins, IS_UP_FLAG);
        log.info(USER_SERVICE_LOG_UP_COINS_BY_EXCURSION, id, coins);
        return updatedUser;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = ServiceException.class)
    @Caching(put= {@CachePut(value= USER_CACHE_NAME, key= "#result.id")})
    @Override
    public User coinsUpByUser(Long id, Long coins) {
        User updatedUser = updateCoins(id, coins, IS_UP_FLAG);
        log.info(USER_SERVICE_LOG_UP_COINS_BY_USER, id, coins);
        return updatedUser;
    }

    private User updateCoins(Long id, Long coins, boolean isUp) {
        checkCoins(coins);
        User userForUpdate = self.findById(id);

        Long nowCoins = userForUpdate.getCoins();
        if(isUp) {
            if((Long.MAX_VALUE - nowCoins) < coins) {
                throw new ServiceException(SERVICE_NAME, USER_COINS_VALIDATION_MESSAGE);
            }
            nowCoins += coins;
        } else {
            nowCoins -= coins;
        }
        return saveUtil(id, userForUpdate.getName(), nowCoins);
    }

    private void checkCoins(Long coins) {
        boolean needException = false;
        if(coins == null) {
            needException = true;
        } else if(coins <= 0) {
            needException = true;
        }

        if(needException) {
            throw new ServiceException(SERVICE_NAME, USER_SERVICE_EXCEPTION_WRONG_COINS_ARGS);
        }
    }

    private User saveUtil(Long id, String name, Long coins) {
        User savedUser;
        User userForSave = new User(name);
        if(id != null) {
            userForSave.setId(id);
        }
        if(coins != null) {
            userForSave.setCoins(coins);
        }

        try {
            savedUser = userRepository.save(userForSave);
            entityManager.flush();
        } catch (ConstraintViolationException e) {
            throw new ServiceException(SERVICE_NAME, e.getConstraintViolations().iterator().next().getMessage());
        }

        return savedUser;
    }
}
