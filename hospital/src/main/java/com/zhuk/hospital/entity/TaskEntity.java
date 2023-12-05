package com.zhuk.hospital.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tasks")
public class TaskEntity {
    @Id
    @UuidGenerator
    private UUID id;
    String patient;
    Long medicationId;
    LocalDateTime dateTimeOfIssue;
    @ManyToOne
    @JoinColumn(name = "department_id")
    private DepartmentEntity department;
}
