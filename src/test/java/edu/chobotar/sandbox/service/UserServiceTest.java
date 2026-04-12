package edu.chobotar.sandbox.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.chobotar.sandbox.model.User;
import edu.chobotar.sandbox.response.ApiResponse;
import edu.chobotar.sandbox.response.BaseMetaData;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*
    @author  User
    @project  sandbox
    @class  UserServiceTest
    @version  1.3.0
*/

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTest {

    @Autowired
    private UserService underTest;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .findAndRegisterModules();

    @BeforeAll
    static void beforeAll() {
    }

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
    }

    @AfterEach
    void tearDown() {
    }

    @AfterAll
    static void afterAll() {
    }

    // 1
    @Test
    void whenUserIsPresentThenReturnAsOkApiResponse() {
        // given
        String id = underTest.getAll().get(0).getId();

        // when
        ApiResponse<BaseMetaData, User> response = underTest.getByIdAsApiResponse(id);

        // then
        assertNotNull(response);
        assertFalse(response.getData().isEmpty());
        assertNotNull(response.getData().get(0));
        assertTrue(response.getMeta().isSuccess());
        assertEquals(200, response.getMeta().getCode());
        assertNull(response.getMeta().getErrorMessage());
        assertEquals(id, response.getData().get(0).getId());
    }

    // 2
    @Test
    void whenUserIsNotPresentThenReturnAsNotFoundApiResponseCode_404() {
        // given
        String id = "invalid_non_existent_id";

        // when
        ApiResponse<BaseMetaData, User> response = underTest.getByIdAsApiResponse(id);

        // then
        assertNotNull(response);
        assertEquals(404, response.getMeta().getCode());
        assertNull(response.getData());
        assertFalse(response.getMeta().isSuccess());
        assertNotNull(response.getMeta().getErrorMessage());
        assertEquals("User not found", response.getMeta().getErrorMessage());
    }

    // 3
    @Test
    void whenDatabaseIsNotEmptyThenReturnAsOkApiResponse() {
        // given
        List<User> users = underTest.getAll();

        // when
        ApiResponse<BaseMetaData, User> response = underTest.getAllAsApiResponse();

        // then
        assertNotNull(response);
        assertEquals(200, response.getMeta().getCode());
        assertNotNull(response.getData());
        assertTrue(response.getMeta().isSuccess());
        assertNull(response.getMeta().getErrorMessage());
        assertEquals(users.size(), response.getData().size());
    }

    // 4
    @Test
    void whenDatabaseIsEmptyThenGetAllReturns404ApiResponse() {
        // given
        underTest.deleteAll();

        // when
        ApiResponse<BaseMetaData, User> response = underTest.getAllAsApiResponse();

        // then
        assertNotNull(response);
        assertEquals(404, response.getMeta().getCode());
        assertFalse(response.getMeta().isSuccess());
        assertEquals("No users found", response.getMeta().getErrorMessage());
        assertNull(response.getData());
    }

    // 5
    @Test
    void whenCreateUserThenReturn201ApiResponse() {
        // given
        User newUser = User.builder().name("John Doe").email("john@example.com").build();

        // when
        ApiResponse<BaseMetaData, User> response = underTest.createAsApiResponse(newUser);

        // then
        assertNotNull(response);
        assertEquals(201, response.getMeta().getCode());
        assertTrue(response.getMeta().isSuccess());
        assertFalse(response.getData().isEmpty());
        assertNotNull(response.getData().get(0).getId());
    }

    // 6
    @Test
    void whenCreateAllUsersThenReturn201ApiResponse() {
        // given
        User u1 = User.builder().name("Alice").build();
        User u2 = User.builder().name("Bob").build();
        List<User> newUsers = List.of(u1, u2); // List.of доступно з Java 9+

        // when
        ApiResponse<BaseMetaData, User> response = underTest.createAllAsApiResponse(newUsers);

        // then
        assertNotNull(response);
        assertEquals(201, response.getMeta().getCode());
        assertTrue(response.getMeta().isSuccess());
        assertEquals(2, response.getData().size());
    }

    // 7
    @Test
    void whenUpdateExistingUserThenReturn200ApiResponse() {
        // given
        User existingUser = underTest.getAll().get(0);
        User updatedUser = User.builder()
                .id(existingUser.getId())
                .name("Updated Name")
                .email(existingUser.getEmail())
                .build();

        // when
        ApiResponse<BaseMetaData, User> response = underTest.updateAsApiResponse(updatedUser);

        // then
        assertNotNull(response);
        assertEquals(200, response.getMeta().getCode());
        assertTrue(response.getMeta().isSuccess());
        assertEquals(existingUser.getId(), response.getData().get(0).getId());
        assertEquals("Updated Name", response.getData().get(0).getName());
    }

    // 8
    @Test
    void whenUpdateUserWithInvalidIdThenReturn404ApiResponse() {
        // given
        User invalidUser = User.builder().id("ghost_id").name("Ghost").build();

        // when
        ApiResponse<BaseMetaData, User> response = underTest.updateAsApiResponse(invalidUser);

        // then
        assertNotNull(response);
        assertEquals(404, response.getMeta().getCode());
        assertFalse(response.getMeta().isSuccess());
        assertEquals("User not found", response.getMeta().getErrorMessage());
    }

    // 9
    @Test
    void whenUpdateUserWithNullIdThenReturn404ApiResponse() {
        // given
        User invalidUser = User.builder().name("No ID").build();

        // when
        ApiResponse<BaseMetaData, User> response = underTest.updateAsApiResponse(invalidUser);

        // then
        assertNotNull(response);
        assertEquals(404, response.getMeta().getCode());
        assertFalse(response.getMeta().isSuccess());
        assertEquals("User not found", response.getMeta().getErrorMessage());
    }

    // 10
    @Test
    void whenDeleteExistingUserThenReturn200ApiResponse() {
        // given
        String id = underTest.getAll().get(0).getId();

        // when
        ApiResponse<BaseMetaData, User> response = underTest.deleteByIdAsApiResponse(id);

        // then
        assertNotNull(response);
        assertEquals(200, response.getMeta().getCode());
        assertTrue(response.getMeta().isSuccess());
        assertEquals(id, response.getData().get(0).getId());
        assertNull(underTest.getById(id));
    }

    // 11
    @Test
    void whenDeleteUserWithInvalidIdThenReturn404ApiResponse() {
        // given
        String invalidId = "invalid_id";

        // when
        ApiResponse<BaseMetaData, User> response = underTest.deleteByIdAsApiResponse(invalidId);

        // then
        assertNotNull(response);
        assertEquals(404, response.getMeta().getCode());
        assertFalse(response.getMeta().isSuccess());
        assertEquals("User not found", response.getMeta().getErrorMessage());
    }

    // 12
    @Test
    void whenDeleteAllUsersThenReturn204ApiResponse() {
        // given
        // БД вже не порожня завдяки @BeforeEach

        // when
        ApiResponse<BaseMetaData, User> response = underTest.deleteAllAsApiResponse();

        // then
        assertNotNull(response);
        assertEquals(204, response.getMeta().getCode());
        assertTrue(response.getMeta().isSuccess());
        assertNull(response.getData());
    }

    // 13
    @Test
    void whenDeleteAllUsersThenDatabaseBecomesEmpty() {
        // given
        // БД заповнена перед тестом

        // when
        underTest.deleteAllAsApiResponse();
        int size = underTest.getAll().size();

        // then
        assertEquals(0, size);
    }

    // 14
    @Test
    void whenCreateUserThenUserCanBeFetchedById() {
        // given
        User newUser = User.builder().name("Integration Test").build();
        ApiResponse<BaseMetaData, User> createResponse = underTest.createAsApiResponse(newUser);
        String newId = createResponse.getData().get(0).getId();

        // when
        ApiResponse<BaseMetaData, User> fetchResponse = underTest.getByIdAsApiResponse(newId);

        // then
        assertNotNull(fetchResponse);
        assertEquals(200, fetchResponse.getMeta().getCode());
        assertTrue(fetchResponse.getMeta().isSuccess());
        assertEquals(newId, fetchResponse.getData().get(0).getId());
        assertEquals("Integration Test", fetchResponse.getData().get(0).getName());
    }

    // 15
    @Test
    void whenDeleteUserTwiceThenSecondTimeReturns404ApiResponse() {
        // given
        String id = underTest.getAll().get(0).getId();
        underTest.deleteByIdAsApiResponse(id); // Перше успішне видалення

        // when
        ApiResponse<BaseMetaData, User> response = underTest.deleteByIdAsApiResponse(id); // Спроба видалити вдруге

        // then
        assertNotNull(response);
        assertEquals(404, response.getMeta().getCode());
        assertFalse(response.getMeta().isSuccess());
        assertEquals("User not found", response.getMeta().getErrorMessage());
    }
}