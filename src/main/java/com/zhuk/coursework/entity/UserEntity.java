package com.zhuk.coursework.entity;

import com.zhuk.coursework.enums.UserRoleEnum;
import jakarta.persistence.*;
import lombok.*;
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
