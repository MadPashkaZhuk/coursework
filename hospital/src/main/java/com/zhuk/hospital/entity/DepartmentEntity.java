package com.zhuk.hospital.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(exclude = {"users", "tasks"})
@Table(name = "departments")
public class DepartmentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    @ManyToMany(mappedBy = "departments")
    private List<UserEntity> users;
    @OneToMany(mappedBy = "department", fetch = FetchType.EAGER)
    private List<TaskEntity> tasks;
    @PreRemove
    private void removeDepartmentFromUsers() {
        for (UserEntity user : users) {
            user.getDepartments().remove(this);
        }
    }
}
