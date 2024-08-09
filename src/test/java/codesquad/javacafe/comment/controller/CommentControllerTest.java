package codesquad.javacafe.comment.controller;

import codesquad.javacafe.comment.dto.request.CommentRequestDto;
import codesquad.javacafe.comment.dto.response.CommentResponseDto;
import codesquad.javacafe.comment.entity.Comment;
import codesquad.javacafe.comment.repository.CommentRepository;
import codesquad.javacafe.comment.service.CommentService;
import codesquad.javacafe.common.db.DBConnection;
import codesquad.javacafe.common.exception.ClientErrorCode;
import codesquad.javacafe.common.exception.CustomException;
import codesquad.javacafe.common.session.MemberInfo;
import codesquad.javacafe.member.entity.Member;
import codesquad.javacafe.member.repository.MemberRepository;
import codesquad.javacafe.post.dto.request.PostRequestDto;
import codesquad.javacafe.post.entity.Post;
import codesquad.javacafe.post.repository.PostRepository;
import codesquad.javacafe.util.CustomHttpServletRequest;
import codesquad.javacafe.util.CustomHttpServletResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class CommentControllerTest {
	private static CommentController commentController;
	private static Connection connection;
	private static long memberId;
	private static long postId;

	@BeforeAll
	static void setUp() throws SQLException {
		commentController = new CommentController();
		DBConnection.setConnectionInfo("jdbc:h2:mem:testdb;MODE=MySQL;DATABASE_TO_LOWER=TRUE", "sa", "");
		connection = DBConnection.getConnection();
		createTable();
	}

	@BeforeEach
	void init(){
		Member member = new Member("user1", "password", "User One");
		MemberRepository.getInstance().save(member);
		memberId = member.getId();
		Map<String, String[]> body = new HashMap<>();
		body.put("title", new String[]{"title1"});
		body.put("contents", new String[]{"contents1"});
		PostRequestDto postDto = new PostRequestDto(body);
		postDto.setWriter("User One");
		postDto.setMemberId(memberId);
		Post savePost = PostRepository.getInstance().save(postDto.toEntity());
		postId = savePost.getId();
	}

	@AfterEach
	void afterEach() throws SQLException {
		clearTable();
	}

	static void createTable() throws SQLException {
		Connection connection = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "");
		Statement statement = connection.createStatement();
		// Create the comment table
		String createTableSql = "CREATE TABLE if not exists comment (" +
			"id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
			"post_id BIGINT NOT NULL, " +
			"member_id BIGINT NOT NULL, " +
			"comment_contents TEXT, " +
			"comment_create TIMESTAMP" +
			")";
		statement.execute(createTableSql);

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
	public void testDoProcessGet() throws ServletException, IOException {
		// Insert a comment into the database
		Map<String, Object> data = new HashMap<>();
		data.put("postId", postId);
		data.put("memberId", memberId);
		data.put("comment", "This is a comment");
		CommentRequestDto commentDto = new CommentRequestDto(data);
		CommentService.getInstance().save(commentDto);

		// Simulate GET request with query parameters
		HttpServletRequest request = new CustomHttpServletRequest();
		HttpServletResponse response = new CustomHttpServletResponse();
		((CustomHttpServletRequest) request).setMethod("GET");
		((CustomHttpServletRequest) request).addParameter("postId", postId+"");
		var lastCreated = LocalDateTime.now().minusDays(1);
		var lastCommentId = 0;
		((CustomHttpServletRequest) request).addParameter("lastCreated", lastCreated.toString());
		((CustomHttpServletRequest) request).addParameter("lastCommentId", lastCommentId+"");
		request.setAttribute("userId", "user1");
		request.getSession().setAttribute("loginInfo", new MemberInfo(memberId, "user1", "User One"));

		commentController.doProcess(request, response);

		List<Comment> comments = CommentRepository.getInstance().findAll(postId, lastCreated, lastCommentId);

		assertNotNull(comments);
		assertFalse(comments.isEmpty());
		assertEquals("This is a comment", comments.get(0).getComment());
	}

	@Test
	public void testDoProcessPost() throws ServletException, IOException {
		// Simulate POST request
		HttpServletRequest request = new CustomHttpServletRequest();
		HttpServletResponse response = new CustomHttpServletResponse();
		((CustomHttpServletRequest) request).setMethod("POST");
		((CustomHttpServletRequest) request).setBody("{\"postId\": 1, \"memberId\": " + memberId + ", \"comment\": \"This is a test comment\"}");
		request.getSession().setAttribute("loginInfo", new MemberInfo(memberId, "user1", "User One"));
		var lastCreated = LocalDateTime.now().minusDays(1);
		var lastCommentId = 0;
		((CustomHttpServletRequest) request).addParameter("lastCreated", lastCreated.toString());
		((CustomHttpServletRequest) request).addParameter("lastCommentId", lastCommentId+"");
		commentController.doProcess(request, response);

		assertEquals(HttpServletResponse.SC_CREATED, response.getStatus());
		CommentResponseDto comment = CommentService.getInstance().getCommentList(1L, lastCreated, lastCommentId).get(0);
		assertNotNull(comment);
		assertEquals("This is a test comment", comment.getComment());
	}

	@Test
	public void testDoProcessDelete() throws ServletException, IOException {
		// Insert a comment into the database
		Map<String, Object> data = new HashMap<>();
		data.put("postId", postId);
		data.put("memberId", memberId);
		data.put("comment", "This is a comment to delete");
		CommentRequestDto commentDto = new CommentRequestDto(data);
		CommentResponseDto savedComment = CommentService.getInstance().save(commentDto);

		// Simulate DELETE request
		HttpServletRequest request = new CustomHttpServletRequest();
		HttpServletResponse response = new CustomHttpServletResponse();
		((CustomHttpServletRequest) request).setMethod("DELETE");
		((CustomHttpServletRequest) request).setBody("{\"commentId\": " + savedComment.getId() + "}");
		request.getSession().setAttribute("loginInfo", new MemberInfo(memberId, "user1", "User One"));
		var lastCreated = LocalDateTime.now().minusDays(1);
		var lastCommentId = 0;
		((CustomHttpServletRequest) request).addParameter("lastCreated", lastCreated.toString());
		((CustomHttpServletRequest) request).addParameter("lastCommentId", lastCommentId+"");

		commentController.doProcess(request, response);

		assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());
		List<CommentResponseDto> comments = CommentService.getInstance().getCommentList(1L, lastCreated, lastCommentId);
		assertNull(comments);
	}

	@Test
	public void testDoProcessDeleteWithNonExistentCommentId() throws ServletException, IOException {
		// Simulate DELETE request with non-existent comment ID
		HttpServletRequest request = new CustomHttpServletRequest();
		HttpServletResponse response = new CustomHttpServletResponse();
		((CustomHttpServletRequest) request).setMethod("DELETE");
		((CustomHttpServletRequest) request).setBody("{\"commentId\": 9999}"); // Non-existent comment ID
		request.getSession().setAttribute("loginInfo", new MemberInfo(memberId, "user1", "User One"));

		CustomException exception = assertThrows(CustomException.class, () -> {
			commentController.doProcess(request, response);
		});

		assertEquals(ClientErrorCode.PARAMETER_IS_NULL.getHttpStatus(), exception.getHttpStatus());
	}
}