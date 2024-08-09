package codesquad.javacafe.post.repository;

import codesquad.javacafe.common.db.DBConnection;
import codesquad.javacafe.member.repository.MemberRepository;
import codesquad.javacafe.post.dto.request.PostRequestDto;
import codesquad.javacafe.post.entity.Post;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class PostRepositoryTest {

    private static PostRepository postRepository;
    private static Connection connection;
    private static long memberId;

    @BeforeAll
    static void setUp() throws SQLException {
        postRepository = PostRepository.getInstance();
        DBConnection.setConnectionInfo("jdbc:h2:mem:testdb;MODE=MySQL;DATABASE_TO_LOWER=TRUE", "sa", "");
        connection = DBConnection.getConnection();
        createTable();
    }

    @AfterEach
    void afterEach() throws SQLException {
        clearTable();
    }

    private static void createTable() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "");

        // Create the post table
        String sql = "CREATE TABLE if not exists post (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "post_writer VARCHAR(255), " +
                "post_title VARCHAR(255), " +
                "post_contents TEXT, " +
                "post_create TIMESTAMP," +
                "member_id BIGINT NOT NULL" +
                ")";


        var statement = connection.createStatement();
        statement.execute(sql);

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
        memberId = MemberRepository.getInstance().findByUserId("user1").getId();


    }

    private void clearTable() throws SQLException {
        var sql = "DELETE FROM post";
        try (var statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    private PostRequestDto createPostDto(String writer, String title, String contents) {
        Map<String, String[]> body = new HashMap<>();
        body.put("title", new String[]{title});
        body.put("contents", new String[]{contents});
        PostRequestDto postDto = new PostRequestDto(body);
        postDto.setWriter(writer);
        return postDto;
    }

    @Test
    public void testSave() throws SQLException {
        PostRequestDto postDto = createPostDto("User One", "title", "contents");
        postDto.setMemberId(memberId);
        postRepository.save(postDto.toEntity());

        List<Post> posts = postRepository.findAll(0);
        assertNotNull(posts);
        assertEquals(1, posts.size());
        assertEquals("User One", posts.get(0).getWriter());
        assertEquals("title", posts.get(0).getTitle());
        assertEquals("contents", posts.get(0).getContents());
    }

    @Test
    public void testFindAll() throws SQLException {
        PostRequestDto postDto1 = createPostDto("User One", "title1", "contents1");
        PostRequestDto postDto2 = createPostDto("User One", "title2", "contents2");
        postDto1.setMemberId(memberId);
        postDto2.setMemberId(memberId);

        postRepository.save(postDto1.toEntity());
        postRepository.save(postDto2.toEntity());

        List<Post> posts = postRepository.findAll(0);
        System.out.println(posts);
        assertNotNull(posts);
        assertEquals(2, posts.size());
        assertEquals("User One", posts.get(0).getWriter());
        assertEquals("title2", posts.get(0).getTitle());
        assertEquals("contents2", posts.get(0).getContents());
        assertEquals("User One", posts.get(1).getWriter());
        assertEquals("title1", posts.get(1).getTitle());
        assertEquals("contents1", posts.get(1).getContents());
    }

    @Test
    public void testFindById() throws SQLException {
        PostRequestDto postDto = createPostDto("User One", "title", "contents");
        postDto.setMemberId(memberId);
        postRepository.save(postDto.toEntity());

        List<Post> posts = postRepository.findAll(0);
        assertNotNull(posts);
        assertEquals(1, posts.size());

        Post post = postRepository.findById(posts.get(0).getId());
        assertNotNull(post);
        assertEquals("User One", post.getWriter());
        assertEquals("title", post.getTitle());
        assertEquals("contents", post.getContents());
    }

    @Test
    public void testUpdate() throws SQLException {
        PostRequestDto postDto = createPostDto("User One", "title", "contents");
        postDto.setMemberId(memberId);
        Post savedPost = postRepository.save(postDto.toEntity());

        // Update the post
        PostRequestDto updatePost = new PostRequestDto(savedPost.getId(), "updatedTitle", "updatedContents", memberId);

        int rowsAffected = postRepository.update(updatePost.toEntity());

        assertEquals(1, rowsAffected);

        Post updatedPost = postRepository.findById(savedPost.getId());
        assertNotNull(updatedPost);
        assertEquals("updatedTitle", updatedPost.getTitle());
        assertEquals("updatedContents", updatedPost.getContents());
    }

    @Test
    public void testDelete() throws SQLException {
        PostRequestDto postDto = createPostDto("User One", "title", "contents");
        postDto.setMemberId(memberId);
        Post savedPost = postRepository.save(postDto.toEntity());

        // Delete the post
        int rowsAffected = postRepository.delete(savedPost.getId());
        assertEquals(1, rowsAffected);

        Post deletedPost = postRepository.findById(savedPost.getId());
        assertNull(deletedPost);
    }

    @Test
    public void testDeleteWithNonExistentPostId() throws SQLException {
        int rowsAffected = postRepository.delete(9999L); // Non-existent post ID
        assertEquals(0, rowsAffected);
    }
}
