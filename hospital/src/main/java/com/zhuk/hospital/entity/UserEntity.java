package com.zhuk.hospital.entity;

import com.zhuk.hospital.enums.UserRoleEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @UuidGenerator
    private UUID id;
    private String username;
    private String password;
    @Enumerated(EnumType.STRING)
    private UserRoleEnum role;
}
