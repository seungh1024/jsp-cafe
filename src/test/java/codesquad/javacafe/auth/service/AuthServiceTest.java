package codesquad.javacafe.auth.service;

import codesquad.javacafe.auth.dto.request.LoginRequestDto;
import codesquad.javacafe.common.db.DBConnection;
import codesquad.javacafe.common.exception.ClientErrorCode;
import codesquad.javacafe.common.exception.CustomException;
import codesquad.javacafe.member.controller.MemberController;
import codesquad.javacafe.member.entity.Member;
import codesquad.javacafe.member.repository.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

    private static Connection connection;
    private static MemberRepository memberRepository;
    @BeforeAll
    static void setUp() throws SQLException {
        DBConnection.setConnectionInfo("jdbc:h2:mem:testdb;MODE=MySQL;DATABASE_TO_LOWER=TRUE", "sa", "");
        connection = DBConnection.getConnection();
        createTable();
        memberRepository = MemberRepository.getInstance();
    }

    @AfterEach
    void afterEach() throws SQLException {
        clearTable();
    }

    private void clearTable() throws SQLException {
        var sql = "DELETE FROM member";
        try (var statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    static void createTable() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "");
        Statement statement = connection.createStatement();

        // Create the member table
        String createTableSql = "CREATE TABLE if not exists member (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "member_id VARCHAR(255), " +
                "member_password VARCHAR(255), " +
                "member_name VARCHAR(255)" +
                ")";
        statement.execute(createTableSql);

        statement.close();
        connection.close();
    }

    @Test
    public void test_instance_creation() {
        AuthService authService = AuthService.getInstance();
        assertNotNull(authService);
    }

    @Test
    public void test_singleton_instance() {
        AuthService instance1 = AuthService.getInstance();
        AuthService instance2 = AuthService.getInstance();
        assertSame(instance1, instance2);
    }

    // Verify that an exception is thrown when an invalid password is provided
    @Test
    public void test_invalid_password_exception() {
        Member member = new Member("testUser", "correctPassword", "Test User");
        memberRepository.save(member);
        LoginRequestDto requestDto = new LoginRequestDto(Map.of("userId", new String[]{"testUser"}, "password", new String[]{"wrongPassword"}));
        MemberRepository memberRepository = new MemberRepository();

        AuthService authService = AuthService.getInstance();

        Exception exception = assertThrows(CustomException.class, () -> {
            authService.getLoginInfo(requestDto);
        });

        assertEquals(ClientErrorCode.INVALID_PASSWORD.getMessage(), exception.getMessage());
    }
}