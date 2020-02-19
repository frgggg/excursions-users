package com.excursions.users.controller;

import com.excursions.users.dto.UserDto;
import com.excursions.users.model.User;
import com.excursions.users.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.excursions.users.log.message.UserControllerLogMessages.*;
import static com.excursions.users.log.message.UserServiceLogMessages.USER_SERVICE_LOG_DOWN_COINS_BY_EXCURSION;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserRest {

    private UserService userService;
    private ModelMapper modelMapper;

    @Autowired
    protected UserRest(UserService userService, ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public List<UserDto> findAll() {
        List<UserDto> allPlaces =  userService.findAll()
                .stream()
                .map(book -> modelMapper.map(book, UserDto.class))
                .collect(Collectors.toList());
        log.info(USER_CONTROLLER_LOG_GET_ALL_USERS);
        return allPlaces;
    }

    @GetMapping(value = "/{id}")
    public UserDto findById(@PathVariable("id") Long id) {
        User user = userService.findById(id);
        UserDto fundedUsers =  modelMapper.map(user, UserDto.class);
        log.info(USER_CONTROLLER_LOG_GET_USER, fundedUsers);
        return fundedUsers;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Validated @RequestBody UserDto userDto) {
        User user = userService.create(userDto.getName());
        UserDto createUser = modelMapper.map(user, UserDto.class);
        log.info(USER_CONTROLLER_LOG_NEW_USER, createUser);
        return createUser;
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto updateById(@Validated @RequestBody UserDto userDto, @PathVariable("id") Long id) {
        User user = userService.update(id, userDto.getName());
        UserDto updatedUser = modelMapper.map(user, UserDto.class);
        log.info(USER_CONTROLLER_LOG_UPDATE_USER, id, updatedUser);
        return updatedUser;
    }

    @DeleteMapping(value = "/{id}")
    public void deleteById(@PathVariable("id") Long id) {
        userService.deleteById(id);
        log.info(USER_CONTROLLER_LOG_DELETE_USER, id);
    }

    @PutMapping(value = "/{id}/coins-down-by-excursion")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void coinsDownByExcursion(@RequestParam(name = "coins", required = true) Long coins, @PathVariable("id") Long id) {
        userService.coinsDownByExcursion(id, coins);
        log.info(USER_CONTROLLER_LOG_DOWN_COINS_BY_EXCURSION, id, coins);
    }

    @PutMapping(value = "/{id}/coins-up-by-excursion")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void coinsUpByExcursion(@RequestParam(name = "coins", required = true) Long coins, @PathVariable("id") Long id) {
        userService.coinsUpByExcursion(id, coins);
        log.info(USER_CONTROLLER_LOG_UP_COINS_BY_EXCURSION, id, coins);
    }

    @PutMapping(value = "/{id}/coins-down-by-user")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void coinsDownByUser(@RequestParam(name = "coins", required = true) Long coins, @PathVariable("id") Long id) {
        userService.coinsDownByUser(id, coins);
        log.info(USER_CONTROLLER_LOG_DOWN_COINS_BY_USER, id, coins);
    }

    @PutMapping(value = "/{id}/coins-up-by-user")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void coinsUpByUser(@RequestParam(name = "coins", required = true) Long coins, @PathVariable("id") Long id) {
        userService.coinsUpByUser(id, coins);
        log.info(USER_CONTROLLER_LOG_UP_COINS_BY_USER, id, coins);
    }
}
