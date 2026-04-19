package edu.chobotar.sandbox.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.chobotar.sandbox.model.User;
import edu.chobotar.sandbox.request.UserPageRequest;
import edu.chobotar.sandbox.response.ApiResponse;
import edu.chobotar.sandbox.response.BaseMetaData;
import edu.chobotar.sandbox.response.PaginationMetaData;
import edu.chobotar.sandbox.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
/*
    @author  User
    @project  sandbox
    @class  LoggingTest
    @version  1.0.0
    @since  19.04.2026 - 19.18
*/

@ExtendWith(OutputCaptureExtension.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LoggingTest {

    @Autowired
    private UserService underTest;

    private String userId;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .findAndRegisterModules();

    @BeforeEach
    void setUp() throws IOException {
        underTest.deleteAll();

        File file = new File("src/test/resources/MOCK_DATA.json");
        if (!file.exists()) {
            file = new File("MOCK_DATA.json");
        }

        if (file.exists()) {
            List<User> mockUsers = objectMapper.readValue(file, new TypeReference<List<User>>() {});
            underTest.createAll(mockUsers);
        } else {
            fail("Файл MOCK_DATA.json не знайдено!");
        }

        userId = underTest.getAll().get((int) (Math.random() * underTest.getAll().size())).getId();
    }

    @AfterEach
    void tearDown() {
    }

    // 1
    @Test
    void testLoggingOutputBeforeMethodGetById(CapturedOutput output) {
        // given
        String testId = userId;
        User user =  underTest.getById(testId);
        //when
        assertNotNull(user);
        // then
        assertTrue(output.toString().contains("Entering method:"));
        assertTrue(output.toString().contains("UserService.getById"));
        assertTrue(output.toString().contains(user.getId()));
    }

    // 2
    @Test
    void testLoggingOutputAfterMethodGetById(CapturedOutput output) {
        // given
        String testId = userId;
        User user =  underTest.getById(testId);
        // when
        assertNotNull(user);
        // then
        assertTrue(output.toString().contains("UserService.getById"));
        assertTrue(output.toString().contains("completed successfully with result"));
        assertTrue(output.toString().contains(user.getId()));
        assertTrue(output.toString().contains(user.getName()));
    }

    // 3
    @Test
    void testLoggingOutputBeforeMethodGetUsersPage(CapturedOutput output) {
        // given
        UserPageRequest request = new UserPageRequest(0,5);
        // when
        ApiResponse<PaginationMetaData, User> page =  underTest.getUsersPage(request);
        assertNotNull(page);
        // then
        assertTrue(output.toString().contains("UserService.getUsersPage"));
        assertTrue(output.toString().contains(String.valueOf(page.getMeta().getNumber())));
        assertTrue(output.toString().contains(String.valueOf(page.getMeta().getSize())));
    }

    // 4
    @Test
    void testLoggingOutputAfterMethodGetUsersPage(CapturedOutput output) {
        // given
        UserPageRequest request = new UserPageRequest(0, 5);
        // when
        ApiResponse<PaginationMetaData, User> page = underTest.getUsersPage(request);
        assertNotNull(page);
        // then
        assertTrue(output.toString().contains("UserService.getUsersPage"));
        assertTrue(output.toString().contains("completed successfully with result"));
        assertTrue(output.toString().contains(String.valueOf(page.getMeta().getCode())));
    }

    // 5
    @Test
    void testLoggingOutputBeforeMethodGetAll(CapturedOutput output) {
        // when
        underTest.getAll();
        // then
        assertTrue(output.toString().contains("Entering method:"));
        assertTrue(output.toString().contains("UserService.getAll"));
    }

    // 6
    @Test
    void testLoggingOutputAfterMethodGetAll(CapturedOutput output) {
        // given
        List<User> users = underTest.getAll();
        // when
        assertNotNull(users);
        // then
        assertTrue(output.toString().contains("UserService.getAll"));
        assertTrue(output.toString().contains("completed successfully with result"));
    }

    // 7
    @Test
    void testLoggingOutputBeforeMethodEmailIsTaken(CapturedOutput output) {
        // given
        String email = "test_logging@gmail.com";
        // when
        underTest.emailIsTaken(email);
        // then
        assertTrue(output.toString().contains("Entering method:"));
        assertTrue(output.toString().contains("UserService.emailIsTaken"));
        assertTrue(output.toString().contains(email));
    }

    // 8
    @Test
    void testLoggingOutputAfterMethodEmailIsTaken(CapturedOutput output) {
        // given
        String email = "test_logging@gmail.com";
        // when
        Boolean isTaken = underTest.emailIsTaken(email);
        assertNotNull(isTaken);
        // then
        assertTrue(output.toString().contains("UserService.emailIsTaken"));
        assertTrue(output.toString().contains("completed successfully with result"));
        assertTrue(output.toString().contains(isTaken.toString()));
    }

    // 9
    @Test
    void testLoggingOutputBeforeMethodGetAllAsApiResponse(CapturedOutput output) {
        // when
        underTest.getAllAsApiResponse();
        // then
        assertTrue(output.toString().contains("Entering method:"));
        assertTrue(output.toString().contains("UserService.getAllAsApiResponse"));
    }

    // 10
    @Test
    void testLoggingOutputAfterMethodGetAllAsApiResponse(CapturedOutput output) {
        // when
        ApiResponse<BaseMetaData, User> response = underTest.getAllAsApiResponse();
        assertNotNull(response);
        // then
        assertTrue(output.toString().contains("UserService.getAllAsApiResponse"));
        assertTrue(output.toString().contains("completed successfully with result"));
    }

    // 11
    @Test
    void testLoggingOutputBeforeMethodGetByIdAsApiResponse(CapturedOutput output) {
        // given
        String testId = userId;
        // when
        underTest.getByIdAsApiResponse(testId);
        // then
        assertTrue(output.toString().contains("Entering method:"));
        assertTrue(output.toString().contains("UserService.getByIdAsApiResponse"));
        assertTrue(output.toString().contains(testId));
    }

    // 12
    @Test
    void testLoggingOutputAfterMethodGetByIdAsApiResponse(CapturedOutput output) {
        // given
        String testId = userId;
        // when
        ApiResponse<BaseMetaData, User> response = underTest.getByIdAsApiResponse(testId);
        assertNotNull(response);
        // then
        assertTrue(output.toString().contains("UserService.getByIdAsApiResponse"));
        assertTrue(output.toString().contains("completed successfully with result"));
        assertTrue(output.toString().contains(testId));
    }

    // 13
    @Test
    void testLoggingOutputBeforeMethodUpdateAsApiResponse(CapturedOutput output) {
        // given
        User userToUpdate = new User();
        userToUpdate.setId("non-existent-id");
        // when
        underTest.updateAsApiResponse(userToUpdate);
        // then
        assertTrue(output.toString().contains("Entering method:"));
        assertTrue(output.toString().contains("UserService.updateAsApiResponse"));
        assertTrue(output.toString().contains("non-existent-id"));
    }

    // 14
    @Test
    void testLoggingOutputAfterMethodUpdateAsApiResponse(CapturedOutput output) {
        // given
        User userToUpdate = new User();
        userToUpdate.setId("non-existent-id");
        // when
        ApiResponse<BaseMetaData, User> response = underTest.updateAsApiResponse(userToUpdate);
        assertNotNull(response);
        // then
        assertTrue(output.toString().contains("UserService.updateAsApiResponse"));
        assertTrue(output.toString().contains("completed successfully with result"));
        assertTrue(output.toString().contains(String.valueOf(response.getMeta().getCode())));
    }

    // 15
    @Test
    void testLoggingOutputBeforeMethodDeleteByIdAsApiResponse(CapturedOutput output) {
        // given
        String fakeId = "delete-fake-id-123";
        // when
        underTest.deleteByIdAsApiResponse(fakeId);
        // then
        assertTrue(output.toString().contains("Entering method:"));
        assertTrue(output.toString().contains("UserService.deleteByIdAsApiResponse"));
        assertTrue(output.toString().contains(fakeId));
    }
}