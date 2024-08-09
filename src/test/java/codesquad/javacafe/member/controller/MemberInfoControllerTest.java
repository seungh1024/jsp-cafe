package codesquad.javacafe.member.controller;

import codesquad.javacafe.common.db.DBConnection;
import codesquad.javacafe.common.session.MemberInfo;
import codesquad.javacafe.member.dto.request.MemberCreateRequestDto;
import codesquad.javacafe.member.dto.response.MemberResponseDto;
import codesquad.javacafe.member.repository.MemberRepository;
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

public class MemberInfoControllerTest {

    private static MemberInfoController memberInfoController;
    private static Connection connection;

    @BeforeAll
    static void setUp() throws SQLException {
        memberInfoController = new MemberInfoController();
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

    private void clearTable() throws SQLException {
        var sql = "DELETE FROM member";
        try (var statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    @Test
    public void testDoProcessGet() throws ServletException, IOException {
        // Insert a member into the database
        Map<String, String[]> body = new HashMap<>();
        body.put("userId", new String[]{"user1"});
        body.put("password", new String[]{"password1"});
        body.put("name", new String[]{"User One"});
        MemberCreateRequestDto memberDto = new MemberCreateRequestDto(body);
        var member = memberDto.toEntity();
        MemberRepository.getInstance().save(member);

        // Simulate GET request
        HttpServletRequest request = new CustomHttpServletRequest();
        HttpServletResponse response = new CustomHttpServletResponse();

        ((CustomHttpServletRequest) request).setMethod("GET");
        ((CustomHttpServletRequest) request).addParameter("userId", "user1");
        request.getSession().setAttribute("loginInfo", new MemberInfo(1, "user1", "User One"));

        memberInfoController.doProcess(request, response);

        String userId = (String) request.getAttribute("userId");
        assertEquals("user1", userId);
        assertEquals("/WEB-INF/user/memberInfo.jsp", ((CustomHttpServletResponse) response).getForwardedUrl());
    }

    @Test
    public void testDoProcessPost() throws ServletException, IOException {
        // Insert a member into the database
        Map<String, String[]> body = new HashMap<>();
        body.put("userId", new String[]{"user1"});
        body.put("password", new String[]{"password1"});
        body.put("name", new String[]{"User One"});
        MemberCreateRequestDto memberDto = new MemberCreateRequestDto(body);
        var member = memberDto.toEntity();
        MemberRepository.getInstance().save(member);

        // Simulate POST request
        HttpServletRequest request = new CustomHttpServletRequest();
        HttpServletResponse response = new CustomHttpServletResponse();
        request.getSession().setAttribute("loginInfo", new MemberInfo(1, "user1", "User One"));

        ((CustomHttpServletRequest) request).setMethod("POST");
        ((CustomHttpServletRequest) request).addParameter("userId", "user1");
        ((CustomHttpServletRequest) request).addParameter("oldPassword", "password1");
        ((CustomHttpServletRequest) request).addParameter("password", "newpassword");
        ((CustomHttpServletRequest) request).addParameter("name", "New User One");

        memberInfoController.doProcess(request, response);

        List<MemberResponseDto> memberList = (List<MemberResponseDto>) request.getAttribute("memberList");
        assertNotNull(memberList);
        assertEquals(1, memberList.size());
        assertEquals("user1", memberList.get(0).getUserId());
        assertEquals("New User One", memberList.get(0).getName());
        assertEquals("/WEB-INF/user/list.jsp", ((CustomHttpServletResponse) response).getForwardedUrl());
    }
}