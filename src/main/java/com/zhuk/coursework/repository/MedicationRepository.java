package com.zhuk.coursework.repository;

import com.zhuk.coursework.entity.MedicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicationRepository extends JpaRepository<MedicationEntity, Long> {
    @Modifying
    @Query("UPDATE MedicationEntity m SET m.name = :name, m.manufacturer = :manufacturer, m.type = :type, " +
            "m.weight = :weight, m.requirePrescription = :requirePrescription, m.additionalInfo = :additionalInfo " +
            "WHERE m.id = :id")
    void updateById(String name, String manufacturer, int weight, boolean requirePrescription, String additionalInfo,
                    String type, Long id);
}
