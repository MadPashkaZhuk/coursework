package com.zhuk.hospital.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.zhuk.hospital.enums.UserRoleEnum;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserDto {
    UUID id;
    String username;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    String password;
    UserRoleEnum role;
}
