package com.zhuk.hospital.repository;

import com.zhuk.hospital.entity.DepartmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<DepartmentEntity, Long> {
    Optional<DepartmentEntity> findDepartmentEntityByName(String name);
    @Modifying
    @Query("UPDATE DepartmentEntity d SET d.name = :name, d.description = :description WHERE d.id = :id")
    void updateById(@Param("name")String name, @Param("description") String description,
                          @Param("id") Long id);
}
