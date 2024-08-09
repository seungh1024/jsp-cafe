package codesquad.javacafe.auth.controller;

import codesquad.javacafe.auth.dto.request.LoginRequestDto;
import codesquad.javacafe.auth.service.AuthService;
import codesquad.javacafe.common.db.DBConnection;
import codesquad.javacafe.common.exception.ClientErrorCode;
import codesquad.javacafe.common.exception.CustomException;
import codesquad.javacafe.common.session.MemberInfo;
import codesquad.javacafe.member.entity.Member;
import codesquad.javacafe.member.repository.MemberRepository;
import codesquad.javacafe.member.service.MemberService;
import codesquad.javacafe.util.CustomHttpServletRequest;
import codesquad.javacafe.util.CustomHttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class AuthControllerTest {
    private static AuthController authController;
    private static Connection connection;

    @BeforeAll
    static void setUp() throws SQLException {
        authController = new AuthController();
        DBConnection.setConnectionInfo("jdbc:h2:mem:testdb;MODE=MySQL;DATABASE_TO_LOWER=TRUE", "sa", "");
        connection = DBConnection.getConnection();
        createTable();
    }

    @AfterEach
    void afterEach() throws SQLException {
        clearTable();
    }

    static void createTable() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "");
        Statement statement = connection.createStatement();
        String createTableSql = "CREATE TABLE if not exists member (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "member_id VARCHAR(255), " +
                "member_password VARCHAR(255), " +
                "member_name VARCHAR(255)" +
                ")";
        statement.execute(createTableSql);

        createTableSql = "CREATE TABLE if not exists post (" +
            "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
            "post_writer VARCHAR(255), " +
            "post_title VARCHAR(255), " +
            "post_contents TEXT, " +
            "post_create TIMESTAMP," +
            "member_id BIGINT NOT NULL" +
            ")";
        statement.execute(createTableSql);

        statement.close();
        connection.close();
    }

    private void clearTable() throws SQLException {
        var sql = "DELETE FROM member";
        try (var statement = connection.createStatement()) {
            statement.execute(sql);
        }


        sql = "DELETE FROM post";
        try (var statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    @Test
    public void testDoProcessGet() throws ServletException, IOException {
        HttpServletRequest request = new CustomHttpServletRequest();
        HttpServletResponse response = new CustomHttpServletResponse();
        ((CustomHttpServletRequest) request).setMethod("GET");

        authController.doProcess(request, response);

        assertEquals("/user/login.jsp", ((CustomHttpServletResponse) response).getForwardedUrl());
    }

    @Test
    public void testDoProcessPost() throws ServletException, IOException {
        Member member = new Member("user1","password1","User One");
        MemberRepository.getInstance().save(member);
        Map<String, String[]> body = new HashMap<>();
        body.put("userId", new String[]{"user1"});
        body.put("password", new String[]{"password1"});
        LoginRequestDto loginDto = new LoginRequestDto(body);

        AuthService authService = AuthService.getInstance();
        authService.getLoginInfo(loginDto);

        HttpServletRequest request = new CustomHttpServletRequest();
        HttpServletResponse response = new CustomHttpServletResponse();
        ((CustomHttpServletRequest) request).setMethod("POST");
        ((CustomHttpServletRequest) request).addParameter("userId", "user1");
        ((CustomHttpServletRequest) request).addParameter("password", "password1");

        authController.doProcess(request, response);

        assertNotNull(request.getSession().getAttribute("loginInfo"));
        assertEquals("/WEB-INF/index.jsp", ((CustomHttpServletResponse) response).getForwardedUrl());
    }

    @Test
    public void testDoProcessDelete() throws ServletException, IOException {
        HttpServletRequest request = new CustomHttpServletRequest();
        HttpServletResponse response = new CustomHttpServletResponse();
        ((CustomHttpServletRequest) request).setMethod("DELETE");

        request.getSession().setAttribute("loginInfo", new MemberInfo(1L, "user1", "User One"));

        authController.doProcess(request, response);

        assertNull(request.getSession().getAttribute("loginInfo"));
        assertEquals(200, ((CustomHttpServletResponse) response).getStatus());
    }

    @Test
    public void testDoProcessPostInvalidCredentials() throws ServletException, IOException {
        HttpServletRequest request = new CustomHttpServletRequest();
        HttpServletResponse response = new CustomHttpServletResponse();
        ((CustomHttpServletRequest) request).setMethod("POST");
        ((CustomHttpServletRequest) request).addParameter("userId", "invalidUser");
        ((CustomHttpServletRequest) request).addParameter("password", "invalidPassword");

        CustomException exception = assertThrows(CustomException.class, () -> {
            authController.doProcess(request, response);
        });

        assertEquals(ClientErrorCode.USER_NOT_FOUND.getHttpStatus(), exception.getHttpStatus());
        assertNull(request.getSession().getAttribute("loginInfo"));
    }

    @Test
    public void testDoProcessPostMissingParameters() throws ServletException, IOException {
        HttpServletRequest request = new CustomHttpServletRequest();
        HttpServletResponse response = new CustomHttpServletResponse();
        ((CustomHttpServletRequest) request).setMethod("POST");

        CustomException exception = assertThrows(CustomException.class, () -> {
            authController.doProcess(request, response);
        });

        assertEquals(ClientErrorCode.PARAMETER_IS_NULL.getHttpStatus(), exception.getHttpStatus());
        assertNull(request.getSession().getAttribute("loginInfo"));
    }

    @Test
    public void testDoProcessDeleteNoSession() throws ServletException, IOException {
        HttpServletRequest request = new CustomHttpServletRequest();
        HttpServletResponse response = new CustomHttpServletResponse();
        ((CustomHttpServletRequest) request).setMethod("DELETE");

        authController.doProcess(request, response);

        assertNull(request.getSession().getAttribute("loginInfo"));
        assertEquals(200, ((CustomHttpServletResponse) response).getStatus());
    }

    @Test
    public void testDoProcessDeleteInvalidSessionAttribute() throws ServletException, IOException {
        HttpServletRequest request = new CustomHttpServletRequest();
        HttpServletResponse response = new CustomHttpServletResponse();
        ((CustomHttpServletRequest) request).setMethod("DELETE");

        request.getSession().setAttribute("invalidAttribute", new MemberInfo(1L, "user1", "User One"));

        authController.doProcess(request, response);

        assertNull(request.getSession().getAttribute("loginInfo"));
        assertEquals(200, ((CustomHttpServletResponse) response).getStatus());
    }

    @Test
    public void testDoProcessDeleteMultipleRequests() throws ServletException, IOException {
        HttpServletRequest request = new CustomHttpServletRequest();
        HttpServletResponse response = new CustomHttpServletResponse();
        ((CustomHttpServletRequest) request).setMethod("DELETE");

        request.getSession().setAttribute("loginInfo", new MemberInfo(1L, "user1", "User One"));

        authController.doProcess(request, response);
        authController.doProcess(request, response);

        assertNull(request.getSession().getAttribute("loginInfo"));
        assertEquals(200, ((CustomHttpServletResponse) response).getStatus());
    }
}