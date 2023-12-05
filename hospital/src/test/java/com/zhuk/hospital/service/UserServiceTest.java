package com.zhuk.hospital.service;

import com.zhuk.hospital.dto.CredentialsDto;
import com.zhuk.hospital.dto.UserDto;
import com.zhuk.hospital.entity.DepartmentEntity;
import com.zhuk.hospital.enums.UserRoleEnum;
import com.zhuk.hospital.exception.user.UserAlreadyExistsException;
import com.zhuk.hospital.exception.user.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserServiceTest {
    @Autowired
    @SpyBean
    UserService userService;
    @Autowired
    DataSource dataSource;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15");

    @BeforeEach
    public void clearTables() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement preparedStatementUserDepartment =
                         connection.prepareStatement("DELETE FROM user_department")) {
                preparedStatementUserDepartment.execute();
            }
            try (PreparedStatement preparedStatementUserDepartment =
                         connection.prepareStatement("DELETE FROM departments")) {
                preparedStatementUserDepartment.execute();
            }
            try (PreparedStatement preparedStatementUsers = connection.prepareStatement("DELETE FROM users")) {
                preparedStatementUsers.execute();
            }
        }
    }


    @Test
    @Transactional
    public void findAll_ShouldReturnUserList_WhenDataExists() throws Exception {
        try(Connection connection = dataSource.getConnection()) {
            CredentialsDto credentialsDto = CredentialsDto.builder()
                    .username("USER")
                    .password("PASS".toCharArray())
                    .role(UserRoleEnum.ROLE_USER.name())
                    .build();
            userService.saveUser(credentialsDto);
            List<UserDto> users = userService.findAll();
            String query = "SELECT * FROM users";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                UserDto userDto = UserDto.builder()
                        .id(UUID.fromString(resultSet.getString("id")))
                        .username(resultSet.getString("username"))
                        .password(resultSet.getString("password"))
                        .role(UserRoleEnum.valueOf(resultSet.getString("role")))
                        .build();
                assertTrue(users.contains(userDto));
            }
            assertEquals(1, users.size());
        }
    }

    @Test
    @Transactional
    public void findByUsername_ShouldReturnUser_WhenUserExists() throws Exception {
        try(Connection connection = dataSource.getConnection()) {
            CredentialsDto credentialsDto = CredentialsDto.builder()
                    .username("USER")
                    .password("PASS".toCharArray())
                    .role(UserRoleEnum.ROLE_USER.name())
                    .build();
            userService.saveUser(credentialsDto);
            UserDto user = userService.findUserByUsername("USER");
            String query = "SELECT * FROM users where username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, "USER");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                UserDto userDto = UserDto.builder()
                        .id(UUID.fromString(resultSet.getString("id")))
                        .username(resultSet.getString("username"))
                        .password(resultSet.getString("password"))
                        .role(UserRoleEnum.valueOf(resultSet.getString("role")))
                        .departments(new ArrayList<>())
                        .build();
                assertEquals(user.getId(), userDto.getId());
                assertEquals(user.getUsername(), userDto.getUsername());
                assertEquals(user.getPassword(), userDto.getPassword());
                assertEquals(user.getRole(), userDto.getRole());
            }
        }
    }

    @Test
    public void findByUsername_ShouldThrowNotFoundException_WhenUserDoesntExist() {
        assertThrows(UserNotFoundException.class, () -> userService.findUserByUsername("USER"));
    }

    @Test
    public void saveUser_ShouldSaveUser_WhenDoesntExist() throws Exception {
        try(Connection connection = dataSource.getConnection()) {
            CredentialsDto credentialsDto = CredentialsDto.builder()
                    .username("USER")
                    .password("PASS".toCharArray())
                    .role(UserRoleEnum.ROLE_USER.name())
                    .build();
            userService.saveUser(credentialsDto);
            String query = "SELECT * FROM users where username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, "USER");
            ResultSet resultSet = preparedStatement.executeQuery();
            int counter = 0;
            while (resultSet.next()) {
                counter++;
                assertEquals("USER", resultSet.getString("username"));
            }
            assertEquals(1, counter);
        }
    }

    @Test
    public void saveUser_ShouldThrowAlreadyExistsException_WhenAlreadyExists() throws Exception {
        CredentialsDto credentialsDto = CredentialsDto.builder()
                .username("USER")
                .password("PASS".toCharArray())
                .role(UserRoleEnum.ROLE_USER.name())
                .build();
        userService.saveUser(credentialsDto);
        assertThrows(UserAlreadyExistsException.class, () -> userService.saveUser(credentialsDto));
    }

    @Test
    public void deleteUser_ShouldDeleteUser_WhenUserExists() throws Exception {
        try(Connection connection = dataSource.getConnection()) {
            CredentialsDto credentialsDto = CredentialsDto.builder()
                    .username("USER")
                    .password("PASS".toCharArray())
                    .role(UserRoleEnum.ROLE_USER.name())
                    .build();
            userService.saveUser(credentialsDto);
            String query = "SELECT COUNT(username) FROM users";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSetBeforeDeletion = preparedStatement.executeQuery();
            resultSetBeforeDeletion.next();
            int countBeforeDeletion = resultSetBeforeDeletion.getInt(1);
            userService.deleteUserByUsername("USER");
            ResultSet resultSetAfterDeletion = preparedStatement.executeQuery();
            resultSetAfterDeletion.next();
            int countAfterDeletion = resultSetAfterDeletion.getInt(1);
            assertEquals(1, countBeforeDeletion);
            assertEquals(0, countAfterDeletion);
        }
    }

    @Test
    public void deleteUser_ShouldDoNothing_WhenUserDoesntExist() throws Exception {
        try(Connection connection = dataSource.getConnection()) {
            String query = "SELECT COUNT(username) FROM users";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            userService.deleteUserByUsername("USER");
            ResultSet resultSetAfterDeletion = preparedStatement.executeQuery();
            resultSetAfterDeletion.next();
            int countAfterDeletion = resultSetAfterDeletion.getInt(1);
            assertEquals(0, countAfterDeletion);
        }
    }

    @Test
    public void updateUser_ShouldSaveUser_WhenUserDoesntExist() throws Exception {
        try(Connection connection = dataSource.getConnection()) {
            CredentialsDto credentialsDto = CredentialsDto.builder()
                    .username("USER")
                    .password("PASS".toCharArray())
                    .role(UserRoleEnum.ROLE_USER.name())
                    .build();
            userService.updateCredentials("USER", credentialsDto);
            String query = "SELECT * FROM users where username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, "USER");
            ResultSet resultSet = preparedStatement.executeQuery();
            int counter = 0;
            while (resultSet.next()) {
                counter++;
                assertEquals("USER", resultSet.getString("username"));
            }
            assertEquals(1, counter);
        }
    }

    @Test
    public void updateUser_ShouldUpdateUser_WhenUserAlreadyExists() throws Exception {
        try(Connection connection = dataSource.getConnection()) {
            CredentialsDto credentialsDto = CredentialsDto.builder()
                    .username("USER")
                    .password("PASS".toCharArray())
                    .role(UserRoleEnum.ROLE_USER.name())
                    .build();
            CredentialsDto newCredentialsDto = CredentialsDto.builder()
                    .username("NEW")
                    .password("PWD".toCharArray())
                    .role(UserRoleEnum.ROLE_USER.name())
                    .build();
            userService.saveUser(credentialsDto);
            userService.updateCredentials("USER", newCredentialsDto);
            String query = "SELECT * FROM users where username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, "NEW");
            ResultSet resultSet = preparedStatement.executeQuery();
            int counter = 0;
            while (resultSet.next()) {
                counter++;
                assertEquals("NEW", resultSet.getString("username"));
                assertTrue(new BCryptPasswordEncoder().matches("PWD", resultSet.getString("password")));
            }
            assertEquals(1, counter);
        }
    }

    @Test
    public void addDepartmentByUsername_ShouldAddDepartment_WhenUserExists() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            CredentialsDto credentialsDto = CredentialsDto.builder()
                    .username("USER")
                    .password("PASS".toCharArray())
                    .role(UserRoleEnum.ROLE_USER.name())
                    .build();
            userService.saveUser(credentialsDto);
            userService.addDepartmentByUsername("USER",
                    DepartmentEntity.builder()
                            .name("TEST")
                            .description("TEST DESCRIPTION")
                            .build());
            String query = "SELECT d.id, d.name, d.description FROM users u " +
                    "JOIN user_department ud ON u.id = ud.user_id " +
                    "JOIN departments d ON ud.department_id = d.id " +
                    "WHERE u.username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, "USER");
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    Set<DepartmentEntity> expectedDepartments = new HashSet<>(Set.of(
                            DepartmentEntity.builder()
                                    .name("TEST")
                                    .description("TEST DESCRIPTION")
                                    .build()));
                    Set<DepartmentEntity> actualDepartments = new HashSet<>();
                    while (resultSet.next()) {
                        DepartmentEntity department = DepartmentEntity.builder()
                                .name(resultSet.getString("name"))
                                .description(resultSet.getString("description"))
                                .build();

                        actualDepartments.add(department);
                    }
                    assertEquals(expectedDepartments, actualDepartments);
                }
            }
        }
    }
    @Test
    public void deleteDepartmentByUsername_ShouldAddDepartment_WhenUserExists() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            CredentialsDto credentialsDto = CredentialsDto.builder()
                    .username("USER")
                    .password("PASS".toCharArray())
                    .role(UserRoleEnum.ROLE_USER.name())
                    .build();
            userService.saveUser(credentialsDto);
            DepartmentEntity entity = DepartmentEntity.builder()
                    .name("TEST")
                    .description("TEST DESCRIPTION")
                    .build();
            userService.addDepartmentByUsername("USER", entity);
            userService.deleteDepartmentByUsername("USER", entity);
            String query = "SELECT d.id, d.name, d.description FROM users u " +
                    "JOIN user_department ud ON u.id = ud.user_id " +
                    "JOIN departments d ON ud.department_id = d.id " +
                    "WHERE u.username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, "USER");
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    int counter = 0;
                    while (resultSet.next()) {
                        counter++;
                    }
                    assertEquals(0, counter);
                }
            }
        }
    }
}
