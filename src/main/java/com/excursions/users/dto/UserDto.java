package com.excursions.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static com.excursions.users.model.User.*;

@Data
@Builder
@AllArgsConstructor
public class UserDto {

    private Long id;

    @NotNull(message = USER_NAME_VALIDATION_MESSAGE)
    @Size(min = USER_NAME_LEN_MIN, max = USER_NAME_LEN_MAX, message = USER_NAME_VALIDATION_MESSAGE)
    private String name;

    private Long coins;

    public UserDto() {}
}
