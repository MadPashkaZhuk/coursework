package com.zhuk.coursework.repository;

import com.zhuk.coursework.entity.MedicationEntity;
import com.zhuk.coursework.enums.MedicationTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MedicationRepository extends JpaRepository<MedicationEntity, Long> {
    @Modifying
    @Query("UPDATE MedicationEntity m SET m.manufacturer = :manufacturer, m.type = :type, " +
            "m.requirePrescription = :requirePrescription, m.additionalInfo = :additionalInfo " +
            "WHERE m.name = :name AND m.weight = :weight")
    void updateByNameAndWeight(String name, String manufacturer, int weight, boolean requirePrescription, String additionalInfo,
                    MedicationTypeEnum type);

    Optional<MedicationEntity> getMedicationEntityByNameAndWeight(String name, int weight);
}
