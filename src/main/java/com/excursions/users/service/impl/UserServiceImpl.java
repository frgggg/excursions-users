package com.excursions.users.service.impl;

import com.excursions.users.exception.ServiceException;
import com.excursions.users.model.User;
import com.excursions.users.repository.UserRepository;
import com.excursions.users.service.ExcursionService;
import com.excursions.users.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.validation.ConstraintViolationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.excursions.users.exception.message.UserServiceExceptionMessages.*;
import static com.excursions.users.log.message.UserServiceLogMessages.*;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private String SERVICE_NAME = "UserServiceImpl";

    private static boolean IS_UP_FLAG = true;
    private static boolean IS_NOT_UP_FLAG = false;

    private UserRepository userRepository;
    private EntityManager entityManager;
    private UserServiceImpl userServiceImpl;
    private ExcursionService excursionService;

    @Lazy
    @Autowired
    protected UserServiceImpl(UserRepository userRepository, EntityManager entityManager, UserServiceImpl userServiceImpl, ExcursionService excursionService) {
        this.userRepository = userRepository;
        this.entityManager = entityManager;
        this.userServiceImpl = userServiceImpl;
        this.excursionService = excursionService;
    }

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

    @Override
    public User update(Long id, String name) {
        User userForUpdate = userServiceImpl.findById(id);
        User updatedUser = saveUtil(id, name, userForUpdate.getCoins());
        log.info(USER_SERVICE_LOG_UPDATE_USER, userForUpdate, updatedUser);
        return updatedUser;
    }

    @Override
    public void deleteById(Long id) {
        User userForDelete = userServiceImpl.findById(id);

        if(excursionService.userTickets(id).size() > 0){
            throw new ServiceException(SERVICE_NAME, String.format(USER_SERVICE_EXCEPTION_USER_HAVE_TICKETS, id));
        }

        userRepository.deleteById(id);
        log.info(USER_SERVICE_LOG_DELETE_USER, userForDelete);
    }

    @Override
    public User coinsDownByExcursion(Long id, Long coins) {
        User updatedUser = updateCoins(id, coins, IS_NOT_UP_FLAG);
        log.info(USER_SERVICE_LOG_DOWN_COINS_BY_EXCURSION, id, coins);
        return updatedUser;
    }

    @Override
    public User coinsDownByUser(Long id, Long coins) {
        User updatedUser = updateCoins(id, coins, IS_NOT_UP_FLAG);
        log.info(USER_SERVICE_LOG_DOWN_COINS_BY_USER, id, coins);
        return updatedUser;
    }

    @Override
    public User coinsUpByExcursion(Long id, Long coins) {
        User updatedUser = updateCoins(id, coins, IS_UP_FLAG);
        log.info(USER_SERVICE_LOG_UP_COINS_BY_EXCURSION, id, coins);
        return updatedUser;
    }

    @Override
    public User coinsUpByUser(Long id, Long coins) {
        User updatedUser = updateCoins(id, coins, IS_UP_FLAG);
        log.info(USER_SERVICE_LOG_UP_COINS_BY_USER, id, coins);
        return updatedUser;
    }

    private User updateCoins(Long id, Long coins, boolean isUp) {
        checkCoins(coins);
        User userForUpdate = userServiceImpl.findById(id);
        Long newCoins = userForUpdate.getCoins();

        if(isUp) {
            newCoins += coins;
        } else {
            newCoins -= coins;
        }
        return saveUtil(id, userForUpdate.getName(), newCoins);
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
        User userForSave = new User(name);
        if(id != null) {
            userForSave.setId(id);
        }
        if(coins != null) {
            userForSave.setCoins(coins);
        }

        User savedUser;
        try {
            savedUser = userRepository.save(userForSave);
            entityManager.flush();
        } catch (ConstraintViolationException e) {
            throw new ServiceException(SERVICE_NAME, e.getConstraintViolations().iterator().next().getMessage());
        }

        return savedUser;
    }
}
