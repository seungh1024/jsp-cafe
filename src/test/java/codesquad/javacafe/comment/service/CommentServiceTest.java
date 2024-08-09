package codesquad.javacafe.comment.service;

import codesquad.javacafe.comment.dto.request.CommentRequestDto;
import codesquad.javacafe.comment.dto.response.CommentResponseDto;
import codesquad.javacafe.common.db.DBConnection;
import codesquad.javacafe.common.exception.ClientErrorCode;
import codesquad.javacafe.common.exception.CustomException;
import codesquad.javacafe.member.entity.Member;
import codesquad.javacafe.member.repository.MemberRepository;
import codesquad.javacafe.post.entity.Post;
import codesquad.javacafe.post.repository.PostRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class CommentServiceTest {
	private static CommentService commentService;
	private static Connection connection;
	private static long memberId;
	private static long postId;

	@BeforeAll
	static void setUp() throws SQLException {
		commentService = CommentService.getInstance();
		DBConnection.setConnectionInfo("jdbc:h2:mem:testdb;MODE=MySQL;DATABASE_TO_LOWER=TRUE", "sa", "");
		connection = DBConnection.getConnection();
		createTable();
	}

	@BeforeEach
	void init() {
		Member member = new Member("user1", "password", "User One");
		MemberRepository.getInstance().save(member);
		memberId = member.getId();

		Post post = new Post();
		post.setWriter("User One");
		post.setTitle("title1");
		post.setContents("contents1");
		post.setMemberId(memberId);
		post.setCreatedAt(LocalDateTime.now());
		PostRepository.getInstance().save(post);
		postId = post.getId();
	}

	@AfterEach
	void afterEach() throws SQLException {
		clearTable();
	}

	static void createTable() throws SQLException {
		Connection connection = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "");
		Statement statement = connection.createStatement();

		String createCommentTableSql = "CREATE TABLE if not exists comment (" +
			"id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
			"post_id BIGINT NOT NULL, " +
			"member_id BIGINT NOT NULL, " +
			"comment_contents TEXT, " +
			"comment_create TIMESTAMP" +
			")";
		statement.execute(createCommentTableSql);

		String createMemberTableSql = "CREATE TABLE if not exists member (" +
			"id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
			"member_id VARCHAR(255), " +
			"member_password VARCHAR(255), " +
			"member_name VARCHAR(255)" +
			")";
		statement.execute(createMemberTableSql);

		String createPostTableSql = "CREATE TABLE if not exists post (" +
			"id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
			"post_writer VARCHAR(255), " +
			"post_title VARCHAR(255), " +
			"post_contents TEXT, " +
			"post_create TIMESTAMP," +
			"member_id BIGINT NOT NULL" +
			")";
		statement.execute(createPostTableSql);

		statement.close();
		connection.close();
	}

	private void clearTable() throws SQLException {
		var sql = "DELETE FROM comment";
		try (var statement = connection.createStatement()) {
			statement.execute(sql);
		}

		sql = "DELETE FROM post";
		try (var statement = connection.createStatement()) {
			statement.execute(sql);
		}

		sql = "DELETE FROM member";
		try (var statement = connection.createStatement()) {
			statement.execute(sql);
		}
	}

	@Test
	public void testSaveComment() {
		Map<String, Object> data = new HashMap<>();
		data.put("postId", postId);
		data.put("memberId", memberId);
		data.put("comment", "This is a test comment");
		CommentRequestDto commentDto = new CommentRequestDto(data);

		CommentResponseDto savedComment = commentService.save(commentDto);

		assertNotNull(savedComment.getId());
		assertEquals("This is a test comment", savedComment.getComment());
	}

	@Test
	public void testGetCommentList() {
		Map<String, Object> data1 = new HashMap<>();
		data1.put("postId", postId);
		data1.put("memberId", memberId);
		data1.put("comment", "First comment");
		CommentRequestDto commentDto1 = new CommentRequestDto(data1);
		commentService.save(commentDto1);

		Map<String, Object> data2 = new HashMap<>();
		data2.put("postId", postId);
		data2.put("memberId", memberId);
		data2.put("comment", "Second comment");
		CommentRequestDto commentDto2 = new CommentRequestDto(data2);
		commentService.save(commentDto2);
		var lastCreated = LocalDateTime.now().minusDays(1);
		var lastCommentId = 0;

		List<CommentResponseDto> comments = commentService.getCommentList(postId, lastCreated,lastCommentId);

		assertNotNull(comments);
		assertEquals(2, comments.size());
		assertEquals("First comment", comments.get(0).getComment());
		assertEquals("Second comment", comments.get(1).getComment());
	}

	@Test
	public void testDeleteComment() {
		Map<String, Object> data = new HashMap<>();
		data.put("postId", postId);
		data.put("memberId", memberId);
		data.put("comment", "Comment to delete");
		CommentRequestDto commentDto = new CommentRequestDto(data);
		CommentResponseDto savedComment = commentService.save(commentDto);
		var lastCreated = LocalDateTime.now().minusDays(1);
		var lastCommentId = 0;

		commentService.delete(savedComment.getId());

		List<CommentResponseDto> comments = commentService.getCommentList(postId, lastCreated,lastCommentId);
		assertNull(comments);
	}

	@Test
	public void testDeleteNonExistentComment() {
		CustomException exception = assertThrows(CustomException.class, () -> {
			commentService.delete(9999L); // Non-existent comment ID
		});

		assertEquals(ClientErrorCode.COMMENT_IS_NULL.getHttpStatus(), exception.getHttpStatus());
	}
}