package codesquad.javacafe.post.controller;

import codesquad.javacafe.common.db.DBConnection;
import codesquad.javacafe.common.exception.ClientErrorCode;
import codesquad.javacafe.common.exception.CustomException;
import codesquad.javacafe.common.session.MemberInfo;
import codesquad.javacafe.member.entity.Member;
import codesquad.javacafe.member.repository.MemberRepository;
import codesquad.javacafe.post.dto.request.PostRequestDto;
import codesquad.javacafe.post.dto.response.PostResponseDto;
import codesquad.javacafe.post.entity.Post;
import codesquad.javacafe.post.repository.PostRepository;
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

public class PostControllerTest {

    private static PostController postController;
    private static Connection connection;
    private static long memberId;

    @BeforeAll
    static void setUp() throws SQLException {
        postController = new PostController();
        DBConnection.setConnectionInfo("jdbc:h2:mem:testdb;MODE=MySQL;DATABASE_TO_LOWER=TRUE", "sa", "");
        connection = DBConnection.getConnection();
        createTable();
        memberId = MemberRepository.getInstance().findByUserId("user1").getId();
    }

    @AfterEach
    void afterEach() throws SQLException {
        clearTable();
    }

    static void createTable() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "");
        Statement statement = connection.createStatement();

        // Create the post table
        String createTableSql = "CREATE TABLE if not exists post (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "post_writer VARCHAR(255), " +
                "post_title VARCHAR(255), " +
                "post_contents TEXT, " +
                "post_create TIMESTAMP," +
                "member_id BIGINT NOT NULL" +
                ")";
        statement.execute(createTableSql);

        String createMemberTableSql = "CREATE TABLE if not exists member (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "member_id VARCHAR(255), " +
                "member_password VARCHAR(255), " +
                "member_name VARCHAR(255)" +
                ")";
        statement.execute(createMemberTableSql);

        String memberInsertSql = "INSERT INTO member (member_id, member_password, member_name) \n" +
                                    "SELECT 'user1', 'password', 'User One' FROM DUAL \n" +
                                    "WHERE NOT EXISTS (SELECT * FROM member WHERE member_id = 'user1')";
        statement.execute(memberInsertSql);

        statement.close();
        connection.close();
    }

    private void clearTable() throws SQLException {
        var sql = "DELETE FROM post";
        try (var statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    @Test
    public void testDoProcessGet() throws ServletException, IOException {
        // Insert a post into the database
        Member member = new Member("user1", "password", "User One");
        MemberRepository.getInstance().save(member);
        long memberId = member.getId();

        Map<String, String[]> body = new HashMap<>();
        body.put("title", new String[]{"title1"});
        body.put("contents", new String[]{"contents1"});
        PostRequestDto postDto = new PostRequestDto(body);
        postDto.setWriter("User One");
        postDto.setMemberId(memberId);
        Post savePost = PostRepository.getInstance().save(postDto.toEntity());

        // Simulate GET request with query parameters
        HttpServletRequest request = new CustomHttpServletRequest();
        HttpServletResponse response = new CustomHttpServletResponse();

        ((CustomHttpServletRequest) request).setMethod("GET");
        ((CustomHttpServletRequest) request).addParameter("postId",savePost.getId()+"");


        request.setAttribute("userId","user1");
        request.getSession().setAttribute("loginInfo", new MemberInfo(memberId, "user1", "User One"));

        postController.doProcess(request, response);

        PostResponseDto post = (PostResponseDto) request.getAttribute("post");
        assertNotNull(post);
        assertEquals("User One", post.getWriter());
        assertEquals("/WEB-INF/qna/show.jsp", ((CustomHttpServletResponse) response).getForwardedUrl());
    }

    @Test
    public void testDoProcessGetWithNonLogin() throws ServletException, IOException {
        // Insert a post into the database
        Map<String, String[]> body = new HashMap<>();
        body.put("writer", new String[]{"writer1"});
        body.put("title", new String[]{"title1"});
        body.put("contents", new String[]{"contents1"});
        PostRequestDto postDto = new PostRequestDto(body);
        PostRepository.getInstance().save(postDto.toEntity());

        // Simulate GET request with query parameters
        HttpServletRequest request = new CustomHttpServletRequest();
        HttpServletResponse response = new CustomHttpServletResponse();

        ((CustomHttpServletRequest) request).setMethod("GET");
        ((CustomHttpServletRequest) request).addParameter("postId","1");


        CustomException exception = assertThrows(CustomException.class, () -> {
            postController.doProcess(request, response);
        });

        assertEquals(ClientErrorCode.UNAUTHORIZED_USER.getHttpStatus(), exception.getHttpStatus());
    }

    @Test
    public void testDoProcessPost() throws ServletException, IOException {
        // Simulate POST request
        HttpServletRequest request = new CustomHttpServletRequest();
        HttpServletResponse response = new CustomHttpServletResponse();

        ((CustomHttpServletRequest) request).setMethod("POST");
        ((CustomHttpServletRequest) request).addParameter("writer", "testWriter");
        ((CustomHttpServletRequest) request).addParameter("title", "testTitle");
        ((CustomHttpServletRequest) request).addParameter("contents", "testContents");
        request.getSession().setAttribute("loginInfo", new MemberInfo(memberId, "user1", "User One"));

        postController.doProcess(request, response);

        assertEquals("/", ((CustomHttpServletResponse) response).getRedirectedUrl());
        Post byId = PostRepository.getInstance().findById(3);
        System.out.println(byId);
        Member byMember = MemberRepository.getInstance().findByUserId("user1");
        System.out.println(byMember);
        List<Post> all = PostRepository.getInstance().findAll(0);
        System.out.println("all");
        System.out.println(all);

        // Verify the post was created
        Post post = PostRepository.getInstance().findById(all.get(0).getId());
        assertNotNull(post);
        assertEquals("User One", post.getWriter());
        assertEquals("testTitle", post.getTitle());
        assertEquals("testContents", post.getContents());
    }

    @Test
    public void testDoProcessPut() throws ServletException, IOException {
        // Insert a post into the database
        Member member = new Member("user1", "password", "User One");
        MemberRepository.getInstance().save(member);
        long memberId = member.getId();
        Map<String, String[]> body = new HashMap<>();
        body.put("title", new String[]{"title1"});
        body.put("contents", new String[]{"contents1"});
        PostRequestDto postDto = new PostRequestDto(body);
        postDto.setWriter("User One");
        postDto.setMemberId(memberId);
        Post savePost = PostRepository.getInstance().save(postDto.toEntity());

        // Simulate PUT request with query parameters
        HttpServletRequest request = new CustomHttpServletRequest();
        HttpServletResponse response = new CustomHttpServletResponse();
        ((CustomHttpServletRequest) request).setMethod("POST");
        ((CustomHttpServletRequest) request).addParameter("postId", savePost.getId() + "");
        ((CustomHttpServletRequest) request).addParameter("title", "updatedTitle");
        ((CustomHttpServletRequest) request).addParameter("contents", "updatedContents");
        ((CustomHttpServletRequest) request).addParameter("method", "PUT");
        ((CustomHttpServletRequest) request).addParameter("memberId", memberId+"");
        request.getSession().setAttribute("loginInfo", new MemberInfo(memberId, "user1", "User One"));

        postController.doProcess(request, response);

        // Verify the post was updated
        Post updatedPost = PostRepository.getInstance().findById(savePost.getId());
        assertNotNull(updatedPost);
        assertEquals("updatedTitle", updatedPost.getTitle());
        assertEquals("updatedContents", updatedPost.getContents());
    }

    @Test
    public void testDoProcessDelete() throws ServletException, IOException {
        // Insert a post into the database
        Member member = new Member("user1", "password", "User One");
        MemberRepository.getInstance().save(member);
        long memberId = member.getId();
        Map<String, String[]> body = new HashMap<>();
        body.put("title", new String[]{"title1"});
        body.put("contents", new String[]{"contents1"});
        PostRequestDto postDto = new PostRequestDto(body);
        postDto.setWriter("User One");
        postDto.setMemberId(memberId);
        Post savePost = PostRepository.getInstance().save(postDto.toEntity());

        // Simulate DELETE request with query parameters
        HttpServletRequest request = new CustomHttpServletRequest();
        HttpServletResponse response = new CustomHttpServletResponse();
        ((CustomHttpServletRequest) request).setMethod("POST");
        ((CustomHttpServletRequest) request).addParameter("postId", savePost.getId() + "");
        ((CustomHttpServletRequest) request).addParameter("memberId", memberId + "");
        ((CustomHttpServletRequest) request).addParameter("method", "DELETE");
        ((CustomHttpServletRequest) request).addParameter("memberId", memberId+"");
        request.getSession().setAttribute("loginInfo", new MemberInfo(memberId, "user1", "User One"));

        postController.doProcess(request, response);

        // Verify the post was deleted
        Post deletedPost = PostRepository.getInstance().findById(savePost.getId());
        assertNull(deletedPost);
    }

    @Test
    public void testDoProcessPutWithNonExistentPostId() throws ServletException, IOException {
        // Simulate PUT request with non-existent post ID
        HttpServletRequest request = new CustomHttpServletRequest();
        HttpServletResponse response = new CustomHttpServletResponse();
        ((CustomHttpServletRequest) request).setMethod("POST");
        ((CustomHttpServletRequest) request).addParameter("postId", "9999"); // Non-existent post ID
        ((CustomHttpServletRequest) request).addParameter("title", "updatedTitle");
        ((CustomHttpServletRequest) request).addParameter("contents", "updatedContents");
        ((CustomHttpServletRequest) request).addParameter("method", "PUT");
        ((CustomHttpServletRequest) request).addParameter("memberId", memberId+"");
        request.getSession().setAttribute("loginInfo", new MemberInfo(memberId, "user1", "User One"));

        CustomException exception = assertThrows(CustomException.class, () -> {
            postController.doProcess(request, response);
        });

        assertEquals(ClientErrorCode.POST_IS_NULL.getHttpStatus(), exception.getHttpStatus());
    }

    @Test
    public void testDoProcessPutWithUnauthorizedUser() throws ServletException, IOException {
        // Insert a post into the database
        Member member = new Member("user1", "password", "User One");
        MemberRepository.getInstance().save(member);
        long memberId = member.getId();
        Map<String, String[]> body = new HashMap<>();
        body.put("title", new String[]{"title1"});
        body.put("contents", new String[]{"contents1"});
        PostRequestDto postDto = new PostRequestDto(body);
        postDto.setWriter("User One");
        postDto.setMemberId(memberId);
        Post savePost = PostRepository.getInstance().save(postDto.toEntity());

        // Simulate PUT request with unauthorized user
        HttpServletRequest request = new CustomHttpServletRequest();
        HttpServletResponse response = new CustomHttpServletResponse();
        ((CustomHttpServletRequest) request).setMethod("POST");
        ((CustomHttpServletRequest) request).addParameter("postId", savePost.getId() + "");
        ((CustomHttpServletRequest) request).addParameter("title", "updatedTitle");
        ((CustomHttpServletRequest) request).addParameter("contents", "updatedContents");
        ((CustomHttpServletRequest) request).addParameter("method", "PUT");
        ((CustomHttpServletRequest) request).addParameter("memberId", memberId+"");
        request.getSession().setAttribute("loginInfo", new MemberInfo(memberId + 1, "user2", "User Two")); // Different user

        CustomException exception = assertThrows(CustomException.class, () -> {
            postController.doProcess(request, response);
        });

        assertEquals(ClientErrorCode.POST_ACCESS_DENIED.getHttpStatus(), exception.getHttpStatus());
    }

    @Test
    public void testDoProcessDeleteWithNonExistentPostId() throws ServletException, IOException {
        // Simulate DELETE request with non-existent post ID
        HttpServletRequest request = new CustomHttpServletRequest();
        HttpServletResponse response = new CustomHttpServletResponse();
        ((CustomHttpServletRequest) request).setMethod("POST");
        ((CustomHttpServletRequest) request).addParameter("postId", "9999"); // Non-existent post ID
        ((CustomHttpServletRequest) request).addParameter("memberId", memberId + "");
        ((CustomHttpServletRequest) request).addParameter("method", "DELETE");
        ((CustomHttpServletRequest) request).addParameter("memberId", memberId+"");
        request.getSession().setAttribute("loginInfo", new MemberInfo(memberId, "user1", "User One"));

        CustomException exception = assertThrows(CustomException.class, () -> {
            postController.doProcess(request, response);
        });

        assertEquals(ClientErrorCode.POST_IS_NULL.getHttpStatus(), exception.getHttpStatus());
    }

    @Test
    public void testDoProcessDeleteWithUnauthorizedUser() throws ServletException, IOException {
        // Insert a post into the database
        Member member = new Member("user1", "password", "User One");
        MemberRepository.getInstance().save(member);
        long memberId = member.getId();
        Map<String, String[]> body = new HashMap<>();
        body.put("title", new String[]{"title1"});
        body.put("contents", new String[]{"contents1"});
        PostRequestDto postDto = new PostRequestDto(body);
        postDto.setWriter("User One");
        postDto.setMemberId(memberId);
        Post savePost = PostRepository.getInstance().save(postDto.toEntity());

        // Simulate DELETE request with unauthorized user
        HttpServletRequest request = new CustomHttpServletRequest();
        HttpServletResponse response = new CustomHttpServletResponse();
        ((CustomHttpServletRequest) request).setMethod("POST");
        ((CustomHttpServletRequest) request).addParameter("postId", savePost.getId() + "");
        ((CustomHttpServletRequest) request).addParameter("memberId", memberId + "");
        ((CustomHttpServletRequest) request).addParameter("method", "DELETE");
        ((CustomHttpServletRequest) request).addParameter("memberId", memberId+"");
        request.getSession().setAttribute("loginInfo", new MemberInfo(memberId + 1, "user2", "User Two")); // Different user

        CustomException exception = assertThrows(CustomException.class, () -> {
            postController.doProcess(request, response);
        });

        assertEquals(ClientErrorCode.POST_ACCESS_DENIED.getHttpStatus(), exception.getHttpStatus());
    }
}