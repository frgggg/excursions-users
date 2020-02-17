package com.excursions.users.log.message;

import static com.excursions.users.log.message.UserServiceLogMessages.*;

public class UserControllerLogMessages {

    public static final String USER_CONTROLLER_LOG_NEW_USER = USER_SERVICE_LOG_NEW_USER;
    public static final String USER_CONTROLLER_LOG_UPDATE_USER = "User with id {} is updated. New = {}";
    public static final String USER_CONTROLLER_LOG_DELETE_USER = "Delete user with id = {}";
    public static final String USER_CONTROLLER_LOG_GET_USER = USER_SERVICE_LOG_GET_USER;
    public static final String USER_CONTROLLER_LOG_GET_ALL_USERS = USER_SERVICE_LOG_GET_ALL_USERS;

    public static final String USER_CONTROLLER_LOG_UP_COINS_BY_USER = USER_SERVICE_LOG_UP_COINS_BY_USER;
    public static final String USER_CONTROLLER_LOG_DOWN_COINS_BY_USER = USER_SERVICE_LOG_DOWN_COINS_BY_USER;

    public static final String USER_CONTROLLER_LOG_UP_COINS_BY_EXCURSION = USER_SERVICE_LOG_UP_COINS_BY_EXCURSION;
    public static final String USER_CONTROLLER_LOG_DOWN_COINS_BY_EXCURSION = USER_SERVICE_LOG_DOWN_COINS_BY_EXCURSION;
}
