package edu.chobotar.sandbox.service;

import edu.chobotar.sandbox.model.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
/*
    @author  User
    @project  sandbox
    @class  UserServiceTest
    @version  1.0.0
    @since  22.03.2026 - 21.35
*/

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTest {

    @Autowired
    private UserService underTest;

    List<User> users = new ArrayList<>();

    @BeforeAll
    static  void beforeAll() {
    }

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void whenGetAllUsersListThenSizeIs30() {
        int size = underTest.getAll().size();
        assertEquals(30, size);
    }
}