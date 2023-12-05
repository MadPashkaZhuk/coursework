package com.zhuk.coursework.repository;

import com.zhuk.coursework.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
    void deleteByUsername(String username);
    @Modifying
    @Query("UPDATE UserEntity u SET u.username = :username, u.password = :password WHERE u.username = :oldUsername")
    void updateByUsername(@Param("oldUsername")String oldUsername, @Param("username") String username,
                          @Param("password") String password);
}
