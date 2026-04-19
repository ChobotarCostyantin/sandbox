package edu.chobotar.sandbox.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.chobotar.sandbox.model.User;
import edu.chobotar.sandbox.request.UserPageRequest;
import edu.chobotar.sandbox.response.ApiResponse;
import edu.chobotar.sandbox.response.PaginationMetaData;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
/*
    @author  User
    @project  sandbox
    @class  UserServicePagingTest
    @version  1.0.0
    @since  19.04.2026 - 16.25
*/

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServicePagingTest {

    @Autowired
    private UserService underTest;

//    List<User> users = new ArrayList<>();

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

    // 1
    @Test
    void whenHappyPathThenOk(){
        // given
        UserPageRequest request = new UserPageRequest(0,5);
        // when
        ApiResponse<PaginationMetaData, User> response = underTest.getUsersPage(request);
        //then
        assertNotNull(response);
        assertNotNull(response.getMeta());

        assertEquals(200, response.getMeta().getCode());
        assertTrue(response.getMeta().isSuccess());
        assertNull(response.getMeta().getErrorMessage());

        assertEquals(0, response.getMeta().getNumber());
        assertEquals(5, response.getMeta().getSize());
        assertEquals(30, response.getMeta().getTotalElements());
        assertEquals(6, response.getMeta().getTotalPages());
        assertTrue(response.getMeta().isFirst());
        assertFalse(response.getMeta().isLast());

        assertNotNull(response.getData());
        assertFalse(response.getData().isEmpty());
        assertEquals(5, response.getData().size());
    }

    // 2
    @Test
    void whenSizeIs_7_AndPageIs_4_ThenIsLast_TrueAndSizeEquals_2() {
        // given
        UserPageRequest request = new UserPageRequest(4, 7);

        // when
        ApiResponse<PaginationMetaData, User> response = underTest.getUsersPage(request);

        // then
        assertNotNull(response);
        assertNotNull(response.getMeta());

        assertEquals(200, response.getMeta().getCode());
        assertTrue(response.getMeta().isSuccess());
        assertNull(response.getMeta().getErrorMessage());

        assertEquals(4, response.getMeta().getNumber());
        assertEquals(7, response.getMeta().getSize());
        assertEquals(30, response.getMeta().getTotalElements());
        assertEquals(5, response.getMeta().getTotalPages());

        assertFalse(response.getMeta().isFirst());
        assertTrue(response.getMeta().isLast());

        assertNotNull(response.getData());
        assertFalse(response.getData().isEmpty());
        assertEquals(2, response.getData().size());
    }

    // 3
    @Test
    void whenTheListIsEmptyThenErrorMessageHasTheWarning() {
        // given
        underTest.deleteAll();
        UserPageRequest request = new UserPageRequest(0, 5);

        // when
        ApiResponse<PaginationMetaData, User> response = underTest.getUsersPage(request);

        // then
        assertNotNull(response);
        assertNotNull(response.getMeta());

        assertEquals(200, response.getMeta().getCode());
        assertTrue(response.getMeta().isSuccess());
        assertEquals("Warning: The list is empty.", response.getMeta().getErrorMessage());

        assertEquals(0, response.getMeta().getNumber());
        assertEquals(5, response.getMeta().getSize());
        assertEquals(0, response.getMeta().getTotalElements());
        assertEquals(0, response.getMeta().getTotalPages());
    }

    // 4
    @Test
    void whenTheListIsEmptyThenMetadataAndDataAreNotNull() {
        // given
        underTest.deleteAll();
        UserPageRequest request = new UserPageRequest(0, 5);

        // when
        ApiResponse<PaginationMetaData, User> response = underTest.getUsersPage(request);

        // then
        assertNotNull(response);
        assertNotNull(response.getMeta());
        assertNotNull(response.getData());

        assertTrue(response.getData().isEmpty());
    }

    // 5
    @Test
    void whenPageValueIsOutOfRangeThenErrorMessageHasTheWarning() {
        // given
        UserPageRequest request = new UserPageRequest(10, 5);

        // when
        ApiResponse<PaginationMetaData, User> response = underTest.getUsersPage(request);

        // then
        assertNotNull(response);
        assertNotNull(response.getMeta());

        assertEquals(200, response.getMeta().getCode());
        assertTrue(response.getMeta().isSuccess());
        assertEquals("Warning: Page value is out of range.", response.getMeta().getErrorMessage());

        assertEquals(10, response.getMeta().getNumber());
        assertEquals(5, response.getMeta().getSize());
        assertEquals(30, response.getMeta().getTotalElements());
        assertEquals(6, response.getMeta().getTotalPages());

        assertFalse(response.getMeta().isFirst());
        assertFalse(response.getMeta().isLast());

        assertNotNull(response.getData());
        assertTrue(response.getData().isEmpty());
    }

    // 6
    @Test
    void whenSizeEqualsTotalElements_thenIsFirstAndIsLastAreTrue() {
        // given
        UserPageRequest request = new UserPageRequest(0, 30);

        // when
        ApiResponse<PaginationMetaData, User> response = underTest.getUsersPage(request);

        // then
        assertTrue(response.getMeta().isFirst());
        assertTrue(response.getMeta().isLast());
        assertEquals(30, response.getData().size());
    }

    // 7
    @Test
    void whenSizeIsGreaterThanTotalElements_thenReturnsAllElements() {
        // given
        UserPageRequest request = new UserPageRequest(0, 100);

        // when
        ApiResponse<PaginationMetaData, User> response = underTest.getUsersPage(request);

        // then
        assertEquals(1, response.getMeta().getTotalPages());
        assertTrue(response.getMeta().isLast());
        assertEquals(30, response.getData().size());
    }

    // 8
    @Test
    void whenPerfectDivisionOnLastPage_thenIsLastIsTrueAndDataIsFull() {
        // given
        UserPageRequest request = new UserPageRequest(2, 10);

        // when
        ApiResponse<PaginationMetaData, User> response = underTest.getUsersPage(request);

        // then
        assertTrue(response.getMeta().isLast());
        assertEquals(10, response.getData().size());
    }

    // 9
    @Test
    void whenRemainderOnLastPage_thenDataSizeMatchesRemainder() {
        // given
        UserPageRequest request = new UserPageRequest(3, 8);

        // when
        ApiResponse<PaginationMetaData, User> response = underTest.getUsersPage(request);

        // then
        assertTrue(response.getMeta().isLast());
        assertEquals(6, response.getData().size());
    }

    // 10
    @Test
    void whenExtremeOutOfRange_thenWarningAndEmptyData() {
        // given
        UserPageRequest request = new UserPageRequest(999, 5);

        // when
        ApiResponse<PaginationMetaData, User> response = underTest.getUsersPage(request);

        // then
        assertEquals("Warning: Page value is out of range.", response.getMeta().getErrorMessage());
        assertTrue(response.getData().isEmpty());
    }

    // 11
    @Test
    void whenSizeIsOne_thenTotalPagesEqualsTotalElements() {
        // given
        UserPageRequest request = new UserPageRequest(0, 1);

        // when
        ApiResponse<PaginationMetaData, User> response = underTest.getUsersPage(request);

        // then
        assertEquals(30, response.getMeta().getTotalPages());
        assertEquals(1, response.getData().size());
    }

    // 12
    @Test
    void whenMiddlePage_thenNeitherFirstNorLast() {
        // given
        UserPageRequest request = new UserPageRequest(1, 10);

        // when
        ApiResponse<PaginationMetaData, User> response = underTest.getUsersPage(request);

        // then
        assertFalse(response.getMeta().isFirst());
        assertFalse(response.getMeta().isLast());
        assertEquals(10, response.getData().size());
    }

    // 13
    @Test
    void whenNegativePage_thenThrowsIllegalArgumentException() {
        // given
        UserPageRequest request = new UserPageRequest(-1, 5);

        // when & then
        assertThrows(IllegalArgumentException.class, () -> underTest.getUsersPage(request));
    }

    // 14
    @Test
    void whenNegativeSize_thenThrowsIllegalArgumentException() {
        // given
        UserPageRequest request = new UserPageRequest(0, -5);

        // when & then
        assertThrows(IllegalArgumentException.class, () -> underTest.getUsersPage(request));
    }

    // 15
    @Test
    void whenSizeIsZero_thenThrowsIllegalArgumentException() {
        // given
        UserPageRequest request = new UserPageRequest(0, 0);

        // when & then
        assertThrows(IllegalArgumentException.class, () -> underTest.getUsersPage(request));
    }
}