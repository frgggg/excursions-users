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

    @Override
    public User create(String name) {
        User userForSave = new User(name);
        User savedUser = saveUtil(userForSave);
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
        User userForUpdate = self.findById(id);
        userForUpdate.setName(name);
        User updatedUser = saveUtil(userForUpdate);
        log.info(USER_SERVICE_LOG_UPDATE_USER, userForUpdate, updatedUser);
        return updatedUser;
    }

    @Override
    public void deleteById(Long id) {
        User userForDelete = self.findById(id);

        if(excursionService.userTickets(id).size() > 0){
            throw new ServiceException(SERVICE_NAME, String.format(USER_SERVICE_EXCEPTION_USER_HAVE_TICKETS, id));
        }

        userRepository.deleteById(id);
        log.info(USER_SERVICE_LOG_DELETE_USER, userForDelete);
    }

    @Override
    public void coinsDownByExcursion(Long id, Long coins) {
        checkCoins(coins);
        updateCoins(id, coins, IS_NOT_UP_FLAG);
        log.info(USER_SERVICE_LOG_DOWN_COINS_BY_EXCURSION, id, coins);
    }

    @Override
    public void coinsDownByUser(Long id, Long coins) {
        checkCoins(coins);
        updateCoins(id, coins, IS_NOT_UP_FLAG);
        log.info(USER_SERVICE_LOG_DOWN_COINS_BY_USER, id, coins);
    }

    @Override
    public void coinsUpByExcursion(Long id, Long coins) {
        checkCoins(coins);
        updateCoins(id, coins, IS_UP_FLAG);
        log.info(USER_SERVICE_LOG_UP_COINS_BY_EXCURSION, id, coins);
    }

    @Override
    public void coinsUpByUser(Long id, Long coins) {
        checkCoins(coins);
        updateCoins(id, coins, IS_UP_FLAG);
        log.info(USER_SERVICE_LOG_UP_COINS_BY_USER, id, coins);
    }

    private void updateCoins(Long id, Long coins, boolean isUp) {
        User userForUpdate = self.findById(id);
        if(isUp) {
            userForUpdate.setCoins(userForUpdate.getCoins() + coins);
        } else {
            userForUpdate.setCoins(userForUpdate.getCoins() - coins);
        }
        User updatedUser = saveUtil(userForUpdate);
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

    private User saveUtil(User userForSave) {
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
