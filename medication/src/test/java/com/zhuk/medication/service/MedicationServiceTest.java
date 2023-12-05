package com.zhuk.medication.service;

import com.zhuk.medication.dto.CredentialsDto;
import com.zhuk.medication.dto.MedicationDto;
import com.zhuk.medication.dto.NewMedicationDto;
import com.zhuk.medication.dto.UpdateQuantityDto;
import com.zhuk.medication.entity.UserEntity;
import com.zhuk.medication.enums.MedicationTypeEnum;
import com.zhuk.medication.enums.UserRoleEnum;
import com.zhuk.medication.exception.medication.MedicationAlreadyExistsException;
import com.zhuk.medication.exception.medication.MedicationNotFoundException;
import com.zhuk.medication.exception.medication.MedicationNotEnoughQuantityException;
import com.zhuk.medication.security.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@MedicationServiceTest.WithCustomUserDetails
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MedicationServiceTest {
    @Autowired
    @SpyBean
    MedicationService medicationService;
    @Autowired
    DataSource dataSource;
    @Autowired
    UserService userService;
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15");

    @BeforeEach
    public void clearTables() throws Exception {
        try(Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM medication");
            preparedStatement.execute();
        }
        try(Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM users");
            preparedStatement.execute();
        }
    }

    @BeforeEach
    public void setup() {
        userService.saveUser(CredentialsDto.builder()
                .username("ADMIN")
                .password("1234".toCharArray())
                .build());
    }

    @Test
    public void findAll_ShouldReturnMedicationList_WhenDataExists() throws Exception {
        try(Connection connection = dataSource.getConnection()) {
            NewMedicationDto newMedicationDto = NewMedicationDto.builder()
                    .name("FIRST")
                    .manufacturer("TEST")
                    .type("PEN")
                    .weight(400)
                    .quantity(10)
                    .additionalInfo("FIRST INFO")
                    .build();
            medicationService.saveMedication(newMedicationDto);
            List<MedicationDto> medications = medicationService.findAll();
            String query = "SELECT * FROM medication";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                MedicationDto medicationDto = MedicationDto.builder()
                        .id(resultSet.getLong("id"))
                        .name(resultSet.getString("name"))
                        .manufacturer(resultSet.getString("manufacturer"))
                        .type(MedicationTypeEnum.valueOf(resultSet.getString("type")))
                        .quantity(resultSet.getInt("quantity"))
                        .weight(resultSet.getInt("weight"))
                        .additionalInfo(resultSet.getString("additional_info"))
                        .build();
                assertTrue(medications.contains(medicationDto));
            }
            assertEquals(1, medications.size());
        }
    }

    @Test
    public void findById_ShouldFindMedication_WhenMedicationExists() throws Exception {
        NewMedicationDto newMedicationDto = NewMedicationDto.builder()
                .name("FIRST")
                .manufacturer("TEST")
                .type("PEN")
                .weight(400)
                .quantity(10)
                .additionalInfo("FIRST INFO")
                .build();
        medicationService.saveMedication(newMedicationDto);
        Long id = getIdByName("FIRST");
        MedicationDto medicationDto = medicationService.findById(id);
        assertEquals(newMedicationDto.getName(), medicationDto.getName());
    }

    @Test
    public void findById_ShouldThrowException_WhenMedicationDoesntExists() {
        assertThrows(MedicationNotFoundException.class, () -> medicationService.findById(999999L));
    }

    @Test
    public void saveMedication_ShouldSave_WhenHappyPath() throws Exception{
        try(Connection connection = dataSource.getConnection()) {
            NewMedicationDto newMedicationDto = NewMedicationDto.builder()
                    .name("FIRST")
                    .manufacturer("TEST")
                    .type("PEN")
                    .weight(400)
                    .quantity(10)
                    .additionalInfo("FIRST INFO")
                    .build();
            medicationService.saveMedication(newMedicationDto);
            Long id = getIdByName("FIRST");
            String query = "SELECT * FROM medication where id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            int counter = 0;
            while (resultSet.next()) {
                counter++;
                assertEquals("FIRST", resultSet.getString("name"));
            }
            assertEquals(1, counter);
        }
    }

    @Test
    public void saveMedication_ShouldThrowAlreadyExistsException_WhenMedicationAlreadyExists() throws Exception{
        NewMedicationDto newMedicationDto = NewMedicationDto.builder()
                .name("FIRST")
                .manufacturer("TEST")
                .type("PEN")
                .weight(400)
                .quantity(10)
                .additionalInfo("FIRST INFO")
                .build();
        medicationService.saveMedication(newMedicationDto);
        assertThrows(MedicationAlreadyExistsException.class, () -> medicationService.saveMedication(newMedicationDto));
    }

    @Test
    public void deleteMedication_ShouldDeleteMedication_WhenMedicationExists() throws Exception {
        try(Connection connection = dataSource.getConnection()) {
            NewMedicationDto newMedicationDto = NewMedicationDto.builder()
                    .name("FIRST")
                    .manufacturer("TEST")
                    .type("PEN")
                    .weight(400)
                    .quantity(10)
                    .additionalInfo("FIRST INFO")
                    .build();
            medicationService.saveMedication(newMedicationDto);
            Long id = getIdByName("FIRST");
            String query = "SELECT COUNT(id) FROM medication";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSetBeforeDeletion = preparedStatement.executeQuery();
            resultSetBeforeDeletion.next();
            int countBeforeDeletion = resultSetBeforeDeletion.getInt(1);
            medicationService.deleteMedication(id);
            ResultSet resultSetAfterDeletion = preparedStatement.executeQuery();
            resultSetAfterDeletion.next();
            int countAfterDeletion = resultSetAfterDeletion.getInt(1);
            assertEquals(1, countBeforeDeletion);
            assertEquals(0, countAfterDeletion);
        }
    }

    @Test
    public void deleteMedication_ShouldDoNothing_WhenMedicationDoesntExist() throws Exception {
        try(Connection connection = dataSource.getConnection()) {
            String query = "SELECT COUNT(id) FROM medication";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            medicationService.deleteMedication(1L);
            ResultSet resultSetAfterDeletion = preparedStatement.executeQuery();
            resultSetAfterDeletion.next();
            int countAfterDeletion = resultSetAfterDeletion.getInt(1);
            assertEquals(0, countAfterDeletion);
        }
    }

    @Test
    public void updateMedication_ShouldSaveMedication_WhenMedicationDoesntExist() throws Exception {
        try(Connection connection = dataSource.getConnection()) {
            NewMedicationDto newMedicationDto = NewMedicationDto.builder()
                    .name("FIRST")
                    .manufacturer("TEST")
                    .type("PEN")
                    .weight(400)
                    .quantity(10)
                    .additionalInfo("FIRST INFO")
                    .build();
            medicationService.updateMedication(newMedicationDto);
            Long id = getIdByName("FIRST");
            String query = "SELECT * FROM medication where id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            int counter = 0;
            while (resultSet.next()) {
                counter++;
                assertEquals("FIRST", resultSet.getString("name"));
            }
            assertEquals(1, counter);
        }
    }

    @Test
    public void updateMedication_ShouldUpdateMedication_WhenMedicationAlreadyExists() throws Exception {
        try(Connection connection = dataSource.getConnection()) {
            NewMedicationDto newMedicationDto = NewMedicationDto.builder()
                    .name("FIRST")
                    .manufacturer("TEST")
                    .type("PEN")
                    .weight(400)
                    .quantity(10)
                    .additionalInfo("FIRST INFO")
                    .build();
            medicationService.saveMedication(newMedicationDto);
            medicationService.updateMedication(newMedicationDto
                    .toBuilder()
                    .manufacturer("UPDATED")
                    .additionalInfo("UPDATED")
                    .build()
            );
            Long id = getIdByName("FIRST");
            String query = "SELECT * FROM medication where id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            int counter = 0;
            while (resultSet.next()) {
                counter++;
                assertEquals("UPDATED", resultSet.getString("manufacturer"));
                assertEquals("UPDATED", resultSet.getString("additional_info"));
            }
            assertEquals(1, counter);
        }
    }

    @Test
    public void updateQuantityForMedication_ShouldUpdateQuantity_WhenHappyPath() throws Exception {
        try(Connection connection = dataSource.getConnection()) {
            NewMedicationDto newMedicationDto = NewMedicationDto.builder()
                    .name("FIRST")
                    .manufacturer("TEST")
                    .type("PEN")
                    .weight(400)
                    .quantity(10)
                    .additionalInfo("FIRST INFO")
                    .build();
            medicationService.saveMedication(newMedicationDto);
            Long id = getIdByName("FIRST");
            medicationService.updateQuantity(id, UpdateQuantityDto.builder().quantity(-5).build());
            String query = "SELECT * FROM medication where id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                assertEquals(5, resultSet.getInt("quantity"));
            }
        }
    }

    @Test
    public void updateQuantityForMedication_ShouldReturnBadRequest_WhenNotEnoughQuantity() throws Exception {
        NewMedicationDto newMedicationDto = NewMedicationDto.builder()
                .name("FIRST")
                .manufacturer("TEST")
                .type("PEN")
                .weight(400)
                .quantity(10)
                .additionalInfo("FIRST INFO")
                .build();
        medicationService.saveMedication(newMedicationDto);
        Long id = getIdByName("FIRST");
        assertThrows(MedicationNotEnoughQuantityException.class,
                () -> medicationService.updateQuantity(id, UpdateQuantityDto.builder().quantity(-100).build()));
    }

    @Test
    public void updateQuantityForMedication_ShouldReturnNotFound_WhenMedicationDoesntExist() throws Exception {
        assertThrows(MedicationNotFoundException.class,
                () -> medicationService.updateQuantity(1000L, UpdateQuantityDto.builder().quantity(1).build()));
    }

    private Long getIdByName(String name) throws Exception{
        try(Connection connection = dataSource.getConnection()) {
            String idQuery = "SELECT id FROM medication m where name = ?";
            PreparedStatement preparedStatementForId = connection.prepareStatement(idQuery);
            preparedStatementForId.setString(1, name);
            ResultSet resultSet = preparedStatementForId.executeQuery();
            resultSet.next();
            return resultSet.getLong(1);
        }
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
