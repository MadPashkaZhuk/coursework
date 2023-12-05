package com.zhuk.hospital.entity;

import com.zhuk.hospital.enums.UserRoleEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.List;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(exclude = {"departments"})
@Table(name = "users")
public class UserEntity {
    @Id
    @UuidGenerator
    private UUID id;
    private String username;
    private String password;
    @Enumerated(EnumType.STRING)
    private UserRoleEnum role;
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_department",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "department_id"))
    private List<DepartmentEntity> departments;
}
