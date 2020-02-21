package com.excursions.users.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.time.LocalDateTime;

import static com.excursions.users.validation.message.ValidationMessagesComponents.*;

@Data
@Entity
@Table(name = "users")//, schema = "users_schema_v3")
public class User {
    public static final String USER_NAME_FIELD_NAME = "name";
    public static final int USER_NAME_LEN_MIN = 1;
    public static final int USER_NAME_LEN_MAX = 90;
    public static final String USER_NAME_VALIDATION_MESSAGE =
            USER_NAME_FIELD_NAME + STRING_FIELD_NOTNULL_MIN_MAX + USER_NAME_LEN_MIN + STRING_FIELD_NOTNULL_MIN_MAX_DIVIDE + USER_NAME_LEN_MAX;

    public static final String USER_COINS_FIELD_NAME = "coins";
    public static final int USER_COINS_MIN = 0;
    public static final String  USER_COINS_VALIDATION_MESSAGE = USER_COINS_FIELD_NAME + LONG_FIELD_NOTNULL_NOT_NEGATIVE;

    public static final String USER_COINS_LAST_UPDATE_FIELD_NAME = "coinsLastUpdate";
    public static final String  USER_COINS_LAST_UPDATE_VALIDATION_MESSAGE = USER_COINS_LAST_UPDATE_FIELD_NAME + LOCAL_DATA_TIME_FIELD_NOTNULL;

    @Id
    /*@SequenceGenerator(name = "users_sequence_gen", sequenceName = "users_sequence",
            initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="users_sequence_gen")*/
    //@GeneratedValue(strategy=GenerationType.IDENTITY)
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @Column(name = "name", length = USER_NAME_LEN_MAX, nullable = false)
    @NotNull(message = USER_NAME_VALIDATION_MESSAGE)
    @Size(min = USER_NAME_LEN_MIN, max = USER_NAME_LEN_MAX, message = USER_NAME_VALIDATION_MESSAGE)
    private String name;

    @Column(name = "coins", nullable = false)
    @NotNull(message = USER_COINS_VALIDATION_MESSAGE)
    @Min(value = USER_COINS_MIN, message = USER_COINS_VALIDATION_MESSAGE)
    private Long coins;

    @Column(name = "coins_last_update", nullable = false)
    @NotNull(message = USER_COINS_LAST_UPDATE_VALIDATION_MESSAGE)
    private LocalDateTime coinsLastUpdate;

    protected User() {}

    public User(String name) {
        this.name = name;
        coins = 0l;
        coinsLastUpdate = LocalDateTime.now();
    }
}
