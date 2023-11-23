package com.zhuk.coursework.service;

import com.zhuk.coursework.dto.CredentialsDto;
import com.zhuk.coursework.dto.UserDto;
import com.zhuk.coursework.enums.UserRoleEnum;
import com.zhuk.coursework.exception.user.UserAlreadyExistsException;
import com.zhuk.coursework.exception.user.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.UUID;

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
        try(Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM users");
            preparedStatement.execute();
        }
    }

    @Test
    public void findAll_ShouldReturnUserList_WhenDataExists() throws Exception {
        try(Connection connection = dataSource.getConnection()) {
            CredentialsDto credentialsDto = CredentialsDto.builder()
                    .username("USER")
                    .password("PASS".toCharArray())
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
    public void findByUsername_ShouldReturnUser_WhenUserExists() throws Exception {
        try(Connection connection = dataSource.getConnection()) {
            CredentialsDto credentialsDto = CredentialsDto.builder()
                    .username("USER")
                    .password("PASS".toCharArray())
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
                        .build();
                assertEquals(user, userDto);
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
                    .build();
            userService.updateUser("USER", credentialsDto);
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
    public void updateMedication_ShouldUpdateMedication_WhenMedicationAlreadyExists() throws Exception {
        try(Connection connection = dataSource.getConnection()) {
            CredentialsDto credentialsDto = CredentialsDto.builder()
                    .username("USER")
                    .password("PASS".toCharArray())
                    .build();
            CredentialsDto newCredentialsDto = CredentialsDto.builder()
                    .username("NEW")
                    .password("PWD".toCharArray())
                    .build();
            userService.saveUser(credentialsDto);
            userService.updateUser("USER", newCredentialsDto);
            String query = "SELECT * FROM users where username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, "NEW");
            ResultSet resultSet = preparedStatement.executeQuery();
            int counter = 0;
            while (resultSet.next()) {
                counter++;
                assertEquals("NEW", resultSet.getString("username"));
                System.out.println(resultSet.getString("password"));
                assertTrue(new BCryptPasswordEncoder().matches("PWD", resultSet.getString("password")));
            }
            assertEquals(1, counter);
        }
    }

}
