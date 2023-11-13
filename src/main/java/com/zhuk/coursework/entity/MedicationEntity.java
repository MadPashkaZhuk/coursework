package com.zhuk.coursework.entity;

import com.zhuk.coursework.enums.MedicationTypeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "medication")
public class MedicationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String name;
    String manufacturer;
    @Enumerated(EnumType.STRING)
    MedicationTypeEnum type;
    int weight;
    boolean requirePrescription;
    String additionalInfo;
}
