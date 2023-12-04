package com.zhuk.hospital.service;

import com.zhuk.hospital.dto.*;
import com.zhuk.hospital.exception.department.DepartmentAlreadyExistsException;
import com.zhuk.hospital.exception.department.DepartmentNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DepartmentServiceTest {
    @Autowired
    @SpyBean
    DepartmentService departmentService;
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
            try (PreparedStatement preparedStatementDepartment =
                         connection.prepareStatement("DELETE FROM departments")) {
                preparedStatementDepartment.execute();
            }
            try (PreparedStatement preparedStatementUsers = connection.prepareStatement("DELETE FROM users")) {
                preparedStatementUsers.execute();
            }
        }
    }

    @Test
    public void findAll_ShouldReturnDepartmentList_WhenDataExists() throws Exception {
        try(Connection connection = dataSource.getConnection()) {
            NewDepartmentDto newDepartmentDto = NewDepartmentDto.builder()
                    .name("first")
                    .description("first")
                    .build();
            departmentService.save(newDepartmentDto);
            List<DepartmentDto> departments = departmentService.findAll();
            String query = "SELECT * FROM departments";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                DepartmentDto departmentDto = DepartmentDto.builder()
                        .id(id)
                        .name(resultSet.getString("name"))
                        .description(resultSet.getString("description"))
                        .tasks(retrieveTasksForDepartment(id))
                        .build();
                assertTrue(departments.contains(departmentDto));
            }
            assertEquals(1, departments.size());
        }
    }

    @Test
    public void findById_ShouldReturnDepartment_WhenDepartmentExists() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            NewDepartmentDto newDepartmentDto = NewDepartmentDto.builder()
                    .name("first")
                    .description("first")
                    .build();
            departmentService.save(newDepartmentDto);
            DepartmentDto actual = departmentService.findById(getIdForName("first"));
            String query = "SELECT * FROM departments";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                DepartmentDto departmentDto = DepartmentDto.builder()
                        .id(id)
                        .name(resultSet.getString("name"))
                        .description(resultSet.getString("description"))
                        .tasks(retrieveTasksForDepartment(id))
                        .build();
                assertEquals(actual.getId(), departmentDto.getId());
                assertEquals(actual.getName(), departmentDto.getName());
                assertEquals(actual.getDescription(), departmentDto.getDescription());
                assertEquals(actual.getTasks(), departmentDto.getTasks());
            }
        }
    }

    @Test
    public void findById_ShouldThrowNotFoundException_WhenDepartmentDoesntExist() {
        assertThrows(DepartmentNotFoundException.class, () -> departmentService.findById(999L));
    }

    @Test
    public void saveDepartment_ShouldSaveDepartment_WhenDoesntExist() throws Exception {
        try(Connection connection = dataSource.getConnection()) {
            NewDepartmentDto newDepartmentDto = NewDepartmentDto.builder()
                    .name("first")
                    .description("first")
                    .build();
            departmentService.save(newDepartmentDto);
            String query = "SELECT * FROM departments where name = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, "first");
            ResultSet resultSet = preparedStatement.executeQuery();
            int counter = 0;
            while (resultSet.next()) {
                counter++;
                assertEquals("first", resultSet.getString("name"));
            }
            assertEquals(1, counter);
        }
    }

    @Test
    public void saveDepartment_ShouldThrowAlreadyExistsException_WhenAlreadyExists() throws Exception {
        NewDepartmentDto newDepartmentDto = NewDepartmentDto.builder()
                .name("first")
                .description("first")
                .build();
        departmentService.save(newDepartmentDto);
        assertThrows(DepartmentAlreadyExistsException.class, () -> departmentService.save(newDepartmentDto));
    }

    @Test
    public void deleteDepartment_ShouldDeleteDepartment_WhenDepartmentExists() throws Exception {
        try(Connection connection = dataSource.getConnection()) {
            NewDepartmentDto newDepartmentDto = NewDepartmentDto.builder()
                    .name("first")
                    .description("first")
                    .build();
            departmentService.save(newDepartmentDto);
            String query = "SELECT COUNT(name) FROM departments";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSetBeforeDeletion = preparedStatement.executeQuery();
            resultSetBeforeDeletion.next();
            int countBeforeDeletion = resultSetBeforeDeletion.getInt(1);
            departmentService.deleteDepartment(getIdForName("first"));
            ResultSet resultSetAfterDeletion = preparedStatement.executeQuery();
            resultSetAfterDeletion.next();
            int countAfterDeletion = resultSetAfterDeletion.getInt(1);
            assertEquals(1, countBeforeDeletion);
            assertEquals(0, countAfterDeletion);
        }
    }

    @Test
    public void deleteDepartment_ShouldDoNothing_WhenDepartmentDoesntExist() throws Exception {
        try(Connection connection = dataSource.getConnection()) {
            String query = "SELECT COUNT(name) FROM departments";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            departmentService.deleteDepartment(999L);
            ResultSet resultSetAfterDeletion = preparedStatement.executeQuery();
            resultSetAfterDeletion.next();
            int countAfterDeletion = resultSetAfterDeletion.getInt(1);
            assertEquals(0, countAfterDeletion);
        }
    }

    @Test
    public void updateDepartment_ShouldThrowBadRequest_WhenDepartmentDoesntExist() throws Exception {
        NewDepartmentDto newDepartmentDto = NewDepartmentDto.builder()
                .name("first")
                .description("first")
                .build();
        assertThrows(DepartmentNotFoundException.class, () -> departmentService.updateDepartment(999L, newDepartmentDto));
    }

    @Test
    public void updateDepartment_ShouldThrowBadRequest_WhenAddingNewDepartmentAndAnotherDepartmentWithSameNameExists() throws Exception {
        NewDepartmentDto firstDepartmentDto = NewDepartmentDto.builder()
                .name("first")
                .description("first")
                .build();
        departmentService.save(firstDepartmentDto);
        NewDepartmentDto secondDepartmentDto = NewDepartmentDto.builder()
                .name("second")
                .description("second")
                .build();
        departmentService.save(secondDepartmentDto);

        assertThrows(DepartmentAlreadyExistsException.class,
                () -> departmentService.updateDepartment(getIdForName("second"), firstDepartmentDto));
    }

    @Test
    public void updateDepartment_ShouldUpdateDepartment_WhenDepartmentAlreadyExists() throws Exception {
        try(Connection connection = dataSource.getConnection()) {
            NewDepartmentDto firstDepartmentDto = NewDepartmentDto.builder()
                    .name("first")
                    .description("first")
                    .build();
            departmentService.save(firstDepartmentDto);
            NewDepartmentDto secondDepartmentDto = NewDepartmentDto.builder()
                    .name("second")
                    .description("second")
                    .build();
            departmentService.updateDepartment(getIdForName("first"), secondDepartmentDto);
            String query = "SELECT * FROM departments where name = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, "second");
            ResultSet resultSet = preparedStatement.executeQuery();
            int counter = 0;
            while (resultSet.next()) {
                counter++;
                assertEquals("second", resultSet.getString("description"));
            }
            assertEquals(1, counter);
        }
    }

    @Test
    public void addUserToDepartment_ShouldAddUser_WhenHappyPath() throws Exception {
        try(Connection connection = dataSource.getConnection()) {
            NewDepartmentDto firstDepartmentDto = NewDepartmentDto.builder()
                    .name("first")
                    .description("first")
                    .build();
            departmentService.save(firstDepartmentDto);
            CredentialsDto credentialsDto = CredentialsDto.builder()
                    .username("USER")
                    .password("PASS".toCharArray())
                    .build();
            userService.saveUser(credentialsDto);
            departmentService.addUserToDepartment(UserDepartmentAssociationDTO.builder()
                    .departmentId(getIdForName("first"))
                    .username("USER")
                    .build());
            String query = "SELECT * FROM user_department where department_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, getIdForName("first"));
            ResultSet resultSet = preparedStatement.executeQuery();
            int counter = 0;
            while (resultSet.next()) {
                counter++;
                assertEquals(userService.findUserByUsername("USER").getId(),
                        UUID.fromString(resultSet.getString("user_id")));
            }
            assertEquals(1, counter);
        }
    }

    @Test
    public void deleteUserFromDepartment_ShouldDeleteUser_WhenHappyPath() throws Exception {
        try(Connection connection = dataSource.getConnection()) {
            NewDepartmentDto firstDepartmentDto = NewDepartmentDto.builder()
                    .name("first")
                    .description("first")
                    .build();
            departmentService.save(firstDepartmentDto);
            CredentialsDto credentialsDto = CredentialsDto.builder()
                    .username("USER")
                    .password("PASS".toCharArray())
                    .build();
            userService.saveUser(credentialsDto);
            departmentService.addUserToDepartment(UserDepartmentAssociationDTO.builder()
                    .departmentId(getIdForName("first"))
                    .username("USER")
                    .build());
            departmentService.deleteUserFromDepartment(UserDepartmentAssociationDTO.builder()
                    .departmentId(getIdForName("first"))
                    .username("USER")
                    .build());
            String query = "SELECT * FROM user_department where department_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, getIdForName("first"));
            ResultSet resultSet = preparedStatement.executeQuery();
            int counter = 0;
            while (resultSet.next()) {
                counter++;
            }
            assertEquals(0, counter);
        }
    }
    private Long getIdForName(String name) throws Exception {
        Long actualId = null;
        try(Connection connection = dataSource.getConnection()) {
            String idQuery = "SELECT * FROM departments where name = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(idQuery)) {
                preparedStatement.setString(1, name);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        actualId = resultSet.getLong("id");
                    }
                }
            }
        }
        return actualId;
    }

    private List<TaskDto> retrieveTasksForDepartment(Long departmentId) throws Exception {
        List<TaskDto> tasks = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            String query = "SELECT * FROM tasks WHERE department_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setLong(1, departmentId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        TaskDto dto = TaskDto.builder()
                                .id(UUID.fromString(resultSet.getString("id")))
                                .medicationId(resultSet.getLong("medication_id"))
                                .patient(resultSet.getString("patient"))
                                .dateTimeOfIssue(resultSet
                                        .getTimestamp("date_time_of_issue").toLocalDateTime())
                                .build();
                        tasks.add(dto);
                    }
                }
            }
            return tasks;
        }
    }
}
