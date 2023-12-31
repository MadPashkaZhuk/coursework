package com.zhuk.hospital.service;

import com.zhuk.hospital.client.MedicationRestClient;
import com.zhuk.hospital.dto.CredentialsDto;
import com.zhuk.hospital.dto.NewDepartmentDto;
import com.zhuk.hospital.dto.NewTaskDto;
import com.zhuk.hospital.dto.TaskDto;
import com.zhuk.hospital.entity.UserEntity;
import com.zhuk.hospital.enums.UserRoleEnum;
import com.zhuk.hospital.exception.task.TaskNotFoundException;
import com.zhuk.hospital.exception.task.TaskOutdatedException;
import com.zhuk.hospital.security.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TaskServiceTest.WithCustomUserDetails
public class TaskServiceTest {
    @Autowired
    @SpyBean
    TaskService taskService;
    @Autowired
    @SpyBean
    DepartmentService departmentService;
    @Autowired
    @SpyBean
    UserService userService;
    @MockBean
    MedicationRestClient medicationRestClient;
    @Autowired
    DataSource dataSource;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15");

    @BeforeEach
    public void clearTables() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement preparedStatementTasks = connection.prepareStatement("DELETE FROM tasks")) {
                preparedStatementTasks.execute();
            }
            try (PreparedStatement preparedStatementUsers = connection.prepareStatement("DELETE FROM users")) {
                preparedStatementUsers.execute();
            }
            try (PreparedStatement preparedStatementDepartments = connection.prepareStatement("DELETE FROM departments")) {
                preparedStatementDepartments.execute();
            }
        }
    }

    @BeforeEach
    public void setup() {
        userService.saveUser(CredentialsDto.builder()
                .username("ADMIN")
                .password("1234".toCharArray())
                .role("ROLE_ADMIN")
                .build());
        departmentService.save(NewDepartmentDto.builder()
                .name("DEP")
                .description("DESC")
                .build());
    }
    @Test
    public void findAll_ShouldReturnTaskList_WhenDataExists() throws Exception {
        try(Connection connection = dataSource.getConnection()) {
            NewTaskDto newTaskDto = NewTaskDto.builder()
                    .medicationId(1L)
                    .patient("PATIENT")
                    .amountOfDays(1)
                    .startDay(LocalDate.now())
                    .timeOfIssuing(List.of(LocalTime.NOON))
                    .departmentId(getDepartmentId())
                    .build();
            taskService.save(newTaskDto);
            List<TaskDto> tasks = taskService.findAll();
            String query = "SELECT * FROM tasks";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                TaskDto dto = TaskDto.builder()
                        .id(UUID.fromString(resultSet.getString("id")))
                        .dateTimeOfIssue(resultSet.getTimestamp("date_time_of_issue").toLocalDateTime())
                        .patient(resultSet.getString("patient"))
                        .medicationId(resultSet.getLong("medication_id"))
                        .build();
                assertTrue(tasks.contains(dto));
            }
            assertEquals(1, tasks.size());
        }
    }

    @Test
    public void findById_ShouldReturnDto_WhenDataExists() throws Exception {
        try(Connection connection = dataSource.getConnection()) {
            NewTaskDto newTaskDto = NewTaskDto.builder()
                    .medicationId(1L)
                    .patient("PATIENT")
                    .amountOfDays(1)
                    .startDay(LocalDate.now())
                    .timeOfIssuing(List.of(LocalTime.NOON))
                    .departmentId(getDepartmentId())
                    .build();
            taskService.save(newTaskDto);
            TaskDto actual = taskService.findById(getTaskId(newTaskDto));
            String query = "SELECT * FROM tasks";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                TaskDto dto = TaskDto.builder()
                        .id(UUID.fromString(resultSet.getString("id")))
                        .dateTimeOfIssue(resultSet.getTimestamp("date_time_of_issue").toLocalDateTime())
                        .patient(resultSet.getString("patient"))
                        .medicationId(resultSet.getLong("medication_id"))
                        .build();
                assertEquals(actual.getId(), dto.getId());
                assertEquals(actual.getMedicationId(), dto.getMedicationId());
                assertEquals(actual.getDateTimeOfIssue(), dto.getDateTimeOfIssue());
                assertEquals(actual.getPatient(), dto.getPatient());
            }
        }
    }

    @Test
    public void findById_ShouldThrowNotFound_WhenDoesntExist() throws Exception {
        assertThrows(TaskNotFoundException.class, () -> taskService.findById(UUID.randomUUID()));
    }

    @Test
    public void save_ShouldThrowBadRequest_WhenTaskOutDated() throws Exception {
        try(Connection connection = dataSource.getConnection()) {
            NewTaskDto newTaskDto = NewTaskDto.builder()
                    .medicationId(1L)
                    .patient("PATIENT")
                    .amountOfDays(1)
                    .startDay(LocalDate.MIN)
                    .timeOfIssuing(List.of(LocalTime.NOON))
                    .departmentId(getDepartmentId())
                    .build();
            assertThrows(TaskOutdatedException.class, () -> taskService.save(newTaskDto));
        }
    }

    @Test
    public void delete_ShouldDeleteTask_WhenHappyPath() throws Exception {
        try(Connection connection = dataSource.getConnection()) {
            NewTaskDto newTaskDto = NewTaskDto.builder()
                    .medicationId(1L)
                    .patient("PATIENT")
                    .amountOfDays(1)
                    .startDay(LocalDate.now())
                    .timeOfIssuing(List.of(LocalTime.NOON))
                    .departmentId(getDepartmentId())
                    .build();
            taskService.save(newTaskDto);
            String query = "SELECT COUNT(id) FROM tasks";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSetBeforeDeletion = preparedStatement.executeQuery();
            resultSetBeforeDeletion.next();
            int countBeforeDeletion = resultSetBeforeDeletion.getInt(1);
            taskService.delete(getTaskId(newTaskDto));
            ResultSet resultSetAfterDeletion = preparedStatement.executeQuery();
            resultSetAfterDeletion.next();
            int countAfterDeletion = resultSetAfterDeletion.getInt(1);
            assertEquals(1, countBeforeDeletion);
            assertEquals(0, countAfterDeletion);
        }
    }

    private Long getDepartmentId() throws Exception {
        Long id = null;
        try(Connection connection = dataSource.getConnection()) {
            String query = "SELECT * FROM departments where name = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, "DEP");
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                id = resultSet.getLong("id");
            }
        }
        return id;
    }

    private UUID getTaskId(NewTaskDto dto) throws Exception {
        UUID id = null;
        try(Connection connection = dataSource.getConnection()) {
            String query = "SELECT * FROM tasks where patient = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, dto.getPatient());
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                id = UUID.fromString(resultSet.getString("id"));
            }
        }
        return id;
    }
    static class CustomUserDetailsSecurityContextFactory implements WithSecurityContextFactory<WithCustomUserDetails> {

        @Override
        public SecurityContext createSecurityContext(WithCustomUserDetails withCustomUserDetails) {
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            UserDetails principal = new CustomUserDetails(UserEntity.builder()
                    .id(UUID.randomUUID())
                    .username("ADMIN")
                    .password(new BCryptPasswordEncoder().encode("1234"))
                    .role(UserRoleEnum.ROLE_ADMIN).build());
            Authentication authentication = new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities());
            context.setAuthentication(authentication);
            return context;
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @WithSecurityContext(factory = CustomUserDetailsSecurityContextFactory.class)
    @interface WithCustomUserDetails {
    }
}
